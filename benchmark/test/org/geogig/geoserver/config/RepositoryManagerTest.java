/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.config;


import ConfigAction.CONFIG_SET;
import GeoGigDataStoreFactory.DISPLAY_NAME;
import Hints.REPOSITORY_NAME;
import Hints.REPOSITORY_URL;
import Ref.HEAD;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;
import org.geogig.geoserver.GeoGigTestData;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.geotools.data.DataAccess;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.locationtech.geogig.geotools.data.GeoGigDataStore;
import org.locationtech.geogig.model.Ref;
import org.locationtech.geogig.plumbing.RefParse;
import org.locationtech.geogig.plumbing.ResolveRepositoryName;
import org.locationtech.geogig.porcelain.BranchCreateOp;
import org.locationtech.geogig.porcelain.BranchListOp;
import org.locationtech.geogig.porcelain.CommitOp;
import org.locationtech.geogig.porcelain.ConfigOp;
import org.locationtech.geogig.porcelain.InitOp;
import org.locationtech.geogig.repository.Hints;
import org.locationtech.geogig.repository.Repository;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;


/**
 * Unit tests for the RepositoryManager
 */
@TestSetup(run = TestSetupFrequency.REPEAT)
public class RepositoryManagerTest extends GeoServerSystemTestSupport {
    @Rule
    public GeoGigTestData geogigData = new GeoGigTestData();

    private RepositoryManager repoManager;

    private static final Random rnd = new Random();

    @Test
    public void testGet() {
        Assert.assertNotNull(repoManager);
        RepositoryManager repoManager2 = RepositoryManager.get();
        Assert.assertNotNull(repoManager2);
        Assert.assertEquals(repoManager, repoManager2);
    }

    @Test
    public void testCloseTwice() {
        Assert.assertNotNull(repoManager);
        RepositoryManager.close();
        // second close will happen in after()
    }

    @Test
    public void testCreateAndGetRepos() throws IOException {
        Hints hints = new Hints();
        hints.set(REPOSITORY_NAME, "repo1");
        Repository repo = repoManager.createRepo(hints);
        Assert.assertNotNull(repo);
        Assert.assertFalse(repo.isOpen());
        repo.command(InitOp.class).call();
        RepositoryInfo info1 = saveRepository(repo);
        repo.close();
        List<RepositoryInfo> repositories = repoManager.getAll();
        Assert.assertEquals(1, repositories.size());
        Assert.assertEquals(info1, repositories.get(0));
        hints = new Hints();
        hints.set(REPOSITORY_NAME, "repo2");
        repo = repoManager.createRepo(hints);
        Assert.assertNotNull(repo);
        Assert.assertFalse(repo.isOpen());
        repo.command(InitOp.class).call();
        URI repoURI = repo.getLocation();
        RepositoryInfo info2 = saveRepository(repo);
        repo.close();
        // creating the same repo should return the one we already made
        repo = repoManager.createRepo(hints);
        Assert.assertNotNull(repo);
        Assert.assertTrue(repo.isOpen());
        Assert.assertEquals(repoURI, repo.getLocation());
        repo.close();
        repositories = repoManager.getAll();
        Assert.assertEquals(2, repositories.size());
        Assert.assertTrue(repositories.contains(info1));
        Assert.assertTrue(repositories.contains(info2));
        RepositoryInfo info1Get = repoManager.get(info1.getId());
        Assert.assertEquals(info1, info1Get);
        RepositoryInfo info2Get = repoManager.get(info2.getId());
        Assert.assertEquals(info2, info2Get);
        String randomUUID = UUID.randomUUID().toString();
        try {
            repoManager.get(randomUUID);
            Assert.fail();
        } catch (NoSuchElementException e) {
            // expected;
            Assert.assertEquals(("Repository not found: " + randomUUID), e.getMessage());
        }
        info1Get = repoManager.getByRepoName("repo1");
        info2Get = repoManager.getByRepoName("repo2");
        Assert.assertEquals(info1, info1Get);
        Assert.assertEquals(info2, info2Get);
        RepositoryInfo rpoByName = repoManager.getByRepoName("nonexistent");
        Assert.assertNull("Expected repository to be non-existent", rpoByName);
    }

    @Test
    public void testCreateRepoUnsupportedURIScheme() {
        Hints hints = new Hints();
        hints.set(REPOSITORY_NAME, "repo1");
        hints.set(REPOSITORY_URL, "unknown://repo1");
        try {
            repoManager.createRepo(hints);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // expected
            Assert.assertEquals("No repository initializer found capable of handling this kind of URI: unknown://repo1", e.getMessage());
        }
    }

    @Test
    public void testInvalidate() throws IOException {
        Hints hints = new Hints();
        hints.set(REPOSITORY_NAME, "repo1");
        Repository repo = repoManager.createRepo(hints);
        Assert.assertNotNull(repo);
        Assert.assertFalse(repo.isOpen());
        repo.command(InitOp.class).call();
        RepositoryInfo info = saveRepository(repo);
        repo.close();
        List<RepositoryInfo> repositories = repoManager.getAll();
        Assert.assertEquals(1, repositories.size());
        Assert.assertEquals(info, repositories.get(0));
        // get repository
        Repository repo1 = repoManager.getRepository(info.getId());
        Assert.assertNotNull(repo1);
        // subsequent calls should return the same repo object
        Assert.assertTrue((repo1 == (repoManager.getRepository(info.getId()))));
        // invalidating should clear the cache
        repoManager.invalidate(info.getId());
        Repository repo1_after = repoManager.getRepository(info.getId());
        Assert.assertNotNull(repo1_after);
        // they should be different instances
        Assert.assertFalse((repo1 == repo1_after));
    }

    @Test
    public void testCatalog() throws IOException {
        Catalog catalog = getCatalog();
        repoManager.setCatalog(catalog);
        Assert.assertTrue((catalog == (repoManager.getCatalog())));
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        geogigData.init().config("user.name", "gabriel").config("user.email", "gabriel@test.com").createTypeTree("lines", "geom:LineString:srid=4326").createTypeTree("points", "geom:Point:srid=4326").add().commit("created type trees").get();
        // 
        // 
        // 
        geogigData.insert("points", "p1=geom:POINT(0 0)", "p2=geom:POINT(1 1)", "p3=geom:POINT(2 2)");
        // 
        // 
        geogigData.insert("lines", "l1=geom:LINESTRING(-10 0, 10 0)", "l2=geom:LINESTRING(0 0, 180 0)");
        geogigData.add().commit("Added test features");
        GeoGigTestData.CatalogBuilder catalogBuilder = geogigData.newCatalogBuilder(catalog);
        int i = RepositoryManagerTest.rnd.nextInt();
        catalogBuilder.namespace(("geogig.org/" + i)).workspace(("geogigws" + i)).store(("geogigstore" + i));
        catalogBuilder.addAllRepoLayers().build();
        String workspaceName = catalogBuilder.workspaceName();
        String storeName = catalogBuilder.storeName();
        String layerName = workspaceName + ":points";
        LayerInfo pointLayerInfo = catalog.getLayerByName(layerName);
        Assert.assertNotNull(pointLayerInfo);
        layerName = workspaceName + ":lines";
        LayerInfo lineLayerInfo = catalog.getLayerByName(layerName);
        Assert.assertNotNull(lineLayerInfo);
        DataStoreInfo dsInfo = catalog.getDataStoreByName(workspaceName, storeName);
        Assert.assertNotNull(dsInfo);
        Assert.assertEquals(DISPLAY_NAME, dsInfo.getType());
        DataAccess<? extends FeatureType, ? extends Feature> dataStore = dsInfo.getDataStore(null);
        Assert.assertNotNull(dataStore);
        Assert.assertTrue((dataStore instanceof GeoGigDataStore));
        List<DataStoreInfo> geogigDataStores = repoManager.findGeogigStores();
        Assert.assertEquals(1, geogigDataStores.size());
        Assert.assertEquals(dsInfo, geogigDataStores.get(0));
        List<RepositoryInfo> repositoryInfos = repoManager.getAll();
        Assert.assertEquals(1, repositoryInfos.size());
        RepositoryInfo repoInfo = repositoryInfos.get(0);
        List<DataStoreInfo> dataStoresForRepoInfo = repoManager.findDataStores(repoInfo.getId());
        Assert.assertEquals(1, dataStoresForRepoInfo.size());
        Assert.assertEquals(dsInfo, dataStoresForRepoInfo.get(0));
        List<LayerInfo> dataStoreLayers = repoManager.findLayers(dsInfo);
        Assert.assertEquals(2, dataStoreLayers.size());
        Assert.assertTrue(dataStoreLayers.contains(pointLayerInfo));
        Assert.assertTrue(dataStoreLayers.contains(lineLayerInfo));
        List<FeatureTypeInfo> dataStoreFeatureTypes = repoManager.findFeatureTypes(dsInfo);
        Assert.assertEquals(2, dataStoreFeatureTypes.size());
        FeatureTypeInfo pointsTypeInfo;
        FeatureTypeInfo linesTypeInfo;
        if (dataStoreFeatureTypes.get(0).getName().equals("points")) {
            pointsTypeInfo = dataStoreFeatureTypes.get(0);
            linesTypeInfo = dataStoreFeatureTypes.get(1);
        } else {
            pointsTypeInfo = dataStoreFeatureTypes.get(1);
            linesTypeInfo = dataStoreFeatureTypes.get(0);
        }
        Assert.assertEquals("points", pointsTypeInfo.getName());
        Assert.assertEquals("lines", linesTypeInfo.getName());
        List<? extends CatalogInfo> catalogObjects = repoManager.findDependentCatalogObjects(repoInfo.getId());
        Assert.assertEquals(5, catalogObjects.size());
        Assert.assertTrue(catalogObjects.contains(dsInfo));
        Assert.assertTrue(catalogObjects.contains(pointLayerInfo));
        Assert.assertTrue(catalogObjects.contains(lineLayerInfo));
        Assert.assertTrue(catalogObjects.contains(pointsTypeInfo));
        Assert.assertTrue(catalogObjects.contains(linesTypeInfo));
    }

    @Test
    public void testRenameRepository() throws IOException {
        Hints hints = new Hints();
        hints.set(REPOSITORY_NAME, "repo1");
        Repository repo = repoManager.createRepo(hints);
        Assert.assertNotNull(repo);
        Assert.assertFalse(repo.isOpen());
        repo.command(InitOp.class).call();
        RepositoryInfo info = saveRepository(repo);
        repo.close();
        List<RepositoryInfo> repositories = repoManager.getAll();
        Assert.assertEquals(1, repositories.size());
        Assert.assertEquals(info, repositories.get(0));
        RepositoryInfo renamed = new RepositoryInfo();
        renamed.setId(info.getId());
        renamed.setLocation(info.getLocation());
        renamed.setRepoName("repo1_renamed");
        repoManager.save(renamed);
        repositories = repoManager.getAll();
        Assert.assertEquals(1, repositories.size());
        Assert.assertEquals(renamed, repositories.get(0));
        RepositoryInfo infoGet = repoManager.get(info.getId());
        Assert.assertEquals(renamed, infoGet);
        infoGet = repoManager.getByRepoName("repo1_renamed");
        Assert.assertEquals(renamed, infoGet);
        RepositoryInfo repoByName = repoManager.getByRepoName("repo1");
        Assert.assertNull("Expected \"repo1\" to be non-existent", repoByName);
    }

    @Test
    public void testIsGeogigDirectory() throws IOException {
        Assert.assertFalse(RepositoryManager.isGeogigDirectory(null));
        File repoDir = new File(testData.getDataDirectoryRoot(), "testRepo");
        repoDir.mkdirs();
        Assert.assertFalse(RepositoryManager.isGeogigDirectory(repoDir));
        Hints hints = new Hints();
        hints.set(REPOSITORY_NAME, "repo1");
        hints.set(REPOSITORY_URL, repoDir.toURI().toString());
        Repository repo = repoManager.createRepo(hints);
        Assert.assertNotNull(repo);
        Assert.assertFalse(repo.isOpen());
        repo.command(InitOp.class).call();
        saveRepository(repo);
        repo.close();
        Assert.assertTrue(RepositoryManager.isGeogigDirectory(repoDir));
        File fakeRepoDir = new File(testData.getDataDirectoryRoot(), "fakeRepoDir");
        fakeRepoDir.mkdirs();
        File fakeGeogig = new File(fakeRepoDir, ".geogig");
        fakeGeogig.createNewFile();
        Assert.assertFalse(RepositoryManager.isGeogigDirectory(fakeRepoDir));
    }

    @Test
    public void testFindOrCreateByLocation() throws IOException {
        File repoDir = new File(testData.getDataDirectoryRoot(), "testRepo");
        repoDir.mkdirs();
        Assert.assertFalse(RepositoryManager.isGeogigDirectory(repoDir));
        URI repoURI = repoDir.toURI();
        RepositoryInfo info = repoManager.findOrCreateByLocation(repoURI);
        Assert.assertEquals(repoURI, info.getLocation());
        // the repository should be created
        Assert.assertTrue(RepositoryManager.isGeogigDirectory(repoDir));
        Repository repo = repoManager.getRepository(info.getId());
        String repoName = repo.command(ResolveRepositoryName.class).call();
        Assert.assertEquals("testRepo", repoName);
        // should return the same info since it was already created.
        RepositoryInfo infoGet = repoManager.findOrCreateByLocation(repoURI);
        Assert.assertEquals(info, infoGet);
    }

    @Test
    public void testDeleteRepository() throws IOException {
        Catalog catalog = getCatalog();
        repoManager.setCatalog(catalog);
        Assert.assertTrue((catalog == (repoManager.getCatalog())));
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        geogigData.init().config("user.name", "gabriel").config("user.email", "gabriel@test.com").createTypeTree("lines", "geom:LineString:srid=4326").createTypeTree("points", "geom:Point:srid=4326").add().commit("created type trees").get();
        // 
        // 
        // 
        geogigData.insert("points", "p1=geom:POINT(0 0)", "p2=geom:POINT(1 1)", "p3=geom:POINT(2 2)");
        // 
        // 
        geogigData.insert("lines", "l1=geom:LINESTRING(-10 0, 10 0)", "l2=geom:LINESTRING(0 0, 180 0)");
        geogigData.add().commit("Added test features");
        GeoGigTestData.CatalogBuilder catalogBuilder = geogigData.newCatalogBuilder(catalog);
        int i = RepositoryManagerTest.rnd.nextInt();
        catalogBuilder.namespace(("geogig.org/" + i)).workspace(("geogigws" + i)).store(("geogigstore" + i));
        catalogBuilder.addAllRepoLayers().build();
        String workspaceName = catalogBuilder.workspaceName();
        String storeName = catalogBuilder.storeName();
        String layerName = workspaceName + ":points";
        LayerInfo pointLayerInfo = catalog.getLayerByName(layerName);
        Assert.assertNotNull(pointLayerInfo);
        layerName = workspaceName + ":lines";
        LayerInfo lineLayerInfo = catalog.getLayerByName(layerName);
        Assert.assertNotNull(lineLayerInfo);
        DataStoreInfo dsInfo = catalog.getDataStoreByName(workspaceName, storeName);
        Assert.assertNotNull(dsInfo);
        Assert.assertEquals(DISPLAY_NAME, dsInfo.getType());
        DataAccess<? extends FeatureType, ? extends Feature> dataStore = dsInfo.getDataStore(null);
        Assert.assertNotNull(dataStore);
        Assert.assertTrue((dataStore instanceof GeoGigDataStore));
        List<DataStoreInfo> geogigDataStores = repoManager.findGeogigStores();
        Assert.assertEquals(1, geogigDataStores.size());
        Assert.assertEquals(dsInfo, geogigDataStores.get(0));
        List<RepositoryInfo> repositoryInfos = repoManager.getAll();
        Assert.assertEquals(1, repositoryInfos.size());
        RepositoryInfo info = repositoryInfos.get(0);
        repoManager.delete(info.getId());
        repositoryInfos = repoManager.getAll();
        Assert.assertEquals(0, repositoryInfos.size());
        RepositoryInfo repoByName = repoManager.getByRepoName("repo1");
        Assert.assertNull("Expected \"repo1\" to be non-existent", repoByName);
        try {
            repoManager.getRepository(info.getId());
            Assert.fail();
        } catch (Exception e) {
            e.printStackTrace();
            // expected
            Assert.assertTrue(e.getMessage().contains(("Repository not found: " + (info.getId()))));
        }
        geogigDataStores = repoManager.findGeogigStores();
        Assert.assertEquals(0, geogigDataStores.size());
    }

    @Test
    public void testListBranches() throws IOException {
        Hints hints = new Hints();
        hints.set(REPOSITORY_NAME, "repo1");
        Repository repo = repoManager.createRepo(hints);
        Assert.assertNotNull(repo);
        Assert.assertFalse(repo.isOpen());
        repo.command(InitOp.class).call();
        repo.command(ConfigOp.class).setAction(CONFIG_SET).setName("user.name").setValue("TestUser").call();
        repo.command(ConfigOp.class).setAction(CONFIG_SET).setName("user.email").setValue("test@user.com").call();
        repo.command(CommitOp.class).setAllowEmpty(true).setMessage("initial commit").call();
        repo.command(BranchCreateOp.class).setName("branch1").call();
        List<Ref> branches = repo.command(BranchListOp.class).call();
        RepositoryInfo info = saveRepository(repo);
        repo.close();
        List<Ref> repoBranches = repoManager.listBranches(info.getId());
        Assert.assertTrue(repoBranches.containsAll(branches));
        Assert.assertTrue(branches.containsAll(repoBranches));
        Assert.assertEquals(branches.size(), repoBranches.size());
    }

    @Test
    public void testPingRemote() throws Exception {
        try {
            RepositoryManager.pingRemote(null, null, null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // expected
            Assert.assertEquals("Please indicate the remote repository URL", e.getMessage());
        }
        Hints hints = new Hints();
        hints.set(REPOSITORY_NAME, "repo1");
        Repository repo = repoManager.createRepo(hints);
        Assert.assertNotNull(repo);
        Assert.assertFalse(repo.isOpen());
        repo.command(InitOp.class).call();
        repo.command(ConfigOp.class).setAction(CONFIG_SET).setName("user.name").setValue("TestUser").call();
        repo.command(ConfigOp.class).setAction(CONFIG_SET).setName("user.email").setValue("test@user.com").call();
        repo.command(CommitOp.class).setAllowEmpty(true).setMessage("initial commit").call();
        Ref headRef = repo.command(RefParse.class).setName(HEAD).call().get();
        repo.command(InitOp.class).call();
        RepositoryInfo info1 = saveRepository(repo);
        repo.close();
        Assert.assertEquals(headRef, RepositoryManager.pingRemote(info1.getLocation().toString(), "user", ""));
        File notInitialized = new File(testData.getDataDirectoryRoot(), "notARepo");
        notInitialized.mkdirs();
        try {
            RepositoryManager.pingRemote(notInitialized.toURI().toString(), "user", "");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().startsWith("Unable to connect: "));
            Assert.assertTrue(e.getMessage().contains("not a geogig repository"));
        }
    }

    @Test
    public void testCreate() throws Exception {
        File repoFolder = new File(testData.getDataDirectoryRoot(), "someRepoName");
        URI uri = repoFolder.toURI();
        RepositoryInfo repoInfo = new RepositoryInfo();
        repoInfo.setLocation(uri);
        // now call save() with the RepositoryInfo
        // since the repo doesn't exist, save() should try to create it
        RepositoryInfo savedInfo = RepositoryManager.get().save(repoInfo);
        Assert.assertNotNull(savedInfo);
        // make sure it's retrievable as well
        Assert.assertNotNull(RepositoryManager.get().getByRepoName("someRepoName"));
    }
}

