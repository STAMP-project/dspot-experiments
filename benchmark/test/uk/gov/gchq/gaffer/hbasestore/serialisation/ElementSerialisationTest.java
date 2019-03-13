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
package uk.gov.gchq.gaffer.hbasestore.serialisation;


import EdgeDirection.DIRECTED;
import EdgeDirection.DIRECTED_REVERSED;
import EdgeDirection.UNDIRECTED;
import HBaseStoreConstants.TIMESTAMP_PROPERTY;
import IdentifierType.VERTEX;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestPropertyNames.TIMESTAMP;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.ByteArrayEscapeUtils;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.commonutil.pair.Pair;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.EdgeDirection;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.Properties;
import uk.gov.gchq.gaffer.data.elementdefinition.exception.SchemaException;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.hbasestore.util.HBasePropertyNames;
import uk.gov.gchq.gaffer.hbasestore.utils.HBaseStoreConstants;
import uk.gov.gchq.gaffer.serialisation.FreqMapSerialiser;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.gaffer.types.FreqMap;
import uk.gov.gchq.gaffer.types.function.FreqMapAggregator;


/**
 * Copied and adapted from the AcummuloStore ElementConverterTests
 */
public class ElementSerialisationTest {
    private ElementSerialisation serialisation;

    // TEST WE CAN RETRIEVE AN ELEMENT FROM A KEY THAT HAS BEEN CREATED CORRECTLY
    @Test
    public void shouldReturnHBaseKeySerialisationFromBasicEdge() throws IOException, SchemaException {
        // Given
        final Edge edge = new Edge.Builder().group(EDGE).dest("2").source("1").directed(true).build();
        // When
        final Pair<byte[], byte[]> keys = serialisation.getRowKeys(edge);
        // Then
        final Edge newEdge = ((Edge) (serialisation.getPartialElement(EDGE, keys.getFirst(), false)));
        Assert.assertEquals("1", newEdge.getSource());
        Assert.assertEquals("2", newEdge.getDestination());
        Assert.assertEquals(true, newEdge.isDirected());
    }

    @Test
    public void shouldReturnHBaseKeySerialisationFromBasicEntity() throws IOException, SchemaException {
        // Given
        final Entity entity = new Entity.Builder().group(ENTITY).vertex("3").build();
        // When
        final byte[] key = serialisation.getRowKey(entity);
        // Then
        final Entity newEntity = ((Entity) (serialisation.getPartialElement(ENTITY, key, false)));
        Assert.assertEquals("3", newEntity.getVertex());
    }

    @Test
    public void shouldReturnHBaseKeySerialisationFromCFCQPropertyEdge() throws IOException, SchemaException {
        // Given
        final Edge edge = new Edge.Builder().group(EDGE).property(HBasePropertyNames.COLUMN_QUALIFIER, 100).build();
        // When
        final byte[] columnQualifier = serialisation.getColumnQualifier(edge);
        final Properties properties = serialisation.getPropertiesFromColumnQualifier(EDGE, columnQualifier);
        // Then
        Assert.assertEquals(100, properties.get(HBasePropertyNames.COLUMN_QUALIFIER));
    }

    @Test
    public void shouldReturnHBaseKeySerialisationFromCFCQPropertyEntity() throws IOException, SchemaException {
        // Given
        final Entity entity = getExampleEntity(100);
        // When
        final byte[] columnQualifier = serialisation.getColumnQualifier(entity);
        final Properties properties = serialisation.getPropertiesFromColumnQualifier(ENTITY, columnQualifier);
        // Then
        Assert.assertEquals(100, properties.get(HBasePropertyNames.COLUMN_QUALIFIER));
    }

    @Test
    public void shouldReturnHBaseKeySerialisationMultipleCQPropertyEdge() throws IOException, SchemaException {
        // Given
        final Edge edge = new Edge.Builder().group(EDGE).source("1").dest("2").directed(true).property(HBasePropertyNames.COLUMN_QUALIFIER, 100).build();
        // When
        final byte[] columnQualifier = serialisation.getColumnQualifier(edge);
        final Properties properties = serialisation.getPropertiesFromColumnQualifier(EDGE, columnQualifier);
        // Then
        Assert.assertEquals(100, properties.get(HBasePropertyNames.COLUMN_QUALIFIER));
    }

    @Test
    public void shouldReturnHBaseKeySerialisationMultipleCQPropertiesEntity() throws IOException, SchemaException {
        // Given
        final Entity entity = new Entity.Builder().group(ENTITY).vertex("3").property(HBasePropertyNames.COLUMN_QUALIFIER, 100).build();
        // When
        final byte[] columnQualifier = serialisation.getColumnQualifier(entity);
        final Properties properties = serialisation.getPropertiesFromColumnQualifier(ENTITY, columnQualifier);
        // Then
        Assert.assertEquals(100, properties.get(HBasePropertyNames.COLUMN_QUALIFIER));
    }

    @Test
    public void shouldGetOriginalEdgeWithMatchAsSourceNotSet() throws IOException, SchemaException {
        // Given
        final Edge edge = new Edge.Builder().group(EDGE).dest("2").source("1").directed(true).build();
        final Pair<byte[], byte[]> keys = serialisation.getRowKeys(edge);
        // When
        final Edge newEdge = ((Edge) (serialisation.getPartialElement(EDGE, keys.getSecond(), false)));
        // Then
        Assert.assertEquals("1", newEdge.getSource());
        Assert.assertEquals("2", newEdge.getDestination());
        Assert.assertEquals(true, newEdge.isDirected());
    }

    @Test
    public void shouldSkipNullPropertyValuesWhenCreatingHBaseKey() throws IOException, SchemaException {
        // Given
        final Edge edge = new Edge.Builder().group(EDGE).source("1").dest("2").directed(true).property(HBasePropertyNames.COLUMN_QUALIFIER, null).build();
        // When
        final byte[] columnQualifier = serialisation.getColumnQualifier(edge);
        Properties properties = serialisation.getPropertiesFromColumnQualifier(EDGE, columnQualifier);
        // Then
        Assert.assertEquals(null, properties.get(HBasePropertyNames.COLUMN_QUALIFIER));
    }

    @Test
    public void shouldSerialiseAndDeSerialiseBetweenPropertyAndValue() throws Exception {
        Properties properties = new Properties();
        properties.put(PROP_1, 60);
        properties.put(PROP_2, 166);
        properties.put(PROP_3, 299);
        properties.put(PROP_4, 10);
        properties.put(COUNT, 8);
        final byte[] value = serialisation.getValue(EDGE, properties);
        final Properties deSerialisedProperties = serialisation.getPropertiesFromValue(EDGE, value);
        Assert.assertEquals(60, deSerialisedProperties.get(PROP_1));
        Assert.assertEquals(166, deSerialisedProperties.get(PROP_2));
        Assert.assertEquals(299, deSerialisedProperties.get(PROP_3));
        Assert.assertEquals(10, deSerialisedProperties.get(PROP_4));
        Assert.assertEquals(8, deSerialisedProperties.get(COUNT));
    }

    @Test
    public void shouldSerialiseAndDeSerialiseBetweenPropertyAndValueMissingMiddleProperty() throws Exception {
        Properties properties = new Properties();
        properties.put(PROP_1, 60);
        properties.put(PROP_3, 299);
        properties.put(PROP_4, 10);
        properties.put(COUNT, 8);
        final byte[] value = serialisation.getValue(EDGE, properties);
        final Properties deSerialisedProperties = serialisation.getPropertiesFromValue(EDGE, value);
        Assert.assertEquals(60, deSerialisedProperties.get(PROP_1));
        Assert.assertEquals(299, deSerialisedProperties.get(PROP_3));
        Assert.assertEquals(10, deSerialisedProperties.get(PROP_4));
        Assert.assertEquals(8, deSerialisedProperties.get(COUNT));
    }

    @Test
    public void shouldSerialiseAndDeSerialiseBetweenPropertyAndValueMissingEndProperty() throws Exception {
        Properties properties = new Properties();
        properties.put(PROP_1, 60);
        properties.put(PROP_2, 166);
        properties.put(PROP_3, 299);
        properties.put(PROP_4, 10);
        final byte[] value = serialisation.getValue(EDGE, properties);
        final Properties deSerialisedProperties = serialisation.getPropertiesFromValue(EDGE, value);
        Assert.assertEquals(60, deSerialisedProperties.get(PROP_1));
        Assert.assertEquals(166, deSerialisedProperties.get(PROP_2));
        Assert.assertEquals(299, deSerialisedProperties.get(PROP_3));
        Assert.assertEquals(10, deSerialisedProperties.get(PROP_4));
    }

    @Test
    public void shouldSerialiseAndDeSerialiseBetweenPropertyAndValueMissingStartProperty() throws Exception {
        Properties properties = new Properties();
        properties.put(PROP_2, 166);
        properties.put(PROP_3, 299);
        properties.put(PROP_4, 10);
        properties.put(COUNT, 8);
        final byte[] value = serialisation.getValue(EDGE, properties);
        final Properties deSerialisedProperties = serialisation.getPropertiesFromValue(EDGE, value);
        Assert.assertEquals(166, deSerialisedProperties.get(PROP_2));
        Assert.assertEquals(299, deSerialisedProperties.get(PROP_3));
        Assert.assertEquals(10, deSerialisedProperties.get(PROP_4));
        Assert.assertEquals(8, deSerialisedProperties.get(COUNT));
    }

    @Test
    public void shouldSerialiseAndDeSerialiseBetweenPropertyAndValueWithNullProperty() throws Exception {
        Properties properties = new Properties();
        properties.put(PROP_1, 5);
        properties.put(PROP_2, null);
        properties.put(PROP_3, 299);
        properties.put(PROP_4, 10);
        properties.put(COUNT, 8);
        final byte[] value = serialisation.getValue(EDGE, properties);
        final Properties deSerialisedProperties = serialisation.getPropertiesFromValue(EDGE, value);
        Assert.assertEquals(5, deSerialisedProperties.get(PROP_1));
        Assert.assertNull(deSerialisedProperties.get(PROP_2));
        Assert.assertEquals(299, deSerialisedProperties.get(PROP_3));
        Assert.assertEquals(10, deSerialisedProperties.get(PROP_4));
        Assert.assertEquals(8, deSerialisedProperties.get(COUNT));
    }

    @Test
    public void shouldTruncatePropertyBytes() throws Exception {
        // Given
        final Properties properties = new Properties() {
            {
                put(HBasePropertyNames.COLUMN_QUALIFIER, 1);
                put(HBasePropertyNames.COLUMN_QUALIFIER_2, 2);
                put(HBasePropertyNames.COLUMN_QUALIFIER_3, 3);
                put(HBasePropertyNames.COLUMN_QUALIFIER_4, 4);
            }
        };
        final byte[] bytes = serialisation.getColumnQualifier(EDGE, properties);
        // When
        final byte[] truncatedPropertyBytes = serialisation.getPropertiesAsBytesFromColumnQualifier(EDGE, bytes, 2);
        // Then
        final Properties truncatedProperties = new Properties() {
            {
                put(HBasePropertyNames.COLUMN_QUALIFIER, 1);
                put(HBasePropertyNames.COLUMN_QUALIFIER_2, 2);
            }
        };
        final byte[] expectedBytes = serialisation.getColumnQualifier(EDGE, truncatedProperties);
        final byte[] expectedTruncatedPropertyBytes = serialisation.getPropertiesAsBytesFromColumnQualifier(EDGE, expectedBytes, 2);
        Assert.assertArrayEquals(expectedTruncatedPropertyBytes, truncatedPropertyBytes);
    }

    @Test
    public void shouldTruncatePropertyBytesWithEmptyBytes() throws Exception {
        // Given
        final byte[] bytes = HBaseStoreConstants.EMPTY_BYTES;
        // When
        final byte[] truncatedBytes = serialisation.getPropertiesAsBytesFromColumnQualifier(EDGE, bytes, 2);
        // Then
        Assert.assertEquals(0, truncatedBytes.length);
    }

    @Test
    public void shouldBuildTimestampFromProperty() throws Exception {
        // Given
        // add extra timestamp property to schema
        final Schema schema = new Schema.Builder().json(StreamUtil.schemas(getClass())).build();
        serialisation = new ElementSerialisation(type("timestamp", Long.class).edge(EDGE, new SchemaEdgeDefinition.Builder().property(TIMESTAMP, "timestamp").build()).config(TIMESTAMP_PROPERTY, TIMESTAMP).build());
        final long propertyTimestamp = 10L;
        final Properties properties = new Properties();
        properties.put(HBasePropertyNames.COLUMN_QUALIFIER, 1);
        properties.put(PROP_1, 2);
        properties.put(TIMESTAMP, propertyTimestamp);
        // When
        final long timestamp = serialisation.getTimestamp(properties);
        // Then
        Assert.assertEquals(propertyTimestamp, timestamp);
    }

    @Test
    public void shouldBuildRandomTimeBasedTimestampWhenPropertyIsNull() throws Exception {
        // Given
        // add extra timestamp property to schema
        final Schema schema = new Schema.Builder().json(StreamUtil.schemas(getClass())).build();
        serialisation = new ElementSerialisation(new Schema.Builder(schema).config(TIMESTAMP_PROPERTY, TIMESTAMP).build());
        final Long propertyTimestamp = null;
        final Properties properties = new Properties();
        properties.put(TIMESTAMP, propertyTimestamp);
        // When
        final int n = 100;
        final Set<Long> timestamps = new HashSet<>(n);
        for (int i = 0; i < n; i++) {
            timestamps.add(serialisation.getTimestamp(properties));
        }
        // Then
        Assert.assertEquals(n, timestamps.size());
    }

    @Test
    public void shouldBuildRandomTimeBasedTimestamp() throws Exception {
        // Given
        final Properties properties = new Properties();
        properties.put(HBasePropertyNames.COLUMN_QUALIFIER, 1);
        properties.put(PROP_1, 2);
        // When
        final int n = 100;
        final Set<Long> timestamps = new HashSet<>(n);
        for (int i = 0; i < n; i++) {
            timestamps.add(serialisation.getTimestamp(properties));
        }
        // Then
        Assert.assertEquals(n, timestamps.size());
    }

    @Test
    public void shouldGetPropertiesFromTimestamp() throws Exception {
        // Given
        // add extra timestamp property to schema
        final Schema schema = new Schema.Builder().json(StreamUtil.schemas(getClass())).build();
        serialisation = new ElementSerialisation(type("timestamp", Long.class).edge(EDGE, new SchemaEdgeDefinition.Builder().property(TIMESTAMP, "timestamp").build()).config(TIMESTAMP_PROPERTY, TIMESTAMP).build());
        final long timestamp = System.currentTimeMillis();
        final String group = TestGroups.EDGE;
        // When
        final Properties properties = serialisation.getPropertiesFromTimestamp(group, timestamp);
        // Then
        Assert.assertEquals(1, properties.size());
        Assert.assertEquals(timestamp, properties.get(TIMESTAMP));
    }

    @Test
    public void shouldGetEmptyPropertiesFromTimestampWhenNoTimestampPropertyInGroup() throws Exception {
        // Given
        // add timestamp property name but don't add the property to the edge group
        final Schema schema = new Schema.Builder().json(StreamUtil.schemas(getClass())).build();
        serialisation = new ElementSerialisation(new Schema.Builder(schema).config(TIMESTAMP_PROPERTY, TIMESTAMP).build());
        final long timestamp = System.currentTimeMillis();
        final String group = TestGroups.EDGE;
        // When
        final Properties properties = serialisation.getPropertiesFromTimestamp(group, timestamp);
        // Then
        Assert.assertEquals(0, properties.size());
    }

    @Test
    public void shouldGetEmptyPropertiesFromTimestampWhenNoTimestampProperty() throws Exception {
        // Given
        final long timestamp = System.currentTimeMillis();
        final String group = TestGroups.EDGE;
        // When
        final Properties properties = serialisation.getPropertiesFromTimestamp(group, timestamp);
        // Then
        Assert.assertEquals(0, properties.size());
    }

    @Test
    public void shouldThrowExceptionWhenGetPropertiesFromTimestampWhenGroupIsNotFound() {
        // Given
        final long timestamp = System.currentTimeMillis();
        final String group = "unknownGroup";
        // When / Then
        try {
            serialisation.getPropertiesFromTimestamp(group, timestamp);
            Assert.fail("Exception expected");
        } catch (final Exception e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldSerialiseAndDeserialisePropertiesWhenAllAreEmpty() throws Exception {
        // Given?
        final Schema schema = type("string", String.class).type("map", new TypeDefinition.Builder().clazz(FreqMap.class).aggregateFunction(new FreqMapAggregator()).serialiser(new FreqMapSerialiser()).build()).build();
        serialisation = new ElementSerialisation(schema);
        final Entity entity = new Entity.Builder().vertex("vertex1").property(PROP_1, new FreqMap()).property(PROP_2, new FreqMap()).build();
        // When 1?
        final byte[] value = serialisation.getValue(ENTITY, entity.getProperties());
        // Then 1
        Assert.assertArrayEquals(new byte[]{ ByteArrayEscapeUtils.DELIMITER, ByteArrayEscapeUtils.DELIMITER }, value);
        // When 2
        final Properties properties = serialisation.getPropertiesFromValue(ENTITY, value);
        // Then 2
        Assert.assertEquals(entity.getProperties(), properties);
    }

    @Test
    public void shouldSerialiseWithHistoricValues() throws Exception {
        // Given
        Properties properties = new Properties();
        properties.put(PROP_1, 60);
        properties.put(PROP_2, Integer.MAX_VALUE);
        properties.put(PROP_3, 299);
        properties.put(PROP_4, Integer.MIN_VALUE);
        properties.put(COUNT, 8);
        // When
        final byte[] value = serialisation.getValue(EDGE, properties);
        // Then
        Assert.assertArrayEquals(new byte[]{ 1, 60, 5, -116, 127, -1, -1, -1, 3, -114, 1, 43, 5, -124, 127, -1, -1, -1, 1, 8, 0 }, value);
    }

    @Test
    public void shouldSerialiseWithHistoricRowKey() throws Exception {
        // Given
        final Entity entityMax = new Entity(TestGroups.ENTITY);
        String vertexString = "test a b c Vertex";
        entityMax.setVertex(vertexString);
        byte[] expectedBytes = new byte[]{ 116, 101, 115, 116, 32, 97, 32, 98, 32, 99, 32, 86, 101, 114, 116, 101, 120, 0, 1 };
        // When
        final byte[] keyMax = serialisation.getRowKey(entityMax);
        Object deserialisedVertex = serialisation.getPartialElement(ENTITY, expectedBytes, false).getIdentifier(VERTEX);
        // Then
        Assert.assertArrayEquals(expectedBytes, keyMax);
        Assert.assertEquals(vertexString, deserialisedVertex);
    }

    @Test
    public void shouldSerialiseWithHistoricColumnQualifier() throws Exception {
        // Given
        @SuppressWarnings("unchecked")
        Pair<Element, byte[]>[] historicSerialisationPairs = new Pair[]{ new Pair(getExampleEntity(100), new byte[]{ 11, 66, 97, 115, 105, 99, 69, 110, 116, 105, 116, 121, 4, 100, 0, 0, 0, 4, 102, 0, 0, 0, 0, 0 }), new Pair(getExampleEntity(Integer.MAX_VALUE), new byte[]{ 11, 66, 97, 115, 105, 99, 69, 110, 116, 105, 116, 121, 4, -1, -1, -1, 127, 4, 1, 0, 0, -128, 0, 0 }), new Pair(getExampleEntity(Integer.MIN_VALUE), new byte[]{ 11, 66, 97, 115, 105, 99, 69, 110, 116, 105, 116, 121, 4, 0, 0, 0, -128, 4, 2, 0, 0, -128, 0, 0 }) };
        for (final Pair<Element, byte[]> pair : historicSerialisationPairs) {
            // When
            final byte[] columnQualifier = serialisation.getColumnQualifier(pair.getFirst());
            Properties propertiesFromColumnQualifier = serialisation.getPropertiesFromColumnQualifier(ENTITY, pair.getSecond());
            // Then
            Assert.assertArrayEquals(pair.getSecond(), columnQualifier);
            Assert.assertEquals(pair.getFirst().getProperties(), propertiesFromColumnQualifier);
        }
    }

    @Test
    public void shouldDeserialiseSourceDestinationValuesCorrectWayRound() throws SerialisationException {
        // Given?
        final Edge edge = new Edge.Builder().source("1").dest("2").directed(true).group(ENTITY).build();
        final byte[] rowKey = serialisation.getRowKeys(edge).getFirst();
        final byte[][] sourceDestValues = new byte[2][];
        // When
        final EdgeDirection direction = serialisation.getSourceAndDestination(rowKey, sourceDestValues);
        // Then
        Assert.assertEquals(DIRECTED, direction);
    }

    @Test
    public void shouldDeserialiseSourceDestinationValuesIncorrectWayRound() throws SerialisationException {
        // Given?
        final Edge edge = new Edge.Builder().source("1").dest("2").directed(true).group(ENTITY).build();
        final byte[] rowKey = serialisation.getRowKeys(edge).getSecond();
        final byte[][] sourceDestValues = new byte[2][];
        // When
        final EdgeDirection direction = serialisation.getSourceAndDestination(rowKey, sourceDestValues);
        // Then
        Assert.assertEquals(DIRECTED_REVERSED, direction);
    }

    @Test
    public void shouldDeserialiseSourceDestinationValuesUndirected() throws SerialisationException {
        final Edge edge = new Edge.Builder().source("1").dest("2").directed(false).group(ENTITY).build();
        final byte[] rowKey = serialisation.getRowKeys(edge).getFirst();
        final byte[][] sourceDestValues = new byte[2][];
        // When
        final EdgeDirection direction = serialisation.getSourceAndDestination(rowKey, sourceDestValues);
        // Then
        Assert.assertEquals(UNDIRECTED, direction);
    }
}

