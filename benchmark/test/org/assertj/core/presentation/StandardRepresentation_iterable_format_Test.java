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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class StandardRepresentation_iterable_format_Test extends AbstractBaseRepresentationTest {
    private static final StandardRepresentation STANDARD_REPRESENTATION = new StandardRepresentation();

    @Test
    public void should_return_null_if_iterable_is_null() {
        Assertions.assertThat(StandardRepresentation_iterable_format_Test.STANDARD_REPRESENTATION.smartFormat(((Iterable<?>) (null)))).isNull();
    }

    @Test
    public void should_return_empty_brackets_if_iterable_is_empty() {
        Assertions.assertThat(StandardRepresentation_iterable_format_Test.STANDARD_REPRESENTATION.smartFormat(Arrays.asList())).isEqualTo("[]");
    }

    @Test
    public void should_format_iterable_on_one_line_if_description_is_short_enough() {
        String e1 = StandardRepresentation_iterable_format_Test.stringOfLength(((StandardRepresentation.getMaxLengthForSingleLineDescription()) / 10));
        String e2 = StandardRepresentation_iterable_format_Test.stringOfLength(((StandardRepresentation.getMaxLengthForSingleLineDescription()) / 10));
        Assertions.assertThat(StandardRepresentation_iterable_format_Test.STANDARD_REPRESENTATION.smartFormat(Arrays.asList(e1, e2))).isEqualTo((((("[\"" + e1) + "\", \"") + e2) + "\"]"));
    }

    @Test
    public void should_format_iterable_with_one_element_per_line_when_single_line_description_is_too_long() {
        String e1 = StandardRepresentation_iterable_format_Test.stringOfLength(StandardRepresentation.getMaxLengthForSingleLineDescription());
        String e2 = StandardRepresentation_iterable_format_Test.stringOfLength(StandardRepresentation.getMaxLengthForSingleLineDescription());
        Assertions.assertThat(StandardRepresentation_iterable_format_Test.STANDARD_REPRESENTATION.smartFormat(Arrays.asList(e1, e2))).isEqualTo(String.format(((((("[\"" + e1) + "\",%n") + "    \"") + e2) + "\"]")));
    }

    @Test
    public void should_format_iterable_with_custom_start_and_end() {
        List<? extends Object> list = Arrays.asList("First", 3);
        Assertions.assertThat(StandardRepresentation_iterable_format_Test.STANDARD_REPRESENTATION.singleLineFormat(list, "{", "}")).isEqualTo("{\"First\", 3}");
        Assertions.assertThat(StandardRepresentation_iterable_format_Test.STANDARD_REPRESENTATION.singleLineFormat(Arrays.asList(), "{", "}")).isEqualTo("{}");
    }

    @Test
    public void should_format_iterable_with_one_element_per_line() {
        String formatted = StandardRepresentation_iterable_format_Test.STANDARD_REPRESENTATION.multiLineFormat(Arrays.asList("First", 3, "foo", "bar"));
        String formattedAfterNewLine = (((System.lineSeparator()) + "  <") + formatted) + ">";
        Assertions.assertThat(formattedAfterNewLine).isEqualTo(String.format(("%n" + ((("  <[\"First\",%n" + "    3,%n") + "    \"foo\",%n") + "    \"bar\"]>"))));
    }

    @Test
    public void should_format_iterable_up_to_the_maximum_allowed_elements_multi_line() {
        StandardRepresentation.setMaxElementsForPrinting(3);
        StandardRepresentation.setMaxLengthForSingleLineDescription(10);
        String formatted = StandardRepresentation_iterable_format_Test.STANDARD_REPRESENTATION.smartFormat(Arrays.asList("First", 3, "foo", "bar"));
        String formattedAfterNewLine = (((System.lineSeparator()) + "  <") + formatted) + ">";
        Assertions.assertThat(formattedAfterNewLine).isEqualTo(String.format(("%n" + ((("  <[\"First\",%n" + "    3,%n") + "    \"foo\",%n") + "    ...]>"))));
    }

    @Test
    public void should_format_iterable_up_to_the_maximum_allowed_elements_single_line() {
        StandardRepresentation.setMaxElementsForPrinting(3);
        String formatted = StandardRepresentation_iterable_format_Test.STANDARD_REPRESENTATION.smartFormat(Arrays.asList("First", 3, "foo", "bar"));
        Assertions.assertThat(formatted).isEqualTo("[\"First\", 3, \"foo\", ...]");
    }

    @Test
    public void should_format_iterable_with_an_element_per_line_according_the_given_representation() {
        String formatted = new HexadecimalRepresentation().multiLineFormat(Arrays.asList(1, 2, 3));
        String formattedAfterNewLine = (((System.lineSeparator()) + "  <") + formatted) + ">";
        Assertions.assertThat(formattedAfterNewLine).isEqualTo(String.format(("%n" + (("  <[0x0000_0001,%n" + "    0x0000_0002,%n") + "    0x0000_0003]>"))));
    }

    @Test
    public void should_format_recursive_iterable() {
        List<Object> list = new ArrayList<>();
        list.add(list);
        list.add(list);
        String formatted = StandardRepresentation_iterable_format_Test.STANDARD_REPRESENTATION.multiLineFormat(list);
        Assertions.assertThat(formatted).isEqualTo(String.format(("[(this Collection),%n" + "    (this Collection)]")));
    }
}

