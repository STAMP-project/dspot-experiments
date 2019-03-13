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
package uk.gov.gchq.gaffer.graph.hook;


import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.named.operation.NamedOperation;
import uk.gov.gchq.gaffer.named.operation.NamedOperationDetail;
import uk.gov.gchq.gaffer.named.operation.ParameterDetail;
import uk.gov.gchq.gaffer.named.operation.cache.exception.CacheOperationFailedException;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.Limit;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.operation.handler.named.cache.NamedOperationCache;
import uk.gov.gchq.gaffer.user.User;


public class NamedOperationResolverTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldResolveNamedOperation() throws CacheOperationFailedException, OperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = Mockito.mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);
        final User user = Mockito.mock(User.class);
        final NamedOperationDetail extendedNamedOperation = Mockito.mock(NamedOperationDetail.class);
        final GetAdjacentIds op1 = Mockito.mock(GetAdjacentIds.class);
        final GetElements op2 = Mockito.mock(GetElements.class);
        final OperationChain namedOperationOpChain = new OperationChain(Arrays.asList(op1, op2));
        final Iterable<?> input = Mockito.mock(CloseableIterable.class);
        final Map<String, Object> params = null;
        BDDMockito.given(op1.getInput()).willReturn(null);
        BDDMockito.given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);
        BDDMockito.given(extendedNamedOperation.getOperationChain(params)).willReturn(namedOperationOpChain);
        final OperationChain<Object> opChain = new OperationChain.Builder().first(new NamedOperation.Builder<>().name(opName).input(input).build()).build();
        // When
        resolver.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(user));
        // Then
        Assert.assertEquals(namedOperationOpChain.getOperations(), opChain.getOperations());
        Mockito.verify(op1).setInput(((Iterable) (input)));
        Mockito.verify(op2, Mockito.never()).setInput(((Iterable) (input)));
    }

    @Test
    public void shouldResolveNestedNamedOperation() throws CacheOperationFailedException, OperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = Mockito.mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);
        final User user = Mockito.mock(User.class);
        final NamedOperationDetail extendedNamedOperation = Mockito.mock(NamedOperationDetail.class);
        final GetAdjacentIds op1 = Mockito.mock(GetAdjacentIds.class);
        final GetElements op2 = Mockito.mock(GetElements.class);
        final OperationChain namedOperationOpChain = new OperationChain(Arrays.asList(op1, op2));
        final Iterable<?> input = Mockito.mock(CloseableIterable.class);
        final Map<String, Object> params = null;
        BDDMockito.given(op1.getInput()).willReturn(null);
        BDDMockito.given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);
        BDDMockito.given(extendedNamedOperation.getOperationChain(params)).willReturn(namedOperationOpChain);
        final OperationChain<Object> opChain = new OperationChain.Builder().first(new OperationChain.Builder().first(new NamedOperation.Builder<>().name(opName).input(input).build()).build()).build();
        // When
        resolver.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(user));
        // Then
        Assert.assertEquals(1, opChain.getOperations().size());
        final OperationChain<?> nestedOpChain = ((OperationChain<?>) (opChain.getOperations().get(0)));
        Assert.assertEquals(namedOperationOpChain.getOperations(), nestedOpChain.getOperations());
        Mockito.verify(op1).setInput(((Iterable) (input)));
        Mockito.verify(op2, Mockito.never()).setInput(((Iterable) (input)));
    }

    @Test
    public void shouldExecuteNamedOperationWithoutOverridingInput() throws CacheOperationFailedException, OperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = Mockito.mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);
        final User user = Mockito.mock(User.class);
        final NamedOperationDetail extendedNamedOperation = Mockito.mock(NamedOperationDetail.class);
        final GetAdjacentIds op1 = Mockito.mock(GetAdjacentIds.class);
        final GetElements op2 = Mockito.mock(GetElements.class);
        final OperationChain namedOpChain = new OperationChain(Arrays.asList(op1, op2));
        final Iterable<?> input = Mockito.mock(CloseableIterable.class);
        final Map<String, Object> params = null;
        BDDMockito.given(op1.getInput()).willReturn(Mockito.mock(CloseableIterable.class));
        BDDMockito.given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);
        BDDMockito.given(extendedNamedOperation.getOperationChain(params)).willReturn(namedOpChain);
        // When
        final OperationChain<Object> opChain = new OperationChain.Builder().first(new NamedOperation.Builder<>().name(opName).input(input).build()).build();
        resolver.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(user));
        // Then
        Assert.assertSame(op1, opChain.getOperations().get(0));
        Mockito.verify(op1, Mockito.never()).setInput(((Iterable) (input)));
        Assert.assertSame(op2, opChain.getOperations().get(1));
        Mockito.verify(op2, Mockito.never()).setInput(((Iterable) (input)));
    }

    @Test
    public void shouldResolveNamedOperationWithParameter() throws CacheOperationFailedException, OperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = Mockito.mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);
        final User user = Mockito.mock(User.class);
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("param1", 1L);
        ParameterDetail param = new ParameterDetail.Builder().defaultValue(1L).description("Limit param").valueClass(Long.class).build();
        Map<String, ParameterDetail> paramDetailMap = Maps.newHashMap();
        paramDetailMap.put("param1", param);
        // Make a real NamedOperationDetail with a parameter
        final NamedOperationDetail extendedNamedOperation = new NamedOperationDetail.Builder().operationName(opName).description("standard operation").operationChain("{ \"operations\": [ { \"class\":\"uk.gov.gchq.gaffer.operation.impl.get.GetAllElements\" }, { \"class\":\"uk.gov.gchq.gaffer.operation.impl.Limit\", \"resultLimit\": \"${param1}\" } ] }").parameters(paramDetailMap).build();
        BDDMockito.given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);
        final OperationChain<Object> opChain = new OperationChain.Builder().first(new NamedOperation.Builder<>().name(opName).parameters(paramMap).build()).build();
        // When
        resolver.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(user));
        // Then
        Assert.assertEquals(opChain.getOperations().get(0).getClass(), GetAllElements.class);
        Assert.assertEquals(opChain.getOperations().get(1).getClass(), Limit.class);
        // Check the parameter has been inserted
        Assert.assertEquals(((long) (getResultLimit())), 1L);
    }

    @Test
    public void shouldNotExecuteNamedOperationWithParameterOfWrongType() throws CacheOperationFailedException, OperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = Mockito.mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);
        final User user = Mockito.mock(User.class);
        Map<String, Object> paramMap = Maps.newHashMap();
        // A parameter of the wrong type
        paramMap.put("param1", new ArrayList());
        ParameterDetail param = new ParameterDetail.Builder().defaultValue(1L).description("Limit param").valueClass(Long.class).build();
        Map<String, ParameterDetail> paramDetailMap = Maps.newHashMap();
        paramDetailMap.put("param1", param);
        // Make a real NamedOperationDetail with a parameter
        final NamedOperationDetail extendedNamedOperation = new NamedOperationDetail.Builder().operationName(opName).description("standard operation").operationChain("{ \"operations\": [ { \"class\":\"uk.gov.gchq.gaffer.operation.impl.get.GetAllElements\" }, { \"class\":\"uk.gov.gchq.gaffer.operation.impl.Limit\", \"resultLimit\": \"${param1}\" } ] }").parameters(paramDetailMap).build();
        BDDMockito.given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);
        // When
        exception.expect(IllegalArgumentException.class);
        resolver.preExecute(new OperationChain.Builder().first(new NamedOperation.Builder<>().name(opName).parameters(paramMap).build()).build(), new uk.gov.gchq.gaffer.store.Context(user));
    }

    @Test
    public void shouldNotExecuteNamedOperationWithWrongParameterName() throws CacheOperationFailedException, OperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = Mockito.mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);
        final User user = Mockito.mock(User.class);
        Map<String, Object> paramMap = Maps.newHashMap();
        // A parameter with the wrong name
        paramMap.put("param2", 1L);
        ParameterDetail param = new ParameterDetail.Builder().defaultValue(1L).description("Limit param").valueClass(Long.class).build();
        Map<String, ParameterDetail> paramDetailMap = Maps.newHashMap();
        paramDetailMap.put("param1", param);
        // Make a real NamedOperationDetail with a parameter
        final NamedOperationDetail extendedNamedOperation = new NamedOperationDetail.Builder().operationName(opName).description("standard operation").operationChain("{ \"operations\": [ { \"class\":\"uk.gov.gchq.gaffer.operation.impl.get.GetAllElements\" }, { \"class\":\"uk.gov.gchq.gaffer.operation.impl.Limit\", \"resultLimit\": \"${param1}\" } ] }").parameters(paramDetailMap).build();
        BDDMockito.given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);
        // When
        exception.expect(IllegalArgumentException.class);
        resolver.preExecute(new OperationChain.Builder().first(new NamedOperation.Builder<>().name(opName).parameters(paramMap).build()).build(), new uk.gov.gchq.gaffer.store.Context(user));
    }

    @Test
    public void shouldNotExecuteNamedOperationWithMissingRequiredArg() throws CacheOperationFailedException, OperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = Mockito.mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);
        final User user = Mockito.mock(User.class);
        // Don't set any parameters
        Map<String, Object> paramMap = Maps.newHashMap();
        ParameterDetail param = new ParameterDetail.Builder().description("Limit param").valueClass(Long.class).required(true).build();
        Map<String, ParameterDetail> paramDetailMap = Maps.newHashMap();
        paramDetailMap.put("param1", param);
        // Make a real NamedOperationDetail with a parameter
        final NamedOperationDetail extendedNamedOperation = new NamedOperationDetail.Builder().operationName(opName).description("standard operation").operationChain("{ \"operations\": [ { \"class\":\"uk.gov.gchq.gaffer.operation.impl.get.GetAllElements\" }, { \"class\":\"uk.gov.gchq.gaffer.operation.impl.Limit\", \"resultLimit\": \"${param1}\" } ] }").parameters(paramDetailMap).build();
        BDDMockito.given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);
        // When
        exception.expect(IllegalArgumentException.class);
        resolver.preExecute(new OperationChain.Builder().first(new NamedOperation.Builder<>().name(opName).parameters(paramMap).build()).build(), new uk.gov.gchq.gaffer.store.Context(user));
    }

    @Test
    public void shouldReturnOperationsInParameters() {
        // Given
        final NamedOperation namedOperation = new NamedOperation();
        Operation operation = new GetElements();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("test param", operation);
        namedOperation.setParameters(paramMap);
        // When
        List<Operation> paramOperations = namedOperation.getOperations();
        Operation op = paramOperations.get(0);
        // Then
        Assert.assertEquals(paramOperations.size(), 1);
        Assert.assertEquals(op.getClass(), GetElements.class);
    }
}

