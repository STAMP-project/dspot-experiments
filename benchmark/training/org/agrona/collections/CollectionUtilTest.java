/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package org.agrona.collections;


import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CollectionUtilTest {
    @Test
    public void removeIfRemovesMiddle() {
        assertRemoveIfRemoves(1, 2, 3);
    }

    @Test
    public void removeIfRemovesStart() {
        assertRemoveIfRemoves(2, 1, 3);
    }

    @Test
    public void removeIfRemovesEnd() {
        assertRemoveIfRemoves(3, 1, 2);
    }

    @Test
    public void getOrDefaultUsesSupplier() {
        final Map<Integer, Integer> ints = new HashMap<>();
        final Integer result = CollectionUtil.getOrDefault(ints, 0, ( x) -> x + 1);
        Assert.assertThat(result, Matchers.is(1));
    }

    @Test
    public void getOrDefaultDoesNotCreateNewValueWhenOneExists() {
        final Map<Integer, Integer> ints = new HashMap<>();
        ints.put(0, 0);
        final Integer result = CollectionUtil.getOrDefault(ints, 0, ( x) -> {
            Assert.fail("Shouldn't be called");
            return x + 1;
        });
        Assert.assertThat(result, Matchers.is(0));
    }
}

