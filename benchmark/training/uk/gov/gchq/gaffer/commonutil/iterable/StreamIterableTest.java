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
package uk.gov.gchq.gaffer.commonutil.iterable;


import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.stream.StreamSupplier;


public class StreamIterableTest {
    @Test
    public void shouldDelegateIteratorToIterable() {
        // Given
        final StreamSupplier<Object> streamSupplier = Mockito.mock(StreamSupplier.class);
        final Stream<Object> stream = Mockito.mock(Stream.class);
        BDDMockito.given(streamSupplier.get()).willReturn(stream);
        final StreamIterable<Object> wrappedIterable = new StreamIterable(streamSupplier);
        final Iterator<Object> iterator = Mockito.mock(Iterator.class);
        BDDMockito.given(stream.iterator()).willReturn(iterator);
        // When
        final CloseableIterator<Object> result = wrappedIterable.iterator();
        // Then - call has next and check it was called on the mock.
        result.hasNext();
        Mockito.verify(iterator).hasNext();
    }

    @Test
    public void shouldDelegateCloseToStreamIterable() throws IOException {
        // Given
        final StreamSupplier<Object> streamSupplier = Mockito.mock(StreamSupplier.class);
        final Stream<Object> stream = Mockito.mock(Stream.class);
        BDDMockito.given(streamSupplier.get()).willReturn(stream);
        final StreamIterable<Object> streamIterable = new StreamIterable(streamSupplier);
        // When
        streamIterable.close();
        // Then
        Mockito.verify(streamSupplier).close();
    }
}

