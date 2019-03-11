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
import com.karumi.rosie.repository.datasource.CacheDataSource;
import com.karumi.rosie.repository.datasource.ReadableDataSource;
import com.karumi.rosie.repository.datasource.WriteableDataSource;
import java.util.Collection;
import java.util.EnumSet;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class RosieRepositoryTest {
    private static final AnyRepositoryKey ANY_KEY = new AnyRepositoryKey(42);

    private static final AnyRepositoryValue ANY_VALUE = new AnyRepositoryValue(RosieRepositoryTest.ANY_KEY);

    @Mock
    private ReadableDataSource<AnyRepositoryKey, AnyRepositoryValue> readableDataSource;

    @Mock
    private WriteableDataSource<AnyRepositoryKey, AnyRepositoryValue> writeableDataSource;

    @Mock
    private CacheDataSource<AnyRepositoryKey, AnyRepositoryValue> cacheDataSource;

    @Test
    public void shouldReturnNullIfThereAreNoDataSourcesWithData() throws Exception {
        givenCacheDataSourceReturnsNull();
        givenReadableDataSourceReturnsNull();
        RosieRepository<?, ?> repository = givenAReadableAndCacheRepository();
        Collection<?> values = repository.getAll();
        Assert.assertNull(values);
    }

    @Test
    public void shouldReturnDataFromCacheDataSourceIfDataIsValid() throws Exception {
        Collection<AnyRepositoryValue> cacheValues = givenCacheDataSourceReturnsValidValues();
        givenReadableDataSourceReturnsNull();
        RosieRepository<?, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        Collection<AnyRepositoryValue> values = repository.getAll();
        Assert.assertEquals(cacheValues, values);
    }

    @Test
    public void shouldReturnDataFromReadableDataSourceIfCacheDataSourceReturnsNullOnGetAll() throws Exception {
        givenCacheDataSourceReturnsNull();
        Collection<AnyRepositoryValue> readableValues = givenReadableDataSourceReturnsValidValues();
        RosieRepository<?, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        Collection<AnyRepositoryValue> values = repository.getAll();
        assertEquals(readableValues, values);
    }

    @Test
    public void shouldReturnDataFromReadableDataSourceIfTheCacheDataSourceReturnsNoValidData() throws Exception {
        givenCacheDataSourceReturnsNonValidValues();
        Collection<AnyRepositoryValue> readableValues = givenReadableDataSourceReturnsValidValues();
        RosieRepository<?, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        Collection<AnyRepositoryValue> values = repository.getAll();
        assertEquals(readableValues, values);
    }

    @Test(expected = Exception.class)
    public void shouldPropagateExceptionsThrownByAnyDataSource() throws Exception {
        givenReadableDataSourceThrowsException();
        RosieRepository<?, ?> repository = givenARepository(EnumSet.of(RosieRepositoryTest.DataSource.READABLE));
        repository.getAll();
    }

    @Test
    public void shouldGetDataFromReadableDataSourceIfReadPolicyForcesOnlyReadable() throws Exception {
        givenCacheDataSourceReturnsValidValues();
        givenReadableDataSourceReturnsValidValues();
        RosieRepository<?, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        repository.getAll(READABLE_ONLY);
        Mockito.verify(cacheDataSource, Mockito.never()).getAll();
        Mockito.verify(readableDataSource).getAll();
    }

    @Test
    public void shouldPopulateCacheDataSources() throws Exception {
        givenCacheDataSourceReturnsNull();
        Collection<AnyRepositoryValue> readableValues = givenReadableDataSourceReturnsValidValues();
        RosieRepository<?, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        repository.getAll();
        Mockito.verify(cacheDataSource).addOrUpdateAll(readableValues);
    }

    @Test
    public void shouldDeleteAllFromCacheDataSourceIfDataIsNotValid() throws Exception {
        givenCacheDataSourceReturnsNonValidValues();
        RosieRepository<?, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        repository.getAll();
        Mockito.verify(cacheDataSource).deleteAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullKeys() throws Exception {
        RosieRepository<?, ?> repository = givenAReadableAndCacheRepository();
        repository.getByKey(null);
    }

    @Test
    public void shouldReturnValueByKeyFromCacheDataSource() throws Exception {
        givenReadableDataSourceReturnsNull();
        AnyRepositoryValue cacheValue = givenCacheDataSourceReturnsValidValueWithKey(RosieRepositoryTest.ANY_KEY);
        RosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        AnyRepositoryValue value = repository.getByKey(RosieRepositoryTest.ANY_KEY);
        assertEquals(cacheValue, value);
    }

    @Test
    public void shouldReturnValueFromReadableDataSourceIfCacheDataSourceValueIsNull() throws Exception {
        givenCacheDataSourceReturnsNull();
        AnyRepositoryValue readableValue = givenReadableDataSourceReturnsValidValueWithKey(RosieRepositoryTest.ANY_KEY);
        RosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        AnyRepositoryValue value = repository.getByKey(RosieRepositoryTest.ANY_KEY);
        assertEquals(readableValue, value);
    }

    @Test
    public void shouldReturnItemFromReadableDataSourceIfCacheDataSourceValueIsNotValid() throws Exception {
        givenCacheDataSourceReturnsNonValidValueWithKey(RosieRepositoryTest.ANY_KEY);
        AnyRepositoryValue readableValue = givenReadableDataSourceReturnsValidValueWithKey(RosieRepositoryTest.ANY_KEY);
        RosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        AnyRepositoryValue value = repository.getByKey(RosieRepositoryTest.ANY_KEY);
        assertEquals(readableValue, value);
    }

    @Test
    public void shouldPopulateCacheDataSourceWithValueIfCacheDataSourceIsNotValid() throws Exception {
        givenCacheDataSourceReturnsNonValidValueWithKey(RosieRepositoryTest.ANY_KEY);
        AnyRepositoryValue readableValue = givenReadableDataSourceReturnsValidValueWithKey(RosieRepositoryTest.ANY_KEY);
        RosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        repository.getByKey(RosieRepositoryTest.ANY_KEY);
        Mockito.verify(cacheDataSource).addOrUpdate(readableValue);
    }

    @Test
    public void shouldDeleteValuesIfAreNotValid() throws Exception {
        givenCacheDataSourceReturnsNonValidValueWithKey(RosieRepositoryTest.ANY_KEY);
        givenReadableDataSourceReturnsValidValueWithKey(RosieRepositoryTest.ANY_KEY);
        RosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        repository.getByKey(RosieRepositoryTest.ANY_KEY);
        Mockito.verify(cacheDataSource).deleteByKey(RosieRepositoryTest.ANY_KEY);
    }

    @Test
    public void shouldLoadItemFromReadableDataSourceIfReadPolicyForcesOnlyReadable() throws Exception {
        givenCacheDataSourceReturnsValidValueWithKey(RosieRepositoryTest.ANY_KEY);
        givenReadableDataSourceReturnsValidValueWithKey(RosieRepositoryTest.ANY_KEY);
        RosieRepository<AnyRepositoryKey, AnyRepositoryValue> repository = givenAReadableAndCacheRepository();
        repository.getByKey(RosieRepositoryTest.ANY_KEY, READABLE_ONLY);
        Mockito.verify(cacheDataSource, Mockito.never()).getByKey(RosieRepositoryTest.ANY_KEY);
        Mockito.verify(readableDataSource).getByKey(RosieRepositoryTest.ANY_KEY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAddOrUpdateNullItems() throws Exception {
        RosieRepository<?, AnyRepositoryValue> repository = givenAWriteableAndCacheRepository();
        repository.addOrUpdate(null);
    }

    @Test
    public void shouldAddOrUpdateItemToWriteableDataSource() throws Exception {
        givenWriteableDataSourceWritesValue(RosieRepositoryTest.ANY_VALUE);
        RosieRepository<?, AnyRepositoryValue> repository = givenAWriteableAndCacheRepository();
        repository.addOrUpdate(RosieRepositoryTest.ANY_VALUE);
        Mockito.verify(writeableDataSource).addOrUpdate(RosieRepositoryTest.ANY_VALUE);
    }

    @Test
    public void shouldPopulateCacheDataSourceWithWriteableDataSourceResult() throws Exception {
        AnyRepositoryValue writeableValue = givenWriteableDataSourceWritesValue(RosieRepositoryTest.ANY_VALUE);
        RosieRepository<?, AnyRepositoryValue> repository = givenAWriteableAndCacheRepository();
        repository.addOrUpdate(RosieRepositoryTest.ANY_VALUE);
        Mockito.verify(cacheDataSource).addOrUpdate(writeableValue);
    }

    @Test
    public void shouldNotPopulateCacheDataSourceIfResultIsNotSuccessful() throws Exception {
        givenWriteableDataSourceDoesNotWriteValue(RosieRepositoryTest.ANY_VALUE);
        RosieRepository<?, AnyRepositoryValue> repository = givenAWriteableAndCacheRepository();
        repository.addOrUpdate(RosieRepositoryTest.ANY_VALUE);
        Mockito.verify(cacheDataSource, Mockito.never()).addOrUpdate(ArgumentMatchers.any(AnyRepositoryValue.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullValues() throws Exception {
        RosieRepository<?, ?> repository = givenAWriteableAndCacheRepository();
        repository.addOrUpdate(null);
    }

    @Test
    public void shouldAddItemsToWriteableDataSource() throws Exception {
        Collection<AnyRepositoryValue> values = getSomeValues();
        givenWriteableDataSourceWritesValues(values);
        RosieRepository<?, AnyRepositoryValue> repository = givenAWriteableAndCacheRepository();
        repository.addOrUpdateAll(values);
        Mockito.verify(writeableDataSource).addOrUpdateAll(values);
    }

    @Test
    public void shouldPopulateCacheDataSourceWithWriteableDataSourceResults() throws Exception {
        Collection<AnyRepositoryValue> values = getSomeValues();
        Collection<AnyRepositoryValue> writeableValues = givenWriteableDataSourceWritesValues(values);
        RosieRepository<?, AnyRepositoryValue> repository = givenAWriteableAndCacheRepository();
        repository.addOrUpdateAll(values);
        Mockito.verify(cacheDataSource).addOrUpdateAll(writeableValues);
    }

    @Test
    public void shouldNotPopulateCacheDataSourceIfWriteableDataSourceResultIsNotSuccessful() throws Exception {
        Collection<AnyRepositoryValue> values = getSomeValues();
        givenWriteableDataSourceDoesNotWriteValues(values);
        RosieRepository<?, AnyRepositoryValue> repository = givenAWriteableAndCacheRepository();
        repository.addOrUpdateAll(values);
        Mockito.verify(cacheDataSource, Mockito.never()).addOrUpdateAll(values);
    }

    @Test
    public void shouldDeleteAllDataSources() throws Exception {
        RosieRepository<?, ?> repository = givenAWriteableAndCacheRepository();
        repository.deleteAll();
        Mockito.verify(writeableDataSource).deleteAll();
        Mockito.verify(cacheDataSource).deleteAll();
    }

    @Test
    public void shouldDeleteAllDataSourcesByKey() throws Exception {
        RosieRepository<AnyRepositoryKey, ?> repository = givenAWriteableAndCacheRepository();
        repository.deleteByKey(RosieRepositoryTest.ANY_KEY);
        Mockito.verify(writeableDataSource).deleteByKey(RosieRepositoryTest.ANY_KEY);
        Mockito.verify(cacheDataSource).deleteByKey(RosieRepositoryTest.ANY_KEY);
    }

    private enum DataSource {

        READABLE,
        WRITEABLE,
        CACHE;}
}

