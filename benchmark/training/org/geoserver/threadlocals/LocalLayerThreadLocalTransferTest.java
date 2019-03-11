/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.threadlocals;


import java.util.concurrent.ExecutionException;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.impl.LayerInfoImpl;
import org.geoserver.ows.LocalPublished;
import org.junit.Assert;
import org.junit.Test;


public class LocalLayerThreadLocalTransferTest extends AbstractThreadLocalTransferTest {
    @Test
    public void testRequest() throws InterruptedException, ExecutionException {
        // setup the state
        final LayerInfo layer = new LayerInfoImpl();
        LocalPublished.set(layer);
        // test it's transferred properly using the base class machinery
        testThreadLocalTransfer(new AbstractThreadLocalTransferTest.ThreadLocalTransferCallable(new LocalPublishedThreadLocalTransfer()) {
            @Override
            void assertThreadLocalCleaned() {
                Assert.assertNull(LocalPublished.get());
            }

            @Override
            void assertThreadLocalApplied() {
                Assert.assertSame(layer, LocalPublished.get());
            }
        });
    }
}

