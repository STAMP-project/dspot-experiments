/**
 * Copyright (c) 2016, Cloudera, Inc. All Rights Reserved.
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


import Pairs.SortOrder.ASCENDING;
import Pairs.SortOrder.DESCENDING;
import com.cloudera.oryx.common.OryxTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Pairs}.
 */
public final class PairsTest extends OryxTest {
    @Test
    public void testOrderBy() {
        Pair<Double, String> a = new Pair(1.0, "foo");
        Pair<Double, String> b = new Pair(2.0, "bar");
        Assert.assertEquals(0, Pairs.<Double, String>orderByFirst(ASCENDING).compare(a, a));
        Assert.assertEquals(0, Pairs.<Double, String>orderByFirst(DESCENDING).compare(a, a));
        Assert.assertEquals(0, Pairs.<Double, String>orderBySecond(ASCENDING).compare(a, a));
        Assert.assertEquals(0, Pairs.<Double, String>orderBySecond(DESCENDING).compare(a, a));
        Assert.assertTrue(((Pairs.<Double, String>orderByFirst(ASCENDING).compare(a, b)) < 0));
        Assert.assertTrue(((Pairs.<Double, String>orderByFirst(DESCENDING).compare(a, b)) > 0));
        Assert.assertTrue(((Pairs.<Double, String>orderBySecond(ASCENDING).compare(a, b)) > 0));
        Assert.assertTrue(((Pairs.<Double, String>orderBySecond(DESCENDING).compare(a, b)) < 0));
    }
}

