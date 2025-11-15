package com.questnextaction.db;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Integration tests for ShopDatabase with real data
 * These tests verify the actual shops.json data is loaded correctly
 */
public class ShopDatabaseIntegrationTest
{
	private ShopDatabase shopDatabase;

	@Before
	public void setUp()
	{
		shopDatabase = new ShopDatabase();
	}

	// ==================== Data Completeness Tests ====================

	@Test
	public void testExpectedShopsAreLoaded()
	{
		List<Shop> shops = shopDatabase.getAllShops();

		// Verify we have a reasonable number of shops
		assertTrue("Should have at least 5 shops loaded", shops.size() >= 5);
		assertTrue("Should not have an unreasonable number of shops", shops.size() < 1000);
	}

	@Test
	public void testCommonItemsAreAvailable()
	{
		// Test for common F2P items that should be in general stores
		String[] commonItems = {"pot", "bucket", "tinderbox"};

		for (String item : commonItems)
		{
			List<String> results = shopDatabase.searchItems(item);
			assertFalse("Should find '" + item + "' in database", results.isEmpty());
		}
	}

	@Test
	public void testRunesAreAvailable()
	{
		// Rune shops should exist with various runes
		List<String> runeResults = shopDatabase.searchItems("rune");

		assertFalse("Should find runes in database", runeResults.isEmpty());

		// Check for specific runes
		boolean hasAirRune = runeResults.stream()
			.anyMatch(item -> item.contains("air"));
		boolean hasFireRune = runeResults.stream()
			.anyMatch(item -> item.contains("fire"));

		assertTrue("Should have air runes available", hasAirRune);
		assertTrue("Should have fire runes available", hasFireRune);
	}

	// ==================== Data Consistency Tests ====================

	@Test
	public void testNoOrphanedItems()
	{
		// Every item should be associated with at least one shop
		List<String> allItems = shopDatabase.getAllItemNames();

		for (String item : allItems)
		{
			List<Shop> shops = shopDatabase.findShopsByItem(item);
			assertFalse("Item '" + item + "' should be sold by at least one shop",
				shops.isEmpty());
		}
	}

	@Test
	public void testShopItemsAreIndexedCorrectly()
	{
		// Verify that items are properly indexed in the database
		List<Shop> allShops = shopDatabase.getAllShops();

		for (Shop shop : allShops)
		{
			for (ShopItem item : shop.getItems())
			{
				String itemName = item.getName().toLowerCase();
				List<Shop> foundShops = shopDatabase.findShopsByItem(itemName);

				boolean shopFound = foundShops.stream()
					.anyMatch(s -> s.getId().equals(shop.getId()));

				assertTrue("Shop '" + shop.getName() + "' should be findable by item '" +
					item.getName() + "'", shopFound);
			}
		}
	}

	// ==================== Real-World Usage Scenarios ====================

	@Test
	public void testFindCheapestShopForItem()
	{
		// Simulate finding the cheapest shop for an item sold by multiple shops
		List<String> allItems = shopDatabase.getAllItemNames();

		for (String itemName : allItems)
		{
			List<Shop> shops = shopDatabase.findShopsByItem(itemName);

			if (shops.size() > 1)
			{
				// Find cheapest price
				int minPrice = Integer.MAX_VALUE;
				Shop cheapestShop = null;

				for (Shop shop : shops)
				{
					for (ShopItem item : shop.getItems())
					{
						if (item.getName().equalsIgnoreCase(itemName))
						{
							if (item.getPrice() < minPrice)
							{
								minPrice = item.getPrice();
								cheapestShop = shop;
							}
						}
					}
				}

				assertNotNull("Should find cheapest shop for " + itemName, cheapestShop);
				assertTrue("Price should be positive", minPrice >= 0);

				// Log for debugging
				System.out.println("Cheapest shop for " + itemName + ": " +
					cheapestShop.getName() + " (" + minPrice + " gp)");

				return; // Test passed with at least one multi-shop item
			}
		}
	}

	@Test
	public void testSearchWorkflow()
	{
		// Simulate user typing in search box
		String[] searchProgression = {"r", "ru", "run", "rune"};

		int lastResultCount = Integer.MAX_VALUE;

		for (String query : searchProgression)
		{
			List<String> results = shopDatabase.searchItems(query);
			assertNotNull("Results should not be null for query: " + query, results);

			// Results should get more specific (or stay same) as query gets longer
			assertTrue("Results should narrow or stay same as query lengthens",
				results.size() <= lastResultCount);

			lastResultCount = results.size();
		}
	}

	@Test
	public void testBuyObjectiveCreationWorkflow()
	{
		// Simulate the workflow of creating a buy objective
		// 1. User searches for item
		List<String> itemResults = shopDatabase.searchItems("sword");

		if (!itemResults.isEmpty())
		{
			// 2. User selects an item
			String selectedItem = itemResults.get(0);

			// 3. Find shops selling that item
			List<Shop> shops = shopDatabase.findShopsByItem(selectedItem);
			assertFalse("Should find shops selling " + selectedItem, shops.isEmpty());

			// 4. User selects a shop
			Shop selectedShop = shops.get(0);

			// 5. Get shop location
			assertNotNull("Shop should have a location name", selectedShop.getLocation());

			// If shop has coordinates, they should be valid
			if (selectedShop.getWorldPoint() != null)
			{
				assertTrue("Shop should have valid coordinates",
					selectedShop.getWorldPoint().getX() > 0);
			}

			System.out.println("Buy objective workflow test passed:");
			System.out.println("  Item: " + selectedItem);
			System.out.println("  Shop: " + selectedShop.getName());
			System.out.println("  Location: " + selectedShop.getLocation());
		}
	}

	// ==================== Data Quality Tests ====================

	@Test
	public void testNoEmptyShopNames()
	{
		List<Shop> shops = shopDatabase.getAllShops();

		for (Shop shop : shops)
		{
			assertNotNull("Shop name should not be null", shop.getName());
			assertFalse("Shop name should not be empty: " + shop.getId(),
				shop.getName().trim().isEmpty());
		}
	}

	@Test
	public void testNoEmptyItemNames()
	{
		List<Shop> shops = shopDatabase.getAllShops();

		for (Shop shop : shops)
		{
			for (ShopItem item : shop.getItems())
			{
				assertNotNull("Item name should not be null in shop " + shop.getName(),
					item.getName());
				assertFalse("Item name should not be empty in shop " + shop.getName(),
					item.getName().trim().isEmpty());
			}
		}
	}

	@Test
	public void testReasonablePrices()
	{
		List<Shop> shops = shopDatabase.getAllShops();

		for (Shop shop : shops)
		{
			for (ShopItem item : shop.getItems())
			{
				assertTrue("Item price should be non-negative: " + item.getName() +
					" in " + shop.getName(), item.getPrice() >= 0);

				// Most items should cost less than max cash stack
				assertTrue("Item price should be reasonable: " + item.getName() +
					" costs " + item.getPrice(), item.getPrice() < Integer.MAX_VALUE);
			}
		}
	}

	@Test
	public void testReasonableStock()
	{
		List<Shop> shops = shopDatabase.getAllShops();

		for (Shop shop : shops)
		{
			for (ShopItem item : shop.getItems())
			{
				// Stock should be -1 (infinite) or a reasonable positive number
				assertTrue("Item stock should be valid: " + item.getName() +
						" in " + shop.getName() + " has stock " + item.getStock(),
					item.getStock() >= -1 && item.getStock() < 100000);
			}
		}
	}

	// ==================== Coverage Tests ====================

	@Test
	public void testMultipleCitiesCovered()
	{
		List<Shop> shops = shopDatabase.getAllShops();
		Set<String> locations = new HashSet<>();

		for (Shop shop : shops)
		{
			locations.add(shop.getLocation().toLowerCase());
		}

		// Should have shops in multiple locations
		assertTrue("Should have shops in at least 2 cities", locations.size() >= 2);

		System.out.println("Cities with shops: " + locations);
	}

	@Test
	public void testMultipleShopTypesCovered()
	{
		List<Shop> shops = shopDatabase.getAllShops();
		Set<String> shopTypes = new HashSet<>();

		for (Shop shop : shops)
		{
			String name = shop.getName().toLowerCase();

			// Categorize shops
			if (name.contains("general")) shopTypes.add("general");
			if (name.contains("rune") || name.contains("magic")) shopTypes.add("magic");
			if (name.contains("combat") || name.contains("sword")) shopTypes.add("combat");
			if (name.contains("staff")) shopTypes.add("staffs");
			if (name.contains("baker") || name.contains("food")) shopTypes.add("food");
		}

		// Should have multiple types of shops
		assertTrue("Should have at least 2 different shop types", shopTypes.size() >= 2);

		System.out.println("Shop types covered: " + shopTypes);
	}

	// ==================== Performance with Real Data ====================

	@Test
	public void testLargeScaleSearch()
	{
		// Search for every letter to test performance
		for (char c = 'a'; c <= 'z'; c++)
		{
			List<String> results = shopDatabase.searchItems(String.valueOf(c));
			assertNotNull("Search should work for letter " + c, results);
		}
	}

	@Test
	public void testRepeatedQueries()
	{
		// Simulate repeated queries (caching test)
		for (int i = 0; i < 100; i++)
		{
			List<Shop> shops = shopDatabase.getAllShops();
			assertNotNull(shops);
			assertFalse(shops.isEmpty());
		}
	}
}
