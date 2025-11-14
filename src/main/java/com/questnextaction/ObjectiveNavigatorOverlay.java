package com.questnextaction;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

/**
 * Navigator overlay that displays the closest objective with distance and direction
 */
public class ObjectiveNavigatorOverlay extends OverlayPanel
{
	private final Client client;
	private final ObjectiveTrackerConfig config;
	private final ObjectiveManager objectiveManager;

	@Inject
	private ObjectiveNavigatorOverlay(Client client, ObjectiveTrackerConfig config, ObjectiveManager objectiveManager)
	{
		this.client = client;
		this.config = config;
		this.objectiveManager = objectiveManager;

		setPosition(OverlayPosition.TOP_CENTER);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showNavigator())
		{
			return null;
		}

		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null;
		}

		WorldPoint playerLocation = player.getWorldLocation();

		// Find the closest active objective
		Objective closestObjective = null;
		double closestDistance = Double.MAX_VALUE;

		for (Objective objective : objectiveManager.getActiveObjectives())
		{
			if (objective.getLocation() == null)
			{
				continue;
			}

			// Only consider objectives on the same plane
			if (objective.getLocation().getPlane() != playerLocation.getPlane())
			{
				continue;
			}

			double distance = calculateDistance(playerLocation, objective.getLocation());
			if (distance < closestDistance)
			{
				closestDistance = distance;
				closestObjective = objective;
			}
		}

		if (closestObjective == null)
		{
			return null;
		}

		// Build the overlay panel
		panelComponent.getChildren().clear();

		// Title
		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Navigator")
			.color(config.highlightColor())
			.build());

		// Objective name
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Target:")
			.right(closestObjective.getTask())
			.build());

		// Location name
		if (closestObjective.getLocationName() != null && !closestObjective.getLocationName().isEmpty())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Location:")
				.right(closestObjective.getLocationName())
				.build());
		}

		// Distance
		String distanceText = String.format("%.0f tiles", closestDistance);
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Distance:")
			.right(distanceText)
			.rightColor(getDistanceColor(closestDistance))
			.build());

		// Direction
		String direction = calculateDirection(playerLocation, closestObjective.getLocation());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Direction:")
			.right(direction)
			.rightColor(config.highlightColor())
			.build());

		return super.render(graphics);
	}

	/**
	 * Calculate the distance between two world points (2D distance, ignoring plane)
	 */
	private double calculateDistance(WorldPoint from, WorldPoint to)
	{
		int deltaX = to.getX() - from.getX();
		int deltaY = to.getY() - from.getY();
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}

	/**
	 * Calculate the cardinal direction and compass bearing from one point to another
	 */
	private String calculateDirection(WorldPoint from, WorldPoint to)
	{
		int deltaX = to.getX() - from.getX();
		int deltaY = to.getY() - from.getY();

		// Calculate angle in degrees (0° = North, 90° = East, 180° = South, 270° = West)
		double angleRadians = Math.atan2(deltaX, deltaY);
		double angleDegrees = Math.toDegrees(angleRadians);

		// Normalize to 0-360
		if (angleDegrees < 0)
		{
			angleDegrees += 360;
		}

		// Get cardinal direction
		String cardinal = getCardinalDirection(angleDegrees);

		return String.format("%s (%.0f°)", cardinal, angleDegrees);
	}

	/**
	 * Convert angle in degrees to cardinal direction
	 */
	private String getCardinalDirection(double degrees)
	{
		// 16-point compass
		if (degrees >= 348.75 || degrees < 11.25)
		{
			return "N";
		}
		else if (degrees >= 11.25 && degrees < 33.75)
		{
			return "NNE";
		}
		else if (degrees >= 33.75 && degrees < 56.25)
		{
			return "NE";
		}
		else if (degrees >= 56.25 && degrees < 78.75)
		{
			return "ENE";
		}
		else if (degrees >= 78.75 && degrees < 101.25)
		{
			return "E";
		}
		else if (degrees >= 101.25 && degrees < 123.75)
		{
			return "ESE";
		}
		else if (degrees >= 123.75 && degrees < 146.25)
		{
			return "SE";
		}
		else if (degrees >= 146.25 && degrees < 168.75)
		{
			return "SSE";
		}
		else if (degrees >= 168.75 && degrees < 191.25)
		{
			return "S";
		}
		else if (degrees >= 191.25 && degrees < 213.75)
		{
			return "SSW";
		}
		else if (degrees >= 213.75 && degrees < 236.25)
		{
			return "SW";
		}
		else if (degrees >= 236.25 && degrees < 258.75)
		{
			return "WSW";
		}
		else if (degrees >= 258.75 && degrees < 281.25)
		{
			return "W";
		}
		else if (degrees >= 281.25 && degrees < 303.75)
		{
			return "WNW";
		}
		else if (degrees >= 303.75 && degrees < 326.25)
		{
			return "NW";
		}
		else // 326.25 - 348.75
		{
			return "NNW";
		}
	}

	/**
	 * Get color based on distance (green if close, yellow if medium, red if far)
	 */
	private Color getDistanceColor(double distance)
	{
		if (distance < 10)
		{
			return Color.GREEN;
		}
		else if (distance < 50)
		{
			return Color.YELLOW;
		}
		else
		{
			return Color.ORANGE;
		}
	}
}
