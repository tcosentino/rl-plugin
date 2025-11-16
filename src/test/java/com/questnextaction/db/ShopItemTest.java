package com.questnextaction.db;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for ShopItem model
 */
public class ShopItemTest
{
	@Test
	public void testShopItemCreation()
	{
		ShopItem item = new ShopItem();
		assertNotNull("ShopItem should be created", item);
	}

	@Test
	public void testShopItemSettersAndGetters()
	{
		ShopItem item = new ShopItem();

		item.setItemId(556);
		item.setName("Air rune");
		item.setStock(300);
		item.setPrice(5);

		assertEquals(556, item.getItemId());
		assertEquals("Air rune", item.getName());
		assertEquals(300, item.getStock());
		assertEquals(5, item.getPrice());
	}

	@Test
	public void testShopItemWithInfiniteStock()
	{
		ShopItem item = new ShopItem();
		item.setStock(-1); // -1 typically represents infinite stock

		assertEquals(-1, item.getStock());
	}

	@Test
	public void testShopItemWithZeroStock()
	{
		ShopItem item = new ShopItem();
		item.setStock(0);

		assertEquals(0, item.getStock());
	}

	@Test
	public void testShopItemWithZeroPrice()
	{
		ShopItem item = new ShopItem();
		item.setPrice(0);

		assertEquals(0, item.getPrice());
	}

	@Test
	public void testShopItemWithHighPrice()
	{
		ShopItem item = new ShopItem();
		item.setPrice(1_000_000_000); // 1 billion gp

		assertEquals(1_000_000_000, item.getPrice());
	}

	@Test
	public void testShopItemEquality()
	{
		ShopItem item1 = new ShopItem();
		item1.setItemId(556);
		item1.setName("Air rune");
		item1.setStock(300);
		item1.setPrice(5);

		ShopItem item2 = new ShopItem();
		item2.setItemId(556);
		item2.setName("Air rune");
		item2.setStock(300);
		item2.setPrice(5);

		// Lombok @Data generates equals/hashCode
		assertEquals("Items with same data should be equal", item1, item2);
		assertEquals("Hash codes should match", item1.hashCode(), item2.hashCode());
	}

	@Test
	public void testShopItemInequality()
	{
		ShopItem item1 = new ShopItem();
		item1.setItemId(556);
		item1.setName("Air rune");

		ShopItem item2 = new ShopItem();
		item2.setItemId(555);
		item2.setName("Water rune");

		assertNotEquals("Items with different data should not be equal", item1, item2);
	}

	@Test
	public void testShopItemToString()
	{
		ShopItem item = new ShopItem();
		item.setItemId(556);
		item.setName("Air rune");
		item.setStock(300);
		item.setPrice(5);

		String toString = item.toString();
		assertNotNull("toString should not be null", toString);
		assertTrue("toString should contain item name", toString.contains("Air rune"));
	}

	@Test
	public void testShopItemWithDifferentItemIds()
	{
		ShopItem item = new ShopItem();

		item.setItemId(1);
		assertEquals(1, item.getItemId());

		item.setItemId(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, item.getItemId());

		item.setItemId(0);
		assertEquals(0, item.getItemId());
	}

	@Test
	public void testShopItemNameEdgeCases()
	{
		ShopItem item = new ShopItem();

		// Empty string
		item.setName("");
		assertEquals("", item.getName());

		// Very long name
		String longName = "A".repeat(1000);
		item.setName(longName);
		assertEquals(longName, item.getName());

		// Special characters
		item.setName("Dragon longsword (++)");
		assertEquals("Dragon longsword (++)", item.getName());
	}

	@Test
	public void testShopItemPriceAndStockCombinations()
	{
		ShopItem freeItem = new ShopItem();
		freeItem.setPrice(0);
		freeItem.setStock(100);
		assertEquals(0, freeItem.getPrice());
		assertEquals(100, freeItem.getStock());

		ShopItem expensiveRareItem = new ShopItem();
		expensiveRareItem.setPrice(1_000_000);
		expensiveRareItem.setStock(1);
		assertEquals(1_000_000, expensiveRareItem.getPrice());
		assertEquals(1, expensiveRareItem.getStock());

		ShopItem infiniteItem = new ShopItem();
		infiniteItem.setPrice(100);
		infiniteItem.setStock(-1);
		assertEquals(100, infiniteItem.getPrice());
		assertEquals(-1, infiniteItem.getStock());
	}
}
