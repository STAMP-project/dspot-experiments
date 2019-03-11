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
import org.assertj.core.test.Jedi;
import org.junit.jupiter.api.Test;


public class AbstractAssert_isInstanceOfSatisfying_Test extends AbstractAssertBaseTest {
    // init here to make it available in create_assertions()
    private Jedi yoda = new Jedi("Yoda", "Green");

    private Jedi luke = new Jedi("Luke Skywalker", "Green");

    private Consumer<Jedi> jediRequirements;

    @Test
    public void should_satisfy_single_requirement() {
        Assertions.assertThat(yoda).isInstanceOfSatisfying(Jedi.class, ( jedi) -> assertThat(jedi.lightSaberColor).isEqualTo("Green"));
    }

    @Test
    public void should_satisfy_multiple_requirements() {
        Assertions.assertThat(yoda).isInstanceOfSatisfying(Jedi.class, jediRequirements);
        Assertions.assertThat(luke).isInstanceOfSatisfying(Jedi.class, jediRequirements);
    }

    @Test
    public void should_fail_according_to_requirements() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(new Jedi("Vader", "Red")).isInstanceOfSatisfying(.class, jediRequirements)).withMessage(String.format("[check light saber] %nExpecting:%n <\"Red\">%nto be equal to:%n <\"Green\">%nbut was not."));
    }

    @Test
    public void should_fail_if_consumer_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(yoda).isInstanceOfSatisfying(.class, null)).withMessage("The Consumer<T> expressing the assertions requirements must not be null");
    }

    @Test
    public void should_fail_if_type_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(yoda).isInstanceOfSatisfying(null, jediRequirements)).withMessage("The given type should not be null");
    }
}

