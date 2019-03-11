/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.adl.live;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.adl.common.Parallelized;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test supported ASCII, UTF-8 character set supported by Adl storage file
 * system on file/folder operation.
 */
@RunWith(Parallelized.class)
public class TestAdlSupportedCharsetInPath {
    private static final String TEST_ROOT = "/test/";

    private static final Logger LOG = LoggerFactory.getLogger(TestAdlSupportedCharsetInPath.class);

    private String path;

    public TestAdlSupportedCharsetInPath(String filePath) {
        path = filePath;
    }

    @Test
    public void testAllowedSpecialCharactersMkdir() throws IOException, URISyntaxException {
        Path parentPath = new Path(TestAdlSupportedCharsetInPath.TEST_ROOT, ((UUID.randomUUID().toString()) + "/"));
        Path specialFile = new Path(parentPath, path);
        FileSystem fs = AdlStorageConfiguration.createStorageConnector();
        Assert.assertTrue(("Mkdir failed : " + specialFile), fs.mkdirs(specialFile));
        Assert.assertTrue(("File not Found after Mkdir success" + specialFile), fs.exists(specialFile));
        Assert.assertTrue(("Not listed under parent " + parentPath), contains(fs.listStatus(parentPath), fs.makeQualified(specialFile).toString()));
        Assert.assertTrue(("Delete failed : " + specialFile), fs.delete(specialFile, true));
        Assert.assertFalse(("File still exist after delete " + specialFile), fs.exists(specialFile));
    }

    @Test
    public void testAllowedSpecialCharactersRename() throws IOException, URISyntaxException {
        String parentPath = ((TestAdlSupportedCharsetInPath.TEST_ROOT) + (UUID.randomUUID().toString())) + "/";
        Path specialFile = new Path((parentPath + (path)));
        Path anotherLocation = new Path((parentPath + (UUID.randomUUID().toString())));
        FileSystem fs = AdlStorageConfiguration.createStorageConnector();
        Assert.assertTrue(("Could not create " + (specialFile.toString())), fs.createNewFile(specialFile));
        Assert.assertTrue(((("Failed to rename " + (specialFile.toString())) + " --> ") + (anotherLocation.toString())), fs.rename(specialFile, anotherLocation));
        Assert.assertFalse(("File should not be present after successful rename : " + (specialFile.toString())), fs.exists(specialFile));
        Assert.assertTrue(("File should be present after successful rename : " + (anotherLocation.toString())), fs.exists(anotherLocation));
        Assert.assertFalse(("Listed under parent whereas expected not listed : " + parentPath), contains(fs.listStatus(new Path(parentPath)), fs.makeQualified(specialFile).toString()));
        Assert.assertTrue(((("Failed to rename " + (anotherLocation.toString())) + " --> ") + (specialFile.toString())), fs.rename(anotherLocation, specialFile));
        Assert.assertTrue((("File should be present after successful rename : " + "") + (specialFile.toString())), fs.exists(specialFile));
        Assert.assertFalse(("File should not be present after successful rename : " + (anotherLocation.toString())), fs.exists(anotherLocation));
        Assert.assertTrue(("Not listed under parent " + parentPath), contains(fs.listStatus(new Path(parentPath)), fs.makeQualified(specialFile).toString()));
        Assert.assertTrue(("Failed to delete " + parentPath), fs.delete(new Path(parentPath), true));
    }
}

