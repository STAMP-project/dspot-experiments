/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.store.operation.handler.output;


import com.google.common.collect.Sets;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.output.ToSet;
import uk.gov.gchq.gaffer.store.Context;


public class ToSetHandlerTest {
    @Test
    public void shouldConvertIterableToSet() throws OperationException {
        // Given
        final Iterable originalResults = new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterable(Arrays.asList(1, 2, 2, 2, 3, 4, 1, 5, 6, 7, 8, 5, 9, 1, 6, 8, 2, 10));
        final ToSetHandler<Integer> handler = new ToSetHandler();
        final ToSet<Integer> operation = Mockito.mock(ToSet.class);
        BDDMockito.given(operation.getInput()).willReturn(originalResults);
        // When
        final Iterable<Integer> results = handler.doOperation(operation, new Context(), null);
        // Then
        Assert.assertEquals(Sets.newHashSet(originalResults), Sets.newHashSet(results));
    }

    @Test
    public void shouldConvertIterableToSetAndMaintainOrder() throws OperationException {
        // Given
        final Iterable originalResults = new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterable(Arrays.asList(10, 9, 8, 10, 7, 8, 7, 6, 6, 5, 6, 9, 4, 5, 3, 4, 2, 2, 2, 1, 1));
        final ToSetHandler<Integer> handler = new ToSetHandler();
        final ToSet<Integer> operation = Mockito.mock(ToSet.class);
        BDDMockito.given(operation.getInput()).willReturn(originalResults);
        // When
        final Iterable<Integer> results = handler.doOperation(operation, new Context(), null);
        // Then
        Assert.assertEquals(Sets.newHashSet(10, 9, 8, 7, 6, 5, 4, 3, 2, 1), Sets.newHashSet(results));
    }

    @Test
    public void shouldHandleNullInput() throws OperationException {
        // Given
        final ToSetHandler<Integer> handler = new ToSetHandler();
        final ToSet<Integer> operation = Mockito.mock(ToSet.class);
        BDDMockito.given(operation.getInput()).willReturn(null);
        // When
        final Iterable<Integer> results = handler.doOperation(operation, new Context(), null);
        // Then
        MatcherAssert.assertThat(results, Is.is(Matchers.nullValue()));
    }
}

