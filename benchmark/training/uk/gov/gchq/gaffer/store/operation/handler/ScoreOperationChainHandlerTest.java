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
package uk.gov.gchq.gaffer.store.operation.handler;


import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.named.operation.NamedOperation;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.If;
import uk.gov.gchq.gaffer.operation.impl.Limit;
import uk.gov.gchq.gaffer.operation.impl.ScoreOperationChain;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.function.Transform;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.operation.declaration.OperationDeclarations;
import uk.gov.gchq.gaffer.store.operation.resolver.DefaultScoreResolver;
import uk.gov.gchq.gaffer.store.operation.resolver.IfScoreResolver;
import uk.gov.gchq.gaffer.store.operation.resolver.ScoreResolver;
import uk.gov.gchq.gaffer.store.operation.resolver.named.NamedOperationScoreResolver;
import uk.gov.gchq.gaffer.user.User;


public class ScoreOperationChainHandlerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldLoadFromScoreOperationChainDeclarationFile() throws SerialisationException {
        final InputStream s = StreamUtil.openStream(getClass(), "TestScoreOperationChainDeclaration.json");
        final OperationDeclarations deserialised = JSONSerialiser.deserialise(s, OperationDeclarations.class);
        Assert.assertEquals(1, deserialised.getOperations().size());
        assert (deserialised.getOperations().get(0).getHandler()) instanceof ScoreOperationChainHandler;
    }

    @Test
    public void shouldExecuteScoreChainOperation() throws OperationException {
        // Given
        final ScoreOperationChainHandler operationHandler = new ScoreOperationChainHandler();
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        final ScoreOperationChain scoreOperationChain = Mockito.mock(ScoreOperationChain.class);
        StoreProperties storeProperties = new StoreProperties();
        final GetAdjacentIds op1 = Mockito.mock(GetAdjacentIds.class);
        final GetElements op2 = Mockito.mock(GetElements.class);
        final OperationChain opChain = new OperationChain(Arrays.asList(op1, op2));
        final Integer expectedResult = 2;
        BDDMockito.given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        BDDMockito.given(user.getOpAuths()).willReturn(opAuths);
        BDDMockito.given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        BDDMockito.given(store.getProperties()).willReturn(storeProperties);
        // When
        final Object result = operationHandler.doOperation(new ScoreOperationChain.Builder().operationChain(opChain).build(), context, store);
        // Then
        Assert.assertSame(expectedResult, result);
    }

    @Test
    public void shouldExecuteScoreChainOperationForNestedOperationChain() throws OperationException {
        // Given
        final ScoreOperationChainHandler operationHandler = new ScoreOperationChainHandler();
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        final ScoreOperationChain scoreOperationChain = Mockito.mock(ScoreOperationChain.class);
        StoreProperties storeProperties = new StoreProperties();
        final GetAdjacentIds op1 = Mockito.mock(GetAdjacentIds.class);
        final GetElements op2 = Mockito.mock(GetElements.class);
        final Limit op3 = Mockito.mock(Limit.class);
        final OperationChain opChain1 = new OperationChain(Arrays.asList(op1, op2));
        final OperationChain opChain = new OperationChain(Arrays.asList(opChain1, op3));
        final Integer expectedResult = 3;
        BDDMockito.given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        BDDMockito.given(user.getOpAuths()).willReturn(opAuths);
        BDDMockito.given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        BDDMockito.given(store.getProperties()).willReturn(storeProperties);
        // When
        final Object result = operationHandler.doOperation(new ScoreOperationChain.Builder().operationChain(opChain).build(), context, store);
        // Then
        Assert.assertSame(expectedResult, result);
    }

    @Test
    public void shouldExecuteScoreOperationChainContainingNamedOperation() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();
        final ScoreResolver scoreResolver = Mockito.mock(NamedOperationScoreResolver.class);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        final ScoreOperationChain scoreOperationChain = Mockito.mock(ScoreOperationChain.class);
        final StoreProperties storeProperties = new StoreProperties();
        final GetAdjacentIds op1 = Mockito.mock(GetAdjacentIds.class);
        final GetElements op2 = Mockito.mock(GetElements.class);
        final Limit op3 = Mockito.mock(Limit.class);
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(GetAdjacentIds.class, 2);
        opScores.put(GetElements.class, 1);
        opScores.put(Limit.class, 1);
        handler.setOpScores(opScores);
        final String opName = "basicOp";
        final NamedOperation<Iterable<? extends Element>, Iterable<? extends Element>> namedOp = Mockito.mock(NamedOperation.class);
        namedOp.setOperationName(opName);
        resolvers.put(namedOp.getClass(), scoreResolver);
        handler.setScoreResolvers(resolvers);
        BDDMockito.given(scoreResolver.getScore(ArgumentMatchers.eq(namedOp), ArgumentMatchers.any())).willReturn(3);
        final OperationChain opChain = new OperationChain(Arrays.asList(op1, op2, op3, namedOp));
        BDDMockito.given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        BDDMockito.given(user.getOpAuths()).willReturn(opAuths);
        BDDMockito.given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        BDDMockito.given(store.getProperties()).willReturn(storeProperties);
        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder().operationChain(opChain).build(), context, store);
        // Then
        Assert.assertEquals(7, result);
    }

    @Test
    public void shouldCorrectlyExecuteScoreOperationChainWhenNamedOperationScoreIsNull() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();
        final ScoreResolver scoreResolver = Mockito.mock(NamedOperationScoreResolver.class);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        final ScoreOperationChain scoreOperationChain = Mockito.mock(ScoreOperationChain.class);
        final StoreProperties storeProperties = new StoreProperties();
        final GetAdjacentIds op1 = Mockito.mock(GetAdjacentIds.class);
        final GetElements op2 = Mockito.mock(GetElements.class);
        final Limit op3 = Mockito.mock(Limit.class);
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(GetAdjacentIds.class, 3);
        opScores.put(GetElements.class, 2);
        opScores.put(Limit.class, 1);
        handler.setOpScores(opScores);
        final String opName = "basicOp";
        final NamedOperation<Iterable<? extends Element>, Iterable<? extends Element>> namedOp = Mockito.mock(NamedOperation.class);
        namedOp.setOperationName(opName);
        resolvers.put(namedOp.getClass(), scoreResolver);
        handler.setScoreResolvers(resolvers);
        BDDMockito.given(scoreResolver.getScore(ArgumentMatchers.eq(namedOp), ArgumentMatchers.any())).willReturn(null);
        final OperationChain opChain = new OperationChain(Arrays.asList(op1, op2, op3, namedOp));
        BDDMockito.given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        BDDMockito.given(user.getOpAuths()).willReturn(opAuths);
        BDDMockito.given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        BDDMockito.given(store.getProperties()).willReturn(storeProperties);
        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder().operationChain(opChain).build(), context, store);
        // Then
        Assert.assertEquals(7, result);
    }

    @Test
    public void shouldResolveScoreOperationChainWithMultipleScoreResolvers() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();
        final ScoreResolver scoreResolver = Mockito.mock(NamedOperationScoreResolver.class);
        final ScoreResolver scoreResolver1 = Mockito.mock(DefaultScoreResolver.class);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        final ScoreOperationChain scoreOperationChain = Mockito.mock(ScoreOperationChain.class);
        final StoreProperties storeProperties = Mockito.mock(StoreProperties.class);
        final GetAdjacentIds op1 = new GetAdjacentIds();
        final AddElements op2 = new AddElements();
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(GetAdjacentIds.class, 2);
        handler.setOpScores(opScores);
        final String opName = "namedOp";
        final NamedOperation<Iterable<? extends Element>, Iterable<? extends Element>> namedOp = Mockito.mock(NamedOperation.class);
        namedOp.setOperationName(opName);
        resolvers.put(namedOp.getClass(), scoreResolver);
        resolvers.put(op2.getClass(), scoreResolver1);
        handler.setScoreResolvers(resolvers);
        BDDMockito.given(scoreResolver.getScore(ArgumentMatchers.eq(namedOp), ArgumentMatchers.any())).willReturn(3);
        BDDMockito.given(scoreResolver1.getScore(ArgumentMatchers.eq(op2), ArgumentMatchers.any())).willReturn(5);
        final OperationChain opChain = new OperationChain(Arrays.asList(op1, op2, namedOp));
        BDDMockito.given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        BDDMockito.given(user.getOpAuths()).willReturn(opAuths);
        BDDMockito.given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        BDDMockito.given(store.getProperties()).willReturn(storeProperties);
        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder().operationChain(opChain).build(), context, store);
        // Then
        Assert.assertEquals(10, result);
    }

    @Test
    public void shouldCorrectlyResolveScoreForNullListOfOperations() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();
        handler.setScoreResolvers(resolvers);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        final ScoreOperationChain scoreOperationChain = Mockito.mock(ScoreOperationChain.class);
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        final List<? extends Operation> opList = null;
        final OperationChain opChain = new OperationChain(opList);
        BDDMockito.given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        BDDMockito.given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        BDDMockito.given(user.getOpAuths()).willReturn(opAuths);
        BDDMockito.given(store.getProperties()).willReturn(properties);
        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder().operationChain(opChain).build(), context, store);
        // Then
        Assert.assertEquals(0, result);
    }

    @Test
    public void shouldCorrectlyResolveScoreForNestedOperationWithNullOperationList() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();
        final ScoreResolver scoreResolver = Mockito.mock(NamedOperationScoreResolver.class);
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        final ScoreOperationChain scoreOperationChain = Mockito.mock(ScoreOperationChain.class);
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        final GetAllElements op1 = Mockito.mock(GetAllElements.class);
        final Transform op2 = Mockito.mock(Transform.class);
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(GetAllElements.class, 2);
        opScores.put(Transform.class, 1);
        handler.setOpScores(opScores);
        final String opName = "namedOp";
        final NamedOperation<Iterable<? extends Element>, Iterable<? extends Element>> namedOp = Mockito.mock(NamedOperation.class);
        namedOp.setOperationName(opName);
        resolvers.put(namedOp.getClass(), scoreResolver);
        handler.setScoreResolvers(resolvers);
        BDDMockito.given(scoreResolver.getScore(ArgumentMatchers.eq(namedOp), ArgumentMatchers.any())).willReturn(3);
        final List<? extends Operation> opList = null;
        final OperationChain nestedOpChain = new OperationChain(opList);
        final OperationChain opChain = new OperationChain(Arrays.asList(op1, op2, nestedOpChain, namedOp));
        BDDMockito.given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        BDDMockito.given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        BDDMockito.given(user.getOpAuths()).willReturn(opAuths);
        BDDMockito.given(store.getProperties()).willReturn(properties);
        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder().operationChain(opChain).build(), context, store);
        // Then
        Assert.assertEquals(6, result);
    }

    @Test
    public void shouldReturnZeroForANullOperationChain() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Context context = Mockito.mock(Context.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        final ScoreOperationChain scoreOperationChain = Mockito.mock(ScoreOperationChain.class);
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        final OperationChain opChain = null;
        BDDMockito.given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        BDDMockito.given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        BDDMockito.given(user.getOpAuths()).willReturn(opAuths);
        BDDMockito.given(store.getProperties()).willReturn(properties);
        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder().operationChain(opChain).build(), context, store);
        // Then
        Assert.assertEquals(0, result);
    }

    @Test
    public void shouldSetAndGetAuthScores() {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<String, Integer> authScores = new HashMap<>();
        authScores.put("auth1", 1);
        authScores.put("auth2", 2);
        authScores.put("auth3", 3);
        // When
        handler.setAuthScores(authScores);
        final Map<String, Integer> result = handler.getAuthScores();
        // Then
        Assert.assertEquals(authScores, result);
    }

    @Test
    public void shouldSetAndGetOpScores() {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final LinkedHashMap<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(GetElements.class, 2);
        opScores.put(GetAllElements.class, 3);
        // When
        handler.setOpScores(opScores);
        final Map<Class<? extends Operation>, Integer> result = handler.getOpScores();
        // Then
        Assert.assertEquals(opScores, result);
    }

    @Test
    public void shouldSetAndGetOpScoresAsStrings() throws ClassNotFoundException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final LinkedHashMap<String, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class.getName(), 1);
        opScores.put(GetElements.class.getName(), 2);
        opScores.put(GetAllElements.class.getName(), 3);
        // When
        handler.setOpScoresFromStrings(opScores);
        final Map<String, Integer> result = handler.getOpScoresAsStrings();
        // Then
        Assert.assertEquals(opScores, result);
    }

    @Test
    public void shouldPassValidationOfOperationScores() throws ClassNotFoundException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final LinkedHashMap<String, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class.getName(), 1);
        opScores.put(GetElements.class.getName(), 2);
        opScores.put(GetAllElements.class.getName(), 3);
        // When
        handler.setOpScoresFromStrings(opScores);
        // Then - no exceptions
    }

    @Test
    public void shouldFailValidationOfOperationScores() throws ClassNotFoundException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final LinkedHashMap<String, Integer> opScores = new LinkedHashMap<>();
        opScores.put(GetElements.class.getName(), 2);
        opScores.put(GetAllElements.class.getName(), 3);
        opScores.put(Operation.class.getName(), 1);
        // When / Then
        try {
            handler.setOpScoresFromStrings(opScores);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Operation scores are configured incorrectly."));
        }
    }

    @Test
    public void shouldAddDefaultScoreResolvers() {
        // Given
        final Map<Class<? extends Operation>, ScoreResolver> defaultResolvers = ScoreOperationChainHandler.getDefaultScoreResolvers();
        // When / Then
        Assert.assertTrue(defaultResolvers.keySet().contains(NamedOperation.class));
        Assert.assertNotNull(defaultResolvers.get(NamedOperation.class));
        Assert.assertTrue(((defaultResolvers.get(NamedOperation.class)) instanceof NamedOperationScoreResolver));
    }

    @Test
    public void shouldReAddDefaultScoreResolversWhenCallingSetMethod() {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> DEFAULT_RESOLVERS = ScoreOperationChainHandler.getDefaultScoreResolvers();
        final Map<Class<? extends Operation>, ScoreResolver> expectedMap = new HashMap<>();
        expectedMap.putAll(DEFAULT_RESOLVERS);
        final Map<Class<? extends Operation>, ScoreResolver> inputMap = new HashMap<>();
        inputMap.put(GetElements.class, new DefaultScoreResolver(null));
        inputMap.put(GetAllElements.class, new DefaultScoreResolver(null));
        expectedMap.putAll(inputMap);
        // When
        handler.setScoreResolvers(inputMap);
        final Map<Class<? extends Operation>, ScoreResolver> results = handler.getScoreResolvers();
        // Then
        Assert.assertEquals(expectedMap.keySet(), results.keySet());
        Assert.assertTrue(((results.get(NamedOperation.class)) instanceof NamedOperationScoreResolver));
        Assert.assertTrue(((results.get(If.class)) instanceof IfScoreResolver));
        Assert.assertEquals(expectedMap.size(), results.size());
    }
}

