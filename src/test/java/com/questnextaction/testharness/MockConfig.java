package com.questnextaction.testharness;

import com.questnextaction.ObjectiveTrackerConfig;
import java.awt.Color;

/**
 * Mock implementation of {@link ObjectiveTrackerConfig} for testing purposes.
 * <p>
 * This class provides a configurable implementation of the plugin configuration
 * interface that can be used in the test harness without requiring RuneLite's
 * configuration system.
 * </p>
 * <p>
 * All configuration values can be modified at runtime using the provided setter
 * methods, making it ideal for testing different configuration scenarios.
 * </p>
 *
 * @see ObjectiveTrackerConfig
 */
public class MockConfig implements ObjectiveTrackerConfig {

    // Default configuration values
    private static final boolean DEFAULT_SHOW_WORLD_MAP_ICON = true;
    private static final boolean DEFAULT_SHOW_MINIMAP_ICON = true;
    private static final boolean DEFAULT_SHOW_SCENE_HIGHLIGHT = true;
    private static final boolean DEFAULT_SHOW_NAVIGATOR = true;
    private static final Color DEFAULT_HIGHLIGHT_COLOR = Color.CYAN;
    private static final int DEFAULT_WORLD_MAP_ICON_SIZE = 24;
    private static final int DEFAULT_MINIMAP_ICON_SIZE = 8;

    private boolean showWorldMapIcon = DEFAULT_SHOW_WORLD_MAP_ICON;
    private boolean showMinimapIcon = DEFAULT_SHOW_MINIMAP_ICON;
    private boolean showSceneHighlight = DEFAULT_SHOW_SCENE_HIGHLIGHT;
    private boolean showNavigator = DEFAULT_SHOW_NAVIGATOR;
    private Color highlightColor = DEFAULT_HIGHLIGHT_COLOR;
    private int worldMapIconSize = DEFAULT_WORLD_MAP_ICON_SIZE;
    private int minimapIconSize = DEFAULT_MINIMAP_ICON_SIZE;

    @Override
    public boolean showWorldMapIcon() {
        return showWorldMapIcon;
    }

    @Override
    public boolean showMinimapIcon() {
        return showMinimapIcon;
    }

    @Override
    public boolean showSceneHighlight() {
        return showSceneHighlight;
    }

    @Override
    public boolean showNavigator() {
        return showNavigator;
    }

    @Override
    public Color highlightColor() {
        return highlightColor;
    }

    @Override
    public int worldMapIconSize() {
        return worldMapIconSize;
    }

    @Override
    public int minimapIconSize() {
        return minimapIconSize;
    }

    // Setters for testing

    /**
     * Sets whether to show world map icons.
     *
     * @param show true to show world map icons, false otherwise
     */
    public void setShowWorldMapIcon(boolean show) {
        this.showWorldMapIcon = show;
    }

    /**
     * Sets whether to show minimap icons.
     *
     * @param show true to show minimap icons, false otherwise
     */
    public void setShowMinimapIcon(boolean show) {
        this.showMinimapIcon = show;
    }

    /**
     * Sets whether to show scene highlights.
     *
     * @param show true to show scene highlights, false otherwise
     */
    public void setShowSceneHighlight(boolean show) {
        this.showSceneHighlight = show;
    }

    /**
     * Sets whether to show the navigator overlay.
     *
     * @param show true to show navigator, false otherwise
     */
    public void setShowNavigator(boolean show) {
        this.showNavigator = show;
    }

    /**
     * Sets the highlight color for objectives.
     *
     * @param color the color to use for highlighting (must not be null)
     * @throws IllegalArgumentException if color is null
     */
    public void setHighlightColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Highlight color cannot be null");
        }
        this.highlightColor = color;
    }

    /**
     * Sets the world map icon size.
     *
     * @param size the icon size in pixels (must be positive)
     * @throws IllegalArgumentException if size is not positive
     */
    public void setWorldMapIconSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("World map icon size must be positive, got: " + size);
        }
        this.worldMapIconSize = size;
    }

    /**
     * Sets the minimap icon size.
     *
     * @param size the icon radius in pixels (must be positive)
     * @throws IllegalArgumentException if size is not positive
     */
    public void setMinimapIconSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Minimap icon size must be positive, got: " + size);
        }
        this.minimapIconSize = size;
    }

    /**
     * Resets all configuration values to their defaults.
     */
    public void resetToDefaults() {
        this.showWorldMapIcon = DEFAULT_SHOW_WORLD_MAP_ICON;
        this.showMinimapIcon = DEFAULT_SHOW_MINIMAP_ICON;
        this.showSceneHighlight = DEFAULT_SHOW_SCENE_HIGHLIGHT;
        this.showNavigator = DEFAULT_SHOW_NAVIGATOR;
        this.highlightColor = DEFAULT_HIGHLIGHT_COLOR;
        this.worldMapIconSize = DEFAULT_WORLD_MAP_ICON_SIZE;
        this.minimapIconSize = DEFAULT_MINIMAP_ICON_SIZE;
    }
}
