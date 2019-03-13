/**
 * Copyright 2013-2019 Real Logic Ltd.
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
package uk.co.real_logic.sbe.generation.java;


import EnumOne.Value10;
import composite.elements.MessageHeaderEncoder;
import composite.elements.MsgDecoder;
import composite.elements.MsgEncoder;
import java.nio.ByteBuffer;
import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.ir.generated.MessageHeaderDecoder;
import uk.co.real_logic.sbe.otf.OtfHeaderDecoder;
import uk.co.real_logic.sbe.otf.OtfMessageDecoder;
import uk.co.real_logic.sbe.otf.TokenListener;


public class CompositeElementsGenerationTest {
    private static final MessageHeaderEncoder MESSAGE_HEADER = new MessageHeaderEncoder();

    private static final MsgEncoder MSG_ENCODER = new MsgEncoder();

    private static final int MSG_BUFFER_CAPACITY = 4 * 1024;

    private static final int SCHEMA_BUFFER_CAPACITY = 16 * 1024;

    @Test
    public void shouldEncodeCorrectly() {
        final ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(CompositeElementsGenerationTest.MSG_BUFFER_CAPACITY);
        CompositeElementsGenerationTest.encodeTestMessage(encodedMsgBuffer);
        final DirectBuffer decodeBuffer = new UnsafeBuffer(encodedMsgBuffer);
        int offset = 0;
        Assert.assertThat(decodeBuffer.getShort(offset), Is.is(((short) (22))));
        offset += BitUtil.SIZE_OF_SHORT;
        Assert.assertThat(decodeBuffer.getShort(offset), Is.is(((short) (1))));
        offset += BitUtil.SIZE_OF_SHORT;
        Assert.assertThat(decodeBuffer.getShort(offset), Is.is(((short) (3))));
        offset += BitUtil.SIZE_OF_SHORT;
        Assert.assertThat(decodeBuffer.getShort(offset), Is.is(((short) (0))));
        offset += BitUtil.SIZE_OF_SHORT;
        Assert.assertThat(decodeBuffer.getByte(offset), Is.is(((byte) (10))));
        offset += BitUtil.SIZE_OF_BYTE;
        Assert.assertThat(decodeBuffer.getByte(offset), Is.is(((byte) (42))));
        offset += BitUtil.SIZE_OF_BYTE;
        Assert.assertThat(decodeBuffer.getInt(offset), Is.is(65536));
        offset += BitUtil.SIZE_OF_INT;
        Assert.assertThat(decodeBuffer.getLong(offset), Is.is(101L));
        offset += BitUtil.SIZE_OF_LONG;
        Assert.assertThat(decodeBuffer.getLong(offset), Is.is(202L));
    }

    @Test
    public void shouldDecodeCorrectly() {
        final ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(CompositeElementsGenerationTest.MSG_BUFFER_CAPACITY);
        CompositeElementsGenerationTest.encodeTestMessage(encodedMsgBuffer);
        final DirectBuffer decodeBuffer = new UnsafeBuffer(encodedMsgBuffer);
        final MessageHeaderDecoder hdrDecoder = new MessageHeaderDecoder();
        final MsgDecoder msgDecoder = new MsgDecoder();
        hdrDecoder.wrap(decodeBuffer, 0);
        msgDecoder.wrap(decodeBuffer, hdrDecoder.encodedLength(), CompositeElementsGenerationTest.MSG_ENCODER.sbeBlockLength(), CompositeElementsGenerationTest.MSG_ENCODER.sbeSchemaVersion());
        Assert.assertThat(hdrDecoder.blockLength(), Is.is(22));
        Assert.assertThat(hdrDecoder.templateId(), Is.is(1));
        Assert.assertThat(hdrDecoder.schemaId(), Is.is(3));
        Assert.assertThat(hdrDecoder.version(), Is.is(0));
        Assert.assertThat(msgDecoder.structure().enumOne(), Is.is(Value10));
        Assert.assertThat(msgDecoder.structure().zeroth(), Is.is(((short) (42))));
        Assert.assertThat(msgDecoder.structure().setOne().bit0(), Is.is(false));
        Assert.assertThat(msgDecoder.structure().setOne().bit16(), Is.is(true));
        Assert.assertThat(msgDecoder.structure().setOne().bit26(), Is.is(false));
        Assert.assertThat(msgDecoder.structure().inner().first(), Is.is(101L));
        Assert.assertThat(msgDecoder.structure().inner().second(), Is.is(202L));
        Assert.assertThat(msgDecoder.encodedLength(), Is.is(22));
    }

    @Test
    public void shouldDisplayCorrectly() {
        final ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(CompositeElementsGenerationTest.MSG_BUFFER_CAPACITY);
        CompositeElementsGenerationTest.encodeTestMessage(encodedMsgBuffer);
        final String compositeString = CompositeElementsGenerationTest.MSG_ENCODER.structure().toString();
        Assert.assertThat(compositeString, Matchers.containsString("enumOne="));
        Assert.assertThat(compositeString, Matchers.not(Matchers.containsString("enumOne=|")));
        Assert.assertThat(compositeString, Matchers.containsString("setOne="));
        Assert.assertThat(compositeString, Matchers.not(Matchers.containsString("setOne=|")));
    }

    @Test
    public void shouldOtfDecodeCorrectly() throws Exception {
        final ByteBuffer encodedSchemaBuffer = ByteBuffer.allocate(CompositeElementsGenerationTest.SCHEMA_BUFFER_CAPACITY);
        CompositeElementsGenerationTest.encodeSchema(encodedSchemaBuffer);
        final ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(CompositeElementsGenerationTest.MSG_BUFFER_CAPACITY);
        CompositeElementsGenerationTest.encodeTestMessage(encodedMsgBuffer);
        encodedSchemaBuffer.flip();
        final Ir ir = CompositeElementsGenerationTest.decodeIr(encodedSchemaBuffer);
        final DirectBuffer decodeBuffer = new UnsafeBuffer(encodedMsgBuffer);
        final OtfHeaderDecoder otfHeaderDecoder = new OtfHeaderDecoder(ir.headerStructure());
        Assert.assertThat(otfHeaderDecoder.getBlockLength(decodeBuffer, 0), Is.is(22));
        Assert.assertThat(otfHeaderDecoder.getSchemaId(decodeBuffer, 0), Is.is(3));
        Assert.assertThat(otfHeaderDecoder.getTemplateId(decodeBuffer, 0), Is.is(1));
        Assert.assertThat(otfHeaderDecoder.getSchemaVersion(decodeBuffer, 0), Is.is(0));
        final TokenListener mockTokenListener = Mockito.mock(TokenListener.class);
        OtfMessageDecoder.decode(decodeBuffer, otfHeaderDecoder.encodedLength(), CompositeElementsGenerationTest.MSG_ENCODER.sbeSchemaVersion(), CompositeElementsGenerationTest.MSG_ENCODER.sbeBlockLength(), ir.getMessage(CompositeElementsGenerationTest.MSG_ENCODER.sbeTemplateId()), mockTokenListener);
        final InOrder inOrder = Mockito.inOrder(mockTokenListener);
        inOrder.verify(mockTokenListener).onBeginComposite(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(2), ArgumentMatchers.eq(17));
        inOrder.verify(mockTokenListener).onEnum(ArgumentMatchers.any(), ArgumentMatchers.eq(decodeBuffer), ArgumentMatchers.eq(8), ArgumentMatchers.any(), ArgumentMatchers.eq(3), ArgumentMatchers.eq(6), ArgumentMatchers.eq(0));
        inOrder.verify(mockTokenListener).onEncoding(ArgumentMatchers.any(), ArgumentMatchers.eq(decodeBuffer), ArgumentMatchers.eq(9), ArgumentMatchers.any(), ArgumentMatchers.eq(0));
        inOrder.verify(mockTokenListener).onBitSet(ArgumentMatchers.any(), ArgumentMatchers.eq(decodeBuffer), ArgumentMatchers.eq(10), ArgumentMatchers.any(), ArgumentMatchers.eq(8), ArgumentMatchers.eq(12), ArgumentMatchers.eq(0));
        inOrder.verify(mockTokenListener).onBeginComposite(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(13), ArgumentMatchers.eq(16));
        inOrder.verify(mockTokenListener).onEncoding(ArgumentMatchers.any(), ArgumentMatchers.eq(decodeBuffer), ArgumentMatchers.eq(14), ArgumentMatchers.any(), ArgumentMatchers.eq(0));
        inOrder.verify(mockTokenListener).onEncoding(ArgumentMatchers.any(), ArgumentMatchers.eq(decodeBuffer), ArgumentMatchers.eq(22), ArgumentMatchers.any(), ArgumentMatchers.eq(0));
        inOrder.verify(mockTokenListener).onEndComposite(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(13), ArgumentMatchers.eq(16));
        inOrder.verify(mockTokenListener).onEndComposite(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(2), ArgumentMatchers.eq(17));
    }
}

