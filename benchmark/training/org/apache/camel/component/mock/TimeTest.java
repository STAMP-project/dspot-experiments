/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.mock;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class TimeTest extends Assert {
    @Test
    public void testTimeSeconds() {
        Time time = new Time(5, TimeUnit.SECONDS);
        Assert.assertNotNull(time);
        Assert.assertEquals(5, time.getNumber());
        Assert.assertEquals(TimeUnit.SECONDS, time.getTimeUnit());
        Assert.assertTrue(((time.toMillis()) > 0));
        Assert.assertNotNull(time.toString());
    }

    @Test
    public void testTimeMinutes() {
        Time time = new Time(3, TimeUnit.MINUTES);
        Assert.assertNotNull(time);
        Assert.assertTrue(((time.toMillis()) > 0));
        Assert.assertNotNull(time.toString());
    }

    @Test
    public void testTimeHours() {
        Time time = new Time(4, TimeUnit.HOURS);
        Assert.assertNotNull(time);
        Assert.assertTrue(((time.toMillis()) > 0));
        Assert.assertNotNull(time.toString());
    }

    @Test
    public void testTimeDays() {
        Time time = new Time(2, TimeUnit.DAYS);
        Assert.assertNotNull(time);
        Assert.assertTrue(((time.toMillis()) > 0));
        Assert.assertNotNull(time.toString());
    }
}

