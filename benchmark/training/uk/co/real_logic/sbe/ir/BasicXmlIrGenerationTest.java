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
import PrimitiveType.CHAR;
import PrimitiveType.UINT16;
import PrimitiveType.UINT32;
import PrimitiveType.UINT8;
import Signal.BEGIN_COMPOSITE;
import Signal.BEGIN_FIELD;
import Signal.BEGIN_GROUP;
import Signal.BEGIN_MESSAGE;
import Signal.BEGIN_VAR_DATA;
import Signal.ENCODING;
import Signal.END_COMPOSITE;
import Signal.END_FIELD;
import Signal.END_MESSAGE;
import Signal.END_VAR_DATA;
import Token.INVALID_ID;
import java.nio.ByteOrder;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.TestUtil;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.XmlSchemaParser;


public class BasicXmlIrGenerationTest {
    @Test
    public void shouldGenerateCorrectIrForMessageHeader() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("basic-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final List<Token> tokens = ir.headerStructure().tokens();
        Assert.assertThat(Integer.valueOf(tokens.size()), Is.is(Integer.valueOf(6)));
        /* assert all elements of node 0 */
        Assert.assertThat(tokens.get(0).signal(), Is.is(BEGIN_COMPOSITE));
        Assert.assertThat(tokens.get(0).name(), Is.is("messageHeader"));
        Assert.assertThat(Integer.valueOf(tokens.get(0).id()), Is.is(Integer.valueOf(INVALID_ID)));
        Assert.assertThat(Integer.valueOf(tokens.get(0).encodedLength()), Is.is(Integer.valueOf(8)));
        Assert.assertThat(Integer.valueOf(tokens.get(0).offset()), Is.is(Integer.valueOf(0)));
        /* assert all elements of node 1 */
        Assert.assertThat(tokens.get(1).signal(), Is.is(ENCODING));
        Assert.assertThat(tokens.get(1).name(), Is.is("blockLength"));
        Assert.assertThat(tokens.get(1).encoding().primitiveType(), Is.is(UINT16));
        Assert.assertThat(Integer.valueOf(tokens.get(1).id()), Is.is(Integer.valueOf(INVALID_ID)));
        Assert.assertThat(Integer.valueOf(tokens.get(1).encodedLength()), Is.is(Integer.valueOf(2)));
        Assert.assertThat(Integer.valueOf(tokens.get(1).offset()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(tokens.get(1).encoding().byteOrder(), Is.is(ByteOrder.LITTLE_ENDIAN));
        /* assert all elements of node 2 */
        Assert.assertThat(tokens.get(2).signal(), Is.is(ENCODING));
        Assert.assertThat(tokens.get(2).name(), Is.is("templateId"));
        Assert.assertThat(tokens.get(2).encoding().primitiveType(), Is.is(UINT16));
        Assert.assertThat(Integer.valueOf(tokens.get(2).id()), Is.is(Integer.valueOf(INVALID_ID)));
        Assert.assertThat(Integer.valueOf(tokens.get(2).encodedLength()), Is.is(Integer.valueOf(2)));
        Assert.assertThat(Integer.valueOf(tokens.get(2).offset()), Is.is(Integer.valueOf(2)));
        Assert.assertThat(tokens.get(2).encoding().byteOrder(), Is.is(ByteOrder.LITTLE_ENDIAN));
        /* assert all elements of node 3 */
        Assert.assertThat(tokens.get(3).signal(), Is.is(ENCODING));
        Assert.assertThat(tokens.get(3).name(), Is.is("schemaId"));
        Assert.assertThat(tokens.get(3).encoding().primitiveType(), Is.is(UINT16));
        Assert.assertThat(Integer.valueOf(tokens.get(3).id()), Is.is(Integer.valueOf(INVALID_ID)));
        Assert.assertThat(Integer.valueOf(tokens.get(3).encodedLength()), Is.is(Integer.valueOf(2)));
        Assert.assertThat(Integer.valueOf(tokens.get(3).offset()), Is.is(Integer.valueOf(4)));
        Assert.assertThat(tokens.get(3).encoding().byteOrder(), Is.is(ByteOrder.LITTLE_ENDIAN));
        /* assert all elements of node 4 */
        Assert.assertThat(tokens.get(4).signal(), Is.is(ENCODING));
        Assert.assertThat(tokens.get(4).name(), Is.is("version"));
        Assert.assertThat(tokens.get(4).encoding().primitiveType(), Is.is(UINT16));
        Assert.assertThat(Integer.valueOf(tokens.get(4).id()), Is.is(Integer.valueOf(INVALID_ID)));
        Assert.assertThat(Integer.valueOf(tokens.get(4).encodedLength()), Is.is(Integer.valueOf(2)));
        Assert.assertThat(Integer.valueOf(tokens.get(4).offset()), Is.is(Integer.valueOf(6)));
        Assert.assertThat(tokens.get(4).encoding().byteOrder(), Is.is(ByteOrder.LITTLE_ENDIAN));
        /* assert all elements of node 6 */
        Assert.assertThat(tokens.get(5).signal(), Is.is(END_COMPOSITE));
        Assert.assertThat(tokens.get(5).name(), Is.is("messageHeader"));
        Assert.assertThat(Integer.valueOf(tokens.get(5).id()), Is.is(Integer.valueOf(INVALID_ID)));
        Assert.assertThat(Integer.valueOf(tokens.get(5).encodedLength()), Is.is(Integer.valueOf(8)));
        Assert.assertThat(Integer.valueOf(tokens.get(5).offset()), Is.is(Integer.valueOf(0)));
    }

    @Test
    public void shouldGenerateCorrectIrForBasicMessage() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("basic-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final List<Token> tokens = ir.getMessage(50001);
        Assert.assertThat(Integer.valueOf(tokens.size()), Is.is(Integer.valueOf(5)));
        /* assert all elements of node 0 */
        Assert.assertThat(tokens.get(0).signal(), Is.is(BEGIN_MESSAGE));
        Assert.assertThat(tokens.get(0).name(), Is.is("TestMessage50001"));
        Assert.assertThat(Integer.valueOf(tokens.get(0).id()), Is.is(Integer.valueOf(50001)));
        Assert.assertThat(Integer.valueOf(tokens.get(0).encodedLength()), Is.is(Integer.valueOf(16)));
        Assert.assertThat(Integer.valueOf(tokens.get(0).offset()), Is.is(Integer.valueOf(0)));
        /* assert all elements of node 1 */
        Assert.assertThat(tokens.get(1).signal(), Is.is(BEGIN_FIELD));
        Assert.assertThat(tokens.get(1).name(), Is.is("Tag40001"));
        Assert.assertThat(Integer.valueOf(tokens.get(1).id()), Is.is(Integer.valueOf(40001)));
        Assert.assertThat(Integer.valueOf(tokens.get(1).encodedLength()), Is.is(Integer.valueOf(4)));
        Assert.assertThat(Integer.valueOf(tokens.get(1).offset()), Is.is(Integer.valueOf(0)));
        /* assert all elements of node 2 */
        Assert.assertThat(tokens.get(2).signal(), Is.is(ENCODING));
        Assert.assertThat(tokens.get(2).name(), Is.is("uint32"));
        Assert.assertThat(tokens.get(2).encoding().primitiveType(), Is.is(UINT32));
        Assert.assertThat(Integer.valueOf(tokens.get(2).id()), Is.is(Integer.valueOf(INVALID_ID)));
        Assert.assertThat(Integer.valueOf(tokens.get(2).encodedLength()), Is.is(Integer.valueOf(4)));
        Assert.assertThat(Integer.valueOf(tokens.get(2).offset()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(tokens.get(2).encoding().byteOrder(), Is.is(ByteOrder.LITTLE_ENDIAN));
        /* assert all elements of node 3 */
        Assert.assertThat(tokens.get(3).signal(), Is.is(END_FIELD));
        Assert.assertThat(tokens.get(3).name(), Is.is("Tag40001"));
        Assert.assertThat(Integer.valueOf(tokens.get(3).id()), Is.is(Integer.valueOf(40001)));
        Assert.assertThat(Integer.valueOf(tokens.get(3).encodedLength()), Is.is(Integer.valueOf(4)));
        Assert.assertThat(Integer.valueOf(tokens.get(3).offset()), Is.is(Integer.valueOf(0)));
        /* assert all elements of node 4 */
        Assert.assertThat(tokens.get(4).signal(), Is.is(END_MESSAGE));
        Assert.assertThat(tokens.get(4).name(), Is.is("TestMessage50001"));
        Assert.assertThat(Integer.valueOf(tokens.get(4).id()), Is.is(Integer.valueOf(50001)));
        Assert.assertThat(Integer.valueOf(tokens.get(4).encodedLength()), Is.is(Integer.valueOf(16)));
        Assert.assertThat(Integer.valueOf(tokens.get(4).offset()), Is.is(Integer.valueOf(0)));
    }

    @Test
    public void shouldGenerateCorrectIrForMessageWithVariableLengthField() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("basic-variable-length-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final List<Token> tokens = ir.getMessage(1);
        Assert.assertThat(Integer.valueOf(tokens.size()), Is.is(Integer.valueOf(8)));
        Assert.assertThat(tokens.get(0).signal(), Is.is(BEGIN_MESSAGE));
        Assert.assertThat(tokens.get(0).name(), Is.is("TestMessage1"));
        Assert.assertThat(Integer.valueOf(tokens.get(0).id()), Is.is(Integer.valueOf(1)));
        Assert.assertThat(Integer.valueOf(tokens.get(0).encodedLength()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(Integer.valueOf(tokens.get(0).offset()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(tokens.get(1).signal(), Is.is(BEGIN_VAR_DATA));
        Assert.assertThat(tokens.get(1).name(), Is.is("encryptedNewPassword"));
        Assert.assertThat(Integer.valueOf(tokens.get(1).id()), Is.is(Integer.valueOf(1404)));
        Assert.assertThat(Integer.valueOf(tokens.get(1).encodedLength()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(Integer.valueOf(tokens.get(1).offset()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(tokens.get(2).signal(), Is.is(BEGIN_COMPOSITE));
        Assert.assertThat(tokens.get(2).name(), Is.is("varDataEncoding"));
        Assert.assertThat(Integer.valueOf(tokens.get(2).encodedLength()), Is.is(Integer.valueOf((-1))));
        Assert.assertThat(Integer.valueOf(tokens.get(2).offset()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(tokens.get(3).signal(), Is.is(ENCODING));
        Assert.assertThat(tokens.get(3).name(), Is.is("length"));
        Assert.assertThat(tokens.get(3).encoding().primitiveType(), Is.is(UINT8));
        Assert.assertThat(Integer.valueOf(tokens.get(3).id()), Is.is(Integer.valueOf(INVALID_ID)));
        Assert.assertThat(Integer.valueOf(tokens.get(3).encodedLength()), Is.is(Integer.valueOf(1)));
        Assert.assertThat(Integer.valueOf(tokens.get(3).offset()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(tokens.get(3).encoding().byteOrder(), Is.is(ByteOrder.LITTLE_ENDIAN));
        Assert.assertThat(tokens.get(4).signal(), Is.is(ENCODING));
        Assert.assertThat(tokens.get(4).name(), Is.is("varData"));
        Assert.assertThat(tokens.get(4).encoding().primitiveType(), Is.is(CHAR));
        Assert.assertThat(Integer.valueOf(tokens.get(4).id()), Is.is(Integer.valueOf(INVALID_ID)));
        Assert.assertThat(Integer.valueOf(tokens.get(4).encodedLength()), Is.is(Integer.valueOf((-1))));
        Assert.assertThat(Integer.valueOf(tokens.get(4).offset()), Is.is(Integer.valueOf(1)));
        Assert.assertThat(tokens.get(4).encoding().byteOrder(), Is.is(ByteOrder.LITTLE_ENDIAN));
        Assert.assertThat(tokens.get(5).signal(), Is.is(END_COMPOSITE));
        Assert.assertThat(tokens.get(5).name(), Is.is("varDataEncoding"));
        Assert.assertThat(Integer.valueOf(tokens.get(5).encodedLength()), Is.is(Integer.valueOf((-1))));
        Assert.assertThat(Integer.valueOf(tokens.get(5).offset()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(tokens.get(6).signal(), Is.is(END_VAR_DATA));
        Assert.assertThat(tokens.get(6).name(), Is.is("encryptedNewPassword"));
        Assert.assertThat(Integer.valueOf(tokens.get(6).id()), Is.is(Integer.valueOf(1404)));
        Assert.assertThat(Integer.valueOf(tokens.get(6).encodedLength()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(Integer.valueOf(tokens.get(6).offset()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(tokens.get(7).signal(), Is.is(END_MESSAGE));
        Assert.assertThat(tokens.get(7).name(), Is.is("TestMessage1"));
        Assert.assertThat(Integer.valueOf(tokens.get(7).id()), Is.is(Integer.valueOf(1)));
        Assert.assertThat(Integer.valueOf(tokens.get(7).encodedLength()), Is.is(Integer.valueOf(0)));
        Assert.assertThat(Integer.valueOf(tokens.get(7).offset()), Is.is(Integer.valueOf(0)));
    }

    @Test
    public void shouldGenerateCorrectIrForMessageWithRepeatingGroupWithEmbeddedDimensions() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("basic-group-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        /* 0=msg, 1=field, 2=enc, 3=fieldend, 4=group, 5=comp, 6=enc, 7=enc, 8=compend, ... */
        final int groupIdx = 4;
        final int dimensionsCompIdx = 5;
        final int dimensionsBlEncIdx = 6;
        final int dimensionsNigEncIdx = 7;
        final List<Token> tokens = ir.getMessage(1);
        /* assert on the group token */
        Assert.assertThat(tokens.get(groupIdx).signal(), Is.is(BEGIN_GROUP));
        Assert.assertThat(tokens.get(groupIdx).name(), Is.is("Entries"));
        Assert.assertThat(Integer.valueOf(tokens.get(groupIdx).id()), Is.is(Integer.valueOf(2)));
        /* assert on the comp token for dimensions */
        Assert.assertThat(tokens.get(dimensionsCompIdx).signal(), Is.is(BEGIN_COMPOSITE));
        Assert.assertThat(tokens.get(dimensionsCompIdx).name(), Is.is("groupSizeEncoding"));
        /* assert on the enc token for dimensions blockLength */
        Assert.assertThat(tokens.get(dimensionsBlEncIdx).signal(), Is.is(ENCODING));
        Assert.assertThat(tokens.get(dimensionsBlEncIdx).name(), Is.is("blockLength"));
        /* assert on the enc token for dimensions numInGroup */
        Assert.assertThat(tokens.get(dimensionsNigEncIdx).signal(), Is.is(ENCODING));
        Assert.assertThat(tokens.get(dimensionsNigEncIdx).name(), Is.is("numInGroup"));
    }

    @Test
    public void shouldGenerateCorrectIrForMessageWithRepeatingGroupWithEmbeddedDimensionsDefaultDimensionType() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("embedded-length-and-count-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        /* 0=msg, 1=field, 2=enc, 3=fieldend, 4=group, 5=comp, 6=enc, 7=enc, 8=compend, 9=field, ... */
        final int groupIdx = 4;
        final int dimensionsCompIdx = 5;
        final int fieldInGroupIdx = 9;
        final List<Token> tokens = ir.getMessage(1);
        Assert.assertThat(tokens.get(groupIdx).signal(), Is.is(BEGIN_GROUP));
        Assert.assertThat(tokens.get(groupIdx).name(), Is.is("ListOrdGrp"));
        Assert.assertThat(Integer.valueOf(tokens.get(groupIdx).id()), Is.is(Integer.valueOf(73)));
        Assert.assertThat(tokens.get(dimensionsCompIdx).signal(), Is.is(BEGIN_COMPOSITE));
        Assert.assertThat(tokens.get(dimensionsCompIdx).name(), Is.is("groupSizeEncoding"));
        Assert.assertThat(tokens.get(fieldInGroupIdx).signal(), Is.is(BEGIN_FIELD));
        Assert.assertThat(tokens.get(fieldInGroupIdx).name(), Is.is("ClOrdID"));
    }

    @Test
    public void shouldGenerateCorrectIrForMessageWithVariableLengthFieldWithEmbeddedLength() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("embedded-length-and-count-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        /* 0=msg, 1=field, 2=enc, 3=fieldend, 4=field, 5=comp, 6=enc, 7=enc, 8=compend, 9=fieldend */
        final int lengthFieldIdx = 4;
        final int lengthEncIdx = 6;
        final int dataEncIdx = 7;
        final List<Token> tokens = ir.getMessage(2);
        /* assert the varDataEncoding field node is formed correctly */
        Assert.assertThat(tokens.get(lengthFieldIdx).signal(), Is.is(BEGIN_VAR_DATA));
        Assert.assertThat(tokens.get(lengthFieldIdx).name(), Is.is("EncryptedPassword"));
        Assert.assertThat(Integer.valueOf(tokens.get(lengthFieldIdx).id()), Is.is(Integer.valueOf(1402)));
        /* assert the length node has correct values */
        Assert.assertThat(tokens.get(lengthEncIdx).signal(), Is.is(ENCODING));
        Assert.assertThat(tokens.get(lengthEncIdx).encoding().primitiveType(), Is.is(UINT8));
        /* assert the group node has the right IrId and xRefIrId, etc. */
        Assert.assertThat(tokens.get(dataEncIdx).signal(), Is.is(ENCODING));
        Assert.assertThat(tokens.get(dataEncIdx).encoding().primitiveType(), Is.is(CHAR));
    }
}

