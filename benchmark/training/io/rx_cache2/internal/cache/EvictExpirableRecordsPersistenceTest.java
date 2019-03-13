/**
 * Copyright 2016 Victor Albertos
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


import Locale.RECORD_CAN_NOT_BE_EVICTED_BECAUSE_NO_ONE_IS_EXPIRABLE;
import io.reactivex.observers.TestObserver;
import io.rx_cache2.internal.Memory;
import io.rx_cache2.internal.common.BaseTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class EvictExpirableRecordsPersistenceTest extends BaseTest {
    private EvictExpirableRecordsPersistence evictExpirableRecordsPersistenceUT;

    private Memory memory;

    @Test
    public void When_Not_Reached_Memory_Threshold_Not_Emit() {
        evictExpirableRecordsPersistenceUT = new io.rx_cache2.internal.cache.EvictExpirableRecordsPersistence(memory, disk, 10, null);
        populate(true);
        Assert.assertThat(disk.allKeys().size(), CoreMatchers.is(100));
        TestObserver testObserver = evictExpirableRecordsPersistenceUT.startTaskIfNeeded(false).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors().assertNoValues();
    }

    @DataPoint
    public static Integer _3_MB = 3;

    @DataPoint
    public static Integer _5_MB = 5;

    @DataPoint
    public static Integer _7_MB = 7;

    @Test
    public void When_Reached_Memory_Threshold_But_Not_Expirable_Records_Do_Not_Evict() {
        int maxMgPersistenceCache = 5;
        evictExpirableRecordsPersistenceUT = new io.rx_cache2.internal.cache.EvictExpirableRecordsPersistence(memory, disk, maxMgPersistenceCache, null);
        populate(false);
        Assert.assertThat(disk.allKeys().size(), CoreMatchers.is(mocksCount()));
        TestObserver testObserver = evictExpirableRecordsPersistenceUT.startTaskIfNeeded(false).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        // testSubscriber.assertNoValues();
        Assert.assertThat(sizeMbDataPopulated(), CoreMatchers.is(disk.storedMB()));
        // after first time does not start process again, just return warning message
        testObserver = new TestObserver();
        evictExpirableRecordsPersistenceUT.startTaskIfNeeded(false).subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertValue(RECORD_CAN_NOT_BE_EVICTED_BECAUSE_NO_ONE_IS_EXPIRABLE);
    }
}

