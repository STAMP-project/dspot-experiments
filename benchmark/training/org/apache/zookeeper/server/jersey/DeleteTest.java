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
package org.apache.zookeeper.server.jersey;


import ClientResponse.Status;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_OCTET_STREAM;
import MediaType.APPLICATION_XML;
import com.sun.jersey.api.client.ClientResponse;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test stand-alone server.
 */
@RunWith(Parameterized.class)
public class DeleteTest extends Base {
    protected static final Logger LOG = LoggerFactory.getLogger(DeleteTest.class);

    private String zpath;

    private Status expectedStatus;

    public static class MyWatcher implements Watcher {
        public void process(WatchedEvent event) {
            // FIXME ignore for now
        }
    }

    public DeleteTest(String path, String zpath, ClientResponse.Status status) {
        this.zpath = zpath;
        this.expectedStatus = status;
    }

    @Test
    public void testDelete() throws Exception {
        verify(APPLICATION_OCTET_STREAM);
        verify(APPLICATION_JSON);
        verify(APPLICATION_XML);
    }
}

