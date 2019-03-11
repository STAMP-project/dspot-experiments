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


import TestGroups.ENTITY;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.Limit;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.operation.OperationChainValidator;
import uk.gov.gchq.gaffer.store.optimiser.OperationChainOptimiser;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.koryphe.ValidationResult;


public class OperationChainHandlerTest {
    @Test
    public void shouldHandleOperationChain() throws OperationException {
        // Given
        final OperationChainValidator opChainValidator = Mockito.mock(OperationChainValidator.class);
        final List<OperationChainOptimiser> opChainOptimisers = Collections.emptyList();
        final OperationChainHandler opChainHandler = new OperationChainHandler(opChainValidator, opChainOptimisers);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        final StoreProperties storeProperties = new StoreProperties();
        final GetAdjacentIds op1 = Mockito.mock(GetAdjacentIds.class);
        final GetElements op2 = Mockito.mock(GetElements.class);
        final OperationChain opChain = new OperationChain(Arrays.asList(op1, op2));
        final Entity expectedResult = new Entity(TestGroups.ENTITY);
        BDDMockito.given(context.getUser()).willReturn(user);
        BDDMockito.given(store.getProperties()).willReturn(storeProperties);
        BDDMockito.given(opChainValidator.validate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(new ValidationResult());
        BDDMockito.given(store.handleOperation(op1, context)).willReturn(new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterable(Collections.singletonList(new EntitySeed())));
        BDDMockito.given(store.handleOperation(op2, context)).willReturn(expectedResult);
        // When
        final Object result = opChainHandler.doOperation(opChain, context, store);
        // Then
        Assert.assertSame(expectedResult, result);
    }

    @Test
    public void shouldHandleNonInputOperation() throws OperationException {
        // Given
        final OperationChainValidator opChainValidator = Mockito.mock(OperationChainValidator.class);
        final List<OperationChainOptimiser> opChainOptimisers = Collections.emptyList();
        final OperationChainHandler opChainHandler = new OperationChainHandler(opChainValidator, opChainOptimisers);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        final StoreProperties storeProperties = new StoreProperties();
        final GetAllElements op = Mockito.mock(GetAllElements.class);
        final OperationChain opChain = new OperationChain(Collections.singletonList(op));
        final Entity expectedResult = new Entity(TestGroups.ENTITY);
        BDDMockito.given(context.getUser()).willReturn(user);
        BDDMockito.given(store.getProperties()).willReturn(storeProperties);
        BDDMockito.given(opChainValidator.validate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(new ValidationResult());
        BDDMockito.given(store.handleOperation(op, context)).willReturn(expectedResult);
        // When
        final Object result = opChainHandler.doOperation(opChain, context, store);
        // Then
        Assert.assertSame(expectedResult, result);
    }

    @Test
    public void shouldHandleNestedOperationChain() throws OperationException {
        // Given
        final OperationChainValidator opChainValidator = Mockito.mock(OperationChainValidator.class);
        final List<OperationChainOptimiser> opChainOptimisers = Collections.emptyList();
        final OperationChainHandler opChainHandler = new OperationChainHandler(opChainValidator, opChainOptimisers);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        final StoreProperties storeProperties = new StoreProperties();
        final GetAdjacentIds op1 = Mockito.mock(GetAdjacentIds.class);
        final GetElements op2 = Mockito.mock(GetElements.class);
        final Limit op3 = Mockito.mock(Limit.class);
        final OperationChain opChain1 = new OperationChain(Arrays.asList(op1, op2));
        final OperationChain opChain2 = new OperationChain(Arrays.asList(opChain1, op3));
        final Entity entityA = new Entity.Builder().group(ENTITY).vertex("A").build();
        final Entity entityB = new Entity.Builder().group(ENTITY).vertex("B").build();
        BDDMockito.given(context.getUser()).willReturn(user);
        BDDMockito.given(store.getProperties()).willReturn(storeProperties);
        BDDMockito.given(opChainValidator.validate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(new ValidationResult());
        BDDMockito.given(store.handleOperation(op1, context)).willReturn(new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterable(Lists.newArrayList(new EntitySeed("A"), new EntitySeed("B"))));
        BDDMockito.given(store.handleOperation(op2, context)).willReturn(new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterable(Lists.newArrayList(entityA, entityB)));
        BDDMockito.given(store.handleOperation(op3, context)).willReturn(entityA);
        // When
        final Object result = opChainHandler.doOperation(opChain2, context, store);
        // Then
        Assert.assertSame(entityA, result);
    }
}

