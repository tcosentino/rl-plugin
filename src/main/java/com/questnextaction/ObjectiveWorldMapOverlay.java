package com.questnextaction;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Custom world map overlay to render objective icons on top of everything
 */
public class ObjectiveWorldMapOverlay extends Overlay
{
	private final Client client;
	private final WorldMapPointManager worldMapPointManager;
	private final ObjectiveManager objectiveManager;
	private final ObjectiveTrackerConfig config;
	private BufferedImage mapIcon;

	@Inject
	public ObjectiveWorldMapOverlay(Client client, WorldMapPointManager worldMapPointManager,
	                                 ObjectiveManager objectiveManager, ObjectiveTrackerConfig config)
	{
		this.client = client;
		this.worldMapPointManager = worldMapPointManager;
		this.objectiveManager = objectiveManager;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.HIGH);
	}

	public void setMapIcon(BufferedImage mapIcon)
	{
		this.mapIcon = mapIcon;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showWorldMapIcon() || mapIcon == null)
		{
			return null;
		}

		// Check if world map is open
		Widget worldMapWidget = client.getWidget(ComponentID.WORLD_MAP_MAPVIEW);
		if (worldMapWidget == null || worldMapWidget.isHidden())
		{
			return null;
		}

		for (Objective objective : objectiveManager.getActiveObjectives())
		{
			if (objective.getLocation() == null)
			{
				continue;
			}

			Point point = worldMapPointManager.mapWorldPointToGraphicsPoint(objective.getLocation());
			if (point == null)
			{
				continue;
			}

			// Draw the icon centered on the point
			int iconSize = config.worldMapIconSize();
			int x = point.getX() - iconSize / 2;
			int y = point.getY() - iconSize / 2;
			graphics.drawImage(mapIcon, x, y, iconSize, iconSize, null);
		}

		return null;
	}
}
