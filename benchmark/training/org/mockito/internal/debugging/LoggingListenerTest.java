/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.debugging;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockitoutil.TestBase;


public class LoggingListenerTest extends TestBase {
    @Test
    public void may_not_have_any_information() {
        // given
        LoggingListener listener = new LoggingListener(true);
        // expect
        Assert.assertEquals("", listener.getStubbingInfo());
    }

    @Test
    public void informs_about_unused_stubs() {
        // given
        LoggingListener listener = new LoggingListener(false);
        // when
        listener.foundUnusedStub(TestBase.invocationAt("at com.FooTest:30"));
        listener.foundUnusedStub(TestBase.invocationAt("at com.FooTest:32"));
        // then
        Assert.assertEquals(("[Mockito] Additional stubbing information (see javadoc for StubbingInfo class):\n" + (((("[Mockito]\n" + "[Mockito] Unused stubbing (perhaps can be removed from the test?):\n") + "[Mockito]\n") + "[Mockito] 1. at com.FooTest:30\n") + "[Mockito] 2. at com.FooTest:32")), listener.getStubbingInfo());
    }

    @Test
    public void calculates_indexes_for_clean_output() {
        Assert.assertEquals(1, LoggingListener.indexOfNextPair(0));
        Assert.assertEquals(2, LoggingListener.indexOfNextPair(2));
        Assert.assertEquals(3, LoggingListener.indexOfNextPair(4));
        Assert.assertEquals(4, LoggingListener.indexOfNextPair(6));
    }

    @Test
    public void informs_about_unused_stubs_due_arg_mismatch() {
        // given
        LoggingListener listener = new LoggingListener(false);
        // when
        listener.foundStubCalledWithDifferentArgs(TestBase.invocationAt("at com.FooTest:20"), TestBase.invocationMatcherAt("at com.Foo:100"));
        listener.foundStubCalledWithDifferentArgs(TestBase.invocationAt("at com.FooTest:21"), TestBase.invocationMatcherAt("at com.Foo:121"));
        // then
        Assert.assertEquals(("[Mockito] Additional stubbing information (see javadoc for StubbingInfo class):\n" + (((((("[Mockito]\n" + "[Mockito] Argument mismatch between stubbing and actual invocation (is stubbing correct in the test?):\n") + "[Mockito]\n") + "[Mockito] 1. Stubbed at com.FooTest:20\n") + "[Mockito]    Invoked at com.Foo:100\n") + "[Mockito] 2. Stubbed at com.FooTest:21\n") + "[Mockito]    Invoked at com.Foo:121")), listener.getStubbingInfo());
    }

    @Test
    public void informs_about_various_kinds_of_stubs() {
        // given
        LoggingListener listener = new LoggingListener(true);
        // when
        listener.foundUnusedStub(TestBase.invocationAt("at com.FooTest:30"));
        listener.foundStubCalledWithDifferentArgs(TestBase.invocationAt("at com.FooTest:20"), TestBase.invocationMatcherAt("at com.Foo:100"));
        listener.foundUnstubbed(TestBase.invocationMatcherAt("at com.Foo:96"));
        // then
        Assert.assertEquals(("[Mockito] Additional stubbing information (see javadoc for StubbingInfo class):\n" + (((((((((((("[Mockito]\n" + "[Mockito] Argument mismatch between stubbing and actual invocation (is stubbing correct in the test?):\n") + "[Mockito]\n") + "[Mockito] 1. Stubbed at com.FooTest:20\n") + "[Mockito]    Invoked at com.Foo:100\n") + "[Mockito]\n") + "[Mockito] Unused stubbing (perhaps can be removed from the test?):\n") + "[Mockito]\n") + "[Mockito] 1. at com.FooTest:30\n") + "[Mockito]\n") + "[Mockito] Unstubbed method invocations (perhaps missing stubbing in the test?):\n") + "[Mockito]\n") + "[Mockito] 1. at com.Foo:96")), listener.getStubbingInfo());
    }

    @Test
    public void hides_unstubbed() {
        // given
        LoggingListener listener = new LoggingListener(false);
        // when
        listener.foundUnstubbed(new InvocationBuilder().toInvocationMatcher());
        // then
        Assert.assertEquals("", listener.getStubbingInfo());
    }

    @Test
    public void informs_about_unstubbed() {
        // given
        LoggingListener listener = new LoggingListener(true);
        // when
        listener.foundUnstubbed(TestBase.invocationMatcherAt("com.Foo:20"));
        // then
        Assert.assertEquals(("[Mockito] Additional stubbing information (see javadoc for StubbingInfo class):\n" + ((("[Mockito]\n" + "[Mockito] Unstubbed method invocations (perhaps missing stubbing in the test?):\n") + "[Mockito]\n") + "[Mockito] 1. com.Foo:20")), listener.getStubbingInfo());
    }
}

