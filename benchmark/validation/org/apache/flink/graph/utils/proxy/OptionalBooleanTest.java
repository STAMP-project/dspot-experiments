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
package org.apache.flink.graph.utils.proxy;


import State.CONFLICTING;
import State.FALSE;
import State.TRUE;
import State.UNSET;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link OptionalBoolean}.
 */
public class OptionalBooleanTest {
    private OptionalBoolean u;

    private OptionalBoolean f;

    private OptionalBoolean t;

    private OptionalBoolean c;

    @Test
    public void testIsMismatchedWith() throws Exception {
        // unset, unset
        Assert.assertFalse(u.conflictsWith(u));
        // unset, false
        Assert.assertFalse(u.conflictsWith(f));
        // unset, true
        Assert.assertFalse(u.conflictsWith(t));
        // unset, conflicting
        Assert.assertTrue(u.conflictsWith(c));
        // false, unset
        Assert.assertFalse(f.conflictsWith(u));
        // false, false
        Assert.assertFalse(f.conflictsWith(f));
        // false, true
        Assert.assertTrue(f.conflictsWith(t));
        // false, conflicting
        Assert.assertTrue(f.conflictsWith(c));
        // true, unset
        Assert.assertFalse(t.conflictsWith(u));
        // true, false
        Assert.assertTrue(t.conflictsWith(f));
        // true, true
        Assert.assertFalse(t.conflictsWith(t));
        // true, conflicting
        Assert.assertTrue(t.conflictsWith(c));
        // conflicting, unset
        Assert.assertTrue(c.conflictsWith(u));
        // conflicting, false
        Assert.assertTrue(c.conflictsWith(f));
        // conflicting, true
        Assert.assertTrue(c.conflictsWith(t));
        // conflicting, conflicting
        Assert.assertTrue(c.conflictsWith(c));
    }

    @Test
    public void testMergeWith() throws Exception {
        // unset, unset => unset
        u.mergeWith(u);
        Assert.assertEquals(UNSET, u.getState());
        // unset, false => false
        u.mergeWith(f);
        Assert.assertEquals(FALSE, u.getState());
        u.unset();
        // unset, true => true
        u.mergeWith(t);
        Assert.assertEquals(TRUE, u.getState());
        u.unset();
        // unset, conflicting => conflicting
        u.mergeWith(c);
        Assert.assertEquals(CONFLICTING, u.getState());
        u.unset();
        // false, unset => false
        f.mergeWith(u);
        Assert.assertEquals(FALSE, f.getState());
        // false, false => false
        f.mergeWith(f);
        Assert.assertEquals(FALSE, f.getState());
        // false, true => conflicting
        f.mergeWith(t);
        Assert.assertEquals(CONFLICTING, f.getState());
        f.set(false);
        // false, conflicting => conflicting
        f.mergeWith(c);
        Assert.assertEquals(CONFLICTING, f.getState());
        f.set(false);
        // true, unset => true
        t.mergeWith(u);
        Assert.assertEquals(TRUE, t.getState());
        // true, false => conflicting
        t.mergeWith(f);
        Assert.assertEquals(CONFLICTING, t.getState());
        t.set(true);
        // true, true => true
        t.mergeWith(t);
        Assert.assertEquals(TRUE, t.getState());
        // true, conflicting => conflicting
        t.mergeWith(c);
        Assert.assertEquals(CONFLICTING, t.getState());
        t.set(true);
        // conflicting, unset => conflicting
        c.mergeWith(u);
        Assert.assertEquals(CONFLICTING, c.getState());
        // conflicting, false => conflicting
        c.mergeWith(f);
        Assert.assertEquals(CONFLICTING, c.getState());
        // conflicting, true => conflicting
        c.mergeWith(t);
        Assert.assertEquals(CONFLICTING, c.getState());
        // conflicting, conflicting => conflicting
        c.mergeWith(c);
        Assert.assertEquals(CONFLICTING, c.getState());
    }
}

