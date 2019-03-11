/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.smoke.mgmt.datasource;


import ConnectionSecurityType.ELYTRON;
import ConnectionSecurityType.ELYTRON_AUTHENTICATION_CONTEXT;
import ConnectionSecurityType.SECURITY_DOMAIN;
import ConnectionSecurityType.USER_PASSWORD;
import java.util.List;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.management.jca.DsMgmtTestBase;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Datasource operation unit test.
 *
 * @author <a href="mailto:stefano.maestri@redhat.com">Stefano Maestri</a>
 * @author <a href="mailto:jeff.zhang@jboss.org">Jeff Zhang</a>
 * @author <a href="mailto:vrastsel@redhat.com">Vladimir Rastseluev</a>
 * @author Flavia Rainone
 */
@RunWith(Arquillian.class)
@RunAsClient
@org.jboss.as.arquillian.api.ServerSetup(DataSourceOperationsUnitTestCase.ServerSetup.class)
public class DataSourceOperationsUnitTestCase extends DsMgmtTestBase {
    public static class ServerSetup implements ServerSetupTask {
        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            ModelNode authContextAdd = Util.createAddOperation(PathAddress.pathAddress("subsystem", "elytron").append("authentication-context", "HsqlAuthCtxt"));
            ModelNode response = managementClient.getControllerClient().execute(authContextAdd);
            Assert.assertEquals(response.toString(), "success", response.get("outcome").asString());
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            ModelNode authContextRemove = Util.createRemoveOperation(PathAddress.pathAddress("subsystem", "elytron").append("authentication-context", "HsqlAuthCtxt"));
            ModelNode response = managementClient.getControllerClient().execute(authContextRemove);
            Assert.assertEquals(response.toString(), "success", response.get("outcome").asString());
        }
    }

    @Test
    public void testAddDsAndTestConnection() throws Exception {
        final ModelNode address = new ModelNode();
        address.add("subsystem", "datasources");
        address.add("data-source", "MyNewDs");
        address.protect();
        final ModelNode operation = new ModelNode();
        operation.get(OP).set("add");
        operation.get(OP_ADDR).set(address);
        operation.get("name").set("MyNewDs");
        operation.get("jndi-name").set("java:jboss/datasources/MyNewDs");
        operation.get("enabled").set(true);
        operation.get("driver-name").set("h2");
        operation.get("pool-name").set("MyNewDs_Pool");
        operation.get("connection-url").set("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        operation.get("user-name").set("sa");
        operation.get("password").set("sa");
        executeOperation(operation);
        testConnection("MyNewDs");
        List<ModelNode> newList = marshalAndReparseDsResources("data-source");
        remove(address);
        Assert.assertNotNull("Reparsing failed:", newList);
        Assert.assertNotNull(findNodeWithProperty(newList, "jndi-name", "java:jboss/datasources/MyNewDs"));
    }

    @Test
    public void testAddAndRemoveSameName() throws Exception {
        final String dsName = "SameNameDs";
        final ModelNode address = new ModelNode();
        address.add("subsystem", "datasources");
        address.add("data-source", dsName);
        address.protect();
        final ModelNode operation = new ModelNode();
        operation.get(OP).set("add");
        operation.get(OP_ADDR).set(address);
        operation.get("name").set(dsName);
        operation.get("jndi-name").set(("java:jboss/datasources/" + dsName));
        operation.get("enabled").set(false);
        operation.get("driver-name").set("h2");
        operation.get("pool-name").set((dsName + "_Pool"));
        operation.get("connection-url").set("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        operation.get("user-name").set("sa");
        operation.get("password").set("sa");
        // do twice, test for AS7-720
        for (int i = 1; i <= 2; i++) {
            executeOperation(operation);
            remove(address);
        }
    }

    /**
     * AS7-1206 test for jndi binding isn't unbound during remove if jndi name
     * and data-source name are different
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddAndRemoveNameAndJndiNameDifferent() throws Exception {
        final String dsName = "DsName";
        final String jndiDsName = "JndiDsName";
        final ModelNode address = new ModelNode();
        address.add("subsystem", "datasources");
        address.add("data-source", dsName);
        address.protect();
        final ModelNode operation = new ModelNode();
        operation.get(OP).set("add");
        operation.get(OP_ADDR).set(address);
        operation.get("name").set(dsName);
        operation.get("jndi-name").set(("java:jboss/datasources/" + jndiDsName));
        operation.get("enabled").set(false);
        operation.get("driver-name").set("h2");
        operation.get("pool-name").set((dsName + "_Pool"));
        operation.get("connection-url").set("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        operation.get("user-name").set("sa");
        operation.get("password").set("sa");
        executeOperation(operation);
        remove(address);
    }

    @Test
    public void testAddAndRemoveXaDs() throws Exception {
        final String dsName = "XaDsName";
        final String jndiDsName = "XaJndiDsName";
        final ModelNode address = new ModelNode();
        address.add("subsystem", "datasources");
        address.add("xa-data-source", dsName);
        address.protect();
        final ModelNode operation = new ModelNode();
        operation.get(OP).set("add");
        operation.get(OP_ADDR).set(address);
        operation.get("name").set(dsName);
        operation.get("jndi-name").set(("java:jboss/datasources/" + jndiDsName));
        operation.get("enabled").set(false);
        operation.get("driver-name").set("h2");
        operation.get("pool-name").set((dsName + "_Pool"));
        operation.get("user-name").set("sa");
        operation.get("password").set("sa");
        executeOperation(operation);
        final ModelNode xaDatasourcePropertiesAddress = address.clone();
        xaDatasourcePropertiesAddress.add("xa-datasource-properties", "URL");
        xaDatasourcePropertiesAddress.protect();
        final ModelNode xaDatasourcePropertyOperation = new ModelNode();
        xaDatasourcePropertyOperation.get(OP).set("add");
        xaDatasourcePropertyOperation.get(OP_ADDR).set(xaDatasourcePropertiesAddress);
        xaDatasourcePropertyOperation.get("value").set("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        executeOperation(xaDatasourcePropertyOperation);
        remove(address);
    }

    /**
     * AS7-1200 test case for xa datasource persistence to xml
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMarshallUnmarshallXaDs() throws Exception {
        final String dsName = "XaDsName2";
        final String jndiDsName = "XaJndiDsName2";
        final ModelNode address = new ModelNode();
        address.add("subsystem", "datasources");
        address.add("xa-data-source", dsName);
        address.protect();
        final ModelNode operation = new ModelNode();
        operation.get(OP).set("add");
        operation.get(OP_ADDR).set(address);
        operation.get("name").set(dsName);
        operation.get("jndi-name").set(("java:jboss/datasources/" + jndiDsName));
        operation.get("enabled").set(false);
        operation.get("driver-name").set("h2");
        operation.get("pool-name").set((dsName + "_Pool"));
        operation.get("user-name").set("sa");
        operation.get("password").set("sa");
        executeOperation(operation);
        final ModelNode xaDatasourcePropertiesAddress = address.clone();
        xaDatasourcePropertiesAddress.add("xa-datasource-properties", "URL");
        xaDatasourcePropertiesAddress.protect();
        final ModelNode xaDatasourcePropertyOperation = new ModelNode();
        xaDatasourcePropertyOperation.get(OP).set("add");
        xaDatasourcePropertyOperation.get(OP_ADDR).set(xaDatasourcePropertiesAddress);
        xaDatasourcePropertyOperation.get("value").set("jdbc:h2:mem:test");
        executeOperation(xaDatasourcePropertyOperation);
        final ModelNode operation2 = new ModelNode();
        operation2.get(OP).set("write-attribute");
        operation2.get("name").set("enabled");
        operation2.get("value").set(true);
        operation2.get(OP_ADDR).set(address);
        executeOperation(operation2);
        List<ModelNode> newList = marshalAndReparseDsResources("xa-data-source");
        remove(address);
        Assert.assertNotNull("Reparsing failed:", newList);
        // remove from xml too
        marshalAndReparseDsResources("xa-data-source");
        Assert.assertNotNull(findNodeWithProperty(newList, "jndi-name", ("java:jboss/datasources/" + jndiDsName)));
    }

    @Test
    public void testReadInstalledDrivers() throws Exception {
        final ModelNode address = new ModelNode();
        address.add("subsystem", "datasources");
        address.protect();
        final ModelNode operation = new ModelNode();
        operation.get(OP).set("installed-drivers-list");
        operation.get(OP_ADDR).set(address);
        final ModelNode result = executeOperation(operation);
        final ModelNode result2 = result.get(0);
        Assert.assertNotNull("There are no installed JDBC drivers", result2);
        Assert.assertTrue("Name of JDBC driver is udefined", result2.hasDefined("driver-name"));
        if (!(result2.hasDefined("deployment-name"))) {
            // deployed drivers haven't these attributes
            Assert.assertTrue("Module name of JDBC driver is udefined", result2.hasDefined("driver-module-name"));
            Assert.assertTrue("Module slot of JDBC driver is udefined", result2.hasDefined("module-slot"));
        }
    }

    @Test
    public void testReadJdbcDriver() throws Exception {
        String h2DriverName = "h2backup";
        final ModelNode addrAddH2DriverAddr = new ModelNode();
        addrAddH2DriverAddr.add("subsystem", "datasources").add("jdbc-driver", h2DriverName);
        final ModelNode addH2DriverOp = new ModelNode();
        addH2DriverOp.get(OP).set("add");
        addH2DriverOp.get(OP_ADDR).set(addrAddH2DriverAddr);
        addH2DriverOp.get("driver-name").set(h2DriverName);
        addH2DriverOp.get("driver-module-name").set("com.h2database.h2");
        executeOperation(addH2DriverOp);
        try {
            final ModelNode address = new ModelNode();
            address.add("subsystem", "datasources");
            address.protect();
            final ModelNode operation = new ModelNode();
            operation.get(OP).set("get-installed-driver");
            operation.get(OP_ADDR).set(address);
            operation.get("driver-name").set(h2DriverName);
            final ModelNode result = executeOperation(operation).get(0);
            Assert.assertEquals(h2DriverName, result.get("driver-name").asString());
            Assert.assertEquals("com.h2database.h2", result.get("driver-module-name").asString());
            Assert.assertEquals("", result.get("driver-xa-datasource-class-name").asString());
        } finally {
            remove(addrAddH2DriverAddr);
        }
    }

    /**
     * AS7-1203 test for missing xa-datasource properties
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddXaDsWithProperties() throws Exception {
        final String xaDs = "MyNewXaDs";
        final String xaDsJndi = "java:jboss/xa-datasources/" + xaDs;
        final ModelNode address = new ModelNode();
        address.add("subsystem", "datasources");
        address.add("xa-data-source", xaDs);
        address.protect();
        final ModelNode operation = new ModelNode();
        operation.get(OP).set("add");
        operation.get(OP_ADDR).set(address);
        operation.get("name").set(xaDs);
        operation.get("jndi-name").set(xaDsJndi);
        operation.get("enabled").set(false);
        operation.get("driver-name").set("h2");
        operation.get("xa-datasource-class").set("org.jboss.as.connector.subsystems.datasources.ModifiableXaDataSource");
        operation.get("pool-name").set((xaDs + "_Pool"));
        operation.get("user-name").set("sa");
        operation.get("password").set("sa");
        executeOperation(operation);
        final ModelNode xaDatasourcePropertiesAddress = address.clone();
        xaDatasourcePropertiesAddress.add("xa-datasource-properties", "URL");
        xaDatasourcePropertiesAddress.protect();
        final ModelNode xaDatasourcePropertyOperation = new ModelNode();
        xaDatasourcePropertyOperation.get(OP).set("add");
        xaDatasourcePropertyOperation.get(OP_ADDR).set(xaDatasourcePropertiesAddress);
        xaDatasourcePropertyOperation.get("value").set("jdbc:h2:mem:test");
        executeOperation(xaDatasourcePropertyOperation);
        final ModelNode operation2 = new ModelNode();
        operation2.get(OP).set("write-attribute");
        operation2.get("name").set("enabled");
        operation2.get("value").set(true);
        operation2.get(OP_ADDR).set(address);
        executeOperation(operation2);
        List<ModelNode> newList = marshalAndReparseDsResources("xa-data-source");
        remove(address);
        Assert.assertNotNull("Reparsing failed:", newList);
        Assert.assertNotNull(findNodeWithProperty(newList, "jndi-name", xaDsJndi));
    }

    /**
     * AS7-2720 tests for parsing particular datasource in standalone mode
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddComplexDsUsername() throws Exception {
        testAddComplexDs(USER_PASSWORD);
    }

    @Test
    public void testAddComplexDsElytron() throws Exception {
        testAddComplexDs(ELYTRON);
    }

    @Test
    public void testAddComplexDsElytronAuthenticationContext() throws Exception {
        testAddComplexDs(ELYTRON_AUTHENTICATION_CONTEXT);
    }

    @Test
    public void testAddComplexDsSecurityDomain() throws Exception {
        testAddComplexDs(SECURITY_DOMAIN);
    }

    /**
     * AS7-2720 tests for parsing particular XA-datasource in standalone mode
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddComplexXaDsUsername() throws Exception {
        testAddComplexXaDs(USER_PASSWORD);
    }

    @Test
    public void testAddComplexXaDsElytron() throws Exception {
        testAddComplexXaDs(ELYTRON);
    }

    @Test
    public void testAddComplexXaDsElytronAuthenticationContext() throws Exception {
        testAddComplexXaDs(ELYTRON_AUTHENTICATION_CONTEXT);
    }

    @Test
    public void testAddComplexXaDsComplexDs() throws Exception {
        testAddComplexXaDs(SECURITY_DOMAIN);
    }
}

