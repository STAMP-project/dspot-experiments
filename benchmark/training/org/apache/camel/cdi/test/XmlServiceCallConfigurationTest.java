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
package org.apache.camel.cdi.test;


import javax.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.cdi.ImportResource;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.cloud.ServiceCallConfigurationDefinition;
import org.apache.camel.model.cloud.StaticServiceCallServiceDiscoveryConfiguration;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@ImportResource("imported-context.xml")
public class XmlServiceCallConfigurationTest {
    @Inject
    private CamelContext context;

    @Test
    public void testServiceDiscoveryConfiguration() {
        ServiceCallConfigurationDefinition conf1 = context.adapt(ModelCamelContext.class).getServiceCallConfiguration("conf1");
        Assert.assertNotNull("No ServiceCallConfiguration (1)", conf1);
        Assert.assertNotNull("No ServiceDiscoveryConfiguration (1)", conf1.getServiceDiscoveryConfiguration());
        StaticServiceCallServiceDiscoveryConfiguration discovery1 = ((StaticServiceCallServiceDiscoveryConfiguration) (conf1.getServiceDiscoveryConfiguration()));
        Assert.assertEquals(1, discovery1.getServers().size());
        Assert.assertEquals("localhost:9091", discovery1.getServers().get(0));
        ServiceCallConfigurationDefinition conf2 = context.adapt(ModelCamelContext.class).getServiceCallConfiguration("conf2");
        Assert.assertNotNull("No ServiceCallConfiguration (2)", conf2);
        Assert.assertNotNull("No ServiceDiscoveryConfiguration (2)", conf2.getServiceDiscoveryConfiguration());
        StaticServiceCallServiceDiscoveryConfiguration discovery2 = ((StaticServiceCallServiceDiscoveryConfiguration) (conf2.getServiceDiscoveryConfiguration()));
        Assert.assertEquals(2, discovery2.getServers().size());
        Assert.assertEquals("localhost:9092", discovery2.getServers().get(0));
        Assert.assertEquals("localhost:9093,localhost:9094", discovery2.getServers().get(1));
    }
}

