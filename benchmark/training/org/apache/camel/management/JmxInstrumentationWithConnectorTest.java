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
package org.apache.camel.management;


import java.rmi.NoSuchObjectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.management.remote.JMXConnector;
import org.junit.Test;


/**
 * Test that verifies JMX connector server can be connected by
 * a client.
 */
public class JmxInstrumentationWithConnectorTest extends JmxInstrumentationUsingDefaultsTest {
    protected String url;

    protected JMXConnector clientConnector;

    protected int registryPort;

    @Test
    public void testRmiRegistryUnexported() throws Exception {
        Registry registry = LocateRegistry.getRegistry(registryPort);
        // before we stop the context the registry is still exported, so list() should work
        Exception e;
        try {
            registry.list();
            e = null;
        } catch (NoSuchObjectException nsoe) {
            e = nsoe;
        }
        assertNull(e);
        // stop the Camel context
        context.stop();
        // stopping the Camel context unexported the registry, so list() should fail
        Exception e2;
        try {
            registry.list();
            e2 = null;
        } catch (NoSuchObjectException nsoe) {
            e2 = nsoe;
        }
        assertNotNull(e2);
    }
}

