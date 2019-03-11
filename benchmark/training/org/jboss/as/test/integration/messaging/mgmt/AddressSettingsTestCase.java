/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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


import ModelDescriptionConstants.NAME;
import ModelDescriptionConstants.OP;
import ModelDescriptionConstants.OP_ADDR;
import ModelDescriptionConstants.VALUE;
import ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION;
import java.util.Set;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Emanuel Muckenhuber
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AddressSettingsTestCase extends ContainerResourceMgmtTestBase {
    private static final String ACTIVEMQ_ADDRESS = "activemq-address";

    private static final String MESSAGE_COUNTER_HISTORY_DAY_LIMIT = "message-counter-history-day-limit";

    private static final String RESOLVE_ADDRESS_SETTING = "resolve-address-setting";

    private ModelNode defaultAddress;

    private ModelNode address;

    @ContainerResource
    private ManagementClient managementClient;

    private JMSOperations jmsOperations;

    @Test
    public void testAddressSettingWrite() throws Exception {
        // <jms server address>/address-setting=jms.queue.foo:write-attribute(name=redelivery-delay,value=50)
        final ModelNode add = new ModelNode();
        add.get(OP).set(ADD);
        add.get(OP_ADDR).set(address);
        executeOperation(add);
        final ModelNode update = new ModelNode();
        update.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        update.get(OP_ADDR).set(address);
        update.get(NAME).set("redistribution-delay");
        update.get(VALUE).set((-1L));
        executeOperation(update);
        remove(address);
    }

    @Test
    public void testResolveAddressSettings() throws Exception {
        final ModelNode readResourceDescription = new ModelNode();
        readResourceDescription.get(OP_ADDR).set(defaultAddress);
        readResourceDescription.get(OP).set(READ_RESOURCE_DESCRIPTION_OPERATION);
        final ModelNode description = executeOperation(readResourceDescription, true);
        Set<String> attributeNames = description.get(ATTRIBUTES).keys();
        final ModelNode readResource = new ModelNode();
        readResource.get(OP_ADDR).set(defaultAddress);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        final ModelNode defaultAddressSetting = executeOperation(readResource);
        // there is no address-setting for the given address but
        // we can resolve its settings based on HornetQ hierarchical
        // repository of address setting.
        final ModelNode resolve = new ModelNode();
        resolve.get(OP_ADDR).set(jmsOperations.getServerAddress());
        resolve.get(OP).set(AddressSettingsTestCase.RESOLVE_ADDRESS_SETTING);
        resolve.get(AddressSettingsTestCase.ACTIVEMQ_ADDRESS).set("jms.queue.foo");
        ModelNode result = executeOperation(resolve);
        for (String attributeName : attributeNames) {
            Assert.assertEquals(("unexpected value for " + attributeName), defaultAddressSetting.get(attributeName), result.get(attributeName));
        }
    }

    @Test
    public void testSpecificAddressSetting() throws Exception {
        // <jms server address>/address-setting=jms.queue.foo:add()
        final ModelNode add = new ModelNode();
        add.get(OP).set(ADD);
        add.get(OP_ADDR).set(address);
        executeOperation(add);
        final ModelNode readResourceWithoutDefault = new ModelNode();
        readResourceWithoutDefault.get(OP).set(READ_RESOURCE_OPERATION);
        readResourceWithoutDefault.get(OP_ADDR).set(address);
        readResourceWithoutDefault.get(INCLUDE_DEFAULTS).set(false);
        ModelNode result = executeOperation(readResourceWithoutDefault);
        // the resource has not defined the message-counter-history-day-limit attribute
        Assert.assertFalse(result.hasDefined(AddressSettingsTestCase.MESSAGE_COUNTER_HISTORY_DAY_LIMIT));
        final ModelNode resolve = new ModelNode();
        resolve.get(OP_ADDR).set(jmsOperations.getServerAddress());
        resolve.get(OP).set(AddressSettingsTestCase.RESOLVE_ADDRESS_SETTING);
        resolve.get(AddressSettingsTestCase.ACTIVEMQ_ADDRESS).set("jms.queue.foo");
        result = executeOperation(resolve);
        // inherit the message-counter-history-day-limit for the '#' address-setting
        Assert.assertEquals(10, result.get(AddressSettingsTestCase.MESSAGE_COUNTER_HISTORY_DAY_LIMIT).asInt());
        remove(address);
    }
}

