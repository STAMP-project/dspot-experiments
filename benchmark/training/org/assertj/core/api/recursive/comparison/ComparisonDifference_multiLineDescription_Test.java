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
package org.assertj.core.api.recursive.comparison;


import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.Maps;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


public class ComparisonDifference_multiLineDescription_Test {
    @Test
    public void should_build_a_multiline_description() {
        // GIVEN
        ComparisonDifference comparisonDifference = new ComparisonDifference(Lists.list("a", "b"), "foo", "bar");
        // WHEN
        String multiLineDescription = comparisonDifference.multiLineDescription();
        // THEN
        Assertions.assertThat(multiLineDescription).isEqualTo(String.format(("field/property 'a.b' differ:%n" + ("- actual value   : \"foo\"%n" + "- expected value : \"bar\""))));
    }

    @Test
    public void multiline_description_should_show_sets_type_difference_when_their_content_is_the_same() {
        // GIVEN
        Set<String> actual = Sets.newLinkedHashSet("bar", "foo");
        Set<String> expected = Sets.newTreeSet("bar", "foo");
        ComparisonDifference comparisonDifference = new ComparisonDifference(Lists.list("a", "b"), actual, expected);
        // WHEN
        String multiLineDescription = comparisonDifference.multiLineDescription();
        // THEN
        Assertions.assertThat(multiLineDescription).contains("field/property 'a.b' differ:").contains("- actual value   : [\"bar\", \"foo\"] (LinkedHashSet@").contains("- expected value : [\"bar\", \"foo\"] (TreeSet@");
    }

    @Test
    public void multiline_description_should_show_maps_type_difference_when_their_content_is_the_same() {
        // GIVEN
        Map<Long, Boolean> actual = Maps.mapOf(Assertions.entry(1L, true), Assertions.entry(2L, false));
        Map<Long, Boolean> expected = new TreeMap<>(actual);
        ComparisonDifference comparisonDifference = new ComparisonDifference(Lists.list("a", "b"), actual, expected);
        // WHEN
        String multiLineDescription = comparisonDifference.multiLineDescription();
        // THEN
        Assertions.assertThat(multiLineDescription).contains("field/property 'a.b' differ:").contains("- actual value   : {1L=true, 2L=false} (LinkedHashMap@").contains("- expected value : {1L=true, 2L=false} (TreeMap@");
    }

    @Test
    public void should_build_comparison_difference_multiline_description_with_additional_information() {
        // GIVEN
        ComparisonDifference com = new ComparisonDifference(Lists.list("a", "b"), "foo", "bar", "additional information");
        // THEN
        Assertions.assertThat(com.multiLineDescription()).isEqualTo(String.format(("field/property 'a.b' differ:%n" + (("- actual value   : \"foo\"%n" + "- expected value : \"bar\"%n") + "additional information"))));
    }

    @Test
    public void should_build_multiline_description_containing_percent() {
        // GIVEN
        ComparisonDifference com = new ComparisonDifference(Lists.list("a", "b"), "foo%", "%bar%%", "%additional %information%");
        // THEN
        Assertions.assertThat(com.multiLineDescription()).isEqualTo(String.format(("field/property 'a.b' differ:%n" + (("- actual value   : \"foo%%\"%n" + "- expected value : \"%%bar%%%%\"%n") + "%%additional %%information%%"))));
    }
}

