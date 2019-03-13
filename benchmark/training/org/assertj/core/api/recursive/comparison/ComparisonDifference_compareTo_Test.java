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


import java.util.Set;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


public class ComparisonDifference_compareTo_Test {
    @Test
    public void should_order_differences_by_alphabetical_path_ignoring_dot_separator() {
        // GIVEN
        ComparisonDifference diff1 = ComparisonDifference_compareTo_Test.comparisonDifference("a", "b");
        ComparisonDifference diff2 = ComparisonDifference_compareTo_Test.comparisonDifference("a", "c");
        ComparisonDifference diff3 = ComparisonDifference_compareTo_Test.comparisonDifference("aa");
        ComparisonDifference diff4 = ComparisonDifference_compareTo_Test.comparisonDifference("a", "b", "c");
        ComparisonDifference diff5 = ComparisonDifference_compareTo_Test.comparisonDifference("b");
        ComparisonDifference diff6 = ComparisonDifference_compareTo_Test.comparisonDifference("aaa");
        // WHEN
        Set<ComparisonDifference> differences = Sets.newTreeSet(diff1, diff2, diff3, diff4, diff5, diff6);
        // THEN
        Assertions.assertThat(differences).extracting(ComparisonDifference::getPath).containsExactly("aa", "aaa", "a.b", "a.b.c", "a.c", "b");
    }
}

