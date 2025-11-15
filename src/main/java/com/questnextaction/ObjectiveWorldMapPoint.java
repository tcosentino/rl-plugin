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

	public ObjectiveWorldMapPoint(Objective objective, BufferedImage mapIcon)
	{
		super(objective.getLocation(), mapIcon);
		this.objective = objective;
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
		sb.append(objective.getLocationName());

		return sb.toString();
	}

	public Objective getObjective()
	{
		return objective;
	}
}
