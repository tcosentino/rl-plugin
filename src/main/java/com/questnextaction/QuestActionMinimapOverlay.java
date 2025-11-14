package com.questnextaction;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

/**
 * Minimap overlay for quest action markers
 */
public class QuestActionMinimapOverlay extends Overlay
{
	private final Client client;
	private final QuestActionManager questActionManager;
	private final QuestNextActionConfig config;

	@Inject
	public QuestActionMinimapOverlay(Client client, QuestActionManager questActionManager,
		QuestNextActionConfig config)
	{
		this.client = client;
		this.questActionManager = questActionManager;
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

		for (QuestAction action : questActionManager.getActiveActions())
		{
			if (action.getLocation() == null)
			{
				continue;
			}

			WorldPoint actionLocation = action.getLocation();

			// Only show if in same plane
			if (actionLocation.getPlane() != playerLocation.getPlane())
			{
				continue;
			}

			LocalPoint localPoint = LocalPoint.fromWorld(client, actionLocation);
			if (localPoint == null)
			{
				continue;
			}

			net.runelite.api.Point minimapPoint = Perspective.localToMinimap(client, localPoint);
			if (minimapPoint == null)
			{
				continue;
			}

			renderMinimapMarker(graphics, minimapPoint, action);
		}

		return null;
	}

	private void renderMinimapMarker(Graphics2D graphics, net.runelite.api.Point point, QuestAction action)
	{
		Color color = config.highlightColor();

		// Draw outer circle
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
		graphics.fillOval(point.getX() - 6, point.getY() - 6, 12, 12);

		// Draw inner circle
		graphics.setColor(color);
		graphics.fillOval(point.getX() - 4, point.getY() - 4, 8, 8);

		// Draw border
		graphics.setColor(Color.BLACK);
		graphics.drawOval(point.getX() - 6, point.getY() - 6, 12, 12);
	}
}
