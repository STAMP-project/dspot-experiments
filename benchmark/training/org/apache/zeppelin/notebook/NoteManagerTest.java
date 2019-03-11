package org.apache.zeppelin.notebook;


import AuthenticationInfo.ANONYMOUS;
import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class NoteManagerTest {
    private NoteManager noteManager;

    @Test
    public void testNoteOperations() throws IOException {
        Assert.assertEquals(0, this.noteManager.getNotesInfo().size());
        Note note1 = createNote("/prod/my_note1");
        Note note2 = createNote("/dev/project_2/my_note2");
        Note note3 = createNote("/dev/project_3/my_note3");
        // add note
        this.noteManager.saveNote(note1);
        this.noteManager.saveNote(note2);
        this.noteManager.saveNote(note3);
        // list notes
        Assert.assertEquals(3, this.noteManager.getNotesInfo().size());
        Assert.assertEquals(note1, this.noteManager.getNote(note1.getId()));
        Assert.assertEquals(note2, this.noteManager.getNote(note2.getId()));
        Assert.assertEquals(note3, this.noteManager.getNote(note3.getId()));
        // move note
        this.noteManager.moveNote(note1.getId(), "/dev/project_1/my_note1", ANONYMOUS);
        Assert.assertEquals(3, this.noteManager.getNotesInfo().size());
        Assert.assertEquals("/dev/project_1/my_note1", this.noteManager.getNote(note1.getId()).getPath());
        // move folder
        this.noteManager.moveFolder("/dev", "/staging", ANONYMOUS);
        Map<String, String> notesInfo = this.noteManager.getNotesInfo();
        Assert.assertEquals(3, notesInfo.size());
        Assert.assertEquals("/staging/project_1/my_note1", notesInfo.get(note1.getId()));
        Assert.assertEquals("/staging/project_2/my_note2", notesInfo.get(note2.getId()));
        Assert.assertEquals("/staging/project_3/my_note3", notesInfo.get(note3.getId()));
        this.noteManager.removeNote(note1.getId(), ANONYMOUS);
        Assert.assertEquals(2, this.noteManager.getNotesInfo().size());
        // remove folder
        this.noteManager.removeFolder("/staging", ANONYMOUS);
        notesInfo = this.noteManager.getNotesInfo();
        Assert.assertEquals(0, notesInfo.size());
    }
}

