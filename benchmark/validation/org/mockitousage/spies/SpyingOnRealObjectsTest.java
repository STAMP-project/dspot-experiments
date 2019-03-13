/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.spies;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.TooLittleActualInvocations;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockitoutil.TestBase;


public class SpyingOnRealObjectsTest extends TestBase {
    List<String> list = new LinkedList<String>();

    List<String> spy = Mockito.spy(list);

    @Test
    public void shouldVerify() {
        spy.add("one");
        spy.add("two");
        Assert.assertEquals("one", spy.get(0));
        Assert.assertEquals("two", spy.get(1));
        Mockito.verify(spy).add("one");
        Mockito.verify(spy).add("two");
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldBeAbleToMockObjectBecauseWhyNot() {
        Mockito.spy(new Object());
    }

    @Test
    public void shouldStub() {
        spy.add("one");
        Mockito.when(spy.get(0)).thenReturn("1").thenReturn("1 again");
        Assert.assertEquals("1", spy.get(0));
        Assert.assertEquals("1 again", spy.get(0));
        Assert.assertEquals("one", spy.iterator().next());
        Assert.assertEquals(1, spy.size());
    }

    @Test
    public void shouldAllowOverridingStubs() {
        Mockito.when(spy.contains(ArgumentMatchers.anyObject())).thenReturn(true);
        Mockito.when(spy.contains("foo")).thenReturn(false);
        Assert.assertTrue(spy.contains("bar"));
        Assert.assertFalse(spy.contains("foo"));
    }

    @Test
    public void shouldStubVoid() {
        Mockito.doNothing().doThrow(new RuntimeException()).when(spy).clear();
        spy.add("one");
        spy.clear();
        try {
            spy.clear();
            Assert.fail();
        } catch (RuntimeException e) {
        }
        Assert.assertEquals(1, spy.size());
    }

    @Test
    public void shouldStubWithDoReturnAndVerify() {
        Mockito.doReturn("foo").doReturn("bar").when(spy).get(0);
        Assert.assertEquals("foo", spy.get(0));
        Assert.assertEquals("bar", spy.get(0));
        Mockito.verify(spy, Mockito.times(2)).get(0);
        Mockito.verifyNoMoreInteractions(spy);
    }

    @Test
    public void shouldVerifyInOrder() {
        spy.add("one");
        spy.add("two");
        InOrder inOrder = Mockito.inOrder(spy);
        inOrder.verify(spy).add("one");
        inOrder.verify(spy).add("two");
        Mockito.verifyNoMoreInteractions(spy);
    }

    @Test
    public void shouldVerifyInOrderAndFail() {
        spy.add("one");
        spy.add("two");
        InOrder inOrder = Mockito.inOrder(spy);
        inOrder.verify(spy).add("two");
        try {
            inOrder.verify(spy).add("one");
            Assert.fail();
        } catch (VerificationInOrderFailure f) {
        }
    }

    @Test
    public void shouldVerifyNumberOfTimes() {
        spy.add("one");
        spy.add("one");
        Mockito.verify(spy, Mockito.times(2)).add("one");
        Mockito.verifyNoMoreInteractions(spy);
    }

    @Test
    public void shouldVerifyNumberOfTimesAndFail() {
        spy.add("one");
        spy.add("one");
        try {
            Mockito.verify(spy, Mockito.times(3)).add("one");
            Assert.fail();
        } catch (TooLittleActualInvocations e) {
        }
    }

    @Test
    public void shouldVerifyNoMoreInteractionsAndFail() {
        spy.add("one");
        spy.add("two");
        Mockito.verify(spy).add("one");
        try {
            Mockito.verifyNoMoreInteractions(spy);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void shouldToString() {
        spy.add("foo");
        Assert.assertEquals("[foo]", spy.toString());
    }

    interface Foo {
        String print();
    }

    @Test
    public void shouldAllowSpyingAnonymousClasses() {
        // when
        SpyingOnRealObjectsTest.Foo spy = Mockito.spy(new SpyingOnRealObjectsTest.Foo() {
            public String print() {
                return "foo";
            }
        });
        // then
        Assert.assertEquals("foo", spy.print());
    }

    @Test
    public void shouldSayNiceMessageWhenSpyingOnPrivateClass() throws Exception {
        List<String> real = Arrays.asList("first", "second");
        try {
            List<String> spy = Mockito.spy(real);
            Assume.assumeTrue("Using inline mocks, it is possible to spy on private types", ((spy.getClass()) != (real.getClass())));
            Assert.fail();
        } catch (MockitoException e) {
            Assert.assertThat(e).hasMessageContaining("Most likely it is due to mocking a private class that is not visible to Mockito");
        }
    }
}

