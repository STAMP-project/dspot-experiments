package de.westnordost.streetcomplete.data.osmnotes;


import QuestStatus.ANSWERED;
import QuestStatus.CLOSED;
import de.westnordost.osmapi.common.errors.OsmConflictException;
import de.westnordost.osmapi.notes.Note;
import de.westnordost.osmapi.notes.NotesDao;
import de.westnordost.streetcomplete.data.statistics.QuestStatisticsDao;
import de.westnordost.streetcomplete.util.ImageUploader;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class OsmNoteQuestChangesUploadTest {
    private ImageUploader imageUploader;

    private NoteDao noteDb;

    private OsmNoteQuestDao questDb;

    private QuestStatisticsDao questStatisticsDb;

    private NotesDao osmDao;

    private OsmNoteQuestChangesUpload osmNoteQuestChangesUpload;

    @Test
    public void cancel() throws InterruptedException {
        Mockito.when(questDb.getAll(null, ANSWERED)).thenAnswer(( invocation) -> {
            Thread.sleep(1000);// take your time...

            ArrayList<OsmNoteQuest> result = new ArrayList<>();
            result.add(null);
            return result;
        });
        final AtomicBoolean cancel = new AtomicBoolean(false);
        Thread t = new Thread(() -> osmNoteQuestChangesUpload.upload(cancel));
        t.start();
        cancel.set(true);
        // cancelling the thread works if we come out here without exceptions. If the note upload
        // would actually try to start anything, there would be a nullpointer exception since we
        // feeded it a null-quest
        t.join();
    }

    @Test
    public void dropCommentWhenConflict() {
        OsmNoteQuest quest = OsmNoteQuestChangesUploadTest.createQuest();
        Mockito.when(osmDao.comment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenThrow(OsmConflictException.class);
        Assert.assertNull(osmNoteQuestChangesUpload.uploadNoteChanges(quest));
        Mockito.verify(questDb).delete(quest.getId());
        Mockito.verify(noteDb).delete(quest.getNote().id);
    }

    @Test
    public void uploadComment() {
        OsmNoteQuest quest = OsmNoteQuestChangesUploadTest.createQuest();
        Mockito.when(osmDao.comment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(Mockito.mock(Note.class));
        Note n = osmNoteQuestChangesUpload.uploadNoteChanges(quest);
        Assert.assertNotNull(n);
        Assert.assertEquals(n, quest.getNote());
        Assert.assertEquals(CLOSED, quest.getStatus());
        Mockito.verify(questDb).update(quest);
        Mockito.verify(noteDb).put(n);
        Mockito.verify(questStatisticsDb).addOneNote();
    }

    @Test
    public void uploadsImagesForComment() {
        OsmNoteQuest quest = OsmNoteQuestChangesUploadTest.createQuest();
        java.util.ArrayList<String> imagePaths = new java.util.ArrayList<>();
        imagePaths.add("Never say");
        quest.setImagePaths(imagePaths);
        Note someNote = new Note();
        someNote.id = 123;
        Mockito.when(osmDao.comment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(someNote);
        Mockito.when(imageUploader.upload(imagePaths)).thenReturn(Collections.singletonList("never"));
        osmNoteQuestChangesUpload.uploadNoteChanges(quest);
        Mockito.verify(osmDao).comment(1, "blablub\n\nAttached photo(s):\nnever");
        Mockito.verify(imageUploader).activate(someNote.id);
    }
}

