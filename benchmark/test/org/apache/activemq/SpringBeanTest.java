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
package org.apache.activemq;


import java.util.HashMap;
import javax.management.ObjectName;
import org.apache.activemq.network.DiscoveryNetworkConnector;
import org.apache.activemq.plugin.RuntimeConfigurationBroker;
import org.apache.activemq.plugin.jmx.RuntimeConfigurationViewMBean;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


public class SpringBeanTest extends RuntimeConfigTestSupport {
    @Test
    public void testModifiable() throws Exception {
        final String brokerConfig = "SpringBeanTest-broker";
        applyNewConfig(brokerConfig, "emptyUpdatableConfig1000-spring-bean");
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        // apply via jmx
        ObjectName objectName = new ObjectName(((brokerService.getBrokerObjectName().toString()) + (RuntimeConfigurationBroker.objectNamePropsAppendage)));
        RuntimeConfigurationViewMBean runtimeConfigurationView = ((RuntimeConfigurationViewMBean) (brokerService.getManagementContext().newProxyInstance(objectName, RuntimeConfigurationViewMBean.class, false)));
        String propOfInterest = "modified";
        HashMap<String, String> props = new HashMap<String, String>();
        IntrospectionSupport.getProperties(runtimeConfigurationView, props, null);
        RuntimeConfigTestSupport.LOG.info(("mbean attributes before: " + props));
        Assert.assertNotEquals("unknown", props.get(propOfInterest));
        String result = runtimeConfigurationView.updateNow();
        RuntimeConfigTestSupport.LOG.info(("Result from update: " + result));
        Assert.assertTrue("got sensible result", result.contains("No material change"));
        HashMap<String, String> propsAfter = new HashMap<String, String>();
        IntrospectionSupport.getProperties(runtimeConfigurationView, propsAfter, null);
        RuntimeConfigTestSupport.LOG.info(("mbean attributes after: " + propsAfter));
        Assert.assertEquals("modified is same", props.get(propOfInterest), propsAfter.get(propOfInterest));
    }

    @Test
    public void testAddPropertyRef() throws Exception {
        System.setProperty("network.uri", "static:(tcp://localhost:8888)");
        final String brokerConfig = "SpringPropertyTest-broker";
        applyNewConfig(brokerConfig, "emptyUpdatableConfig1000-spring-property");
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        applyNewConfig(brokerConfig, "emptyUpdatableConfig1000-spring-property-nc", RuntimeConfigTestSupport.SLEEP);
        Assert.assertTrue("new network connectors", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 1 == (brokerService.getNetworkConnectors().size());
            }
        }));
        DiscoveryNetworkConnector discoveryNetworkConnector = ((DiscoveryNetworkConnector) (brokerService.getNetworkConnectors().get(0)));
        Assert.assertEquals("property replaced", System.getProperty("network.uri"), discoveryNetworkConnector.getUri().toASCIIString());
    }

    @Test
    public void testAddPropertyRefFromFile() throws Exception {
        System.setProperty("network.uri", "static:(tcp://localhost:8888)");
        System.setProperty("props.base", "classpath:");
        final String brokerConfig = "SpringPropertyTest-broker";
        applyNewConfig(brokerConfig, "emptyUpdatableConfig1000-spring-property-file");
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        applyNewConfig(brokerConfig, "emptyUpdatableConfig1000-spring-property-file-nc", RuntimeConfigTestSupport.SLEEP);
        Assert.assertTrue("new network connectors", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 1 == (brokerService.getNetworkConnectors().size());
            }
        }));
        DiscoveryNetworkConnector discoveryNetworkConnector = ((DiscoveryNetworkConnector) (brokerService.getNetworkConnectors().get(0)));
        Assert.assertEquals("property replaced", System.getProperty("network.uri"), discoveryNetworkConnector.getUri().toASCIIString());
        Assert.assertEquals("name is replaced", "guest", discoveryNetworkConnector.getName());
    }

    @Test
    public void testAddPropertyRefFromFileAsList() throws Exception {
        System.setProperty("network.uri", "static:(tcp://localhost:8888)");
        System.setProperty("props.base", "classpath:");
        final String brokerConfig = "SpringPropertyTestFileList-broker";
        applyNewConfig(brokerConfig, "emptyUpdatableConfig1000-spring-property-file-list");
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        ObjectName objectName = new ObjectName(((brokerService.getBrokerObjectName().toString()) + (RuntimeConfigurationBroker.objectNamePropsAppendage)));
        RuntimeConfigurationViewMBean runtimeConfigurationView = ((RuntimeConfigurationViewMBean) (brokerService.getManagementContext().newProxyInstance(objectName, RuntimeConfigurationViewMBean.class, false)));
        String propOfInterest = "modified";
        HashMap<String, String> props = new HashMap<String, String>();
        IntrospectionSupport.getProperties(runtimeConfigurationView, props, null);
        RuntimeConfigTestSupport.LOG.info(("mbean attributes before: " + props));
        Assert.assertNotEquals("unknown", props.get(propOfInterest));
    }

    @Test
    public void testAddPropertyRefFromFileAndBeanFactory() throws Exception {
        System.setProperty("network.uri", "static:(tcp://localhost:8888)");
        System.setProperty("props.base", "classpath:");
        final String brokerConfig = "SpringPropertyTestFileListBeanFactory-broker";
        applyNewConfig(brokerConfig, "emptyUpdatableConfig1000-spring-property-file-list-and-beanFactory");
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        ObjectName objectName = new ObjectName(((brokerService.getBrokerObjectName().toString()) + (RuntimeConfigurationBroker.objectNamePropsAppendage)));
        RuntimeConfigurationViewMBean runtimeConfigurationView = ((RuntimeConfigurationViewMBean) (brokerService.getManagementContext().newProxyInstance(objectName, RuntimeConfigurationViewMBean.class, false)));
        String propOfInterest = "modified";
        HashMap<String, String> props = new HashMap<String, String>();
        IntrospectionSupport.getProperties(runtimeConfigurationView, props, null);
        RuntimeConfigTestSupport.LOG.info(("mbean attributes before: " + props));
        Assert.assertNotEquals("unknown", props.get(propOfInterest));
        Assert.assertEquals("our custom prop is applied", "isKing", brokerService.getBrokerName());
        applyNewConfig(brokerConfig, "spring-property-file-list-and-beanFactory-new-nc", RuntimeConfigTestSupport.SLEEP);
        Assert.assertTrue("new network connectors", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 1 == (brokerService.getNetworkConnectors().size());
            }
        }));
        Assert.assertEquals("our custom prop is applied", "isKing", brokerService.getNetworkConnectors().get(0).getName());
    }
}

