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


import com.hazelcast.core.LifecycleService;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.request.ConsoleCommandRequest;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConsoleCommandRequestTest extends HazelcastTestSupport {
    private ManagementCenterService managementCenterService;

    private LifecycleService lifecycleService;

    @Test
    public void testConsoleCommand_exitNotAllowed() throws Exception {
        String[] forbiddenCommands = new String[]{ "exit", "quit", "shutdown", "EXIT", "ExiT", "QUIT", "SHUTDOWN", "#1 shutdown", "#3 exit", "&2 quit", "echo 1;exit; echo 2;" };
        for (String command : forbiddenCommands) {
            JsonObject requestJson = new JsonObject();
            requestJson.add("command", command);
            ConsoleCommandRequest consoleCommandRequest = new ConsoleCommandRequest();
            consoleCommandRequest.fromJson(requestJson);
            JsonObject responseJson = new JsonObject();
            consoleCommandRequest.writeResponse(managementCenterService, responseJson);
            HazelcastTestSupport.assertContains(JsonUtil.getString(JsonUtil.getObject(responseJson, "result"), "output"), "is not allowed!");
            Assert.assertTrue(lifecycleService.isRunning());
        }
    }
}

