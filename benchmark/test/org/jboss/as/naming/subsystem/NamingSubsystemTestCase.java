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
package org.jboss.as.naming.subsystem;


import ModelDescriptionConstants.OP_ADDR;
import ModelDescriptionConstants.SUBSYSTEM;
import ModelTestControllerVersion.EAP_6_4_0;
import ModelTestControllerVersion.EAP_7_0_0;
import NamingBindingResourceDefinition.BINDING_TYPE;
import NamingBindingResourceDefinition.CACHE;
import NamingExtension.SUBSYSTEM_NAME;
import NamingSubsystemModel.BINDING;
import NamingSubsystemModel.LOOKUP;
import Operations.CompositeOperationBuilder;
import java.util.List;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.model.test.ModelTestUtils;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;

import static BindingType.EXTERNAL_CONTEXT;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class NamingSubsystemTestCase extends AbstractSubsystemBaseTest {
    public NamingSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new NamingExtension());
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }

    @Test
    public void testOnlyExternalContextAllowsCache() throws Exception {
        KernelServices services = createKernelServicesBuilder(AdditionalInitialization.MANAGEMENT).build();
        Assert.assertTrue(services.isSuccessfulBoot());
        List<ModelNode> list = parse(ModelTestUtils.readResource(this.getClass(), "subsystem.xml"));
        for (ModelNode addOp : list) {
            PathAddress addr = PathAddress.pathAddress(addOp.require(OP_ADDR));
            if ((((addr.size()) == 2) && (addr.getLastElement().getKey().equals(BINDING))) && ((BindingType.forName(addOp.get(BINDING_TYPE.getName()).asString())) != (EXTERNAL_CONTEXT))) {
                // Add the cache attribute and make sure it fails
                addOp.get(CACHE.getName()).set(true);
                services.executeForFailure(addOp);
                // Remove the cache attribute and make sure it succeeds
                addOp.remove(CACHE.getName());
                ModelTestUtils.checkOutcome(services.executeOperation(addOp));
                // Try to write the cache attribute, which should fail
                ModelTestUtils.checkFailed(services.executeOperation(Util.getWriteAttributeOperation(addr, CACHE.getName(), new ModelNode(true))));
            } else {
                ModelTestUtils.checkOutcome(services.executeOperation(addOp));
            }
        }
    }

    /**
     * Asserts that bindings may be added through composite ops.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCompositeBindingOps() throws Exception {
        final KernelServices services = createKernelServicesBuilder(createAdditionalInitialization()).setSubsystemXml(getSubsystemXml()).build();
        // add binding 'alookup' through composite op
        // note that a binding-type of 'lookup' requires 'lookup' attr value, which in this case is set by a followup step
        final ModelNode addr = Operations.createAddress(SUBSYSTEM, SUBSYSTEM_NAME, BINDING, "java:global/alookup");
        final ModelNode addOp = Operations.createAddOperation(addr);
        addOp.get(NamingSubsystemModel.BINDING_TYPE).set(LOOKUP);
        final ModelNode compositeOp = CompositeOperationBuilder.create().addStep(addOp).addStep(Operations.createWriteAttributeOperation(addr, LOOKUP, "java:global/a")).build().getOperation();
        ModelTestUtils.checkOutcome(services.executeOperation(compositeOp));
    }

    /**
     * Asserts that bindings may be updated through composite ops.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCompositeBindingUpdate() throws Exception {
        final KernelServices services = createKernelServicesBuilder(createAdditionalInitialization()).setSubsystemXml(getSubsystemXml()).build();
        // updates binding 'a' through composite op
        // binding-type used is lookup, op should succeed even if lookup value is set by a followup step
        final ModelNode addr = Operations.createAddress(SUBSYSTEM, SUBSYSTEM_NAME, BINDING, "java:global/a");
        final ModelNode compositeOp = CompositeOperationBuilder.create().addStep(Operations.createWriteAttributeOperation(addr, NamingSubsystemModel.BINDING_TYPE, LOOKUP)).addStep(Operations.createWriteAttributeOperation(addr, LOOKUP, "java:global/b")).build().getOperation();
        ModelTestUtils.checkOutcome(services.executeOperation(compositeOp));
    }

    @Test
    public void testRejectionsEAP7() throws Exception {
        testTransformer("subsystem.xml", EAP_7_0_0, ModelVersion.create(2, 0), "wildfly-naming");
    }

    @Test
    public void testRejectionsEAP6() throws Exception {
        testTransformer("subsystem.xml", EAP_6_4_0, ModelVersion.create(1, 3), "jboss-as-naming");
    }
}

