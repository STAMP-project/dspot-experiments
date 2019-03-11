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
package uk.gov.gchq.gaffer.store.operation.handler;


import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.function.ExtractProperty;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.Map;
import uk.gov.gchq.gaffer.operation.impl.While;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.io.Output;
import uk.gov.gchq.gaffer.operation.util.Conditional;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.koryphe.impl.predicate.IsFalse;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;


public class WhileHandlerTest {
    @Test
    public void shouldSetAndGetMaxRepeats() {
        // Given
        final WhileHandler handler = new WhileHandler();
        // When
        handler.setMaxRepeats(10);
        // Then
        Assert.assertEquals(10, handler.getMaxRepeats());
        // When 2
        handler.setMaxRepeats(25);
        // Then 2
        Assert.assertEquals(25, handler.getMaxRepeats());
    }

    @Test
    public void shouldRepeatDelegateOperationUntilMaxRepeatsReached() throws OperationException {
        // Given
        final List<EntitySeed> input = Collections.singletonList(Mockito.mock(EntitySeed.class));
        final int maxRepeats = 3;
        final GetAdjacentIds delegate = Mockito.mock(GetAdjacentIds.class);
        final GetAdjacentIds delegateClone1 = Mockito.mock(GetAdjacentIds.class);
        final GetAdjacentIds delegateClone2 = Mockito.mock(GetAdjacentIds.class);
        final GetAdjacentIds delegateClone3 = Mockito.mock(GetAdjacentIds.class);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(delegate.shallowClone()).willReturn(delegateClone1, delegateClone2, delegateClone3);
        final CloseableIterable result1 = Mockito.mock(CloseableIterable.class);
        final CloseableIterable result2 = Mockito.mock(CloseableIterable.class);
        final CloseableIterable result3 = Mockito.mock(CloseableIterable.class);
        BDDMockito.given(store.execute(delegateClone1, context)).willReturn(result1);
        BDDMockito.given(store.execute(delegateClone2, context)).willReturn(result2);
        BDDMockito.given(store.execute(delegateClone3, context)).willReturn(result3);
        final While operation = new While.Builder<>().input(input).maxRepeats(maxRepeats).operation(delegate).build();
        final WhileHandler handler = new WhileHandler();
        // When
        final Object result = handler.doOperation(operation, context, store);
        // Then
        Mockito.verify(delegateClone1).setInput(input);
        Mockito.verify(delegateClone2).setInput(result1);
        Mockito.verify(delegateClone3).setInput(result2);
        Mockito.verify(store).execute(((Output) (delegateClone1)), context);
        Mockito.verify(store).execute(((Output) (delegateClone2)), context);
        Mockito.verify(store).execute(((Output) (delegateClone3)), context);
        Assert.assertSame(result3, result);
    }

    @Test
    public void shouldRepeatWhileConditionIsTrue() throws OperationException {
        // Given
        final List<EntitySeed> input = Collections.singletonList(Mockito.mock(EntitySeed.class));
        final boolean condition = true;
        final int maxRepeats = 3;
        final GetElements delegate = Mockito.mock(GetElements.class);
        final GetElements delegateClone1 = Mockito.mock(GetElements.class);
        final GetElements delegateClone2 = Mockito.mock(GetElements.class);
        final GetElements delegateClone3 = Mockito.mock(GetElements.class);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(delegate.shallowClone()).willReturn(delegateClone1, delegateClone2, delegateClone3);
        final CloseableIterable result1 = Mockito.mock(CloseableIterable.class);
        final CloseableIterable result2 = Mockito.mock(CloseableIterable.class);
        final CloseableIterable result3 = Mockito.mock(CloseableIterable.class);
        BDDMockito.given(store.execute(delegateClone1, context)).willReturn(result1);
        BDDMockito.given(store.execute(delegateClone2, context)).willReturn(result2);
        BDDMockito.given(store.execute(delegateClone3, context)).willReturn(result3);
        final While operation = new While.Builder<>().input(input).condition(condition).maxRepeats(maxRepeats).operation(delegate).build();
        final WhileHandler handler = new WhileHandler();
        // When
        final Object result = handler.doOperation(operation, context, store);
        // Then
        Mockito.verify(delegateClone1).setInput(input);
        Mockito.verify(delegateClone2).setInput(result1);
        Mockito.verify(delegateClone3).setInput(result2);
        Mockito.verify(store).execute(((Output) (delegateClone1)), context);
        Mockito.verify(store).execute(((Output) (delegateClone2)), context);
        Mockito.verify(store).execute(((Output) (delegateClone3)), context);
        Assert.assertSame(result3, result);
    }

    @Test
    public void shouldNotRepeatWhileConditionIsFalse() throws OperationException {
        // Given
        final EntitySeed input = Mockito.mock(EntitySeed.class);
        final int maxRepeats = 3;
        final boolean condition = false;
        final Operation delegate = Mockito.mock(GetElements.class);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final While operation = new While.Builder<>().input(input).maxRepeats(maxRepeats).condition(condition).operation(delegate).build();
        final WhileHandler handler = new WhileHandler();
        // When
        handler.doOperation(operation, context, store);
        // Then
        Mockito.verify(store, Mockito.never()).execute(((Output) (delegate)), context);
    }

    @Test
    public void shouldThrowExceptionWhenMaxConfiguredNumberOfRepeatsExceeded() throws OperationException {
        // Given
        final EntitySeed input = Mockito.mock(EntitySeed.class);
        final int maxRepeats = 2500;
        final Operation delegate = Mockito.mock(GetElements.class);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final While operation = new While.Builder<>().input(input).maxRepeats(maxRepeats).operation(delegate).build();
        final WhileHandler handler = new WhileHandler();
        // When / Then
        try {
            handler.doOperation(operation, context, store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains(((("Max repeats of the While operation is too large: " + maxRepeats) + " > ") + (While.MAX_REPEATS))));
        }
    }

    @Test
    public void shouldThrowExceptionWhenPredicateCannotAcceptInputType() throws OperationException {
        // Given
        final Predicate predicate = new IsFalse();
        final Object input = new EntitySeed();
        final Conditional conditional = new Conditional(predicate);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final While operation = new While.Builder<>().input(input).conditional(conditional).operation(new GetElements()).build();
        final WhileHandler handler = new WhileHandler();
        // When / Then
        try {
            handler.doOperation(operation, context, store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains((((("The predicate '" + (predicate.getClass().getSimpleName())) + "' cannot accept an input of type '") + (input.getClass().getSimpleName())) + "'")));
        }
    }

    @Test
    public void shouldUpdateTransformInputAndTestAgainstPredicate() throws OperationException {
        final Edge input = new Edge.Builder().group("testEdge").source("src").dest("dest").directed(true).property("count", 3).build();
        final Map<Element, Object> transform = Mockito.mock(Map.class);
        final Map<Element, Object> transformClone = Mockito.mock(Map.class);
        BDDMockito.given(transform.shallowClone()).willReturn(transformClone);
        final Predicate predicate = new IsMoreThan(2);
        final Conditional conditional = new Conditional(predicate, transform);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final GetElements getElements = Mockito.mock(GetElements.class);
        final While operation = new While.Builder<>().input(input).maxRepeats(1).conditional(conditional).operation(getElements).build();
        final WhileHandler handler = new WhileHandler();
        // When
        handler.doOperation(operation, context, store);
        // Then
        Mockito.verify(transformClone).setInput(input);
        Mockito.verify(store).execute(transformClone, context);
    }

    @Test
    public void shouldFailPredicateTestAndNotExecuteDelegateOperation() throws OperationException {
        // Given
        final Edge input = new Edge.Builder().group("testEdge").source("src").dest("dest").directed(true).property("count", 3).build();
        final Map<Element, Object> transform = new Map.Builder<Element>().first(new ExtractProperty("count")).build();
        final Predicate predicate = new IsMoreThan(5);
        final Conditional conditional = new Conditional(predicate, transform);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final GetElements getElements = Mockito.mock(GetElements.class);
        final While operation = new While.Builder<>().input(input).maxRepeats(1).conditional(conditional).operation(getElements).build();
        final WhileHandler handler = new WhileHandler();
        // When
        handler.doOperation(operation, context, store);
        // Then
        Mockito.verify(store, Mockito.never()).execute(((Output) (getElements)), context);
    }

    @Test
    public void shouldExecuteNonOutputOperation() throws OperationException {
        // Given
        final AddElements addElements = new AddElements.Builder().input(new Edge.Builder().build()).build();
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final While operation = new While.Builder<>().operation(addElements).condition(true).maxRepeats(3).build();
        final WhileHandler handler = new WhileHandler();
        // When
        handler.doOperation(operation, context, store);
        // Then
        Mockito.verify(store, Mockito.times(3)).execute(addElements, context);
    }

    @Test
    public void shouldJsonSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final WhileHandler handler = new WhileHandler();
        handler.setMaxRepeats(5);
        // When
        final byte[] json = JSONSerialiser.serialise(handler);
        final WhileHandler deserialisedHandler = JSONSerialiser.deserialise(json, WhileHandler.class);
        // Then
        Assert.assertEquals(5, deserialisedHandler.getMaxRepeats());
    }
}

