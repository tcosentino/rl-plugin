package com.questnextaction;

import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * World map marker for quest actions
 */
public class QuestActionWorldMapPoint extends WorldMapPoint
{
	private final QuestAction questAction;
	private final BufferedImage mapIcon;

	public QuestActionWorldMapPoint(QuestAction questAction, BufferedImage mapIcon)
	{
		super(questAction.getLocation(), mapIcon);
		this.questAction = questAction;
		this.mapIcon = mapIcon;
		this.setSnapToEdge(true);
		this.setJumpOnClick(true);
		this.setTooltip(buildTooltip());
		this.setName(questAction.getQuestName());
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
		sb.append(questAction.getQuestName());
		sb.append("</br>");
		sb.append(questAction.getDescription());

		if (questAction.getHint() != null && !questAction.getHint().isEmpty())
		{
			sb.append("</br>");
			sb.append("<i>").append(questAction.getHint()).append("</i>");
		}

		return sb.toString();
	}

	public QuestAction getQuestAction()
	{
		return questAction;
	}
}
