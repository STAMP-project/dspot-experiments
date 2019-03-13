/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.web.repository;


import DropDownModel.DIRECTORY_CONFIG;
import DropDownModel.PG_CONFIG;
import java.io.IOException;
import java.util.ArrayList;
import org.geogig.geoserver.model.DropDownModel;
import org.geogig.geoserver.model.DropDownTestUtil;
import org.geogig.geoserver.web.RepositoriesPage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Parent test class to hold common methods.
 */
public abstract class CommonPanelTest extends GeoServerWicketTestSupport {
    protected RepositoriesPage repoPage;

    @Rule
    public TemporaryFolder temp;

    @Test
    public void testNoRocksDBBackend() throws IOException {
        // override the available backends
        ArrayList<String> configs = new ArrayList<>(1);
        configs.add(PG_CONFIG);
        DropDownTestUtil.setAvailableBackends(configs, configs.get(0));
        navigateToStartPage();
        // try to select Directory from the dropdown
        try {
            select(DIRECTORY_CONFIG);
            Assert.fail(("DropDown config option should not be available: " + (DropDownModel.DIRECTORY_CONFIG)));
        } catch (AssertionError ae) {
            // AssertionException expected here from the call to select()
        }
        // verify PostgreSQL config components are visible
        verifyPostgreSQLBackendComponents();
    }

    @Test
    public void testNoPostgreSQLBackend() throws IOException {
        // override the available backends
        ArrayList<String> configs = new ArrayList<>(1);
        configs.add(DIRECTORY_CONFIG);
        DropDownTestUtil.setAvailableBackends(configs, configs.get(0));
        navigateToStartPage();
        // try to select PostgreSQL from the dropdown
        try {
            select(PG_CONFIG);
            Assert.fail(("DropDown config option should not be available: " + (DropDownModel.PG_CONFIG)));
        } catch (AssertionError ae) {
            // AssertionException expected here from the call to select()
        }
        // verify Directory config components are visible
        verifyDirectoryBackendComponents();
    }

    @Test
    public void testNoAvailableBackends() throws IOException {
        // override the available backends
        DropDownTestUtil.setAvailableBackends(new ArrayList<>(0), null);
        navigateToStartPage();
        // try to select Directory from the dropdown
        try {
            select(DIRECTORY_CONFIG);
            Assert.fail(("DropDown config option should not be available: " + (DropDownModel.DIRECTORY_CONFIG)));
        } catch (AssertionError ae) {
            // AssertionException expected here from the call to select()
        }
        // try to select PostgreSQL from the dropdown
        try {
            select(PG_CONFIG);
            Assert.fail(("DropDown config option should not be available: " + (DropDownModel.PG_CONFIG)));
        } catch (AssertionError ae) {
            // AssertionException expected here from the call to select()
        }
        // verify Directory and PostgreSQL config components are invisible
        verifyNoBackendComponents();
    }
}

