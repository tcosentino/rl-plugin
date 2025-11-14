package com.questnextaction;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

/**
 * Scene overlay for quest action tile markers
 */
public class QuestActionSceneOverlay extends Overlay
{
	private final Client client;
	private final QuestActionManager questActionManager;
	private final QuestNextActionConfig config;

	@Inject
	public QuestActionSceneOverlay(Client client, QuestActionManager questActionManager,
		QuestNextActionConfig config)
	{
		this.client = client;
		this.questActionManager = questActionManager;
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

		for (QuestAction action : questActionManager.getActiveActions())
		{
			if (action.getLocation() == null)
			{
				continue;
			}

			renderTileMarker(graphics, action);
		}

		return null;
	}

	private void renderTileMarker(Graphics2D graphics, QuestAction action)
	{
		WorldPoint worldPoint = action.getLocation();
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
			client, graphics, localPoint, action.getQuestName(), 0);

		if (textPoint != null)
		{
			OverlayUtil.renderTextLocation(graphics, textPoint, action.getQuestName(), color);
		}
	}
}
