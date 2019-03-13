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


import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.commonutil.iterable.LimitedCloseableIterable;
import uk.gov.gchq.gaffer.operation.impl.Limit;


public class LimitHandlerTest {
    @Test
    public void shouldLimitResults() throws Exception {
        // Given
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> expectedResult = Arrays.asList(1, 2, 3);
        final Integer resultLimit = expectedResult.size();
        final Limit<Integer> limit = new Limit.Builder<Integer>().input(input).resultLimit(resultLimit).build();
        final LimitHandler<Integer> handler = new LimitHandler();
        // When
        final Iterable<? extends Integer> result = handler.doOperation(limit, null, null);
        // Then
        Assert.assertTrue((result instanceof LimitedCloseableIterable));
        Assert.assertEquals(0, getStart());
        Assert.assertEquals(resultLimit, getEnd());
        Assert.assertEquals(expectedResult, Lists.newArrayList(result));
    }

    @Test
    public void shouldNotLimitResultsOfGetOperationWhenLimitIsNull() throws Exception {
        // Given
        final CloseableIterable<Integer> input = new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterable(Arrays.asList(1, 2, 3, 4, 5));
        final Integer resultLimit = null;
        final Limit<Integer> limit = new Limit.Builder<Integer>().input(input).resultLimit(resultLimit).build();
        final LimitHandler<Integer> handler = new LimitHandler();
        // When
        final Iterable<? extends Integer> result = handler.doOperation(limit, null, null);
        // Then
        Assert.assertSame(input, result);
    }

    @Test
    public void shouldHandleNullInput() throws Exception {
        // Given
        final CloseableIterable<Integer> input = null;
        final Limit<Integer> limit = new Limit.Builder<Integer>().input(input).build();
        final LimitHandler<Integer> handler = new LimitHandler();
        // When
        final Iterable<? extends Integer> result = handler.doOperation(limit, null, null);
        // Then
        Assert.assertThat(result, Is.is(CoreMatchers.nullValue()));
    }
}

