/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.rest;


import java.io.File;
import java.net.URI;
import org.geogig.geoserver.GeoGigTestData;
import org.geogig.geoserver.config.GeoServerGeoGigRepositoryResolver;
import org.geogig.geoserver.config.RepositoryInfo;
import org.geogig.geoserver.config.RepositoryManager;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.rest.catalog.CatalogRESTTestSupport;
import org.geotools.data.DataStore;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.locationtech.geogig.plumbing.ResolveGeogigDir;
import org.locationtech.geogig.porcelain.InitOp;
import org.locationtech.geogig.repository.impl.GeoGIG;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Integration test suite with GeoServer's REST API
 */
public class GeoGigGeoServerRESTntegrationTest extends CatalogRESTTestSupport {
    @Rule
    public GeoGigTestData geogigData = new GeoGigTestData();

    @Test
    public void createDataStoreNewConfig() throws Exception {
        String message = "<dataStore>\n"// 
         + (((((" <name>repo_new_config</name>\n"// 
         + " <type>GeoGIG</type>\n")// 
         + " <connectionParameters>\n")// 
         + "   <entry key=\"geogig_repository\">${repository}</entry>\n")// 
         + " </connectionParameters>\n")// 
         + "</dataStore>\n");
        GeoGIG geogig = geogigData.createRepository("new_repo");
        try {
            geogig.command(InitOp.class).call();
            File repo = geogig.command(ResolveGeogigDir.class).getFile().get();
            final URI location = repo.getParentFile().getAbsoluteFile().toURI();
            RepositoryManager manager = RepositoryManager.get();
            RepositoryInfo info = new RepositoryInfo();
            info.setLocation(location);
            info = manager.save(info);
            final String repoName = info.getRepoName();
            message = message.replace("${repository}", GeoServerGeoGigRepositoryResolver.getURI(repoName));
            // System.err.println(message);
            Catalog catalog = getCatalog();
            GeoGigTestData.CatalogBuilder catalogBuilder = geogigData.newCatalogBuilder(catalog);
            catalogBuilder.setUpWorkspace("new_ws");
            final String uri = "/rest/workspaces/new_ws/datastores";
            MockHttpServletResponse response = postAsServletResponse(uri, message, "text/xml");
            Assert.assertEquals(201, response.getStatus());
            String locationHeader = response.getHeader("Location");
            Assert.assertNotNull(locationHeader);
            Assert.assertTrue(locationHeader.endsWith("/workspaces/new_ws/datastores/repo_new_config"));
            DataStoreInfo newDataStore = catalog.getDataStoreByName("repo_new_config");
            Assert.assertNotNull(newDataStore);
            DataStore ds = ((DataStore) (newDataStore.getDataStore(null)));
            Assert.assertNotNull(ds);
            checkNewConfig(newDataStore);
        } finally {
            geogig.close();
        }
    }

    @Test
    public void createDataStoreCustomURIWithName() throws Exception {
        String message = "<dataStore>\n"// 
         + (((((" <name>repo_new_config2</name>\n"// 
         + " <type>GeoGIG</type>\n")// 
         + " <connectionParameters>\n")// 
         + "   <entry key=\"geogig_repository\">${repository}</entry>\n")// 
         + " </connectionParameters>\n")// 
         + "</dataStore>\n");
        GeoGIG geogig = geogigData.createRepository("new_repo1");
        try {
            geogig.command(InitOp.class).call();
            File repo = geogig.command(ResolveGeogigDir.class).getFile().get();
            final URI location = repo.getParentFile().getAbsoluteFile().toURI();
            RepositoryManager manager = RepositoryManager.get();
            RepositoryInfo info = new RepositoryInfo();
            info.setLocation(location);
            info = manager.save(info);
            final String customURI = GeoServerGeoGigRepositoryResolver.getURI("new_repo1");
            message = message.replace("${repository}", customURI);
            final String uri = "/rest/workspaces/gigws/datastores";
            MockHttpServletResponse response = postAsServletResponse(uri, message, "text/xml");
            Assert.assertEquals(201, response.getStatus());
            String locationHeader = response.getHeader("Location");
            Assert.assertNotNull(locationHeader);
            Assert.assertTrue(locationHeader.endsWith("/workspaces/gigws/datastores/repo_new_config2"));
            DataStoreInfo newDataStore = catalog.getDataStoreByName("repo_new_config2");
            Assert.assertNotNull(newDataStore);
            DataStore ds = ((DataStore) (newDataStore.getDataStore(null)));
            Assert.assertNotNull(ds);
            checkNewConfig(newDataStore);
        } finally {
            geogig.close();
        }
    }
}

