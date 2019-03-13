/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.threadlocals;


import Dispatcher.REQUEST;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.LayerInfoImpl;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.geoserver.ows.LocalPublished;
import org.geoserver.ows.LocalWorkspace;
import org.geoserver.ows.Request;
import org.geoserver.security.AdminRequest;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class ThreadLocalsTransferTest extends GeoServerSystemTestSupport {
    protected ExecutorService executor;

    @Test
    public void testThreadLocalTransfer() throws InterruptedException, ExecutionException {
        final Request request = new Request();
        REQUEST.set(request);
        final LayerInfo layer = new LayerInfoImpl();
        LocalPublished.set(layer);
        final WorkspaceInfo ws = new WorkspaceInfoImpl();
        LocalWorkspace.set(ws);
        final Object myState = new Object();
        AdminRequest.start(myState);
        final Authentication auth = new UsernamePasswordAuthenticationToken("user", "password");
        SecurityContextHolder.getContext().setAuthentication(auth);
        final ThreadLocalsTransfer transfer = new ThreadLocalsTransfer();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                testApply();
                testCleanup();
                return null;
            }

            private void testApply() {
                transfer.apply();
                // check all thread locals have been applied to the current thread
                Assert.assertSame(request, REQUEST.get());
                Assert.assertSame(myState, AdminRequest.get());
                Assert.assertSame(layer, LocalPublished.get());
                Assert.assertSame(ws, LocalWorkspace.get());
                Assert.assertSame(auth, SecurityContextHolder.getContext().getAuthentication());
            }

            private void testCleanup() {
                transfer.cleanup();
                // check all thread locals have been cleaned up from the current
                // thread
                Assert.assertNull(REQUEST.get());
                Assert.assertNull(AdminRequest.get());
                Assert.assertNull(LocalPublished.get());
                Assert.assertNull(LocalWorkspace.get());
                Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
            }
        });
        future.get();
    }

    protected abstract static class ThreadLocalTransferCallable implements Callable<Void> {
        Thread originalThread;

        ThreadLocalTransfer transfer;

        Map<String, Object> storage = new HashMap<String, Object>();

        public ThreadLocalTransferCallable(ThreadLocalTransfer transfer) {
            this.originalThread = Thread.currentThread();
            this.transfer = transfer;
            this.transfer.collect(storage);
        }

        @Override
        public Void call() throws Exception {
            // this is the the main thread, we are actually running inside the thread pool
            Assert.assertNotEquals(originalThread, Thread.currentThread());
            // apply the thread local, check it has been applied correctly
            transfer.apply(storage);
            assertThreadLocalApplied();
            // clean up, check the therad local is now empty
            transfer.cleanup();
            assertThreadLocalCleaned();
            return null;
        }

        abstract void assertThreadLocalCleaned();

        abstract void assertThreadLocalApplied();
    }
}

