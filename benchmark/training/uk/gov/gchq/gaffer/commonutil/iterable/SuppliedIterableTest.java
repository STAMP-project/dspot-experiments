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


import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


public class SuppliedIterableTest {
    @Test
    public void shouldRequestNewIterableFromSupplierWhenIteratorInvoked() {
        // Given
        final Supplier<Iterable<Integer>> supplier = Mockito.mock(Supplier.class);
        final Iterable<Integer> iterable1 = Arrays.asList(1, 2, 3);
        final Iterable<Integer> iterable2 = Arrays.asList(4, 5, 6);
        BDDMockito.given(supplier.get()).willReturn(iterable1, iterable2);
        final SuppliedIterable<Integer> suppliedItr = new SuppliedIterable(supplier);
        // When 1
        final CloseableIterator<Integer> result1 = suppliedItr.iterator();
        final CloseableIterator<Integer> result2 = suppliedItr.iterator();
        // Then 2
        Mockito.verify(supplier, Mockito.times(2)).get();
        Assert.assertEquals(iterable1, Lists.newArrayList(result1));
        Assert.assertEquals(iterable2, Lists.newArrayList(result2));
    }

    @Test
    public void shouldCloseIterables() {
        // Given
        final Supplier<Iterable<Integer>> supplier = Mockito.mock(Supplier.class);
        final CloseableIterable<Integer> iterable1 = Mockito.mock(CloseableIterable.class);
        final CloseableIterable<Integer> iterable2 = Mockito.mock(CloseableIterable.class);
        final CloseableIterator<Integer> iterator1 = Mockito.mock(CloseableIterator.class);
        final CloseableIterator<Integer> iterator2 = Mockito.mock(CloseableIterator.class);
        BDDMockito.given(iterable1.iterator()).willReturn(iterator1);
        BDDMockito.given(iterable2.iterator()).willReturn(iterator2);
        BDDMockito.given(supplier.get()).willReturn(iterable1, iterable2);
        final SuppliedIterable<Integer> suppliedIter = new SuppliedIterable(supplier);
        suppliedIter.iterator();
        suppliedIter.iterator();
        // When 1
        suppliedIter.close();
        // Then 2
        Mockito.verify(iterable1).close();
        Mockito.verify(iterable2).close();
    }
}

