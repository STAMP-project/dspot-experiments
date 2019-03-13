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


import SbeTool.KEYWORD_APPEND_TOKEN;
import java.nio.ByteBuffer;
import java.util.Map;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.generation.CompilerUtil;
import org.agrona.generation.StringWriterOutputManager;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.TestUtil;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.ir.IrDecoder;
import uk.co.real_logic.sbe.ir.IrEncoder;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.ParserOptions;
import uk.co.real_logic.sbe.xml.XmlSchemaParser;


public class GenerateFixBinaryTest {
    private static final Class<?> BUFFER_CLASS = MutableDirectBuffer.class;

    private static final String BUFFER_NAME = GenerateFixBinaryTest.BUFFER_CLASS.getName();

    private static final Class<DirectBuffer> READ_ONLY_BUFFER_CLASS = DirectBuffer.class;

    private static final String READ_ONLY_BUFFER_NAME = GenerateFixBinaryTest.READ_ONLY_BUFFER_CLASS.getName();

    private final StringWriterOutputManager outputManager = new StringWriterOutputManager();

    @Test
    public void shouldGenerateValidJava() throws Exception {
        System.setProperty(KEYWORD_APPEND_TOKEN, "_");
        final ParserOptions options = ParserOptions.builder().stopOnError(true).build();
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("FixBinary.xml"), options);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final JavaGenerator generator = new JavaGenerator(ir, GenerateFixBinaryTest.BUFFER_NAME, GenerateFixBinaryTest.READ_ONLY_BUFFER_NAME, false, false, false, outputManager);
        outputManager.setPackageName(ir.applicableNamespace());
        generator.generateMessageHeaderStub();
        generator.generateTypeStubs();
        generator.generate();
        final Map<String, CharSequence> sources = outputManager.getSources();
        final String className = "MDIncrementalRefreshTradeSummary42Decoder";
        final String fqClassName = ((ir.applicableNamespace()) + ".") + className;
        final Class<?> aClass = CompilerUtil.compileInMemory(fqClassName, sources);
        Assert.assertNotNull(aClass);
    }

    @Test
    public void shouldGenerateAndEncodeIr() throws Exception {
        System.setProperty(KEYWORD_APPEND_TOKEN, "_");
        final ParserOptions options = ParserOptions.builder().stopOnError(true).build();
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("FixBinary.xml"), options);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final ByteBuffer buffer = ByteBuffer.allocate((1024 * 1024));
        final IrEncoder irEncoder = new IrEncoder(buffer, ir);
        irEncoder.encode();
        buffer.flip();
        final IrDecoder irDecoder = new IrDecoder(buffer);
        final Ir decodedIr = irDecoder.decode();
        Assert.assertEquals(ir.id(), decodedIr.id());
        Assert.assertEquals(ir.version(), decodedIr.version());
        Assert.assertEquals(ir.byteOrder(), decodedIr.byteOrder());
        Assert.assertEquals(ir.applicableNamespace(), decodedIr.applicableNamespace());
        Assert.assertEquals(ir.packageName(), decodedIr.packageName());
        Assert.assertEquals(ir.types().size(), decodedIr.types().size());
        Assert.assertEquals(ir.messages().size(), decodedIr.messages().size());
    }
}

