# Test Harness Quick Start Guide

## What is it?

The Test Harness is a standalone UI application that lets you test and debug the Objective Tracker plugin without running the full RuneLite client. This dramatically speeds up development cycles.

## Quick Start

### Running the Test Harness

```bash
./gradlew runTestHarness
```

Or run `TestHarnessRunner` from your IDE.

## UI Layout

```
┌─────────────────────────────────────────────────────────────┐
│                    Test Harness Header                      │
├──────────────────────┬──────────────────────────────────────┤
│   Objectives Panel   │        Debug View                    │
│   (Left Side)        │        (Right Side)                  │
│                      │                                      │
│  - Add objectives    │  - Detailed objective data           │
│  - Toggle active     │  - All internal fields               │
│  - View status       │  - Coordinates & IDs                 │
│                      │  - Auto-refresh option               │
│                      │  - Clear all button                  │
└──────────────────────┴──────────────────────────────────────┘
│                     Status Bar                              │
└─────────────────────────────────────────────────────────────┘
```

## Features

### Left Panel - Objectives UI
✓ Identical to RuneLite sidebar
✓ Click '+' to add objectives
✓ Click objectives to toggle active/inactive
✓ Visual status indicators (● active, ○ inactive)
✓ Color-coded by status (green = active, gray = inactive)

### Right Panel - Debug View
✓ Shows all objective fields in detail
✓ Displays coordinates, IDs, item names, quantities
✓ Lists all possible locations for multi-location objectives
✓ Auto-refresh every 1 second (optional)
✓ Manual refresh button
✓ Clear all objectives button

## Workflow

1. **Start the harness**: `./gradlew runTestHarness`
2. **Add objectives**: Click the '+' button
3. **Search for items**: Type in the autocomplete dropdown
4. **Set quantity**: Adjust the spinner
5. **View details**: Check the debug panel for all data
6. **Toggle objectives**: Click to activate/deactivate
7. **Monitor changes**: Debug panel auto-refreshes every 2 seconds

## Development Benefits

- ⚡ **Fast**: No RuneLite client startup time
- 🔍 **Visible**: See all internal data at once
- 🐛 **Debug-friendly**: Raw data view for troubleshooting
- 🧪 **Isolated**: Test UI changes independently
- 📊 **Informative**: Status bar shows live statistics

## Files

- `src/test/java/com/questnextaction/testharness/`
  - `TestHarnessRunner.java` - Main entry point
  - `TestHarnessUI.java` - Main window
  - `ObjectiveDebugPanel.java` - Debug view
  - `MockConfig.java` - Configuration mock
  - `README.md` - Detailed documentation

## Tips

- Enable "Auto-refresh" in the debug panel for live updates
- The status bar shows objective counts in real-time
- All changes are in-memory only (not persisted)
- Perfect for rapid UI iteration and testing
- Use the debug view to verify coordinate calculations

## Example Use Cases

1. **Testing UI changes**: Modify panel layouts and see results instantly
2. **Verifying data**: Check that objectives have correct coordinates
3. **Testing edge cases**: Create many objectives and test performance
4. **Debugging**: See exactly what data is stored in each objective
5. **Shop verification**: Ensure shop database loaded correctly

## Need Help?

See `src/test/java/com/questnextaction/testharness/README.md` for detailed documentation.
