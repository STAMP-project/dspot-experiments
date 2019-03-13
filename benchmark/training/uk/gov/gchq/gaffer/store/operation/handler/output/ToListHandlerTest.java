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
package uk.gov.gchq.gaffer.store.operation.handler.output;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.output.ToList;
import uk.gov.gchq.gaffer.store.Context;


public class ToListHandlerTest {
    @Test
    public void shouldConvertIterableToList() throws OperationException {
        // Given
        final List<Integer> originalList = Arrays.asList(1, 2, 3);
        final Iterable originalResults = new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterable(originalList);
        final ToListHandler handler = new ToListHandler();
        final ToList<Integer> operation = Mockito.mock(ToList.class);
        BDDMockito.given(operation.getInput()).willReturn(originalResults);
        // When
        final Iterable<Integer> results = handler.doOperation(operation, new Context(), null);
        // Then
        Assert.assertEquals(originalList, results);
    }

    @Test
    public void shouldHandleNullInput() throws OperationException {
        // Given
        final ToListHandler handler = new ToListHandler();
        final ToList<Integer> operation = Mockito.mock(ToList.class);
        BDDMockito.given(operation.getInput()).willReturn(null);
        // When
        final Iterable<Integer> results = handler.doOperation(operation, new Context(), null);
        // Then
        Assert.assertThat(results, Is.is(Matchers.nullValue()));
    }
}

