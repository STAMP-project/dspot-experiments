/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.quartz;


import java.util.Calendar;
import java.util.Date;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.impl.triggers.CalendarIntervalTriggerImpl;


/**
 * Unit tests for DateIntervalTrigger.
 */
public class CalendarIntervalTriggerTest extends SerializationTestSupport {
    private static final String[] VERSIONS = new String[]{ "2.0" };

    @Test
    public void testQTZ331FireTimeAfterBoundary() {
        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(2013, Calendar.FEBRUARY, 15);
        Date startTime = start.getTime();
        start.add(Calendar.DAY_OF_MONTH, 1);
        Date triggerTime = start.getTime();
        CalendarIntervalTriggerImpl trigger = new CalendarIntervalTriggerImpl("test", startTime, null, IntervalUnit.DAY, 1);
        Assert.assertThat(trigger.getFireTimeAfter(startTime), CoreMatchers.equalTo(triggerTime));
        Date after = new Date(((start.getTimeInMillis()) - 500));
        Assert.assertThat(trigger.getFireTimeAfter(after), CoreMatchers.equalTo(triggerTime));
    }
}

