/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.presentation;


import org.assertj.core.api.Assertions;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link StandardRepresentation#formatArray(Object)}</code>.
 */
public class StandardRepresentation_array_format_Test extends AbstractBaseRepresentationTest {
    private static final StandardRepresentation STANDARD_REPRESENTATION = new StandardRepresentation();

    @Test
    public void should_return_null_if_array_is_null() {
        final Object array = null;
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isNull();
    }

    @Test
    public void should_return_empty_brackets_if_array_is_empty() {
        final Object[] array = new Object[0];
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[]");
    }

    @Test
    public void should_format_boolean_array() {
        Object array = new boolean[]{ true, false };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[true, false]");
    }

    @Test
    public void should_format_byte_array() {
        Object array = new byte[]{ ((byte) (3)), ((byte) (8)) };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[3, 8]");
    }

    @Test
    public void should_format_byte_array_in_hex_representation() {
        Object array = new byte[]{ ((byte) (3)), ((byte) (8)) };
        Assertions.assertThat(new HexadecimalRepresentation().formatArray(array)).isEqualTo("[0x03, 0x08]");
    }

    @Test
    public void should_format_char_array() {
        Object array = new char[]{ 'a', 'b' };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("['a', 'b']");
    }

    @Test
    public void should_format_double_array() {
        Object array = new double[]{ 6.8, 8.3 };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[6.8, 8.3]");
    }

    @Test
    public void should_format_float_array() {
        Object array = new float[]{ 6.1F, 8.6F };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[6.1f, 8.6f]");
    }

    @Test
    public void should_format_int_array() {
        Object array = new int[]{ 78, 66 };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[78, 66]");
    }

    @Test
    public void should_format_primitive_array_up_to_the_maximum_allowed_elements() {
        Object array = new int[]{ 1, 2, 3, 4 };
        StandardRepresentation.setMaxElementsForPrinting(3);
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[1, 2, 3, ...]");
    }

    @Test
    public void should_format_long_array() {
        Object array = new long[]{ 160L, 98L };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[160L, 98L]");
    }

    @Test
    public void should_format_short_array() {
        Object array = new short[]{ ((short) (5)), ((short) (8)) };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[5, 8]");
    }

    @Test
    public void should_return_null_if_parameter_is_not_array() {
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray("Hello")).isNull();
    }

    @Test
    public void should_format_longArray() {
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(new long[]{ 6L, 8L })).isEqualTo("[6L, 8L]");
    }

    @Test
    public void should_format_String_array() {
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(new Object[]{ "Hello", "World" })).isEqualTo("[\"Hello\", \"World\"]");
    }

    @Test
    public void should_format_array_with_null_element() {
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(new Object[]{ "Hello", null })).isEqualTo("[\"Hello\", null]");
    }

    @Test
    public void should_format_Object_array() {
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(new Object[]{ "Hello", new StandardRepresentation_array_format_Test.Person("Anakin") })).isEqualTo("[\"Hello\", \'Anakin\']");
    }

    @Test
    public void should_format_Object_array_on_new_line_smart() {
        StandardRepresentation.setMaxLengthForSingleLineDescription(11);
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(new Object[]{ "Hello", new StandardRepresentation_array_format_Test.Person("Anakin") })).isEqualTo(String.format(("[\"Hello\",%n" + "    'Anakin']")));
    }

    @Test
    public void should_format_Object_array_that_has_primitive_array_as_element() {
        boolean[] booleans = new boolean[]{ true, false };
        Object[] array = new Object[]{ "Hello", booleans };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[\"Hello\", [true, false]]");
    }

    @Test
    public void should_format_Object_array_having_itself_as_element() {
        Object[] array1 = new Object[]{ "Hello", "World" };
        Object[] array2 = new Object[]{ array1 };
        array1[1] = array2;
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array2)).isEqualTo("[[\"Hello\", (this array)]]");
    }

    @Test
    public void should_format_Object_array_having_empty_primitive_array() {
        Object[] array = new Object[]{ "Hello", new int[]{  } };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[\"Hello\", []]");
    }

    @Test
    public void should_format_Object_array_having_null_element() {
        Object[] array = new Object[]{ "Hello", null };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[\"Hello\", null]");
    }

    @Test
    public void should_format_array_up_to_the_maximum_allowed_elements() {
        StandardRepresentation.setMaxElementsForPrinting(3);
        Object[] array = new Object[]{ "First", "Second", "Third", "Fourth" };
        Assertions.assertThat(StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array)).isEqualTo("[\"First\", \"Second\", \"Third\", ...]");
    }

    @Test
    public void should_format_array_with_one_element_per_line() {
        StandardRepresentation.setMaxLengthForSingleLineDescription(25);
        Object[] array = new Object[]{ "1234567890", "1234567890", "1234567890", "1234567890" };
        String formatted = StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array);
        String formattedAfterNewLine = (((System.lineSeparator()) + "  <") + formatted) + ">";
        Assertions.assertThat(formattedAfterNewLine).isEqualTo(String.format(("%n" + ((("  <[\"1234567890\",%n" + "    \"1234567890\",%n") + "    \"1234567890\",%n") + "    \"1234567890\"]>"))));
    }

    @Test
    public void should_format_array_up_to_the_maximum_allowed_elements_and_max_line_length() {
        StandardRepresentation.setMaxElementsForPrinting(4);
        StandardRepresentation.setMaxLengthForSingleLineDescription(25);
        Object[] array = new Object[]{ "1234567890", "1234567890", "1234567890", "1234567890", "1234567890" };
        String formatted = StandardRepresentation_array_format_Test.STANDARD_REPRESENTATION.formatArray(array);
        String formattedAfterNewLine = (((System.lineSeparator()) + "  <") + formatted) + ">";
        Assertions.assertThat(formattedAfterNewLine).isEqualTo(String.format(("%n" + (((("  <[\"1234567890\",%n" + "    \"1234567890\",%n") + "    \"1234567890\",%n") + "    \"1234567890\",%n") + "    ...]>"))));
    }

    private static class Person {
        private final String name;

        Person(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return Strings.quote(name);
        }
    }
}

