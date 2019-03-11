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
import PrimitiveType.INT32;
import PrimitiveType.INT64;
import PrimitiveType.INT8;
import java.util.Map;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.PrimitiveValue;


public class CompositeTypeTest {
    @Test
    public void shouldHandleDecimalCompositeType() throws Exception {
        final String testXmlString = "<types>" + (((("<composite name=\"decimal\">" + "    <type name=\"mantissa\" primitiveType=\"int64\"/>") + "    <type name=\"exponent\" primitiveType=\"int8\"/>") + "</composite>") + "</types>");
        final Map<String, Type> map = CompositeTypeTest.parseTestXmlWithMap("/types/composite", testXmlString);
        final CompositeType decimal = ((CompositeType) (map.get("decimal")));
        Assert.assertThat(decimal.name(), Is.is("decimal"));
        final EncodedDataType mantissa = ((EncodedDataType) (decimal.getType("mantissa")));
        final EncodedDataType exponent = ((EncodedDataType) (decimal.getType("exponent")));
        Assert.assertThat(mantissa.primitiveType(), Is.is(INT64));
        Assert.assertThat(exponent.primitiveType(), Is.is(INT8));
        Assert.assertThat(decimal.encodedLength(), Is.is(9));
    }

    @Test
    public void shouldHandleDecimal32CompositeType() throws Exception {
        final String testXmlString = "<types>" + (((("<composite name=\"decimal32\">" + "    <type name=\"mantissa\" primitiveType=\"int32\"/>") + "    <type name=\"exponent\" primitiveType=\"int8\" presence=\"constant\">-2</type>") + "</composite>") + "</types>");
        final Map<String, Type> map = CompositeTypeTest.parseTestXmlWithMap("/types/composite", testXmlString);
        final CompositeType decimal32 = ((CompositeType) (map.get("decimal32")));
        Assert.assertThat(decimal32.name(), Is.is("decimal32"));
        final EncodedDataType mantissa = ((EncodedDataType) (decimal32.getType("mantissa")));
        final EncodedDataType exponent = ((EncodedDataType) (decimal32.getType("exponent")));
        Assert.assertThat(mantissa.primitiveType(), Is.is(INT32));
        Assert.assertThat(exponent.primitiveType(), Is.is(INT8));
        Assert.assertThat(exponent.presence(), Is.is(CONSTANT));
        Assert.assertThat(exponent.constVal(), Is.is(PrimitiveValue.parse("-2", INT8)));
        Assert.assertThat(decimal32.encodedLength(), Is.is(4));
    }

    @Test
    public void shouldHandleDecimal64CompositeType() throws Exception {
        final String testXmlString = "<types>" + (((("<composite name=\"decimal64\">" + "    <type name=\"mantissa\" primitiveType=\"int64\"/>") + "    <type name=\"exponent\" primitiveType=\"int8\" presence=\"constant\">-2</type>") + "</composite>") + "</types>");
        final Map<String, Type> map = CompositeTypeTest.parseTestXmlWithMap("/types/composite", testXmlString);
        final CompositeType decimal64 = ((CompositeType) (map.get("decimal64")));
        Assert.assertThat(decimal64.name(), Is.is("decimal64"));
        final EncodedDataType mantissa = ((EncodedDataType) (decimal64.getType("mantissa")));
        final EncodedDataType exponent = ((EncodedDataType) (decimal64.getType("exponent")));
        Assert.assertThat(mantissa.primitiveType(), Is.is(INT64));
        Assert.assertThat(exponent.primitiveType(), Is.is(INT8));
        Assert.assertThat(exponent.presence(), Is.is(CONSTANT));
        Assert.assertThat(exponent.constVal(), Is.is(PrimitiveValue.parse("-2", INT8)));
        Assert.assertThat(decimal64.encodedLength(), Is.is(8));
    }

    @Test
    public void shouldHandleCompositeTypeList() throws Exception {
        final String testXmlString = "<types>" + (((("<composite name=\"decimal\">" + "    <type name=\"mantissa\" primitiveType=\"int64\"/>") + "    <type name=\"exponent\" primitiveType=\"int8\"/>") + "</composite>") + "</types>");
        final Map<String, Type> map = CompositeTypeTest.parseTestXmlWithMap("/types/composite", testXmlString);
        final CompositeType c = ((CompositeType) (map.get("decimal")));
        Assert.assertThat(c.getTypeList().size(), Is.is(2));
        Assert.assertThat(c.getTypeList().get(0).name(), Is.is("mantissa"));
        Assert.assertThat(c.getTypeList().get(1).name(), Is.is("exponent"));
    }

    @Test
    public void shouldHandleCompositeHasNullableType() throws Exception {
        final String nullValStr = "9223372036854775807";
        final String testXmlString = (((((("<types>" + ("<composite name=\"PRICENULL\" description=\"Price NULL\" semanticType=\"Price\">" + "    <type name=\"mantissa\" description=\"mantissa\" presence=\"optional\" nullValue=\"")) + nullValStr) + "\" primitiveType=\"int64\"/>") + "    <type name=\"exponent\" description=\"exponent\" presence=\"constant\" primitiveType=\"int8\">") + "-7</type>") + "</composite>") + "</types>";
        final Map<String, Type> map = CompositeTypeTest.parseTestXmlWithMap("/types/composite", testXmlString);
        final CompositeType c = ((CompositeType) (map.get("PRICENULL")));
        final EncodedDataType mantissa = ((EncodedDataType) (c.getType("mantissa")));
        Assert.assertThat(mantissa.nullValue(), Is.is(PrimitiveValue.parse(nullValStr, INT64)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCompositeTypeHasTypeNameDuplicates() throws Exception {
        final String testXmlString = "<types>" + ((((("<composite name=\"decimal\">" + "    <type name=\"mantissa\" primitiveType=\"int64\"/>") + "    <type name=\"mantissa\" primitiveType=\"int64\"/>") + "    <type name=\"exponent\" primitiveType=\"int8\"/>") + "</composite>") + "</types>");
        CompositeTypeTest.parseTestXmlWithMap("/types/composite", testXmlString);
    }
}

