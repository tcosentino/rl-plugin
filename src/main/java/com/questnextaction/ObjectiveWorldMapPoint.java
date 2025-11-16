package com.questnextaction;

import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * World map marker for objectives
 */
public class ObjectiveWorldMapPoint extends WorldMapPoint
{
	private final Objective objective;
	private final BufferedImage mapIcon;
	private final WorldPoint specificLocation;

	public ObjectiveWorldMapPoint(Objective objective, WorldPoint location, BufferedImage mapIcon)
	{
		super(location, mapIcon);
		this.objective = objective;
		this.specificLocation = location;
		this.mapIcon = mapIcon;
		this.setSnapToEdge(true);
		this.setJumpOnClick(true);
		this.setTooltip(buildTooltip());
		this.setName(objective.getTask());
	}

	@Override
	public void onEdgeSnap()
	{
		this.setSnapToEdge(true);
	}

	@Override
	public void onEdgeUnsnap()
	{
		this.setSnapToEdge(false);
	}

	private String buildTooltip()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(objective.getTask());
		sb.append("</br>");

		// For multi-location objectives, show which specific shop this marker represents
		if (objective.getPossibleLocations() != null && objective.getPossibleLocations().size() > 1)
		{
			// Find which shop this location corresponds to by looking at shop database
			sb.append("One of ").append(objective.getPossibleLocations().size()).append(" shops");
		}
		else
		{
			sb.append(objective.getLocationName());
		}

		return sb.toString();
	}

	public Objective getObjective()
	{
		return objective;
	}
}
