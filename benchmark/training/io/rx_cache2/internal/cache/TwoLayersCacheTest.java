/**
 * Copyright 2015 Victor Albertos
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
package io.rx_cache2.internal.cache;


import Source.MEMORY;
import Source.PERSISTENCE;
import io.rx_cache2.internal.Memory;
import io.rx_cache2.internal.Mock;
import io.rx_cache2.internal.Record;
import io.rx_cache2.internal.common.BaseTest;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Test;


/**
 * Created by victor on 20/12/15.
 */
public class TwoLayersCacheTest extends BaseTest {
    private TwoLayersCache twoLayersCacheUT;

    private Memory memory;

    private static final long ONE_SECOND_LIFE = 1000;

    private static final long THREE_SECOND_LIFE = 3000;

    private static final long MORE_THAN_ONE_SECOND_LIFE = 1250;

    private static final long DUMMY_LIFE_TIME = -1;

    private static final String PROVIDER_KEY = "get_mocks";

    private static final String MOCK_VALUE = "mock_value";

    @Test
    public void When_Save_And_Object_Not_Expired_And_Memory_Not_Destroyed_Retrieve_It_From_Memory() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "", "", false, ((TwoLayersCacheTest.ONE_SECOND_LIFE) + 1000), false);
        Mock mock = record.getData();
        MatcherAssert.assertThat(mock.getMessage(), Is.is(TwoLayersCacheTest.MOCK_VALUE));
        MatcherAssert.assertThat(record.getSource(), Is.is(MEMORY));
    }

    @Test
    public void When_Save_And_Record_Has_Not_Expired_And_Memory_Destroyed_Retrieve_It_From_Disk() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.mockMemoryDestroyed();
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        Mock mock = record.getData();
        MatcherAssert.assertThat(mock.getMessage(), Is.is(TwoLayersCacheTest.MOCK_VALUE));
        MatcherAssert.assertThat(record.getSource(), Is.is(PERSISTENCE));
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(mock.getMessage(), Is.is(TwoLayersCacheTest.MOCK_VALUE));
        MatcherAssert.assertThat(record.getSource(), Is.is(MEMORY));
    }

    @Test
    public void When_Save_And_Provider_Record_Has_Expired_Get_Null() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        waitTime(TwoLayersCacheTest.MORE_THAN_ONE_SECOND_LIFE);
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "", "", false, TwoLayersCacheTest.THREE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
    }

    @Test
    public void When_Save_And_Dynamic_Key_Record_Has_Expired_Only_Get_Null_For_Dynamic_Key() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "1", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.ONE_SECOND_LIFE, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "2", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.ONE_SECOND_LIFE, true, false);
        waitTime(TwoLayersCacheTest.MORE_THAN_ONE_SECOND_LIFE);
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "", false, TwoLayersCacheTest.THREE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "2", "", false, TwoLayersCacheTest.THREE_SECOND_LIFE, false);
        TestCase.assertNotNull(record);
    }

    @Test
    public void When_Save_And_Dynamic_Key_Group_Record_Has_Expired_Only_Get_Null_For_Dynamic_Key() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "1", "1", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.ONE_SECOND_LIFE, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "1", "2", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "2", "1", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "2", "2", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        waitTime(TwoLayersCacheTest.MORE_THAN_ONE_SECOND_LIFE);
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "1", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "1", false, TwoLayersCacheTest.THREE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "2", false, TwoLayersCacheTest.THREE_SECOND_LIFE, false);
        TestCase.assertNotNull(record);
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "2", "1", false, TwoLayersCacheTest.THREE_SECOND_LIFE, false);
        TestCase.assertNotNull(record);
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "2", "2", false, TwoLayersCacheTest.THREE_SECOND_LIFE, false);
        TestCase.assertNotNull(record);
    }

    @Test
    public void When_Save_And_Record_Has_Not_Expired_Date_Do_Not_Get_Null() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        waitTime(TwoLayersCacheTest.MORE_THAN_ONE_SECOND_LIFE);
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "", "", false, null, false);
        MatcherAssert.assertThat(record.getData().getMessage(), Is.is(TwoLayersCacheTest.MOCK_VALUE));
        MatcherAssert.assertThat(record.getSource(), Is.is(MEMORY));
        twoLayersCacheUT.mockMemoryDestroyed();
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "", "", false, null, false);
        MatcherAssert.assertThat(record.getData().getMessage(), Is.is(TwoLayersCacheTest.MOCK_VALUE));
        MatcherAssert.assertThat(record.getSource(), Is.is(PERSISTENCE));
    }

    @Test
    public void When_Save_And_Evict_Get_Null() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.evictProviderKey(TwoLayersCacheTest.PROVIDER_KEY);
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
    }

    @Test
    public void When_Save_And_Evict_All_Get_Null() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, ("" + 1), "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(((TwoLayersCacheTest.PROVIDER_KEY) + 1), "", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(((TwoLayersCacheTest.PROVIDER_KEY) + 1), ("" + 1), "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.evictAll();
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
        record = twoLayersCacheUT.retrieve(((TwoLayersCacheTest.PROVIDER_KEY) + 1), "", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
        record = twoLayersCacheUT.retrieve(((TwoLayersCacheTest.PROVIDER_KEY) + 1), "", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
    }

    @Test
    public void When_Save_And_Not_Evict_Dynamic_Keys_Get_All() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "filter_1", "", new Mock(((TwoLayersCacheTest.MOCK_VALUE) + 1)), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "filter_2", "", new Mock(((TwoLayersCacheTest.MOCK_VALUE) + 2)), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "filter_3", "", new Mock(((TwoLayersCacheTest.MOCK_VALUE) + 3)), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        Record<Mock> record1 = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "filter_1", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        Record<Mock> record2 = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "filter_2", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        Record<Mock> record3 = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "filter_3", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record1.getData().getMessage(), Is.is(((TwoLayersCacheTest.MOCK_VALUE) + 1)));
        MatcherAssert.assertThat(record2.getData().getMessage(), Is.is(((TwoLayersCacheTest.MOCK_VALUE) + 2)));
        MatcherAssert.assertThat(record3.getData().getMessage(), Is.is(((TwoLayersCacheTest.MOCK_VALUE) + 3)));
    }

    @Test
    public void When_Save_Dynamic_Key_And_Re_Save_Dynamic_Key_Get_Last_Value() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "1", "", new Mock(((TwoLayersCacheTest.MOCK_VALUE) + 1)), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "1", "", new Mock(((TwoLayersCacheTest.MOCK_VALUE) + 2)), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record.getData().getMessage(), Is.is(((TwoLayersCacheTest.MOCK_VALUE) + 2)));
    }

    @Test
    public void When_Save_Dynamic_Keys_And_Evict_Provider_Key_Get_All_Null() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "filer_1", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "filer_2", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "filer_3", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.evictProviderKey(TwoLayersCacheTest.PROVIDER_KEY);
        MatcherAssert.assertThat(twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "filer_1", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false), Is.is(IsNull.nullValue()));
        MatcherAssert.assertThat(twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "filer_2", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false), Is.is(IsNull.nullValue()));
        MatcherAssert.assertThat(twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "filer_3", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false), Is.is(IsNull.nullValue()));
    }

    @Test
    public void When_Save_Dynamic_Key_And_Evict_One_Dynamic_Key_Get_Others() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "filer_1", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "filer_2", "", new Mock(((TwoLayersCacheTest.MOCK_VALUE) + 1)), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "filer_3", "", new Mock(((TwoLayersCacheTest.MOCK_VALUE) + 2)), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.evictDynamicKey(TwoLayersCacheTest.PROVIDER_KEY, "filer_1");
        MatcherAssert.assertThat(twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "filer_1", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false), Is.is(IsNull.nullValue()));
        Record<Mock> record1 = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "filer_2", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record1.getData().getMessage(), Is.is(((TwoLayersCacheTest.MOCK_VALUE) + 1)));
        Record<Mock> record2 = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "filer_3", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record2.getData().getMessage(), Is.is(((TwoLayersCacheTest.MOCK_VALUE) + 2)));
    }

    @Test
    public void When_Save_Dynamic_Key_Group_And_Evict_One_Dynamic_Key_Group_Get_Others() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "filer_1", "page_1", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "filer_1", "page_2", new Mock(((TwoLayersCacheTest.MOCK_VALUE) + 1)), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        twoLayersCacheUT.evictDynamicKeyGroup(TwoLayersCacheTest.PROVIDER_KEY, "filer_1", "page_2");
        MatcherAssert.assertThat(twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "filer_1", "page_2", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false), Is.is(IsNull.nullValue()));
        Record<Mock> record1 = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "filer_1", "page_1", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record1.getData().getMessage(), Is.is(TwoLayersCacheTest.MOCK_VALUE));
    }

    @Test
    public void When_Expiration_Date_Has_Been_Modified_Then_Reflect_This_Change() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "1", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        waitTime(TwoLayersCacheTest.MORE_THAN_ONE_SECOND_LIFE);
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "", false, TwoLayersCacheTest.THREE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record.getData().getMessage(), Is.is(TwoLayersCacheTest.MOCK_VALUE));
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "1", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        waitTime(TwoLayersCacheTest.MORE_THAN_ONE_SECOND_LIFE);
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "", false, TwoLayersCacheTest.THREE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record.getData().getMessage(), Is.is(TwoLayersCacheTest.MOCK_VALUE));
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "1", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        waitTime(TwoLayersCacheTest.MORE_THAN_ONE_SECOND_LIFE);
        record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "", false, null, false);
        MatcherAssert.assertThat(record.getData().getMessage(), Is.is(TwoLayersCacheTest.MOCK_VALUE));
    }

    @Test
    public void When_Expired_Date_And_Not_Use_ExpiredDataIfLoaderNotAvailable_Then_Get_Null() {
        twoLayersCacheUT = new io.rx_cache2.internal.cache.TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "1", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        waitTime(TwoLayersCacheTest.MORE_THAN_ONE_SECOND_LIFE);
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "", false, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record, Is.is(IsNull.nullValue()));
    }

    @Test
    public void When_Expired_Date_But_Use_ExpiredDataIfLoaderNotAvailable_Then_GetMock() {
        twoLayersCacheUT = new TwoLayersCache(evictRecord(memory), retrieveRecord(memory), saveRecord(memory));
        twoLayersCacheUT.save(TwoLayersCacheTest.PROVIDER_KEY, "1", "", new Mock(TwoLayersCacheTest.MOCK_VALUE), TwoLayersCacheTest.DUMMY_LIFE_TIME, true, false);
        waitTime(TwoLayersCacheTest.MORE_THAN_ONE_SECOND_LIFE);
        Record<Mock> record = twoLayersCacheUT.retrieve(TwoLayersCacheTest.PROVIDER_KEY, "1", "", true, TwoLayersCacheTest.ONE_SECOND_LIFE, false);
        MatcherAssert.assertThat(record.getData().getMessage(), Is.is(TwoLayersCacheTest.MOCK_VALUE));
    }
}

