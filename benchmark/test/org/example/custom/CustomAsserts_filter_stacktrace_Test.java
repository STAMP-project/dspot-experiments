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
package org.example.custom;


import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.internal.Failures;
import org.junit.jupiter.api.Test;


public class CustomAsserts_filter_stacktrace_Test {
    @Test
    public void should_filter_when_custom_assert_fails_with_message() {
        try {
            new CustomAsserts_filter_stacktrace_Test.CustomAssert("").fail();
        } catch (AssertionError e) {
            Assertions.assertThat(e.getStackTrace()).areNot(CustomAsserts_filter_stacktrace_Test.elementOf(CustomAsserts_filter_stacktrace_Test.CustomAssert.class));
        }
    }

    @Test
    public void should_filter_when_custom_assert_fails_with_overridden_message() {
        try {
            overridingErrorMessage("overridden message").fail();
        } catch (AssertionError e) {
            Assertions.assertThat(e.getStackTrace()).areNot(CustomAsserts_filter_stacktrace_Test.elementOf(CustomAsserts_filter_stacktrace_Test.CustomAssert.class));
        }
    }

    @Test
    public void should_filter_when_custom_assert_throws_assertion_error() {
        try {
            new CustomAsserts_filter_stacktrace_Test.CustomAssert("").throwAnAssertionError();
        } catch (AssertionError e) {
            Assertions.assertThat(e.getStackTrace()).areNot(CustomAsserts_filter_stacktrace_Test.elementOf(CustomAsserts_filter_stacktrace_Test.CustomAssert.class));
        }
    }

    @Test
    public void should_filter_when_abstract_custom_assert_fails() {
        try {
            new CustomAsserts_filter_stacktrace_Test.CustomAssert("").failInAbstractAssert();
        } catch (AssertionError e) {
            Assertions.assertThat(e.getStackTrace()).areNot(CustomAsserts_filter_stacktrace_Test.elementOf(CustomAsserts_filter_stacktrace_Test.CustomAbstractAssert.class));
        }
    }

    @Test
    public void should_not_filter_when_global_remove_option_is_disabled() {
        Failures.instance().setRemoveAssertJRelatedElementsFromStackTrace(false);
        try {
            new CustomAsserts_filter_stacktrace_Test.CustomAssert("").fail();
        } catch (AssertionError e) {
            Assertions.assertThat(e.getStackTrace()).areAtLeastOne(CustomAsserts_filter_stacktrace_Test.elementOf(CustomAsserts_filter_stacktrace_Test.CustomAssert.class));
        }
    }

    private static class CustomAssert extends CustomAsserts_filter_stacktrace_Test.CustomAbstractAssert<CustomAsserts_filter_stacktrace_Test.CustomAssert> {
        public CustomAssert(String actual) {
            super(actual, CustomAsserts_filter_stacktrace_Test.CustomAssert.class);
        }

        public CustomAsserts_filter_stacktrace_Test.CustomAssert fail() {
            failWithMessage("failing assert");
            return this;
        }

        public CustomAsserts_filter_stacktrace_Test.CustomAssert throwAnAssertionError() {
            throwAssertionError(new BasicErrorMessageFactory("failing assert"));
            return this;
        }
    }

    private static class CustomAbstractAssert<S extends CustomAsserts_filter_stacktrace_Test.CustomAbstractAssert<S>> extends AbstractObjectAssert<S, String> {
        protected CustomAbstractAssert(String actual, Class<?> selfType) {
            super(actual, selfType);
        }

        public S failInAbstractAssert() {
            CustomAsserts_filter_stacktrace_Test.CustomAbstractAssert.failWithMessage("failing assert");
            return myself;
        }
    }
}

