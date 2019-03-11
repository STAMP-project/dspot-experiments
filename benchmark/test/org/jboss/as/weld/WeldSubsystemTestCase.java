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
package org.jboss.as.weld;


import AdditionalInitialization.MANAGEMENT;
import ModelTestControllerVersion.EAP_6_2_0;
import ModelTestControllerVersion.EAP_6_3_0;
import ModelTestControllerVersion.EAP_6_4_0;
import ModelTestControllerVersion.EAP_7_0_0;
import WeldExtension.PATH_SUBSYSTEM;
import WeldExtension.SUBSYSTEM_NAME;
import WeldResourceDefinition.NON_PORTABLE_MODE_ATTRIBUTE;
import WeldResourceDefinition.REQUIRE_BEAN_DESCRIPTOR_ATTRIBUTE;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.model.test.FailedOperationTransformationConfig;
import org.jboss.as.model.test.FailedOperationTransformationConfig.ChainedConfig;
import org.jboss.as.model.test.ModelTestUtils;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.as.subsystem.test.KernelServicesBuilder;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;

import static WeldResourceDefinition.THREAD_POOL_SIZE_ATTRIBUTE;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class WeldSubsystemTestCase extends AbstractSubsystemBaseTest {
    public WeldSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new WeldExtension());
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }

    @Test
    public void testSubsystem10() throws Exception {
        standardSubsystemTest("subsystem_1_0.xml", false);
    }

    @Test
    public void testSubsystem20() throws Exception {
        standardSubsystemTest("subsystem_2_0.xml", false);
    }

    @Test
    public void testSubsystem30() throws Exception {
        standardSubsystemTest("subsystem_3_0.xml", false);
    }

    @Test
    public void testTransformersASEAP620() throws Exception {
        testTransformers10(EAP_6_2_0);
    }

    @Test
    public void testTransformersASEAP630() throws Exception {
        testTransformers10(EAP_6_3_0);
    }

    @Test
    public void testTransformersASEAP640() throws Exception {
        testTransformers10(EAP_6_4_0);
    }

    @Test
    public void testTransformersEAP70() throws Exception {
        ModelVersion modelVersion = ModelVersion.create(3, 0, 0);
        KernelServicesBuilder builder = createKernelServicesBuilder(MANAGEMENT).setSubsystemXmlResource("subsystem_4_0-transformers.xml");
        builder.createLegacyKernelServicesBuilder(MANAGEMENT, EAP_7_0_0, modelVersion).addMavenResourceURL(("org.jboss.eap:wildfly-weld:" + (EAP_7_0_0.getMavenGavVersion()))).dontPersistXml();
        KernelServices mainServices = builder.build();
        KernelServices legacyServices = mainServices.getLegacyServices(modelVersion);
        Assert.assertTrue(mainServices.isSuccessfulBoot());
        Assert.assertTrue(legacyServices.isSuccessfulBoot());
        checkSubsystemModelTransformation(mainServices, modelVersion);
    }

    @Test
    public void testTransformersRejectionASEAP620() throws Exception {
        testRejectTransformers10(EAP_6_2_0);
    }

    @Test
    public void testTransformersRejectionASEAP630() throws Exception {
        testRejectTransformers10(EAP_6_3_0);
    }

    @Test
    public void testTransformersRejectionASEAP640() throws Exception {
        testRejectTransformers10(EAP_6_4_0);
    }

    @Test
    public void testTransformersRejectionEAP700() throws Exception {
        ModelVersion modelVersion = ModelVersion.create(3, 0, 0);
        KernelServicesBuilder builder = createKernelServicesBuilder(MANAGEMENT);
        builder.createLegacyKernelServicesBuilder(MANAGEMENT, EAP_7_0_0, modelVersion).addMavenResourceURL(("org.jboss.eap:wildfly-weld:" + (EAP_7_0_0.getMavenGavVersion()))).dontPersistXml();
        KernelServices mainServices = builder.build();
        Assert.assertTrue(mainServices.isSuccessfulBoot());
        Assert.assertTrue(mainServices.getLegacyServices(modelVersion).isSuccessfulBoot());
        ModelTestUtils.checkFailedTransformedBootOperations(mainServices, modelVersion, parse(getSubsystemXml("subsystem-reject.xml")), new FailedOperationTransformationConfig().addFailedAttribute(PathAddress.pathAddress(PATH_SUBSYSTEM), ChainedConfig.createBuilder(NON_PORTABLE_MODE_ATTRIBUTE, REQUIRE_BEAN_DESCRIPTOR_ATTRIBUTE).addConfig(new org.jboss.as.model.test.FailedOperationTransformationConfig.NewAttributesConfig(THREAD_POOL_SIZE_ATTRIBUTE)).build()));
    }

    private static class FalseOrUndefinedToTrueConfig extends FailedOperationTransformationConfig.AttributesPathAddressConfig<WeldSubsystemTestCase.FalseOrUndefinedToTrueConfig> {
        FalseOrUndefinedToTrueConfig(AttributeDefinition... defs) {
            super(convert(defs));
        }

        @Override
        protected boolean isAttributeWritable(String attributeName) {
            return true;
        }

        @Override
        protected boolean checkValue(String attrName, ModelNode attribute, boolean isWriteAttribute) {
            return (!(attribute.isDefined())) || (attribute.asString().equals("false"));
        }

        @Override
        protected ModelNode correctValue(ModelNode toResolve, boolean isWriteAttribute) {
            return new ModelNode(true);
        }
    }
}

