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
package org.apache.ambari.server.metadata;


import RootComponent.AMBARI_AGENT;
import RootComponent.AMBARI_SERVER;
import com.google.inject.Injector;
import java.util.List;
import junit.framework.Assert;
import org.apache.ambari.server.state.alert.AlertDefinition;
import org.junit.Test;


/**
 * Tets {@link AmbariServiceAlertDefinitions}.
 */
public class AgentAlertDefinitionsTest {
    private Injector m_injector;

    /**
     * Tests loading the agent alerts.
     */
    @Test
    public void testLoadingAgentHostAlerts() {
        AmbariServiceAlertDefinitions ambariServiceAlertDefinitions = m_injector.getInstance(AmbariServiceAlertDefinitions.class);
        List<AlertDefinition> definitions = ambariServiceAlertDefinitions.getAgentDefinitions();
        Assert.assertEquals(3, definitions.size());
        for (AlertDefinition definition : definitions) {
            Assert.assertEquals(AMBARI_AGENT.name(), definition.getComponentName());
            Assert.assertEquals("AMBARI", definition.getServiceName());
        }
    }

    /**
     * Tests loading the agent alerts.
     */
    @Test
    public void testLoadingServertAlerts() {
        AmbariServiceAlertDefinitions ambariServiceAlertDefinitions = m_injector.getInstance(AmbariServiceAlertDefinitions.class);
        List<AlertDefinition> definitions = ambariServiceAlertDefinitions.getServerDefinitions();
        Assert.assertEquals(4, definitions.size());
        for (AlertDefinition definition : definitions) {
            Assert.assertEquals(AMBARI_SERVER.name(), definition.getComponentName());
            Assert.assertEquals("AMBARI", definition.getServiceName());
        }
    }
}

