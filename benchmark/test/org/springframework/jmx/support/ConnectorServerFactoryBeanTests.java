/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.jmx.support;


import javax.management.InstanceNotFoundException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jmx.AbstractMBeanServerTests;


/**
 * To run the tests in the class, set the following Java system property:
 * {@code -DtestGroups=jmxmp}.
 *
 * @author Rob Harrop
 * @author Chris Beams
 * @author Sam Brannen
 */
public class ConnectorServerFactoryBeanTests extends AbstractMBeanServerTests {
    private static final String OBJECT_NAME = "spring:type=connector,name=test";

    @Test
    public void startupWithLocatedServer() throws Exception {
        ConnectorServerFactoryBean bean = new ConnectorServerFactoryBean();
        bean.afterPropertiesSet();
        try {
            checkServerConnection(getServer());
        } finally {
            bean.destroy();
        }
    }

    @Test
    public void startupWithSuppliedServer() throws Exception {
        // Added a brief snooze here - seems to fix occasional
        // java.net.BindException: Address already in use errors
        Thread.sleep(1);
        ConnectorServerFactoryBean bean = new ConnectorServerFactoryBean();
        bean.setServer(getServer());
        bean.afterPropertiesSet();
        try {
            checkServerConnection(getServer());
        } finally {
            bean.destroy();
        }
    }

    @Test
    public void registerWithMBeanServer() throws Exception {
        // Added a brief snooze here - seems to fix occasional
        // java.net.BindException: Address already in use errors
        Thread.sleep(1);
        ConnectorServerFactoryBean bean = new ConnectorServerFactoryBean();
        bean.setObjectName(ConnectorServerFactoryBeanTests.OBJECT_NAME);
        bean.afterPropertiesSet();
        try {
            // Try to get the connector bean.
            ObjectInstance instance = getServer().getObjectInstance(ObjectName.getInstance(ConnectorServerFactoryBeanTests.OBJECT_NAME));
            Assert.assertNotNull("ObjectInstance should not be null", instance);
        } finally {
            bean.destroy();
        }
    }

    @Test
    public void noRegisterWithMBeanServer() throws Exception {
        ConnectorServerFactoryBean bean = new ConnectorServerFactoryBean();
        bean.afterPropertiesSet();
        try {
            // Try to get the connector bean.
            getServer().getObjectInstance(ObjectName.getInstance(ConnectorServerFactoryBeanTests.OBJECT_NAME));
            Assert.fail("Instance should not be found");
        } catch (InstanceNotFoundException ex) {
            // expected
        } finally {
            bean.destroy();
        }
    }
}

