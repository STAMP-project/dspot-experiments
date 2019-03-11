/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.store.library;


import GraphLibrary.A_GRAPH_LIBRARY_CAN_T_BE_ADDED_WITH_A_NULL_S_GRAPH_ID_S;
import TestGroups.ENTITY;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.commonutil.JsonUtil;
import uk.gov.gchq.gaffer.commonutil.exception.OverwritingException;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.Schema.Builder;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;


public abstract class AbstractGraphLibraryTest {
    protected GraphLibrary graphLibrary;

    private static final String TEST_GRAPH_ID = "testGraphId";

    private static final String TEST_GRAPH_ID_1 = "testGraphId1";

    private static final String TEST_UNKNOWN_ID = "unknownId";

    private static final String TEST_SCHEMA_ID = "testSchemaId";

    private static final String TEST_PROPERTIES_ID = "testPropertiesId";

    private static final String EXCEPTION_EXPECTED = "Exception expected";

    private Schema schema = new Schema.Builder().build();

    private Schema schema1 = new Schema.Builder().build();

    private StoreProperties storeProperties = new StoreProperties();

    private StoreProperties storeProperties1 = new StoreProperties();

    @Test
    public void shouldAddAndGetMultipleIdsInGraphLibrary() {
        // When
        graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema, storeProperties);
        graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID_1, schema1, storeProperties1);
        Assert.assertEquals(new uk.gov.gchq.gaffer.commonutil.pair.Pair(AbstractGraphLibraryTest.TEST_GRAPH_ID, AbstractGraphLibraryTest.TEST_GRAPH_ID), graphLibrary.getIds(AbstractGraphLibraryTest.TEST_GRAPH_ID));
        Assert.assertEquals(new uk.gov.gchq.gaffer.commonutil.pair.Pair(AbstractGraphLibraryTest.TEST_GRAPH_ID_1, AbstractGraphLibraryTest.TEST_GRAPH_ID_1), graphLibrary.getIds(AbstractGraphLibraryTest.TEST_GRAPH_ID_1));
    }

    @Test
    public void shouldAddAndGetIdsInGraphLibrary() {
        // When
        graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema, storeProperties);
        // Then
        Assert.assertEquals(new uk.gov.gchq.gaffer.commonutil.pair.Pair(AbstractGraphLibraryTest.TEST_GRAPH_ID, AbstractGraphLibraryTest.TEST_GRAPH_ID), graphLibrary.getIds(AbstractGraphLibraryTest.TEST_GRAPH_ID));
    }

    @Test
    public void shouldThrowExceptionWithInvalidGraphId() {
        // When / Then
        try {
            graphLibrary.add(((AbstractGraphLibraryTest.TEST_GRAPH_ID) + "@#"), schema, storeProperties);
            Assert.fail(AbstractGraphLibraryTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldAddAndGetSchema() {
        // When
        graphLibrary.addSchema(AbstractGraphLibraryTest.TEST_SCHEMA_ID, schema);
        // Then
        JsonAssert.assertEquals(schema.toJson(false), graphLibrary.getSchema(AbstractGraphLibraryTest.TEST_SCHEMA_ID).toJson(false));
    }

    @Test
    public void shouldNotAddNullSchema() {
        // When / Then
        try {
            graphLibrary.addSchema(null, null);
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Id is invalid: null"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenGraphIdWithDifferentSchemaExists() {
        // Given
        graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema, storeProperties);
        Schema tempSchema = new Schema.Builder().edge("testEdge", new SchemaEdgeDefinition()).build();
        // When / Then
        try {
            graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID, tempSchema, storeProperties);
            Assert.fail(AbstractGraphLibraryTest.EXCEPTION_EXPECTED);
        } catch (final OverwritingException e) {
            Assert.assertTrue(e.getMessage().contains("already exists with a different schema"));
        }
    }

    @Test
    public void shouldUpdateSchema() {
        // Given
        graphLibrary.addOrUpdateSchema(AbstractGraphLibraryTest.TEST_SCHEMA_ID, schema);
        Schema tempSchema = new Schema.Builder().edge("testEdge", new SchemaEdgeDefinition()).build();
        // Then
        JsonAssert.assertEquals(schema.toJson(false), graphLibrary.getSchema(AbstractGraphLibraryTest.TEST_SCHEMA_ID).toJson(false));
        // When
        graphLibrary.addOrUpdateSchema(AbstractGraphLibraryTest.TEST_SCHEMA_ID, tempSchema);
        // Then
        JsonAssert.assertEquals(tempSchema.toJson(false), graphLibrary.getSchema(AbstractGraphLibraryTest.TEST_SCHEMA_ID).toJson(false));
    }

    @Test
    public void shouldAddAndGetProperties() {
        // When
        graphLibrary.addProperties(AbstractGraphLibraryTest.TEST_PROPERTIES_ID, storeProperties);
        // Then
        Assert.assertEquals(storeProperties, graphLibrary.getProperties(AbstractGraphLibraryTest.TEST_PROPERTIES_ID));
    }

    @Test
    public void shouldNotAddNullProperties() {
        // When / Then
        try {
            graphLibrary.addProperties(null, null);
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Id is invalid: null"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenGraphIdWithDifferentPropertiesExists() {
        // Given
        graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema, storeProperties);
        StoreProperties tempStoreProperties = storeProperties.clone();
        tempStoreProperties.set("testKey", "testValue");
        // When / Then
        try {
            graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema, tempStoreProperties);
            Assert.fail(AbstractGraphLibraryTest.EXCEPTION_EXPECTED);
        } catch (final Exception e) {
            Assert.assertTrue(e.getMessage().contains("already exists with a different store properties"));
        }
    }

    @Test
    public void shouldUpdateStoreProperties() {
        // Given
        graphLibrary.addOrUpdateProperties(AbstractGraphLibraryTest.TEST_PROPERTIES_ID, storeProperties);
        StoreProperties tempStoreProperties = storeProperties.clone();
        tempStoreProperties.set("testKey", "testValue");
        // Then
        Assert.assertEquals(storeProperties.getProperties(), graphLibrary.getProperties(AbstractGraphLibraryTest.TEST_PROPERTIES_ID).getProperties());
        // When
        graphLibrary.addOrUpdateProperties(AbstractGraphLibraryTest.TEST_PROPERTIES_ID, tempStoreProperties);
        // Then
        Assert.assertEquals(tempStoreProperties.getProperties(), graphLibrary.getProperties(AbstractGraphLibraryTest.TEST_PROPERTIES_ID).getProperties());
    }

    @Test
    public void shouldNotThrowExceptionWhenGraphIdWithSameSchemaExists() {
        // Given
        graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema1, storeProperties);
        final Schema schema1Clone = schema1.clone();
        // When
        graphLibrary.checkExisting(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema1Clone, storeProperties);
        // Then - no exceptions
    }

    @Test
    public void shouldNotThrowExceptionWhenGraphIdWithSamePropertiesExists() {
        // Given
        graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema1, storeProperties);
        final StoreProperties storePropertiesClone = storeProperties.clone();
        // When
        graphLibrary.checkExisting(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema1, storePropertiesClone);
        // Then - no exceptions
    }

    @Test
    public void shouldUpdateWhenGraphIdExists() {
        // When
        graphLibrary.addOrUpdate(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema, storeProperties);
        // Then
        Assert.assertEquals(storeProperties, graphLibrary.getProperties(AbstractGraphLibraryTest.TEST_GRAPH_ID));
        // When
        graphLibrary.addOrUpdate(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema, storeProperties1);
        // Then
        Assert.assertEquals(storeProperties1, graphLibrary.getProperties(AbstractGraphLibraryTest.TEST_GRAPH_ID));
    }

    @Test
    public void shouldReturnNullWhenPropertyIdIsNotFound() {
        // When
        final StoreProperties unknownStoreProperties = graphLibrary.getProperties(AbstractGraphLibraryTest.TEST_UNKNOWN_ID);
        // Then
        Assert.assertNull(unknownStoreProperties);
    }

    @Test
    public void shouldReturnNullWhenSchemaIdIsNotFound() {
        // When
        final Schema unknownSchema = graphLibrary.getSchema(AbstractGraphLibraryTest.TEST_UNKNOWN_ID);
        // Then
        Assert.assertNull(unknownSchema);
    }

    @Test
    public void shouldThrowExceptionWhenNewStorePropertiesAreAddedWithSamePropertiesIdAndDifferentProperties() {
        // Given
        final StoreProperties tempStoreProperties = storeProperties.clone();
        tempStoreProperties.set("randomKey", "randomValue");
        // When
        graphLibrary.addProperties(AbstractGraphLibraryTest.TEST_PROPERTIES_ID, storeProperties);
        // Then
        try {
            graphLibrary.addProperties(AbstractGraphLibraryTest.TEST_PROPERTIES_ID, tempStoreProperties);
            Assert.fail(AbstractGraphLibraryTest.EXCEPTION_EXPECTED);
        } catch (final OverwritingException e) {
            Assert.assertTrue(e.getMessage().contains("already exists with a different store properties"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenNewSchemaIsAddedWithSameSchemaIdAndDifferentSchema() {
        // Given
        final Schema tempSchema = new Schema.Builder().edge(ENTITY, new SchemaEdgeDefinition.Builder().build()).build();
        // When
        graphLibrary.addSchema(AbstractGraphLibraryTest.TEST_SCHEMA_ID, schema);
        // Then
        try {
            graphLibrary.addSchema(AbstractGraphLibraryTest.TEST_SCHEMA_ID, tempSchema);
            Assert.fail(AbstractGraphLibraryTest.EXCEPTION_EXPECTED);
        } catch (final OverwritingException e) {
            Assert.assertTrue(e.getMessage().contains("already exists with a different schema"));
        }
    }

    @Test
    public void shouldIgnoreDuplicateAdditionWhenStorePropertiesAreIdentical() {
        // Given
        final StoreProperties tempStoreProperties = storeProperties.clone();
        // When
        graphLibrary.addProperties(AbstractGraphLibraryTest.TEST_PROPERTIES_ID, storeProperties);
        graphLibrary.addProperties(AbstractGraphLibraryTest.TEST_PROPERTIES_ID, tempStoreProperties);
        // Then - no exception
    }

    @Test
    public void shouldIgnoreDuplicateAdditionWhenSchemasAreIdentical() {
        // Given
        final Schema tempSchema = schema.clone();
        // When
        graphLibrary.addSchema(AbstractGraphLibraryTest.TEST_SCHEMA_ID, schema);
        graphLibrary.addSchema(AbstractGraphLibraryTest.TEST_SCHEMA_ID, tempSchema);
        // Then - no exceptions
    }

    @Test
    public void shouldNotOverwriteSchemaWithClashingName() throws Exception {
        final String clashingId = "clashingId";
        byte[] entitySchema = new Builder().entity("e1", new SchemaEntityDefinition.Builder().property("p1", "string").build()).type("string", String.class).build().toJson(true);
        byte[] edgeSchema = new Builder().edge("e1", new SchemaEdgeDefinition.Builder().property("p1", "string").build()).type("string", String.class).build().toJson(true);
        graphLibrary.addSchema(clashingId, Schema.fromJson(entitySchema));
        try {
            graphLibrary.add("graph", clashingId, Schema.fromJson(edgeSchema), AbstractGraphLibraryTest.TEST_PROPERTIES_ID, new StoreProperties());
            Assert.fail(AbstractGraphLibraryTest.EXCEPTION_EXPECTED);
        } catch (final OverwritingException e) {
            Assert.assertTrue(e.getMessage().contains("schemaId clashingId already exists with a different schema"));
        }
        Schema schemaFromLibrary = graphLibrary.getSchema(clashingId);
        Assert.assertTrue(JsonUtil.equals(entitySchema, schemaFromLibrary.toJson(true)));
        Assert.assertFalse(JsonUtil.equals(schemaFromLibrary.toJson(true), edgeSchema));
    }

    @Test
    public void shouldNotOverwriteStorePropertiesWithClashingName() throws Exception {
        final String clashingId = "clashingId";
        StoreProperties propsA = new StoreProperties();
        propsA.set("a", "a");
        StoreProperties propsB = new StoreProperties();
        propsB.set("b", "b");
        graphLibrary.addProperties(clashingId, propsA);
        try {
            graphLibrary.add("graph", AbstractGraphLibraryTest.TEST_SCHEMA_ID, new Schema(), clashingId, propsB);
            Assert.fail(AbstractGraphLibraryTest.EXCEPTION_EXPECTED);
        } catch (final OverwritingException e) {
            Assert.assertTrue(e.getMessage().contains("propertiesId clashingId already exists with a different store properties"));
        }
        StoreProperties storePropertiesFromLibrary = graphLibrary.getProperties(clashingId);
        Assert.assertEquals(propsA.getProperties(), storePropertiesFromLibrary.getProperties());
        Assert.assertNotEquals(propsB.getProperties(), storePropertiesFromLibrary.getProperties());
    }

    @Test
    public void shouldThrowExceptionWhenAddingAFullLibraryWithNullSchema() throws Exception {
        try {
            graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID, null, storeProperties);
            Assert.fail(AbstractGraphLibraryTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(String.format(A_GRAPH_LIBRARY_CAN_T_BE_ADDED_WITH_A_NULL_S_GRAPH_ID_S, Schema.class.getSimpleName(), AbstractGraphLibraryTest.TEST_GRAPH_ID), e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingAFullLibraryWithNullStoreProperties() throws Exception {
        try {
            graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID, schema, null);
            Assert.fail(AbstractGraphLibraryTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(String.format(A_GRAPH_LIBRARY_CAN_T_BE_ADDED_WITH_A_NULL_S_GRAPH_ID_S, StoreProperties.class.getSimpleName(), AbstractGraphLibraryTest.TEST_GRAPH_ID), e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingAFullLibraryWithNullSchemaAndStoreProperties() throws Exception {
        try {
            graphLibrary.add(AbstractGraphLibraryTest.TEST_GRAPH_ID, null, null);
            Assert.fail(AbstractGraphLibraryTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(String.format(A_GRAPH_LIBRARY_CAN_T_BE_ADDED_WITH_A_NULL_S_GRAPH_ID_S, (((Schema.class.getSimpleName()) + " and ") + (StoreProperties.class.getSimpleName())), AbstractGraphLibraryTest.TEST_GRAPH_ID), e.getMessage());
        }
    }
}

