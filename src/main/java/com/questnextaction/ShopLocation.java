package com.questnextaction;

import lombok.Builder;
import lombok.Value;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;

/**
 * Represents a shop location with pricing information.
 * <p>
 * This class is used for BUY objectives to track which shops sell an item,
 * where they are located, and what the price is at each shop.
 * </p>
 *
 * @see Objective
 */
@Value
@Builder
public class ShopLocation
{
	/**
	 * The shop's unique identifier
	 */
	String shopId;

	/**
	 * Display name of the shop (e.g., "Lumbridge General Store")
	 */
	String shopName;

	/**
	 * Name of the shop owner/keeper (e.g., "Shop keeper", "Aubury")
	 */
	@Nullable
	String ownerName;

	/**
	 * Location name (e.g., "Lumbridge", "Varrock")
	 */
	String locationName;

	/**
	 * World coordinates of the shop
	 */
	WorldPoint worldPoint;

	/**
	 * Price of the item at this shop (in coins)
	 */
	int price;

	/**
	 * Available stock at this shop (-1 for infinite stock)
	 */
	int stock;

	/**
	 * Whether this shop has sufficient stock for the objective quantity
	 */
	public boolean hasSufficientStock(int requiredQuantity)
	{
		return stock == -1 || stock >= requiredQuantity;
	}

	/**
	 * Get the region ID for this shop's location
	 */
	public int getRegionId()
	{
		return worldPoint != null ? worldPoint.getRegionID() : 0;
	}

	/**
	 * Calculate total cost for purchasing a given quantity
	 */
	public int calculateTotalCost(int quantity)
	{
		return price * quantity;
	}

	/**
	 * Get a summary string for display
	 */
	public String getSummary()
	{
		return String.format("%s (%s) - %d gp", shopName, locationName, price);
	}
}
