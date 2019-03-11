/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util;


import org.junit.Assert;
import org.junit.Test;


public class DefaultAttributeMapTest {
    private DefaultAttributeMap map;

    @Test
    public void testMapExists() {
        Assert.assertNotNull(map);
    }

    @Test
    public void testGetSetString() {
        AttributeKey<String> key = AttributeKey.valueOf("Nothing");
        Attribute<String> one = map.attr(key);
        Assert.assertSame(one, map.attr(key));
        one.setIfAbsent("Whoohoo");
        Assert.assertSame("Whoohoo", one.get());
        one.setIfAbsent("What");
        Assert.assertNotSame("What", one.get());
        one.remove();
        Assert.assertNull(one.get());
    }

    @Test
    public void testGetSetInt() {
        AttributeKey<Integer> key = AttributeKey.valueOf("Nada");
        Attribute<Integer> one = map.attr(key);
        Assert.assertSame(one, map.attr(key));
        one.setIfAbsent(3653);
        Assert.assertEquals(Integer.valueOf(3653), one.get());
        one.setIfAbsent(1);
        Assert.assertNotSame(1, one.get());
        one.remove();
        Assert.assertNull(one.get());
    }

    // See https://github.com/netty/netty/issues/2523
    @Test
    public void testSetRemove() {
        AttributeKey<Integer> key = AttributeKey.valueOf("key");
        Attribute<Integer> attr = map.attr(key);
        attr.set(1);
        Assert.assertSame(1, attr.getAndRemove());
        Attribute<Integer> attr2 = map.attr(key);
        attr2.set(2);
        Assert.assertSame(2, attr2.get());
        Assert.assertNotSame(attr, attr2);
    }

    @Test
    public void testGetAndSetWithNull() {
        AttributeKey<Integer> key = AttributeKey.valueOf("key");
        Attribute<Integer> attr = map.attr(key);
        attr.set(1);
        Assert.assertSame(1, attr.getAndSet(null));
        Attribute<Integer> attr2 = map.attr(key);
        attr2.set(2);
        Assert.assertSame(2, attr2.get());
        Assert.assertSame(attr, attr2);
    }
}

