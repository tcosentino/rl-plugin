package com.questnextaction;

import com.questnextaction.db.Shop;
import com.questnextaction.db.ShopDatabase;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Dialog for adding new objectives
 */
@Slf4j
public class AddObjectiveDialog extends JDialog
{
	private final ObjectiveManager objectiveManager;
	private final ObjectiveTrackerPanel parentPanel;
	private final ShopDatabase shopDatabase;

	// Form fields
	private final JComboBox<String> itemComboBox;
	private final JComboBox<String> shopComboBox;
	private final JTextField xCoordField;
	private final JTextField yCoordField;
	private final JTextField planeField;

	private boolean updatingFields = false;

	public AddObjectiveDialog(JFrame parent, ObjectiveManager objectiveManager,
		ObjectiveTrackerPanel parentPanel, ShopDatabase shopDatabase)
	{
		super(parent, "Add Shop Purchase Objective", true);
		this.objectiveManager = objectiveManager;
		this.parentPanel = parentPanel;
		this.shopDatabase = shopDatabase;

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

		// Item name autocomplete
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel itemLabel = new JLabel("Item to buy:");
		itemLabel.setForeground(Color.WHITE);
		formPanel.add(itemLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		itemComboBox = new JComboBox<>();
		itemComboBox.setEditable(true);
		itemComboBox.setMaximumRowCount(10);
		populateItemComboBox("");
		itemComboBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED && !updatingFields)
			{
				onItemSelected();
			}
		});
		// Add autocomplete listener to item text field
		JTextField itemEditor = (JTextField) itemComboBox.getEditor().getEditorComponent();
		itemEditor.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				updateItemSuggestions();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				updateItemSuggestions();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				updateItemSuggestions();
			}
		});
		formPanel.add(itemComboBox, gbc);

		// Shop name dropdown
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		JLabel shopLabel = new JLabel("Shop name:");
		shopLabel.setForeground(Color.WHITE);
		formPanel.add(shopLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		shopComboBox = new JComboBox<>();
		shopComboBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED && !updatingFields)
			{
				onShopSelected();
			}
		});
		formPanel.add(shopComboBox, gbc);

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

	private void populateItemComboBox(String filter)
	{
		updatingFields = true;
		itemComboBox.removeAllItems();

		List<String> items;
		if (filter == null || filter.trim().isEmpty())
		{
			items = shopDatabase.getAllItemNames();
		}
		else
		{
			items = shopDatabase.searchItems(filter);
		}

		// Limit to first 50 for performance
		items.stream().limit(50).forEach(itemComboBox::addItem);
		updatingFields = false;
	}

	private void updateItemSuggestions()
	{
		if (updatingFields)
		{
			return;
		}

		JTextField editor = (JTextField) itemComboBox.getEditor().getEditorComponent();
		String text = editor.getText();

		SwingUtilities.invokeLater(() -> {
			populateItemComboBox(text);
			itemComboBox.setPopupVisible(true);
			editor.setText(text);
		});
	}

	private void onItemSelected()
	{
		Object selected = itemComboBox.getSelectedItem();
		if (selected == null)
		{
			return;
		}

		String itemName = selected.toString().trim();
		List<Shop> shops = shopDatabase.findShopsByItem(itemName);

		updatingFields = true;
		shopComboBox.removeAllItems();

		if (shops.isEmpty())
		{
			// Add all shops as fallback
			shopDatabase.getAllShops().forEach(shop ->
				shopComboBox.addItem(shop.getName()));
		}
		else
		{
			shops.forEach(shop -> shopComboBox.addItem(shop.getName()));
		}

		updatingFields = false;

		// Auto-select first shop if available
		if (shopComboBox.getItemCount() > 0)
		{
			shopComboBox.setSelectedIndex(0);
		}
	}

	private void onShopSelected()
	{
		Object selected = shopComboBox.getSelectedItem();
		if (selected == null)
		{
			return;
		}

		String shopName = selected.toString();
		Shop shop = shopDatabase.getAllShops().stream()
			.filter(s -> s.getName().equals(shopName))
			.findFirst()
			.orElse(null);

		if (shop != null && shop.getWorldPoint() != null)
		{
			WorldPoint point = shop.getWorldPoint();
			xCoordField.setText(String.valueOf(point.getX()));
			yCoordField.setText(String.valueOf(point.getY()));
			planeField.setText(String.valueOf(point.getPlane()));
		}
	}

	private void addObjective()
	{
		Object itemObj = itemComboBox.getSelectedItem();
		Object shopObj = shopComboBox.getSelectedItem();

		String itemName = itemObj != null ? itemObj.toString().trim() : "";
		String shopName = shopObj != null ? shopObj.toString().trim() : "";

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
