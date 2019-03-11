/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.basicapi;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.exceptions.misusing.UnfinishedVerificationException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class ResetTest extends TestBase {
    @Mock
    private IMethods mock;

    @Mock
    private IMethods mockTwo;

    @Test
    public void shouldResetOngoingStubbingSoThatMoreMeaningfulExceptionsAreRaised() {
        mock.booleanReturningMethod();
        Mockito.reset(mock);
        try {
            Mockito.when(null).thenReturn("anything");
            Assert.fail();
        } catch (MissingMethodInvocationException e) {
        }
    }

    @Test(expected = NotAMockException.class)
    public void resettingNonMockIsSafe() {
        Mockito.reset("");
    }

    @Test(expected = NotAMockException.class)
    public void resettingNullIsSafe() {
        Mockito.reset(new Object[]{ null });
    }

    @Test
    public void shouldRemoveAllStubbing() throws Exception {
        Mockito.when(mock.objectReturningMethod(ArgumentMatchers.isA(Integer.class))).thenReturn(100);
        Mockito.when(mock.objectReturningMethod(200)).thenReturn(200);
        Mockito.reset(mock);
        Assert.assertNull(mock.objectReturningMethod(200));
        Assert.assertEquals("default behavior should return null", null, mock.objectReturningMethod("blah"));
    }

    @Test
    public void shouldRemoveAllInteractions() throws Exception {
        mock.simpleMethod(1);
        Mockito.reset(mock);
        Mockito.verifyZeroInteractions(mock);
    }

    @Test
    public void shouldRemoveStubbingToString() throws Exception {
        IMethods mockTwo = Mockito.mock(IMethods.class);
        Mockito.when(mockTwo.toString()).thenReturn("test");
        Mockito.reset(mockTwo);
        Assert.assertThat(mockTwo.toString()).contains("Mock for IMethods");
    }

    @Test
    public void shouldStubbingNotBeTreatedAsInteraction() {
        Mockito.when(mock.simpleMethod("one")).thenThrow(new RuntimeException());
        Mockito.doThrow(new RuntimeException()).when(mock).simpleMethod("two");
        Mockito.reset(mock);
        Mockito.verifyZeroInteractions(mock);
    }

    @Test
    public void shouldNotAffectMockName() {
        IMethods mock = Mockito.mock(IMethods.class, "mockie");
        IMethods mockTwo = Mockito.mock(IMethods.class);
        Mockito.reset(mock);
        Assert.assertThat(mockTwo.toString()).contains("Mock for IMethods");
        Assert.assertEquals("mockie", ("" + mock));
    }

    @Test
    public void shouldResetMultipleMocks() {
        mock.simpleMethod();
        mockTwo.simpleMethod();
        Mockito.reset(mock, mockTwo);
        Mockito.verifyNoMoreInteractions(mock, mockTwo);
    }

    @SuppressWarnings({ "MockitoUsage", "CheckReturnValue" })
    @Test
    public void shouldValidateStateWhenResetting() {
        // invalid verify:
        Mockito.verify(mock);
        try {
            Mockito.reset(mockTwo);
            Assert.fail();
        } catch (UnfinishedVerificationException e) {
        }
    }

    @Test
    public void shouldMaintainPreviousDefaultAnswer() {
        // given
        mock = Mockito.mock(IMethods.class, Mockito.RETURNS_MOCKS);
        // when
        Mockito.reset(mock);
        // then
        Assert.assertNotNull(mock.iMethodsReturningMethod());
    }
}

