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


import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.SetVariable;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.user.User;


public class SetVariableHandlerTest {
    @Test
    public void shouldSetVariableInContext() throws OperationException {
        // Given
        final Context context = new Context(new User());
        final Store store = Mockito.mock(Store.class);
        final String testVarName = "testVarName";
        final int testVarValue = 4;
        SetVariableHandler handler = new SetVariableHandler();
        SetVariable op = new SetVariable.Builder().variableName(testVarName).input(testVarValue).build();
        // When
        handler.doOperation(op, context, store);
        // Then
        Assert.assertTrue(context.getVariable(testVarName).equals(testVarValue));
        Assert.assertTrue(context.getVariables().equals(ImmutableMap.of(testVarName, testVarValue)));
    }

    @Test
    public void shouldThrowExceptionWithNullVariableKey() throws OperationException {
        // Given
        final Context context = new Context(new User());
        final Store store = Mockito.mock(Store.class);
        SetVariableHandler handler = new SetVariableHandler();
        SetVariable op = new SetVariable();
        // When / Then
        try {
            handler.doOperation(op, context, store);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Variable name cannot be null"));
        }
    }

    @Test
    public void shouldNotAllowNullInputVariableToBeAdded() throws OperationException {
        // Given
        final Context context = new Context(new User());
        final Store store = Mockito.mock(Store.class);
        final String testVarName = "testVarName";
        final Object testVarValue = null;
        SetVariableHandler handler = new SetVariableHandler();
        SetVariable op = new SetVariable.Builder().variableName(testVarName).input(testVarValue).build();
        // When / Then
        try {
            handler.doOperation(op, context, store);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Variable input value cannot be null"));
        }
    }

    @Test
    public void setTwoVarsWithoutFailure() throws OperationException {
        // Given
        final Context context = new Context(new User());
        final Store store = Mockito.mock(Store.class);
        final String varName = "testVarName";
        final String varVal = "varVal";
        final String varName1 = "testVarName1";
        final String varVal1 = "varVal1";
        SetVariableHandler handler = new SetVariableHandler();
        SetVariable op = new SetVariable.Builder().variableName(varName).input(varVal).build();
        SetVariable op1 = new SetVariable.Builder().variableName(varName1).input(varVal1).build();
        // When
        handler.doOperation(op, context, store);
        handler.doOperation(op1, context, store);
        // Then
        Assert.assertEquals(2, context.getVariables().size());
        Assert.assertEquals(ImmutableMap.of(varName, varVal, varName1, varVal1), context.getVariables());
    }
}

