/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
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
 */
package com.github.ambry.messageformat;


import com.codahale.metrics.MetricRegistry;
import com.github.ambry.store.MessageReadSet;
import com.github.ambry.store.MockId;
import com.github.ambry.store.MockIdFactory;
import com.github.ambry.store.StoreKey;
import com.github.ambry.utils.ByteBufferOutputStream;
import com.github.ambry.utils.Crc32;
import com.github.ambry.utils.TestUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static MessageFormatErrorCodes.Store_Key_Id_MisMatch;
import static MessageFormatFlags.All;
import static MessageFormatFlags.Blob;
import static MessageFormatRecord.Message_Header_Version_V1;
import static MessageFormatRecord.Message_Header_Version_V2;
import static MessageFormatRecord.headerVersionToUse;


@RunWith(Parameterized.class)
public class MessageFormatSendTest {
    private final String putFormat;

    private static short messageFormatHeaderVersionSaved;

    public MessageFormatSendTest(String putFormat) {
        this.putFormat = putFormat;
    }

    class MockMessageReadSet implements MessageReadSet {
        ArrayList<ByteBuffer> buffers;

        ArrayList<StoreKey> keys;

        private long prefetchRelativeOffset;

        private long preFetchSize;

        public MockMessageReadSet(ArrayList<ByteBuffer> buffers, ArrayList<StoreKey> keys) {
            this.buffers = buffers;
            this.keys = keys;
        }

        @Override
        public long writeTo(int index, WritableByteChannel channel, long relativeOffset, long maxSize) throws IOException {
            buffers.get(index).position(((int) (relativeOffset)));
            buffers.get(index).limit(((int) (Math.min(buffers.get(index).limit(), (relativeOffset + maxSize)))));
            int written = channel.write(buffers.get(index));
            buffers.get(index).clear();
            return written;
        }

        @Override
        public int count() {
            return buffers.size();
        }

        @Override
        public long sizeInBytes(int index) {
            return buffers.get(index).remaining();
        }

        @Override
        public StoreKey getKeyAt(int index) {
            return keys.get(index);
        }

        @Override
        public void doPrefetch(int index, long relativeOffset, long size) {
            this.prefetchRelativeOffset = relativeOffset;
            this.preFetchSize = size;
        }

        /**
         * Check if prefetched offset and size are correct.
         */
        boolean isPrefetchInfoCorrect(long relativeOffset, long size) {
            return (relativeOffset == (this.prefetchRelativeOffset)) && (size == (this.preFetchSize));
        }
    }

    /**
     * Tests cases involving single messages across different header versions and with and without encryption keys
     */
    @Test
    public void sendWriteSingleMessageTest() throws Exception {
        if (putFormat.equals(PutMessageFormatInputStream.class.getSimpleName())) {
            ByteBuffer encryptionKey = ByteBuffer.wrap(TestUtils.getRandomBytes(256));
            headerVersionToUse = Message_Header_Version_V1;
            doSendWriteSingleMessageTest(null, null, false);
            doSendWriteSingleMessageTest(encryptionKey.duplicate(), null, false);
            // with store data prefetch
            doSendWriteSingleMessageTest(null, null, true);
            doSendWriteSingleMessageTest(encryptionKey.duplicate(), null, true);
            headerVersionToUse = Message_Header_Version_V2;
            doSendWriteSingleMessageTest(null, null, false);
            doSendWriteSingleMessageTest(ByteBuffer.allocate(0), ByteBuffer.allocate(0), false);
            doSendWriteSingleMessageTest(encryptionKey.duplicate(), encryptionKey.duplicate(), false);
            // with store data prefetch
            doSendWriteSingleMessageTest(null, null, true);
            doSendWriteSingleMessageTest(ByteBuffer.allocate(0), ByteBuffer.allocate(0), true);
            doSendWriteSingleMessageTest(encryptionKey.duplicate(), encryptionKey.duplicate(), true);
        } else {
            doSendWriteSingleMessageTest(null, null, false);
            doSendWriteSingleMessageTest(null, null, true);
        }
    }

    /**
     * Tests involving multiple messages in a single Send involving different combinations of header format versions, put
     * formats and encryption keys.
     */
    @Test
    public void sendWriteCompositeMessagesTest() throws Exception {
        short savedVersion = headerVersionToUse;
        if (!(putFormat.equals(PutMessageFormatInputStream.class.getSimpleName()))) {
            return;
        }
        String putFormat1 = PutMessageFormatBlobV1InputStream.class.getSimpleName();
        String putFormat2 = PutMessageFormatInputStream.class.getSimpleName();
        short headerFormatV1 = Message_Header_Version_V1;
        short headerFormatV2 = Message_Header_Version_V2;
        byte[][] blob = new byte[][]{ TestUtils.getRandomBytes(1000), TestUtils.getRandomBytes(2000), TestUtils.getRandomBytes(10000), TestUtils.getRandomBytes(20000), TestUtils.getRandomBytes(40000) };
        byte[][] userMetadata = new byte[][]{ TestUtils.getRandomBytes(200), TestUtils.getRandomBytes(400), TestUtils.getRandomBytes(2000), TestUtils.getRandomBytes(4000), TestUtils.getRandomBytes(8000) };
        StoreKey[] storeKeys = new StoreKey[]{ new MockId("64"), new MockId("32"), new MockId("16"), new MockId("08"), new MockId("04") };
        ByteBuffer[] encryptionKeys = new ByteBuffer[]{ ByteBuffer.wrap(TestUtils.getRandomBytes(64)), ByteBuffer.wrap(TestUtils.getRandomBytes(128)), ByteBuffer.wrap(TestUtils.getRandomBytes(256)), ByteBuffer.wrap(TestUtils.getRandomBytes(512)), ByteBuffer.wrap(TestUtils.getRandomBytes(1024)) };
        String[] putFormat1s = new String[]{ putFormat1, putFormat1, putFormat1, putFormat1, putFormat1 };
        String[] putFormat2s = new String[]{ putFormat2, putFormat2, putFormat2, putFormat2, putFormat2 };
        String[] putFormatComposite1 = new String[]{ putFormat1, putFormat2, putFormat2, putFormat2, putFormat1 };
        String[] putFormatComposite2 = new String[]{ putFormat2, putFormat1, putFormat1, putFormat2, putFormat2 };
        short[] headerFormat1s = new short[]{ headerFormatV1, headerFormatV1, headerFormatV1, headerFormatV1, headerFormatV1 };
        short[] headerFormat2s = new short[]{ headerFormatV2, headerFormatV2, headerFormatV2, headerFormatV2, headerFormatV2 };
        short[] headerFormatComposite1 = new short[]{ headerFormatV1, headerFormatV2, headerFormatV2, headerFormatV1, headerFormatV1 };
        short[] headerFormatComposite2 = new short[]{ headerFormatV2, headerFormatV1, headerFormatV1, headerFormatV2, headerFormatV2 };
        doSendWriteCompositeMessagesTest(blob, userMetadata, storeKeys, encryptionKeys, putFormat1s, headerFormat1s);
        doSendWriteCompositeMessagesTest(blob, userMetadata, storeKeys, encryptionKeys, putFormat2s, headerFormat1s);
        doSendWriteCompositeMessagesTest(blob, userMetadata, storeKeys, encryptionKeys, putFormat2s, headerFormat2s);
        doSendWriteCompositeMessagesTest(blob, userMetadata, storeKeys, encryptionKeys, putFormat2s, headerFormatComposite1);
        doSendWriteCompositeMessagesTest(blob, userMetadata, storeKeys, encryptionKeys, putFormat2s, headerFormatComposite2);
        doSendWriteCompositeMessagesTest(blob, userMetadata, storeKeys, encryptionKeys, putFormatComposite1, headerFormatComposite1);
        doSendWriteCompositeMessagesTest(blob, userMetadata, storeKeys, encryptionKeys, putFormatComposite2, headerFormatComposite2);
        headerVersionToUse = savedVersion;
    }

    @Test
    public void sendWriteTestWithBadId() throws MessageFormatException, IOException {
        // add header,system metadata, user metadata and data to the buffers
        ByteBuffer buf1 = ByteBuffer.allocate(1010);
        // fill header
        buf1.putShort(((short) (1)));
        // version
        buf1.putLong(950);// total size

        // put relative offsets
        buf1.putInt(60);
        // blob property relative offset
        buf1.putInt((-1));
        // delete relative offset
        buf1.putInt(81);
        // user metadata relative offset
        buf1.putInt(191);
        // data relative offset
        Crc32 crc = new Crc32();
        crc.update(buf1.array(), 0, buf1.position());
        buf1.putLong(crc.getValue());
        // crc
        String id = new String("012345678910123456789012");// blob id

        buf1.putShort(((short) (id.length())));
        buf1.put(id.getBytes());
        buf1.putShort(((short) (1)));// blob property version

        String attribute1 = "ttl";
        String attribute2 = "del";
        buf1.put(attribute1.getBytes());// ttl name

        buf1.putLong(12345);
        // ttl value
        buf1.put(attribute2.getBytes());// delete name

        byte b = 1;
        buf1.put(b);
        // delete flag
        buf1.putInt(456);// crc

        buf1.putShort(((short) (1)));// user metadata version

        buf1.putInt(100);
        byte[] usermetadata = new byte[100];
        new Random().nextBytes(usermetadata);
        buf1.put(usermetadata);
        buf1.putInt(123);
        buf1.putShort(((short) (0)));// blob version

        buf1.putLong(805);
        // blob size
        byte[] data = new byte[805];
        // blob
        new Random().nextBytes(data);
        buf1.put(data);
        buf1.putInt(123);
        // blob crc
        buf1.flip();
        ArrayList<ByteBuffer> listbuf = new ArrayList<ByteBuffer>();
        listbuf.add(buf1);
        ArrayList<StoreKey> storeKeys = new ArrayList<StoreKey>();
        storeKeys.add(new MockId("012345678910123223233456789012"));
        MessageReadSet readSet = new MessageFormatSendTest.MockMessageReadSet(listbuf, storeKeys);
        MetricRegistry registry = new MetricRegistry();
        MessageFormatMetrics metrics = new MessageFormatMetrics(registry);
        // get all
        MessageFormatSend send = new MessageFormatSend(readSet, All, metrics, new MockIdFactory(), false);
        Assert.assertEquals(send.sizeInBytes(), 1010);
        ByteBuffer bufresult = ByteBuffer.allocate(1010);
        WritableByteChannel channel1 = Channels.newChannel(new ByteBufferOutputStream(bufresult));
        while (!(send.isSendComplete())) {
            send.writeTo(channel1);
        } 
        Assert.assertArrayEquals(buf1.array(), bufresult.array());
        try {
            // get blob
            MessageFormatSend send1 = new MessageFormatSend(readSet, Blob, metrics, new MockIdFactory(), false);
            Assert.assertTrue(false);
        } catch (MessageFormatException e) {
            Assert.assertTrue(((e.getErrorCode()) == (Store_Key_Id_MisMatch)));
        }
    }

    /**
     * Test {@link MessageReadSetIndexInputStream} with different offsets and lengths.
     */
    @Test
    public void messageReadSetIndexInputStreamTest() throws Exception {
        ArrayList<ByteBuffer> listbuf = new ArrayList<ByteBuffer>();
        byte[] buf1 = new byte[1024];
        byte[] buf2 = new byte[2048];
        byte[] buf3 = new byte[4096];
        new Random().nextBytes(buf1);
        new Random().nextBytes(buf2);
        new Random().nextBytes(buf3);
        listbuf.add(ByteBuffer.wrap(buf1));
        listbuf.add(ByteBuffer.wrap(buf2));
        listbuf.add(ByteBuffer.wrap(buf3));
        ArrayList<StoreKey> storeKeys = new ArrayList<StoreKey>();
        storeKeys.add(new MockId("012345678910123223233456789012"));
        storeKeys.add(new MockId("012345678910123223233456789013"));
        storeKeys.add(new MockId("012345678910123223233456789014"));
        MessageReadSet readSet = new MessageFormatSendTest.MockMessageReadSet(listbuf, storeKeys);
        MessageReadSetIndexInputStream stream1 = new MessageReadSetIndexInputStream(readSet, 0, 0);
        byte[] buf1Output = new byte[1024];
        Assert.assertEquals("Number of bytes read doesn't match", 1024, stream1.read(buf1Output, 0, 1024));
        Assert.assertArrayEquals(buf1Output, buf1);
        MessageReadSetIndexInputStream stream2 = new MessageReadSetIndexInputStream(readSet, 1, 1024);
        byte[] buf2Output = new byte[1025];
        Assert.assertEquals("Number of bytes read doesn't match", 512, stream2.read(buf2Output, 0, 512));
        Assert.assertEquals("Number of bytes read doesn't match", 512, stream2.read(buf2Output, 512, 513));
        for (int i = 0; i < 1024; i++) {
            Assert.assertEquals(buf2Output[i], buf2[(i + 1024)]);
        }
        MessageReadSetIndexInputStream stream3 = new MessageReadSetIndexInputStream(readSet, 2, 2048);
        byte[] buf3Output = new byte[2048];
        for (int i = 0; i < 2048; i++) {
            Assert.assertEquals(((byte) (stream3.read())), buf3[(i + 2048)]);
        }
        Assert.assertEquals("Should return -1 if no more data available", (-1), stream3.read(buf3Output, 0, 1));
        Assert.assertEquals("Should return -1 if no more data available", (-1), stream3.read());
    }

    /**
     * Test Exceptions cases for {@link MessageReadSetIndexInputStream}
     * IndexOutOfBoundsException should be thrown if offset or length is invalid.
     * 0 is expected if length requested is 0.
     * -1 is expected if no more data available.
     */
    @Test
    public void messageReadSetIndexInputStreamTestException() throws Exception {
        ArrayList<ByteBuffer> listBuf = new ArrayList<ByteBuffer>();
        byte[] buf = new byte[1024];
        new Random().nextBytes(buf);
        listBuf.add(ByteBuffer.wrap(buf));
        ArrayList<StoreKey> storeKeys = new ArrayList<StoreKey>();
        storeKeys.add(new MockId("012345678910123223233456789012"));
        MessageReadSet readSet = new MessageFormatSendTest.MockMessageReadSet(listBuf, storeKeys);
        MessageReadSetIndexInputStream stream = new MessageReadSetIndexInputStream(readSet, 0, 0);
        byte[] bufOutput = new byte[1024];
        Assert.assertEquals("Should return 0 if length requested is 0", 0, stream.read(bufOutput, 0, 0));
        Assert.assertEquals("Should return 0 if length requested is 0", 0, stream.read(bufOutput, 1, 0));
        try {
            stream.read(bufOutput, (-1), 10);
            Assert.fail("IndexOutOfBoundsException is expected.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            stream.read(bufOutput, 0, (-1));
            Assert.fail("IndexOutOfBoundsException is expected.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            stream.read(bufOutput, 1, 1024);
            Assert.fail("IndexOutOfBoundsException is expected.");
        } catch (IndexOutOfBoundsException e) {
        }
        stream.read(bufOutput, 0, 1024);
        Assert.assertArrayEquals("Output doesn't match", bufOutput, buf);
        Assert.assertEquals("Should return -1 if no more data", (-1), stream.read());
        Assert.assertEquals("Should return -1 if no more data", (-1), stream.read(bufOutput, 0, 1));
    }
}

