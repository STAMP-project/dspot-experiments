/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.config;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.tests.util.TestFileUtil;


public class CacheTest {
    @Test
    public void testCacheSizeOkay() throws Exception {
        File testCacheFolder = TestFileUtil.createTempDirectoryInSystemTemp();
        Cache cache = new Cache(testCacheFolder);
        cache.setKeepBytes((50 * 1024));
        TestFileUtil.createRandomFilesInDirectory(testCacheFolder, (10 * 1024), 4);
        Assert.assertEquals(4, testCacheFolder.listFiles().length);
        cache.clear();
        Assert.assertEquals(4, testCacheFolder.listFiles().length);
        TestFileUtil.deleteDirectory(testCacheFolder);
    }

    @Test
    public void testCacheNeedsCleaning5Left() throws Exception {
        File testCacheFolder = TestFileUtil.createTempDirectoryInSystemTemp();
        Cache cache = new Cache(testCacheFolder);
        cache.setKeepBytes((50 * 1024));
        TestFileUtil.createRandomFilesInDirectory(testCacheFolder, (10 * 1024), 10);
        Assert.assertEquals(10, testCacheFolder.listFiles().length);
        cache.clear();
        Assert.assertEquals(5, testCacheFolder.listFiles().length);
        TestFileUtil.deleteDirectory(testCacheFolder);
    }

    @Test
    public void testCacheNeedsCleaning1Left() throws Exception {
        File testCacheFolder = TestFileUtil.createTempDirectoryInSystemTemp();
        Cache cache = new Cache(testCacheFolder);
        cache.setKeepBytes((50 * 1024));
        TestFileUtil.createRandomFile(new File(testCacheFolder, "10"), (10 * 1024));
        Thread.sleep(1001);// Linux/ext3 only has 1s accuracy

        TestFileUtil.createRandomFile(new File(testCacheFolder, "30"), (30 * 1024));
        Thread.sleep(1001);
        TestFileUtil.createRandomFile(new File(testCacheFolder, "20"), (20 * 1024));
        Thread.sleep(1001);
        TestFileUtil.createRandomFile(new File(testCacheFolder, "40"), (40 * 1024));
        Assert.assertEquals(4, testCacheFolder.listFiles().length);
        cache.clear();
        Assert.assertEquals(1, testCacheFolder.listFiles().length);
        Assert.assertFalse(new File(testCacheFolder, "10").exists());
        Assert.assertFalse(new File(testCacheFolder, "30").exists());
        Assert.assertFalse(new File(testCacheFolder, "20").exists());
        Assert.assertTrue(new File(testCacheFolder, "40").exists());
        TestFileUtil.deleteDirectory(testCacheFolder);
    }

    @Test
    public void testCacheNeedsCleaning2Left() throws Exception {
        File testCacheFolder = TestFileUtil.createTempDirectoryInSystemTemp();
        Cache cache = new Cache(testCacheFolder);
        cache.setKeepBytes((50 * 1024));
        TestFileUtil.createRandomFile(new File(testCacheFolder, "40"), (40 * 1024));
        Thread.sleep(1001);// Linux/ext3 only has 1s accuracy

        TestFileUtil.createRandomFile(new File(testCacheFolder, "30"), (30 * 1024));
        Thread.sleep(1001);
        TestFileUtil.createRandomFile(new File(testCacheFolder, "20"), (20 * 1024));
        Thread.sleep(1001);
        TestFileUtil.createRandomFile(new File(testCacheFolder, "10"), (10 * 1024));
        Assert.assertEquals(4, testCacheFolder.listFiles().length);
        cache.clear();
        Assert.assertEquals(2, testCacheFolder.listFiles().length);
        Assert.assertFalse(new File(testCacheFolder, "40").exists());
        Assert.assertFalse(new File(testCacheFolder, "30").exists());
        Assert.assertTrue(new File(testCacheFolder, "20").exists());
        Assert.assertTrue(new File(testCacheFolder, "10").exists());
        TestFileUtil.deleteDirectory(testCacheFolder);
    }
}

