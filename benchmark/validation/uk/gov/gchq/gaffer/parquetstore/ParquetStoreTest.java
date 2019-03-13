/**
 * Copyright 2017-2018. Crown Copyright
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
package uk.gov.gchq.gaffer.parquetstore;


import ParquetMetadataConverter.NO_FILTER;
import StoreTrait.INGEST_AGGREGATION;
import StoreTrait.ORDERED;
import StoreTrait.PRE_AGGREGATION_FILTERING;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import TestPropertyNames.PROP_1;
import TestTypes.ID_STRING;
import TestTypes.PROP_INTEGER;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.BlockMetaData;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.parquetstore.serialisation.impl.IntegerParquetSerialiser;
import uk.gov.gchq.gaffer.parquetstore.serialisation.impl.StringParquetSerialiser;
import uk.gov.gchq.gaffer.parquetstore.testutils.TestUtils;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.gaffer.user.User;


public class ParquetStoreTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    private static final String VERTEX = "vertex";

    private final Schema schema = new Schema.Builder().type(ID_STRING, new TypeDefinition.Builder().clazz(String.class).build()).type(PROP_INTEGER, Integer.class).type(DIRECTED_EITHER, Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source(ID_STRING).destination(ID_STRING).directed(DIRECTED_EITHER).property(PROP_1, PROP_INTEGER).aggregate(false).build()).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, PROP_INTEGER).vertex(ID_STRING).aggregate(false).build()).build();

    private final Edge unknownEdge = new Edge.Builder().group(EDGE_2).source("X").dest("Y").directed(false).property(PROP_1, 2).build();

    private final Entity unknownEntity = new Entity.Builder().vertex(ParquetStoreTest.VERTEX).group(ENTITY_2).property(PROP_1, 2).build();

    private final Entity knownEntity = new Entity.Builder().vertex(ParquetStoreTest.VERTEX).group(ENTITY).property(PROP_1, 2).build();

    @Test
    public void testTraits() throws StoreException {
        final ParquetStore store = new ParquetStore();
        final Set<StoreTrait> expectedTraits = new HashSet<>();
        expectedTraits.add(INGEST_AGGREGATION);
        expectedTraits.add(PRE_AGGREGATION_FILTERING);
        expectedTraits.add(ORDERED);
        // expectedTraits.add(StoreTrait.STORE_VALIDATION);
        // expectedTraits.add(StoreTrait.VISIBILITY);
        Assert.assertEquals(expectedTraits, store.getTraits());
    }

    @Test
    public void testMissingDataDirectory() {
        // Given
        final ParquetStoreProperties properties = new ParquetStoreProperties();
        properties.setTempFilesDir("/tmp/tmpdata");
        // When / Then
        try {
            ParquetStore.createStore("G", TestUtils.gafferSchema("schemaUsingStringVertexType"), properties);
        } catch (final IllegalArgumentException e) {
            // Expected
            return;
        }
        Assert.fail("IllegalArgumentException should have been thrown");
    }

    @Test
    public void testMissingTmpDataDirectory() {
        // Given
        final ParquetStoreProperties properties = new ParquetStoreProperties();
        properties.setDataDir("/tmp/data");
        // When / Then
        try {
            ParquetStore.createStore("G", TestUtils.gafferSchema("schemaUsingStringVertexType"), properties);
        } catch (final IllegalArgumentException e) {
            // Expected
            return;
        }
        Assert.fail("IllegalArgumentException should have been thrown");
    }

    @Test
    public void shouldFailSettingSnapshotWhenSnapshotNotExists() throws IOException {
        // Given
        final ParquetStoreProperties properties = TestUtils.getParquetStoreProperties(testFolder);
        ParquetStore store = ((ParquetStore) (ParquetStore.createStore("G", TestUtils.gafferSchema("schemaUsingStringVertexType"), properties)));
        // When / Then
        try {
            store.setLatestSnapshot(12345L);
        } catch (StoreException e) {
            // Expected
            Assert.assertThat(e.getMessage(), Matchers.containsString("does not exist"));
            return;
        }
        Assert.fail("StoreException should have been thrown as folder already exists");
    }

    @Test
    public void shouldNotFailSettingSnapshotWhenSnapshotExists() throws IOException {
        // Given
        final ParquetStoreProperties properties = TestUtils.getParquetStoreProperties(testFolder);
        ParquetStore store = ((ParquetStore) (ParquetStore.createStore("G", TestUtils.gafferSchema("schemaUsingStringVertexType"), properties)));
        testFolder.newFolder("data", ParquetStore.getSnapshotPath(12345L));
        // When / Then
        try {
            store.setLatestSnapshot(12345L);
        } catch (StoreException e) {
            Assert.fail(("StoreException should not have been thrown. Message is:\n" + (e.getMessage())));
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenAddingASingleEdgeWithGroupNotInSchema() throws Exception {
        getGraph().execute(new AddElements.Builder().input(unknownEdge).build(), new User());
    }

    @Test
    public void shouldNotThrowExceptionWhenAddingASingleEntityWithGroupNotInSchema() throws Exception {
        getGraph().execute(new AddElements.Builder().input(unknownEntity).build(), new User());
    }

    @Test
    public void shouldNotThrowExceptionWhenAddingAMultipleElementsWithGroupsNotInSchema() throws Exception {
        getGraph().execute(new AddElements.Builder().input(unknownEntity, unknownEdge).build(), new User());
    }

    @Test
    public void shouldAddElementWhenAddingBothValidAndInvalidElementsWithoutException() throws Exception {
        final Graph graph = getGraph();
        graph.execute(new AddElements.Builder().input(knownEntity, unknownEntity).build(), new User());
        Iterable<? extends Element> results = graph.execute(new GetAllElements(), new User());
        Iterator<? extends Element> iter = results.iterator();
        Assert.assertEquals(1, Iterables.size(results));
        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(knownEntity, iter.next());
        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void shouldCorrectlyUseCompressionOption() throws Exception {
        for (final String compressionType : Sets.newHashSet("GZIP", "SNAPPY", "UNCOMPRESSED")) {
            // Given
            final Schema schema = new Schema.Builder().type("int", new TypeDefinition.Builder().clazz(Integer.class).serialiser(new IntegerParquetSerialiser()).build()).type("string", new TypeDefinition.Builder().clazz(String.class).serialiser(new StringParquetSerialiser()).build()).type(DIRECTED_EITHER, Boolean.class).entity("entity", new SchemaEntityDefinition.Builder().vertex("string").property("property1", "int").aggregate(false).build()).edge("edge", new SchemaEdgeDefinition.Builder().source("string").destination("string").property("property2", "int").directed(DIRECTED_EITHER).aggregate(false).build()).vertexSerialiser(new StringParquetSerialiser()).build();
            final ParquetStoreProperties parquetStoreProperties = TestUtils.getParquetStoreProperties(testFolder);
            parquetStoreProperties.setCompressionCodecName(compressionType);
            final ParquetStore parquetStore = ((ParquetStore) (ParquetStore.createStore("graphId", schema, parquetStoreProperties)));
            final List<Element> elements = new ArrayList<>();
            elements.add(new Entity.Builder().group("entity").vertex("A").property("property1", 1).build());
            elements.add(new Edge.Builder().group("edge").source("B").dest("C").property("property2", 100).build());
            // When
            final AddElements add = new AddElements.Builder().input(elements).build();
            parquetStore.execute(add, new Context());
            // Then
            final List<Path> files = parquetStore.getFilesForGroup("entity");
            for (final Path path : files) {
                final ParquetMetadata parquetMetadata = ParquetFileReader.readFooter(new Configuration(), path, NO_FILTER);
                for (final BlockMetaData blockMetadata : parquetMetadata.getBlocks()) {
                    blockMetadata.getColumns().forEach(( c) -> assertEquals(compressionType, c.getCodec().name()));
                }
            }
        }
    }
}

