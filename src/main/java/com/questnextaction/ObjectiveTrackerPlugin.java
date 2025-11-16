package com.questnextaction;

import com.google.inject.Provides;
import com.questnextaction.db.ShopDatabase;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
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
import java.util.ArrayList;
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
	private ShopDatabase shopDatabase;

	@Inject
	private ObjectiveMinimapOverlay minimapOverlay;

	@Inject
	private ObjectiveSceneOverlay sceneOverlay;

	@Inject
	private ObjectiveWorldMapOverlay worldMapOverlay;

	@Inject
	private ObjectiveNavigatorOverlay navigatorOverlay;

	private ObjectiveTrackerPanel panel;
	private NavigationButton navigationButton;

	private final Map<String, List<ObjectiveWorldMapPoint>> worldMapPoints = new HashMap<>();
	private BufferedImage mapIcon;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Objective Tracker plugin started!");

		// Create map icon programmatically
		mapIcon = createObjectiveIcon();

		// Initialize panel
		panel = new ObjectiveTrackerPanel(objectiveManager, config, shopDatabase);

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
		overlayManager.add(worldMapOverlay);
		overlayManager.add(navigatorOverlay);

		// Set the map icon on the world map overlay
		worldMapOverlay.setMapIcon(mapIcon);

		// Initialize world map points (keeping for fallback)
		updateWorldMapPoints();
	}

	/**
	 * Create a simple objective icon
	 */
	private BufferedImage createObjectiveIcon()
	{
		int size = config.worldMapIconSize();
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = image.createGraphics();
		g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw a cyan circle with a white checkmark
		g.setColor(new java.awt.Color(0, 200, 200));
		g.fillOval(1, 1, size - 2, size - 2);

		g.setColor(java.awt.Color.WHITE);
		int fontSize = Math.max(10, (int)(size * 0.75));
		g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, fontSize));

		// Center the checkmark
		java.awt.FontMetrics fm = g.getFontMetrics();
		String checkmark = "✓";
		int x = (size - fm.stringWidth(checkmark)) / 2;
		int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
		g.drawString(checkmark, x, y);

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
		overlayManager.remove(worldMapOverlay);
		overlayManager.remove(navigatorOverlay);

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
			if (objective == null || !objective.isActive())
			{
				for (ObjectiveWorldMapPoint point : entry.getValue())
				{
					worldMapPointManager.remove(point);
				}
				return true;
			}
			return false;
		});

		// Add new active points
		for (Objective objective : activeObjectives)
		{
			if (objective.getLocation() == null &&
				(objective.getPossibleLocations() == null || objective.getPossibleLocations().isEmpty()))
			{
				continue;
			}

			if (!worldMapPoints.containsKey(objective.getId()))
			{
				List<ObjectiveWorldMapPoint> points = new ArrayList<>();

				// For objectives with multiple possible locations, show all shops on the map
				if (objective.getPossibleLocations() != null && !objective.getPossibleLocations().isEmpty())
				{
					for (WorldPoint location : objective.getPossibleLocations())
					{
						if (location != null)
						{
							ObjectiveWorldMapPoint point = new ObjectiveWorldMapPoint(
								objective, location, mapIcon);
							points.add(point);
							worldMapPointManager.add(point);
						}
					}
				}
				else if (objective.getLocation() != null)
				{
					// Single location objective
					ObjectiveWorldMapPoint point = new ObjectiveWorldMapPoint(
						objective, objective.getLocation(), mapIcon);
					points.add(point);
					worldMapPointManager.add(point);
				}

				if (!points.isEmpty())
				{
					worldMapPoints.put(objective.getId(), points);
				}
			}
		}
	}

	private void clearWorldMapPoints()
	{
		for (List<ObjectiveWorldMapPoint> points : worldMapPoints.values())
		{
			for (ObjectiveWorldMapPoint point : points)
			{
				worldMapPointManager.remove(point);
			}
		}
		worldMapPoints.clear();
	}

	@Provides
	ObjectiveTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ObjectiveTrackerConfig.class);
	}
}
