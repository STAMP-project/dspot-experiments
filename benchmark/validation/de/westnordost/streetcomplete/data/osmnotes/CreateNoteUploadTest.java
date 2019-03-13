package de.westnordost.streetcomplete.data.osmnotes;


import Element.Type;
import Note.Status;
import de.westnordost.osmapi.common.errors.OsmConflictException;
import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.data.Way;
import de.westnordost.osmapi.notes.Note;
import de.westnordost.osmapi.notes.NotesDao;
import de.westnordost.streetcomplete.ApplicationConstants;
import de.westnordost.streetcomplete.data.statistics.QuestStatisticsDao;
import de.westnordost.streetcomplete.util.ImageUploader;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CreateNoteUploadTest {
    private MapDataDao mapDataDao;

    private CreateNoteDao createNoteDb;

    private NotesDao notesDao;

    private OsmNoteQuestDao osmNoteQuestDb;

    private NoteDao noteDb;

    private QuestStatisticsDao questStatisticsDb;

    private ImageUploader imageUploader;

    private CreateNoteUpload createNoteUpload;

    @Test
    public void cancel() throws InterruptedException {
        Mockito.when(createNoteDb.getAll(ArgumentMatchers.any())).thenAnswer(( invocation) -> {
            Thread.sleep(1000);// take your time...

            ArrayList<CreateNote> result = new ArrayList<>();
            result.add(null);
            return result;
        });
        final AtomicBoolean cancel = new AtomicBoolean(false);
        Thread t = new Thread(() -> createNoteUpload.upload(cancel));
        t.start();
        cancel.set(true);
        // cancelling the thread works if we come out here without exceptions. If the note upload
        // would actually try to start anything, there would be a nullpointer exception since we
        // feeded it only with nulls to work with
        t.join();
    }

    @Test
    public void uploadNoteForDeletedElementWillCancel() {
        /* the mock for MapDataDao returns null for getNode, getWay, getRelation... by defai?t */
        CreateNote createNote = createACreateNote();
        createNote.elementType = Type.WAY;
        createNote.elementId = 5L;
        Assert.assertNull(createNoteUpload.uploadCreateNote(createNote));
        verifyNoteNotInsertedIntoDb(createNote.id);
    }

    @Test
    public void createNoteOnExistingNoteWillCommentOnExistingNote() {
        CreateNote createNote = createACreateNote();
        createNote.elementType = Type.WAY;
        createNote.elementId = 5L;
        Note note = createNote(createNote);
        setUpThereIsANoteFor(createNote, note);
        Mockito.when(notesDao.comment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(note);
        Assert.assertNotNull(createNoteUpload.uploadCreateNote(createNote));
        Mockito.verify(notesDao).comment(note.id, createNote.text);
        verifyNoteInsertedIntoDb(createNote.id, note);
    }

    @Test
    public void createNoteOnExistingClosedNoteWillCancel() {
        CreateNote createNote = createACreateNote();
        createNote.elementType = Type.WAY;
        createNote.elementId = 5L;
        Note note = createNote(createNote);
        note.status = Status.CLOSED;
        setUpThereIsANoteFor(createNote, note);
        Assert.assertNull(createNoteUpload.uploadCreateNote(createNote));
        Mockito.verify(notesDao).getAll(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verifyNoMoreInteractions(notesDao);
        verifyNoteNotInsertedIntoDb(createNote.id);
    }

    @Test
    public void createNoteOnExistingNoteWillCancelWhenConflictException() {
        CreateNote createNote = createACreateNote();
        createNote.elementType = Type.WAY;
        createNote.elementId = 5L;
        Note note = createNote(createNote);
        setUpThereIsANoteFor(createNote, note);
        Mockito.when(notesDao.comment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenThrow(OsmConflictException.class);
        Assert.assertNull(createNoteUpload.uploadCreateNote(createNote));
        Mockito.verify(notesDao).comment(note.id, createNote.text);
        verifyNoteNotInsertedIntoDb(createNote.id);
    }

    @Test
    public void createNoteWithNoAssociatedElement() {
        CreateNote createNote = createACreateNote();
        Note note = createNote(null);
        Mockito.when(notesDao.create(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(note);
        Assert.assertNotNull(createNoteUpload.uploadCreateNote(createNote));
        Mockito.verify(notesDao).create(createNote.position, (((createNote.text) + "\n\nvia ") + (ApplicationConstants.USER_AGENT)));
        verifyNoteInsertedIntoDb(createNote.id, note);
    }

    @Test
    public void createNoteWithNoQuestTitleButAssociatedElement() {
        CreateNote createNote = createACreateNote();
        createNote.elementType = Type.WAY;
        createNote.elementId = 5L;
        Mockito.when(mapDataDao.getWay(createNote.elementId)).thenReturn(Mockito.mock(Way.class));
        Note note = createNote(null);
        Mockito.when(notesDao.create(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(note);
        Assert.assertNotNull(createNoteUpload.uploadCreateNote(createNote));
        Mockito.verify(notesDao).create(createNote.position, (("for https://osm.org/way/5 via " + (ApplicationConstants.USER_AGENT)) + ":\n\njo ho"));
        verifyNoteInsertedIntoDb(createNote.id, note);
    }

    @Test
    public void createNoteWithAssociatedElementAndNoNoteYet() {
        CreateNote createNote = createACreateNote();
        createNote.elementType = Type.WAY;
        createNote.elementId = 5L;
        createNote.questTitle = "What?";
        Mockito.when(mapDataDao.getWay(createNote.elementId)).thenReturn(Mockito.mock(Way.class));
        Note note = createNote(createNote);
        Mockito.when(notesDao.create(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(note);
        Assert.assertNotNull(createNoteUpload.uploadCreateNote(createNote));
        Mockito.verify(notesDao).create(createNote.position, (("Unable to answer \"What?\" for https://osm.org/way/5 via " + (ApplicationConstants.USER_AGENT)) + ":\n\njo ho"));
        verifyNoteInsertedIntoDb(createNote.id, note);
    }

    @Test
    public void createNoteUploadsImagesAndDisplaysLinks() {
        CreateNote createNote = createACreateNote();
        createNote.imagePaths = new java.util.ArrayList();
        createNote.imagePaths.add("hello");
        Note note = createNote(null);
        Mockito.when(notesDao.create(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(note);
        Mockito.when(imageUploader.upload(createNote.imagePaths)).thenReturn(Collections.singletonList("hello, too"));
        Assert.assertNotNull(createNoteUpload.uploadCreateNote(createNote));
        Mockito.verify(imageUploader).upload(createNote.imagePaths);
        Mockito.verify(notesDao).create(createNote.position, (("jo ho\n\nvia " + (ApplicationConstants.USER_AGENT)) + "\n\nAttached photo(s):\nhello, too"));
    }

    @Test
    public void commentNoteUploadsImagesAndDisplaysLinks() {
        CreateNote createNote = createACreateNote();
        createNote.elementType = Type.WAY;
        createNote.elementId = 5L;
        createNote.imagePaths = new java.util.ArrayList();
        createNote.imagePaths.add("hello");
        Note note = createNote(createNote);
        setUpThereIsANoteFor(createNote, note);
        Mockito.when(imageUploader.upload(createNote.imagePaths)).thenReturn(Collections.singletonList("hello, too"));
        Mockito.when(notesDao.comment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(note);
        Assert.assertNotNull(createNoteUpload.uploadCreateNote(createNote));
        Mockito.verify(notesDao).comment(note.id, "jo ho\n\nAttached photo(s):\nhello, too");
        verifyNoteInsertedIntoDb(createNote.id, note);
    }
}

