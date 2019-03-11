/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geopkg;


import GeoPackageGetFeatureOutputFormat.PROPERTY_INDEXED;
import GeoPkg.MIME_TYPE;
import SystemTestData.BASIC_POLYGONS;
import WfsFactory.eINSTANCE;
import java.io.IOException;
import net.opengis.wfs.GetFeatureType;
import org.geoserver.platform.Operation;
import org.geoserver.wfs.WFSTestSupport;
import org.geoserver.wfs.request.FeatureCollectionResponse;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Test for WFS GetFeature OutputFormat for GeoPackage
 *
 * @author Niels Charlier
 */
public class GeoPackageGetFeatureOutputFormatTest extends WFSTestSupport {
    protected static FilterFactory ff = CommonFactoryFinder.getFilterFactory();

    protected GeoPackageGetFeatureOutputFormat format;

    protected Operation op;

    protected GetFeatureType gft;

    @Test
    public void testGetFeatureOneType() throws IOException {
        FeatureCollectionResponse fct = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        FeatureSource<? extends FeatureType, ? extends Feature> fs = getFeatureSource(BASIC_POLYGONS);
        fct.getFeature().add(fs.getFeatures());
        testGetFeature(fct, false);
    }

    @Test
    public void testGetFeatureTwoTypes() throws IOException {
        FeatureCollectionResponse fct = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        FeatureSource<? extends FeatureType, ? extends Feature> fs = getFeatureSource(SystemTestData.LAKES);
        fct.getFeature().add(fs.getFeatures());
        fs = getFeatureSource(SystemTestData.STREAMS);
        fct.getFeature().add(fs.getFeatures());
        testGetFeature(fct, false);
    }

    @Test
    public void testGetFeatureWithFilter() throws IOException {
        FeatureCollectionResponse fct = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        FeatureSource<? extends FeatureType, ? extends Feature> fs = getFeatureSource(SystemTestData.LAKES);
        fct.getFeature().add(fs.getFeatures());
        fs = getFeatureSource(SystemTestData.STREAMS);
        FeatureCollection coll = fs.getFeatures(GeoPackageGetFeatureOutputFormatTest.ff.equals(GeoPackageGetFeatureOutputFormatTest.ff.property("NAME"), GeoPackageGetFeatureOutputFormatTest.ff.literal("Cam Stream")));
        Assert.assertEquals(1, coll.size());
        fct.getFeature().add(coll);
        testGetFeature(fct, false);
    }

    @Test
    public void testGetFeatureWithSpatialIndex() throws IOException {
        System.setProperty(PROPERTY_INDEXED, "true");
        FeatureCollectionResponse fct = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        FeatureSource<? extends FeatureType, ? extends Feature> fs = getFeatureSource(BASIC_POLYGONS);
        fct.getFeature().add(fs.getFeatures());
        testGetFeature(fct, true);
        System.getProperties().remove(PROPERTY_INDEXED);
    }

    @Test
    public void testHttpStuff() throws Exception {
        String layerName = BASIC_POLYGONS.getLocalPart();
        MockHttpServletResponse resp = getAsServletResponse((("wfs?request=getfeature&typename=" + layerName) + "&outputformat=geopackage"));
        Assert.assertEquals(MIME_TYPE, resp.getContentType());
        Assert.assertEquals((("attachment; filename=" + layerName) + ".gpkg"), resp.getHeader("Content-Disposition"));
    }
}

