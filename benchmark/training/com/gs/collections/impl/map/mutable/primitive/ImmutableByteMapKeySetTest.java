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
package com.gs.collections.impl.map.mutable.primitive;


import com.gs.collections.api.set.primitive.ByteSet;
import com.gs.collections.impl.set.mutable.primitive.AbstractImmutableByteHashSetTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link ImmutableByteSet} created from the freeze() method.
 */
public class ImmutableByteMapKeySetTest extends AbstractImmutableByteHashSetTestCase {
    @Test
    @Override
    public void contains() {
        super.contains();
        byte collision1 = ImmutableByteMapKeySetTest.generateCollisions().getFirst();
        byte collision2 = ImmutableByteMapKeySetTest.generateCollisions().get(1);
        ByteShortHashMap byteShortHashMap = ByteShortHashMap.newWithKeysValues(collision1, ((short) (0)), collision2, ((short) (0)));
        byteShortHashMap.removeKey(collision2);
        ByteSet byteSet = byteShortHashMap.keySet().freeze();
        Assert.assertTrue(byteSet.contains(collision1));
        Assert.assertFalse(byteSet.contains(collision2));
    }
}

