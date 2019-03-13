/**
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.debugging;


import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.listeners.StubbingLookupEvent;
import org.mockito.listeners.StubbingLookupListener;
import org.mockito.mock.MockCreationSettings;
import org.mockitousage.IMethods;
import org.mockitoutil.ConcurrentTesting;
import org.mockitoutil.TestBase;


public class StubbingLookupListenerCallbackTest extends TestBase {
    StubbingLookupListener listener = Mockito.mock(StubbingLookupListener.class);

    StubbingLookupListener listener2 = Mockito.mock(StubbingLookupListener.class);

    Foo mock = Mockito.mock(Foo.class, Mockito.withSettings().stubbingLookupListeners(listener));

    @Test
    public void should_call_listener_when_mock_return_normally_with_stubbed_answer() {
        // given
        Mockito.doReturn("coke").when(mock).giveMeSomeString("soda");
        Mockito.doReturn("java").when(mock).giveMeSomeString("coffee");
        // when
        mock.giveMeSomeString("soda");
        // then
        Mockito.verify(listener).onStubbingLookup(ArgumentMatchers.argThat(new ArgumentMatcher<StubbingLookupEvent>() {
            @Override
            public boolean matches(StubbingLookupEvent argument) {
                Assert.assertEquals("soda", argument.getInvocation().getArgument(0));
                Assert.assertEquals("mock", argument.getMockSettings().getMockName().toString());
                Assert.assertEquals(2, argument.getAllStubbings().size());
                Assert.assertNotNull(argument.getStubbingFound());
                return true;
            }
        }));
    }

    @Test
    public void should_call_listener_when_mock_return_normally_with_default_answer() {
        // given
        Mockito.doReturn("java").when(mock).giveMeSomeString("coffee");
        // when
        mock.giveMeSomeString("soda");
        // then
        Mockito.verify(listener).onStubbingLookup(ArgumentMatchers.argThat(new ArgumentMatcher<StubbingLookupEvent>() {
            @Override
            public boolean matches(StubbingLookupEvent argument) {
                Assert.assertEquals("soda", argument.getInvocation().getArgument(0));
                Assert.assertEquals("mock", argument.getMockSettings().getMockName().toString());
                Assert.assertEquals(1, argument.getAllStubbings().size());
                Assert.assertNull(argument.getStubbingFound());
                return true;
            }
        }));
    }

    @Test
    public void should_not_call_listener_when_mock_is_not_called() {
        // when stubbing is recorded
        Mockito.doReturn("java").when(mock).giveMeSomeString("coffee");
        // then
        Mockito.verifyZeroInteractions(listener);
    }

    @Test
    public void should_allow_same_listener() {
        // given
        Foo mock = Mockito.mock(Foo.class, Mockito.withSettings().stubbingLookupListeners(listener, listener));
        // when
        mock.giveMeSomeString("tea");
        mock.giveMeSomeString("coke");
        // then each listener was notified 2 times (notified 4 times in total)
        Mockito.verify(listener, Mockito.times(4)).onStubbingLookup(ArgumentMatchers.any(StubbingLookupEvent.class));
    }

    @Test
    public void should_call_all_listeners_in_order() {
        // given
        Foo mock = Mockito.mock(Foo.class, Mockito.withSettings().stubbingLookupListeners(listener, listener2));
        Mockito.doReturn("sprite").when(mock).giveMeSomeString("soda");
        // when
        mock.giveMeSomeString("soda");
        // then
        InOrder inOrder = Mockito.inOrder(listener, listener2);
        inOrder.verify(listener).onStubbingLookup(ArgumentMatchers.any(StubbingLookupEvent.class));
        inOrder.verify(listener2).onStubbingLookup(ArgumentMatchers.any(StubbingLookupEvent.class));
    }

    @Test
    public void should_call_all_listeners_when_mock_throws_exception() {
        // given
        Foo mock = Mockito.mock(Foo.class, Mockito.withSettings().stubbingLookupListeners(listener, listener2));
        Mockito.doThrow(new StubbingLookupListenerCallbackTest.NoWater()).when(mock).giveMeSomeString("tea");
        // when
        try {
            mock.giveMeSomeString("tea");
            Assert.fail();
        } catch (StubbingLookupListenerCallbackTest.NoWater e) {
            // then
            Mockito.verify(listener).onStubbingLookup(ArgumentMatchers.any(StubbingLookupEvent.class));
            Mockito.verify(listener2).onStubbingLookup(ArgumentMatchers.any(StubbingLookupEvent.class));
        }
    }

    @Test
    public void should_delete_listener() {
        // given
        Foo mock = Mockito.mock(Foo.class, Mockito.withSettings().stubbingLookupListeners(listener, listener2));
        // when
        mock.doSomething("1");
        Mockito.mockingDetails(mock).getMockCreationSettings().getStubbingLookupListeners().remove(listener2);
        mock.doSomething("2");
        // then
        Mockito.verify(listener, Mockito.times(2)).onStubbingLookup(ArgumentMatchers.any(StubbingLookupEvent.class));
        Mockito.verify(listener2, Mockito.times(1)).onStubbingLookup(ArgumentMatchers.any(StubbingLookupEvent.class));
    }

    @Test
    public void should_clear_listeners() {
        // given
        Foo mock = Mockito.mock(Foo.class, Mockito.withSettings().stubbingLookupListeners(listener, listener2));
        // when
        Mockito.mockingDetails(mock).getMockCreationSettings().getStubbingLookupListeners().clear();
        mock.doSomething("foo");
        // then
        Mockito.verifyZeroInteractions(listener, listener2);
    }

    @Test
    public void add_listeners_concurrently_sanity_check() throws Exception {
        // given
        final IMethods mock = Mockito.mock(IMethods.class);
        final MockCreationSettings<?> settings = Mockito.mockingDetails(mock).getMockCreationSettings();
        List<Runnable> runnables = new LinkedList<Runnable>();
        for (int i = 0; i < 50; i++) {
            runnables.add(new Runnable() {
                public void run() {
                    StubbingLookupListener listener1 = Mockito.mock(StubbingLookupListener.class);
                    StubbingLookupListener listener2 = Mockito.mock(StubbingLookupListener.class);
                    settings.getStubbingLookupListeners().add(listener1);
                    settings.getStubbingLookupListeners().add(listener2);
                    settings.getStubbingLookupListeners().remove(listener1);
                }
            });
        }
        // when
        ConcurrentTesting.concurrently(runnables.toArray(new Runnable[runnables.size()]));
        // then
        // This assertion may be flaky. If it is let's fix it or remove the test. For now, I'm keeping the test.
        Assert.assertEquals(50, settings.getStubbingLookupListeners().size());
    }

    private static class NoWater extends RuntimeException {}
}

