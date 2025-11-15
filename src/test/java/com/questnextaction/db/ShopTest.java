package com.questnextaction.db;

import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for Shop model
 */
public class ShopTest
{
	@Test
	public void testShopCreation()
	{
		Shop shop = new Shop();
		assertNotNull("Shop should be created", shop);
	}

	@Test
	public void testShopSettersAndGetters()
	{
		Shop shop = new Shop();

		shop.setId("test_shop");
		shop.setName("Test Shop");
		shop.setOwner("Test Owner");
		shop.setLocation("Test Location");

		assertEquals("test_shop", shop.getId());
		assertEquals("Test Shop", shop.getName());
		assertEquals("Test Owner", shop.getOwner());
		assertEquals("Test Location", shop.getLocation());
	}

	@Test
	public void testShopWithNullOwner()
	{
		Shop shop = new Shop();
		shop.setOwner(null);

		assertNull("Owner can be null", shop.getOwner());
	}

	@Test
	public void testShopWithNullWorldPoint()
	{
		Shop shop = new Shop();
		shop.setWorldPoint(null);

		assertNull("WorldPoint can be null", shop.getWorldPoint());
	}

	@Test
	public void testShopSetCoordinates()
	{
		Shop shop = new Shop();
		shop.setCoordinates(3200, 3400, 0);

		assertNotNull("WorldPoint should be set", shop.getWorldPoint());
		assertEquals("X coordinate should match", 3200, shop.getWorldPoint().getX());
		assertEquals("Y coordinate should match", 3400, shop.getWorldPoint().getY());
		assertEquals("Plane should match", 0, shop.getWorldPoint().getPlane());
	}

	@Test
	public void testShopWithItems()
	{
		Shop shop = new Shop();

		ShopItem item1 = new ShopItem();
		item1.setName("Item 1");
		item1.setPrice(100);

		ShopItem item2 = new ShopItem();
		item2.setName("Item 2");
		item2.setPrice(200);

		List<ShopItem> items = Arrays.asList(item1, item2);
		shop.setItems(items);

		assertNotNull("Items should be set", shop.getItems());
		assertEquals("Should have 2 items", 2, shop.getItems().size());
		assertEquals("Item 1", shop.getItems().get(0).getName());
		assertEquals("Item 2", shop.getItems().get(1).getName());
	}

	@Test
	public void testShopWithEmptyItems()
	{
		Shop shop = new Shop();
		shop.setItems(new ArrayList<>());

		assertNotNull("Items list should not be null", shop.getItems());
		assertTrue("Items list should be empty", shop.getItems().isEmpty());
	}

	@Test
	public void testShopEquality()
	{
		Shop shop1 = new Shop();
		shop1.setId("shop1");
		shop1.setName("Shop 1");
		shop1.setLocation("Location 1");
		shop1.setItems(new ArrayList<>());

		Shop shop2 = new Shop();
		shop2.setId("shop1");
		shop2.setName("Shop 1");
		shop2.setLocation("Location 1");
		shop2.setItems(new ArrayList<>());

		// Note: Lombok @Data generates equals/hashCode
		assertEquals("Shops with same data should be equal", shop1, shop2);
		assertEquals("Hash codes should match", shop1.hashCode(), shop2.hashCode());
	}

	@Test
	public void testShopToString()
	{
		Shop shop = new Shop();
		shop.setId("test_shop");
		shop.setName("Test Shop");

		String toString = shop.toString();
		assertNotNull("toString should not be null", toString);
		assertTrue("toString should contain shop ID", toString.contains("test_shop"));
		assertTrue("toString should contain shop name", toString.contains("Test Shop"));
	}

	@Test
	public void testShopWithDifferentPlanes()
	{
		Shop shop = new Shop();

		// Test ground floor
		shop.setCoordinates(3200, 3400, 0);
		assertEquals(0, shop.getWorldPoint().getPlane());

		// Test first floor
		shop.setCoordinates(3200, 3400, 1);
		assertEquals(1, shop.getWorldPoint().getPlane());

		// Test underground
		shop.setCoordinates(3200, 3400, 3);
		assertEquals(3, shop.getWorldPoint().getPlane());
	}

	@Test
	public void testShopCoordinatesOverwrite()
	{
		Shop shop = new Shop();

		shop.setCoordinates(100, 200, 0);
		assertEquals(100, shop.getWorldPoint().getX());

		// Overwrite with new coordinates
		shop.setCoordinates(300, 400, 1);
		assertEquals(300, shop.getWorldPoint().getX());
		assertEquals(400, shop.getWorldPoint().getY());
		assertEquals(1, shop.getWorldPoint().getPlane());
	}
}
