/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.external;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geotools.feature.NameImpl;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class ExtTypesTest extends AbstractTaskManagerTest {
    @Autowired
    ExtTypes extTypes;

    @Test
    public void testDbName() {
        List<String> domain = extTypes.dbName.getDomain(null);
        Assert.assertEquals(4, domain.size());
        Assert.assertEquals("myjndidb", domain.get(0));
        Assert.assertEquals("mypostgresdb", domain.get(1));
        Assert.assertEquals("testsourcedb", domain.get(2));
        Assert.assertEquals("testtargetdb", domain.get(3));
        Assert.assertFalse(extTypes.dbName.validate("doesntexist", null));
        Assert.assertTrue(extTypes.dbName.validate("myjndidb", null));
        Assert.assertTrue(((extTypes.dbName.parse("myjndidb", null)) instanceof DbSource));
        Assert.assertNull(extTypes.dbName.parse("doesntexist", null));
    }

    @Test
    public void testDbNameWithLogin() {
        login("someone", "pw", "ROLE_SOMEONE");
        List<String> domain = extTypes.dbName.getDomain(null);
        Assert.assertEquals(3, domain.size());
        Assert.assertEquals("mypostgresdb", domain.get(0));
        Assert.assertEquals("testsourcedb", domain.get(1));
        Assert.assertEquals("testtargetdb", domain.get(2));
        Assert.assertFalse(extTypes.dbName.validate("myjndidb", null));
        Assert.assertNull(extTypes.dbName.parse("myjndidb", null));
        login("someone_else", "pw", "ROLE_SOMEONE_ELSE");
        domain = extTypes.dbName.getDomain(null);
        Assert.assertEquals(3, domain.size());
        Assert.assertEquals("myjndidb", domain.get(0));
        Assert.assertEquals("mypostgresdb", domain.get(1));
        Assert.assertEquals("testsourcedb", domain.get(2));
        Assert.assertFalse(extTypes.dbName.validate("testtargetdb", null));
        Assert.assertNull(extTypes.dbName.parse("testtargetdb", null));
        logout();
    }

    @Test
    public void testTableName() {
        List<String> domain = extTypes.tableName.getDomain(Collections.singletonList("myjndidb"));
        Assert.assertEquals("", domain.get(0));
        final String tableName = "grondwaterlichamen_new";
        Assert.assertTrue(extTypes.tableName.validate("doesntexist", Collections.singletonList("myjndidb")));
        Assert.assertTrue(extTypes.tableName.validate(tableName, Collections.singletonList("myjndidb")));
        DbSource source = ((DbSource) (extTypes.dbName.parse("myjndidb", null)));
        try (Connection conn = source.getDataSource().getConnection()) {
            try (ResultSet res = conn.getMetaData().getTables(null, null, tableName, null)) {
                Assume.assumeTrue(res.next());
            }
        } catch (SQLException e) {
            Assume.assumeTrue(false);
        }
        Assert.assertTrue(domain.contains(tableName));
        Assert.assertTrue(((extTypes.tableName.parse(tableName, Collections.singletonList("myjndidb"))) instanceof DbTable));
        Assert.assertTrue(((extTypes.tableName.parse("doesntexist", Collections.singletonList("myjndidb"))) instanceof DbTable));
    }

    @Test
    public void testExtGeoserver() {
        List<String> domain = extTypes.extGeoserver.getDomain(null);
        Assert.assertEquals(1, domain.size());
        Assert.assertEquals("mygs", domain.get(0));
        Assert.assertTrue(extTypes.extGeoserver.validate("mygs", null));
        Assert.assertFalse(extTypes.extGeoserver.validate("doesntexist", null));
        Assert.assertTrue(((extTypes.extGeoserver.parse("mygs", null)) instanceof ExternalGS));
        Assert.assertNull(extTypes.extGeoserver.parse("doesntexist", null));
    }

    @Test
    public void testInternalLayer() {
        List<String> domain = extTypes.internalLayer.getDomain(null);
        Assert.assertEquals(4, domain.size());
        Assert.assertEquals("wcs:BlueMarble", domain.get(0));
        Assert.assertEquals("wcs:DEM", domain.get(1));
        Assert.assertEquals("wcs:RotatedCad", domain.get(2));
        Assert.assertEquals("wcs:World", domain.get(3));
        Assert.assertTrue(extTypes.internalLayer.validate("wcs:BlueMarble", null));
        Assert.assertFalse(extTypes.internalLayer.validate("doesntexist", null));
        Assert.assertTrue(((extTypes.internalLayer.parse("wcs:BlueMarble", null)) instanceof LayerInfo));
        Assert.assertNull(extTypes.internalLayer.parse("doesntexist", null));
    }

    @Test
    public void testNames() {
        Assert.assertTrue(extTypes.name.validate("bla", null));
        Assert.assertTrue(extTypes.name.validate("gs:bla", null));
        Assert.assertFalse(extTypes.name.validate("doesntexist:bla", null));
        Assert.assertEquals(new NameImpl("http://geoserver.org", "bla"), extTypes.name.parse("bla", null));
        Assert.assertEquals(new NameImpl("http://geoserver.org", "bla"), extTypes.name.parse("gs:bla", null));
    }

    @Test
    public void testFileServices() {
        List<String> domain = extTypes.fileService.getDomain(null);
        Assert.assertEquals(2, domain.size());
        Assert.assertEquals("data-directory", domain.get(0));
        Assert.assertEquals("temp-directory", domain.get(1));
        Assert.assertTrue(extTypes.fileService.validate("data-directory", null));
        Assert.assertFalse(extTypes.fileService.validate("doesntexist", null));
        Assert.assertTrue(((extTypes.fileService.parse("data-directory", null)) instanceof FileService));
        Assert.assertNull(extTypes.fileService.parse("doesntexist", null));
    }

    @Test
    public void testFile() throws IOException {
        FileService service = ((FileService) (extTypes.fileService.parse("data-directory", null)));
        try (InputStream is = new ByteArrayInputStream("test".getBytes())) {
            service.create("temp", is);
        }
        Assert.assertTrue(extTypes.file(true, false).validate("temp", Collections.singletonList("data-directory")));
        Assert.assertTrue(extTypes.file(true, false).validate("doesntexist", Collections.singletonList("data-directory")));
        Assert.assertTrue(((extTypes.file(true, false).parse("temp", Collections.singletonList("data-directory"))) instanceof FileReference));
        Assert.assertNull(extTypes.file(true, false).parse("doesntexist", Collections.singletonList("data-directory")));
        Assert.assertTrue(((extTypes.file(false, false).parse("doesntexist", Collections.singletonList("data-directory"))) instanceof FileReference));
        Assert.assertEquals("temp.1", getNextVersion());
    }
}

