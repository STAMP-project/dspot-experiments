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
package uk.gov.gchq.gaffer.store.operation.resolver.named;


import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.named.operation.NamedOperation;
import uk.gov.gchq.gaffer.named.operation.NamedOperationDetail;
import uk.gov.gchq.gaffer.named.operation.cache.exception.CacheOperationFailedException;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.store.operation.handler.named.cache.NamedOperationCache;
import uk.gov.gchq.gaffer.store.operation.resolver.DefaultScoreResolver;
import uk.gov.gchq.gaffer.store.operation.resolver.ScoreResolver;


public class NamedOperationScoreResolverTest {
    @Test
    public void shouldGetScore() throws CacheOperationFailedException {
        final Integer expectedScore = 5;
        final String opName = "otherOp";
        final NamedOperation<Element, Iterable<? extends Element>> namedOp = Mockito.mock(NamedOperation.class);
        namedOp.setOperationName(opName);
        final NamedOperationDetail namedOpDetail = Mockito.mock(NamedOperationDetail.class);
        final NamedOperationCache cache = Mockito.mock(NamedOperationCache.class);
        final NamedOperationScoreResolver resolver = new NamedOperationScoreResolver(cache);
        BDDMockito.given(cache.getFromCache(namedOpDetail.getOperationName())).willReturn(namedOpDetail);
        BDDMockito.given(namedOpDetail.getOperationName()).willReturn(opName);
        BDDMockito.given(namedOpDetail.getScore()).willReturn(5);
        final Integer result = resolver.getScore(namedOp);
        Assert.assertEquals(expectedScore, result);
    }

    @Test
    public void shouldGetScoreFromOperationsInParameters() throws CacheOperationFailedException {
        // Given
        final Integer expectedScore = 8;
        final String opName = "otherOp";
        final NamedOperation<Element, Iterable<? extends Element>> namedOp = Mockito.mock(NamedOperation.class);
        namedOp.setOperationName(opName);
        Operation operation = new GetAllElements();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("test param", operation);
        namedOp.setParameters(paramMap);
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(GetAllElements.class, 3);
        final ScoreResolver scoreResolver = new DefaultScoreResolver(opScores);
        final NamedOperationDetail namedOpDetail = Mockito.mock(NamedOperationDetail.class);
        final NamedOperationCache cache = Mockito.mock(NamedOperationCache.class);
        final NamedOperationScoreResolver resolver = new NamedOperationScoreResolver(cache);
        BDDMockito.given(cache.getFromCache(namedOpDetail.getOperationName())).willReturn(namedOpDetail);
        BDDMockito.given(namedOpDetail.getOperationName()).willReturn(opName);
        BDDMockito.given(namedOpDetail.getScore()).willReturn(5);
        final List<Operation> operations = new ArrayList<>();
        operations.add(operation);
        BDDMockito.given(namedOp.getOperations()).willReturn(operations);
        // When
        final Integer result = resolver.getScore(namedOp, scoreResolver);
        // Then
        Assert.assertEquals(expectedScore, result);
    }

    @Test
    public void shouldDefaultScoreFromOpChainIfNamedOpScoreEmpty() throws CacheOperationFailedException {
        // Given
        final String namedOpName = "namedOp";
        final Map parametersMap = new HashMap<>();
        final Integer expectedScore = 7;
        final NamedOperation<Element, Iterable<? extends Element>> namedOp = Mockito.mock(NamedOperation.class);
        final NamedOperationCache cache = Mockito.mock(NamedOperationCache.class);
        final ScoreResolver defaultScoreResolver = Mockito.mock(DefaultScoreResolver.class);
        final NamedOperationDetail namedOpDetail = Mockito.mock(NamedOperationDetail.class);
        final NamedOperationScoreResolver resolver = new NamedOperationScoreResolver(cache);
        final OperationChain opChain = new OperationChain();
        namedOp.setOperationName(namedOpName);
        namedOp.setParameters(parametersMap);
        BDDMockito.given(namedOpDetail.getOperationChain(parametersMap)).willReturn(opChain);
        BDDMockito.given(namedOpDetail.getScore()).willReturn(null);
        BDDMockito.given(cache.getFromCache(namedOpDetail.getOperationName())).willReturn(namedOpDetail);
        BDDMockito.given(defaultScoreResolver.getScore(opChain)).willReturn(7);
        // When
        final Integer result = resolver.getScore(namedOp, defaultScoreResolver);
        // Then
        Assert.assertEquals(expectedScore, result);
    }

    @Test
    public void shouldCatchExceptionForCacheFailures() {
        final NamedOperation<Element, Iterable<? extends Element>> namedOp = Mockito.mock(NamedOperation.class);
        final NamedOperationScoreResolver resolver = new NamedOperationScoreResolver();
        final Integer result = resolver.getScore(namedOp);
        Assert.assertNull(result);
    }
}

