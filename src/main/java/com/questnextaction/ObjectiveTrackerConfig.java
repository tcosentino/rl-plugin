package com.questnextaction;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.Color;

@ConfigGroup("objectivetracker")
public interface ObjectiveTrackerConfig extends Config
{
	@ConfigItem(
		keyName = "showWorldMapIcon",
		name = "Show World Map Icons",
		description = "Display objective markers on the world map"
	)
	default boolean showWorldMapIcon()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMinimapIcon",
		name = "Show Minimap Icons",
		description = "Display objective markers on the minimap"
	)
	default boolean showMinimapIcon()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSceneHighlight",
		name = "Show Scene Highlights",
		description = "Highlight objective locations in the game world"
	)
	default boolean showSceneHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "highlightColor",
		name = "Highlight Color",
		description = "Color for objective highlights"
	)
	default Color highlightColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "worldMapIconSize",
		name = "World Map Icon Size",
		description = "Size of icons on the world map (in pixels)"
	)
	default int worldMapIconSize()
	{
		return 24;
	}

	@ConfigItem(
		keyName = "minimapIconSize",
		name = "Minimap Icon Size",
		description = "Size of icons on the minimap (radius in pixels)"
	)
	default int minimapIconSize()
	{
		return 8;
	}
}
