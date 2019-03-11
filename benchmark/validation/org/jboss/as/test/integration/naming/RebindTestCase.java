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


import java.net.URL;
import javax.ejb.EJB;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.naming.NamingContext;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case for changing JBDI bound values in the naming subsystem without forcing a reload/restart (see WFLY-3239).
 * Uses AS controller to do the bind/rebind, lookup is through an EJB.
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class RebindTestCase {
    @ArquillianResource
    private ManagementClient managementClient;

    @EJB(mappedName = "java:global/RebindTestCase/BindingLookupBean")
    private BindingLookupBean bean;

    @Test
    public void testRebinding() throws Exception {
        final String name = "java:global/rebind";
        final String lookup = "java:global/lookup";
        Exception error = null;
        try {
            ModelNode operation = prepareAddBindingOperation(name, SIMPLE);
            operation.get(VALUE).set("http://localhost");
            operation.get(TYPE).set(URL.class.getName());
            ModelNode operationResult = managementClient.getControllerClient().execute(operation);
            verifyBinding(operationResult, name, "http://localhost");
            operation = prepareRebindOperation(name, SIMPLE);
            operation.get(VALUE).set("http://localhost2");
            operation.get(TYPE).set(URL.class.getName());
            operationResult = managementClient.getControllerClient().execute(operation);
            verifyBinding(operationResult, name, "http://localhost2");
            operation = prepareRebindOperation(name, SIMPLE);
            operation.get(VALUE).set("2");
            operation.get(TYPE).set(Integer.class.getName());
            operationResult = managementClient.getControllerClient().execute(operation);
            verifyBinding(operationResult, name, "2");
            operation = prepareAddBindingOperation(lookup, SIMPLE);
            operation.get(VALUE).set("looked up");
            operation.get(TYPE).set(String.class.getName());
            operationResult = managementClient.getControllerClient().execute(operation);
            verifyBinding(operationResult, lookup, "looked up");
            operation = prepareRebindOperation(name, LOOKUP);
            operation.get(LOOKUP).set(lookup);
            operationResult = managementClient.getControllerClient().execute(operation);
            verifyBinding(operationResult, name, "looked up");
            operation = prepareReadResourceOperation(name);
            operationResult = managementClient.getControllerClient().execute(operation);
            Assert.assertFalse(operationResult.get(FAILURE_DESCRIPTION).toString(), operationResult.get(FAILURE_DESCRIPTION).isDefined());
            Assert.assertEquals("java:global/lookup", operationResult.get(RESULT).get(LOOKUP).asString());
        } catch (Exception e) {
            error = e;
            throw e;
        } finally {
            removeBinding(name, error);
            removeBinding(lookup, error);
        }
    }

    @Test
    public void testRebindingObjectFactory() throws Exception {
        final String bindingName = "java:global/bind";
        Exception error = null;
        try {
            ModelNode operation = prepareAddBindingOperation(bindingName, SIMPLE);
            operation.get(VALUE).set("2");
            operation.get(TYPE).set(Integer.class.getName());
            ModelNode operationResult = managementClient.getControllerClient().execute(operation);
            verifyBindingClass(operationResult, bindingName, Integer.class.getName());
            operation = prepareRebindOperation(bindingName, OBJECT_FACTORY);
            operation.get("module").set("org.jboss.as.naming");
            operation.get("class").set("org.jboss.as.naming.interfaces.java.javaURLContextFactory");
            operationResult = managementClient.getControllerClient().execute(operation);
            verifyBindingClass(operationResult, bindingName, NamingContext.class.getName());
        } catch (Exception e) {
            error = e;
            throw e;
        } finally {
            removeBinding(bindingName, error);
        }
    }

    @Test
    public void testRebindingLookup() throws Exception {
        final String simpleBindingName1 = "java:global/simple1";
        final String simpleBindingName2 = "java:global/simple2";
        final String lookupBindingName = "java:global/lookup";
        Exception error = null;
        try {
            ModelNode operation = prepareAddBindingOperation(simpleBindingName1, SIMPLE);
            operation.get(VALUE).set("simple1");
            operation.get(TYPE).set(String.class.getName());
            ModelNode operationResult = managementClient.getControllerClient().execute(operation);
            verifyBinding(operationResult, simpleBindingName1, "simple1");
            operation = prepareAddBindingOperation(simpleBindingName2, SIMPLE);
            operation.get(VALUE).set("simple2");
            operation.get(TYPE).set(String.class.getName());
            operationResult = managementClient.getControllerClient().execute(operation);
            verifyBinding(operationResult, simpleBindingName2, "simple2");
            operation = prepareAddBindingOperation(lookupBindingName, LOOKUP);
            operation.get(LOOKUP).set(simpleBindingName1);
            operationResult = managementClient.getControllerClient().execute(operation);
            verifyBinding(operationResult, lookupBindingName, "simple1");
            operation = prepareRebindOperation(lookupBindingName, LOOKUP);
            operation.get(LOOKUP).set(simpleBindingName2);
            operationResult = managementClient.getControllerClient().execute(operation);
            verifyBinding(operationResult, lookupBindingName, "simple2");
        } catch (Exception e) {
            error = e;
            throw e;
        } finally {
            removeBinding(simpleBindingName1, error);
            removeBinding(simpleBindingName2, error);
            removeBinding(lookupBindingName, error);
        }
    }
}

