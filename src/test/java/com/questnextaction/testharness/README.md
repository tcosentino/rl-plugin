# Objective Tracker Test Harness

A standalone test UI for developing and debugging the Objective Tracker plugin without needing to run the full RuneLite client.

## Features

### Left Panel - Objectives UI
- Identical to the sidebar panel shown in RuneLite
- Add new objectives using the '+' button
- Click objectives to toggle their active state
- View all objectives with their status indicators

### Right Panel - Debug View
- Detailed objective data display
- Shows all internal fields (ID, type, coordinates, etc.)
- Auto-refresh option for real-time monitoring
- Clear all objectives button for testing
- Raw data view for debugging

## Running the Test Harness

### Option 1: Using Gradle (Recommended)
```bash
./gradlew runTestHarness
```

### Option 2: From Your IDE
Run the main class: `com.questnextaction.testharness.TestHarnessRunner`

## Usage

1. **Adding Objectives**
   - Click the '+' button in the Objectives panel
   - Search for an item in the dropdown
   - Set the quantity
   - Click 'Add Objective'

2. **Viewing Objectives**
   - All objectives appear in the left panel
   - Active objectives are highlighted in green with a filled circle (●)
   - Inactive objectives are gray with an empty circle (○)

3. **Toggling Objectives**
   - Click any objective to toggle between active/inactive state
   - The debug panel shows the updated state

4. **Debug View**
   - Shows detailed information about all objectives
   - Displays coordinates, region IDs, item names, quantities
   - Lists all possible locations for multi-location objectives
   - Enable 'Auto-refresh' for real-time updates
   - Click 'Refresh' to manually update the view
   - Click 'Clear All' to remove all objectives

## Benefits

- **Faster Development**: No need to start RuneLite client
- **Easy Debugging**: See all objective data in one view
- **Quick Testing**: Test UI changes instantly
- **Better Visibility**: Debug panel shows internal state

## Components

- `TestHarnessRunner.java` - Main entry point
- `TestHarnessUI.java` - Main window with split panel layout
- `ObjectiveDebugPanel.java` - Debug view panel
- `MockConfig.java` - Mock configuration for testing

## Notes

- The test harness uses the same `ObjectiveManager` and `ShopDatabase` as the real plugin
- Sample objectives are loaded automatically on startup
- Changes made in the test harness are not persisted (in-memory only)
- Perfect for UI development and data model testing
