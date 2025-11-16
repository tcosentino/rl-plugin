package com.questnextaction;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages objectives for the current player
 */
@Slf4j
@Singleton
public class ObjectiveManager
{
	private final Map<String, Objective> objectives = new HashMap<>();

	public ObjectiveManager()
	{
		loadSampleObjectives();
	}

	/**
	 * Get all objectives
	 */
	public Collection<Objective> getAllObjectives()
	{
		return objectives.values();
	}

	/**
	 * Get all active objectives
	 */
	public List<Objective> getActiveObjectives()
	{
		return objectives.values().stream()
			.filter(Objective::isActive)
			.collect(Collectors.toList());
	}

	/**
	 * Get a specific objective
	 */
	public Objective getObjective(String id)
	{
		return objectives.get(id);
	}

	/**
	 * Toggle whether an objective is being tracked
	 */
	public void toggleObjective(String id)
	{
		Objective existing = objectives.get(id);
		if (existing != null)
		{
			Objective.ObjectiveBuilder builder = Objective.builder()
				.id(existing.getId())
				.type(existing.getType())
				.task(existing.getTask())
				.locationName(existing.getLocationName())
				.location(existing.getLocation())
				.regionId(existing.getRegionId())
				.active(!existing.isActive())
				.itemName(existing.getItemName())
				.quantity(existing.getQuantity());

			// Copy possible locations
			if (existing.getPossibleLocations() != null)
			{
				for (WorldPoint loc : existing.getPossibleLocations())
				{
					builder.possibleLocation(loc);
				}
			}

			objectives.put(id, builder.build());
		}
	}

	/**
	 * Add a new objective
	 */
	public void addObjective(String id, ObjectiveType type, String task,
		String locationName, WorldPoint location)
	{
		int regionId = location != null ? location.getRegionID() : 0;

		Objective objective = Objective.builder()
			.id(id)
			.type(type)
			.task(task)
			.locationName(locationName)
			.location(location)
			.regionId(regionId)
			.active(false)
			.build();

		objectives.put(id, objective);
	}

	/**
	 * Add a new objective (direct)
	 */
	public void addObjective(Objective objective)
	{
		objectives.put(objective.getId(), objective);
	}

	/**
	 * Remove an objective (completed or cancelled)
	 */
	public void removeObjective(String id)
	{
		objectives.remove(id);
	}

	/**
	 * Load sample objectives for testing
	 */
	private void loadSampleObjectives()
	{
		// Cook's Assistant - Talk to Cook
		objectives.put("cooks_assistant_1", Objective.builder()
			.id("cooks_assistant_1")
			.type(ObjectiveType.TALK)
			.task("Talk to Cook")
			.locationName("Lumbridge Castle")
			.location(new WorldPoint(3207, 3214, 0))
			.regionId(12850)
			.active(false)
			.build());

		// Collect items example
		objectives.put("collect_wool", Objective.builder()
			.id("collect_wool")
			.type(ObjectiveType.COLLECT)
			.task("Collect 20 wool")
			.locationName("Lumbridge sheep pen")
			.location(new WorldPoint(3209, 3259, 0))
			.regionId(12851)
			.active(false)
			.build());

		// Travel example
		objectives.put("go_to_varrock", Objective.builder()
			.id("go_to_varrock")
			.type(ObjectiveType.TRAVEL)
			.task("Visit Varrock Square")
			.locationName("Varrock Square")
			.location(new WorldPoint(3211, 3422, 0))
			.regionId(12853)
			.active(false)
			.build());

		log.debug("Loaded {} objectives", objectives.size());
	}
}
