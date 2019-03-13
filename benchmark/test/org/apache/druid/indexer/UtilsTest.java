/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.indexer;


import com.google.common.base.Function;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobContext;
import org.easymock.EasyMock;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class UtilsTest {
    private static final String DUMMY_STRING = "Very important string";

    private static final String TMP_FILE_NAME = "test_file";

    private Configuration jobConfig;

    private JobContext mockJobContext;

    private Map expectedMap;

    private File tmpFile;

    private Path tmpPath;

    private FileSystem defaultFileSystem;

    private Set setOfKeys;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private static class CreateValueFromKey implements Function {
        @Override
        public Object apply(Object input) {
            return input + (UtilsTest.DUMMY_STRING);
        }
    }

    @Test
    public void testExistsPlainFile() throws IOException {
        boolean expected = Utils.exists(mockJobContext, defaultFileSystem, tmpPath);
        Assert.assertTrue("Should be true since file is created", expected);
        tmpFolder.delete();
        expected = Utils.exists(mockJobContext, defaultFileSystem, tmpPath);
        Assert.assertFalse("Should be false since file is deleted", expected);
        EasyMock.verify(mockJobContext);
    }

    @Test
    public void testPlainStoreThenGetStats() throws IOException {
        Utils.storeStats(mockJobContext, tmpPath, expectedMap);
        Map actualMap = Utils.getStats(mockJobContext, tmpPath);
        Assert.assertThat(actualMap, Is.is(actualMap));
        EasyMock.verify(mockJobContext);
    }

    @Test(expected = ISE.class)
    public void testExceptionInMakePathAndOutputStream() throws IOException {
        boolean overwrite = false;
        Utils.makePathAndOutputStream(mockJobContext, tmpPath, overwrite);
    }

    @Test
    public void testPlainOpenInputStream() throws IOException {
        FileUtils.writeStringToFile(tmpFile, UtilsTest.DUMMY_STRING);
        InputStream inStream = Utils.openInputStream(mockJobContext, tmpPath);
        Assert.assertNotNull(inStream);
        String expected = StringUtils.fromUtf8(ByteStreams.toByteArray(inStream));
        Assert.assertEquals(expected, UtilsTest.DUMMY_STRING);
    }
}

