/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.junitrule;


import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.exceptions.misusing.UnfinishedVerificationException;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;
import org.mockito.junit.MockitoJUnit;
import org.mockito.quality.Strictness;
import org.mockitousage.IMethods;
import org.mockitousage.strictness.ProductionCode;
import org.mockitoutil.SafeJUnitRule;
import org.mockitoutil.TestBase;


public class StrictJUnitRuleTest {
    @Rule
    public SafeJUnitRule rule = new SafeJUnitRule(MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS));

    @Mock
    IMethods mock;

    @Mock
    IMethods mock2;

    @Test
    public void ok_when_no_stubbings() throws Throwable {
        mock.simpleMethod();
        Mockito.verify(mock).simpleMethod();
    }

    @Test
    public void ok_when_all_stubbings_used() throws Throwable {
        BDDMockito.given(mock.simpleMethod(10)).willReturn("foo");
        mock.simpleMethod(10);
    }

    @Test
    public void ok_when_used_and_mismatched_argument() throws Throwable {
        BDDMockito.given(mock.simpleMethod(10)).willReturn("foo");
        mock.simpleMethod(10);
        mock.simpleMethod(15);
    }

    @Test
    public void fails_when_unused_stubbings() throws Throwable {
        // expect
        rule.expectFailure(UnnecessaryStubbingException.class);
        // when
        BDDMockito.given(mock.simpleMethod(10)).willReturn("foo");
        mock2.simpleMethod(10);
    }

    @Test
    public void test_failure_trumps_unused_stubbings() throws Throwable {
        // expect
        rule.expectFailure(AssertionError.class, "x");
        // when
        BDDMockito.given(mock.simpleMethod(10)).willReturn("foo");
        mock.otherMethod();
        throw new AssertionError("x");
    }

    @Test
    public void why_do_return_syntax_is_useful() throws Throwable {
        // Trade-off of Mockito strictness documented in test
        // expect
        rule.expectFailure(PotentialStubbingProblem.class);
        // when
        Mockito.when(mock.simpleMethod(10)).thenReturn("10");
        ProductionCode.simpleMethod(mock, 20);
    }

    @Test
    public void fails_fast_when_stubbing_invoked_with_different_argument() throws Throwable {
        // expect
        rule.expectFailure(new SafeJUnitRule.FailureAssert() {
            public void doAssert(Throwable t) {
                Assertions.assertThat(t).isInstanceOf(PotentialStubbingProblem.class);
                Assert.assertEquals(TestBase.filterLineNo(("\n" + (((((((((((((((("Strict stubbing argument mismatch. Please check:\n" + " - this invocation of \'simpleMethod\' method:\n") + "    mock.simpleMethod(15);\n") + "    -> at org.mockitousage.strictness.ProductionCode.simpleMethod(ProductionCode.java:0)\n") + " - has following stubbing(s) with different arguments:\n") + "    1. mock.simpleMethod(20);\n") + "      -> at org.mockitousage.junitrule.StrictJUnitRuleTest.fails_fast_when_stubbing_invoked_with_different_argument(StrictJUnitRuleTest.java:0)\n") + "    2. mock.simpleMethod(30);\n") + "      -> at org.mockitousage.junitrule.StrictJUnitRuleTest.fails_fast_when_stubbing_invoked_with_different_argument(StrictJUnitRuleTest.java:0)\n") + "Typically, stubbing argument mismatch indicates user mistake when writing tests.\n") + "Mockito fails early so that you can debug potential problem easily.\n") + "However, there are legit scenarios when this exception generates false negative signal:\n") + "  - stubbing the same method multiple times using \'given().will()\' or \'when().then()\' API\n") + "    Please use \'will().given()\' or \'doReturn().when()\' API for stubbing.\n") + "  - stubbed method is intentionally invoked with different arguments by code under test\n") + "    Please use default or \'silent\' JUnit Rule (equivalent of Strictness.LENIENT).\n") + "For more information see javadoc for PotentialStubbingProblem class."))), TestBase.filterLineNo(t.getMessage()));
            }
        });
        // when stubbings in the test code:
        BDDMockito.willReturn("10").given(mock).simpleMethod(10);// used

        BDDMockito.willReturn("20").given(mock).simpleMethod(20);// unused

        BDDMockito.willReturn("30").given(mock).simpleMethod(30);// unused

        // then
        mock.otherMethod();// ok, different method

        mock.simpleMethod(10);// ok, stubbed with this argument

        // invocation in the code under test uses different argument and should fail immediately
        // this helps with debugging and is essential for Mockito strictness
        ProductionCode.simpleMethod(mock, 15);
    }

    @Test
    public void verify_no_more_interactions_ignores_stubs() throws Throwable {
        // when stubbing in test:
        BDDMockito.given(mock.simpleMethod(10)).willReturn("foo");
        // and code under test does:
        mock.simpleMethod(10);// implicitly verifies the stubbing

        mock.otherMethod();
        // and in test we:
        Mockito.verify(mock).otherMethod();
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void unused_stubs_with_multiple_mocks() throws Throwable {
        // expect
        rule.expectFailure(new SafeJUnitRule.FailureAssert() {
            public void doAssert(Throwable t) {
                Assert.assertEquals(TestBase.filterLineNo(("\n" + ((((("Unnecessary stubbings detected.\n" + "Clean & maintainable test code requires zero unnecessary code.\n") + "Following stubbings are unnecessary (click to navigate to relevant line of code):\n") + "  1. -> at org.mockitousage.junitrule.StrictJUnitRuleTest.unused_stubs_with_multiple_mocks(StrictJUnitRuleTest.java:0)\n") + "  2. -> at org.mockitousage.junitrule.StrictJUnitRuleTest.unused_stubs_with_multiple_mocks(StrictJUnitRuleTest.java:0)\n") + "Please remove unnecessary stubbings or use 'lenient' strictness. More info: javadoc for UnnecessaryStubbingException class."))), TestBase.filterLineNo(t.getMessage()));
            }
        });
        // when test has
        BDDMockito.given(mock.simpleMethod(10)).willReturn("foo");
        BDDMockito.given(mock2.simpleMethod(20)).willReturn("foo");
        BDDMockito.given(mock.otherMethod()).willReturn("foo");// used and should not be reported

        // and code has
        mock.otherMethod();
        mock2.booleanObjectReturningMethod();
    }

    @SuppressWarnings({ "MockitoUsage", "CheckReturnValue" })
    @Test
    public void rule_validates_mockito_usage() throws Throwable {
        // expect
        rule.expectFailure(UnfinishedVerificationException.class);
        // when test contains unfinished verification
        Mockito.verify(mock);
    }
}

