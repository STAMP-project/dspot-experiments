package de.westnordost.streetcomplete.data.osm;


import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.streetcomplete.data.QuestType;
import de.westnordost.streetcomplete.data.osm.download.OverpassMapDataDao;
import de.westnordost.streetcomplete.data.osmnotes.OsmNoteQuestType;
import de.westnordost.streetcomplete.quests.QuestModule;
import de.westnordost.streetcomplete.quests.localized_name.data.PutRoadNameSuggestionsHandler;
import de.westnordost.streetcomplete.quests.localized_name.data.RoadNameSuggestionsDao;
import de.westnordost.streetcomplete.quests.oneway.data.TrafficFlowSegmentsDao;
import de.westnordost.streetcomplete.quests.oneway.data.WayTrafficFlowDao;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SimpleOverpassQuestsValidityTest {
    @Test
    public void queryValid() {
        OverpassMapDataDao overpassMapDataDao = Mockito.mock(OverpassMapDataDao.class);
        RoadNameSuggestionsDao roadNameSuggestionsDao = Mockito.mock(RoadNameSuggestionsDao.class);
        PutRoadNameSuggestionsHandler putRoadNameSuggestionsHandler = Mockito.mock(PutRoadNameSuggestionsHandler.class);
        TrafficFlowSegmentsDao trafficFlowSegmentsDao = Mockito.mock(TrafficFlowSegmentsDao.class);
        WayTrafficFlowDao wayTrafficFlowDao = Mockito.mock(WayTrafficFlowDao.class);
        BoundingBox bbox = new BoundingBox(0, 0, 1, 1);
        List<QuestType> questTypes = QuestModule.questTypeRegistry(new OsmNoteQuestType(), overpassMapDataDao, roadNameSuggestionsDao, putRoadNameSuggestionsHandler, trafficFlowSegmentsDao, wayTrafficFlowDao).getAll();
        for (QuestType questType : questTypes) {
            if (questType instanceof SimpleOverpassQuestType) {
                // if this fails and the returned exception is not informative, catch here and record
                // the name of the SimpleOverpassQuestType
                ((SimpleOverpassQuestType) (questType)).getOverpassQuery(bbox);
            }
        }
        // parsing the query threw no errors -> valid
        Assert.assertTrue(true);
    }
}

