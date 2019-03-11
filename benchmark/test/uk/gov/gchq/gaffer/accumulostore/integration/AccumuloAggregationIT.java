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
package uk.gov.gchq.gaffer.accumulostore.integration;


import TestGroups.ENTITY;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import TestPropertyNames.PROP_3;
import TestPropertyNames.PROP_4;
import com.google.common.collect.Lists;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.utils.AccumuloPropertyNames;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.integration.StandaloneIT;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.user.User;


public class AccumuloAggregationIT extends StandaloneIT {
    private final String VERTEX = "vertex";

    private final String PUBLIC_VISIBILITY = "publicVisibility";

    private final String PRIVATE_VISIBILITY = "privateVisibility";

    private final User user = getUser();

    @Test
    public void shouldOnlyAggregateVisibilityWhenGroupByIsNull() throws Exception {
        final Graph graph = createGraph();
        final Entity entity1 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "value 3a").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "value 4").property(AccumuloPropertyNames.VISIBILITY, PUBLIC_VISIBILITY).build();
        final Entity entity2 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "value 3a").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "value 4").property(AccumuloPropertyNames.VISIBILITY, PRIVATE_VISIBILITY).build();
        final Entity entity3 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "value 3b").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "value 4").property(AccumuloPropertyNames.VISIBILITY, PRIVATE_VISIBILITY).build();
        graph.execute(new AddElements.Builder().input(entity1, entity2, entity3).build(), user);
        // Given
        final GetElements getElements = new GetElements.Builder().input(new EntitySeed(VERTEX)).view(new View.Builder().entity(ENTITY).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(graph.execute(getElements, user));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        final Entity expectedSummarisedEntity = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "value 3a").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "value 4").property(AccumuloPropertyNames.VISIBILITY, (((PRIVATE_VISIBILITY) + ",") + (PUBLIC_VISIBILITY))).build();
        final Entity expectedEntity = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "value 3b").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "value 4").property(AccumuloPropertyNames.VISIBILITY, PRIVATE_VISIBILITY).build();
        Assert.assertThat(results, IsCollectionContaining.hasItems(expectedSummarisedEntity, expectedEntity));
    }

    @Test
    public void shouldAggregateOverAllPropertiesExceptForGroupByProperties() throws Exception {
        final Graph graph = createGraph();
        final Entity entity1 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "some value").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "some value 2").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "some value 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "some value 4").property(AccumuloPropertyNames.VISIBILITY, PUBLIC_VISIBILITY).build();
        final Entity entity2 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "some value").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "some value 2b").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "some value 3b").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "some value 4b").property(AccumuloPropertyNames.VISIBILITY, PRIVATE_VISIBILITY).build();
        final Entity entity3 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "some value c").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "some value 2c").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "some value 3c").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "some value 4c").property(AccumuloPropertyNames.VISIBILITY, PRIVATE_VISIBILITY).build();
        graph.execute(new AddElements.Builder().input(entity1, entity2, entity3).build(), user);
        // Given
        final GetElements getElements = new GetElements.Builder().input(new EntitySeed(VERTEX)).view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy(AccumuloPropertyNames.COLUMN_QUALIFIER).build()).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(graph.execute(getElements, user));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        final Entity expectedEntity = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "some value").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "some value 2,some value 2b").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "some value 3,some value 3b").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "some value 4,some value 4b").property(AccumuloPropertyNames.VISIBILITY, (((PUBLIC_VISIBILITY) + ",") + (PRIVATE_VISIBILITY))).build();
        Assert.assertThat(results, IsCollectionContaining.hasItems(expectedEntity, entity3));
    }

    @Test
    public void shouldHandleAggregationWhenGroupByPropertiesAreNull() throws UnsupportedEncodingException, OperationException {
        final Graph graph = createGraphNoVisibility();
        final Entity entity1 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, null).property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, null).property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, null).property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, null).build();
        final Entity entity2 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "test 4").build();
        graph.execute(new AddElements.Builder().input(entity1, entity2).build(), user);
        // Given
        final GetElements getElements = new GetElements.Builder().input(new EntitySeed(VERTEX)).view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy().build()).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(graph.execute(getElements, user));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        final Entity expectedEntity = // String Aggregation is combining one empty strings -> "","test 4"
        // String Aggregation is combining one empty strings -> "","test 3"
        // String Aggregation is combining two empty strings -> "",""
        // String Aggregation is combining two empty strings -> "",""
        new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, ",").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, ",").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, ",test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, ",test 4").build();
        Assert.assertEquals(expectedEntity, results.get(0));
    }

    @Test
    public void shouldHandleAggregationWhenAllColumnQualifierPropertiesAreGroupByProperties() throws UnsupportedEncodingException, OperationException {
        final Graph graph = createGraphNoVisibility();
        final Entity entity1 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "test 4").build();
        final Entity entity2 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "test 4").build();
        graph.execute(new AddElements.Builder().input(entity1, entity2).build(), user);
        // Given
        final GetElements getElements = new GetElements.Builder().input(new EntitySeed(VERTEX)).view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy(AccumuloPropertyNames.COLUMN_QUALIFIER, AccumuloPropertyNames.COLUMN_QUALIFIER_2).build()).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(graph.execute(getElements, user));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        final Entity expectedEntity = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "test 4").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "").build();
        Assert.assertEquals(expectedEntity, results.get(0));
    }

    @Test
    public void shouldHandleAggregationWhenGroupByPropertiesAreNotSet() throws UnsupportedEncodingException, OperationException {
        final Graph graph = createGraphNoVisibility();
        final Entity entity1 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "test 4").build();
        final Entity entity2 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "test 4").build();
        graph.execute(new AddElements.Builder().input(entity1, entity2).build(), user);
        // Given
        final GetElements getElements = new GetElements.Builder().input(new EntitySeed(VERTEX)).view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy(AccumuloPropertyNames.COLUMN_QUALIFIER, AccumuloPropertyNames.COLUMN_QUALIFIER_2).build()).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(graph.execute(getElements, user));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        final Entity expectedEntity = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "test 4").build();
        Assert.assertEquals(expectedEntity, results.get(0));
    }

    @Test
    public void shouldHandleAggregationWithMultipleCombinations() throws UnsupportedEncodingException, OperationException {
        final Graph graph = createGraphNoVisibility();
        final Entity entity1 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "test 4").build();
        final Entity entity2 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, null).property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "test 4").build();
        final Entity entity3 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "test1a").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "test 4").build();
        final Entity entity4 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "test1b").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "test 4").build();
        final Entity entity5 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "test1a").build();
        final Entity entity6 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "test1b").build();
        final Entity entity7 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "test2a").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "test 3").build();
        graph.execute(new AddElements.Builder().input(entity1, entity2, entity3, entity4, entity5, entity6, entity7).build(), user);
        // Duplicate the entities to check they are aggregated properly
        graph.execute(new AddElements.Builder().input(entity1, entity2, entity3, entity4, entity5, entity6, entity7).build(), user);
        // Given
        final GetElements getElements = new GetElements.Builder().input(new EntitySeed(VERTEX)).view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy(AccumuloPropertyNames.COLUMN_QUALIFIER, AccumuloPropertyNames.COLUMN_QUALIFIER_2).build()).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(graph.execute(getElements, user));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(4, results.size());
        final Entity expectedEntity1 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "test 4").build();
        final Entity expectedEntity2 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "test1a").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, ",test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, ",test 4").build();
        final Entity expectedEntity3 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "test1b").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, ",test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, ",test 4").build();
        final Entity expectedEntity4 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(AccumuloPropertyNames.COLUMN_QUALIFIER, "").property(AccumuloPropertyNames.COLUMN_QUALIFIER_2, "test2a").property(AccumuloPropertyNames.COLUMN_QUALIFIER_3, "test 3").property(AccumuloPropertyNames.COLUMN_QUALIFIER_4, "").build();
        Assert.assertThat(results, IsCollectionContaining.hasItems(expectedEntity1, expectedEntity2, expectedEntity3, expectedEntity4));
    }

    @Test
    public void shouldHandleAggregationWhenNoAggregatorsAreProvided() throws OperationException {
        final Graph graph = createGraphNoAggregators();
        final Entity entity1 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(PROP_3, "test 3").property(PROP_4, "test 4").build();
        final Entity entity2 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(PROP_1, null).property(PROP_3, "test 3").property(PROP_4, "test 4").build();
        final Entity entity3 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(PROP_1, "test1a").property(PROP_3, "test 3").property(PROP_4, "test 4").build();
        final Entity entity4 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(PROP_1, "test1b").property(PROP_3, "test 3").property(PROP_4, "test 4").build();
        final Entity entity5 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(PROP_1, "test1a").build();
        final Entity entity6 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(PROP_1, "test1b").build();
        final Entity entity7 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(PROP_2, "test2a").property(PROP_3, "test 3").build();
        graph.execute(new AddElements.Builder().input(entity1, entity2, entity3, entity4, entity5, entity6, entity7).build(), user);
        // Duplicate the entities to check they are not aggregated
        graph.execute(new AddElements.Builder().input(entity1, entity2, entity3, entity4, entity5, entity6, entity7).build(), user);
        // Given
        final GetAllElements getAllEntities = new GetAllElements.Builder().view(new View.Builder().entity(ENTITY).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(graph.execute(getAllEntities, user));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(14, results.size());
        final Entity expectedEntity1 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(PROP_1, "").property(PROP_2, "").property(PROP_3, "test 3").property(PROP_4, "test 4").build();
        final Entity expectedEntity2 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(PROP_1, "test1a").property(PROP_2, "").property(PROP_3, "test 3").property(PROP_4, "test 4").build();
        final Entity expectedEntity3 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(PROP_1, "test1b").property(PROP_2, "").property(PROP_3, "test 3").property(PROP_4, "test 4").build();
        final Entity expectedEntity4 = new Entity.Builder().vertex(VERTEX).group(ENTITY).property(PROP_1, "").property(PROP_2, "test2a").property(PROP_3, "test 3").property(PROP_4, "").build();
        Assert.assertThat(results, IsCollectionContaining.hasItems(expectedEntity1, expectedEntity2, expectedEntity3, expectedEntity4));
    }
}

