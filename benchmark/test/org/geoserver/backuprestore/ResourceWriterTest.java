/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.backuprestore;


import Paths.BASE;
import StyleInfo.DEFAULT_POINT;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.IOException;
import org.geoserver.backuprestore.tasklet.CatalogBackupRestoreTasklet;
import org.geoserver.backuprestore.utils.BackupUtils;
import org.geoserver.backuprestore.writer.ResourceInfoAdditionalResourceWriter;
import org.geoserver.backuprestore.writer.StyleInfoAdditionalResourceWriter;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.LoggingInfo;
import org.geoserver.config.SettingsInfo;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.platform.resource.Files;
import org.geoserver.platform.resource.Paths;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alessio Fabiani, GeoSolutions
 */
public class ResourceWriterTest extends BackupRestoreTestSupport {
    protected static Backup backupFacade;

    @Test
    public void testResourceInfoAdditionalResourceWriter() throws IOException {
        Catalog cat = getCatalog();
        GeoServerDataDirectory dd = ResourceWriterTest.backupFacade.getGeoServerDataDirectory();
        GeoServerDataDirectory td = new GeoServerDataDirectory(BackupRestoreTestSupport.root);
        Resource srcTemplatesDir = BackupUtils.dir(dd.get(BASE), "templates");
        File srcTitleFtl = Resources.createNewFile(Files.asResource(new File(srcTemplatesDir.dir(), "title.ftl")));
        File srcHeaderFtl = Resources.createNewFile(Files.asResource(new File(Paths.toFile(dd.get(BASE).dir(), Paths.path("workspaces", "gs", "foo", "t1")), "header.ftl")));
        File srcFakeFtl = Resources.createNewFile(Files.asResource(new File(Paths.toFile(dd.get(BASE).dir(), Paths.path("workspaces", "gs", "foo", "t1")), "fake.ftl")));
        Assert.assertTrue(Resources.exists(Files.asResource(srcTitleFtl)));
        Assert.assertTrue(Resources.exists(Files.asResource(srcHeaderFtl)));
        Assert.assertTrue(Resources.exists(Files.asResource(srcFakeFtl)));
        FeatureTypeInfo ft = cat.getFeatureTypeByName("t1");
        Assert.assertNotNull(ft);
        ResourceInfoAdditionalResourceWriter riarw = new ResourceInfoAdditionalResourceWriter();
        riarw.writeAdditionalResources(ResourceWriterTest.backupFacade, td.get(BASE), ft);
        Resource trgTemplatesDir = BackupUtils.dir(td.get(BASE), "templates");
        Assert.assertTrue(Resources.exists(trgTemplatesDir));
        Resource trgTitleFtl = Files.asResource(new File(trgTemplatesDir.dir(), "title.ftl"));
        Resource trgHeaderFtl = Files.asResource(new File(Paths.toFile(td.get(BASE).dir(), Paths.path("workspaces", "gs", "foo", "t1")), "header.ftl"));
        Resource trgFakeFtl = Files.asResource(new File(Paths.toFile(td.get(BASE).dir(), Paths.path("workspaces", "gs", "foo", "t1")), "fake.ftl"));
        Assert.assertTrue(Resources.exists(trgTitleFtl));
        Assert.assertTrue(Resources.exists(trgHeaderFtl));
        Assert.assertTrue((!(Resources.exists(trgFakeFtl))));
    }

    @Test
    public void testStyleInfoAdditionalResourceWriter() throws IOException {
        GeoServerDataDirectory dd = ResourceWriterTest.backupFacade.getGeoServerDataDirectory();
        GeoServerDataDirectory td = new GeoServerDataDirectory(BackupRestoreTestSupport.root);
        StyleInfo style = BackupRestoreTestSupport.catalog.getStyleByName(DEFAULT_POINT);
        StyleInfoAdditionalResourceWriter siarw = new StyleInfoAdditionalResourceWriter();
        siarw.writeAdditionalResources(ResourceWriterTest.backupFacade, td.get(BASE), style);
        Resource srcStylesDir = BackupUtils.dir(dd.get(BASE), "styles");
        Resource trgStylesDir = BackupUtils.dir(td.get(BASE), "styles");
        Assert.assertTrue(Resources.exists(srcStylesDir));
        Assert.assertTrue(Resources.exists(trgStylesDir));
        Assert.assertTrue(Resources.exists(Files.asResource(new File(trgStylesDir.dir(), style.getFilename()))));
    }

    @Test
    public void testSidecarFilesWriter() throws Exception {
        CatalogBackupRestoreTasklet catalogTsklet = new CatalogBackupRestoreTasklet(ResourceWriterTest.backupFacade);
        File tmpDd = File.createTempFile("template", "tmp", new File("target"));
        tmpDd.delete();
        tmpDd.mkdir();
        GeoServerDataDirectory dd = new GeoServerDataDirectory(tmpDd);
        File tmpTd = File.createTempFile("template", "tmp", new File("target"));
        tmpTd.delete();
        tmpTd.mkdir();
        GeoServerDataDirectory td = new GeoServerDataDirectory(tmpTd);
        BackupUtils.extractTo(BackupRestoreTestSupport.file("data.zip"), dd.get(BASE));
        // Backup other configuration bits, like images, palettes, user projections and so on...
        catalogTsklet.backupRestoreAdditionalResources(dd.getResourceStore(), td.get(BASE));
        Assert.assertTrue(Resources.exists(Files.asResource(new File(td.get(BASE).dir(), "demo"))));
        Assert.assertTrue(Resources.exists(Files.asResource(new File(td.get(BASE).dir(), "images"))));
        Assert.assertTrue(Resources.exists(Files.asResource(new File(td.get(BASE).dir(), "logs"))));
        Assert.assertTrue(Resources.exists(Files.asResource(new File(td.get(BASE).dir(), "palettes"))));
        Assert.assertTrue(Resources.exists(Files.asResource(new File(td.get(BASE).dir(), "user_projections"))));
        Assert.assertTrue(Resources.exists(Files.asResource(new File(td.get(BASE).dir(), "validation"))));
        Assert.assertTrue(Resources.exists(Files.asResource(new File(td.get(BASE).dir(), "www"))));
    }

    @Test
    public void testGeoServerGlobalSettingsStorage() throws Exception {
        Catalog cat = getCatalog();
        GeoServer geoserver = getGeoServer();
        CatalogBackupRestoreTasklet catalogTsklet = new CatalogBackupRestoreTasklet(ResourceWriterTest.backupFacade);
        GeoServerDataDirectory td = new GeoServerDataDirectory(BackupRestoreTestSupport.root);
        catalogTsklet.doWrite(geoserver.getGlobal(), td.get(BASE), "global.xml");
        catalogTsklet.doWrite(geoserver.getSettings(), td.get(BASE), "settings.xml");
        catalogTsklet.doWrite(geoserver.getLogging(), td.get(BASE), "logging.xml");
        Assert.assertTrue(Resources.exists(Files.asResource(new File(td.get(BASE).dir(), "global.xml"))));
        Assert.assertTrue(Resources.exists(Files.asResource(new File(td.get(BASE).dir(), "settings.xml"))));
        Assert.assertTrue(Resources.exists(Files.asResource(new File(td.get(BASE).dir(), "logging.xml"))));
        XStreamPersister xstream = catalogTsklet.getxStreamPersisterFactory().createXMLPersister();
        xstream.setCatalog(cat);
        xstream.setReferenceByName(true);
        xstream.setExcludeIds();
        XStream xp = xstream.getXStream();
        GeoServerInfo gsGlobal = ((GeoServerInfo) (xp.fromXML(new File(td.get(BASE).dir(), "global.xml"))));
        Assert.assertNotNull(gsGlobal);
        SettingsInfo gsSettins = ((SettingsInfo) (xp.fromXML(new File(td.get(BASE).dir(), "settings.xml"))));
        Assert.assertNotNull(gsSettins);
        LoggingInfo gsLogging = ((LoggingInfo) (xp.fromXML(new File(td.get(BASE).dir(), "logging.xml"))));
        Assert.assertNotNull(gsLogging);
        Assert.assertEquals(geoserver.getGlobal(), gsGlobal);
        Assert.assertEquals(geoserver.getSettings(), gsSettins);
        Assert.assertEquals(geoserver.getLogging(), gsLogging);
        catalogTsklet.doWrite(cat.getDefaultWorkspace(), BackupUtils.dir(td.get(BASE), "workspaces"), "default.xml");
        Assert.assertTrue(Resources.exists(Files.asResource(new File(BackupUtils.dir(td.get(BASE), "workspaces").dir(), "default.xml"))));
        WorkspaceInfo defaultWorkspace = ((WorkspaceInfo) (xp.fromXML(new File(BackupUtils.dir(td.get(BASE), "workspaces").dir(), "default.xml"))));
        Assert.assertEquals(cat.getDefaultWorkspace().getName(), defaultWorkspace.getName());
    }
}

