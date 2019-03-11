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
package org.jboss.as.connector.subsystems.resourceadapters;


import ModelTestControllerVersion.EAP_6_2_0;
import ModelTestControllerVersion.EAP_7_0_0;
import ResourceAdaptersExtension.SUBSYSTEM_NAME;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.junit.Test;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ResourceAdaptersSubsystemTestCase extends AbstractSubsystemBaseTest {
    public ResourceAdaptersSubsystemTestCase() {
        // FIXME ResourceAdaptersSubsystemTestCase constructor
        super(SUBSYSTEM_NAME, new ResourceAdaptersExtension());
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }

    @Test
    public void testFullConfig() throws Exception {
        standardSubsystemTest("resource-adapters-pool.xml", null, true);
    }

    @Test
    public void testFullConfigXa() throws Exception {
        standardSubsystemTest("resource-adapters-xapool.xml", null, true);
    }

    @Test
    public void testExpressionConfig() throws Exception {
        standardSubsystemTest("resource-adapters-pool-expression.xml", "resource-adapters-pool.xml", true);
    }

    @Test
    public void testExpressionConfigXa() throws Exception {
        standardSubsystemTest("resource-adapters-xapool-expression.xml", "resource-adapters-xapool.xml", true);
    }

    @Test
    public void testTransformerEAP62() throws Exception {
        testRejectingTransformer("resource-adapters-pool-20.xml", EAP_6_2_0, ModelVersion.create(1, 3, 0));
    }

    @Test
    public void testExpressionsEAP62() throws Exception {
        // this file contain expression for all supported fields except bean-validation-groups and recovery-plugin-properties
        // for a limitation in test suite not permitting to have expression in type LIST or OBJECT for legacyServices
        testTransformer("resource-adapters-xapool-expression2.xml", EAP_6_2_0, ModelVersion.create(1, 3, 0));
    }

    @Test
    public void testElytronEnabledEAP62() throws Exception {
        testRejectingTransformerElytronEnabled("resource-adapters-pool-elytron-enabled.xml", EAP_6_2_0, ModelVersion.create(1, 3, 0));
    }

    @Test
    public void testElytronEnabledEAP7() throws Exception {
        testRejectingTransformer7ElytronEnabled("resource-adapters-pool-elytron-enabled.xml", EAP_7_0_0, ModelVersion.create(4, 0, 0));
    }
}

