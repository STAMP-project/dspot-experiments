/**
 * Copyright 2018-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.integration.impl.loader;


import IdentifierType.ADJACENT_MATCHED_VERTEX;
import IdentifierType.VERTEX;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestPropertyNames.COUNT;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View.Builder;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.integration.TraitRequirement;
import uk.gov.gchq.gaffer.integration.VisibilityUser;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.koryphe.impl.predicate.IsEqual;
import uk.gov.gchq.koryphe.impl.predicate.IsIn;


/**
 * Unit test specifications for data loading operations.
 *
 * @param <T>
 * 		the operation implementation to test
 */
public abstract class AbstractLoaderIT<T extends Operation> extends AbstractStoreIT {
    protected Iterable<? extends Element> input;

    // ////////////////////////////////////////////////////////////////
    // Add Elements error handling                 //
    // ////////////////////////////////////////////////////////////////
    @Test
    public void shouldThrowExceptionWithUsefulMessageWhenInvalidElementsAdded() throws OperationException {
        // Given
        final AddElements addElements = new AddElements.Builder().input(new Edge("UnknownGroup", "source", "dest", true)).build();
        // When / Then
        try {
            AbstractStoreIT.graph.execute(addElements, getUser());
        } catch (final Exception e) {
            String msg = e.getMessage();
            if ((!(msg.contains("Element of type Entity"))) && (null != (e.getCause()))) {
                msg = e.getCause().getMessage();
            }
            Assert.assertTrue(("Message was: " + msg), msg.contains("UnknownGroup"));
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenInvalidElementsAddedWithSkipInvalidSetToTrue() throws OperationException {
        // Given
        final AddElements addElements = new AddElements.Builder().input(new Edge("Unknown group", "source", "dest", true)).skipInvalidElements(true).build();
        // When
        AbstractStoreIT.graph.execute(addElements, getUser());
        // Then - no exceptions
    }

    @Test
    public void shouldNotThrowExceptionWhenInvalidElementsAddedWithValidateSetToFalse() throws OperationException {
        // Given
        final AddElements addElements = new AddElements.Builder().input(new Edge("Unknown group", "source", "dest", true)).validate(false).build();
        // When
        AbstractStoreIT.graph.execute(addElements, getUser());
        // Then - no exceptions
    }

    // ////////////////////////////////////////////////////////////////
    // Get Elements                         //
    // ////////////////////////////////////////////////////////////////
    @Test
    @TraitRequirement(StoreTrait.QUERY_AGGREGATION)
    public void shouldGetAllElements() throws Exception {
        // Then
        getAllElements();
    }

    @Test
    public void shouldGetAllElementsWithProvidedProperties() throws Exception {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build();
        // When
        final Consumer<Iterable<? extends Element>> resultTest = ( iter) -> {
            iter.forEach(( element) -> {
                Assert.assertEquals(1, getProperties().size());
                Assert.assertEquals(((long) (AbstractStoreIT.DUPLICATES)), getProperties().get(COUNT));
            });
        };
        // Then
        getAllElementsWithView(resultTest, view);
    }

    @Test
    @TraitRequirement(StoreTrait.QUERY_AGGREGATION)
    public void shouldGetAllElementsWithExcludedProperties() throws Exception {
        // Given
        final View view = new Builder().edge(EDGE, new ViewElementDefinition.Builder().excludeProperties(COUNT).build()).build();
        final GetAllElements op = new GetAllElements.Builder().view(view).build();
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        // When
        final List<Element> expected = getQuerySummarisedEdges(view).stream().map(( edge) -> {
            edge.getProperties().remove(TestPropertyNames.COUNT);
            return edge;
        }).collect(Collectors.toList());
        // Then
        assertElementEquals(expected, results);
    }

    @Test
    public void shouldReturnEmptyIteratorIfNoSeedsProvidedForGetElements() throws Exception {
        // Then
        final GetElements op = new GetElements.Builder().input(new uk.gov.gchq.gaffer.commonutil.iterable.EmptyClosableIterable()).build();
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        Assert.assertFalse(results.iterator().hasNext());
    }

    @TraitRequirement({ StoreTrait.MATCHED_VERTEX, StoreTrait.QUERY_AGGREGATION })
    @Test
    public void shouldGetElementsWithMatchedVertex() throws Exception {
        // Then
        final View view = new Builder().edge(EDGE).build();
        final GetElements op = new GetElements.Builder().input(new uk.gov.gchq.gaffer.operation.data.EntitySeed(AbstractStoreIT.SOURCE_DIR_1), new uk.gov.gchq.gaffer.operation.data.EntitySeed(AbstractStoreIT.DEST_DIR_2), new uk.gov.gchq.gaffer.operation.data.EntitySeed(AbstractStoreIT.SOURCE_DIR_3)).view(view).build();
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        assertElementEquals(getQuerySummarisedEdges(view).stream().filter(Edge::isDirected).filter(( edge) -> {
            final List<String> vertices = Lists.newArrayList(AbstractStoreIT.SOURCE_DIR_1, AbstractStoreIT.SOURCE_DIR_2, AbstractStoreIT.SOURCE_DIR_3);
            return vertices.contains(edge.getMatchedVertexValue());
        }).collect(Collectors.toList()), results);
    }

    // ////////////////////////////////////////////////////////////////
    // Visibility                           //
    // ////////////////////////////////////////////////////////////////
    @TraitRequirement(StoreTrait.VISIBILITY)
    @VisibilityUser("basic")
    @Test
    public void shouldGetOnlyVisibleElements() throws Exception {
        getAllElements();
    }

    // ////////////////////////////////////////////////////////////////
    // Filtering                            //
    // ////////////////////////////////////////////////////////////////
    @TraitRequirement({ StoreTrait.PRE_AGGREGATION_FILTERING, StoreTrait.INGEST_AGGREGATION })
    @Test
    public void shouldGetAllElementsFilteredOnGroup() throws Exception {
        // Then
        final GetAllElements op = new GetAllElements.Builder().view(new View.Builder().entity(ENTITY).build()).build();
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        final List<Element> resultList = Lists.newArrayList(results);
        Assert.assertEquals(getEntities().size(), resultList.size());
        for (final Element element : resultList) {
            Assert.assertEquals(ENTITY, element.getGroup());
        }
    }

    @TraitRequirement(StoreTrait.PRE_AGGREGATION_FILTERING)
    @Test
    public void shouldGetAllFilteredElements() throws Exception {
        // Then
        final GetAllElements op = new GetAllElements.Builder().view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(VERTEX.name()).execute(new IsEqual("A1")).build()).build()).build()).build();
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        final List<Element> resultList = Lists.newArrayList(results);
        Assert.assertEquals(1, resultList.size());
        Assert.assertEquals("A1", getVertex());
    }

    @TraitRequirement({ StoreTrait.MATCHED_VERTEX, StoreTrait.QUERY_AGGREGATION })
    @Test
    public void shouldGetElementsWithMatchedVertexFilter() throws Exception {
        // Then
        final View view = new Builder().edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(ADJACENT_MATCHED_VERTEX.name()).execute(new IsIn(AbstractStoreIT.DEST_DIR_1, AbstractStoreIT.DEST_DIR_2, AbstractStoreIT.DEST_DIR_3)).build()).build()).build();
        final GetElements op = new GetElements.Builder().input(new uk.gov.gchq.gaffer.operation.data.EntitySeed(AbstractStoreIT.SOURCE_DIR_1), new uk.gov.gchq.gaffer.operation.data.EntitySeed(AbstractStoreIT.DEST_DIR_2), new uk.gov.gchq.gaffer.operation.data.EntitySeed(AbstractStoreIT.SOURCE_DIR_3)).view(view).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        assertElementEquals(getQuerySummarisedEdges(view).stream().filter(Edge::isDirected).filter(( edge) -> {
            final List<String> vertices = Lists.newArrayList(AbstractStoreIT.SOURCE_DIR_1, AbstractStoreIT.DEST_DIR_2, AbstractStoreIT.SOURCE_DIR_3);
            return vertices.contains(edge.getMatchedVertexValue());
        }).filter(( edge) -> {
            final List<String> vertices = Lists.newArrayList(AbstractStoreIT.DEST_DIR_1, AbstractStoreIT.DEST_DIR_2, AbstractStoreIT.DEST_DIR_3);
            return vertices.contains(edge.getAdjacentMatchedVertexValue());
        }).collect(Collectors.toList()), results);
    }
}

