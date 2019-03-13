/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.jca.datasource;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.jca.JcaMgmtBase;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the situation when abstract DataSource class is specified when creating a data source.
 *
 * @author lgao
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DatasourceWrongDsClassTestCase extends JcaMgmtBase {
    private static final String DEPLOYMENT = "dummydriver";

    @Test
    public void testJdbcDrivers() throws Exception {
        String driverName = (DatasourceWrongDsClassTestCase.DEPLOYMENT) + ".jar";
        ModelNode address = new ModelNode().add(SUBSYSTEM, "datasources");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("get-installed-driver");
        operation.get(OP_ADDR).set(address);
        operation.get("driver-name").set(driverName);
        ModelNode result = executeOperation(operation).asList().get(0);
        Assert.assertEquals(driverName, result.get("driver-name").asString());
        Assert.assertEquals(driverName, result.get("deployment-name").asString());
        Assert.assertEquals(TestDriver.class.getName(), result.get("driver-class-name").asString());
        Assert.assertEquals(1, result.get("driver-major-version").asInt());
        Assert.assertEquals(0, result.get("driver-minor-version").asInt());
        Assert.assertFalse(result.get("jdbc-compliant").asBoolean());
        String driver2 = ((driverName + "_") + (TestDriver2.class.getName())) + "_1_1";
        operation.get("driver-name").set(driver2);
        result = executeOperation(operation).asList().get(0);
        Assert.assertEquals(driver2, result.get("driver-name").asString());
        Assert.assertEquals(driver2, result.get("deployment-name").asString());
        Assert.assertEquals(TestDriver2.class.getName(), result.get("driver-class-name").asString());
        Assert.assertEquals(1, result.get("driver-major-version").asInt());
        Assert.assertEquals(1, result.get("driver-minor-version").asInt());
        Assert.assertTrue(result.get("jdbc-compliant").asBoolean());
    }

    @Test
    public void testWrongDSClass() throws Exception {
        String driverName = (DatasourceWrongDsClassTestCase.DEPLOYMENT) + ".jar";
        ModelNode address = getDataSourceAddress("wrongClsDs");
        ModelNode operation = getDataSourceOperation(address, "java:/wrongClsDs", driverName, DummyDataSource.class.getName());
        try {
            executeOperation(operation);
            Assert.fail("Not supposed to succeed");
        } catch (MgmtOperationException e) {
            ModelNode result = e.getResult();
            Assert.assertEquals("failed", result.get("outcome").asString());
            String failDesc = result.get("failure-description").asString();
            Assert.assertTrue(failDesc.contains("WFLYJCA0117"));
            return;
        }
        Assert.fail("Not supposed to be here");
    }

    @Test
    public void testWrongXADSClass() throws Exception {
        String driverName = (DatasourceWrongDsClassTestCase.DEPLOYMENT) + ".jar";
        ModelNode address = getXADataSourceAddress("wrongXAClsDs");
        ModelNode operation = getXADataSourceOperation(address, "java:/wrongXAClsDs", driverName, DummyXADataSource.class.getName());
        try {
            executeOperation(operation);
            Assert.fail("Not supposed to succeed");
        } catch (MgmtOperationException e) {
            ModelNode result = e.getResult();
            Assert.assertEquals("failed", result.get("outcome").asString());
            return;
        }
        Assert.fail("Not supposed to be here");
    }
}

