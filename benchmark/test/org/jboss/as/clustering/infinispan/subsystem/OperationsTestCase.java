package org.jboss.as.clustering.infinispan.subsystem;


import CacheContainerResourceDefinition.Attribute.ALIASES;
import CacheContainerResourceDefinition.Attribute.DEFAULT_CACHE;
import CacheResourceDefinition.Attribute.STATISTICS_ENABLED;
import JDBCStoreResourceDefinition.Attribute.DATA_SOURCE;
import LocalCacheResourceDefinition.WILDCARD_PATH;
import MixedKeyedJDBCStoreResourceDefinition.DeprecatedAttribute.STRING_TABLE;
import ModelDescriptionConstants.NAME;
import StoreResourceDefinition.Attribute.PROPERTIES;
import java.util.Collections;
import org.jboss.as.clustering.controller.Attribute;
import org.jboss.as.clustering.controller.Operations;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.junit.Assert;
import org.junit.Test;

import static StorePropertyResourceDefinition.VALUE;


/**
 * Test case for testing individual management operations.
 *
 *  These test cases are based on the XML config in subsystem-infinispan-test,
 *  a non-exhaustive subsystem configuration.
 *
 * @author Richard Achmatowicz (c) 2011 Red Hat Inc.
 */
public class OperationsTestCase extends OperationTestCaseBase {
    /* Tests access to cache container attributes */
    @Test
    public void testCacheContainerReadWriteOperation() throws Exception {
        // Parse and install the XML into the controller
        String subsystemXml = getSubsystemXml();
        KernelServices servicesA = this.createKernelServicesBuilder().setSubsystemXml(subsystemXml).build();
        // read the cache container default cache attribute
        ModelNode result = servicesA.executeOperation(OperationTestCaseBase.getCacheContainerReadOperation("maximal", DEFAULT_CACHE));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("local", result.get(RESULT).asString());
        // write the default cache attribute
        result = servicesA.executeOperation(OperationTestCaseBase.getCacheContainerWriteOperation("maximal", DEFAULT_CACHE, "new-default-cache"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // re-read the default cache attribute
        result = servicesA.executeOperation(OperationTestCaseBase.getCacheContainerReadOperation("maximal", DEFAULT_CACHE));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("new-default-cache", result.get(RESULT).asString());
    }

    /* Tests access to local cache attributes */
    @Test
    public void testLocalCacheReadWriteOperation() throws Exception {
        // Parse and install the XML into the controller
        String subsystemXml = getSubsystemXml();
        KernelServices servicesA = this.createKernelServicesBuilder().setSubsystemXml(subsystemXml).build();
        ModelNode readOperation = OperationTestCaseBase.getCacheReadOperation("maximal", WILDCARD_PATH.getKey(), "local", STATISTICS_ENABLED);
        // read the cache container batching attribute
        ModelNode result = servicesA.executeOperation(readOperation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertTrue(result.get(RESULT).asBoolean());
        ModelNode writeOperation = OperationTestCaseBase.getCacheWriteOperation("maximal", WILDCARD_PATH.getKey(), "local", STATISTICS_ENABLED, "false");
        // write the batching attribute
        result = servicesA.executeOperation(writeOperation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // re-read the batching attribute
        result = servicesA.executeOperation(readOperation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).asBoolean());
    }

    /* Tests access to local cache attributes */
    @SuppressWarnings("deprecation")
    @Test
    public void testDistributedCacheJDBCStoreReadWriteOperation() throws Exception {
        ModelNode stringKeyedTable = OperationsTestCase.createStringKeyedTable();
        // Parse and install the XML into the controller
        String subsystemXml = getSubsystemXml();
        KernelServices servicesA = this.createKernelServicesBuilder().setSubsystemXml(subsystemXml).build();
        // read the distributed cache mixed-keyed-jdbc-store datasource attribute
        ModelNode result = servicesA.executeOperation(OperationTestCaseBase.getJDBCCacheStoreReadOperation("maximal", DistributedCacheResourceDefinition.WILDCARD_PATH.getKey(), "dist", DATA_SOURCE));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("ExampleDS", result.get(RESULT).asString());
        // write the batching attribute
        result = servicesA.executeOperation(OperationTestCaseBase.getJDBCCacheStoreWriteOperation("maximal", DistributedCacheResourceDefinition.WILDCARD_PATH.getKey(), "dist", DATA_SOURCE, "new-datasource"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // re-read the batching attribute
        result = servicesA.executeOperation(OperationTestCaseBase.getJDBCCacheStoreReadOperation("maximal", DistributedCacheResourceDefinition.WILDCARD_PATH.getKey(), "dist", DATA_SOURCE));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("new-datasource", result.get(RESULT).asString());
        // read the string-keyed-table attribute
        result = servicesA.executeOperation(OperationTestCaseBase.getJDBCCacheStoreReadOperation("maximal", DistributedCacheResourceDefinition.WILDCARD_PATH.getKey(), "dist", STRING_TABLE));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals(stringKeyedTable.asPropertyList().size(), result.get(RESULT).asPropertyList().size());
        for (Property property : stringKeyedTable.asPropertyList()) {
            Assert.assertTrue(result.get(RESULT).hasDefined(property.getName()));
            Assert.assertEquals(property.getValue(), result.get(RESULT).get(property.getName()));
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testStoreProperties() throws Exception {
        KernelServices services = this.createKernelServicesBuilder().setSubsystemXml(this.getSubsystemXml()).build();
        PathAddress address = OperationTestCaseBase.getRemoteCacheStoreAddress("maximal", InvalidationCacheResourceDefinition.WILDCARD_PATH.getKey(), "invalid");
        String key = "infinispan.client.hotrod.ping_on_startup";
        String value = "true";
        ModelNode operation = Operations.createMapPutOperation(address, PROPERTIES, key, value);
        ModelNode result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        operation = Operations.createMapGetOperation(address, PROPERTIES, key);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals(value, result.get(RESULT).asString());
        operation = Operations.createMapRemoveOperation(address, PROPERTIES, key);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        operation = Operations.createMapGetOperation(address, PROPERTIES, key);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        // Validate that properties can still be added/removed/updated via child property resources
        PathAddress propertyAddress = address.append(StorePropertyResourceDefinition.pathElement(key));
        operation = Operations.createAddOperation(propertyAddress, Collections.<Attribute, ModelNode>singletonMap(new org.jboss.as.clustering.controller.SimpleAttribute(VALUE), new ModelNode(value)));
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        operation = Operations.createMapGetOperation(address, PROPERTIES, key);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals(value, result.get(RESULT).asString());
        value = "false";
        operation = Operations.createWriteAttributeOperation(propertyAddress, new org.jboss.as.clustering.controller.SimpleAttribute(VALUE), new ModelNode(value));
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        operation = Operations.createMapGetOperation(address, PROPERTIES, key);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals(value, result.get(RESULT).asString());
        operation = Operations.createReadAttributeOperation(propertyAddress, new org.jboss.as.clustering.controller.SimpleAttribute(VALUE));
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals(value, result.get(RESULT).asString());
        operation = Util.createRemoveOperation(propertyAddress);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        operation = Operations.createMapGetOperation(address, PROPERTIES, key);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
    }

    @Test
    public void testAliases() throws Exception {
        KernelServices services = this.createKernelServicesBuilder().setSubsystemXml(this.getSubsystemXml()).build();
        PathAddress address = OperationTestCaseBase.getCacheContainerAddress("minimal");
        String alias = "alias0";
        ModelNode operation = Operations.createListAddOperation(address, ALIASES, alias);
        ModelNode result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        operation = Operations.createListGetOperation(address, ALIASES, 0);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals(new ModelNode(alias), result.get(RESULT));
        operation = Operations.createListRemoveOperation(address, ALIASES, 0);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        operation = Operations.createListGetOperation(address, ALIASES, 0);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        // Validate that aliases can still be added/removed via legacy operations
        operation = Util.createOperation("add-alias", address);
        operation.get(NAME).set(alias);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        operation = Operations.createListGetOperation(address, ALIASES, 0);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        /* This currently fails due to WFCORE-626, requires wildfly-core-1.0.0.Beta4
        Assert.assertEquals(new ModelNode(alias), result.get(RESULT));
         */
        operation = Util.createOperation("remove-alias", address);
        operation.get(NAME).set(alias);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        operation = Operations.createListGetOperation(address, ALIASES, 0);
        result = services.executeOperation(operation);
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
    }
}

