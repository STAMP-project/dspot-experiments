/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.jca.ijdeployment;


import java.util.Properties;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * JBQA-6277 -IronJacamar deployments subsystem test case
 *
 * @author <a href="vrastsel@redhat.com">Vladimir Rastseluev</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class IronJacamarDeploymentTestCase extends ContainerResourceMgmtTestBase {
    /**
     * Test configuration - if all properties propagated to the model
     *
     * @throws Throwable
     * 		Thrown if case of an error
     */
    @Test
    public void testConfiguration() throws Throwable {
        ModelNode address = new ModelNode();
        address.add("deployment", "ij.rar").add("subsystem", "resource-adapters").add("ironjacamar", "ironjacamar").add("resource-adapter", "ij.rar");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("read-resource");
        operation.get(OP_ADDR).set(address);
        operation.get(RECURSIVE).set(true);
        ModelNode result = executeOperation(operation);
        Assert.assertEquals("Bootstrap-context value is wrong", result.get("bootstrap-context").asString(), "default");
        Assert.assertEquals("Transaction-support value is wrong", result.get("transaction-support").asString(), "XATransaction");
        Assert.assertEquals("RA config property value is wrong", result.get("config-properties", "raProperty", "value").asString(), "4");
        Assert.assertEquals("Bean validation groups set wrong", result.get("beanvalidationgroups").asString(), "[\"org.jboss.as.test.integration.jca.beanvalidation.ra.ValidGroup\",\"org.jboss.as.test.integration.jca.beanvalidation.ra.ValidGroup1\"]");
        ModelNode node = result.get("admin-objects", "java:jboss/VAO");
        Properties params = getAOProperties(true);
        Assert.assertTrue(((("compare failed, node:" + (node.asString())) + "\nparams:") + params), checkModelParams(node, params));
        Assert.assertEquals("AO config property value is wrong", node.get("config-properties", "aoProperty", "value").asString(), "admin");
        node = result.get("admin-objects", "java:jboss/VAO1");
        params = getAOProperties(false);
        Assert.assertTrue(((("compare failed, node:" + (node.asString())) + "\nparams:") + params), checkModelParams(node, params));
        Assert.assertEquals("AO1 config property value is wrong", node.get("config-properties", "aoProperty", "value").asString(), "admin1");
        node = result.get("connection-definitions", "java:jboss/VCF");
        params = getCFProperties(true);
        Assert.assertTrue(((("compare failed, node:" + (node.asString())) + "\nparams:") + params), checkModelParams(node, params));
        Assert.assertEquals("CF config property value is wrong", node.get("config-properties", "cfProperty", "value").asString(), "first");
        Assert.assertEquals("Recovery plugin property value is wrong", "C", node.get("recovery-plugin-properties", "Property").asString());
        node = result.get("connection-definitions", "java:jboss/VCF1");
        params = getCFProperties(false);
        Assert.assertTrue(((("compare failed, node:" + (node.asString())) + "\nparams:") + params), checkModelParams(node, params));
        Assert.assertEquals("CF1 config property value is wrong", node.get("config-properties", "cfProperty", "value").asString(), "2nd");
        Assert.assertEquals("Recovery plugin 1 property value is wrong", "C", node.get("recovery-plugin-properties", "Property").asString());
    }
}

