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
package org.assertj.core.api;


import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.util.Introspection_getProperty_Test;
import org.junit.jupiter.api.Test;


public class Assertions_useRepresentation_Test {
    private Assertions_useRepresentation_Test.CustomRepresentation customRepresentation = new Assertions_useRepresentation_Test.CustomRepresentation();

    @Test
    public void should_use_given_representation_in_assertion_error_messages() {
        Assertions.useRepresentation(customRepresentation);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat("foo").startsWith("bar")).withMessageContaining("$foo$").withMessageContaining("$bar$");
    }

    @Test
    public void should_use_default_representation_in_assertion_error_messages_after_calling_useDefaultRepresentation() {
        Assertions.useRepresentation(customRepresentation);
        Assertions.useDefaultRepresentation();
        try {
            Assertions.assertThat("foo").startsWith("bar");
        } catch (AssertionError e) {
            Assertions.assertThat(e.getMessage()).doesNotContain("$foo$").doesNotContain("bar$");
            return;
        }
        Assertions.fail("AssertionError expected");
    }

    private class CustomRepresentation extends StandardRepresentation {
        // override needed to hook specific formatting
        @Override
        public String toStringOf(Object o) {
            if (o instanceof Introspection_getProperty_Test.Example)
                return "Example";

            // fallback to default formatting.
            return super.toStringOf(o);
        }

        // change String representation
        @Override
        protected String toStringOf(String s) {
            return ("$" + s) + "$";
        }
    }
}

