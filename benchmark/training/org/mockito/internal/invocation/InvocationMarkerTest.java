/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.verification.InOrderContextImpl;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.MatchableInvocation;
import org.mockitoutil.TestBase;


public class InvocationMarkerTest extends TestBase {
    @Test
    public void shouldMarkInvocationAsVerified() {
        // given
        Invocation i = new InvocationBuilder().toInvocation();
        InvocationMatcher im = new InvocationBuilder().toInvocationMatcher();
        Assert.assertFalse(i.isVerified());
        // when
        InvocationMarker.markVerified(Arrays.asList(i), im);
        // then
        Assert.assertTrue(i.isVerified());
    }

    @Test
    public void shouldCaptureArguments() {
        // given
        Invocation i = new InvocationBuilder().toInvocation();
        final AtomicReference<Invocation> box = new AtomicReference<Invocation>();
        MatchableInvocation c = new InvocationMatcher(i) {
            public void captureArgumentsFrom(Invocation i) {
                box.set(i);
            }
        };
        // when
        InvocationMarker.markVerified(Arrays.asList(i), c);
        // then
        Assert.assertEquals(i, box.get());
    }

    @Test
    public void shouldMarkInvocationsAsVerifiedInOrder() {
        // given
        InOrderContextImpl context = new InOrderContextImpl();
        Invocation i = new InvocationBuilder().toInvocation();
        InvocationMatcher im = new InvocationBuilder().toInvocationMatcher();
        Assert.assertFalse(context.isVerified(i));
        Assert.assertFalse(i.isVerified());
        // when
        InvocationMarker.markVerifiedInOrder(Arrays.asList(i), im, context);
        // then
        Assert.assertTrue(context.isVerified(i));
        Assert.assertTrue(i.isVerified());
    }
}

