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
package org.apache.camel.component.etcd;


import EtcdConstants.ETCD_NAMESPACE;
import EtcdConstants.ETCD_PATH;
import EtcdConstants.ETCD_TIMEOUT;
import EtcdErrorCode.KeyNotFound;
import EtcdNamespace.watch;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdException;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class EtcdWatchTest extends EtcdTestSupport {
    @Test
    public void testWatchWithPath() throws Exception {
        testWatch("mock:watch-with-path", "/myKey1", 10);
    }

    @Test
    public void testWatchWithConfigPath() throws Exception {
        testWatch("mock:watch-with-config-path", "/myKey2", 10);
    }

    @Test
    public void testWatchRecursive() throws Exception {
        testWatch("mock:watch-recursive", "/recursive/myKey1", 10);
    }

    @Test
    public void testWatchRecovery() throws Exception {
        final String key = "/myKeyRecovery";
        final EtcdClient client = getClient();
        try {
            // Delete the key if present
            client.delete(key).send().get();
        } catch (EtcdException e) {
            if (!(e.isErrorCode(KeyNotFound))) {
                throw e;
            }
        }
        // Fill the vent backlog ( > 1000)
        for (int i = 0; i < 2000; i++) {
            client.put(key, ("v" + i)).send().get();
        }
        context().getRouteController().startRoute("watchRecovery");
        testWatch("mock:watch-recovery", key, 10);
    }

    @Test
    public void testWatchWithTimeout() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:watch-with-timeout");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(ETCD_NAMESPACE, watch.name());
        mock.expectedHeaderReceived(ETCD_PATH, "/timeoutKey");
        mock.expectedHeaderReceived(ETCD_TIMEOUT, true);
        mock.allMessages().body().isNull();
        mock.assertIsSatisfied();
    }
}

