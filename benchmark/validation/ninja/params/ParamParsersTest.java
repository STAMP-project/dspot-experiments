/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.params;


import ParamParsers.BooleanParamParser;
import ParamParsers.DoubleParamParser;
import ParamParsers.EmptyStringParamParser;
import ParamParsers.FloatParamParser;
import ParamParsers.IntegerParamParser;
import ParamParsers.LongParamParser;
import ParamParsers.PrimitiveBooleanParamParser;
import ParamParsers.PrimitiveDoubleParamParser;
import ParamParsers.PrimitiveFloatParamParser;
import ParamParsers.PrimitiveIntegerParamParser;
import ParamParsers.PrimitiveLongParamParser;
import ParamParsers.PrimitiveShortParamParser;
import ParamParsers.ShortParamParser;
import ParamParsers.StringParamParser;
import ParamParsers.UUIDParamParser;
import java.util.UUID;
import ninja.validation.Validation;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ParamParsersTest {
    @Mock
    Validation validation;

    @Test
    public void testBooleanParamParser() {
        ParamParsers.BooleanParamParser booleanParamParser = new ParamParsers.BooleanParamParser();
        Assert.assertThat(booleanParamParser.getParsedType(), Matchers.is(Boolean.class));
        Assert.assertThat(booleanParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        Assert.assertThat(booleanParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());
        Assert.assertThat(booleanParamParser.parseParameter("param1", "123123", validation), Matchers.nullValue());
        Assert.assertThat(booleanParamParser.parseParameter("param1", "-", validation), Matchers.nullValue());
        Assert.assertThat(booleanParamParser.parseParameter("param1", "+", validation), Matchers.nullValue());
        Assert.assertThat(booleanParamParser.parseParameter("param1", "true", validation), Matchers.is(Boolean.TRUE));
        Assert.assertThat(booleanParamParser.parseParameter("param1", "TRUE", validation), Matchers.is(Boolean.TRUE));
        Assert.assertThat(booleanParamParser.parseParameter("param1", "false", validation), Matchers.is(Boolean.FALSE));
        Assert.assertThat(booleanParamParser.parseParameter("param1", "FALSE", validation), Matchers.is(Boolean.FALSE));
    }

    @Test
    public void testPrimitiveBooleanParamParser() {
        ParamParsers.PrimitiveBooleanParamParser booleanParamParser = new ParamParsers.PrimitiveBooleanParamParser();
        Assert.assertThat(booleanParamParser.getParsedType(), Matchers.is(Boolean.class));
        // No null for primitives
        Assert.assertThat(booleanParamParser.parseParameter("param1", null, validation), Matchers.is(Boolean.FALSE));
        Assert.assertThat(booleanParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(Boolean.FALSE));
        Assert.assertThat(booleanParamParser.parseParameter("param1", "123123", validation), Matchers.is(Boolean.FALSE));
        Assert.assertThat(booleanParamParser.parseParameter("param1", "-", validation), Matchers.is(Boolean.FALSE));
        Assert.assertThat(booleanParamParser.parseParameter("param1", "+", validation), Matchers.is(Boolean.FALSE));
        Assert.assertThat(booleanParamParser.parseParameter("param1", "true", validation), Matchers.is(Boolean.TRUE));
        Assert.assertThat(booleanParamParser.parseParameter("param1", "TRUE", validation), Matchers.is(Boolean.TRUE));
        Assert.assertThat(booleanParamParser.parseParameter("param1", "false", validation), Matchers.is(Boolean.FALSE));
        Assert.assertThat(booleanParamParser.parseParameter("param1", "FALSE", validation), Matchers.is(Boolean.FALSE));
    }

    @Test
    public void testStringParamParser() {
        ParamParsers.StringParamParser stringParamParser = new ParamParsers.StringParamParser();
        Assert.assertThat(stringParamParser.getParsedType(), Matchers.is(String.class));
        Assert.assertThat(stringParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        Assert.assertThat(stringParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        Assert.assertThat(stringParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(new String("asdfasdf")));
    }

    @Test
    public void testEmptyStringParamParser() {
        ParamParsers.EmptyStringParamParser stringParamParser = new ParamParsers.EmptyStringParamParser();
        Assert.assertThat(stringParamParser.getParsedType(), Matchers.is(String.class));
        Assert.assertThat(stringParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        Assert.assertThat(stringParamParser.parseParameter("param1", "", validation), Matchers.emptyString());
        Assert.assertThat(stringParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(new String("asdfasdf")));
    }

    @Test
    public void testIntegerParamParser() {
        ParamParsers.IntegerParamParser integerParamParser = new ParamParsers.IntegerParamParser();
        Assert.assertThat(integerParamParser.getParsedType(), Matchers.is(Integer.class));
        Assert.assertThat(integerParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        Assert.assertThat(integerParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        Assert.assertThat(integerParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());
        Assert.assertThat(integerParamParser.parseParameter("param1", "0", validation), Matchers.is(new Integer(0)));
        Assert.assertThat(integerParamParser.parseParameter("param1", "000", validation), Matchers.is(new Integer(0)));
        Assert.assertThat(integerParamParser.parseParameter("param1", "123", validation), Matchers.is(new Integer(123)));
        Assert.assertThat(integerParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Integer((-123))));
    }

    @Test
    public void testPrimitiveIntegerParamParser() {
        ParamParsers.PrimitiveIntegerParamParser integerParamParser = new ParamParsers.PrimitiveIntegerParamParser();
        Assert.assertThat(integerParamParser.getParsedType(), Matchers.is(Integer.class));
        // No null form primitives
        Assert.assertThat(integerParamParser.parseParameter("param1", null, validation), Matchers.is(((int) (0))));
        Assert.assertThat(integerParamParser.parseParameter("param1", "", validation), Matchers.is(((int) (0))));
        Assert.assertThat(integerParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(((int) (0))));
        Assert.assertThat(integerParamParser.parseParameter("param1", "0", validation), Matchers.is(((int) (0))));
        Assert.assertThat(integerParamParser.parseParameter("param1", "000", validation), Matchers.is(((int) (0))));
        Assert.assertThat(integerParamParser.parseParameter("param1", "123", validation), Matchers.is(((int) (123))));
        Assert.assertThat(integerParamParser.parseParameter("param1", "-123", validation), Matchers.is(((int) (-123))));
    }

    @Test
    public void testShortParamParser() {
        ParamParsers.ShortParamParser shortParamParser = new ParamParsers.ShortParamParser();
        Assert.assertThat(shortParamParser.getParsedType(), Matchers.is(Short.class));
        Assert.assertThat(shortParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        Assert.assertThat(shortParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        Assert.assertThat(shortParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());
        Assert.assertThat(shortParamParser.parseParameter("param1", "0", validation), Matchers.is(new Short(((short) (0)))));
        Assert.assertThat(shortParamParser.parseParameter("param1", "000", validation), Matchers.is(new Short(((short) (0)))));
        Assert.assertThat(shortParamParser.parseParameter("param1", "123", validation), Matchers.is(new Short(((short) (123)))));
        Assert.assertThat(shortParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Short(((short) (-123)))));
    }

    @Test
    public void testPrimitiveShortParamParser() {
        ParamParsers.PrimitiveShortParamParser shortParamParser = new ParamParsers.PrimitiveShortParamParser();
        Assert.assertThat(shortParamParser.getParsedType(), Matchers.is(Short.class));
        // No null form primitives
        Assert.assertThat(shortParamParser.parseParameter("param1", null, validation), Matchers.is(((short) (0))));
        Assert.assertThat(shortParamParser.parseParameter("param1", "", validation), Matchers.is(((short) (0))));
        Assert.assertThat(shortParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(((short) (0))));
        Assert.assertThat(shortParamParser.parseParameter("param1", "0", validation), Matchers.is(((short) (0))));
        Assert.assertThat(shortParamParser.parseParameter("param1", "000", validation), Matchers.is(((short) (0))));
        Assert.assertThat(shortParamParser.parseParameter("param1", "123", validation), Matchers.is(((short) (123))));
        Assert.assertThat(shortParamParser.parseParameter("param1", "-123", validation), Matchers.is(((short) (-123))));
    }

    @Test
    public void testLongParamParser() {
        ParamParsers.LongParamParser longParamParser = new ParamParsers.LongParamParser();
        Assert.assertThat(longParamParser.getParsedType(), Matchers.is(Long.class));
        Assert.assertThat(longParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        Assert.assertThat(longParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        Assert.assertThat(longParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());
        Assert.assertThat(longParamParser.parseParameter("param1", "0", validation), Matchers.is(new Long(0)));
        Assert.assertThat(longParamParser.parseParameter("param1", "000", validation), Matchers.is(new Long(0)));
        Assert.assertThat(longParamParser.parseParameter("param1", "123", validation), Matchers.is(new Long(123)));
        Assert.assertThat(longParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Long((-123))));
    }

    @Test
    public void testPrimitiveLongParamParser() {
        ParamParsers.PrimitiveLongParamParser longParamParser = new ParamParsers.PrimitiveLongParamParser();
        Assert.assertThat(longParamParser.getParsedType(), Matchers.is(Long.class));
        // No null form primitives
        Assert.assertThat(longParamParser.parseParameter("param1", null, validation), Matchers.is(new Long(0)));
        Assert.assertThat(longParamParser.parseParameter("param1", "", validation), Matchers.is(new Long(0)));
        Assert.assertThat(longParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(new Long(0)));
        Assert.assertThat(longParamParser.parseParameter("param1", "0", validation), Matchers.is(new Long(0)));
        Assert.assertThat(longParamParser.parseParameter("param1", "000", validation), Matchers.is(new Long(0)));
        Assert.assertThat(longParamParser.parseParameter("param1", "123", validation), Matchers.is(new Long(123)));
        Assert.assertThat(longParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Long((-123))));
    }

    @Test
    public void testFloatParamParser() {
        ParamParsers.FloatParamParser floatParamParser = new ParamParsers.FloatParamParser();
        Assert.assertThat(floatParamParser.getParsedType(), Matchers.is(Float.class));
        Assert.assertThat(floatParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        Assert.assertThat(floatParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        Assert.assertThat(floatParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());
        Assert.assertThat(floatParamParser.parseParameter("param1", "0", validation), Matchers.is(new Float(0)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "000", validation), Matchers.is(new Float(0)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "123", validation), Matchers.is(new Float(123)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Float((-123))));
        Assert.assertThat(floatParamParser.parseParameter("param1", "0.1", validation), Matchers.is(new Float(0.1)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "123.1", validation), Matchers.is(new Float(123.1)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "-123.1", validation), Matchers.is(new Float((-123.1))));
    }

    @Test
    public void testPrimitiveFloatParamParser() {
        ParamParsers.PrimitiveFloatParamParser floatParamParser = new ParamParsers.PrimitiveFloatParamParser();
        Assert.assertThat(floatParamParser.getParsedType(), Matchers.is(Float.class));
        // No null form primitives
        Assert.assertThat(floatParamParser.parseParameter("param1", null, validation), Matchers.is(new Float(0)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "", validation), Matchers.is(new Float(0)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(new Float(0)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "0", validation), Matchers.is(new Float(0)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "000", validation), Matchers.is(new Float(0)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "123", validation), Matchers.is(new Float(123)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Float((-123))));
        Assert.assertThat(floatParamParser.parseParameter("param1", "0.1", validation), Matchers.is(new Float(0.1)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "123.1", validation), Matchers.is(new Float(123.1)));
        Assert.assertThat(floatParamParser.parseParameter("param1", "-123.1", validation), Matchers.is(new Float((-123.1))));
    }

    @Test
    public void testDoubleParamParser() {
        ParamParsers.DoubleParamParser doubleParamParser = new ParamParsers.DoubleParamParser();
        Assert.assertThat(doubleParamParser.getParsedType(), Matchers.is(Double.class));
        Assert.assertThat(doubleParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        Assert.assertThat(doubleParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        Assert.assertThat(doubleParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());
        Assert.assertThat(doubleParamParser.parseParameter("param1", "0", validation), Matchers.is(new Double(0)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "000", validation), Matchers.is(new Double(0)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "123", validation), Matchers.is(new Double(123)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Double((-123))));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "0.1", validation), Matchers.is(new Double(0.1)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "123.1", validation), Matchers.is(new Double(123.1)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "-123.1", validation), Matchers.is(new Double((-123.1))));
    }

    @Test
    public void testPrimitiveDoubleParamParser() {
        ParamParsers.PrimitiveDoubleParamParser doubleParamParser = new ParamParsers.PrimitiveDoubleParamParser();
        Assert.assertThat(doubleParamParser.getParsedType(), Matchers.is(Double.class));
        // No null form primitives
        Assert.assertThat(doubleParamParser.parseParameter("param1", null, validation), Matchers.is(new Double(0)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "", validation), Matchers.is(new Double(0)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(new Double(0)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "0", validation), Matchers.is(new Double(0)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "000", validation), Matchers.is(new Double(0)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "123", validation), Matchers.is(new Double(123)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Double((-123))));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "0.1", validation), Matchers.is(new Double(0.1)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "123.1", validation), Matchers.is(new Double(123.1)));
        Assert.assertThat(doubleParamParser.parseParameter("param1", "-123.1", validation), Matchers.is(new Double((-123.1))));
    }

    @Test
    public void testUUIDParamParser() {
        ParamParsers.UUIDParamParser uuidParamParser = new ParamParsers.UUIDParamParser();
        Assert.assertThat(uuidParamParser.getParsedType(), Matchers.is(UUID.class));
        Assert.assertThat(uuidParamParser.parseParameter("param1", null, validation), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(uuidParamParser.parseParameter("param1", "", validation), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(uuidParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(uuidParamParser.parseParameter("param1", "fe45481f-ed31-40e4-9bca-9cec383302c2", validation), Matchers.is(UUID.fromString("fe45481f-ed31-40e4-9bca-9cec383302c2")));
    }
}

