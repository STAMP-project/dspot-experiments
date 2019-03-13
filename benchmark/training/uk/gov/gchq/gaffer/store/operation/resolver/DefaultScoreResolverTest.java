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
package uk.gov.gchq.gaffer.store.operation.resolver;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.named.operation.NamedOperation;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.GetWalks;
import uk.gov.gchq.gaffer.operation.impl.Limit;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.operation.resolver.named.NamedOperationScoreResolver;


public class DefaultScoreResolverTest {
    @Test
    public void shouldGetDefaultScoreWhenNoOperationScores() throws OperationException {
        // Given
        final DefaultScoreResolver resolver = new DefaultScoreResolver(new LinkedHashMap());
        final GetAdjacentIds op1 = Mockito.mock(GetAdjacentIds.class);
        // When
        final int score = resolver.getScore(op1);
        // Then
        Assert.assertEquals(1, score);
    }

    @Test
    public void shouldGetScore() {
        // Given
        final GetAdjacentIds op1 = Mockito.mock(GetAdjacentIds.class);
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(GetAdjacentIds.class, 2);
        opScores.put(GetElements.class, 1);
        opScores.put(Limit.class, 1);
        final DefaultScoreResolver resolver = new DefaultScoreResolver(opScores);
        // When
        final int score = resolver.getScore(op1);
        // Then
        Assert.assertEquals(2, score);
    }

    @Test
    public void shouldGetScoreForOperationChain() {
        // Given
        final GetAdjacentIds getAdjacentIds = Mockito.mock(GetAdjacentIds.class);
        final GetElements getElements = Mockito.mock(GetElements.class);
        final Limit limit = Mockito.mock(Limit.class);
        final List<Operation> opList = Arrays.asList(getAdjacentIds, getElements, limit);
        final OperationChain opChain = Mockito.mock(OperationChain.class);
        BDDMockito.given(opChain.getOperations()).willReturn(opList);
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(GetElements.class, 2);
        opScores.put(GetAdjacentIds.class, 3);
        opScores.put(Limit.class, 1);
        final DefaultScoreResolver resolver = new DefaultScoreResolver(opScores);
        // When
        final int score = resolver.getScore(opChain);
        // Then
        Assert.assertEquals(6, score);
    }

    @Test
    public void shouldGetScoreForNestedOperations() {
        // Given
        final GetElements getElements = Mockito.mock(GetElements.class);
        final GetWalks getWalks = Mockito.mock(GetWalks.class);
        final GetAdjacentIds getAdjacentIds = Mockito.mock(GetAdjacentIds.class);
        BDDMockito.given(getWalks.getOperations()).willReturn(Collections.singletonList(new OperationChain(getAdjacentIds, getAdjacentIds)));
        final Limit limit = Mockito.mock(Limit.class);
        final List<Operation> opList = Arrays.asList(getElements, getWalks, limit);
        final OperationChain opChain = Mockito.mock(OperationChain.class);
        BDDMockito.given(opChain.getOperations()).willReturn(opList);
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(GetElements.class, 2);
        opScores.put(GetAdjacentIds.class, 2);
        opScores.put(Limit.class, 1);
        final DefaultScoreResolver resolver = new DefaultScoreResolver(opScores);
        // When
        final int score = resolver.getScore(opChain);
        // Then
        Assert.assertEquals(7, score);
    }

    @Test
    public void shouldGetOperationChainScore() throws OperationException {
        // Given
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver();
        final OperationChain opChain = new OperationChain.Builder().first(Mockito.mock(GetAdjacentIds.class)).then(Mockito.mock(GetElements.class)).build();
        // When
        final Object result = scoreResolver.getScore(opChain);
        // Then
        Assert.assertSame(2, result);
    }

    @Test
    public void shouldGetScoreForOperationChainWithNestedOperationChain() throws OperationException {
        // Given
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver();
        final OperationChain opChain = new OperationChain.Builder().first(new OperationChain.Builder().first(Mockito.mock(GetAdjacentIds.class)).then(Mockito.mock(GetElements.class)).build()).then(Mockito.mock(Limit.class)).build();
        // When
        final Object result = scoreResolver.getScore(opChain);
        // Then
        Assert.assertSame(3, result);
    }

    @Test
    public void shouldGetScoreForOperationChainContainingNamedOperation() throws OperationException {
        // Given
        final ScoreResolver mockResolver = Mockito.mock(NamedOperationScoreResolver.class);
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(GetAdjacentIds.class, 2);
        opScores.put(GetElements.class, 3);
        opScores.put(Limit.class, 4);
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();
        final String opName = "basicOp";
        final NamedOperation<Object, Object> namedOp = new uk.gov.gchq.gaffer.named.operation.NamedOperation.Builder().name(opName).build();
        resolvers.put(NamedOperation.class, mockResolver);
        BDDMockito.given(mockResolver.getScore(ArgumentMatchers.eq(namedOp), ArgumentMatchers.any())).willReturn(5);
        final OperationChain opChain = new OperationChain.Builder().first(new GetAdjacentIds()).then(new GetElements()).then(new Limit()).then(new GetWalks.Builder().addOperations(namedOp, new GetElements()).build()).build();
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver(opScores, resolvers);
        // When
        final Object result = scoreResolver.getScore(opChain);
        // Then
        Assert.assertEquals(17, result);
    }

    @Test
    public void shouldPreventInfiniteRecusion() throws OperationException {
        // Given
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();
        resolvers.put(GetElements.class, new ScoreResolver() {
            @Override
            public Integer getScore(final Operation operation) {
                throw new IllegalArgumentException("defaultResolver is required");
            }

            @Override
            public Integer getScore(final Operation operation, final ScoreResolver defaultScoreResolver) {
                // infinite loop
                return defaultScoreResolver.getScore(operation);
            }
        });
        final OperationChain opChain = new OperationChain.Builder().first(new GetAdjacentIds()).then(new GetElements()).build();
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver(null, resolvers);
        // When
        final Object result = scoreResolver.getScore(opChain);
        // Then
        Assert.assertEquals(2, result);
    }

    @Test
    public void shouldGetScoreForOperationChainWhenNamedOperationScoreIsNull() throws OperationException {
        // Given
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(GetAdjacentIds.class, 2);
        opScores.put(GetElements.class, 1);
        opScores.put(Limit.class, 1);
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();
        final String opName = "basicOp";
        final NamedOperation<Object, Object> namedOp = new uk.gov.gchq.gaffer.named.operation.NamedOperation.Builder().name(opName).build();
        final ScoreResolver mockResolver = Mockito.mock(NamedOperationScoreResolver.class);
        resolvers.put(NamedOperation.class, mockResolver);
        BDDMockito.given(mockResolver.getScore(ArgumentMatchers.eq(namedOp), ArgumentMatchers.any())).willReturn(null);
        final OperationChain opChain = new OperationChain.Builder().first(new GetAdjacentIds()).then(new GetElements()).then(new Limit()).then(namedOp).build();
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver(opScores, resolvers);
        // When
        final Object result = scoreResolver.getScore(opChain);
        // Then
        Assert.assertEquals(5, result);
    }

    @Test
    public void shouldGetScoreForOperationChainWithMultipleScoreResolvers() throws OperationException {
        // Given
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();
        final ScoreResolver mockResolver = Mockito.mock(NamedOperationScoreResolver.class);
        final ScoreResolver mockResolver1 = Mockito.mock(DefaultScoreResolver.class);
        final GetElements op1 = new GetElements();
        final AddElements op2 = new AddElements();
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(GetElements.class, 2);
        final String opName = "namedOp";
        final NamedOperation<Iterable<? extends Element>, Iterable<? extends Element>> namedOp = Mockito.mock(NamedOperation.class);
        namedOp.setOperationName(opName);
        resolvers.put(namedOp.getClass(), mockResolver);
        resolvers.put(op2.getClass(), mockResolver1);
        BDDMockito.given(mockResolver.getScore(ArgumentMatchers.eq(namedOp), ArgumentMatchers.any())).willReturn(3);
        BDDMockito.given(mockResolver1.getScore(ArgumentMatchers.eq(op2), ArgumentMatchers.any())).willReturn(5);
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver(opScores, resolvers);
        final OperationChain opChain = new OperationChain.Builder().first(op1).then(op2).then(namedOp).build();
        // When
        final Object result = scoreResolver.getScore(opChain);
        // Then
        Assert.assertEquals(10, result);
    }

    @Test
    public void shouldGetScoreForNestedOperationWithNullOperationList() throws OperationException {
        // Given
        final GetElements op1 = Mockito.mock(GetElements.class);
        final AddElements op2 = Mockito.mock(AddElements.class);
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver();
        final OperationChain opChain = new OperationChain.Builder().first(op1).then(op2).then(new OperationChain(((List) (null)))).build();
        // When
        final Object result = scoreResolver.getScore(opChain);
        // Then
        Assert.assertEquals(2, result);
    }

    @Test
    public void shouldReturnZeroForANullOperationChain() throws OperationException {
        // Given
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver();
        final OperationChain opChain = null;
        // When
        final Object result = scoreResolver.getScore(opChain);
        // Then
        Assert.assertEquals(0, result);
    }
}

