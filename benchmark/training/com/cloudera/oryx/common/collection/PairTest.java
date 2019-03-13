/**
 * Copyright (c) 2014, Cloudera, Inc. and Intel Corp. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.common.collection;


import com.cloudera.oryx.common.OryxTest;
import org.junit.Assert;
import org.junit.Test;


public final class PairTest extends OryxTest {
    @Test
    public void testEquals() {
        Assert.assertEquals(new Pair(3.0, "foo"), new Pair(3.0, "foo"));
        Assert.assertEquals(new Pair(null, null), new Pair(null, null));
        Assert.assertNotEquals(new Pair(3.0, "foo"), new Pair(4.0, "foo"));
        Assert.assertNotEquals(new Pair(3.0, "foo"), new Pair("foo", 3.0));
        Assert.assertNotEquals("3.0,foo", new Pair(3.0, "foo"));
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(new Pair(3.0, "foo").hashCode(), new Pair(3.0, "foo").hashCode());
        Assert.assertEquals(new Pair(null, null).hashCode(), new Pair(null, null).hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("3.0,foo", new Pair(3.0, "foo").toString());
    }
}

