package com.questnextaction.db;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests specifically for shop coordinate accuracy and item queries
 */
public class ShopCoordinateTest
{
	private ShopDatabase shopDatabase;

	@Before
	public void setUp()
	{
		shopDatabase = new ShopDatabase();
	}

	@Test
	public void testVarrockSwordShopCoordinates()
	{
		Shop shop = shopDatabase.getShopById("varrock_sword_shop");

		assertNotNull("Varrock Sword Shop should exist", shop);
		assertEquals("Varrock Sword Shop", shop.getName());
		assertNotNull("Should have coordinates", shop.getWorldPoint());
		assertEquals("X coordinate", 3167, shop.getWorldPoint().getX());
		assertEquals("Y coordinate", 3418, shop.getWorldPoint().getY());
		assertEquals("Plane", 0, shop.getWorldPoint().getPlane());
	}

	@Test
	public void testAdamantSwordAvailability()
	{
		List<Shop> shops = shopDatabase.findShopsByItem("adamant sword");

		assertNotNull("Result should not be null", shops);
		assertFalse("Should find shops selling adamant sword", shops.isEmpty());

		// Verify it's the Varrock Sword Shop
		boolean foundVarrockSwordShop = shops.stream()
			.anyMatch(shop -> shop.getId().equals("varrock_sword_shop"));

		assertTrue("Varrock Sword Shop should sell adamant sword", foundVarrockSwordShop);

		// Print shop details for debugging
		for (Shop shop : shops)
		{
			System.out.println("Shop: " + shop.getName());
			System.out.println("Location: " + shop.getLocation());
			if (shop.getWorldPoint() != null)
			{
				System.out.println("Coordinates: " + shop.getWorldPoint().getX() +
					", " + shop.getWorldPoint().getY() +
					", plane " + shop.getWorldPoint().getPlane());
			}
			System.out.println("---");
		}
	}

	@Test
	public void testAllShopsHaveValidCoordinates()
	{
		List<Shop> allShops = shopDatabase.getAllShops();

		for (Shop shop : allShops)
		{
			// Skip Grand Exchange which might not have items
			if (shop.getId().equals("grand_exchange"))
			{
				continue;
			}

			if (!shop.getItems().isEmpty())
			{
				assertNotNull("Shop " + shop.getName() + " should have coordinates",
					shop.getWorldPoint());

				int x = shop.getWorldPoint().getX();
				int y = shop.getWorldPoint().getY();
				int plane = shop.getWorldPoint().getPlane();

				// Verify coordinates are reasonable for OSRS
				assertTrue("Shop " + shop.getName() + " X coordinate should be valid (got " + x + ")",
					x >= 1000 && x <= 4000);
				assertTrue("Shop " + shop.getName() + " Y coordinate should be valid (got " + y + ")",
					y >= 1000 && y <= 4000);
				assertTrue("Shop " + shop.getName() + " plane should be valid (got " + plane + ")",
					plane >= 0 && plane <= 3);

				System.out.println(shop.getName() + ": " + x + ", " + y + ", plane " + plane);
			}
		}
	}
}
