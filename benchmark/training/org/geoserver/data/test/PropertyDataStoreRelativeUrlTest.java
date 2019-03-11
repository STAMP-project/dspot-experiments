/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.data.test;


import PropertyDataStoreFactory.DIRECTORY.key;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.geoserver.catalog.impl.DataStoreInfoImpl;
import org.geoserver.test.GeoServerBaseTestSupport;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.data.DataAccess;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.property.PropertyDataStoreFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * *
 *
 * @author Niels Charlier
 */
public class PropertyDataStoreRelativeUrlTest extends GeoServerSystemTestSupport {
    @Test
    public void testPropertyDataStoreRelativeUrl() throws IOException {
        // create dir
        File testDS = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "testDS").getCanonicalFile();
        testDS.mkdir();
        HashMap params = new HashMap();
        params.put(key, "file:./testDS");
        params.put(PropertyDataStoreFactory.NAMESPACE.key, "http://www.geotools.org/test");
        DataStoreInfoImpl info = new DataStoreInfoImpl(getGeoServer().getCatalog());
        info.setConnectionParameters(params);
        DataAccessFactory f = getGeoServer().getCatalog().getResourcePool().getDataStoreFactory(info);
        Assert.assertNotNull(f);
        Assert.assertTrue((f instanceof PropertyDataStoreFactory));
        DataAccess store = getGeoServer().getCatalog().getResourcePool().getDataStore(info);
        Assert.assertEquals(testDS.toURI().toString().toLowerCase(), store.getInfo().getSource().toString().replace("/./", "/").toLowerCase());
    }
}

