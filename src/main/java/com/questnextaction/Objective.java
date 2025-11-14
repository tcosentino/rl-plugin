package com.questnextaction;

import lombok.Builder;
import lombok.Value;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;

/**
 * Represents a trackable objective
 */
@Value
@Builder
public class Objective
{
	/**
	 * Unique identifier for this objective
	 */
	String id;

	/**
	 * Type of objective
	 */
	ObjectiveType type;

	/**
	 * What needs to be done
	 */
	String task;

	/**
	 * Location name (e.g., "Lumbridge Castle", "Varrock Square")
	 */
	String locationName;

	/**
	 * World location for this objective (null if no specific location)
	 */
	@Nullable
	WorldPoint location;

	/**
	 * Region ID for the location (used for map rendering)
	 */
	int regionId;

	/**
	 * Whether this objective is currently active/being tracked
	 */
	boolean active;
}
