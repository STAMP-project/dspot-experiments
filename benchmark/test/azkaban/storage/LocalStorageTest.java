/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.storage;


import azkaban.spi.StorageMetadata;
import azkaban.utils.Md5Hasher;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class LocalStorageTest {
    static final String SAMPLE_FILE = "sample_flow_01.zip";

    static final String LOCAL_STORAGE = "LOCAL_STORAGE";

    static final File BASE_DIRECTORY = new File(LocalStorageTest.LOCAL_STORAGE);

    private static final Logger log = Logger.getLogger(LocalStorageTest.class);

    private LocalStorage localStorage;

    @Test
    public void testPutGetDelete() throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File testFile = new File(classLoader.getResource(LocalStorageTest.SAMPLE_FILE).getFile());
        final StorageMetadata metadata = new StorageMetadata(1, 1, "testuser", Md5Hasher.md5Hash(testFile));
        final String key = this.localStorage.put(metadata, testFile);
        Assert.assertNotNull(key);
        LocalStorageTest.log.info(("Key URI: " + key));
        final File expectedTargetFile = new File(LocalStorageTest.BASE_DIRECTORY, new StringBuilder().append(metadata.getProjectId()).append(File.separator).append(metadata.getProjectId()).append("-").append(new String(Hex.encodeHex(metadata.getHash()))).append(".zip").toString());
        Assert.assertTrue(expectedTargetFile.exists());
        Assert.assertTrue(FileUtils.contentEquals(testFile, expectedTargetFile));
        // test get
        final InputStream getIs = this.localStorage.get(key);
        Assert.assertNotNull(getIs);
        final File getFile = new File("tmp.get");
        FileUtils.copyInputStreamToFile(getIs, getFile);
        Assert.assertTrue(FileUtils.contentEquals(testFile, getFile));
        // Cleanup temp file
        getFile.delete();
        Assert.assertTrue(this.localStorage.delete(key));
        boolean exceptionThrown = false;
        try {
            this.localStorage.get(key);
        } catch (final FileNotFoundException e) {
            exceptionThrown = true;
        }
        Assert.assertTrue(exceptionThrown);
    }
}

