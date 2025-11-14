package com.questnextaction;

import lombok.Builder;
import lombok.Value;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;

/**
 * Represents the next action needed to progress a quest
 */
@Value
@Builder
public class QuestAction
{
	/**
	 * Quest name (e.g., "Cook's Assistant")
	 */
	String questName;

	/**
	 * Type of action required
	 */
	QuestActionType actionType;

	/**
	 * Description of what needs to be done
	 */
	String description;

	/**
	 * World location for this action (null if no specific location)
	 */
	@Nullable
	WorldPoint location;

	/**
	 * Optional additional details or hints
	 */
	@Nullable
	String hint;

	/**
	 * Region ID for the location (used for map rendering)
	 */
	int regionId;

	/**
	 * Whether this action is currently active/being tracked
	 */
	boolean active;
}
