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
package org.apache.hadoop.yarn.service.monitor;


import Component.RestartPolicyEnum.NEVER;
import Component.RestartPolicyEnum.ON_FAILURE;
import java.io.File;
import java.util.Collections;
import org.apache.curator.test.TestingCluster;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.service.MockServiceAM;
import org.apache.hadoop.yarn.service.ServiceTestUtils;
import org.apache.hadoop.yarn.service.api.records.Component;
import org.apache.hadoop.yarn.service.api.records.Service;
import org.junit.Assert;
import org.junit.Test;


public class TestServiceMonitor extends ServiceTestUtils {
    private File basedir;

    YarnConfiguration conf = new YarnConfiguration();

    TestingCluster zkCluster;

    // Create compa with 1 container
    // Create compb with 1 container
    // Verify compb dependency satisfied
    // Increase compa to 2 containers
    // Verify compb dependency becomes unsatisfied.
    @Test
    public void testComponentDependency() throws Exception {
        ApplicationId applicationId = ApplicationId.newInstance(123456, 1);
        Service exampleApp = new Service();
        exampleApp.setVersion("v1");
        exampleApp.setId(applicationId.toString());
        exampleApp.setName("testComponentDependency");
        exampleApp.addComponent(ServiceTestUtils.createComponent("compa", 1, "sleep 1000"));
        // Let compb depends on compa;
        Component compb = ServiceTestUtils.createComponent("compb", 1, "sleep 1000", ON_FAILURE, Collections.singletonList("compa"));
        // Let compb depends on compb;
        Component compc = ServiceTestUtils.createComponent("compc", 1, "sleep 1000", NEVER, Collections.singletonList("compb"));
        exampleApp.addComponent(compb);
        exampleApp.addComponent(compc);
        MockServiceAM am = new MockServiceAM(exampleApp);
        am.init(conf);
        start();
        // compa ready
        Assert.assertTrue(am.getComponent("compa").areDependenciesReady());
        // compb not ready
        Assert.assertFalse(am.getComponent("compb").areDependenciesReady());
        // feed 1 container to compa,
        am.feedContainerToComp(exampleApp, 1, "compa");
        // waiting for compb's dependencies are satisfied
        am.waitForDependenciesSatisfied("compb");
        // feed 1 container to compb,
        am.feedContainerToComp(exampleApp, 2, "compb");
        // waiting for compc's dependencies are satisfied
        am.waitForDependenciesSatisfied("compc");
        // feed 1 container to compb
        am.feedContainerToComp(exampleApp, 2, "compb");
        am.flexComponent("compa", 2);
        am.waitForNumDesiredContainers("compa", 2);
        // compb dependencies not satisfied again.
        Assert.assertFalse(am.getComponent("compb").areDependenciesReady());
        stop();
    }
}

