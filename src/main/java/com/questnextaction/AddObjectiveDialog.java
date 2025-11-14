package com.questnextaction;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.UUID;

/**
 * Dialog for adding new objectives
 */
@Slf4j
public class AddObjectiveDialog extends JDialog
{
	private final ObjectiveManager objectiveManager;
	private final ObjectiveTrackerPanel parentPanel;

	// Form fields
	private final JTextField itemNameField;
	private final JTextField shopNameField;
	private final JTextField xCoordField;
	private final JTextField yCoordField;
	private final JTextField planeField;

	public AddObjectiveDialog(JFrame parent, ObjectiveManager objectiveManager,
		ObjectiveTrackerPanel parentPanel)
	{
		super(parent, "Add Shop Purchase Objective", true);
		this.objectiveManager = objectiveManager;
		this.parentPanel = parentPanel;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		// Main form panel
		JPanel formPanel = new JPanel();
		formPanel.setLayout(new GridBagLayout());
		formPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		// Item name
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel itemLabel = new JLabel("Item to buy:");
		itemLabel.setForeground(Color.WHITE);
		formPanel.add(itemLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		itemNameField = new JTextField(20);
		formPanel.add(itemNameField, gbc);

		// Shop name
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		JLabel shopLabel = new JLabel("Shop name:");
		shopLabel.setForeground(Color.WHITE);
		formPanel.add(shopLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		shopNameField = new JTextField(20);
		formPanel.add(shopNameField, gbc);

		// Location section
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		JLabel locationLabel = new JLabel("Shop Location (optional):");
		locationLabel.setForeground(Color.WHITE);
		locationLabel.setFont(locationLabel.getFont().deriveFont(Font.BOLD));
		formPanel.add(locationLabel, gbc);

		// X coordinate
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		JLabel xLabel = new JLabel("X:");
		xLabel.setForeground(Color.WHITE);
		formPanel.add(xLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		xCoordField = new JTextField(10);
		formPanel.add(xCoordField, gbc);

		// Y coordinate
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weightx = 0;
		JLabel yLabel = new JLabel("Y:");
		yLabel.setForeground(Color.WHITE);
		formPanel.add(yLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		yCoordField = new JTextField(10);
		formPanel.add(yCoordField, gbc);

		// Plane
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.weightx = 0;
		JLabel planeLabel = new JLabel("Plane:");
		planeLabel.setForeground(Color.WHITE);
		formPanel.add(planeLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		planeField = new JTextField(10);
		planeField.setText("0");
		formPanel.add(planeField, gbc);

		add(formPanel, BorderLayout.CENTER);

		// Button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		buttonPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

		JButton addButton = new JButton("Add Objective");
		addButton.addActionListener(e -> addObjective());

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> dispose());

		buttonPanel.add(addButton);
		buttonPanel.add(cancelButton);

		add(buttonPanel, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(parent);
	}

	private void addObjective()
	{
		String itemName = itemNameField.getText().trim();
		String shopName = shopNameField.getText().trim();

		if (itemName.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
				"Please enter an item name",
				"Validation Error",
				JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (shopName.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
				"Please enter a shop name",
				"Validation Error",
				JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Parse coordinates if provided
		WorldPoint location = null;
		try
		{
			String xText = xCoordField.getText().trim();
			String yText = yCoordField.getText().trim();
			String planeText = planeField.getText().trim();

			if (!xText.isEmpty() && !yText.isEmpty() && !planeText.isEmpty())
			{
				int x = Integer.parseInt(xText);
				int y = Integer.parseInt(yText);
				int plane = Integer.parseInt(planeText);
				location = new WorldPoint(x, y, plane);
			}
		}
		catch (NumberFormatException ex)
		{
			JOptionPane.showMessageDialog(this,
				"Invalid coordinates. Please enter valid numbers.",
				"Validation Error",
				JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Create the objective
		String id = "buy_" + UUID.randomUUID().toString().substring(0, 8);
		String task = "Buy " + itemName;

		objectiveManager.addObjective(id, ObjectiveType.BUY, task, shopName, location);

		log.debug("Added new objective: {} at {}", task, shopName);

		// Refresh the panel
		parentPanel.rebuild();

		dispose();
	}
}
