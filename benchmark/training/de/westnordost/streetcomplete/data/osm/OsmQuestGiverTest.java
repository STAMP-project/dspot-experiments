package de.westnordost.streetcomplete.data.osm;


import Element.Type;
import Element.Type.NODE;
import OsmQuestGiver.QuestUpdates;
import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.OsmLatLon;
import de.westnordost.streetcomplete.data.QuestStatus;
import de.westnordost.streetcomplete.data.osm.persist.OsmQuestDao;
import de.westnordost.streetcomplete.data.osmnotes.OsmNoteQuestDao;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class OsmQuestGiverTest {
    private static LatLon POS = new OsmLatLon(10, 10);

    private static Node NODE = new de.westnordost.osmapi.map.data.OsmNode(1, 0, OsmQuestGiverTest.POS, null, null, null);

    private OsmNoteQuestDao osmNoteQuestDao;

    private OsmQuestDao osmQuestDao;

    private OsmQuestGiver osmQuestUnlocker;

    private OsmElementQuestType questType;

    @Test
    public void noteBlocksNewQuests() {
        Mockito.when(questType.isApplicableTo(OsmQuestGiverTest.NODE)).thenReturn(true);
        Mockito.when(osmNoteQuestDao.getAllPositions(ArgumentMatchers.any())).thenReturn(Collections.singletonList(OsmQuestGiverTest.POS));
        Assert.assertTrue(osmQuestUnlocker.updateQuests(OsmQuestGiverTest.NODE).createdQuests.isEmpty());
    }

    @Test
    public void previousQuestBlocksNewQuest() {
        OsmQuest q = new OsmQuest(questType, Type.NODE, 1, new ElementGeometry(OsmQuestGiverTest.POS));
        Mockito.when(osmQuestDao.getAll(null, null, null, Element.Type.NODE, 1L)).thenReturn(Collections.singletonList(q));
        Mockito.when(questType.isApplicableTo(OsmQuestGiverTest.NODE)).thenReturn(true);
        OsmQuestGiver.QuestUpdates r = osmQuestUnlocker.updateQuests(OsmQuestGiverTest.NODE);
        Assert.assertTrue(r.createdQuests.isEmpty());
        Assert.assertTrue(r.removedQuestIds.isEmpty());
    }

    @Test
    public void notApplicableBlocksNewQuest() {
        Mockito.when(questType.isApplicableTo(OsmQuestGiverTest.NODE)).thenReturn(false);
        OsmQuestGiver.QuestUpdates r = osmQuestUnlocker.updateQuests(OsmQuestGiverTest.NODE);
        Assert.assertTrue(r.createdQuests.isEmpty());
        Assert.assertTrue(r.removedQuestIds.isEmpty());
    }

    @Test
    public void notApplicableRemovesPreviousQuest() {
        OsmQuest q = new OsmQuest(123L, questType, Type.NODE, 1, QuestStatus.NEW, null, null, new Date(), new ElementGeometry(OsmQuestGiverTest.POS));
        Mockito.when(osmQuestDao.getAll(null, null, null, Element.Type.NODE, 1L)).thenReturn(Collections.singletonList(q));
        Mockito.when(questType.isApplicableTo(OsmQuestGiverTest.NODE)).thenReturn(false);
        OsmQuestGiver.QuestUpdates r = osmQuestUnlocker.updateQuests(OsmQuestGiverTest.NODE);
        Assert.assertTrue(r.createdQuests.isEmpty());
        Assert.assertEquals(1, r.removedQuestIds.size());
        Assert.assertEquals(123L, ((long) (r.removedQuestIds.get(0))));
        Mockito.verify(osmQuestDao).deleteAll(Collections.singletonList(123L));
    }

    @Test
    public void applicableAddsNewQuest() {
        Mockito.when(questType.isApplicableTo(OsmQuestGiverTest.NODE)).thenReturn(true);
        List<OsmQuest> quests = osmQuestUnlocker.updateQuests(OsmQuestGiverTest.NODE).createdQuests;
        Assert.assertEquals(1, quests.size());
        OsmQuest quest = quests.get(0);
        Assert.assertEquals(1, quest.getElementId());
        Assert.assertEquals(Element.Type.NODE, quest.getElementType());
        Assert.assertEquals(questType, quest.getType());
        Mockito.verify(osmQuestDao).deleteAllReverted(Element.Type.NODE, 1);
        Mockito.verify(osmQuestDao).addAll(Collections.singletonList(quest));
    }
}

