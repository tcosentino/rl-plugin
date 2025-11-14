package com.questnextaction;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * UI component representing a single quest action in the panel
 */
public class QuestActionBox extends JPanel
{
	private static final Color ACTIVE_COLOR = new Color(50, 150, 50);
	private static final Color INACTIVE_COLOR = ColorScheme.DARKER_GRAY_COLOR;

	private final QuestAction action;
	private final QuestActionManager manager;
	private final QuestNextActionConfig config;
	private final QuestNextActionPanel parentPanel;

	public QuestActionBox(QuestAction action, QuestActionManager manager,
		QuestNextActionConfig config, QuestNextActionPanel parentPanel)
	{
		this.action = action;
		this.manager = manager;
		this.config = config;
		this.parentPanel = parentPanel;

		setLayout(new BorderLayout());
		setBackground(action.isActive() ? ACTIVE_COLOR : INACTIVE_COLOR);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		// Quest name
		JLabel questNameLabel = new JLabel(action.getQuestName());
		questNameLabel.setForeground(Color.WHITE);
		questNameLabel.setFont(new Font("Arial", Font.BOLD, 14));

		// Action type badge
		JLabel typeLabel = new JLabel(formatActionType(action.getActionType()));
		typeLabel.setForeground(Color.YELLOW);
		typeLabel.setFont(new Font("Arial", Font.PLAIN, 11));

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		topPanel.add(questNameLabel, BorderLayout.NORTH);
		topPanel.add(typeLabel, BorderLayout.SOUTH);

		// Action description
		JTextArea descriptionArea = new JTextArea(action.getDescription());
		descriptionArea.setForeground(Color.LIGHT_GRAY);
		descriptionArea.setBackground(new Color(0, 0, 0, 0));
		descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		descriptionArea.setEditable(false);
		descriptionArea.setFocusable(false);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);
		centerPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
		centerPanel.add(descriptionArea, BorderLayout.NORTH);

		// Hint (if enabled and available)
		if (config.showHints() && action.getHint() != null && !action.getHint().isEmpty())
		{
			JLabel hintLabel = new JLabel("Hint: " + action.getHint());
			hintLabel.setForeground(new Color(200, 200, 150));
			hintLabel.setFont(new Font("Arial", Font.ITALIC, 11));
			centerPanel.add(hintLabel, BorderLayout.SOUTH);
		}

		// Status label
		JLabel statusLabel = new JLabel(action.isActive() ? "TRACKING" : "Click to track");
		statusLabel.setForeground(action.isActive() ? Color.GREEN : Color.GRAY);
		statusLabel.setFont(new Font("Arial", Font.BOLD, 11));
		statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		add(topPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(statusLabel, BorderLayout.SOUTH);

		// Click to toggle tracking
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		addMouseListener(new java.awt.event.MouseAdapter()
		{
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e)
			{
				manager.toggleQuest(action.getQuestName());
				parentPanel.rebuild();
			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent e)
			{
				setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(Color.WHITE, 1),
					new EmptyBorder(9, 9, 9, 9)
				));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e)
			{
				setBorder(new EmptyBorder(10, 10, 10, 10));
			}
		});
	}

	private String formatActionType(QuestActionType type)
	{
		switch (type)
		{
			case TALK_TO_NPC:
				return "Talk to NPC";
			case GO_TO_LOCATION:
				return "Go to Location";
			case USE_ITEM:
				return "Use Item";
			case OBTAIN_ITEM:
				return "Obtain Item";
			case KILL_NPC:
				return "Kill NPC";
			case USE_OBJECT:
				return "Use Object";
			case EQUIP_ITEM:
				return "Equip Item";
			case ENTER_AREA:
				return "Enter Area";
			case COMPLETE_PUZZLE:
				return "Complete Puzzle";
			default:
				return "Quest Action";
		}
	}
}
