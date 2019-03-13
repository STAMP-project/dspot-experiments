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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.authz.permission;


import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 1.3
 */
public class DomainPermissionTest {
    @Test
    public void testDefaultConstructor() {
        DomainPermission p;
        List<Set<String>> parts;
        Set<String> set;
        String entry;
        // No arg constructor
        p = new DomainPermission();
        // Verify domain
        Assert.assertTrue("domain".equals(p.getDomain()));
        // Verify actions
        set = p.getActions();
        Assert.assertNull(set);
        // Verify targets
        set = p.getTargets();
        Assert.assertNull(set);
        // Verify parts
        parts = p.getParts();
        Assert.assertEquals("Number of parts", 1, parts.size());
        set = parts.get(0);
        Assert.assertEquals(1, set.size());
        entry = set.iterator().next();
        Assert.assertEquals("domain", entry);
    }

    @Test
    public void testActionsConstructorWithSingleAction() {
        DomainPermission p;
        List<Set<String>> parts;
        Set<String> set;
        Iterator<String> iterator;
        String entry;
        // Actions constructor with a single action
        p = new DomainPermission("action1");
        // Verify domain
        Assert.assertEquals("domain", p.getDomain());
        // Verify actions
        set = p.getActions();
        Assert.assertNotNull(set);
        Assert.assertEquals(1, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("action1", entry);
        // Verify targets
        set = p.getTargets();
        Assert.assertNull(set);
        // Verify parts
        parts = p.getParts();
        Assert.assertEquals(2, parts.size());
        set = parts.get(0);
        Assert.assertEquals(1, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("domain", entry);
        set = parts.get(1);
        Assert.assertEquals(1, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("action1", entry);
    }

    @Test
    public void testActionsConstructorWithMultipleActions() {
        DomainPermission p;
        List<Set<String>> parts;
        Set<String> set;
        Iterator<String> iterator;
        String entry;
        // Actions constructor with three actions
        p = new DomainPermission("action1,action2,action3");
        // Verify domain
        Assert.assertEquals("domain", p.getDomain());
        // Verify actions
        set = p.getActions();
        Assert.assertNotNull(set);
        Assert.assertEquals(3, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("action1", entry);
        entry = iterator.next();
        Assert.assertEquals("action2", entry);
        entry = iterator.next();
        Assert.assertEquals("action3", entry);
        // Verify targets
        set = p.getTargets();
        Assert.assertNull(set);
        // Verify parts
        parts = p.getParts();
        Assert.assertEquals(2, parts.size());
        set = parts.get(0);
        Assert.assertEquals(1, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("domain", entry);
        set = parts.get(1);
        Assert.assertEquals(3, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("action1", entry);
        entry = iterator.next();
        Assert.assertEquals("action2", entry);
        entry = iterator.next();
        Assert.assertEquals("action3", entry);
    }

    @Test
    public void testActionsTargetsConstructorWithSingleActionAndTarget() {
        DomainPermission p;
        List<Set<String>> parts;
        Set<String> set;
        Iterator<String> iterator;
        String entry;
        // Actions and target constructor with a single action and target
        p = new DomainPermission("action1", "target1");
        // Verify domain
        Assert.assertEquals("domain", p.getDomain());
        // Verify actions
        set = p.getActions();
        Assert.assertNotNull(set);
        Assert.assertEquals(1, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("action1", entry);
        // Verify targets
        set = p.getTargets();
        Assert.assertNotNull(set);
        Assert.assertEquals(1, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("target1", entry);
        // Verify parts
        parts = p.getParts();
        Assert.assertEquals(3, parts.size());
        set = parts.get(0);
        Assert.assertEquals(1, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("domain", entry);
        set = parts.get(1);
        Assert.assertEquals(1, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("action1", entry);
        set = parts.get(2);
        Assert.assertEquals(1, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("target1", entry);
    }

    @Test
    public void testActionsTargetsConstructorWithMultipleActionsAndTargets() {
        DomainPermission p;
        List<Set<String>> parts;
        Set<String> set;
        Iterator<String> iterator;
        String entry;
        // Actions and target constructor with a single action and target
        p = new DomainPermission("action1,action2,action3", "target1,target2,target3");
        // Verify domain
        Assert.assertEquals("domain", p.getDomain());
        // Verify actions
        set = p.getActions();
        Assert.assertNotNull(set);
        Assert.assertEquals(3, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("action1", entry);
        entry = iterator.next();
        Assert.assertEquals("action2", entry);
        entry = iterator.next();
        Assert.assertEquals("action3", entry);
        // Verify targets
        set = p.getTargets();
        Assert.assertNotNull(set);
        Assert.assertEquals(3, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("target1", entry);
        entry = iterator.next();
        Assert.assertEquals("target2", entry);
        entry = iterator.next();
        Assert.assertEquals("target3", entry);
        // Verify parts
        parts = p.getParts();
        Assert.assertEquals(3, parts.size());
        set = parts.get(0);
        Assert.assertEquals(1, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("domain", entry);
        set = parts.get(1);
        Assert.assertEquals(3, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("action1", entry);
        entry = iterator.next();
        Assert.assertEquals("action2", entry);
        entry = iterator.next();
        Assert.assertEquals("action3", entry);
        set = parts.get(2);
        Assert.assertEquals(3, set.size());
        iterator = set.iterator();
        entry = iterator.next();
        Assert.assertEquals("target1", entry);
        entry = iterator.next();
        Assert.assertEquals("target2", entry);
        entry = iterator.next();
        Assert.assertEquals("target3", entry);
    }
}

