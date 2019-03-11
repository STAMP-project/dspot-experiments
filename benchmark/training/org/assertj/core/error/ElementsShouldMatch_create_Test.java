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


import PredicateDescription.GIVEN;
import org.assertj.core.api.Assertions;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.PredicateDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class ElementsShouldMatch_create_Test {
    @Test
    public void should_create_error_message_with_one_non_matching_element() {
        ErrorMessageFactory factory = ElementsShouldMatch.elementsShouldMatch(Lists.newArrayList("Luke", "Yoda"), "Yoda", GIVEN);
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((("Expecting all elements of:%n" + "  <[\"Luke\", \"Yoda\"]>%n") + "to match given predicate but this element did not:%n") + "  <\"Yoda\">"))));
    }

    @Test
    public void should_create_error_message_with_multiple_non_matching_elements() {
        ErrorMessageFactory factory = ElementsShouldMatch.elementsShouldMatch(Lists.newArrayList("Luke", "Yoda"), Lists.newArrayList("Luke", "Yoda"), GIVEN);
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((("Expecting all elements of:%n" + "  <[\"Luke\", \"Yoda\"]>%n") + "to match given predicate but these elements did not:%n") + "  <[\"Luke\", \"Yoda\"]>"))));
    }

    @Test
    public void should_create_error_message_with_custom_description() {
        ErrorMessageFactory factory = ElementsShouldMatch.elementsShouldMatch(Lists.newArrayList("Luke", "Yoda"), "Yoda", new PredicateDescription("custom"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((("Expecting all elements of:%n" + "  <[\"Luke\", \"Yoda\"]>%n") + "to match 'custom' predicate but this element did not:%n") + "  <\"Yoda\">"))));
    }
}

