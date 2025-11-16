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
}
