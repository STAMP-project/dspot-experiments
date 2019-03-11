/**
 * Copyright 2013-2019 Real Logic Ltd.
 * Copyright 2017 MarketFactory Inc.
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


import java.util.HashMap;
import java.util.Map;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.real_logic.sbe.TestUtil;


public class ErrorHandlerTest {
    @Rule
    public final ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void shouldNotExitOnTypeErrorsAndWarnings() throws Exception {
        final String testXmlString = "<types>" + ((((((((((((((((((((((((((((("<enum name=\"NullBoolean\" encodingType=\"uint8\" nullValue=\"255\" semanticType=\"Boolean\">" + "    <validValue name=\"false\">0</validValue>") + "    <validValue name=\"true\">1</validValue>") + "</enum>") + "<enum name=\"DupNameBoolean\" encodingType=\"uint8\" semanticType=\"Boolean\">") + "    <validValue name=\"false\">0</validValue>") + "    <validValue name=\"anotherFalse\">0</validValue>") + "    <validValue name=\"true\">1</validValue>") + "</enum>") + "<enum name=\"DupValBoolean\" encodingType=\"uint8\" semanticType=\"Boolean\">") + "    <validValue name=\"false\">0</validValue>") + "    <validValue name=\"false\">2</validValue>") + "    <validValue name=\"true\">1</validValue>") + "</enum>") + "<set name=\"DupValueSet\" encodingType=\"uint8\">") + "    <choice name=\"Bit0\">0</choice>") + "    <choice name=\"AnotherBit0\">0</choice>") + "</set>") + "<set name=\"DupNameSet\" encodingType=\"uint8\">") + "    <choice name=\"Bit0\">0</choice>") + "    <choice name=\"Bit0\">1</choice>") + "</set>") + "<composite name=\"decimal\">") + "    <type name=\"mantissa\" primitiveType=\"int64\"/>") + "    <type name=\"mantissa\" primitiveType=\"int64\"/>") + "    <type name=\"exponent\" primitiveType=\"int8\"/>") + "</composite>") + "<type name=\"ConstButNoValue\" primitiveType=\"char\" presence=\"constant\"></type>") + "<type name=\"NullButNotOptional\" primitiveType=\"int8\" presence=\"required\" nullValue=\"10\"/>") + "</types>");
        final Map<String, Type> map = new HashMap<>();
        final ParserOptions options = ParserOptions.builder().suppressOutput(true).build();
        final ErrorHandler handler = new ErrorHandler(options);
        ErrorHandlerTest.parseTestXmlAddToMap(map, "/types/composite", testXmlString, handler);
        ErrorHandlerTest.parseTestXmlAddToMap(map, "/types/type", testXmlString, handler);
        ErrorHandlerTest.parseTestXmlAddToMap(map, "/types/enum", testXmlString, handler);
        ErrorHandlerTest.parseTestXmlAddToMap(map, "/types/set", testXmlString, handler);
        Assert.assertThat(Integer.valueOf(handler.errorCount()), Is.is(Integer.valueOf(3)));
        Assert.assertThat(Integer.valueOf(handler.warningCount()), Is.is(Integer.valueOf(33)));
    }

    @Test
    public void shouldExitAfterTypes() throws Exception {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("had 2 errors");
        final ParserOptions options = ParserOptions.builder().suppressOutput(true).build();
        XmlSchemaParser.parse(TestUtil.getLocalResource("error-handler-types-schema.xml"), options);
    }

    @Test
    public void shouldExitAfterTypesWhenDupTypesDefined() throws Exception {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("had 1 warnings");
        final ParserOptions options = ParserOptions.builder().suppressOutput(true).warningsFatal(true).build();
        XmlSchemaParser.parse(TestUtil.getLocalResource("error-handler-types-dup-schema.xml"), options);
    }

    @Test
    public void shouldExitAfterMessageWhenDupMessageIdsDefined() throws Exception {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("had 1 errors");
        final ParserOptions options = ParserOptions.builder().suppressOutput(true).warningsFatal(true).build();
        XmlSchemaParser.parse(TestUtil.getLocalResource("error-handler-dup-message-schema.xml"), options);
    }

    @Test
    public void shouldExitAfterMessage() throws Exception {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("had 13 errors");
        final ParserOptions options = ParserOptions.builder().suppressOutput(true).warningsFatal(true).build();
        XmlSchemaParser.parse(TestUtil.getLocalResource("error-handler-message-schema.xml"), options);
    }

    @Test
    public void shouldExitAfterMessageWhenGroupDimensionsNotComposite() throws Exception {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("had 1 errors");
        final ParserOptions options = ParserOptions.builder().suppressOutput(true).warningsFatal(true).build();
        XmlSchemaParser.parse(TestUtil.getLocalResource("error-handler-group-dimensions-schema.xml"), options);
    }

    @Test
    public void shouldExitAfterTypesWhenCompositeOffsetsIncorrect() throws Exception {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("had 2 errors");
        final ParserOptions options = ParserOptions.builder().suppressOutput(true).warningsFatal(true).build();
        XmlSchemaParser.parse(TestUtil.getLocalResource("error-handler-invalid-composite-offsets-schema.xml"), options);
    }

    @Test
    public void shouldExitInvalidFieldNames() throws Exception {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("had 16 warnings");
        final ParserOptions options = ParserOptions.builder().suppressOutput(true).warningsFatal(true).build();
        XmlSchemaParser.parse(TestUtil.getLocalResource("error-handler-invalid-name.xml"), options);
    }
}

