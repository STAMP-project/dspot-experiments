/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.misuse;


import java.util.Observer;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockito.exceptions.misusing.UnfinishedVerificationException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class DetectingMisusedMatchersTest extends TestBase {
    class WithFinal {
        final Object finalMethod(Object object) {
            return null;
        }
    }

    @Mock
    private DetectingMisusedMatchersTest.WithFinal withFinal;

    @Test
    public void should_fail_fast_when_argument_matchers_are_abused() {
        misplaced_anyObject_argument_matcher();
        try {
            Mockito.mock(IMethods.class);
            Assert.fail();
        } catch (InvalidUseOfMatchersException e) {
            assertThat(e).hasMessageContaining("Misplaced or misused argument matcher");
        }
    }

    @Test
    public void should_report_argument_locations_when_argument_matchers_misused() {
        try {
            Observer observer = Mockito.mock(Observer.class);
            misplaced_anyInt_argument_matcher();
            misplaced_anyObject_argument_matcher();
            misplaced_anyBoolean_argument_matcher();
            observer.update(null, null);
            Mockito.validateMockitoUsage();
            Assert.fail();
        } catch (InvalidUseOfMatchersException e) {
            assertThat(e).hasMessageContaining("DetectingMisusedMatchersTest.misplaced_anyInt_argument_matcher").hasMessageContaining("DetectingMisusedMatchersTest.misplaced_anyObject_argument_matcher").hasMessageContaining("DetectingMisusedMatchersTest.misplaced_anyBoolean_argument_matcher");
        }
    }

    @SuppressWarnings({ "MockitoUsage", "CheckReturnValue" })
    @Test
    public void shouldSayUnfinishedVerificationButNotInvalidUseOfMatchers() {
        Assume.assumeTrue("Does not apply for inline mocks", ((withFinal.getClass()) != (DetectingMisusedMatchersTest.WithFinal.class)));
        Mockito.verify(withFinal).finalMethod(ArgumentMatchers.anyObject());
        try {
            Mockito.verify(withFinal);
            Assert.fail();
        } catch (UnfinishedVerificationException e) {
        }
    }
}

