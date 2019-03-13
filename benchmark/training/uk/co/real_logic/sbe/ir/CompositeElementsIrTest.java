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
import PrimitiveType.INT64;
import PrimitiveType.INT8;
import PrimitiveType.UINT32;
import PrimitiveType.UINT8;
import Signal.BEGIN_COMPOSITE;
import Signal.BEGIN_ENUM;
import Signal.BEGIN_FIELD;
import Signal.BEGIN_SET;
import Signal.ENCODING;
import Signal.END_COMPOSITE;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.TestUtil;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.XmlSchemaParser;


public class CompositeElementsIrTest {
    @Test
    public void shouldGenerateCorrectIrForCompositeElementsSchema() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("composite-elements-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final List<Token> tokens = ir.getMessage(1);
        final Token fieldToken = tokens.get(1);
        final Token outerCompositeToken = tokens.get(2);
        final Token enumToken = tokens.get(3);
        final Token zerothToken = tokens.get(7);
        final Token setToken = tokens.get(8);
        final Token innerCompositeToken = tokens.get(13);
        final Token firstToken = tokens.get(14);
        final Token secondToken = tokens.get(15);
        final Token endOuterCompositeToken = tokens.get(17);
        Assert.assertThat(fieldToken.signal(), Is.is(BEGIN_FIELD));
        Assert.assertThat(fieldToken.name(), Is.is("structure"));
        Assert.assertThat(outerCompositeToken.signal(), Is.is(BEGIN_COMPOSITE));
        Assert.assertThat(outerCompositeToken.name(), Is.is("outer"));
        Assert.assertThat(outerCompositeToken.componentTokenCount(), Is.is(16));
        Assert.assertThat(enumToken.signal(), Is.is(BEGIN_ENUM));
        Assert.assertThat(enumToken.name(), Is.is("enumOne"));
        Assert.assertThat(enumToken.encodedLength(), Is.is(1));
        Assert.assertThat(enumToken.encoding().primitiveType(), Is.is(UINT8));
        Assert.assertThat(enumToken.offset(), Is.is(0));
        Assert.assertThat(enumToken.componentTokenCount(), Is.is(4));
        Assert.assertThat(zerothToken.signal(), Is.is(ENCODING));
        Assert.assertThat(zerothToken.offset(), Is.is(1));
        Assert.assertThat(zerothToken.encoding().primitiveType(), Is.is(UINT8));
        Assert.assertThat(setToken.signal(), Is.is(BEGIN_SET));
        Assert.assertThat(setToken.name(), Is.is("setOne"));
        Assert.assertThat(setToken.encodedLength(), Is.is(4));
        Assert.assertThat(setToken.encoding().primitiveType(), Is.is(UINT32));
        Assert.assertThat(setToken.offset(), Is.is(2));
        Assert.assertThat(setToken.componentTokenCount(), Is.is(5));
        Assert.assertThat(innerCompositeToken.signal(), Is.is(BEGIN_COMPOSITE));
        Assert.assertThat(innerCompositeToken.name(), Is.is("inner"));
        Assert.assertThat(innerCompositeToken.offset(), Is.is(6));
        Assert.assertThat(innerCompositeToken.componentTokenCount(), Is.is(4));
        Assert.assertThat(firstToken.signal(), Is.is(ENCODING));
        Assert.assertThat(firstToken.name(), Is.is("first"));
        Assert.assertThat(firstToken.offset(), Is.is(0));
        Assert.assertThat(firstToken.encoding().primitiveType(), Is.is(INT64));
        Assert.assertThat(secondToken.signal(), Is.is(ENCODING));
        Assert.assertThat(secondToken.name(), Is.is("second"));
        Assert.assertThat(secondToken.offset(), Is.is(8));
        Assert.assertThat(secondToken.encoding().primitiveType(), Is.is(INT64));
        Assert.assertThat(endOuterCompositeToken.signal(), Is.is(END_COMPOSITE));
        Assert.assertThat(endOuterCompositeToken.name(), Is.is("outer"));
    }

    @Test
    public void shouldGenerateCorrectIrForCompositeElementsWithOffsetsSchemaRc4() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("composite-elements-schema-rc4.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final List<Token> tokens = ir.getMessage(2);
        final Token outerCompositeToken = tokens.get(2);
        final Token enumToken = tokens.get(3);
        final Token zerothToken = tokens.get(7);
        final Token setToken = tokens.get(8);
        final Token innerCompositeToken = tokens.get(13);
        final Token firstToken = tokens.get(14);
        final Token secondToken = tokens.get(15);
        final Token endOuterCompositeToken = tokens.get(17);
        Assert.assertThat(outerCompositeToken.signal(), Is.is(BEGIN_COMPOSITE));
        Assert.assertThat(outerCompositeToken.name(), Is.is("outerWithOffsets"));
        Assert.assertThat(outerCompositeToken.encodedLength(), Is.is(32));
        Assert.assertThat(enumToken.signal(), Is.is(BEGIN_ENUM));
        Assert.assertThat(enumToken.encodedLength(), Is.is(1));
        Assert.assertThat(enumToken.encoding().primitiveType(), Is.is(UINT8));
        Assert.assertThat(enumToken.offset(), Is.is(2));
        Assert.assertThat(zerothToken.signal(), Is.is(ENCODING));
        Assert.assertThat(zerothToken.offset(), Is.is(3));
        Assert.assertThat(zerothToken.encoding().primitiveType(), Is.is(UINT8));
        Assert.assertThat(setToken.signal(), Is.is(BEGIN_SET));
        Assert.assertThat(setToken.name(), Is.is("setOne"));
        Assert.assertThat(setToken.encodedLength(), Is.is(4));
        Assert.assertThat(setToken.encoding().primitiveType(), Is.is(UINT32));
        Assert.assertThat(setToken.offset(), Is.is(8));
        Assert.assertThat(innerCompositeToken.signal(), Is.is(BEGIN_COMPOSITE));
        Assert.assertThat(innerCompositeToken.name(), Is.is("inner"));
        Assert.assertThat(innerCompositeToken.offset(), Is.is(16));
        Assert.assertThat(firstToken.signal(), Is.is(ENCODING));
        Assert.assertThat(firstToken.name(), Is.is("first"));
        Assert.assertThat(firstToken.offset(), Is.is(0));
        Assert.assertThat(firstToken.encoding().primitiveType(), Is.is(INT64));
        Assert.assertThat(secondToken.signal(), Is.is(ENCODING));
        Assert.assertThat(secondToken.name(), Is.is("second"));
        Assert.assertThat(secondToken.offset(), Is.is(8));
        Assert.assertThat(secondToken.encoding().primitiveType(), Is.is(INT64));
        Assert.assertThat(endOuterCompositeToken.signal(), Is.is(END_COMPOSITE));
        Assert.assertThat(endOuterCompositeToken.name(), Is.is("outerWithOffsets"));
    }

    @Test
    public void shouldGenerateCorrectIrForCompositeWithRefSchema() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("composite-elements-schema-rc4.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final List<Token> tokens = ir.getMessage(3);
        final Token beginCompositeToken = tokens.get(2);
        final Token mantissaToken = tokens.get(3);
        final Token exponentToken = tokens.get(4);
        final Token enumToken = tokens.get(5);
        final Token endCompositeToken = tokens.get(9);
        Assert.assertThat(beginCompositeToken.signal(), Is.is(BEGIN_COMPOSITE));
        Assert.assertThat(beginCompositeToken.name(), Is.is("futuresPrice"));
        Assert.assertThat(beginCompositeToken.encodedLength(), Is.is(11));
        Assert.assertThat(mantissaToken.signal(), Is.is(ENCODING));
        Assert.assertThat(mantissaToken.name(), Is.is("mantissa"));
        Assert.assertThat(mantissaToken.offset(), Is.is(0));
        Assert.assertThat(mantissaToken.encoding().primitiveType(), Is.is(INT64));
        Assert.assertThat(exponentToken.signal(), Is.is(ENCODING));
        Assert.assertThat(exponentToken.name(), Is.is("exponent"));
        Assert.assertThat(exponentToken.offset(), Is.is(8));
        Assert.assertThat(exponentToken.encoding().primitiveType(), Is.is(INT8));
        Assert.assertThat(enumToken.signal(), Is.is(BEGIN_ENUM));
        Assert.assertThat(enumToken.encodedLength(), Is.is(1));
        Assert.assertThat(enumToken.encoding().primitiveType(), Is.is(UINT8));
        Assert.assertThat(enumToken.offset(), Is.is(10));
        Assert.assertThat(endCompositeToken.signal(), Is.is(END_COMPOSITE));
        Assert.assertThat(endCompositeToken.name(), Is.is("futuresPrice"));
    }
}

