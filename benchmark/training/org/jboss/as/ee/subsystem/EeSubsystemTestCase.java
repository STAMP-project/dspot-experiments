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
package org.jboss.as.ee.subsystem;


import EeExtension.SUBSYSTEM_NAME;
import GlobalModulesDefinition.INSTANCE;
import ModelTestControllerVersion.EAP_6_2_0;
import ModelTestControllerVersion.EAP_6_3_0;
import ModelTestControllerVersion.EAP_6_4_0;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.model.test.FailedOperationTransformationConfig.AttributesPathAddressConfig;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.dmr.ModelNode;
import org.junit.Test;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author Eduardo Martins
 */
public class EeSubsystemTestCase extends AbstractSubsystemBaseTest {
    public EeSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new EeExtension());
    }

    @Test
    public void testTransformersEAP620() throws Exception {
        testTransformers1_1(EAP_6_2_0, ModelVersion.create(1, 0));
    }

    @Test
    public void testTransformersEAP630() throws Exception {
        testTransformers1_1(EAP_6_3_0, ModelVersion.create(1, 1));
    }

    @Test
    public void testTransformersEAP640() throws Exception {
        testTransformers1_1(EAP_6_4_0, ModelVersion.create(1, 1));
    }

    @Test
    public void testTransformersEAP620Reject() throws Exception {
        testTransformers1_0_x_reject(EAP_6_2_0, ModelVersion.create(1, 0));
    }

    @Test
    public void testTransformersEAP630Reject() throws Exception {
        testTransformers1_1_x_reject(EAP_6_3_0);
    }

    @Test
    public void testTransformersEAP640Reject() throws Exception {
        testTransformers1_1_x_reject(EAP_6_4_0);
    }

    @Test
    public void testTransformersDiscardsImpliedValuesEAP620() throws Exception {
        testTransformersDiscardsImpliedValues1_0_0(EAP_6_2_0);
    }

    @Test
    public void testTransformersDiscardsImpliedValuesEAP630() throws Exception {
        testTransformersDiscardsImpliedValues1_1_0(EAP_6_3_0);
    }

    @Test
    public void testTransformersDiscardsImpliedValuesEAP640() throws Exception {
        testTransformersDiscardsImpliedValues1_1_0(EAP_6_4_0);
    }

    private static final class GlobalModulesConfig extends AttributesPathAddressConfig<EeSubsystemTestCase.GlobalModulesConfig> {
        public GlobalModulesConfig() {
            super(INSTANCE.getName());
        }

        @Override
        protected boolean isAttributeWritable(String attributeName) {
            return true;
        }

        @Override
        protected boolean checkValue(String attrName, ModelNode attribute, boolean isWriteAttribute) {
            for (ModelNode module : attribute.asList()) {
                if (((module.has(GlobalModulesDefinition.ANNOTATIONS)) || (module.has(GlobalModulesDefinition.META_INF))) || (module.has(GlobalModulesDefinition.SERVICES))) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected ModelNode correctValue(ModelNode toResolve, boolean isWriteAttribute) {
            for (ModelNode module : toResolve.asList()) {
                module.remove(GlobalModulesDefinition.ANNOTATIONS);
                module.remove(GlobalModulesDefinition.META_INF);
                module.remove(GlobalModulesDefinition.SERVICES);
            }
            return toResolve;
        }
    }
}

