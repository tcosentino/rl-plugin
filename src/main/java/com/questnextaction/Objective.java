package com.questnextaction;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a trackable objective
 */
@Value
@Builder
public class Objective
{
	/**
	 * Unique identifier for this objective
	 */
	String id;

	/**
	 * Type of objective
	 */
	ObjectiveType type;

	/**
	 * What needs to be done
	 */
	String task;

	/**
	 * Location name (e.g., "Lumbridge Castle", "Varrock Square")
	 * For multi-location objectives, this is a summary (e.g., "Multiple shops")
	 */
	String locationName;

	/**
	 * Primary world location for this objective (null if no specific location)
	 * For multi-location objectives, this is the default/fallback location
	 */
	@Nullable
	WorldPoint location;

	/**
	 * Alternative locations where this objective can be completed
	 * Used for shop purchases where multiple shops sell the same item
	 */
	@Nullable
	@Singular
	List<WorldPoint> possibleLocations;

	/**
	 * Region ID for the location (used for map rendering)
	 */
	int regionId;

	/**
	 * Whether this objective is currently active/being tracked
	 */
	boolean active;

	/**
	 * For BUY objectives: the item being purchased
	 */
	@Nullable
	String itemName;

	/**
	 * For BUY/COLLECT objectives: how many items needed
	 */
	@Nullable
	Integer quantity;

	/**
	 * For BUY objectives: detailed shop information including prices
	 * This provides richer data than possibleLocations for shop-based objectives
	 */
	@Nullable
	@Singular
	List<ShopLocation> shopLocations;

	/**
	 * Get the best location to display based on player's current position.
	 * For objectives with multiple possible locations, returns the closest one.
	 * For objectives with a single location, returns that location.
	 *
	 * @param playerPosition Current player position
	 * @return The optimal location to show, or null if no locations available
	 */
	@Nullable
	public WorldPoint getBestLocation(@Nullable WorldPoint playerPosition)
	{
		// If no player position or no possible locations, use primary location
		if (playerPosition == null || possibleLocations == null || possibleLocations.isEmpty())
		{
			return location;
		}

		// Find closest location from possible locations
		WorldPoint closest = null;
		int minDistance = Integer.MAX_VALUE;

		for (WorldPoint shopLocation : possibleLocations)
		{
			if (shopLocation == null)
			{
				continue;
			}

			// Only consider locations on the same plane
			if (shopLocation.getPlane() != playerPosition.getPlane())
			{
				continue;
			}

			int distance = shopLocation.distanceTo(playerPosition);
			if (distance < minDistance)
			{
				minDistance = distance;
				closest = shopLocation;
			}
		}

		// If no location on same plane found, use primary location
		return closest != null ? closest : location;
	}

	/**
	 * Get region ID for a given location
	 */
	public int getRegionIdForLocation(WorldPoint worldPoint)
	{
		return worldPoint != null ? worldPoint.getRegionID() : regionId;
	}

	/**
	 * Get the best shop location based on player position and price.
	 * Prioritizes the closest shop, but considers price if shops are nearby.
	 *
	 * @param playerPosition Current player position
	 * @return The optimal shop location, or null if no shop locations available
	 */
	@Nullable
	public ShopLocation getBestShopLocation(@Nullable WorldPoint playerPosition)
	{
		if (shopLocations == null || shopLocations.isEmpty())
		{
			return null;
		}

		// If only one shop, return it
		if (shopLocations.size() == 1)
		{
			return shopLocations.get(0);
		}

		// If no player position, return cheapest shop
		if (playerPosition == null)
		{
			return getCheapestShop();
		}

		// Find closest shop on the same plane
		ShopLocation closestShop = null;
		int minDistance = Integer.MAX_VALUE;

		for (ShopLocation shop : shopLocations)
		{
			if (shop.getWorldPoint() == null)
			{
				continue;
			}

			// Only consider shops on the same plane
			if (shop.getWorldPoint().getPlane() != playerPosition.getPlane())
			{
				continue;
			}

			int distance = shop.getWorldPoint().distanceTo(playerPosition);
			if (distance < minDistance)
			{
				minDistance = distance;
				closestShop = shop;
			}
		}

		// If no shop on same plane, return cheapest
		return closestShop != null ? closestShop : getCheapestShop();
	}

	/**
	 * Get the shop with the lowest price for this item.
	 *
	 * @return The cheapest shop, or null if no shop locations available
	 */
	@Nullable
	public ShopLocation getCheapestShop()
	{
		if (shopLocations == null || shopLocations.isEmpty())
		{
			return null;
		}

		ShopLocation cheapest = shopLocations.get(0);
		for (ShopLocation shop : shopLocations)
		{
			if (shop.getPrice() < cheapest.getPrice())
			{
				cheapest = shop;
			}
		}

		return cheapest;
	}

	/**
	 * Get the total cost to complete this BUY objective at the cheapest shop.
	 *
	 * @return Total cost in coins, or 0 if not a BUY objective or no shops available
	 */
	public int getCheapestTotalCost()
	{
		ShopLocation cheapest = getCheapestShop();
		if (cheapest == null || quantity == null)
		{
			return 0;
		}

		return cheapest.calculateTotalCost(quantity);
	}

	/**
	 * Get shops that have sufficient stock for this objective.
	 *
	 * @return List of shops with sufficient stock, empty list if none available
	 */
	public List<ShopLocation> getShopsWithSufficientStock()
	{
		if (shopLocations == null || quantity == null)
		{
			return List.of();
		}

		return shopLocations.stream()
			.filter(shop -> shop.hasSufficientStock(quantity))
			.collect(java.util.stream.Collectors.toList());
	}
}
