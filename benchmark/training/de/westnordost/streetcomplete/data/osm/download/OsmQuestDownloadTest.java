package de.westnordost.streetcomplete.data.osm.download;


import Element.Type;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.westnordost.countryboundaries.CountryBoundaries;
import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.OsmLatLon;
import de.westnordost.streetcomplete.data.QuestStatus;
import de.westnordost.streetcomplete.data.VisibleQuestListener;
import de.westnordost.streetcomplete.data.osm.Countries;
import de.westnordost.streetcomplete.data.osm.ElementGeometry;
import de.westnordost.streetcomplete.data.osm.OsmElementQuestType;
import de.westnordost.streetcomplete.data.osm.OsmQuest;
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder;
import de.westnordost.streetcomplete.data.osm.persist.ElementGeometryDao;
import de.westnordost.streetcomplete.data.osm.persist.MergedElementDao;
import de.westnordost.streetcomplete.data.osm.persist.OsmQuestDao;
import de.westnordost.streetcomplete.quests.AbstractQuestAnswerFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class OsmQuestDownloadTest {
    private ElementGeometryDao geometryDb;

    private MergedElementDao elementDb;

    private OsmQuestDao osmQuestDao;

    private FutureTask<CountryBoundaries> countryBoundariesFuture;

    @Test
    public void ignoreBlacklistedPositionsAndInvalidGeometry() {
        LatLon blacklistPos = new OsmLatLon(3.0, 4.0);
        OsmQuestDownloadTest.ElementWithGeometry blacklistElement = new OsmQuestDownloadTest.ElementWithGeometry();
        blacklistElement.element = new de.westnordost.osmapi.map.data.OsmNode(0, 0, blacklistPos, null);
        blacklistElement.geometry = new ElementGeometry(blacklistPos);
        OsmQuestDownloadTest.ElementWithGeometry invalidGeometryElement = new OsmQuestDownloadTest.ElementWithGeometry();
        invalidGeometryElement.element = new de.westnordost.osmapi.map.data.OsmNode(0, 0, new OsmLatLon(1.0, 1.0), null);
        invalidGeometryElement.geometry = null;
        OsmElementQuestType questType = new OsmQuestDownloadTest.ListBackedQuestType(Arrays.asList(blacklistElement, invalidGeometryElement));
        setUpOsmQuestDaoMockWithNoPreviousElements();
        OsmQuestDownload dl = new OsmQuestDownload(geometryDb, elementDb, osmQuestDao, countryBoundariesFuture);
        VisibleQuestListener listener = Mockito.mock(VisibleQuestListener.class);
        dl.setQuestListener(listener);
        dl.download(questType, new BoundingBox(0, 0, 1, 1), Collections.singleton(blacklistPos));
        Mockito.verify(listener, VerificationModeFactory.times(0)).onQuestsCreated(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void deleteObsoleteQuests() {
        LatLon pos = new OsmLatLon(3.0, 4.0);
        OsmQuestDownloadTest.ElementWithGeometry node4 = new OsmQuestDownloadTest.ElementWithGeometry();
        node4.element = new de.westnordost.osmapi.map.data.OsmNode(4, 0, pos, null);
        node4.geometry = new ElementGeometry(pos);
        // questType mock will only "find" the Node #4
        OsmElementQuestType questType = new OsmQuestDownloadTest.ListBackedQuestType(Collections.singletonList(node4));
        // in the quest database mock, there are quests for node 4 and node 5
        List<OsmQuest> quests = new ArrayList<>();
        quests.add(new OsmQuest(12L, questType, Type.NODE, 4, QuestStatus.NEW, null, null, new Date(), new ElementGeometry(pos)));
        quests.add(new OsmQuest(13L, questType, Type.NODE, 5, QuestStatus.NEW, null, null, new Date(), new ElementGeometry(pos)));
        Mockito.when(osmQuestDao.getAll(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(quests);
        Mockito.doAnswer(( invocation) -> {
            Collection<Long> deletedQuests = ((Collection<Long>) (invocation.getArguments()[0]));
            Assert.assertEquals(1, deletedQuests.size());
            Assert.assertEquals(13L, ((long) (deletedQuests.iterator().next())));
            return 1;
        }).when(osmQuestDao).deleteAll(ArgumentMatchers.any());
        OsmQuestDownload dl = new OsmQuestDownload(geometryDb, elementDb, osmQuestDao, countryBoundariesFuture);
        VisibleQuestListener listener = Mockito.mock(VisibleQuestListener.class);
        dl.setQuestListener(listener);
        // -> we expect that quest with node #5 is removed
        dl.download(questType, new BoundingBox(0, 0, 1, 1), Collections.emptySet());
        Mockito.verify(osmQuestDao).deleteAll(ArgumentMatchers.any());
        Mockito.verify(listener).onQuestsRemoved(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    private static class ElementWithGeometry {
        Element element;

        ElementGeometry geometry;
    }

    private static class ListBackedQuestType implements OsmElementQuestType<String> {
        private final List<OsmQuestDownloadTest.ElementWithGeometry> list;

        public ListBackedQuestType(List<OsmQuestDownloadTest.ElementWithGeometry> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public AbstractQuestAnswerFragment<String> createForm() {
            return new AbstractQuestAnswerFragment<String>() {};
        }

        @Override
        public int getIcon() {
            return 0;
        }

        @Override
        public int getTitle(@NonNull
        Map<String, String> tags) {
            return 0;
        }

        @Override
        public void applyAnswerTo(@NonNull
        String answer, @NonNull
        StringMapChangesBuilder changes) {
        }

        @Override
        @NonNull
        public String getCommitMessage() {
            return "";
        }

        @Nullable
        @Override
        public Boolean isApplicableTo(@NonNull
        Element element) {
            return false;
        }

        @Override
        public boolean download(@NonNull
        BoundingBox bbox, @NonNull
        MapDataWithGeometryHandler handler) {
            for (OsmQuestDownloadTest.ElementWithGeometry e : list) {
                handler.handle(e.element, e.geometry);
            }
            return true;
        }

        @NotNull
        @Override
        public Countries getEnabledForCountries() {
            return Countries.ALL;
        }

        @Override
        public boolean getHasMarkersAtEnds() {
            return false;
        }

        @Override
        public int getTitle() {
            return 0;
        }

        @Override
        public void cleanMetadata() {
        }

        @Override
        public int getDefaultDisabledMessage() {
            return 0;
        }
    }
}

