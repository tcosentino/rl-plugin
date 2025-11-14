package com.questnextaction;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

/**
 * Minimap overlay for objective markers
 */
public class ObjectiveMinimapOverlay extends Overlay
{
	private final Client client;
	private final ObjectiveManager objectiveManager;
	private final ObjectiveTrackerConfig config;

	@Inject
	public ObjectiveMinimapOverlay(Client client, ObjectiveManager objectiveManager,
		ObjectiveTrackerConfig config)
	{
		this.client = client;
		this.objectiveManager = objectiveManager;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showMinimapIcon())
		{
			return null;
		}

		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

		for (Objective objective : objectiveManager.getActiveObjectives())
		{
			if (objective.getLocation() == null)
			{
				continue;
			}

			WorldPoint objectiveLocation = objective.getLocation();

			// Only show if in same plane
			if (objectiveLocation.getPlane() != playerLocation.getPlane())
			{
				continue;
			}

			LocalPoint localPoint = LocalPoint.fromWorld(client, objectiveLocation);
			if (localPoint == null)
			{
				continue;
			}

			net.runelite.api.Point minimapPoint = Perspective.localToMinimap(client, localPoint);
			if (minimapPoint == null)
			{
				continue;
			}

			renderMinimapMarker(graphics, minimapPoint, objective);
		}

		return null;
	}

	private void renderMinimapMarker(Graphics2D graphics, net.runelite.api.Point point, Objective objective)
	{
		Color color = config.highlightColor();
		int radius = config.minimapIconSize();
		int diameter = radius * 2;
		int innerRadius = Math.max(2, radius - 2);
		int innerDiameter = innerRadius * 2;

		// Draw outer circle
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
		graphics.fillOval(point.getX() - radius, point.getY() - radius, diameter, diameter);

		// Draw inner circle
		graphics.setColor(color);
		graphics.fillOval(point.getX() - innerRadius, point.getY() - innerRadius, innerDiameter, innerDiameter);

		// Draw border
		graphics.setColor(Color.BLACK);
		graphics.drawOval(point.getX() - radius, point.getY() - radius, diameter, diameter);
	}
}
