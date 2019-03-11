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
package io.rx_cache2.internal;


import io.rx_cache2.internal.common.BaseTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class DiskTest extends BaseTest {
    private static final String KEY = "store/store";

    private static final String VALUE = "dummy";

    @Test
    public void When_A_Record_Is_Supplied_Retrieve_It() {
        disk.save(DiskTest.KEY, new io.rx_cache2.internal.Record(new Mock(DiskTest.VALUE)), false, null);
        io.rx_cache2.internal.Record<Mock> diskRecord = disk.retrieveRecord(DiskTest.KEY, false, null);
        MatcherAssert.assertThat(diskRecord.getData().getMessage(), Is.is(DiskTest.VALUE));
    }

    @Test
    public void When_A_Record_Collection_Is_Supplied_Retrieve_It() {
        List<Mock> mocks = Arrays.asList(new Mock(DiskTest.VALUE), new Mock(((DiskTest.VALUE) + 1)));
        disk.save(DiskTest.KEY, new io.rx_cache2.internal.Record(mocks), false, null);
        io.rx_cache2.internal.Record<List<Mock>> diskRecord = disk.retrieveRecord(DiskTest.KEY, false, null);
        MatcherAssert.assertThat(diskRecord.getData().get(0).getMessage(), Is.is(DiskTest.VALUE));
        MatcherAssert.assertThat(diskRecord.getData().get(1).getMessage(), Is.is(((DiskTest.VALUE) + 1)));
    }

    @Test
    public void When_A_Record_Array_Is_Supplied_Retrieve_It() {
        Mock[] mocks = new Mock[]{ new Mock(DiskTest.VALUE), new Mock(((DiskTest.VALUE) + 1)) };
        disk.save(DiskTest.KEY, new io.rx_cache2.internal.Record(mocks), false, null);
        io.rx_cache2.internal.Record<Mock[]> diskRecord = disk.retrieveRecord(DiskTest.KEY, false, null);
        MatcherAssert.assertThat(diskRecord.getData()[0].getMessage(), Is.is(DiskTest.VALUE));
        MatcherAssert.assertThat(diskRecord.getData()[1].getMessage(), Is.is(((DiskTest.VALUE) + 1)));
    }

    @Test
    public void When_A_Record_Map_Is_Supplied_Retrieve_It() {
        Map<Integer, Mock> mocks = new HashMap();
        mocks.put(1, new Mock(DiskTest.VALUE));
        mocks.put(2, new Mock(((DiskTest.VALUE) + 1)));
        disk.save(DiskTest.KEY, new io.rx_cache2.internal.Record(mocks), false, null);
        io.rx_cache2.internal.Record<Map<Integer, Mock>> diskRecord = disk.retrieveRecord(DiskTest.KEY, false, null);
        MatcherAssert.assertThat(diskRecord.getData().get(1).getMessage(), Is.is(DiskTest.VALUE));
        MatcherAssert.assertThat(diskRecord.getData().get(2).getMessage(), Is.is(((DiskTest.VALUE) + 1)));
    }

    @Test
    public void When_A_Collection_Is_Supplied_Retrieve_It() {
        List<Mock> mockArrayList = new ArrayList();
        mockArrayList.add(new Mock(DiskTest.VALUE));
        mockArrayList.add(new Mock(((DiskTest.VALUE) + 1)));
        disk.save(DiskTest.KEY, mockArrayList, false, null);
        mockArrayList = disk.retrieveCollection(DiskTest.KEY, List.class, Mock.class);
        MatcherAssert.assertThat(mockArrayList.get(0).getMessage(), Is.is(DiskTest.VALUE));
        MatcherAssert.assertThat(mockArrayList.get(1).getMessage(), Is.is(((DiskTest.VALUE) + 1)));
    }

    @Test
    public void When_A_Map_Is_Supplied_Retrieve_It() {
        Map<Integer, Mock> mocksHashMap = new HashMap();
        mocksHashMap.put(1, new Mock(DiskTest.VALUE));
        mocksHashMap.put(2, new Mock(((DiskTest.VALUE) + 1)));
        disk.save(DiskTest.KEY, mocksHashMap, false, null);
        mocksHashMap = disk.retrieveMap(DiskTest.KEY, Map.class, Integer.class, Mock.class);
        MatcherAssert.assertThat(mocksHashMap.get(1).getMessage(), Is.is(DiskTest.VALUE));
        MatcherAssert.assertThat(mocksHashMap.get(2).getMessage(), Is.is(((DiskTest.VALUE) + 1)));
    }

    @Test
    public void When_An_Array_Is_Supplied_Retrieve_It() {
        Mock[] mocksArray = new Mock[]{ new Mock(DiskTest.VALUE), new Mock(((DiskTest.VALUE) + 1)) };
        disk.save(DiskTest.KEY, mocksArray, false, null);
        mocksArray = disk.retrieveArray(DiskTest.KEY, Mock.class);
        MatcherAssert.assertThat(mocksArray[0].getMessage(), Is.is(DiskTest.VALUE));
        MatcherAssert.assertThat(mocksArray[1].getMessage(), Is.is(((DiskTest.VALUE) + 1)));
    }

    @Test
    public void When_Encrypt_Is_False_Then_Retrieve_Record_Without_Encrypt() {
        disk.save(DiskTest.KEY, new io.rx_cache2.internal.Record(new Mock(DiskTest.VALUE)), false, null);
        io.rx_cache2.internal.Record<Mock> diskRecord = disk.retrieveRecord(DiskTest.KEY, false, null);
        MatcherAssert.assertThat(diskRecord.getData().getMessage(), Is.is(DiskTest.VALUE));
    }

    @Test
    public void When_Encrypt_Is_True_Then_Retrieve_Record_Decrypted() {
        disk.save(DiskTest.KEY, new io.rx_cache2.internal.Record(new Mock(DiskTest.VALUE)), true, "key");
        io.rx_cache2.internal.Record<Mock> diskRecord = disk.retrieveRecord(DiskTest.KEY, true, "key");
        MatcherAssert.assertThat(diskRecord.getData().getMessage(), Is.is(DiskTest.VALUE));
    }

    @Test
    public void When_Encrypt_Is_False_And_I_Try_Retrieve_It_Encrypted_Then_Record_Is_Null() {
        disk.save(DiskTest.KEY, new io.rx_cache2.internal.Record(new Mock(DiskTest.VALUE)), false, null);
        io.rx_cache2.internal.Record<Mock> diskRecord = disk.retrieveRecord(DiskTest.KEY, true, "key");
        Assert.assertNull(diskRecord);
    }

    @Test
    public void When_Encrypt_Is_True_And_I_Try_Retrieve_It_Without_Encrypt_Then_Record_Is_Null() {
        disk.save(DiskTest.KEY, new io.rx_cache2.internal.Record(new Mock(DiskTest.VALUE)), true, "key");
        io.rx_cache2.internal.Record<Mock> diskRecord = disk.retrieveRecord(DiskTest.KEY, false, null);
        Assert.assertNull(diskRecord);
    }

    @Test
    public void When_Encrypt_Is_True_And_I_Try_Retrieve_It_With_Another_Key_Then_Record_Is_Null() {
        disk.save(DiskTest.KEY, new io.rx_cache2.internal.Record(new Mock(DiskTest.VALUE)), true, "key");
        io.rx_cache2.internal.Record<Mock> diskRecord = disk.retrieveRecord(DiskTest.KEY, true, "otherkey");
        Assert.assertNull(diskRecord);
    }

    @Test
    public void When_Evict_Cache_Then_Evict_Cache() {
        for (int i = 0; i < 100; i++) {
            disk.save(String.valueOf(i), new io.rx_cache2.internal.Record(new Mock(DiskTest.VALUE)), false, null);
        }
        MatcherAssert.assertThat(disk.allKeys().size(), Is.is(100));
        disk.evictAll();
        MatcherAssert.assertThat(disk.allKeys().size(), Is.is(0));
    }

    @Test
    public void When_A_Record_Multi_Level_Map_Is_Supplied_Retrieve_It() {
        Map multiLevelMap = new HashMap<>();
        multiLevelMap.put(1, DiskTest.VALUE);
        multiLevelMap.put(2, ((DiskTest.VALUE) + 1));
        Map innerMap = new HashMap();
        innerMap.put("foo", ((DiskTest.VALUE) + 2));
        innerMap.put("bar", ((DiskTest.VALUE) + 3));
        multiLevelMap.put(3, innerMap);
        disk.save(DiskTest.KEY, new io.rx_cache2.internal.Record(multiLevelMap), false, null);
        io.rx_cache2.internal.Record<Map> diskRecord = disk.retrieveRecord(DiskTest.KEY, false, null);
        MatcherAssert.assertThat(diskRecord.getData().get(1), Is.is(((Object) (DiskTest.VALUE))));
        MatcherAssert.assertThat(diskRecord.getData().get(2), Is.is(((Object) ((DiskTest.VALUE) + 1))));
        final Object value3 = diskRecord.getData().get(3);
        Assert.assertTrue((value3 instanceof Map));
        MatcherAssert.assertThat(((Map) (value3)).get("foo"), Is.is(((Object) ((DiskTest.VALUE) + 2))));
        MatcherAssert.assertThat(((Map) (value3)).get("bar"), Is.is(((Object) ((DiskTest.VALUE) + 3))));
    }
}

