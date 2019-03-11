/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.storm.daemon.logviewer.handler;


import Response.Status.NOT_FOUND;
import Response.Status.OK;
import java.io.IOException;
import javax.ws.rs.core.Response;
import org.apache.storm.testing.TmpPath;
import org.apache.storm.utils.Utils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class LogviewerLogDownloadHandlerTest {
    @Test
    public void testDownloadLogFile() throws IOException {
        try (TmpPath rootPath = new TmpPath()) {
            LogviewerLogDownloadHandler handler = createHandlerTraversalTests(rootPath.getFile().toPath());
            Response topoAResponse = handler.downloadLogFile("topoA/1111/worker.log", "user");
            Response topoBResponse = handler.downloadLogFile("topoB/1111/worker.log", "user");
            Utils.forceDelete(rootPath.toString());
            Assert.assertThat(topoAResponse.getStatus(), CoreMatchers.is(OK.getStatusCode()));
            Assert.assertThat(topoAResponse.getEntity(), CoreMatchers.not(CoreMatchers.nullValue()));
            Assert.assertThat(topoBResponse.getStatus(), CoreMatchers.is(OK.getStatusCode()));
            Assert.assertThat(topoBResponse.getEntity(), CoreMatchers.not(CoreMatchers.nullValue()));
        }
    }

    @Test
    public void testDownloadLogFileTraversal() throws IOException {
        try (TmpPath rootPath = new TmpPath()) {
            LogviewerLogDownloadHandler handler = createHandlerTraversalTests(rootPath.getFile().toPath());
            Response topoAResponse = handler.downloadLogFile("../nimbus.log", "user");
            Utils.forceDelete(rootPath.toString());
            Assert.assertThat(topoAResponse.getStatus(), CoreMatchers.is(NOT_FOUND.getStatusCode()));
        }
    }

    @Test
    public void testDownloadDaemonLogFile() throws IOException {
        try (TmpPath rootPath = new TmpPath()) {
            LogviewerLogDownloadHandler handler = createHandlerTraversalTests(rootPath.getFile().toPath());
            Response response = handler.downloadDaemonLogFile("nimbus.log", "user");
            Utils.forceDelete(rootPath.toString());
            Assert.assertThat(response.getStatus(), CoreMatchers.is(OK.getStatusCode()));
            Assert.assertThat(response.getEntity(), CoreMatchers.not(CoreMatchers.nullValue()));
        }
    }

    @Test
    public void testDownloadDaemonLogFilePathIntoWorkerLogs() throws IOException {
        try (TmpPath rootPath = new TmpPath()) {
            LogviewerLogDownloadHandler handler = createHandlerTraversalTests(rootPath.getFile().toPath());
            Response response = handler.downloadDaemonLogFile("workers-artifacts/topoA/1111/worker.log", "user");
            Utils.forceDelete(rootPath.toString());
            Assert.assertThat(response.getStatus(), CoreMatchers.is(NOT_FOUND.getStatusCode()));
        }
    }

    @Test
    public void testDownloadDaemonLogFilePathOutsideLogRoot() throws IOException {
        try (TmpPath rootPath = new TmpPath()) {
            LogviewerLogDownloadHandler handler = createHandlerTraversalTests(rootPath.getFile().toPath());
            Response response = handler.downloadDaemonLogFile("../evil.sh", "user");
            Utils.forceDelete(rootPath.toString());
            Assert.assertThat(response.getStatus(), CoreMatchers.is(NOT_FOUND.getStatusCode()));
        }
    }
}

