package com.questnextaction;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Compact UI component for a single objective item
 */
public class ObjectiveListItem extends JPanel
{
	private static final Color ACTIVE_COLOR = new Color(40, 120, 40);
	private static final Color INACTIVE_COLOR = ColorScheme.DARKER_GRAY_COLOR;
	private static final Color HOVER_COLOR = new Color(60, 60, 60);

	private final Objective objective;
	private final ObjectiveManager manager;
	private final ObjectiveTrackerPanel parentPanel;

	public ObjectiveListItem(Objective objective, ObjectiveManager manager,
		ObjectiveTrackerPanel parentPanel)
	{
		this.objective = objective;
		this.manager = manager;
		this.parentPanel = parentPanel;

		setLayout(new BorderLayout());
		setBackground(objective.isActive() ? ACTIVE_COLOR : INACTIVE_COLOR);
		setBorder(new EmptyBorder(4, 6, 4, 6));

		// Type badge (small, colored square with initial)
		JLabel typeBadge = new JLabel(getTypeBadge(objective.getType()));
		typeBadge.setForeground(getTypeColor(objective.getType()));
		typeBadge.setFont(new Font("Arial", Font.BOLD, 10));
		typeBadge.setBorder(new EmptyBorder(0, 0, 0, 4));

		// Main content panel (task + location)
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setOpaque(false);

		// Task label
		JLabel taskLabel = new JLabel(objective.getTask());
		taskLabel.setForeground(Color.WHITE);
		taskLabel.setFont(new Font("Arial", Font.BOLD, 11));
		taskLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Location label
		JLabel locationLabel = new JLabel(objective.getLocationName());
		locationLabel.setForeground(Color.LIGHT_GRAY);
		locationLabel.setFont(new Font("Arial", Font.PLAIN, 10));
		locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		contentPanel.add(taskLabel);
		contentPanel.add(locationLabel);

		// Status indicator (small dot)
		JLabel statusDot = new JLabel(objective.isActive() ? "●" : "○");
		statusDot.setForeground(objective.isActive() ? Color.GREEN : Color.GRAY);
		statusDot.setFont(new Font("Arial", Font.PLAIN, 12));
		statusDot.setBorder(new EmptyBorder(0, 4, 0, 0));

		add(typeBadge, BorderLayout.WEST);
		add(contentPanel, BorderLayout.CENTER);
		add(statusDot, BorderLayout.EAST);

		// Click to toggle tracking
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		addMouseListener(new java.awt.event.MouseAdapter()
		{
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e)
			{
				manager.toggleObjective(objective.getId());
				parentPanel.rebuild();
			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent e)
			{
				if (!objective.isActive())
				{
					setBackground(HOVER_COLOR);
				}
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e)
			{
				setBackground(objective.isActive() ? ACTIVE_COLOR : INACTIVE_COLOR);
			}
		});
	}

	private String getTypeBadge(ObjectiveType type)
	{
		switch (type)
		{
			case TALK:
				return "[T]";
			case TRAVEL:
				return "[→]";
			case COLLECT:
				return "[C]";
			case KILL:
				return "[K]";
			case USE:
				return "[U]";
			case SKILL:
				return "[S]";
			default:
				return "[·]";
		}
	}

	private Color getTypeColor(ObjectiveType type)
	{
		switch (type)
		{
			case TALK:
				return new Color(100, 200, 255);  // Light blue
			case TRAVEL:
				return new Color(255, 200, 100);  // Orange
			case COLLECT:
				return new Color(255, 255, 100);  // Yellow
			case KILL:
				return new Color(255, 100, 100);  // Red
			case USE:
				return new Color(200, 100, 255);  // Purple
			case SKILL:
				return new Color(100, 255, 150);  // Green
			default:
				return Color.LIGHT_GRAY;
		}
	}
}
