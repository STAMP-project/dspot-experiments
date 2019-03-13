/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.management;


import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.request.ExecuteScriptRequest;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.JsonUtil;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ExecuteScriptRequestTest extends HazelcastTestSupport {
    private ManagementCenterService managementCenterService;

    private String nodeAddressWithBrackets;

    private String nodeAddressWithoutBrackets;

    /**
     * Zulu 6 and 7 doesn't have Rhino script engine, so this test should be excluded.
     * see http://zulu.org/forum/thread/nullpointerexception-on-loading-the-javascript-engine-in-zulu-7-u76/
     */
    @Rule
    public ZuluExcludeRule zuluExcludeRule = new ZuluExcludeRule();

    @Test
    public void testExecuteScriptRequest() throws Exception {
        ExecuteScriptRequest request = new ExecuteScriptRequest("print('test');", "JavaScript", Collections.singleton(nodeAddressWithoutBrackets));
        JsonObject jsonObject = new JsonObject();
        request.writeResponse(managementCenterService, jsonObject);
        JsonObject result = ((JsonObject) (jsonObject.get("result")));
        JsonObject json = JsonUtil.getObject(result, nodeAddressWithBrackets);
        Assert.assertTrue(JsonUtil.getBoolean(json, "success"));
        Assert.assertEquals("error\n", JsonUtil.getString(json, "result"));
    }

    @Test
    public void testExecuteScriptRequest_noTargets() throws Exception {
        ExecuteScriptRequest request = new ExecuteScriptRequest("print('test');", "JavaScript", Collections.<String>emptySet());
        JsonObject jsonObject = new JsonObject();
        request.writeResponse(managementCenterService, jsonObject);
        JsonObject result = ((JsonObject) (jsonObject.get("result")));
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testExecuteScriptRequest_withIllegalScriptEngine() throws Exception {
        ExecuteScriptRequest request = new ExecuteScriptRequest("script", "engine", Collections.singleton(nodeAddressWithoutBrackets));
        JsonObject jsonObject = new JsonObject();
        request.writeResponse(managementCenterService, jsonObject);
        JsonObject result = ((JsonObject) (jsonObject.get("result")));
        JsonObject json = JsonUtil.getObject(result, nodeAddressWithBrackets);
        Assert.assertFalse(JsonUtil.getBoolean(json, "success"));
        Assert.assertNotNull(JsonUtil.getString(json, "result"));
        HazelcastTestSupport.assertContains(JsonUtil.getString(json, "stackTrace"), "IllegalArgumentException");
    }

    @Test
    public void testExecuteScriptRequest_withScriptException() throws Exception {
        ExecuteScriptRequest request = new ExecuteScriptRequest("print(;", "JavaScript", Collections.singleton(nodeAddressWithoutBrackets));
        JsonObject jsonObject = new JsonObject();
        request.writeResponse(managementCenterService, jsonObject);
        JsonObject result = ((JsonObject) (jsonObject.get("result")));
        JsonObject json = JsonUtil.getObject(result, nodeAddressWithBrackets);
        Assert.assertFalse(JsonUtil.getBoolean(json, "success"));
        Assert.assertNotNull(JsonUtil.getString(json, "result"));
        HazelcastTestSupport.assertContains(JsonUtil.getString(json, "stackTrace"), "ScriptException");
    }
}

