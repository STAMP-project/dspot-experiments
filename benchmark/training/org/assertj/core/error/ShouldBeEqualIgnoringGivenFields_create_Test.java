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


import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.test.Jedi;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ShouldBeEqualToIgnoringFields#create(Description)}</code>.
 *
 * @author Nicolas Fran?ois
 * @author Joel Costigliola
 */
public class ShouldBeEqualIgnoringGivenFields_create_Test {
    private ErrorMessageFactory factory;

    @Test
    public void should_create_error_message_with_all_fields_differences() {
        factory = ShouldBeEqualToIgnoringFields.shouldBeEqualToIgnoringGivenFields(new Jedi("Yoda", "blue"), newArrayList("name", "lightSaberColor"), newArrayList(((Object) ("Yoda")), "blue"), newArrayList(((Object) ("Yoda")), "green"), newArrayList("someIgnoredField"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((((("Expecting values:%n" + "  <[\"Yoda\", \"green\"]>%n") + "in fields:%n") + "  <[\"name\", \"lightSaberColor\"]>%n") + "but were:%n") + "  <[\"Yoda\", \"blue\"]>%n") + "in <Yoda the Jedi>.%n") + "Comparison was performed on all fields but <[\"someIgnoredField\"]>"))));
    }

    @Test
    public void should_create_error_message_with_single_field_difference() {
        factory = ShouldBeEqualToIgnoringFields.shouldBeEqualToIgnoringGivenFields(new Jedi("Yoda", "blue"), newArrayList("lightSaberColor"), newArrayList(((Object) ("blue"))), newArrayList(((Object) ("green"))), newArrayList("someIgnoredField"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + (("Expecting value <\"green\"> in field <\"lightSaberColor\"> " + "but was <\"blue\"> in <Yoda the Jedi>.%n") + "Comparison was performed on all fields but <[\"someIgnoredField\"]>"))));
    }

    @Test
    public void should_create_error_message_with_all_fields_differences_without_ignored_fields() {
        List<String> ignoredFields = newArrayList();
        factory = ShouldBeEqualToIgnoringFields.shouldBeEqualToIgnoringGivenFields(new Jedi("Yoda", "blue"), newArrayList("name", "lightSaberColor"), newArrayList(((Object) ("Yoda")), "blue"), newArrayList(((Object) ("Yoda")), "green"), ignoredFields);
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %nExpecting values:%n" + (((((("  <[\"Yoda\", \"green\"]>%n" + "in fields:%n") + "  <[\"name\", \"lightSaberColor\"]>%n") + "but were:%n") + "  <[\"Yoda\", \"blue\"]>%n") + "in <Yoda the Jedi>.%n") + "Comparison was performed on all fields"))));
    }

    @Test
    public void should_create_error_message_with_single_field_difference_without_ignored_fields() {
        List<String> ignoredFields = newArrayList();
        factory = ShouldBeEqualToIgnoringFields.shouldBeEqualToIgnoringGivenFields(new Jedi("Yoda", "blue"), newArrayList("lightSaberColor"), newArrayList(((Object) ("blue"))), newArrayList(((Object) ("green"))), ignoredFields);
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %nExpecting value <\"green\"> " + (("in field <\"lightSaberColor\"> " + "but was <\"blue\"> in <Yoda the Jedi>.%n") + "Comparison was performed on all fields"))));
    }
}

