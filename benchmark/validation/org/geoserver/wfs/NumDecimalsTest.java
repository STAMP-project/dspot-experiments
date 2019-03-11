/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.config.GeoServerInfo;
import org.junit.Test;
import org.w3c.dom.Document;


public class NumDecimalsTest extends WFSTestSupport {
    @Test
    public void testDefaults() throws Exception {
        Document dom = getAsDOM("wfs?request=getfeature&featureid=PrimitiveGeoFeature.f008&version=1.0.0");
        runAssertions(dom, 3);
    }

    @Test
    public void testGlobal() throws Exception {
        Catalog cat = getCatalog();
        FeatureTypeInfo ft = cat.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        ft.setNumDecimals((-1));
        cat.save(ft);
        GeoServerInfo global = getGeoServer().getGlobal();
        global.getSettings().setNumDecimals(1);
        getGeoServer().save(global);
        Document dom = getAsDOM("wfs?request=getfeature&featureid=PrimitiveGeoFeature.f008&version=1.0.0");
        runAssertions(dom, 1);
    }

    @Test
    public void testPerFeatureType() throws Exception {
        Catalog cat = getCatalog();
        FeatureTypeInfo ft = cat.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        ft.setNumDecimals(1);
        cat.save(ft);
        Document dom = getAsDOM("wfs?request=getfeature&featureid=PrimitiveGeoFeature.f008&version=1.0.0");
        runAssertions(dom, 1);
    }

    @Test
    public void testMultipleFeatureTypes() throws Exception {
        Catalog cat = getCatalog();
        FeatureTypeInfo ft1 = cat.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        FeatureTypeInfo ft2 = cat.getFeatureTypeByName("sf", "AggregateGeoFeature");
        ft1.setNumDecimals(3);
        cat.save(ft1);
        ft2.setNumDecimals(1);
        cat.save(ft2);
        Document dom = getAsDOM("wfs?request=getfeature&featureid=PrimitiveGeoFeature.f008,AggregateGeoFeature.f009&version=1.0.0");
        runAssertions(dom, 3);
    }
}

