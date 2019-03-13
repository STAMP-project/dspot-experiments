/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.common.options;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static OptionDocumentationCategory.UNCATEGORIZED;
import static OptionEffectTag.NO_OP;


/**
 * A test for {@link BoolOrEnumConverter}.
 */
@RunWith(JUnit4.class)
public class BoolOrEnumConverterTest {
    private enum CompilationMode {

        DBG,
        OPT;}

    private static class CompilationModeConverter extends BoolOrEnumConverter<BoolOrEnumConverterTest.CompilationMode> {
        public CompilationModeConverter() {
            super(BoolOrEnumConverterTest.CompilationMode.class, "compilation mode", BoolOrEnumConverterTest.CompilationMode.DBG, BoolOrEnumConverterTest.CompilationMode.OPT);
        }
    }

    /**
     * The test options for the CompilationMode hybrid converter.
     */
    public static class CompilationModeTestOptions extends OptionsBase {
        @Option(name = "compile_mode", converter = BoolOrEnumConverterTest.CompilationModeConverter.class, documentationCategory = UNCATEGORIZED, effectTags = { NO_OP }, defaultValue = "dbg")
        public BoolOrEnumConverterTest.CompilationMode compileMode;
    }

    @Test
    public void converterFromEnum() throws Exception {
        BoolOrEnumConverterTest.CompilationModeConverter converter = new BoolOrEnumConverterTest.CompilationModeConverter();
        assertThat(convert("dbg")).isEqualTo(BoolOrEnumConverterTest.CompilationMode.DBG);
        assertThat(convert("opt")).isEqualTo(BoolOrEnumConverterTest.CompilationMode.OPT);
        try {
            convert("none");
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().isEqualTo("Not a valid compilation mode: 'none' (should be dbg or opt)");
        }
        assertThat(getTypeDescription()).isEqualTo("dbg or opt");
    }

    @Test
    public void convertFromBooleanValues() throws Exception {
        String[] falseValues = new String[]{ "false", "0" };
        String[] trueValues = new String[]{ "true", "1" };
        BoolOrEnumConverterTest.CompilationModeConverter converter = new BoolOrEnumConverterTest.CompilationModeConverter();
        for (String falseValue : falseValues) {
            assertThat(convert(falseValue)).isEqualTo(BoolOrEnumConverterTest.CompilationMode.OPT);
        }
        for (String trueValue : trueValues) {
            assertThat(convert(trueValue)).isEqualTo(BoolOrEnumConverterTest.CompilationMode.DBG);
        }
    }

    @Test
    public void prefixedWithNo() throws OptionsParsingException {
        OptionsParser parser = OptionsParser.newOptionsParser(BoolOrEnumConverterTest.CompilationModeTestOptions.class);
        parser.parse("--nocompile_mode");
        BoolOrEnumConverterTest.CompilationModeTestOptions options = parser.getOptions(BoolOrEnumConverterTest.CompilationModeTestOptions.class);
        assertThat(options.compileMode).isNotNull();
        assertThat(options.compileMode).isEqualTo(BoolOrEnumConverterTest.CompilationMode.OPT);
    }

    @Test
    public void missingValueAsBoolConversion() throws OptionsParsingException {
        OptionsParser parser = OptionsParser.newOptionsParser(BoolOrEnumConverterTest.CompilationModeTestOptions.class);
        parser.parse("--compile_mode");
        BoolOrEnumConverterTest.CompilationModeTestOptions options = parser.getOptions(BoolOrEnumConverterTest.CompilationModeTestOptions.class);
        assertThat(options.compileMode).isNotNull();
        assertThat(options.compileMode).isEqualTo(BoolOrEnumConverterTest.CompilationMode.DBG);
    }
}

