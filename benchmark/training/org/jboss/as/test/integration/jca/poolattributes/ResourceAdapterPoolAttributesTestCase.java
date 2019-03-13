/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.integration.jca.poolattributes;


import Constants.BLOCKING_TIMEOUT_WAIT_MILLIS;
import Constants.INITIAL_POOL_SIZE;
import Constants.MAX_POOL_SIZE;
import Constants.MIN_POOL_SIZE;
import Constants.POOL_FAIR;
import Constants.POOL_USE_STRICT_MIN;
import Namespace.RESOURCEADAPTERS_1_1;
import java.util.List;
import javax.annotation.Resource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.connector.subsystems.resourceadapters.ResourceAdapterSubsystemParser;
import org.jboss.as.test.integration.jca.JcaMgmtBase;
import org.jboss.as.test.integration.jca.JcaMgmtServerSetupTask;
import org.jboss.as.test.integration.jca.JcaTestsUtil;
import org.jboss.as.test.integration.jca.lazyconnectionmanager.rar.LazyConnectionFactory;
import org.jboss.as.test.shared.FileUtils;
import org.jboss.dmr.ModelNode;
import org.jboss.jca.core.api.connectionmanager.pool.PoolConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Checks that pool attributes can be set and do not require a reload.
 *
 * @author <a href="mailto:thofman@redhat.com">Tomas Hofman</a>
 */
@RunWith(Arquillian.class)
@ServerSetup(ResourceAdapterPoolAttributesTestCase.ResourceAdapterCapacityPoliciesServerSetupTask.class)
public class ResourceAdapterPoolAttributesTestCase extends JcaMgmtBase {
    private static final String RA_NAME = "pool-attributes-test.rar";

    private static final ModelNode RA_ADDRESS = new ModelNode().add(SUBSYSTEM, "resource-adapters").add("resource-adapter", ResourceAdapterPoolAttributesTestCase.RA_NAME);

    private static final ModelNode CONNECTION_ADDRESS = ResourceAdapterPoolAttributesTestCase.RA_ADDRESS.clone().add("connection-definitions", "Lazy");

    static {
        ResourceAdapterPoolAttributesTestCase.RA_ADDRESS.protect();
        ResourceAdapterPoolAttributesTestCase.CONNECTION_ADDRESS.protect();
    }

    @Resource(mappedName = "java:/eis/Lazy")
    private LazyConnectionFactory lcf;

    @ArquillianResource
    private ManagementClient managementClient;

    @Test
    public void testModifyPoolAttributes() throws Exception {
        PoolConfiguration poolConfiguration = JcaTestsUtil.exctractPoolConfiguration(lcf);
        // check initial values
        Assert.assertNotNull(poolConfiguration);
        Assert.assertEquals(2, poolConfiguration.getMinSize());
        Assert.assertEquals(5, poolConfiguration.getMaxSize());
        Assert.assertEquals(2, poolConfiguration.getInitialSize());
        Assert.assertEquals(30000, poolConfiguration.getBlockingTimeout());
        Assert.assertEquals(true, poolConfiguration.isFair());
        Assert.assertEquals(false, poolConfiguration.isStrictMin());
        // modify values
        writeAttribute(ResourceAdapterPoolAttributesTestCase.CONNECTION_ADDRESS, INITIAL_POOL_SIZE.getName(), "4");
        writeAttribute(ResourceAdapterPoolAttributesTestCase.CONNECTION_ADDRESS, BLOCKING_TIMEOUT_WAIT_MILLIS.getName(), "10000");
        writeAttribute(ResourceAdapterPoolAttributesTestCase.CONNECTION_ADDRESS, POOL_FAIR.getName(), "false");
        writeAttribute(ResourceAdapterPoolAttributesTestCase.CONNECTION_ADDRESS, POOL_USE_STRICT_MIN.getName(), "true");
        // check that server is not in reload-required state
        ModelNode serverState = readAttribute(new ModelNode(), "server-state");
        Assert.assertEquals("running", serverState.asString());
        // check that runtime was updated
        Assert.assertEquals(4, poolConfiguration.getInitialSize());
        Assert.assertEquals(10000, poolConfiguration.getBlockingTimeout());
        Assert.assertEquals(false, poolConfiguration.isFair());
        Assert.assertEquals(true, poolConfiguration.isStrictMin());
        writeAttribute(ResourceAdapterPoolAttributesTestCase.CONNECTION_ADDRESS, MIN_POOL_SIZE.getName(), "4");
        writeAttribute(ResourceAdapterPoolAttributesTestCase.CONNECTION_ADDRESS, MAX_POOL_SIZE.getName(), "10");
        // check that server is in reload-required state
        serverState = readAttribute(new ModelNode(), "server-state");
        Assert.assertEquals("reload-required", serverState.asString());
        // check that runtime was updated
        Assert.assertEquals(4, poolConfiguration.getMinSize());
        Assert.assertEquals(10, poolConfiguration.getMaxSize());
    }

    static class ResourceAdapterCapacityPoliciesServerSetupTask extends JcaMgmtServerSetupTask {
        @Override
        public void doSetup(final ManagementClient managementClient) throws Exception {
            String xml = FileUtils.readFile(ResourceAdapterPoolAttributesTestCase.class, "ra-def.xml");
            List<ModelNode> operations = xmlToModelOperations(xml, RESOURCEADAPTERS_1_1.getUriString(), new ResourceAdapterSubsystemParser());
            executeOperation(operationListToCompositeOperation(operations));
            reload();
        }
    }
}

