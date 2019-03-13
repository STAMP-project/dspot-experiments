/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.notification;


import java.io.IOException;
import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.notification.common.NotificationConfiguration;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.wfs.TransactionPlugin;
import org.junit.Assert;
import org.junit.Test;


public class SystemTest extends GeoServerSystemTestSupport {
    @Test
    public void testCatalogNotifierIntialization() throws IOException {
        NotificationConfiguration cfg = null;
        int counter = 0;
        for (CatalogListener listener : getGeoServer().getCatalog().getListeners()) {
            if (listener instanceof INotificationCatalogListener) {
                counter++;
            }
        }
        Assert.assertEquals(1, counter);
    }

    @Test
    public void testTransactionNotifierIntialization() throws IOException {
        NotificationConfiguration cfg = null;
        int counter = 0;
        for (TransactionPlugin listener : GeoServerExtensions.extensions(TransactionPlugin.class)) {
            if (listener instanceof INotificationTransactionListener) {
                counter++;
            }
        }
        Assert.assertEquals(1, counter);
    }
}

