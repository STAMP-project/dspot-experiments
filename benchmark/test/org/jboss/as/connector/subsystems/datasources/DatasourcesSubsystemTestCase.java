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
package org.jboss.as.connector.subsystems.datasources;


import DataSourcesExtension.SUBSYSTEM_NAME;
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
 * @author <a href="stefano.maestri@redhat.com>Stefano Maestri</a>
 */
public class DatasourcesSubsystemTestCase extends AbstractSubsystemBaseTest {
    public DatasourcesSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new DataSourcesExtension());
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }

    @Test
    public void testFullConfig() throws Exception {
        standardSubsystemTest("datasources-full.xml");
    }

    @Test
    public void testElytronConfig() throws Exception {
        standardSubsystemTest("datasources-elytron-enabled_5_0.xml");
    }

    @Test
    public void testExpressionConfig() throws Exception {
        standardSubsystemTest("datasources-full-expression.xml", "datasources-full.xml");
    }

    @Test
    public void testTransformerEAP62() throws Exception {
        testTransformer("datasources-full.xml", EAP_6_2_0, ModelVersion.create(1, 2, 0));
    }

    @Test
    public void testTransformerExpressionEAP62() throws Exception {
        testTransformer("datasources-full-expression111.xml", EAP_6_2_0, ModelVersion.create(1, 2, 0));
    }

    @Test
    public void testTransformerEAP63() throws Exception {
        testTransformer("datasources-full.xml", EAP_6_3_0, ModelVersion.create(1, 3, 0));
    }

    @Test
    public void testTransformerExpressionEAP63() throws Exception {
        testTransformer("datasources-full-expression111.xml", EAP_6_3_0, ModelVersion.create(1, 3, 0));
    }

    @Test
    public void testTransformerEAP64() throws Exception {
        testTransformer("datasources-full.xml", EAP_6_4_0, ModelVersion.create(1, 3, 0));
    }

    @Test
    public void testTransformerExpressionEAP64() throws Exception {
        testTransformer("datasources-full-expression111.xml", EAP_6_4_0, ModelVersion.create(1, 3, 0));
    }

    @Test
    public void testTransformerElytronEnabledEAP64() throws Exception {
        testTransformerElytronEnabled("datasources-elytron-enabled_5_0.xml", EAP_6_4_0, ModelVersion.create(1, 3, 0));
    }

    @Test
    public void testTransformerEAP7() throws Exception {
        testTransformerEAP7FullConfiguration("datasources-full.xml");
    }

    @Test
    public void testRejectionsEAP7() throws Exception {
        testTransformerEAP7Rejection("datasources-no-connection-url.xml");
    }

    private static class RejectUndefinedAttribute extends AttributesPathAddressConfig<DatasourcesSubsystemTestCase.RejectUndefinedAttribute> {
        private RejectUndefinedAttribute(final String... attributes) {
            super(attributes);
        }

        @Override
        protected boolean isAttributeWritable(final String attributeName) {
            return true;
        }

        @Override
        protected boolean checkValue(final String attrName, final ModelNode attribute, final boolean isWriteAttribute) {
            return !(attribute.isDefined());
        }

        @Override
        protected ModelNode correctValue(final ModelNode toResolve, final boolean isWriteAttribute) {
            // Correct by providing a value
            return new ModelNode("token");
        }
    }
}

