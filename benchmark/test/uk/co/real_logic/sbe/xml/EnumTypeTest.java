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
package uk.co.real_logic.sbe.xml;


import EnumType.ValidValue;
import ParserOptions.DEFAULT;
import PrimitiveType.CHAR;
import PrimitiveType.UINT8;
import java.util.List;
import java.util.Map;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.PrimitiveValue;
import uk.co.real_logic.sbe.TestUtil;


public class EnumTypeTest {
    @Test
    public void shouldHandleBinaryEnumType() throws Exception {
        final String testXmlString = "<types>" + (((("<enum name=\"biOp\" encodingType=\"uint8\">" + "    <validValue name=\"off\" description=\"switch is off\">0</validValue>") + "    <validValue name=\"on\" description=\"switch is on\">1</validValue>") + "</enum>") + "</types>");
        final Map<String, Type> map = EnumTypeTest.parseTestXmlWithMap("/types/enum", testXmlString);
        final EnumType e = ((EnumType) (map.get("biOp")));
        Assert.assertThat(e.name(), Is.is("biOp"));
        Assert.assertThat(e.encodingType(), Is.is(UINT8));
        Assert.assertThat(e.validValues().size(), Is.is(2));
        Assert.assertThat(e.getValidValue("on").primitiveValue(), Is.is(PrimitiveValue.parse("1", UINT8)));
        Assert.assertThat(e.getValidValue("off").primitiveValue(), Is.is(PrimitiveValue.parse("0", UINT8)));
    }

    @Test
    public void shouldHandleBooleanEnumType() throws Exception {
        final String testXmlString = "<types>" + (((("<enum name=\"Boolean\" encodingType=\"uint8\" semanticType=\"Boolean\">" + "    <validValue name=\"False\">0</validValue>") + "    <validValue name=\"True\">1</validValue>") + "</enum>") + "</types>");
        final Map<String, Type> map = EnumTypeTest.parseTestXmlWithMap("/types/enum", testXmlString);
        final EnumType e = ((EnumType) (map.get("Boolean")));
        Assert.assertThat(e.name(), Is.is("Boolean"));
        Assert.assertThat(e.encodingType(), Is.is(UINT8));
        Assert.assertThat(e.validValues().size(), Is.is(2));
        Assert.assertThat(e.getValidValue("True").primitiveValue(), Is.is(PrimitiveValue.parse("1", UINT8)));
        Assert.assertThat(e.getValidValue("False").primitiveValue(), Is.is(PrimitiveValue.parse("0", UINT8)));
    }

    @Test
    public void shouldHandleOptionalBooleanEnumType() throws Exception {
        final String nullValueStr = "255";
        final String testXmlString = (((((("<types>" + ("<enum name=\"optionalBoolean\" encodingType=\"uint8\" presence=\"optional\"" + "      nullValue=\"")) + nullValueStr) + "\" semanticType=\"Boolean\">") + "    <validValue name=\"False\">0</validValue>") + "    <validValue name=\"True\">1</validValue>") + "</enum>") + "</types>";
        final Map<String, Type> map = EnumTypeTest.parseTestXmlWithMap("/types/enum", testXmlString);
        final EnumType e = ((EnumType) (map.get("optionalBoolean")));
        Assert.assertThat(e.name(), Is.is("optionalBoolean"));
        Assert.assertThat(e.encodingType(), Is.is(UINT8));
        Assert.assertThat(e.validValues().size(), Is.is(2));
        Assert.assertThat(e.getValidValue("True").primitiveValue(), Is.is(PrimitiveValue.parse("1", UINT8)));
        Assert.assertThat(e.getValidValue("False").primitiveValue(), Is.is(PrimitiveValue.parse("0", UINT8)));
        Assert.assertThat(e.nullValue(), Is.is(PrimitiveValue.parse(nullValueStr, UINT8)));
    }

    @Test
    public void shouldHandleEnumTypeList() throws Exception {
        final String testXmlString = "<types>" + ((((("<enum name=\"triOp\" encodingType=\"uint8\">" + "    <validValue name=\"off\" description=\"switch is off\">0</validValue>") + "    <validValue name=\"on\" description=\"switch is on\">1</validValue>") + "    <validValue name=\"notKnown\" description=\"switch is unknown\">2</validValue>") + "</enum>") + "</types>");
        final Map<String, Type> map = EnumTypeTest.parseTestXmlWithMap("/types/enum", testXmlString);
        final EnumType e = ((EnumType) (map.get("triOp")));
        Assert.assertThat(e.name(), Is.is("triOp"));
        Assert.assertThat(e.encodingType(), Is.is(UINT8));
        int foundOn = 0;
        int foundOff = 0;
        int foundNotKnown = 0;
        int count = 0;
        for (final EnumType.ValidValue v : e.validValues()) {
            switch (v.name()) {
                case "on" :
                    foundOn++;
                    break;
                case "off" :
                    foundOff++;
                    break;
                case "notKnown" :
                    foundNotKnown++;
                    break;
            }
            count++;
        }
        Assert.assertThat(count, Is.is(3));
        Assert.assertThat(foundOn, Is.is(1));
        Assert.assertThat(foundOff, Is.is(1));
        Assert.assertThat(foundNotKnown, Is.is(1));
    }

    @Test
    public void shouldHandleCharEnumEncodingType() throws Exception {
        final String testXmlString = "<types>" + (((((("<enum name=\"mixed\" encodingType=\"char\">" + "    <validValue name=\"Cee\">C</validValue>") + "    <validValue name=\"One\">1</validValue>") + "    <validValue name=\"Two\">2</validValue>") + "    <validValue name=\"Eee\">E</validValue>") + "</enum>") + "</types>");
        final Map<String, Type> map = EnumTypeTest.parseTestXmlWithMap("/types/enum", testXmlString);
        final EnumType e = ((EnumType) (map.get("mixed")));
        Assert.assertThat(e.encodingType(), Is.is(CHAR));
        Assert.assertThat(e.getValidValue("Cee").primitiveValue(), Is.is(PrimitiveValue.parse("C", CHAR)));
        Assert.assertThat(e.getValidValue("One").primitiveValue(), Is.is(PrimitiveValue.parse("1", CHAR)));
        Assert.assertThat(e.getValidValue("Two").primitiveValue(), Is.is(PrimitiveValue.parse("2", CHAR)));
        Assert.assertThat(e.getValidValue("Eee").primitiveValue(), Is.is(PrimitiveValue.parse("E", CHAR)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenIllegalEncodingTypeSpecified() throws Exception {
        final String testXmlString = "<types>" + (((("<enum name=\"boolean\" encodingType=\"int64\" semanticType=\"Boolean\">" + "    <validValue name=\"false\">0</validValue>") + "    <validValue name=\"true\">1</validValue>") + "</enum>") + "</types>");
        EnumTypeTest.parseTestXmlWithMap("/types/enum", testXmlString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDuplicateValueSpecified() throws Exception {
        final String testXmlString = "<types>" + ((((("<enum name=\"boolean\" encodingType=\"uint8\" semanticType=\"Boolean\">" + "    <validValue name=\"false\">0</validValue>") + "    <validValue name=\"anotherFalse\">0</validValue>") + "    <validValue name=\"true\">1</validValue>") + "</enum>") + "</types>");
        EnumTypeTest.parseTestXmlWithMap("/types/enum", testXmlString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDuplicateNameSpecified() throws Exception {
        final String testXmlString = "<types>" + ((((("<enum name=\"boolean\" encodingType=\"uint8\" semanticType=\"Boolean\">" + "    <validValue name=\"false\">0</validValue>") + "    <validValue name=\"false\">2</validValue>") + "    <validValue name=\"true\">1</validValue>") + "</enum>") + "</types>");
        EnumTypeTest.parseTestXmlWithMap("/types/enum", testXmlString);
    }

    @Test
    public void shouldHandleEncodingTypesWithNamedTypes() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("encoding-types-schema.xml"), DEFAULT);
        final List<Field> fields = schema.getMessage(1).fields();
        Assert.assertNotNull(fields);
        EnumType type = ((EnumType) (fields.get(1).type()));
        Assert.assertThat(type.encodingType(), Is.is(CHAR));
        type = ((EnumType) (fields.get(2).type()));
        Assert.assertThat(type.encodingType(), Is.is(UINT8));
    }
}

