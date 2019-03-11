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
package org.assertj.core.internal.maps;


import ElementsShouldSatisfy.UnsatisfiedRequirement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ElementsShouldSatisfy;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.Player;
import org.assertj.core.test.TestData;
import org.assertj.core.util.AssertionsUtil;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class Maps_assertAnySatisfyingConsumer_Test extends MapsBaseTest {
    private Map<String, Player> greatPlayers;

    @Test
    public void should_pass_if_one_entry_satisfies_the_given_requirements() {
        maps.assertAnySatisfy(TestData.someInfo(), greatPlayers, ( team, player) -> {
            assertThat(team).isEqualTo("Lakers");
            assertThat(player.getPointsPerGame()).isGreaterThan(18);
        });
    }

    @Test
    public void should_fail_if_the_map_under_test_is_empty_whatever_the_assertions_requirements_are() {
        // GIVEN
        actual.clear();
        // WHEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> maps.assertAnySatisfy(someInfo(), actual, ( $1, $2) -> assertThat(true).isTrue()));
        // THEN
        Assertions.assertThat(error).hasMessage(ElementsShouldSatisfy.elementsShouldSatisfyAny(actual, Lists.emptyList(), TestData.someInfo()).create());
    }

    @Test
    public void should_fail_if_no_entry_satisfies_the_given_requirements() {
        // WHEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> maps.assertAnySatisfy(someInfo(), actual, ( $1, $2) -> assertThat(true).isFalse()));
        // THEN
        Iterator<Map.Entry<String, String>> actualEntries = actual.entrySet().iterator();
        List<ElementsShouldSatisfy.UnsatisfiedRequirement> errors = Lists.list(ElementsShouldSatisfy.unsatisfiedRequirement(actualEntries.next(), String.format(("%n" + (((("Expecting:%n" + " <true>%n") + "to be equal to:%n") + " <false>%n") + "but was not.")))), ElementsShouldSatisfy.unsatisfiedRequirement(actualEntries.next(), String.format(("%n" + (((("Expecting:%n" + " <true>%n") + "to be equal to:%n") + " <false>%n") + "but was not.")))));
        Assertions.assertThat(error).hasMessage(ElementsShouldSatisfy.elementsShouldSatisfyAny(actual, errors, TestData.someInfo()).create());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        // WHEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> maps.assertAnySatisfy(someInfo(), null, ( team, player) -> {
        }));
        // THEN
        Assertions.assertThat(error).hasMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_given_requirements_are_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> maps.assertAnySatisfy(someInfo(), greatPlayers, null)).withMessage("The BiConsumer<K, V> expressing the assertions requirements must not be null");
    }
}

