package com.questnextaction.testharness;

import com.questnextaction.ObjectiveManager;
import com.questnextaction.ObjectiveTrackerConfig;
import com.questnextaction.ObjectiveTrackerPanel;
import com.questnextaction.db.ShopDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main test harness UI that combines the objective panel with a debug panel
 * for development and testing without running the full RuneLite client
 */
public class TestHarnessUI extends JFrame {
    private final ObjectiveManager objectiveManager;
    private final ObjectiveTrackerConfig config;
    private final ShopDatabase shopDatabase;
    private final ObjectiveTrackerPanel objectivePanel;
    private final ObjectiveDebugPanel debugPanel;

    public TestHarnessUI() {
        super("Objective Tracker - Test Harness");

        // Initialize dependencies
        this.objectiveManager = new ObjectiveManager();
        this.config = new MockConfig();
        this.shopDatabase = new ShopDatabase();

        // Load shop database
        shopDatabase.loadShops();

        // Initialize sample objectives
        objectiveManager.initializeSampleObjectives();

        // Create panels
        this.objectivePanel = new ObjectiveTrackerPanel(objectiveManager, config, shopDatabase);
        this.debugPanel = new ObjectiveDebugPanel(objectiveManager);

        // Setup UI
        setupUI();

        // Setup auto-refresh of debug panel when objectives change
        setupAutoRefresh();

        // Setup window properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    private void setupAutoRefresh() {
        // Refresh debug panel periodically to catch any changes
        Timer autoRefreshTimer = new Timer(2000, e -> {
            debugPanel.refresh();
        });
        autoRefreshTimer.start();
    }

    private void setupUI() {
        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(40, 40, 40));

        // Create split pane with objective panel and debug panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.3);

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

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 30, 30));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("RuneLite Objective Tracker - Test Harness");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Testing UI without RuneLite client");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton refreshButton = new JButton("Refresh Debug View");
        refreshButton.addActionListener(e -> debugPanel.refresh());
        buttonPanel.add(refreshButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(30, 30, 30));
        statusPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        JLabel infoLabel = new JLabel(String.format(
            "Shop Database: %d shops loaded | Objective Manager initialized",
            shopDatabase.getAllShops().size()
        ));
        infoLabel.setForeground(Color.LIGHT_GRAY);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(infoLabel, BorderLayout.EAST);

        // Update status when objectives change
        Timer statusUpdateTimer = new Timer(500, e -> {
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

    public ObjectiveManager getObjectiveManager() {
        return objectiveManager;
    }

    public ObjectiveTrackerConfig getConfig() {
        return config;
    }

    public ShopDatabase getShopDatabase() {
        return shopDatabase;
    }

    public void refreshDebugPanel() {
        debugPanel.refresh();
    }

    public void refreshObjectivePanel() {
        objectivePanel.rebuild();
    }

    public void refreshAll() {
        refreshObjectivePanel();
        refreshDebugPanel();
    }

    @Override
    public void dispose() {
        debugPanel.cleanup();
        super.dispose();
    }
}
