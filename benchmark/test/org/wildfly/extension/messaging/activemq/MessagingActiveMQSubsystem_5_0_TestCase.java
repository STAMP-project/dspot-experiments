/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.wildfly.extension.messaging.activemq;


import MessagingExtension.SUBSYSTEM_NAME;
import MessagingExtension.VERSION_1_0_0;
import MessagingExtension.VERSION_2_0_0;
import org.jboss.as.clustering.controller.Operations;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.model.test.FailedOperationTransformationConfig;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.dmr.ModelNode;
import org.junit.Test;


/**
 * * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2012 Red Hat inc
 */
public class MessagingActiveMQSubsystem_5_0_TestCase extends AbstractSubsystemBaseTest {
    public MessagingActiveMQSubsystem_5_0_TestCase() {
        super(SUBSYSTEM_NAME, new MessagingExtension());
    }

    // ///////////////////////////////////////
    // Tests for HA Policy Configuration  //
    // ///////////////////////////////////////
    @Test
    public void testHAPolicyConfiguration() throws Exception {
        standardSubsystemTest("subsystem_5_0_ha-policy.xml");
    }

    // /////////////////////
    // Transformers test //
    // /////////////////////
    @Test
    public void testTransformersEAP_7_1_0() throws Exception {
        testTransformers(EAP_7_1_0, VERSION_2_0_0);
    }

    @Test
    public void testTransformersEAP_7_0_0() throws Exception {
        testTransformers(EAP_7_0_0, VERSION_1_0_0);
    }

    @Test
    public void testRejectingTransformersEAP_7_1_0() throws Exception {
        testRejectingTransformers(EAP_7_1_0, VERSION_2_0_0);
    }

    @Test
    public void testRejectingTransformersEAP_7_0_0() throws Exception {
        testRejectingTransformers(EAP_7_0_0, VERSION_1_0_0);
    }

    private static class ChangeToTrueConfig extends FailedOperationTransformationConfig.AttributesPathAddressConfig<MessagingActiveMQSubsystem_5_0_TestCase.ChangeToTrueConfig> {
        private final String attribute;

        ChangeToTrueConfig(String attribute) {
            super(attribute);
            this.attribute = attribute;
        }

        @Override
        protected boolean isAttributeWritable(String attributeName) {
            return true;
        }

        @Override
        protected boolean checkValue(ModelNode operation, String attrName, ModelNode attribute, boolean isGeneratedWriteAttribute) {
            if ((((!isGeneratedWriteAttribute) && (Operations.getName(operation).equals(ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION))) && (operation.hasDefined(ModelDescriptionConstants.NAME))) && (operation.get(ModelDescriptionConstants.NAME).asString().equals(this.attribute))) {
                // The attribute won't be defined in the :write-attribute(name=<attribute name>,.. boot operation so don't reject in that case
                return false;
            }
            return !(attribute.equals(new ModelNode(true)));
        }

        @Override
        protected boolean checkValue(String attrName, ModelNode attribute, boolean isWriteAttribute) {
            throw new IllegalStateException();
        }

        @Override
        protected ModelNode correctValue(ModelNode toResolve, boolean isWriteAttribute) {
            return new ModelNode(true);
        }
    }
}

