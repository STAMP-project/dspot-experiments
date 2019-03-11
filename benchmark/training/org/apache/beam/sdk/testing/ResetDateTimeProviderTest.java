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
package org.apache.beam.sdk.testing;


import org.joda.time.DateTimeUtils;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ResetDateTimeProvider}.
 */
@RunWith(JUnit4.class)
public class ResetDateTimeProviderTest {
    private static final String TEST_TIME = "2014-12-08T19:07:06.698Z";

    private static final long TEST_TIME_MS = ISODateTimeFormat.dateTime().parseMillis(ResetDateTimeProviderTest.TEST_TIME);

    @Rule
    public ResetDateTimeProvider resetDateTimeProviderRule = new ResetDateTimeProvider();

    /* Since these tests can run out of order, both test A and B change the provider
    and verify that the provider was reset.
     */
    @Test
    public void testResetA() {
        Assert.assertNotEquals(ResetDateTimeProviderTest.TEST_TIME_MS, DateTimeUtils.currentTimeMillis());
        resetDateTimeProviderRule.setDateTimeFixed(ResetDateTimeProviderTest.TEST_TIME);
        Assert.assertEquals(ResetDateTimeProviderTest.TEST_TIME_MS, DateTimeUtils.currentTimeMillis());
    }

    @Test
    public void testResetB() {
        Assert.assertNotEquals(ResetDateTimeProviderTest.TEST_TIME_MS, DateTimeUtils.currentTimeMillis());
        resetDateTimeProviderRule.setDateTimeFixed(ResetDateTimeProviderTest.TEST_TIME);
        Assert.assertEquals(ResetDateTimeProviderTest.TEST_TIME_MS, DateTimeUtils.currentTimeMillis());
    }
}

