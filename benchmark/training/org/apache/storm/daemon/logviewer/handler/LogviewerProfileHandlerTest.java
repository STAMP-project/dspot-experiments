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


public class LogviewerProfileHandlerTest {
    @Test
    public void testListDumpFiles() throws Exception {
        try (TmpPath rootPath = new TmpPath()) {
            LogviewerProfileHandler handler = createHandlerTraversalTests(rootPath.getFile().toPath());
            Response topoAResponse = handler.listDumpFiles("topoA", "localhost:1111", "user");
            Response topoBResponse = handler.listDumpFiles("topoB", "localhost:1111", "user");
            Utils.forceDelete(rootPath.toString());
            Assert.assertThat(topoAResponse.getStatus(), CoreMatchers.is(OK.getStatusCode()));
            String contentA = ((String) (topoAResponse.getEntity()));
            Assert.assertThat(contentA, CoreMatchers.containsString("worker.jfr"));
            Assert.assertThat(contentA, CoreMatchers.not(CoreMatchers.containsString("worker.bin")));
            Assert.assertThat(contentA, CoreMatchers.not(CoreMatchers.containsString("worker.txt")));
            String contentB = ((String) (topoBResponse.getEntity()));
            Assert.assertThat(contentB, CoreMatchers.containsString("worker.txt"));
            Assert.assertThat(contentB, CoreMatchers.not(CoreMatchers.containsString("worker.jfr")));
            Assert.assertThat(contentB, CoreMatchers.not(CoreMatchers.containsString("worker.bin")));
        }
    }

    @Test
    public void testListDumpFilesTraversalInTopoId() throws Exception {
        try (TmpPath rootPath = new TmpPath()) {
            LogviewerProfileHandler handler = createHandlerTraversalTests(rootPath.getFile().toPath());
            Response response = handler.listDumpFiles("../../", "localhost:logs", "user");
            Utils.forceDelete(rootPath.toString());
            Assert.assertThat(response.getStatus(), CoreMatchers.is(NOT_FOUND.getStatusCode()));
        }
    }

    @Test
    public void testListDumpFilesTraversalInPort() throws Exception {
        try (TmpPath rootPath = new TmpPath()) {
            LogviewerProfileHandler handler = createHandlerTraversalTests(rootPath.getFile().toPath());
            Response response = handler.listDumpFiles("../", "localhost:../logs", "user");
            Utils.forceDelete(rootPath.toString());
            Assert.assertThat(response.getStatus(), CoreMatchers.is(NOT_FOUND.getStatusCode()));
        }
    }

    @Test
    public void testDownloadDumpFile() throws IOException {
        try (TmpPath rootPath = new TmpPath()) {
            LogviewerProfileHandler handler = createHandlerTraversalTests(rootPath.getFile().toPath());
            Response topoAResponse = handler.downloadDumpFile("topoA", "localhost:1111", "worker.jfr", "user");
            Response topoBResponse = handler.downloadDumpFile("topoB", "localhost:1111", "worker.txt", "user");
            Utils.forceDelete(rootPath.toString());
            Assert.assertThat(topoAResponse.getStatus(), CoreMatchers.is(OK.getStatusCode()));
            Assert.assertThat(topoAResponse.getEntity(), CoreMatchers.not(CoreMatchers.nullValue()));
            Assert.assertThat(topoBResponse.getStatus(), CoreMatchers.is(OK.getStatusCode()));
            Assert.assertThat(topoBResponse.getEntity(), CoreMatchers.not(CoreMatchers.nullValue()));
        }
    }

    @Test
    public void testDownloadDumpFileTraversalInTopoId() throws IOException {
        try (TmpPath rootPath = new TmpPath()) {
            LogviewerProfileHandler handler = createHandlerTraversalTests(rootPath.getFile().toPath());
            Response topoAResponse = handler.downloadDumpFile("../../", "localhost:logs", "daemon-dump.bin", "user");
            Utils.forceDelete(rootPath.toString());
            Assert.assertThat(topoAResponse.getStatus(), CoreMatchers.is(NOT_FOUND.getStatusCode()));
        }
    }

    @Test
    public void testDownloadDumpFileTraversalInPort() throws IOException {
        try (TmpPath rootPath = new TmpPath()) {
            LogviewerProfileHandler handler = createHandlerTraversalTests(rootPath.getFile().toPath());
            Response topoAResponse = handler.downloadDumpFile("../", "localhost:../logs", "daemon-dump.bin", "user");
            Utils.forceDelete(rootPath.toString());
            Assert.assertThat(topoAResponse.getStatus(), CoreMatchers.is(NOT_FOUND.getStatusCode()));
        }
    }
}

