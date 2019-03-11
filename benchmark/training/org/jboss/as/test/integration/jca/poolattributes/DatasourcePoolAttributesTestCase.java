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
import Constants.POOL_FAIR;
import Constants.POOL_USE_STRICT_MIN;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.connector.subsystems.datasources.WildFlyDataSource;
import org.jboss.as.test.integration.jca.JcaMgmtBase;
import org.jboss.as.test.integration.jca.JcaMgmtServerSetupTask;
import org.jboss.as.test.integration.jca.JcaTestsUtil;
import org.jboss.as.test.integration.jca.datasource.Datasource;
import org.jboss.dmr.ModelNode;
import org.jboss.jca.adapters.jdbc.WrapperDataSource;
import org.jboss.jca.core.api.connectionmanager.pool.PoolConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Checks that pool attributes can be set and do (not) require a reload.
 *
 * @author <a href="mailto:thofman@redhat.com">Tomas Hofman</a>
 */
@RunWith(Arquillian.class)
@ServerSetup(DatasourcePoolAttributesTestCase.DatasourceServerSetupTask.class)
public class DatasourcePoolAttributesTestCase extends JcaMgmtBase {
    private static final String DS_NAME = "DS";

    private static final ModelNode DS_ADDRESS = new ModelNode().add(SUBSYSTEM, "datasources").add("data-source", DatasourcePoolAttributesTestCase.DS_NAME);

    static {
        DatasourcePoolAttributesTestCase.DS_ADDRESS.protect();
    }

    @Resource(mappedName = "java:jboss/datasources/" + (DatasourcePoolAttributesTestCase.DS_NAME))
    private DataSource datasource;

    @ArquillianResource
    private ManagementClient managementClient;

    @ArquillianResource
    private static ContainerController container;

    /**
     * Checks that attributes not requiring reload can be set.
     */
    @Test
    public void testModifyNonReloadAttributes() throws Exception {
        WrapperDataSource wrapperDataSource = JcaTestsUtil.extractWrapperDatasource(((WildFlyDataSource) (datasource)));
        PoolConfiguration poolConfiguration = JcaTestsUtil.exctractPoolConfiguration(wrapperDataSource);
        // check initial values
        Assert.assertNotNull(poolConfiguration);
        Assert.assertEquals(0, poolConfiguration.getInitialSize());
        Assert.assertEquals(30000, poolConfiguration.getBlockingTimeout());
        Assert.assertEquals(true, poolConfiguration.isFair());
        Assert.assertEquals(false, poolConfiguration.isStrictMin());
        // modify values
        writeAttribute(DatasourcePoolAttributesTestCase.DS_ADDRESS, INITIAL_POOL_SIZE.getName(), "4");
        writeAttribute(DatasourcePoolAttributesTestCase.DS_ADDRESS, BLOCKING_TIMEOUT_WAIT_MILLIS.getName(), "10000");
        writeAttribute(DatasourcePoolAttributesTestCase.DS_ADDRESS, POOL_FAIR.getName(), "false");
        writeAttribute(DatasourcePoolAttributesTestCase.DS_ADDRESS, POOL_USE_STRICT_MIN.getName(), "true");
        // check that server is not in reload-required state
        ModelNode serverState = readAttribute(new ModelNode(), "server-state");
        Assert.assertEquals("running", serverState.asString());
        // check that runtime was updated
        Assert.assertEquals(4, poolConfiguration.getInitialSize());
        Assert.assertEquals(10000, poolConfiguration.getBlockingTimeout());
        Assert.assertEquals(false, poolConfiguration.isFair());
        Assert.assertEquals(true, poolConfiguration.isStrictMin());
    }

    static class DatasourceServerSetupTask extends JcaMgmtServerSetupTask {
        @Override
        protected void doSetup(ManagementClient managementClient) throws Exception {
            setupDs(managementClient, DatasourcePoolAttributesTestCase.DS_NAME, true);
            reload();
        }

        private void setupDs(ManagementClient managementClient, String dsName, boolean jta) throws Exception {
            Datasource ds = Datasource.Builder(dsName).build();
            ModelNode address = new ModelNode();
            address.add("subsystem", "datasources");
            address.add("data-source", dsName);
            ModelNode operation = new ModelNode();
            operation.get(OP).set(ADD);
            operation.get(OP_ADDR).set(address);
            operation.get("jndi-name").set(ds.getJndiName());
            operation.get("use-java-context").set("true");
            operation.get("driver-name").set(ds.getDriverName());
            operation.get("enabled").set("true");
            operation.get("user-name").set(ds.getUserName());
            operation.get("password").set(ds.getPassword());
            operation.get("jta").set(jta);
            operation.get("use-ccm").set("true");
            operation.get("connection-url").set(ds.getConnectionUrl());
            managementClient.getControllerClient().execute(operation);
        }
    }
}

