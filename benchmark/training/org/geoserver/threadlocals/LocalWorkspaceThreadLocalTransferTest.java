/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.threadlocals;


import java.util.concurrent.ExecutionException;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.geoserver.ows.LocalWorkspace;
import org.junit.Assert;
import org.junit.Test;


public class LocalWorkspaceThreadLocalTransferTest extends AbstractThreadLocalTransferTest {
    @Test
    public void testRequest() throws InterruptedException, ExecutionException {
        // setup the state
        final WorkspaceInfo ws = new WorkspaceInfoImpl();
        LocalWorkspace.set(ws);
        // test it's transferred properly using the base class machinery
        testThreadLocalTransfer(new AbstractThreadLocalTransferTest.ThreadLocalTransferCallable(new LocalWorkspaceThreadLocalTransfer()) {
            @Override
            void assertThreadLocalCleaned() {
                Assert.assertNull(LocalWorkspace.get());
            }

            @Override
            void assertThreadLocalApplied() {
                Assert.assertSame(ws, LocalWorkspace.get());
            }
        });
    }
}

