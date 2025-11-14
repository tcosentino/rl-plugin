# Quest Next Action Plugin

A RuneLite plugin that helps you track the next action needed for each quest and displays them on the map.

## Overview

Unlike traditional quest helpers that provide step-by-step guidance through entire quests, this plugin focuses on tracking just **the next action** needed for each quest you're working on. This gives you a streamlined view of what to do next without cluttering your interface with detailed step guides.

## Features

### Quest Tracking Panel
- **Sidebar Panel**: View all available quests in a clean, organized list
- **Click to Track**: Simply click on any quest to start tracking it
- **Action Details**: See the quest name, action type, description, and optional hints
- **Visual Feedback**: Active quests are highlighted in green

### Map Integration
- **World Map Markers**: Quest action locations appear as markers on the world map with tooltips
- **Minimap Indicators**: Cyan circular markers show quest locations on your minimap
- **Scene Highlights**: Active quest locations are highlighted in the game world with colored tiles
- **Smart Filtering**: Only shows markers for quests on your current plane

### Action Types
The plugin categorizes quest actions into different types:
- Talk to NPC
- Go to Location
- Use Item
- Obtain Item
- Kill NPC
- Use Object
- Equip Item
- Enter Area
- Complete Puzzle
- Other

## Configuration

The plugin includes several configuration options:

- **Show World Map Icons**: Toggle world map markers on/off
- **Show Minimap Icons**: Toggle minimap indicators on/off
- **Show Scene Highlights**: Toggle in-game world highlights on/off
- **Highlight Color**: Customize the color of quest markers (default: Cyan)
- **Show Hints**: Toggle helpful hints for quest actions

## Usage

1. **Open the Plugin Panel**: Click the Quest Next Action icon in the RuneLite sidebar
2. **Browse Available Quests**: Scroll through the list of quests
3. **Track a Quest**: Click on a quest card to start tracking it (turns green)
4. **View on Map**: The next action location will appear on your world map, minimap, and in the game world
5. **Untrack**: Click the quest again to stop tracking it

## Sample Quests

The plugin comes with sample data for these quests:
- Cook's Assistant
- Sheep Shearer
- Romeo & Juliet
- Rune Mysteries
- The Restless Ghost

## Extending the Plugin

### Adding New Quest Actions

Quest data is managed in `QuestActionManager.java`. To add a new quest action, add it to the `loadQuestData()` method:

```java
questActions.put("Quest Name", QuestAction.builder()
    .questName("Quest Name")
    .actionType(QuestActionType.TALK_TO_NPC)
    .description("What you need to do")
    .location(new WorldPoint(x, y, plane))
    .hint("Optional helpful hint")
    .regionId(regionId)
    .active(false)
    .build());
```

### Updating Quest Actions Dynamically

You can update quest actions at runtime using:

```java
questActionManager.updateQuestAction(
    "Quest Name",
    QuestActionType.USE_ITEM,
    "New description",
    newLocation,
    "New hint"
);
```

## Architecture

### Core Components

- **QuestAction**: Data model representing a single quest action
- **QuestActionType**: Enum of possible action types
- **QuestActionManager**: Manages quest data and active tracking state
- **QuestNextActionPlugin**: Main plugin class coordinating all components
- **QuestNextActionConfig**: Configuration interface

### UI Components

- **QuestNextActionPanel**: Sidebar panel displaying quest list
- **QuestActionBox**: Individual quest card in the panel

### Overlays

- **QuestActionWorldMapPoint**: World map markers
- **QuestActionMinimapOverlay**: Minimap indicators
- **QuestActionSceneOverlay**: In-game scene highlights

## Building

```bash
gradle build
```

## Installation

1. Build the plugin using Gradle
2. Copy the JAR from `build/libs/` to your RuneLite plugins folder
3. Restart RuneLite
4. Enable the plugin in the RuneLite plugin hub

## Future Enhancements

Potential improvements for the plugin:
- Load quest data from external JSON files
- Automatically track quest progress from game state
- Integration with the quest journal to auto-update actions
- Support for multiple actions per quest
- Quest completion detection
- Import/export quest action data
- Community-driven quest action database

## License

This plugin is open source and available under the BSD 2-Clause License.