/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitoutil;


import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;


public class SafeJUnitRuleTest {
    SafeJUnitRuleTest.MethodRuleStub delegate = new SafeJUnitRuleTest.MethodRuleStub();

    SafeJUnitRule rule = new SafeJUnitRule(delegate);

    @Test
    public void happy_path_no_exception() throws Throwable {
        // when
        rule.apply(new Statement() {
            public void evaluate() throws Throwable {
                // all good
            }
        }, Mockito.mock(FrameworkMethod.class), this).evaluate();
        // then
        Assert.assertTrue(delegate.statementEvaluated);
    }

    @Test(expected = IllegalArgumentException.class)
    public void regular_failing_test() throws Throwable {
        // when
        rule.apply(new Statement() {
            public void evaluate() throws Throwable {
                throw new IllegalArgumentException();
            }
        }, Mockito.mock(FrameworkMethod.class), this).evaluate();
    }

    @Test
    public void rule_threw_exception() throws Throwable {
        // expect
        rule.expectFailure(AssertionError.class, "x");
        // when
        rule.apply(new Statement() {
            public void evaluate() throws Throwable {
                throw new AssertionError("x");
            }
        }, Mockito.mock(FrameworkMethod.class), this).evaluate();
    }

    @Test
    public void expected_exception_but_no_exception() throws Throwable {
        // expect
        rule.expectFailure(AssertionError.class, "x");
        // when
        try {
            rule.apply(new Statement() {
                public void evaluate() throws Throwable {
                    // all good
                }
            }, Mockito.mock(FrameworkMethod.class), this).evaluate();
            Assert.fail();
            // then
        } catch (SafeJUnitRule.ExpectedThrowableNotReported t) {
            // yup, expected
        }
    }

    @Test
    public void expected_exception_message_did_not_match() throws Throwable {
        // expect
        rule.expectFailure(AssertionError.class, "FOO");
        // when
        try {
            rule.apply(new Statement() {
                public void evaluate() throws Throwable {
                    throw new AssertionError("BAR");
                }
            }, Mockito.mock(FrameworkMethod.class), this).evaluate();
            Assert.fail();
        } catch (AssertionError throwable) {
            Assertions.assertThat(throwable).hasMessageContaining("Expecting message");
        }
    }

    @Test
    public void expected_exception_type_did_not_match() throws Throwable {
        // expect
        rule.expectFailure(AssertionError.class, "x");
        // when
        try {
            rule.apply(new Statement() {
                public void evaluate() throws Throwable {
                    throw new RuntimeException("x");
                }
            }, Mockito.mock(FrameworkMethod.class), this).evaluate();
            Assert.fail();
        } catch (AssertionError throwable) {
            Assertions.assertThat(throwable).hasMessageContaining("but was:");
        }
    }

    @Test
    public void expected_exception_assert_did_not_match() throws Throwable {
        // expect
        rule.expectFailure(new SafeJUnitRule.FailureAssert() {
            public void doAssert(Throwable t) {
                throw new AssertionError("x");
            }
        });
        // when
        try {
            rule.apply(new Statement() {
                public void evaluate() throws Throwable {
                    throw new RuntimeException();
                }
            }, Mockito.mock(FrameworkMethod.class), this).evaluate();
            Assert.fail();
        } catch (AssertionError throwable) {
            Assert.assertEquals(throwable.getMessage(), "x");
        }
    }

    private static class MethodRuleStub implements MethodRule {
        private boolean statementEvaluated;

        public Statement apply(final Statement base, FrameworkMethod method, Object target) {
            return new Statement() {
                public void evaluate() throws Throwable {
                    statementEvaluated = true;
                    base.evaluate();
                }
            };
        }
    }
}

