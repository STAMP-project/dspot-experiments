/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.table;


import MockData.CITE_PREFIX;
import MockData.TASMANIA_DEM;
import java.util.List;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.layer.NewLayerPageProvider;
import org.junit.Assert;
import org.junit.Test;


public class NewLayerProviderTest extends GeoServerWicketTestSupport {
    @Test
    public void testFeatureType() {
        StoreInfo cite = getCatalog().getStoreByName(CITE_PREFIX, StoreInfo.class);
        NewLayerPageProvider provider = new NewLayerPageProvider();
        provider.setStoreId(cite.getId());
        provider.setShowPublished(true);
        Assert.assertTrue(((provider.size()) > 0));
        provider.setShowPublished(false);
        Assert.assertEquals(0, provider.size());
    }

    @Test
    public void testCoverages() {
        StoreInfo dem = getCatalog().getStoreByName(TASMANIA_DEM.getLocalPart(), StoreInfo.class);
        NewLayerPageProvider provider = new NewLayerPageProvider();
        provider.setStoreId(dem.getId());
        provider.setShowPublished(true);
        Assert.assertTrue(((provider.size()) > 0));
        provider.setShowPublished(false);
        // todo: fix this
        // assertEquals(0, provider.size());
    }

    @Test
    public void testEmpty() {
        NewLayerPageProvider provider = new NewLayerPageProvider();
        provider.setShowPublished(true);
        Assert.assertEquals(0, provider.size());
        provider.setShowPublished(false);
        Assert.assertEquals(0, provider.size());
    }

    /**
     * As per GEOS-3120, if a resource is published but it's name changed, it should still show up
     * as published. It wasn't being the case due to comparing the resource's name instead of the
     * nativeName against the name the DataStore provides
     */
    @Test
    public void testPublishedUnpublishedWithChangedResourceName() {
        Catalog catalog = getCatalog();
        StoreInfo cite = catalog.getStoreByName(CITE_PREFIX, StoreInfo.class);
        List<FeatureTypeInfo> resources = catalog.getResourcesByStore(cite, FeatureTypeInfo.class);
        Assert.assertTrue(((resources.size()) > 0));
        final int numberOfPublishedResources = resources.size();
        NewLayerPageProvider provider = new NewLayerPageProvider();
        provider.setStoreId(cite.getId());
        provider.setShowPublished(false);
        Assert.assertEquals(0, provider.size());
        provider.setShowPublished(true);
        Assert.assertEquals(numberOfPublishedResources, provider.size());
        FeatureTypeInfo typeInfo = resources.get(0);
        typeInfo.setName("notTheNativeName");
        catalog.save(typeInfo);
        provider = new NewLayerPageProvider();
        provider.setStoreId(cite.getId());
        provider.setShowPublished(true);
        Assert.assertEquals(numberOfPublishedResources, provider.size());
        provider.setShowPublished(false);
        Assert.assertEquals(0, provider.size());
    }
}

