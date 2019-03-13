/**
 * Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.nio;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class ByteBufferWrapperTest {
    @Test
    public void wrapsIntoShortBuffer() {
        byte[] array = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.limit(80);
        buffer.get(new byte[10]);
        buffer = buffer.slice();
        buffer.put(0, ((byte) (35)));
        buffer.put(1, ((byte) (36)));
        ShortBuffer wrapper = buffer.asShortBuffer();
        Assert.assertThat(wrapper.capacity(), CoreMatchers.is(35));
        Assert.assertThat(wrapper.position(), CoreMatchers.is(0));
        Assert.assertThat(wrapper.limit(), CoreMatchers.is(35));
        Assert.assertThat(wrapper.get(0), CoreMatchers.is(((short) (8996))));
        wrapper.put(0, ((short) (9510)));
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((byte) (37))));
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) (38))));
    }

    @Test
    public void wrapsIntoIntBuffer() {
        byte[] array = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.limit(70);
        buffer.get(new byte[10]);
        buffer = buffer.slice();
        buffer.put(0, ((byte) (35)));
        buffer.put(1, ((byte) (36)));
        buffer.put(2, ((byte) (37)));
        IntBuffer wrapper = buffer.asIntBuffer();
        Assert.assertThat(wrapper.capacity(), CoreMatchers.is(15));
        Assert.assertThat(wrapper.position(), CoreMatchers.is(0));
        Assert.assertThat(wrapper.limit(), CoreMatchers.is(15));
        Assert.assertThat(wrapper.get(0), CoreMatchers.is(589571328));
        wrapper.put(0, 640100393);
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((byte) (38))));
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) (39))));
        Assert.assertThat(buffer.get(2), CoreMatchers.is(((byte) (40))));
        Assert.assertThat(buffer.get(3), CoreMatchers.is(((byte) (41))));
    }

    @Test
    public void wrapsIntoLongBuffer() {
        byte[] array = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.limit(50);
        buffer.get(new byte[10]);
        buffer = buffer.slice();
        buffer.put(0, ((byte) (35)));
        buffer.put(1, ((byte) (36)));
        buffer.put(2, ((byte) (37)));
        buffer.put(3, ((byte) (38)));
        buffer.put(4, ((byte) (39)));
        buffer.put(5, ((byte) (40)));
        buffer.put(6, ((byte) (41)));
        buffer.put(7, ((byte) (42)));
        LongBuffer wrapper = buffer.asLongBuffer();
        Assert.assertThat(wrapper.capacity(), CoreMatchers.is(5));
        Assert.assertThat(wrapper.position(), CoreMatchers.is(0));
        Assert.assertThat(wrapper.limit(), CoreMatchers.is(5));
        Assert.assertThat(wrapper.get(0), CoreMatchers.is(2532189736284989738L));
        wrapper.put(0, 3110911118989603122L);
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((byte) (43))));
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) (44))));
        Assert.assertThat(buffer.get(2), CoreMatchers.is(((byte) (45))));
        Assert.assertThat(buffer.get(3), CoreMatchers.is(((byte) (46))));
        Assert.assertThat(buffer.get(4), CoreMatchers.is(((byte) (47))));
        Assert.assertThat(buffer.get(5), CoreMatchers.is(((byte) (48))));
        Assert.assertThat(buffer.get(6), CoreMatchers.is(((byte) (49))));
        Assert.assertThat(buffer.get(7), CoreMatchers.is(((byte) (50))));
    }

    @Test
    public void wrapsIntoFloatBuffer() {
        byte[] array = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.limit(70);
        buffer.get(new byte[10]);
        buffer = buffer.slice();
        buffer.put(0, ((byte) (64)));
        buffer.put(1, ((byte) (73)));
        buffer.put(2, ((byte) (15)));
        buffer.put(3, ((byte) (208)));
        FloatBuffer wrapper = buffer.asFloatBuffer();
        Assert.assertThat(wrapper.capacity(), CoreMatchers.is(15));
        Assert.assertThat(wrapper.position(), CoreMatchers.is(0));
        Assert.assertThat(wrapper.limit(), CoreMatchers.is(15));
        Assert.assertEquals(3.14159, wrapper.get(0), 1.0E-5);
        wrapper.put(0, 2.71828F);
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((byte) (64))));
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) (45))));
        Assert.assertThat(buffer.get(2), CoreMatchers.is(((byte) (248))));
        Assert.assertThat(((buffer.get(3)) & 240), CoreMatchers.is(64));
    }

    @Test
    public void shortEndiannesWorks() {
        byte[] array = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(0, ((byte) (35)));
        buffer.put(1, ((byte) (36)));
        ShortBuffer wrapper = buffer.asShortBuffer();
        Assert.assertThat(wrapper.get(0), CoreMatchers.is(((short) (9251))));
    }

    @Test
    public void intEndiannesWorks() {
        byte[] array = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(0, ((byte) (35)));
        buffer.put(1, ((byte) (36)));
        buffer.put(2, ((byte) (37)));
        buffer.put(3, ((byte) (38)));
        IntBuffer wrapper = buffer.asIntBuffer();
        Assert.assertThat(wrapper.get(0), CoreMatchers.is(639968291));
    }

    @Test
    public void changesInWrapperSeenInBuffer() {
        byte[] array = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        ShortBuffer wrapper = buffer.asShortBuffer();
        wrapper.put(0, ((short) (8996)));
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((byte) (35))));
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) (36))));
    }

    @Test
    public void changesInBufferSeenInWrapper() {
        byte[] array = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        ShortBuffer wrapper = buffer.asShortBuffer();
        buffer.put(1, ((byte) (36)));
        Assert.assertThat(wrapper.get(0), CoreMatchers.is(((short) (36))));
    }
}

