/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.jdbcconfig.catalog;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.config.GeoServerPersistersTest;
import org.geoserver.jdbcconfig.JDBCConfigTestSupport;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class JDBGeoserverResourcePersisterTest {
    private JDBCCatalogFacade facade;

    private JDBCConfigTestSupport testSupport;

    private Catalog catalog;

    public JDBGeoserverResourcePersisterTest(JDBCConfigTestSupport.DBConfig dbConfig) {
        testSupport = new JDBCConfigTestSupport(dbConfig);
    }

    @Test
    public void testRemoveStyle() throws Exception {
        addStyle();
        File sf = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld");
        sf.createNewFile();
        Assert.assertTrue(sf.exists());
        StyleInfo s = catalog.getStyleByName("foostyle");
        catalog.remove(s);
        Assert.assertThat(sf, Matchers.not(fileExists()));
        File sfb = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld.bak");
        Assert.assertThat(sfb, fileExists());
        // do it a second time
        addStyle();
        sf = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld");
        sf.createNewFile();
        Assert.assertTrue(sf.exists());
        s = catalog.getStyleByName("foostyle");
        catalog.remove(s);
        Assert.assertThat(sf, Matchers.not(fileExists()));
        sfb = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld.bak.1");
        Assert.assertThat(sfb, fileExists());
    }

    @Test
    public void testRenameStyle() throws Exception {
        addStyle();
        File sldFile = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld");
        sldFile.createNewFile();
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setName("boostyle");
        catalog.save(s);
        File renamedSldFile = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/boostyle.sld");
        Assert.assertThat(sldFile, Matchers.not(fileExists()));
        Assert.assertThat(renamedSldFile, fileExists());
    }

    @Test
    public void testRenameStyleConflict() throws Exception {
        addStyle();
        File sldFile = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld");
        sldFile.createNewFile();
        File conflictingFile = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/boostyle.sld");
        conflictingFile.createNewFile();
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setName("boostyle");
        catalog.save(s);
        File renamedSldFile = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/boostyle1.sld");
        Assert.assertThat(sldFile, Matchers.not(fileExists()));
        Assert.assertThat(renamedSldFile, fileExists());
    }

    @Test
    public void testRenameStyleWithExistingIncrementedVersion() throws Exception {
        addStyle();
        File sldFile = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld");
        sldFile.createNewFile();
        File sldFile1 = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle1.sld");
        sldFile1.createNewFile();
        File sldFile2 = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle2.sld");
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setName("foostyle");
        catalog.save(s);
        Assert.assertThat(sldFile, Matchers.not(fileExists()));
        Assert.assertThat(sldFile1, fileExists());
        Assert.assertThat(sldFile2, fileExists());
        sldFile1.delete();
    }

    @Test
    public void testModifyStyleChangeWorkspace() throws Exception {
        addStyle();
        // copy an sld into place
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("default_line.sld"), new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld"));
        Assert.assertTrue(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld").exists());
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.save(s);
        Assert.assertFalse(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld").exists());
        Assert.assertTrue(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/foostyle.sld").exists());
    }

    @Test
    public void testModifyStyleChangeWorkspaceToGlobal() throws Exception {
        addStyleWithWorkspace();
        // copy an sld into place
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("default_line.sld"), new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/foostyle.sld"));
        Assert.assertTrue(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/foostyle.sld").exists());
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(null);
        catalog.save(s);
        Assert.assertTrue(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld").exists());
        Assert.assertFalse(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/foostyle.sld").exists());
    }

    @Test
    public void testModifyStyleWithResourceChangeWorkspace() throws Exception {
        addStyle();
        // copy an sld with its resource into place
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("burg.sld"), new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld"));
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("burg02.svg"), new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"), fileExists());
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.save(s);
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld"), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/foostyle.sld"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/burg02.svg"), fileExists());
    }

    @Test
    public void testModifyStyleWithResourcesInParentDirChangeWorkspace() throws Exception {
        addStyle();
        // If a relative URI with parent references is used, give up on trying to copy the resource.
        // The style will break but copying arbitrary files from parent directories around is a bad
        // idea. Handle the rest normally. KS
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("burgParentReference.sld"), new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld"));
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("burg02.svg"), new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"));
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("burg02.svg"), new File(testSupport.getResourceLoader().getBaseDirectory(), "burg03.svg"));
        new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg03.svg").delete();
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "burg03.svg"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg03.svg"), Matchers.not(fileExists()));
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.save(s);
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld"), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "burg03.svg"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/foostyle.sld"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/burg03.svg"), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/burg03.svg"), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/burg02.svg"), fileExists());
    }

    @Test
    public void testModifyStyleWithResourcesAbsoluteChangeWorkspace() throws Exception {
        addStyle();
        // If an absolute uri is used, don't copy it anywhere. The reference is absolute
        // so it will still work.
        File styleFile = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld");
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("burgParentReference.sld"), styleFile);
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("burg02.svg"), new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"));
        File target = new File(testSupport.getResourceLoader().getBaseDirectory(), "burg03.svg");
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("burg02.svg"), target);
        // Insert an absolute path to test
        String content = new String(Files.readAllBytes(styleFile.toPath()), StandardCharsets.UTF_8);
        content = content.replaceAll("./burg03.svg", "http://doesnotexist.example.org/burg03.svg");
        Files.write(styleFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
        new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg03.svg").delete();
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"), fileExists());
        Assert.assertThat(target, fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg03.svg"), Matchers.not(fileExists()));
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.save(s);
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld"), Matchers.not(fileExists()));
        Assert.assertThat(target, fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/foostyle.sld"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/burg03.svg"), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/burg03.svg"), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), ("workspaces/gs" + (target.getPath()))), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), ("workspaces/gs/styles" + (target.getPath()))), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/burg02.svg"), fileExists());
    }

    @Test
    public void testModifyStyleWithResourcesRemoteChangeWorkspace() throws Exception {
        addStyle();
        // If an absolute uri is used, don't copy it anywhere. The reference is absolute
        // so it will still work.
        File styleFile = new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld");
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("burgRemoteReference.sld"), styleFile);
        FileUtils.copyURLToFile(GeoServerPersistersTest.class.getResource("burg02.svg"), new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"));
        new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg03.svg").delete();
        new File(testSupport.getResourceLoader().getBaseDirectory(), "burg03.svg").delete();
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg03.svg"), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "burg03.svg"), Matchers.not(fileExists()));
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.save(s);
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/foostyle.sld"), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "styles/burg02.svg"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/foostyle.sld"), fileExists());
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/burg03.svg"), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/burg03.svg"), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/example.com/burg03.svg"), Matchers.not(fileExists()));
        Assert.assertThat(new File(testSupport.getResourceLoader().getBaseDirectory(), "workspaces/gs/styles/burg02.svg"), fileExists());
    }
}

