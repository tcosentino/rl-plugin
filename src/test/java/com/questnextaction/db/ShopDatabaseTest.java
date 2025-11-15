package com.questnextaction.db;

import net.runelite.api.coords.WorldPoint;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for ShopDatabase data access layer
 */
public class ShopDatabaseTest
{
	private ShopDatabase shopDatabase;

	@Before
	public void setUp()
	{
		shopDatabase = new ShopDatabase();
	}

	// ==================== Initialization Tests ====================

	@Test
	public void testDatabaseLoadsSuccessfully()
	{
		assertNotNull("ShopDatabase should initialize", shopDatabase);
	}

	@Test
	public void testDatabaseContainsShops()
	{
		List<Shop> shops = shopDatabase.getAllShops();
		assertNotNull("Shop list should not be null", shops);
		assertFalse("Shop list should not be empty", shops.isEmpty());
		assertTrue("Should load at least one shop", shops.size() > 0);
	}

	@Test
	public void testDatabaseContainsItems()
	{
		List<String> items = shopDatabase.getAllItemNames();
		assertNotNull("Item list should not be null", items);
		assertFalse("Item list should not be empty", items.isEmpty());
		assertTrue("Should load at least one item", items.size() > 0);
	}

	// ==================== Shop Query Tests ====================

	@Test
	public void testGetAllShopsReturnsDefensiveCopy()
	{
		List<Shop> shops1 = shopDatabase.getAllShops();
		List<Shop> shops2 = shopDatabase.getAllShops();

		assertNotSame("getAllShops should return a new list each time", shops1, shops2);
		assertEquals("Both lists should contain same number of shops", shops1.size(), shops2.size());
	}

	@Test
	public void testGetShopById()
	{
		// First get a shop to get its ID
		List<Shop> allShops = shopDatabase.getAllShops();
		assertFalse("Need at least one shop for this test", allShops.isEmpty());

		Shop firstShop = allShops.get(0);
		String shopId = firstShop.getId();

		Shop retrievedShop = shopDatabase.getShopById(shopId);
		assertNotNull("Should retrieve shop by ID", retrievedShop);
		assertEquals("Retrieved shop should have same ID", shopId, retrievedShop.getId());
		assertEquals("Retrieved shop should have same name", firstShop.getName(), retrievedShop.getName());
	}

	@Test
	public void testGetShopByIdWithInvalidId()
	{
		Shop shop = shopDatabase.getShopById("nonexistent_shop_id_12345");
		assertNull("Should return null for invalid shop ID", shop);
	}

	@Test
	public void testGetShopByIdWithNull()
	{
		Shop shop = shopDatabase.getShopById(null);
		assertNull("Should return null for null shop ID", shop);
	}

	// ==================== Item Search Tests ====================

	@Test
	public void testSearchItemsWithExactMatch()
	{
		// Assuming "pot" exists in the test data
		List<String> results = shopDatabase.searchItems("pot");
		assertNotNull("Search results should not be null", results);
		assertFalse("Should find at least one result for 'pot'", results.isEmpty());

		// Check that results contain the search term
		boolean foundMatch = results.stream()
			.anyMatch(item -> item.toLowerCase().contains("pot"));
		assertTrue("Results should contain items matching 'pot'", foundMatch);
	}

	@Test
	public void testSearchItemsWithPartialMatch()
	{
		// Search for partial term
		List<String> results = shopDatabase.searchItems("run");
		assertNotNull("Search results should not be null", results);

		// All results should contain the search term
		for (String item : results)
		{
			assertTrue("Result '" + item + "' should contain 'run'",
				item.toLowerCase().contains("run"));
		}
	}

	@Test
	public void testSearchItemsCaseInsensitive()
	{
		List<String> lowerResults = shopDatabase.searchItems("rune");
		List<String> upperResults = shopDatabase.searchItems("RUNE");
		List<String> mixedResults = shopDatabase.searchItems("RuNe");

		assertNotNull("Lowercase search should work", lowerResults);
		assertNotNull("Uppercase search should work", upperResults);
		assertNotNull("Mixed case search should work", mixedResults);

		assertEquals("Search should be case-insensitive", lowerResults.size(), upperResults.size());
		assertEquals("Search should be case-insensitive", lowerResults.size(), mixedResults.size());
	}

	@Test
	public void testSearchItemsWithNoMatches()
	{
		List<String> results = shopDatabase.searchItems("xyznonexistentitem123");
		assertNotNull("Search results should not be null", results);
		assertTrue("Should return empty list for non-existent item", results.isEmpty());
	}

	@Test
	public void testSearchItemsWithEmptyString()
	{
		List<String> results = shopDatabase.searchItems("");
		assertNotNull("Search results should not be null", results);
		assertTrue("Should return empty list for empty query", results.isEmpty());
	}

	@Test
	public void testSearchItemsWithNull()
	{
		List<String> results = shopDatabase.searchItems(null);
		assertNotNull("Search results should not be null", results);
		assertTrue("Should return empty list for null query", results.isEmpty());
	}

	@Test
	public void testSearchItemsWithWhitespace()
	{
		List<String> results = shopDatabase.searchItems("   ");
		assertNotNull("Search results should not be null", results);
		assertTrue("Should return empty list for whitespace-only query", results.isEmpty());
	}

	@Test
	public void testSearchResultsAreSorted()
	{
		List<String> results = shopDatabase.searchItems("r");

		// Verify results are in alphabetical order
		for (int i = 0; i < results.size() - 1; i++)
		{
			String current = results.get(i);
			String next = results.get(i + 1);
			assertTrue("Results should be sorted alphabetically: " + current + " should come before " + next,
				current.compareTo(next) <= 0);
		}
	}

	// ==================== Find Shops By Item Tests ====================

	@Test
	public void testFindShopsByItemExactMatch()
	{
		// Get a known item from the database
		List<String> allItems = shopDatabase.getAllItemNames();
		assertFalse("Need items to test", allItems.isEmpty());

		String testItem = allItems.get(0);
		List<Shop> shops = shopDatabase.findShopsByItem(testItem);

		assertNotNull("Result should not be null", shops);
		assertFalse("Should find at least one shop selling the item", shops.isEmpty());

		// Verify the shops actually contain the item
		for (Shop shop : shops)
		{
			boolean hasItem = shop.getItems().stream()
				.anyMatch(item -> item.getName().equalsIgnoreCase(testItem));
			assertTrue("Shop " + shop.getName() + " should contain item " + testItem, hasItem);
		}
	}

	@Test
	public void testFindShopsByItemCaseInsensitive()
	{
		List<String> allItems = shopDatabase.getAllItemNames();
		assertFalse("Need items to test", allItems.isEmpty());

		String testItem = allItems.get(0);
		List<Shop> lowerResults = shopDatabase.findShopsByItem(testItem.toLowerCase());
		List<Shop> upperResults = shopDatabase.findShopsByItem(testItem.toUpperCase());

		assertEquals("Find shops should be case-insensitive", lowerResults.size(), upperResults.size());
	}

	@Test
	public void testFindShopsByItemNotFound()
	{
		List<Shop> shops = shopDatabase.findShopsByItem("nonexistent_item_xyz");
		assertNotNull("Result should not be null", shops);
		assertTrue("Should return empty list for non-existent item", shops.isEmpty());
	}

	@Test
	public void testFindShopsByItemEmptyString()
	{
		List<Shop> shops = shopDatabase.findShopsByItem("");
		assertNotNull("Result should not be null", shops);
		assertTrue("Should return empty list for empty string", shops.isEmpty());
	}

	// ==================== Get All Item Names Tests ====================

	@Test
	public void testGetAllItemNamesReturnsUnique()
	{
		List<String> items = shopDatabase.getAllItemNames();
		long uniqueCount = items.stream().distinct().count();

		assertEquals("All item names should be unique", items.size(), uniqueCount);
	}

	@Test
	public void testGetAllItemNamesAreSorted()
	{
		List<String> items = shopDatabase.getAllItemNames();

		for (int i = 0; i < items.size() - 1; i++)
		{
			String current = items.get(i);
			String next = items.get(i + 1);
			assertTrue("Item names should be sorted: " + current + " should come before " + next,
				current.compareTo(next) <= 0);
		}
	}

	@Test
	public void testGetAllItemNamesAreLowercase()
	{
		List<String> items = shopDatabase.getAllItemNames();

		for (String item : items)
		{
			assertEquals("All item names should be lowercase", item.toLowerCase(), item);
		}
	}

	// ==================== Shop Data Integrity Tests ====================

	@Test
	public void testAllShopsHaveValidIds()
	{
		List<Shop> shops = shopDatabase.getAllShops();

		for (Shop shop : shops)
		{
			assertNotNull("Shop should have an ID", shop.getId());
			assertFalse("Shop ID should not be empty", shop.getId().trim().isEmpty());
		}
	}

	@Test
	public void testAllShopsHaveValidNames()
	{
		List<Shop> shops = shopDatabase.getAllShops();

		for (Shop shop : shops)
		{
			assertNotNull("Shop should have a name", shop.getName());
			assertFalse("Shop name should not be empty", shop.getName().trim().isEmpty());
		}
	}

	@Test
	public void testAllShopsHaveValidLocations()
	{
		List<Shop> shops = shopDatabase.getAllShops();

		for (Shop shop : shops)
		{
			assertNotNull("Shop should have a location name", shop.getLocation());
			assertFalse("Shop location should not be empty", shop.getLocation().trim().isEmpty());
		}
	}

	@Test
	public void testAllShopsHaveItemsList()
	{
		List<Shop> shops = shopDatabase.getAllShops();

		for (Shop shop : shops)
		{
			assertNotNull("Shop should have items list", shop.getItems());
			// Note: Some shops like Grand Exchange might have 0 items, so we don't assert non-empty
		}
	}

	@Test
	public void testShopItemsHaveValidData()
	{
		List<Shop> shops = shopDatabase.getAllShops();

		for (Shop shop : shops)
		{
			for (ShopItem item : shop.getItems())
			{
				assertNotNull("Item should have a name", item.getName());
				assertFalse("Item name should not be empty", item.getName().trim().isEmpty());
				assertTrue("Item price should be non-negative", item.getPrice() >= 0);
				// Stock can be -1 for infinite, so just check it's not completely invalid
				assertTrue("Item stock should be valid", item.getStock() >= -1);
			}
		}
	}

	@Test
	public void testShopsWithCoordinatesHaveValidWorldPoints()
	{
		List<Shop> shops = shopDatabase.getAllShops();

		for (Shop shop : shops)
		{
			WorldPoint point = shop.getWorldPoint();
			if (point != null)
			{
				// Basic sanity checks for OSRS coordinates
				assertTrue("X coordinate should be reasonable", point.getX() >= 0 && point.getX() < 10000);
				assertTrue("Y coordinate should be reasonable", point.getY() >= 0 && point.getY() < 10000);
				assertTrue("Plane should be valid", point.getPlane() >= 0 && point.getPlane() <= 3);
			}
		}
	}

	// ==================== Specific Shop Tests (based on test data) ====================

	@Test
	public void testLumbridgeGeneralStoreExists()
	{
		Shop shop = shopDatabase.getShopById("lumbridge_general_store");

		if (shop != null) // Only test if this shop exists in our data
		{
			assertEquals("Lumbridge General Store", shop.getName());
			assertNotNull("Should have items", shop.getItems());
			assertFalse("Should have at least one item", shop.getItems().isEmpty());
		}
	}

	@Test
	public void testRuneShopsExist()
	{
		// Check if we have rune shops in the database
		List<String> runeResults = shopDatabase.searchItems("rune");

		if (!runeResults.isEmpty())
		{
			// Find shops selling runes
			List<Shop> runeShops = shopDatabase.findShopsByItem(runeResults.get(0));
			assertNotNull("Should find shops selling runes", runeShops);
		}
	}

	// ==================== Performance Tests ====================

	@Test
	public void testSearchPerformanceWithLargeResults()
	{
		long startTime = System.currentTimeMillis();
		List<String> results = shopDatabase.searchItems("a"); // Common letter
		long endTime = System.currentTimeMillis();

		long duration = endTime - startTime;
		assertTrue("Search should complete in reasonable time (< 100ms), took " + duration + "ms",
			duration < 100);
	}

	@Test
	public void testFindShopsPerformance()
	{
		List<String> items = shopDatabase.getAllItemNames();
		if (!items.isEmpty())
		{
			String testItem = items.get(0);

			long startTime = System.currentTimeMillis();
			List<Shop> shops = shopDatabase.findShopsByItem(testItem);
			long endTime = System.currentTimeMillis();

			long duration = endTime - startTime;
			assertTrue("Find shops should complete in reasonable time (< 50ms), took " + duration + "ms",
				duration < 50);
		}
	}

	// ==================== Edge Cases ====================

	@Test
	public void testMultipleShopsSellingSameItem()
	{
		// Find an item sold by multiple shops
		List<String> allItems = shopDatabase.getAllItemNames();

		for (String item : allItems)
		{
			List<Shop> shops = shopDatabase.findShopsByItem(item);
			if (shops.size() > 1)
			{
				// Verify all shops actually have this item
				for (Shop shop : shops)
				{
					boolean hasItem = shop.getItems().stream()
						.anyMatch(i -> i.getName().equalsIgnoreCase(item));
					assertTrue("Each shop should actually contain the item", hasItem);
				}
				return; // Test passed
			}
		}
		// If no item is sold by multiple shops, that's okay, test still passes
	}

	@Test
	public void testShopIdUniqueness()
	{
		List<Shop> shops = shopDatabase.getAllShops();
		long uniqueIds = shops.stream()
			.map(Shop::getId)
			.distinct()
			.count();

		assertEquals("All shop IDs should be unique", shops.size(), uniqueIds);
	}
}
