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
package com.karumi.rosie.repository;


import ReadPolicy.READABLE_ONLY;
import com.karumi.rosie.doubles.AnyRepositoryKey;
import com.karumi.rosie.doubles.AnyRepositoryValue;
import com.karumi.rosie.repository.datasource.paginated.Page;
import com.karumi.rosie.repository.datasource.paginated.PaginatedCacheDataSource;
import com.karumi.rosie.repository.datasource.paginated.PaginatedReadableDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PaginatedRosieRepositoryTest {
    private static final int ANY_OFFSET = 0;

    private static final int ANY_LIMIT = 20;

    @Mock
    private PaginatedCacheDataSource<AnyRepositoryKey, AnyRepositoryValue> cacheDataSource;

    @Mock
    private PaginatedReadableDataSource<AnyRepositoryKey, AnyRepositoryValue> readableDataSource;

    @Test
    public void shouldReturnValuesFromCacheDataSourceIfDataIsValid() throws Exception {
        Page page = Page.withOffsetAndLimit(PaginatedRosieRepositoryTest.ANY_OFFSET, PaginatedRosieRepositoryTest.ANY_LIMIT);
        PaginatedCollection<AnyRepositoryValue> cacheValues = givenCacheDataSourceReturnsValidValues(page);
        PaginatedRosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAPaginatedRepository();
        PaginatedCollection<AnyRepositoryValue> values = repository.getPage(page);
        Assert.assertEquals(cacheValues, values);
    }

    @Test
    public void shouldReturnItemsFromReadableDataSourceIfCacheDataSourceHasNoData() throws Exception {
        Page page = Page.withOffsetAndLimit(PaginatedRosieRepositoryTest.ANY_OFFSET, PaginatedRosieRepositoryTest.ANY_LIMIT);
        givenCacheDataSourceReturnsNull(page);
        PaginatedCollection<AnyRepositoryValue> readableValues = givenReadableDataSourceReturnsValues(page);
        PaginatedRosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAPaginatedRepository();
        PaginatedCollection<AnyRepositoryValue> values = repository.getPage(page);
        Assert.assertEquals(readableValues, values);
    }

    @Test
    public void shouldReturnValuesFromReadableDataSourceIfCacheDataSourceIsNotValid() throws Exception {
        Page page = Page.withOffsetAndLimit(PaginatedRosieRepositoryTest.ANY_OFFSET, PaginatedRosieRepositoryTest.ANY_LIMIT);
        givenCacheDataSourceReturnsNonValidValues(page);
        PaginatedCollection<AnyRepositoryValue> readableValues = givenReadableDataSourceReturnsValues(page);
        PaginatedRosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAPaginatedRepository();
        PaginatedCollection<AnyRepositoryValue> values = repository.getPage(page);
        Assert.assertEquals(readableValues, values);
    }

    @Test
    public void shouldReturnValuesFromReadableDataSourceIfCacheDoNotHaveThisPage() throws Exception {
        Page page = Page.withOffsetAndLimit(PaginatedRosieRepositoryTest.ANY_OFFSET, PaginatedRosieRepositoryTest.ANY_LIMIT);
        givenCacheDataSourceReturnsNonValidValues(page);
        givenReadableDataSourceReturnsValues(page);
        PaginatedRosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAPaginatedRepository();
        repository.getPage(page);
        Page nextPage = Page.withOffsetAndLimit(((PaginatedRosieRepositoryTest.ANY_OFFSET) + (PaginatedRosieRepositoryTest.ANY_LIMIT)), PaginatedRosieRepositoryTest.ANY_LIMIT);
        repository.getPage(nextPage);
        Mockito.verify(cacheDataSource).getPage(page);
        Mockito.verify(readableDataSource).getPage(page);
        Mockito.verify(cacheDataSource).getPage(nextPage);
        Mockito.verify(readableDataSource).getPage(nextPage);
    }

    @Test
    public void shouldNotRemoveDataFromCacheIfCacheDoNotHaveThisPage() throws Exception {
        Page page = Page.withOffsetAndLimit(PaginatedRosieRepositoryTest.ANY_OFFSET, PaginatedRosieRepositoryTest.ANY_LIMIT);
        givenCacheDataSourceReturnsNonValidValues(page);
        givenReadableDataSourceReturnsValues(page);
        PaginatedRosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAPaginatedRepository();
        repository.getPage(page);
        Page nextPage = Page.withOffsetAndLimit(((PaginatedRosieRepositoryTest.ANY_OFFSET) + (PaginatedRosieRepositoryTest.ANY_LIMIT)), PaginatedRosieRepositoryTest.ANY_LIMIT);
        repository.getPage(nextPage);
        Mockito.verify(cacheDataSource, Mockito.times(1)).deleteAll();
    }

    @Test
    public void shouldReturnValuesFromReadableDataSourceIfPolicyForcesOnlyReadable() throws Exception {
        Page page = Page.withOffsetAndLimit(PaginatedRosieRepositoryTest.ANY_OFFSET, PaginatedRosieRepositoryTest.ANY_LIMIT);
        givenCacheDataSourceReturnsValidValues(page);
        PaginatedCollection<AnyRepositoryValue> readableValues = givenReadableDataSourceReturnsValues(page);
        PaginatedRosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAPaginatedRepository();
        PaginatedCollection<AnyRepositoryValue> values = repository.getPage(page, READABLE_ONLY);
        Assert.assertEquals(readableValues, values);
    }

    @Test
    public void shouldPopulateCacheDataSourceAfterGetPageFromReadableDataSource() throws Exception {
        Page page = Page.withOffsetAndLimit(PaginatedRosieRepositoryTest.ANY_OFFSET, PaginatedRosieRepositoryTest.ANY_LIMIT);
        givenCacheDataSourceReturnsNull(page);
        PaginatedCollection<AnyRepositoryValue> readableValues = givenReadableDataSourceReturnsValues(page);
        PaginatedRosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAPaginatedRepository();
        repository.getPage(page);
        Mockito.verify(cacheDataSource).addOrUpdatePage(page, readableValues.getItems(), true);
    }

    @Test
    public void shouldDeleteCacheDataIfItemsAreNotValid() throws Exception {
        Page page = Page.withOffsetAndLimit(PaginatedRosieRepositoryTest.ANY_OFFSET, PaginatedRosieRepositoryTest.ANY_LIMIT);
        givenCacheDataSourceReturnsNonValidValues(page);
        PaginatedRosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAPaginatedRepository();
        repository.getPage(page);
        Mockito.verify(cacheDataSource).deleteAll();
    }
}

