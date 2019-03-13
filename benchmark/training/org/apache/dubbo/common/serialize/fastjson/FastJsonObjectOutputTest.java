/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.serialize.fastjson;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.dubbo.common.serialize.model.media.Image;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.apache.dubbo.common.serialize.model.media.Image.Size.SMALL;


public class FastJsonObjectOutputTest {
    private FastJsonObjectOutput fastJsonObjectOutput;

    private FastJsonObjectInput fastJsonObjectInput;

    private ByteArrayOutputStream byteArrayOutputStream;

    private ByteArrayInputStream byteArrayInputStream;

    @Test
    public void testWriteBool() throws IOException {
        this.fastJsonObjectOutput.writeBool(true);
        this.flushToInput();
        MatcherAssert.assertThat(fastJsonObjectInput.readBool(), CoreMatchers.is(true));
    }

    @Test
    public void testWriteShort() throws IOException {
        this.fastJsonObjectOutput.writeShort(((short) (2)));
        this.flushToInput();
        MatcherAssert.assertThat(fastJsonObjectInput.readShort(), CoreMatchers.is(((short) (2))));
    }

    @Test
    public void testWriteInt() throws IOException {
        this.fastJsonObjectOutput.writeInt(1);
        this.flushToInput();
        MatcherAssert.assertThat(fastJsonObjectInput.readInt(), CoreMatchers.is(1));
    }

    @Test
    public void testWriteLong() throws IOException {
        this.fastJsonObjectOutput.writeLong(1000L);
        this.flushToInput();
        MatcherAssert.assertThat(fastJsonObjectInput.readLong(), CoreMatchers.is(1000L));
    }

    @Test
    public void testWriteUTF() throws IOException {
        this.fastJsonObjectOutput.writeUTF("Pace Has?t? ?? ???");
        this.flushToInput();
        MatcherAssert.assertThat(fastJsonObjectInput.readUTF(), CoreMatchers.is("Pace Has?t? ?? ???"));
    }

    @Test
    public void testWriteFloat() throws IOException {
        this.fastJsonObjectOutput.writeFloat(1.88F);
        this.flushToInput();
        MatcherAssert.assertThat(this.fastJsonObjectInput.readFloat(), CoreMatchers.is(1.88F));
    }

    @Test
    public void testWriteDouble() throws IOException {
        this.fastJsonObjectOutput.writeDouble(1.66);
        this.flushToInput();
        MatcherAssert.assertThat(this.fastJsonObjectInput.readDouble(), CoreMatchers.is(1.66));
    }

    @Test
    public void testWriteBytes() throws IOException {
        this.fastJsonObjectOutput.writeBytes("hello".getBytes());
        this.flushToInput();
        MatcherAssert.assertThat(this.fastJsonObjectInput.readBytes(), CoreMatchers.is("hello".getBytes()));
    }

    @Test
    public void testWriteBytesWithSubLength() throws IOException {
        this.fastJsonObjectOutput.writeBytes("hello".getBytes(), 2, 2);
        this.flushToInput();
        MatcherAssert.assertThat(this.fastJsonObjectInput.readBytes(), CoreMatchers.is("ll".getBytes()));
    }

    @Test
    public void testWriteByte() throws IOException {
        this.fastJsonObjectOutput.writeByte(((byte) (123)));
        this.flushToInput();
        MatcherAssert.assertThat(this.fastJsonObjectInput.readByte(), CoreMatchers.is(((byte) (123))));
    }

    @Test
    public void testWriteObject() throws IOException, ClassNotFoundException {
        Image image = new Image("http://dubbo.io/logo.png", "logo", 300, 480, SMALL);
        this.fastJsonObjectOutput.writeObject(image);
        this.flushToInput();
        Image readObjectForImage = fastJsonObjectInput.readObject(Image.class);
        MatcherAssert.assertThat(readObjectForImage, CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(readObjectForImage, CoreMatchers.is(image));
    }
}

