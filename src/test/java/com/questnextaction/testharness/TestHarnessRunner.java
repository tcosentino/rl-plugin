package com.questnextaction.testharness;

import javax.swing.*;

/**
 * Main runner for the Objective Tracker test harness
 *
 * This provides a standalone UI for testing the plugin without needing to run
 * the full RuneLite client, allowing for faster development and debugging.
 *
 * Usage:
 *   Run this class directly from your IDE or with:
 *   ./gradlew runTestHarness
 *
 * Features:
 *   - Left panel: Objective tracker panel (same as sidebar UI)
 *   - Right panel: Debug view with detailed objective data
 *   - Add/remove objectives
 *   - Toggle objective active state
 *   - View raw objective data for debugging
 */
public class TestHarnessRunner {
    public static void main(String[] args) {
        // Set look and feel
        try {
            // Use system look and feel for native appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set look and feel: " + e.getMessage());
        }

        // Create and show UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                TestHarnessUI harness = new TestHarnessUI();
                harness.setVisible(true);

                System.out.println("=".repeat(70));
                System.out.println("  Objective Tracker Test Harness Started");
                System.out.println("=".repeat(70));
                System.out.println();
                System.out.println("  The test harness is now running!");
                System.out.println();
                System.out.println("  Features:");
                System.out.println("    - Left panel shows the objective tracker (sidebar UI)");
                System.out.println("    - Right panel shows detailed debug information");
                System.out.println("    - Click '+' to add new shop objectives");
                System.out.println("    - Click objectives to toggle active state");
                System.out.println("    - Use 'Refresh Debug View' to update debug info");
                System.out.println();
                System.out.println("  Loaded:");
                System.out.println("    - Shop Database: " + harness.getShopDatabase().getAllShops().size() + " shops");
                System.out.println("    - Objectives: " + harness.getObjectiveManager().getAllObjectives().size() + " initial objectives");
                System.out.println();
                System.out.println("=".repeat(70));

            } catch (Exception e) {
                System.err.println("Error starting test harness: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to start test harness:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
