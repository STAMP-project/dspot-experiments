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


import HostComponentProcessResourceProvider.CLUSTER_NAME;
import HostComponentProcessResourceProvider.COMPONENT_NAME;
import HostComponentProcessResourceProvider.HOST_NAME;
import HostComponentProcessResourceProvider.NAME;
import HostComponentProcessResourceProvider.STATUS;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import junit.framework.Assert;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.junit.Test;


/**
 * Tests the {@link HostComponentProcessResourceProvider} class
 *
 * @author ncole
 */
public class HostComponentProcessResourceProviderTest {
    @Test
    public void testGetResources() throws Exception {
        @SuppressWarnings("unchecked")
        ResourceProvider provider = init(new HashMap<String, String>() {
            {
                put("status", "RUNNING");
                put("name", "a1");
            }
        });
        PredicateBuilder pb = new PredicateBuilder().property(CLUSTER_NAME).equals("c1").and();
        pb = pb.property(HOST_NAME).equals("h1").and();
        Predicate predicate = pb.property(COMPONENT_NAME).equals("comp1").toPredicate();
        Request request = PropertyHelper.getReadRequest(Collections.emptySet());
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(Integer.valueOf(1), Integer.valueOf(resources.size()));
        Resource res = resources.iterator().next();
        Assert.assertNotNull(res.getPropertyValue(NAME));
        Assert.assertNotNull(res.getPropertyValue(STATUS));
    }

    @Test
    public void testGetResources_none() throws Exception {
        @SuppressWarnings("unchecked")
        ResourceProvider provider = init();
        PredicateBuilder pb = new PredicateBuilder().property(CLUSTER_NAME).equals("c1").and();
        pb = pb.property(HOST_NAME).equals("h1").and();
        Predicate predicate = pb.property(COMPONENT_NAME).equals("comp1").toPredicate();
        Request request = PropertyHelper.getReadRequest(Collections.emptySet());
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(resources.size()));
    }

    @Test
    public void testGetResources_many() throws Exception {
        @SuppressWarnings("unchecked")
        ResourceProvider provider = init(new HashMap<String, String>() {
            {
                put("status", "RUNNING");
                put("name", "a");
            }
        }, new HashMap<String, String>() {
            {
                put("status", "RUNNING");
                put("name", "b");
            }
        }, new HashMap<String, String>() {
            {
                put("status", "NOT_RUNNING");
                put("name", "c");
            }
        });
        PredicateBuilder pb = new PredicateBuilder().property(CLUSTER_NAME).equals("c1").and();
        pb = pb.property(HOST_NAME).equals("h1").and();
        Predicate predicate = pb.property(COMPONENT_NAME).equals("comp1").toPredicate();
        Request request = PropertyHelper.getReadRequest(Collections.emptySet());
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(Integer.valueOf(3), Integer.valueOf(resources.size()));
        for (Resource r : resources) {
            Assert.assertNotNull(r.getPropertyValue(NAME));
            Assert.assertNotNull(r.getPropertyValue(STATUS));
        }
    }
}

