/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc;


import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.geoserver.gwc.config.GWCConfig;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.util.logging.Logging;
import org.geowebcache.io.ByteArrayResource;
import org.geowebcache.io.Resource;
import org.geowebcache.storage.BlobStore;
import org.geowebcache.storage.BlobStoreListener;
import org.geowebcache.storage.TileObject;
import org.geowebcache.storage.blobstore.file.FileBlobStore;
import org.geowebcache.storage.blobstore.memory.CacheProvider;
import org.geowebcache.storage.blobstore.memory.MemoryBlobStore;
import org.geowebcache.storage.blobstore.memory.NullBlobStore;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * This class tests the functionalities of the {@link ConfigurableBlobStore} class.
 *
 * @author Nicola Lagomarsini Geosolutions
 */
public class ConfigurableBlobStoreTest extends GeoServerSystemTestSupport {
    /**
     * {@link Logger} used for reporting exceptions
     */
    private static final Logger LOGGER = Logging.getLogger(ConfigurableBlobStoreTest.class);

    /**
     * Name of the test directory
     */
    public static final String TEST_BLOB_DIR_NAME = "gwcTestBlobs";

    /**
     * {@link CacheProvider} object used for testing purposes
     */
    private static CacheProvider cache;

    private BlobStore defaultStore;

    /**
     * {@link ConfigurableBlobStore} object to test
     */
    private static ConfigurableBlobStore blobStore;

    /**
     * Directory containing files for the {@link FileBlobStore}
     */
    private File directory;

    @Test
    public void testNullStore() throws Exception {
        // Configure the blobstore
        GWCConfig gwcConfig = new GWCConfig();
        gwcConfig.setInnerCachingEnabled(true);
        gwcConfig.setEnabledPersistence(false);
        ConfigurableBlobStoreTest.blobStore.setChanged(gwcConfig, false);
        BlobStore delegate = ConfigurableBlobStoreTest.blobStore.getDelegate();
        Assert.assertTrue((delegate instanceof MemoryBlobStore));
        Assert.assertTrue(((getStore()) instanceof NullBlobStore));
        // Put a TileObject
        Resource bytes = new ByteArrayResource("1 2 3 4 5 6 test".getBytes());
        long[] xyz = new long[]{ 1L, 2L, 3L };
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("a", "x");
        parameters.put("b", "?");
        TileObject to = TileObject.createCompleteTileObject("test:123123 112", xyz, "EPSG:4326", "image/jpeg", parameters, bytes);
        ConfigurableBlobStoreTest.blobStore.put(to);
        // Try to get the Tile Object
        TileObject to2 = TileObject.createQueryTileObject("test:123123 112", xyz, "EPSG:4326", "image/jpeg", parameters);
        ConfigurableBlobStoreTest.blobStore.get(to2);
        // Check formats
        Assert.assertEquals(to.getBlobFormat(), to2.getBlobFormat());
        // Check if the resources are equals
        InputStream is = to.getBlob().getInputStream();
        InputStream is2 = to2.getBlob().getInputStream();
        checkInputStreams(is, is2);
        // Ensure Cache contains the result
        TileObject to3 = ConfigurableBlobStoreTest.cache.getTileObj(to);
        Assert.assertNotNull(to3);
        Assert.assertEquals(to.getBlobFormat(), to3.getBlobFormat());
        // Check if the resources are equals
        is = to.getBlob().getInputStream();
        InputStream is3 = to3.getBlob().getInputStream();
        checkInputStreams(is, is3);
        // Ensure that NullBlobStore does not contain anything
        Assert.assertFalse(getStore().get(to));
    }

    @Test
    public void testTilePut() throws Exception {
        // Configure the blobstore
        GWCConfig gwcConfig = new GWCConfig();
        gwcConfig.setInnerCachingEnabled(true);
        gwcConfig.setEnabledPersistence(true);
        ConfigurableBlobStoreTest.blobStore.setChanged(gwcConfig, false);
        Assert.assertTrue(((ConfigurableBlobStoreTest.blobStore.getDelegate()) instanceof MemoryBlobStore));
        // Put a TileObject
        Resource bytes = new ByteArrayResource("1 2 3 4 5 6 test".getBytes());
        long[] xyz = new long[]{ 1L, 2L, 3L };
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("a", "x");
        parameters.put("b", "?");
        TileObject to = TileObject.createCompleteTileObject("test:123123 112", xyz, "EPSG:4326", "image/jpeg", parameters, bytes);
        ConfigurableBlobStoreTest.blobStore.put(to);
        // Try to get the Tile Object
        TileObject to2 = TileObject.createQueryTileObject("test:123123 112", xyz, "EPSG:4326", "image/jpeg", parameters);
        ConfigurableBlobStoreTest.blobStore.get(to2);
        // Check formats
        Assert.assertEquals(to.getBlobFormat(), to2.getBlobFormat());
        // Check if the resources are equals
        InputStream is = to.getBlob().getInputStream();
        InputStream is2 = to2.getBlob().getInputStream();
        checkInputStreams(is, is2);
        // Ensure Cache contains the result
        TileObject to3 = ConfigurableBlobStoreTest.cache.getTileObj(to);
        Assert.assertNotNull(to3);
        Assert.assertEquals(to.getBlobFormat(), to3.getBlobFormat());
        is = to.getBlob().getInputStream();
        InputStream is3 = to3.getBlob().getInputStream();
        checkInputStreams(is, is3);
    }

    @Test
    public void testTileDelete() throws Exception {
        GWCConfig gwcConfig = new GWCConfig();
        gwcConfig.setInnerCachingEnabled(false);
        ConfigurableBlobStoreTest.blobStore.setChanged(gwcConfig, false);
        Assert.assertTrue(((ConfigurableBlobStoreTest.blobStore.getDelegate()) instanceof FileBlobStore));
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("a", "x");
        parameters.put("b", "?");
        // Put a TileObject
        Resource bytes = new ByteArrayResource("1 2 3 4 5 6 test".getBytes());
        long[] xyz = new long[]{ 5L, 6L, 7L };
        TileObject to = TileObject.createCompleteTileObject("test:123123 112", xyz, "EPSG:4326", "image/jpeg", parameters, bytes);
        ConfigurableBlobStoreTest.blobStore.put(to);
        // Try to get the Tile Object
        TileObject to2 = TileObject.createQueryTileObject("test:123123 112", xyz, "EPSG:4326", "image/jpeg", parameters);
        ConfigurableBlobStoreTest.blobStore.get(to2);
        // Check if the resources are equals
        InputStream is = to2.getBlob().getInputStream();
        InputStream is2 = bytes.getInputStream();
        checkInputStreams(is, is2);
        // Remove TileObject
        TileObject to3 = TileObject.createQueryTileObject("test:123123 112", xyz, "EPSG:4326", "image/jpeg", parameters);
        ConfigurableBlobStoreTest.blobStore.delete(to3);
        // Ensure TileObject is no more present
        TileObject to4 = TileObject.createQueryTileObject("test:123123 112", xyz, "EPSG:4326", "image/jpeg", parameters);
        Assert.assertFalse(ConfigurableBlobStoreTest.blobStore.get(to4));
    }

    @Test
    public void testListeners() throws Exception {
        // Configure the blobstore
        GWCConfig gwcConfig = new GWCConfig();
        gwcConfig.setInnerCachingEnabled(true);
        gwcConfig.setEnabledPersistence(true);
        ConfigurableBlobStoreTest.blobStore.setChanged(gwcConfig, false);
        BlobStoreListener l1 = Mockito.mock(BlobStoreListener.class);
        BlobStoreListener l2 = Mockito.mock(BlobStoreListener.class);
        Assert.assertTrue(((ConfigurableBlobStoreTest.blobStore.getDelegate()) instanceof MemoryBlobStore));
        ConfigurableBlobStoreTest.blobStore.addListener(l1);
        ConfigurableBlobStoreTest.blobStore.addListener(l2);
        Mockito.verify(defaultStore, Mockito.times(2)).addListener(Mockito.any(BlobStoreListener.class));
        Mockito.reset(defaultStore);
        // change the configuration
        GWCConfig newConfig = new GWCConfig();
        newConfig.setInnerCachingEnabled(false);
        newConfig.setEnabledPersistence(true);
        ConfigurableBlobStoreTest.blobStore.setChanged(newConfig, false);
        Assert.assertFalse(((ConfigurableBlobStoreTest.blobStore.getDelegate()) instanceof MemoryBlobStore));
        Mockito.verify(defaultStore, Mockito.times(2)).removeListener(Mockito.any(BlobStoreListener.class));
        Mockito.verify(defaultStore, Mockito.times(2)).addListener(Mockito.any(BlobStoreListener.class));
    }
}

