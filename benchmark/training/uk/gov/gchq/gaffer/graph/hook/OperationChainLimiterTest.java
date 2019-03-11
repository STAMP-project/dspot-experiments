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


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.exception.UnauthorisedException;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.user.User;


public class OperationChainLimiterTest extends GraphHookTest<OperationChainLimiter> {
    private static final String OP_CHAIN_LIMITER_PATH = "opChainLimiter.json";

    public OperationChainLimiterTest() {
        super(OperationChainLimiter.class);
    }

    @Test
    public void shouldAcceptOperationChainWhenUserHasAuthScoreGreaterThanChainScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OperationChainLimiterTest.OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder().first(new GetElements()).build();
        final User user = new User.Builder().opAuths("User").build();
        // When
        hook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(user));
        // Then - no exceptions
    }

    @Test
    public void shouldAcceptOperationChainWhenUserHasAuthScoreEqualToChainScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OperationChainLimiterTest.OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder().first(new GetAdjacentIds()).then(new GetElements()).then(new uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects()).build();
        final User user = new User.Builder().opAuths("User").build();
        // When
        hook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(user));
        // Then - no exceptions
    }

    @Test
    public void shouldRejectOperationChainWhenUserHasAuthScoreLessThanChainScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OperationChainLimiterTest.OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder().first(new GetAdjacentIds()).then(new GetAdjacentIds()).then(new GetAdjacentIds()).then(new GetElements()).then(new uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects()).build();
        final User user = new User.Builder().opAuths("User").build();
        // When/Then
        try {
            hook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(user));
            Assert.fail("Exception expected");
        } catch (final UnauthorisedException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldAcceptOperationChainWhenUserHasMaxAuthScoreGreaterThanChainScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OperationChainLimiterTest.OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder().first(new GetAdjacentIds()).then(new GetAdjacentIds()).then(new GetElements()).then(new uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects()).build();
        final User user = new User.Builder().opAuths("SuperUser", "User").build();
        // When
        hook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(user));
        // Then - no exceptions
    }

    @Test
    public void shouldRejectOperationChainWhenUserHasMaxAuthScoreLessThanChainScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OperationChainLimiterTest.OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder().first(new GetAllElements()).then(new GetElements()).then(new uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects()).build();
        final User user = new User.Builder().opAuths("SuperUser", "User").build();
        // When/Then
        try {
            hook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(user));
            Assert.fail("Exception expected");
        } catch (final UnauthorisedException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldRejectOperationChainWhenUserHasNoAuthWithAConfiguredScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OperationChainLimiterTest.OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder().first(new GetElements()).build();
        final User user = new User.Builder().opAuths("NoScore").build();
        // When/Then
        try {
            hook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(user));
            Assert.fail("Exception expected");
        } catch (final UnauthorisedException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldReturnResultWithoutModification() {
        // Given
        final OperationChainLimiter hook = fromJson(OperationChainLimiterTest.OP_CHAIN_LIMITER_PATH);
        final Object result = Mockito.mock(Object.class);
        final OperationChain opChain = new OperationChain.Builder().first(new uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects()).build();
        final User user = new User.Builder().opAuths("NoScore").build();
        // When
        final Object returnedResult = hook.postExecute(result, opChain, new uk.gov.gchq.gaffer.store.Context(user));
        // Then
        Assert.assertSame(result, returnedResult);
    }

    @Test
    public void shouldSetAndGetAuthScores() {
        // Given
        final OperationChainLimiter hook = new OperationChainLimiter();
        final Map<String, Integer> authScores = new HashMap<>();
        authScores.put("auth1", 1);
        authScores.put("auth2", 2);
        authScores.put("auth3", 3);
        // When
        hook.setAuthScores(authScores);
        final Map<String, Integer> result = hook.getAuthScores();
        // Then
        Assert.assertEquals(authScores, result);
    }

    @Test
    public void shouldSetAndGetOpScores() {
        // Given
        final OperationChainLimiter hook = new OperationChainLimiter();
        final LinkedHashMap<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(GetElements.class, 2);
        opScores.put(GetAllElements.class, 3);
        // When
        hook.setOpScores(opScores);
        final Map<Class<? extends Operation>, Integer> result = hook.getOpScores();
        // Then
        Assert.assertEquals(opScores, result);
    }

    @Test
    public void shouldSetAndGetOpScoresAsStrings() throws ClassNotFoundException {
        // Given
        final OperationChainLimiter hook = new OperationChainLimiter();
        final LinkedHashMap<String, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class.getName(), 1);
        opScores.put(GetElements.class.getName(), 2);
        opScores.put(GetAllElements.class.getName(), 3);
        // When
        hook.setOpScoresFromStrings(opScores);
        final Map<String, Integer> result = hook.getOpScoresAsStrings();
        // Then
        Assert.assertEquals(opScores, result);
    }
}

