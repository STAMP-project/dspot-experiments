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
package org.jboss.as.test.integration.messaging.mgmt;


import ModelType.INT;
import ModelType.LIST;
import ModelType.LONG;
import ModelType.OBJECT;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the management API for HornetQ core addresss.
 *
 * @author Brian Stansberry (c) 2011 Red Hat Inc.
 */
@RunAsClient
@RunWith(Arquillian.class)
public class AddressControlManagementTestCase {
    private static long count = System.currentTimeMillis();

    @ContainerResource
    private static ManagementClient managementClient;

    private JMSOperations jmsOperations;

    @Test
    public void testSubsystemRootOperations() throws Exception {
        ModelNode op = getSubsystemOperation("read-children-types");
        op.get("child-type").set("core-address");
        ModelNode result = execute(op, true);
        Assert.assertTrue(result.isDefined());
        boolean found = false;
        for (ModelNode type : result.asList()) {
            if ("core-address".equals(type.asString())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
        op = getSubsystemOperation("read-children-names");
        op.get("child-type").set("core-address");
        result = execute(op, true);
        Assert.assertTrue(result.isDefined());
        found = false;
        for (ModelNode address : result.asList()) {
            if (AddressControlManagementTestCase.getAddress().equals(address.asString())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
        op = getSubsystemOperation("read-children-resources");
        op.get("child-type").set("core-address");
        result = execute(op, true);
        Assert.assertTrue(result.isDefined());
        found = false;
        for (Property address : result.asPropertyList()) {
            if (AddressControlManagementTestCase.getAddress().equals(address.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testAddressGlobalOperations() throws Exception {
        ModelNode op = getAddressOperation("read-children-types");
        op.get("child-type").set("core-address");
        ModelNode result = execute(op, true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(1, result.asInt());
        op = getAddressOperation("read-children-names");
        op.get("child-type").set("role");
        result = execute(op, true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(1, result.asInt());
        op = getAddressOperation("read-children-resources");
        op.get("child-type").set("role");
        result = execute(op, true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(1, result.asInt());
    }

    @Test
    public void testReadResource() throws Exception {
        ModelNode op = getAddressOperation("read-resource");
        op.get("include-runtime").set(true);
        op.get("recursive").set(true);
        ModelNode result = execute(op, true);
        Assert.assertEquals(OBJECT, result.getType());
        Assert.assertEquals(OBJECT, result.get("role").getType());
        Assert.assertEquals(INT, result.get("number-of-pages").getType());
        Assert.assertEquals(LONG, result.get("number-of-bytes-per-page").getType());
        Assert.assertEquals(LIST, result.get("binding-names").getType());
        boolean foundMain = false;
        boolean foundOther = false;
        for (ModelNode node : result.get("binding-names").asList()) {
            if (AddressControlManagementTestCase.getQueueName().equals(node.asString())) {
                Assert.assertFalse(foundMain);
                foundMain = true;
            } else
                if (AddressControlManagementTestCase.getOtherQueueName().equals(node.asString())) {
                    Assert.assertFalse(foundOther);
                    foundOther = true;
                }

        }
        Assert.assertTrue(foundMain);
        Assert.assertTrue(foundOther);
    }
}

