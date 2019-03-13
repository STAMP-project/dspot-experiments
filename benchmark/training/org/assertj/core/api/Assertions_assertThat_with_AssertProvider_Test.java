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


import actual.text;
import org.assertj.core.internal.Strings;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link Assertions#assertThat(AssertProvider)}.
 *
 * @author Tobias Liefke
 */
public class Assertions_assertThat_with_AssertProvider_Test {
    private Assertions_assertThat_with_AssertProvider_Test.TestedObject object;

    @Test
    public void should_allow_assert_provider_within_assertThat() {
        Assertions.assertThat(object).containsText("es");
    }

    @Test
    public void should_use_assert_provider_directly() {
        object.assertThat().containsText("es");
    }

    private static class TestedObject implements AssertProvider<Assertions_assertThat_with_AssertProvider_Test.TestedObjectAssert> {
        private final String text;

        public TestedObject(String text) {
            this.text = text;
        }

        public Assertions_assertThat_with_AssertProvider_Test.TestedObjectAssert assertThat() {
            return new Assertions_assertThat_with_AssertProvider_Test.TestedObjectAssert(this);
        }
    }

    private static class TestedObjectAssert extends AbstractAssert<Assertions_assertThat_with_AssertProvider_Test.TestedObjectAssert, Assertions_assertThat_with_AssertProvider_Test.TestedObject> {
        private Strings strings = Strings.instance();

        public TestedObjectAssert(Assertions_assertThat_with_AssertProvider_Test.TestedObject testedObject) {
            super(testedObject, Assertions_assertThat_with_AssertProvider_Test.TestedObjectAssert.class);
        }

        public Assertions_assertThat_with_AssertProvider_Test.TestedObjectAssert containsText(String text) {
            strings.assertContains(info, text, text);
            return this;
        }
    }
}

