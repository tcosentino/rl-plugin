package com.questnextaction.testharness;

import com.questnextaction.ObjectiveManager;
import com.questnextaction.ObjectiveTrackerConfig;
import com.questnextaction.ObjectiveTrackerPanel;
import com.questnextaction.db.ShopDatabase;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Main test harness UI for the Objective Tracker plugin.
 * <p>
 * This class provides a standalone application for testing and developing the
 * plugin without requiring the full RuneLite client. It combines the objective
 * tracker panel (sidebar UI) with a comprehensive debug view.
 * </p>
 * <p>
 * Features include:
 * </p>
 * <ul>
 *   <li>Split-pane layout with objectives on the left and debug view on the right</li>
 *   <li>Real-time status bar showing objective counts</li>
 *   <li>Auto-refresh capabilities</li>
 *   <li>Keyboard shortcuts for common operations</li>
 *   <li>Proper resource cleanup on disposal</li>
 * </ul>
 * <p>
 * Keyboard Shortcuts:
 * </p>
 * <ul>
 *   <li>F5 - Refresh debug view</li>
 *   <li>Ctrl+R - Refresh all panels</li>
 *   <li>Ctrl+Q - Quit application</li>
 * </ul>
 *
 * @see ObjectiveTrackerPanel
 * @see ObjectiveDebugPanel
 * @see TestHarnessRunner
 */
@Slf4j
public class TestHarnessUI extends JFrame {

    // UI Constants
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;
    private static final int SPLIT_PANE_DIVIDER_LOCATION = 350;
    private static final double SPLIT_PANE_RESIZE_WEIGHT = 0.3;
    private static final int HEADER_PADDING = 10;
    private static final int STATUS_PADDING = 5;
    private static final int DEBUG_AUTO_REFRESH_MS = 2000;
    private static final int STATUS_UPDATE_INTERVAL_MS = 500;

    // Color Constants
    private static final Color BACKGROUND_COLOR = new Color(40, 40, 40);
    private static final Color HEADER_BACKGROUND = new Color(30, 30, 30);
    private static final Color STATUS_BACKGROUND = new Color(30, 30, 30);

    // Font Constants
    private static final String FONT_FAMILY = "Arial";
    private static final int TITLE_FONT_SIZE = 18;
    private static final int SUBTITLE_FONT_SIZE = 12;
    private static final int STATUS_FONT_SIZE = 11;

    // Component References
    private final ObjectiveManager objectiveManager;
    private final ObjectiveTrackerConfig config;
    private final ShopDatabase shopDatabase;
    private final ObjectiveTrackerPanel objectivePanel;
    private final ObjectiveDebugPanel debugPanel;

    /**
     * Constructs a new TestHarnessUI.
     * <p>
     * Initializes all dependencies, loads the shop database, creates sample
     * objectives, and sets up the UI with keyboard shortcuts and auto-refresh.
     * </p>
     */
    public TestHarnessUI() {
        super("Objective Tracker - Test Harness");
        log.info("Initializing Test Harness UI");

        try {
            // Initialize dependencies
            this.objectiveManager = new ObjectiveManager();
            this.config = new MockConfig();
            this.shopDatabase = new ShopDatabase();

            // Load shop database
            shopDatabase.loadShops();
            log.info("Loaded {} shops from database", shopDatabase.getAllShops().size());

            // Initialize sample objectives
            objectiveManager.initializeSampleObjectives();
            log.info("Initialized {} sample objectives", objectiveManager.getAllObjectives().size());

            // Create panels
            this.objectivePanel = new ObjectiveTrackerPanel(objectiveManager, config, shopDatabase);
            this.debugPanel = new ObjectiveDebugPanel(objectiveManager);

            // Setup UI
            setupUI();
            setupKeyboardShortcuts();

            // Setup auto-refresh of debug panel when objectives change
            setupAutoRefresh();

            // Setup window properties
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            setLocationRelativeTo(null);

            log.info("Test Harness UI initialized successfully");
        } catch (Exception e) {
            log.error("Error initializing Test Harness UI", e);
            JOptionPane.showMessageDialog(this,
                "Failed to initialize test harness: " + e.getMessage(),
                "Initialization Error",
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Failed to initialize test harness", e);
        }
    }

    /**
     * Sets up keyboard shortcuts for common operations.
     */
    private void setupKeyboardShortcuts() {
        JRootPane rootPane = getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        // F5 - Refresh debug view
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh_debug");
        actionMap.put("refresh_debug", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("F5 pressed - refreshing debug view");
                refreshDebugPanel();
            }
        });

        // Ctrl+R - Refresh all panels
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), "refresh_all");
        actionMap.put("refresh_all", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Ctrl+R pressed - refreshing all panels");
                refreshAll();
            }
        });

        // Ctrl+Q - Quit application
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK), "quit");
        actionMap.put("quit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Ctrl+Q pressed - quitting application");
                dispose();
            }
        });

        log.info("Keyboard shortcuts registered: F5, Ctrl+R, Ctrl+Q");
    }

    /**
     * Sets up auto-refresh timer for the debug panel.
     * <p>
     * The debug panel is automatically refreshed every 2 seconds to reflect
     * any changes made to objectives.
     * </p>
     */
    private void setupAutoRefresh() {
        Timer autoRefreshTimer = new Timer(DEBUG_AUTO_REFRESH_MS, e -> {
            debugPanel.refresh();
        });
        autoRefreshTimer.start();
        log.info("Auto-refresh timer started (interval: {}ms)", DEBUG_AUTO_REFRESH_MS);
    }

    /**
     * Sets up the main UI components and layout.
     */
    private void setupUI() {
        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Create split pane with objective panel and debug panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(SPLIT_PANE_DIVIDER_LOCATION);
        splitPane.setResizeWeight(SPLIT_PANE_RESIZE_WEIGHT);

        // Left panel - Objective tracker (wrap in scroll pane for consistency)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Objectives Panel (Sidebar UI)",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP
        ));
        leftPanel.add(objectivePanel, BorderLayout.CENTER);

        // Right panel - Debug view
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Debug View",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP
        ));
        rightPanel.add(debugPanel, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Creates the header panel with title and action buttons.
     *
     * @return the header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(HEADER_PADDING, HEADER_PADDING,
            HEADER_PADDING, HEADER_PADDING));

        JLabel titleLabel = new JLabel("RuneLite Objective Tracker - Test Harness");
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, TITLE_FONT_SIZE));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Testing UI without RuneLite client | Press F5 to refresh");
        subtitleLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, SUBTITLE_FONT_SIZE));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton refreshButton = new JButton("Refresh Debug View");
        refreshButton.setToolTipText("Refresh debug panel (F5)");
        refreshButton.addActionListener(e -> refreshDebugPanel());
        buttonPanel.add(refreshButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Creates the status panel with real-time objective statistics.
     *
     * @return the status panel
     */
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(STATUS_BACKGROUND);
        statusPanel.setBorder(new EmptyBorder(STATUS_PADDING, HEADER_PADDING,
            STATUS_PADDING, HEADER_PADDING));

        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, STATUS_FONT_SIZE));

        JLabel infoLabel = new JLabel(String.format(
            "Shop Database: %d shops loaded | Objective Manager initialized",
            shopDatabase.getAllShops().size()
        ));
        infoLabel.setForeground(Color.LIGHT_GRAY);
        infoLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, STATUS_FONT_SIZE));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(infoLabel, BorderLayout.EAST);

        // Update status when objectives change
        Timer statusUpdateTimer = new Timer(STATUS_UPDATE_INTERVAL_MS, e -> {
            int totalObjectives = objectiveManager.getAllObjectives().size();
            int activeObjectives = objectiveManager.getActiveObjectives().size();
            statusLabel.setText(String.format(
                "Objectives: %d total, %d active",
                totalObjectives, activeObjectives
            ));
        });
        statusUpdateTimer.start();

        return statusPanel;
    }

    /**
     * Gets the objective manager instance.
     *
     * @return the objective manager
     */
    public ObjectiveManager getObjectiveManager() {
        return objectiveManager;
    }

    /**
     * Gets the configuration instance.
     *
     * @return the configuration
     */
    public ObjectiveTrackerConfig getConfig() {
        return config;
    }

    /**
     * Gets the shop database instance.
     *
     * @return the shop database
     */
    public ShopDatabase getShopDatabase() {
        return shopDatabase;
    }

    /**
     * Refreshes the debug panel.
     * <p>
     * Triggered by F5 keyboard shortcut or refresh button.
     * </p>
     */
    public void refreshDebugPanel() {
        log.debug("Refreshing debug panel");
        debugPanel.refresh();
    }

    /**
     * Refreshes the objective panel.
     * <p>
     * Rebuilds the objective list from the manager.
     * </p>
     */
    public void refreshObjectivePanel() {
        log.debug("Refreshing objective panel");
        objectivePanel.rebuild();
    }

    /**
     * Refreshes all panels (objectives and debug).
     * <p>
     * Triggered by Ctrl+R keyboard shortcut.
     * </p>
     */
    public void refreshAll() {
        log.debug("Refreshing all panels");
        refreshObjectivePanel();
        refreshDebugPanel();
    }

    /**
     * Cleans up resources before disposing the window.
     * <p>
     * Ensures that timers and other resources are properly released.
     * </p>
     */
    @Override
    public void dispose() {
        log.info("Disposing Test Harness UI");
        debugPanel.cleanup();
        super.dispose();
    }
}
