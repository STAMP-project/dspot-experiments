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


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.ForEach;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.io.InputOutput;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.user.User;


public class ForEachHandlerTest {
    @Test
    public void shouldThrowExceptionWithNullOperation() {
        // Given
        final Store store = Mockito.mock(Store.class);
        final Context context = new Context(new User());
        final ForEach op = new ForEach.Builder<>().operation(null).input(Arrays.asList("1", "2")).build();
        final ForEachHandler handler = new ForEachHandler();
        // When / Then
        try {
            handler.doOperation(op, context, store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains("Operation cannot be null"));
        }
    }

    @Test
    public void shouldThrowExceptionWithNullInput() {
        // Given
        final Store store = Mockito.mock(Store.class);
        final Context context = new Context(new User());
        final ForEach op = new ForEach.Builder<>().operation(new GetElements()).build();
        final ForEachHandler handler = new ForEachHandler();
        // When / Then
        try {
            handler.doOperation(op, context, store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains("Inputs cannot be null"));
        }
    }

    @Test
    public void shouldExecuteAndReturnExpected() throws OperationException {
        // Given
        final Store store = Mockito.mock(Store.class);
        final Context context = new Context(new User());
        final InputOutput op = Mockito.mock(InputOutput.class);
        final InputOutput opClone = Mockito.mock(InputOutput.class);
        BDDMockito.given(op.shallowClone()).willReturn(opClone);
        final Object input = Mockito.mock(Object.class);
        final Object output = Mockito.mock(Object.class);
        final ForEach forEach = new ForEach.Builder<>().input(input).operation(op).build();
        final ForEachHandler handler = new ForEachHandler();
        BDDMockito.given(store.execute(opClone, context)).willReturn(output);
        // When
        final List<Object> result = ((List<Object>) (handler.doOperation(forEach, context, store)));
        // Then
        Mockito.verify(opClone).setInput(input);
        Assert.assertEquals(1, result.size());
        Assert.assertSame(output, result.get(0));
    }
}

