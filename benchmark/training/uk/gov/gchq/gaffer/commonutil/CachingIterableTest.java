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
package uk.gov.gchq.gaffer.commonutil;


import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.iterable.CachingIterable;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterator;


public class CachingIterableTest {
    private static final List<Integer> SMALL_LIST = Arrays.asList(0, 1, 2, 3, 4);

    private static final List<Integer> LARGE_LIST = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

    @Test
    public void shouldCacheSmallIterable() {
        // Given
        final CloseableIterable<Integer> iterable = Mockito.mock(CloseableIterable.class);
        BDDMockito.given(iterable.iterator()).willReturn(new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator(CachingIterableTest.SMALL_LIST.iterator()), new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator(CachingIterableTest.SMALL_LIST.iterator()));
        // When
        final CachingIterable<Integer> cachingIterable = new CachingIterable(iterable, 5);
        // Then
        Assert.assertEquals(CachingIterableTest.SMALL_LIST, Lists.newArrayList(cachingIterable));
        Assert.assertEquals(CachingIterableTest.SMALL_LIST, Lists.newArrayList(cachingIterable));
        Mockito.verify(iterable, Mockito.times(1)).iterator();
    }

    @Test
    public void shouldNotCacheALargeIterable() {
        // Given
        final CloseableIterable<Integer> iterable = Mockito.mock(CloseableIterable.class);
        BDDMockito.given(iterable.iterator()).willReturn(new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator(CachingIterableTest.LARGE_LIST.iterator()), new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator(CachingIterableTest.LARGE_LIST.iterator()));
        // When
        final CachingIterable<Integer> cachingIterable = new CachingIterable(iterable, 5);
        // Then
        Assert.assertEquals(CachingIterableTest.LARGE_LIST, Lists.newArrayList(cachingIterable));
        Assert.assertEquals(CachingIterableTest.LARGE_LIST, Lists.newArrayList(cachingIterable));
        Mockito.verify(iterable, Mockito.times(2)).iterator();
    }

    @Test
    public void shouldHandleNullIterable() {
        // When
        final CachingIterable<Integer> cachingIterable = new CachingIterable(null);
        // Then
        Assert.assertEquals(Collections.emptyList(), Lists.newArrayList(cachingIterable));
        Assert.assertEquals(Collections.emptyList(), Lists.newArrayList(cachingIterable));
    }

    @Test
    public void shouldCloseTheIterable() {
        // Given
        final CloseableIterable<Integer> iterable = Mockito.mock(CloseableIterable.class);
        final CachingIterable<Integer> cachingIterable = new CachingIterable(iterable, 5);
        // When
        cachingIterable.close();
        // Then
        Mockito.verify(iterable).close();
    }

    @Test
    public void shouldCloseTheIterableWhenFullyCached() {
        // Given
        final CloseableIterable<Integer> iterable = Mockito.mock(CloseableIterable.class);
        BDDMockito.given(iterable.iterator()).willReturn(new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator(CachingIterableTest.SMALL_LIST.iterator()), new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator(CachingIterableTest.SMALL_LIST.iterator()));
        final CachingIterable<Integer> cachingIterable = new CachingIterable(iterable, 5);
        // When
        Assert.assertEquals(CachingIterableTest.SMALL_LIST, Lists.newArrayList(cachingIterable));
        // Then
        Mockito.verify(iterable).close();
    }

    @Test
    public void shouldHandleMultipleIterators() {
        // Given
        final CloseableIterable<Integer> iterable = Mockito.mock(CloseableIterable.class);
        BDDMockito.given(iterable.iterator()).willReturn(new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator(CachingIterableTest.SMALL_LIST.iterator()), new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator(CachingIterableTest.SMALL_LIST.iterator()), new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator(CachingIterableTest.SMALL_LIST.iterator()), new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator(CachingIterableTest.SMALL_LIST.iterator()), new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterator(CachingIterableTest.SMALL_LIST.iterator()));
        final CachingIterable<Integer> cachingIterable = new CachingIterable(iterable, 5);
        // When / Then
        final CloseableIterator<Integer> itr1 = cachingIterable.iterator();
        itr1.next();
        final CloseableIterator<Integer> itr2 = cachingIterable.iterator();
        itr1.close();
        itr2.next();
        itr1.close();
        final CloseableIterator<Integer> itr3 = cachingIterable.iterator();
        itr3.next();
        final CloseableIterator<Integer> itr4 = cachingIterable.iterator();
        Assert.assertEquals(CachingIterableTest.SMALL_LIST, Lists.newArrayList(itr4));
        // should be cached now as it has been fully read.
        Mockito.verify(iterable, Mockito.times(3)).close();
        itr3.next();
        Mockito.verify(iterable, Mockito.times(4)).iterator();
        Assert.assertEquals(CachingIterableTest.SMALL_LIST, Lists.newArrayList(cachingIterable));
        final CloseableIterator<Integer> itr5 = cachingIterable.iterator();
        Assert.assertEquals(((Integer) (0)), itr5.next());
        Mockito.verify(iterable, Mockito.times(4)).iterator();
    }
}

