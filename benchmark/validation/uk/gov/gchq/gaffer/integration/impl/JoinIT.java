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
package uk.gov.gchq.gaffer.integration.impl;


import JoinType.FULL;
import JoinType.INNER;
import JoinType.OUTER;
import MatchKey.LEFT;
import MatchKey.RIGHT;
import TestGroups.ENTITY_3;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestPropertyNames;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.impl.join.Join;
import uk.gov.gchq.koryphe.tuple.MapTuple;


public class JoinIT extends AbstractStoreIT {
    private List<Element> inputElements = new java.util.ArrayList(Arrays.asList(getJoinEntity(ENTITY_3, 1), getJoinEntity(ENTITY_3, 2), getJoinEntity(ENTITY_3, 3), getJoinEntity(ENTITY_3, 4), getJoinEntity(ENTITY_3, 6)));

    private List<Element> innerJoinElements = new java.util.ArrayList(Arrays.asList(getJoinEntity(ENTITY_3, 1), getJoinEntity(ENTITY_3, 2), getJoinEntity(ENTITY_3, 3), getJoinEntity(ENTITY_3, 4)));

    private final GetElements rhsGetElementsOperation = new GetElements.Builder().input(new EntitySeed(((AbstractStoreIT.VERTEX_PREFIXES[0]) + 0))).view(new View.Builder().entity(ENTITY_3).build()).build();

    @Test
    public void testNestedViewCompletedIfNotSupplied() throws Exception {
        // Given
        Join<Element> joinOp = new Join.Builder<Element>().input(inputElements).operation(new GetAllElements()).joinType(INNER).matchKey(LEFT).matchMethod(new uk.gov.gchq.gaffer.store.operation.handler.join.match.ElementMatch(TestPropertyNames.COUNT)).build();
        // When / Then - no exceptions
        AbstractStoreIT.graph.execute(joinOp, getUser());
    }

    @Test
    public void shouldLeftKeyFullJoin() throws OperationException {
        // Given
        final List<Element> expectedResults = new java.util.ArrayList(inputElements);
        Join<Element> joinOp = new Join.Builder<Element>().input(inputElements).operation(rhsGetElementsOperation).joinType(FULL).matchKey(LEFT).matchMethod(new uk.gov.gchq.gaffer.store.operation.handler.join.match.ElementMatch(TestPropertyNames.COUNT)).flatten(false).build();
        // When
        final Iterable<? extends MapTuple> results = AbstractStoreIT.graph.execute(joinOp, getUser());
        // Then
        assertKeysExist(expectedResults, results, LEFT);
    }

    @Test
    public void shouldRightKeyFullJoin() throws OperationException {
        // Given
        final List<Element> expectedResults = innerJoinElements;
        expectedResults.add(getJoinEntity(ENTITY_3, 8));
        Join<Element> joinOp = new Join.Builder<Element>().input(inputElements).operation(rhsGetElementsOperation).joinType(FULL).matchKey(RIGHT).matchMethod(new uk.gov.gchq.gaffer.store.operation.handler.join.match.ElementMatch(TestPropertyNames.COUNT)).flatten(false).build();
        // When
        final Iterable<? extends MapTuple> results = AbstractStoreIT.graph.execute(joinOp, getUser());
        // Then
        assertKeysExist(expectedResults, results, RIGHT);
    }

    @Test
    public void shouldLeftKeyOuterJoin() throws OperationException {
        // Given
        final List<Element> expectedResults = new java.util.ArrayList(Arrays.asList(getJoinEntity(ENTITY_3, 6)));
        Join<Element> joinOp = new Join.Builder<Element>().input(inputElements).operation(rhsGetElementsOperation).joinType(OUTER).matchKey(LEFT).flatten(false).matchMethod(new uk.gov.gchq.gaffer.store.operation.handler.join.match.ElementMatch(TestPropertyNames.COUNT)).build();
        // When
        final Iterable<? extends MapTuple> results = AbstractStoreIT.graph.execute(joinOp, getUser());
        // Then
        assertKeysExist(expectedResults, results, LEFT);
    }

    @Test
    public void shouldRightKeyOuterJoin() throws OperationException {
        // Given
        final List<Element> expectedResults = new java.util.ArrayList(Arrays.asList(getJoinEntity(ENTITY_3, 8)));
        Join<Element> joinOp = new Join.Builder<Element>().input(inputElements).operation(rhsGetElementsOperation).joinType(OUTER).matchKey(RIGHT).flatten(false).matchMethod(new uk.gov.gchq.gaffer.store.operation.handler.join.match.ElementMatch(TestPropertyNames.COUNT)).build();
        // When
        final Iterable<? extends MapTuple> results = AbstractStoreIT.graph.execute(joinOp, getUser());
        // Then
        assertKeysExist(expectedResults, results, RIGHT);
    }

    @Test
    public void shouldLeftKeyInnerJoin() throws OperationException {
        // Given
        final List<Element> expectedResults = innerJoinElements;
        Join<Element> joinOp = new Join.Builder<Element>().input(inputElements).operation(rhsGetElementsOperation).joinType(INNER).matchKey(LEFT).flatten(false).matchMethod(new uk.gov.gchq.gaffer.store.operation.handler.join.match.ElementMatch(TestPropertyNames.COUNT)).build();
        // When
        final Iterable<? extends MapTuple> results = AbstractStoreIT.graph.execute(joinOp, getUser());
        // Then
        assertKeysExist(expectedResults, results, LEFT);
    }

    @Test
    public void shouldRightKeyInnerJoin() throws OperationException {
        // Given
        final List<Element> expectedResults = innerJoinElements;
        Join<Element> joinOp = new Join.Builder<Element>().input(inputElements).operation(rhsGetElementsOperation).joinType(INNER).matchKey(RIGHT).matchMethod(new uk.gov.gchq.gaffer.store.operation.handler.join.match.ElementMatch(TestPropertyNames.COUNT)).flatten(false).build();
        // When
        final Iterable<? extends MapTuple> results = AbstractStoreIT.graph.execute(joinOp, getUser());
        // Then
        assertKeysExist(expectedResults, results, RIGHT);
    }
}

