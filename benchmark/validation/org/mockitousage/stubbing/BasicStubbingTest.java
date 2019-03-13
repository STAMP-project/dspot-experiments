/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.CannotVerifyStubOnlyMock;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class BasicStubbingTest extends TestBase {
    private IMethods mock;

    @Test
    public void should_evaluate_latest_stubbing_first() throws Exception {
        Mockito.when(mock.objectReturningMethod(ArgumentMatchers.isA(Integer.class))).thenReturn(100);
        Mockito.when(mock.objectReturningMethod(200)).thenReturn(200);
        Assert.assertEquals(200, mock.objectReturningMethod(200));
        Assert.assertEquals(100, mock.objectReturningMethod(666));
        Assert.assertEquals("default behavior should return null", null, mock.objectReturningMethod("blah"));
    }

    @Test
    public void should_stubbing_be_treated_as_interaction() throws Exception {
        Mockito.when(mock.booleanReturningMethod()).thenReturn(true);
        mock.booleanReturningMethod();
        try {
            Mockito.verifyNoMoreInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void should_allow_stubbing_to_string() throws Exception {
        IMethods mockTwo = Mockito.mock(IMethods.class);
        Mockito.when(mockTwo.toString()).thenReturn("test");
        assertThat(mock.toString()).contains("Mock for IMethods");
        assertThat(mockTwo.toString()).isEqualTo("test");
    }

    @Test
    public void should_stubbing_not_be_treated_as_interaction() {
        Mockito.when(mock.simpleMethod("one")).thenThrow(new RuntimeException());
        Mockito.doThrow(new RuntimeException()).when(mock).simpleMethod("two");
        Mockito.verifyZeroInteractions(mock);
    }

    @Test
    public void unfinished_stubbing_cleans_up_the_state() {
        Mockito.reset(mock);
        try {
            Mockito.when("").thenReturn("");
            Assert.fail();
        } catch (MissingMethodInvocationException e) {
        }
        // anything that can cause state validation
        Mockito.verifyZeroInteractions(mock);
    }

    @Test
    public void should_to_string_mock_name() {
        IMethods mock = Mockito.mock(IMethods.class, "mockie");
        IMethods mockTwo = Mockito.mock(IMethods.class);
        assertThat(mockTwo.toString()).contains("Mock for IMethods");
        Assert.assertEquals("mockie", ("" + mock));
    }

    class Foo {
        public final String toString() {
            return "foo";
        }
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void should_allow_mocking_when_to_string_is_final() throws Exception {
        Mockito.mock(BasicStubbingTest.Foo.class);
    }

    @Test
    public void test_stub_only_not_verifiable() throws Exception {
        IMethods localMock = Mockito.mock(IMethods.class, Mockito.withSettings().stubOnly());
        Mockito.when(localMock.objectReturningMethod(ArgumentMatchers.isA(Integer.class))).thenReturn(100);
        Mockito.when(localMock.objectReturningMethod(200)).thenReturn(200);
        Assert.assertEquals(200, localMock.objectReturningMethod(200));
        Assert.assertEquals(100, localMock.objectReturningMethod(666));
        Assert.assertEquals("default behavior should return null", null, localMock.objectReturningMethod("blah"));
        try {
            Mockito.verify(localMock, Mockito.atLeastOnce()).objectReturningMethod(ArgumentMatchers.eq(200));
            Assert.fail();
        } catch (CannotVerifyStubOnlyMock e) {
        }
    }

    @SuppressWarnings("MockitoUsage")
    @Test
    public void test_stub_only_not_verifiable_fail_fast() {
        IMethods localMock = Mockito.mock(IMethods.class, Mockito.withSettings().stubOnly());
        try {
            Mockito.verify(localMock);// throws exception before method invocation

            Assert.fail();
        } catch (CannotVerifyStubOnlyMock e) {
            Assert.assertEquals(("\n" + ("Argument \"iMethods\" passed to verify is a stubOnly() mock which cannot be verified.\n" + "If you intend to verify invocations on this mock, don't use stubOnly() in its MockSettings.")), e.getMessage());
        }
    }

    @Test
    public void test_stub_only_not_verifiable_verify_no_more_interactions() {
        IMethods localMock = Mockito.mock(IMethods.class, Mockito.withSettings().stubOnly());
        try {
            Mockito.verifyNoMoreInteractions(localMock);
            Assert.fail();
        } catch (CannotVerifyStubOnlyMock e) {
        }
    }

    @Test
    public void test_stub_only_not_verifiable_in_order() {
        IMethods localMock = Mockito.mock(IMethods.class, Mockito.withSettings().stubOnly());
        try {
            Mockito.inOrder(localMock);
            Assert.fail();
        } catch (CannotVerifyStubOnlyMock e) {
        }
    }
}

