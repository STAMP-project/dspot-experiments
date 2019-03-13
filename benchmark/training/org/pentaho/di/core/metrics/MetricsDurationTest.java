/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.metrics;


import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class MetricsDurationTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    @Test
    @SuppressWarnings("deprecation")
    public void test() {
        Date startDate = new Date((2016 - 1900), Calendar.JANUARY, 10);
        Long duration = Long.valueOf(4L);
        MetricsDuration metric = new MetricsDuration(startDate, "theDesc", "theSubj", "theLogChannel", duration);
        Assert.assertEquals("theDesc", metric.getDescription());
        Assert.assertEquals("theSubj", metric.getSubject());
        Assert.assertEquals("theLogChannel", metric.getLogChannelId());
        Assert.assertEquals(startDate, metric.getDate());
        Assert.assertEquals(duration, metric.getDuration());
        Assert.assertEquals(Long.valueOf(1L), metric.getCount());
        metric.incrementCount();
        Assert.assertEquals(Long.valueOf(2L), metric.getCount());
        Assert.assertEquals(new Date(((startDate.getTime()) + duration)), metric.getEndDate());
    }
}

