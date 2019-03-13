/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.classmap;


import com.github.dozermapper.core.AbstractDozerTest;
import org.junit.Assert;
import org.junit.Test;


public class ClassMapKeyFactoryTest extends AbstractDozerTest {
    private ClassMapKeyFactory factory;

    @Test
    public void testCreateKey() {
        String key1 = factory.createKey(String.class, Long.class);
        String key2 = factory.createKey(String.class, Long.class);
        Assert.assertEquals(key1, key2);
    }

    @Test
    public void testCreateKey_Order() {
        String key1 = factory.createKey(String.class, Long.class);
        String key2 = factory.createKey(Long.class, String.class);
        Assert.assertNotSame(key1, key2);
        Assert.assertFalse(key1.equals(key2));
    }

    @Test
    public void testCreateKey_MapId() {
        String key1 = factory.createKey(String.class, Long.class, "id");
        String key2 = factory.createKey(String.class, Long.class);
        Assert.assertNotSame(key1, key2);
        Assert.assertFalse(key1.equals(key2));
    }

    @Test
    public void testCreateKey_MapIdNull() {
        String key = factory.createKey(String.class, Long.class, null);
        Assert.assertNotNull(key);
    }
}

