/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.internal.matchers.Any;
import org.mockito.internal.matchers.CapturingMatcher;
import org.mockito.internal.matchers.Equals;
import org.mockito.internal.matchers.NotNull;
import org.mockito.invocation.Invocation;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class InvocationMatcherTest extends TestBase {
    private InvocationMatcher simpleMethod;

    @Mock
    private IMethods mock;

    @Test
    public void should_be_a_citizen_of_hashes() throws Exception {
        Invocation invocation = new InvocationBuilder().toInvocation();
        Invocation invocationTwo = new InvocationBuilder().args("blah").toInvocation();
        Map<InvocationMatcher, String> map = new HashMap<InvocationMatcher, String>();
        map.put(new InvocationMatcher(invocation), "one");
        map.put(new InvocationMatcher(invocationTwo), "two");
        Assert.assertEquals(2, map.size());
    }

    @Test
    public void should_not_equal_if_number_of_arguments_differ() throws Exception {
        InvocationMatcher withOneArg = new InvocationMatcher(new InvocationBuilder().args("test").toInvocation());
        InvocationMatcher withTwoArgs = new InvocationMatcher(new InvocationBuilder().args("test", 100).toInvocation());
        Assert.assertFalse(withOneArg.equals(null));
        Assert.assertFalse(withOneArg.equals(withTwoArgs));
    }

    @Test
    public void should_to_string_with_matchers() throws Exception {
        ArgumentMatcher m = NotNull.NOT_NULL;
        InvocationMatcher notNull = new InvocationMatcher(new InvocationBuilder().toInvocation(), Arrays.asList(m));
        ArgumentMatcher mTwo = new Equals('x');
        InvocationMatcher equals = new InvocationMatcher(new InvocationBuilder().toInvocation(), Arrays.asList(mTwo));
        assertThat(notNull.toString()).contains("simpleMethod(notNull())");
        assertThat(equals.toString()).contains("simpleMethod('x')");
    }

    @Test
    public void should_know_if_is_similar_to() throws Exception {
        Invocation same = new InvocationBuilder().mock(mock).simpleMethod().toInvocation();
        Assert.assertTrue(simpleMethod.hasSimilarMethod(same));
        Invocation different = new InvocationBuilder().mock(mock).differentMethod().toInvocation();
        Assert.assertFalse(simpleMethod.hasSimilarMethod(different));
    }

    @Test
    public void should_not_be_similar_to_verified_invocation() throws Exception {
        Invocation verified = new InvocationBuilder().simpleMethod().verified().toInvocation();
        Assert.assertFalse(simpleMethod.hasSimilarMethod(verified));
    }

    @Test
    public void should_not_be_similar_if_mocks_are_different() throws Exception {
        Invocation onDifferentMock = new InvocationBuilder().simpleMethod().mock("different mock").toInvocation();
        Assert.assertFalse(simpleMethod.hasSimilarMethod(onDifferentMock));
    }

    @Test
    public void should_not_be_similar_if_is_overloaded_but_used_with_the_same_arg() throws Exception {
        Method method = IMethods.class.getMethod("simpleMethod", String.class);
        Method overloadedMethod = IMethods.class.getMethod("simpleMethod", Object.class);
        String sameArg = "test";
        InvocationMatcher invocation = new InvocationBuilder().method(method).arg(sameArg).toInvocationMatcher();
        Invocation overloadedInvocation = new InvocationBuilder().method(overloadedMethod).arg(sameArg).toInvocation();
        Assert.assertFalse(invocation.hasSimilarMethod(overloadedInvocation));
    }

    @Test
    public void should_be_similar_if_is_overloaded_but_used_with_different_arg() throws Exception {
        Method method = IMethods.class.getMethod("simpleMethod", String.class);
        Method overloadedMethod = IMethods.class.getMethod("simpleMethod", Object.class);
        InvocationMatcher invocation = new InvocationBuilder().mock(mock).method(method).arg("foo").toInvocationMatcher();
        Invocation overloadedInvocation = new InvocationBuilder().mock(mock).method(overloadedMethod).arg("bar").toInvocation();
        Assert.assertTrue(invocation.hasSimilarMethod(overloadedInvocation));
    }

    @Test
    public void should_capture_arguments_from_invocation() throws Exception {
        // given
        Invocation invocation = new InvocationBuilder().args("1", 100).toInvocation();
        CapturingMatcher capturingMatcher = new CapturingMatcher();
        InvocationMatcher invocationMatcher = new InvocationMatcher(invocation, ((List) (Arrays.asList(new Equals("1"), capturingMatcher))));
        // when
        invocationMatcher.captureArgumentsFrom(invocation);
        // then
        Assert.assertEquals(1, capturingMatcher.getAllValues().size());
        Assert.assertEquals(100, capturingMatcher.getLastValue());
    }

    @Test
    public void should_match_varargs_using_any_varargs() throws Exception {
        // given
        mock.varargs("1", "2");
        Invocation invocation = TestBase.getLastInvocation();
        InvocationMatcher invocationMatcher = new InvocationMatcher(invocation, ((List) (Arrays.asList(Any.ANY))));
        // when
        boolean match = invocationMatcher.matches(invocation);
        // then
        Assert.assertTrue(match);
    }

    @Test
    public void should_capture_varargs_as_vararg() throws Exception {
        // given
        mock.mixedVarargs(1, "a", "b");
        Invocation invocation = TestBase.getLastInvocation();
        CapturingMatcher m = new CapturingMatcher();
        InvocationMatcher invocationMatcher = new InvocationMatcher(invocation, Arrays.<ArgumentMatcher>asList(new Equals(1), m));
        // when
        invocationMatcher.captureArgumentsFrom(invocation);
        // then
        Assertions.assertThat(m.getAllValues()).containsExactly("a", "b");
    }

    // like using several time the captor in the vararg
    @Test
    public void should_capture_arguments_when_args_count_does_NOT_match() throws Exception {
        // given
        mock.varargs();
        Invocation invocation = TestBase.getLastInvocation();
        // when
        InvocationMatcher invocationMatcher = new InvocationMatcher(invocation, ((List) (Arrays.asList(Any.ANY))));
        // then
        invocationMatcher.captureArgumentsFrom(invocation);
    }

    @Test
    public void should_create_from_invocations() throws Exception {
        // given
        Invocation i = new InvocationBuilder().toInvocation();
        // when
        List<InvocationMatcher> out = InvocationMatcher.createFrom(Arrays.asList(i));
        // then
        Assert.assertEquals(1, out.size());
        Assert.assertEquals(i, out.get(0).getInvocation());
    }
}

