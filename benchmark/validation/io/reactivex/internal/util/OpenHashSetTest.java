/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package io.reactivex.internal.util;


import org.junit.Assert;
import org.junit.Test;


public class OpenHashSetTest {
    static class Value {
        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return (this) == o;
        }
    }

    @Test
    public void addRemoveCollision() {
        OpenHashSetTest.Value v1 = new OpenHashSetTest.Value();
        OpenHashSetTest.Value v2 = new OpenHashSetTest.Value();
        OpenHashSet<OpenHashSetTest.Value> set = new OpenHashSet<OpenHashSetTest.Value>();
        Assert.assertTrue(set.add(v1));
        Assert.assertFalse(set.add(v1));
        Assert.assertFalse(set.remove(v2));
        Assert.assertTrue(set.add(v2));
        Assert.assertFalse(set.add(v2));
        Assert.assertTrue(set.remove(v2));
        Assert.assertFalse(set.remove(v2));
    }
}

