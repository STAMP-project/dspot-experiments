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


import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.Count;
import uk.gov.gchq.gaffer.operation.impl.Map;
import uk.gov.gchq.gaffer.operation.impl.While;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.impl.output.ToSet;
import uk.gov.gchq.gaffer.operation.util.Conditional;


public class WhileScoreResolverTest {
    @Test
    public void shouldGetDefaultScoreWithNoOperationScores() {
        // Given
        final WhileScoreResolver resolver = new WhileScoreResolver();
        final DefaultScoreResolver defaultResolver = new DefaultScoreResolver(new LinkedHashMap());
        final While operation = new While();
        // When
        final int score = resolver.getScore(operation, defaultResolver);
        // Then
        Assert.assertEquals(0, score);
    }

    @Test
    public void shouldGetScoreWithFullyPopulatedOperation() {
        // Given
        final Object input = new EntitySeed(2);
        final Count count = Mockito.mock(Count.class);
        final int repeats = 5;
        final GetElements getElements = Mockito.mock(GetElements.class);
        final Conditional conditional = Mockito.mock(Conditional.class);
        BDDMockito.given(conditional.getTransform()).willReturn(count);
        final While operation = new While.Builder<>().input(input).conditional(conditional).maxRepeats(repeats).operation(getElements).build();
        final LinkedHashMap<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Count.class, 1);
        opScores.put(GetElements.class, 2);
        final DefaultScoreResolver defaultResolver = new DefaultScoreResolver(opScores);
        final WhileScoreResolver resolver = new WhileScoreResolver();
        // When
        final int score = resolver.getScore(operation, defaultResolver);
        // Then
        Assert.assertEquals(15, score);
    }

    @Test
    public void shouldGetScoreWithOperationChainAsOperation() {
        // Given
        final Object input = new EntitySeed(3);
        final int repeats = 3;
        final GetElements getElements = Mockito.mock(GetElements.class);
        final Map map = Mockito.mock(Map.class);
        final ToSet toSet = Mockito.mock(ToSet.class);
        final OperationChain transformChain = Mockito.mock(OperationChain.class);
        final List<Operation> transformOps = new LinkedList<>();
        transformOps.add(map);
        transformOps.add(toSet);
        BDDMockito.given(transformChain.getOperations()).willReturn(transformOps);
        final Conditional conditional = Mockito.mock(Conditional.class);
        BDDMockito.given(conditional.getTransform()).willReturn(transformChain);
        final While operation = new While.Builder<>().input(input).maxRepeats(repeats).conditional(conditional).operation(getElements).build();
        final LinkedHashMap<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(Map.class, 3);
        opScores.put(ToSet.class, 1);
        opScores.put(GetElements.class, 2);
        final WhileScoreResolver resolver = new WhileScoreResolver();
        final DefaultScoreResolver defaultResolver = new DefaultScoreResolver(opScores);
        // When
        final int score = resolver.getScore(operation, defaultResolver);
        // Then
        Assert.assertEquals(18, score);
    }

    @Test
    public void shouldThrowErrorWhenNoDefaultResolverConfigured() {
        // Given
        final WhileScoreResolver resolver = new WhileScoreResolver();
        final While operation = new While.Builder<>().conditional(new Conditional()).operation(new uk.gov.gchq.gaffer.operation.impl.get.GetAllElements()).build();
        // When / Then
        try {
            resolver.getScore(operation);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertTrue(e.getMessage().contains("Default Score Resolver has not been provided."));
        }
    }
}

