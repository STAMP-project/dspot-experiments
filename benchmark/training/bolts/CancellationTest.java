/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package bolts;


import java.util.concurrent.CancellationException;
import org.junit.Assert;
import org.junit.Test;


public class CancellationTest {
    @Test
    public void testTokenIsCancelled() {
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken token = cts.getToken();
        Assert.assertFalse(token.isCancellationRequested());
        Assert.assertFalse(cts.isCancellationRequested());
        cts.cancel();
        Assert.assertTrue(token.isCancellationRequested());
        Assert.assertTrue(cts.isCancellationRequested());
    }

    @Test
    public void testTokenIsCancelledAfterNoDelay() throws Exception {
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken token = cts.getToken();
        Assert.assertFalse(token.isCancellationRequested());
        cts.cancelAfter(0);
        Assert.assertTrue(token.isCancellationRequested());
        Assert.assertTrue(cts.isCancellationRequested());
    }

    @Test
    public void testTokenIsCancelledAfterDelay() throws Exception {
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken token = cts.getToken();
        Assert.assertFalse(token.isCancellationRequested());
        cts.cancelAfter(100);
        Assert.assertFalse(token.isCancellationRequested());
        Assert.assertFalse(cts.isCancellationRequested());
        Thread.sleep(150);
        Assert.assertTrue(token.isCancellationRequested());
        Assert.assertTrue(cts.isCancellationRequested());
    }

    @Test
    public void testTokenCancelAfterDelayCancellation() throws Exception {
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken token = cts.getToken();
        Assert.assertFalse(token.isCancellationRequested());
        cts.cancelAfter(100);
        Assert.assertFalse(token.isCancellationRequested());
        Assert.assertFalse(cts.isCancellationRequested());
        cts.cancelAfter((-1));
        Thread.sleep(150);
        Assert.assertFalse(token.isCancellationRequested());
        Assert.assertFalse(cts.isCancellationRequested());
    }

    @Test
    public void testTokenThrowsWhenCancelled() {
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken token = cts.getToken();
        try {
            token.throwIfCancellationRequested();
        } catch (CancellationException e) {
            Assert.fail((("Token has not been cancelled yet, " + (CancellationException.class.getSimpleName())) + " should not be thrown"));
        }
        cts.cancel();
        try {
            token.throwIfCancellationRequested();
            Assert.fail(((CancellationException.class.getSimpleName()) + " should be thrown"));
        } catch (CancellationException e) {
            // Do nothing
        }
    }

    @Test
    public void testTokenCallsRegisteredActionWhenCancelled() {
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken token = cts.getToken();
        final Capture<Object> result = new Capture();
        token.register(new Runnable() {
            @Override
            public void run() {
                result.set("Run!");
            }
        });
        Assert.assertNull(result.get());
        cts.cancel();
        Assert.assertNotNull(result.get());
    }

    @Test
    public void testCancelledTokenCallsRegisteredActionImmediately() {
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken token = cts.getToken();
        final Capture<Object> result = new Capture();
        cts.cancel();
        token.register(new Runnable() {
            @Override
            public void run() {
                result.set("Run!");
            }
        });
        Assert.assertNotNull(result.get());
    }

    @Test
    public void testTokenDoesNotCallUnregisteredAction() {
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken token = cts.getToken();
        final Capture<Object> result1 = new Capture();
        final Capture<Object> result2 = new Capture();
        CancellationTokenRegistration registration1 = token.register(new Runnable() {
            @Override
            public void run() {
                result1.set("Run!");
            }
        });
        token.register(new Runnable() {
            @Override
            public void run() {
                result2.set("Run!");
            }
        });
        registration1.close();
        cts.cancel();
        Assert.assertNull(result1.get());
        Assert.assertNotNull(result2.get());
    }

    @Test
    public void testCloseCancellationTokenSource() {
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken token = cts.getToken();
        token.register(new Runnable() {
            @Override
            public void run() {
                // Do nothing
            }
        });
        cts.close();
    }
}

