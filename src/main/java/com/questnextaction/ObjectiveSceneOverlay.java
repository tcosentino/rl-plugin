package com.questnextaction;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

/**
 * Scene overlay for objective tile markers
 */
public class ObjectiveSceneOverlay extends Overlay
{
	private final Client client;
	private final ObjectiveManager objectiveManager;
	private final ObjectiveTrackerConfig config;

	@Inject
	public ObjectiveSceneOverlay(Client client, ObjectiveManager objectiveManager,
		ObjectiveTrackerConfig config)
	{
		this.client = client;
		this.objectiveManager = objectiveManager;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showSceneHighlight())
		{
			return null;
		}

		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

		for (Objective objective : objectiveManager.getActiveObjectives())
		{
			// Get the best location based on player's position
			// For multi-location objectives, this returns the closest shop
			WorldPoint objectiveLocation = objective.getBestLocation(playerLocation);

			if (objectiveLocation == null)
			{
				continue;
			}

			renderTileMarker(graphics, objective, objectiveLocation);
		}

		return null;
	}

	private void renderTileMarker(Graphics2D graphics, Objective objective, WorldPoint worldPoint)
	{
		LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

		if (localPoint == null)
		{
			return;
		}

		Polygon polygon = Perspective.getCanvasTilePoly(client, localPoint);
		if (polygon == null)
		{
			return;
		}

		Color color = config.highlightColor();

		// Fill the tile
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
		graphics.fillPolygon(polygon);

		// Draw border
		graphics.setColor(color);
		graphics.setStroke(new BasicStroke(2));
		graphics.drawPolygon(polygon);

		// Draw text above the tile
		net.runelite.api.Point textPoint = Perspective.getCanvasTextLocation(
			client, graphics, localPoint, objective.getTask(), 0);

		if (textPoint != null)
		{
			OverlayUtil.renderTextLocation(graphics, textPoint, objective.getTask(), color);
		}
	}
}
