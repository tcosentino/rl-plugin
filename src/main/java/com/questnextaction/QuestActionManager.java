package com.questnextaction;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages quest actions and provides quest data
 */
@Slf4j
@Singleton
public class QuestActionManager
{
	private final Map<String, QuestAction> questActions = new HashMap<>();
	private final Set<String> activeQuests = new HashSet<>();

	public QuestActionManager()
	{
		loadQuestData();
	}

	/**
	 * Get all quest actions
	 */
	public Collection<QuestAction> getAllActions()
	{
		return questActions.values();
	}

	/**
	 * Get all active quest actions
	 */
	public List<QuestAction> getActiveActions()
	{
		return questActions.values().stream()
			.filter(QuestAction::isActive)
			.collect(Collectors.toList());
	}

	/**
	 * Get a specific quest action
	 */
	public QuestAction getAction(String questName)
	{
		return questActions.get(questName);
	}

	/**
	 * Toggle whether a quest is being tracked
	 */
	public void toggleQuest(String questName)
	{
		QuestAction existing = questActions.get(questName);
		if (existing != null)
		{
			QuestAction updated = QuestAction.builder()
				.questName(existing.getQuestName())
				.actionType(existing.getActionType())
				.description(existing.getDescription())
				.location(existing.getLocation())
				.hint(existing.getHint())
				.regionId(existing.getRegionId())
				.active(!existing.isActive())
				.build();
			questActions.put(questName, updated);
		}
	}

	/**
	 * Update the next action for a quest
	 */
	public void updateQuestAction(String questName, QuestActionType actionType,
		String description, WorldPoint location, String hint)
	{
		int regionId = location != null ? location.getRegionID() : 0;

		QuestAction action = QuestAction.builder()
			.questName(questName)
			.actionType(actionType)
			.description(description)
			.location(location)
			.hint(hint)
			.regionId(regionId)
			.active(true)
			.build();

		questActions.put(questName, action);
	}

	/**
	 * Remove a quest action (quest completed)
	 */
	public void removeQuestAction(String questName)
	{
		questActions.remove(questName);
	}

	/**
	 * Load initial quest data (sample data for testing)
	 */
	private void loadQuestData()
	{
		// Sample quest actions - these would normally be loaded from a data file
		// Cook's Assistant
		questActions.put("Cook's Assistant", QuestAction.builder()
			.questName("Cook's Assistant")
			.actionType(QuestActionType.TALK_TO_NPC)
			.description("Talk to the Cook in Lumbridge Castle")
			.location(new WorldPoint(3207, 3214, 0))
			.hint("The Cook is in the kitchen on the ground floor")
			.regionId(12850)
			.active(false)
			.build());

		// Sheep Shearer
		questActions.put("Sheep Shearer", QuestAction.builder()
			.questName("Sheep Shearer")
			.actionType(QuestActionType.OBTAIN_ITEM)
			.description("Collect 20 balls of wool")
			.location(new WorldPoint(3209, 3259, 0))
			.hint("Shear sheep and spin wool at the Lumbridge spinning wheel")
			.regionId(12851)
			.active(false)
			.build());

		// Romeo & Juliet
		questActions.put("Romeo & Juliet", QuestAction.builder()
			.questName("Romeo & Juliet")
			.actionType(QuestActionType.TALK_TO_NPC)
			.description("Talk to Romeo in Varrock Square")
			.location(new WorldPoint(3211, 3422, 0))
			.hint("Romeo is wandering around Varrock Square")
			.regionId(12853)
			.active(false)
			.build());

		// Rune Mysteries
		questActions.put("Rune Mysteries", QuestAction.builder()
			.questName("Rune Mysteries")
			.actionType(QuestActionType.TALK_TO_NPC)
			.description("Talk to Duke Horacio in Lumbridge Castle")
			.location(new WorldPoint(3210, 3222, 1))
			.hint("The Duke is on the 1st floor of Lumbridge Castle")
			.regionId(12850)
			.active(false)
			.build());

		// Restless Ghost
		questActions.put("The Restless Ghost", QuestAction.builder()
			.questName("The Restless Ghost")
			.actionType(QuestActionType.TALK_TO_NPC)
			.description("Talk to Father Aereck in Lumbridge Church")
			.location(new WorldPoint(3243, 3206, 0))
			.hint("Father Aereck is in the Lumbridge Church")
			.regionId(12850)
			.active(false)
			.build());

		log.debug("Loaded {} quest actions", questActions.size());
	}
}
