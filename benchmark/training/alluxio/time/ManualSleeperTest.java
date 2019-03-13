/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.time;


import Constants.SECOND_MS;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link ManualSleeper}.
 */
public final class ManualSleeperTest {
    private AtomicInteger mSleepTime;

    private ManualSleeper mSleeper;

    private Thread mTestThread;

    @Test
    public void checkSleepTime() throws InterruptedException {
        for (int i = 1; i < 100; i++) {
            Assert.assertEquals(i, mSleeper.waitForSleep().toMillis());
            mSleeper.wakeUp();
        }
    }

    @Test
    public void propagateInterrupt() throws InterruptedException {
        mTestThread.interrupt();
        mTestThread.join(SECOND_MS);
        Assert.assertFalse(mTestThread.isAlive());
    }
}

