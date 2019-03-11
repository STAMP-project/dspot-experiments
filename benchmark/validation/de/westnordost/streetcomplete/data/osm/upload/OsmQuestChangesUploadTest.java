package de.westnordost.streetcomplete.data.osm.upload;


import Element.Type.NODE;
import Prefs.OSM_USER_ID;
import QuestStatus.ANSWERED;
import QuestStatus.CLOSED;
import android.content.SharedPreferences;
import de.westnordost.osmapi.changesets.ChangesetsDao;
import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.osmapi.map.data.Element;
import de.westnordost.streetcomplete.data.changesets.OpenChangesetsDao;
import de.westnordost.streetcomplete.data.osm.Countries;
import de.westnordost.streetcomplete.data.osm.OsmElementQuestType;
import de.westnordost.streetcomplete.data.osm.OsmQuest;
import de.westnordost.streetcomplete.data.osm.OsmQuestGiver;
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder;
import de.westnordost.streetcomplete.data.osm.download.MapDataWithGeometryHandler;
import de.westnordost.streetcomplete.data.osm.persist.ElementGeometryDao;
import de.westnordost.streetcomplete.data.osm.persist.MergedElementDao;
import de.westnordost.streetcomplete.data.osm.persist.OsmQuestDao;
import de.westnordost.streetcomplete.data.statistics.QuestStatisticsDao;
import de.westnordost.streetcomplete.data.tiles.DownloadedTilesDao;
import de.westnordost.streetcomplete.quests.AbstractQuestAnswerFragment;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class OsmQuestChangesUploadTest {
    private static long A_NODE_ID = 5;

    @Test
    public void cancel() throws InterruptedException {
        OsmQuestDao questDb = Mockito.mock(OsmQuestDao.class);
        ElementGeometryDao elementGeometryDao = Mockito.mock(ElementGeometryDao.class);
        MergedElementDao elementDB = Mockito.mock(MergedElementDao.class);
        OpenChangesetsDao openChangesetsDb = Mockito.mock(OpenChangesetsDao.class);
        Mockito.when(questDb.getAll(null, ANSWERED)).thenAnswer(( invocation) -> {
            Thread.sleep(1000);// take your time...

            ArrayList<OsmQuest> result = new ArrayList<>();
            result.add(null);
            return result;
        });
        final OsmQuestChangesUpload u = new OsmQuestChangesUpload(null, questDb, elementDB, elementGeometryDao, null, openChangesetsDb, null, null, null, null);
        final AtomicBoolean cancel = new AtomicBoolean(false);
        Thread t = new Thread(() -> u.upload(cancel));
        t.start();
        cancel.set(true);
        // cancelling the thread works if we come out here without exceptions. If the upload
        // would actually try to start anything, there would be a nullpointer exception since we
        // feeded it only with nulls to work with
        t.join();
        // this is not called anymore immediately since quests are kept in DB for some time before deletion (#373)
        // verify(elementGeometryDao).deleteUnreferenced();
        // verify(elementDB).deleteUnreferenced();
    }

    @Test
    public void dropChangeWhenElementDeleted() {
        OsmQuest quest = OsmQuestChangesUploadTest.createAnsweredQuest(null);
        OsmQuestDao questDb = Mockito.mock(OsmQuestDao.class);
        DownloadedTilesDao downloadedTilesDao = Mockito.mock(DownloadedTilesDao.class);
        OsmQuestChangesUpload u = new OsmQuestChangesUpload(null, questDb, null, null, null, null, null, downloadedTilesDao, null, null);
        Assert.assertFalse(u.uploadQuestChange((-1), quest, null, false, false));
        Mockito.verify(downloadedTilesDao).remove(ArgumentMatchers.any());
        Mockito.verify(questDb).delete(quest.getId());
    }

    @Test
    public void dropChangeWhenUnresolvableElementChange() {
        OsmQuest quest = OsmQuestChangesUploadTest.createAnsweredQuestWithNonAppliableChange();
        Element element = OsmQuestChangesUploadTest.createElement();
        OsmQuestDao questDb = Mockito.mock(OsmQuestDao.class);
        MergedElementDao elementDao = Mockito.mock(MergedElementDao.class);
        DownloadedTilesDao downloadedTilesDao = Mockito.mock(DownloadedTilesDao.class);
        OsmQuestChangesUpload u = new OsmQuestChangesUpload(null, questDb, elementDao, null, null, null, null, downloadedTilesDao, null, null);
        Assert.assertFalse(u.uploadQuestChange(123, quest, element, false, false));
        Mockito.verify(questDb).delete(quest.getId());
        Mockito.verify(downloadedTilesDao).remove(ArgumentMatchers.any());
    }

    /* Simulates an element conflict while uploading the element, when updating the element from
    mock server, it turns out that it has been deleted
     */
    @Test
    public void handleElementConflictAndThenDeleted() {
        final long changesetId = 123;
        final long userId = 10;
        OsmQuest quest = OsmQuestChangesUploadTest.createAnsweredQuestWithAppliableChange();
        Element element = OsmQuestChangesUploadTest.createElement();
        MergedElementDao elementDb = Mockito.mock(MergedElementDao.class);
        OsmQuestDao questDb = Mockito.mock(OsmQuestDao.class);
        DownloadedTilesDao downloadedTilesDao = Mockito.mock(DownloadedTilesDao.class);
        OsmQuestGiver questGiver = Mockito.mock(OsmQuestGiver.class);
        MapDataDao mapDataDao = OsmQuestChangesUploadTest.createMapDataDaoThatReportsConflictOnUploadAndNodeDeleted();
        // a changeset dao+prefs that report that the changeset is open and the changeset is owned by the user
        ChangesetsDao changesetsDao = Mockito.mock(ChangesetsDao.class);
        Mockito.when(changesetsDao.get(changesetId)).thenReturn(OsmQuestChangesUploadTest.createOpenChangesetForUser(userId));
        SharedPreferences prefs = Mockito.mock(SharedPreferences.class);
        Mockito.when(prefs.getLong(OSM_USER_ID, (-1))).thenReturn(userId);
        OsmQuestChangesUpload u = new OsmQuestChangesUpload(mapDataDao, questDb, elementDb, null, null, null, changesetsDao, downloadedTilesDao, prefs, questGiver);
        Assert.assertFalse(u.uploadQuestChange(changesetId, quest, element, false, false));
        Mockito.verify(questDb).delete(quest.getId());
        Mockito.verify(elementDb).delete(NODE, OsmQuestChangesUploadTest.A_NODE_ID);
        Mockito.verify(downloadedTilesDao).remove(ArgumentMatchers.any());
    }

    /* Simulates the changeset that is about to be used was created by a different user, so a new
     changeset needs to be created. (after that, it runs into the same case above, for simplicity
     sake
     */
    @Test
    public void handleChangesetConflictDifferentUser() {
        final long userId = 10;
        final long otherUserId = 15;
        final long firstChangesetId = 123;
        final long secondChangesetId = 124;
        // reports that the changeset is open but does belong to another user
        ChangesetsDao changesetsDao = Mockito.mock(ChangesetsDao.class);
        Mockito.when(changesetsDao.get(firstChangesetId)).thenReturn(OsmQuestChangesUploadTest.createOpenChangesetForUser(otherUserId));
        Mockito.when(changesetsDao.get(secondChangesetId)).thenReturn(OsmQuestChangesUploadTest.createOpenChangesetForUser(userId));
        doTestHandleChangesetConflict(changesetsDao, userId, firstChangesetId, secondChangesetId);
    }

    /* Simulates the changeset that is about to be used is already closed, so a new changeset needs
    to be created. (after that, it runs into the same case above, for simplicity sake
     */
    @Test
    public void handleChangesetConflictAlreadyClosed() {
        final long userId = 10;
        final long firstChangesetId = 123;
        final long secondChangesetId = 124;
        // reports that the changeset is open but does belong to another user
        ChangesetsDao changesetsDao = Mockito.mock(ChangesetsDao.class);
        Mockito.when(changesetsDao.get(firstChangesetId)).thenReturn(OsmQuestChangesUploadTest.createClosedChangesetForUser(userId));
        Mockito.when(changesetsDao.get(secondChangesetId)).thenReturn(OsmQuestChangesUploadTest.createOpenChangesetForUser(userId));
        doTestHandleChangesetConflict(changesetsDao, userId, firstChangesetId, secondChangesetId);
    }

    @Test
    public void uploadNormally() {
        OsmQuest quest = OsmQuestChangesUploadTest.createAnsweredQuestWithAppliableChange();
        Element element = OsmQuestChangesUploadTest.createElement();
        OsmQuestDao questDb = Mockito.mock(OsmQuestDao.class);
        MapDataDao mapDataDao = Mockito.mock(MapDataDao.class);
        QuestStatisticsDao statisticsDao = Mockito.mock(QuestStatisticsDao.class);
        MergedElementDao elementDb = Mockito.mock(MergedElementDao.class);
        OsmQuestGiver osmQuestUnlocker = Mockito.mock(OsmQuestGiver.class);
        Mockito.when(osmQuestUnlocker.updateQuests(ArgumentMatchers.any())).thenReturn(new OsmQuestGiver.QuestUpdates());
        OsmQuestChangesUpload u = new OsmQuestChangesUpload(mapDataDao, questDb, elementDb, null, statisticsDao, null, null, null, null, osmQuestUnlocker);
        Assert.assertTrue(u.uploadQuestChange(1, quest, element, false, false));
        Assert.assertEquals(CLOSED, quest.getStatus());
        Mockito.verify(statisticsDao).addOne("TestQuestType");
    }

    private static class TestQuestType implements OsmElementQuestType<String> {
        @NotNull
        @Override
        public String getCommitMessage() {
            return "";
        }

        @Override
        public int getTitle(@NotNull
        Map<String, String> tags) {
            return 0;
        }

        @Override
        public boolean download(@NotNull
        BoundingBox bbox, @NotNull
        MapDataWithGeometryHandler handler) {
            return false;
        }

        @Override
        public Boolean isApplicableTo(@NotNull
        Element element) {
            return null;
        }

        @Override
        public void applyAnswerTo(@NotNull
        String answer, @NotNull
        StringMapChangesBuilder changes) {
        }

        @Override
        public int getIcon() {
            return 0;
        }

        @NotNull
        @Override
        public AbstractQuestAnswerFragment createForm() {
            return new AbstractQuestAnswerFragment<String>() {};
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

