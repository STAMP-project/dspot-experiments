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
package com.gs.collections.impl.string.immutable;


import com.gs.collections.api.list.primitive.ImmutableCharList;
import com.gs.collections.impl.list.immutable.primitive.AbstractImmutableCharListTestCase;
import org.junit.Assert;
import org.junit.Test;


public class CharAdapterTest extends AbstractImmutableCharListTestCase {
    private static final String UNICODE_STRING = "\u3042\ud840\udc00\u3044\ud840\udc03\u3046\ud83d\ude09";

    @SuppressWarnings("StringBufferReplaceableByString")
    @Test
    public void stringBuilder() {
        CharAdapter adapt = CharAdapter.adapt(CharAdapterTest.UNICODE_STRING);
        Assert.assertEquals(CharAdapterTest.UNICODE_STRING, new StringBuilder(adapt).toString());
    }

    @Test
    public void subSequence() {
        CharAdapter adapt = CharAdapter.adapt(CharAdapterTest.UNICODE_STRING);
        CharSequence sequence = adapt.subSequence(1, 3);
        Assert.assertEquals(CharAdapterTest.UNICODE_STRING.subSequence(1, 3), sequence);
    }

    @Override
    @Test
    public void makeString() {
        ImmutableCharList list = this.classUnderTest();
        StringBuilder expectedString = new StringBuilder("");
        StringBuilder expectedString1 = new StringBuilder("");
        int size = list.size();
        for (char each = 0; each < size; each++) {
            expectedString.append(((char) (each + ((char) (1)))));
            expectedString1.append(((char) (each + ((char) (1)))));
            expectedString.append((each == (size - 1) ? "" : ", "));
            expectedString1.append((each == (size - 1) ? "" : "/"));
        }
        Assert.assertEquals(expectedString.toString(), list.makeString());
        Assert.assertEquals(expectedString1.toString(), list.makeString("/"));
    }

    @Override
    @Test
    public void appendString() {
        StringBuilder expectedString = new StringBuilder("");
        StringBuilder expectedString1 = new StringBuilder("");
        int size = this.classUnderTest().size();
        for (char each = 0; each < size; each++) {
            expectedString.append(((char) (each + ((char) (1)))));
            expectedString1.append(((char) (each + ((char) (1)))));
            expectedString.append((each == (size - 1) ? "" : ", "));
            expectedString1.append((each == (size - 1) ? "" : "/"));
        }
        ImmutableCharList list = this.classUnderTest();
        StringBuilder appendable2 = new StringBuilder();
        list.appendString(appendable2);
        Assert.assertEquals(expectedString.toString(), appendable2.toString());
        StringBuilder appendable3 = new StringBuilder();
        list.appendString(appendable3, "/");
        Assert.assertEquals(expectedString1.toString(), appendable3.toString());
    }

    @Override
    @Test
    public void testToString() {
        StringBuilder expectedString = new StringBuilder();
        int size = this.classUnderTest().size();
        for (char each = 0; each < size; each++) {
            expectedString.append(((char) (each + ((char) (1)))));
        }
        Assert.assertEquals(expectedString.toString(), this.classUnderTest().toString());
    }
}

