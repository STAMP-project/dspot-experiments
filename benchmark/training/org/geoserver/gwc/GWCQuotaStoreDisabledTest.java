/**
 * (c) 2014 -2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc;


import java.io.File;
import junit.framework.Assert;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Test;


public class GWCQuotaStoreDisabledTest extends GeoServerSystemTestSupport {
    @Test
    public void testQuotaDisabled() throws Exception {
        // the provider returns no quota store
        ConfigurableQuotaStoreProvider provider = GeoServerExtensions.bean(ConfigurableQuotaStoreProvider.class);
        Assert.assertNull(provider.getQuotaStore());
        // check there is no quota database
        GeoServerDataDirectory dd = GeoServerExtensions.bean(GeoServerDataDirectory.class);
        File gwc = dd.findOrCreateDir("gwc");
        File h2QuotaStore = new File("diskquota_page_store_h2");
        Assert.assertFalse(h2QuotaStore.exists());
    }
}

