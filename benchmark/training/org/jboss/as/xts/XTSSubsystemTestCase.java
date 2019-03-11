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
package org.jboss.as.xts;


import ModelTestControllerVersion.EAP_6_4_0;
import ModelTestControllerVersion.EAP_7_0_0;
import ModelTestControllerVersion.EAP_7_1_0;
import XTSExtension.SUBSYSTEM_NAME;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.junit.Test;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class XTSSubsystemTestCase extends AbstractSubsystemBaseTest {
    // These are named for the last version supporting a given model version
    private static final ModelVersion EAP6_4_0 = ModelVersion.create(1, 0, 0);

    private static final ModelVersion EAP7_1_0 = ModelVersion.create(2, 0, 0);

    private static final ModelVersion EAP7_2_0 = ModelVersion.create(3, 0, 0);

    public XTSSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new XTSExtension());
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }

    @Test
    public void testTransformersEAP_6_4_0() throws Exception {
        // EAP 6.4.0 still uses 1.0.0 XTS subsystem version
        testTransformation(EAP_6_4_0, XTSSubsystemTestCase.EAP6_4_0, "jboss-as-xts");
    }

    @Test
    public void testTransformersEAP_7_0_0() throws Exception {
        testTransformation(EAP_7_0_0, XTSSubsystemTestCase.EAP7_1_0, "wildfly-xts");
    }

    @Test
    public void testTransformersEAP_7_1_0() throws Exception {
        testTransformation(EAP_7_1_0, XTSSubsystemTestCase.EAP7_1_0, "wildfly-xts");
    }

    @Test
    public void testTransformersFailedEAP_6_4_0() throws Exception {
        testRejectTransformation(EAP_6_4_0, XTSSubsystemTestCase.EAP6_4_0, "jboss-as-xts");
    }

    @Test
    public void testTransformersFailedEAP_7_0_0() throws Exception {
        testRejectTransformation(EAP_7_0_0, XTSSubsystemTestCase.EAP7_1_0, "wildfly-xts");
    }

    @Test
    public void testTransformersFailedEAP_7_1_0() throws Exception {
        testRejectTransformation(EAP_7_1_0, XTSSubsystemTestCase.EAP7_1_0, "wildfly-xts");
    }
}

