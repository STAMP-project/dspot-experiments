/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.jaxr.extension;


import JAXRExtension.SUBSYSTEM_NAME;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.junit.Test;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
// TODO WFCORE-1353 means this doesn't have to always fail now; consider just deleting this
// @Override
// protected void validateDescribeOperation(KernelServices hc, AdditionalInitialization serverInit, ModelNode expectedModel) throws Exception {
// final ModelNode operation = createDescribeOperation();
// final ModelNode result = hc.executeOperation(operation);
// Assert.assertTrue("The subsystem describe operation must fail",
// result.hasDefined(ModelDescriptionConstants.FAILURE_DESCRIPTION));
// }
public class JaxrSubsystemTestCase extends AbstractSubsystemBaseTest {
    public JaxrSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new JAXRExtension());
    }

    @Test
    public void testFullConfig() throws Exception {
        standardSubsystemTest("xsd1_1full.xml");
    }

    @Test
    public void testFullExpressions() throws Exception {
        standardSubsystemTest("xsd1_1expressions.xml");
    }

    @Test
    public void testSubsystem1_0() throws Exception {
        standardSubsystemTest("xsd1_0.xml", false);
    }
}

