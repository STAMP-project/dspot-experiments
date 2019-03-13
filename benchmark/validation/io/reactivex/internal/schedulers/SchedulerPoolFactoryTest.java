/**
 * Copyright (c) 2016-present, RxJava Contributors.
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
package io.reactivex.internal.schedulers;


import SchedulerPoolFactory.POOLS;
import SchedulerPoolFactory.PURGE_ENABLED_KEY;
import SchedulerPoolFactory.PURGE_PERIOD_SECONDS_KEY;
import SchedulerPoolFactory.PURGE_THREAD;
import io.reactivex.TestHelper;
import io.reactivex.internal.schedulers.SchedulerPoolFactory.PurgeProperties;
import io.reactivex.schedulers.Schedulers;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


public class SchedulerPoolFactoryTest {
    @Test
    public void utilityClass() {
        TestHelper.checkUtilityClass(SchedulerPoolFactory.class);
    }

    @Test
    public void multiStartStop() {
        SchedulerPoolFactory.shutdown();
        SchedulerPoolFactory.shutdown();
        SchedulerPoolFactory.tryStart(false);
        Assert.assertNull(PURGE_THREAD.get());
        SchedulerPoolFactory.start();
        // restart schedulers
        Schedulers.shutdown();
        Schedulers.start();
    }

    @Test
    public void startRace() throws InterruptedException {
        try {
            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                SchedulerPoolFactory.shutdown();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        SchedulerPoolFactory.start();
                    }
                };
                TestHelper.race(r1, r1);
            }
        } finally {
            // restart schedulers
            Schedulers.shutdown();
            Thread.sleep(200);
            Schedulers.start();
        }
    }

    @Test
    public void loadPurgeProperties() {
        Properties props1 = new Properties();
        PurgeProperties pp = new PurgeProperties();
        pp.load(props1);
        Assert.assertTrue(pp.purgeEnable);
        Assert.assertEquals(pp.purgePeriod, 1);
    }

    @Test
    public void loadPurgePropertiesDisabled() {
        Properties props1 = new Properties();
        props1.setProperty(PURGE_ENABLED_KEY, "false");
        PurgeProperties pp = new PurgeProperties();
        pp.load(props1);
        Assert.assertFalse(pp.purgeEnable);
        Assert.assertEquals(pp.purgePeriod, 1);
    }

    @Test
    public void loadPurgePropertiesEnabledCustomPeriod() {
        Properties props1 = new Properties();
        props1.setProperty(PURGE_ENABLED_KEY, "true");
        props1.setProperty(PURGE_PERIOD_SECONDS_KEY, "2");
        PurgeProperties pp = new PurgeProperties();
        pp.load(props1);
        Assert.assertTrue(pp.purgeEnable);
        Assert.assertEquals(pp.purgePeriod, 2);
    }

    @Test
    public void loadPurgePropertiesEnabledCustomPeriodNaN() {
        Properties props1 = new Properties();
        props1.setProperty(PURGE_ENABLED_KEY, "true");
        props1.setProperty(PURGE_PERIOD_SECONDS_KEY, "abc");
        PurgeProperties pp = new PurgeProperties();
        pp.load(props1);
        Assert.assertTrue(pp.purgeEnable);
        Assert.assertEquals(pp.purgePeriod, 1);
    }

    @Test
    public void putIntoPoolNoPurge() {
        int s = POOLS.size();
        SchedulerPoolFactory.tryPutIntoPool(false, null);
        Assert.assertEquals(s, POOLS.size());
    }

    @Test
    public void putIntoPoolNonThreadPool() {
        int s = POOLS.size();
        SchedulerPoolFactory.tryPutIntoPool(true, null);
        Assert.assertEquals(s, POOLS.size());
    }
}

