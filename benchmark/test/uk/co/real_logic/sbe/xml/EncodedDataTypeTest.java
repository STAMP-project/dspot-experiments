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


import Presence.CONSTANT;
import Presence.OPTIONAL;
import Presence.REQUIRED;
import PrimitiveType.CHAR;
import PrimitiveType.DOUBLE;
import PrimitiveType.FLOAT;
import PrimitiveType.INT16;
import PrimitiveType.INT32;
import PrimitiveType.INT64;
import PrimitiveType.INT8;
import PrimitiveType.UINT16;
import PrimitiveType.UINT32;
import PrimitiveType.UINT64;
import PrimitiveType.UINT8;
import java.util.Map;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.PrimitiveValue;


public class EncodedDataTypeTest {
    @Test
    public void shouldHandleSettingAllAttributes() throws Exception {
        final String testXmlString = "<types>" + (("    <type name=\"testType\" presence=\"required\" primitiveType=\"char\" length=\"1\" " + "variableLength=\"false\"/>") + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        // assert that testType is in map and name of Type is correct
        final Type t = map.get("testType");
        Assert.assertThat(t.name(), Is.is("testType"));
        Assert.assertThat(t.presence(), Is.is(REQUIRED));
        final EncodedDataType d = ((EncodedDataType) (t));
        Assert.assertThat(d.primitiveType(), Is.is(CHAR));
        Assert.assertThat(d.length(), Is.is(1));
        Assert.assertThat(d.isVariableLength(), Is.is(false));
    }

    @Test
    public void shouldHandleMultipleTypes() throws Exception {
        final String testXmlString = "<types>" + (((("    <type name=\"testType1\" presence=\"required\" primitiveType=\"char\" length=\"1\" " + "variableLength=\"false\"/>") + "    <type name=\"testType2\" presence=\"required\" primitiveType=\"int8\" length=\"1\" ") + "variableLength=\"false\"/>") + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        // assert that testType is in map and name of Type is correct
        Assert.assertThat(map.size(), Is.is(2));
        Assert.assertThat(map.get("testType1").name(), Is.is("testType1"));
        Assert.assertThat(map.get("testType2").name(), Is.is("testType2"));
    }

    @Test
    public void shouldSetAppropriateDefaultsWhenNoneSpecified() throws Exception {
        final String testXmlString = "<types>" + ("    <type name=\"testType\" primitiveType=\"char\"/>" + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        // assert that testType is in map and name of Type is correct
        Assert.assertThat(map.get("testType").name(), Is.is("testType"));
        // assert defaults for length, variableLength and presence
        final Type t = map.get("testType");
        Assert.assertThat(t.presence(), Is.is(REQUIRED));
        final EncodedDataType d = ((EncodedDataType) (t));
        Assert.assertThat(d.length(), Is.is(1));
        Assert.assertThat(d.isVariableLength(), Is.is(false));
    }

    @Test
    public void shouldUseAppropriatePresence() throws Exception {
        final String testXmlString = "<types>" + (((("    <type name=\"testTypeDefault\" primitiveType=\"char\"/>" + "    <type name=\"testTypeRequired\" presence=\"required\" primitiveType=\"char\"/>") + "    <type name=\"testTypeOptional\" presence=\"optional\" primitiveType=\"char\"/>") + "    <type name=\"testTypeConstant\" presence=\"constant\" primitiveType=\"char\">A</type>") + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertThat(map.get("testTypeDefault").presence(), Is.is(REQUIRED));
        Assert.assertThat(map.get("testTypeRequired").presence(), Is.is(REQUIRED));
        Assert.assertThat(map.get("testTypeOptional").presence(), Is.is(OPTIONAL));
        Assert.assertThat(map.get("testTypeConstant").presence(), Is.is(CONSTANT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUnknownPresenceSpecified() throws Exception {
        final String testXmlString = "<types>" + ("    <type name=\"testTyeUnknown\" presence=\"XXXXX\" primitiveType=\"char\"/>" + "</types>");
        EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenNoPrimitiveTypeSpecified() throws Exception {
        final String testXmlString = "<types>" + ("    <type name=\"testType\"/>" + "</types>");
        EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenNoNameSpecified() throws Exception {
        final String testXmlString = "<types>" + ("    <type primitiveType=\"char\"/>" + "</types>");
        EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
    }

    @Test
    public void shouldUseAppropriatePrimitiveType() throws Exception {
        final String testXmlString = "<types>" + ((((((((((("    <type name=\"testTypeChar\" primitiveType=\"char\"/>" + "    <type name=\"testTypeInt8\" primitiveType=\"int8\"/>") + "    <type name=\"testTypeInt16\" primitiveType=\"int16\"/>") + "    <type name=\"testTypeInt32\" primitiveType=\"int32\"/>") + "    <type name=\"testTypeInt64\" primitiveType=\"int64\"/>") + "    <type name=\"testTypeUInt8\" primitiveType=\"uint8\"/>") + "    <type name=\"testTypeUInt16\" primitiveType=\"uint16\"/>") + "    <type name=\"testTypeUInt32\" primitiveType=\"uint32\"/>") + "    <type name=\"testTypeUInt64\" primitiveType=\"uint64\"/>") + "    <type name=\"testTypeFloat\" primitiveType=\"float\"/>") + "    <type name=\"testTypeDouble\" primitiveType=\"double\"/>") + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertThat(primitiveType(), Is.is(CHAR));
        Assert.assertThat(primitiveType(), Is.is(INT8));
        Assert.assertThat(primitiveType(), Is.is(INT16));
        Assert.assertThat(primitiveType(), Is.is(INT32));
        Assert.assertThat(primitiveType(), Is.is(INT64));
        Assert.assertThat(primitiveType(), Is.is(UINT8));
        Assert.assertThat(primitiveType(), Is.is(UINT16));
        Assert.assertThat(primitiveType(), Is.is(UINT32));
        Assert.assertThat(primitiveType(), Is.is(UINT64));
        Assert.assertThat(primitiveType(), Is.is(FLOAT));
        Assert.assertThat(primitiveType(), Is.is(DOUBLE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUnknownPrimitiveTypeSpecified() throws Exception {
        final String testXmlString = "<types>" + ("    <type name=\"testTypeUnknown\" primitiveType=\"XXXX\"/>" + "</types>");
        EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
    }

    @Test
    public void shouldReturnCorrectSizeForPrimitiveTypes() throws Exception {
        final String testXmlString = "<types>" + ((((((((((("    <type name=\"testTypeChar\" primitiveType=\"char\"/>" + "    <type name=\"testTypeInt8\" primitiveType=\"int8\"/>") + "    <type name=\"testTypeInt16\" primitiveType=\"int16\"/>") + "    <type name=\"testTypeInt32\" primitiveType=\"int32\"/>") + "    <type name=\"testTypeInt64\" primitiveType=\"int64\"/>") + "    <type name=\"testTypeUInt8\" primitiveType=\"uint8\"/>") + "    <type name=\"testTypeUInt16\" primitiveType=\"uint16\"/>") + "    <type name=\"testTypeUInt32\" primitiveType=\"uint32\"/>") + "    <type name=\"testTypeUInt64\" primitiveType=\"uint64\"/>") + "    <type name=\"testTypeFloat\" primitiveType=\"float\"/>") + "    <type name=\"testTypeDouble\" primitiveType=\"double\"/>") + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertThat(map.get("testTypeChar").encodedLength(), Is.is(1));
        Assert.assertThat(map.get("testTypeInt8").encodedLength(), Is.is(1));
        Assert.assertThat(map.get("testTypeInt32").encodedLength(), Is.is(4));
        Assert.assertThat(map.get("testTypeInt64").encodedLength(), Is.is(8));
        Assert.assertThat(map.get("testTypeUInt8").encodedLength(), Is.is(1));
        Assert.assertThat(map.get("testTypeUInt16").encodedLength(), Is.is(2));
        Assert.assertThat(map.get("testTypeUInt32").encodedLength(), Is.is(4));
        Assert.assertThat(map.get("testTypeUInt64").encodedLength(), Is.is(8));
        Assert.assertThat(map.get("testTypeFloat").encodedLength(), Is.is(4));
        Assert.assertThat(map.get("testTypeDouble").encodedLength(), Is.is(8));
    }

    @Test
    public void shouldReturnCorrectDescriptionForType() throws Exception {
        final String desc = "basic description attribute of a type element";
        final String testXmlString = ((("<types>" + "    <type name=\"testTypeDescription\" primitiveType=\"char\" description=\"") + desc) + "\"/>") + "</types>";
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertThat(map.get("testTypeDescription").description(), Is.is(desc));
    }

    @Test
    public void shouldReturnNullOnNoDescriptionSet() throws Exception {
        final String testXmlString = "<types>" + ("    <type name=\"testTypeNoDescription\" primitiveType=\"char\"/>" + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        final String description = map.get("testTypeNoDescription").description();
        Assert.assertNull(description);
    }

    @Test
    public void shouldReturnCorrectSemanticTypeForType() throws Exception {
        final String semanticType = "char";
        final String testXmlString = ((("<types>" + "    <type name=\"testType\" primitiveType=\"char\" semanticType=\"") + semanticType) + "\"/>") + "</types>";
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertThat(map.get("testType").semanticType(), Is.is(semanticType));
    }

    @Test
    public void shouldReturnNullWhenSemanticTypeNotSpecified() throws Exception {
        final String testXmlString = "<types>" + ("    <type name=\"testType\" primitiveType=\"char\"/>" + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertNull(map.get("testType").semanticType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenConstantPresenceButNoDataSpecified() throws Exception {
        final String testXmlString = "<types>" + ("    <type name=\"testTypePresenceConst\" primitiveType=\"char\" presence=\"constant\"></type>" + "</types>");
        EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
    }

    @Test
    public void shouldReturnCorrectPresenceConstantWhenSpecified() throws Exception {
        final String testXmlString = "<types>" + ("    <type name=\"testTypePresenceConst\" primitiveType=\"char\" presence=\"constant\">F</type>" + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        final String expectedString = "F";
        final PrimitiveValue expectedValue = PrimitiveValue.parse(expectedString, CHAR);
        Assert.assertThat(constVal(), Is.is(expectedValue));
    }

    @Test
    public void shouldReturnCorrectConstantStringWhenSpecified() throws Exception {
        final String strConst = "string constant";
        final String testXmlString = (((((("<types>" + ("    <type name=\"testTypeConstString\" primitiveType=\"char\" presence=\"constant\" " + "length=\"")) + (strConst.length())) + "\"") + ">") + strConst) + "</type>") + "</types>";
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertThat(constVal(), Is.is(PrimitiveValue.parse(strConst, strConst.length(), "US-ASCII")));
    }

    @Test
    public void shouldReturnDefaultMinValueWhenSpecified() throws Exception {
        final String testXmlString = "<types>" + ("    <type name=\"testTypeDefaultCharMinValue\" primitiveType=\"char\"/>" + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertNull(minValue());
    }

    @Test
    public void shouldReturnDefaultMaxValueWhenSpecified() throws Exception {
        final String testXmlString = "<types>" + ("    <type name=\"testTypeDefaultCharMaxValue\" primitiveType=\"char\"/>" + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertNull(maxValue());
    }

    @Test
    public void shouldReturnDefaultNullValueWhenSpecified() throws Exception {
        final String testXmlString = "<types>" + ("    <type name=\"testTypeDefaultCharNullValue\" primitiveType=\"char\"/>" + "</types>");
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertNull(nullValue());
    }

    @Test
    public void shouldReturnCorrectMinValueWhenSpecified() throws Exception {
        final String minVal = "10";
        final String testXmlString = ((("<types>" + "    <type name=\"testTypeInt8MinValue\" primitiveType=\"int8\" minValue=\"") + minVal) + "\"/>") + "</types>";
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertThat(minValue(), Is.is(PrimitiveValue.parse(minVal, INT8)));
    }

    @Test
    public void shouldReturnCorrectMaxValueWhenSpecified() throws Exception {
        final String maxVal = "10";
        final String testXmlString = ((("<types>" + "    <type name=\"testTypeInt8MaxValue\" primitiveType=\"int8\" maxValue=\"") + maxVal) + "\"/>") + "</types>";
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertThat(maxValue(), Is.is(PrimitiveValue.parse(maxVal, INT8)));
    }

    @Test
    public void shouldReturnCorrectNullValueWhenSpecified() throws Exception {
        final String nullVal = "10";
        final String testXmlString = ((("<types>" + "    <type name=\"testTypeInt8NullValue\" primitiveType=\"int8\" presence=\"optional\" nullValue=\"") + nullVal) + "\"/>") + "</types>";
        final Map<String, Type> map = EncodedDataTypeTest.parseTestXmlWithMap("/types/type", testXmlString);
        Assert.assertThat(nullValue(), Is.is(PrimitiveValue.parse(nullVal, INT8)));
    }
}

