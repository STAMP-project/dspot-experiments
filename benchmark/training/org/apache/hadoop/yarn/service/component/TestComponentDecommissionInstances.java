/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.service.component;


import ServiceState.STOPPED;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.registry.client.binding.RegistryUtils;
import org.apache.hadoop.yarn.service.ServiceTestUtils;
import org.apache.hadoop.yarn.service.api.records.Component;
import org.apache.hadoop.yarn.service.api.records.Service;
import org.apache.hadoop.yarn.service.client.ServiceClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test decommissioning component instances.
 */
public class TestComponentDecommissionInstances extends ServiceTestUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TestComponentDecommissionInstances.class);

    private static final String APP_NAME = "test-decommission";

    private static final String COMPA = "compa";

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testDecommissionInstances() throws Exception {
        setupInternal(3);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        Service exampleApp = new Service();
        exampleApp.setName(TestComponentDecommissionInstances.APP_NAME);
        exampleApp.setVersion("v1");
        Component comp = ServiceTestUtils.createComponent(TestComponentDecommissionInstances.COMPA, 6L, "sleep 1000");
        exampleApp.addComponent(comp);
        client.actionCreate(exampleApp);
        waitForServiceToBeStable(client, exampleApp);
        TestComponentDecommissionInstances.checkInstances(client, ((TestComponentDecommissionInstances.COMPA) + "-0"), ((TestComponentDecommissionInstances.COMPA) + "-1"), ((TestComponentDecommissionInstances.COMPA) + "-2"), ((TestComponentDecommissionInstances.COMPA) + "-3"), ((TestComponentDecommissionInstances.COMPA) + "-4"), ((TestComponentDecommissionInstances.COMPA) + "-5"));
        client.actionDecommissionInstances(TestComponentDecommissionInstances.APP_NAME, Arrays.asList(((TestComponentDecommissionInstances.COMPA) + "-1"), ((TestComponentDecommissionInstances.COMPA) + "-5")));
        TestComponentDecommissionInstances.waitForNumInstances(client, 4);
        TestComponentDecommissionInstances.checkInstances(client, ((TestComponentDecommissionInstances.COMPA) + "-0"), ((TestComponentDecommissionInstances.COMPA) + "-2"), ((TestComponentDecommissionInstances.COMPA) + "-3"), ((TestComponentDecommissionInstances.COMPA) + "-4"));
        // Stop and start service
        client.actionStop(TestComponentDecommissionInstances.APP_NAME);
        waitForServiceToBeInState(client, exampleApp, STOPPED);
        client.actionStart(TestComponentDecommissionInstances.APP_NAME);
        waitForServiceToBeStable(client, exampleApp);
        TestComponentDecommissionInstances.checkInstances(client, ((TestComponentDecommissionInstances.COMPA) + "-0"), ((TestComponentDecommissionInstances.COMPA) + "-2"), ((TestComponentDecommissionInstances.COMPA) + "-3"), ((TestComponentDecommissionInstances.COMPA) + "-4"));
        Map<String, String> compCounts = new HashMap<>();
        compCounts.put(TestComponentDecommissionInstances.COMPA, "5");
        client.actionFlex(TestComponentDecommissionInstances.APP_NAME, compCounts);
        TestComponentDecommissionInstances.waitForNumInstances(client, 5);
        TestComponentDecommissionInstances.checkInstances(client, ((TestComponentDecommissionInstances.COMPA) + "-0"), ((TestComponentDecommissionInstances.COMPA) + "-2"), ((TestComponentDecommissionInstances.COMPA) + "-3"), ((TestComponentDecommissionInstances.COMPA) + "-4"), ((TestComponentDecommissionInstances.COMPA) + "-6"));
        client.actionDecommissionInstances(TestComponentDecommissionInstances.APP_NAME, Arrays.asList((((((TestComponentDecommissionInstances.COMPA) + "-0.") + (TestComponentDecommissionInstances.APP_NAME)) + ".") + (RegistryUtils.currentUser()))));
        TestComponentDecommissionInstances.waitForNumInstances(client, 4);
        TestComponentDecommissionInstances.checkInstances(client, ((TestComponentDecommissionInstances.COMPA) + "-2"), ((TestComponentDecommissionInstances.COMPA) + "-3"), ((TestComponentDecommissionInstances.COMPA) + "-4"), ((TestComponentDecommissionInstances.COMPA) + "-6"));
    }
}

