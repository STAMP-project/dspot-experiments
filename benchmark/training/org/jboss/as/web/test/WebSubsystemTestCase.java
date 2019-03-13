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
package org.jboss.as.web.test;


import ModelTestControllerVersion.EAP_6_2_0;
import ModelTestControllerVersion.EAP_6_3_0;
import WebExtension.SUBSYSTEM_NAME;
import java.io.Serializable;
import org.jboss.as.controller.ProcessType;
import org.jboss.as.controller.RunningMode;
import org.jboss.as.model.test.FailedOperationTransformationConfig.RejectExpressionsConfig;
import org.jboss.as.model.test.ModelFixer;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.AdditionalInitialization;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.as.web.Constants;
import org.jboss.as.web.WebExtension;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.dmr.Property;
import org.junit.Test;


/**
 *
 *
 * @author Jean-Frederic Clere
 * @author Kabir Khan
 */
public class WebSubsystemTestCase extends AbstractSubsystemBaseTest {
    static {
        System.setProperty("jboss.server.config.dir", "target/jbossas");
    }

    public WebSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new WebExtension());
    }

    @Test
    public void testAliases() throws Exception {
        KernelServices services = createKernelServicesBuilder(createAdditionalInitialization()).setSubsystemXmlResource("subsystem.xml").build();
        ModelNode noAliasModel = services.readWholeModel();
        ModelNode aliasModel = services.readWholeModel(true);
        testSSLAlias(services, noAliasModel, aliasModel);
        testSSOAlias(services, noAliasModel, aliasModel);
        testAccessLogAlias(services, noAliasModel, aliasModel);
    }

    @Test
    public void testTransformationEAP620() throws Exception {
        testTransformation_1_3_0(EAP_6_2_0);
    }

    @Test
    public void testTransformationEAP630() throws Exception {
        testTransformation_1_4_0(EAP_6_3_0);
    }

    @Test
    public void testRejectingTransformersAS620() throws Exception {
        testRejectingTransformers_1_3_0(EAP_6_2_0);
    }

    @Test
    public void testRejectingTransformersAS630() throws Exception {
        testRejectingTransformers_1_4_0(EAP_6_3_0);
    }

    // TODO WFCORE-1353 means this doesn't have to always fail now; consider just deleting this
    // @Override
    // protected void validateDescribeOperation(KernelServices hc, AdditionalInitialization serverInit, ModelNode expectedModel) throws Exception {
    // final ModelNode operation = createDescribeOperation();
    // final ModelNode result = hc.executeOperation(operation);
    // Assert.assertTrue("The subsystem describe operation must fail",
    // result.hasDefined(ModelDescriptionConstants.FAILURE_DESCRIPTION));
    // }
    private static class SSLConfigurationNameFixer implements ModelFixer {
        private static final ModelFixer INSTANCE = new WebSubsystemTestCase.SSLConfigurationNameFixer();

        @Override
        public ModelNode fixModel(ModelNode modelNode) {
            // In the current and legacy models this is handled by a read attribute handler rather than existing in the model
            modelNode.get("connector", "https", "configuration", "ssl", "name").set("ssl");
            return modelNode;
        }
    }

    private static class AccessLogPrefixFixer_1_2_0 implements ModelFixer {
        private static final ModelFixer INSTANCE = new WebSubsystemTestCase.AccessLogPrefixFixer_1_2_0();

        @Override
        public ModelNode fixModel(ModelNode modelNode) {
            if (modelNode.hasDefined(Constants.VIRTUAL_SERVER)) {
                for (Property property : modelNode.get(Constants.VIRTUAL_SERVER).asPropertyList()) {
                    ModelNode virtualServer = property.getValue();
                    if (virtualServer.hasDefined(Constants.CONFIGURATION)) {
                        if (virtualServer.get(Constants.CONFIGURATION).hasDefined(Constants.ACCESS_LOG)) {
                            ModelNode prefix = virtualServer.get(Constants.CONFIGURATION, Constants.ACCESS_LOG, Constants.PREFIX);
                            if ((prefix.getType()) == (ModelType.BOOLEAN)) {
                                modelNode.get(Constants.VIRTUAL_SERVER, property.getName(), Constants.CONFIGURATION, Constants.ACCESS_LOG, Constants.PREFIX).set("access_log.");
                            }
                        }
                    }
                }
            }
            return modelNode;
        }
    }

    private static final class IntExpressionConfig extends RejectExpressionsConfig {
        public IntExpressionConfig(String... attributes) {
            // FIXME GlobalSessionTimeOutConfig constructor
            super(attributes);
        }

        @Override
        protected ModelNode correctValue(ModelNode toResolve, boolean isWriteAttribute) {
            ModelNode value = super.correctValue(toResolve, isWriteAttribute);
            return new ModelNode(value.asInt());
        }
    }

    private static final class BooleanExpressionConfig extends RejectExpressionsConfig {
        public BooleanExpressionConfig(String... attributes) {
            // FIXME GlobalSessionTimeOutConfig constructor
            super(attributes);
        }

        @Override
        protected ModelNode correctValue(ModelNode toResolve, boolean isWriteAttribute) {
            ModelNode value = super.correctValue(toResolve, isWriteAttribute);
            return new ModelNode(value.asBoolean());
        }
    }

    private static class TestAdditionalInitialization extends AdditionalInitialization implements Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        protected ProcessType getProcessType() {
            return ProcessType.HOST_CONTROLLER;
        }

        @Override
        protected RunningMode getRunningMode() {
            return RunningMode.ADMIN_ONLY;
        }
    }
}

