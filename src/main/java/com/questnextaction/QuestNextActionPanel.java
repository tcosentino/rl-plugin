package com.questnextaction;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QuestNextActionPanel extends PluginPanel
{
	private final QuestActionManager questActionManager;
	private final QuestNextActionConfig config;

	private final JPanel questListPanel = new JPanel();
	private final PluginErrorPanel noQuestsPanel = new PluginErrorPanel();

	public QuestNextActionPanel(QuestActionManager questActionManager, QuestNextActionConfig config)
	{
		this.questActionManager = questActionManager;
		this.config = config;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		northPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel title = new JLabel("Quest Next Actions");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Arial", Font.BOLD, 16));
		northPanel.add(title, BorderLayout.NORTH);

		JLabel subtitle = new JLabel("Click to track/untrack");
		subtitle.setForeground(Color.LIGHT_GRAY);
		subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
		northPanel.add(subtitle, BorderLayout.SOUTH);

		add(northPanel, BorderLayout.NORTH);

		questListPanel.setLayout(new BoxLayout(questListPanel, BoxLayout.Y_AXIS));
		questListPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel questListWrapper = new JPanel(new BorderLayout());
		questListWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		questListWrapper.add(questListPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(questListWrapper);
		scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, BorderLayout.CENTER);

		noQuestsPanel.setContent("No quests available", "Add quest data to get started");
		add(noQuestsPanel, BorderLayout.SOUTH);

		rebuild();
	}

	public void rebuild()
	{
		SwingUtilities.invokeLater(() ->
		{
			questListPanel.removeAll();

			List<QuestAction> allActions = new ArrayList<>(questActionManager.getAllActions());
			allActions.sort((a, b) -> a.getQuestName().compareTo(b.getQuestName()));

			if (allActions.isEmpty())
			{
				noQuestsPanel.setVisible(true);
			}
			else
			{
				noQuestsPanel.setVisible(false);

				for (QuestAction action : allActions)
				{
					questListPanel.add(new QuestActionBox(action, questActionManager, config, this));
					questListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
				}
			}

			questListPanel.revalidate();
			questListPanel.repaint();
		});
	}
}
