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
package org.agrona;


import java.nio.ByteBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BufferCompareTest {
    @Test
    public void shouldEqualOnSameTypeAndValue() {
        final MutableDirectBuffer lhsBuffer = new ExpandableArrayBuffer();
        final MutableDirectBuffer rhsBuffer = new ExpandableArrayBuffer();
        lhsBuffer.putStringUtf8(0, "Hello World");
        rhsBuffer.putStringUtf8(0, "Hello World");
        Assert.assertThat(lhsBuffer.compareTo(rhsBuffer), Matchers.is(0));
    }

    @Test
    public void shouldEqualOnDifferentTypeAndValue() {
        final MutableDirectBuffer lhsBuffer = new ExpandableArrayBuffer();
        final MutableDirectBuffer rhsBuffer = new org.agrona.concurrent.UnsafeBuffer(ByteBuffer.allocateDirect(lhsBuffer.capacity()));
        lhsBuffer.putStringUtf8(0, "Hello World");
        rhsBuffer.putStringUtf8(0, "Hello World");
        Assert.assertThat(lhsBuffer.compareTo(rhsBuffer), Matchers.is(0));
    }

    @Test
    public void shouldEqualOnDifferentExpandableTypeAndValue() {
        final MutableDirectBuffer lhsBuffer = new ExpandableArrayBuffer();
        final MutableDirectBuffer rhsBuffer = new ExpandableDirectByteBuffer();
        lhsBuffer.putStringUtf8(0, "Hello World");
        rhsBuffer.putStringUtf8(0, "Hello World");
        Assert.assertThat(lhsBuffer.compareTo(rhsBuffer), Matchers.is(0));
    }

    @Test
    public void shouldBeGreater() {
        final MutableDirectBuffer lhsBuffer = new ExpandableArrayBuffer();
        final MutableDirectBuffer rhsBuffer = new ExpandableArrayBuffer();
        lhsBuffer.putStringUtf8(0, "123");
        rhsBuffer.putStringUtf8(0, "124");
        Assert.assertThat(lhsBuffer.compareTo(rhsBuffer), Matchers.lessThan(0));
    }

    @Test
    public void shouldBeLess() {
        final MutableDirectBuffer lhsBuffer = new ExpandableArrayBuffer();
        final MutableDirectBuffer rhsBuffer = new ExpandableArrayBuffer();
        lhsBuffer.putStringUtf8(0, "124");
        rhsBuffer.putStringUtf8(0, "123");
        Assert.assertThat(lhsBuffer.compareTo(rhsBuffer), Matchers.greaterThan(0));
    }
}

