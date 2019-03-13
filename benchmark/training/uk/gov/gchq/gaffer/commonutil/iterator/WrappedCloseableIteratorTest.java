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
package uk.gov.gchq.gaffer.commonutil.iterator;


import java.util.Iterator;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterator;
import uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator;


public class WrappedCloseableIteratorTest {
    @Test
    public void shouldDelegateCloseToWrappedIterator() {
        // Given
        final CloseableIterator<Object> closeableIterator = Mockito.mock(CloseableIterator.class);
        final WrappedCloseableIterator<Object> wrappedIterator = new WrappedCloseableIterator(closeableIterator);
        // When
        wrappedIterator.close();
        // Then
        Mockito.verify(closeableIterator).close();
    }

    @Test
    public void shouldDoNothingWhenCloseCalledOnNoncloseableIterator() {
        // Given
        final Iterator<Object> iterator = Mockito.mock(Iterator.class);
        final WrappedCloseableIterator<Object> wrappedIterator = new WrappedCloseableIterator(iterator);
        // When
        wrappedIterator.close();
        // Then - no exception
    }
}

