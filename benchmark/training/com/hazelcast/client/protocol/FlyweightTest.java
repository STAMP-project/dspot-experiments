/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.client.protocol;


import com.hazelcast.client.impl.protocol.util.MessageFlyweight;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.nio.ByteBuffer;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Flyweight Tests
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class FlyweightTest {
    private static final byte[] DATA = new byte[]{ ((byte) (97)), ((byte) (98)), ((byte) (99)), ((byte) (194)), ((byte) (169)), ((byte) (226)), ((byte) (152)), ((byte) (186)) };

    private MessageFlyweight flyweight = new MessageFlyweight();

    private ByteBuffer byteBuffer;

    @Test
    public void shouldEncodeLong() {
        flyweight.set(305419896L);
        Assert.assertEquals(8, flyweight.index());
        Assert.assertThat(byteBuffer.get(0), Is.is(((byte) (120))));
        Assert.assertThat(byteBuffer.get(1), Is.is(((byte) (86))));
        Assert.assertThat(byteBuffer.get(2), Is.is(((byte) (52))));
        Assert.assertThat(byteBuffer.get(3), Is.is(((byte) (18))));
    }

    @Test
    public void shouldEncodeInt() {
        flyweight.set(4660);
        Assert.assertEquals(4, flyweight.index());
        Assert.assertThat(byteBuffer.get(0), Is.is(((byte) (52))));
        Assert.assertThat(byteBuffer.get(1), Is.is(((byte) (18))));
    }

    @Test
    public void shouldEncodeBoolean() {
        flyweight.set(true);
        Assert.assertEquals(1, flyweight.index());
        Assert.assertThat(byteBuffer.get(0), Is.is(((byte) (1))));
    }

    @Test
    public void shouldEncodeStringUtf8() {
        // 0x61 0x62 0x63 0xC2 0xA9 0xE2 0x98 0xBA
        flyweight.set("abc??");
        Assert.assertEquals(12, flyweight.index());
        Assert.assertThat(byteBuffer.get(0), Is.is(((byte) (8))));
        Assert.assertThat(byteBuffer.get(1), Is.is(((byte) (0))));
        Assert.assertThat(byteBuffer.get(2), Is.is(((byte) (0))));
        Assert.assertThat(byteBuffer.get(3), Is.is(((byte) (0))));
        Assert.assertThat(byteBuffer.get(4), Is.is(((byte) (97))));
        Assert.assertThat(byteBuffer.get(5), Is.is(((byte) (98))));
        Assert.assertThat(byteBuffer.get(6), Is.is(((byte) (99))));
        Assert.assertThat(byteBuffer.get(7), Is.is(((byte) (194))));
        Assert.assertThat(byteBuffer.get(8), Is.is(((byte) (169))));
        Assert.assertThat(byteBuffer.get(9), Is.is(((byte) (226))));
        Assert.assertThat(byteBuffer.get(10), Is.is(((byte) (152))));
        Assert.assertThat(byteBuffer.get(11), Is.is(((byte) (186))));
    }

    @Test
    public void shouldEncodeByteArray() {
        byte[] data = new byte[]{ ((byte) (97)), ((byte) (98)), ((byte) (99)), ((byte) (194)), ((byte) (169)), ((byte) (226)), ((byte) (152)), ((byte) (186)) };
        flyweight.set(data);
        Assert.assertEquals(12, flyweight.index());
        Assert.assertThat(byteBuffer.get(0), Is.is(((byte) (8))));
        Assert.assertThat(byteBuffer.get(1), Is.is(((byte) (0))));
        Assert.assertThat(byteBuffer.get(2), Is.is(((byte) (0))));
        Assert.assertThat(byteBuffer.get(3), Is.is(((byte) (0))));
        Assert.assertThat(byteBuffer.get(4), Is.is(((byte) (97))));
        Assert.assertThat(byteBuffer.get(5), Is.is(((byte) (98))));
        Assert.assertThat(byteBuffer.get(6), Is.is(((byte) (99))));
        Assert.assertThat(byteBuffer.get(7), Is.is(((byte) (194))));
        Assert.assertThat(byteBuffer.get(8), Is.is(((byte) (169))));
        Assert.assertThat(byteBuffer.get(9), Is.is(((byte) (226))));
        Assert.assertThat(byteBuffer.get(10), Is.is(((byte) (152))));
        Assert.assertThat(byteBuffer.get(11), Is.is(((byte) (186))));
    }

    @Test
    public void shouldDecodeLong() {
        flyweight.set(305419896L);
        flyweight.index(0);
        Assert.assertEquals(305419896L, flyweight.getLong());
    }

    @Test
    public void shouldDecodeInt() {
        flyweight.set(4660);
        flyweight.index(0);
        Assert.assertEquals(4660, flyweight.getInt());
        Assert.assertEquals(4, flyweight.index());
    }

    @Test
    public void shouldDecodeBoolean() {
        flyweight.set(true);
        flyweight.index(0);
        Assert.assertTrue(flyweight.getBoolean());
        Assert.assertEquals(1, flyweight.index());
    }

    @Test
    public void shouldDecodeStringUtf8() {
        final String staticValue = "abc??";
        flyweight.set(staticValue);
        flyweight.index(0);
        Assert.assertThat(flyweight.getStringUtf8(), Is.is(staticValue));
        Assert.assertEquals(12, flyweight.index());
    }

    @Test
    public void shouldDecodeByteArray() {
        flyweight.set(FlyweightTest.DATA);
        flyweight.index(0);
        Assert.assertArrayEquals(FlyweightTest.DATA, flyweight.getByteArray());
        Assert.assertEquals((4 + (FlyweightTest.DATA.length)), flyweight.index());
    }

    @Test
    public void shouldDecodeData() {
        flyweight.set(FlyweightTest.DATA);
        flyweight.index(0);
        Assert.assertArrayEquals(FlyweightTest.DATA, flyweight.getData().toByteArray());
        Assert.assertEquals((4 + (FlyweightTest.DATA.length)), flyweight.index());
    }

    @Test
    public void shouldEncodeDecodeMultipleData() {
        flyweight.set(305419896L);
        flyweight.set(4660);
        flyweight.set(((short) (18)));
        flyweight.set(true);
        flyweight.set(FlyweightTest.DATA);
    }
}

