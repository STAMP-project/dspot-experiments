/**
 * Copyright 2014 Goldman Sachs.
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
package com.gs.collections.impl.list.immutable.primitive;


import org.junit.Assert;
import org.junit.Test;


public class ImmutableBooleanSingletonListTest extends AbstractImmutableBooleanListTestCase {
    @Override
    @Test
    public void testEquals() {
        super.testEquals();
        Assert.assertNotEquals(this.newWith(true), this.newWith());
    }

    @Override
    @Test
    public void toReversed() {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().toReversed());
    }

    @Override
    @Test
    public void forEachWithIndex() {
        String[] sum = new String[2];
        sum[0] = "";
        this.classUnderTest().forEachWithIndex(( each, index) -> sum[0] += (index + ":") + each);
        Assert.assertEquals("0:true", sum[0]);
    }
}

