/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import java.io.IOException;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.security.decorators.ReadOnlyDataAccess;
import org.geotools.data.DataAccess;
import org.geotools.data.util.NullProgressListener;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.w3c.dom.Document;


/**
 * WFS GetFeature to test secured feature with GeoServer.
 *
 * @author Victor Tey (CSIRO Earth Science and Resource Engineering)
 */
public class SecuredFeatureChainingTest extends AbstractAppSchemaTestSupport {
    /**
     * Test that denormalized data reports the correct number of features
     */
    @Test
    public void testDenormalisedFeaturesCount() {
        setRequestAuth("cite_readatts", "cite");
        Document doc = getAsDOM(("wfs?request=GetFeature&version=1.1.0&typename=gsml:GeologicUnit" + "&maxFeatures=3&resultType=hits"));
        LOGGER.info(("WFS GetFeature&typename=gsml:GeologicUnit&maxFeatures=3 response:\n" + (prettyString(doc))));
        assertXpathEvaluatesTo("3", "//wfs:FeatureCollection/@numberOfFeatures", doc);
    }

    /**
     * Test that denormalized data reports the right output
     */
    @Test
    public void testSecureFeatureContent() {
        setRequestAuth("cite_readatts", "cite");
        Document doc = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=gsml:GeologicUnit&maxFeatures=3");
        LOGGER.info(("WFS GetFeature&typename=gsml:GeologicUnit&maxFeatures=3 response:\n" + (prettyString(doc))));
        assertXpathCount(3, "//gsml:GeologicUnit", doc);
        assertXpathCount(0, "//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:exposureColor", doc);
        assertXpathCount(0, "//gsml:GeologicUnit[@gml:id='gu.25678']/gsml:exposureColor", doc);
        assertXpathCount(0, "//gsml:GeologicUnit[@gml:id='gu.25682']/gsml:exposureColor", doc);
        assertXpathCount(1, "//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:composition", doc);
        assertXpathCount(2, "//gsml:GeologicUnit[@gml:id='gu.25678']/gsml:composition", doc);
        assertXpathCount(1, "//gsml:GeologicUnit[@gml:id='gu.25682']/gsml:composition", doc);
    }

    /**
     * Tests that {@link SecuredDataStoreInfo#getDataStore(org.opengis.util.ProgressListener)}
     * correctly returns a {@link DataAccess} instance.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSecuredDataStoreInfo() throws IOException {
        login("cite_readatts", "cite", "ROLE_DUMMY");
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName("gsml:GeologicUnit");
        Assert.assertNotNull(ftInfo);
        DataAccess<? extends FeatureType, ? extends Feature> dataAccess = ftInfo.getStore().getDataStore(new NullProgressListener());
        Assert.assertNotNull(dataAccess);
        Assert.assertTrue((dataAccess instanceof ReadOnlyDataAccess));
    }
}

