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
package org.jboss.as.test.integration.naming;


import NamingExtension.SUBSYSTEM_NAME;
import java.net.URL;
import javax.ejb.EJB;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case for binding of {@link URL} (see AS7-5140). Uses AS controller to do the bind, lookup is through an EJB.
 *
 * @author Eduardo Martins
 */
@RunWith(Arquillian.class)
public class URLBindingTestCase {
    @ArquillianResource
    private ManagementClient managementClient;

    @EJB(mappedName = "java:global/URLBindingTestCaseBean/BindingLookupBean")
    private BindingLookupBean bean;

    @Test
    public void testURLBinding() throws Exception {
        final String name = "java:global/as75140";
        final ModelNode address = new ModelNode();
        address.add(SUBSYSTEM, SUBSYSTEM_NAME);
        address.add(BINDING, name);
        // bind a URL
        final ModelNode bindingAdd = new ModelNode();
        bindingAdd.get(OP).set(ADD);
        bindingAdd.get(OP_ADDR).set(address);
        bindingAdd.get(BINDING_TYPE).set(SIMPLE);
        bindingAdd.get(VALUE).set("http://localhost");
        bindingAdd.get(TYPE).set(URL.class.getName());
        try {
            final ModelNode addResult = managementClient.getControllerClient().execute(bindingAdd);
            Assert.assertFalse(addResult.get(FAILURE_DESCRIPTION).toString(), addResult.get(FAILURE_DESCRIPTION).isDefined());
            Assert.assertEquals("http://localhost", bean.lookupBind(name).toString());
        } finally {
            // unbind it
            final ModelNode bindingRemove = new ModelNode();
            bindingRemove.get(OP).set(REMOVE);
            bindingRemove.get(OP_ADDR).set(address);
            bindingRemove.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            final ModelNode removeResult = managementClient.getControllerClient().execute(bindingRemove);
            Assert.assertFalse(removeResult.get(FAILURE_DESCRIPTION).toString(), removeResult.get(FAILURE_DESCRIPTION).isDefined());
        }
    }
}

