/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.spatialite;


import Query.ALL;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.data.DataAccess;
import org.geotools.feature.NameImpl;
import org.geotools.util.logging.Logging;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.type.Name;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Contains tests that invoke REST resources that will use SpatiaLite data store.
 */
public final class RestTest extends GeoServerSystemTestSupport {
    private static final Logger LOGGER = Logging.getLogger(RestTest.class);

    private static final String SQLITE_MIME_TYPE = "application/x-sqlite3";

    private static final String WORKSPACE_NAME = "spatialite-tests";

    private static final String WORKSPACE_URI = "http://spatialite-tests.org";

    private static File ROOT_DIRECTORY;

    private static File DATABASE_FILE;

    private static byte[] DATABASE_FILE_AS_BYTES;

    @Test
    public void createDataStoreUsingRest() throws Exception {
        String dataStoreName = UUID.randomUUID().toString();
        // perform a PUT request, a new SpatiaLite data store should be created
        // we require that all available feature types should be created
        String path = String.format("/rest/workspaces/%s/datastores/%s/file.spatialite?configure=all", RestTest.WORKSPACE_NAME, dataStoreName);
        MockHttpServletResponse response = putAsServletResponse(path, RestTest.DATABASE_FILE_AS_BYTES, RestTest.SQLITE_MIME_TYPE);
        // we should get a HTTP 201 status code meaning that the data store was created
        Assert.assertThat(response.getStatus(), CoreMatchers.is(201));
        // let's see if the data store was correctly created
        DataStoreInfo storeInfo = getCatalog().getDataStoreByName(dataStoreName);
        Assert.assertThat(storeInfo, CoreMatchers.notNullValue());
        DataAccess store = storeInfo.getDataStore(null);
        Assert.assertThat(store, CoreMatchers.notNullValue());
        List<Name> names = store.getNames();
        Assert.assertThat(store, CoreMatchers.notNullValue());
        // check that at least the table points is available
        Name found = names.stream().filter(( name) -> (name != null) && (name.getLocalPart().equals("points"))).findFirst().orElse(null);
        Assert.assertThat(found, CoreMatchers.notNullValue());
        // check that the points layer was correctly created
        LayerInfo layerInfo = getCatalog().getLayerByName(new NameImpl(RestTest.WORKSPACE_URI, "points"));
        Assert.assertThat(layerInfo, CoreMatchers.notNullValue());
        Assert.assertThat(layerInfo.getResource(), CoreMatchers.notNullValue());
        Assert.assertThat(layerInfo.getResource(), CoreMatchers.instanceOf(FeatureTypeInfo.class));
        // check that we have the expected features
        FeatureTypeInfo featureTypeInfo = ((FeatureTypeInfo) (layerInfo.getResource()));
        int count = featureTypeInfo.getFeatureSource(null, null).getCount(ALL);
        Assert.assertThat(count, CoreMatchers.is(4));
    }
}

