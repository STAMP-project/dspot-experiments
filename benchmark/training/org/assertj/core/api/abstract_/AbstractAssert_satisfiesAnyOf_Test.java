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
package org.assertj.core.api.abstract_;


import java.util.function.Consumer;
import org.assertj.core.api.AbstractAssertBaseTest;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.TolkienCharacter;
import org.assertj.core.util.AssertionsUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.data.TolkienCharacter.Race.DWARF;
import static org.assertj.core.data.TolkienCharacter.Race.ELF;
import static org.assertj.core.data.TolkienCharacter.Race.HOBBIT;
import static org.assertj.core.data.TolkienCharacter.Race.MAN;


public class AbstractAssert_satisfiesAnyOf_Test extends AbstractAssertBaseTest {
    private TolkienCharacter frodo = TolkienCharacter.of("Frodo", 33, HOBBIT);

    private TolkienCharacter legolas = TolkienCharacter.of("Legolas", 1000, ELF);

    private Consumer<TolkienCharacter> isHobbit = ( tolkienCharacter) -> Assertions.assertThat(tolkienCharacter.getRace()).isEqualTo(HOBBIT);

    private Consumer<TolkienCharacter> isElf = ( tolkienCharacter) -> Assertions.assertThat(tolkienCharacter.getRace()).isEqualTo(ELF);

    private Consumer<TolkienCharacter> isDwarf = ( tolkienCharacter) -> Assertions.assertThat(tolkienCharacter.getRace()).isEqualTo(DWARF);

    @Test
    public void should_pass_when_one_of_the_given_assertions_group_is_met() {
        Assertions.assertThat(frodo).satisfiesAnyOf(isHobbit, isElf);
        Assertions.assertThat(legolas).satisfiesAnyOf(isHobbit, isElf, isDwarf).satisfiesAnyOf(isHobbit, isElf);
    }

    @Test
    public void should_pass_when_all_of_the_given_assertions_groups_are_met() {
        // GIVEN
        Consumer<TolkienCharacter> namesStartsWithF = ( tolkienCharacter) -> Assertions.assertThat(tolkienCharacter.getName()).startsWith("F");
        // THEN
        Assertions.assertThat(frodo).satisfiesAnyOf(isHobbit, namesStartsWithF).satisfiesAnyOf(isHobbit, namesStartsWithF, isHobbit);
    }

    @Test
    public void should_fail_if_all_of_the_given_assertions_groups_fail() {
        // GIVEN
        TolkienCharacter boromir = TolkienCharacter.of("Boromir", 45, MAN);
        // WHEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> assertThat(boromir).as("description").satisfiesAnyOf(isHobbit, isElf));
        // THEN
        Assertions.assertThat(error).isNotNull();
    }

    @Test
    public void should_throw_an_IllegalArgumentException_if_one_of_the_given_assertions_group_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(frodo).satisfiesAnyOf(isHobbit, null)).withMessage("No assertions group should be null");
    }

    @Test
    public void should_honor_description() {
        // GIVEN
        Consumer<String> isEmpty = ( string) -> Assertions.assertThat(string).isEmpty();
        Consumer<String> endsWithZ = ( string) -> Assertions.assertThat(string).endsWith("Z");
        // THEN
        Throwable thrown = Assertions.catchThrowable(() -> assertThat("abc").as("String checks").satisfiesAnyOf(isEmpty, endsWithZ));
        // THEN
        Assertions.assertThat(thrown).isInstanceOf(AssertionError.class).hasMessageContaining("String checks");
    }

    @Test
    public void should_not_honor_overriding_error_message() {
        // GIVEN
        Consumer<String> isEmpty = ( string) -> Assertions.assertThat(string).isEmpty();
        Consumer<String> endsWithZ = ( string) -> Assertions.assertThat(string).endsWith("Z");
        // THEN
        Throwable thrown = Assertions.catchThrowable(() -> assertThat("abc").as("String checks").satisfiesAnyOf(isEmpty, endsWithZ));
        // THEN
        Assertions.assertThat(thrown).isInstanceOf(AssertionError.class).hasMessageContaining("String checks");
    }
}

