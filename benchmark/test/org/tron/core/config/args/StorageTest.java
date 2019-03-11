/**
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tron.core.config.args;


import CompressionType.SNAPPY;
import Constant.TEST_CONF;
import org.iq80.leveldb.Options;
import org.junit.Assert;
import org.junit.Test;


public class StorageTest {
    private static Storage storage;

    static {
        Args.setParam(new String[]{  }, TEST_CONF);
        StorageTest.storage = Args.getInstance().getStorage();
    }

    @Test
    public void getDirectory() {
        Assert.assertEquals("database", StorageTest.storage.getDbDirectory());
        Assert.assertEquals("index", StorageTest.storage.getIndexDirectory());
    }

    @Test
    public void getPath() {
        Assert.assertEquals("storage_directory_test", StorageTest.storage.getPathByDbName("account"));
        Assert.assertEquals("test_path", StorageTest.storage.getPathByDbName("test_name"));
        Assert.assertNull(StorageTest.storage.getPathByDbName("some_name_not_exists"));
    }

    @Test
    public void getOptions() {
        Options options = StorageTest.storage.getOptionsByDbName("account");
        Assert.assertTrue(options.createIfMissing());
        Assert.assertTrue(options.paranoidChecks());
        Assert.assertTrue(options.verifyChecksums());
        Assert.assertEquals(SNAPPY, options.compressionType());
        Assert.assertEquals(4096, options.blockSize());
        Assert.assertEquals(10485760, options.writeBufferSize());
        Assert.assertEquals(10485760L, options.cacheSize());
        Assert.assertEquals(100, options.maxOpenFiles());
        options = StorageTest.storage.getOptionsByDbName("test_name");
        Assert.assertFalse(options.createIfMissing());
        Assert.assertFalse(options.paranoidChecks());
        Assert.assertFalse(options.verifyChecksums());
        Assert.assertEquals(SNAPPY, options.compressionType());
        Assert.assertEquals(2, options.blockSize());
        Assert.assertEquals(3, options.writeBufferSize());
        Assert.assertEquals(4L, options.cacheSize());
        Assert.assertEquals(5, options.maxOpenFiles());
        options = StorageTest.storage.getOptionsByDbName("some_name_not_exists");
        Assert.assertTrue(options.createIfMissing());
        Assert.assertTrue(options.paranoidChecks());
        Assert.assertTrue(options.verifyChecksums());
        Assert.assertEquals(SNAPPY, options.compressionType());
        Assert.assertEquals((4 * 1024), options.blockSize());
        Assert.assertEquals(((10 * 1024) * 1024), options.writeBufferSize());
        Assert.assertEquals(((10 * 1024) * 1024L), options.cacheSize());
        Assert.assertEquals(100, options.maxOpenFiles());
    }
}

