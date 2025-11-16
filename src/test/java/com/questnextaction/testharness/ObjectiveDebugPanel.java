package com.questnextaction.testharness;

import com.questnextaction.Objective;
import com.questnextaction.ObjectiveManager;
import net.runelite.api.coords.WorldPoint;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Debug panel that shows detailed objective data for development and debugging
 */
public class ObjectiveDebugPanel extends JPanel {
    private final ObjectiveManager objectiveManager;
    private final JTextArea debugTextArea;
    private final JCheckBox autoRefreshCheckBox;
    private Timer refreshTimer;

    public ObjectiveDebugPanel(ObjectiveManager objectiveManager) {
        this.objectiveManager = objectiveManager;

        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header panel with controls
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(createControlPanel(), BorderLayout.NORTH);

        // Debug text area
        debugTextArea = new JTextArea();
        debugTextArea.setEditable(false);
        debugTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        debugTextArea.setLineWrap(false);
        debugTextArea.setWrapStyleWord(false);

        JScrollPane scrollPane = new JScrollPane(debugTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Objective Data (Raw)",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Auto-refresh checkbox
        autoRefreshCheckBox = new JCheckBox("Auto-refresh (1s)", false);
        autoRefreshCheckBox.addActionListener(e -> {
            if (autoRefreshCheckBox.isSelected()) {
                startAutoRefresh();
            } else {
                stopAutoRefresh();
            }
        });

        // Initial refresh
        refresh();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());

        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> {
            objectiveManager.getAllObjectives().forEach(obj ->
                objectiveManager.removeObjective(obj.getId())
            );
            refresh();
        });

        autoRefreshCheckBox.setSelected(false);

        panel.add(refreshButton);
        panel.add(clearButton);
        panel.add(autoRefreshCheckBox);

        return panel;
    }

    public void refresh() {
        StringBuilder sb = new StringBuilder();
        List<Objective> objectives = objectiveManager.getAllObjectives();

        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append(String.format("  OBJECTIVE DEBUG VIEW - Total: %d objectives\n", objectives.size()));
        sb.append("═══════════════════════════════════════════════════════════════\n\n");

        if (objectives.isEmpty()) {
            sb.append("  No objectives found.\n");
            sb.append("  Click the '+' button in the Objectives panel to add one.\n\n");
        } else {
            List<Objective> activeObjectives = objectiveManager.getActiveObjectives();
            sb.append(String.format("  Active: %d | Inactive: %d\n\n",
                activeObjectives.size(), objectives.size() - activeObjectives.size()));

            for (int i = 0; i < objectives.size(); i++) {
                Objective obj = objectives.get(i);
                sb.append(formatObjective(obj, i + 1));
                sb.append("\n");
            }
        }

        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("  Last updated: ").append(new java.util.Date()).append("\n");
        sb.append("═══════════════════════════════════════════════════════════════\n");

        debugTextArea.setText(sb.toString());
        debugTextArea.setCaretPosition(0);
    }

    private String formatObjective(Objective obj, int index) {
        StringBuilder sb = new StringBuilder();

        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append(String.format("  Objective #%d\n", index));
        sb.append("───────────────────────────────────────────────────────────────\n");

        sb.append(String.format("  ID:               %s\n", obj.getId()));
        sb.append(String.format("  Type:             %s\n", obj.getType()));
        sb.append(String.format("  Task:             %s\n", obj.getTask()));
        sb.append(String.format("  Location Name:    %s\n", obj.getLocationName()));
        sb.append(String.format("  Active:           %s\n", obj.isActive() ? "YES ●" : "NO ○"));

        if (obj.getLocation() != null) {
            WorldPoint loc = obj.getLocation();
            sb.append(String.format("  Location:         (%d, %d, %d)\n",
                loc.getX(), loc.getY(), loc.getPlane()));
        } else {
            sb.append("  Location:         null\n");
        }

        if (obj.getRegionId() != null) {
            sb.append(String.format("  Region ID:        %d\n", obj.getRegionId()));
        }

        if (obj.getItemName() != null) {
            sb.append(String.format("  Item Name:        %s\n", obj.getItemName()));
        }

        if (obj.getQuantity() != null && obj.getQuantity() > 0) {
            sb.append(String.format("  Quantity:         %d\n", obj.getQuantity()));
        }

        if (obj.getPossibleLocations() != null && !obj.getPossibleLocations().isEmpty()) {
            sb.append(String.format("  Possible Locs:    %d location(s)\n",
                obj.getPossibleLocations().size()));
            for (int i = 0; i < obj.getPossibleLocations().size(); i++) {
                WorldPoint loc = obj.getPossibleLocations().get(i);
                sb.append(String.format("    [%d] (%d, %d, %d)\n",
                    i + 1, loc.getX(), loc.getY(), loc.getPlane()));
            }
        }

        return sb.toString();
    }

    private void startAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        refreshTimer = new Timer(1000, e -> refresh());
        refreshTimer.start();
    }

    private void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
        }
    }

    public void cleanup() {
        stopAutoRefresh();
    }
}
