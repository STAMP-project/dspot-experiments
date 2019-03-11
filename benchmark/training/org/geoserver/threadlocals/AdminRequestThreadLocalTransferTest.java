/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.threadlocals;


import java.util.concurrent.ExecutionException;
import org.geoserver.security.AdminRequest;
import org.junit.Assert;
import org.junit.Test;


public class AdminRequestThreadLocalTransferTest extends AbstractThreadLocalTransferTest {
    @Test
    public void testAdminRequest() throws InterruptedException, ExecutionException {
        // setup the state
        final Object myState = new Object();
        AdminRequest.start(myState);
        // test it's transferred properly using the base class machinery
        testThreadLocalTransfer(new AbstractThreadLocalTransferTest.ThreadLocalTransferCallable(new AdminRequestThreadLocalTransfer()) {
            @Override
            void assertThreadLocalCleaned() {
                Assert.assertNull(AdminRequest.get());
            }

            @Override
            void assertThreadLocalApplied() {
                Assert.assertSame(myState, AdminRequest.get());
            }
        });
    }
}

