package com.questnextaction.db;

import lombok.Data;

/**
 * Represents an item sold in a shop
 */
@Data
public class ShopItem
{
	/**
	 * Item ID from the game
	 */
	private int itemId;

	/**
	 * Display name of the item
	 */
	private String name;

	/**
	 * Base stock quantity (-1 for infinite)
	 */
	private int stock;

	/**
	 * Price in coins
	 */
	private int price;
}
