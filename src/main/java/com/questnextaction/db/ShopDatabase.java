package com.questnextaction.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Database of shops and items they sell
 * Similar to the approach used by "Not Enough Runes" plugin
 */
@Slf4j
@Singleton
public class ShopDatabase
{
	private static final String SHOPS_DATA_FILE = "/com/questnextaction/data/shops.json";

	private List<Shop> shops = new ArrayList<>();
	private Map<String, List<Shop>> itemToShopsMap = new HashMap<>();
	private Map<String, Shop> shopsByIdMap = new HashMap<>();

	public ShopDatabase()
	{
		loadShopsData();
	}

	/**
	 * Load shops data from JSON file
	 */
	private void loadShopsData()
	{
		try (InputStream is = ShopDatabase.class.getResourceAsStream(SHOPS_DATA_FILE))
		{
			if (is == null)
			{
				log.warn("Shops data file not found: {}", SHOPS_DATA_FILE);
				return;
			}

			BufferedReader reader = new BufferedReader(
				new InputStreamReader(is, StandardCharsets.UTF_8));

			Gson gson = new Gson();
			Type listType = new TypeToken<List<ShopData>>(){}.getType();
			List<ShopData> shopDataList = gson.fromJson(reader, listType);

			// Convert ShopData to Shop objects
			for (ShopData data : shopDataList)
			{
				Shop shop = new Shop();
				shop.setId(data.getId());
				shop.setName(data.getName());
				shop.setOwner(data.getOwner());
				shop.setLocation(data.getLocation());

				if (data.getX() != null && data.getY() != null && data.getPlane() != null)
				{
					shop.setCoordinates(data.getX(), data.getY(), data.getPlane());
				}

				shop.setItems(data.getItems());
				shops.add(shop);
				shopsByIdMap.put(shop.getId(), shop);

				// Index items for quick lookup
				for (ShopItem item : shop.getItems())
				{
					String itemName = item.getName().toLowerCase();
					itemToShopsMap.computeIfAbsent(itemName, k -> new ArrayList<>()).add(shop);
				}
			}

			log.debug("Loaded {} shops with {} unique items", shops.size(), itemToShopsMap.size());
		}
		catch (Exception e)
		{
			log.error("Failed to load shops data", e);
		}
	}

	/**
	 * Get all shops
	 */
	public List<Shop> getAllShops()
	{
		return new ArrayList<>(shops);
	}

	/**
	 * Get shop by ID
	 */
	public Shop getShopById(String id)
	{
		return shopsByIdMap.get(id);
	}

	/**
	 * Find shops that sell a specific item
	 */
	public List<Shop> findShopsByItem(String itemName)
	{
		return itemToShopsMap.getOrDefault(itemName.toLowerCase(), Collections.emptyList());
	}

	/**
	 * Search for items by name (partial match)
	 */
	public List<String> searchItems(String query)
	{
		if (query == null || query.trim().isEmpty())
		{
			return Collections.emptyList();
		}

		String lowerQuery = query.toLowerCase();
		return itemToShopsMap.keySet().stream()
			.filter(item -> item.contains(lowerQuery))
			.sorted()
			.collect(Collectors.toList());
	}

	/**
	 * Get all unique item names sold in shops
	 */
	public List<String> getAllItemNames()
	{
		return itemToShopsMap.keySet().stream()
			.sorted()
			.collect(Collectors.toList());
	}

	/**
	 * Data transfer object for JSON deserialization
	 */
	private static class ShopData
	{
		private String id;
		private String name;
		private String owner;
		private String location;
		private Integer x;
		private Integer y;
		private Integer plane;
		private List<ShopItem> items;

		public String getId() { return id; }
		public String getName() { return name; }
		public String getOwner() { return owner; }
		public String getLocation() { return location; }
		public Integer getX() { return x; }
		public Integer getY() { return y; }
		public Integer getPlane() { return plane; }
		public List<ShopItem> getItems() { return items; }
	}
}
