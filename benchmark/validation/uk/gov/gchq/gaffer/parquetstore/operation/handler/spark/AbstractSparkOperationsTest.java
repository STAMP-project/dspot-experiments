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
 * limitations under the License
 */
package uk.gov.gchq.gaffer.parquetstore.operation.handler.spark;


import SeedMatching.SeedMatchingType.RELATED;
import java.io.IOException;
import java.util.List;
import org.apache.spark.rdd.RDD;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.util.ElementUtil;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.integration.StandaloneIT;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.ElementSeed;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.spark.operation.scalardd.ImportRDDOfElements;
import uk.gov.gchq.gaffer.user.User;


// @Test
// public void getDataFrameOfElementsTest() throws IOException, OperationException, StoreException {
// final Graph graph = genData(false);
// final Dataset<Row> data = graph.execute(new GetDataFrameOfElements.Builder()
// .build(), user);
// checkGetDataFrameOfElements(data, false);
// }
// 
// @Test
// public void getDataFrameOfElementsWithViewTest() throws IOException, OperationException, StoreException {
// final Graph graph = genData(false);
// final View view = new View.Builder()
// .entity(TestGroups.ENTITY,
// new ViewElementDefinition.Builder().preAggregationFilter(
// new ElementFilter.Builder().select("double").execute(new IsEqual(0.2)).build()
// ).build())
// .build();
// try {
// graph.execute(new GetDataFrameOfElements.Builder()
// .view(view).build(), user);
// fail();
// } catch (final OperationException e) {
// assertEquals("Views are not supported by this operation yet", e.getMessage());
// } catch (final Exception e) {
// fail();
// }
// }
// 
// @Test
// public void getDataFrameOfElementsWithVisibilitiesTest() throws OperationException, StoreException, IOException {
// final Graph graph = genData(true);
// final Dataset<Row> data = graph.execute(new GetDataFrameOfElements.Builder()
// .build(), user);
// checkGetDataFrameOfElements(data, true);
// }
// @Test
// public void shouldReturnEmptyDataframeWithEmptyParquetStore() throws IOException, OperationException {
// final Schema gafferSchema = TestUtils.gafferSchema("schemaUsingStringVertexType");
// final ParquetStoreProperties parquetStoreProperties = TestUtils.getParquetStoreProperties(testFolder);
// parquetStoreProperties.setAddElementsOutputFilesPerGroup(1);
// final Graph graph = new Graph.Builder()
// .config(new GraphConfig.Builder()
// .graphId("emptyStore2")
// .build())
// .addSchemas(gafferSchema)
// .storeProperties(parquetStoreProperties)
// .build();
// 
// final Dataset<Row> data = graph.execute(new GetDataFrameOfElements.Builder().build(), user);
// 
// assertEquals(0, data.count());
// }
public abstract class AbstractSparkOperationsTest extends StandaloneIT {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    protected User user = getUser();

    @Test
    public void getAllElementsAfterImportElementsFromRDDTest() throws OperationException {
        // Given
        final Graph graph = createGraph();
        final RDD<Element> elements = getInputDataForGetAllElementsTest();
        graph.execute(new ImportRDDOfElements.Builder().input(elements).build(), user);
        // When
        final CloseableIterable<? extends Element> results = graph.execute(new GetAllElements.Builder().build(), user);
        // Then
        ElementUtil.assertElementEquals(getResultsForGetAllElementsTest(), results);
    }

    @Test
    public void getElementsWithSeedsRelatedAfterImportElementsFromRDDTest() throws OperationException {
        // Given
        final Graph graph = createGraph();
        final RDD<Element> elements = getInputDataForGetAllElementsTest();
        graph.execute(new ImportRDDOfElements.Builder().input(elements).build(), user);
        // When
        final List<ElementSeed> seeds = getSeeds();
        final CloseableIterable<? extends Element> results = graph.execute(new GetElements.Builder().input(seeds).seedMatching(RELATED).build(), user);
        // Then
        ElementUtil.assertElementEquals(getResultsForGetElementsWithSeedsRelatedTest(), results);
    }

    @Test
    public void getElementsWithSeedsRelatedAfterImportElementsFromRDDTestWhenMoreFilesThanElements() throws IOException, OperationException {
        // Given
        final int numFiles = 2 * (getNumberOfItemsInInputDataForGetAllElementsTest());
        final Graph graph = createGraph(numFiles);
        final RDD<Element> elements = getInputDataForGetAllElementsTest();
        graph.execute(new ImportRDDOfElements.Builder().input(elements).build(), user);
        // When
        final List<ElementSeed> seeds = getSeeds();
        final CloseableIterable<? extends Element> results = graph.execute(new GetElements.Builder().input(seeds).seedMatching(RELATED).build(), user);
        // Then
        ElementUtil.assertElementEquals(getResultsForGetElementsWithSeedsRelatedTest(), results);
    }
}

