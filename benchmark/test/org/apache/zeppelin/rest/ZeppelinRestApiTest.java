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
import ConfVars.ZEPPELIN_NOTEBOOK_CRON_ENABLE;
import ConfVars.ZEPPELIN_NOTEBOOK_CRON_FOLDERS;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.lang3.StringUtils;
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
 * BASIC Zeppelin rest api tests.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ZeppelinRestApiTest extends AbstractTestRestApi {
    Gson gson = new Gson();

    AuthenticationInfo anonymous;

    /**
     * ROOT API TEST.
     */
    @Test
    public void getApiRoot() throws IOException {
        // when
        GetMethod httpGetRoot = AbstractTestRestApi.httpGet("/");
        // then
        Assert.assertThat(httpGetRoot, isAllowed());
        httpGetRoot.releaseConnection();
    }

    @Test
    public void testGetNoteInfo() throws IOException {
        AbstractTestRestApi.LOG.info("testGetNoteInfo");
        // Create note to get info
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Assert.assertNotNull("can't create new note", note);
        note.setName("note");
        Paragraph paragraph = note.addNewParagraph(ANONYMOUS);
        Map config = paragraph.getConfig();
        config.put("enabled", true);
        paragraph.setConfig(config);
        String paragraphText = "%md This is my new paragraph in my new note";
        paragraph.setText(paragraphText);
        TestUtils.getInstance(Notebook.class).saveNote(note, anonymous);
        String sourceNoteId = note.getId();
        GetMethod get = AbstractTestRestApi.httpGet(("/notebook/" + sourceNoteId));
        AbstractTestRestApi.LOG.info(("testGetNoteInfo \n" + (get.getResponseBodyAsString())));
        Assert.assertThat("test note get method:", get, isAllowed());
        Map<String, Object> resp = gson.fromJson(get.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertNotNull(resp);
        Assert.assertEquals("OK", resp.get("status"));
        Map<String, Object> body = ((Map<String, Object>) (resp.get("body")));
        List<Map<String, Object>> paragraphs = ((List<Map<String, Object>>) (body.get("paragraphs")));
        Assert.assertTrue(((paragraphs.size()) > 0));
        Assert.assertEquals(paragraphText, paragraphs.get(0).get("text"));
        // 
        TestUtils.getInstance(Notebook.class).removeNote(sourceNoteId, anonymous);
    }

    @Test
    public void testNoteCreateWithName() throws IOException {
        String noteName = "Test note name";
        testNoteCreate(noteName);
    }

    @Test
    public void testNoteCreateNoName() throws IOException {
        testNoteCreate("");
    }

    @Test
    public void testNoteCreateWithParagraphs() throws IOException {
        // Call Create Note REST API
        String noteName = "test";
        String jsonRequest = ((((((("{\"name\":\"" + noteName) + "\", \"paragraphs\": [") + "{\"title\": \"title1\", \"text\": \"text1\"},") + "{\"title\": \"title2\", \"text\": \"text2\"},") + "{\"title\": \"titleConfig\", \"text\": \"text3\", ") + "\"config\": {\"colWidth\": 9.0, \"title\": true, ") + "\"results\": [{\"graph\": {\"mode\": \"pieChart\"}}] ") + "}}]} ";
        PostMethod post = AbstractTestRestApi.httpPost("/notebook/", jsonRequest);
        AbstractTestRestApi.LOG.info(("testNoteCreate \n" + (post.getResponseBodyAsString())));
        Assert.assertThat("test note create method:", post, isAllowed());
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        String newNoteId = ((String) (resp.get("body")));
        AbstractTestRestApi.LOG.info(("newNoteId:=" + newNoteId));
        Note newNote = TestUtils.getInstance(Notebook.class).getNote(newNoteId);
        Assert.assertNotNull("Can not find new note by id", newNote);
        // This is partial test as newNote is in memory but is not persistent
        String newNoteName = newNote.getName();
        AbstractTestRestApi.LOG.info(("new note name is: " + newNoteName));
        String expectedNoteName = noteName;
        if (noteName.isEmpty()) {
            expectedNoteName = "Note " + newNoteId;
        }
        Assert.assertEquals("compare note name", expectedNoteName, newNoteName);
        Assert.assertEquals("initial paragraph check failed", 4, newNote.getParagraphs().size());
        for (Paragraph p : newNote.getParagraphs()) {
            if (StringUtils.isEmpty(p.getText())) {
                continue;
            }
            Assert.assertTrue("paragraph title check failed", p.getTitle().startsWith("title"));
            Assert.assertTrue("paragraph text check failed", p.getText().startsWith("text"));
            if (p.getTitle().equals("titleConfig")) {
                Assert.assertEquals("paragraph col width check failed", 9.0, p.getConfig().get("colWidth"));
                Assert.assertTrue("paragraph show title check failed", ((boolean) (p.getConfig().get("title"))));
                Map graph = ((List<Map>) (p.getConfig().get("results"))).get(0);
                String mode = ((Map) (graph.get("graph"))).get("mode").toString();
                Assert.assertEquals("paragraph graph mode check failed", "pieChart", mode);
            }
        }
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(newNoteId, anonymous);
        post.releaseConnection();
    }

    @Test
    public void testDeleteNote() throws IOException {
        AbstractTestRestApi.LOG.info("testDeleteNote");
        // Create note and get ID
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testDeletedNote", anonymous);
        String noteId = note.getId();
        testDeleteNote(noteId);
    }

    @Test
    public void testDeleteNoteBadId() throws IOException {
        AbstractTestRestApi.LOG.info("testDeleteNoteBadId");
        testDeleteNotExistNote("bad_ID");
    }

    @Test
    public void testExportNote() throws IOException {
        AbstractTestRestApi.LOG.info("testExportNote");
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testExportNote", anonymous);
        Assert.assertNotNull("can't create new note", note);
        note.setName("source note for export");
        Paragraph paragraph = note.addNewParagraph(ANONYMOUS);
        Map config = paragraph.getConfig();
        config.put("enabled", true);
        paragraph.setConfig(config);
        paragraph.setText("%md This is my new paragraph in my new note");
        TestUtils.getInstance(Notebook.class).saveNote(note, anonymous);
        String sourceNoteId = note.getId();
        // Call export Note REST API
        GetMethod get = AbstractTestRestApi.httpGet(("/notebook/export/" + sourceNoteId));
        AbstractTestRestApi.LOG.info(("testNoteExport \n" + (get.getResponseBodyAsString())));
        Assert.assertThat("test note export method:", get, isAllowed());
        Map<String, Object> resp = gson.fromJson(get.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        String exportJSON = ((String) (resp.get("body")));
        Assert.assertNotNull("Can not find new notejson", exportJSON);
        AbstractTestRestApi.LOG.info(("export JSON:=" + exportJSON));
        TestUtils.getInstance(Notebook.class).removeNote(sourceNoteId, anonymous);
        get.releaseConnection();
    }

    @Test
    public void testImportNotebook() throws IOException {
        Map<String, Object> resp;
        String noteName = "source note for import";
        AbstractTestRestApi.LOG.info("testImportNote");
        // create test note
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testImportNotebook", anonymous);
        Assert.assertNotNull("can't create new note", note);
        note.setName(noteName);
        Paragraph paragraph = note.addNewParagraph(ANONYMOUS);
        Map config = paragraph.getConfig();
        config.put("enabled", true);
        paragraph.setConfig(config);
        paragraph.setText("%md This is my new paragraph in my new note");
        TestUtils.getInstance(Notebook.class).saveNote(note, anonymous);
        String sourceNoteId = note.getId();
        // get note content as JSON
        String oldJson = getNoteContent(sourceNoteId);
        // delete it first then import it
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
        // call note post
        PostMethod importPost = AbstractTestRestApi.httpPost("/notebook/import/", oldJson);
        Assert.assertThat(importPost, isAllowed());
        resp = gson.fromJson(importPost.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        String importId = ((String) (resp.get("body")));
        Assert.assertNotNull("Did not get back a note id in body", importId);
        Note newNote = TestUtils.getInstance(Notebook.class).getNote(importId);
        Assert.assertEquals("Compare note names", noteName, newNote.getName());
        Assert.assertEquals("Compare paragraphs count", note.getParagraphs().size(), newNote.getParagraphs().size());
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(newNote.getId(), anonymous);
        importPost.releaseConnection();
    }

    @Test
    public void testCloneNote() throws IOException, IllegalArgumentException {
        AbstractTestRestApi.LOG.info("testCloneNote");
        // Create note to clone
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testCloneNote", anonymous);
        Assert.assertNotNull("can't create new note", note);
        note.setName("source note for clone");
        Paragraph paragraph = note.addNewParagraph(ANONYMOUS);
        Map config = paragraph.getConfig();
        config.put("enabled", true);
        paragraph.setConfig(config);
        paragraph.setText("%md This is my new paragraph in my new note");
        TestUtils.getInstance(Notebook.class).saveNote(note, anonymous);
        String sourceNoteId = note.getId();
        String noteName = "clone Note Name";
        // Call Clone Note REST API
        String jsonRequest = ("{\"name\":\"" + noteName) + "\"}";
        PostMethod post = AbstractTestRestApi.httpPost(("/notebook/" + sourceNoteId), jsonRequest);
        AbstractTestRestApi.LOG.info(("testNoteClone \n" + (post.getResponseBodyAsString())));
        Assert.assertThat("test note clone method:", post, isAllowed());
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        String newNoteId = ((String) (resp.get("body")));
        AbstractTestRestApi.LOG.info(("newNoteId:=" + newNoteId));
        Note newNote = TestUtils.getInstance(Notebook.class).getNote(newNoteId);
        Assert.assertNotNull("Can not find new note by id", newNote);
        Assert.assertEquals("Compare note names", noteName, newNote.getName());
        Assert.assertEquals("Compare paragraphs count", note.getParagraphs().size(), newNote.getParagraphs().size());
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
        TestUtils.getInstance(Notebook.class).removeNote(newNote.getId(), anonymous);
        post.releaseConnection();
    }

    @Test
    public void testListNotes() throws IOException {
        AbstractTestRestApi.LOG.info("testListNotes");
        GetMethod get = AbstractTestRestApi.httpGet("/notebook/ ");
        Assert.assertThat("List notes method", get, isAllowed());
        Map<String, Object> resp = gson.fromJson(get.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        List<Map<String, String>> body = ((List<Map<String, String>>) (resp.get("body")));
        // TODO(khalid): anonymous or specific user notes?
        HashSet<String> anonymous = Sets.newHashSet("anonymous");
        Assert.assertEquals("List notes are equal", TestUtils.getInstance(Notebook.class).getAllNotes(anonymous).size(), body.size());
        get.releaseConnection();
    }

    @Test
    public void testNoteJobs() throws IOException, InterruptedException {
        AbstractTestRestApi.LOG.info("testNoteJobs");
        // Create note to run test.
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testNoteJobs", anonymous);
        Assert.assertNotNull("can't create new note", note);
        note.setName("note for run test");
        Paragraph paragraph = note.addNewParagraph(ANONYMOUS);
        Map config = paragraph.getConfig();
        config.put("enabled", true);
        paragraph.setConfig(config);
        paragraph.setText("%md This is test paragraph.");
        TestUtils.getInstance(Notebook.class).saveNote(note, anonymous);
        String noteId = note.getId();
        note.runAll(anonymous, true);
        // wait until job is finished or timeout.
        int timeout = 1;
        while (!(paragraph.isTerminated())) {
            Thread.sleep(1000);
            if ((timeout++) > 10) {
                AbstractTestRestApi.LOG.info("testNoteJobs timeout job.");
                break;
            }
        } 
        // Call Run note jobs REST API
        PostMethod postNoteJobs = AbstractTestRestApi.httpPost(("/notebook/job/" + noteId), "");
        Assert.assertThat("test note jobs run:", postNoteJobs, isAllowed());
        postNoteJobs.releaseConnection();
        // Call Stop note jobs REST API
        DeleteMethod deleteNoteJobs = AbstractTestRestApi.httpDelete(("/notebook/job/" + noteId));
        Assert.assertThat("test note stop:", deleteNoteJobs, isAllowed());
        deleteNoteJobs.releaseConnection();
        Thread.sleep(1000);
        // Call Run paragraph REST API
        PostMethod postParagraph = AbstractTestRestApi.httpPost(((("/notebook/job/" + noteId) + "/") + (paragraph.getId())), "");
        Assert.assertThat("test paragraph run:", postParagraph, isAllowed());
        postParagraph.releaseConnection();
        Thread.sleep(1000);
        // Call Stop paragraph REST API
        DeleteMethod deleteParagraph = AbstractTestRestApi.httpDelete(((("/notebook/job/" + noteId) + "/") + (paragraph.getId())));
        Assert.assertThat("test paragraph stop:", deleteParagraph, isAllowed());
        deleteParagraph.releaseConnection();
        Thread.sleep(1000);
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testGetNoteJob() throws IOException, InterruptedException {
        AbstractTestRestApi.LOG.info("testGetNoteJob");
        // Create note to run test.
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testGetNoteJob", anonymous);
        Assert.assertNotNull("can't create new note", note);
        note.setName("note for run test");
        Paragraph paragraph = note.addNewParagraph(ANONYMOUS);
        Map config = paragraph.getConfig();
        config.put("enabled", true);
        paragraph.setConfig(config);
        paragraph.setText("%sh sleep 1");
        paragraph.setAuthenticationInfo(anonymous);
        TestUtils.getInstance(Notebook.class).saveNote(note, anonymous);
        String noteId = note.getId();
        note.runAll(anonymous, true);
        // assume that status of the paragraph is running
        GetMethod get = AbstractTestRestApi.httpGet(("/notebook/job/" + noteId));
        Assert.assertThat("test get note job: ", get, isAllowed());
        String responseBody = get.getResponseBodyAsString();
        get.releaseConnection();
        AbstractTestRestApi.LOG.info(("test get note job: \n" + responseBody));
        Map<String, Object> resp = gson.fromJson(responseBody, new TypeToken<Map<String, Object>>() {}.getType());
        List<Map<String, Object>> paragraphs = ((List<Map<String, Object>>) (resp.get("body")));
        Assert.assertEquals(1, paragraphs.size());
        Assert.assertTrue(paragraphs.get(0).containsKey("progress"));
        int progress = Integer.parseInt(((String) (paragraphs.get(0).get("progress"))));
        Assert.assertTrue(((progress >= 0) && (progress <= 100)));
        // wait until job is finished or timeout.
        int timeout = 1;
        while (!(paragraph.isTerminated())) {
            Thread.sleep(100);
            if ((timeout++) > 10) {
                AbstractTestRestApi.LOG.info("testGetNoteJob timeout job.");
                break;
            }
        } 
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testRunParagraphWithParams() throws IOException, InterruptedException {
        AbstractTestRestApi.LOG.info("testRunParagraphWithParams");
        // Create note to run test.
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testRunParagraphWithParams", anonymous);
        Assert.assertNotNull("can't create new note", note);
        note.setName("note for run test");
        Paragraph paragraph = note.addNewParagraph(ANONYMOUS);
        Map config = paragraph.getConfig();
        config.put("enabled", true);
        paragraph.setConfig(config);
        paragraph.setText("%spark\nval param = z.input(\"param\").toString\nprintln(param)");
        TestUtils.getInstance(Notebook.class).saveNote(note, anonymous);
        String noteId = note.getId();
        note.runAll(anonymous, true);
        // Call Run paragraph REST API
        PostMethod postParagraph = AbstractTestRestApi.httpPost(((("/notebook/job/" + noteId) + "/") + (paragraph.getId())), "{\"params\": {\"param\": \"hello\", \"param2\": \"world\"}}");
        Assert.assertThat("test paragraph run:", postParagraph, isAllowed());
        postParagraph.releaseConnection();
        Thread.sleep(1000);
        Note retrNote = TestUtils.getInstance(Notebook.class).getNote(noteId);
        Paragraph retrParagraph = retrNote.getParagraph(paragraph.getId());
        Map<String, Object> params = retrParagraph.settings.getParams();
        Assert.assertEquals("hello", params.get("param"));
        Assert.assertEquals("world", params.get("param2"));
        // cleanup
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testJobs() throws IOException, InterruptedException {
        // create a note and a paragraph
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testJobs", anonymous);
        note.setName("note for run test");
        Paragraph paragraph = note.addNewParagraph(ANONYMOUS);
        paragraph.setText("%md This is test paragraph.");
        Map config = paragraph.getConfig();
        config.put("enabled", true);
        paragraph.setConfig(config);
        note.runAll(ANONYMOUS, false);
        String jsonRequest = "{\"cron\":\"* * * * * ?\" }";
        // right cron expression but not exist note.
        PostMethod postCron = AbstractTestRestApi.httpPost("/notebook/cron/notexistnote", jsonRequest);
        Assert.assertThat("", postCron, isNotFound());
        postCron.releaseConnection();
        // right cron expression.
        postCron = AbstractTestRestApi.httpPost(("/notebook/cron/" + (note.getId())), jsonRequest);
        Assert.assertThat("", postCron, isAllowed());
        postCron.releaseConnection();
        Thread.sleep(1000);
        // wrong cron expression.
        jsonRequest = "{\"cron\":\"a * * * * ?\" }";
        postCron = AbstractTestRestApi.httpPost(("/notebook/cron/" + (note.getId())), jsonRequest);
        Assert.assertThat("", postCron, isBadRequest());
        postCron.releaseConnection();
        Thread.sleep(1000);
        // remove cron job.
        DeleteMethod deleteCron = AbstractTestRestApi.httpDelete(("/notebook/cron/" + (note.getId())));
        Assert.assertThat("", deleteCron, isAllowed());
        deleteCron.releaseConnection();
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testCronDisable() throws IOException, InterruptedException {
        // create a note and a paragraph
        System.setProperty(ZEPPELIN_NOTEBOOK_CRON_ENABLE.getVarName(), "false");
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testCronDisable", anonymous);
        note.setName("note for run test");
        Paragraph paragraph = note.addNewParagraph(ANONYMOUS);
        paragraph.setText("%md This is test paragraph.");
        Map config = paragraph.getConfig();
        config.put("enabled", true);
        paragraph.setConfig(config);
        note.runAll(ANONYMOUS, false);
        String jsonRequest = "{\"cron\":\"* * * * * ?\" }";
        // right cron expression.
        PostMethod postCron = AbstractTestRestApi.httpPost(("/notebook/cron/" + (note.getId())), jsonRequest);
        Assert.assertThat("", postCron, isForbidden());
        postCron.releaseConnection();
        System.setProperty(ZEPPELIN_NOTEBOOK_CRON_ENABLE.getVarName(), "true");
        System.setProperty(ZEPPELIN_NOTEBOOK_CRON_FOLDERS.getVarName(), "System/*");
        note.setName("System/test2");
        note.runAll(ANONYMOUS, false);
        postCron = AbstractTestRestApi.httpPost(("/notebook/cron/" + (note.getId())), jsonRequest);
        Assert.assertThat("", postCron, isAllowed());
        postCron.releaseConnection();
        Thread.sleep(1000);
        // remove cron job.
        DeleteMethod deleteCron = AbstractTestRestApi.httpDelete(("/notebook/cron/" + (note.getId())));
        Assert.assertThat("", deleteCron, isAllowed());
        deleteCron.releaseConnection();
        Thread.sleep(1000);
        System.clearProperty(ZEPPELIN_NOTEBOOK_CRON_FOLDERS.getVarName());
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testRegressionZEPPELIN_527() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testRegressionZEPPELIN_527", anonymous);
        note.setName("note for run test");
        Paragraph paragraph = note.addNewParagraph(ANONYMOUS);
        paragraph.setText("%spark\nval param = z.input(\"param\").toString\nprintln(param)");
        TestUtils.getInstance(Notebook.class).saveNote(note, anonymous);
        GetMethod getNoteJobs = AbstractTestRestApi.httpGet(("/notebook/job/" + (note.getId())));
        Assert.assertThat("test note jobs run:", getNoteJobs, isAllowed());
        Map<String, Object> resp = gson.fromJson(getNoteJobs.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        List<Map<String, String>> body = ((List<Map<String, String>>) (resp.get("body")));
        Assert.assertFalse(body.get(0).containsKey("started"));
        Assert.assertFalse(body.get(0).containsKey("finished"));
        getNoteJobs.releaseConnection();
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testInsertParagraph() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testInsertParagraph", anonymous);
        String jsonRequest = "{\"title\": \"title1\", \"text\": \"text1\"}";
        PostMethod post = AbstractTestRestApi.httpPost((("/notebook/" + (note.getId())) + "/paragraph"), jsonRequest);
        AbstractTestRestApi.LOG.info(("testInsertParagraph response\n" + (post.getResponseBodyAsString())));
        Assert.assertThat("Test insert method:", post, isAllowed());
        post.releaseConnection();
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        String newParagraphId = ((String) (resp.get("body")));
        AbstractTestRestApi.LOG.info(("newParagraphId:=" + newParagraphId));
        Note retrNote = TestUtils.getInstance(Notebook.class).getNote(note.getId());
        Paragraph newParagraph = retrNote.getParagraph(newParagraphId);
        Assert.assertNotNull("Can not find new paragraph by id", newParagraph);
        Assert.assertEquals("title1", newParagraph.getTitle());
        Assert.assertEquals("text1", newParagraph.getText());
        Paragraph lastParagraph = note.getLastParagraph();
        Assert.assertEquals(newParagraph.getId(), lastParagraph.getId());
        // insert to index 0
        String jsonRequest2 = "{\"index\": 0, \"title\": \"title2\", \"text\": \"text2\"}";
        PostMethod post2 = AbstractTestRestApi.httpPost((("/notebook/" + (note.getId())) + "/paragraph"), jsonRequest2);
        AbstractTestRestApi.LOG.info(("testInsertParagraph response2\n" + (post2.getResponseBodyAsString())));
        Assert.assertThat("Test insert method:", post2, isAllowed());
        post2.releaseConnection();
        Paragraph paragraphAtIdx0 = note.getParagraphs().get(0);
        Assert.assertEquals("title2", paragraphAtIdx0.getTitle());
        Assert.assertEquals("text2", paragraphAtIdx0.getText());
        // append paragraph providing graph
        String jsonRequest3 = "{\"title\": \"title3\", \"text\": \"text3\", " + ("\"config\": {\"colWidth\": 9.0, \"title\": true, " + "\"results\": [{\"graph\": {\"mode\": \"pieChart\"}}]}}");
        PostMethod post3 = AbstractTestRestApi.httpPost((("/notebook/" + (note.getId())) + "/paragraph"), jsonRequest3);
        AbstractTestRestApi.LOG.info(("testInsertParagraph response4\n" + (post3.getResponseBodyAsString())));
        Assert.assertThat("Test insert method:", post3, isAllowed());
        post3.releaseConnection();
        Paragraph p = note.getLastParagraph();
        Assert.assertEquals("title3", p.getTitle());
        Assert.assertEquals("text3", p.getText());
        Map result = ((List<Map>) (p.getConfig().get("results"))).get(0);
        String mode = ((Map) (result.get("graph"))).get("mode").toString();
        Assert.assertEquals("pieChart", mode);
        Assert.assertEquals(9.0, p.getConfig().get("colWidth"));
        Assert.assertTrue(((boolean) (p.getConfig().get("title"))));
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testUpdateParagraph() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testUpdateParagraph", anonymous);
        String jsonRequest = "{\"title\": \"title1\", \"text\": \"text1\"}";
        PostMethod post = AbstractTestRestApi.httpPost((("/notebook/" + (note.getId())) + "/paragraph"), jsonRequest);
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        post.releaseConnection();
        String newParagraphId = ((String) (resp.get("body")));
        Paragraph newParagraph = TestUtils.getInstance(Notebook.class).getNote(note.getId()).getParagraph(newParagraphId);
        Assert.assertEquals("title1", newParagraph.getTitle());
        Assert.assertEquals("text1", newParagraph.getText());
        String updateRequest = "{\"text\": \"updated text\"}";
        PutMethod put = AbstractTestRestApi.httpPut(((("/notebook/" + (note.getId())) + "/paragraph/") + newParagraphId), updateRequest);
        Assert.assertThat("Test update method:", put, isAllowed());
        put.releaseConnection();
        Paragraph updatedParagraph = TestUtils.getInstance(Notebook.class).getNote(note.getId()).getParagraph(newParagraphId);
        Assert.assertEquals("title1", updatedParagraph.getTitle());
        Assert.assertEquals("updated text", updatedParagraph.getText());
        String updateBothRequest = "{\"title\": \"updated title\", \"text\" : \"updated text 2\" }";
        PutMethod updatePut = AbstractTestRestApi.httpPut(((("/notebook/" + (note.getId())) + "/paragraph/") + newParagraphId), updateBothRequest);
        updatePut.releaseConnection();
        Paragraph updatedBothParagraph = TestUtils.getInstance(Notebook.class).getNote(note.getId()).getParagraph(newParagraphId);
        Assert.assertEquals("updated title", updatedBothParagraph.getTitle());
        Assert.assertEquals("updated text 2", updatedBothParagraph.getText());
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testGetParagraph() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testGetParagraph", anonymous);
        Paragraph p = note.addNewParagraph(ANONYMOUS);
        p.setTitle("hello");
        p.setText("world");
        TestUtils.getInstance(Notebook.class).saveNote(note, anonymous);
        GetMethod get = AbstractTestRestApi.httpGet(((("/notebook/" + (note.getId())) + "/paragraph/") + (p.getId())));
        AbstractTestRestApi.LOG.info(("testGetParagraph response\n" + (get.getResponseBodyAsString())));
        Assert.assertThat("Test get method: ", get, isAllowed());
        get.releaseConnection();
        Map<String, Object> resp = gson.fromJson(get.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertNotNull(resp);
        Assert.assertEquals("OK", resp.get("status"));
        Map<String, Object> body = ((Map<String, Object>) (resp.get("body")));
        Assert.assertEquals(p.getId(), body.get("id"));
        Assert.assertEquals("hello", body.get("title"));
        Assert.assertEquals("world", body.get("text"));
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testMoveParagraph() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testMoveParagraph", anonymous);
        Paragraph p = note.addNewParagraph(ANONYMOUS);
        p.setTitle("title1");
        p.setText("text1");
        Paragraph p2 = note.addNewParagraph(ANONYMOUS);
        p2.setTitle("title2");
        p2.setText("text2");
        TestUtils.getInstance(Notebook.class).saveNote(note, anonymous);
        PostMethod post = AbstractTestRestApi.httpPost(((((("/notebook/" + (note.getId())) + "/paragraph/") + (p2.getId())) + "/move/") + 0), "");
        Assert.assertThat("Test post method: ", post, isAllowed());
        post.releaseConnection();
        Note retrNote = TestUtils.getInstance(Notebook.class).getNote(note.getId());
        Paragraph paragraphAtIdx0 = retrNote.getParagraphs().get(0);
        Assert.assertEquals(p2.getId(), paragraphAtIdx0.getId());
        Assert.assertEquals(p2.getTitle(), paragraphAtIdx0.getTitle());
        Assert.assertEquals(p2.getText(), paragraphAtIdx0.getText());
        PostMethod post2 = AbstractTestRestApi.httpPost(((((("/notebook/" + (note.getId())) + "/paragraph/") + (p2.getId())) + "/move/") + 10), "");
        Assert.assertThat("Test post method: ", post2, isBadRequest());
        post.releaseConnection();
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testDeleteParagraph() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testDeleteParagraph", anonymous);
        Paragraph p = note.addNewParagraph(ANONYMOUS);
        p.setTitle("title1");
        p.setText("text1");
        TestUtils.getInstance(Notebook.class).saveNote(note, anonymous);
        DeleteMethod delete = AbstractTestRestApi.httpDelete(((("/notebook/" + (note.getId())) + "/paragraph/") + (p.getId())));
        Assert.assertThat("Test delete method: ", delete, isAllowed());
        delete.releaseConnection();
        Note retrNote = TestUtils.getInstance(Notebook.class).getNote(note.getId());
        Paragraph retrParagrah = retrNote.getParagraph(p.getId());
        Assert.assertNull("paragraph should be deleted", retrParagrah);
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testTitleSearch() throws IOException, InterruptedException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1_testTitleSearch", anonymous);
        String jsonRequest = "{\"title\": \"testTitleSearchOfParagraph\", " + "\"text\": \"ThisIsToTestSearchMethodWithTitle \"}";
        PostMethod postNoteText = AbstractTestRestApi.httpPost((("/notebook/" + (note.getId())) + "/paragraph"), jsonRequest);
        postNoteText.releaseConnection();
        Thread.sleep(1000);
        GetMethod searchNote = AbstractTestRestApi.httpGet("/notebook/search?q='testTitleSearchOfParagraph'");
        searchNote.addRequestHeader("Origin", "http://localhost");
        Map<String, Object> respSearchResult = gson.fromJson(searchNote.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        ArrayList searchBody = ((ArrayList) (respSearchResult.get("body")));
        int numberOfTitleHits = 0;
        for (int i = 0; i < (searchBody.size()); i++) {
            Map<String, String> searchResult = ((Map<String, String>) (searchBody.get(i)));
            if (searchResult.get("header").contains("testTitleSearchOfParagraph")) {
                numberOfTitleHits++;
            }
        }
        Assert.assertEquals("Paragraph title hits must be at-least one", true, (numberOfTitleHits >= 1));
        searchNote.releaseConnection();
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }
}

