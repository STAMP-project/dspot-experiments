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


import StringValue.Builder;
import org.junit.Assert;
import org.junit.Test;


public class StringValueTest {
    private static final String CONTENT = "hello world";

    @Test
    public void testToBuilder() throws Exception {
        StringValue value = StringValue.of(StringValueTest.CONTENT);
        Assert.assertEquals(value, value.toBuilder().build());
    }

    @Test
    public void testOf() throws Exception {
        StringValue value = StringValue.of(StringValueTest.CONTENT);
        Assert.assertEquals(StringValueTest.CONTENT, value.get());
        Assert.assertFalse(value.excludeFromIndexes());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testBuilder() throws Exception {
        StringValue.Builder builder = StringValue.newBuilder(StringValueTest.CONTENT);
        StringValue value = builder.setMeaning(1).setExcludeFromIndexes(true).build();
        Assert.assertEquals(StringValueTest.CONTENT, value.get());
        Assert.assertEquals(1, value.getMeaning());
        Assert.assertTrue(value.excludeFromIndexes());
    }
}

