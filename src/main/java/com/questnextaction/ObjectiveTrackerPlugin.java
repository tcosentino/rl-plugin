package com.questnextaction;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@PluginDescriptor(
	name = "Objective Tracker",
	description = "Track objectives and show them on the map",
	tags = {"objective", "tracker", "helper", "map"}
)
public class ObjectiveTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ObjectiveTrackerConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WorldMapPointManager worldMapPointManager;

	@Inject
	private ObjectiveManager objectiveManager;

	@Inject
	private ObjectiveMinimapOverlay minimapOverlay;

	@Inject
	private ObjectiveSceneOverlay sceneOverlay;

	private ObjectiveTrackerPanel panel;
	private NavigationButton navigationButton;

	private final Map<String, ObjectiveWorldMapPoint> worldMapPoints = new HashMap<>();
	private BufferedImage mapIcon;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Objective Tracker plugin started!");

		// Create map icon programmatically
		mapIcon = createObjectiveIcon();

		// Initialize panel
		panel = new ObjectiveTrackerPanel(objectiveManager, config);

		// Create navigation button
		navigationButton = NavigationButton.builder()
			.tooltip("Objective Tracker")
			.icon(mapIcon)
			.priority(5)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navigationButton);

		// Add overlays
		overlayManager.add(minimapOverlay);
		overlayManager.add(sceneOverlay);

		// Initialize world map points
		updateWorldMapPoints();
	}

	/**
	 * Create a simple objective icon
	 */
	private BufferedImage createObjectiveIcon()
	{
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = image.createGraphics();

		// Draw a cyan circle with a white checkmark
		g.setColor(new java.awt.Color(0, 200, 200));
		g.fillOval(1, 1, 14, 14);

		g.setColor(java.awt.Color.WHITE);
		g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
		g.drawString("✓", 4, 12);

		g.dispose();
		return image;
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Objective Tracker plugin stopped!");

		// Remove UI
		clientToolbar.removeNavigation(navigationButton);

		// Remove overlays
		overlayManager.remove(minimapOverlay);
		overlayManager.remove(sceneOverlay);

		// Clear world map points
		clearWorldMapPoints();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		// Update UI when game state changes
		if (panel != null)
		{
			panel.rebuild();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		// Periodically update world map points
		updateWorldMapPoints();
	}

	private void updateWorldMapPoints()
	{
		if (!config.showWorldMapIcon())
		{
			clearWorldMapPoints();
			return;
		}

		// Get active objectives
		List<Objective> activeObjectives = objectiveManager.getActiveObjectives();

		// Remove points that are no longer active
		worldMapPoints.entrySet().removeIf(entry -> {
			Objective objective = objectiveManager.getObjective(entry.getKey());
			if (objective == null || !objective.isActive() || objective.getLocation() == null)
			{
				worldMapPointManager.remove(entry.getValue());
				return true;
			}
			return false;
		});

		// Add new active points
		for (Objective objective : activeObjectives)
		{
			if (objective.getLocation() == null)
			{
				continue;
			}

			if (!worldMapPoints.containsKey(objective.getId()))
			{
				ObjectiveWorldMapPoint point = new ObjectiveWorldMapPoint(objective, mapIcon);
				worldMapPoints.put(objective.getId(), point);
				worldMapPointManager.add(point);
			}
		}
	}

	private void clearWorldMapPoints()
	{
		for (ObjectiveWorldMapPoint point : worldMapPoints.values())
		{
			worldMapPointManager.remove(point);
		}
		worldMapPoints.clear();
	}

	@Provides
	ObjectiveTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ObjectiveTrackerConfig.class);
	}
}
