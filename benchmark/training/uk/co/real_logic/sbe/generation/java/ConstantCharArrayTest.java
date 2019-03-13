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


import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.generation.StringWriterOutputManager;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.TestUtil;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.ParserOptions;
import uk.co.real_logic.sbe.xml.XmlSchemaParser;


public class ConstantCharArrayTest {
    private static final Class<?> BUFFER_CLASS = MutableDirectBuffer.class;

    private static final String BUFFER_NAME = ConstantCharArrayTest.BUFFER_CLASS.getName();

    private static final Class<DirectBuffer> READ_ONLY_BUFFER_CLASS = DirectBuffer.class;

    private static final String READ_ONLY_BUFFER_NAME = ConstantCharArrayTest.READ_ONLY_BUFFER_CLASS.getName();

    @Test
    public void shouldGenerateConstCharArrayMethods() throws Exception {
        final ParserOptions options = ParserOptions.builder().stopOnError(true).build();
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("issue505.xml"), options);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final StringWriterOutputManager outputManager = new StringWriterOutputManager();
        outputManager.setPackageName(ir.applicableNamespace());
        final JavaGenerator generator = new JavaGenerator(ir, ConstantCharArrayTest.BUFFER_NAME, ConstantCharArrayTest.READ_ONLY_BUFFER_NAME, false, false, false, outputManager);
        generator.generate();
        final String sources = outputManager.getSources().toString();
        final String expectedOne = "    public byte sourceOne()\n" + (("    {\n" + "        return (byte)67;\n") + "    }");
        Assert.assertThat(sources, StringContains.containsString(expectedOne));
        final String expectedTwo = "    public byte sourceTwo()\n" + (("    {\n" + "        return (byte)68;\n") + "    }");
        Assert.assertThat(sources, StringContains.containsString(expectedTwo));
        final String expectedThree = "    public String sourceThree()\n" + (("    {\n" + "        return \"EF\";\n") + "    }");
        Assert.assertThat(sources, StringContains.containsString(expectedThree));
        final String expectedFour = "    public String sourceFour()\n" + (("    {\n" + "        return \"GH\";\n") + "    }");
        Assert.assertThat(sources, StringContains.containsString(expectedFour));
    }
}

