/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.util;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MutableLongTest {
    @Test
    public void testToString() {
        MutableLong mutableLong = new MutableLong();
        String s = mutableLong.toString();
        Assert.assertEquals("MutableLong{value=0}", s);
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(MutableLong.valueOf(0), MutableLong.valueOf(0));
        Assert.assertEquals(MutableLong.valueOf(10), MutableLong.valueOf(10));
        Assert.assertNotEquals(MutableLong.valueOf(0), MutableLong.valueOf(10));
        Assert.assertFalse(MutableLong.valueOf(0).equals(null));
        Assert.assertFalse(MutableLong.valueOf(0).equals("foo"));
        MutableLong self = MutableLong.valueOf(0);
        Assert.assertEquals(self, self);
    }

    @Test
    public void testHash() {
        Assert.assertEquals(MutableLong.valueOf(10).hashCode(), MutableLong.valueOf(10).hashCode());
    }
}

