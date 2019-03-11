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
import InterpreterOption.ISOLATED;
import InterpreterOption.SCOPED;
import InterpreterOption.SHARED;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.zeppelin.interpreter.InterpreterSetting;
import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.Notebook;
import org.apache.zeppelin.notebook.Paragraph;
import org.apache.zeppelin.scheduler.Job.Status;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.apache.zeppelin.utils.TestUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Zeppelin interpreter rest api tests.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InterpreterRestApiTest extends AbstractTestRestApi {
    private Gson gson = new Gson();

    private AuthenticationInfo anonymous;

    @Test
    public void getAvailableInterpreters() throws IOException {
        // when
        GetMethod get = AbstractTestRestApi.httpGet("/interpreter");
        JsonObject body = getBodyFieldFromResponse(get.getResponseBodyAsString());
        // then
        MatcherAssert.assertThat(get, isAllowed());
        Assert.assertEquals(TestUtils.getInstance(Notebook.class).getInterpreterSettingManager().getInterpreterSettingTemplates().size(), body.entrySet().size());
        get.releaseConnection();
    }

    @Test
    public void getSettings() throws IOException {
        // when
        GetMethod get = AbstractTestRestApi.httpGet("/interpreter/setting");
        // then
        MatcherAssert.assertThat(get, isAllowed());
        // DO NOT REMOVE: implies that body is properly parsed as an array
        JsonArray body = getArrayBodyFieldFromResponse(get.getResponseBodyAsString());
        get.releaseConnection();
    }

    @Test
    public void testGetNonExistInterpreterSetting() throws IOException {
        // when
        String nonExistInterpreterSettingId = "apache_.zeppelin_1s_.aw3some$";
        GetMethod get = AbstractTestRestApi.httpGet(("/interpreter/setting/" + nonExistInterpreterSettingId));
        // then
        MatcherAssert.assertThat("Test get method:", get, isNotFound());
        get.releaseConnection();
    }

    @Test
    public void testSettingsCRUD() throws IOException {
        // when: call create setting API
        String rawRequest = "{\"name\":\"md3\",\"group\":\"md\"," + (((("\"properties\":{\"propname\": {\"value\": \"propvalue\", \"name\": \"propname\", " + "\"type\": \"textarea\"}},") + "\"interpreterGroup\":[{\"class\":\"org.apache.zeppelin.markdown.Markdown\",") + "\"name\":\"md\"}],\"dependencies\":[],") + "\"option\": { \"remote\": true, \"session\": false }}");
        JsonObject jsonRequest = gson.fromJson(rawRequest, JsonElement.class).getAsJsonObject();
        PostMethod post = AbstractTestRestApi.httpPost("/interpreter/setting/", jsonRequest.toString());
        String postResponse = post.getResponseBodyAsString();
        AbstractTestRestApi.LOG.info(("testSettingCRUD create response\n" + (post.getResponseBodyAsString())));
        InterpreterSetting created = convertResponseToInterpreterSetting(postResponse);
        String newSettingId = created.getId();
        // then : call create setting API
        MatcherAssert.assertThat("test create method:", post, isAllowed());
        post.releaseConnection();
        // when: call read setting API
        GetMethod get = AbstractTestRestApi.httpGet(("/interpreter/setting/" + newSettingId));
        String getResponse = get.getResponseBodyAsString();
        AbstractTestRestApi.LOG.info(("testSettingCRUD get response\n" + getResponse));
        InterpreterSetting previouslyCreated = convertResponseToInterpreterSetting(getResponse);
        // then : read Setting API
        MatcherAssert.assertThat("Test get method:", get, isAllowed());
        Assert.assertEquals(newSettingId, previouslyCreated.getId());
        get.releaseConnection();
        // when: call update setting API
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "propname2");
        jsonObject.addProperty("value", "this is new prop");
        jsonObject.addProperty("type", "textarea");
        jsonRequest.getAsJsonObject("properties").add("propname2", jsonObject);
        PutMethod put = AbstractTestRestApi.httpPut(("/interpreter/setting/" + newSettingId), jsonRequest.toString());
        AbstractTestRestApi.LOG.info(("testSettingCRUD update response\n" + (put.getResponseBodyAsString())));
        // then: call update setting API
        MatcherAssert.assertThat("test update method:", put, isAllowed());
        put.releaseConnection();
        // when: call delete setting API
        DeleteMethod delete = AbstractTestRestApi.httpDelete(("/interpreter/setting/" + newSettingId));
        AbstractTestRestApi.LOG.info(("testSettingCRUD delete response\n" + (delete.getResponseBodyAsString())));
        // then: call delete setting API
        MatcherAssert.assertThat("Test delete method:", delete, isAllowed());
        delete.releaseConnection();
    }

    @Test
    public void testCreatedInterpreterDependencies() throws IOException {
        // when: Create 2 interpreter settings `md1` and `md2` which have different dep.
        String md1Name = "md1";
        String md2Name = "md2";
        String md1Dep = "org.apache.drill.exec:drill-jdbc:jar:1.7.0";
        String md2Dep = "org.apache.drill.exec:drill-jdbc:jar:1.6.0";
        String reqBody1 = (((((((((((("{\"name\":\"" + md1Name) + "\",\"group\":\"md\",") + "\"properties\":{\"propname\": {\"value\": \"propvalue\", \"name\": \"propname\", ") + "\"type\": \"textarea\"}},") + "\"interpreterGroup\":[{\"class\":\"org.apache.zeppelin.markdown.Markdown\",") + "\"name\":\"md\"}],") + "\"dependencies\":[ {\n") + "      \"groupArtifactVersion\": \"") + md1Dep) + "\",\n") + "      \"exclusions\":[]\n") + "    }],") + "\"option\": { \"remote\": true, \"session\": false }}";
        PostMethod post = AbstractTestRestApi.httpPost("/interpreter/setting", reqBody1);
        MatcherAssert.assertThat("test create method:", post, isAllowed());
        post.releaseConnection();
        String reqBody2 = (((((((((((("{\"name\":\"" + md2Name) + "\",\"group\":\"md\",") + "\"properties\": {\"propname\": {\"value\": \"propvalue\", \"name\": \"propname\", ") + "\"type\": \"textarea\"}},") + "\"interpreterGroup\":[{\"class\":\"org.apache.zeppelin.markdown.Markdown\",") + "\"name\":\"md\"}],") + "\"dependencies\":[ {\n") + "      \"groupArtifactVersion\": \"") + md2Dep) + "\",\n") + "      \"exclusions\":[]\n") + "    }],") + "\"option\": { \"remote\": true, \"session\": false }}";
        post = AbstractTestRestApi.httpPost("/interpreter/setting", reqBody2);
        MatcherAssert.assertThat("test create method:", post, isAllowed());
        post.releaseConnection();
        // 1. Call settings API
        GetMethod get = AbstractTestRestApi.httpGet("/interpreter/setting");
        String rawResponse = get.getResponseBodyAsString();
        get.releaseConnection();
        // 2. Parsing to List<InterpreterSettings>
        JsonObject responseJson = gson.fromJson(rawResponse, JsonElement.class).getAsJsonObject();
        JsonArray bodyArr = responseJson.getAsJsonArray("body");
        List<InterpreterSetting> settings = new Gson().fromJson(bodyArr, new TypeToken<ArrayList<InterpreterSetting>>() {}.getType());
        // 3. Filter interpreters out we have just created
        InterpreterSetting md1 = null;
        InterpreterSetting md2 = null;
        for (InterpreterSetting setting : settings) {
            if (md1Name.equals(setting.getName())) {
                md1 = setting;
            } else
                if (md2Name.equals(setting.getName())) {
                    md2 = setting;
                }

        }
        // then: should get created interpreters which have different dependencies
        // 4. Validate each md interpreter has its own dependencies
        Assert.assertEquals(1, md1.getDependencies().size());
        Assert.assertEquals(1, md2.getDependencies().size());
        Assert.assertEquals(md1Dep, md1.getDependencies().get(0).getGroupArtifactVersion());
        Assert.assertEquals(md2Dep, md2.getDependencies().get(0).getGroupArtifactVersion());
    }

    @Test
    public void testSettingsCreateWithEmptyJson() throws IOException {
        // Call Create Setting REST API
        PostMethod post = AbstractTestRestApi.httpPost("/interpreter/setting/", "");
        AbstractTestRestApi.LOG.info(("testSettingCRUD create response\n" + (post.getResponseBodyAsString())));
        MatcherAssert.assertThat("test create method:", post, isBadRequest());
        post.releaseConnection();
    }

    @Test
    public void testRestartInterpreterPerNote() throws IOException, InterruptedException {
        // when: create new note
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        note.addNewParagraph(ANONYMOUS);
        Paragraph p = note.getLastParagraph();
        Map config = p.getConfig();
        config.put("enabled", true);
        // when: run markdown paragraph.
        p.setConfig(config);
        p.setText("%md markdown");
        p.setAuthenticationInfo(anonymous);
        note.run(p.getId());
        while ((p.getStatus()) != (Status.FINISHED)) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(p.getReturn().message().get(0).getData(), InterpreterRestApiTest.getSimulatedMarkdownResult("markdown"));
        // when: get md interpreter
        InterpreterSetting mdIntpSetting = null;
        for (InterpreterSetting setting : TestUtils.getInstance(Notebook.class).getInterpreterSettingManager().getInterpreterSettings(note.getId())) {
            if (setting.getName().equals("md")) {
                mdIntpSetting = setting;
                break;
            }
        }
        String jsonRequest = ("{\"noteId\":\"" + (note.getId())) + "\"}";
        // Restart isolated mode of Interpreter for note.
        mdIntpSetting.getOption().setPerNote(ISOLATED);
        PutMethod put = AbstractTestRestApi.httpPut(("/interpreter/setting/restart/" + (mdIntpSetting.getId())), jsonRequest);
        MatcherAssert.assertThat("isolated interpreter restart:", put, isAllowed());
        put.releaseConnection();
        // Restart scoped mode of Interpreter for note.
        mdIntpSetting.getOption().setPerNote(SCOPED);
        put = AbstractTestRestApi.httpPut(("/interpreter/setting/restart/" + (mdIntpSetting.getId())), jsonRequest);
        MatcherAssert.assertThat("scoped interpreter restart:", put, isAllowed());
        put.releaseConnection();
        // Restart shared mode of Interpreter for note.
        mdIntpSetting.getOption().setPerNote(SHARED);
        put = AbstractTestRestApi.httpPut(("/interpreter/setting/restart/" + (mdIntpSetting.getId())), jsonRequest);
        MatcherAssert.assertThat("shared interpreter restart:", put, isAllowed());
        put.releaseConnection();
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testListRepository() throws IOException {
        GetMethod get = AbstractTestRestApi.httpGet("/interpreter/repository");
        MatcherAssert.assertThat(get, isAllowed());
        get.releaseConnection();
    }

    @Test
    public void testAddDeleteRepository() throws IOException {
        // Call create repository API
        String repoId = "securecentral";
        String jsonRequest = ("{\"id\":\"" + repoId) + "\",\"url\":\"https://repo1.maven.org/maven2\",\"snapshot\":\"false\"}";
        PostMethod post = AbstractTestRestApi.httpPost("/interpreter/repository/", jsonRequest);
        MatcherAssert.assertThat("Test create method:", post, isAllowed());
        post.releaseConnection();
        // Call delete repository API
        DeleteMethod delete = AbstractTestRestApi.httpDelete(("/interpreter/repository/" + repoId));
        MatcherAssert.assertThat("Test delete method:", delete, isAllowed());
        delete.releaseConnection();
    }
}

