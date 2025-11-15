package com.questnextaction;

import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Custom world map overlay to render objective icons on top of everything
 */
public class ObjectiveWorldMapOverlay extends WorldMapOverlay
{
	private final ObjectiveManager objectiveManager;
	private final ObjectiveTrackerConfig config;
	private BufferedImage mapIcon;

	@Inject
	public ObjectiveWorldMapOverlay(ObjectiveManager objectiveManager, ObjectiveTrackerConfig config)
	{
		this.objectiveManager = objectiveManager;
		this.config = config;
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

		for (Objective objective : objectiveManager.getActiveObjectives())
		{
			if (objective.getLocation() == null)
			{
				continue;
			}

			Point point = mapWorldPointToGraphicsPoint(objective.getLocation());
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
