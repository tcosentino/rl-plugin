package com.questnextaction.testharness;

import org.junit.Before;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link MockConfig}.
 */
public class MockConfigTest {

    private MockConfig config;

    @Before
    public void setUp() {
        config = new MockConfig();
    }

    @Test
    public void testDefaultValues() {
        assertTrue("showWorldMapIcon should default to true", config.showWorldMapIcon());
        assertTrue("showMinimapIcon should default to true", config.showMinimapIcon());
        assertTrue("showSceneHighlight should default to true", config.showSceneHighlight());
        assertTrue("showNavigator should default to true", config.showNavigator());
        assertEquals("highlightColor should default to cyan", Color.CYAN, config.highlightColor());
        assertEquals("worldMapIconSize should default to 24", 24, config.worldMapIconSize());
        assertEquals("minimapIconSize should default to 8", 8, config.minimapIconSize());
    }

    @Test
    public void testSetShowWorldMapIcon() {
        config.setShowWorldMapIcon(false);
        assertFalse(config.showWorldMapIcon());

        config.setShowWorldMapIcon(true);
        assertTrue(config.showWorldMapIcon());
    }

    @Test
    public void testSetShowMinimapIcon() {
        config.setShowMinimapIcon(false);
        assertFalse(config.showMinimapIcon());

        config.setShowMinimapIcon(true);
        assertTrue(config.showMinimapIcon());
    }

    @Test
    public void testSetShowSceneHighlight() {
        config.setShowSceneHighlight(false);
        assertFalse(config.showSceneHighlight());

        config.setShowSceneHighlight(true);
        assertTrue(config.showSceneHighlight());
    }

    @Test
    public void testSetShowNavigator() {
        config.setShowNavigator(false);
        assertFalse(config.showNavigator());

        config.setShowNavigator(true);
        assertTrue(config.showNavigator());
    }

    @Test
    public void testSetHighlightColor() {
        Color red = Color.RED;
        config.setHighlightColor(red);
        assertEquals(red, config.highlightColor());

        Color blue = Color.BLUE;
        config.setHighlightColor(blue);
        assertEquals(blue, config.highlightColor());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetHighlightColorNull() {
        config.setHighlightColor(null);
    }

    @Test
    public void testSetWorldMapIconSize() {
        config.setWorldMapIconSize(32);
        assertEquals(32, config.worldMapIconSize());

        config.setWorldMapIconSize(16);
        assertEquals(16, config.worldMapIconSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetWorldMapIconSizeZero() {
        config.setWorldMapIconSize(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetWorldMapIconSizeNegative() {
        config.setWorldMapIconSize(-5);
    }

    @Test
    public void testSetMinimapIconSize() {
        config.setMinimapIconSize(12);
        assertEquals(12, config.minimapIconSize());

        config.setMinimapIconSize(4);
        assertEquals(4, config.minimapIconSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMinimapIconSizeZero() {
        config.setMinimapIconSize(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMinimapIconSizeNegative() {
        config.setMinimapIconSize(-3);
    }

    @Test
    public void testResetToDefaults() {
        // Change all values
        config.setShowWorldMapIcon(false);
        config.setShowMinimapIcon(false);
        config.setShowSceneHighlight(false);
        config.setShowNavigator(false);
        config.setHighlightColor(Color.RED);
        config.setWorldMapIconSize(100);
        config.setMinimapIconSize(50);

        // Reset to defaults
        config.resetToDefaults();

        // Verify defaults
        assertTrue(config.showWorldMapIcon());
        assertTrue(config.showMinimapIcon());
        assertTrue(config.showSceneHighlight());
        assertTrue(config.showNavigator());
        assertEquals(Color.CYAN, config.highlightColor());
        assertEquals(24, config.worldMapIconSize());
        assertEquals(8, config.minimapIconSize());
    }

    @Test
    public void testMultipleModifications() {
        // Test that multiple modifications work correctly
        config.setShowWorldMapIcon(false);
        assertFalse(config.showWorldMapIcon());

        config.setHighlightColor(Color.GREEN);
        assertEquals(Color.GREEN, config.highlightColor());

        config.setWorldMapIconSize(48);
        assertEquals(48, config.worldMapIconSize());

        // Original changes should still be in effect
        assertFalse(config.showWorldMapIcon());
        assertEquals(Color.GREEN, config.highlightColor());
    }
}
