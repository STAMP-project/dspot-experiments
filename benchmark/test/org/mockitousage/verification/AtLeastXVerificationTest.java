/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockitoutil.TestBase;


public class AtLeastXVerificationTest extends TestBase {
    @Mock
    private List<String> mock;

    @Test
    public void shouldVerifyAtLeastXTimes() throws Exception {
        // when
        mock.clear();
        mock.clear();
        mock.clear();
        // then
        Mockito.verify(mock, Mockito.atLeast(2)).clear();
    }

    @Test
    public void shouldFailVerificationAtLeastXTimes() throws Exception {
        mock.add("one");
        Mockito.verify(mock, Mockito.atLeast(1)).add(ArgumentMatchers.anyString());
        try {
            Mockito.verify(mock, Mockito.atLeast(2)).add(ArgumentMatchers.anyString());
            Assert.fail();
        } catch (MockitoAssertionError e) {
        }
    }

    @Test
    public void shouldAllowAtLeastZeroForTheSakeOfVerifyNoMoreInteractionsSometimes() throws Exception {
        // when
        mock.add("one");
        mock.clear();
        // then
        Mockito.verify(mock, Mockito.atLeast(0)).add("one");
        Mockito.verify(mock, Mockito.atLeast(0)).clear();
        Mockito.verifyNoMoreInteractions(mock);
    }
}

