/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.view.pig.test;


import ScriptService.PigScriptRequest;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.ambari.view.pig.HDFSTest;
import org.apache.ambari.view.pig.resources.scripts.ScriptService;
import org.apache.ambari.view.pig.resources.scripts.models.PigScript;
import org.apache.ambari.view.pig.utils.NotFoundFormattedException;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ScriptTest extends HDFSTest {
    private ScriptService scriptService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createScript() {
        Response response = doCreateScript();
        Assert.assertEquals(201, response.getStatus());
        JSONObject obj = ((JSONObject) (response.getEntity()));
        Assert.assertTrue(obj.containsKey("script"));
        Assert.assertNotNull(getId());
        Assert.assertFalse(getId().isEmpty());
    }

    @Test
    public void createScriptAutoCreate() {
        Response response = doCreateScript("Test", null);
        Assert.assertEquals(201, response.getStatus());
        JSONObject obj = ((JSONObject) (response.getEntity()));
        Assert.assertTrue(obj.containsKey("script"));
        Assert.assertNotNull(getId());
        Assert.assertFalse(getId().isEmpty());
        Assert.assertFalse(getPigScript().isEmpty());
    }

    @Test
    public void scriptNotFound() {
        thrown.expect(NotFoundFormattedException.class);
        scriptService.getScript("4242");
    }

    @Test
    public void updateScript() {
        Response createdScript = doCreateScript();
        String createdScriptId = ((PigScript) (((JSONObject) (createdScript.getEntity())).get("script"))).getId();
        ScriptService.PigScriptRequest request = new ScriptService.PigScriptRequest();
        request.script = new PigScript();
        request.script.setTitle("Updated Script");
        Response response = scriptService.updateScript(request, createdScriptId);
        Assert.assertEquals(204, response.getStatus());
        Response response2 = scriptService.getScript(createdScriptId);
        Assert.assertEquals(200, response2.getStatus());
        JSONObject obj = ((JSONObject) (response2.getEntity()));
        Assert.assertTrue(obj.containsKey("script"));
        Assert.assertEquals(getTitle(), request.script.getTitle());
    }

    @Test
    public void deleteScript() {
        Response createdScript = doCreateScript();
        String createdScriptId = ((PigScript) (((JSONObject) (createdScript.getEntity())).get("script"))).getId();
        Response response = scriptService.deleteScript(createdScriptId);
        Assert.assertEquals(204, response.getStatus());
        thrown.expect(NotFoundFormattedException.class);
        scriptService.getScript(createdScriptId);
    }

    @Test
    public void listScripts() {
        Response createdScript1 = doCreateScript("Title 1", "/path/to/file.pig");
        Response createdScript2 = doCreateScript("Title 2", "/path/to/file.pig");
        String createdScriptId = ((PigScript) (((JSONObject) (createdScript1.getEntity())).get("script"))).getId();
        Response response = scriptService.getScriptList();
        Assert.assertEquals(200, response.getStatus());
        JSONObject obj = ((JSONObject) (response.getEntity()));
        Assert.assertTrue(obj.containsKey("scripts"));
        List<PigScript> scripts = ((List<PigScript>) (obj.get("scripts")));
        boolean containsTitle = false;
        for (PigScript script : scripts)
            containsTitle = containsTitle || ((script.getTitle().compareTo("Title 1")) == 0);

        Assert.assertTrue(containsTitle);
        containsTitle = false;
        for (PigScript script : scripts)
            containsTitle = containsTitle || ((script.getTitle().compareTo("Title 2")) == 0);

        Assert.assertTrue(containsTitle);
    }
}

