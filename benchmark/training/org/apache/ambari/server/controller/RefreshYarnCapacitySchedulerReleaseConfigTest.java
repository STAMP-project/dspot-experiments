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
package org.apache.ambari.server.controller;


import com.google.inject.Injector;
import java.util.Collections;
import java.util.Set;
import junit.framework.Assert;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ConfigHelper;
import org.junit.Test;


@SuppressWarnings("serial")
public class RefreshYarnCapacitySchedulerReleaseConfigTest {
    private Injector injector;

    private AmbariManagementController controller;

    private Clusters clusters;

    private ConfigHelper configHelper;

    private OrmTestHelper ormTestHelper;

    private String stackName;

    @Test
    public void testConfigInComponent() throws Exception {
        StackServiceRequest requestWithParams = new StackServiceRequest("HDP", "2.0.6", "YARN");
        Set<StackServiceResponse> responsesWithParams = controller.getStackServices(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackServiceResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getServiceName(), "YARN");
            Assert.assertTrue(responseWithParams.getConfigTypes().containsKey("capacity-scheduler"));
        }
    }

    @Test
    public void testConfigInComponentOverwrited() throws Exception {
        StackServiceRequest requestWithParams = new StackServiceRequest("HDP", "2.0.7", "YARN");
        Set<StackServiceResponse> responsesWithParams = controller.getStackServices(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackServiceResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getServiceName(), "YARN");
            Assert.assertTrue(responseWithParams.getConfigTypes().containsKey("capacity-scheduler"));
        }
    }
}

