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


import org.assertj.core.api.Assertions;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.api.Test;


public class AbstractAssert_withRepresentation_Test {
    @Test
    public void should_throw_error_if_description_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(new org.assertj.core.api.abstract_.Example()).withRepresentation(null)).withMessage("The representation to use should not be null.");
    }

    @Test
    public void should_be_able_to_use_a_custom_representation_for_error_messages() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            assertThat(new org.assertj.core.api.abstract_.Example()).withRepresentation(new org.assertj.core.api.abstract_.CustomRepresentation()).isNull();
        }).withMessage(String.format("%nExpecting:%n <Example>%nto be equal to:%n <null>%nbut was not."));
    }

    @Test
    public void should_be_able_to_override_an_existing_representation() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat("foo").withRepresentation(new org.assertj.core.api.abstract_.CustomRepresentation()).startsWith("bar")).withMessageContaining("$foo$").withMessageContaining("$bar$");
    }

    private class Example {}

    private class CustomRepresentation extends StandardRepresentation {
        @Override
        public String toStringOf(Object o) {
            if (o instanceof AbstractAssert_withRepresentation_Test.Example)
                return "Example";

            return super.toStringOf(o);
        }

        @Override
        protected String toStringOf(String s) {
            return ("$" + s) + "$";
        }
    }
}

