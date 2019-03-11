/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.service;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang.StringUtils;
import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.NoteInfo;
import org.apache.zeppelin.notebook.Paragraph;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class NotebookServiceTest {
    private static NotebookService notebookService;

    private ServiceContext context = new ServiceContext(AuthenticationInfo.ANONYMOUS, new HashSet());

    private ServiceCallback callback = Mockito.mock(ServiceCallback.class);

    private Gson gson = new Gson();

    @Test
    public void testNoteOperations() throws IOException {
        // get home note
        Note homeNote = NotebookServiceTest.notebookService.getHomeNote(context, callback);
        Assert.assertNull(homeNote);
        Mockito.verify(callback).onSuccess(homeNote, context);
        // create note
        Note note1 = NotebookServiceTest.notebookService.createNote("/folder_1/note1", "test", context, callback);
        Assert.assertEquals("note1", note1.getName());
        Assert.assertEquals(1, note1.getParagraphCount());
        Mockito.verify(callback).onSuccess(note1, context);
        // list note
        Mockito.reset(callback);
        List<NoteInfo> notesInfo = NotebookServiceTest.notebookService.listNotesInfo(false, context, callback);
        Assert.assertEquals(1, notesInfo.size());
        Assert.assertEquals(note1.getId(), notesInfo.get(0).getId());
        Assert.assertEquals(note1.getName(), notesInfo.get(0).getNoteName());
        Mockito.verify(callback).onSuccess(notesInfo, context);
        // get note
        Mockito.reset(callback);
        Note note1_copy = NotebookServiceTest.notebookService.getNote(note1.getId(), context, callback);
        Assert.assertEquals(note1, note1_copy);
        Mockito.verify(callback).onSuccess(note1_copy, context);
        // rename note
        Mockito.reset(callback);
        NotebookServiceTest.notebookService.renameNote(note1.getId(), "/folder_2/new_name", false, context, callback);
        Mockito.verify(callback).onSuccess(note1, context);
        Assert.assertEquals("new_name", note1.getName());
        // move folder
        Mockito.reset(callback);
        notesInfo = NotebookServiceTest.notebookService.renameFolder("/folder_2", "/folder_3", context, callback);
        Mockito.verify(callback).onSuccess(notesInfo, context);
        Assert.assertEquals(1, notesInfo.size());
        Assert.assertEquals("/folder_3/new_name", notesInfo.get(0).getPath());
        // create another note
        Note note2 = NotebookServiceTest.notebookService.createNote("/note2", "test", context, callback);
        Assert.assertEquals("note2", note2.getName());
        Mockito.verify(callback).onSuccess(note2, context);
        // rename note
        Mockito.reset(callback);
        NotebookServiceTest.notebookService.renameNote(note2.getId(), "new_note2", true, context, callback);
        Mockito.verify(callback).onSuccess(note2, context);
        Assert.assertEquals("new_note2", note2.getName());
        // list note
        Mockito.reset(callback);
        notesInfo = NotebookServiceTest.notebookService.listNotesInfo(false, context, callback);
        Assert.assertEquals(2, notesInfo.size());
        Mockito.verify(callback).onSuccess(notesInfo, context);
        // delete note
        Mockito.reset(callback);
        NotebookServiceTest.notebookService.removeNote(note2.getId(), context, callback);
        Mockito.verify(callback).onSuccess("Delete note successfully", context);
        // list note again
        Mockito.reset(callback);
        notesInfo = NotebookServiceTest.notebookService.listNotesInfo(false, context, callback);
        Assert.assertEquals(1, notesInfo.size());
        Mockito.verify(callback).onSuccess(notesInfo, context);
        // delete folder
        notesInfo = NotebookServiceTest.notebookService.removeFolder("/folder_3", context, callback);
        Mockito.verify(callback).onSuccess(notesInfo, context);
        // list note again
        Mockito.reset(callback);
        notesInfo = NotebookServiceTest.notebookService.listNotesInfo(false, context, callback);
        Assert.assertEquals(0, notesInfo.size());
        Mockito.verify(callback).onSuccess(notesInfo, context);
        // import note
        Mockito.reset(callback);
        Note importedNote = NotebookServiceTest.notebookService.importNote("/Imported Note", "{}", context, callback);
        Assert.assertNotNull(importedNote);
        Mockito.verify(callback).onSuccess(importedNote, context);
        // clone note
        Mockito.reset(callback);
        Note clonedNote = NotebookServiceTest.notebookService.cloneNote(importedNote.getId(), "/Backup/Cloned Note", context, callback);
        Assert.assertEquals(importedNote.getParagraphCount(), clonedNote.getParagraphCount());
        Mockito.verify(callback).onSuccess(clonedNote, context);
        // list note
        Mockito.reset(callback);
        notesInfo = NotebookServiceTest.notebookService.listNotesInfo(false, context, callback);
        Assert.assertEquals(2, notesInfo.size());
        Mockito.verify(callback).onSuccess(notesInfo, context);
        // move note to Trash
        NotebookServiceTest.notebookService.moveNoteToTrash(importedNote.getId(), context, callback);
        Mockito.reset(callback);
        notesInfo = NotebookServiceTest.notebookService.listNotesInfo(false, context, callback);
        Assert.assertEquals(2, notesInfo.size());
        Mockito.verify(callback).onSuccess(notesInfo, context);
        boolean moveToTrash = false;
        for (NoteInfo noteInfo : notesInfo) {
            if (noteInfo.getId().equals(importedNote.getId())) {
                Assert.assertEquals("/~Trash/Imported Note", noteInfo.getPath());
                moveToTrash = true;
            }
        }
        Assert.assertTrue("No note is moved to trash", moveToTrash);
        // restore it
        NotebookServiceTest.notebookService.restoreNote(importedNote.getId(), context, callback);
        Note restoredNote = NotebookServiceTest.notebookService.getNote(importedNote.getId(), context, callback);
        Assert.assertNotNull(restoredNote);
        Assert.assertEquals("/Imported Note", restoredNote.getPath());
        // move it to Trash again
        NotebookServiceTest.notebookService.moveNoteToTrash(restoredNote.getId(), context, callback);
        // remove note from Trash
        Mockito.reset(callback);
        NotebookServiceTest.notebookService.removeNote(importedNote.getId(), context, callback);
        notesInfo = NotebookServiceTest.notebookService.listNotesInfo(false, context, callback);
        Assert.assertEquals(1, notesInfo.size());
        // move folder to Trash
        NotebookServiceTest.notebookService.moveFolderToTrash("Backup", context, callback);
        Mockito.reset(callback);
        notesInfo = NotebookServiceTest.notebookService.listNotesInfo(false, context, callback);
        Assert.assertEquals(1, notesInfo.size());
        Mockito.verify(callback).onSuccess(notesInfo, context);
        moveToTrash = false;
        for (NoteInfo noteInfo : notesInfo) {
            if (noteInfo.getId().equals(clonedNote.getId())) {
                Assert.assertEquals("/~Trash/Backup/Cloned Note", noteInfo.getPath());
                moveToTrash = true;
            }
        }
        Assert.assertTrue("No folder is moved to trash", moveToTrash);
        // restore folder
        Mockito.reset(callback);
        NotebookServiceTest.notebookService.restoreFolder("/~Trash/Backup", context, callback);
        restoredNote = NotebookServiceTest.notebookService.getNote(clonedNote.getId(), context, callback);
        Assert.assertNotNull(restoredNote);
        Assert.assertEquals("/Backup/Cloned Note", restoredNote.getPath());
        // move the folder to trash again
        NotebookServiceTest.notebookService.moveFolderToTrash("Backup", context, callback);
        // remove folder from Trash
        Mockito.reset(callback);
        NotebookServiceTest.notebookService.removeFolder("/~Trash/Backup", context, callback);
        notesInfo = NotebookServiceTest.notebookService.listNotesInfo(false, context, callback);
        Assert.assertEquals(0, notesInfo.size());
        // empty trash
        NotebookServiceTest.notebookService.emptyTrash(context, callback);
        notesInfo = NotebookServiceTest.notebookService.listNotesInfo(false, context, callback);
        Assert.assertEquals(0, notesInfo.size());
    }

    @Test
    public void testParagraphOperations() throws IOException {
        // create note
        Note note1 = NotebookServiceTest.notebookService.createNote("note1", "python", context, callback);
        Assert.assertEquals("note1", note1.getName());
        Assert.assertEquals(1, note1.getParagraphCount());
        Mockito.verify(callback).onSuccess(note1, context);
        // add paragraph
        Mockito.reset(callback);
        Paragraph p = NotebookServiceTest.notebookService.insertParagraph(note1.getId(), 1, new HashMap(), context, callback);
        Assert.assertNotNull(p);
        Mockito.verify(callback).onSuccess(p, context);
        Assert.assertEquals(2, note1.getParagraphCount());
        // update paragraph
        Mockito.reset(callback);
        NotebookServiceTest.notebookService.updateParagraph(note1.getId(), p.getId(), "my_title", "my_text", new HashMap(), new HashMap(), context, callback);
        Assert.assertEquals("my_title", p.getTitle());
        Assert.assertEquals("my_text", p.getText());
        // move paragraph
        Mockito.reset(callback);
        NotebookServiceTest.notebookService.moveParagraph(note1.getId(), p.getId(), 0, context, callback);
        Assert.assertEquals(p, note1.getParagraph(0));
        Mockito.verify(callback).onSuccess(p, context);
        // run paragraph asynchronously
        Mockito.reset(callback);
        boolean runStatus = NotebookServiceTest.notebookService.runParagraph(note1.getId(), p.getId(), "my_title", "1+1", new HashMap(), new HashMap(), false, false, context, callback);
        Assert.assertTrue(runStatus);
        Mockito.verify(callback).onSuccess(p, context);
        // run paragraph synchronously via correct code
        Mockito.reset(callback);
        runStatus = NotebookServiceTest.notebookService.runParagraph(note1.getId(), p.getId(), "my_title", "1+1", new HashMap(), new HashMap(), false, true, context, callback);
        Assert.assertTrue(runStatus);
        Mockito.verify(callback).onSuccess(p, context);
        // run all paragraphs
        Mockito.reset(callback);
        NotebookServiceTest.notebookService.runAllParagraphs(note1.getId(), gson.fromJson(gson.toJson(note1.getParagraphs()), new TypeToken<List>() {}.getType()), context, callback);
        Mockito.verify(callback, Mockito.times(2)).onSuccess(ArgumentMatchers.any(), ArgumentMatchers.any());
        // run paragraph synchronously via invalid code
        // TODO(zjffdu) must sleep for a while, otherwise will get wrong status. This should be due to
        // bug of job component.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.reset(callback);
        runStatus = NotebookServiceTest.notebookService.runParagraph(note1.getId(), p.getId(), "my_title", "invalid_code", new HashMap(), new HashMap(), false, true, context, callback);
        Assert.assertFalse(runStatus);
        // TODO(zjffdu) Enable it after ZEPPELIN-3699
        // assertNotNull(p.getResult());
        Mockito.verify(callback).onSuccess(p, context);
        // clean output
        Mockito.reset(callback);
        NotebookServiceTest.notebookService.clearParagraphOutput(note1.getId(), p.getId(), context, callback);
        Assert.assertNull(p.getReturn());
        Mockito.verify(callback).onSuccess(p, context);
    }

    @Test
    public void testNormalizeNotePath() throws IOException {
        Assert.assertEquals("/Untitled Note", NotebookServiceTest.notebookService.normalizeNotePath(" "));
        Assert.assertEquals("/Untitled Note", NotebookServiceTest.notebookService.normalizeNotePath(null));
        Assert.assertEquals("/my_note", NotebookServiceTest.notebookService.normalizeNotePath("my_note"));
        Assert.assertEquals("/my  note", NotebookServiceTest.notebookService.normalizeNotePath("my\r\nnote"));
        try {
            String longNoteName = StringUtils.join(IntStream.range(0, 256).boxed().collect(Collectors.toList()), "");
            NotebookServiceTest.notebookService.normalizeNotePath(longNoteName);
            Assert.fail("Should fail");
        } catch (IOException e) {
            Assert.assertEquals("Note name must be less than 255", e.getMessage());
        }
        try {
            NotebookServiceTest.notebookService.normalizeNotePath("my..note");
            Assert.fail("Should fail");
        } catch (IOException e) {
            Assert.assertEquals("Note name can not contain '..'", e.getMessage());
        }
    }
}

