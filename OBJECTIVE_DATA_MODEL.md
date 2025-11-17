# Objective Data Model Documentation

This document describes the complete data model for objectives in the RuneLite Objective Tracker plugin.

## Overview

The `Objective` class represents a trackable objective with support for multiple location types and detailed shop information for BUY objectives.

---

## Core Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | String | Yes | Unique identifier for the objective |
| `type` | ObjectiveType | Yes | Type of objective (TALK, BUY, COLLECT, etc.) |
| `task` | String | Yes | Description of what needs to be done |
| `locationName` | String | Yes | Display name of location |
| `location` | WorldPoint | No | Primary world coordinates |
| `possibleLocations` | List<WorldPoint> | No | Alternative locations (backward compatibility) |
| `regionId` | int | Yes | RuneLite region ID for map rendering |
| `active` | boolean | Yes | Whether objective is currently being tracked |
| `itemName` | String | No | For BUY objectives - the item name |
| `quantity` | Integer | No | For BUY/COLLECT objectives - how many items |
| `shopLocations` | List<ShopLocation> | No | **NEW** - Detailed shop information with pricing |

---

## ShopLocation Model

The `ShopLocation` class provides detailed information about shops selling an item:

| Field | Type | Description |
|-------|------|-------------|
| `shopId` | String | Unique shop identifier |
| `shopName` | String | Display name (e.g., "Lumbridge General Store") |
| `ownerName` | String | Shop owner NPC name |
| `locationName` | String | Location (e.g., "Lumbridge", "Varrock") |
| `worldPoint` | WorldPoint | Shop coordinates |
| `price` | int | Item price at this shop (in coins) |
| `stock` | int | Available stock (-1 for unlimited) |

### ShopLocation Methods

```java
// Check if shop has enough stock
boolean hasSufficientStock(int requiredQuantity)

// Calculate total cost for quantity
int calculateTotalCost(int quantity)

// Get formatted summary
String getSummary()  // Returns: "Lumbridge General Store (Lumbridge) - 1 gp"
```

---

## Complete JSON Example - BUY Objective

Here's a complete example of a BUY objective with shop pricing information:

```json
{
  "id": "buy_pot_5",
  "type": "BUY",
  "task": "Buy 5x Pot",
  "locationName": "2 shops",
  "location": {
    "x": 3212,
    "y": 3247,
    "plane": 0
  },
  "possibleLocations": [
    {"x": 3212, "y": 3247, "plane": 0},
    {"x": 3217, "y": 3412, "plane": 0}
  ],
  "regionId": 12850,
  "active": false,
  "itemName": "Pot",
  "quantity": 5,
  "shopLocations": [
    {
      "shopId": "lumbridge_general_store",
      "shopName": "Lumbridge General Store",
      "ownerName": "Shop keeper",
      "locationName": "Lumbridge",
      "worldPoint": {
        "x": 3212,
        "y": 3247,
        "plane": 0
      },
      "price": 1,
      "stock": 5
    },
    {
      "shopId": "varrock_general_store",
      "shopName": "Varrock General Store",
      "ownerName": "Shop keeper",
      "locationName": "Varrock",
      "worldPoint": {
        "x": 3217,
        "y": 3412,
        "plane": 0
      },
      "price": 1,
      "stock": 5
    }
  ]
}
```

---

## Helper Methods on Objective

### Shop-Related Methods

```java
// Get best shop based on player position (closest) or price
ShopLocation getBestShopLocation(WorldPoint playerPosition)

// Get the cheapest shop
ShopLocation getCheapestShop()

// Get total cost at cheapest shop
int getCheapestTotalCost()

// Get shops with sufficient stock
List<ShopLocation> getShopsWithSufficientStock()
```

### Location-Related Methods

```java
// Get best location (for backward compatibility)
WorldPoint getBestLocation(WorldPoint playerPosition)

// Get region ID for a location
int getRegionIdForLocation(WorldPoint worldPoint)
```

---

## Debug Output Example

When you view an objective in the test harness debug panel, you'll see:

```
───────────────────────────────────────────────────────────────
  Objective #1
───────────────────────────────────────────────────────────────
  ID:               buy_pot_5
  Type:             BUY
  Task:             Buy 5x Pot
  Location Name:    2 shops
  Active:           NO ○
  Location:         (3212, 3247, 0)
  Region ID:        12850
  Item Name:        Pot
  Quantity:         5
  Possible Locs:    2 location(s)
    [1] (3212, 3247, 0)
    [2] (3217, 3412, 0)
  Shop Details:     2 shop(s)
    [1] Lumbridge General Store
        Owner:    Shop keeper
        Location: Lumbridge
        Price:    1 gp
        Stock:    5
        Coords:   (3212, 3247, 0)
        Total:    5 gp (in stock)
    [2] Varrock General Store
        Owner:    Shop keeper
        Location: Varrock
        Price:    1 gp
        Stock:    5
        Coords:   (3217, 3412, 0)
        Total:    5 gp (in stock)
    Cheapest:     Lumbridge General Store - 1 gp (Total: 5 gp)
```

---

## Other Objective Type Examples

### TALK Objective

```json
{
  "id": "cooks_assistant_1",
  "type": "TALK",
  "task": "Talk to Cook",
  "locationName": "Lumbridge Castle",
  "location": {"x": 3207, "y": 3214, "plane": 0},
  "possibleLocations": null,
  "regionId": 12850,
  "active": false,
  "itemName": null,
  "quantity": null,
  "shopLocations": null
}
```

### COLLECT Objective

```json
{
  "id": "collect_wool",
  "type": "COLLECT",
  "task": "Collect 20 wool",
  "locationName": "Lumbridge sheep pen",
  "location": {"x": 3209, "y": 3259, "plane": 0},
  "possibleLocations": null,
  "regionId": 12851,
  "active": false,
  "itemName": null,
  "quantity": 20,
  "shopLocations": null
}
```

### TRAVEL Objective

```json
{
  "id": "go_to_varrock",
  "type": "TRAVEL",
  "task": "Visit Varrock Square",
  "locationName": "Varrock Square",
  "location": {"x": 3211, "y": 3422, "plane": 0},
  "possibleLocations": null,
  "regionId": 12853,
  "active": false,
  "itemName": null,
  "quantity": null,
  "shopLocations": null
}
```

---

## Objective Types

```java
public enum ObjectiveType {
    TALK,      // Talk to an NPC
    TRAVEL,    // Go to a location
    COLLECT,   // Obtain or collect items
    KILL,      // Kill NPCs/monsters
    USE,       // Use an item or object
    SKILL,     // Skill training objective
    BUY,       // Buy an item from a shop (uses shopLocations)
    OTHER      // General objective
}
```

---

## Usage Examples

### Creating a BUY Objective with Shop Data

```java
// Find shops selling the item
List<Shop> shops = shopDatabase.findShopsByItem("Pot");

Objective.ObjectiveBuilder builder = Objective.builder()
    .id("buy_pot_5")
    .type(ObjectiveType.BUY)
    .task("Buy 5x Pot")
    .locationName("2 shops")
    .location(shops.get(0).getWorldPoint())
    .regionId(shops.get(0).getWorldPoint().getRegionID())
    .active(false)
    .itemName("Pot")
    .quantity(5);

// Add shop location data with pricing
for (Shop shop : shops) {
    ShopItem item = shop.getItems().stream()
        .filter(i -> i.getName().equals("Pot"))
        .findFirst()
        .orElse(null);

    if (item != null) {
        ShopLocation shopLoc = ShopLocation.builder()
            .shopId(shop.getId())
            .shopName(shop.getName())
            .ownerName(shop.getOwner())
            .locationName(shop.getLocation())
            .worldPoint(shop.getWorldPoint())
            .price(item.getPrice())
            .stock(item.getStock())
            .build();

        builder.shopLocation(shopLoc);
    }
}

Objective objective = builder.build();
```

### Finding the Cheapest Shop

```java
Objective objective = // ... get objective

ShopLocation cheapest = objective.getCheapestShop();
if (cheapest != null) {
    System.out.println("Cheapest: " + cheapest.getShopName());
    System.out.println("Price: " + cheapest.getPrice() + " gp");
    System.out.println("Total: " + objective.getCheapestTotalCost() + " gp");
}
```

### Getting Closest Shop

```java
WorldPoint playerPos = new WorldPoint(3200, 3200, 0);
ShopLocation nearest = objective.getBestShopLocation(playerPos);
if (nearest != null) {
    System.out.println("Nearest shop: " + nearest.getShopName());
    System.out.println("Distance: " +
        nearest.getWorldPoint().distanceTo(playerPos));
}
```

### Checking Stock Availability

```java
List<ShopLocation> inStock = objective.getShopsWithSufficientStock();
System.out.println("Shops with stock: " + inStock.size());

for (ShopLocation shop : inStock) {
    System.out.println("- " + shop.getSummary());
}
```

---

## Backward Compatibility

The data model maintains backward compatibility:

- `possibleLocations` - Still populated for all multi-location objectives
- `shopLocations` - NEW field only populated for BUY objectives with shop data
- Existing code using `getBestLocation()` continues to work
- New code can use `getBestShopLocation()` for richer data

---

## File Locations

- **Objective.java**: `/home/user/rl-plugin/src/main/java/com/questnextaction/Objective.java`
- **ShopLocation.java**: `/home/user/rl-plugin/src/main/java/com/questnextaction/ShopLocation.java`
- **ObjectiveType.java**: `/home/user/rl-plugin/src/main/java/com/questnextaction/ObjectiveType.java`
- **Shop Data**: `/home/user/rl-plugin/src/main/resources/com/questnextaction/data/shops.json`

---

## Testing

View objective data in the test harness:

```bash
./gradlew runTestHarness
```

1. Click '+' to add an objective
2. Search for an item (e.g., "Pot")
3. Set quantity
4. Click "Add Objective"
5. View detailed data in the debug panel (right side)
6. Click "Export" to save debug output to file
7. Click "Copy" to copy to clipboard

The debug view shows all fields including shop pricing, stock, and cost calculations!
