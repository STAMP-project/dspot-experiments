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
package uk.gov.gchq.gaffer.sparkaccumulo.operation.handler.dataframe;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.sources.EqualTo;
import org.apache.spark.sql.sources.Filter;
import org.apache.spark.sql.sources.GreaterThan;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.SingleUseMockAccumuloStore;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.spark.SparkContextUtil;
import uk.gov.gchq.gaffer.spark.SparkSessionProvider;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;
import uk.gov.gchq.koryphe.tuple.predicate.TupleAdaptedPredicate;


/**
 * Contains unit tests for {@link AccumuloStoreRelation}.
 */
public class AccumuloStoreRelationTest {
    @Test
    public void testBuildScanFullView() throws OperationException, StoreException {
        final Schema schema = AccumuloStoreRelationTest.getSchema();
        final View view = AccumuloStoreRelationTest.getViewFromSchema(schema);
        testBuildScanWithView("testBuildScanFullView", view, ( e) -> true);
    }

    @Test
    public void testBuildScanRestrictViewToOneGroup() throws OperationException, StoreException {
        final View view = new View.Builder().edge(GetDataFrameOfElementsHandlerTest.EDGE_GROUP).build();
        final Predicate<Element> returnElement = (Element element) -> element.getGroup().equals(GetDataFrameOfElementsHandlerTest.EDGE_GROUP);
        testBuildScanWithView("testBuildScanRestrictViewToOneGroup", view, returnElement);
    }

    @Test
    public void testBuildScanRestrictViewByProperty() throws OperationException, StoreException {
        final List<TupleAdaptedPredicate<String, ?>> filters = new ArrayList<>();
        filters.add(new TupleAdaptedPredicate(new IsMoreThan(5, false), new String[]{ "property1" }));
        final View view = new View.Builder().edge(GetDataFrameOfElementsHandlerTest.EDGE_GROUP, new ViewElementDefinition.Builder().postAggregationFilterFunctions(filters).build()).build();
        final Predicate<Element> returnElement = (Element element) -> (element.getGroup().equals(GetDataFrameOfElementsHandlerTest.EDGE_GROUP)) && (((Integer) (element.getProperty("property1"))) > 5);
        testBuildScanWithView("testBuildScanRestrictViewByProperty", view, returnElement);
    }

    @Test
    public void testBuildScanSpecifyColumnsFullView() throws OperationException, StoreException {
        final Schema schema = AccumuloStoreRelationTest.getSchema();
        final View view = AccumuloStoreRelationTest.getViewFromSchema(schema);
        final String[] requiredColumns = new String[]{ "property1" };
        testBuildScanSpecifyColumnsWithView(view, requiredColumns, ( e) -> true);
    }

    @Test
    public void testBuildScanSpecifyColumnsAndFiltersFullView() throws OperationException, StoreException {
        final Schema schema = AccumuloStoreRelationTest.getSchema();
        final View view = AccumuloStoreRelationTest.getViewFromSchema(schema);
        final String[] requiredColumns = new String[1];
        requiredColumns[0] = "property1";
        final Filter[] filters = new Filter[1];
        filters[0] = new GreaterThan("property1", 4);
        final Predicate<Element> returnElement = (Element element) -> ((Integer) (element.getProperty("property1"))) > 4;
        testBuildScanSpecifyColumnsAndFiltersWithView(view, requiredColumns, filters, returnElement);
    }

    @Test
    public void shouldReturnEmptyDataFrameWithNoResultsFromFilter() throws OperationException, StoreException {
        // Given
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        final Schema schema = AccumuloStoreRelationTest.getSchema();
        final View view = AccumuloStoreRelationTest.getViewFromSchema(schema);
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(getClass().getResourceAsStream("/store.properties"));
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        store.initialise("graphId", schema, properties);
        AccumuloStoreRelationTest.addElements(store);
        final String[] requiredColumns = new String[1];
        requiredColumns[0] = "property1";
        final Filter[] filters = new Filter[1];
        filters[0] = new EqualTo("group", "abc");
        // When
        final AccumuloStoreRelation relation = new AccumuloStoreRelation(SparkContextUtil.createContext(new User(), sparkSession), Collections.emptyList(), view, store, null);
        final RDD<Row> rdd = relation.buildScan(requiredColumns, filters);
        // Then
        Assert.assertTrue(rdd.isEmpty());
    }
}

