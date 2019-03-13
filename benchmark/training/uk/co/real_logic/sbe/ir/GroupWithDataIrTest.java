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
import PrimitiveType.UINT8;
import Signal.BEGIN_COMPOSITE;
import Signal.BEGIN_GROUP;
import Signal.BEGIN_VAR_DATA;
import Signal.ENCODING;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.TestUtil;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.XmlSchemaParser;


public class GroupWithDataIrTest {
    @Test
    public void shouldGenerateCorrectIrForSingleVarDataInRepeatingGroup() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("group-with-data-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final List<Token> tokens = ir.getMessage(1);
        /* 0=msg, 1=field, 2=enc, 3=fieldend, 4=group, 5=comp, 6=enc, 7=enc, 8=compend, ... */
        final Token groupToken = tokens.get(4);
        final Token dimensionsCompToken = tokens.get(5);
        final Token dimensionsBlEncToken = tokens.get(6);
        final Token varDataFieldToken = tokens.get(15);
        final Token lengthEncToken = tokens.get(17);
        final Token dataEncToken = tokens.get(18);
        /* assert on the group token */
        Assert.assertThat(groupToken.signal(), Is.is(BEGIN_GROUP));
        Assert.assertThat(groupToken.name(), Is.is("Entries"));
        Assert.assertThat(Integer.valueOf(groupToken.id()), Is.is(Integer.valueOf(2)));
        /* assert on the comp token for dimensions */
        Assert.assertThat(dimensionsCompToken.signal(), Is.is(BEGIN_COMPOSITE));
        Assert.assertThat(dimensionsCompToken.name(), Is.is("groupSizeEncoding"));
        /* assert on the enc token for dimensions blockLength */
        Assert.assertThat(dimensionsBlEncToken.signal(), Is.is(ENCODING));
        Assert.assertThat(dimensionsBlEncToken.name(), Is.is("blockLength"));
        Assert.assertThat(varDataFieldToken.signal(), Is.is(BEGIN_VAR_DATA));
        Assert.assertThat(varDataFieldToken.name(), Is.is("varDataField"));
        Assert.assertThat(Integer.valueOf(varDataFieldToken.id()), Is.is(Integer.valueOf(5)));
        Assert.assertThat(lengthEncToken.signal(), Is.is(ENCODING));
        Assert.assertThat(lengthEncToken.encoding().primitiveType(), Is.is(UINT8));
        Assert.assertThat(dataEncToken.signal(), Is.is(ENCODING));
        Assert.assertThat(dataEncToken.encoding().primitiveType(), Is.is(CHAR));
    }

    @Test
    public void shouldGenerateCorrectIrForMultipleVarDataInRepeatingGroup() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("group-with-data-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final List<Token> tokens = ir.getMessage(2);
        /* 0=msg, 1=field, 2=enc, 3=fieldend, 4=group, 5=comp, 6=enc, 7=enc, 8=compend, ... */
        final Token groupToken = tokens.get(4);
        final Token varDataField1Token = tokens.get(15);
        final Token varDataField2Token = tokens.get(21);
        /* assert on the group token */
        Assert.assertThat(groupToken.signal(), Is.is(BEGIN_GROUP));
        Assert.assertThat(groupToken.name(), Is.is("Entries"));
        Assert.assertThat(Integer.valueOf(groupToken.id()), Is.is(Integer.valueOf(2)));
        Assert.assertThat(varDataField1Token.signal(), Is.is(BEGIN_VAR_DATA));
        Assert.assertThat(varDataField1Token.name(), Is.is("varDataField1"));
        Assert.assertThat(Integer.valueOf(varDataField1Token.id()), Is.is(Integer.valueOf(5)));
        Assert.assertThat(varDataField2Token.signal(), Is.is(BEGIN_VAR_DATA));
        Assert.assertThat(varDataField2Token.name(), Is.is("varDataField2"));
        Assert.assertThat(Integer.valueOf(varDataField2Token.id()), Is.is(Integer.valueOf(6)));
    }

    @Test
    public void shouldGenerateCorrectIrForVarDataInNestedRepeatingGroup() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("group-with-data-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final List<Token> tokens = ir.getMessage(3);
        /* 0=msg, 1=field, 2=enc, 3=fieldend, 4=group, 5=comp, 6=enc, 7=enc, 8=compend, ... */
        final Token groupToken = tokens.get(4);
        final Token nestedGroupToken = tokens.get(12);
        final Token varDataFieldNestedToken = tokens.get(20);
        final Token varDataFieldToken = tokens.get(27);
        Assert.assertThat(groupToken.signal(), Is.is(BEGIN_GROUP));
        Assert.assertThat(groupToken.name(), Is.is("Entries"));
        Assert.assertThat(Integer.valueOf(groupToken.id()), Is.is(Integer.valueOf(2)));
        Assert.assertThat(nestedGroupToken.signal(), Is.is(BEGIN_GROUP));
        Assert.assertThat(nestedGroupToken.name(), Is.is("NestedEntries"));
        Assert.assertThat(Integer.valueOf(nestedGroupToken.id()), Is.is(Integer.valueOf(4)));
        Assert.assertThat(varDataFieldNestedToken.signal(), Is.is(BEGIN_VAR_DATA));
        Assert.assertThat(varDataFieldNestedToken.name(), Is.is("varDataFieldNested"));
        Assert.assertThat(Integer.valueOf(varDataFieldNestedToken.id()), Is.is(Integer.valueOf(6)));
        Assert.assertThat(varDataFieldToken.signal(), Is.is(BEGIN_VAR_DATA));
        Assert.assertThat(varDataFieldToken.name(), Is.is("varDataField"));
        Assert.assertThat(Integer.valueOf(varDataFieldToken.id()), Is.is(Integer.valueOf(7)));
    }

    @Test
    public void shouldGenerateCorrectIrForOnlyMultipleVarDataInRepeatingGroup() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("group-with-data-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final List<Token> tokens = ir.getMessage(4);
        /* 0=msg, 1=field, 2=enc, 3=fieldend, 4=group, 5=comp, 6=enc, 7=enc, 8=compend, ... */
        final Token groupToken = tokens.get(4);
        final Token varDataField1Token = tokens.get(9);
        final Token varDataField2Token = tokens.get(15);
        /* assert on the group token */
        Assert.assertThat(groupToken.signal(), Is.is(BEGIN_GROUP));
        Assert.assertThat(groupToken.name(), Is.is("Entries"));
        Assert.assertThat(Integer.valueOf(groupToken.id()), Is.is(Integer.valueOf(2)));
        Assert.assertThat(varDataField1Token.signal(), Is.is(BEGIN_VAR_DATA));
        Assert.assertThat(varDataField1Token.name(), Is.is("varDataField1"));
        Assert.assertThat(Integer.valueOf(varDataField1Token.id()), Is.is(Integer.valueOf(5)));
        Assert.assertThat(varDataField2Token.signal(), Is.is(BEGIN_VAR_DATA));
        Assert.assertThat(varDataField2Token.name(), Is.is("varDataField2"));
        Assert.assertThat(Integer.valueOf(varDataField2Token.id()), Is.is(Integer.valueOf(6)));
    }
}

