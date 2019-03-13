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
package com.gs.collections.impl.list.mutable;


import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.test.Verify;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;


public class MultiReaderFastListAsWriteUntouchableTest extends AbstractListTestCase {
    @Override
    @Test
    public void serialization() {
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4, 5);
        Assert.assertFalse((collection instanceof Serializable));
    }

    @Override
    @Test
    public void asSynchronized() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.newWith().asUnmodifiable());
    }

    @Override
    @Test
    public void testToString() {
        Assert.assertEquals("[1, 2, 3]", this.newWith(1, 2, 3).toString());
    }

    @Override
    @Test
    public void makeString() {
        Assert.assertEquals("1, 2, 3", this.newWith(1, 2, 3).makeString());
    }

    @Override
    @Test
    public void appendString() {
        Appendable builder = new StringBuilder();
        this.newWith(1, 2, 3).appendString(builder);
        Assert.assertEquals("1, 2, 3", builder.toString());
    }
}

