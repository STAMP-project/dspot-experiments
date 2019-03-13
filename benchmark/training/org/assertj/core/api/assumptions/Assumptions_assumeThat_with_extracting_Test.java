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
package org.assertj.core.api.assumptions;


import java.util.Set;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.CartoonCharacter;
import org.assertj.core.test.Jedi;
import org.junit.AssumptionViolatedException;
import org.junit.jupiter.api.Test;


public class Assumptions_assumeThat_with_extracting_Test {
    private Set<Jedi> jedis;

    private Jedi yoda;

    private Jedi luke;

    private CartoonCharacter bart;

    private CartoonCharacter lisa;

    private CartoonCharacter maggie;

    private CartoonCharacter homer;

    private CartoonCharacter pebbles;

    private CartoonCharacter fred;

    @Test
    public void should_run_test_when_assumption_using_extracting_on_list_passes() {
        Assertions.assertThatCode(() -> assumeThat(jedis).extracting("name").contains("Luke")).doesNotThrowAnyException();
    }

    @Test
    public void should_run_test_when_assumption_using_extracting_on_object_passes() {
        Assertions.assertThatCode(() -> assumeThat(yoda).extracting("name").containsExactly("Yoda")).doesNotThrowAnyException();
    }

    @Test
    public void should_allow_assumptions_with_flatExtracting() {
        Assertions.assertThatCode(() -> assumeThat(newArrayList(homer, fred)).flatExtracting("children").containsOnly(bart, lisa, maggie, pebbles)).doesNotThrowAnyException();
    }

    @Test
    public void should_ignore_test_when_assumption_using_extracting_fails() {
        Assertions.assertThatExceptionOfType(AssumptionViolatedException.class).isThrownBy(() -> assumeThat(jedis).extracting("name").contains("Vader"));
    }
}

