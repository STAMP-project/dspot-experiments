/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.web.data;


import Filter.INCLUDE;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.Predicates;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.util.CloseableIterator;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class StylePageTest extends GeoServerWicketTestSupport {
    @Test
    public void testPageLoad() {
        login();
        tester.startPage(StylePage.class);
        tester.assertRenderedPage(StylePage.class);
    }

    @Test
    public void testStyleProvider() {
        login();
        tester.startPage(StylePage.class);
        tester.assertRenderedPage(StylePage.class);
        // Get the StyleProvider
        DataView dv = ((DataView) (tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        Catalog catalog = getCatalog();
        Assert.assertEquals(dv.size(), catalog.getStyles().size());
        IDataProvider dataProvider = dv.getDataProvider();
        // Ensure the data provider is an instance of StoreProvider
        Assert.assertTrue((dataProvider instanceof StyleProvider));
        // Cast to StoreProvider
        StyleProvider provider = ((StyleProvider) (dataProvider));
        // Ensure that an unsupportedException is thrown when requesting the Items directly
        boolean catchedException = false;
        try {
            provider.getItems();
        } catch (UnsupportedOperationException e) {
            catchedException = true;
        }
        // Ensure the exception is cacthed
        Assert.assertTrue(catchedException);
        StyleInfo actual = provider.iterator(0, 1).next();
        CloseableIterator<StyleInfo> list = catalog.list(StyleInfo.class, INCLUDE, 0, 1, Predicates.sortBy("name", true));
        Assert.assertTrue(list.hasNext());
        StyleInfo expected = list.next();
        // Close the iterator
        try {
            if (list != null) {
                list.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Ensure equality
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testIsDefaultStyle() {
        Catalog cat = getCatalog();
        Assert.assertTrue(StylePage.isDefaultStyle(cat.getStyleByName("line")));
        StyleInfo s = cat.getFactory().createStyle();
        s.setName("line");
        s.setFilename("line.sld");
        s.setWorkspace(cat.getDefaultWorkspace());
        Assert.assertFalse(StylePage.isDefaultStyle(s));
    }
}

