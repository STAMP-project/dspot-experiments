/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.nsg.pagination.random;


import IndexConfigurationManager.STORE_SCHEMA_NAME;
import JDBCDataStoreFactory.DATABASE;
import JDBCDataStoreFactory.DATABASE.key;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.wicket.util.file.File;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.geoserver.wfs.v2_0.WFS20TestSupport;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.filter.text.cql2.CQL;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.w3c.dom.Document;

import static IndexConfigurationManager.MODULE_DIR;
import static IndexConfigurationManager.PROPERTY_DB_PREFIX;
import static IndexConfigurationManager.PROPERTY_FILENAME;


public class GetFeatureRequestStorageTest extends WFS20TestSupport {
    @Test
    public void testCleanOldRequest() throws Exception {
        Document doc = getAsDOM("ows?service=WFS&version=2.0.0&request=GetFeature&typeNames=cdf:Fifteen&resultType=index");
        String resultSetId = doc.getDocumentElement().getAttribute("resultSetID");
        IndexConfigurationManager ic = applicationContext.getBean(IndexConfigurationManager.class);
        SimpleFeature feature = getFeatureStore(ic).getFeatures(CQL.toFilter((("ID='" + resultSetId) + "'"))).features().next();
        Assert.assertNotNull(feature);
        // Change timeout from default to 5 seconds
        GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        GeoServerDataDirectory dd = new GeoServerDataDirectory(loader);
        Properties properties = new Properties();
        Resource resource = dd.get((((MODULE_DIR) + "/") + (PROPERTY_FILENAME)));
        InputStream is = resource.in();
        properties.load(is);
        is.close();
        properties.put("resultSets.timeToLive", "1");
        OutputStream out = resource.out();
        properties.store(out, null);
        out.close();
        final CountDownLatch done1 = new CountDownLatch(1);
        new Thread(() -> {
            while (true) {
                try {
                    Long ttl = ic.getTimeToLiveInSec();
                    if (ttl == 1) {
                        done1.countDown();
                        break;
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            } 
        }).start();
        done1.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(Long.valueOf(1), ic.getTimeToLiveInSec());
        // Check that feature not used is deleted
        final CountDownLatch done2 = new CountDownLatch(1);
        new Thread(() -> {
            while (true) {
                try {
                    boolean exists = getFeatureStore(ic).getFeatures(CQL.toFilter((("ID='" + resultSetId) + "'"))).features().hasNext();
                    if (!exists) {
                        done2.countDown();
                        break;
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            } 
        }).start();
        done2.await(10, TimeUnit.SECONDS);
        boolean exists = getFeatureStore(ic).getFeatures(CQL.toFilter((("ID='" + resultSetId) + "'"))).features().hasNext();
        Assert.assertFalse(exists);
    }

    @Test
    public void testConcurrentChangeDatabaseParameters() throws Exception {
        GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        GeoServerDataDirectory dd = new GeoServerDataDirectory(loader);
        Resource resource = dd.get((((MODULE_DIR) + "/") + (PROPERTY_FILENAME)));
        Properties properties = new Properties();
        InputStream is = resource.in();
        properties.load(is);
        is.close();
        properties.put(((PROPERTY_DB_PREFIX) + (DATABASE.key)), ((dd.root().getPath()) + "/nsg-profile/db/resultSets2"));
        IndexConfigurationManager ic = applicationContext.getBean(IndexConfigurationManager.class);
        ExecutorCompletionService<Object> es = new ExecutorCompletionService<>(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        final int REQUESTS = 100;
        for (int i = 0; i < REQUESTS; i++) {
            int count = i;
            es.submit(() -> {
                getAsDOM("ows?service=WFS&version=2.0.0&request=GetFeature&typeNames=cdf:Fifteen&resultType=index");
                if (count == (REQUESTS / 2)) {
                    // Change database
                    OutputStream out = resource.out();
                    properties.store(out, null);
                    out.close();
                }
                return null;
            });
        }
        // just check there are no exceptions
        for (int i = 0; i < REQUESTS; i++) {
            es.take().get();
        }
        // wait for listener receive database notification changes
        File dbDataFile = new File(((dd.root().getPath()) + "/nsg-profile/db/resultSets2.data.db"));
        final CountDownLatch done = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Map<String, Object> params = ic.getCurrentDataStoreParams();
                    boolean condition = (((dd.root().getPath()) + "/nsg-profile/db/resultSets2").equals(params.get(key))) && (dbDataFile.exists());
                    if (condition) {
                        done.countDown();
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }
                } 
            }
        }).start();
        done.await(20, TimeUnit.SECONDS);
        DataStore dataStore = ic.getCurrentDataStore();
        Assert.assertTrue(dbDataFile.exists());
        Map<String, Object> params = ic.getCurrentDataStoreParams();
        Assert.assertEquals(((dd.root().getPath()) + "/nsg-profile/db/resultSets2"), params.get(key));
        SimpleFeatureStore featureStore = ((SimpleFeatureStore) (dataStore.getFeatureSource(STORE_SCHEMA_NAME)));
        Assert.assertEquals(REQUESTS, featureStore.getFeatures().size());
    }
}

