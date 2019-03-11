/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.store.schema;


import IdentifierType.DESTINATION;
import IdentifierType.DIRECTED;
import IdentifierType.SOURCE;
import IdentifierType.VERTEX;
import StreamUtil.ELEMENTS_SCHEMA;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.EDGE_3;
import TestGroups.EDGE_4;
import TestGroups.EDGE_5;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import TestGroups.ENTITY_4;
import TestGroups.ENTITY_5;
import TestPropertyNames.COUNT;
import TestPropertyNames.DATE;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import TestPropertyNames.PROP_3;
import TestPropertyNames.PROP_4;
import TestPropertyNames.PROP_5;
import TestPropertyNames.TIMESTAMP;
import TestPropertyNames.VISIBILITY;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.commonutil.TestPropertyNames;
import uk.gov.gchq.gaffer.data.element.function.ElementAggregator;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.elementdefinition.exception.SchemaException;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.function.ExampleAggregateFunction;
import uk.gov.gchq.gaffer.function.ExampleFilterFunction;
import uk.gov.gchq.gaffer.serialisation.Serialiser;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.JavaSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.MapSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.raw.RawLongSerialiser;
import uk.gov.gchq.gaffer.store.TestTypes;
import uk.gov.gchq.gaffer.store.library.HashMapGraphLibrary;
import uk.gov.gchq.koryphe.impl.predicate.Exists;
import uk.gov.gchq.koryphe.impl.predicate.IsA;
import uk.gov.gchq.koryphe.impl.predicate.IsXMoreThanY;
import uk.gov.gchq.koryphe.tuple.binaryoperator.TupleAdaptedBinaryOperator;
import uk.gov.gchq.koryphe.tuple.predicate.TupleAdaptedPredicate;


public class SchemaTest {
    public static final String EDGE_DESCRIPTION = "Edge description";

    public static final String ENTITY_DESCRIPTION = "Entity description";

    public static final String STRING_TYPE_DESCRIPTION = "String type description";

    public static final String INTEGER_TYPE_DESCRIPTION = "Integer type description";

    public static final String TIMESTAMP_TYPE_DESCRIPTION = "Timestamp type description";

    public static final String DATE_TYPE_DESCRIPTION = "Date type description";

    public static final String MAP_TYPE_DESCRIPTION = "Map type description";

    private Schema schema = new Schema.Builder().json(StreamUtil.schemas(getClass())).build();

    @Test
    public void shouldCloneSchema() throws SerialisationException {
        // Given
        // When
        final Schema clonedSchema = schema.clone();
        // Then
        // Check they are different instances
        Assert.assertNotSame(schema, clonedSchema);
        // Check they are equal by comparing the json
        JsonAssert.assertEquals(schema.toJson(true), clonedSchema.toJson(true));
    }

    @Test
    public void shouldDeserialiseAndReserialiseIntoTheSameJson() throws SerialisationException {
        // Given
        final byte[] json1 = schema.toCompactJson();
        final Schema schema2 = new Schema.Builder().json(json1).build();
        // When
        final byte[] json2 = schema2.toCompactJson();
        // Then
        JsonAssert.assertEquals(json1, json2);
    }

    @Test
    public void shouldDeserialiseAndReserialiseIntoTheSamePrettyJson() throws SerialisationException {
        // Given
        final byte[] json1 = schema.toJson(true);
        final Schema schema2 = new Schema.Builder().json(json1).build();
        // When
        final byte[] json2 = schema2.toJson(true);
        // Then
        JsonAssert.assertEquals(json1, json2);
    }

    @Test
    public void testLoadingSchemaFromJson() {
        // Edge definitions
        SchemaElementDefinition edgeDefinition = schema.getEdge(EDGE);
        Assert.assertNotNull(edgeDefinition);
        Assert.assertEquals(SchemaTest.EDGE_DESCRIPTION, edgeDefinition.getDescription());
        final Map<String, String> propertyMap = edgeDefinition.getPropertyMap();
        Assert.assertEquals(3, propertyMap.size());
        Assert.assertEquals("prop.string", propertyMap.get(PROP_2));
        Assert.assertEquals("prop.date", propertyMap.get(DATE));
        Assert.assertEquals("timestamp", propertyMap.get(TIMESTAMP));
        Assert.assertEquals(Sets.newLinkedHashSet(Collections.singletonList(DATE)), edgeDefinition.getGroupBy());
        // Check validator
        ElementFilter validator = edgeDefinition.getValidator();
        List<TupleAdaptedPredicate<String, ?>> valContexts = validator.getComponents();
        int index = 0;
        TupleAdaptedPredicate<String, ?> tuplePredicate = valContexts.get((index++));
        Assert.assertTrue(((tuplePredicate.getPredicate()) instanceof IsA));
        Assert.assertEquals(1, tuplePredicate.getSelection().length);
        Assert.assertEquals(SOURCE.name(), tuplePredicate.getSelection()[0]);
        tuplePredicate = valContexts.get((index++));
        Assert.assertTrue(((tuplePredicate.getPredicate()) instanceof IsA));
        Assert.assertEquals(1, tuplePredicate.getSelection().length);
        Assert.assertEquals(DESTINATION.name(), tuplePredicate.getSelection()[0]);
        tuplePredicate = valContexts.get((index++));
        Assert.assertTrue(((tuplePredicate.getPredicate()) instanceof IsA));
        Assert.assertEquals(1, tuplePredicate.getSelection().length);
        Assert.assertEquals(DIRECTED.name(), tuplePredicate.getSelection()[0]);
        tuplePredicate = valContexts.get((index++));
        Assert.assertTrue(((tuplePredicate.getPredicate()) instanceof ExampleFilterFunction));
        Assert.assertEquals(1, tuplePredicate.getSelection().length);
        Assert.assertEquals(DIRECTED.name(), tuplePredicate.getSelection()[0]);
        tuplePredicate = valContexts.get((index++));
        Assert.assertTrue(((tuplePredicate.getPredicate()) instanceof IsA));
        Assert.assertEquals(1, tuplePredicate.getSelection().length);
        Assert.assertEquals(PROP_2, tuplePredicate.getSelection()[0]);
        tuplePredicate = valContexts.get((index++));
        Assert.assertTrue(((tuplePredicate.getPredicate()) instanceof ExampleFilterFunction));
        Assert.assertEquals(1, tuplePredicate.getSelection().length);
        Assert.assertEquals(PROP_2, tuplePredicate.getSelection()[0]);
        tuplePredicate = valContexts.get((index++));
        Assert.assertTrue(((tuplePredicate.getPredicate()) instanceof IsA));
        Assert.assertEquals(1, tuplePredicate.getSelection().length);
        Assert.assertEquals(DATE, tuplePredicate.getSelection()[0]);
        tuplePredicate = valContexts.get((index++));
        Assert.assertTrue(((tuplePredicate.getPredicate()) instanceof IsA));
        Assert.assertEquals(1, tuplePredicate.getSelection().length);
        Assert.assertEquals(TIMESTAMP, tuplePredicate.getSelection()[0]);
        Assert.assertEquals(index, valContexts.size());
        TypeDefinition type = edgeDefinition.getPropertyTypeDef(DATE);
        Assert.assertEquals(Date.class, type.getClazz());
        Assert.assertEquals(SchemaTest.DATE_TYPE_DESCRIPTION, type.getDescription());
        Assert.assertNull(type.getSerialiser());
        Assert.assertTrue(((type.getAggregateFunction()) instanceof ExampleAggregateFunction));
        // Entity definitions
        SchemaElementDefinition entityDefinition = schema.getEntity(ENTITY);
        Assert.assertNotNull(entityDefinition);
        Assert.assertEquals(SchemaTest.ENTITY_DESCRIPTION, entityDefinition.getDescription());
        Assert.assertTrue(entityDefinition.containsProperty(PROP_1));
        type = entityDefinition.getPropertyTypeDef(PROP_1);
        Assert.assertEquals(0, entityDefinition.getGroupBy().size());
        Assert.assertEquals(SchemaTest.STRING_TYPE_DESCRIPTION, type.getDescription());
        Assert.assertEquals(String.class, type.getClazz());
        Assert.assertNull(type.getSerialiser());
        Assert.assertTrue(((type.getAggregateFunction()) instanceof ExampleAggregateFunction));
        validator = entityDefinition.getValidator();
        valContexts = validator.getComponents();
        index = 0;
        tuplePredicate = valContexts.get((index++));
        Assert.assertTrue(((tuplePredicate.getPredicate()) instanceof IsXMoreThanY));
        Assert.assertEquals(2, tuplePredicate.getSelection().length);
        Assert.assertEquals(PROP_1, tuplePredicate.getSelection()[0]);
        Assert.assertEquals(VISIBILITY, tuplePredicate.getSelection()[1]);
        tuplePredicate = valContexts.get((index++));
        Assert.assertTrue(((tuplePredicate.getPredicate()) instanceof IsA));
        Assert.assertEquals(1, tuplePredicate.getSelection().length);
        Assert.assertEquals(VERTEX.name(), tuplePredicate.getSelection()[0]);
        tuplePredicate = valContexts.get((index++));
        Assert.assertTrue(((tuplePredicate.getPredicate()) instanceof IsA));
        Assert.assertEquals(1, tuplePredicate.getSelection().length);
        Assert.assertEquals(PROP_1, tuplePredicate.getSelection()[0]);
        final ElementAggregator aggregator = edgeDefinition.getFullAggregator();
        final List<TupleAdaptedBinaryOperator<String, ?>> aggContexts = aggregator.getComponents();
        Assert.assertEquals(3, aggContexts.size());
        TupleAdaptedBinaryOperator<String, ?> aggContext = aggContexts.get(0);
        Assert.assertTrue(((aggContext.getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertEquals(1, aggContext.getSelection().length);
        Assert.assertEquals(PROP_2, aggContext.getSelection()[0]);
        aggContext = aggContexts.get(1);
        Assert.assertTrue(((aggContext.getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertEquals(1, aggContext.getSelection().length);
        Assert.assertEquals(DATE, aggContext.getSelection()[0]);
        TypeDefinition mapTypeDef = schema.getType(TestTypes.PROP_MAP);
        Assert.assertEquals(LinkedHashMap.class, mapTypeDef.getClazz());
        Assert.assertEquals(SchemaTest.MAP_TYPE_DESCRIPTION, mapTypeDef.getDescription());
        Serialiser serialiser = mapTypeDef.getSerialiser();
        Assert.assertEquals(MapSerialiser.class, serialiser.getClass());
        MapSerialiser mapSerialiser = ((MapSerialiser) (serialiser));
        Assert.assertEquals(StringSerialiser.class, mapSerialiser.getKeySerialiser().getClass());
        Assert.assertEquals(RawLongSerialiser.class, mapSerialiser.getValueSerialiser().getClass());
        Assert.assertNull(mapSerialiser.getMapClass());
    }

    @Test
    public void shouldReturnTrueWhenSchemaHasAggregationEnabled() {
        final Schema schemaWithAggregators = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().aggregate(true).build()).build();
        Assert.assertTrue(schemaWithAggregators.isAggregationEnabled());
    }

    @Test
    public void shouldReturnFalseWhenSchemaHasAggregationDisabled() {
        final Schema schemaNoAggregators = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().aggregate(false).build()).build();
        Assert.assertFalse(schemaNoAggregators.isAggregationEnabled());
    }

    @Test
    public void createProgramaticSchema() {
        createSchema();
    }

    @Test
    public void writeProgramaticSchemaAsJson() throws IOException, SchemaException {
        Schema schema = createSchema();
        JsonAssert.assertEquals(String.format(("{%n" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("  \"edges\" : {%n" + "    \"BasicEdge\" : {%n") + "      \"properties\" : {%n") + "        \"property1\" : \"prop.string\",%n") + "        \"property2\" : \"prop.integer\",%n") + "        \"timestamp\" : \"timestamp\"%n") + "      },%n") + "      \"groupBy\" : [ \"property1\" ],%n") + "      \"description\" : \"Edge description\",%n") + "      \"source\" : \"id.string\",%n") + "      \"destination\" : \"id.string\",%n") + "      \"validateFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"property1\" ]%n") + "      } ]%n") + "    }%n") + "  },%n") + "  \"entities\" : {%n") + "    \"BasicEntity\" : {%n") + "      \"properties\" : {%n") + "        \"property1\" : \"prop.string\",%n") + "        \"property2\" : \"prop.integer\",%n") + "        \"timestamp\" : \"timestamp\"%n") + "      },%n") + "      \"groupBy\" : [ \"property1\" ],%n") + "      \"description\" : \"Edge description\",%n") + "      \"vertex\" : \"id.string\",%n") + "      \"validateFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"property1\" ]%n") + "      } ]%n") + "    }%n") + "  },%n") + "  \"types\" : {%n") + "    \"id.string\" : {%n") + "      \"description\" : \"String type description\",%n") + "      \"class\" : \"java.lang.String\"%n") + "    },%n") + "    \"prop.map\" : {%n") + "      \"serialiser\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.serialisation.implementation.MapSerialiser\",%n") + "          \"keySerialiser\" : \"uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser\",%n") + "          \"valueSerialiser\" : \"uk.gov.gchq.gaffer.serialisation.implementation.raw.RawLongSerialiser\",%n") + "          \"mapClass\" : \"java.util.LinkedHashMap\"%n") + "      },%n") + "      \"description\" : \"Map type description\",%n") + "      \"class\" : \"java.util.LinkedHashMap\"%n") + "    },%n") + "    \"prop.string\" : {%n") + "      \"description\" : \"String type description\",%n") + "      \"class\" : \"java.lang.String\"%n") + "    },%n") + "    \"prop.integer\" : {%n") + "      \"description\" : \"Integer type description\",%n") + "      \"class\" : \"java.lang.Integer\"%n") + "    },%n") + "    \"timestamp\" : {%n") + "      \"description\" : \"Timestamp type description\",%n") + "      \"class\" : \"java.lang.Long\"%n") + "    }%n") + "  },%n") + "  \"visibilityProperty\" : \"visibility\",%n") + "  \"timestampProperty\" : \"timestamp\",%n") + "  \"config\" : {\n") + "    \"key\" : \"value\",\n") + "    \"timestampProperty\" : \"timestamp\"\n") + "  }") + "}"))), new String(schema.toJson(true)));
    }

    @Test
    public void testCorrectSerialiserRetrievableFromConfig() throws NotSerializableException {
        Schema store = new Schema.Builder().type(TestTypes.PROP_STRING, new TypeDefinition.Builder().clazz(String.class).serialiser(new JavaSerialiser()).build()).edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, TestTypes.PROP_STRING).build()).build();
        Assert.assertEquals(JavaSerialiser.class, store.getElement(EDGE).getPropertyTypeDef(PROP_1).getSerialiser().getClass());
    }

    @Test
    public void testStoreConfigUsableWithSchemaInitialisationAndProgramaticListOfElements() {
        final SchemaEntityDefinition entityDef = new SchemaEntityDefinition.Builder().property(PROP_1, TestTypes.PROP_STRING).build();
        final SchemaEdgeDefinition edgeDef = new SchemaEdgeDefinition.Builder().property(PROP_2, TestTypes.PROP_STRING).build();
        final Schema schema = new Schema.Builder().type(TestTypes.PROP_STRING, String.class).type(TestTypes.PROP_STRING, Integer.class).entity(ENTITY, entityDef).edge(EDGE, edgeDef).build();
        Assert.assertSame(entityDef, schema.getEntity(ENTITY));
        Assert.assertSame(edgeDef, schema.getEdge(EDGE));
    }

    @Test
    public void testSchemaConstructedFromInputStream() throws IOException {
        final InputStream resourceAsStream = this.getClass().getResourceAsStream(ELEMENTS_SCHEMA);
        Assert.assertNotNull(resourceAsStream);
        final Schema deserialisedSchema = new Schema.Builder().json(resourceAsStream).build();
        Assert.assertNotNull(deserialisedSchema);
        final Map<String, SchemaEdgeDefinition> edges = deserialisedSchema.getEdges();
        Assert.assertEquals(1, edges.size());
        final SchemaElementDefinition edgeGroup = edges.get(EDGE);
        Assert.assertEquals(3, edgeGroup.getProperties().size());
        final Map<String, SchemaEntityDefinition> entities = deserialisedSchema.getEntities();
        Assert.assertEquals(1, entities.size());
        final SchemaElementDefinition entityGroup = entities.get(ENTITY);
        Assert.assertEquals(3, entityGroup.getProperties().size());
        Assert.assertEquals(VISIBILITY, deserialisedSchema.getVisibilityProperty());
        Assert.assertEquals(TIMESTAMP, deserialisedSchema.getTimestampProperty());
        Assert.assertEquals(2, deserialisedSchema.getConfig().size());
        Assert.assertEquals("value", deserialisedSchema.getConfig("key"));
        Assert.assertEquals(TIMESTAMP, deserialisedSchema.getConfig("timestampProperty"));
    }

    @Test
    public void shouldBuildSchema() {
        // Given
        final Serialiser vertexSerialiser = Mockito.mock(Serialiser.class);
        // When
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition()).entity(ENTITY, new SchemaEntityDefinition()).entity(ENTITY_2, new SchemaEntityDefinition()).edge(EDGE_2, new SchemaEdgeDefinition()).vertexSerialiser(vertexSerialiser).type(TestTypes.PROP_STRING, String.class).visibilityProperty(VISIBILITY).config("key", "value").build();
        // Then
        Assert.assertEquals(2, schema.getEdges().size());
        Assert.assertNotNull(schema.getEdge(EDGE));
        Assert.assertNotNull(schema.getEdge(EDGE_2));
        Assert.assertEquals(2, schema.getEntities().size());
        Assert.assertNotNull(schema.getEntity(ENTITY));
        Assert.assertNotNull(schema.getEntity(ENTITY_2));
        Assert.assertEquals(String.class, schema.getType(TestTypes.PROP_STRING).getClazz());
        Assert.assertSame(vertexSerialiser, schema.getVertexSerialiser());
        Assert.assertEquals(VISIBILITY, schema.getVisibilityProperty());
        Assert.assertEquals("value", schema.getConfig("key"));
    }

    @Test
    public void shouldMergeDifferentSchemas() {
        // Given
        final String typeShared = "typeShared";
        final String type1 = "type1";
        final String type2 = "type2";
        final Serialiser vertexSerialiser = Mockito.mock(Serialiser.class);
        final Schema schema1 = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, type1).build()).entity(ENTITY, new SchemaEntityDefinition.Builder().property(COUNT, typeShared).build()).vertexSerialiser(vertexSerialiser).type(typeShared, Long.class).type(type1, Integer.class).visibilityProperty(VISIBILITY).config("key1a", "value1a").config("key1b", "value1b").build();
        final Schema schema2 = new Schema.Builder().entity(ENTITY_2, new SchemaEntityDefinition.Builder().property(COUNT, typeShared).build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().property(PROP_2, type2).build()).type(type2, String.class).type(typeShared, Long.class).config("key1b", "value1c").config("key2", "value2").build();
        // When
        final Schema mergedSchema = // should be able to merge a duplicate schema
        new Schema.Builder().merge(schema1).merge(schema1).merge(schema2).merge(schema2).build();
        // Then
        Assert.assertEquals(2, mergedSchema.getEdges().size());
        Assert.assertEquals(1, mergedSchema.getEdge(EDGE).getPropertyMap().size());
        Assert.assertEquals(type1, mergedSchema.getEdge(EDGE).getPropertyMap().get(PROP_1));
        Assert.assertEquals(1, mergedSchema.getEdge(EDGE_2).getPropertyMap().size());
        Assert.assertEquals(type2, mergedSchema.getEdge(EDGE_2).getPropertyMap().get(PROP_2));
        Assert.assertEquals(2, mergedSchema.getEntities().size());
        Assert.assertEquals(1, mergedSchema.getEntity(ENTITY).getPropertyMap().size());
        Assert.assertEquals(typeShared, mergedSchema.getEntity(ENTITY).getPropertyMap().get(COUNT));
        Assert.assertEquals(1, mergedSchema.getEntity(ENTITY_2).getPropertyMap().size());
        Assert.assertEquals(typeShared, mergedSchema.getEntity(ENTITY_2).getPropertyMap().get(COUNT));
        Assert.assertEquals(Integer.class, mergedSchema.getType(type1).getClazz());
        Assert.assertEquals(String.class, mergedSchema.getType(type2).getClazz());
        Assert.assertSame(vertexSerialiser, mergedSchema.getVertexSerialiser());
        Assert.assertEquals(VISIBILITY, mergedSchema.getVisibilityProperty());
        Assert.assertEquals("value1a", mergedSchema.getConfig("key1a"));
        Assert.assertEquals("value1c", mergedSchema.getConfig("key1b"));
        Assert.assertEquals("value2", mergedSchema.getConfig("key2"));
    }

    @Test
    public void shouldMergeDifferentSchemasOppositeWayAround() {
        // Given
        // Given
        final String typeShared = "typeShared";
        final String type1 = "type1";
        final String type2 = "type2";
        final Serialiser vertexSerialiser = Mockito.mock(Serialiser.class);
        final Schema schema1 = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, type1).build()).entity(ENTITY, new SchemaEntityDefinition.Builder().property(COUNT, typeShared).build()).vertexSerialiser(vertexSerialiser).type(typeShared, Long.class).type(type1, Integer.class).visibilityProperty(VISIBILITY).build();
        final Schema schema2 = new Schema.Builder().entity(ENTITY_2, new SchemaEntityDefinition.Builder().property(COUNT, typeShared).build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().property(PROP_2, type2).build()).type(type2, String.class).type(typeShared, Long.class).build();
        // When
        final Schema mergedSchema = // should be able to merge a duplicate schema
        new Schema.Builder().merge(schema2).merge(schema2).merge(schema1).merge(schema1).build();
        // Then
        Assert.assertEquals(2, mergedSchema.getEdges().size());
        Assert.assertEquals(1, mergedSchema.getEdge(EDGE).getPropertyMap().size());
        Assert.assertEquals(type1, mergedSchema.getEdge(EDGE).getPropertyMap().get(PROP_1));
        Assert.assertEquals(1, mergedSchema.getEdge(EDGE_2).getPropertyMap().size());
        Assert.assertEquals(type2, mergedSchema.getEdge(EDGE_2).getPropertyMap().get(PROP_2));
        Assert.assertEquals(2, mergedSchema.getEntities().size());
        Assert.assertEquals(1, mergedSchema.getEntity(ENTITY).getPropertyMap().size());
        Assert.assertEquals(typeShared, mergedSchema.getEntity(ENTITY).getPropertyMap().get(COUNT));
        Assert.assertEquals(1, mergedSchema.getEntity(ENTITY_2).getPropertyMap().size());
        Assert.assertEquals(typeShared, mergedSchema.getEntity(ENTITY_2).getPropertyMap().get(COUNT));
        Assert.assertEquals(Integer.class, mergedSchema.getType(type1).getClazz());
        Assert.assertEquals(String.class, mergedSchema.getType(type2).getClazz());
        Assert.assertSame(vertexSerialiser, mergedSchema.getVertexSerialiser());
        Assert.assertEquals(VISIBILITY, mergedSchema.getVisibilityProperty());
    }

    @Test
    public void shouldThrowExceptionWhenMergeSchemasWithASharedEdgeGroup() {
        // Given
        final Schema schema1 = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "string").build()).build();
        final Schema schema2 = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_2, "string").build()).build();
        // When / Then
        try {
            new Schema.Builder().merge(schema1).merge(schema2);
            Assert.fail("Exception expected");
        } catch (final SchemaException e) {
            Assert.assertTrue(("Actual message was: " + (e.getMessage())), e.getMessage().contains("Element group properties cannot be defined in different schema parts"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenMergeSchemasWithASharedEntityGroup() {
        // Given
        final Schema schema1 = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "string").build()).build();
        final Schema schema2 = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_2, "string").build()).build();
        // When / Then
        try {
            new Schema.Builder().merge(schema1).merge(schema2);
            Assert.fail("Exception expected");
        } catch (final SchemaException e) {
            Assert.assertTrue(("Actual message was: " + (e.getMessage())), e.getMessage().contains("Element group properties cannot be defined in different schema parts"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenMergeSchemasWithConflictingVertexSerialiser() {
        // Given
        final Serialiser vertexSerialiser1 = Mockito.mock(Serialiser.class);
        final Serialiser vertexSerialiser2 = Mockito.mock(SchemaTest.SerialisationImpl.class);
        final Schema schema1 = new Schema.Builder().vertexSerialiser(vertexSerialiser1).build();
        final Schema schema2 = new Schema.Builder().vertexSerialiser(vertexSerialiser2).build();
        // When / Then
        try {
            new Schema.Builder().merge(schema1).merge(schema2).build();
            Assert.fail("Exception expected");
        } catch (final SchemaException e) {
            Assert.assertTrue(e.getMessage().contains("vertex serialiser"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenMergeSchemasWithConflictingVisibility() {
        // Given
        final Schema schema1 = new Schema.Builder().visibilityProperty(VISIBILITY).build();
        final Schema schema2 = new Schema.Builder().visibilityProperty(COUNT).build();
        // When / Then
        try {
            new Schema.Builder().merge(schema1).merge(schema2).build();
            Assert.fail("Exception expected");
        } catch (final SchemaException e) {
            Assert.assertTrue(e.getMessage().contains("visibility property"));
        }
    }

    @Test
    public void shouldNotRemoveMissingParentsWhenExpanded() {
        // When
        final Schema schema = new Schema.Builder().edge(EDGE_2, new SchemaEdgeDefinition.Builder().parents(EDGE).build()).build();
        // Then
        Assert.assertArrayEquals(new String[]{ TestGroups.EDGE }, schema.getEdge(EDGE_2).getParents().toArray());
    }

    @Test
    public void shouldInheritIdentifiersFromParents() {
        // When
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("int").build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().parents(EDGE).destination("long").directed("true").build()).merge(new Schema.Builder().edge(EDGE_3, new SchemaEdgeDefinition.Builder().parents(EDGE_2, EDGE).source("date").build()).build()).build();
        // Then
        final SchemaEdgeDefinition childEdge1 = schema.getEdge(EDGE);
        Assert.assertEquals("string", childEdge1.getSource());
        Assert.assertEquals("int", childEdge1.getDestination());
        Assert.assertEquals(null, childEdge1.getDirected());
        final SchemaEdgeDefinition childEdge2 = schema.getEdge(EDGE_2);
        Assert.assertEquals("string", childEdge2.getSource());
        Assert.assertEquals("long", childEdge2.getDestination());
        Assert.assertEquals("true", childEdge2.getDirected());
        final SchemaEdgeDefinition childEdge3 = schema.getEdge(EDGE_3);
        Assert.assertEquals("date", childEdge3.getSource());
        Assert.assertEquals("int", childEdge3.getDestination());
        Assert.assertEquals("true", childEdge3.getDirected());
    }

    @Test
    public void shouldInheritPropertiesFromParentsInOrderFromJson() {
        // When
        final Schema schema = new Schema.Builder().json(StreamUtil.openStream(getClass(), "schemaWithParents.json")).build();
        // Then
        // Check edges
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1, TestPropertyNames.PROP_2, TestPropertyNames.PROP_3, TestPropertyNames.PROP_4 }, schema.getEdge(EDGE_4).getProperties().toArray());
        // Check order of properties and overrides is from order of parents
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1, TestPropertyNames.PROP_2, TestPropertyNames.PROP_3, TestPropertyNames.PROP_4, TestPropertyNames.PROP_5 }, schema.getEdge(EDGE_5).getProperties().toArray());
        Assert.assertEquals("A parent edge with a single property", schema.getEdge(EDGE).getDescription());
        Assert.assertEquals("An edge that should have properties: 1, 2, 3, 4 and 5", schema.getEdge(EDGE_5).getDescription());
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1 }, schema.getEdge(EDGE).getGroupBy().toArray());
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_4 }, schema.getEdge(EDGE_5).getGroupBy().toArray());
        // Check entities
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1, TestPropertyNames.PROP_2, TestPropertyNames.PROP_3, TestPropertyNames.PROP_4 }, schema.getEntity(ENTITY_4).getProperties().toArray());
        // Check order of properties and overrides is from order of parents
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1, TestPropertyNames.PROP_2, TestPropertyNames.PROP_3, TestPropertyNames.PROP_4, TestPropertyNames.PROP_5 }, schema.getEntity(ENTITY_5).getProperties().toArray());
        Assert.assertEquals("A parent entity with a single property", schema.getEntity(ENTITY).getDescription());
        Assert.assertEquals("An entity that should have properties: 1, 2, 3, 4 and 5", schema.getEntity(ENTITY_5).getDescription());
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1 }, schema.getEntity(ENTITY).getGroupBy().toArray());
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_4 }, schema.getEntity(ENTITY_5).getGroupBy().toArray());
    }

    @Test
    public void shouldInheritPropertiesFromParentsInOrder() {
        // When
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "prop.string").build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().property(PROP_2, "prop.string2").build()).edge(EDGE_3, new SchemaEdgeDefinition.Builder().parents(EDGE, EDGE_2).property(PROP_3, "prop.string3").build()).edge(EDGE_4, new SchemaEdgeDefinition.Builder().parents(EDGE_3).property(PROP_4, "prop.string4").build()).edge(EDGE_5, new SchemaEdgeDefinition.Builder().parents(EDGE_4).property(PROP_5, "prop.string5").build()).build();
        // Then
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1, TestPropertyNames.PROP_2, TestPropertyNames.PROP_3, TestPropertyNames.PROP_4 }, schema.getEdge(EDGE_4).getProperties().toArray());
        // Then - check order of properties and overrides is from order of parents
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1, TestPropertyNames.PROP_2, TestPropertyNames.PROP_3, TestPropertyNames.PROP_4, TestPropertyNames.PROP_5 }, schema.getEdge(EDGE_5).getProperties().toArray());
    }

    @Test
    public void shouldThrowExceptionIfPropertyExistsInParentAndChild() {
        // When / Then
        try {
            new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "prop.string").property(PROP_2, "prop.integer").build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().parents(EDGE).property(PROP_1, "prop.string.changed").build()).build();
            Assert.fail("Exception expected");
        } catch (final SchemaException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void shouldOverrideInheritedParentGroupBy() {
        // When
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "prop.string").property(PROP_2, "prop.integer").groupBy(PROP_1).build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().groupBy(PROP_2).parents(EDGE).build()).build();
        // Then
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_2 }, schema.getEdge(EDGE_2).getGroupBy().toArray());
    }

    @Test
    public void shouldOverrideInheritedParentDescriptionWhenSet() {
        // When
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "prop.string").property(PROP_2, "prop.integer").groupBy(PROP_1).description("A description").build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().parents(EDGE).description("A new description").build()).build();
        // Then
        Assert.assertEquals("A new description", schema.getEdge(EDGE_2).getDescription());
    }

    @Test
    public void shouldNotOverrideInheritedParentDescriptionWhenNotSet() {
        // When
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "prop.string").property(PROP_2, "prop.integer").groupBy(PROP_1).description("A description").build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().parents(EDGE).build()).build();
        // Then
        Assert.assertEquals("A description", schema.getEdge(EDGE_2).getDescription());
    }

    @Test
    public void shouldOverrideInheritedParentGroupByWhenSet() {
        // When
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "prop.string").property(PROP_2, "prop.integer").groupBy(PROP_1).build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().parents(EDGE).groupBy(PROP_2).build()).build();
        // Then
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_2 }, schema.getEdge(EDGE_2).getGroupBy().toArray());
    }

    @Test
    public void shouldNotOverrideInheritedParentGroupByWhenEmpty() {
        // When
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "prop.string").property(PROP_2, "prop.integer").groupBy(PROP_1).build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().groupBy().parents(EDGE).build()).build();
        // Then
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1 }, schema.getEdge(EDGE_2).getGroupBy().toArray());
    }

    @Test
    public void shouldNotOverrideInheritedParentGroupByWhenNotSet() {
        // When
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "prop.string").property(PROP_2, "prop.integer").groupBy(PROP_1).build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().parents(EDGE).build()).build();
        // Then
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1 }, schema.getEdge(EDGE_2).getGroupBy().toArray());
    }

    @Test
    public void shouldNotOverrideGroupByWhenMergingAndItIsNotSet() {
        // When
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "prop.string").property(PROP_2, "prop.integer").groupBy(PROP_1).build()).merge(new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().build()).build()).build();
        // Then
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1 }, schema.getEdge(EDGE).getGroupBy().toArray());
    }

    @Test
    public void shouldNotOverrideGroupByWhenMergingAndItIsEmpty() {
        // When
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "prop.string").property(PROP_2, "prop.integer").groupBy(PROP_1).build()).merge(new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().groupBy().build()).build()).build();
        // Then
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1 }, schema.getEdge(EDGE).getGroupBy().toArray());
    }

    @Test
    public void shouldSerialiseToCompactJson() {
        // Given - schema loaded from file
        // When
        final String compactJson = new String(schema.toCompactJson());
        // Then - no description fields or new lines
        Assert.assertFalse(compactJson.contains("description"));
        Assert.assertFalse(compactJson.contains(String.format("%n")));
    }

    @Test
    public void shouldGetAllGroups() {
        // Given - schema loaded from file
        // When
        final Set<String> groups = schema.getGroups();
        // Then
        final Set<String> allGroups = new java.util.HashSet(schema.getEntityGroups());
        allGroups.addAll(schema.getEdgeGroups());
        Assert.assertEquals(allGroups, groups);
    }

    @Test
    public void shouldReturnTrueWhenSchemaHasValidatorEntityFilters() {
        // Given
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().validator(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).edge(EDGE, new SchemaEdgeDefinition()).build();
        // When
        final boolean result = schema.hasValidation();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWhenSchemaHasValidatorEntityPropertyFilters() {
        // Given
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "str").build()).type("str", new TypeDefinition.Builder().validateFunctions(new Exists()).build()).edge(EDGE, new SchemaEdgeDefinition()).build();
        // When
        final boolean result = schema.hasValidation();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWhenSchemaHasValidatorEntityIdentifierFilters() {
        // Given
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("str").build()).type("str", new TypeDefinition.Builder().validateFunctions(new Exists()).build()).edge(EDGE, new SchemaEdgeDefinition()).build();
        // When
        final boolean result = schema.hasValidation();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWhenSchemaHasValidatorEdgeFilters() {
        // Given
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().validator(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).edge(ENTITY, new SchemaEdgeDefinition()).build();
        // When
        final boolean result = schema.hasValidation();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWhenSchemaHasValidatorEdgePropertyFilters() {
        // Given
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "str").build()).type("str", new TypeDefinition.Builder().validateFunctions(new Exists()).build()).edge(ENTITY, new SchemaEdgeDefinition()).build();
        // When
        final boolean result = schema.hasValidation();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWhenSchemaHasValidatorEdgeIdentifierFilters() {
        // Given
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().source("str").destination("dest").build()).type("str", new TypeDefinition.Builder().validateFunctions(new Exists()).build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().source("src").destination("dest").build()).build();
        // When
        final boolean result = schema.hasValidation();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenSchemaHasNullValidatorEdgeFilters() {
        // Given
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().validator(null).build()).build();
        // When
        final boolean result = schema.hasValidation();
        // Then
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnFalseWhenSchemaHasEmptyValidatorEdgeFilters() {
        // Given
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().validator(new ElementFilter.Builder().build()).build()).build();
        // When
        final boolean result = schema.hasValidation();
        // Then
        Assert.assertFalse(result);
    }

    @Test
    public void shouldThrowExceptionWhenEdgeGroupIsInvalid() {
        // Given
        final SchemaEdgeDefinition edgeDef = new SchemaEdgeDefinition();
        final String invalidGroupString = "invalidGroup-@?";
        // When / Then
        try {
            new Schema.Builder().edge(invalidGroupString, edgeDef).build();
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Group is invalid"));
        }
    }

    @Test
    public void shouldBuildSchemaWhenEdgeGroupIsValid() {
        // Given
        final SchemaEdgeDefinition edgeDef = new SchemaEdgeDefinition();
        final String validGroupString = "val1dGr0up||";
        // When
        new Schema.Builder().edge(validGroupString, edgeDef).build();
        // Then - no exceptions
    }

    @Test
    public void shouldThrowExceptionWhenEntityGroupIsInvalid() {
        // Given
        final SchemaEntityDefinition entityDef = new SchemaEntityDefinition();
        final String invalidGroupString = "invalidGroup-@?";
        // When / Then
        try {
            new Schema.Builder().entity(invalidGroupString, entityDef).build();
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Group is invalid"));
        }
    }

    @Test
    public void shouldBuildSchemaWhenEntityGroupIsValid() {
        // Given
        final SchemaEntityDefinition entityDef = new SchemaEntityDefinition();
        final String validGroupString = "val1dGr0up||";
        // When
        new Schema.Builder().entity(validGroupString, entityDef).build();
        // Then - no exceptions
    }

    @Test
    public void shouldThrowExceptionWhenEdgePropertyIsInvalid() {
        // When / Then
        try {
            new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property("invalidPropName{@3#", "str").build());
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Property is invalid"));
        }
    }

    @Test
    public void shouldBuildSchemaWhenEdgePropertyisValid() {
        // Given
        final SchemaEdgeDefinition edgeDef = new SchemaEdgeDefinition.Builder().property("val1dPr0perty||", "str").build();
        // When
        new Schema.Builder().edge(EDGE, edgeDef);
        // Then - no exceptions
    }

    @Test
    public void shouldThrowExceptionWhenEntityPropertyIsInvalid() {
        // When / Then
        try {
            new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().property("invalidPropName{@3#", "str").build());
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Property is invalid"));
        }
    }

    @Test
    public void shouldBuildSchemaWhenEntityPropertyIsValid() {
        // Given
        final SchemaEntityDefinition entityDef = new SchemaEntityDefinition.Builder().property("val1dPr0perty||", "str").build();
        // When
        new Schema.Builder().entity(ENTITY, entityDef);
        // Then - no exceptions
    }

    @Test
    public void shouldAddMergedSchemaToLibrary() {
        // Given
        final HashMapGraphLibrary graphLibrary = new HashMapGraphLibrary();
        final Schema schema1ToMerge = new Schema.Builder().build();
        final Schema schema2ToMerge = new Schema.Builder().build();
        final Schema schema = new Schema.Builder().merge(schema1ToMerge).merge(schema2ToMerge).build();
        // When
        graphLibrary.addSchema("TEST_SCHEMA_ID_merged", schema);
        // Then - no exceptions
    }

    private class SerialisationImpl implements ToBytesSerialiser<Object> {
        private static final long serialVersionUID = 5055359689222968046L;

        @Override
        public boolean canHandle(final Class clazz) {
            return false;
        }

        @Override
        public byte[] serialise(final Object object) throws SerialisationException {
            return new byte[0];
        }

        @Override
        public Object deserialise(final byte[] bytes) throws SerialisationException {
            return null;
        }

        @Override
        public Object deserialiseEmpty() throws SerialisationException {
            return null;
        }

        @Override
        public boolean preservesObjectOrdering() {
            return true;
        }

        @Override
        public boolean isConsistent() {
            return true;
        }
    }
}

