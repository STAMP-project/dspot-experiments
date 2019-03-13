/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs;


import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Test;


/**
 * Test of FileContext apis on Webhdfs.
 */
public class TestWebHdfsFileContextMainOperations extends FileContextMainOperationsBaseTest {
    protected static MiniDFSCluster cluster;

    private static Path defaultWorkingDirectory;

    protected static URI webhdfsUrl;

    protected static int numBlocks = 2;

    protected static final byte[] data = FileContextTestHelper.getFileData(TestWebHdfsFileContextMainOperations.numBlocks, FileContextTestHelper.getDefaultBlockSize());

    protected static final HdfsConfiguration CONF = new HdfsConfiguration();

    /**
     * Test FileContext APIs when symlinks are not supported
     * TODO: Open separate JIRA for full support of the Symlink in webhdfs
     */
    @Test
    public void testUnsupportedSymlink() throws IOException {
        /**
         * WebHdfs client Partially supports the Symlink.
         * creation of Symlink is supported, but the getLinkTargetPath() api is not supported currently,
         * Implement the test case once the full support is available.
         */
    }
}

