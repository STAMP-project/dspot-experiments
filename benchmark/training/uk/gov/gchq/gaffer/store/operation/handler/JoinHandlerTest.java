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
package uk.gov.gchq.gaffer.store.operation.handler;


import JoinType.FULL;
import MatchKey.LEFT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.join.Join;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.operation.handler.join.JoinHandler;
import uk.gov.gchq.gaffer.store.operation.handler.join.match.ElementMatch;
import uk.gov.gchq.gaffer.store.operation.handler.join.match.KeyFunctionMatch;
import uk.gov.gchq.gaffer.user.User;


public class JoinHandlerTest {
    private final Store store = Mockito.mock(Store.class);

    private final Context context = new Context(new User());

    @Test
    public void shouldSetInputToNewArrayListWhenNull() throws OperationException {
        // Given
        final JoinHandler handler = new JoinHandler();
        final Join joinOp = new Join.Builder<>().joinType(FULL).matchMethod(new ElementMatch()).matchKey(LEFT).build();
        // When
        handler.doOperation(joinOp, context, store);
        // Then
        Assert.assertTrue(joinOp.getInput().equals(new ArrayList()));
    }

    @Test
    public void shouldThrowExceptionWhenInputIsMoreThanLimit() {
        // Given
        final JoinHandler handler = new JoinHandler();
        final List<Integer> inputList = Arrays.asList(1, 2, 3);
        final Join<Integer> joinOp = new Join.Builder<Integer>().input(inputList).joinType(FULL).matchKey(LEFT).matchMethod(new KeyFunctionMatch()).collectionLimit(1).build();
        // When / Then
        try {
            handler.doOperation(joinOp, context, store);
            Assert.fail("exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getCause().getMessage().contains("exceeded"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenNoMatchMethodIsSpecified() {
        // Given
        final JoinHandler handler = new JoinHandler();
        final List<Integer> inputList = Arrays.asList(1, 2, 3);
        Join<Object> joinOp = new Join.Builder<>().input(inputList).joinType(FULL).matchKey(LEFT).build();
        // When / Then
        try {
            handler.doOperation(joinOp, context, store);
            Assert.fail("exception expected");
        } catch (final OperationException e) {
            Assert.assertEquals("A match method must be supplied", e.getMessage());
        }
    }
}

