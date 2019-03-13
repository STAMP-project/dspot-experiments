/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.quality.Strictness;
import org.mockitousage.IMethods;
import org.mockitousage.strictness.ProductionCode;
import org.mockitoutil.ThrowableAssert;


public class StrictStubbingTest {
    @Mock
    IMethods mock;

    MockitoSession mockito = Mockito.mockitoSession().initMocks(this).strictness(Strictness.STRICT_STUBS).startMocking();

    @Test
    public void no_interactions() throws Throwable {
        // expect no exception
        mockito.finishMocking();
    }

    @Test
    public void few_interactions() throws Throwable {
        mock.simpleMethod(100);
        mock.otherMethod();
    }

    @Test
    public void few_verified_interactions() throws Throwable {
        // when
        mock.simpleMethod(100);
        mock.otherMethod();
        // and
        Mockito.verify(mock).simpleMethod(100);
        Mockito.verify(mock).otherMethod();
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void stubbed_method_is_implicitly_verified() throws Throwable {
        // when
        BDDMockito.given(mock.simpleMethod(100)).willReturn("100");
        mock.simpleMethod(100);
        // no exceptions:
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void unused_stubbed_is_not_implicitly_verified() throws Throwable {
        // when
        BDDMockito.given(mock.simpleMethod(100)).willReturn("100");
        mock.simpleMethod(100);// <- implicitly verified

        mock.simpleMethod(200);// <- unverified

        // expect
        ThrowableAssert.assertThat(new Runnable() {
            public void run() {
                Mockito.verifyNoMoreInteractions(mock);
            }
        }).throwsException(NoInteractionsWanted.class);
    }

    @Test
    public void stubbing_argument_mismatch() throws Throwable {
        // when
        BDDMockito.given(mock.simpleMethod(100)).willReturn("100");
        // stubbing argument mismatch is detected
        ThrowableAssert.assertThat(new Runnable() {
            public void run() {
                ProductionCode.simpleMethod(mock, 200);
            }
        }).throwsException(PotentialStubbingProblem.class);
    }

    @Test
    public void unused_stubbing() throws Throwable {
        // when
        BDDMockito.given(mock.simpleMethod(100)).willReturn("100");
        // unused stubbing is reported
        ThrowableAssert.assertThat(new Runnable() {
            public void run() {
                mockito.finishMocking();
            }
        }).throwsException(UnnecessaryStubbingException.class);
    }
}

