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


import java.lang.reflect.Modifier;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.assertj.core.description.TextDescription;
import org.assertj.core.test.Maps;
import org.assertj.core.test.Person;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ShouldHaveMethods}</code>
 */
public class ShouldHaveMethods_create_Test {
    @Test
    public void should_create_error_message_for_methods() {
        ErrorMessageFactory factory = ShouldHaveMethods.shouldHaveMethods(Person.class, false, Sets.newTreeSet("getName", "getAddress"), Sets.newTreeSet("getAddress"));
        String message = factory.create(new TextDescription("Test"), CONFIGURATION_PROVIDER.representation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting%n" + "  <org.assertj.core.test.Person>%n") + "to have methods:%n") + "  <[\"getAddress\", \"getName\"]>%n") + "but could not find:%n") + "  <[\"getAddress\"]>"))));
    }

    @Test
    public void should_create_error_message_for_declared_methods() {
        ErrorMessageFactory factory = ShouldHaveMethods.shouldHaveMethods(Person.class, true, Sets.newTreeSet("getName", "getAddress"), Sets.newTreeSet("getAddress"));
        String message = factory.create(new TextDescription("Test"), CONFIGURATION_PROVIDER.representation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting%n" + "  <org.assertj.core.test.Person>%n") + "to have declared methods:%n") + "  <[\"getAddress\", \"getName\"]>%n") + "but could not find:%n") + "  <[\"getAddress\"]>"))));
    }

    @Test
    public void should_create_error_message_for_shouldNotHave_PublicDeclared_Methods() {
        ErrorMessageFactory factory = ShouldHaveMethods.shouldNotHaveMethods(Person.class, Modifier.toString(Modifier.PUBLIC), true, Sets.newTreeSet("getName"));
        String message = factory.create(new TextDescription("Test"), CONFIGURATION_PROVIDER.representation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((("Expecting%n" + "  <org.assertj.core.test.Person>%n") + "not to have any declared public methods but it has the following:%n") + "  <[\"getName\"]>"))));
    }

    @Test
    public void should_create_error_message_for_shouldNotHave_Public_Methods() {
        ErrorMessageFactory factory = ShouldHaveMethods.shouldNotHaveMethods(Person.class, Modifier.toString(Modifier.PUBLIC), false, Sets.newTreeSet("getName"));
        String message = factory.create(new TextDescription("Test"), CONFIGURATION_PROVIDER.representation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((("Expecting%n" + "  <org.assertj.core.test.Person>%n") + "not to have any public methods but it has the following:%n") + "  <[\"getName\"]>"))));
    }

    @Test
    public void should_create_error_message_for_shouldNotHave_Declared_Methods() {
        ErrorMessageFactory factory = ShouldHaveMethods.shouldNotHaveMethods(Person.class, true, Sets.newTreeSet("getName"));
        String message = factory.create(new TextDescription("Test"), CONFIGURATION_PROVIDER.representation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((("Expecting%n" + "  <org.assertj.core.test.Person>%n") + "not to have any declared methods but it has the following:%n") + "  <[\"getName\"]>"))));
    }

    @Test
    public void should_create_error_message_for_shouldNotHaveMethods() {
        ErrorMessageFactory factory = ShouldHaveMethods.shouldNotHaveMethods(Person.class, false, Sets.newTreeSet("getName"));
        String message = factory.create(new TextDescription("Test"), CONFIGURATION_PROVIDER.representation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((("Expecting%n" + "  <org.assertj.core.test.Person>%n") + "not to have any methods but it has the following:%n") + "  <[\"getName\"]>"))));
    }

    @Test
    public void should_create_error_message_for_shouldHaveMethods_with_non_matching_modifier() {
        ErrorMessageFactory factory = ShouldHaveMethods.shouldHaveMethods(Person.class, false, Sets.newTreeSet("finalize"), Modifier.toString(Modifier.PUBLIC), Maps.mapOf(MapEntry.entry("finalize", Modifier.toString(Modifier.PROTECTED))));
        String message = factory.create(new TextDescription("Test"), CONFIGURATION_PROVIDER.representation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting%n" + "  <org.assertj.core.test.Person>%n") + "to have public methods:%n") + "  <[\"finalize\"]>%n") + "but the following are not public:%n") + "  <{\"finalize\"=\"protected\"}>"))));
    }
}

