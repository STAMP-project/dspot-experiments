/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.box;


import BoxTask.Action.REVIEW;
import BoxTask.Info;
import com.box.sdk.BoxTask;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.component.box.internal.BoxApiCollection;
import org.apache.camel.component.box.internal.BoxTasksManagerApiMethod;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link BoxTasksManager}
 * APIs.
 */
public class BoxTasksManagerIntegrationTest extends AbstractBoxTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BoxTasksManagerIntegrationTest.class);

    private static final String PATH_PREFIX = BoxApiCollection.getCollection().getApiName(BoxTasksManagerApiMethod.class).getName();

    private static final String CAMEL_TEST_FILE = "/CamelTestFile.txt";

    private static final String CAMEL_TEST_FILE_NAME = "CamelTestFile.txt";

    private static final String CAMEL_TEST_MESSAGE = "Camel Test Message";

    private static final long TEN_MINUTES_IN_MILLIS = 600000;

    private BoxTask testTask;

    @Test
    public void testAddAssignmentToTask() throws Exception {
        BoxTask result = null;
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.taskId", testTask.getID());
        // parameter type is com.box.sdk.BoxUser
        headers.put("CamelBox.assignTo", getCurrentUser());
        result = requestBodyAndHeaders("direct://ADDASSIGNMENTTOTASK", null, headers);
        assertNotNull("addAssignmentToTask result", result);
        BoxTasksManagerIntegrationTest.LOG.debug(("addAssignmentToTask: " + result));
    }

    @Test
    public void testAddFileTask() throws Exception {
        BoxTask result = null;
        try {
            final Map<String, Object> headers = new HashMap<>();
            // parameter type is String
            headers.put("CamelBox.fileId", testFile.getID());
            // parameter type is com.box.sdk.BoxTask.Action
            headers.put("CamelBox.action", REVIEW);
            // parameter type is java.util.Date
            Date now = new Date();
            Date dueAt = new Date(((now.getTime()) + (BoxTasksManagerIntegrationTest.TEN_MINUTES_IN_MILLIS)));
            headers.put("CamelBox.dueAt", dueAt);
            // parameter type is String
            headers.put("CamelBox.message", BoxTasksManagerIntegrationTest.CAMEL_TEST_MESSAGE);
            result = requestBodyAndHeaders("direct://ADDFILETASK", null, headers);
            assertNotNull("addFileTask result", result);
            BoxTasksManagerIntegrationTest.LOG.debug(("addFileTask: " + result));
        } finally {
            if (result != null) {
                try {
                    result.delete();
                } catch (Throwable t) {
                }
            }
        }
    }

    @Test
    public void testDeleteTask() throws Exception {
        // using String message body for single parameter "taskId"
        requestBody("direct://DELETETASK", testTask.getID());
        List<BoxTask.Info> tasks = testFile.getTasks();
        boolean exists = (tasks.size()) != 0;
        assertEquals("deleteTask task still exists.", false, exists);
    }

    @Test
    public void testGetFileTasks() throws Exception {
        // using String message body for single parameter "fileId"
        @SuppressWarnings("rawtypes")
        final List result = requestBody("direct://GETFILETASKS", testFile.getID());
        assertNotNull("getFileTasks result", result);
        BoxTasksManagerIntegrationTest.LOG.debug(("getFileTasks: " + result));
    }

    @Test
    public void testGetTaskAssignments() throws Exception {
        // using String message body for single parameter "taskId"
        @SuppressWarnings("rawtypes")
        final List result = requestBody("direct://GETTASKASSIGNMENTS", testTask.getID());
        assertNotNull("getTaskAssignments result", result);
        BoxTasksManagerIntegrationTest.LOG.debug(("getTaskAssignments: " + result));
    }

    @Test
    public void testGetTaskInfo() throws Exception {
        // using String message body for single parameter "taskId"
        final BoxTask.Info result = requestBody("direct://GETTASKINFO", testTask.getID());
        assertNotNull("getTaskInfo result", result);
        BoxTasksManagerIntegrationTest.LOG.debug(("getTaskInfo: " + result));
    }
}

