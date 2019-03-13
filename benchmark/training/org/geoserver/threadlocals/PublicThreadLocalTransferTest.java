/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.threadlocals;


import Dispatcher.REQUEST;
import java.util.concurrent.ExecutionException;
import org.geoserver.ows.Dispatcher;
import org.geoserver.ows.Request;
import org.junit.Assert;
import org.junit.Test;


public class PublicThreadLocalTransferTest extends AbstractThreadLocalTransferTest {
    @Test
    public void testRequest() throws InterruptedException, NoSuchFieldException, SecurityException, ExecutionException {
        // setup the state
        final Request request = new Request();
        REQUEST.set(request);
        // test it's transferred properly using the base class machinery
        testThreadLocalTransfer(new AbstractThreadLocalTransferTest.ThreadLocalTransferCallable(new PublicThreadLocalTransfer(Dispatcher.class, "REQUEST")) {
            @Override
            void assertThreadLocalCleaned() {
                Assert.assertNull(REQUEST.get());
            }

            @Override
            void assertThreadLocalApplied() {
                Assert.assertSame(request, REQUEST.get());
            }
        });
    }
}

