/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore;


import TimestampValue.Builder;
import com.google.cloud.Timestamp;
import org.junit.Assert;
import org.junit.Test;


public class TimestampValueTest {
    private static final Timestamp CONTENT = Timestamp.now();

    @Test
    public void testToBuilder() throws Exception {
        TimestampValue value = TimestampValue.of(TimestampValueTest.CONTENT);
        Assert.assertEquals(value, value.toBuilder().build());
    }

    @Test
    public void testOf() throws Exception {
        TimestampValue value = TimestampValue.of(TimestampValueTest.CONTENT);
        Assert.assertEquals(TimestampValueTest.CONTENT, value.get());
        Assert.assertFalse(value.excludeFromIndexes());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testBuilder() throws Exception {
        TimestampValue.Builder builder = TimestampValue.newBuilder(TimestampValueTest.CONTENT);
        TimestampValue value = builder.setMeaning(1).setExcludeFromIndexes(true).build();
        Assert.assertEquals(TimestampValueTest.CONTENT, value.get());
        Assert.assertEquals(1, value.getMeaning());
        Assert.assertTrue(value.excludeFromIndexes());
    }
}

