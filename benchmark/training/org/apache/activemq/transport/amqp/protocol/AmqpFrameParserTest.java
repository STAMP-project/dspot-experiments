/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.amqp.protocol;


import java.nio.ByteBuffer;
import java.util.List;
import org.apache.activemq.transport.amqp.AmqpFrameParser;
import org.apache.activemq.transport.amqp.AmqpHeader;
import org.apache.activemq.transport.amqp.AmqpWireFormat;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AmqpFrameParserTest {
    private static final Logger LOG = LoggerFactory.getLogger(AmqpFrameParserTest.class);

    private final AmqpWireFormat amqpWireFormat = new AmqpWireFormat();

    private List<Object> frames;

    private AmqpFrameParser codec;

    private final int MESSAGE_SIZE = (5 * 1024) * 1024;

    @Test
    public void testAMQPHeaderReadEmptyBuffer() throws Exception {
        codec.parse(ByteBuffer.allocate(0));
    }

    @Test
    public void testAMQPHeaderReadNull() throws Exception {
        codec.parse(((ByteBuffer) (null)));
    }

    @Test
    public void testAMQPHeaderRead() throws Exception {
        AmqpHeader inputHeader = new AmqpHeader();
        codec.parse(inputHeader.getBuffer().toByteBuffer());
        Assert.assertEquals(1, frames.size());
        Object outputFrame = frames.get(0);
        Assert.assertTrue((outputFrame instanceof AmqpHeader));
        AmqpHeader outputHeader = ((AmqpHeader) (outputFrame));
        assertHeadersEqual(inputHeader, outputHeader);
    }

    @Test
    public void testAMQPHeaderReadSingleByteReads() throws Exception {
        AmqpHeader inputHeader = new AmqpHeader();
        for (int i = 0; i < (inputHeader.getBuffer().length()); ++i) {
            codec.parse(inputHeader.getBuffer().slice(i, (i + 1)).toByteBuffer());
        }
        Assert.assertEquals(1, frames.size());
        Object outputFrame = frames.get(0);
        Assert.assertTrue((outputFrame instanceof AmqpHeader));
        AmqpHeader outputHeader = ((AmqpHeader) (outputFrame));
        assertHeadersEqual(inputHeader, outputHeader);
    }

    @Test
    public void testResetReadsNextAMQPHeaderMidParse() throws Exception {
        AmqpHeader inputHeader = new AmqpHeader();
        DataByteArrayOutputStream headers = new DataByteArrayOutputStream();
        headers.write(inputHeader.getBuffer());
        headers.write(inputHeader.getBuffer());
        headers.write(inputHeader.getBuffer());
        headers.close();
        codec = new AmqpFrameParser(new AmqpFrameParser.AMQPFrameSink() {
            @Override
            public void onFrame(Object frame) {
                frames.add(frame);
                codec.reset();
            }
        });
        codec.parse(headers.toBuffer().toByteBuffer());
        Assert.assertEquals(3, frames.size());
        for (Object header : frames) {
            Assert.assertTrue((header instanceof AmqpHeader));
            AmqpHeader outputHeader = ((AmqpHeader) (header));
            assertHeadersEqual(inputHeader, outputHeader);
        }
    }

    @Test
    public void testResetReadsNextAMQPHeader() throws Exception {
        AmqpHeader inputHeader = new AmqpHeader();
        for (int i = 1; i <= 3; ++i) {
            codec.parse(inputHeader.getBuffer().toByteBuffer());
            codec.reset();
            Assert.assertEquals(i, frames.size());
            Object outputFrame = frames.get((i - 1));
            Assert.assertTrue((outputFrame instanceof AmqpHeader));
            AmqpHeader outputHeader = ((AmqpHeader) (outputFrame));
            assertHeadersEqual(inputHeader, outputHeader);
        }
    }

    @Test
    public void testResetReadsNextAMQPHeaderAfterContentParsed() throws Exception {
        AmqpHeader inputHeader = new AmqpHeader();
        byte[] CONTENTS = new byte[MESSAGE_SIZE];
        for (int i = 0; i < (MESSAGE_SIZE); i++) {
            CONTENTS[i] = 'a';
        }
        DataByteArrayOutputStream output = new DataByteArrayOutputStream();
        output.write(inputHeader.getBuffer());
        output.writeInt(((MESSAGE_SIZE) + 4));
        output.write(CONTENTS);
        output.write(inputHeader.getBuffer());
        output.writeInt(((MESSAGE_SIZE) + 4));
        output.write(CONTENTS);
        output.close();
        codec = new AmqpFrameParser(new AmqpFrameParser.AMQPFrameSink() {
            @Override
            public void onFrame(Object frame) {
                frames.add(frame);
                if (!(frame instanceof AmqpHeader)) {
                    codec.reset();
                }
            }
        });
        codec.parse(output.toBuffer().toByteBuffer());
        for (int i = 0; i < 4; ++i) {
            Object frame = frames.get(i);
            Assert.assertTrue((frame instanceof AmqpHeader));
            AmqpHeader outputHeader = ((AmqpHeader) (frame));
            assertHeadersEqual(inputHeader, outputHeader);
            frame = frames.get((++i));
            Assert.assertFalse((frame instanceof AmqpHeader));
            Assert.assertTrue((frame instanceof Buffer));
            Assert.assertEquals(((MESSAGE_SIZE) + 4), getLength());
        }
    }

    @Test
    public void testHeaderAndFrameAreRead() throws Exception {
        AmqpHeader inputHeader = new AmqpHeader();
        DataByteArrayOutputStream output = new DataByteArrayOutputStream();
        byte[] CONTENTS = new byte[MESSAGE_SIZE];
        for (int i = 0; i < (MESSAGE_SIZE); i++) {
            CONTENTS[i] = 'a';
        }
        output.write(inputHeader.getBuffer());
        output.writeInt(((MESSAGE_SIZE) + 4));
        output.write(CONTENTS);
        output.close();
        codec.parse(output.toBuffer().toByteBuffer());
        Assert.assertEquals(2, frames.size());
        Object outputFrame = frames.get(0);
        Assert.assertTrue((outputFrame instanceof AmqpHeader));
        AmqpHeader outputHeader = ((AmqpHeader) (outputFrame));
        assertHeadersEqual(inputHeader, outputHeader);
        outputFrame = frames.get(1);
        Assert.assertTrue((outputFrame instanceof Buffer));
        Buffer frame = ((Buffer) (outputFrame));
        Assert.assertEquals(((MESSAGE_SIZE) + 4), frame.length());
    }

    @Test
    public void testHeaderAndFrameAreReadNoWireFormat() throws Exception {
        codec.setWireFormat(null);
        AmqpHeader inputHeader = new AmqpHeader();
        DataByteArrayOutputStream output = new DataByteArrayOutputStream();
        byte[] CONTENTS = new byte[MESSAGE_SIZE];
        for (int i = 0; i < (MESSAGE_SIZE); i++) {
            CONTENTS[i] = 'a';
        }
        output.write(inputHeader.getBuffer());
        output.writeInt(((MESSAGE_SIZE) + 4));
        output.write(CONTENTS);
        output.close();
        codec.parse(output.toBuffer().toByteBuffer());
        Assert.assertEquals(2, frames.size());
        Object outputFrame = frames.get(0);
        Assert.assertTrue((outputFrame instanceof AmqpHeader));
        AmqpHeader outputHeader = ((AmqpHeader) (outputFrame));
        assertHeadersEqual(inputHeader, outputHeader);
        outputFrame = frames.get(1);
        Assert.assertTrue((outputFrame instanceof Buffer));
        Buffer frame = ((Buffer) (outputFrame));
        Assert.assertEquals(((MESSAGE_SIZE) + 4), frame.length());
    }

    @Test
    public void testHeaderAndMulitpleFramesAreRead() throws Exception {
        AmqpHeader inputHeader = new AmqpHeader();
        final int FRAME_SIZE_HEADER = 4;
        final int FRAME_SIZE = 65531;
        final int NUM_FRAMES = 5;
        DataByteArrayOutputStream output = new DataByteArrayOutputStream();
        byte[] CONTENTS = new byte[FRAME_SIZE];
        for (int i = 0; i < FRAME_SIZE; i++) {
            CONTENTS[i] = 'a';
        }
        output.write(inputHeader.getBuffer());
        for (int i = 0; i < NUM_FRAMES; ++i) {
            output.writeInt((FRAME_SIZE + FRAME_SIZE_HEADER));
            output.write(CONTENTS);
        }
        output.close();
        codec.parse(output.toBuffer().toByteBuffer());
        Assert.assertEquals((NUM_FRAMES + 1), frames.size());
        Object outputFrame = frames.get(0);
        Assert.assertTrue((outputFrame instanceof AmqpHeader));
        AmqpHeader outputHeader = ((AmqpHeader) (outputFrame));
        assertHeadersEqual(inputHeader, outputHeader);
        for (int i = 1; i <= NUM_FRAMES; ++i) {
            outputFrame = frames.get(i);
            Assert.assertTrue((outputFrame instanceof Buffer));
            Buffer frame = ((Buffer) (outputFrame));
            Assert.assertEquals((FRAME_SIZE + FRAME_SIZE_HEADER), frame.length());
        }
    }

    @Test
    public void testCodecRejectsToLargeFrames() throws Exception {
        amqpWireFormat.setMaxFrameSize(MESSAGE_SIZE);
        AmqpHeader inputHeader = new AmqpHeader();
        DataByteArrayOutputStream output = new DataByteArrayOutputStream();
        byte[] CONTENTS = new byte[MESSAGE_SIZE];
        for (int i = 0; i < (MESSAGE_SIZE); i++) {
            CONTENTS[i] = 'a';
        }
        output.write(inputHeader.getBuffer());
        output.writeInt(((MESSAGE_SIZE) + 4));
        output.write(CONTENTS);
        output.close();
        try {
            codec.parse(output.toBuffer().toByteBuffer());
            Assert.fail("Should have failed to read the large frame.");
        } catch (Exception ex) {
            AmqpFrameParserTest.LOG.debug("Caught expected error: {}", ex.getMessage());
        }
    }

    @Test
    public void testReadPartialPayload() throws Exception {
        AmqpHeader inputHeader = new AmqpHeader();
        DataByteArrayOutputStream output = new DataByteArrayOutputStream();
        byte[] HALF_CONTENT = new byte[(MESSAGE_SIZE) / 2];
        for (int i = 0; i < ((MESSAGE_SIZE) / 2); i++) {
            HALF_CONTENT[i] = 'a';
        }
        output.write(inputHeader.getBuffer());
        output.writeInt(((MESSAGE_SIZE) + 4));
        output.close();
        codec.parse(output.toBuffer().toByteBuffer());
        Assert.assertEquals(1, frames.size());
        output = new DataByteArrayOutputStream();
        output.write(HALF_CONTENT);
        output.close();
        codec.parse(output.toBuffer().toByteBuffer());
        Assert.assertEquals(1, frames.size());
        output = new DataByteArrayOutputStream();
        output.write(HALF_CONTENT);
        output.close();
        codec.parse(output.toBuffer().toByteBuffer());
        Assert.assertEquals(2, frames.size());
    }
}

