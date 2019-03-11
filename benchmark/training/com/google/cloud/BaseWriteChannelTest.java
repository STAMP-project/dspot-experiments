/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import com.google.cloud.spi.ServiceRpcFactory;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.Random;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class BaseWriteChannelTest {
    private abstract static class CustomService implements Service<BaseWriteChannelTest.CustomServiceOptions> {}

    private abstract static class CustomServiceOptions extends ServiceOptions<BaseWriteChannelTest.CustomService, BaseWriteChannelTest.CustomServiceOptions> {
        private static final long serialVersionUID = 3302358029307467197L;

        protected CustomServiceOptions(Class<? extends ServiceFactory<BaseWriteChannelTest.CustomService, BaseWriteChannelTest.CustomServiceOptions>> serviceFactoryClass, Class<? extends ServiceRpcFactory<BaseWriteChannelTest.CustomServiceOptions>> rpcFactoryClass, Builder<BaseWriteChannelTest.CustomService, BaseWriteChannelTest.CustomServiceOptions, ?> builder) {
            super(serviceFactoryClass, rpcFactoryClass, builder, null);
        }
    }

    private static final Serializable ENTITY = 42L;

    private static final String UPLOAD_ID = "uploadId";

    private static final byte[] CONTENT = new byte[]{ 13, 14, 10, 13 };

    private static final int MIN_CHUNK_SIZE = 256 * 1024;

    private static final int DEFAULT_CHUNK_SIZE = 8 * (BaseWriteChannelTest.MIN_CHUNK_SIZE);

    private static final Random RANDOM = new Random();

    private static BaseWriteChannel channel;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructor() {
        Assert.assertEquals(null, BaseWriteChannelTest.channel.getOptions());
        Assert.assertEquals(BaseWriteChannelTest.ENTITY, BaseWriteChannelTest.channel.getEntity());
        Assert.assertEquals(0, BaseWriteChannelTest.channel.getPosition());
        Assert.assertEquals(BaseWriteChannelTest.UPLOAD_ID, BaseWriteChannelTest.channel.getUploadId());
        Assert.assertEquals(0, BaseWriteChannelTest.channel.getLimit());
        TestCase.assertTrue(BaseWriteChannelTest.channel.isOpen());
        Assert.assertArrayEquals(new byte[0], BaseWriteChannelTest.channel.getBuffer());
        Assert.assertEquals(BaseWriteChannelTest.DEFAULT_CHUNK_SIZE, BaseWriteChannelTest.channel.getChunkSize());
    }

    @Test
    public void testClose() throws IOException {
        BaseWriteChannelTest.channel.close();
        TestCase.assertFalse(BaseWriteChannelTest.channel.isOpen());
        Assert.assertNull(BaseWriteChannelTest.channel.getBuffer());
    }

    @Test
    public void testValidateOpen() throws IOException {
        BaseWriteChannelTest.channel.close();
        thrown.expect(ClosedChannelException.class);
        BaseWriteChannelTest.channel.write(ByteBuffer.allocate(42));
    }

    @Test
    public void testChunkSize() {
        BaseWriteChannelTest.channel.setChunkSize(42);
        assertThat(((BaseWriteChannelTest.channel.getChunkSize()) >= (BaseWriteChannelTest.MIN_CHUNK_SIZE))).isTrue();
        assertThat(((BaseWriteChannelTest.channel.getChunkSize()) % (BaseWriteChannelTest.MIN_CHUNK_SIZE))).isEqualTo(0);
        BaseWriteChannelTest.channel.setChunkSize((2 * (BaseWriteChannelTest.MIN_CHUNK_SIZE)));
        assertThat(((BaseWriteChannelTest.channel.getChunkSize()) >= (BaseWriteChannelTest.MIN_CHUNK_SIZE))).isTrue();
        assertThat(((BaseWriteChannelTest.channel.getChunkSize()) % (BaseWriteChannelTest.MIN_CHUNK_SIZE))).isEqualTo(0);
        BaseWriteChannelTest.channel.setChunkSize(((2 * (BaseWriteChannelTest.MIN_CHUNK_SIZE)) + 1));
        assertThat(((BaseWriteChannelTest.channel.getChunkSize()) >= (BaseWriteChannelTest.MIN_CHUNK_SIZE))).isTrue();
        assertThat(((BaseWriteChannelTest.channel.getChunkSize()) % (BaseWriteChannelTest.MIN_CHUNK_SIZE))).isEqualTo(0);
    }

    @Test
    public void testWrite() throws IOException {
        BaseWriteChannelTest.channel.write(ByteBuffer.wrap(BaseWriteChannelTest.CONTENT));
        Assert.assertEquals(BaseWriteChannelTest.CONTENT.length, BaseWriteChannelTest.channel.getLimit());
        Assert.assertEquals(BaseWriteChannelTest.DEFAULT_CHUNK_SIZE, BaseWriteChannelTest.channel.getBuffer().length);
        Assert.assertArrayEquals(Arrays.copyOf(BaseWriteChannelTest.CONTENT, BaseWriteChannelTest.DEFAULT_CHUNK_SIZE), BaseWriteChannelTest.channel.getBuffer());
    }

    @Test
    public void testWriteAndFlush() throws IOException {
        ByteBuffer content = BaseWriteChannelTest.randomBuffer(((BaseWriteChannelTest.DEFAULT_CHUNK_SIZE) + 1));
        BaseWriteChannelTest.channel.write(content);
        Assert.assertEquals(BaseWriteChannelTest.DEFAULT_CHUNK_SIZE, BaseWriteChannelTest.channel.getPosition());
        Assert.assertEquals(1, BaseWriteChannelTest.channel.getLimit());
        byte[] newContent = new byte[BaseWriteChannelTest.DEFAULT_CHUNK_SIZE];
        newContent[0] = content.get(BaseWriteChannelTest.DEFAULT_CHUNK_SIZE);
        Assert.assertArrayEquals(newContent, BaseWriteChannelTest.channel.getBuffer());
    }
}

