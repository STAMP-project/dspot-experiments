/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog;


import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.SystemTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SystemTest.class)
public class SchemaMappingTest extends GeoServerSystemTestSupport {
    public SchemaMappingTest() {
        super();
    }

    @Test
    public void testNoMapping() throws Exception {
        reloadCatalogAndConfiguration();
        FeatureTypeInfo ft = getCatalog().getFeatureTypeByName("DividedRoutes");
        Assert.assertEquals(4, ft.attributes().size());
    }

    @Test
    public void testXsdMapping() throws Exception {
        getDataDirectory().copyToResourceDir(getDividedRoutes(), getClass().getResourceAsStream("schema.xsd"), "schema.xsd");
        reloadCatalogAndConfiguration();
        FeatureTypeInfo ft = getCatalog().getFeatureTypeByName("DividedRoutes");
        Assert.assertEquals(3, ft.attributes().size());
    }

    @Test
    public void testXmlMapping() throws Exception {
        getDataDirectory().copyToResourceDir(getDividedRoutes(), getClass().getResourceAsStream("schema.xml"), "schema.xml");
        reloadCatalogAndConfiguration();
        FeatureTypeInfo ft = getCatalog().getFeatureTypeByName("DividedRoutes");
        Assert.assertEquals(2, ft.attributes().size());
    }
}

