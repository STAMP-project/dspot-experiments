/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.log;


import org.apache.hadoop.log.LogThrottlingHelper.LogAction;
import org.apache.hadoop.util.FakeTimer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link LogThrottlingHelper}.
 */
public class TestLogThrottlingHelper {
    private static final int LOG_PERIOD = 100;

    private LogThrottlingHelper helper;

    private FakeTimer timer;

    @Test
    public void testBasicLogging() {
        Assert.assertTrue(helper.record().shouldLog());
        for (int i = 0; i < 5; i++) {
            timer.advance(((TestLogThrottlingHelper.LOG_PERIOD) / 10));
            Assert.assertFalse(helper.record().shouldLog());
        }
        timer.advance(TestLogThrottlingHelper.LOG_PERIOD);
        Assert.assertTrue(helper.record().shouldLog());
    }

    @Test
    public void testLoggingWithValue() {
        Assert.assertTrue(helper.record(1).shouldLog());
        for (int i = 0; i < 4; i++) {
            timer.advance(((TestLogThrottlingHelper.LOG_PERIOD) / 5));
            Assert.assertFalse(helper.record(((i % 2) == 0 ? 0 : 1)).shouldLog());
        }
        timer.advance(TestLogThrottlingHelper.LOG_PERIOD);
        LogAction action = helper.record(0.5);
        Assert.assertTrue(action.shouldLog());
        Assert.assertEquals(5, action.getCount());
        Assert.assertEquals(0.5, action.getStats(0).getMean(), 0.01);
        Assert.assertEquals(1.0, action.getStats(0).getMax(), 0.01);
        Assert.assertEquals(0.0, action.getStats(0).getMin(), 0.01);
    }

    @Test
    public void testLoggingWithMultipleValues() {
        Assert.assertTrue(helper.record(1).shouldLog());
        for (int i = 0; i < 4; i++) {
            timer.advance(((TestLogThrottlingHelper.LOG_PERIOD) / 5));
            int base = ((i % 2) == 0) ? 0 : 1;
            Assert.assertFalse(helper.record(base, (base * 2)).shouldLog());
        }
        timer.advance(TestLogThrottlingHelper.LOG_PERIOD);
        LogAction action = helper.record(0.5, 1.0);
        Assert.assertTrue(action.shouldLog());
        Assert.assertEquals(5, action.getCount());
        for (int i = 1; i <= 2; i++) {
            Assert.assertEquals((0.5 * i), action.getStats((i - 1)).getMean(), 0.01);
            Assert.assertEquals((1.0 * i), action.getStats((i - 1)).getMax(), 0.01);
            Assert.assertEquals(0.0, action.getStats((i - 1)).getMin(), 0.01);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoggingWithInconsistentValues() {
        Assert.assertTrue(helper.record(1, 2).shouldLog());
        helper.record(1, 2);
        helper.record(1, 2, 3);
    }

    @Test
    public void testNamedLoggersWithoutSpecifiedPrimary() {
        Assert.assertTrue(helper.record("foo", 0).shouldLog());
        Assert.assertTrue(helper.record("bar", 0).shouldLog());
        Assert.assertFalse(helper.record("foo", ((TestLogThrottlingHelper.LOG_PERIOD) / 2)).shouldLog());
        Assert.assertFalse(helper.record("bar", ((TestLogThrottlingHelper.LOG_PERIOD) / 2)).shouldLog());
        Assert.assertTrue(helper.record("foo", TestLogThrottlingHelper.LOG_PERIOD).shouldLog());
        Assert.assertTrue(helper.record("bar", TestLogThrottlingHelper.LOG_PERIOD).shouldLog());
        Assert.assertFalse(helper.record("foo", (((TestLogThrottlingHelper.LOG_PERIOD) * 3) / 2)).shouldLog());
        Assert.assertFalse(helper.record("bar", (((TestLogThrottlingHelper.LOG_PERIOD) * 3) / 2)).shouldLog());
        Assert.assertFalse(helper.record("bar", ((TestLogThrottlingHelper.LOG_PERIOD) * 2)).shouldLog());
        Assert.assertTrue(helper.record("foo", ((TestLogThrottlingHelper.LOG_PERIOD) * 2)).shouldLog());
        Assert.assertTrue(helper.record("bar", ((TestLogThrottlingHelper.LOG_PERIOD) * 2)).shouldLog());
    }

    @Test
    public void testPrimaryAndDependentLoggers() {
        helper = new LogThrottlingHelper(TestLogThrottlingHelper.LOG_PERIOD, "foo", timer);
        Assert.assertTrue(helper.record("foo", 0).shouldLog());
        Assert.assertTrue(helper.record("bar", 0).shouldLog());
        Assert.assertFalse(helper.record("bar", 0).shouldLog());
        Assert.assertFalse(helper.record("foo", 0).shouldLog());
        Assert.assertFalse(helper.record("foo", ((TestLogThrottlingHelper.LOG_PERIOD) / 2)).shouldLog());
        Assert.assertFalse(helper.record("bar", ((TestLogThrottlingHelper.LOG_PERIOD) / 2)).shouldLog());
        // Both should log once the period has elapsed
        Assert.assertTrue(helper.record("foo", TestLogThrottlingHelper.LOG_PERIOD).shouldLog());
        Assert.assertTrue(helper.record("bar", TestLogThrottlingHelper.LOG_PERIOD).shouldLog());
        // "bar" should not log yet because "foo" hasn't been triggered
        Assert.assertFalse(helper.record("bar", ((TestLogThrottlingHelper.LOG_PERIOD) * 2)).shouldLog());
        Assert.assertTrue(helper.record("foo", ((TestLogThrottlingHelper.LOG_PERIOD) * 2)).shouldLog());
        // The timing of "bar" shouldn't matter as it is dependent on "foo"
        Assert.assertTrue(helper.record("bar", 0).shouldLog());
    }

    @Test
    public void testMultipleLoggersWithValues() {
        helper = new LogThrottlingHelper(TestLogThrottlingHelper.LOG_PERIOD, "foo", timer);
        Assert.assertTrue(helper.record("foo", 0).shouldLog());
        Assert.assertTrue(helper.record("bar", 0, 2).shouldLog());
        Assert.assertTrue(helper.record("baz", 0, 3, 3).shouldLog());
        // "bar"/"baz" should not log yet because "foo" hasn't been triggered
        Assert.assertFalse(helper.record("bar", TestLogThrottlingHelper.LOG_PERIOD, 2).shouldLog());
        Assert.assertFalse(helper.record("baz", TestLogThrottlingHelper.LOG_PERIOD, 3, 3).shouldLog());
        // All should log once the period has elapsed
        LogAction foo = helper.record("foo", TestLogThrottlingHelper.LOG_PERIOD);
        LogAction bar = helper.record("bar", TestLogThrottlingHelper.LOG_PERIOD, 2);
        LogAction baz = helper.record("baz", TestLogThrottlingHelper.LOG_PERIOD, 3, 3);
        Assert.assertTrue(foo.shouldLog());
        Assert.assertTrue(bar.shouldLog());
        Assert.assertTrue(baz.shouldLog());
        Assert.assertEquals(1, foo.getCount());
        Assert.assertEquals(2, bar.getCount());
        Assert.assertEquals(2, baz.getCount());
        Assert.assertEquals(2.0, bar.getStats(0).getMean(), 0.01);
        Assert.assertEquals(3.0, baz.getStats(0).getMean(), 0.01);
        Assert.assertEquals(3.0, baz.getStats(1).getMean(), 0.01);
        Assert.assertEquals(2.0, helper.getCurrentStats("bar", 0).getMax(), 0);
        Assert.assertEquals(3.0, helper.getCurrentStats("baz", 0).getMax(), 0);
    }
}

