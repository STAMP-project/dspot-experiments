/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.base.metric;


import com.alibaba.csp.sentinel.slots.statistic.base.WindowWrap;
import com.alibaba.csp.sentinel.slots.statistic.data.MetricBucket;
import com.alibaba.csp.sentinel.slots.statistic.metric.MetricsLeapArray;
import com.alibaba.csp.sentinel.test.AbstractTimeBasedTest;
import com.alibaba.csp.sentinel.util.TimeUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link MetricsLeapArray}.
 *
 * @author Eric Zhao
 */
public class MetricsLeapArrayTest extends AbstractTimeBasedTest {
    private final int windowLengthInMs = 1000;

    private final int intervalInSec = 2;

    private final int intervalInMs = (intervalInSec) * 1000;

    private final int sampleCount = (intervalInMs) / (windowLengthInMs);

    @Test
    public void testNewWindow() {
        MetricsLeapArray leapArray = new MetricsLeapArray(sampleCount, intervalInMs);
        long time = TimeUtil.currentTimeMillis();
        WindowWrap<MetricBucket> window = leapArray.currentWindow(time);
        Assert.assertEquals(window.windowLength(), windowLengthInMs);
        Assert.assertEquals(window.windowStart(), (time - (time % (windowLengthInMs))));
        Assert.assertNotNull(window.value());
        Assert.assertEquals(0L, window.value().pass());
    }

    @Test
    public void testLeapArrayWindowStart() {
        MetricsLeapArray leapArray = new MetricsLeapArray(sampleCount, intervalInMs);
        long firstTime = TimeUtil.currentTimeMillis();
        long previousWindowStart = firstTime - (firstTime % (windowLengthInMs));
        WindowWrap<MetricBucket> window = leapArray.currentWindow(firstTime);
        Assert.assertEquals(windowLengthInMs, window.windowLength());
        Assert.assertEquals(previousWindowStart, window.windowStart());
    }

    @Test
    public void testWindowAfterOneInterval() {
        MetricsLeapArray leapArray = new MetricsLeapArray(sampleCount, intervalInMs);
        long firstTime = TimeUtil.currentTimeMillis();
        long previousWindowStart = firstTime - (firstTime % (windowLengthInMs));
        WindowWrap<MetricBucket> window = leapArray.currentWindow(previousWindowStart);
        Assert.assertEquals(windowLengthInMs, window.windowLength());
        Assert.assertEquals(previousWindowStart, window.windowStart());
        MetricBucket currentWindow = window.value();
        Assert.assertNotNull(currentWindow);
        currentWindow.addPass(1);
        currentWindow.addBlock(1);
        Assert.assertEquals(1L, currentWindow.pass());
        Assert.assertEquals(1L, currentWindow.block());
        long middleTime = previousWindowStart + ((windowLengthInMs) / 2);
        window = leapArray.currentWindow(middleTime);
        Assert.assertEquals(previousWindowStart, window.windowStart());
        MetricBucket middleWindow = window.value();
        middleWindow.addPass(1);
        Assert.assertSame(currentWindow, middleWindow);
        Assert.assertEquals(2L, middleWindow.pass());
        Assert.assertEquals(1L, middleWindow.block());
        long nextTime = middleTime + ((windowLengthInMs) / 2);
        window = leapArray.currentWindow(nextTime);
        Assert.assertEquals(windowLengthInMs, window.windowLength());
        Assert.assertEquals(windowLengthInMs, ((window.windowStart()) - previousWindowStart));
        currentWindow = window.value();
        Assert.assertNotNull(currentWindow);
        Assert.assertEquals(0L, currentWindow.pass());
        Assert.assertEquals(0L, currentWindow.block());
    }

    @Test
    public void testMultiThreadUpdateEmptyWindow() throws Exception {
        final long time = TimeUtil.currentTimeMillis();
        final int nThreads = 16;
        final MetricsLeapArray leapArray = new MetricsLeapArray(sampleCount, intervalInMs);
        final CountDownLatch latch = new CountDownLatch(nThreads);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                leapArray.currentWindow(time).value().addPass(1);
                latch.countDown();
            }
        };
        for (int i = 0; i < nThreads; i++) {
            new Thread(task).start();
        }
        latch.await();
        Assert.assertEquals(nThreads, leapArray.currentWindow(time).value().pass());
    }

    @Test
    public void testGetPreviousWindow() {
        setCurrentMillis(System.currentTimeMillis());
        MetricsLeapArray leapArray = new MetricsLeapArray(sampleCount, intervalInMs);
        long time = TimeUtil.currentTimeMillis();
        WindowWrap<MetricBucket> previousWindow = leapArray.currentWindow(time);
        Assert.assertNull(leapArray.getPreviousWindow(time));
        long nextTime = time + (windowLengthInMs);
        Assert.assertSame(previousWindow, leapArray.getPreviousWindow(nextTime));
        long longTime = time + (11 * (windowLengthInMs));
        Assert.assertNull(leapArray.getPreviousWindow(longTime));
    }

    @Test
    public void testListWindowsResetOld() throws Exception {
        final int windowLengthInMs = 100;
        final int intervalInMs = 1000;
        final int sampleCount = intervalInMs / windowLengthInMs;
        setCurrentMillis(System.currentTimeMillis());
        MetricsLeapArray leapArray = new MetricsLeapArray(sampleCount, intervalInMs);
        long time = TimeUtil.currentTimeMillis();
        Set<WindowWrap<MetricBucket>> windowWraps = new HashSet<WindowWrap<MetricBucket>>();
        windowWraps.add(leapArray.currentWindow(time));
        windowWraps.add(leapArray.currentWindow((time + windowLengthInMs)));
        List<WindowWrap<MetricBucket>> list = leapArray.list();
        for (WindowWrap<MetricBucket> wrap : list) {
            Assert.assertTrue(windowWraps.contains(wrap));
        }
        sleep((windowLengthInMs + intervalInMs));
        // This will replace the deprecated bucket, so all deprecated buckets will be reset.
        leapArray.currentWindow(((time + windowLengthInMs) + intervalInMs)).value().addPass(1);
        Assert.assertEquals(1, leapArray.list().size());
    }

    @Test
    public void testListWindowsNewBucket() throws Exception {
        final int windowLengthInMs = 100;
        final int intervalInSec = 1;
        final int intervalInMs = intervalInSec * 1000;
        final int sampleCount = intervalInMs / windowLengthInMs;
        MetricsLeapArray leapArray = new MetricsLeapArray(sampleCount, intervalInMs);
        long time = TimeUtil.currentTimeMillis();
        Set<WindowWrap<MetricBucket>> windowWraps = new HashSet<WindowWrap<MetricBucket>>();
        windowWraps.add(leapArray.currentWindow(time));
        windowWraps.add(leapArray.currentWindow((time + windowLengthInMs)));
        sleep((intervalInMs + (windowLengthInMs * 3)));
        List<WindowWrap<MetricBucket>> list = leapArray.list();
        for (WindowWrap<MetricBucket> wrap : list) {
            Assert.assertTrue(windowWraps.contains(wrap));
        }
        // This won't hit deprecated bucket, so no deprecated buckets will be reset.
        // But deprecated buckets can be filtered when collecting list.
        leapArray.currentWindow(TimeUtil.currentTimeMillis()).value().addPass(1);
        Assert.assertEquals(1, leapArray.list().size());
    }
}

