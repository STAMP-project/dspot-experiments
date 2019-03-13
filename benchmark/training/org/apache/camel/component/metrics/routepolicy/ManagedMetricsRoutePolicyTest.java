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
package org.apache.camel.component.metrics.routepolicy;


import com.codahale.metrics.MetricRegistry;
import java.util.Set;
import javax.management.ObjectName;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class ManagedMetricsRoutePolicyTest extends CamelTestSupport {
    private MetricRegistry metricRegistry = new MetricRegistry();

    @Test
    public void testMetricsRoutePolicy() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(10);
        for (int i = 0; i < 10; i++) {
            if ((i % 2) == 0) {
                template.sendBody("seda:foo", ("Hello " + i));
            } else {
                template.sendBody("seda:bar", ("Hello " + i));
            }
        }
        assertMockEndpointsSatisfied();
        // there should be 3 names
        assertEquals(3, metricRegistry.getNames().size());
        // there should be 3 mbeans
        Set<ObjectName> set = getMBeanServer().queryNames(new ObjectName("org.apache.camel.metrics:*"), null);
        assertEquals(3, set.size());
        String name = String.format("org.apache.camel:context=%s,type=services,name=MetricsRegistryService", context.getManagementName());
        ObjectName on = ObjectName.getInstance(name);
        String json = ((String) (getMBeanServer().invoke(on, "dumpStatisticsAsJson", null, null)));
        assertNotNull(json);
        log.info(json);
        assertTrue(json.contains("test"));
        assertTrue(json.contains("bar.responses"));
        assertTrue(json.contains("foo.responses"));
    }
}

