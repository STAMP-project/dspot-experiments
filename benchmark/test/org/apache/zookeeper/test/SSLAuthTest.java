/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.test;


import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.TestableZooKeeper;
import org.apache.zookeeper.common.ClientX509Util;
import org.junit.Assert;
import org.junit.Test;


public class SSLAuthTest extends ClientBase {
    private ClientX509Util clientX509Util;

    @Test
    public void testRejection() throws Exception {
        String testDataPath = System.getProperty("test.data.dir", "src/test/resources/data");
        // Replace trusted keys with a valid key that is not trusted by the server
        System.setProperty(clientX509Util.getSslKeystoreLocationProperty(), (testDataPath + "/ssl/testUntrustedKeyStore.jks"));
        System.setProperty(clientX509Util.getSslKeystorePasswdProperty(), "testpass");
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        // Handshake will take place, and then X509AuthenticationProvider should reject the untrusted cert
        new TestableZooKeeper(hostPort, ClientBase.CONNECTION_TIMEOUT, watcher);
        Assert.assertFalse("Untrusted certificate should not result in successful connection", watcher.clientConnected.await(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testMisconfiguration() throws Exception {
        System.clearProperty(clientX509Util.getSslAuthProviderProperty());
        System.clearProperty(clientX509Util.getSslKeystoreLocationProperty());
        System.clearProperty(clientX509Util.getSslKeystorePasswdProperty());
        System.clearProperty(clientX509Util.getSslTruststoreLocationProperty());
        System.clearProperty(clientX509Util.getSslTruststorePasswdProperty());
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        new TestableZooKeeper(hostPort, ClientBase.CONNECTION_TIMEOUT, watcher);
        Assert.assertFalse("Missing SSL configuration should not result in successful connection", watcher.clientConnected.await(1000, TimeUnit.MILLISECONDS));
    }
}

