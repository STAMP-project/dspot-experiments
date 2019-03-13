/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.wildfly.extension.mod_cluster;


import CustomLoadMetricResourceDefinition.Attribute.MODULE;
import DynamicLoadProviderResourceDefinition.Attribute.INITIAL_LOAD;
import ModClusterExtension.SUBSYSTEM_NAME;
import ModelTestControllerVersion.EAP_6_4_0;
import ModelTestControllerVersion.EAP_7_0_0;
import ModelTestControllerVersion.EAP_7_1_0;
import ModelTestControllerVersion.EAP_7_2_0;
import ProxyConfigurationResourceDefinition.Attribute.PROXIES;
import ProxyConfigurationResourceDefinition.Attribute.STATUS_INTERVAL;
import org.jboss.as.model.test.FailedOperationTransformationConfig;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.jboss.dmr.ModelNode;
import org.junit.Test;


/**
 *
 *
 * @author Radoslav Husar
 */
public class ModClusterTransformersTestCase extends AbstractSubsystemTest {
    public ModClusterTransformersTestCase() {
        super(SUBSYSTEM_NAME, new ModClusterExtension());
    }

    @Test
    public void testTransformerEAP_6_4_0() throws Exception {
        this.testTransformation(EAP_6_4_0);
    }

    @Test
    public void testTransformerEAP_7_0_0() throws Exception {
        this.testTransformation(EAP_7_0_0);
    }

    @Test
    public void testTransformerEAP_7_1_0() throws Exception {
        this.testTransformation(EAP_7_1_0);
    }

    @Test
    public void testTransformerEAP_7_2_0() throws Exception {
        this.testTransformation(EAP_7_2_0);
    }

    @Test
    public void testRejectionsEAP_6_4_0() throws Exception {
        this.testRejections(EAP_6_4_0);
    }

    @Test
    public void testRejectionsEAP_7_0_0() throws Exception {
        this.testRejections(EAP_7_0_0);
    }

    @Test
    public void testRejectionsEAP_7_1_0() throws Exception {
        this.testRejections(EAP_7_1_0);
    }

    @Test
    public void testRejectionsEAP_7_2_0() throws Exception {
        this.testRejections(EAP_7_2_0);
    }

    static class ProxiesConfig extends FailedOperationTransformationConfig.AttributesPathAddressConfig<ModClusterTransformersTestCase.ProxiesConfig> {
        ProxiesConfig() {
            super(PROXIES.getName());
        }

        @Override
        protected boolean isAttributeWritable(String attributeName) {
            return true;
        }

        @Override
        protected boolean checkValue(String attrName, ModelNode attribute, boolean isWriteAttribute) {
            return !(attribute.equals(new ModelNode()));
        }

        @Override
        protected ModelNode correctValue(ModelNode toResolve, boolean isWriteAttribute) {
            return new ModelNode();
        }
    }

    static class StatusIntervalConfig extends FailedOperationTransformationConfig.AttributesPathAddressConfig<ModClusterTransformersTestCase.StatusIntervalConfig> {
        StatusIntervalConfig() {
            super(STATUS_INTERVAL.getName());
        }

        @Override
        protected boolean isAttributeWritable(String attributeName) {
            return true;
        }

        @Override
        protected boolean checkValue(String attrName, ModelNode attribute, boolean isWriteAttribute) {
            return !(attribute.equals(new ModelNode(10)));
        }

        @Override
        protected ModelNode correctValue(ModelNode toResolve, boolean isWriteAttribute) {
            return new ModelNode(10);
        }
    }

    static class ModuleAttributeTransformationConfig extends FailedOperationTransformationConfig.AttributesPathAddressConfig<ModClusterTransformersTestCase.ModuleAttributeTransformationConfig> {
        ModuleAttributeTransformationConfig() {
            super(MODULE.getName());
        }

        @Override
        protected boolean isAttributeWritable(String attributeName) {
            return true;
        }

        @Override
        protected boolean checkValue(String attrName, ModelNode attribute, boolean isWriteAttribute) {
            return !(attribute.equals(MODULE.getDefinition().getDefaultValue()));
        }

        @Override
        protected ModelNode correctValue(ModelNode toResolve, boolean isWriteAttribute) {
            return MODULE.getDefinition().getDefaultValue();
        }
    }

    static class InitialLoadFailedAttributeConfig extends FailedOperationTransformationConfig.AttributesPathAddressConfig {
        InitialLoadFailedAttributeConfig() {
            super(INITIAL_LOAD.getName());
        }

        @Override
        protected boolean isAttributeWritable(String attributeName) {
            return true;
        }

        protected boolean checkValue(String attrName, ModelNode attribute, boolean isGeneratedWriteAttribute) {
            return !(attribute.equals(new ModelNode((-1))));
        }

        @Override
        protected ModelNode correctValue(ModelNode toResolve, boolean isGeneratedWriteAttribute) {
            return new ModelNode((-1));
        }
    }
}

