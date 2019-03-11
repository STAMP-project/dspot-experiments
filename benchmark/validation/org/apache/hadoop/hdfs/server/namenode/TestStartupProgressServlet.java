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
package org.apache.hadoop.hdfs.server.namenode;


import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.hdfs.server.namenode.startupprogress.StartupProgress;
import org.apache.hadoop.hdfs.server.namenode.startupprogress.StartupProgressTestHelper;
import org.eclipse.jetty.util.ajax.JSON;
import org.junit.Assert;
import org.junit.Test;


public class TestStartupProgressServlet {
    private HttpServletRequest req;

    private HttpServletResponse resp;

    private ByteArrayOutputStream respOut;

    private StartupProgress startupProgress;

    private StartupProgressServlet servlet;

    @Test
    public void testInitialState() throws Exception {
        String respBody = doGetAndReturnResponseBody();
        Assert.assertNotNull(respBody);
        Map<String, Object> expected = ImmutableMap.<String, Object>builder().put("percentComplete", 0.0F).put("phases", Arrays.<Object>asList(ImmutableMap.<String, Object>builder().put("name", "LoadingFsImage").put("desc", "Loading fsimage").put("status", "PENDING").put("percentComplete", 0.0F).put("steps", Collections.emptyList()).build(), ImmutableMap.<String, Object>builder().put("name", "LoadingEdits").put("desc", "Loading edits").put("status", "PENDING").put("percentComplete", 0.0F).put("steps", Collections.emptyList()).build(), ImmutableMap.<String, Object>builder().put("name", "SavingCheckpoint").put("desc", "Saving checkpoint").put("status", "PENDING").put("percentComplete", 0.0F).put("steps", Collections.emptyList()).build(), ImmutableMap.<String, Object>builder().put("name", "SafeMode").put("desc", "Safe mode").put("status", "PENDING").put("percentComplete", 0.0F).put("steps", Collections.emptyList()).build())).build();
        Assert.assertEquals(JSON.toString(expected), filterJson(respBody));
    }

    @Test
    public void testRunningState() throws Exception {
        StartupProgressTestHelper.setStartupProgressForRunningState(startupProgress);
        String respBody = doGetAndReturnResponseBody();
        Assert.assertNotNull(respBody);
        Map<String, Object> expected = ImmutableMap.<String, Object>builder().put("percentComplete", 0.375F).put("phases", Arrays.<Object>asList(ImmutableMap.<String, Object>builder().put("name", "LoadingFsImage").put("desc", "Loading fsimage").put("status", "COMPLETE").put("percentComplete", 1.0F).put("steps", Collections.<Object>singletonList(ImmutableMap.<String, Object>builder().put("name", "Inodes").put("desc", "inodes").put("count", 100L).put("total", 100L).put("percentComplete", 1.0F).build())).build(), ImmutableMap.<String, Object>builder().put("name", "LoadingEdits").put("desc", "Loading edits").put("status", "RUNNING").put("percentComplete", 0.5F).put("steps", Collections.<Object>singletonList(ImmutableMap.<String, Object>builder().put("count", 100L).put("file", "file").put("size", 1000L).put("total", 200L).put("percentComplete", 0.5F).build())).build(), ImmutableMap.<String, Object>builder().put("name", "SavingCheckpoint").put("desc", "Saving checkpoint").put("status", "PENDING").put("percentComplete", 0.0F).put("steps", Collections.emptyList()).build(), ImmutableMap.<String, Object>builder().put("name", "SafeMode").put("desc", "Safe mode").put("status", "PENDING").put("percentComplete", 0.0F).put("steps", Collections.emptyList()).build())).build();
        Assert.assertEquals(JSON.toString(expected), filterJson(respBody));
    }

    @Test
    public void testFinalState() throws Exception {
        StartupProgressTestHelper.setStartupProgressForFinalState(startupProgress);
        String respBody = doGetAndReturnResponseBody();
        Assert.assertNotNull(respBody);
        Map<String, Object> expected = ImmutableMap.<String, Object>builder().put("percentComplete", 1.0F).put("phases", Arrays.<Object>asList(ImmutableMap.<String, Object>builder().put("name", "LoadingFsImage").put("desc", "Loading fsimage").put("status", "COMPLETE").put("percentComplete", 1.0F).put("steps", Collections.<Object>singletonList(ImmutableMap.<String, Object>builder().put("name", "Inodes").put("desc", "inodes").put("count", 100L).put("total", 100L).put("percentComplete", 1.0F).build())).build(), ImmutableMap.<String, Object>builder().put("name", "LoadingEdits").put("desc", "Loading edits").put("status", "COMPLETE").put("percentComplete", 1.0F).put("steps", Collections.<Object>singletonList(ImmutableMap.<String, Object>builder().put("count", 200L).put("file", "file").put("size", 1000L).put("total", 200L).put("percentComplete", 1.0F).build())).build(), ImmutableMap.<String, Object>builder().put("name", "SavingCheckpoint").put("desc", "Saving checkpoint").put("status", "COMPLETE").put("percentComplete", 1.0F).put("steps", Collections.<Object>singletonList(ImmutableMap.<String, Object>builder().put("name", "Inodes").put("desc", "inodes").put("count", 300L).put("total", 300L).put("percentComplete", 1.0F).build())).build(), ImmutableMap.<String, Object>builder().put("name", "SafeMode").put("desc", "Safe mode").put("status", "COMPLETE").put("percentComplete", 1.0F).put("steps", Collections.<Object>singletonList(ImmutableMap.<String, Object>builder().put("name", "AwaitingReportedBlocks").put("desc", "awaiting reported blocks").put("count", 400L).put("total", 400L).put("percentComplete", 1.0F).build())).build())).build();
        Assert.assertEquals(JSON.toString(expected), filterJson(respBody));
    }
}

