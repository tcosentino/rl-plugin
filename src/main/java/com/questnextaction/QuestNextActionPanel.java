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
public class ObjectiveTrackerPanel extends PluginPanel
{
	private final ObjectiveManager objectiveManager;
	private final ObjectiveTrackerConfig config;

	private final JPanel objectiveListPanel = new JPanel();
	private final PluginErrorPanel noObjectivesPanel = new PluginErrorPanel();

	public ObjectiveTrackerPanel(ObjectiveManager objectiveManager, ObjectiveTrackerConfig config)
	{
		this.objectiveManager = objectiveManager;
		this.config = config;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		northPanel.setBorder(new EmptyBorder(5, 5, 3, 5));

		JLabel title = new JLabel("Objectives");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Arial", Font.BOLD, 14));
		northPanel.add(title, BorderLayout.CENTER);

		add(northPanel, BorderLayout.NORTH);

		objectiveListPanel.setLayout(new BoxLayout(objectiveListPanel, BoxLayout.Y_AXIS));
		objectiveListPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel objectiveListWrapper = new JPanel(new BorderLayout());
		objectiveListWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		objectiveListWrapper.add(objectiveListPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(objectiveListWrapper);
		scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(null);
		add(scrollPane, BorderLayout.CENTER);

		noObjectivesPanel.setContent("No objectives", "Add objectives to track");
		add(noObjectivesPanel, BorderLayout.SOUTH);

		rebuild();
	}

	public void rebuild()
	{
		SwingUtilities.invokeLater(() ->
		{
			objectiveListPanel.removeAll();

			List<Objective> allObjectives = new ArrayList<>(objectiveManager.getAllObjectives());
			allObjectives.sort((a, b) -> a.getTask().compareTo(b.getTask()));

			if (allObjectives.isEmpty())
			{
				noObjectivesPanel.setVisible(true);
			}
			else
			{
				noObjectivesPanel.setVisible(false);

				for (Objective objective : allObjectives)
				{
					objectiveListPanel.add(new ObjectiveListItem(objective, objectiveManager, this));
					objectiveListPanel.add(Box.createRigidArea(new Dimension(0, 2)));
				}
			}

			objectiveListPanel.revalidate();
			objectiveListPanel.repaint();
		});
	}
}
