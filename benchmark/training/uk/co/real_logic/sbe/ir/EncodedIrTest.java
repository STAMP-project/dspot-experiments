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
package uk.co.real_logic.sbe.ir;


import ParserOptions.DEFAULT;
import java.nio.ByteBuffer;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.TestUtil;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.XmlSchemaParser;


public class EncodedIrTest {
    private static final int CAPACITY = 1024 * 16;

    @Test
    public void shouldEncodeIr() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("basic-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(EncodedIrTest.CAPACITY);
        final IrEncoder irEncoder = new IrEncoder(buffer, ir);
        irEncoder.encode();
    }

    @Test
    public void shouldEncodeThenDecodeIr() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("basic-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(EncodedIrTest.CAPACITY);
        final IrEncoder irEncoder = new IrEncoder(buffer, ir);
        irEncoder.encode();
        buffer.flip();
        final IrDecoder decoder = new IrDecoder(buffer);
        decoder.decode();
    }

    @Test
    public void shouldHandleRightSizedBuffer() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("basic-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(EncodedIrTest.CAPACITY);
        final IrEncoder irEncoder = new IrEncoder(buffer, ir);
        irEncoder.encode();
        buffer.flip();
        final ByteBuffer readBuffer = ByteBuffer.allocateDirect(buffer.remaining());
        readBuffer.put(buffer);
        readBuffer.flip();
        final IrDecoder irDecoder = new IrDecoder(readBuffer);
        irDecoder.decode();
    }

    @Test
    public void shouldDecodeCorrectFrame() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("code-generation-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(EncodedIrTest.CAPACITY);
        final IrEncoder irEncoder = new IrEncoder(buffer, ir);
        irEncoder.encode();
        buffer.flip();
        final IrDecoder irDecoder = new IrDecoder(buffer);
        final Ir decodedIr = irDecoder.decode();
        Assert.assertThat(decodedIr.id(), Is.is(ir.id()));
        Assert.assertThat(decodedIr.version(), Is.is(ir.version()));
        Assert.assertThat(decodedIr.semanticVersion(), Is.is(ir.semanticVersion()));
        Assert.assertThat(decodedIr.packageName(), Is.is(ir.packageName()));
        Assert.assertThat(decodedIr.namespaceName(), Is.is(ir.namespaceName()));
    }

    @Test
    public void shouldDecodeCorrectHeader() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("code-generation-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(EncodedIrTest.CAPACITY);
        final IrEncoder irEncoder = new IrEncoder(buffer, ir);
        irEncoder.encode();
        buffer.flip();
        final IrDecoder irDecoder = new IrDecoder(buffer);
        final Ir decodedIr = irDecoder.decode();
        final List<Token> tokens = decodedIr.headerStructure().tokens();
        Assert.assertThat(tokens.size(), Is.is(ir.headerStructure().tokens().size()));
        for (int i = 0, size = tokens.size(); i < size; i++) {
            assertEqual(tokens.get(i), ir.headerStructure().tokens().get(i));
        }
    }

    @Test
    public void shouldDecodeCorrectMessagesAndTypes() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("code-generation-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(EncodedIrTest.CAPACITY);
        final IrEncoder irEncoder = new IrEncoder(buffer, ir);
        irEncoder.encode();
        buffer.flip();
        final IrDecoder irDecoder = new IrDecoder(buffer);
        final Ir decodedIr = irDecoder.decode();
        Assert.assertThat(decodedIr.messages().size(), Is.is(ir.messages().size()));
        for (final List<Token> decodedTokenList : decodedIr.messages()) {
            final List<Token> tokens = ir.getMessage(decodedTokenList.get(0).id());
            Assert.assertThat(decodedTokenList.size(), Is.is(tokens.size()));
            for (int i = 0, size = decodedTokenList.size(); i < size; i++) {
                assertEqual(decodedTokenList.get(i), tokens.get(i));
            }
        }
        Assert.assertThat(decodedIr.types().size(), Is.is(ir.types().size()));
        for (final List<Token> decodedTokenList : decodedIr.types()) {
            final Token t = decodedTokenList.get(0);
            final String name = ((t.referencedName()) != null) ? t.referencedName() : t.name();
            final List<Token> tokens = ir.getType(name);
            Assert.assertThat((name + " token count"), decodedTokenList.size(), Is.is(tokens.size()));
            for (int i = 0, size = decodedTokenList.size(); i < size; i++) {
                assertEqual(decodedTokenList.get(i), tokens.get(i));
            }
        }
    }
}

