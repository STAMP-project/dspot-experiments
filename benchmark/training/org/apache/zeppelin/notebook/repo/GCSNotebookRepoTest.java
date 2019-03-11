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


import ConfVars.ZEPPELIN_NOTEBOOK_GCS_STORAGE_DIR;
import Status.ABORT;
import Status.RUNNING;
import com.google.cloud.storage.Storage;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.NoteInfo;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class GCSNotebookRepoTest {
    private static final AuthenticationInfo AUTH_INFO = AuthenticationInfo.ANONYMOUS;

    private GCSNotebookRepo notebookRepo;

    private Storage storage;

    @Parameterized.Parameter(0)
    public String bucketName;

    @Parameterized.Parameter(1)
    public Optional<String> basePath;

    @Parameterized.Parameter(2)
    public String uriPath;

    private Note runningNote;

    @Test
    public void testList_nonexistent() throws Exception {
        assertThat(notebookRepo.list(GCSNotebookRepoTest.AUTH_INFO)).isEmpty();
    }

    @Test
    public void testList() throws Exception {
        createAt(runningNote, "note.zpln");
        createAt(runningNote, "/note.zpln");
        createAt(runningNote, "validid/my_12.zpln");
        createAt(runningNote, "validid-2/my_123.zpln");
        createAt(runningNote, "cannot-be-dir/note.json/foo");
        createAt(runningNote, "cannot/be/nested/note.json");
        Map<String, NoteInfo> infos = notebookRepo.list(GCSNotebookRepoTest.AUTH_INFO);
        List<String> noteIds = new ArrayList<>();
        for (NoteInfo info : infos.values()) {
            noteIds.add(info.getId());
        }
        // Only valid paths are gs://bucketname/path/<noteid>/note.json
        assertThat(noteIds).containsExactlyElementsIn(ImmutableList.of("12", "123"));
    }

    @Test
    public void testGet_nonexistent() throws Exception {
        try {
            notebookRepo.get("id", "", GCSNotebookRepoTest.AUTH_INFO);
            TestCase.fail();
        } catch (IOException e) {
        }
    }

    @Test
    public void testGet() throws Exception {
        create(runningNote);
        // Status of saved running note is removed in get()
        Note got = notebookRepo.get(runningNote.getId(), runningNote.getPath(), GCSNotebookRepoTest.AUTH_INFO);
        assertThat(got.getLastParagraph().getStatus()).isEqualTo(ABORT);
        // But otherwise equal
        got.getLastParagraph().setStatus(RUNNING);
        assertThat(got).isEqualTo(runningNote);
    }

    @Test
    public void testGet_malformed() throws Exception {
        createMalformed("id", "/name");
        try {
            notebookRepo.get("id", "/name", GCSNotebookRepoTest.AUTH_INFO);
            TestCase.fail();
        } catch (IOException e) {
        }
    }

    @Test
    public void testSave_create() throws Exception {
        notebookRepo.save(runningNote, GCSNotebookRepoTest.AUTH_INFO);
        // Output is saved
        assertThat(storage.readAllBytes(makeBlobId(runningNote.getId(), runningNote.getPath()))).isEqualTo(runningNote.toJson().getBytes("UTF-8"));
    }

    @Test
    public void testSave_update() throws Exception {
        notebookRepo.save(runningNote, GCSNotebookRepoTest.AUTH_INFO);
        // Change name of runningNote
        runningNote.setPath("/new-name");
        notebookRepo.save(runningNote, GCSNotebookRepoTest.AUTH_INFO);
        assertThat(storage.readAllBytes(makeBlobId(runningNote.getId(), runningNote.getPath()))).isEqualTo(runningNote.toJson().getBytes("UTF-8"));
    }

    @Test
    public void testRemove_nonexistent() throws Exception {
        try {
            notebookRepo.remove("id", "/name", GCSNotebookRepoTest.AUTH_INFO);
            TestCase.fail();
        } catch (IOException e) {
        }
    }

    @Test
    public void testRemove() throws Exception {
        create(runningNote);
        notebookRepo.remove(runningNote.getId(), runningNote.getPath(), GCSNotebookRepoTest.AUTH_INFO);
        assertThat(storage.get(makeBlobId(runningNote.getId(), runningNote.getPath()))).isNull();
    }

    /* These tests test path parsing for illegal paths, and do not use the parameterized vars */
    @Test
    public void testInitialization_pathNotSet() throws Exception {
        try {
            System.setProperty(ZEPPELIN_NOTEBOOK_GCS_STORAGE_DIR.getVarName(), "");
            new GCSNotebookRepo(new ZeppelinConfiguration(), storage);
            TestCase.fail();
        } catch (IOException e) {
        }
    }

    @Test
    public void testInitialization_malformedPath() throws Exception {
        try {
            System.setProperty(ZEPPELIN_NOTEBOOK_GCS_STORAGE_DIR.getVarName(), "foo");
            new GCSNotebookRepo(new ZeppelinConfiguration(), storage);
            TestCase.fail();
        } catch (IOException e) {
        }
    }
}

