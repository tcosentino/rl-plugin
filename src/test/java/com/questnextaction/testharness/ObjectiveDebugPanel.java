package com.questnextaction.testharness;

import com.questnextaction.Objective;
import com.questnextaction.ObjectiveManager;
import com.questnextaction.ShopLocation;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Debug panel that displays detailed objective data for development and debugging.
 * <p>
 * This panel provides a comprehensive view of all objective data including:
 * </p>
 * <ul>
 *   <li>Objective IDs, types, and tasks</li>
 *   <li>World coordinates and region IDs</li>
 *   <li>Item names and quantities</li>
 *   <li>All possible locations for multi-location objectives</li>
 *   <li>Active/inactive status</li>
 * </ul>
 * <p>
 * Features include:
 * </p>
 * <ul>
 *   <li>Auto-refresh functionality for real-time monitoring</li>
 *   <li>Export to file capability</li>
 *   <li>Copy to clipboard</li>
 *   <li>Clear all objectives</li>
 * </ul>
 *
 * @see Objective
 * @see ObjectiveManager
 */
@Slf4j
public class ObjectiveDebugPanel extends JPanel {

    // UI Constants
    private static final int PANEL_PADDING = 10;
    private static final int BORDER_SPACING = 0;
    private static final String FONT_FAMILY = "Monospaced";
    private static final int FONT_SIZE = 12;
    private static final int AUTO_REFRESH_INTERVAL_MS = 1000;

    // Display Constants
    private static final String HEADER_SEPARATOR = "═══════════════════════════════════════════════════════════════";
    private static final String ITEM_SEPARATOR = "───────────────────────────────────────────────────────────────";
    private static final String ACTIVE_INDICATOR = "YES ●";
    private static final String INACTIVE_INDICATOR = "NO ○";

    // Component References
    private final ObjectiveManager objectiveManager;
    private final JTextArea debugTextArea;
    private final JCheckBox autoRefreshCheckBox;
    private Timer refreshTimer;

    /**
     * Constructs a new ObjectiveDebugPanel.
     *
     * @param objectiveManager the objective manager to monitor (must not be null)
     * @throws IllegalArgumentException if objectiveManager is null
     */
    public ObjectiveDebugPanel(ObjectiveManager objectiveManager) {
        if (objectiveManager == null) {
            throw new IllegalArgumentException("ObjectiveManager cannot be null");
        }
        this.objectiveManager = objectiveManager;

        setLayout(new BorderLayout(BORDER_SPACING, PANEL_PADDING));
        setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));

        // Header panel with controls
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(createControlPanel(), BorderLayout.NORTH);

        // Debug text area
        debugTextArea = new JTextArea();
        debugTextArea.setEditable(false);
        debugTextArea.setFont(new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE));
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
        autoRefreshCheckBox.setToolTipText("Automatically refresh the view every second");
        autoRefreshCheckBox.addActionListener(e -> {
            if (autoRefreshCheckBox.isSelected()) {
                startAutoRefresh();
            } else {
                stopAutoRefresh();
            }
        });

        // Initial refresh
        refresh();
        log.info("ObjectiveDebugPanel initialized");
    }

    /**
     * Creates the control panel with action buttons and options.
     *
     * @return the control panel
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setToolTipText("Manually refresh the debug view");
        refreshButton.addActionListener(e -> refresh());

        // Copy to clipboard button
        JButton copyButton = new JButton("Copy");
        copyButton.setToolTipText("Copy debug output to clipboard");
        copyButton.addActionListener(e -> copyToClipboard());

        // Export to file button
        JButton exportButton = new JButton("Export");
        exportButton.setToolTipText("Export debug output to a file");
        exportButton.addActionListener(e -> exportToFile());

        // Clear all button
        JButton clearButton = new JButton("Clear All");
        clearButton.setToolTipText("Remove all objectives from the manager");
        clearButton.addActionListener(e -> clearAllObjectives());

        autoRefreshCheckBox.setSelected(false);

        panel.add(refreshButton);
        panel.add(copyButton);
        panel.add(exportButton);
        panel.add(clearButton);
        panel.add(autoRefreshCheckBox);

        return panel;
    }

    /**
     * Refreshes the debug view with current objective data.
     * <p>
     * This method rebuilds the entire debug output from the objective manager
     * and updates the text area. The caret is positioned at the top after refresh.
     * </p>
     */
    public void refresh() {
        try {
            String debugOutput = generateDebugOutput();
            SwingUtilities.invokeLater(() -> {
                debugTextArea.setText(debugOutput);
                debugTextArea.setCaretPosition(0);
            });
            log.debug("Debug view refreshed");
        } catch (Exception e) {
            log.error("Error refreshing debug view", e);
            debugTextArea.setText("Error refreshing view: " + e.getMessage());
        }
    }

    /**
     * Generates the complete debug output string.
     *
     * @return formatted debug output
     */
    private String generateDebugOutput() {
        StringBuilder sb = new StringBuilder();
        List<Objective> objectives = objectiveManager.getAllObjectives();

        sb.append(HEADER_SEPARATOR).append("\n");
        sb.append(String.format("  OBJECTIVE DEBUG VIEW - Total: %d objectives\n", objectives.size()));
        sb.append(HEADER_SEPARATOR).append("\n\n");

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

        sb.append(HEADER_SEPARATOR).append("\n");
        sb.append("  Last updated: ").append(new Date()).append("\n");
        sb.append(HEADER_SEPARATOR).append("\n");

        return sb.toString();
    }

    /**
     * Formats an objective for display in the debug view.
     *
     * @param obj the objective to format
     * @param index the 1-based index of this objective
     * @return formatted objective string
     */
    private String formatObjective(Objective obj, int index) {
        StringBuilder sb = new StringBuilder();

        sb.append(ITEM_SEPARATOR).append("\n");
        sb.append(String.format("  Objective #%d\n", index));
        sb.append(ITEM_SEPARATOR).append("\n");

        sb.append(String.format("  ID:               %s\n", obj.getId()));
        sb.append(String.format("  Type:             %s\n", obj.getType()));
        sb.append(String.format("  Task:             %s\n", obj.getTask()));
        sb.append(String.format("  Location Name:    %s\n", obj.getLocationName()));
        sb.append(String.format("  Active:           %s\n",
            obj.isActive() ? ACTIVE_INDICATOR : INACTIVE_INDICATOR));

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

        if (obj.getShopLocations() != null && !obj.getShopLocations().isEmpty()) {
            sb.append(String.format("  Shop Details:     %d shop(s)\n",
                obj.getShopLocations().size()));

            for (int i = 0; i < obj.getShopLocations().size(); i++) {
                ShopLocation shop = obj.getShopLocations().get(i);
                sb.append(String.format("    [%d] %s\n", i + 1, shop.getShopName()));
                sb.append(String.format("        Owner:    %s\n",
                    shop.getOwnerName() != null ? shop.getOwnerName() : "N/A"));
                sb.append(String.format("        Location: %s\n", shop.getLocationName()));
                sb.append(String.format("        Price:    %d gp\n", shop.getPrice()));
                sb.append(String.format("        Stock:    %s\n",
                    shop.getStock() == -1 ? "Unlimited" : String.valueOf(shop.getStock())));
                sb.append(String.format("        Coords:   (%d, %d, %d)\n",
                    shop.getWorldPoint().getX(),
                    shop.getWorldPoint().getY(),
                    shop.getWorldPoint().getPlane()));

                if (obj.getQuantity() != null) {
                    int totalCost = shop.calculateTotalCost(obj.getQuantity());
                    boolean sufficient = shop.hasSufficientStock(obj.getQuantity());
                    sb.append(String.format("        Total:    %d gp (%s)\n",
                        totalCost,
                        sufficient ? "in stock" : "insufficient stock"));
                }
            }

            // Show cheapest option
            if (obj.getCheapestShop() != null) {
                ShopLocation cheapest = obj.getCheapestShop();
                sb.append(String.format("    Cheapest:     %s - %d gp",
                    cheapest.getShopName(), cheapest.getPrice()));
                if (obj.getQuantity() != null) {
                    sb.append(String.format(" (Total: %d gp)", obj.getCheapestTotalCost()));
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Copies the current debug output to the system clipboard.
     */
    private void copyToClipboard() {
        try {
            String text = generateDebugOutput();
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);

            JOptionPane.showMessageDialog(this,
                "Debug output copied to clipboard!",
                "Copy Successful",
                JOptionPane.INFORMATION_MESSAGE);
            log.info("Debug output copied to clipboard");
        } catch (Exception e) {
            log.error("Error copying to clipboard", e);
            JOptionPane.showMessageDialog(this,
                "Failed to copy to clipboard: " + e.getMessage(),
                "Copy Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exports the current debug output to a file.
     */
    private void exportToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Debug Output");

        // Set default filename with timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String defaultFilename = "objective_debug_" + dateFormat.format(new Date()) + ".txt";
        fileChooser.setSelectedFile(new File(defaultFilename));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(generateDebugOutput());
                JOptionPane.showMessageDialog(this,
                    "Debug output exported to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                log.info("Debug output exported to: {}", fileToSave.getAbsolutePath());
            } catch (IOException e) {
                log.error("Error exporting to file", e);
                JOptionPane.showMessageDialog(this,
                    "Failed to export file: " + e.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Clears all objectives from the manager after confirmation.
     */
    private void clearAllObjectives() {
        int objectiveCount = objectiveManager.getAllObjectives().size();

        if (objectiveCount == 0) {
            JOptionPane.showMessageDialog(this,
                "No objectives to clear.",
                "Nothing to Clear",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int response = JOptionPane.showConfirmDialog(this,
            String.format("Are you sure you want to remove all %d objective(s)?", objectiveCount),
            "Confirm Clear All",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            objectiveManager.getAllObjectives().forEach(obj ->
                objectiveManager.removeObjective(obj.getId())
            );
            refresh();
            log.info("Cleared {} objectives", objectiveCount);
        }
    }

    /**
     * Starts the auto-refresh timer.
     * <p>
     * Refreshes the debug view automatically every second while enabled.
     * </p>
     */
    private void startAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        refreshTimer = new Timer(AUTO_REFRESH_INTERVAL_MS, e -> refresh());
        refreshTimer.start();
        log.info("Auto-refresh started");
    }

    /**
     * Stops the auto-refresh timer.
     */
    private void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
            log.info("Auto-refresh stopped");
        }
    }

    /**
     * Cleans up resources used by this panel.
     * <p>
     * Should be called when the panel is being disposed to prevent memory leaks.
     * </p>
     */
    public void cleanup() {
        stopAutoRefresh();
        log.info("ObjectiveDebugPanel cleaned up");
    }
}
