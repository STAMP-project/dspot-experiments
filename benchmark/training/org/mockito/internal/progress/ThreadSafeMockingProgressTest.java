/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.progress;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.DummyVerificationMode;
import org.mockitoutil.TestBase;


public class ThreadSafeMockingProgressTest extends TestBase {
    @Test
    public void shouldShareState() throws Exception {
        // given
        MockingProgress p = ThreadSafeMockingProgress.mockingProgress();
        p.verificationStarted(new DummyVerificationMode());
        // then
        p = ThreadSafeMockingProgress.mockingProgress();
        Assert.assertNotNull(p.pullVerificationMode());
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldKnowWhenVerificationHasStarted() throws Exception {
        // given
        Mockito.verify(Mockito.mock(List.class));
        MockingProgress p = ThreadSafeMockingProgress.mockingProgress();
        // then
        Assert.assertNotNull(p.pullVerificationMode());
    }
}

