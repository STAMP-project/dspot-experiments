/**
 * Copyright 2015 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.block.function;


import Maps.mutable;
import MinSizeFunction.COLLECTION;
import MinSizeFunction.MAP;
import com.gs.collections.impl.list.mutable.FastList;
import org.junit.Assert;
import org.junit.Test;


/**
 * Junit test for {@link MinSizeFunction}.
 */
public class MinSizeFunctionTest {
    @Test
    public void minSizeCollection() {
        Assert.assertEquals(Integer.valueOf(2), COLLECTION.value(2, FastList.newListWith(1, 2, 3)));
        Assert.assertEquals(Integer.valueOf(2), COLLECTION.value(3, FastList.newListWith(1, 2)));
    }

    @Test
    public void minSizeMap() {
        Assert.assertEquals(Integer.valueOf(2), MAP.value(2, mutable.of(1, 1, 2, 2, 3, 3)));
        Assert.assertEquals(Integer.valueOf(2), MAP.value(3, mutable.of(1, 1, 2, 2)));
    }
}

