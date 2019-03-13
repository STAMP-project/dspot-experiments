/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ee.injection.resource.jndi.bad;


import ModelDescriptionConstants.ADDRESS;
import ModelDescriptionConstants.DEPLOY;
import ModelDescriptionConstants.DEPLOYMENT;
import ModelDescriptionConstants.ENABLED;
import ModelDescriptionConstants.FAILURE_DESCRIPTION;
import ModelDescriptionConstants.OP;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author baranowb
 */
@RunWith(Arquillian.class)
@RunAsClient
public class BadResourceTestCase {
    private static ModelControllerClient controllerClient = TestSuiteEnvironment.getModelControllerClient();

    @Test
    public void testBadDU() throws Exception {
        final ModelNode deployOp = new ModelNode();
        deployOp.get(OP).set(DEPLOY);
        deployOp.get(ADDRESS).add(DEPLOYMENT, Constants.TESTED_DU_NAME);
        deployOp.get(ENABLED).set(true);
        final OperationBuilder ob = new OperationBuilder(deployOp, true);
        final ModelNode result = BadResourceTestCase.controllerClient.execute(ob.build());
        // just to blow up
        Assert.assertTrue(("Failed to deploy: " + result), (!(Operations.isSuccessfulOutcome(result))));
        // asserts
        String failureDescription = result.get(FAILURE_DESCRIPTION).toString();
        Assert.assertThat(String.format("Results doesn't contain correct error code (%s): %s", Constants.ERROR_MESSAGE, result.toString()), failureDescription, CoreMatchers.containsString(Constants.ERROR_MESSAGE));
        Assert.assertThat(String.format("Results doesn't contain correct JNDI in error message (%s): %s", Constants.JNDI_NAME_BAD, result.toString()), failureDescription, CoreMatchers.containsString(Constants.JNDI_NAME_BAD));
    }
}

