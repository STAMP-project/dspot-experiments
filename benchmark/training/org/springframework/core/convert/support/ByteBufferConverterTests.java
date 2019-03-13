/**
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core.convert.support;


import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;


/**
 * Tests for {@link ByteBufferConverter}.
 *
 * @author Phillip Webb
 * @author Juergen Hoeller
 */
public class ByteBufferConverterTests {
    private GenericConversionService conversionService;

    @Test
    public void byteArrayToByteBuffer() throws Exception {
        byte[] bytes = new byte[]{ 1, 2, 3 };
        ByteBuffer convert = this.conversionService.convert(bytes, ByteBuffer.class);
        Assert.assertThat(convert.array(), not(sameInstance(bytes)));
        Assert.assertThat(convert.array(), equalTo(bytes));
    }

    @Test
    public void byteBufferToByteArray() throws Exception {
        byte[] bytes = new byte[]{ 1, 2, 3 };
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byte[] convert = this.conversionService.convert(byteBuffer, byte[].class);
        Assert.assertThat(convert, not(sameInstance(bytes)));
        Assert.assertThat(convert, equalTo(bytes));
    }

    @Test
    public void byteBufferToOtherType() throws Exception {
        byte[] bytes = new byte[]{ 1, 2, 3 };
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        ByteBufferConverterTests.OtherType convert = this.conversionService.convert(byteBuffer, ByteBufferConverterTests.OtherType.class);
        Assert.assertThat(convert.bytes, not(sameInstance(bytes)));
        Assert.assertThat(convert.bytes, equalTo(bytes));
    }

    @Test
    public void otherTypeToByteBuffer() throws Exception {
        byte[] bytes = new byte[]{ 1, 2, 3 };
        ByteBufferConverterTests.OtherType otherType = new ByteBufferConverterTests.OtherType(bytes);
        ByteBuffer convert = this.conversionService.convert(otherType, ByteBuffer.class);
        Assert.assertThat(convert.array(), not(sameInstance(bytes)));
        Assert.assertThat(convert.array(), equalTo(bytes));
    }

    @Test
    public void byteBufferToByteBuffer() throws Exception {
        byte[] bytes = new byte[]{ 1, 2, 3 };
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        ByteBuffer convert = this.conversionService.convert(byteBuffer, ByteBuffer.class);
        Assert.assertThat(convert, not(sameInstance(byteBuffer.rewind())));
        Assert.assertThat(convert, equalTo(byteBuffer.rewind()));
        Assert.assertThat(convert, equalTo(ByteBuffer.wrap(bytes)));
        Assert.assertThat(convert.array(), equalTo(bytes));
    }

    private static class OtherType {
        private byte[] bytes;

        public OtherType(byte[] bytes) {
            this.bytes = bytes;
        }
    }

    private static class ByteArrayToOtherTypeConverter implements Converter<byte[], ByteBufferConverterTests.OtherType> {
        @Override
        public ByteBufferConverterTests.OtherType convert(byte[] source) {
            return new ByteBufferConverterTests.OtherType(source);
        }
    }

    private static class OtherTypeToByteArrayConverter implements Converter<ByteBufferConverterTests.OtherType, byte[]> {
        @Override
        public byte[] convert(ByteBufferConverterTests.OtherType source) {
            return source.bytes;
        }
    }
}

