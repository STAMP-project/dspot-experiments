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


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.GetVariable;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.user.User;


public class GetVariableHandlerTest {
    private final String varName = "varName";

    private final String varVal = "varVal";

    private final Store store = Mockito.mock(Store.class);

    @Test
    public void shouldGetVariableWhenExists() throws OperationException {
        // Given
        final Context context = Mockito.mock(Context.class);
        final GetVariableHandler handler = new GetVariableHandler();
        final GetVariable op = new GetVariable.Builder().variableName(varName).build();
        BDDMockito.given(context.getVariable(varName)).willReturn(varVal);
        // When
        final Object variableValueFromOp = handler.doOperation(op, context, store);
        // Then
        Assert.assertEquals(varVal, variableValueFromOp);
    }

    @Test
    public void shouldReturnNullWhenVariableDoesntExist() throws OperationException {
        // Given
        final Context context = Mockito.mock(Context.class);
        final GetVariableHandler handler = new GetVariableHandler();
        final GetVariable op = new GetVariable.Builder().variableName(varName).build();
        BDDMockito.given(context.getVariable(varName)).willReturn(null);
        // When
        final Object variableValueFromOp = handler.doOperation(op, context, store);
        // Then
        Assert.assertNull(variableValueFromOp);
    }

    @Test
    public void shouldThrowExceptionWhenVariableKeyIsNull() throws OperationException {
        // Given
        final Context context = Mockito.mock(Context.class);
        final GetVariableHandler handler = new GetVariableHandler();
        final GetVariable op = new GetVariable.Builder().variableName(null).build();
        // When / Then
        try {
            handler.doOperation(op, context, store);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Variable name cannot be null"));
        }
    }

    @Test
    public void shouldNotThrowNPEWhenVariablesSet() throws OperationException {
        // Given
        final Context context = new Context(new User());
        final GetVariableHandler handler = new GetVariableHandler();
        final GetVariable op = new GetVariable.Builder().variableName(varName).build();
        // When
        final Object variableValueFromOp = handler.doOperation(op, context, store);
        // Then
        Assert.assertNull(variableValueFromOp);
    }
}

