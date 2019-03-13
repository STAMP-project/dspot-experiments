/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.monitor.metric.memory;


import DetailedMemoryMetric.UNCOLLECTED_USAGE;
import com.navercorp.pinpoint.profiler.context.provider.stat.jvmgc.DetailedMemoryMetricProvider;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static DetailedMemoryMetric.UNCOLLECTED_USAGE;
import static MemoryPoolType.CMS;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class DefaultDetailedMemoryMetricTest {
    DetailedMemoryMetricProvider detailedMemoryMetricProvider = new DetailedMemoryMetricProvider();

    @Test
    public void testJvmSupplied() {
        // Given
        DetailedMemoryMetric detailedMemoryMetric = detailedMemoryMetricProvider.get();
        // When
        DetailedMemoryMetricSnapshot snapshot = detailedMemoryMetric.getSnapshot();
        // Then
        Assert.assertNotEquals(UNCOLLECTED_USAGE, snapshot.getNewGenUsage());
        Assert.assertNotEquals(UNCOLLECTED_USAGE, snapshot.getOldGenUsage());
        Assert.assertNotEquals(UNCOLLECTED_USAGE, snapshot.getSurvivorSpaceUsage());
        Assert.assertNotEquals(UNCOLLECTED_USAGE, snapshot.getCodeCacheUsage());
        if ((UNCOLLECTED_USAGE) != (snapshot.getPermGenUsage())) {
            Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getMetaspaceUsage())));
        }
        if ((UNCOLLECTED_USAGE) != (snapshot.getMetaspaceUsage())) {
            Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getPermGenUsage())));
        }
    }

    @Test
    public void testNullMemoryPoolMXBeans() {
        // Given
        DetailedMemoryMetric detailedMemoryMetric = new DefaultDetailedMemoryMetric(CMS, null, null, null, null, null, null);
        // When
        DetailedMemoryMetricSnapshot snapshot = detailedMemoryMetric.getSnapshot();
        // Then
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getNewGenUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getOldGenUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getSurvivorSpaceUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getCodeCacheUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getPermGenUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getMetaspaceUsage())));
    }

    @Test
    public void testNullMemoryUsage() {
        // Given
        MemoryPoolMXBean mockMXBean = Mockito.mock(MemoryPoolMXBean.class);
        MemoryUsage nullMemoryUsage = null;
        Mockito.when(mockMXBean.getUsage()).thenReturn(nullMemoryUsage);
        DetailedMemoryMetric detailedMemoryMetric = new DefaultDetailedMemoryMetric(CMS, mockMXBean, mockMXBean, mockMXBean, mockMXBean, mockMXBean, mockMXBean);
        // When
        DetailedMemoryMetricSnapshot snapshot = detailedMemoryMetric.getSnapshot();
        // Then
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getNewGenUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getOldGenUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getSurvivorSpaceUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getCodeCacheUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getPermGenUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getMetaspaceUsage())));
    }

    @Test
    public void testUnknownMax() {
        // Given
        MemoryPoolMXBean mockMXBean = Mockito.mock(MemoryPoolMXBean.class);
        MemoryUsage mockUsage = Mockito.mock(MemoryUsage.class);
        Mockito.when(mockMXBean.getUsage()).thenReturn(mockUsage);
        Mockito.when(mockUsage.getMax()).thenReturn((-1L));
        DetailedMemoryMetric detailedMemoryMetric = new DefaultDetailedMemoryMetric(CMS, mockMXBean, mockMXBean, mockMXBean, mockMXBean, mockMXBean, mockMXBean);
        // When
        DetailedMemoryMetricSnapshot snapshot = detailedMemoryMetric.getSnapshot();
        // Then
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getNewGenUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getOldGenUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getSurvivorSpaceUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getCodeCacheUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getPermGenUsage())));
        Assert.assertTrue(((UNCOLLECTED_USAGE) == (snapshot.getMetaspaceUsage())));
    }

    @Test
    public void testValidMax() {
        // Given
        long givenUsed = 50L;
        long givenMax = 100L;
        double expectedUsage = givenUsed / ((double) (givenMax));
        MemoryPoolMXBean mockMXBean = Mockito.mock(MemoryPoolMXBean.class);
        MemoryUsage mockUsage = Mockito.mock(MemoryUsage.class);
        Mockito.when(mockMXBean.getUsage()).thenReturn(mockUsage);
        Mockito.when(mockUsage.getUsed()).thenReturn(50L);
        Mockito.when(mockUsage.getMax()).thenReturn(100L);
        DetailedMemoryMetric detailedMemoryMetric = new DefaultDetailedMemoryMetric(CMS, mockMXBean, mockMXBean, mockMXBean, mockMXBean, mockMXBean, mockMXBean);
        // When
        DetailedMemoryMetricSnapshot snapshot = detailedMemoryMetric.getSnapshot();
        // Then
        Assert.assertEquals(expectedUsage, snapshot.getNewGenUsage(), 0.01);
        Assert.assertEquals(expectedUsage, snapshot.getOldGenUsage(), 0.01);
        Assert.assertEquals(expectedUsage, snapshot.getSurvivorSpaceUsage(), 0.01);
        Assert.assertEquals(expectedUsage, snapshot.getCodeCacheUsage(), 0.01);
        Assert.assertEquals(expectedUsage, snapshot.getPermGenUsage(), 0.01);
        Assert.assertEquals(expectedUsage, snapshot.getMetaspaceUsage(), 0.01);
    }
}

