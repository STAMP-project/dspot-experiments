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
package com.alibaba.csp.sentinel.slots.statistic.metric;


import RollingParamEvent.REQUEST_PASSED;
import com.alibaba.csp.sentinel.slots.block.flow.param.RollingParamEvent;
import com.alibaba.csp.sentinel.slots.statistic.data.ParamMapBucket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test cases for {@link HotParameterLeapArray}.
 *
 * @author Eric Zhao
 * @since 0.2.0
 */
public class HotParameterLeapArrayTest {
    @Test
    public void testAddValueToBucket() {
        HotParameterLeapArray leapArray = Mockito.mock(HotParameterLeapArray.class);
        String paramA = "paramA";
        int initialCountA = 3;
        RollingParamEvent passEvent = RollingParamEvent.REQUEST_PASSED;
        final ParamMapBucket bucket = new ParamMapBucket();
        bucket.add(passEvent, initialCountA, paramA);
        Mockito.doCallRealMethod().when(leapArray).addValue(ArgumentMatchers.any(RollingParamEvent.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Object.class));
        Mockito.when(leapArray.currentWindow()).thenReturn(new com.alibaba.csp.sentinel.slots.statistic.base.WindowWrap<ParamMapBucket>(0, 0, bucket));
        Assert.assertEquals(initialCountA, leapArray.currentWindow().value().get(passEvent, paramA));
        int delta = 2;
        leapArray.addValue(passEvent, delta, paramA);
        Assert.assertEquals((initialCountA + delta), leapArray.currentWindow().value().get(passEvent, paramA));
    }

    @Test
    public void testGetTopValues() {
        int intervalInSec = 2;
        int a1 = 3;
        int a2 = 5;
        String paramPrefix = "param-";
        HotParameterLeapArray leapArray = Mockito.mock(HotParameterLeapArray.class);
        Mockito.when(leapArray.getIntervalInSecond()).thenReturn(((double) (intervalInSec)));
        final ParamMapBucket b1 = generateBucket(a1, paramPrefix);
        final ParamMapBucket b2 = generateBucket(a2, paramPrefix);
        List<ParamMapBucket> buckets = new ArrayList<ParamMapBucket>() {
            {
                add(b1);
                add(b2);
            }
        };
        Mockito.when(leapArray.values()).thenReturn(buckets);
        Mockito.when(leapArray.getTopValues(ArgumentMatchers.any(RollingParamEvent.class), ArgumentMatchers.any(int.class))).thenCallRealMethod();
        Map<Object, Double> top2Values = leapArray.getTopValues(REQUEST_PASSED, (a1 - 1));
        // Top 2 should be 5 and 3
        Assert.assertEquals(((((double) (5)) * 10) / intervalInSec), top2Values.get((paramPrefix + 5)), 0.01);
        Assert.assertEquals(((((double) (3)) * 20) / intervalInSec), top2Values.get((paramPrefix + 3)), 0.01);
        Map<Object, Double> top4Values = leapArray.getTopValues(REQUEST_PASSED, (a2 - 1));
        Assert.assertEquals((a2 - 1), top4Values.size());
        Assert.assertFalse(top4Values.containsKey((paramPrefix + 1)));
        Map<Object, Double> topMoreValues = leapArray.getTopValues(REQUEST_PASSED, (a2 + 1));
        Assert.assertEquals(("This should contain all parameters but no more than " + a2), a2, topMoreValues.size());
    }

    @Test
    public void testGetRollingSum() {
        HotParameterLeapArray leapArray = Mockito.mock(HotParameterLeapArray.class);
        String v1 = "a";
        String v2 = "B";
        String v3 = "Cc";
        int p1a = 19;
        int p1b = 3;
        int p2a = 6;
        int p2c = 17;
        RollingParamEvent passEvent = RollingParamEvent.REQUEST_PASSED;
        final ParamMapBucket b1 = new ParamMapBucket().add(passEvent, p1a, v1).add(passEvent, p1b, v2);
        final ParamMapBucket b2 = new ParamMapBucket().add(passEvent, p2a, v1).add(passEvent, p2c, v3);
        List<ParamMapBucket> buckets = new ArrayList<ParamMapBucket>() {
            {
                add(b1);
                add(b2);
            }
        };
        Mockito.when(leapArray.values()).thenReturn(buckets);
        Mockito.when(leapArray.getRollingSum(ArgumentMatchers.any(RollingParamEvent.class), ArgumentMatchers.any(Object.class))).thenCallRealMethod();
        Assert.assertEquals((p1a + p2a), leapArray.getRollingSum(passEvent, v1));
        Assert.assertEquals(p1b, leapArray.getRollingSum(passEvent, v2));
        Assert.assertEquals(p2c, leapArray.getRollingSum(passEvent, v3));
    }

    @Test
    public void testGetRollingAvg() {
        HotParameterLeapArray leapArray = Mockito.mock(HotParameterLeapArray.class);
        Mockito.when(leapArray.getRollingSum(ArgumentMatchers.any(RollingParamEvent.class), ArgumentMatchers.any(Object.class))).thenReturn(15L);
        Mockito.when(leapArray.getIntervalInSecond()).thenReturn(1.0).thenReturn(2.0);
        Mockito.when(leapArray.getRollingAvg(ArgumentMatchers.any(RollingParamEvent.class), ArgumentMatchers.any(Object.class))).thenCallRealMethod();
        Assert.assertEquals(15.0, leapArray.getRollingAvg(REQUEST_PASSED, "abc"), 0.001);
        Assert.assertEquals((15.0 / 2), leapArray.getRollingAvg(REQUEST_PASSED, "abc"), 0.001);
    }
}

