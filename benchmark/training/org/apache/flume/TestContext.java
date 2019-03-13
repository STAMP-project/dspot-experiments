/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume;


import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;


public class TestContext {
    private Context context;

    @Test
    public void testPutGet() {
        Assert.assertEquals("Context is empty", 0, context.getParameters().size());
        context.put("test", "value");
        Assert.assertEquals("value", context.getString("test"));
        context.clear();
        Assert.assertNull(context.getString("test"));
        Assert.assertEquals("value", context.getString("test", "value"));
        context.put("test", "true");
        Assert.assertEquals(new Boolean(true), context.getBoolean("test"));
        context.clear();
        Assert.assertNull(context.getBoolean("test"));
        Assert.assertEquals(new Boolean(true), context.getBoolean("test", true));
        context.put("test", "1");
        Assert.assertEquals(new Integer(1), context.getInteger("test"));
        context.clear();
        Assert.assertNull(context.getInteger("test"));
        Assert.assertEquals(new Integer(1), context.getInteger("test", 1));
        context.put("test", String.valueOf(Long.MAX_VALUE));
        Assert.assertEquals(new Long(Long.MAX_VALUE), context.getLong("test"));
        context.clear();
        Assert.assertNull(context.getLong("test"));
        Assert.assertEquals(new Long(Long.MAX_VALUE), context.getLong("test", Long.MAX_VALUE));
        context.put("test", "0.1");
        Assert.assertEquals(new Float(0.1), context.getFloat("test"));
        context.clear();
        Assert.assertNull(context.getFloat("test"));
        Assert.assertEquals(new Float(1.1), context.getFloat("test", 1.1F));
        context.put("test", "0.1");
        Assert.assertEquals(new Double(0.1), context.getDouble("test"));
        context.clear();
        Assert.assertNull(context.getDouble("test"));
        Assert.assertEquals(new Double(1.1), context.getDouble("test", 1.1));
    }

    @Test
    public void testSubProperties() {
        context.put("my.key", "1");
        context.put("otherKey", "otherValue");
        Assert.assertEquals(ImmutableMap.of("key", "1"), context.getSubProperties("my."));
    }

    @Test
    public void testClear() {
        context.put("test", "1");
        context.clear();
        Assert.assertNull(context.getInteger("test"));
    }

    @Test
    public void testPutAll() {
        context.putAll(ImmutableMap.of("test", "1"));
        Assert.assertEquals("1", context.getString("test"));
    }
}

