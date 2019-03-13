/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import org.mockito.internal.matchers.Any;
import org.mockito.internal.matchers.Equals;
import org.mockito.internal.matchers.InstanceOf;
import org.mockito.internal.matchers.VarargMatcher;
import org.mockito.invocation.Invocation;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class MatcherApplicationStrategyTest extends TestBase {
    @Mock
    IMethods mock;

    private Invocation invocation;

    private List matchers;

    private MatcherApplicationStrategyTest.RecordingAction recordAction;

    @Test
    public void shouldKnowWhenActualArgsSizeIsDifferent1() {
        // given
        invocation = varargs("1");
        matchers = Arrays.asList(new Equals("1"));
        // when
        boolean match = MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(MatcherApplicationStrategyTest.RETURN_ALWAYS_FALSE);
        // then
        Assert.assertFalse(match);
    }

    @Test
    public void shouldKnowWhenActualArgsSizeIsDifferent2() {
        // given
        invocation = varargs("1");
        matchers = Arrays.asList(new Equals("1"));
        // when
        boolean match = MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(MatcherApplicationStrategyTest.RETURN_ALWAYS_TRUE);
        // then
        Assert.assertTrue(match);
    }

    @Test
    public void shouldKnowWhenActualArgsSizeIsDifferent() {
        // given
        invocation = varargs("1", "2");
        matchers = Arrays.asList(new Equals("1"));
        // when
        boolean match = MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(MatcherApplicationStrategyTest.RETURN_ALWAYS_TRUE);
        // then
        Assert.assertFalse(match);
    }

    @Test
    public void shouldKnowWhenMatchersSizeIsDifferent() {
        // given
        invocation = varargs("1");
        matchers = Arrays.asList(new Equals("1"), new Equals("2"));
        // when
        boolean match = MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(MatcherApplicationStrategyTest.RETURN_ALWAYS_TRUE);
        // then
        Assert.assertFalse(match);
    }

    @Test
    public void shouldKnowWhenVarargsMatch() {
        // given
        invocation = varargs("1", "2", "3");
        matchers = Arrays.asList(new Equals("1"), Any.ANY, new InstanceOf(String.class));
        // when
        boolean match = MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(recordAction);
        // then
        Assert.assertTrue(match);
    }

    @Test
    public void shouldAllowAnyVarargMatchEntireVararg() {
        // given
        invocation = varargs("1", "2");
        matchers = Arrays.asList(Any.ANY);
        // when
        boolean match = MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(recordAction);
        // then
        Assert.assertTrue(match);
    }

    @Test
    public void shouldNotAllowAnyObjectWithMixedVarargs() {
        // given
        invocation = mixedVarargs(1, "1", "2");
        matchers = Arrays.asList(new Equals(1));
        // when
        boolean match = MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(recordAction);
        // then
        Assert.assertFalse(match);
    }

    @Test
    public void shouldAllowAnyObjectWithMixedVarargs() {
        // given
        invocation = mixedVarargs(1, "1", "2");
        matchers = Arrays.asList(new Equals(1), Any.ANY);
        // when
        boolean match = MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(recordAction);
        // then
        Assert.assertTrue(match);
    }

    @Test
    public void shouldAnyObjectVarargDealWithDifferentSizeOfArgs() {
        // given
        invocation = mixedVarargs(1, "1", "2");
        matchers = Arrays.asList(new Equals(1));
        // when
        boolean match = MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(recordAction);
        // then
        Assert.assertFalse(match);
        recordAction.assertIsEmpty();
    }

    @Test
    public void shouldMatchAnyVarargEvenIfOneOfTheArgsIsNull() {
        // given
        invocation = mixedVarargs(null, null, "2");
        matchers = Arrays.asList(new Equals(null), Any.ANY);
        // when
        MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(recordAction);
        // then
        recordAction.assertContainsExactly(new Equals(null), Any.ANY, Any.ANY);
    }

    @Test
    public void shouldMatchAnyVarargEvenIfMatcherIsDecorated() {
        // given
        invocation = varargs("1", "2");
        matchers = Arrays.asList(Any.ANY);
        // when
        MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(recordAction);
        // then
        recordAction.assertContainsExactly(Any.ANY, Any.ANY);
    }

    @Test
    public void shouldMatchAnyVarargEvenIfMatcherIsWrappedInHamcrestMatcher() {
        // given
        invocation = varargs("1", "2");
        HamcrestArgumentMatcher argumentMatcher = new HamcrestArgumentMatcher(new MatcherApplicationStrategyTest.IntMatcher());
        matchers = Arrays.asList(argumentMatcher);
        // when
        MatcherApplicationStrategy.getMatcherApplicationStrategyFor(invocation, matchers).forEachMatcherAndArgument(recordAction);
        // then
        recordAction.assertContainsExactly(argumentMatcher, argumentMatcher);
    }

    class IntMatcher extends BaseMatcher<Integer> implements VarargMatcher {
        public boolean matches(Object o) {
            return true;
        }

        public void describeTo(Description description) {
        }
    }

    private class RecordingAction implements ArgumentMatcherAction {
        private List<ArgumentMatcher<?>> matchers = new ArrayList<ArgumentMatcher<?>>();

        @Override
        public boolean apply(ArgumentMatcher<?> matcher, Object argument) {
            matchers.add(matcher);
            return true;
        }

        public void assertIsEmpty() {
            assertThat(matchers).isEmpty();
        }

        public void assertContainsExactly(ArgumentMatcher<?>... matchers) {
            assertThat(this.matchers).containsExactly(matchers);
        }
    }

    private static final ArgumentMatcherAction RETURN_ALWAYS_TRUE = new ArgumentMatcherAction() {
        @Override
        public boolean apply(ArgumentMatcher<?> matcher, Object argument) {
            return true;
        }
    };

    private static final ArgumentMatcherAction RETURN_ALWAYS_FALSE = new ArgumentMatcherAction() {
        @Override
        public boolean apply(ArgumentMatcher<?> matcher, Object argument) {
            return false;
        }
    };
}

