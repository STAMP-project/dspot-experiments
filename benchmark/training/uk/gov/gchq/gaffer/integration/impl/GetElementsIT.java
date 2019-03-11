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
package uk.gov.gchq.gaffer.integration.impl;


import EdgeId.MatchedVertex.DESTINATION;
import EdgeId.MatchedVertex.SOURCE;
import IdentifierType.ADJACENT_MATCHED_VERTEX;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestPropertyNames.COUNT;
import TestPropertyNames.INT;
import TestPropertyNames.SET;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.CollectionUtil;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.element.id.DirectedType;
import uk.gov.gchq.gaffer.data.element.id.ElementId;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.data.util.ElementUtil;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.integration.TraitRequirement;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EdgeSeed;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.graph.SeededGraphFilters.IncludeIncomingOutgoingType;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.koryphe.impl.predicate.IsIn;


public class GetElementsIT extends AbstractStoreIT {
    // ElementId Seeds
    public static final Collection<ElementId> ENTITY_SEEDS_EXIST = Arrays.asList(((ElementId) (new EntitySeed(AbstractStoreIT.SOURCE_2))), new EntitySeed(AbstractStoreIT.DEST_3), new EntitySeed(AbstractStoreIT.SOURCE_DIR_2), new EntitySeed(AbstractStoreIT.DEST_DIR_3));

    public static final Collection<Element> ENTITIES_EXIST = GetElementsIT.getElements(GetElementsIT.ENTITY_SEEDS_EXIST, null);

    public static final Collection<ElementId> EDGE_SEEDS_EXIST = Arrays.asList(((ElementId) (new EdgeSeed(AbstractStoreIT.SOURCE_1, AbstractStoreIT.DEST_1, false))), ((ElementId) (new EdgeSeed(((AbstractStoreIT.VERTEX_PREFIXES[0]) + 0), ((AbstractStoreIT.VERTEX_PREFIXES[1]) + 0)))), ((ElementId) (new EdgeSeed(((AbstractStoreIT.VERTEX_PREFIXES[0]) + 2), ((AbstractStoreIT.VERTEX_PREFIXES[1]) + 2)))));

    public static final Collection<ElementId> EDGE_SEEDS_BOTH = Arrays.asList(((ElementId) (new EdgeSeed(((AbstractStoreIT.VERTEX_PREFIXES[0]) + 0), ((AbstractStoreIT.VERTEX_PREFIXES[1]) + 0)))), ((ElementId) (new EdgeSeed(((AbstractStoreIT.VERTEX_PREFIXES[0]) + 2), ((AbstractStoreIT.VERTEX_PREFIXES[1]) + 2)))));

    public static final Collection<Element> EDGES_EXIST = GetElementsIT.getElements(GetElementsIT.EDGE_SEEDS_EXIST, false);

    public static final Collection<ElementId> EDGE_DIR_SEEDS_EXIST = Arrays.asList(((ElementId) (new EdgeSeed(AbstractStoreIT.SOURCE_DIR_1, AbstractStoreIT.DEST_DIR_1, true))), ((ElementId) (new EdgeSeed(((AbstractStoreIT.VERTEX_PREFIXES[0]) + 0), ((AbstractStoreIT.VERTEX_PREFIXES[1]) + 0)))), ((ElementId) (new EdgeSeed(((AbstractStoreIT.VERTEX_PREFIXES[0]) + 2), ((AbstractStoreIT.VERTEX_PREFIXES[1]) + 2)))));

    public static final Collection<Element> EDGES_DIR_EXIST = GetElementsIT.getElements(GetElementsIT.EDGE_DIR_SEEDS_EXIST, true);

    public static final Collection<ElementId> EDGE_SEEDS_DONT_EXIST = Arrays.asList(((ElementId) (new EdgeSeed(AbstractStoreIT.SOURCE_1, "dest2DoesNotExist", false))), new EdgeSeed("source2DoesNotExist", AbstractStoreIT.DEST_1, false), new EdgeSeed(AbstractStoreIT.SOURCE_1, AbstractStoreIT.DEST_1, true));// does not exist


    public static final Collection<ElementId> ENTITY_SEEDS_DONT_EXIST = Collections.singletonList(((ElementId) (new EntitySeed("idDoesNotExist"))));

    public static final Collection<ElementId> ENTITY_SEEDS = GetElementsIT.getEntityIds();

    public static final Collection<ElementId> EDGE_SEEDS = GetElementsIT.getEdgeIds();

    public static final Collection<ElementId> ALL_SEEDS = GetElementsIT.getAllSeeds();

    public static final Collection<Object> ALL_SEED_VERTICES = GetElementsIT.getAllSeededVertices();

    @Test
    public void shouldGetElements() {
        final List<DirectedType> directedTypes = Lists.newArrayList(DirectedType.values());
        directedTypes.add(null);
        final List<IncludeIncomingOutgoingType> inOutTypes = Lists.newArrayList(IncludeIncomingOutgoingType.values());
        inOutTypes.add(null);
        for (final boolean includeEntities : Arrays.asList(true, false)) {
            for (final boolean includeEdges : Arrays.asList(true, false)) {
                if ((!includeEntities) && (!includeEdges)) {
                    // Cannot query for nothing!
                    continue;
                }
                for (final DirectedType directedType : directedTypes) {
                    for (final IncludeIncomingOutgoingType inOutType : inOutTypes) {
                        try {
                            shouldGetElementsBySeed(includeEntities, includeEdges, directedType, inOutType);
                        } catch (final Throwable e) {
                            throw new AssertionError(((((((("GetElementsBySeed failed with parameters: \nincludeEntities=" + includeEntities) + " \nincludeEdges=") + includeEdges) + " \ndirectedType=") + directedType) + " \ninOutType=") + inOutType), e);
                        }
                        try {
                            shouldGetRelatedElements(includeEntities, includeEdges, directedType, inOutType);
                        } catch (final Throwable e) {
                            throw new AssertionError(((((((("GetRelatedElements failed with parameters: \nincludeEntities=" + includeEntities) + " \nincludeEdges=") + includeEdges) + " \ndirectedType=") + directedType) + " \ninOutType=") + inOutType), e);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void shouldGetAllEdgesWhenFlagSet() throws Exception {
        // Given
        final User user = new User();
        final GetElements opExcludingAllEdges = new GetElements.Builder().input(new EntitySeed(AbstractStoreIT.SOURCE_1), new EntitySeed(AbstractStoreIT.DEST_2)).view(new View.Builder().entity(ENTITY).build()).build();
        final GetElements opIncludingAllEdges = new GetElements.Builder().input(new EntitySeed(AbstractStoreIT.SOURCE_1), new EntitySeed(AbstractStoreIT.DEST_2)).view(new View.Builder().allEdges(true).build()).build();
        // When
        final CloseableIterable<? extends Element> resultsExcludingAllEdges = AbstractStoreIT.graph.execute(opExcludingAllEdges, user);
        // Then
        ElementUtil.assertElementEquals(Arrays.asList(new Entity.Builder().group(ENTITY).vertex(AbstractStoreIT.SOURCE_1).property(SET, CollectionUtil.treeSet("3")).property(COUNT, 1L).build(), new Entity.Builder().group(ENTITY).vertex(AbstractStoreIT.DEST_2).property(SET, CollectionUtil.treeSet("3")).property(COUNT, 1L).build()), resultsExcludingAllEdges);
        // When
        final CloseableIterable<? extends Element> resultsIncludingAllEdges = AbstractStoreIT.graph.execute(opIncludingAllEdges, user);
        // Then
        ElementUtil.assertElementEquals(Arrays.asList(new Edge.Builder().group(EDGE).source(AbstractStoreIT.SOURCE_1).dest(AbstractStoreIT.DEST_1).directed(false).matchedVertex(EdgeId.MatchedVertex.SOURCE).property(INT, 1).property(COUNT, 1L).build(), new Edge.Builder().group(EDGE).source(AbstractStoreIT.SOURCE_2).dest(AbstractStoreIT.DEST_2).directed(false).matchedVertex(DESTINATION).property(INT, 1).property(COUNT, 1L).build()), resultsIncludingAllEdges);
    }

    @Test
    public void shouldGetAllEntitiesWhenFlagSet() throws OperationException {
        // Given
        final User user = new User();
        final GetElements opExcludingAllEntities = new GetElements.Builder().input(new EntitySeed(AbstractStoreIT.SOURCE_1), new EntitySeed(AbstractStoreIT.DEST_2)).view(new View.Builder().edge(EDGE).build()).build();
        final GetElements opIncludingAllEntities = new GetElements.Builder().input(new EntitySeed(AbstractStoreIT.SOURCE_1), new EntitySeed(AbstractStoreIT.DEST_2)).view(new View.Builder().allEntities(true).build()).build();
        // When
        final CloseableIterable<? extends Element> resultsExcludingAllEntities = AbstractStoreIT.graph.execute(opExcludingAllEntities, user);
        // Then
        ElementUtil.assertElementEquals(Arrays.asList(new Edge.Builder().group(EDGE).source(AbstractStoreIT.SOURCE_1).dest(AbstractStoreIT.DEST_1).directed(false).matchedVertex(EdgeId.MatchedVertex.SOURCE).property(INT, 1).property(COUNT, 1L).build(), new Edge.Builder().group(EDGE).source(AbstractStoreIT.SOURCE_2).dest(AbstractStoreIT.DEST_2).directed(false).matchedVertex(DESTINATION).property(INT, 1).property(COUNT, 1L).build()), resultsExcludingAllEntities);
        // When
        final CloseableIterable<? extends Element> resultsIncludingAllEntities = AbstractStoreIT.graph.execute(opIncludingAllEntities, user);
        // Then
        ElementUtil.assertElementEquals(Arrays.asList(new Entity.Builder().group(ENTITY).vertex(AbstractStoreIT.SOURCE_1).property(SET, CollectionUtil.treeSet("3")).property(COUNT, 1L).build(), new Entity.Builder().group(ENTITY).vertex(AbstractStoreIT.DEST_2).property(SET, CollectionUtil.treeSet("3")).property(COUNT, 1L).build()), resultsIncludingAllEntities);
    }

    @TraitRequirement(StoreTrait.MATCHED_VERTEX)
    @Test
    public void shouldGetElementsWithMatchedVertex() throws Exception {
        // Given
        final User user = new User();
        final GetElements op = new GetElements.Builder().input(new EntitySeed(AbstractStoreIT.SOURCE_DIR_1), new EntitySeed(AbstractStoreIT.DEST_DIR_2), new EntitySeed(AbstractStoreIT.SOURCE_DIR_3)).view(new View.Builder().edge(EDGE).build()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, user);
        // Then
        ElementUtil.assertElementEquals(Arrays.asList(new Edge.Builder().group(EDGE).source(AbstractStoreIT.SOURCE_DIR_1).dest(AbstractStoreIT.DEST_DIR_1).directed(true).matchedVertex(EdgeId.MatchedVertex.SOURCE).property(INT, 1).property(COUNT, 1L).build(), new Edge.Builder().group(EDGE).source(AbstractStoreIT.SOURCE_DIR_2).dest(AbstractStoreIT.DEST_DIR_2).directed(true).matchedVertex(DESTINATION).property(INT, 1).property(COUNT, 1L).build(), new Edge.Builder().group(EDGE).source(AbstractStoreIT.SOURCE_DIR_3).dest(AbstractStoreIT.DEST_DIR_3).directed(true).matchedVertex(EdgeId.MatchedVertex.SOURCE).property(INT, 1).property(COUNT, 1L).build()), results);
    }

    @Test
    @TraitRequirement(StoreTrait.MATCHED_VERTEX)
    public void shouldGetElementsWithMatchedVertexFilter() throws Exception {
        // Given
        final User user = new User();
        final GetElements op = new GetElements.Builder().input(new EntitySeed(AbstractStoreIT.SOURCE_DIR_1), new EntitySeed(AbstractStoreIT.DEST_DIR_2), new EntitySeed(AbstractStoreIT.SOURCE_DIR_3)).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(ADJACENT_MATCHED_VERTEX.name()).execute(new IsIn(AbstractStoreIT.DEST_DIR_1, AbstractStoreIT.DEST_DIR_2, AbstractStoreIT.DEST_DIR_3)).build()).build()).build()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, user);
        // Then
        ElementUtil.assertElementEquals(Arrays.asList(new Edge.Builder().group(EDGE).source(AbstractStoreIT.SOURCE_DIR_1).dest(AbstractStoreIT.DEST_DIR_1).directed(true).matchedVertex(EdgeId.MatchedVertex.SOURCE).property(INT, 1).property(COUNT, 1L).build(), new Edge.Builder().group(EDGE).source(AbstractStoreIT.SOURCE_DIR_3).dest(AbstractStoreIT.DEST_DIR_3).directed(true).matchedVertex(EdgeId.MatchedVertex.SOURCE).property(INT, 1).property(COUNT, 1L).build()), results);
    }

    @Test
    public void shouldGetElementsWithProvidedProperties() throws Exception {
        // Given
        final User user = new User();
        final GetElements op = new GetElements.Builder().input(new EntitySeed(AbstractStoreIT.SOURCE_2)).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, user);
        // Then
        for (final Element result : results) {
            Assert.assertEquals(1, result.getProperties().size());
            Assert.assertEquals(1L, result.getProperties().get(COUNT));
        }
    }

    @Test
    public void shouldGetElementsWithExcludedProperties() throws Exception {
        // Given
        final User user = new User();
        final GetElements op = new GetElements.Builder().input(new EntitySeed(AbstractStoreIT.SOURCE_2)).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, user);
        // Then
        for (final Element result : results) {
            Assert.assertEquals(1, result.getProperties().size());
            Assert.assertEquals(1L, result.getProperties().get(COUNT));
        }
    }

    @Test
    public void shouldReturnEmptyIteratorIfNoSeedsProvidedForGetElementsBySeed() throws Exception {
        // Given
        final GetElements op = new GetElements.Builder().input(new uk.gov.gchq.gaffer.commonutil.iterable.EmptyClosableIterable()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        Assert.assertFalse(results.iterator().hasNext());
    }

    @Test
    public void shouldReturnEmptyIteratorIfNoSeedsProvidedForGetRelatedElements() throws Exception {
        // Given
        final GetElements op = new GetElements.Builder().input(new uk.gov.gchq.gaffer.commonutil.iterable.EmptyClosableIterable()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        Assert.assertFalse(results.iterator().hasNext());
    }
}

