/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.jdbcstore;


import Kind.ENTRY_CREATE;
import Kind.ENTRY_DELETE;
import Kind.ENTRY_MODIFY;
import Paths.BASE;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import org.geoserver.catalog.impl.StyleInfoImpl;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.config.GeoServerDataDirectoryTest;
import org.geoserver.jdbcstore.cache.SimpleResourceCache;
import org.geoserver.jdbcstore.internal.JDBCResourceStoreProperties;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.DataDirectoryResourceStore;
import org.geoserver.platform.resource.FileSystemResourceStore;
import org.geoserver.platform.resource.NullLockProvider;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.ResourceListener;
import org.geoserver.platform.resource.ResourceNotification;
import org.geoserver.platform.resource.ResourceNotification.Event;
import org.geoserver.platform.resource.ResourceStore;
import org.geoserver.platform.resource.Resources;
import org.geoserver.util.IOUtils;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.geotools.util.Version;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.opengis.style.GraphicalSymbol;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author Kevin Smith, Boundless
 * @author Niels Charlier
 */
public abstract class AbstractJDBCResourceStoreTest {
    DatabaseTestSupport support;

    @Test
    public void testInitializeEmptyDB() throws Exception {
        JDBCResourceStoreProperties config = getConfig(true, true);
        @SuppressWarnings("unused")
        ResourceStore store = new JDBCResourceStore(support.getDataSource(), config);
        // Check that the database has a resources table with a root record
        ResultSet rs = support.getConnection().createStatement().executeQuery("SELECT * from resources where oid = 0");
        Assert.assertThat(rs.next(), describedAs("found root record", is(true)));
        Assert.assertThat(rs.getString("name"), equalTo(""));
        rs.getInt("parent");
        Assert.assertThat(rs.wasNull(), is(true));
        Assert.assertThat(rs.getBlob("content"), nullValue());
        Assert.assertThat(rs.next(), describedAs("only one root", is(false)));
    }

    @Test
    public void testAcceptInitializedDB() throws Exception {
        standardData();
        JDBCResourceStoreProperties config = getConfig(true, false);
        JDBCResourceStore store = new JDBCResourceStore(support.getDataSource(), config);
        store.setLockProvider(new NullLockProvider());
        {
            // Check that the database has a resources table with a root record
            ResultSet rs = support.getConnection().createStatement().executeQuery("SELECT * from resources where oid = 0");
            Assert.assertThat(rs.next(), describedAs("found root record", is(true)));
            Assert.assertThat(rs.getString("name"), equalTo(""));
            rs.getInt("parent");
            Assert.assertThat(rs.wasNull(), is(true));
            Assert.assertThat(rs.getBlob("content"), nullValue());
            Assert.assertThat(rs.next(), describedAs("only one root", is(false)));
        }
        {
            // Check that the database has one of the child nodes
            ResultSet rs = support.getConnection().createStatement().executeQuery("SELECT * from resources where parent = 0 and name='FileA'");
            Assert.assertThat(rs.next(), describedAs("found child FileA", is(true)));
            Assert.assertThat(rs.getString("name"), equalTo("FileA"));
            Assert.assertThat(rs.getInt("parent"), equalTo(0));
            Assert.assertThat(rs.wasNull(), is(false));
            Assert.assertThat(rs.getBinaryStream("content"), not(nullValue()));
            Assert.assertThat(rs.getInt("oid"), not(equalTo(0)));
        }
    }

    @Test
    public void testInitializeDatabaseWithIrrelevantTable() throws Exception {
        support.getConnection().createStatement().execute("CREATE TABLE foo (oid INTEGER PRIMARY KEY);");
        JDBCResourceStoreProperties config = getConfig(true, true);
        JDBCResourceStore store = new JDBCResourceStore(support.getDataSource(), config);
        store.setLockProvider(new NullLockProvider());
        {
            // Check that the database has a resources table with a root record
            ResultSet rs = support.getConnection().createStatement().executeQuery("SELECT * from resources where oid = 0");
            Assert.assertThat(rs.next(), describedAs("found root record", is(true)));
            Assert.assertThat(rs.getString("name"), equalTo(""));
            rs.getInt("parent");
            Assert.assertThat(rs.wasNull(), is(true));
            Assert.assertThat(rs.getBlob("content"), nullValue());
            Assert.assertThat(rs.next(), describedAs("only one root", is(false)));
        }
    }

    @Test
    public void testBasicResourceQuery() throws Exception {
        standardData();
        JDBCResourceStoreProperties config = getConfig(true, false);
        JDBCResourceStore store = new JDBCResourceStore(support.getDataSource(), config);
        store.setLockProvider(new NullLockProvider());
        Resource r = store.get("FileA");
        Assert.assertThat(r, not(nullValue()));
        Assert.assertThat(r, resource());
    }

    @Test
    public void testBasicDirectoryQuery() throws Exception {
        standardData();
        JDBCResourceStoreProperties config = getConfig(true, false);
        JDBCResourceStore store = new JDBCResourceStore(support.getDataSource(), config);
        store.setLockProvider(new NullLockProvider());
        Resource r = store.get("DirE");
        Assert.assertThat(r, not(nullValue()));
        Assert.assertThat(r, directory());
    }

    @Test
    public void testBasicUndefinedQuery() throws Exception {
        standardData();
        JDBCResourceStoreProperties config = getConfig(true, false);
        JDBCResourceStore store = new JDBCResourceStore(support.getDataSource(), config);
        store.setLockProvider(new NullLockProvider());
        Resource r = store.get("DoesntExist");
        Assert.assertThat(r, not(nullValue()));
        Assert.assertThat(r, undefined());
    }

    @Test
    public void testLongQuery() throws Exception {
        standardData();
        JDBCResourceStoreProperties config = getConfig(true, false);
        JDBCResourceStore store = new JDBCResourceStore(support.getDataSource(), config);
        store.setLockProvider(new NullLockProvider());
        Resource r = store.get("DirC/DirF/DirG/FileH");
        Assert.assertThat(r, not(nullValue()));
        Assert.assertThat(r, resource());
    }

    @Test
    public void testBasicRead() throws Exception {
        standardData();
        JDBCResourceStoreProperties config = getConfig(true, false);
        JDBCResourceStore store = new JDBCResourceStore(support.getDataSource(), config);
        store.setLockProvider(new NullLockProvider());
        Resource r = store.get("FileA");
        byte[] expected = "FileA Contents".getBytes();
        InputStream in = r.in();
        try {
            byte[] result = new byte[expected.length];
            Assert.assertThat(in.read(result), describedAs("file contents same length", equalTo(expected.length)));
            Assert.assertThat(result, equalTo(expected));
            Assert.assertThat(in.read(), describedAs("stream is empty", equalTo((-1))));
        } finally {
            in.close();
        }
    }

    @Test
    public void testCache() throws Exception {
        standardData();
        cache.create();
        JDBCResourceStoreProperties config = getConfig(true, false);
        ResourceStore fileStore = new FileSystemResourceStore(cache.getRoot());
        ResourceStore jdbcStore = new JDBCResourceStore(support.getDataSource(), config, fileStore);
        ((JDBCResourceStore) (jdbcStore)).setCache(new SimpleResourceCache(cache.getRoot()));
        // Initialize FileA in cache
        Resource jdbcResource = jdbcStore.get("FileA");
        jdbcResource.file();
        // Make sure the timestamp is different
        Thread.sleep(2);
        // Update the Resource in the JDBCStore
        byte[] expected = "FileA Updated Contents".getBytes();
        OutputStream out = jdbcResource.out();
        try {
            out.write(expected);
        } finally {
            out.close();
        }
        // Force an update to the cache
        jdbcResource.file();
        // Verify this update actually occurs
        Resource fileResource = fileStore.get("FileA");
        InputStream in = fileResource.in();
        try {
            byte[] result = new byte[expected.length];
            in.read(result);
            Assert.assertThat(result, equalTo(expected));
        } finally {
            in.close();
        }
    }

    @Test
    public void testDelete() throws Exception {
        standardData();
        cache.create();
        JDBCResourceStoreProperties config = getConfig(true, false);
        ResourceStore fileStore = new FileSystemResourceStore(cache.getRoot());
        ResourceStore jdbcStore = new JDBCResourceStore(support.getDataSource(), config, fileStore);
        ((JDBCResourceStore) (jdbcStore)).setCache(new SimpleResourceCache(cache.getRoot()));
        // Initialize FileA in cache
        Resource jdbcResource = jdbcStore.get("FileA");
        // Update the Resource in the JDBCStore
        jdbcResource.delete();
        // Verify this update actually occurs
        Resource fileResource = fileStore.get("FileA");
        Assert.assertThat(fileResource.getType(), equalTo(Resource.Type.UNDEFINED));
    }

    @Test
    public void testIgnoreDir() throws Exception {
        support.initialize();
        JDBCResourceStoreProperties config = getConfig(true, false);
        ResourceStore dataDirStore = new DataDirectoryResourceStore();
        ResourceStore store = new JDBCResourceStore(support.getDataSource(), config, dataDirStore);
        Assert.assertEquals(store.get("DirIgnore"), dataDirStore.get("DirIgnore"));
        Assert.assertEquals(store.get("DirIgnore/myfile"), dataDirStore.get("DirIgnore/myfile"));
        Assert.assertNotEquals(store.get("DirDontIgnore"), dataDirStore.get("DirDontIgnore"));
    }

    private static class TestResourceListener implements ResourceListener {
        private ResourceNotification notify;

        @Override
        public void changed(ResourceNotification notify) {
            this.notify = notify;
        }

        public ResourceNotification getNotify() {
            return notify;
        }

        public void reset() {
            notify = null;
        }
    }

    @Test
    public void fileEvents() throws Exception {
        standardData();
        ResourceStore store = new JDBCResourceStore(support.getDataSource(), getConfig(false, false));
        AbstractJDBCResourceStoreTest.TestResourceListener listener = new AbstractJDBCResourceStoreTest.TestResourceListener();
        Resource fileD = store.get("DirC/FileD");
        fileD.addListener(listener);
        long before = fileD.lastmodified();
        try (OutputStream out = fileD.out()) {
            out.write(1234);
        }
        long after = fileD.lastmodified();
        Assert.assertTrue((after > before));
        Assert.assertNotNull(listener.getNotify());
        Assert.assertEquals(ENTRY_MODIFY, listener.getNotify().getKind());
        Assert.assertEquals(1, listener.getNotify().events().size());
        long timeStamp = listener.getNotify().getTimestamp();
        Assert.assertTrue((timeStamp > before));
        listener.reset();
        fileD.delete();
        Assert.assertNotNull(listener.getNotify());
        Assert.assertEquals(ENTRY_DELETE, listener.getNotify().getKind());
        Assert.assertEquals(1, listener.getNotify().events().size());
        listener.reset();
        try (OutputStream out = fileD.out()) {
            Assert.assertNotNull(listener.getNotify());
            Assert.assertEquals(ENTRY_CREATE, listener.getNotify().getKind());
            Assert.assertEquals(1, listener.getNotify().events().size());
        }
        listener.reset();
        Resource fileE = store.get("DirC/FileE");
        AbstractJDBCResourceStoreTest.TestResourceListener listener2 = new AbstractJDBCResourceStoreTest.TestResourceListener();
        fileE.addListener(listener2);
        fileD.renameTo(fileE);
        Assert.assertNotNull(listener.getNotify());
        Assert.assertEquals(ENTRY_DELETE, listener.getNotify().getKind());
        Assert.assertNotNull(listener2.getNotify());
        Assert.assertEquals(ENTRY_CREATE, listener2.getNotify().getKind());
        fileD.removeListener(listener);
        fileE.removeListener(listener2);
    }

    @Test
    public void directoryEvents() throws Exception {
        standardData();
        ResourceStore store = new JDBCResourceStore(support.getDataSource(), getConfig(false, false));
        Resource fileA = store.get("FileA");
        Resource fileD = store.get("DirC/FileD");
        AbstractJDBCResourceStoreTest.TestResourceListener listener = new AbstractJDBCResourceStoreTest.TestResourceListener();
        store.get("DirC").addListener(listener);
        long before = fileD.lastmodified();
        try (OutputStream out = fileD.out()) {
            out.write(1234);
        }
        long after = fileD.lastmodified();
        Assert.assertTrue((after > before));
        Assert.assertNotNull(listener.getNotify());
        Assert.assertEquals(ENTRY_MODIFY, listener.getNotify().getKind());
        Assert.assertEquals(1, listener.getNotify().events().size());
        Assert.assertEquals("DirC", listener.getNotify().getPath());
        long timeStamp = listener.getNotify().getTimestamp();
        Assert.assertTrue((timeStamp > before));
        Event e = listener.getNotify().events().get(0);
        Assert.assertEquals(ENTRY_MODIFY, e.getKind());
        Assert.assertEquals("FileD", e.getPath());
        listener.reset();
        store.get("DirC").removeListener(listener);
        store.get(BASE).addListener(listener);
        fileA.delete();
        Assert.assertNotNull(listener.getNotify());
        Assert.assertEquals(ENTRY_MODIFY, listener.getNotify().getKind());
        Assert.assertEquals(1, listener.getNotify().events().size());
        Assert.assertEquals(BASE, listener.getNotify().getPath());
        e = listener.getNotify().events().get(0);
        Assert.assertEquals(ENTRY_DELETE, e.getKind());
        Assert.assertEquals("FileA", e.getPath());
        listener.reset();
        try (OutputStream out = fileA.out()) {
            Assert.assertEquals(ENTRY_MODIFY, listener.getNotify().getKind());
            Assert.assertEquals(1, listener.getNotify().events().size());
            Assert.assertEquals(BASE, listener.getNotify().getPath());
            e = listener.getNotify().events().get(0);
            Assert.assertEquals(ENTRY_CREATE, e.getKind());
            Assert.assertEquals("FileA", e.getPath());
        }
        store.get(BASE).removeListener(listener);
    }

    @Rule
    public TemporaryFolder cache = new TemporaryFolder();

    @Test
    public void testParsedStyle() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("GeoServerDataDirectoryTest-applicationContext.xml", GeoServerDataDirectoryTest.class);
        ctx.refresh();
        support.initialize();
        cache.create();
        JDBCResourceStore store = new JDBCResourceStore(support.getDataSource(), getConfig(false, false));
        store.setCache(new SimpleResourceCache(cache.getRoot()));
        GeoServerResourceLoader loader = new GeoServerResourceLoader(store);
        GeoServerDataDirectory dataDir = new GeoServerDataDirectory(loader);
        Resource styleDir = store.get("styles");
        // Copy the sld to the temp style dir
        Resource styleResource = styleDir.get("external.sld");
        IOUtils.copy(getClass().getResourceAsStream("external.sld"), styleResource.out());
        Resource noIconResource = styleDir.get("noicon.png");
        Assert.assertFalse(Resources.exists(noIconResource));
        Resource iconResource = styleDir.get("icon.png");
        IOUtils.copy(getClass().getResourceAsStream("icon.png"), iconResource.out());
        Assert.assertTrue(Resources.exists(iconResource));
        StyleInfoImpl si = new StyleInfoImpl(null);
        si.setName("");
        si.setId("");
        si.setFormat("sld");
        si.setFormatVersion(new Version("1.0.0"));
        si.setFilename(styleResource.name());
        Style s = dataDir.parsedStyle(si);
        // Verify style is actually parsed correctly
        Symbolizer symbolizer = s.featureTypeStyles().get(0).rules().get(0).symbolizers().get(0);
        Assert.assertTrue((symbolizer instanceof PointSymbolizer));
        GraphicalSymbol graphic = getGraphic().graphicalSymbols().get(0);
        Assert.assertTrue((graphic instanceof ExternalGraphic));
        File iconFile = new File(cache.getRoot(), "styles/icon.png");
        File noiconFile = new File(cache.getRoot(), "styles/noicon.png");
        // GEOS-7025: verify the icon file is not created if it doesn't exist in store
        Assert.assertFalse(noiconFile.exists());
        // GEOS-7741: verify the icon file is created if it does exist in store
        Assert.assertTrue(iconFile.exists());
        ctx.destroy();
        ctx.close();
    }
}

