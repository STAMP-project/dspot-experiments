/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.buffer;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractPooledByteBufTest extends AbstractByteBufTest {
    @Test
    public void ensureWritableWithEnoughSpaceShouldNotThrow() {
        ByteBuf buf = newBuffer(1, 10);
        buf.ensureWritable(3);
        MatcherAssert.assertThat(buf.writableBytes(), Matchers.is(Matchers.greaterThanOrEqualTo(3)));
        buf.release();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void ensureWritableWithNotEnoughSpaceShouldThrow() {
        ByteBuf buf = newBuffer(1, 10);
        try {
            buf.ensureWritable(11);
            Assert.fail();
        } finally {
            buf.release();
        }
    }
}

