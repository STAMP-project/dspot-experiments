/**
 * Copyright 2016 The Netty Project
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
package io.netty.handler.codec.serialization;


import io.netty.channel.embedded.EmbeddedChannel;
import java.io.IOException;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;


public class CompatibleObjectEncoderTest {
    @Test
    public void testMultipleEncodeReferenceCount() throws IOException, ClassNotFoundException {
        EmbeddedChannel channel = new EmbeddedChannel(new CompatibleObjectEncoder());
        CompatibleObjectEncoderTest.testEncode(channel, new CompatibleObjectEncoderTest.TestSerializable(6, 8));
        CompatibleObjectEncoderTest.testEncode(channel, new CompatibleObjectEncoderTest.TestSerializable(10, 5));
        CompatibleObjectEncoderTest.testEncode(channel, new CompatibleObjectEncoderTest.TestSerializable(1, 5));
        Assert.assertFalse(channel.finishAndReleaseAll());
    }

    private static final class TestSerializable implements Serializable {
        private static final long serialVersionUID = 2235771472534930360L;

        public final int x;

        public final int y;

        TestSerializable(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompatibleObjectEncoderTest.TestSerializable)) {
                return false;
            }
            CompatibleObjectEncoderTest.TestSerializable rhs = ((CompatibleObjectEncoderTest.TestSerializable) (o));
            return ((x) == (rhs.x)) && ((y) == (rhs.y));
        }

        @Override
        public int hashCode() {
            return (31 * (31 + (x))) + (y);
        }
    }
}

