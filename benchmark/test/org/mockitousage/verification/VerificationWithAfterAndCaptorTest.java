/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockitousage.IMethods;
import org.mockitoutil.Stopwatch;


public class VerificationWithAfterAndCaptorTest {
    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    private IMethods mock;

    @Captor
    private ArgumentCaptor<Character> captor;

    private Stopwatch watch = Stopwatch.createNotStarted();

    /**
     * Test for issue #345.
     */
    @Test
    public void shouldReturnListOfArgumentsWithSameSizeAsGivenInAtMostVerification() {
        // given
        int n = 3;
        // when
        exerciseMockNTimes(n);
        watch.start();
        // then
        Mockito.verify(mock, Mockito.after(200).atMost(n)).oneArg(((char) (captor.capture())));
        watch.assertElapsedTimeIsMoreThan(200, TimeUnit.MILLISECONDS);
        assertThat(captor.getAllValues()).containsExactly('0', '1', '2');
    }
}

