/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.core;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class SimpleAliasRegistryTests {
    @Test
    public void testAliasChaining() {
        SimpleAliasRegistry registry = new SimpleAliasRegistry();
        registry.registerAlias("test", "testAlias");
        registry.registerAlias("testAlias", "testAlias2");
        registry.registerAlias("testAlias2", "testAlias3");
        Assert.assertTrue(registry.hasAlias("test", "testAlias"));
        Assert.assertTrue(registry.hasAlias("test", "testAlias2"));
        Assert.assertTrue(registry.hasAlias("test", "testAlias3"));
        Assert.assertSame("test", registry.canonicalName("testAlias"));
        Assert.assertSame("test", registry.canonicalName("testAlias2"));
        Assert.assertSame("test", registry.canonicalName("testAlias3"));
    }

    // SPR-17191
    @Test
    public void testAliasChainingWithMultipleAliases() {
        SimpleAliasRegistry registry = new SimpleAliasRegistry();
        registry.registerAlias("name", "alias_a");
        registry.registerAlias("name", "alias_b");
        Assert.assertTrue(registry.hasAlias("name", "alias_a"));
        Assert.assertTrue(registry.hasAlias("name", "alias_b"));
        registry.registerAlias("real_name", "name");
        Assert.assertTrue(registry.hasAlias("real_name", "name"));
        Assert.assertTrue(registry.hasAlias("real_name", "alias_a"));
        Assert.assertTrue(registry.hasAlias("real_name", "alias_b"));
        registry.registerAlias("name", "alias_c");
        Assert.assertTrue(registry.hasAlias("real_name", "name"));
        Assert.assertTrue(registry.hasAlias("real_name", "alias_a"));
        Assert.assertTrue(registry.hasAlias("real_name", "alias_b"));
        Assert.assertTrue(registry.hasAlias("real_name", "alias_c"));
    }
}

