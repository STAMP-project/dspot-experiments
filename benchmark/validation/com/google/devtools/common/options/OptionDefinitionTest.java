/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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


import com.google.devtools.common.options.Converters.AssignmentConverter;
import com.google.devtools.common.options.Converters.IntegerConverter;
import com.google.devtools.common.options.Converters.StringConverter;
import com.google.devtools.common.options.OptionDefinition.NotAnOptionException;
import com.google.devtools.common.options.OptionsParser.ConstructionException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static OptionDocumentationCategory.UNCATEGORIZED;
import static OptionEffectTag.NO_OP;


/**
 * Tests for {@link OptionDefinition}.
 */
@RunWith(JUnit4.class)
public class OptionDefinitionTest {
    /**
     * Dummy options class, to test various expected failures of the OptionDefinition.
     */
    public static class BrokenOptions extends OptionsBase {
        public String notAnOption;

        @Option(name = "assignments", defaultValue = "foo is not an assignment", converter = AssignmentConverter.class, documentationCategory = UNCATEGORIZED, effectTags = NO_OP)
        public Map.Entry<String, String> assignments;
    }

    @Test
    public void optionConverterCannotParseDefaultValue() throws Exception {
        OptionDefinition optionDef = OptionDefinition.extractOptionDefinition(OptionDefinitionTest.BrokenOptions.class.getField("assignments"));
        try {
            optionDef.getDefaultValue();
            Assert.fail("Incorrect default should have caused getDefaultValue to fail.");
        } catch (ConstructionException e) {
            assertThat(e).hasMessageThat().contains(("OptionsParsingException while retrieving the default value for assignments: " + "Variable definitions must be in the form of a 'name=value' assignment"));
        }
    }

    @Test
    public void optionDefinitionRejectsNonOptions() throws Exception {
        try {
            OptionDefinition.extractOptionDefinition(OptionDefinitionTest.BrokenOptions.class.getField("notAnOption"));
            Assert.fail("notAnOption isn't an Option, and shouldn't be accepted as one.");
        } catch (NotAnOptionException e) {
            assertThat(e).hasMessageThat().contains(("The field notAnOption does not have the right annotation to be considered an " + "option."));
        }
    }

    /**
     * Dummy options class with valid options for testing the memoization of converters and default
     * values.
     */
    public static class ValidOptionUsingDefaultConverterForMocking extends OptionsBase {
        @Option(name = "foo", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "42")
        public int foo;

        @Option(name = "bar", converter = StringConverter.class, documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "strings")
        public String bar;
    }

    /**
     * Test that the converter and option default values are only computed once and then are obtained
     * from the stored values, in the case where a default converter is used.
     */
    @Test
    public void optionDefinitionMemoizesDefaultConverterValue() throws Exception {
        OptionDefinition optionDefinition = OptionDefinition.extractOptionDefinition(OptionDefinitionTest.ValidOptionUsingDefaultConverterForMocking.class.getField("foo"));
        OptionDefinition mockOptionDef = Mockito.spy(optionDefinition);
        // Do a bunch of potentially repeat operations on this option that need to know information
        // about the converter and default value. Also verify that the values are as expected.
        boolean isBoolean = mockOptionDef.usesBooleanValueSyntax();
        assertThat(isBoolean).isFalse();
        Converter<?> converter = mockOptionDef.getConverter();
        assertThat(converter).isInstanceOf(IntegerConverter.class);
        int value = ((int) (mockOptionDef.getDefaultValue()));
        assertThat(value).isEqualTo(42);
        // Expect reference equality, since we didn't recompute the value
        Converter<?> secondConverter = mockOptionDef.getConverter();
        assertThat(secondConverter).isSameAs(converter);
        mockOptionDef.getDefaultValue();
        // Verify that we didn't re-calculate the converter from the provided class object.
        Mockito.verify(mockOptionDef, Mockito.times(1)).getProvidedConverter();
        // The first call to getDefaultValue checks isSpecialNullDefault, which called
        // getUnparsedValueDefault as well, but expect no more calls to it after the initial call.
        Mockito.verify(mockOptionDef, Mockito.times(1)).isSpecialNullDefault();
        Mockito.verify(mockOptionDef, Mockito.times(2)).getUnparsedDefaultValue();
    }

    /**
     * Test that the converter and option default values are only computed once and then are obtained
     * from the stored values, in the case where a converter was provided.
     */
    @Test
    public void optionDefinitionMemoizesProvidedConverterValue() throws Exception {
        OptionDefinition optionDefinition = OptionDefinition.extractOptionDefinition(OptionDefinitionTest.ValidOptionUsingDefaultConverterForMocking.class.getField("bar"));
        OptionDefinition mockOptionDef = Mockito.spy(optionDefinition);
        // Do a bunch of potentially repeat operations on this option that need to know information
        // about the converter and default value. Also verify that the values are as expected.
        boolean isBoolean = mockOptionDef.usesBooleanValueSyntax();
        assertThat(isBoolean).isFalse();
        Converter<?> converter = mockOptionDef.getConverter();
        assertThat(converter).isInstanceOf(StringConverter.class);
        String value = ((String) (mockOptionDef.getDefaultValue()));
        assertThat(value).isEqualTo("strings");
        // Expect reference equality, since we didn't recompute the value
        Converter<?> secondConverter = mockOptionDef.getConverter();
        assertThat(secondConverter).isSameAs(converter);
        mockOptionDef.getDefaultValue();
        // Verify that we didn't re-calculate the converter from the provided class object.
        Mockito.verify(mockOptionDef, Mockito.times(1)).getProvidedConverter();
        // The first call to getDefaultValue checks isSpecialNullDefault, which called
        // getUnparsedValueDefault as well, but expect no more calls to it after the initial call.
        Mockito.verify(mockOptionDef, Mockito.times(1)).isSpecialNullDefault();
        Mockito.verify(mockOptionDef, Mockito.times(2)).getUnparsedDefaultValue();
    }
}

