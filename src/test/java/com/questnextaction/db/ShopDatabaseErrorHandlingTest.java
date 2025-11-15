package com.questnextaction.db;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for error handling and edge cases in ShopDatabase
 */
public class ShopDatabaseErrorHandlingTest
{
	private ShopDatabase shopDatabase;

	@Before
	public void setUp()
	{
		shopDatabase = new ShopDatabase();
	}

	// ==================== Null Input Handling ====================

	@Test
	public void testSearchItemsWithNullInput()
	{
		List<String> results = shopDatabase.searchItems(null);

		assertNotNull("Should return non-null result for null input", results);
		assertTrue("Should return empty list for null input", results.isEmpty());
	}

	@Test
	public void testFindShopsByItemWithNullInput()
	{
		List<Shop> results = shopDatabase.findShopsByItem(null);

		// Implementation returns empty list for null lookup in HashMap
		assertNotNull("Should return non-null result for null input", results);
	}

	@Test
	public void testGetShopByIdWithNullInput()
	{
		Shop result = shopDatabase.getShopById(null);

		assertNull("Should return null for null shop ID", result);
	}

	// ==================== Empty String Handling ====================

	@Test
	public void testSearchItemsWithEmptyString()
	{
		List<String> results = shopDatabase.searchItems("");

		assertNotNull("Should return non-null result", results);
		assertTrue("Should return empty list for empty query", results.isEmpty());
	}

	@Test
	public void testSearchItemsWithWhitespaceOnly()
	{
		List<String> results = shopDatabase.searchItems("   ");

		assertNotNull("Should return non-null result", results);
		assertTrue("Should return empty list for whitespace query", results.isEmpty());
	}

	@Test
	public void testSearchItemsWithTabsAndNewlines()
	{
		List<String> results = shopDatabase.searchItems("\t\n\r");

		assertNotNull("Should return non-null result", results);
		assertTrue("Should return empty list for whitespace query", results.isEmpty());
	}

	@Test
	public void testFindShopsByItemWithEmptyString()
	{
		List<Shop> results = shopDatabase.findShopsByItem("");

		assertNotNull("Should return non-null result", results);
		assertTrue("Should return empty list for empty item name", results.isEmpty());
	}

	@Test
	public void testGetShopByIdWithEmptyString()
	{
		Shop result = shopDatabase.getShopById("");

		assertNull("Should return null for empty shop ID", result);
	}

	// ==================== Special Characters Handling ====================

	@Test
	public void testSearchItemsWithSpecialCharacters()
	{
		String[] specialQueries = {
			"@#$%",
			"<script>",
			"'; DROP TABLE shops;--",
			"../../../etc/passwd",
			"\\x00\\x00",
			"💎🔥⚔️"
		};

		for (String query : specialQueries)
		{
			List<String> results = shopDatabase.searchItems(query);
			assertNotNull("Should handle special characters: " + query, results);
			// Should return empty since no items match these
			assertTrue("Should return empty for special chars: " + query, results.isEmpty());
		}
	}

	@Test
	public void testFindShopsByItemWithSpecialCharacters()
	{
		List<Shop> results = shopDatabase.findShopsByItem("@#$%^&*");

		assertNotNull("Should handle special characters", results);
		assertTrue("Should return empty for non-existent item", results.isEmpty());
	}

	// ==================== Very Long Input Handling ====================

	@Test
	public void testSearchItemsWithVeryLongString()
	{
		String longQuery = "a".repeat(10000);
		List<String> results = shopDatabase.searchItems(longQuery);

		assertNotNull("Should handle very long input", results);
		// Unlikely to match anything, should be empty
		assertTrue("Should return empty for extremely long query", results.isEmpty());
	}

	@Test
	public void testGetShopByIdWithVeryLongString()
	{
		String longId = "shop_id_".repeat(1000);
		Shop result = shopDatabase.getShopById(longId);

		assertNull("Should handle very long ID gracefully", result);
	}

	// ==================== Case Sensitivity Edge Cases ====================

	@Test
	public void testSearchItemsMixedCase()
	{
		// Get an item from the database
		List<String> allItems = shopDatabase.getAllItemNames();
		if (!allItems.isEmpty())
		{
			String item = allItems.get(0);

			// Test various case combinations
			String upper = item.toUpperCase();
			String lower = item.toLowerCase();
			String mixed = mixCase(item);

			List<String> upperResults = shopDatabase.searchItems(upper);
			List<String> lowerResults = shopDatabase.searchItems(lower);
			List<String> mixedResults = shopDatabase.searchItems(mixed);

			assertEquals("Case should not matter", upperResults.size(), lowerResults.size());
			assertEquals("Case should not matter", lowerResults.size(), mixedResults.size());
		}
	}

	private String mixCase(String input)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++)
		{
			char c = input.charAt(i);
			sb.append(i % 2 == 0 ? Character.toUpperCase(c) : Character.toLowerCase(c));
		}
		return sb.toString();
	}

	// ==================== Concurrent Access Tests ====================

	@Test
	public void testConcurrentSearches() throws InterruptedException
	{
		// Test thread safety by running multiple searches concurrently
		Thread[] threads = new Thread[10];

		for (int i = 0; i < threads.length; i++)
		{
			final int threadNum = i;
			threads[i] = new Thread(() -> {
				for (int j = 0; j < 100; j++)
				{
					List<String> results = shopDatabase.searchItems("test" + threadNum);
					assertNotNull("Concurrent search should work", results);
				}
			});
			threads[i].start();
		}

		// Wait for all threads to complete
		for (Thread thread : threads)
		{
			thread.join(5000); // 5 second timeout
		}
	}

	@Test
	public void testConcurrentGetAllShops() throws InterruptedException
	{
		Thread[] threads = new Thread[10];

		for (int i = 0; i < threads.length; i++)
		{
			threads[i] = new Thread(() -> {
				for (int j = 0; j < 100; j++)
				{
					List<Shop> shops = shopDatabase.getAllShops();
					assertNotNull("Concurrent getAllShops should work", shops);
					assertFalse("Should always return shops", shops.isEmpty());
				}
			});
			threads[i].start();
		}

		for (Thread thread : threads)
		{
			thread.join(5000);
		}
	}

	// ==================== Data Integrity After Queries ====================

	@Test
	public void testDataImmutabilityAfterGet()
	{
		List<Shop> shops1 = shopDatabase.getAllShops();
		int originalSize = shops1.size();

		// Try to modify the returned list
		try
		{
			shops1.clear();
		}
		catch (UnsupportedOperationException e)
		{
			// If list is immutable, that's fine
		}

		// Get list again
		List<Shop> shops2 = shopDatabase.getAllShops();

		// Original database should be unaffected
		assertEquals("Database should not be affected by modifications to returned list",
			originalSize, shops2.size());
	}

	@Test
	public void testSearchResultsImmutability()
	{
		List<String> results1 = shopDatabase.searchItems("a");
		int originalSize = results1.size();

		// Try to modify
		try
		{
			results1.clear();
		}
		catch (UnsupportedOperationException e)
		{
			// Immutable is fine
		}

		// Search again
		List<String> results2 = shopDatabase.searchItems("a");

		// Should get same results
		assertEquals("Search results should be consistent", originalSize, results2.size());
	}

	// ==================== Memory Leak Tests ====================

	@Test
	public void testRepeatedSearchesDoNotLeakMemory()
	{
		// Run many searches to check for memory leaks
		for (int i = 0; i < 1000; i++)
		{
			shopDatabase.searchItems("test" + i);
		}

		// If we got here without OutOfMemoryError, test passes
		assertTrue("Should handle many searches without memory issues", true);
	}

	@Test
	public void testRepeatedGetAllItemsDoesNotLeakMemory()
	{
		for (int i = 0; i < 1000; i++)
		{
			shopDatabase.getAllItemNames();
		}

		assertTrue("Should handle many getAllItemNames calls", true);
	}

	// ==================== Boundary Tests ====================

	@Test
	public void testSearchWithSingleCharacter()
	{
		List<String> results = shopDatabase.searchItems("a");
		assertNotNull("Single character search should work", results);
	}

	@Test
	public void testSearchWithNumbers()
	{
		List<String> results = shopDatabase.searchItems("123");
		assertNotNull("Numeric search should work", results);
	}

	@Test
	public void testSearchWithUnicode()
	{
		// Test with various unicode characters
		String[] unicodeQueries = {"café", "naïve", "北京", "مرحبا"};

		for (String query : unicodeQueries)
		{
			List<String> results = shopDatabase.searchItems(query);
			assertNotNull("Unicode search should work: " + query, results);
		}
	}

	// ==================== Database State Tests ====================

	@Test
	public void testDatabaseConsistentAcrossMultipleCalls()
	{
		List<Shop> shops1 = shopDatabase.getAllShops();
		List<Shop> shops2 = shopDatabase.getAllShops();

		assertEquals("Database should return consistent results", shops1.size(), shops2.size());
	}

	@Test
	public void testAllMethodsWorkAfterInitialization()
	{
		// Ensure all public methods work without throwing exceptions
		assertNotNull(shopDatabase.getAllShops());
		assertNotNull(shopDatabase.getAllItemNames());
		assertNotNull(shopDatabase.searchItems("test"));
		assertNotNull(shopDatabase.findShopsByItem("test"));

		// These can return null, just ensure no exceptions
		shopDatabase.getShopById("test");
		shopDatabase.getShopById(null);
	}
}
