/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache;


import AttributesFactory.DEFAULT_DISK_SYNCHRONOUS;
import DataPolicy.DEFAULT;
import DataPolicy.PERSISTENT_REPLICATE;
import DataPolicy.PRELOADED;
import DataPolicy.REPLICATE;
import DiskStoreFactory.DEFAULT_DISK_DIR_SIZE;
import MirrorType.KEYS;
import MirrorType.NONE;
import Scope.DISTRIBUTED_NO_ACK;
import Scope.LOCAL;
import java.io.File;
import java.util.Arrays;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.geode.cache.util.CacheWriterAdapter;
import org.apache.geode.compression.SnappyCompressor;
import org.junit.Assert;
import org.junit.Test;

import static ExpirationAction.LOCAL_DESTROY;
import static ExpirationAction.LOCAL_INVALIDATE;
import static InterestPolicy.ALL;


/**
 * Tests the functionality of the {@link AttributesFactory} class.
 *
 * @since GemFire 3.0
 */
public class AttributesFactoryJUnitTest {
    @Test
    public void testCopyConstructor() {
        AttributesFactory f1 = new AttributesFactory();
        f1.setLockGrantor(true);
        RegionAttributes origAttrs = f1.create();
        Assert.assertEquals(true, origAttrs.isLockGrantor());
        AttributesFactory f2 = new AttributesFactory(origAttrs);
        RegionAttributes attrs = f2.create();
        Assert.assertEquals(true, attrs.isLockGrantor());
    }

    /**
     * Tests the {@link AttributesFactory#create} throws the appropriate exception with
     * poorly-configured factory.
     */
    @Test
    public void testInvalidConfigurations() {
        AttributesFactory factory;
        ExpirationAttributes invalidate = new ExpirationAttributes(1, LOCAL_INVALIDATE);
        ExpirationAttributes destroy = new ExpirationAttributes(1, LOCAL_DESTROY);
        // DataPolicy.REPLICATE is incompatible with
        // ExpirationAction.LOCAL_INVALIDATE
        factory = new AttributesFactory();
        factory.setDataPolicy(REPLICATE);
        factory.setEntryIdleTimeout(invalidate);
        factory.setStatisticsEnabled(true);
        {
            RegionAttributes ra = factory.create();
            Assert.assertEquals(PRELOADED, ra.getDataPolicy());
            Assert.assertEquals(new SubscriptionAttributes(ALL), ra.getSubscriptionAttributes());
        }
        // DataPolicy.REPLICATE is incompatible with
        // ExpirationAction.LOCAL_DESTROY.
        factory = new AttributesFactory();
        factory.setDataPolicy(REPLICATE);
        factory.setEntryIdleTimeout(destroy);
        factory.setStatisticsEnabled(true);
        {
            RegionAttributes ra = factory.create();
            Assert.assertEquals(PRELOADED, ra.getDataPolicy());
            Assert.assertEquals(new SubscriptionAttributes(ALL), ra.getSubscriptionAttributes());
        }
        // MirrorType KEYS is incompatible with
        // ExpirationAction.LOCAL_INVALIDATE
        factory = new AttributesFactory();
        factory.setMirrorType(KEYS);
        factory.setEntryIdleTimeout(destroy);
        factory.setStatisticsEnabled(true);
        {
            RegionAttributes ra = factory.create();
            Assert.assertEquals(PRELOADED, ra.getDataPolicy());
            Assert.assertEquals(new SubscriptionAttributes(ALL), ra.getSubscriptionAttributes());
        }
        // MirrorType.KEYS are incompatible with
        // ExpirationAction.LOCAL_DESTROY.
        factory = new AttributesFactory();
        factory.setMirrorType(KEYS);
        factory.setEntryIdleTimeout(destroy);
        factory.setStatisticsEnabled(true);
        {
            RegionAttributes ra = factory.create();
            Assert.assertEquals(PRELOADED, ra.getDataPolicy());
            Assert.assertEquals(new SubscriptionAttributes(ALL), ra.getSubscriptionAttributes());
        }
        // Entry idle expiration requires that
        // statistics are enabled
        factory = new AttributesFactory();
        factory.setEntryIdleTimeout(destroy);
        factory.setStatisticsEnabled(false);
        try {
            factory.create();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof IllegalStateException));
            // pass...
        }
        // Entry ttl expiration requires that
        // statistics are enabled
        factory = new AttributesFactory();
        factory.setEntryTimeToLive(destroy);
        factory.setStatisticsEnabled(false);
        try {
            factory.create();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof IllegalStateException));
            // pass...
        }
        // MembershipAttributes (required roles)
        // requires distributed scope
        factory = new AttributesFactory();
        factory.setScope(LOCAL);
        MembershipAttributes ra = new MembershipAttributes(new String[]{ "A" });
        factory.setMembershipAttributes(ra);
        try {
            factory.create();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof IllegalStateException));
            // pass...
        }
        // Used mixed mode API for disk store and DWA
        factory = new AttributesFactory();
        factory.setDataPolicy(PERSISTENT_REPLICATE);
        factory.setDiskStoreName("ds1");
        DiskWriteAttributesFactory dwaf = new DiskWriteAttributesFactory();
        try {
            factory.setDiskWriteAttributes(dwaf.create());
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof IllegalStateException));
            // pass...
        }
        // Used mixed mode API for disk store and DWA
        factory = new AttributesFactory();
        factory.setDataPolicy(PERSISTENT_REPLICATE);
        DiskWriteAttributesFactory dwaf2 = new DiskWriteAttributesFactory();
        factory.setDiskWriteAttributes(dwaf2.create());
        try {
            factory.setDiskStoreName("ds1");
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof IllegalStateException));
            // pass...
        }
        // Used mixed mode API for disk store and DWA
        factory = new AttributesFactory();
        factory.setDataPolicy(PERSISTENT_REPLICATE);
        File[] dirs1 = new File[]{ new File("").getAbsoluteFile() };
        factory.setDiskStoreName("ds1");
        try {
            factory.setDiskDirs(dirs1);
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof IllegalStateException));
            // pass...
        }
        // Used mixed mode API for disk store and DWA
        factory = new AttributesFactory();
        factory.setDataPolicy(PERSISTENT_REPLICATE);
        File[] dirs2 = new File[]{ new File("").getAbsoluteFile() };
        factory.setDiskDirs(dirs2);
        try {
            factory.setDiskStoreName("ds1");
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof IllegalStateException));
            // pass...
        }
        // Cloning cannot be disabled when a compressor is set
        factory = new AttributesFactory();
        factory.setCompressor(SnappyCompressor.getDefaultInstance());
        factory.setCloningEnabled(false);
        try {
            RegionAttributes ccra = factory.create();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException expected) {
            // Expected
        }
    }

    /**
     * Tests that the {@link AttributesFactory#AttributesFactory() default} attributes factory has the
     * advertised default configuration.
     */
    @Test
    public void testDefaultConfiguration() {
        AttributesFactory factory = new AttributesFactory();
        RegionAttributes attrs = factory.create();
        Assert.assertNull(attrs.getCacheLoader());
        Assert.assertNull(attrs.getCacheWriter());
        Assert.assertNull(attrs.getCacheListener());
        Assert.assertEquals(Arrays.asList(new CacheListener[0]), Arrays.asList(attrs.getCacheListeners()));
        Assert.assertEquals(0, attrs.getRegionTimeToLive().getTimeout());
        Assert.assertEquals(0, attrs.getRegionIdleTimeout().getTimeout());
        Assert.assertEquals(0, attrs.getEntryTimeToLive().getTimeout());
        Assert.assertEquals(null, attrs.getCustomEntryTimeToLive());
        Assert.assertEquals(0, attrs.getEntryIdleTimeout().getTimeout());
        Assert.assertEquals(null, attrs.getCustomEntryIdleTimeout());
        Assert.assertEquals(DISTRIBUTED_NO_ACK, attrs.getScope());
        Assert.assertEquals(DEFAULT, attrs.getDataPolicy());
        Assert.assertEquals(InterestPolicy.DEFAULT, attrs.getSubscriptionAttributes().getInterestPolicy());
        Assert.assertEquals(NONE, attrs.getMirrorType());
        Assert.assertEquals(null, attrs.getDiskStoreName());
        Assert.assertEquals(DEFAULT_DISK_SYNCHRONOUS, attrs.isDiskSynchronous());
        Assert.assertNull(attrs.getKeyConstraint());
        Assert.assertEquals(16, attrs.getInitialCapacity());
        Assert.assertEquals(0.75, attrs.getLoadFactor(), 0.0);
        Assert.assertFalse(attrs.getStatisticsEnabled());
        Assert.assertFalse(attrs.getPersistBackup());
        DiskWriteAttributes dwa = attrs.getDiskWriteAttributes();
        Assert.assertNotNull(dwa);
        {
            DiskWriteAttributesFactory dwaf = new DiskWriteAttributesFactory();
            dwaf.setSynchronous(DEFAULT_DISK_SYNCHRONOUS);
            Assert.assertEquals(dwaf.create(), dwa);
        }
        Assert.assertNull(attrs.getDiskStoreName());
        File[] diskDirs = attrs.getDiskDirs();
        Assert.assertNotNull(diskDirs);
        Assert.assertEquals(1, diskDirs.length);
        Assert.assertEquals(new File("."), diskDirs[0]);
        int[] diskSizes = attrs.getDiskDirSizes();
        Assert.assertNotNull(diskSizes);
        Assert.assertEquals(1, diskSizes.length);
        Assert.assertEquals(DEFAULT_DISK_DIR_SIZE, diskSizes[0]);
        Assert.assertTrue(attrs.getConcurrencyChecksEnabled());
    }

    @Test
    public void testDiskSynchronous() {
        {
            AttributesFactory factory = new AttributesFactory();
            factory.setDiskSynchronous(true);
            RegionAttributes attrs = factory.create();
            Assert.assertEquals(true, attrs.isDiskSynchronous());
            Assert.assertEquals(true, attrs.getDiskWriteAttributes().isSynchronous());
        }
        {
            AttributesFactory factory = new AttributesFactory();
            factory.setDiskSynchronous(false);
            RegionAttributes attrs = factory.create();
            Assert.assertEquals(false, attrs.isDiskSynchronous());
            Assert.assertEquals(false, attrs.getDiskWriteAttributes().isSynchronous());
        }
        // Test backwards compat interaction with diskSync.
        // If the old apis are used then we should get the old default of async.
        {
            DiskWriteAttributesFactory dwaf = new DiskWriteAttributesFactory();
            AttributesFactory factory = new AttributesFactory();
            factory.setDiskWriteAttributes(dwaf.create());
            RegionAttributes attrs = factory.create();
            Assert.assertEquals(false, attrs.getDiskWriteAttributes().isSynchronous());
            Assert.assertEquals(false, attrs.isDiskSynchronous());
        }
        {
            AttributesFactory factory = new AttributesFactory();
            factory.setDiskDirs(new File[]{ new File("").getAbsoluteFile() });
            RegionAttributes attrs = factory.create();
            Assert.assertEquals(false, attrs.getDiskWriteAttributes().isSynchronous());
            Assert.assertEquals(false, attrs.isDiskSynchronous());
        }
        {
            AttributesFactory factory = new AttributesFactory();
            factory.setDiskDirsAndSizes(new File[]{ new File("").getAbsoluteFile() }, new int[]{ 100 });
            RegionAttributes attrs = factory.create();
            Assert.assertEquals(false, attrs.getDiskWriteAttributes().isSynchronous());
            Assert.assertEquals(false, attrs.isDiskSynchronous());
        }
    }

    /**
     * Tests the cacheListener functionality
     *
     * @since GemFire 5.0
     */
    @Test
    public void testCacheListeners() {
        RegionAttributes ra;
        CacheListener cl1 = new AttributesFactoryJUnitTest.MyCacheListener();
        CacheListener cl2 = new AttributesFactoryJUnitTest.MyCacheListener();
        Assert.assertFalse(cl1.equals(cl2));
        Assert.assertFalse(Arrays.asList(new CacheListener[]{ cl1, cl2 }).equals(Arrays.asList(new CacheListener[]{ cl2, cl1 })));
        AttributesFactory factory = new AttributesFactory();
        try {
            factory.addCacheListener(null);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            factory.initCacheListeners(new CacheListener[]{ null });
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        ra = factory.create();
        Assert.assertEquals(null, ra.getCacheListener());
        Assert.assertEquals(Arrays.asList(new CacheListener[0]), Arrays.asList(ra.getCacheListeners()));
        factory.setCacheListener(cl1);
        ra = factory.create();
        Assert.assertEquals(cl1, ra.getCacheListener());
        Assert.assertEquals(Arrays.asList(new CacheListener[]{ cl1 }), Arrays.asList(ra.getCacheListeners()));
        factory.setCacheListener(cl2);
        ra = factory.create();
        Assert.assertEquals(cl2, ra.getCacheListener());
        Assert.assertEquals(Arrays.asList(new CacheListener[]{ cl2 }), Arrays.asList(ra.getCacheListeners()));
        factory.setCacheListener(null);
        ra = factory.create();
        Assert.assertEquals(null, ra.getCacheListener());
        Assert.assertEquals(Arrays.asList(new CacheListener[0]), Arrays.asList(ra.getCacheListeners()));
        factory.setCacheListener(cl1);
        factory.initCacheListeners(new CacheListener[]{ cl1, cl2 });
        ra = factory.create();
        try {
            ra.getCacheListener();
            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException expected) {
        }
        Assert.assertEquals(Arrays.asList(new CacheListener[]{ cl1, cl2 }), Arrays.asList(ra.getCacheListeners()));
        factory.initCacheListeners(null);
        ra = factory.create();
        Assert.assertEquals(Arrays.asList(new CacheListener[0]), Arrays.asList(ra.getCacheListeners()));
        factory.initCacheListeners(new CacheListener[0]);
        ra = factory.create();
        Assert.assertEquals(Arrays.asList(new CacheListener[0]), Arrays.asList(ra.getCacheListeners()));
        factory.addCacheListener(cl1);
        ra = factory.create();
        Assert.assertEquals(Arrays.asList(new CacheListener[]{ cl1 }), Arrays.asList(ra.getCacheListeners()));
        factory.addCacheListener(cl2);
        ra = factory.create();
        Assert.assertEquals(Arrays.asList(new CacheListener[]{ cl1, cl2 }), Arrays.asList(ra.getCacheListeners()));
        factory.initCacheListeners(new CacheListener[]{ cl2 });
        ra = factory.create();
        Assert.assertEquals(Arrays.asList(new CacheListener[]{ cl2 }), Arrays.asList(ra.getCacheListeners()));
    }

    /**
     *
     *
     * @since GemFire 5.7
     */
    @Test
    public void testConnectionPool() {
        CacheLoader cl = new CacheLoader() {
            @Override
            public Object load(LoaderHelper helper) throws CacheLoaderException {
                return null;
            }

            @Override
            public void close() {
            }
        };
        AttributesFactory factory = new AttributesFactory();
        factory.setPoolName("mypool");
        factory = new AttributesFactory();
        factory.setCacheWriter(new CacheWriterAdapter());
        factory.setPoolName("mypool");
        factory = new AttributesFactory();
        factory.setCacheLoader(cl);
        factory.setPoolName("mypool");
    }

    /**
     * Trivial cache listener impl
     *
     * @since GemFire 5.0
     */
    // empty impl
    private static class MyCacheListener extends CacheListenerAdapter {}
}

