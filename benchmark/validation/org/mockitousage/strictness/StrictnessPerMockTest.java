/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.strictness;


import junit.framework.TestCase;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.mock.MockCreationSettings;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


// TODO 792 also move other Strictness tests to this package (unless they already have good package)
public class StrictnessPerMockTest {
    MockitoSession mockito;

    @Mock
    IMethods strictStubsMock;

    IMethods lenientMock;

    @Test
    public void knows_if_mock_is_lenient() {
        TestCase.assertTrue(Mockito.mockingDetails(lenientMock).getMockCreationSettings().isLenient());
        TestCase.assertFalse(Mockito.mockingDetails(strictStubsMock).getMockCreationSettings().isLenient());
    }

    @Test
    public void potential_stubbing_problem() {
        // when
        BDDMockito.given(lenientMock.simpleMethod(100)).willReturn("100");
        BDDMockito.given(strictStubsMock.simpleMethod(100)).willReturn("100");
        // then on lenient mock (created by hand), we can call the stubbed method with different arg:
        lenientMock.simpleMethod(200);
        // and on strict stub mock (created by session), we cannot call stubbed method with different arg:
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() throws Throwable {
                ProductionCode.simpleMethod(strictStubsMock, 200);
            }
        }).isInstanceOf(PotentialStubbingProblem.class);
    }

    @Test
    public void unnecessary_stubbing() {
        // when
        BDDMockito.given(lenientMock.simpleMethod(100)).willReturn("100");
        BDDMockito.given(strictStubsMock.simpleMethod(100)).willReturn("100");
        // then unnecessary stubbing flags method only on the strict stub mock:
        // good enough to prove that we're flagging just one unnecessary stubbing:
        // TODO 792: let's make UnnecessaryStubbingException exception contain the Invocation instance
        // so that we can write clean assertion rather than depending on string
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                mockito.finishMocking();
            }
        }).isInstanceOf(UnnecessaryStubbingException.class).hasMessageContaining("1. -> ").isNot(TestBase.hasMessageContaining("2. ->"));
    }

    @Test
    public void verify_no_more_invocations() {
        // when
        BDDMockito.given(lenientMock.simpleMethod(100)).willReturn("100");
        BDDMockito.given(strictStubsMock.simpleMethod(100)).willReturn("100");
        // and:
        strictStubsMock.simpleMethod(100);
        lenientMock.simpleMethod(100);
        // then 'verifyNoMoreInteractions' ignores strict stub (implicitly verified) but flags the lenient mock
        // TODO 792: let's make NoInteractionsWanted exception contain the Invocation instances
        // so that we can write clean assertion rather than depending on string
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                Mockito.verifyNoMoreInteractions(strictStubsMock, lenientMock);
            }
        }).isInstanceOf(NoInteractionsWanted.class).hasMessageContaining("But found this interaction on mock 'iMethods'").hasMessageContaining("Actually, above is the only interaction with this mock");
    }
}

