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


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static OptionDocumentationCategory.UNCATEGORIZED;
import static OptionEffectTag.NO_OP;


/**
 * A test for {@link EnumConverter}.
 */
@RunWith(JUnit4.class)
public class EnumConverterTest {
    private enum CompilationMode {

        DBG,
        OPT;}

    private static class CompilationModeConverter extends EnumConverter<EnumConverterTest.CompilationMode> {
        public CompilationModeConverter() {
            super(EnumConverterTest.CompilationMode.class, "compilation mode");
        }
    }

    @Test
    public void converterForEnumWithTwoValues() throws Exception {
        EnumConverterTest.CompilationModeConverter converter = new EnumConverterTest.CompilationModeConverter();
        assertThat(convert("dbg")).isEqualTo(EnumConverterTest.CompilationMode.DBG);
        assertThat(convert("opt")).isEqualTo(EnumConverterTest.CompilationMode.OPT);
        try {
            convert("none");
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().isEqualTo("Not a valid compilation mode: 'none' (should be dbg or opt)");
        }
        assertThat(getTypeDescription()).isEqualTo("dbg or opt");
    }

    private enum Fruit {

        Apple,
        Banana,
        Cherry;}

    private static class FruitConverter extends EnumConverter<EnumConverterTest.Fruit> {
        public FruitConverter() {
            super(EnumConverterTest.Fruit.class, "fruit");
        }
    }

    @Test
    public void typeDescriptionForEnumWithThreeValues() throws Exception {
        EnumConverterTest.FruitConverter converter = new EnumConverterTest.FruitConverter();
        // We always use lowercase in the user-visible messages:
        assertThat(getTypeDescription()).isEqualTo("apple, banana or cherry");
    }

    @Test
    public void converterIsCaseInsensitive() throws Exception {
        EnumConverterTest.FruitConverter converter = new EnumConverterTest.FruitConverter();
        assertThat(convert("bAnANa")).isSameAs(EnumConverterTest.Fruit.Banana);
    }

    // Regression test: lists of enum using a subclass of EnumConverter don't work
    private static class AlphabetEnumConverter extends EnumConverter<EnumConverterTest.AlphabetEnum> {
        public AlphabetEnumConverter() {
            super(EnumConverterTest.AlphabetEnum.class, "alphabet enum");
        }
    }

    private static enum AlphabetEnum {

        ALPHA,
        BRAVO,
        CHARLY,
        DELTA,
        ECHO;}

    public static class EnumListTestOptions extends OptionsBase {
        @Option(name = "goo", allowMultiple = true, converter = EnumConverterTest.AlphabetEnumConverter.class, documentationCategory = UNCATEGORIZED, effectTags = { NO_OP }, defaultValue = "null")
        public List<EnumConverterTest.AlphabetEnum> goo;
    }

    @Test
    public void enumList() throws OptionsParsingException {
        OptionsParser parser = OptionsParser.newOptionsParser(EnumConverterTest.EnumListTestOptions.class);
        parser.parse("--goo=ALPHA", "--goo=BRAVO");
        EnumConverterTest.EnumListTestOptions options = parser.getOptions(EnumConverterTest.EnumListTestOptions.class);
        assertThat(options.goo).isNotNull();
        assertThat(options.goo).hasSize(2);
        assertThat(options.goo.get(0)).isEqualTo(EnumConverterTest.AlphabetEnum.ALPHA);
        assertThat(options.goo.get(1)).isEqualTo(EnumConverterTest.AlphabetEnum.BRAVO);
    }
}

