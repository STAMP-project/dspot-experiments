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


import LongValue.Builder;
import org.junit.Assert;
import org.junit.Test;


public class LongValueTest {
    private static final Long CONTENT = 125L;

    @Test
    public void testToBuilder() throws Exception {
        LongValue value = LongValue.of(LongValueTest.CONTENT);
        Assert.assertEquals(value, value.toBuilder().build());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testOf() throws Exception {
        LongValue value = LongValue.of(LongValueTest.CONTENT);
        Assert.assertEquals(LongValueTest.CONTENT, value.get());
        Assert.assertFalse(value.excludeFromIndexes());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testBuilder() throws Exception {
        LongValue.Builder builder = LongValue.newBuilder(LongValueTest.CONTENT);
        LongValue value = builder.setMeaning(1).setExcludeFromIndexes(true).build();
        Assert.assertEquals(LongValueTest.CONTENT, value.get());
        Assert.assertEquals(1, value.getMeaning());
        Assert.assertTrue(value.excludeFromIndexes());
    }
}

