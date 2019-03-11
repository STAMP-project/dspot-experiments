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
package org.apache.activemq.util;


import org.junit.Assert;
import org.junit.Test;


public class StringArrayConverterTest {
    @Test
    public void testConvertToStringArray() throws Exception {
        Assert.assertNull(StringArrayConverter.convertToStringArray(null));
        Assert.assertNull(StringArrayConverter.convertToStringArray(""));
        String[] array = StringArrayConverter.convertToStringArray("foo");
        Assert.assertEquals(1, array.length);
        Assert.assertEquals("foo", array[0]);
        array = StringArrayConverter.convertToStringArray("foo,bar");
        Assert.assertEquals(2, array.length);
        Assert.assertEquals("foo", array[0]);
        Assert.assertEquals("bar", array[1]);
        array = StringArrayConverter.convertToStringArray("foo,bar,baz");
        Assert.assertEquals(3, array.length);
        Assert.assertEquals("foo", array[0]);
        Assert.assertEquals("bar", array[1]);
        Assert.assertEquals("baz", array[2]);
    }

    @Test
    public void testConvertToString() throws Exception {
        Assert.assertEquals(null, StringArrayConverter.convertToString(null));
        Assert.assertEquals(null, StringArrayConverter.convertToString(new String[]{  }));
        Assert.assertEquals("", StringArrayConverter.convertToString(new String[]{ "" }));
        Assert.assertEquals("foo", StringArrayConverter.convertToString(new String[]{ "foo" }));
        Assert.assertEquals("foo,bar", StringArrayConverter.convertToString(new String[]{ "foo", "bar" }));
        Assert.assertEquals("foo,bar,baz", StringArrayConverter.convertToString(new String[]{ "foo", "bar", "baz" }));
    }
}

