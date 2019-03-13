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
package org.apache.ambari.server.controller.internal;


import RequestOperationLevel.OPERATION_CLUSTER_ID;
import RequestOperationLevel.OPERATION_HOSTCOMPONENT_ID;
import RequestOperationLevel.OPERATION_HOST_NAME;
import RequestOperationLevel.OPERATION_LEVEL_ID;
import RequestOperationLevel.OPERATION_SERVICE_ID;
import RequestResourceProvider.COMMAND_ID;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.fail;


public class RequestOperationLevelTest {
    private final String host_component = "HOST_COMPONENT";

    private final String hostComponent = "HostComponent";

    @Test
    public void test_ConstructionFromRequestProperties() throws Exception {
        String c1 = "c1";
        String host_component = "HOST_COMPONENT";
        String service_id = "HDFS";
        String hostcomponent_id = "Namenode";
        String host_id = "host1";
        Map<String, String> requestInfoProperties = new HashMap<>();
        requestInfoProperties.put(COMMAND_ID, "RESTART");
        requestInfoProperties.put(OPERATION_LEVEL_ID, host_component);
        requestInfoProperties.put(OPERATION_CLUSTER_ID, c1);
        requestInfoProperties.put(OPERATION_SERVICE_ID, service_id);
        requestInfoProperties.put(OPERATION_HOSTCOMPONENT_ID, hostcomponent_id);
        requestInfoProperties.put(OPERATION_HOST_NAME, host_id);
        // Check normal creation
        RequestOperationLevel opLevel = new RequestOperationLevel(requestInfoProperties);
        Assert.assertEquals(opLevel.getLevel().toString(), "HostComponent");
        Assert.assertEquals(opLevel.getClusterName(), c1);
        Assert.assertEquals(opLevel.getServiceName(), service_id);
        Assert.assertEquals(opLevel.getHostComponentName(), hostcomponent_id);
        Assert.assertEquals(opLevel.getHostName(), host_id);
        // Check exception wrong operation level is specified
        requestInfoProperties.put(OPERATION_LEVEL_ID, "wrong_value");
        try {
            new RequestOperationLevel(requestInfoProperties);
            fail("Should throw an exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
        requestInfoProperties.put(OPERATION_LEVEL_ID, host_component);
        // Check exception when cluster name is not specified
        requestInfoProperties.remove(OPERATION_CLUSTER_ID);
        try {
            new RequestOperationLevel(requestInfoProperties);
            fail("Should throw an exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testGetInternalLevelName() throws Exception {
        String internal = RequestOperationLevel.getInternalLevelName(host_component);
        Assert.assertEquals(internal, hostComponent);
        // Check case-insensitivity
        internal = RequestOperationLevel.getInternalLevelName(host_component.toLowerCase());
        Assert.assertEquals(internal, hostComponent);
        // Check wrong param
        try {
            RequestOperationLevel.getInternalLevelName("Wrong_param");
            Assert.fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testGetExternalLevelName() throws Exception {
        String external = RequestOperationLevel.getExternalLevelName(hostComponent);
        Assert.assertEquals(external, host_component);
        // Check wrong param
        try {
            RequestOperationLevel.getExternalLevelName("Wrong_param");
            Assert.fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}

