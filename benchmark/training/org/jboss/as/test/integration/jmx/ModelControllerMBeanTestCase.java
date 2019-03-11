/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.jmx;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.jmx.model.ModelControllerMBeanHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ModelControllerMBeanTestCase {
    private static final String RESOLVED_DOMAIN = "jboss.as";

    static final ObjectName RESOLVED_MODEL_FILTER = ModelControllerMBeanTestCase.createObjectName(((ModelControllerMBeanTestCase.RESOLVED_DOMAIN) + ":*"));

    static final ObjectName RESOLVED_ROOT_MODEL_NAME = ModelControllerMBeanHelper.createRootObjectName(ModelControllerMBeanTestCase.RESOLVED_DOMAIN);

    static JMXConnector connector;

    static MBeanServerConnection connection;

    @ContainerResource
    private ManagementClient managementClient;

    /**
     * Test that all the MBean infos can be read properly
     */
    @Test
    public void testAllMBeanInfos() throws Exception {
        Set<ObjectName> names = ModelControllerMBeanTestCase.connection.queryNames(ModelControllerMBeanTestCase.RESOLVED_MODEL_FILTER, null);
        Map<ObjectName, Exception> failedInfos = new HashMap<ObjectName, Exception>();
        for (ObjectName name : names) {
            try {
                Assert.assertNotNull(ModelControllerMBeanTestCase.connection.getMBeanInfo(name));
            } catch (Exception e) {
                failedInfos.put(name, e);
            }
        }
        Assert.assertTrue(failedInfos.toString(), failedInfos.isEmpty());
    }
}

