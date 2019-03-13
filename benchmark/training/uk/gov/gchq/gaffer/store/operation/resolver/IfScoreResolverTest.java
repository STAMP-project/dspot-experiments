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
package uk.gov.gchq.gaffer.store.operation.resolver;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.impl.Count;
import uk.gov.gchq.gaffer.operation.impl.GetWalks;
import uk.gov.gchq.gaffer.operation.impl.If;
import uk.gov.gchq.gaffer.operation.impl.Map;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.impl.output.ToMap;
import uk.gov.gchq.gaffer.operation.util.Conditional;


public class IfScoreResolverTest {
    @Test
    public void shouldGetDefaultScoreWithNoOperationScores() {
        // Given
        final IfScoreResolver resolver = new IfScoreResolver();
        final DefaultScoreResolver defaultResolver = new DefaultScoreResolver(new LinkedHashMap());
        final If operation = new If();
        // When
        final int score = resolver.getScore(operation, defaultResolver);
        // Then
        Assert.assertEquals(2, score);
    }

    @Test
    public void shouldGetScoreWithFullyPopulatedOperation() {
        // Given
        final Count count = Mockito.mock(Count.class);
        final GetAllElements getAllElements = Mockito.mock(GetAllElements.class);
        final GetWalks getWalks = Mockito.mock(GetWalks.class);
        final Conditional conditional = Mockito.mock(Conditional.class);
        BDDMockito.given(conditional.getTransform()).willReturn(count);
        final If operation = new If.Builder<>().conditional(conditional).then(getWalks).otherwise(getAllElements).build();
        final LinkedHashMap<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Count.class, 1);
        opScores.put(GetAllElements.class, 3);
        opScores.put(GetWalks.class, 4);
        final DefaultScoreResolver defaultResolver = new DefaultScoreResolver(opScores);
        final IfScoreResolver resolver = new IfScoreResolver();
        // When
        final int score = resolver.getScore(operation, defaultResolver);
        // Then
        Assert.assertEquals(4, score);
    }

    @Test
    public void shouldGetScoreWithOperationChainAsAnOperation() {
        // Given
        final GetElements getElements = Mockito.mock(GetElements.class);
        final ToMap toMap = Mockito.mock(ToMap.class);
        final Map map = Mockito.mock(Map.class);
        final OperationChain conditionalChain = Mockito.mock(OperationChain.class);
        final List<Operation> conditionalOps = new LinkedList<>();
        conditionalOps.add(getElements);
        conditionalOps.add(toMap);
        conditionalOps.add(map);
        BDDMockito.given(conditionalChain.getOperations()).willReturn(conditionalOps);
        final Conditional conditional = Mockito.mock(Conditional.class);
        BDDMockito.given(conditional.getTransform()).willReturn(conditionalChain);
        final GetAdjacentIds getAdjacentIds = Mockito.mock(GetAdjacentIds.class);
        final GetAllElements getAllElements = Mockito.mock(GetAllElements.class);
        final If operation = new If.Builder<>().conditional(conditional).then(getAdjacentIds).otherwise(getAllElements).build();
        final LinkedHashMap<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(GetElements.class, 2);
        opScores.put(ToMap.class, 2);
        opScores.put(Map.class, 3);
        opScores.put(GetAdjacentIds.class, 3);
        opScores.put(GetAllElements.class, 4);
        final IfScoreResolver resolver = new IfScoreResolver();
        final DefaultScoreResolver defaultResolver = new DefaultScoreResolver(opScores);
        // When
        final int score = resolver.getScore(operation, defaultResolver);
        // Then
        Assert.assertEquals(11, score);
    }

    @Test
    public void shouldGetScoreForNestedOperations() {
        // Given
        final Map map = Mockito.mock(Map.class);
        final Conditional conditional = Mockito.mock(Conditional.class);
        BDDMockito.given(conditional.getTransform()).willReturn(map);
        final GetWalks getWalks = Mockito.mock(GetWalks.class);
        BDDMockito.given(getWalks.getOperations()).willReturn(Collections.singletonList(new OperationChain(new GetAdjacentIds(), new GetAdjacentIds())));
        final GetAllElements getAllElements = new GetAllElements();
        final If operation = new If.Builder<>().conditional(conditional).then(getWalks).otherwise(getAllElements).build();
        final LinkedHashMap<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(Map.class, 3);
        opScores.put(GetAdjacentIds.class, 2);
        opScores.put(GetAllElements.class, 3);
        final IfScoreResolver resolver = new IfScoreResolver();
        final DefaultScoreResolver defaultResolver = new DefaultScoreResolver(opScores);
        // When
        final int score = resolver.getScore(operation, defaultResolver);
        // Then
        Assert.assertEquals(7, score);
    }

    @Test
    public void shouldThrowErrorWhenNoDefaultResolverConfigured() {
        // Given
        final IfScoreResolver resolver = new IfScoreResolver();
        final If operation = new If.Builder<>().conditional(new Conditional()).then(new GetAllElements()).build();
        // When / Then
        try {
            resolver.getScore(operation);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertTrue(e.getMessage().contains("Default Score Resolver has not been provided."));
        }
    }
}

