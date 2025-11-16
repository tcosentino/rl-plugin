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
	private final JSpinner quantitySpinner;
	private final JLabel shopsLabel;

	private boolean updatingFields = false;
	private String lastSearchText = "";

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

		// Quantity spinner
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		JLabel quantityLabel = new JLabel("Quantity:");
		quantityLabel.setForeground(Color.WHITE);
		formPanel.add(quantityLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10000, 1);
		quantitySpinner = new JSpinner(spinnerModel);
		formPanel.add(quantitySpinner, gbc);

		// Available shops label (shows where item can be bought)
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		shopsLabel = new JLabel(" ");
		shopsLabel.setForeground(Color.LIGHT_GRAY);
		shopsLabel.setFont(shopsLabel.getFont().deriveFont(Font.ITALIC, 10f));
		formPanel.add(shopsLabel, gbc);

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

		// Only update if text actually changed
		if (text.equals(lastSearchText))
		{
			return;
		}

		lastSearchText = text;
		int caretPosition = editor.getCaretPosition();

		SwingUtilities.invokeLater(() -> {
			updatingFields = true;
			populateItemComboBox(text);

			// Only show popup if we have a non-empty search
			if (!text.isEmpty())
			{
				itemComboBox.setPopupVisible(true);
			}

			// Restore the text and caret position
			editor.setText(text);
			if (caretPosition <= text.length())
			{
				editor.setCaretPosition(caretPosition);
			}
			updatingFields = false;
		});
	}

	private void onItemSelected()
	{
		Object selected = itemComboBox.getSelectedItem();
		if (selected == null)
		{
			shopsLabel.setText(" ");
			return;
		}

		String itemName = selected.toString().trim();
		List<Shop> shops = shopDatabase.findShopsByItem(itemName);

		// Update the label to show available shops
		if (shops.isEmpty())
		{
			shopsLabel.setText("⚠ Item not found in shop database");
		}
		else if (shops.size() == 1)
		{
			shopsLabel.setText("Available at: " + shops.get(0).getName());
		}
		else
		{
			String shopNames = shops.stream()
				.map(Shop::getName)
				.limit(3)
				.collect(Collectors.joining(", "));
			if (shops.size() > 3)
			{
				shopNames += ", and " + (shops.size() - 3) + " more";
			}
			shopsLabel.setText("Available at " + shops.size() + " shops: " + shopNames);
		}
	}

	private void addObjective()
	{
		Object itemObj = itemComboBox.getSelectedItem();
		String itemName = itemObj != null ? itemObj.toString().trim() : "";

		if (itemName.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
				"Please enter an item name",
				"Validation Error",
				JOptionPane.ERROR_MESSAGE);
			return;
		}

		int quantity = (Integer) quantitySpinner.getValue();

		// Find all shops selling this item
		List<Shop> shops = shopDatabase.findShopsByItem(itemName);

		if (shops.isEmpty())
		{
			int result = JOptionPane.showConfirmDialog(this,
				"This item is not in the shop database.\nCreate objective anyway?",
				"Item Not Found",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);

			if (result != JOptionPane.YES_OPTION)
			{
				return;
			}
		}

		// Create the objective with all shop locations
		String id = "buy_" + UUID.randomUUID().toString().substring(0, 8);
		String task = quantity > 1 ? "Buy " + quantity + "x " + itemName : "Buy " + itemName;

		// Build location info
		String locationName;
		WorldPoint primaryLocation = null;

		if (shops.isEmpty())
		{
			locationName = "Unknown";
		}
		else if (shops.size() == 1)
		{
			locationName = shops.get(0).getName();
			primaryLocation = shops.get(0).getWorldPoint();
		}
		else
		{
			locationName = shops.size() + " shops";
			primaryLocation = shops.get(0).getWorldPoint(); // Use first as fallback
		}

		// Build the objective with multiple locations
		Objective.ObjectiveBuilder builder = Objective.builder()
			.id(id)
			.type(ObjectiveType.BUY)
			.task(task)
			.locationName(locationName)
			.location(primaryLocation)
			.regionId(primaryLocation != null ? primaryLocation.getRegionID() : 0)
			.active(false)
			.itemName(itemName)
			.quantity(quantity);

		// Add all shop locations as possible locations
		for (Shop shop : shops)
		{
			if (shop.getWorldPoint() != null)
			{
				builder.possibleLocation(shop.getWorldPoint());
			}
		}

		Objective objective = builder.build();
		objectiveManager.addObjective(objective);

		log.debug("Added new objective: {} at {} (with {} possible locations)",
			task, locationName, shops.size());

		// Refresh the panel
		parentPanel.rebuild();

		dispose();
	}
}
