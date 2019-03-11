package org.jboss.as.ejb3.subsystem;


import EJB3Extension.SUBSYSTEM_NAME;
import EJB3SubsystemModel.FILE_DATA_STORE_PATH;
import java.util.Collections;
import java.util.List;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.model.test.FailedOperationTransformationConfig;
import org.jboss.as.model.test.ModelTestControllerVersion;
import org.jboss.as.model.test.ModelTestUtils;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Test;


/**
 * Test cases for transformers used in the EJB3 subsystem.
 *
 * @author <a href="tomasz.cerar@redhat.com"> Tomasz Cerar</a>
 * @author Richard Achmatowicz (c) 2015 Red Hat Inc.
 */
public class Ejb3TransformersTestCase extends AbstractSubsystemBaseTest {
    private static final String LEGACY_EJB_CLIENT_ARTIFACT = "org.jboss:jboss-ejb-client:2.1.2.Final";

    public Ejb3TransformersTestCase() {
        super(SUBSYSTEM_NAME, new EJB3Extension());
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }

    @Test
    public void testTransformerEAP620() throws Exception {
        ModelTestControllerVersion controller = ModelTestControllerVersion.EAP_6_2_0;
        testTransformation(ModelVersion.create(1, 2, 1), controller, Ejb3TransformersTestCase.formatLegacySubsystemArtifact(controller), Ejb3TransformersTestCase.formatArtifact("org.jboss.as:jboss-as-threads:%s", controller));
    }

    @Test
    public void testTransformerEAP630() throws Exception {
        ModelTestControllerVersion controller = ModelTestControllerVersion.EAP_6_3_0;
        testTransformation(ModelVersion.create(1, 2, 1), controller, Ejb3TransformersTestCase.formatLegacySubsystemArtifact(controller), Ejb3TransformersTestCase.formatArtifact("org.jboss.as:jboss-as-threads:%s", controller));
    }

    @Test
    public void testTransformerEAP640() throws Exception {
        ModelTestControllerVersion controller = ModelTestControllerVersion.EAP_6_4_0;
        testTransformation(ModelVersion.create(1, 3, 0), controller, Ejb3TransformersTestCase.formatLegacySubsystemArtifact(controller), Ejb3TransformersTestCase.formatArtifact("org.jboss.as:jboss-as-threads:%s", controller));
    }

    @Test
    public void testRejectionsEAP620() throws Exception {
        ModelTestControllerVersion controller = ModelTestControllerVersion.EAP_6_2_0;
        this.testRejections(ModelVersion.create(1, 2, 1), controller, Ejb3TransformersTestCase.formatLegacySubsystemArtifact(controller), Ejb3TransformersTestCase.formatArtifact("org.jboss.as:jboss-as-threads:%s", controller), Ejb3TransformersTestCase.LEGACY_EJB_CLIENT_ARTIFACT);
    }

    @Test
    public void testRejectionsEAP630() throws Exception {
        ModelTestControllerVersion controller = ModelTestControllerVersion.EAP_6_3_0;
        this.testRejections(ModelVersion.create(1, 2, 1), controller, Ejb3TransformersTestCase.formatLegacySubsystemArtifact(controller), Ejb3TransformersTestCase.formatArtifact("org.jboss.as:jboss-as-threads:%s", controller), Ejb3TransformersTestCase.LEGACY_EJB_CLIENT_ARTIFACT);
    }

    @Test
    public void testRejectionsEAP640() throws Exception {
        ModelTestControllerVersion controller = ModelTestControllerVersion.EAP_6_4_0;
        this.testRejections(ModelVersion.create(1, 3, 0), controller, Ejb3TransformersTestCase.formatLegacySubsystemArtifact(controller), Ejb3TransformersTestCase.formatArtifact("org.jboss.as:jboss-as-threads:%s", controller), Ejb3TransformersTestCase.LEGACY_EJB_CLIENT_ARTIFACT);
    }

    private static class CorrectFalseToTrue extends FailedOperationTransformationConfig.AttributesPathAddressConfig<Ejb3TransformersTestCase.CorrectFalseToTrue> {
        public CorrectFalseToTrue(AttributeDefinition... defs) {
            super(convert(defs));
        }

        @Override
        protected boolean isAttributeWritable(String attributeName) {
            return true;
        }

        @Override
        protected boolean checkValue(String attrName, ModelNode attribute, boolean isWriteAttribute) {
            return attribute.asString().equals("true");
        }

        @Override
        protected ModelNode correctValue(ModelNode toResolve, boolean isWriteAttribute) {
            return new ModelNode(false);
        }
    }

    private abstract static class BasePathAddressConfig implements FailedOperationTransformationConfig.PathAddressConfig {
        @Override
        public boolean expectDiscarded(ModelNode operation) {
            // The reject simply forwards on the original operation to make it fail
            return false;
        }

        @Override
        public List<ModelNode> createWriteAttributeOperations(ModelNode operation) {
            return Collections.emptyList();
        }

        @Override
        public boolean expectFailedWriteAttributeOperation(ModelNode operation) {
            throw new IllegalStateException("Should not get called");
        }

        @Override
        public ModelNode correctWriteAttributeOperation(ModelNode operation) {
            throw new IllegalStateException("Should not get called");
        }
    }

    private static class ChangeAddressConfig extends Ejb3TransformersTestCase.BasePathAddressConfig {
        KernelServices services;

        private final PathAddress badAddress;

        private final PathAddress newAddress;

        private ChangeAddressConfig(KernelServices services, PathAddress badAddress, PathAddress newAddress) {
            this.services = services;
            this.badAddress = badAddress;
            this.newAddress = newAddress;
        }

        @Override
        public boolean expectFailed(ModelNode operation) {
            return isBadAddress(operation);
        }

        @Override
        public boolean canCorrectMore(ModelNode operation) {
            return isBadAddress(operation);
        }

        @Override
        public ModelNode correctOperation(ModelNode operation) {
            // As part of this we also need to update the main model, since the transformer will look at the
            // values already in the model in order to know what to reject. We basically move the
            // resource found at badAddress to newAddress
            try {
                ModelNode ds = services.executeForResult(Util.createEmptyOperation(READ_RESOURCE_OPERATION, badAddress));
                ModelTestUtils.checkOutcome(services.executeOperation(Util.createRemoveOperation(badAddress)));
                ds.get(OP).set(ADD);
                ds.get(OP_ADDR).set(newAddress.toModelNode());
                ModelTestUtils.checkOutcome(services.executeOperation(ds));
            } catch (OperationFailedException e) {
                throw new RuntimeException(e);
            }
            // Now fix up the operation as normal
            ModelNode op = operation.clone();
            op.get(OP_ADDR).set(newAddress.toModelNode());
            return op;
        }

        private boolean isBadAddress(ModelNode operation) {
            return PathAddress.pathAddress(operation.require(OP_ADDR)).equals(badAddress);
        }
    }

    private static class RemoveExtraFileStoreConfig extends Ejb3TransformersTestCase.BasePathAddressConfig {
        private final KernelServices kernelServices;

        private final PathAddress timerServiceAddress;

        private final PathAddress rejectedFileDataStoreAddress;

        private ModelNode removedResourceModel;

        public RemoveExtraFileStoreConfig(KernelServices kernelServices, PathAddress timerServiceAddress) {
            this.kernelServices = kernelServices;
            this.timerServiceAddress = timerServiceAddress;
            rejectedFileDataStoreAddress = timerServiceAddress.append(FILE_DATA_STORE_PATH.getKey(), "file-data-store-rejected");
        }

        @Override
        public boolean expectFailed(ModelNode operation) {
            return hasTooManyFileStores();
        }

        @Override
        public boolean canCorrectMore(ModelNode operation) {
            return hasTooManyFileStores();
        }

        private boolean hasTooManyFileStores() {
            ModelNode op = Util.createOperation(READ_CHILDREN_NAMES_OPERATION, timerServiceAddress);
            op.get(CHILD_TYPE).set(FILE_DATA_STORE_PATH.getKey());
            ModelNode result = ModelTestUtils.checkOutcome(kernelServices.executeOperation(op)).get(RESULT);
            List<ModelNode> list = result.asList();
            return (list.size()) > 1;
        }

        @Override
        public ModelNode correctOperation(ModelNode operation) {
            // Here we don't actually correct the operation, but we remove the extra file-data-store which causes the
            // rejection
            ModelNode rr = Util.createEmptyOperation(READ_RESOURCE_OPERATION, rejectedFileDataStoreAddress);
            removedResourceModel = ModelTestUtils.checkOutcome(kernelServices.executeOperation(rr)).get(RESULT);
            removeExtraFileDataStore();
            return operation;
        }

        void removeExtraFileDataStore() {
            ModelNode remove = Util.createRemoveOperation(rejectedFileDataStoreAddress);
            ModelTestUtils.checkOutcome(kernelServices.executeOperation(remove));
        }

        @Override
        public void operationDone(ModelNode operation) {
            if ((removedResourceModel) != null) {
                // Re-add the removed resource, since we have more checks in the config for file-data-store=file-data-store-rejected
                ModelNode add = Util.createAddOperation(timerServiceAddress.append(FILE_DATA_STORE_PATH.getKey(), "file-data-store-rejected"));
                for (String key : removedResourceModel.keys()) {
                    add.get(key).set(removedResourceModel.get(key));
                }
                ModelNode result = ModelTestUtils.checkOutcome(kernelServices.executeOperation(add)).get(RESULT);
            }
        }
    }
}

