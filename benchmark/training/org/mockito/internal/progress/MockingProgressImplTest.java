/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.progress;


import java.util.LinkedHashSet;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.misusing.RedundantListenerException;
import org.mockito.internal.listeners.AutoCleanableListener;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.listeners.MockitoListener;
import org.mockito.verification.VerificationMode;
import org.mockitoutil.TestBase;


public class MockingProgressImplTest extends TestBase {
    private MockingProgress mockingProgress;

    @Test
    public void shouldStartVerificationAndPullVerificationMode() throws Exception {
        Assert.assertNull(mockingProgress.pullVerificationMode());
        VerificationMode mode = VerificationModeFactory.times(19);
        mockingProgress.verificationStarted(mode);
        Assert.assertSame(mode, mockingProgress.pullVerificationMode());
        Assert.assertNull(mockingProgress.pullVerificationMode());
    }

    @Test
    public void shouldCheckIfVerificationWasFinished() throws Exception {
        mockingProgress.verificationStarted(VerificationModeFactory.atLeastOnce());
        try {
            mockingProgress.verificationStarted(VerificationModeFactory.atLeastOnce());
            Assert.fail();
        } catch (MockitoException e) {
        }
    }

    @Test
    public void shouldNotifyListenerSafely() throws Exception {
        // when
        mockingProgress.addListener(null);
        // then no exception is thrown:
        mockingProgress.mockingStarted(null, null);
    }

    @Test
    public void should_not_allow_redundant_listeners() {
        MockitoListener listener1 = Mockito.mock(MockitoListener.class);
        final MockitoListener listener2 = Mockito.mock(MockitoListener.class);
        final Set<MockitoListener> listeners = new LinkedHashSet<MockitoListener>();
        // when
        MockingProgressImpl.addListener(listener1, listeners);
        // then
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() {
                MockingProgressImpl.addListener(listener2, listeners);
            }
        }).isInstanceOf(RedundantListenerException.class);
    }

    @Test
    public void should_clean_up_listeners_automatically() {
        MockitoListener someListener = Mockito.mock(MockitoListener.class);
        MockingProgressImplTest.MyListener cleanListener = Mockito.mock(MockingProgressImplTest.MyListener.class);
        MockingProgressImplTest.MyListener dirtyListener = Mockito.when(isListenerDirty()).thenReturn(true).getMock();
        Set<MockitoListener> listeners = new LinkedHashSet<MockitoListener>();
        // when
        MockingProgressImpl.addListener(someListener, listeners);
        MockingProgressImpl.addListener(dirtyListener, listeners);
        // then
        Assertions.assertThat(listeners).containsExactlyInAnyOrder(someListener, dirtyListener);
        // when
        MockingProgressImpl.addListener(cleanListener, listeners);
        // then dirty listener was removed automatically
        Assertions.assertThat(listeners).containsExactlyInAnyOrder(someListener, cleanListener);
    }

    interface MyListener extends AutoCleanableListener , MockitoListener {}
}

