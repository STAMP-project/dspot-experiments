/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.layer;


import CachedLayerProvider.QUOTA_USAGE;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.geoserver.gwc.GWC;
import org.geoserver.test.GeoServerTestSupport;
import org.geowebcache.config.ConfigurationException;
import org.geowebcache.diskquota.DiskQuotaConfig;
import org.geowebcache.layer.TileLayer;
import org.junit.Test;
import org.mockito.Mockito;


public class CachedLayerProviderTest extends GeoServerTestSupport {
    @Test
    public void testQuotaEnabled() throws IOException, InterruptedException, ConfigurationException {
        GWC gwc = GWC.get();
        DiskQuotaConfig config = gwc.getDiskQuotaConfig();
        config.setEnabled(true);
        gwc.saveDiskQuotaConfig(config, null);
        CachedLayerProvider provider = new CachedLayerProvider();
        List<TileLayer> layers = provider.getItems();
        for (TileLayer tileLayer : layers) {
            // we are returning the values from the quota subsystem
            assertNotNull(QUOTA_USAGE.getPropertyValue(tileLayer));
        }
    }

    @Test
    public void testQuotaDisabled() throws IOException, InterruptedException, ConfigurationException {
        GWC gwc = GWC.get();
        DiskQuotaConfig config = gwc.getDiskQuotaConfig();
        config.setEnabled(false);
        gwc.saveDiskQuotaConfig(config, null);
        CachedLayerProvider provider = new CachedLayerProvider();
        List<TileLayer> layers = provider.getItems();
        for (TileLayer tileLayer : layers) {
            // we are not returning the values from the quota subsystem, they are not up to date
            // anyways
            assertNull(QUOTA_USAGE.getPropertyValue(tileLayer));
        }
    }

    @Test
    public void testAdvertised() {
        GWC oldGWC = GWC.get();
        GWC gwc = Mockito.mock(GWC.class);
        GWC.set(gwc);
        // Adding a few Mocks for an Unadvertised Layer
        TileLayer l = Mockito.mock(TileLayer.class);
        Mockito.when(l.isAdvertised()).thenReturn(false);
        // Calculating the size of the Layers with the unadvertised one
        Set<String> tileLayerNames = gwc.getTileLayerNames();
        tileLayerNames.add("testUnAdvertised");
        // Real size of the Layer names Set
        int gwcSize = (tileLayerNames.size()) - 1;
        // Mocks for the GWC class
        Mockito.when(gwc.getTileLayerNames()).thenReturn(tileLayerNames);
        Mockito.when(gwc.getTileLayerByName("testUnAdvertised")).thenReturn(l);
        // Calculate the number of TileLayers found
        CachedLayerProvider provider = new CachedLayerProvider();
        int providerSize = provider.getItems().size();
        // Ensure that the two numbers are equal
        assertEquals(gwcSize, providerSize);
        // Set the old GWC
        GWC.set(oldGWC);
    }
}

