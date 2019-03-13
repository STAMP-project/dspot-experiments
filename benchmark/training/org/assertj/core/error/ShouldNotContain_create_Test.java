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
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.util.CaseInsensitiveStringComparator;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for
 * <code>{@link ShouldNotContain#create(org.assertj.core.description.Description, org.assertj.core.presentation.Representation)}</code>
 * .
 *
 * @author Alex Ruiz
 * @author Yvonne Wang
 * @author Joel Costigliola
 */
public class ShouldNotContain_create_Test {
    @Test
    public void should_create_error_message() {
        ErrorMessageFactory factory = ShouldNotContain.shouldNotContain(Lists.newArrayList("Yoda"), Lists.newArrayList("Luke", "Yoda"), Sets.newLinkedHashSet("Yoda"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting%n" + " <[\"Yoda\"]>%n") + "not to contain%n") + " <[\"Luke\", \"Yoda\"]>%n") + "but found%n") + " <[\"Yoda\"]>%n"))));
    }

    @Test
    public void should_create_error_message_with_custom_comparison_strategy() {
        ErrorMessageFactory factory = ShouldNotContain.shouldNotContain(Lists.newArrayList("Yoda"), Lists.newArrayList("Luke", "Yoda"), Sets.newLinkedHashSet("Yoda"), new ComparatorBasedComparisonStrategy(CaseInsensitiveStringComparator.instance));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting%n" + " <[\"Yoda\"]>%n") + "not to contain%n") + " <[\"Luke\", \"Yoda\"]>%n") + "but found%n <[\"Yoda\"]>%n") + "when comparing values using CaseInsensitiveStringComparator"))));
    }
}

