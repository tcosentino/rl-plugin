package com.questnextaction.db;

import lombok.Data;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a shop in the game
 */
@Data
public class Shop
{
	/**
	 * Unique identifier for the shop
	 */
	private String id;

	/**
	 * Display name of the shop
	 */
	private String name;

	/**
	 * Shop owner/NPC name
	 */
	@Nullable
	private String owner;

	/**
	 * Location name (e.g., "Varrock", "Lumbridge")
	 */
	private String location;

	/**
	 * World coordinates of the shop
	 */
	@Nullable
	private WorldPoint worldPoint;

	/**
	 * Items sold at this shop
	 */
	private List<ShopItem> items;

	/**
	 * Helper method to create WorldPoint from coordinates
	 */
	public void setCoordinates(int x, int y, int plane)
	{
		this.worldPoint = new WorldPoint(x, y, plane);
	}
}
