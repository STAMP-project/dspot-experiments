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
import org.assertj.core.api.Assertions;
import org.assertj.core.test.Jedi;
import org.junit.jupiter.api.Test;


public class AbstractAssert_satisfies_with_Consumer_Test {
    private Jedi yoda;

    private Jedi luke;

    private Consumer<Jedi> jediRequirements;

    @Test
    public void should_satisfy_single_requirement() {
        Assertions.assertThat(yoda).satisfies(( jedi) -> assertThat(jedi.lightSaberColor).isEqualTo("Green"));
    }

    @Test
    public void should_satisfy_multiple_requirements() {
        Assertions.assertThat(yoda).satisfies(jediRequirements);
        Assertions.assertThat(luke).satisfies(jediRequirements);
    }

    @Test
    public void should_fail_according_to_requirements() {
        Jedi vader = new Jedi("Vader", "Red");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(vader).satisfies(jediRequirements)).withMessage(String.format("[check light saber] %nExpecting:%n <\"Red\">%nto be equal to:%n <\"Green\">%nbut was not."));
    }

    @Test
    public void should_fail_if_consumer_is_null() {
        Consumer<Jedi> nullRequirements = null;
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(yoda).satisfies(nullRequirements)).withMessage("The Consumer<T> expressing the assertions requirements must not be null");
    }
}

