/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import Task.BACKGROUND_EXECUTOR;
import bolts.Task;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static ParseKeyValueCache.maxKeyValueCacheFiles;


public class ParseKeyValueCacheTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File keyValueCacheDir;

    @Test
    public void testMultipleAsynchronousWrites() throws ParseException {
        int max = 100;
        maxKeyValueCacheFiles = max;
        // Max out KeyValueCache
        for (int i = 0; i < max; i++) {
            ParseKeyValueCache.saveToKeyValueCache(("key " + i), "test");
        }
        List<Task<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            tasks.add(Task.call(new Callable<Void>() {
                @Override
                public Void call() {
                    ParseKeyValueCache.saveToKeyValueCache("foo", "test");
                    return null;
                }
            }, BACKGROUND_EXECUTOR));
        }
        ParseTaskUtils.wait(Task.whenAll(tasks));
    }

    @Test
    public void testSaveToKeyValueCacheWithoutCacheDir() throws Exception {
        // Delete the cache folder(Simulate users clear the app cache)
        Assert.assertTrue(keyValueCacheDir.exists());
        keyValueCacheDir.delete();
        Assert.assertFalse(keyValueCacheDir.exists());
        // Save a key value pair
        ParseKeyValueCache.saveToKeyValueCache("key", "value");
        // Verify cache file is correct
        Assert.assertEquals(1, keyValueCacheDir.listFiles().length);
        Assert.assertArrayEquals("value".getBytes(), ParseFileUtils.readFileToByteArray(keyValueCacheDir.listFiles()[0]));
    }

    @Test
    public void testGetSizeWithoutCacheDir() {
        // Delete the cache folder(Simulate users clear the app cache)
        Assert.assertTrue(keyValueCacheDir.exists());
        keyValueCacheDir.delete();
        Assert.assertFalse(keyValueCacheDir.exists());
        // Verify size is zero
        Assert.assertEquals(0, ParseKeyValueCache.size());
    }
}

