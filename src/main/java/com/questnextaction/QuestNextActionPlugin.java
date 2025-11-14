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
	name = "Quest Next Action",
	description = "Track the next action needed for each quest and show on map",
	tags = {"quest", "helper", "map"}
)
public class QuestNextActionPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private QuestNextActionConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WorldMapPointManager worldMapPointManager;

	@Inject
	private QuestActionManager questActionManager;

	@Inject
	private QuestActionMinimapOverlay minimapOverlay;

	@Inject
	private QuestActionSceneOverlay sceneOverlay;

	private QuestNextActionPanel panel;
	private NavigationButton navigationButton;

	private final Map<String, QuestActionWorldMapPoint> worldMapPoints = new HashMap<>();
	private BufferedImage mapIcon;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Quest Next Action plugin started!");

		// Create map icon programmatically
		mapIcon = createQuestIcon();

		// Initialize panel
		panel = new QuestNextActionPanel(questActionManager, config);

		// Create navigation button
		navigationButton = NavigationButton.builder()
			.tooltip("Quest Next Action")
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
	 * Create a simple quest icon
	 */
	private BufferedImage createQuestIcon()
	{
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = image.createGraphics();

		// Draw a cyan circle with a white exclamation mark
		g.setColor(new java.awt.Color(0, 200, 200));
		g.fillOval(1, 1, 14, 14);

		g.setColor(java.awt.Color.WHITE);
		g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
		g.drawString("!", 6, 12);

		g.dispose();
		return image;
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Quest Next Action plugin stopped!");

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

		// Get active actions
		List<QuestAction> activeActions = questActionManager.getActiveActions();

		// Remove points that are no longer active
		worldMapPoints.entrySet().removeIf(entry -> {
			QuestAction action = questActionManager.getAction(entry.getKey());
			if (action == null || !action.isActive() || action.getLocation() == null)
			{
				worldMapPointManager.remove(entry.getValue());
				return true;
			}
			return false;
		});

		// Add new active points
		for (QuestAction action : activeActions)
		{
			if (action.getLocation() == null)
			{
				continue;
			}

			if (!worldMapPoints.containsKey(action.getQuestName()))
			{
				QuestActionWorldMapPoint point = new QuestActionWorldMapPoint(action, mapIcon);
				worldMapPoints.put(action.getQuestName(), point);
				worldMapPointManager.add(point);
			}
		}
	}

	private void clearWorldMapPoints()
	{
		for (QuestActionWorldMapPoint point : worldMapPoints.values())
		{
			worldMapPointManager.remove(point);
		}
		worldMapPoints.clear();
	}

	@Provides
	QuestNextActionConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(QuestNextActionConfig.class);
	}
}
