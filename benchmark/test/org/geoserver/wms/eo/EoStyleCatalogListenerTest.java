/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.eo;


import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Davide Savazzi - geo-solutions.it
 */
public class EoStyleCatalogListenerTest extends GeoServerSystemTestSupport {
    @Test
    public void testStylesExist() {
        Catalog catalog = getCatalog();
        for (String styleName : getStyleNames()) {
            Assert.assertNotNull(catalog.getStyleByName(styleName));
        }
    }

    @Test
    public void testDelete() {
        Catalog catalog = getCatalog();
        StyleInfo style = catalog.getStyleByName(getStyleNames()[0]);
        Assert.assertNotNull(style);
        catalog.remove(style);
        // style should have been recreated
        Assert.assertNotNull(catalog.getStyleByName(getStyleNames()[0]));
    }
}

