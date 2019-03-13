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
package org.apache.beam.runners.dataflow.worker.status;


import org.apache.beam.runners.dataflow.worker.util.MemoryMonitor;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for {@link WorkerStatusPages}.
 */
@RunWith(JUnit4.class)
public class WorkerStatusPagesTest {
    private final Server server = new Server();

    private final LocalConnector connector = new LocalConnector(server);

    @Mock
    private MemoryMonitor mockMemoryMonitor;

    private WorkerStatusPages wsp;

    @Test
    public void testThreadz() throws Exception {
        String response = getPage("/threadz");
        Assert.assertThat(response, Matchers.containsString("HTTP/1.1 200 OK"));
        Assert.assertThat("Test method should appear in stack trace", response, Matchers.containsString("WorkerStatusPagesTest.testThreadz"));
    }

    @Test
    public void testHealthz() throws Exception {
        String response = getPage("/threadz");
        Assert.assertThat(response, Matchers.containsString("HTTP/1.1 200 OK"));
        Assert.assertThat(response, Matchers.containsString("ok"));
    }

    @Test
    public void testUnknownHandler() throws Exception {
        String response = getPage("/missinghandlerz");
        Assert.assertThat(response, Matchers.containsString("HTTP/1.1 302 Found"));
        Assert.assertThat(response, Matchers.containsString("Location: http://localhost/statusz"));
    }
}

