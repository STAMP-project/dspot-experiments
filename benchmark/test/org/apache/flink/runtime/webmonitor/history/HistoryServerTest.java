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
package org.apache.flink.runtime.webmonitor.history;


import HistoryServerOptions.HISTORY_SERVER_ARCHIVE_DIRS;
import HistoryServerOptions.HISTORY_SERVER_WEB_DIR;
import HistoryServerOptions.HISTORY_SERVER_WEB_PORT;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.rest.messages.JobsOverviewHeaders;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonGenerator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the HistoryServer.
 */
public class HistoryServerTest extends TestLogger {
    @ClassRule
    public static final TemporaryFolder TMP = new TemporaryFolder();

    private MiniClusterWithClientResource cluster;

    private File jmDirectory;

    private File hsDirectory;

    @Test
    public void testHistoryServerIntegration() throws Exception {
        final int numJobs = 2;
        for (int x = 0; x < numJobs; x++) {
            HistoryServerTest.runJob();
        }
        HistoryServerTest.createLegacyArchive(jmDirectory.toPath());
        CountDownLatch numFinishedPolls = new CountDownLatch(1);
        Configuration historyServerConfig = new Configuration();
        historyServerConfig.setString(HISTORY_SERVER_ARCHIVE_DIRS, jmDirectory.toURI().toString());
        historyServerConfig.setString(HISTORY_SERVER_WEB_DIR, hsDirectory.getAbsolutePath());
        historyServerConfig.setInteger(HISTORY_SERVER_WEB_PORT, 0);
        // the job is archived asynchronously after env.execute() returns
        File[] archives = jmDirectory.listFiles();
        while ((archives == null) || ((archives.length) != (numJobs + 1))) {
            Thread.sleep(50);
            archives = jmDirectory.listFiles();
        } 
        HistoryServer hs = new HistoryServer(historyServerConfig, numFinishedPolls);
        try {
            hs.start();
            String baseUrl = "http://localhost:" + (hs.getWebPort());
            numFinishedPolls.await(10L, TimeUnit.SECONDS);
            ObjectMapper mapper = new ObjectMapper();
            String response = HistoryServerTest.getFromHTTP((baseUrl + (JobsOverviewHeaders.URL)));
            MultipleJobsDetails overview = mapper.readValue(response, MultipleJobsDetails.class);
            Assert.assertEquals((numJobs + 1), overview.getJobs().size());
        } finally {
            hs.stop();
        }
    }

    private static final class JsonObject implements AutoCloseable {
        private final JsonGenerator gen;

        JsonObject(JsonGenerator gen) throws IOException {
            this.gen = gen;
            gen.writeStartObject();
        }

        private JsonObject(JsonGenerator gen, String name) throws IOException {
            this.gen = gen;
            gen.writeObjectFieldStart(name);
        }

        @Override
        public void close() throws IOException {
            gen.writeEndObject();
        }
    }

    private static final class JsonArray implements AutoCloseable {
        private final JsonGenerator gen;

        JsonArray(JsonGenerator gen, String name) throws IOException {
            this.gen = gen;
            gen.writeArrayFieldStart(name);
        }

        @Override
        public void close() throws IOException {
            gen.writeEndArray();
        }
    }
}

