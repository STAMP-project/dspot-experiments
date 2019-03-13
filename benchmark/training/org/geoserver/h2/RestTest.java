/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.h2;


import java.io.File;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Test;


/**
 * Contains tests that invoke REST resources that will use H2 data store.
 */
public final class RestTest extends GeoServerSystemTestSupport {
    private static final String WORKSPACE_NAME = "h2-tests";

    private static final String WORKSPACE_URI = "http://h2-tests.org";

    private static File ROOT_DIRECTORY;

    private static File DATABASE_DIR;

    @Test
    public void createDataStoreUsingRestSingleFile() throws Exception {
        String dataStoreName = "h2-test-db-single";
        // send only the database file
        byte[] content = RestTest.readSqLiteDatabaseFile();
        genericCreateDataStoreUsingRestTest(dataStoreName, "application/octet-stream", content);
    }

    @Test
    public void createDataStoreUsingRestZipFile() throws Exception {
        String dataStoreName = "h2-test-db-zip";
        // send the database directory
        byte[] content = RestTest.readSqLiteDatabaseDir();
        genericCreateDataStoreUsingRestTest(dataStoreName, "application/zip", content);
    }
}

