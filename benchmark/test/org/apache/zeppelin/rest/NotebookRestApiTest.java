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


import AuthenticationInfo.ANONYMOUS;
import InterpreterResult.Code;
import InterpreterResult.Type;
import Job.Status.ERROR;
import Job.Status.FINISHED;
import Job.Status.READY;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.Notebook;
import org.apache.zeppelin.notebook.Paragraph;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.apache.zeppelin.utils.TestUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Zeppelin notebook rest api tests.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotebookRestApiTest extends AbstractTestRestApi {
    Gson gson = new Gson();

    AuthenticationInfo anonymous;

    @Test
    public void testGetNoteParagraphJobStatus() throws IOException {
        Note note1 = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        note1.addNewParagraph(ANONYMOUS);
        String paragraphId = note1.getLastParagraph().getId();
        GetMethod get = AbstractTestRestApi.httpGet(((("/notebook/job/" + (note1.getId())) + "/") + paragraphId));
        Assert.assertThat(get, isAllowed());
        Map<String, Object> resp = gson.fromJson(get.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Map<String, Set<String>> paragraphStatus = ((Map<String, Set<String>>) (resp.get("body")));
        // Check id and status have proper value
        Assert.assertEquals(paragraphStatus.get("id"), paragraphId);
        Assert.assertEquals(paragraphStatus.get("status"), "READY");
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(note1.getId(), anonymous);
    }

    @Test
    public void testRunParagraphJob() throws IOException {
        Note note1 = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        note1.addNewParagraph(ANONYMOUS);
        Paragraph p = note1.addNewParagraph(ANONYMOUS);
        // run blank paragraph
        PostMethod post = AbstractTestRestApi.httpPost(((("/notebook/job/" + (note1.getId())) + "/") + (p.getId())), "");
        Assert.assertThat(post, isAllowed());
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertEquals(resp.get("status"), "OK");
        post.releaseConnection();
        Assert.assertEquals(p.getStatus(), FINISHED);
        // run non-blank paragraph
        p.setText("test");
        post = AbstractTestRestApi.httpPost(((("/notebook/job/" + (note1.getId())) + "/") + (p.getId())), "");
        Assert.assertThat(post, isAllowed());
        resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertEquals(resp.get("status"), "OK");
        post.releaseConnection();
        Assert.assertNotEquals(p.getStatus(), READY);
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(note1.getId(), anonymous);
    }

    @Test
    public void testRunParagraphSynchronously() throws IOException {
        Note note1 = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        note1.addNewParagraph(ANONYMOUS);
        Paragraph p = note1.addNewParagraph(ANONYMOUS);
        // run non-blank paragraph
        String title = "title";
        String text = "%sh\n sleep 1";
        p.setTitle(title);
        p.setText(text);
        PostMethod post = AbstractTestRestApi.httpPost(((("/notebook/run/" + (note1.getId())) + "/") + (p.getId())), "");
        Assert.assertThat(post, isAllowed());
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertEquals(resp.get("status"), "OK");
        post.releaseConnection();
        Assert.assertNotEquals(p.getStatus(), READY);
        // Check if the paragraph is emptied
        Assert.assertEquals(title, p.getTitle());
        Assert.assertEquals(text, p.getText());
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(note1.getId(), anonymous);
    }

    @Test
    public void testRunAllParagraph_AllSuccess() throws IOException {
        Note note1 = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        // 2 paragraphs
        // P1:
        // %python
        // import time
        // time.sleep(1)
        // user='abc'
        // P2:
        // %python
        // from __future__ import print_function
        // print(user)
        // 
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        Paragraph p2 = note1.addNewParagraph(ANONYMOUS);
        p1.setText("%python import time\ntime.sleep(1)\nuser=\'abc\'");
        p2.setText("%python from __future__ import print_function\nprint(user)");
        PostMethod post = AbstractTestRestApi.httpPost(("/notebook/job/" + (note1.getId())), "");
        Assert.assertThat(post, isAllowed());
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertEquals(resp.get("status"), "OK");
        post.releaseConnection();
        Assert.assertEquals(FINISHED, p1.getStatus());
        Assert.assertEquals(FINISHED, p2.getStatus());
        Assert.assertEquals("abc\n", p2.getReturn().message().get(0).getData());
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(note1.getId(), anonymous);
    }

    @Test
    public void testRunAllParagraph_FirstFailed() throws IOException {
        Note note1 = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        // 2 paragraphs
        // P1:
        // %python
        // import time
        // time.sleep(1)
        // from __future__ import print_function
        // print(user)
        // P2:
        // %python
        // user='abc'
        // 
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        Paragraph p2 = note1.addNewParagraph(ANONYMOUS);
        p1.setText("%python import time\ntime.sleep(1)\nfrom __future__ import print_function\nprint(user2)");
        p2.setText("%python user2=\'abc\'\nprint(user2)");
        PostMethod post = AbstractTestRestApi.httpPost(("/notebook/job/" + (note1.getId())), "");
        Assert.assertThat(post, isAllowed());
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertEquals(resp.get("status"), "OK");
        post.releaseConnection();
        Assert.assertEquals(ERROR, p1.getStatus());
        // p2 will be skipped because p1 is failed.
        Assert.assertEquals(READY, p2.getStatus());
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(note1.getId(), anonymous);
    }

    @Test
    public void testCloneNote() throws IOException {
        Note note1 = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        PostMethod post = AbstractTestRestApi.httpPost(("/notebook/" + (note1.getId())), "");
        AbstractTestRestApi.LOG.info(("testCloneNote response\n" + (post.getResponseBodyAsString())));
        Assert.assertThat(post, isAllowed());
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        String clonedNoteId = ((String) (resp.get("body")));
        post.releaseConnection();
        GetMethod get = AbstractTestRestApi.httpGet(("/notebook/" + clonedNoteId));
        Assert.assertThat(get, isAllowed());
        Map<String, Object> resp2 = gson.fromJson(get.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Map<String, Object> resp2Body = ((Map<String, Object>) (resp2.get("body")));
        // assertEquals(resp2Body.get("name"), "Note " + clonedNoteId);
        get.releaseConnection();
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(note1.getId(), anonymous);
        TestUtils.getInstance(Notebook.class).removeNote(clonedNoteId, anonymous);
    }

    @Test
    public void testRenameNote() throws IOException {
        String oldName = "old_name";
        Note note = TestUtils.getInstance(Notebook.class).createNote(oldName, anonymous);
        Assert.assertEquals(note.getName(), oldName);
        String noteId = note.getId();
        final String newName = "testName";
        String jsonRequest = ("{\"name\": " + newName) + "}";
        PutMethod put = AbstractTestRestApi.httpPut((("/notebook/" + noteId) + "/rename/"), jsonRequest);
        Assert.assertThat("test testRenameNote:", put, isAllowed());
        put.releaseConnection();
        Assert.assertEquals(note.getName(), newName);
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(noteId, anonymous);
    }

    @Test
    public void testUpdateParagraphConfig() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        String noteId = note.getId();
        Paragraph p = note.addNewParagraph(ANONYMOUS);
        Assert.assertNull(p.getConfig().get("colWidth"));
        String paragraphId = p.getId();
        String jsonRequest = "{\"colWidth\": 6.0}";
        PutMethod put = AbstractTestRestApi.httpPut((((("/notebook/" + noteId) + "/paragraph/") + paragraphId) + "/config"), jsonRequest);
        Assert.assertThat("test testUpdateParagraphConfig:", put, isAllowed());
        Map<String, Object> resp = gson.fromJson(put.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Map<String, Object> respBody = ((Map<String, Object>) (resp.get("body")));
        Map<String, Object> config = ((Map<String, Object>) (respBody.get("config")));
        put.releaseConnection();
        Assert.assertEquals(config.get("colWidth"), 6.0);
        note = TestUtils.getInstance(Notebook.class).getNote(noteId);
        Assert.assertEquals(note.getParagraph(paragraphId).getConfig().get("colWidth"), 6.0);
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(noteId, anonymous);
    }

    @Test
    public void testClearAllParagraphOutput() throws IOException {
        // Create note and set result explicitly
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        InterpreterResult result = new InterpreterResult(Code.SUCCESS, Type.TEXT, "result");
        p1.setResult(result);
        Paragraph p2 = note.addNewParagraph(ANONYMOUS);
        p2.setReturn(result, new Throwable());
        // clear paragraph result
        PutMethod put = AbstractTestRestApi.httpPut((("/notebook/" + (note.getId())) + "/clear"), "");
        AbstractTestRestApi.LOG.info(("test clear paragraph output response\n" + (put.getResponseBodyAsString())));
        Assert.assertThat(put, isAllowed());
        put.releaseConnection();
        // check if paragraph results are cleared
        GetMethod get = AbstractTestRestApi.httpGet(((("/notebook/" + (note.getId())) + "/paragraph/") + (p1.getId())));
        Assert.assertThat(get, isAllowed());
        Map<String, Object> resp1 = gson.fromJson(get.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Map<String, Object> resp1Body = ((Map<String, Object>) (resp1.get("body")));
        Assert.assertNull(resp1Body.get("result"));
        get = AbstractTestRestApi.httpGet(((("/notebook/" + (note.getId())) + "/paragraph/") + (p2.getId())));
        Assert.assertThat(get, isAllowed());
        Map<String, Object> resp2 = gson.fromJson(get.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Map<String, Object> resp2Body = ((Map<String, Object>) (resp2.get("body")));
        Assert.assertNull(resp2Body.get("result"));
        get.releaseConnection();
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testRunWithServerRestart() throws Exception {
        Note note1 = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        // 2 paragraphs
        // P1:
        // %python
        // import time
        // time.sleep(1)
        // from __future__ import print_function
        // print(user)
        // P2:
        // %python
        // user='abc'
        // 
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        Paragraph p2 = note1.addNewParagraph(ANONYMOUS);
        p1.setText("%python import time\ntime.sleep(1)\nuser=\'abc\'");
        p2.setText("%python from __future__ import print_function\nprint(user)");
        PostMethod post1 = AbstractTestRestApi.httpPost(("/notebook/job/" + (note1.getId())), "");
        Assert.assertThat(post1, isAllowed());
        post1.releaseConnection();
        PutMethod put = AbstractTestRestApi.httpPut((("/notebook/" + (note1.getId())) + "/clear"), "");
        AbstractTestRestApi.LOG.info(("test clear paragraph output response\n" + (put.getResponseBodyAsString())));
        Assert.assertThat(put, isAllowed());
        put.releaseConnection();
        // restart server (while keeping interpreter configuration)
        AbstractTestRestApi.shutDown(false);
        AbstractTestRestApi.startUp(NotebookRestApiTest.class.getSimpleName(), false);
        note1 = TestUtils.getInstance(Notebook.class).getNote(note1.getId());
        p1 = note1.getParagraph(p1.getId());
        p2 = note1.getParagraph(p2.getId());
        PostMethod post2 = AbstractTestRestApi.httpPost(("/notebook/job/" + (note1.getId())), "");
        Assert.assertThat(post2, isAllowed());
        Map<String, Object> resp = gson.fromJson(post2.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertEquals(resp.get("status"), "OK");
        post2.releaseConnection();
        Assert.assertEquals(FINISHED, p1.getStatus());
        Assert.assertEquals(FINISHED, p2.getStatus());
        Assert.assertNotNull(p2.getReturn());
        Assert.assertEquals("abc\n", p2.getReturn().message().get(0).getData());
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(note1.getId(), anonymous);
    }
}

