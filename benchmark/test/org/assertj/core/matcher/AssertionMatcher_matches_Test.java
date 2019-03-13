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
package org.assertj.core.matcher;


import org.assertj.core.api.Assertions;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AssertionMatcher_matches_Test {
    private static final Integer ZERO = 0;

    private static final Integer ONE = 1;

    private final AssertionMatcher<Integer> isZeroMatcher = new AssertionMatcher<Integer>() {
        @Override
        public void assertion(Integer actual) throws AssertionError {
            Assertions.assertThat(actual).isZero();
        }
    };

    private boolean removeAssertJRelatedElementsFromStackTrace;

    @Test
    public void matcher_should_pass_when_assertion_passes() {
        Assertions.assertThat(isZeroMatcher.matches(AssertionMatcher_matches_Test.ZERO)).isTrue();
    }

    @Test
    public void matcher_should_not_fill_description_when_assertion_passes() {
        Description description = Mockito.mock(Description.class);
        Assertions.assertThat(isZeroMatcher.matches(AssertionMatcher_matches_Test.ZERO)).isTrue();
        isZeroMatcher.describeTo(description);
        Mockito.verifyZeroInteractions(description);
    }

    @Test
    public void matcher_should_fail_when_assertion_fails() {
        Assertions.assertThat(isZeroMatcher.matches(AssertionMatcher_matches_Test.ONE)).isFalse();
    }

    /**
     * {@link Failures#removeAssertJRelatedElementsFromStackTrace} must be set to true
     * in order for this test to pass. It is in {@link this#setUp()}.
     */
    @Test
    public void matcher_should_fill_description_when_assertion_fails() {
        Description description = Mockito.mock(Description.class);
        Assertions.assertThat(isZeroMatcher.matches(AssertionMatcher_matches_Test.ONE)).isFalse();
        isZeroMatcher.describeTo(description);
        Mockito.verify(description).appendText("AssertionError with message: ");
        Mockito.verify(description).appendText(String.format("%nExpecting:%n <1>%nto be equal to:%n <0>%nbut was not."));
        Mockito.verify(description).appendText(String.format("%n%nStacktrace was: "));
        // @format:off
        Mockito.verify(description).appendText(ArgumentMatchers.argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String s) {
                return (((s.contains(String.format("%nExpecting:%n <1>%nto be equal to:%n <0>%nbut was not."))) && (s.contains("at org.assertj.core.matcher.AssertionMatcher_matches_Test$1.assertion(AssertionMatcher_matches_Test.java:"))) && (s.contains("at org.assertj.core.matcher.AssertionMatcher.matches(AssertionMatcher.java:"))) && (s.contains("at org.assertj.core.matcher.AssertionMatcher_matches_Test.matcher_should_fill_description_when_assertion_fails(AssertionMatcher_matches_Test.java:"));
            }
        }));
        // @format:on
    }
}

