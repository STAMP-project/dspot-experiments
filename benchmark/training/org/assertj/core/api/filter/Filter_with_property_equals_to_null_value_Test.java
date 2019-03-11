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
import org.junit.jupiter.api.Test;


public class Filter_with_property_equals_to_null_value_Test extends WithPlayerData {
    @Test
    public void should_filter_iterable_elements_with_property_in_given_values() {
        WithPlayerData.jordan.setTeam(null);
        WithPlayerData.kobe.setTeam(null);
        Iterable<Player> filteredPlayers = Filters.filter(WithPlayerData.players).with("team").equalsTo(null).get();
        Assertions.assertThat(filteredPlayers).containsOnly(WithPlayerData.jordan, WithPlayerData.kobe);
        // players is not modified
        Assertions.assertThat(WithPlayerData.players).hasSize(4);
    }
}

