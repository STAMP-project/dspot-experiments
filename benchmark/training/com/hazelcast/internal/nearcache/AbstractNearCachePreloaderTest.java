/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.nearcache;


import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TimeConstants;
import com.hazelcast.test.annotation.SlowTest;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


/**
 * Contains the logic code for unified Near Cache preloader tests.
 *
 * @param <NK>
 * 		key type of the tested Near Cache
 * @param <NV>
 * 		value type of the tested Near Cache
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractNearCachePreloaderTest<NK, NV> extends HazelcastTestSupport {
    /**
     * The type of the key of the used {@link com.hazelcast.internal.adapter.DataStructureAdapter}.
     */
    public enum KeyType {

        INTEGER,
        STRING;}

    protected static final int KEY_COUNT = 10023;

    protected static final int THREAD_COUNT = 10;

    protected static final int CREATE_AND_DESTROY_RUNS = 5000;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    protected final String defaultNearCache = HazelcastTestSupport.randomName();

    private final File preloadFile10kInt = AbstractNearCachePreloaderTest.getFileFromResources("nearcache-10k-int.store");

    private final File preloadFile10kString = AbstractNearCachePreloaderTest.getFileFromResources("nearcache-10k-string.store");

    private final File preloadFileEmpty = AbstractNearCachePreloaderTest.getFileFromResources("nearcache-empty.store");

    private final File preloadFileInvalidMagicBytes = AbstractNearCachePreloaderTest.getFileFromResources("nearcache-invalid-magicbytes.store");

    private final File preloadFileInvalidFileFormat = AbstractNearCachePreloaderTest.getFileFromResources("nearcache-invalid-fileformat.store");

    private final File preloadFileNegativeFileFormat = AbstractNearCachePreloaderTest.getFileFromResources("nearcache-negative-fileformat.store");

    /**
     * The {@link NearCacheConfig} used by the Near Cache tests.
     * <p>
     * Needs to be set by the implementations of this class in their {@link org.junit.Before} methods.
     */
    protected NearCacheConfig nearCacheConfig;

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    @Category(SlowTest.class)
    public void testStoreAndLoad_withIntegerKeys() {
        storeAndLoad(2342, AbstractNearCachePreloaderTest.KeyType.INTEGER);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    @Category(SlowTest.class)
    public void testStoreAndLoad_withStringKeys() {
        storeAndLoad(4223, AbstractNearCachePreloaderTest.KeyType.STRING);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    @Category(SlowTest.class)
    public void testCreateStoreFile_withInvalidDirectory() {
        String directory = "/dev/null/";
        nearCacheConfig.getPreloaderConfig().setStoreInitialDelaySeconds(1).setStoreIntervalSeconds(1).setDirectory(directory);
        File lockFile = new File(directory, getStoreFile().getName());
        expectedException.expectMessage(("Cannot create lock file " + (lockFile.getAbsolutePath())));
        expectedException.expect(HazelcastException.class);
        createContext(true);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    @Category(SlowTest.class)
    public void testCreateStoreFile_withStringKey() {
        createStoreFile(AbstractNearCachePreloaderTest.KEY_COUNT, AbstractNearCachePreloaderTest.KeyType.STRING);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    @Category(SlowTest.class)
    public void testCreateStoreFile_withIntegerKey() {
        createStoreFile(AbstractNearCachePreloaderTest.KEY_COUNT, AbstractNearCachePreloaderTest.KeyType.INTEGER);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    @Category(SlowTest.class)
    public void testCreateStoreFile_withEmptyNearCache() {
        createStoreFile(0, AbstractNearCachePreloaderTest.KeyType.INTEGER);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    @Category(SlowTest.class)
    public void testCreateStoreFile_whenTwoClientsWithSameStoreFile_thenThrowException() {
        nearCacheConfig.getPreloaderConfig().setStoreInitialDelaySeconds(2).setStoreIntervalSeconds(1);
        // the first client creates the lock file and holds the lock on it
        NearCacheTestContext<Object, String, NK, NV> context = createContext(true);
        populateDataAdapter(context, AbstractNearCachePreloaderTest.KEY_COUNT, AbstractNearCachePreloaderTest.KeyType.INTEGER);
        // assure that the pre-loader is working on the first client
        AbstractNearCachePreloaderTest.populateNearCache(context, AbstractNearCachePreloaderTest.KEY_COUNT, AbstractNearCachePreloaderTest.KeyType.INTEGER);
        AbstractNearCachePreloaderTest.waitForNearCachePersistence(context, 3);
        AbstractNearCachePreloaderTest.assertLastNearCachePersistence(context, getStoreFile(), AbstractNearCachePreloaderTest.KEY_COUNT);
        // the second client cannot acquire the lock, so it fails with an exception
        expectedException.expectMessage(("Cannot acquire lock on " + (getStoreFile())));
        expectedException.expect(HazelcastException.class);
        createNearCacheContext();
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    public void testPreloadNearCache_withIntegerKeys() {
        preloadNearCache(preloadFile10kInt, 10000, AbstractNearCachePreloaderTest.KeyType.INTEGER);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    public void testPreloadNearCache_withStringKeys() {
        preloadNearCache(preloadFile10kString, 10000, AbstractNearCachePreloaderTest.KeyType.STRING);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    public void testPreloadNearCache_withEmptyFile() {
        preloadNearCache(preloadFileEmpty, 0, AbstractNearCachePreloaderTest.KeyType.INTEGER);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    public void testPreloadNearCache_withInvalidMagicBytes() {
        preloadNearCache(preloadFileInvalidMagicBytes, 0, AbstractNearCachePreloaderTest.KeyType.INTEGER);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    public void testPreloadNearCache_withInvalidFileFormat() {
        preloadNearCache(preloadFileInvalidFileFormat, 0, AbstractNearCachePreloaderTest.KeyType.INTEGER);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    public void testPreloadNearCache_withNegativeFileFormat() {
        preloadNearCache(preloadFileNegativeFileFormat, 0, AbstractNearCachePreloaderTest.KeyType.INTEGER);
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    public void testPreloadNearCacheLock_withSharedConfig_concurrently() {
        nearCacheConfig.getPreloaderConfig().setDirectory("");
        ThreadPoolExecutor pool = ((ThreadPoolExecutor) (Executors.newFixedThreadPool(AbstractNearCachePreloaderTest.THREAD_COUNT)));
        final NearCacheTestContext<String, String, NK, NV> context = createContext(true);
        final CountDownLatch startLatch = new CountDownLatch(AbstractNearCachePreloaderTest.THREAD_COUNT);
        final CountDownLatch finishLatch = new CountDownLatch(AbstractNearCachePreloaderTest.THREAD_COUNT);
        for (int i = 0; i < (AbstractNearCachePreloaderTest.THREAD_COUNT); i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    startLatch.countDown();
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    String threadName = Thread.currentThread().toString();
                    String dataStructureName = ((nearCacheConfig.getName()) + "-") + threadName;
                    DataStructureAdapter<String, String> adapter = getDataStructure(context, dataStructureName);
                    for (int i = 0; i < 100; i++) {
                        adapter.put(((("key-" + threadName) + "-") + i), ((("value-" + threadName) + "-") + i));
                    }
                    finishLatch.countDown();
                }
            });
        }
        HazelcastTestSupport.assertOpenEventually(finishLatch);
        pool.shutdownNow();
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    public void testCreateAndDestroyDataStructure_withSameName() {
        String name = HazelcastTestSupport.randomMapName("createDestroyNearCache");
        nearCacheConfig.setName(name);
        NearCacheTestContext<Object, String, NK, NV> context = createContext(true);
        populateDataAdapter(context, AbstractNearCachePreloaderTest.KEY_COUNT, AbstractNearCachePreloaderTest.KeyType.INTEGER);
        DataStructureAdapter<Object, String> adapter = context.nearCacheAdapter;
        for (int i = 0; i < (AbstractNearCachePreloaderTest.CREATE_AND_DESTROY_RUNS); i++) {
            adapter.destroy();
            adapter = getDataStructure(context, name);
        }
    }

    @Test(timeout = 10 * (TimeConstants.MINUTE))
    public void testCreateAndDestroyDataStructure_withDifferentNames() {
        String name = HazelcastTestSupport.randomMapName("createDestroyNearCache");
        nearCacheConfig.setName((name + "*"));
        NearCacheTestContext<Object, String, NK, NV> context = createContext(true);
        populateDataAdapter(context, AbstractNearCachePreloaderTest.KEY_COUNT, AbstractNearCachePreloaderTest.KeyType.INTEGER);
        DataStructureAdapter<Object, String> adapter = context.nearCacheAdapter;
        for (int i = 0; i < (AbstractNearCachePreloaderTest.CREATE_AND_DESTROY_RUNS); i++) {
            adapter.destroy();
            adapter = getDataStructure(context, (name + i));
        }
    }
}

