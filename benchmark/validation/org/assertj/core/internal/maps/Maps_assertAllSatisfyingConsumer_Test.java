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


import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ElementsShouldSatisfy;
import org.assertj.core.error.ElementsShouldSatisfy.UnsatisfiedRequirement;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.Player;
import org.assertj.core.test.TestData;
import org.assertj.core.test.WithPlayerData;
import org.assertj.core.util.AssertionsUtil;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class Maps_assertAllSatisfyingConsumer_Test extends MapsBaseTest {
    private Map<String, Player> greatPlayers;

    @Test
    public void should_pass_if_all_entries_satisfy_the_given_requirements() {
        maps.assertAllSatisfy(TestData.someInfo(), greatPlayers, ( team, player) -> {
            assertThat(team).isIn("Lakers", "Bulls", "Spurs");
            assertThat(player.getPointsPerGame()).isGreaterThan(18);
        });
    }

    @Test
    public void should_pass_if_actual_map_is_empty() {
        // GIVEN
        greatPlayers.clear();
        // WHEN THEN
        maps.assertAllSatisfy(TestData.someInfo(), greatPlayers, ( $1, $2) -> assertThat(true).isFalse());
    }

    @Test
    public void should_fail_if_one_entry_does_not_satisfy_the_given_requirements() {
        // WHEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> maps.assertAllSatisfy(someInfo(), greatPlayers, ( team, player) -> {
            assertThat(team).isIn("Lakers", "Bulls", "Spurs");
            assertThat(player.getPointsPerGame()).as("%s %s ppg", player.getName().first, player.getName().getLast()).isLessThan(30);
        }));
        // THEN
        List<UnsatisfiedRequirement> unsatisfiedRequirements = Lists.list(Maps_assertAllSatisfyingConsumer_Test.failOnPpgLessThan("Bulls", WithPlayerData.jordan, 30));
        Assertions.assertThat(error).hasMessage(ElementsShouldSatisfy.elementsShouldSatisfy(greatPlayers, unsatisfiedRequirements, TestData.someInfo()).create());
    }

    @Test
    public void should_report_all_the_entries_not_satisfying_the_given_requirements() {
        // WHEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> maps.assertAllSatisfy(someInfo(), greatPlayers, ( team, player) -> {
            assertThat(team).isIn("Lakers", "Bulls", "Spurs");
            assertThat(player.getPointsPerGame()).as("%s %s ppg", player.getName().first, player.getName().getLast()).isGreaterThanOrEqualTo(30);
        }));
        // THEN
        List<UnsatisfiedRequirement> unsatisfiedRequirements = Lists.list(Maps_assertAllSatisfyingConsumer_Test.failOnPpgGreaterThanEqual("Spurs", WithPlayerData.duncan, 30), Maps_assertAllSatisfyingConsumer_Test.failOnPpgGreaterThanEqual("Lakers", WithPlayerData.magic, 30));
        Assertions.assertThat(error).hasMessage(ElementsShouldSatisfy.elementsShouldSatisfy(greatPlayers, unsatisfiedRequirements, TestData.someInfo()).create());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        // WHEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> maps.assertAllSatisfy(someInfo(), null, ( team, player) -> {
        }));
        // THEN
        Assertions.assertThat(error).hasMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_given_requirements_are_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> maps.assertAllSatisfy(someInfo(), greatPlayers, null)).withMessage("The BiConsumer<K, V> expressing the assertions requirements must not be null");
    }
}

