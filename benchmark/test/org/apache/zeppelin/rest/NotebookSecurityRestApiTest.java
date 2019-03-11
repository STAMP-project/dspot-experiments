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
package org.apache.zeppelin.rest;


import com.google.gson.Gson;
import java.io.IOException;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.Notebook;
import org.apache.zeppelin.utils.TestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class NotebookSecurityRestApiTest extends AbstractTestRestApi {
    Gson gson = new Gson();

    @Test
    public void testThatUserCanCreateAndRemoveNote() throws IOException {
        String noteId = createNoteForUser("test", "admin", "password1");
        Assert.assertNotNull(noteId);
        String id = getNoteIdForUser(noteId, "admin", "password1");
        Assert.assertThat(id, CoreMatchers.is(noteId));
        deleteNoteForUser(noteId, "admin", "password1");
    }

    @Test
    public void testThatOtherUserCanAccessNoteIfPermissionNotSet() throws IOException {
        String noteId = createNoteForUser("test", "admin", "password1");
        userTryGetNote(noteId, "user1", "password2", isAllowed());
        deleteNoteForUser(noteId, "admin", "password1");
    }

    @Test
    public void testThatOtherUserCannotAccessNoteIfPermissionSet() throws IOException {
        String noteId = createNoteForUser("test", "admin", "password1");
        // set permission
        String payload = "{ \"owners\": [\"admin\"], \"readers\": [\"user2\"], " + "\"runners\": [\"user2\"], \"writers\": [\"user2\"] }";
        PutMethod put = AbstractTestRestApi.httpPut((("/notebook/" + noteId) + "/permissions"), payload, "admin", "password1");
        Assert.assertThat("test set note permission method:", put, isAllowed());
        put.releaseConnection();
        userTryGetNote(noteId, "user1", "password2", isForbidden());
        userTryGetNote(noteId, "user2", "password3", isAllowed());
        deleteNoteForUser(noteId, "admin", "password1");
    }

    @Test
    public void testThatWriterCannotRemoveNote() throws IOException {
        String noteId = createNoteForUser("test", "admin", "password1");
        // set permission
        String payload = "{ \"owners\": [\"admin\", \"user1\"], \"readers\": [\"user2\"], " + "\"runners\": [\"user2\"], \"writers\": [\"user2\"] }";
        PutMethod put = AbstractTestRestApi.httpPut((("/notebook/" + noteId) + "/permissions"), payload, "admin", "password1");
        Assert.assertThat("test set note permission method:", put, isAllowed());
        put.releaseConnection();
        userTryRemoveNote(noteId, "user2", "password3", isForbidden());
        userTryRemoveNote(noteId, "user1", "password2", isAllowed());
        Note deletedNote = TestUtils.getInstance(Notebook.class).getNote(noteId);
        Assert.assertNull("Deleted note should be null", deletedNote);
    }

    @Test
    public void testThatUserCanSearchNote() throws IOException {
        String noteId1 = createNoteForUser("test1", "admin", "password1");
        createParagraphForUser(noteId1, "admin", "password1", "title1", "ThisIsToTestSearchMethodWithPermissions 1");
        String noteId2 = createNoteForUser("test2", "user1", "password2");
        createParagraphForUser(noteId1, "admin", "password1", "title2", "ThisIsToTestSearchMethodWithPermissions 2");
        // set permission for each note
        setPermissionForNote(noteId1, "admin", "password1");
        setPermissionForNote(noteId1, "user1", "password2");
        searchNoteBasedOnPermission("ThisIsToTestSearchMethodWithPermissions", "admin", "password1");
        deleteNoteForUser(noteId1, "admin", "password1");
        deleteNoteForUser(noteId2, "user1", "password2");
    }
}

