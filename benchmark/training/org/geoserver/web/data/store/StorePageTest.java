/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;


import Filter.INCLUDE;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.Predicates;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.util.CloseableIterator;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class StorePageTest extends GeoServerWicketTestSupport {
    @Test
    public void testLoad() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(StorePage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        DataView dv = ((DataView) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        Catalog catalog = getCatalog();
        Assert.assertEquals(dv.size(), catalog.getStores(StoreInfo.class).size());
        IDataProvider dataProvider = dv.getDataProvider();
        // Ensure the data provider is an instance of StoreProvider
        Assert.assertTrue((dataProvider instanceof StoreProvider));
        // Cast to StoreProvider
        StoreProvider provider = ((StoreProvider) (dataProvider));
        // Ensure that an unsupportedException is thrown when requesting the Items directly
        boolean catchedException = false;
        try {
            provider.getItems();
        } catch (UnsupportedOperationException e) {
            catchedException = true;
        }
        // Ensure the exception is cacthed
        Assert.assertTrue(catchedException);
        StoreInfo actual = provider.iterator(0, 1).next();
        CloseableIterator<StoreInfo> list = catalog.list(StoreInfo.class, INCLUDE, 0, 1, Predicates.sortBy("name", true));
        Assert.assertTrue(list.hasNext());
        StoreInfo expected = list.next();
        // Close the iterator
        try {
            if (list != null) {
                list.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(expected, actual);
    }
}

