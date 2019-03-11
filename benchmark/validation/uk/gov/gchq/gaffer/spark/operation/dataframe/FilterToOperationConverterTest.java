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
package uk.gov.gchq.gaffer.spark.operation.dataframe;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.sources.Filter;
import org.apache.spark.sql.sources.GreaterThan;
import org.apache.spark.sql.sources.LessThan;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.spark.SparkSessionProvider;
import uk.gov.gchq.gaffer.spark.operation.dataframe.converter.schema.SchemaToStructTypeConverter;
import uk.gov.gchq.gaffer.spark.operation.scalardd.GetRDDOfAllElements;
import uk.gov.gchq.gaffer.spark.operation.scalardd.GetRDDOfElements;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.koryphe.impl.predicate.IsLessThan;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;
import uk.gov.gchq.koryphe.tuple.predicate.TupleAdaptedPredicate;


public class FilterToOperationConverterTest {
    private static final String ENTITY_GROUP = "BasicEntity";

    private static final String EDGE_GROUP = "BasicEdge";

    private static final String EDGE_GROUP2 = "BasicEdge2";

    private static final Set<String> EDGE_GROUPS = new HashSet<>(Arrays.asList(FilterToOperationConverterTest.EDGE_GROUP, FilterToOperationConverterTest.EDGE_GROUP2));

    @Test
    public void testIncompatibleGroups() {
        final Schema schema = getSchema();
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        final Filter[] filters = new Filter[2];
        filters[0] = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.GROUP, "A");
        filters[1] = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.GROUP, "B");
        final FiltersToOperationConverter converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        final Operation operation = converter.getOperation();
        Assert.assertNull(operation);
        sparkSession.sparkContext().stop();
    }

    @Test
    public void testSingleGroup() {
        final Schema schema = getSchema();
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        final Filter[] filters = new Filter[1];
        filters[0] = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.GROUP, FilterToOperationConverterTest.ENTITY_GROUP);
        final FiltersToOperationConverter converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        final Operation operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfAllElements));
        Assert.assertEquals(Collections.singleton(FilterToOperationConverterTest.ENTITY_GROUP), getView().getEntityGroups());
        Assert.assertEquals(0, getView().getEdgeGroups().size());
        sparkSession.sparkContext().stop();
    }

    @Test
    public void testSingleGroupNotInSchema() {
        final Schema schema = getSchema();
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        final Filter[] filters = new Filter[1];
        filters[0] = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.GROUP, "random");
        final FiltersToOperationConverter converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        final Operation operation = converter.getOperation();
        Assert.assertNull(operation);
        sparkSession.sparkContext().stop();
    }

    @Test
    public void testTwoGroups() {
        final Schema schema = getSchema();
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        final Filter[] filters = new Filter[1];
        final Filter left = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.GROUP, FilterToOperationConverterTest.ENTITY_GROUP);
        final Filter right = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.GROUP, FilterToOperationConverterTest.EDGE_GROUP2);
        filters[0] = new org.apache.spark.sql.sources.Or(left, right);
        final FiltersToOperationConverter converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        final Operation operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfAllElements));
        Assert.assertEquals(Collections.singleton(FilterToOperationConverterTest.ENTITY_GROUP), getView().getEntityGroups());
        Assert.assertEquals(Collections.singleton(FilterToOperationConverterTest.EDGE_GROUP2), getView().getEdgeGroups());
        sparkSession.sparkContext().stop();
    }

    @Test
    public void testSpecifyVertex() {
        final Schema schema = getSchema();
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        final Filter[] filters = new Filter[1];
        filters[0] = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.VERTEX_COL_NAME, "0");
        final FiltersToOperationConverter converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        final Operation operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfElements));
        Assert.assertEquals(Collections.singleton(FilterToOperationConverterTest.ENTITY_GROUP), getView().getEntityGroups());
        Assert.assertEquals(0, getView().getEdgeGroups().size());
        final Set<EntityId> seeds = new HashSet<>();
        for (final Object seed : getInput()) {
            seeds.add(((EntitySeed) (seed)));
        }
        Assert.assertEquals(Collections.singleton(new EntitySeed("0")), seeds);
        sparkSession.sparkContext().stop();
    }

    @Test
    public void testSpecifySource() {
        final Schema schema = getSchema();
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        final Filter[] filters = new Filter[1];
        filters[0] = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.SRC_COL_NAME, "0");
        FiltersToOperationConverter converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        Operation operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfElements));
        Assert.assertEquals(0, getView().getEntityGroups().size());
        Assert.assertEquals(FilterToOperationConverterTest.EDGE_GROUPS, getView().getEdgeGroups());
        final Set<EntityId> seeds = new HashSet<>();
        for (final Object seed : getInput()) {
            seeds.add(((EntitySeed) (seed)));
        }
        Assert.assertEquals(Collections.singleton(new EntitySeed("0")), seeds);
        sparkSession.sparkContext().stop();
    }

    @Test
    public void testSpecifyDestination() {
        final Schema schema = getSchema();
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        final Filter[] filters = new Filter[1];
        filters[0] = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.DST_COL_NAME, "0");
        final FiltersToOperationConverter converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        final Operation operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfElements));
        Assert.assertEquals(0, getView().getEntityGroups().size());
        Assert.assertEquals(FilterToOperationConverterTest.EDGE_GROUPS, getView().getEdgeGroups());
        final Set<EntityId> seeds = new HashSet<>();
        for (final Object seed : getInput()) {
            seeds.add(((EntitySeed) (seed)));
        }
        Assert.assertEquals(Collections.singleton(new EntitySeed("0")), seeds);
        sparkSession.sparkContext().stop();
    }

    @Test
    public void testSpecifyPropertyFilters() {
        final Schema schema = getSchema();
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        final Filter[] filters = new Filter[1];
        // GreaterThan
        filters[0] = new GreaterThan("property1", 5);
        FiltersToOperationConverter converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        Operation operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfAllElements));
        View opView = ((uk.gov.gchq.gaffer.operation.graph.GraphFilters) (operation)).getView();
        List<TupleAdaptedPredicate<String, ?>> entityPostAggFilters = opView.getEntity(FilterToOperationConverterTest.ENTITY_GROUP).getPostAggregationFilterFunctions();
        Assert.assertEquals(1, entityPostAggFilters.size());
        Assert.assertArrayEquals(new String[]{ "property1" }, entityPostAggFilters.get(0).getSelection());
        Assert.assertEquals(new IsMoreThan(5, false), entityPostAggFilters.get(0).getPredicate());
        for (final String edgeGroup : FilterToOperationConverterTest.EDGE_GROUPS) {
            final List<TupleAdaptedPredicate<String, ?>> edgePostAggFilters = opView.getEdge(edgeGroup).getPostAggregationFilterFunctions();
            Assert.assertEquals(1, edgePostAggFilters.size());
            Assert.assertArrayEquals(new String[]{ "property1" }, edgePostAggFilters.get(0).getSelection());
            Assert.assertEquals(new IsMoreThan(5, false), edgePostAggFilters.get(0).getPredicate());
        }
        // LessThan
        filters[0] = new LessThan("property4", 8L);
        converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfAllElements));
        // Only groups ENTITY_GROUP and EDGE_GROUP should be in the view as only they have property4
        opView = getView();
        entityPostAggFilters = opView.getEntity(FilterToOperationConverterTest.ENTITY_GROUP).getPostAggregationFilterFunctions();
        Assert.assertEquals(1, entityPostAggFilters.size());
        Assert.assertArrayEquals(new String[]{ "property4" }, entityPostAggFilters.get(0).getSelection());
        Assert.assertEquals(new IsLessThan(8L, false), entityPostAggFilters.get(0).getPredicate());
        List<TupleAdaptedPredicate<String, ?>> edgePostAggFilters = opView.getEdge(FilterToOperationConverterTest.EDGE_GROUP).getPostAggregationFilterFunctions();
        Assert.assertEquals(1, edgePostAggFilters.size());
        Assert.assertArrayEquals(new String[]{ "property4" }, edgePostAggFilters.get(0).getSelection());
        Assert.assertEquals(new IsLessThan(8L, false), edgePostAggFilters.get(0).getPredicate());
        // And
        final Filter left = new GreaterThan("property1", 5);
        final Filter right = new GreaterThan("property4", 8L);
        filters[0] = new org.apache.spark.sql.sources.And(left, right);
        converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfAllElements));
        // Only groups ENTITY_GROUP and EDGE_GROUP should be in the view as only they have property1 and property4
        opView = getView();
        entityPostAggFilters = opView.getEntity(FilterToOperationConverterTest.ENTITY_GROUP).getPostAggregationFilterFunctions();
        Assert.assertEquals(2, entityPostAggFilters.size());
        final ArrayList<String> expectedProperties = new ArrayList<>();
        expectedProperties.add("property1");
        expectedProperties.add("property4");
        Assert.assertEquals(1, entityPostAggFilters.get(0).getSelection().length);
        Assert.assertEquals(expectedProperties.get(0), entityPostAggFilters.get(0).getSelection()[0]);
        Assert.assertEquals(1, entityPostAggFilters.get(1).getSelection().length);
        Assert.assertEquals(expectedProperties.get(1), entityPostAggFilters.get(1).getSelection()[0]);
        final ArrayList<Predicate> expectedFunctions = new ArrayList<>();
        expectedFunctions.add(new IsMoreThan(5, false));
        expectedFunctions.add(new IsMoreThan(8L, false));
        Assert.assertEquals(expectedFunctions.get(0), entityPostAggFilters.get(0).getPredicate());
        Assert.assertEquals(expectedFunctions.get(1), entityPostAggFilters.get(1).getPredicate());
        edgePostAggFilters = opView.getEdge(FilterToOperationConverterTest.EDGE_GROUP).getPostAggregationFilterFunctions();
        Assert.assertEquals(2, edgePostAggFilters.size());
        Assert.assertEquals(1, edgePostAggFilters.get(0).getSelection().length);
        Assert.assertEquals(expectedProperties.get(0), edgePostAggFilters.get(0).getSelection()[0]);
        Assert.assertEquals(1, edgePostAggFilters.get(1).getSelection().length);
        Assert.assertEquals(expectedProperties.get(1), edgePostAggFilters.get(1).getSelection()[0]);
        sparkSession.sparkContext().stop();
    }

    @Test
    public void testSpecifyMultiplePropertyFilters() {
        final Schema schema = getSchema();
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        final Filter[] filters = new Filter[2];
        filters[0] = new GreaterThan("property1", 5);
        filters[1] = new LessThan("property4", 8L);
        FiltersToOperationConverter converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        Operation operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfAllElements));
        // Only groups ENTITY_GROUP and EDGE_GROUP should be in the view as only they have property1 and property4
        View opView = ((uk.gov.gchq.gaffer.operation.graph.GraphFilters) (operation)).getView();
        List<TupleAdaptedPredicate<String, ?>> entityPostAggFilters = opView.getEntity(FilterToOperationConverterTest.ENTITY_GROUP).getPostAggregationFilterFunctions();
        Assert.assertEquals(2, entityPostAggFilters.size());
        final ArrayList<String> expectedProperties = new ArrayList<>();
        expectedProperties.add("property1");
        expectedProperties.add("property4");
        Assert.assertEquals(1, entityPostAggFilters.get(0).getSelection().length);
        Assert.assertEquals(expectedProperties.get(0), entityPostAggFilters.get(0).getSelection()[0]);
        Assert.assertEquals(1, entityPostAggFilters.get(1).getSelection().length);
        Assert.assertEquals(expectedProperties.get(1), entityPostAggFilters.get(1).getSelection()[0]);
        final ArrayList<Predicate> expectedFunctions = new ArrayList<>();
        expectedFunctions.add(new IsMoreThan(5, false));
        expectedFunctions.add(new IsLessThan(8L, false));
        Assert.assertEquals(expectedFunctions.get(0), entityPostAggFilters.get(0).getPredicate());
        Assert.assertEquals(expectedFunctions.get(1), entityPostAggFilters.get(1).getPredicate());
        final List<TupleAdaptedPredicate<String, ?>> edgePostAggFilters = opView.getEdge(FilterToOperationConverterTest.EDGE_GROUP).getPostAggregationFilterFunctions();
        Assert.assertEquals(2, edgePostAggFilters.size());
        Assert.assertEquals(1, edgePostAggFilters.get(0).getSelection().length);
        Assert.assertEquals(expectedProperties.get(0), edgePostAggFilters.get(0).getSelection()[0]);
        Assert.assertEquals(1, edgePostAggFilters.get(1).getSelection().length);
        Assert.assertEquals(expectedProperties.get(1), edgePostAggFilters.get(1).getSelection()[0]);
        sparkSession.sparkContext().stop();
    }

    @Test
    public void testSpecifyVertexAndPropertyFilter() {
        final Schema schema = getSchema();
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        // Specify vertex and a filter on property1
        Filter[] filters = new Filter[2];
        filters[0] = new GreaterThan("property1", 5);
        filters[1] = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.VERTEX_COL_NAME, "0");
        FiltersToOperationConverter converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        Operation operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfElements));
        Assert.assertEquals(1, getView().getEntityGroups().size());
        Assert.assertEquals(0, getView().getEdgeGroups().size());
        final Set<EntityId> seeds = new HashSet<>();
        for (final Object seed : getInput()) {
            seeds.add(((EntitySeed) (seed)));
        }
        Assert.assertEquals(Collections.singleton(new EntitySeed("0")), seeds);
        View opView = ((uk.gov.gchq.gaffer.operation.graph.GraphFilters) (operation)).getView();
        List<TupleAdaptedPredicate<String, ?>> entityPostAggFilters = opView.getEntity(FilterToOperationConverterTest.ENTITY_GROUP).getPostAggregationFilterFunctions();
        Assert.assertEquals(1, entityPostAggFilters.size());
        final ArrayList<String> expectedProperties = new ArrayList<>();
        expectedProperties.add("property1");
        Assert.assertEquals(1, entityPostAggFilters.get(0).getSelection().length);
        Assert.assertEquals(expectedProperties.get(0), entityPostAggFilters.get(0).getSelection()[0]);
        final ArrayList<Predicate> expectedFunctions = new ArrayList<>();
        expectedFunctions.add(new IsMoreThan(5, false));
        Assert.assertEquals(expectedFunctions.get(0), entityPostAggFilters.get(0).getPredicate());
        // Specify vertex and filters on properties property1 and property4
        filters = new Filter[3];
        filters[0] = new GreaterThan("property1", 5);
        filters[1] = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.VERTEX_COL_NAME, "0");
        filters[2] = new LessThan("property4", 8);
        converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfElements));
        Assert.assertEquals(1, getView().getEntityGroups().size());
        Assert.assertEquals(0, getView().getEdgeGroups().size());
        seeds.clear();
        for (final Object seed : getInput()) {
            seeds.add(((EntitySeed) (seed)));
        }
        Assert.assertEquals(Collections.singleton(new EntitySeed("0")), seeds);
        opView = getView();
        entityPostAggFilters = opView.getEntity(FilterToOperationConverterTest.ENTITY_GROUP).getPostAggregationFilterFunctions();
        Assert.assertEquals(2, entityPostAggFilters.size());
        expectedProperties.clear();
        expectedProperties.add("property1");
        expectedProperties.add("property4");
        Assert.assertEquals(1, entityPostAggFilters.get(0).getSelection().length);
        Assert.assertEquals(expectedProperties.get(0), entityPostAggFilters.get(0).getSelection()[0]);
        Assert.assertEquals(1, entityPostAggFilters.get(1).getSelection().length);
        Assert.assertEquals(expectedProperties.get(1), entityPostAggFilters.get(1).getSelection()[0]);
        expectedFunctions.clear();
        expectedFunctions.add(new IsMoreThan(5, false));
        expectedFunctions.add(new IsLessThan(8, false));
        Assert.assertEquals(expectedFunctions.get(0), entityPostAggFilters.get(0).getPredicate());
        Assert.assertEquals(expectedFunctions.get(1), entityPostAggFilters.get(1).getPredicate());
        sparkSession.sparkContext().stop();
    }

    @Test
    public void testSpecifySourceOrDestinationAndPropertyFilter() {
        final Schema schema = getSchema();
        final SparkSession sparkSession = SparkSessionProvider.getSparkSession();
        // Specify src and a filter on property1
        Filter[] filters = new Filter[2];
        filters[0] = new GreaterThan("property1", 5);
        filters[1] = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.SRC_COL_NAME, "0");
        FiltersToOperationConverter converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        Operation operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfElements));
        Assert.assertEquals(0, getView().getEntityGroups().size());
        Assert.assertEquals(2, getView().getEdgeGroups().size());
        final Set<EntityId> seeds = new HashSet<>();
        for (final Object seed : getInput()) {
            seeds.add(((EntitySeed) (seed)));
        }
        Assert.assertEquals(Collections.singleton(new EntitySeed("0")), seeds);
        View opView = ((uk.gov.gchq.gaffer.operation.graph.GraphFilters) (operation)).getView();
        for (final String edgeGroup : FilterToOperationConverterTest.EDGE_GROUPS) {
            final List<TupleAdaptedPredicate<String, ?>> edgePostAggFilters = opView.getEdge(edgeGroup).getPostAggregationFilterFunctions();
            Assert.assertEquals(1, edgePostAggFilters.size());
            Assert.assertArrayEquals(new String[]{ "property1" }, edgePostAggFilters.get(0).getSelection());
            Assert.assertEquals(new IsMoreThan(5, false), edgePostAggFilters.get(0).getPredicate());
        }
        // Specify src and filters on property1 and property4
        filters = new Filter[3];
        filters[0] = new GreaterThan("property1", 5);
        filters[1] = new org.apache.spark.sql.sources.EqualTo(SchemaToStructTypeConverter.SRC_COL_NAME, "0");
        filters[2] = new LessThan("property4", 8);
        converter = new FiltersToOperationConverter(getViewFromSchema(schema), schema, filters);
        operation = converter.getOperation();
        Assert.assertTrue((operation instanceof GetRDDOfElements));
        Assert.assertEquals(0, getView().getEntityGroups().size());
        Assert.assertEquals(1, getView().getEdgeGroups().size());
        seeds.clear();
        for (final Object seed : getInput()) {
            seeds.add(((EntitySeed) (seed)));
        }
        Assert.assertEquals(Collections.singleton(new EntitySeed("0")), seeds);
        opView = getView();
        final List<TupleAdaptedPredicate<String, ?>> entityPostAggFilters = opView.getEdge(FilterToOperationConverterTest.EDGE_GROUP).getPostAggregationFilterFunctions();
        Assert.assertEquals(2, entityPostAggFilters.size());
        final List<String> expectedProperties = new ArrayList<>();
        expectedProperties.add("property1");
        expectedProperties.add("property4");
        Assert.assertEquals(1, entityPostAggFilters.get(0).getSelection().length);
        Assert.assertEquals(expectedProperties.get(0), entityPostAggFilters.get(0).getSelection()[0]);
        Assert.assertEquals(new IsMoreThan(5, false), entityPostAggFilters.get(0).getPredicate());
        Assert.assertEquals(1, entityPostAggFilters.get(1).getSelection().length);
        Assert.assertEquals(expectedProperties.get(1), entityPostAggFilters.get(1).getSelection()[0]);
        Assert.assertEquals(new IsLessThan(8, false), entityPostAggFilters.get(1).getPredicate());
        sparkSession.sparkContext().stop();
    }
}

