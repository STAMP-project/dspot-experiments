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
package uk.gov.gchq.gaffer.store.operation.handler.output;


import EdgeId.MatchedVertex;
import EdgeVertices.BOTH;
import EdgeVertices.DESTINATION;
import EdgeVertices.NONE;
import EdgeVertices.SOURCE;
import ToVertices.UseMatchedVertex.EQUAL;
import ToVertices.UseMatchedVertex.IGNORE;
import ToVertices.UseMatchedVertex.OPPOSITE;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EdgeSeed;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.output.ToVertices;
import uk.gov.gchq.gaffer.store.Context;


public class ToVerticesHandlerTest {
    private Object vertex1;

    private Object vertex2;

    private Object vertex3;

    private Object vertex4;

    private Object vertex5;

    private Object vertex6;

    private Object vertex7;

    private Object vertex8;

    @Test
    public void shouldConvertElementSeedsToVertices() throws OperationException {
        // Given
        final List elementIds = Arrays.asList(new EntitySeed(vertex1), new EntitySeed(vertex2));
        final ToVerticesHandler handler = new ToVerticesHandler();
        final ToVertices operation = Mockito.mock(ToVertices.class);
        BDDMockito.given(operation.getInput()).willReturn(elementIds);
        BDDMockito.given(operation.getEdgeVertices()).willReturn(NONE);
        // When
        final Iterable<Object> results = handler.doOperation(operation, new Context(), null);
        // Then
        MatcherAssert.assertThat(results, Matchers.containsInAnyOrder(vertex1, vertex2));
    }

    @Test
    public void shouldBeAbleToIterableOverTheResultsMultipleTimes() throws OperationException {
        // Given
        final List elementIds = Arrays.asList(new EntitySeed(vertex1), new EntitySeed(vertex2));
        final ToVerticesHandler handler = new ToVerticesHandler();
        final ToVertices operation = Mockito.mock(ToVertices.class);
        BDDMockito.given(operation.getInput()).willReturn(elementIds);
        BDDMockito.given(operation.getEdgeVertices()).willReturn(NONE);
        // When
        final Iterable<Object> results = handler.doOperation(operation, new Context(), null);
        // Then
        final Set<Object> set1 = Sets.newHashSet(results);
        final Set<Object> set2 = Sets.newHashSet(results);
        Assert.assertEquals(Sets.newHashSet(vertex1, vertex2), set1);
        Assert.assertEquals(set1, set2);
    }

    @Test
    public void shouldConvertEdgeSeedsToVertices_matchedVertexEqual() throws OperationException {
        // Given
        final List elementIds = Arrays.asList(new EdgeSeed(vertex1, vertex2, false, MatchedVertex.SOURCE), new EdgeSeed(vertex3, vertex4, false, MatchedVertex.DESTINATION), new EdgeSeed(vertex5, vertex6, false, MatchedVertex.DESTINATION), new EdgeSeed(vertex7, vertex8, false, null));
        final ToVerticesHandler handler = new ToVerticesHandler();
        final ToVertices operation = Mockito.mock(ToVertices.class);
        BDDMockito.given(operation.getInput()).willReturn(elementIds);
        BDDMockito.given(operation.getUseMatchedVertex()).willReturn(EQUAL);
        BDDMockito.given(operation.getEdgeVertices()).willReturn(DESTINATION);
        // When
        final Iterable<Object> results = handler.doOperation(operation, new Context(), null);
        // Then
        MatcherAssert.assertThat(Sets.newHashSet(results), Matchers.containsInAnyOrder(vertex1, vertex4, vertex6, vertex8));
    }

    @Test
    public void shouldConvertEdgeSeedsToVertices_matchedVertexOpposite() throws OperationException {
        // Given
        final List elementIds = Arrays.asList(new EdgeSeed(vertex1, vertex2, false, MatchedVertex.SOURCE), new EdgeSeed(vertex3, vertex4, false, MatchedVertex.DESTINATION), new EdgeSeed(vertex5, vertex6, false, MatchedVertex.DESTINATION), new EdgeSeed(vertex7, vertex8, false, null));
        final ToVerticesHandler handler = new ToVerticesHandler();
        final ToVertices operation = Mockito.mock(ToVertices.class);
        BDDMockito.given(operation.getInput()).willReturn(elementIds);
        BDDMockito.given(operation.getUseMatchedVertex()).willReturn(OPPOSITE);
        BDDMockito.given(operation.getEdgeVertices()).willReturn(SOURCE);
        // When
        final Iterable<Object> results = handler.doOperation(operation, new Context(), null);
        // Then
        MatcherAssert.assertThat(Sets.newHashSet(results), Matchers.containsInAnyOrder(vertex2, vertex3, vertex5, vertex7));
    }

    @Test
    public void shouldConvertEdgeSeedsToVertices_sourceAndDestination() throws OperationException {
        // Given
        final List elementIds = Arrays.asList(new EdgeSeed(vertex1, vertex2, false), new EdgeSeed(vertex1, vertex3, false));
        final ToVerticesHandler handler = new ToVerticesHandler();
        final ToVertices operation = Mockito.mock(ToVertices.class);
        BDDMockito.given(operation.getInput()).willReturn(elementIds);
        BDDMockito.given(operation.getEdgeVertices()).willReturn(BOTH);
        // When
        final Iterable<Object> results = handler.doOperation(operation, new Context(), null);
        // Then
        MatcherAssert.assertThat(Sets.newHashSet(results), Matchers.containsInAnyOrder(vertex1, vertex2, vertex3));
    }

    @Test
    public void shouldConvertEdgeSeedsToVertices_sourceOnly() throws OperationException {
        // Given
        final List elementIds = Collections.singletonList(new EdgeSeed(vertex1, vertex2, false));
        final ToVerticesHandler handler = new ToVerticesHandler();
        final ToVertices operation = Mockito.mock(ToVertices.class);
        BDDMockito.given(operation.getInput()).willReturn(elementIds);
        BDDMockito.given(operation.getEdgeVertices()).willReturn(SOURCE);
        // When
        final Iterable<Object> results = handler.doOperation(operation, new Context(), null);
        // Then
        MatcherAssert.assertThat(Sets.newHashSet(results), Matchers.containsInAnyOrder(vertex1));
    }

    @Test
    public void shouldConvertEdgeSeedsToVertices_destinationOnly() throws OperationException {
        // Given
        final List elementIds = Collections.singletonList(new EdgeSeed(vertex1, vertex2, false));
        final ToVerticesHandler handler = new ToVerticesHandler();
        final ToVertices operation = Mockito.mock(ToVertices.class);
        BDDMockito.given(operation.getInput()).willReturn(elementIds);
        BDDMockito.given(operation.getEdgeVertices()).willReturn(DESTINATION);
        // When
        final Iterable<Object> results = handler.doOperation(operation, new Context(), null);
        // Then
        MatcherAssert.assertThat(Sets.newHashSet(results), Matchers.containsInAnyOrder(vertex2));
    }

    @Test
    public void shouldCorrectlyConvertEdgeSeedsWithEqualUseMatchedVertex() throws OperationException {
        // Given
        final List elementIds = Arrays.asList(new EdgeSeed(vertex1, vertex2, false, null), new EdgeSeed(vertex3, vertex4, false, null), new EdgeSeed(vertex5, vertex6, false, null), new EdgeSeed(vertex7, vertex8, false, null));
        final ToVerticesHandler handler = new ToVerticesHandler();
        final ToVertices operation = Mockito.mock(ToVertices.class);
        BDDMockito.given(operation.getInput()).willReturn(elementIds);
        BDDMockito.given(operation.getUseMatchedVertex()).willReturn(EQUAL);
        // When
        final Iterable<Object> results = handler.doOperation(operation, new Context(), null);
        // Then
        MatcherAssert.assertThat(Sets.newHashSet(results), Matchers.containsInAnyOrder(vertex1, vertex3, vertex5, vertex7));
    }

    @Test
    public void shouldCorrectlyConvertEdgeSeedsWithOppositeUseMatchedVertex() throws OperationException {
        // Given
        final List elementIds = Arrays.asList(new EdgeSeed(vertex1, vertex2, false, null), new EdgeSeed(vertex3, vertex4, false, null), new EdgeSeed(vertex5, vertex6, false, null), new EdgeSeed(vertex7, vertex8, false, null));
        final ToVerticesHandler handler = new ToVerticesHandler();
        final ToVertices operation = Mockito.mock(ToVertices.class);
        BDDMockito.given(operation.getInput()).willReturn(elementIds);
        BDDMockito.given(operation.getUseMatchedVertex()).willReturn(OPPOSITE);
        // When
        final Iterable<Object> results = handler.doOperation(operation, new Context(), null);
        // Then
        MatcherAssert.assertThat(Sets.newHashSet(results), Matchers.containsInAnyOrder(vertex2, vertex4, vertex6, vertex8));
    }

    @Test
    public void shouldCorrectlyConvertEdgeSeedsWithNoneUseMatchedVertex() throws OperationException {
        // Given
        final List elementIds = Arrays.asList(new EdgeSeed(vertex1, vertex2, false, null), new EdgeSeed(vertex3, vertex4, false, null), new EdgeSeed(vertex5, vertex6, false, null), new EdgeSeed(vertex7, vertex8, false, null));
        final ToVerticesHandler handler = new ToVerticesHandler();
        final ToVertices operation = Mockito.mock(ToVertices.class);
        BDDMockito.given(operation.getInput()).willReturn(elementIds);
        BDDMockito.given(operation.getUseMatchedVertex()).willReturn(IGNORE);
        // When
        final Iterable<Object> results = handler.doOperation(operation, new Context(), null);
        // Then
        MatcherAssert.assertThat(Sets.newHashSet(results), Is.is(Matchers.empty()));
    }

    @Test
    public void shouldHandleNullInput() throws OperationException {
        // Given
        final ToVerticesHandler handler = new ToVerticesHandler();
        final ToVertices operation = Mockito.mock(ToVertices.class);
        BDDMockito.given(operation.getInput()).willReturn(null);
        BDDMockito.given(operation.getEdgeVertices()).willReturn(NONE);
        // When
        final Iterable<Object> results = handler.doOperation(operation, new Context(), null);
        // Then
        MatcherAssert.assertThat(results, Is.is(Matchers.nullValue()));
    }
}

