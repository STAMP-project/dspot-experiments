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
package org.apache.zeppelin.notebook;


import AuthenticationInfo.ANONYMOUS;
import ConfVars.ZEPPELIN_NOTEBOOK_CRON_ENABLE;
import ConfVars.ZEPPELIN_NOTEBOOK_CRON_FOLDERS;
import ConfVars.ZEPPELIN_NOTEBOOK_PUBLIC;
import InterpreterOption.ISOLATED;
import InterpreterOption.SCOPED;
import Status.ABORT;
import Status.FINISHED;
import Status.PENDING;
import Status.READY;
import Status.RUNNING;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.display.AngularObjectRegistry;
import org.apache.zeppelin.interpreter.AbstractInterpreterTest;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterGroup;
import org.apache.zeppelin.interpreter.InterpreterNotFoundException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterSetting;
import org.apache.zeppelin.notebook.repo.NotebookRepo;
import org.apache.zeppelin.notebook.repo.NotebookRepoSettingsInfo;
import org.apache.zeppelin.notebook.repo.NotebookRepoWithVersionControl;
import org.apache.zeppelin.scheduler.Job;
import org.apache.zeppelin.scheduler.Job.Status;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.apache.zeppelin.user.Credentials;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.RepositoryException;


public class NotebookTest extends AbstractInterpreterTest implements ParagraphJobListener {
    private static final Logger logger = LoggerFactory.getLogger(NotebookTest.class);

    private Notebook notebook;

    private NotebookRepo notebookRepo;

    private NotebookAuthorization notebookAuthorization;

    private Credentials credentials;

    private AuthenticationInfo anonymous = AuthenticationInfo.ANONYMOUS;

    private NotebookTest.StatusChangedListener afterStatusChangedListener;

    @Test
    public void testRevisionSupported() throws IOException, SchedulerException {
        NotebookRepo notebookRepo;
        Notebook notebook;
        notebookRepo = new NotebookTest.DummyNotebookRepo();
        notebook = new Notebook(conf, notebookRepo, interpreterFactory, interpreterSettingManager, null, notebookAuthorization, credentials, null);
        Assert.assertFalse("Revision is not supported in DummyNotebookRepo", notebook.isRevisionSupported());
        notebookRepo = new NotebookTest.DummyNotebookRepoWithVersionControl();
        notebook = new Notebook(conf, notebookRepo, interpreterFactory, interpreterSettingManager, null, notebookAuthorization, credentials, null);
        Assert.assertTrue("Revision is supported in DummyNotebookRepoWithVersionControl", notebook.isRevisionSupported());
    }

    public static class DummyNotebookRepo implements NotebookRepo {
        @Override
        public void init(ZeppelinConfiguration zConf) throws IOException {
        }

        @Override
        public Map<String, NoteInfo> list(AuthenticationInfo subject) throws IOException {
            return new HashMap<>();
        }

        @Override
        public Note get(String noteId, String notePath, AuthenticationInfo subject) throws IOException {
            return null;
        }

        @Override
        public void move(String noteId, String notePath, String newNotePath, AuthenticationInfo subject) {
        }

        @Override
        public void move(String folderPath, String newFolderPath, AuthenticationInfo subject) {
        }

        @Override
        public void save(Note note, AuthenticationInfo subject) throws IOException {
        }

        @Override
        public void remove(String noteId, String notePath, AuthenticationInfo subject) throws IOException {
        }

        @Override
        public void remove(String folderPath, AuthenticationInfo subject) {
        }

        @Override
        public void close() {
        }

        @Override
        public List<NotebookRepoSettingsInfo> getSettings(AuthenticationInfo subject) {
            return null;
        }

        @Override
        public void updateSettings(Map<String, String> settings, AuthenticationInfo subject) {
        }
    }

    public static class DummyNotebookRepoWithVersionControl implements NotebookRepoWithVersionControl {
        @Override
        public Revision checkpoint(String noteId, String noteName, String checkpointMsg, AuthenticationInfo subject) throws IOException {
            return null;
        }

        @Override
        public Note get(String noteId, String noteName, String revId, AuthenticationInfo subject) throws IOException {
            return null;
        }

        @Override
        public List<Revision> revisionHistory(String noteId, String noteName, AuthenticationInfo subject) {
            return null;
        }

        @Override
        public Note setNoteRevision(String noteId, String noteName, String revId, AuthenticationInfo subject) throws IOException {
            return null;
        }

        @Override
        public void init(ZeppelinConfiguration zConf) throws IOException {
        }

        @Override
        public Map<String, NoteInfo> list(AuthenticationInfo subject) throws IOException {
            return new HashMap<>();
        }

        @Override
        public Note get(String noteId, String notePath, AuthenticationInfo subject) throws IOException {
            return null;
        }

        @Override
        public void save(Note note, AuthenticationInfo subject) throws IOException {
        }

        @Override
        public void move(String noteId, String notePath, String newNotePath, AuthenticationInfo subject) {
        }

        @Override
        public void move(String folderPath, String newFolderPath, AuthenticationInfo subject) {
        }

        @Override
        public void remove(String noteId, String notePath, AuthenticationInfo subject) throws IOException {
        }

        @Override
        public void remove(String folderPath, AuthenticationInfo subject) {
        }

        @Override
        public void close() {
        }

        @Override
        public List<NotebookRepoSettingsInfo> getSettings(AuthenticationInfo subject) {
            return null;
        }

        @Override
        public void updateSettings(Map<String, String> settings, AuthenticationInfo subject) {
        }
    }

    @Test
    public void testSelectingReplImplementation() throws IOException {
        Note note = notebook.createNote("note1", anonymous);
        // run with default repl
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        Map config = p1.getConfig();
        config.put("enabled", true);
        p1.setConfig(config);
        p1.setText("%mock1 hello world");
        p1.setAuthenticationInfo(anonymous);
        note.run(p1.getId());
        while (((p1.isTerminated()) == false) || ((p1.getReturn()) == null))
            Thread.yield();

        Assert.assertEquals("repl1: hello world", p1.getReturn().message().get(0).getData());
        // run with specific repl
        Paragraph p2 = note.addNewParagraph(ANONYMOUS);
        p2.setConfig(config);
        p2.setText("%mock2 hello world");
        p2.setAuthenticationInfo(anonymous);
        note.run(p2.getId());
        while (((p2.isTerminated()) == false) || ((p2.getReturn()) == null))
            Thread.yield();

        Assert.assertEquals("repl2: hello world", p2.getReturn().message().get(0).getData());
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testReloadAndSetInterpreter() throws IOException {
        Note note = notebook.createNote("note1", ANONYMOUS);
        Paragraph p1 = note.insertNewParagraph(0, ANONYMOUS);
        p1.setText("%md hello world");
        // when load
        notebook.reloadAllNotes(anonymous);
        Assert.assertEquals(1, notebook.getAllNotes().size());
        // then interpreter factory should be injected into all the paragraphs
        note = notebook.getAllNotes().get(0);
        try {
            note.getParagraphs().get(0).getBindedInterpreter();
            Assert.fail("Should throw InterpreterNotFoundException");
        } catch (InterpreterNotFoundException e) {
        }
    }

    @Test
    public void testReloadAllNotes() throws IOException {
        Note note1 = notebook.createNote("note1", ANONYMOUS);
        Paragraph p1 = note1.insertNewParagraph(0, ANONYMOUS);
        p1.setText("%md hello world");
        Note note2 = notebook.cloneNote(note1.getId(), "copied note", ANONYMOUS);
        // load copied notebook on memory when reloadAllNotes() is called
        Note copiedNote = notebookRepo.get(note2.getId(), note2.getPath(), anonymous);
        notebook.reloadAllNotes(anonymous);
        List<Note> notes = notebook.getAllNotes();
        Assert.assertEquals(notes.size(), 2);
        Assert.assertEquals(notes.get(0).getId(), copiedNote.getId());
        Assert.assertEquals(notes.get(0).getName(), copiedNote.getName());
        // format has make some changes due to
        // Notebook.convertFromSingleResultToMultipleResultsFormat
        Assert.assertEquals(notes.get(0).getParagraphs().size(), copiedNote.getParagraphs().size());
        Assert.assertEquals(notes.get(0).getParagraphs().get(0).getText(), copiedNote.getParagraphs().get(0).getText());
        Assert.assertEquals(notes.get(0).getParagraphs().get(0).settings, copiedNote.getParagraphs().get(0).settings);
        Assert.assertEquals(notes.get(0).getParagraphs().get(0).getTitle(), copiedNote.getParagraphs().get(0).getTitle());
        // delete notebook from notebook list when reloadAllNotes() is called
        reset();
        notebook.reloadAllNotes(anonymous);
        notes = notebook.getAllNotes();
        Assert.assertEquals(notes.size(), 0);
    }

    @Test
    public void testLoadAllNotes() {
        Note note;
        try {
            Assert.assertEquals(0, notebook.getAllNotes().size());
            note = notebook.createNote("note1", anonymous);
            Paragraph p1 = note.addNewParagraph(ANONYMOUS);
            Map config = p1.getConfig();
            config.put("enabled", true);
            p1.setConfig(config);
            p1.setText("hello world");
            notebook.saveNote(note, anonymous);
        } catch (IOException fe) {
            NotebookTest.logger.warn("Failed to create note and paragraph. Possible problem with persisting note, safe to ignore", fe);
        }
        Assert.assertEquals(1, notebook.getAllNotes().size());
    }

    @Test
    public void testPersist() throws IOException, SchedulerException {
        Note note = notebook.createNote("note1", anonymous);
        // run with default repl
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        Map config = p1.getConfig();
        config.put("enabled", true);
        p1.setConfig(config);
        p1.setText("hello world");
        notebook.saveNote(note, anonymous);
        // Notebook notebook2 = new Notebook(
        // conf, notebookRepo,
        // new InterpreterFactory(interpreterSettingManager),
        // interpreterSettingManager, null, null, null);
        // assertEquals(1, notebook2.getAllNotes().size());
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testCreateNoteWithSubject() throws IOException, SchedulerException, RepositoryException {
        AuthenticationInfo subject = new AuthenticationInfo("user1");
        Note note = notebook.createNote("note1", subject);
        Assert.assertNotNull(notebook.getNotebookAuthorization().getOwners(note.getId()));
        Assert.assertEquals(1, notebook.getNotebookAuthorization().getOwners(note.getId()).size());
        Set<String> owners = new HashSet<>();
        owners.add("user1");
        Assert.assertEquals(owners, notebook.getNotebookAuthorization().getOwners(note.getId()));
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testClearParagraphOutput() throws IOException, SchedulerException {
        Note note = notebook.createNote("note1", anonymous);
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        Map config = p1.getConfig();
        config.put("enabled", true);
        p1.setConfig(config);
        p1.setText("%mock1 hello world");
        p1.setAuthenticationInfo(anonymous);
        note.run(p1.getId());
        while (((p1.isTerminated()) == false) || ((p1.getReturn()) == null))
            Thread.yield();

        Assert.assertEquals("repl1: hello world", p1.getReturn().message().get(0).getData());
        // clear paragraph output/result
        note.clearParagraphOutput(p1.getId());
        Assert.assertNull(p1.getReturn());
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testRunBlankParagraph() throws IOException, InterruptedException, SchedulerException {
        Note note = notebook.createNote("note1", anonymous);
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        p1.setText("");
        p1.setAuthenticationInfo(anonymous);
        note.run(p1.getId());
        Thread.sleep((2 * 1000));
        Assert.assertEquals(p1.getStatus(), FINISHED);
        Assert.assertNull(p1.getDateStarted());
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testRunAll() throws IOException {
        Note note = notebook.createNote("note1", anonymous);
        // p1
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        Map config1 = p1.getConfig();
        config1.put("enabled", true);
        p1.setConfig(config1);
        p1.setText("%mock1 p1");
        // p2
        Paragraph p2 = note.addNewParagraph(ANONYMOUS);
        Map config2 = p2.getConfig();
        config2.put("enabled", false);
        p2.setConfig(config2);
        p2.setText("%mock1 p2");
        // p3
        Paragraph p3 = note.addNewParagraph(ANONYMOUS);
        p3.setText("%mock1 p3");
        // when
        note.runAll(anonymous, true);
        Assert.assertEquals("repl1: p1", p1.getReturn().message().get(0).getData());
        Assert.assertNull(p2.getReturn());
        Assert.assertEquals("repl1: p3", p3.getReturn().message().get(0).getData());
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testSchedule() throws IOException, InterruptedException {
        // create a note and a paragraph
        Note note = notebook.createNote("note1", anonymous);
        Paragraph p = note.addNewParagraph(ANONYMOUS);
        Map config = new HashMap<>();
        p.setConfig(config);
        p.setText("p1");
        Date dateFinished = p.getDateFinished();
        Assert.assertNull(dateFinished);
        // set cron scheduler, once a second
        config = note.getConfig();
        config.put("enabled", true);
        config.put("cron", "* * * * * ?");
        note.setConfig(config);
        notebook.refreshCron(note.getId());
        Thread.sleep((2 * 1000));
        // remove cron scheduler.
        config.put("cron", null);
        note.setConfig(config);
        notebook.refreshCron(note.getId());
        Thread.sleep((2 * 1000));
        dateFinished = p.getDateFinished();
        Assert.assertNotNull(dateFinished);
        Thread.sleep((2 * 1000));
        Assert.assertEquals(dateFinished, p.getDateFinished());
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testScheduleAgainstRunningAndPendingParagraph() throws IOException, InterruptedException {
        // create a note
        Note note = notebook.createNote("note1", anonymous);
        // append running and pending paragraphs to the note
        for (Status status : new Status[]{ Status.RUNNING, Status.PENDING }) {
            Paragraph p = note.addNewParagraph(ANONYMOUS);
            Map config = new HashMap<>();
            p.setConfig(config);
            p.setText("p");
            p.setStatus(status);
            Assert.assertNull(p.getDateFinished());
        }
        // set cron scheduler, once a second
        Map config = note.getConfig();
        config.put("enabled", true);
        config.put("cron", "* * * * * ?");
        note.setConfig(config);
        notebook.refreshCron(note.getId());
        Thread.sleep((2 * 1000));
        // remove cron scheduler.
        config.put("cron", null);
        note.setConfig(config);
        notebook.refreshCron(note.getId());
        Thread.sleep((2 * 1000));
        // check if the executions of the running and pending paragraphs were skipped
        for (Paragraph p : note.getParagraphs()) {
            Assert.assertNull(p.getDateFinished());
        }
        // remove the note
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testSchedulePoolUsage() throws IOException, InterruptedException {
        final int timeout = 30;
        final String everySecondCron = "* * * * * ?";
        final CountDownLatch jobsToExecuteCount = new CountDownLatch(13);
        final Note note = notebook.createNote("note1", anonymous);
        executeNewParagraphByCron(note, everySecondCron);
        afterStatusChangedListener = new NotebookTest.StatusChangedListener() {
            @Override
            public void onStatusChanged(Job job, Status before, Status after) {
                if (after == (Status.FINISHED)) {
                    jobsToExecuteCount.countDown();
                }
            }
        };
        Assert.assertTrue(jobsToExecuteCount.await(timeout, TimeUnit.SECONDS));
        terminateScheduledNote(note);
        afterStatusChangedListener = null;
    }

    @Test
    public void testScheduleDisabled() throws IOException, InterruptedException {
        System.setProperty(ZEPPELIN_NOTEBOOK_CRON_ENABLE.getVarName(), "false");
        try {
            final int timeout = 10;
            final String everySecondCron = "* * * * * ?";
            final CountDownLatch jobsToExecuteCount = new CountDownLatch(5);
            final Note note = notebook.createNote("note1", anonymous);
            executeNewParagraphByCron(note, everySecondCron);
            afterStatusChangedListener = new NotebookTest.StatusChangedListener() {
                @Override
                public void onStatusChanged(Job job, Status before, Status after) {
                    if (after == (Status.FINISHED)) {
                        jobsToExecuteCount.countDown();
                    }
                }
            };
            // This job should not run because "ZEPPELIN_NOTEBOOK_CRON_ENABLE" is set to false
            Assert.assertFalse(jobsToExecuteCount.await(timeout, TimeUnit.SECONDS));
            terminateScheduledNote(note);
            afterStatusChangedListener = null;
        } finally {
            System.setProperty(ZEPPELIN_NOTEBOOK_CRON_ENABLE.getVarName(), "true");
        }
    }

    @Test
    public void testScheduleDisabledWithName() throws IOException, InterruptedException {
        System.setProperty(ZEPPELIN_NOTEBOOK_CRON_FOLDERS.getVarName(), "System/*");
        try {
            final int timeout = 10;
            final String everySecondCron = "* * * * * ?";
            final CountDownLatch jobsToExecuteCount = new CountDownLatch(5);
            final Note note = notebook.createNote("note1", anonymous);
            executeNewParagraphByCron(note, everySecondCron);
            afterStatusChangedListener = new NotebookTest.StatusChangedListener() {
                @Override
                public void onStatusChanged(Job job, Status before, Status after) {
                    if (after == (Status.FINISHED)) {
                        jobsToExecuteCount.countDown();
                    }
                }
            };
            // This job should not run because it's path does not matches "ZEPPELIN_NOTEBOOK_CRON_FOLDERS"
            Assert.assertFalse(jobsToExecuteCount.await(timeout, TimeUnit.SECONDS));
            terminateScheduledNote(note);
            afterStatusChangedListener = null;
            final Note noteNameSystem = notebook.createNote("note1", anonymous);
            noteNameSystem.setName("System/test1");
            final CountDownLatch jobsToExecuteCountNameSystem = new CountDownLatch(5);
            executeNewParagraphByCron(noteNameSystem, everySecondCron);
            afterStatusChangedListener = new NotebookTest.StatusChangedListener() {
                @Override
                public void onStatusChanged(Job job, Status before, Status after) {
                    if (after == (Status.FINISHED)) {
                        jobsToExecuteCountNameSystem.countDown();
                    }
                }
            };
            // This job should run because it's path contains "System/"
            Assert.assertTrue(jobsToExecuteCountNameSystem.await(timeout, TimeUnit.SECONDS));
            terminateScheduledNote(noteNameSystem);
            afterStatusChangedListener = null;
        } finally {
            System.clearProperty(ZEPPELIN_NOTEBOOK_CRON_FOLDERS.getVarName());
        }
    }

    @Test
    public void testCronNoteInTrash() throws IOException, InterruptedException, SchedulerException {
        Note note = notebook.createNote("~Trash/NotCron", anonymous);
        Map<String, Object> config = note.getConfig();
        config.put("enabled", true);
        config.put("cron", "* * * * * ?");
        note.setConfig(config);
        final int jobsBeforeRefresh = notebook.quartzSched.getJobKeys(GroupMatcher.anyGroup()).size();
        notebook.refreshCron(note.getId());
        final int jobsAfterRefresh = notebook.quartzSched.getJobKeys(GroupMatcher.anyGroup()).size();
        Assert.assertEquals(jobsBeforeRefresh, jobsAfterRefresh);
        // remove cron scheduler.
        config.remove("cron");
        notebook.refreshCron(note.getId());
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testExportAndImportNote() throws IOException, CloneNotSupportedException, InterruptedException, InterpreterException, SchedulerException, RepositoryException {
        Note note = notebook.createNote("note1", anonymous);
        final Paragraph p = note.addNewParagraph(ANONYMOUS);
        String simpleText = "hello world";
        p.setText(simpleText);
        note.runAll(anonymous, true);
        String exportedNoteJson = notebook.exportNote(note.getId());
        Note importedNote = notebook.importNote(exportedNoteJson, "Title", anonymous);
        Paragraph p2 = importedNote.getParagraphs().get(0);
        // Test
        Assert.assertEquals(p.getId(), p2.getId());
        Assert.assertEquals(p.getText(), p2.getText());
        Assert.assertEquals(p.getReturn().message().get(0).getData(), p2.getReturn().message().get(0).getData());
        // Verify import note with subject
        AuthenticationInfo subject = new AuthenticationInfo("user1");
        Note importedNote2 = notebook.importNote(exportedNoteJson, "Title2", subject);
        Assert.assertNotNull(notebook.getNotebookAuthorization().getOwners(importedNote2.getId()));
        Assert.assertEquals(1, notebook.getNotebookAuthorization().getOwners(importedNote2.getId()).size());
        Set<String> owners = new HashSet<>();
        owners.add("user1");
        Assert.assertEquals(owners, notebook.getNotebookAuthorization().getOwners(importedNote2.getId()));
        notebook.removeNote(note.getId(), anonymous);
        notebook.removeNote(importedNote.getId(), anonymous);
        notebook.removeNote(importedNote2.getId(), anonymous);
    }

    @Test
    public void testCloneNote() throws IOException {
        Note note = notebook.createNote("note1", anonymous);
        final Paragraph p = note.addNewParagraph(ANONYMOUS);
        p.setText("hello world");
        note.runAll(anonymous, true);
        p.setStatus(RUNNING);
        Note cloneNote = notebook.cloneNote(note.getId(), "clone note", anonymous);
        Paragraph cp = cloneNote.getParagraph(0);
        Assert.assertEquals(cp.getStatus(), READY);
        // Keep same ParagraphId
        Assert.assertEquals(cp.getId(), p.getId());
        Assert.assertEquals(cp.getText(), p.getText());
        Assert.assertEquals(cp.getReturn().message().get(0).getData(), p.getReturn().message().get(0).getData());
        // Verify clone note with subject
        AuthenticationInfo subject = new AuthenticationInfo("user1");
        Note cloneNote2 = notebook.cloneNote(note.getId(), "clone note2", subject);
        Assert.assertNotNull(notebook.getNotebookAuthorization().getOwners(cloneNote2.getId()));
        Assert.assertEquals(1, notebook.getNotebookAuthorization().getOwners(cloneNote2.getId()).size());
        Set<String> owners = new HashSet<>();
        owners.add("user1");
        Assert.assertEquals(owners, notebook.getNotebookAuthorization().getOwners(cloneNote2.getId()));
        notebook.removeNote(note.getId(), anonymous);
        notebook.removeNote(cloneNote.getId(), anonymous);
        notebook.removeNote(cloneNote2.getId(), anonymous);
    }

    @Test
    public void testResourceRemovealOnParagraphNoteRemove() throws IOException {
        Note note = notebook.createNote("note1", anonymous);
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        p1.setText("%mock1 hello");
        Paragraph p2 = note.addNewParagraph(ANONYMOUS);
        p2.setText("%mock2 world");
        for (InterpreterGroup intpGroup : interpreterSettingManager.getAllInterpreterGroup()) {
            intpGroup.setResourcePool(new org.apache.zeppelin.resource.LocalResourcePool(intpGroup.getId()));
        }
        note.runAll(anonymous, true);
        Assert.assertEquals(2, interpreterSettingManager.getAllResources().size());
        // remove a paragraph
        note.removeParagraph(anonymous.getUser(), p1.getId());
        Assert.assertEquals(1, interpreterSettingManager.getAllResources().size());
        // remove note
        notebook.removeNote(note.getId(), anonymous);
        Assert.assertEquals(0, interpreterSettingManager.getAllResources().size());
    }

    @Test
    public void testAngularObjectRemovalOnNotebookRemove() throws IOException, InterruptedException {
        // create a note and a paragraph
        Note note = notebook.createNote("note1", anonymous);
        AngularObjectRegistry registry = interpreterSettingManager.getInterpreterSettings(note.getId()).get(0).getOrCreateInterpreterGroup(anonymous.getUser(), "sharedProcess").getAngularObjectRegistry();
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        // add paragraph scope object
        registry.add("o1", "object1", note.getId(), p1.getId());
        // add notebook scope object
        registry.add("o2", "object2", note.getId(), null);
        // add global scope object
        registry.add("o3", "object3", null, null);
        // remove notebook
        notebook.removeNote(note.getId(), anonymous);
        // notebook scope or paragraph scope object should be removed
        Assert.assertNull(registry.get("o1", note.getId(), null));
        Assert.assertNull(registry.get("o2", note.getId(), p1.getId()));
        // global object sould be remained
        Assert.assertNotNull(registry.get("o3", null, null));
    }

    @Test
    public void testAngularObjectRemovalOnParagraphRemove() throws IOException, InterruptedException {
        // create a note and a paragraph
        Note note = notebook.createNote("note1", anonymous);
        AngularObjectRegistry registry = interpreterSettingManager.getInterpreterSettings(note.getId()).get(0).getOrCreateInterpreterGroup(anonymous.getUser(), "sharedProcess").getAngularObjectRegistry();
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        // add paragraph scope object
        registry.add("o1", "object1", note.getId(), p1.getId());
        // add notebook scope object
        registry.add("o2", "object2", note.getId(), null);
        // add global scope object
        registry.add("o3", "object3", null, null);
        // remove notebook
        note.removeParagraph(anonymous.getUser(), p1.getId());
        // paragraph scope should be removed
        Assert.assertNull(registry.get("o1", note.getId(), null));
        // notebook scope and global object sould be remained
        Assert.assertNotNull(registry.get("o2", note.getId(), null));
        Assert.assertNotNull(registry.get("o3", null, null));
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testAngularObjectRemovalOnInterpreterRestart() throws IOException, InterruptedException, InterpreterException {
        // create a note and a paragraph
        Note note = notebook.createNote("note1", anonymous);
        AngularObjectRegistry registry = interpreterSettingManager.getInterpreterSettings(note.getId()).get(0).getOrCreateInterpreterGroup(anonymous.getUser(), "sharedProcess").getAngularObjectRegistry();
        // add local scope object
        registry.add("o1", "object1", note.getId(), null);
        // add global scope object
        registry.add("o2", "object2", null, null);
        // restart interpreter
        interpreterSettingManager.restart(interpreterSettingManager.getInterpreterSettings(note.getId()).get(0).getId());
        registry = interpreterSettingManager.getInterpreterSettings(note.getId()).get(0).getOrCreateInterpreterGroup(anonymous.getUser(), "sharedProcess").getAngularObjectRegistry();
        // New InterpreterGroup will be created and its AngularObjectRegistry will be created
        Assert.assertNull(registry.get("o1", note.getId(), null));
        Assert.assertNull(registry.get("o2", null, null));
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testPermissions() throws IOException {
        // create a note and a paragraph
        Note note = notebook.createNote("note1", anonymous);
        NotebookAuthorization notebookAuthorization = notebook.getNotebookAuthorization();
        // empty owners, readers or writers means note is public
        Assert.assertEquals(notebookAuthorization.isOwner(note.getId(), new HashSet(Arrays.asList("user2"))), true);
        Assert.assertEquals(notebookAuthorization.isReader(note.getId(), new HashSet(Arrays.asList("user2"))), true);
        Assert.assertEquals(notebookAuthorization.isRunner(note.getId(), new HashSet(Arrays.asList("user2"))), true);
        Assert.assertEquals(notebookAuthorization.isWriter(note.getId(), new HashSet(Arrays.asList("user2"))), true);
        notebookAuthorization.setOwners(note.getId(), new HashSet(Arrays.asList("user1")));
        notebookAuthorization.setReaders(note.getId(), new HashSet(Arrays.asList("user1", "user2")));
        notebookAuthorization.setRunners(note.getId(), new HashSet(Arrays.asList("user3")));
        notebookAuthorization.setWriters(note.getId(), new HashSet(Arrays.asList("user1")));
        Assert.assertEquals(notebookAuthorization.isOwner(note.getId(), new HashSet(Arrays.asList("user2"))), false);
        Assert.assertEquals(notebookAuthorization.isOwner(note.getId(), new HashSet(Arrays.asList("user1"))), true);
        Assert.assertEquals(notebookAuthorization.isReader(note.getId(), new HashSet(Arrays.asList("user4"))), false);
        Assert.assertEquals(notebookAuthorization.isReader(note.getId(), new HashSet(Arrays.asList("user2"))), true);
        Assert.assertEquals(notebookAuthorization.isRunner(note.getId(), new HashSet(Arrays.asList("user3"))), true);
        Assert.assertEquals(notebookAuthorization.isRunner(note.getId(), new HashSet(Arrays.asList("user2"))), false);
        Assert.assertEquals(notebookAuthorization.isWriter(note.getId(), new HashSet(Arrays.asList("user2"))), false);
        Assert.assertEquals(notebookAuthorization.isWriter(note.getId(), new HashSet(Arrays.asList("user1"))), true);
        // Test clearing of permissions
        notebookAuthorization.setReaders(note.getId(), Sets.<String>newHashSet());
        Assert.assertEquals(notebookAuthorization.isReader(note.getId(), new HashSet(Arrays.asList("user2"))), true);
        Assert.assertEquals(notebookAuthorization.isReader(note.getId(), new HashSet(Arrays.asList("user4"))), true);
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testAuthorizationRoles() throws IOException {
        String user1 = "user1";
        String user2 = "user2";
        Set<String> roles = Sets.newHashSet("admin");
        // set admin roles for both user1 and user2
        notebookAuthorization.setRoles(user1, roles);
        notebookAuthorization.setRoles(user2, roles);
        Note note = notebook.createNote("note1", new AuthenticationInfo(user1));
        // check that user1 is owner, reader, runner and writer
        Assert.assertEquals(notebookAuthorization.isOwner(note.getId(), Sets.newHashSet(user1)), true);
        Assert.assertEquals(notebookAuthorization.isReader(note.getId(), Sets.newHashSet(user1)), true);
        Assert.assertEquals(notebookAuthorization.isRunner(note.getId(), Sets.newHashSet(user2)), true);
        Assert.assertEquals(notebookAuthorization.isWriter(note.getId(), Sets.newHashSet(user1)), true);
        // since user1 and user2 both have admin role, user2 will be reader and writer as well
        Assert.assertEquals(notebookAuthorization.isOwner(note.getId(), Sets.newHashSet(user2)), false);
        Assert.assertEquals(notebookAuthorization.isReader(note.getId(), Sets.newHashSet(user2)), true);
        Assert.assertEquals(notebookAuthorization.isRunner(note.getId(), Sets.newHashSet(user2)), true);
        Assert.assertEquals(notebookAuthorization.isWriter(note.getId(), Sets.newHashSet(user2)), true);
        // check that user1 has note listed in his workbench
        Set<String> user1AndRoles = notebookAuthorization.getRoles(user1);
        user1AndRoles.add(user1);
        List<Note> user1Notes = notebook.getAllNotes(user1AndRoles);
        Assert.assertEquals(user1Notes.size(), 1);
        Assert.assertEquals(user1Notes.get(0).getId(), note.getId());
        // check that user2 has note listed in his workbench because of admin role
        Set<String> user2AndRoles = notebookAuthorization.getRoles(user2);
        user2AndRoles.add(user2);
        List<Note> user2Notes = notebook.getAllNotes(user2AndRoles);
        Assert.assertEquals(user2Notes.size(), 1);
        Assert.assertEquals(user2Notes.get(0).getId(), note.getId());
    }

    @Test
    public void testInterpreterSettingConfig() {
        AbstractInterpreterTest.LOGGER.info("testInterpreterSettingConfig >>> ");
        Note note = new Note("testInterpreterSettingConfig", "config_test", interpreterFactory, interpreterSettingManager, this, credentials, new ArrayList());
        // create paragraphs
        Paragraph p1 = note.addNewParagraph(anonymous);
        Map<String, Object> config = p1.getConfig();
        Assert.assertTrue(config.containsKey("runOnSelectionChange"));
        Assert.assertTrue(config.containsKey("title"));
        Assert.assertEquals(config.get("runOnSelectionChange"), false);
        Assert.assertEquals(config.get("title"), true);
        // The config_test interpreter sets the default parameters
        // in interpreter/config_test/interpreter-setting.json
        // "config": {
        // "runOnSelectionChange": false,
        // "title": true
        // },
        p1.setText("%config_test sleep 1000");
        note.runAll(ANONYMOUS, false);
        // wait until first paragraph finishes and second paragraph starts
        while ((p1.getStatus()) != (Status.FINISHED))
            Thread.yield();

        // Check if the config_test interpreter default parameter takes effect
        AbstractInterpreterTest.LOGGER.info(("p1.getConfig() =  " + (p1.getConfig())));
        Assert.assertEquals(config.get("runOnSelectionChange"), false);
        Assert.assertEquals(config.get("title"), true);
        // The mock1 interpreter does not set default parameters
        p1.setText("%mock1 sleep 1000");
        note.runAll(ANONYMOUS, false);
        // wait until first paragraph finishes and second paragraph starts
        while ((p1.getStatus()) != (Status.FINISHED))
            Thread.yield();

        // Check if the mock1 interpreter parameter is updated
        AbstractInterpreterTest.LOGGER.info(("changed intp p1.getConfig() =  " + (p1.getConfig())));
        Assert.assertEquals(config.get("runOnSelectionChange"), true);
        Assert.assertEquals(config.get("title"), false);
    }

    @Test
    public void testAbortParagraphStatusOnInterpreterRestart() throws IOException, InterruptedException, InterpreterException {
        Note note = notebook.createNote("note1", anonymous);
        // create three paragraphs
        Paragraph p1 = note.addNewParagraph(anonymous);
        p1.setText("%mock1 sleep 1000");
        Paragraph p2 = note.addNewParagraph(anonymous);
        p2.setText("%mock1 sleep 1000");
        Paragraph p3 = note.addNewParagraph(anonymous);
        p3.setText("%mock1 sleep 1000");
        note.runAll(ANONYMOUS, false);
        // wait until first paragraph finishes and second paragraph starts
        while (((p1.getStatus()) != (Status.FINISHED)) || ((p2.getStatus()) != (Status.RUNNING)))
            Thread.yield();

        Assert.assertEquals(FINISHED, p1.getStatus());
        Assert.assertEquals(RUNNING, p2.getStatus());
        Assert.assertEquals(PENDING, p3.getStatus());
        // restart interpreter
        interpreterSettingManager.restart(interpreterSettingManager.getInterpreterSettingByName("mock1").getId());
        // make sure three different status aborted well.
        Assert.assertEquals(FINISHED, p1.getStatus());
        Assert.assertEquals(ABORT, p2.getStatus());
        Assert.assertEquals(ABORT, p3.getStatus());
        notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void testPerSessionInterpreterCloseOnNoteRemoval() throws IOException, InterpreterException {
        // create a notes
        Note note1 = notebook.createNote("note1", anonymous);
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        p1.setText("%mock1 getId");
        p1.setAuthenticationInfo(anonymous);
        // restart interpreter with per user session enabled
        for (InterpreterSetting setting : interpreterSettingManager.getInterpreterSettings(note1.getId())) {
            setting.getOption().setPerNote(setting.getOption().SCOPED);
            notebook.getInterpreterSettingManager().restart(setting.getId());
        }
        note1.run(p1.getId());
        while ((p1.getStatus()) != (Status.FINISHED))
            Thread.yield();

        InterpreterResult result = p1.getReturn();
        // remove note and recreate
        notebook.removeNote(note1.getId(), anonymous);
        note1 = notebook.createNote("note1", anonymous);
        p1 = note1.addNewParagraph(ANONYMOUS);
        p1.setText("%mock1 getId");
        p1.setAuthenticationInfo(anonymous);
        note1.run(p1.getId());
        while ((p1.getStatus()) != (Status.FINISHED))
            Thread.yield();

        Assert.assertNotEquals(p1.getReturn().message(), result.message());
        notebook.removeNote(note1.getId(), anonymous);
    }

    @Test
    public void testPerSessionInterpreter() throws IOException, InterpreterException {
        // create two notes
        Note note1 = notebook.createNote("note1", anonymous);
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        Note note2 = notebook.createNote("note2", anonymous);
        Paragraph p2 = note2.addNewParagraph(ANONYMOUS);
        p1.setText("%mock1 getId");
        p1.setAuthenticationInfo(anonymous);
        p2.setText("%mock1 getId");
        p2.setAuthenticationInfo(anonymous);
        // run per note session disabled
        note1.run(p1.getId());
        note2.run(p2.getId());
        while ((p1.getStatus()) != (Status.FINISHED))
            Thread.yield();

        while ((p2.getStatus()) != (Status.FINISHED))
            Thread.yield();

        Assert.assertEquals(p1.getReturn().message().get(0).getData(), p2.getReturn().message().get(0).getData());
        // restart interpreter with per note session enabled
        for (InterpreterSetting setting : notebook.getInterpreterSettingManager().getInterpreterSettings(note1.getId())) {
            setting.getOption().setPerNote(SCOPED);
            notebook.getInterpreterSettingManager().restart(setting.getId());
        }
        // run per note session enabled
        note1.run(p1.getId());
        note2.run(p2.getId());
        while ((p1.getStatus()) != (Status.FINISHED))
            Thread.yield();

        while ((p2.getStatus()) != (Status.FINISHED))
            Thread.yield();

        Assert.assertNotEquals(p1.getReturn().message(), p2.getReturn().message().get(0).getData());
        notebook.removeNote(note1.getId(), anonymous);
        notebook.removeNote(note2.getId(), anonymous);
    }

    @Test
    public void testPerNoteSessionInterpreter() throws IOException, InterpreterException {
        // create two notes
        Note note1 = notebook.createNote("note1", anonymous);
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        Note note2 = notebook.createNote("note2", anonymous);
        Paragraph p2 = note2.addNewParagraph(ANONYMOUS);
        p1.setText("%mock1 getId");
        p1.setAuthenticationInfo(anonymous);
        p2.setText("%mock1 getId");
        p2.setAuthenticationInfo(anonymous);
        // shared mode.
        note1.run(p1.getId());
        note2.run(p2.getId());
        while ((p1.getStatus()) != (Status.FINISHED))
            Thread.yield();

        while ((p2.getStatus()) != (Status.FINISHED))
            Thread.yield();

        Assert.assertEquals(p1.getReturn().message().get(0).getData(), p2.getReturn().message().get(0).getData());
        // restart interpreter with scoped mode enabled
        for (InterpreterSetting setting : notebook.getInterpreterSettingManager().getInterpreterSettings(note1.getId())) {
            setting.getOption().setPerNote(SCOPED);
            notebook.getInterpreterSettingManager().restart(setting.getId());
        }
        // run per note session enabled
        note1.run(p1.getId());
        note2.run(p2.getId());
        while ((p1.getStatus()) != (Status.FINISHED))
            Thread.yield();

        while ((p2.getStatus()) != (Status.FINISHED))
            Thread.yield();

        Assert.assertNotEquals(p1.getReturn().message().get(0).getData(), p2.getReturn().message().get(0).getData());
        // restart interpreter with isolated mode enabled
        for (InterpreterSetting setting : notebook.getInterpreterSettingManager().getInterpreterSettings(note1.getId())) {
            setting.getOption().setPerNote(ISOLATED);
            setting.getInterpreterSettingManager().restart(setting.getId());
        }
        // run per note process enabled
        note1.run(p1.getId());
        note2.run(p2.getId());
        while ((p1.getStatus()) != (Status.FINISHED))
            Thread.yield();

        while ((p2.getStatus()) != (Status.FINISHED))
            Thread.yield();

        Assert.assertNotEquals(p1.getReturn().message().get(0).getData(), p2.getReturn().message().get(0).getData());
        notebook.removeNote(note1.getId(), anonymous);
        notebook.removeNote(note2.getId(), anonymous);
    }

    @Test
    public void testGetAllNotes() throws Exception {
        Note note1 = notebook.createNote("note1", anonymous);
        Note note2 = notebook.createNote("note2", anonymous);
        Assert.assertEquals(2, notebook.getAllNotes(Sets.newHashSet("anonymous")).size());
        notebook.getNotebookAuthorization().setOwners(note1.getId(), Sets.newHashSet("user1"));
        notebook.getNotebookAuthorization().setWriters(note1.getId(), Sets.newHashSet("user1"));
        notebook.getNotebookAuthorization().setRunners(note1.getId(), Sets.newHashSet("user1"));
        notebook.getNotebookAuthorization().setReaders(note1.getId(), Sets.newHashSet("user1"));
        Assert.assertEquals(1, notebook.getAllNotes(Sets.newHashSet("anonymous")).size());
        Assert.assertEquals(2, notebook.getAllNotes(Sets.newHashSet("user1")).size());
        notebook.getNotebookAuthorization().setOwners(note2.getId(), Sets.newHashSet("user2"));
        notebook.getNotebookAuthorization().setWriters(note2.getId(), Sets.newHashSet("user2"));
        notebook.getNotebookAuthorization().setReaders(note2.getId(), Sets.newHashSet("user2"));
        notebook.getNotebookAuthorization().setRunners(note2.getId(), Sets.newHashSet("user2"));
        Assert.assertEquals(0, notebook.getAllNotes(Sets.newHashSet("anonymous")).size());
        Assert.assertEquals(1, notebook.getAllNotes(Sets.newHashSet("user1")).size());
        Assert.assertEquals(1, notebook.getAllNotes(Sets.newHashSet("user2")).size());
        notebook.removeNote(note1.getId(), anonymous);
        notebook.removeNote(note2.getId(), anonymous);
    }

    @Test
    public void testGetAllNotesWithDifferentPermissions() throws IOException {
        HashSet<String> user1 = Sets.newHashSet("user1");
        HashSet<String> user2 = Sets.newHashSet("user1");
        List<Note> notes1 = notebook.getAllNotes(user1);
        List<Note> notes2 = notebook.getAllNotes(user2);
        Assert.assertEquals(notes1.size(), 0);
        Assert.assertEquals(notes2.size(), 0);
        // creates note and sets user1 owner
        Note note = notebook.createNote("note1", new AuthenticationInfo("user1"));
        // note is public since readers and writers empty
        notes1 = notebook.getAllNotes(user1);
        notes2 = notebook.getAllNotes(user2);
        Assert.assertEquals(notes1.size(), 1);
        Assert.assertEquals(notes2.size(), 1);
        notebook.getNotebookAuthorization().setReaders(note.getId(), Sets.newHashSet("user1"));
        // note is public since writers empty
        notes1 = notebook.getAllNotes(user1);
        notes2 = notebook.getAllNotes(user2);
        Assert.assertEquals(notes1.size(), 1);
        Assert.assertEquals(notes2.size(), 1);
        notebook.getNotebookAuthorization().setRunners(note.getId(), Sets.newHashSet("user1"));
        notes1 = notebook.getAllNotes(user1);
        notes2 = notebook.getAllNotes(user2);
        Assert.assertEquals(notes1.size(), 1);
        Assert.assertEquals(notes2.size(), 1);
        notebook.getNotebookAuthorization().setWriters(note.getId(), Sets.newHashSet("user1"));
        notes1 = notebook.getAllNotes(user1);
        notes2 = notebook.getAllNotes(user2);
        Assert.assertEquals(notes1.size(), 1);
        Assert.assertEquals(notes2.size(), 1);
    }

    @Test
    public void testPublicPrivateNewNote() throws IOException, SchedulerException {
        HashSet<String> user1 = Sets.newHashSet("user1");
        HashSet<String> user2 = Sets.newHashSet("user2");
        // case of public note
        Assert.assertTrue(conf.isNotebookPublic());
        Assert.assertTrue(notebookAuthorization.isPublic());
        List<Note> notes1 = notebook.getAllNotes(user1);
        List<Note> notes2 = notebook.getAllNotes(user2);
        Assert.assertEquals(notes1.size(), 0);
        Assert.assertEquals(notes2.size(), 0);
        // user1 creates note
        Note notePublic = notebook.createNote("note1", new AuthenticationInfo("user1"));
        // both users have note
        notes1 = notebook.getAllNotes(user1);
        notes2 = notebook.getAllNotes(user2);
        Assert.assertEquals(notes1.size(), 1);
        Assert.assertEquals(notes2.size(), 1);
        Assert.assertEquals(notes1.get(0).getId(), notePublic.getId());
        Assert.assertEquals(notes2.get(0).getId(), notePublic.getId());
        // user1 is only owner
        Assert.assertEquals(notebookAuthorization.getOwners(notePublic.getId()).size(), 1);
        Assert.assertEquals(notebookAuthorization.getReaders(notePublic.getId()).size(), 0);
        Assert.assertEquals(notebookAuthorization.getRunners(notePublic.getId()).size(), 0);
        Assert.assertEquals(notebookAuthorization.getWriters(notePublic.getId()).size(), 0);
        // case of private note
        System.setProperty(ZEPPELIN_NOTEBOOK_PUBLIC.getVarName(), "false");
        ZeppelinConfiguration conf2 = ZeppelinConfiguration.create();
        Assert.assertFalse(conf2.isNotebookPublic());
        // notebook authorization reads from conf, so no need to re-initilize
        Assert.assertFalse(notebookAuthorization.isPublic());
        // check that still 1 note per user
        notes1 = notebook.getAllNotes(user1);
        notes2 = notebook.getAllNotes(user2);
        Assert.assertEquals(notes1.size(), 1);
        Assert.assertEquals(notes2.size(), 1);
        // create private note
        Note notePrivate = notebook.createNote("note2", new AuthenticationInfo("user1"));
        // only user1 have notePrivate right after creation
        notes1 = notebook.getAllNotes(user1);
        notes2 = notebook.getAllNotes(user2);
        Assert.assertEquals(notes1.size(), 2);
        Assert.assertEquals(notes2.size(), 1);
        Assert.assertEquals(true, notes1.contains(notePrivate));
        // user1 have all rights
        Assert.assertEquals(notebookAuthorization.getOwners(notePrivate.getId()).size(), 1);
        Assert.assertEquals(notebookAuthorization.getReaders(notePrivate.getId()).size(), 1);
        Assert.assertEquals(notebookAuthorization.getRunners(notePrivate.getId()).size(), 1);
        Assert.assertEquals(notebookAuthorization.getWriters(notePrivate.getId()).size(), 1);
        // set back public to true
        System.setProperty(ZEPPELIN_NOTEBOOK_PUBLIC.getVarName(), "true");
        ZeppelinConfiguration.create();
    }

    @Test
    public void testCloneImportCheck() throws IOException {
        Note sourceNote = notebook.createNote("note1", new AuthenticationInfo("user"));
        sourceNote.setName("TestNote");
        Assert.assertEquals("TestNote", sourceNote.getName());
        Paragraph sourceParagraph = sourceNote.addNewParagraph(ANONYMOUS);
        Assert.assertEquals("anonymous", sourceParagraph.getUser());
        Note destNote = notebook.createNote("note2", new AuthenticationInfo("user"));
        destNote.setName("ClonedNote");
        Assert.assertEquals("ClonedNote", destNote.getName());
        List<Paragraph> paragraphs = sourceNote.getParagraphs();
        for (Paragraph p : paragraphs) {
            destNote.addCloneParagraph(p, ANONYMOUS);
            Assert.assertEquals("anonymous", p.getUser());
        }
    }

    private interface StatusChangedListener {
        void onStatusChanged(Job job, Status before, Status after);
    }
}

