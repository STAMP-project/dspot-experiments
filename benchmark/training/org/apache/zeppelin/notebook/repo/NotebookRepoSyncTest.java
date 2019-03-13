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
package org.apache.zeppelin.notebook.repo;


import AuthenticationInfo.ANONYMOUS;
import ConfVars.ZEPPELIN_NOTEBOOK_DIR;
import ConfVars.ZEPPELIN_NOTEBOOK_ONE_WAY_SYNC;
import ConfVars.ZEPPELIN_NOTEBOOK_STORAGE;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.interpreter.InterpreterFactory;
import org.apache.zeppelin.interpreter.InterpreterSettingManager;
import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.NoteInfo;
import org.apache.zeppelin.notebook.Notebook;
import org.apache.zeppelin.notebook.NotebookAuthorization;
import org.apache.zeppelin.notebook.Paragraph;
import org.apache.zeppelin.search.SearchService;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.apache.zeppelin.user.Credentials;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO(zjffdu) move it to zeppelin-zengine
public class NotebookRepoSyncTest {
    private File ZEPPELIN_HOME;

    private ZeppelinConfiguration conf;

    private File mainNotebookDir;

    private File secNotebookDir;

    private Notebook notebookSync;

    private NotebookRepoSync notebookRepoSync;

    private InterpreterFactory factory;

    private InterpreterSettingManager interpreterSettingManager;

    private SearchService search;

    private NotebookAuthorization notebookAuthorization;

    private Credentials credentials;

    private AuthenticationInfo anonymous;

    private static final Logger LOG = LoggerFactory.getLogger(NotebookRepoSyncTest.class);

    @Test
    public void testRepoCount() throws IOException {
        Assert.assertTrue(((notebookRepoSync.getMaxRepoNum()) >= (notebookRepoSync.getRepoCount())));
    }

    @Test
    public void testSyncOnCreate() throws IOException {
        /* check that both storage systems are empty */
        Assert.assertTrue(((notebookRepoSync.getRepoCount()) > 1));
        Assert.assertEquals(0, notebookRepoSync.list(0, anonymous).size());
        Assert.assertEquals(0, notebookRepoSync.list(1, anonymous).size());
        /* create note */
        Note note = notebookSync.createNote("test", "", anonymous);
        // check that automatically saved on both storages
        Assert.assertEquals(1, notebookRepoSync.list(0, anonymous).size());
        Assert.assertEquals(1, notebookRepoSync.list(1, anonymous).size());
        Assert.assertEquals(notebookRepoSync.list(0, anonymous).get(0).getId(), notebookRepoSync.list(1, anonymous).get(0).getId());
        notebookSync.removeNote(notebookRepoSync.list(0, null).get(0).getId(), anonymous);
    }

    @Test
    public void testSyncOnDelete() throws IOException {
        /* create note */
        Assert.assertTrue(((notebookRepoSync.getRepoCount()) > 1));
        Assert.assertEquals(0, notebookRepoSync.list(0, anonymous).size());
        Assert.assertEquals(0, notebookRepoSync.list(1, anonymous).size());
        Note note = notebookSync.createNote("test", "", anonymous);
        /* check that created in both storage systems */
        Assert.assertEquals(1, notebookRepoSync.list(0, anonymous).size());
        Assert.assertEquals(1, notebookRepoSync.list(1, anonymous).size());
        Assert.assertEquals(notebookRepoSync.list(0, anonymous).get(0).getId(), notebookRepoSync.list(1, anonymous).get(0).getId());
        /* remove Note */
        notebookSync.removeNote(notebookRepoSync.list(0, anonymous).get(0).getId(), anonymous);
        /* check that deleted in both storages */
        Assert.assertEquals(0, notebookRepoSync.list(0, anonymous).size());
        Assert.assertEquals(0, notebookRepoSync.list(1, anonymous).size());
    }

    @Test
    public void testSyncUpdateMain() throws IOException {
        /* create note */
        Note note = notebookSync.createNote("/test", "test", anonymous);
        note.setInterpreterFactory(Mockito.mock(InterpreterFactory.class));
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        Map config = p1.getConfig();
        config.put("enabled", true);
        p1.setConfig(config);
        p1.setText("hello world");
        /* new paragraph exists in note instance */
        Assert.assertEquals(1, note.getParagraphs().size());
        /* new paragraph not yet saved into storages */
        Assert.assertEquals(0, notebookRepoSync.get(0, notebookRepoSync.list(0, anonymous).get(0).getId(), notebookRepoSync.list(0, anonymous).get(0).getPath(), anonymous).getParagraphs().size());
        Assert.assertEquals(0, notebookRepoSync.get(1, notebookRepoSync.list(1, anonymous).get(0).getId(), notebookRepoSync.list(1, anonymous).get(0).getPath(), anonymous).getParagraphs().size());
        /* save to storage under index 0 (first storage) */
        notebookRepoSync.save(0, note, anonymous);
        /* check paragraph saved to first storage */
        Assert.assertEquals(1, notebookRepoSync.get(0, notebookRepoSync.list(0, anonymous).get(0).getId(), notebookRepoSync.list(0, anonymous).get(0).getPath(), anonymous).getParagraphs().size());
        /* check paragraph isn't saved to second storage */
        Assert.assertEquals(0, notebookRepoSync.get(1, notebookRepoSync.list(1, anonymous).get(0).getId(), notebookRepoSync.list(0, anonymous).get(0).getPath(), anonymous).getParagraphs().size());
        /* apply sync */
        notebookRepoSync.sync(null);
        /* check whether added to second storage */
        Assert.assertEquals(1, notebookRepoSync.get(1, notebookRepoSync.list(1, anonymous).get(0).getId(), notebookRepoSync.list(1, anonymous).get(0).getPath(), anonymous).getParagraphs().size());
        /* check whether same paragraph id */
        Assert.assertEquals(p1.getId(), notebookRepoSync.get(0, notebookRepoSync.list(0, anonymous).get(0).getId(), notebookRepoSync.list(0, anonymous).get(0).getPath(), anonymous).getLastParagraph().getId());
        Assert.assertEquals(p1.getId(), notebookRepoSync.get(1, notebookRepoSync.list(1, anonymous).get(0).getId(), notebookRepoSync.list(1, anonymous).get(0).getPath(), anonymous).getLastParagraph().getId());
        notebookRepoSync.remove(note.getId(), note.getPath(), anonymous);
    }

    @Test
    public void testSyncOnReloadedList() throws Exception {
        /* check that both storage repos are empty */
        Assert.assertTrue(((notebookRepoSync.getRepoCount()) > 1));
        Assert.assertEquals(0, notebookRepoSync.list(0, anonymous).size());
        Assert.assertEquals(0, notebookRepoSync.list(1, anonymous).size());
        File srcDir = new File("src/test/resources/notebook");
        File destDir = secNotebookDir;
        /* copy manually new notebook into secondary storage repo and check repos */
        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            NotebookRepoSyncTest.LOG.error(e.toString(), e);
        }
        Assert.assertEquals(0, notebookRepoSync.list(0, anonymous).size());
        Assert.assertEquals(2, notebookRepoSync.list(1, anonymous).size());
        // After reloading notebooks repos should be synchronized
        notebookSync.reloadAllNotes(anonymous);
        Assert.assertEquals(2, notebookRepoSync.list(0, anonymous).size());
        Assert.assertEquals(2, notebookRepoSync.list(1, anonymous).size());
    }

    @Test
    public void testOneWaySyncOnReloadedList() throws IOException, SchedulerException {
        System.setProperty(ZEPPELIN_NOTEBOOK_DIR.getVarName(), mainNotebookDir.getAbsolutePath());
        System.setProperty(ZEPPELIN_NOTEBOOK_ONE_WAY_SYNC.getVarName(), "true");
        conf = ZeppelinConfiguration.create();
        notebookRepoSync = new NotebookRepoSync(conf);
        notebookSync = new Notebook(conf, notebookRepoSync, factory, interpreterSettingManager, search, notebookAuthorization, credentials, null);
        // check that both storage repos are empty
        Assert.assertTrue(((notebookRepoSync.getRepoCount()) > 1));
        Assert.assertEquals(0, notebookRepoSync.list(0, null).size());
        Assert.assertEquals(0, notebookRepoSync.list(1, null).size());
        File srcDir = new File("src/test/resources/notebook");
        File destDir = secNotebookDir;
        /* copy manually new notebook into secondary storage repo and check repos */
        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            NotebookRepoSyncTest.LOG.error(e.toString(), e);
        }
        Assert.assertEquals(0, notebookRepoSync.list(0, null).size());
        Assert.assertEquals(2, notebookRepoSync.list(1, null).size());
        // after reloading the notebook should be wiped from secondary storage
        notebookSync.reloadAllNotes(null);
        Assert.assertEquals(0, notebookRepoSync.list(0, null).size());
        Assert.assertEquals(0, notebookRepoSync.list(1, null).size());
        destDir = mainNotebookDir;
        // copy manually new notebook into primary storage repo and check repos
        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            NotebookRepoSyncTest.LOG.error(e.toString(), e);
        }
        Assert.assertEquals(2, notebookRepoSync.list(0, null).size());
        Assert.assertEquals(0, notebookRepoSync.list(1, null).size());
        // after reloading notebooks repos should be synchronized
        notebookSync.reloadAllNotes(null);
        Assert.assertEquals(2, notebookRepoSync.list(0, null).size());
        Assert.assertEquals(2, notebookRepoSync.list(1, null).size());
    }

    @Test
    public void testCheckpointOneStorage() throws IOException, SchedulerException {
        System.setProperty(ZEPPELIN_NOTEBOOK_STORAGE.getVarName(), "org.apache.zeppelin.notebook.repo.GitNotebookRepo");
        ZeppelinConfiguration vConf = ZeppelinConfiguration.create();
        NotebookRepoSync vRepoSync = new NotebookRepoSync(vConf);
        Notebook vNotebookSync = new Notebook(vConf, vRepoSync, factory, interpreterSettingManager, search, notebookAuthorization, credentials, null);
        // one git versioned storage initialized
        assertThat(vRepoSync.getRepoCount()).isEqualTo(1);
        assertThat(vRepoSync.getRepo(0)).isInstanceOf(GitNotebookRepo.class);
        GitNotebookRepo gitRepo = ((GitNotebookRepo) (vRepoSync.getRepo(0)));
        // no notes
        assertThat(vRepoSync.list(anonymous).size()).isEqualTo(0);
        // create note
        Note note = vNotebookSync.createNote("/test", "test", anonymous);
        assertThat(vRepoSync.list(anonymous).size()).isEqualTo(1);
        NoteInfo noteInfo = vRepoSync.list(anonymous).values().iterator().next();
        String noteId = noteInfo.getId();
        String notePath = noteInfo.getPath();
        // first checkpoint
        vRepoSync.checkpoint(noteId, notePath, "checkpoint message", anonymous);
        int vCount = gitRepo.revisionHistory(noteId, notePath, anonymous).size();
        assertThat(vCount).isEqualTo(1);
        note.setInterpreterFactory(Mockito.mock(InterpreterFactory.class));
        Paragraph p = note.addNewParagraph(ANONYMOUS);
        Map<String, Object> config = p.getConfig();
        config.put("enabled", true);
        p.setConfig(config);
        p.setText("%md checkpoint test");
        // save and checkpoint again
        vRepoSync.save(note, anonymous);
        vRepoSync.checkpoint(noteId, notePath, "checkpoint message 2", anonymous);
        assertThat(gitRepo.revisionHistory(noteId, notePath, anonymous).size()).isEqualTo((vCount + 1));
        notebookRepoSync.remove(note.getId(), note.getPath(), anonymous);
    }

    @Test
    public void testSyncWithAcl() throws IOException {
        /* scenario 1 - note exists with acl on main storage */
        AuthenticationInfo user1 = new AuthenticationInfo("user1");
        Note note = notebookSync.createNote("/test", "test", user1);
        Assert.assertEquals(0, note.getParagraphs().size());
        // saved on both storages
        Assert.assertEquals(1, notebookRepoSync.list(0, null).size());
        Assert.assertEquals(1, notebookRepoSync.list(1, null).size());
        /* check that user1 is the only owner */
        NotebookAuthorization authInfo = NotebookAuthorization.getInstance();
        Set<String> entity = new HashSet<String>();
        entity.add(user1.getUser());
        Assert.assertEquals(true, authInfo.isOwner(note.getId(), entity));
        Assert.assertEquals(1, authInfo.getOwners(note.getId()).size());
        Assert.assertEquals(0, authInfo.getReaders(note.getId()).size());
        Assert.assertEquals(0, authInfo.getRunners(note.getId()).size());
        Assert.assertEquals(0, authInfo.getWriters(note.getId()).size());
        /* update note and save on secondary storage */
        note.setInterpreterFactory(Mockito.mock(InterpreterFactory.class));
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        p1.setText("hello world");
        Assert.assertEquals(1, note.getParagraphs().size());
        notebookRepoSync.save(1, note, null);
        /* check paragraph isn't saved into first storage */
        Assert.assertEquals(0, notebookRepoSync.get(0, notebookRepoSync.list(0, null).get(0).getId(), notebookRepoSync.list(0, null).get(0).getPath(), null).getParagraphs().size());
        /* check paragraph is saved into second storage */
        Assert.assertEquals(1, notebookRepoSync.get(1, notebookRepoSync.list(1, null).get(0).getId(), notebookRepoSync.list(1, null).get(0).getPath(), null).getParagraphs().size());
        /* now sync by user1 */
        notebookRepoSync.sync(user1);
        /* check that note updated and acl are same on main storage */
        Assert.assertEquals(1, notebookRepoSync.get(0, notebookRepoSync.list(0, null).get(0).getId(), notebookRepoSync.list(0, null).get(0).getPath(), null).getParagraphs().size());
        Assert.assertEquals(true, authInfo.isOwner(note.getId(), entity));
        Assert.assertEquals(1, authInfo.getOwners(note.getId()).size());
        Assert.assertEquals(0, authInfo.getReaders(note.getId()).size());
        Assert.assertEquals(0, authInfo.getRunners(note.getId()).size());
        Assert.assertEquals(0, authInfo.getWriters(note.getId()).size());
        /* scenario 2 - note doesn't exist on main storage */
        /* remove from main storage */
        notebookRepoSync.remove(0, note.getId(), note.getPath(), user1);
        Assert.assertEquals(0, notebookRepoSync.list(0, null).size());
        Assert.assertEquals(1, notebookRepoSync.list(1, null).size());
        authInfo.removeNote(note.getId());
        Assert.assertEquals(0, authInfo.getOwners(note.getId()).size());
        Assert.assertEquals(0, authInfo.getReaders(note.getId()).size());
        Assert.assertEquals(0, authInfo.getRunners(note.getId()).size());
        Assert.assertEquals(0, authInfo.getWriters(note.getId()).size());
        /* now sync - should bring note from secondary storage with added acl */
        notebookRepoSync.sync(user1);
        Assert.assertEquals(1, notebookRepoSync.list(0, null).size());
        Assert.assertEquals(1, notebookRepoSync.list(1, null).size());
        Assert.assertEquals(1, authInfo.getOwners(note.getId()).size());
        Assert.assertEquals(1, authInfo.getReaders(note.getId()).size());
        Assert.assertEquals(1, authInfo.getRunners(note.getId()).size());
        Assert.assertEquals(1, authInfo.getWriters(note.getId()).size());
        Assert.assertEquals(true, authInfo.isOwner(note.getId(), entity));
        Assert.assertEquals(true, authInfo.isReader(note.getId(), entity));
        Assert.assertEquals(true, authInfo.isRunner(note.getId(), entity));
        Assert.assertEquals(true, authInfo.isWriter(note.getId(), entity));
    }
}

