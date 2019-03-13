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
package org.jboss.as.connector.subsystems.jca;


import JcaExtension.SUBSYSTEM_NAME;
import ModelTestControllerVersion.EAP_6_2_0;
import ModelTestControllerVersion.EAP_7_0_0;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author <a href="stefano.maestri@redhat.com>Stefano Maestri</a>
 */
public class JcaSubsystemTestCase extends AbstractSubsystemBaseTest {
    public JcaSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new JcaExtension());
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }

    @Test
    public void testFullConfig() throws Exception {
        standardSubsystemTest("jca-full.xml");
    }

    @Test
    public void testExpressionConfig() throws Exception {
        standardSubsystemTest("jca-full-expression.xml", "jca-full.xml");
    }

    /**
     * WFLY-2640 and WFLY-8141
     */
    @Test
    public void testCCMHandling() throws Exception {
        String xml = readResource("jca-minimal.xml");
        final KernelServices services = createKernelServicesBuilder(createAdditionalInitialization()).setSubsystemXml(xml).build();
        Assert.assertTrue("Subsystem boot failed!", services.isSuccessfulBoot());
        // Get the model and the persisted xml from the first controller
        final ModelNode model = services.readWholeModel();
        // ccm is present despite not being in xml
        ModelNode ccm = model.get("subsystem", "jca", "cached-connection-manager", "cached-connection-manager");
        Assert.assertTrue(ccm.isDefined());
        Assert.assertTrue(ccm.hasDefined("install"));// only true because readWholeModel reads defaults

        // Because it exists we can do a write-attribute
        PathAddress ccmAddress = PathAddress.pathAddress("subsystem", "jca").append("cached-connection-manager", "cached-connection-manager");
        ModelNode writeOp = Util.getWriteAttributeOperation(ccmAddress, "install", true);
        services.executeForResult(writeOp);
        ModelNode readOp = Util.getReadAttributeOperation(ccmAddress, "install");
        ModelNode result = services.executeForResult(readOp);
        Assert.assertTrue(result.asBoolean());
        ModelNode removeOp = Util.createRemoveOperation(ccmAddress);
        services.executeForResult(removeOp);
        // Read still works despite removal, but now the attributes are back to defaults
        result = services.executeForResult(readOp);
        Assert.assertFalse(result.asBoolean());
        // Write attribute works despite removal
        services.executeForResult(writeOp);
        ModelNode addOp = Util.createAddOperation(ccmAddress);
        addOp.get("debug").set(true);
        services.executeForFailure(addOp);// already exists, with install=true

        // Reset to default state and now we can add
        ModelNode undefineOp = Util.createEmptyOperation("undefine-attribute", ccmAddress);
        undefineOp.get("name").set("install");
        services.executeForResult(undefineOp);
        result = services.executeForResult(readOp);
        Assert.assertFalse(result.asBoolean());
        // Now add works
        services.executeForResult(addOp);
        ModelNode readOp2 = Util.getReadAttributeOperation(ccmAddress, "debug");
        result = services.executeForResult(readOp2);
        Assert.assertTrue(result.asBoolean());
        // Cant' add again
        services.executeForFailure(addOp);
        // Remove and re-add
        services.executeForResult(removeOp);
        result = services.executeForResult(readOp2);
        Assert.assertFalse(result.asBoolean());
        services.executeForResult(addOp);
        result = services.executeForResult(readOp2);
        Assert.assertTrue(result.asBoolean());
    }

    @Test
    public void testTransformerEAP62() throws Exception {
        testTransformer(EAP_6_2_0, ModelVersion.create(1, 2, 0), "jca-full.xml");
    }

    @Test
    public void testTransformerEAP62WithExpressions() throws Exception {
        testTransformer(EAP_6_2_0, ModelVersion.create(1, 2, 0), "jca-full-expression.xml");
    }

    @Test
    public void testTransformerEAP7() throws Exception {
        testTransformer7(EAP_7_0_0, ModelVersion.create(4, 0, 0), "jca-full.xml");
    }

    @Test
    public void testTransformerEAP7Elytron() throws Exception {
        testRejectingTransformerElytronEnabled(EAP_7_0_0, ModelVersion.create(4, 0, 0), "jca-full-elytron.xml");
    }
}

