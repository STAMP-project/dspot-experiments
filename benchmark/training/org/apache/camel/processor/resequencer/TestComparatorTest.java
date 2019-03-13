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
package org.apache.camel.processor.resequencer;


import org.junit.Assert;
import org.junit.Test;


public class TestComparatorTest extends Assert {
    private TestComparator c;

    private TestObject e1;

    private TestObject e2;

    private TestObject e3;

    @Test
    public void testPredecessor() {
        Assert.assertTrue(c.predecessor(e1, e2));
        Assert.assertFalse(c.predecessor(e2, e1));
        Assert.assertFalse(c.predecessor(e1, e3));
        Assert.assertFalse(c.predecessor(e3, e1));
        Assert.assertFalse(c.predecessor(e3, e3));
    }

    @Test
    public void testSuccessor() {
        Assert.assertTrue(c.successor(e2, e1));
        Assert.assertFalse(c.successor(e1, e2));
        Assert.assertFalse(c.successor(e3, e1));
        Assert.assertFalse(c.successor(e1, e3));
        Assert.assertFalse(c.successor(e3, e3));
    }

    @Test
    public void testCompare() {
        Assert.assertTrue(((c.compare(e1, e2)) < 0));
        Assert.assertTrue(((c.compare(e2, e1)) > 0));
        Assert.assertTrue(((c.compare(e1, e3)) < 0));
        Assert.assertTrue(((c.compare(e3, e1)) > 0));
        Assert.assertTrue(((c.compare(e3, e3)) == 0));
    }
}

