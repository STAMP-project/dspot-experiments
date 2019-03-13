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
package org.assertj.core.error;


import org.assertj.core.api.Assertions;
import org.assertj.core.description.TextDescription;
import org.assertj.core.internal.ComparatorBasedComparisonStrategy;
import org.assertj.core.util.CaseInsensitiveStringComparator;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class ShouldContainsAnyOf_create_Test {
    private ErrorMessageFactory factory;

    @Test
    public void should_create_error_message() {
        String message = factory.create(new TextDescription("Test"), CONFIGURATION_PROVIDER.representation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + (((("Expecting:%n" + "  <[\"Yoda\", \"Han\", \"Han\"]>%n") + "to contain at least one of the following elements:%n") + "  <[\"Vador\", \"Leia\"]>%n") + "but none were found "))));
    }

    @Test
    public void should_create_error_message_with_custom_comparison_strategy() {
        ErrorMessageFactory factory = ShouldContainAnyOf.shouldContainAnyOf(Lists.newArrayList("Yoda", "Han", "Han"), Lists.newArrayList("Vador", "Leia"), new ComparatorBasedComparisonStrategy(CaseInsensitiveStringComparator.instance));
        String message = factory.create(new TextDescription("Test"), CONFIGURATION_PROVIDER.representation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting:%n" + "  <[\"Yoda\", \"Han\", \"Han\"]>%n") + "to contain at least one of the following elements:%n") + "  <[\"Vador\", \"Leia\"]>%n") + "but none were found ") + "when comparing values using CaseInsensitiveStringComparator"))));
    }
}

