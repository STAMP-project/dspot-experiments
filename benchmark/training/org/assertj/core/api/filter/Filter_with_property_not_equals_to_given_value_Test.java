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
package org.assertj.core.api.filter;


import org.assertj.core.api.Assertions;
import org.assertj.core.test.Player;
import org.assertj.core.test.WithPlayerData;
import org.assertj.core.util.introspection.IntrospectionError;
import org.junit.jupiter.api.Test;


public class Filter_with_property_not_equals_to_given_value_Test extends WithPlayerData {
    @Test
    public void should_filter_iterable_elements_with_property_not_equals_to_given_value() {
        Iterable<Player> nonOKCPlayers = Filters.filter(WithPlayerData.players).with("team").notEqualsTo("Chicago Bulls").get();
        Assertions.assertThat(nonOKCPlayers).containsOnly(WithPlayerData.kobe, WithPlayerData.duncan, WithPlayerData.magic);
        // players is not modified
        Assertions.assertThat(WithPlayerData.players).hasSize(4);
        Iterable<Player> filteredPlayers = Filters.filter(WithPlayerData.players).with("name.last").notEqualsTo("Jordan").get();
        Assertions.assertThat(filteredPlayers).containsOnly(WithPlayerData.kobe, WithPlayerData.duncan, WithPlayerData.magic);
        // players is not modified
        Assertions.assertThat(WithPlayerData.players).hasSize(4);
    }

    @Test
    public void should_fail_if_property_to_filter_on_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> filter(WithPlayerData.players).with(null).notEqualsTo("foo")).withMessage("The property/field name to filter on should not be null or empty");
    }

    @Test
    public void should_fail_if_elements_to_filter_do_not_have_property_used_by_filter() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> filter(WithPlayerData.players).with("country").notEqualsTo("France")).withMessageContaining("Can't find any field or property with name 'country'");
    }
}

