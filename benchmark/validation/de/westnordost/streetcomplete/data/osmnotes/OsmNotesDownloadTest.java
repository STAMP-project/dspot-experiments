package de.westnordost.streetcomplete.data.osmnotes;


import Prefs.SHOW_NOTES_NOT_PHRASED_AS_QUESTIONS;
import android.content.SharedPreferences;
import de.westnordost.osmapi.common.Handler;
import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.osmapi.notes.Note;
import de.westnordost.osmapi.notes.NotesDao;
import de.westnordost.streetcomplete.data.QuestStatus;
import de.westnordost.streetcomplete.data.VisibleQuestListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class OsmNotesDownloadTest {
    private NoteDao noteDB;

    private OsmNoteQuestDao noteQuestDB;

    private CreateNoteDao createNoteDB;

    private SharedPreferences preferences;

    private OsmAvatarsDownload avatarsDownload;

    @Test
    public void deleteObsoleteQuests() {
        Mockito.when(preferences.getBoolean(SHOW_NOTES_NOT_PHRASED_AS_QUESTIONS, false)).thenReturn(true);
        // in the quest database mock, there are quests for note 4 and note 5
        List<OsmNoteQuest> quests = new ArrayList<>();
        Note note1 = createANote();
        note1.id = 4L;
        quests.add(new OsmNoteQuest(12L, note1, QuestStatus.NEW, null, new Date(), new OsmNoteQuestType(), null));
        Note note2 = createANote();
        note2.id = 5L;
        quests.add(new OsmNoteQuest(13L, note2, QuestStatus.NEW, null, new Date(), new OsmNoteQuestType(), null));
        Mockito.when(noteQuestDB.getAll(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(quests);
        Mockito.doAnswer(( invocation) -> {
            Collection<Long> deletedQuests = ((Collection<Long>) (invocation.getArguments()[0]));
            Assert.assertEquals(1, deletedQuests.size());
            Assert.assertEquals(13L, ((long) (deletedQuests.iterator().next())));
            return 1;
        }).when(noteQuestDB).deleteAll(ArgumentMatchers.any());
        // note dao mock will only "find" the note #4
        List<Note> notes = new ArrayList<>();
        notes.add(note1);
        NotesDao noteServer = new OsmNotesDownloadTest.TestListBasedNotesDao(notes);
        OsmNotesDownload dl = new OsmNotesDownload(noteServer, noteDB, noteQuestDB, createNoteDB, preferences, new OsmNoteQuestType(), avatarsDownload);
        VisibleQuestListener listener = Mockito.mock(VisibleQuestListener.class);
        dl.setQuestListener(listener);
        dl.download(new BoundingBox(0, 0, 1, 1), null, 1000);
        Mockito.verify(noteQuestDB).deleteAll(ArgumentMatchers.any());
        Mockito.verify(listener).onQuestsRemoved(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    private static class TestListBasedNotesDao extends NotesDao {
        List<Note> notes;

        public TestListBasedNotesDao(List<Note> notes) {
            super(null);
            this.notes = notes;
        }

        @Override
        public void getAll(BoundingBox bounds, Handler<Note> handler, int limit, int hideClosedNoteAfter) {
            // ignoring all the parameters except the handler...
            for (Note note : notes) {
                handler.handle(note);
            }
        }
    }
}

