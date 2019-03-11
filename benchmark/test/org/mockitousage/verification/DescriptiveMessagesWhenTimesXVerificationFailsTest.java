/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.TooLittleActualInvocations;
import org.mockito.exceptions.verification.TooManyActualInvocations;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class DescriptiveMessagesWhenTimesXVerificationFailsTest extends TestBase {
    @Mock
    private LinkedList mock;

    @Test
    public void shouldVerifyActualNumberOfInvocationsSmallerThanWanted() throws Exception {
        mock.clear();
        mock.clear();
        mock.clear();
        Mockito.verify(mock, Mockito.times(3)).clear();
        try {
            Mockito.verify(mock, Mockito.times(100)).clear();
            Assert.fail();
        } catch (TooLittleActualInvocations e) {
            assertThat(e).hasMessageContaining("mock.clear();").hasMessageContaining("Wanted 100 times").hasMessageContaining("was 3");
        }
    }

    @Test
    public void shouldVerifyActualNumberOfInvocationsLargerThanWanted() throws Exception {
        mock.clear();
        mock.clear();
        mock.clear();
        mock.clear();
        Mockito.verify(mock, Mockito.times(4)).clear();
        try {
            Mockito.verify(mock, Mockito.times(1)).clear();
            Assert.fail();
        } catch (TooManyActualInvocations e) {
            assertThat(e).hasMessageContaining("mock.clear();").hasMessageContaining("Wanted 1 time").hasMessageContaining("was 4");
        }
    }
}

