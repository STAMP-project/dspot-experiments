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


import ClientMessage.BEGIN_AND_END_FLAGS;
import ClientMessage.HEADER_SIZE;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.exception.MaxMessageSizeExceeded;
import com.hazelcast.client.impl.protocol.util.ClientProtocolBuffer;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.client.impl.protocol.util.SafeBuffer;
import com.hazelcast.nio.Bits;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * ClientMessage Tests of Flyweight functionality
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMessageTest {
    private static final String DEFAULT_ENCODING = "UTF8";

    private static final String VAR_DATA_STR_1 = "abcdef";

    private static final byte[] BYTE_DATA = ClientMessageTest.VAR_DATA_STR_1.getBytes();

    @Test
    public void shouldEncodeClientMessageCorrectly() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        SafeBuffer safeBuffer = new SafeBuffer(byteBuffer.array());
        ClientMessage cmEncode = ClientMessageTest.TestClientMessage.createForEncode(safeBuffer, 0);
        cmEncode.setMessageType(4386).setVersion(((short) (239))).addFlag(BEGIN_AND_END_FLAGS).setCorrelationId(1311768465173141112L).setPartitionId(287454020);
        // little endian
        // FRAME LENGTH
        Assert.assertThat(byteBuffer.get(0), Is.is(((byte) (HEADER_SIZE))));
        Assert.assertThat(byteBuffer.get(1), Is.is(((byte) (0))));
        Assert.assertThat(byteBuffer.get(2), Is.is(((byte) (0))));
        Assert.assertThat(byteBuffer.get(3), Is.is(((byte) (0))));
        // VERSION
        Assert.assertThat(byteBuffer.get(4), Is.is(((byte) (239))));
        // FLAGS
        Assert.assertThat(byteBuffer.get(5), Is.is(((byte) (192))));
        // TYPE
        Assert.assertThat(byteBuffer.get(6), Is.is(((byte) (34))));
        Assert.assertThat(byteBuffer.get(7), Is.is(((byte) (17))));
        // setCorrelationId
        Assert.assertThat(byteBuffer.get(8), Is.is(((byte) (120))));
        Assert.assertThat(byteBuffer.get(9), Is.is(((byte) (86))));
        Assert.assertThat(byteBuffer.get(10), Is.is(((byte) (52))));
        Assert.assertThat(byteBuffer.get(11), Is.is(((byte) (18))));
        Assert.assertThat(byteBuffer.get(12), Is.is(((byte) (120))));
        Assert.assertThat(byteBuffer.get(13), Is.is(((byte) (86))));
        Assert.assertThat(byteBuffer.get(14), Is.is(((byte) (52))));
        Assert.assertThat(byteBuffer.get(15), Is.is(((byte) (18))));
        // setPartitionId
        Assert.assertThat(byteBuffer.get(16), Is.is(((byte) (68))));
        Assert.assertThat(byteBuffer.get(17), Is.is(((byte) (51))));
        Assert.assertThat(byteBuffer.get(18), Is.is(((byte) (34))));
        Assert.assertThat(byteBuffer.get(19), Is.is(((byte) (17))));
        // data offset
        Assert.assertThat(byteBuffer.get(20), Is.is(((byte) (HEADER_SIZE))));
        Assert.assertThat(byteBuffer.get(21), Is.is(((byte) (0))));
    }

    @Test
    public void shouldEncodeAndDecodeClientMessageCorrectly() {
        SafeBuffer byteBuffer = new SafeBuffer(new byte[512]);
        ClientMessage cmEncode = ClientMessageTest.TestClientMessage.createForEncode(byteBuffer, 0);
        cmEncode.setMessageType(7).setVersion(((short) (3))).addFlag(BEGIN_AND_END_FLAGS).setCorrelationId(66).setPartitionId(77);
        ClientMessage cmDecode = ClientMessage.createForDecode(byteBuffer, 0);
        Assert.assertEquals(7, cmDecode.getMessageType());
        Assert.assertEquals(3, cmDecode.getVersion());
        Assert.assertEquals(BEGIN_AND_END_FLAGS, cmDecode.getFlags());
        Assert.assertEquals(66, cmDecode.getCorrelationId());
        Assert.assertEquals(77, cmDecode.getPartitionId());
        Assert.assertEquals(HEADER_SIZE, cmDecode.getFrameLength());
    }

    @Test
    public void shouldEncodeAndDecodeClientMessageCorrectly_withPayLoadData() throws UnsupportedEncodingException {
        SafeBuffer byteBuffer = new SafeBuffer(new byte[1024]);
        ClientMessage cmEncode = ClientMessageTest.TestClientMessage.createForEncode(byteBuffer, 0);
        cmEncode.setMessageType(7).setVersion(((short) (3))).addFlag(BEGIN_AND_END_FLAGS).setCorrelationId(66).setPartitionId(77);
        final byte[] data1 = ClientMessageTest.VAR_DATA_STR_1.getBytes(ClientMessageTest.DEFAULT_ENCODING);
        final int calculatedFrameSize = (ClientMessage.HEADER_SIZE) + (ParameterUtil.calculateDataSize(data1));
        cmEncode.set(data1);
        cmEncode.updateFrameLength();
        ClientMessage cmDecode = ClientMessage.createForDecode(byteBuffer, 0);
        byte[] cmDecodeVarData1 = cmDecode.getByteArray();
        Assert.assertEquals(calculatedFrameSize, cmDecode.getFrameLength());
        Assert.assertArrayEquals(cmDecodeVarData1, data1);
    }

    @Test
    public void shouldEncodeAndDecodeClientMessageCorrectly_withPayLoadData_fromOffset() {
        SafeBuffer byteBuffer = new SafeBuffer(new byte[150]);
        int offset = 100;
        ClientMessage cmEncode = ClientMessageTest.TestClientMessage.createForEncode(byteBuffer, offset);
        cmEncode.setMessageType(7).setVersion(((short) (3))).addFlag(BEGIN_AND_END_FLAGS).setCorrelationId(66).setPartitionId(77);
        byte[] bytes = ClientMessageTest.VAR_DATA_STR_1.getBytes();
        final int calculatedFrameSize = ((ClientMessage.HEADER_SIZE) + (Bits.INT_SIZE_IN_BYTES)) + (ParameterUtil.calculateDataSize(bytes));
        cmEncode.set(1);
        cmEncode.set(bytes);
        cmEncode.updateFrameLength();
        ClientMessage cmDecode = ClientMessage.createForDecode(byteBuffer, offset);
        Assert.assertEquals(1, cmDecode.getInt());
        Assert.assertArrayEquals(bytes, cmDecode.getByteArray());
        Assert.assertEquals(calculatedFrameSize, cmDecode.getFrameLength());
    }

    @Test
    public void shouldEncodeWithNewVersionAndDecodeWithOldVersionCorrectly_withPayLoadData() {
        SafeBuffer byteBuffer = new SafeBuffer(new byte[1024]);
        ClientMessageTest.FutureClientMessage cmEncode = new ClientMessageTest.FutureClientMessage();
        cmEncode.wrapForEncode(byteBuffer, 0);
        cmEncode.theNewField(999).setMessageType(7).setVersion(((short) (3))).addFlag(BEGIN_AND_END_FLAGS).setCorrelationId(66).setPartitionId(77);
        final int calculatedFrameSize = (ClientMessageTest.FutureClientMessage.THE_NEW_HEADER_SIZE) + (ParameterUtil.calculateDataSize(ClientMessageTest.BYTE_DATA));
        set(ClientMessageTest.BYTE_DATA);
        updateFrameLength();
        ClientMessage cmDecode = ClientMessage.createForDecode(byteBuffer, 0);
        final byte[] cmDecodeVarData1 = cmDecode.getByteArray();
        Assert.assertEquals(7, cmDecode.getMessageType());
        Assert.assertEquals(3, cmDecode.getVersion());
        Assert.assertEquals(BEGIN_AND_END_FLAGS, cmDecode.getFlags());
        Assert.assertEquals(66, cmDecode.getCorrelationId());
        Assert.assertEquals(77, cmDecode.getPartitionId());
        Assert.assertEquals(calculatedFrameSize, cmDecode.getFrameLength());
        Assert.assertArrayEquals(cmDecodeVarData1, ClientMessageTest.BYTE_DATA);
    }

    @Test
    public void shouldEncodeWithOldVersionAndDecodeWithNewVersionCorrectly_withPayLoadData() {
        SafeBuffer byteBuffer = new SafeBuffer(new byte[1024]);
        ClientMessage cmEncode = ClientMessageTest.TestClientMessage.createForEncode(byteBuffer, 0);
        cmEncode.setMessageType(7).setVersion(((short) (3))).addFlag(BEGIN_AND_END_FLAGS).setCorrelationId(66).setPartitionId(77);
        final int calculatedFrameSize = (ClientMessage.HEADER_SIZE) + (ParameterUtil.calculateDataSize(ClientMessageTest.BYTE_DATA));
        cmEncode.set(ClientMessageTest.BYTE_DATA);
        cmEncode.updateFrameLength();
        ClientMessage cmDecode = ClientMessageTest.FutureClientMessage.createForDecode(byteBuffer, 0);
        final byte[] cmDecodeVarData1 = cmDecode.getByteArray();
        Assert.assertEquals(7, cmDecode.getMessageType());
        Assert.assertEquals(3, cmDecode.getVersion());
        Assert.assertEquals(BEGIN_AND_END_FLAGS, cmDecode.getFlags());
        Assert.assertEquals(66, cmDecode.getCorrelationId());
        Assert.assertEquals(77, cmDecode.getPartitionId());
        Assert.assertEquals(calculatedFrameSize, cmDecode.getFrameLength());
        Assert.assertArrayEquals(cmDecodeVarData1, ClientMessageTest.BYTE_DATA);
    }

    @Test
    public void shouldEncodeAndDecodeClientMessageCorrectly_withPayLoadData_multipleMessages() {
        SafeBuffer byteBuffer = new SafeBuffer(new byte[1024]);
        ClientMessage cmEncode = ClientMessageTest.TestClientMessage.createForEncode(byteBuffer, 0);
        cmEncode.setMessageType(7).setVersion(((short) (3))).addFlag(BEGIN_AND_END_FLAGS).setCorrelationId(1).setPartitionId(77);
        cmEncode.set(ClientMessageTest.BYTE_DATA);
        cmEncode.updateFrameLength();
        final int calculatedFrame1Size = (ClientMessage.HEADER_SIZE) + (ParameterUtil.calculateDataSize(ClientMessageTest.BYTE_DATA));
        final int nexMessageOffset = cmEncode.getFrameLength();
        ClientMessage cmEncode2 = ClientMessageTest.TestClientMessage.createForEncode(byteBuffer, nexMessageOffset);
        cmEncode2.setMessageType(7).setVersion(((short) (3))).addFlag(BEGIN_AND_END_FLAGS).setCorrelationId(2).setPartitionId(77);
        cmEncode2.set(ClientMessageTest.BYTE_DATA);
        cmEncode2.updateFrameLength();
        final int calculatedFrame2Size = (ClientMessage.HEADER_SIZE) + (ParameterUtil.calculateDataSize(ClientMessageTest.BYTE_DATA));
        ClientMessage cmDecode1 = ClientMessage.createForDecode(byteBuffer, 0);
        final byte[] cmDecodeVarData = cmDecode1.getByteArray();
        Assert.assertEquals(7, cmDecode1.getMessageType());
        Assert.assertEquals(3, cmDecode1.getVersion());
        Assert.assertEquals(BEGIN_AND_END_FLAGS, cmDecode1.getFlags());
        Assert.assertEquals(1, cmDecode1.getCorrelationId());
        Assert.assertEquals(77, cmDecode1.getPartitionId());
        Assert.assertEquals(calculatedFrame1Size, cmDecode1.getFrameLength());
        Assert.assertArrayEquals(cmDecodeVarData, ClientMessageTest.BYTE_DATA);
        ClientMessage cmDecode2 = ClientMessage.createForDecode(byteBuffer, cmDecode1.getFrameLength());
        byte[] cmDecodeVarData2 = cmDecode2.getByteArray();
        Assert.assertEquals(7, cmDecode2.getMessageType());
        Assert.assertEquals(3, cmDecode2.getVersion());
        Assert.assertEquals(BEGIN_AND_END_FLAGS, cmDecode2.getFlags());
        Assert.assertEquals(2, cmDecode2.getCorrelationId());
        Assert.assertEquals(77, cmDecode2.getPartitionId());
        Assert.assertEquals(calculatedFrame2Size, cmDecode2.getFrameLength());
        Assert.assertArrayEquals(cmDecodeVarData2, ClientMessageTest.BYTE_DATA);
    }

    private static class FutureClientMessage extends ClientMessageTest.TestClientMessage {
        private static final int THE_NEW_FIELD_OFFSET = (HEADER_SIZE) + (Bits.SHORT_SIZE_IN_BYTES);

        private static final int THE_NEW_HEADER_SIZE = (HEADER_SIZE) + (Bits.INT_SIZE_IN_BYTES);

        @Override
        protected void wrapForEncode(ClientProtocolBuffer buffer, int offset) {
            super.wrap(buffer.byteArray(), offset, true);
            setDataOffset(ClientMessageTest.FutureClientMessage.THE_NEW_HEADER_SIZE);
            setFrameLength(ClientMessageTest.FutureClientMessage.THE_NEW_HEADER_SIZE);
            index(getDataOffset());
        }

        public int theNewField() {
            return ((int) (uint32Get(ClientMessageTest.FutureClientMessage.THE_NEW_FIELD_OFFSET)));
        }

        public ClientMessageTest.FutureClientMessage theNewField(int value) {
            uint32Put(ClientMessageTest.FutureClientMessage.THE_NEW_FIELD_OFFSET, value);
            return this;
        }
    }

    private static class TestClientMessage extends ClientMessage {
        @Override
        public ClientMessage setMessageType(int type) {
            return super.setMessageType(type);
        }
    }

    @Test
    public void test_empty_toString() {
        ClientMessage.create().toString();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_byteArray_constructor_withSmallBuffer() {
        ClientMessage.createForEncode(new SafeBuffer(new byte[10]), 1);
    }

    @Test
    public void test_byteArray_constructor_withHeaderSizeBuffer() {
        ClientMessage.createForEncode(new SafeBuffer(new byte[ClientMessage.HEADER_SIZE]), 0);
    }

    @Test
    public void test_byteArray_constructor_withLargeBuffer() {
        ClientMessage.createForEncode(new SafeBuffer(new byte[100]), 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_MutableDirectBuffer_constructor_withSmallBuffer() {
        SafeBuffer buffer = new SafeBuffer(new byte[10]);
        ClientMessage.createForEncode(buffer, 1);
    }

    @Test
    public void test_MutableDirectBuffer_constructor_withHeaderSizeBuffer() {
        SafeBuffer buffer = new SafeBuffer(new byte[ClientMessage.HEADER_SIZE]);
        ClientMessage.createForEncode(buffer, 0);
    }

    @Test
    public void test_MutableDirectBuffer_constructor_withLargeBuffer() {
        SafeBuffer buffer = new SafeBuffer(new byte[100]);
        ClientMessage.createForEncode(buffer, 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_wrapForEncode_withSmallBuffer() {
        ClientMessage.createForEncode(new SafeBuffer(new byte[10]), 1);
    }

    @Test
    public void test_wrapForEncode_withHeaderSizeBuffer() {
        ClientMessage.createForEncode(new SafeBuffer(new byte[ClientMessage.HEADER_SIZE]), 0);
    }

    @Test
    public void test_wrapForEncode_withLargeBuffer() {
        ClientMessage.createForEncode(new SafeBuffer(new byte[100]), 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_wrapForDecode_withSmallBuffer() {
        ClientMessage.createForDecode(new SafeBuffer(new byte[10]), 1);
    }

    @Test
    public void test_wrapForDecode_withHeaderSizeBuffer() {
        ClientMessage.createForDecode(new SafeBuffer(new byte[ClientMessage.HEADER_SIZE]), 0);
    }

    @Test
    public void test_wrapForDecode_withLargeBuffer() {
        ClientMessage.createForDecode(new SafeBuffer(new byte[100]), 10);
    }

    @Test
    public void testUnsignedFields() {
        ClientProtocolBuffer buffer = new SafeBuffer(new byte[22]);
        ClientMessage cmEncode = ClientMessage.createForEncode(buffer, 0);
        cmEncode.setVersion(((short) ((Byte.MAX_VALUE) + 10)));
        cmEncode.setMessageType(((Short.MAX_VALUE) + 10));
        cmEncode.setDataOffset((((int) (Short.MAX_VALUE)) + 10));
        cmEncode.setCorrelationId(((Integer.MAX_VALUE) + 10));
        ClientMessage cmDecode = ClientMessage.createForDecode(buffer, 0);
        Assert.assertEquals(((Byte.MAX_VALUE) + 10), cmDecode.getVersion());
        Assert.assertEquals(((Short.MAX_VALUE) + 10), cmDecode.getMessageType());
        Assert.assertEquals((((int) (Short.MAX_VALUE)) + 10), cmDecode.getDataOffset());
    }

    @Test(expected = MaxMessageSizeExceeded.class)
    public void testMessageSizeOverflow() {
        ClientMessage.createForEncode(((Integer.MAX_VALUE) << 1));
    }

    @Test
    public void testCopyClientMessage() {
        SafeBuffer byteBuffer = new SafeBuffer(new byte[1024]);
        ClientMessage clientMessage = ClientMessageTest.TestClientMessage.createForEncode(byteBuffer, 0);
        clientMessage.setMessageType(7).setVersion(((short) (3))).addFlag(BEGIN_AND_END_FLAGS).setCorrelationId(66).setPartitionId(77);
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(true);
        clientMessage.setOperationName("operationName");
        ClientMessage copyMessage = clientMessage.copy();
        Assert.assertEquals(copyMessage, clientMessage);
        Assert.assertEquals(copyMessage.getMessageType(), clientMessage.getMessageType());
        Assert.assertEquals(copyMessage.getVersion(), clientMessage.getVersion());
        Assert.assertEquals(copyMessage.getFlags(), clientMessage.getFlags());
        Assert.assertEquals(copyMessage.getCorrelationId(), clientMessage.getCorrelationId());
        Assert.assertEquals(copyMessage.getPartitionId(), clientMessage.getPartitionId());
        Assert.assertEquals(copyMessage.isRetryable(), clientMessage.isComplete());
        Assert.assertEquals(copyMessage.acquiresResource(), clientMessage.acquiresResource());
        Assert.assertEquals(copyMessage.getOperationName(), clientMessage.getOperationName());
    }
}

