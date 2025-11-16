package com.questnextaction.testharness;

import com.questnextaction.ObjectiveTrackerConfig;
import java.awt.Color;

/**
 * Mock implementation of ObjectiveTrackerConfig for testing
 */
public class MockConfig implements ObjectiveTrackerConfig {
    private boolean showWorldMapIcon = true;
    private boolean showMinimapIcon = true;
    private boolean showSceneHighlight = true;
    private boolean showNavigator = true;
    private Color highlightColor = Color.CYAN;
    private int worldMapIconSize = 24;
    private int minimapIconSize = 8;

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
    public void setShowWorldMapIcon(boolean show) {
        this.showWorldMapIcon = show;
    }

    public void setShowMinimapIcon(boolean show) {
        this.showMinimapIcon = show;
    }

    public void setShowSceneHighlight(boolean show) {
        this.showSceneHighlight = show;
    }

    public void setShowNavigator(boolean show) {
        this.showNavigator = show;
    }

    public void setHighlightColor(Color color) {
        this.highlightColor = color;
    }

    public void setWorldMapIconSize(int size) {
        this.worldMapIconSize = size;
    }

    public void setMinimapIconSize(int size) {
        this.minimapIconSize = size;
    }
}
