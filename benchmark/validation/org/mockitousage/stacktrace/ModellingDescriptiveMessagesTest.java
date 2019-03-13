/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stacktrace;


import java.util.LinkedList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ModellingDescriptiveMessagesTest extends TestBase {
    @Mock
    private IMethods mock;

    @SuppressWarnings({ "MockitoUsage", "CheckReturnValue" })
    @Test
    public void makeSureStateIsValidatedInTheVeryFirstTestThanksToTheRunner() {
        // mess up the state:
        Mockito.verify(mock);
    }

    @Test
    public void shouldSayWantedButNotInvoked() {
        Mockito.verify(mock).otherMethod();
    }

    @Test
    public void shouldPointOutInteractionsOnMockWhenOrdinaryVerificationFails() {
        mock.otherMethod();
        mock.booleanObjectReturningMethod();
        Mockito.verify(mock).simpleMethod();
    }

    @Test
    public void shouldShowActualAndExpected() {
        mock.simpleMethod("blah");
        Mockito.verify(mock).simpleMethod();
    }

    @Test
    public void shouldSayTooLittleInvocations() {
        mock.simpleMethod();
        Mockito.verify(mock, Mockito.times(2)).simpleMethod();
    }

    @Test
    public void shouldSayTooManyInvocations() {
        mock.simpleMethod();
        mock.simpleMethod();
        Mockito.verify(mock, Mockito.times(1)).simpleMethod();
    }

    @Test
    public void shouldSayWantedButNotInvokedInOrder() {
        mock.simpleMethod();
        mock.otherMethod();
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock).otherMethod();
        inOrder.verify(mock).simpleMethod();
    }

    @Test
    public void shouldSayTooLittleInvocationsInOrder() {
        mock.simpleMethod();
        mock.otherMethod();
        mock.otherMethod();
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock).simpleMethod();
        inOrder.verify(mock, Mockito.times(3)).otherMethod();
    }

    @Test
    public void shouldSayTooManyInvocationsInOrder() {
        mock.otherMethod();
        mock.otherMethod();
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock, Mockito.times(1)).otherMethod();
    }

    @Test
    public void shouldSayNeverWantedButInvokedHere() {
        mock.otherMethod();
        Mockito.verify(mock, Mockito.never()).otherMethod();
    }

    @Test
    public void shouldSayTooLittleInvocationsInAtLeastModeInOrder() {
        mock.simpleMethod();
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock, Mockito.atLeast(2)).simpleMethod();
    }

    @Test
    public void shouldSayTooLittleInvocationsInAtLeastMode() {
        mock.simpleMethod();
        Mockito.verify(mock, Mockito.atLeast(2)).simpleMethod();
    }

    @Test
    public void shouldSayNoMoreInteractions() {
        mock.simpleMethod();
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void shouldSayUnstubbedMethodWasInvokedHere() {
        mock = Mockito.mock(IMethods.class, Mockito.RETURNS_SMART_NULLS);
        IMethods m = mock.iMethodsReturningMethod();
        m.simpleMethod();
    }

    @SuppressWarnings({ "MockitoUsage", "CheckReturnValue" })
    @Test
    public void shouldPointOutUnfinishedStubbing() {
        Mockito.when(mock.simpleMethod());
        Mockito.verify(mock).simpleMethod();
    }

    @Test
    public void shouldMentionFinalAndObjectMethodsWhenMissingMockCall() {
        Mockito.when("".equals(null)).thenReturn(false);
    }

    @Test
    public void shouldMentionFinalAndObjectMethodsWhenVerifying() {
        Mockito.verify(mock).equals(null);
        Mockito.verify(mock).simpleMethod();
    }

    @Test
    public void shouldMentionFinalAndObjectMethodsWhenMisplacedArgumentMatcher() {
        Mockito.when(mock.equals(ArgumentMatchers.anyObject())).thenReturn(false);
    }

    @Test
    public void shouldShowExampleOfCorrectArgumentCapturing() {
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        argument.capture();
        argument.getValue();
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldScreamWhenNullPassedInsteadOfAnInterface() {
        Mockito.mock(IMethods.class, Mockito.withSettings().extraInterfaces(List.class, null));
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldScreamWhenNonInterfacePassed() {
        Mockito.mock(IMethods.class, Mockito.withSettings().extraInterfaces(LinkedList.class));
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldScreamWhenExtraIsTheSame() {
        Mockito.mock(IMethods.class, Mockito.withSettings().extraInterfaces(IMethods.class));
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldScreamWhenExtraInterfacesEmpty() {
        Mockito.mock(IMethods.class, Mockito.withSettings().extraInterfaces());
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldScreamWhenExtraInterfacesIsANullArray() {
        Mockito.mock(IMethods.class, Mockito.withSettings().extraInterfaces(((Class<?>[]) (null))));
    }

    @Test
    public void shouldMentionSpiesWhenVoidMethodIsToldToReturnValue() {
        List list = Mockito.mock(List.class);
        Mockito.doReturn("foo").when(list).clear();
    }
}

