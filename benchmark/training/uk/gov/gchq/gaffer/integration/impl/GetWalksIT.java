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
package uk.gov.gchq.gaffer.integration.impl;


import DirectedType.DIRECTED;
import SeededGraphFilters.IncludeIncomingOutgoingType.OUTGOING;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.EDGE_3;
import TestGroups.ENTITY;
import TestPropertyNames.COUNT;
import TestPropertyNames.INT;
import TestPropertyNames.PROP_1;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.data.graph.Walk;
import uk.gov.gchq.gaffer.graph.GraphConfig;
import uk.gov.gchq.gaffer.graph.hook.AddOperationsToChain;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.integration.TraitRequirement;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.GetWalks;
import uk.gov.gchq.gaffer.operation.impl.GetWalks.Builder;
import uk.gov.gchq.gaffer.operation.impl.Limit;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.koryphe.function.KorypheFunction;
import uk.gov.gchq.koryphe.impl.predicate.IsLessThan;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;


public class GetWalksIT extends AbstractStoreIT {
    final EntitySeed seedA = new EntitySeed("A");

    final EntitySeed seedE = new EntitySeed("E");

    @Test
    public void shouldGetPaths() throws Exception {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(operation, operation).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AED,ABC")));
    }

    @Test
    public void shouldGetPathsWithWhileRepeat() throws Exception {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(new uk.gov.gchq.gaffer.operation.impl.While.Builder<>().operation(operation).maxRepeats(2).build()).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AED,ABC")));
    }

    @Test
    public void shouldGetPathsWithWhile() throws Exception {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new Builder().input(seedA).operations(new uk.gov.gchq.gaffer.operation.impl.While.Builder<>().conditional(// This will always be true
        new uk.gov.gchq.gaffer.operation.util.Conditional(new uk.gov.gchq.koryphe.impl.predicate.Exists(), new uk.gov.gchq.gaffer.operation.impl.Map.Builder<>().first(new GetWalksIT.AssertEntityIdsUnwrapped()).build())).operation(operation).maxRepeats(2).build()).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AED,ABC")));
    }

    @Test
    public void shouldGetPathsWithPruning() throws Exception {
        // Given
        final StoreProperties properties = AbstractStoreIT.getStoreProperties();
        properties.setOperationDeclarationPaths("getWalksWithPruningDeclaration.json");
        createGraph(properties);
        addDefaultElements();
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(operation, operation).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AED,ABC")));
    }

    @Test
    public void shouldReturnNoResultsWhenNoEntityResults() throws Exception {
        // Given
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(new GetElements.Builder().view(new View.Builder().edge(EDGE).build()).build(), new OperationChain.Builder().first(new GetElements.Builder().view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(INT).execute(new IsMoreThan(10000)).build()).build()).build()).build()).then(new GetElements()).build()).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        Assert.assertEquals(0, Lists.newArrayList(results).size());
    }

    @Test
    public void shouldGetPathsWithEntities() throws Exception {
        // Given
        final GetElements getEntities = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().entity(ENTITY).build()).build();
        final GetElements getElements = new GetElements.Builder().directedType(DIRECTED).inOutType(OUTGOING).view(new View.Builder().entity(ENTITY).edge(EDGE).build()).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(getElements, getElements, getEntities).build();
        // When
        final List<Walk> results = Lists.newArrayList(AbstractStoreIT.graph.execute(op, getUser()));
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AED,ABC")));
        results.forEach(( r) -> r.getEntities().forEach(( l) -> {
            assertThat(l, is(not(empty())));
        }));
    }

    @Test
    public void shouldThrowExceptionIfGetPathsWithHopContainingNoEdges() throws Exception {
        // Given
        final GetElements getEntities = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().entity(ENTITY).build()).build();
        final GetElements getElements = new GetElements.Builder().directedType(DIRECTED).inOutType(OUTGOING).view(new View.Builder().entity(ENTITY).edge(EDGE).build()).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(getElements, getEntities, getElements).build();
        // When / Then
        try {
            Lists.newArrayList(AbstractStoreIT.graph.execute(op, getUser()));
        } catch (final Exception e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("must contain a single hop"));
        }
    }

    @Test
    public void shouldGetPathsWithMultipleSeeds() throws Exception {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new GetWalks.Builder().input(seedA, seedE).operations(operation, operation).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AED,ABC,EDA")));
    }

    @Test
    public void shouldGetPathsWithMultipleEdgeTypes() throws Exception {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).edge(EDGE_2, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(operation, operation).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AED,AEF,ABC")));
    }

    @Test
    public void shouldGetPathsWithMultipleSeedsAndMultipleEdgeTypes() throws Exception {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).edge(EDGE_2, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new GetWalks.Builder().input(seedA, seedE).operations(operation, operation).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AED,AEF,ABC,EDA,EFC")));
    }

    @Test
    public void shouldGetPathsWithLoops() throws Exception {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).edge(EDGE_2, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(operation, operation, operation).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AEDA,AEFC")));
    }

    @Test
    public void shouldGetPathsWithLoops_2() throws Exception {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).edge(EDGE_2, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(operation, operation, operation, operation).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AEDAE,AEDAB")));
    }

    @Test
    public void shouldGetPathsWithLoops_3() throws Exception {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE_3, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(operation, operation, operation, operation).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AAAAA")));
    }

    @Test
    @TraitRequirement(StoreTrait.POST_AGGREGATION_FILTERING)
    public void shouldGetPathsWithPreFiltering_1() throws Exception {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final OperationChain operationChain = // only walk down entities which have a property set to an integer
        // larger than 3.
        new OperationChain.Builder().first(new GetElements.Builder().view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().postAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new IsMoreThan(3)).build()).build()).build()).build()).then(operation).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(operation, operationChain).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AED")));
    }

    @Test
    @TraitRequirement(StoreTrait.POST_AGGREGATION_FILTERING)
    public void shouldGetPathsWithPreFiltering_2() throws Exception {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final OperationChain operationChain = // only walk down entities which have a property set to an integer
        // less than 3.
        new OperationChain.Builder().first(new GetElements.Builder().view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().postAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new IsLessThan(3)).build()).build()).build()).build()).then(operation).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(operationChain, operationChain).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("ABC")));
    }

    @Test
    public void shouldGetPathsWithModifiedViews() throws OperationException {
        // Given
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).inOutType(OUTGOING).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(COUNT).execute(new IsMoreThan(0L)).build()).build()).build()).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(operation, operation).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("AED,ABC")));
    }

    @Test
    public void shouldGetPathsWithSimpleGraphHook_1() throws Exception {
        // Given
        final AddOperationsToChain graphHook = new AddOperationsToChain();
        graphHook.setEnd(Lists.newArrayList(new Limit.Builder<>().resultLimit(1).build()));
        final GraphConfig config = new GraphConfig.Builder().addHook(graphHook).graphId("integrationTest").build();
        createGraph(config);
        addDefaultElements();
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(operation, operation).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        Assert.assertEquals(1, Lists.newArrayList(results).size());
    }

    @Test
    public void shouldGetPathsWithSimpleGraphHook_2() throws Exception {
        // Given
        final AddOperationsToChain graphHook = new AddOperationsToChain();
        final Map<String, List<Operation>> graphHookConfig = new HashMap<>();
        graphHookConfig.put(GetElements.class.getName(), Lists.newArrayList(new Limit.Builder<>().resultLimit(1).build()));
        graphHook.setAfter(graphHookConfig);
        final GraphConfig config = new GraphConfig.Builder().addHook(graphHook).graphId("integrationTest").build();
        createGraph(config);
        addDefaultElements();
        final GetElements operation = new GetElements.Builder().directedType(DIRECTED).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).inOutType(OUTGOING).build();
        final GetWalks op = new GetWalks.Builder().input(seedA).operations(operation, operation).build();
        // When
        final Iterable<Walk> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        MatcherAssert.assertThat(getPaths(results), Is.is(CoreMatchers.equalTo("ABC")));
    }

    public static class AssertEntityIdsUnwrapped extends KorypheFunction<Object, Object> {
        @Override
        public Object apply(final Object obj) {
            // Check the vertices have been extracted correctly.
            Assert.assertTrue((obj instanceof Iterable));
            for (final Object item : ((Iterable) (obj))) {
                Assert.assertFalse((item instanceof EntityId));
            }
            return obj;
        }
    }
}

