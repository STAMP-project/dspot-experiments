/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class DescriptiveMessagesWhenVerificationFailsTest extends TestBase {
    private IMethods mock;

    @Test
    public void should_print_method_name() {
        try {
            Mockito.verify(mock).simpleMethod();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            String expectedMessage = "\n" + (((("Wanted but not invoked:" + "\n") + "iMethods.simpleMethod();") + "\n") + "-> at");
            assertThat(e).hasMessageContaining(expectedMessage);
        }
    }

    private class Foo {
        public String toString() {
            return "foo";
        }
    }

    @Test
    public void should_print_method_name_and_arguments() {
        try {
            Mockito.verify(mock).threeArgumentMethod(12, new DescriptiveMessagesWhenVerificationFailsTest.Foo(), "xx");
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            assertThat(e).hasMessageContaining("iMethods.threeArgumentMethod(12, foo, \"xx\")");
        }
    }

    @Test
    public void should_print_actual_and_wanted_in_line() {
        mock.varargs(1, 2);
        try {
            Mockito.verify(mock).varargs(1, 1000);
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            String wanted = "\n" + (("Argument(s) are different! Wanted:" + "\n") + "iMethods.varargs(1, 1000);");
            assertThat(e).hasMessageContaining(wanted);
            String actual = "\n" + (("Actual invocation has different arguments:" + "\n") + "iMethods.varargs(1, 2);");
            assertThat(e).hasMessageContaining(actual);
        }
    }

    @Test
    public void should_print_actual_and_wanted_in_multiple_lines() {
        mock.varargs("this is very long string", "this is another very long string");
        try {
            Mockito.verify(mock).varargs("x", "y", "z");
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            String wanted = "\n" + (((((((((("Argument(s) are different! Wanted:" + "\n") + "iMethods.varargs(") + "\n") + "    \"x\",") + "\n") + "    \"y\",") + "\n") + "    \"z\"") + "\n") + ");");
            assertThat(e).hasMessageContaining(wanted);
            String actual = "\n" + (((((((("Actual invocation has different arguments:" + "\n") + "iMethods.varargs(") + "\n") + "    \"this is very long string\",") + "\n") + "    \"this is another very long string\"") + "\n") + ");");
            assertThat(e).hasMessageContaining(actual);
        }
    }

    @Test
    public void should_print_actual_and_wanted_when_actual_method_name_and_wanted_method_name_are_the_same() {
        mock.simpleMethod();
        try {
            Mockito.verify(mock).simpleMethod(10);
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            assertThat(e).hasMessageContaining("simpleMethod(10)").hasMessageContaining("simpleMethod()");
        }
    }

    @Test
    public void should_print_actual_and_unverified_wanted_when_the_difference_is_about_arguments() {
        mock.twoArgumentMethod(1, 1);
        mock.twoArgumentMethod(2, 2);
        Mockito.verify(mock).twoArgumentMethod(1, 1);
        try {
            Mockito.verify(mock).twoArgumentMethod(2, 1000);
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            assertThat(e).hasMessageContaining("(2, 1000)").hasMessageContaining("(2, 2)");
        }
    }

    @Test
    public void should_print_first_unexpected_invocation() {
        mock.oneArg(true);
        mock.oneArg(false);
        mock.threeArgumentMethod(1, "2", "3");
        Mockito.verify(mock).oneArg(true);
        try {
            Mockito.verifyNoMoreInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
            String expectedMessage = "\n" + (("No interactions wanted here:" + "\n") + "-> at");
            assertThat(e).hasMessageContaining(expectedMessage);
            String expectedCause = (((("\n" + "But found this interaction on mock '") + (mock)) + "':") + "\n") + "-> at";
            assertThat(e).hasMessageContaining(expectedCause);
        }
    }

    @Test
    public void should_print_first_unexpected_invocation_when_verifying_zero_interactions() {
        mock.twoArgumentMethod(1, 2);
        mock.threeArgumentMethod(1, "2", "3");
        try {
            Mockito.verifyZeroInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
            String expected = "\n" + (("No interactions wanted here:" + "\n") + "-> at");
            assertThat(e).hasMessageContaining(expected);
            String expectedCause = (((("\n" + "But found this interaction on mock '") + (mock)) + "':") + "\n") + "-> at";
            assertThat(e).hasMessageContaining(expectedCause);
        }
    }

    @Test
    public void should_print_method_name_when_verifying_at_least_once() throws Exception {
        try {
            Mockito.verify(mock, Mockito.atLeastOnce()).twoArgumentMethod(1, 2);
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            assertThat(e).hasMessageContaining("twoArgumentMethod(1, 2)");
        }
    }

    @Test
    public void should_print_method_when_matcher_used() throws Exception {
        try {
            Mockito.verify(mock, Mockito.atLeastOnce()).twoArgumentMethod(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(100));
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            String expectedMessage = "\n" + ((((("Wanted but not invoked:" + "\n") + "iMethods.twoArgumentMethod(\n") + "    <any integer>,\n") + "    100\n") + ");");
            assertThat(e).hasMessageContaining(expectedMessage);
        }
    }

    @Test
    public void should_print_method_when_missing_invocation_with_array_matcher() {
        mock.oneArray(new boolean[]{ true, false, false });
        try {
            Mockito.verify(mock).oneArray(AdditionalMatchers.aryEq(new boolean[]{ false, false, false }));
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            assertThat(e).hasMessageContaining("[false, false, false]").hasMessageContaining("[true, false, false]");
        }
    }

    @Test
    public void should_print_method_when_missing_invocation_with_vararg_matcher() {
        mock.varargsString(10, "xxx", "yyy", "zzz");
        try {
            Mockito.verify(mock).varargsString(10, "111", "222", "333");
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            assertThat(e).hasMessageContaining("111").hasMessageContaining("\"xxx\"");
        }
    }

    @Test
    public void should_print_method_when_missing_invocation_with_matcher() {
        mock.simpleMethod("foo");
        try {
            Mockito.verify(mock).simpleMethod(ArgumentMatchers.matches("burrito from Exmouth"));
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            assertThat(e).hasMessageContaining("matches(\"burrito from Exmouth\")").hasMessageContaining("\"foo\"");
        }
    }

    @Test
    public void should_print_null_arguments() throws Exception {
        mock.simpleMethod(null, ((Integer) (null)));
        try {
            Mockito.verify(mock).simpleMethod("test");
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            assertThat(e).hasMessageContaining("simpleMethod(null, null);");
        }
    }

    @Test
    public void should_say_never_wanted_but_invoked() throws Exception {
        mock.simpleMethod(1);
        Mockito.verify(mock, Mockito.never()).simpleMethod(2);
        try {
            Mockito.verify(mock, Mockito.never()).simpleMethod(1);
            Assert.fail();
        } catch (NeverWantedButInvoked e) {
            assertThat(e).hasMessageContaining("Never wanted here:").hasMessageContaining("But invoked here:");
        }
    }

    @Test
    public void should_show_right_actual_method() throws Exception {
        mock.simpleMethod(9191);
        mock.simpleMethod("foo");
        try {
            Mockito.verify(mock).simpleMethod("bar");
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            assertThat(e).hasMessageContaining("bar").hasMessageContaining("foo");
        }
    }

    @Mock
    private IMethods iHavefunkyName;

    @Test
    public void should_print_field_name_when_annotations_used() throws Exception {
        iHavefunkyName.simpleMethod(10);
        try {
            Mockito.verify(iHavefunkyName).simpleMethod(20);
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            assertThat(e).hasMessageContaining("iHavefunkyName.simpleMethod(20)").hasMessageContaining("iHavefunkyName.simpleMethod(10)");
        }
    }

    @Test
    public void should_print_interactions_on_mock_when_ordinary_verification_fail() throws Exception {
        mock.otherMethod();
        mock.booleanReturningMethod();
        try {
            Mockito.verify(mock).simpleMethod();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            // assertContains("")
        }
    }

    @Mock
    private IMethods veeeeeeeeeeeeeeeeeeeeeeeerylongNameMock;

    @Test
    public void should_never_break_method_string_when_no_args_in_method() throws Exception {
        try {
            Mockito.verify(veeeeeeeeeeeeeeeeeeeeeeeerylongNameMock).simpleMethod();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            assertThat(e).hasMessageContaining("veeeeeeeeeeeeeeeeeeeeeeeerylongNameMock.simpleMethod()");
        }
    }

    @Test
    public void should_print_method_name_and_arguments_of_other_interactions_with_different_methods() throws Exception {
        try {
            mock.arrayMethod(new String[]{ "a", "b", "c" });
            mock.forByte(((byte) (25)));
            Mockito.verify(mock).threeArgumentMethod(12, new DescriptiveMessagesWhenVerificationFailsTest.Foo(), "xx");
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            assertThat(e).hasMessageContaining("iMethods.threeArgumentMethod(12, foo, \"xx\")").hasMessageContaining("iMethods.arrayMethod([\"a\", \"b\", \"c\"])").hasMessageContaining("iMethods.forByte((byte) 0x19)");
        }
    }

    public interface AnInterface {
        void foo(int i);
    }
}

