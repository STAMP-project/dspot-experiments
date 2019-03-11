/**
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.karumi.rosie.repository.datasource;


import com.karumi.rosie.doubles.AnyRepositoryKey;
import com.karumi.rosie.doubles.AnyRepositoryValue;
import com.karumi.rosie.repository.PaginatedCollection;
import com.karumi.rosie.repository.datasource.paginated.Page;
import com.karumi.rosie.repository.datasource.paginated.PaginatedCacheDataSource;
import com.karumi.rosie.time.TimeProvider;
import java.util.Collection;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class InMemoryPaginatedCacheDataSourceTest {
    private static final long ANY_TTL = 10;

    private static final int ANY_OFFSET = 0;

    private static final int ANY_LIMIT = 20;

    private static final boolean ANY_HAS_MORE = true;

    @Mock
    private TimeProvider timeProvider;

    @Test
    public void shouldAddValuesBasedOnTheOffsetAndLimit() throws Exception {
        Page page = Page.withOffsetAndLimit(InMemoryPaginatedCacheDataSourceTest.ANY_OFFSET, InMemoryPaginatedCacheDataSourceTest.ANY_LIMIT);
        Collection<AnyRepositoryValue> values = givenSomeItems(((InMemoryPaginatedCacheDataSourceTest.ANY_OFFSET) + (InMemoryPaginatedCacheDataSourceTest.ANY_LIMIT)));
        PaginatedCacheDataSource<AnyRepositoryKey, AnyRepositoryValue> cache = givenAnInMemoryPaginatedCacheDataSource();
        cache.addOrUpdatePage(page, values, InMemoryPaginatedCacheDataSourceTest.ANY_HAS_MORE);
        PaginatedCollection<AnyRepositoryValue> paginatedCollection = cache.getPage(page);
        Assert.assertEquals(values, paginatedCollection.getItems());
    }

    @Test
    public void shouldReturnTheRequestedOffset() throws Exception {
        Page page = Page.withOffsetAndLimit(InMemoryPaginatedCacheDataSourceTest.ANY_OFFSET, InMemoryPaginatedCacheDataSourceTest.ANY_LIMIT);
        Collection<AnyRepositoryValue> values = givenSomeItems(((InMemoryPaginatedCacheDataSourceTest.ANY_OFFSET) + (InMemoryPaginatedCacheDataSourceTest.ANY_LIMIT)));
        PaginatedCacheDataSource<AnyRepositoryKey, AnyRepositoryValue> cache = givenAnInMemoryPaginatedCacheDataSource();
        cache.addOrUpdatePage(page, values, InMemoryPaginatedCacheDataSourceTest.ANY_HAS_MORE);
        PaginatedCollection<AnyRepositoryValue> paginatedCollection = cache.getPage(page);
        Assert.assertEquals(InMemoryPaginatedCacheDataSourceTest.ANY_OFFSET, paginatedCollection.getPage().getOffset());
    }

    @Test
    public void shouldReturnRequestedLimit() throws Exception {
        Page page = Page.withOffsetAndLimit(InMemoryPaginatedCacheDataSourceTest.ANY_OFFSET, InMemoryPaginatedCacheDataSourceTest.ANY_LIMIT);
        Collection<AnyRepositoryValue> values = givenSomeItems(((InMemoryPaginatedCacheDataSourceTest.ANY_OFFSET) + (InMemoryPaginatedCacheDataSourceTest.ANY_LIMIT)));
        PaginatedCacheDataSource<AnyRepositoryKey, AnyRepositoryValue> cache = givenAnInMemoryPaginatedCacheDataSource();
        cache.addOrUpdatePage(page, values, InMemoryPaginatedCacheDataSourceTest.ANY_HAS_MORE);
        PaginatedCollection<AnyRepositoryValue> paginatedCollection = cache.getPage(page);
        Assert.assertEquals(InMemoryPaginatedCacheDataSourceTest.ANY_LIMIT, paginatedCollection.getPage().getLimit());
    }

    @Test
    public void shouldReturnHasMoreIfThereAreMoreItemsToLoad() throws Exception {
        Page page = Page.withOffsetAndLimit(InMemoryPaginatedCacheDataSourceTest.ANY_OFFSET, InMemoryPaginatedCacheDataSourceTest.ANY_LIMIT);
        Collection<AnyRepositoryValue> values = givenSomeItems(((InMemoryPaginatedCacheDataSourceTest.ANY_OFFSET) + (InMemoryPaginatedCacheDataSourceTest.ANY_LIMIT)));
        PaginatedCacheDataSource<AnyRepositoryKey, AnyRepositoryValue> cache = givenAnInMemoryPaginatedCacheDataSource();
        cache.addOrUpdatePage(page, values, InMemoryPaginatedCacheDataSourceTest.ANY_HAS_MORE);
        PaginatedCollection<AnyRepositoryValue> paginatedCollection = cache.getPage(page);
        Assert.assertEquals(InMemoryPaginatedCacheDataSourceTest.ANY_HAS_MORE, paginatedCollection.hasMore());
    }
}

