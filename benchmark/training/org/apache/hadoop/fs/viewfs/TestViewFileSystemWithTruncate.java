/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.viewfs;


import com.google.common.base.Supplier;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verify truncate through ViewFileSystem functionality.
 */
public class TestViewFileSystemWithTruncate {
    private static MiniDFSCluster cluster;

    private static Configuration clusterConf = new Configuration();

    private static FileSystem fHdfs;

    private FileSystem fsView;

    private Configuration fsViewConf;

    private FileSystem fsTarget;

    private Path targetTestRoot;

    private Path mountOnNn1;

    private FileSystemTestHelper fileSystemTestHelper = new FileSystemTestHelper("/tmp/TestViewFileSystemWithXAttrs");

    @Test(timeout = 30000)
    public void testTruncateWithViewFileSystem() throws Exception {
        Path filePath = new Path(((mountOnNn1) + "/ttest"));
        Path hdfFilepath = new Path("/tmp/TestViewFileSystemWithXAttrs/ttest");
        FSDataOutputStream out = fsView.create(filePath);
        out.writeBytes("drtatedasfdasfgdfas");
        out.close();
        int newLength = 10;
        boolean isReady = fsView.truncate(filePath, newLength);
        if (!isReady) {
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    try {
                        return TestViewFileSystemWithTruncate.cluster.getFileSystem(0).isFileClosed(hdfFilepath);
                    } catch (IOException e) {
                        return false;
                    }
                }
            }, 100, (60 * 1000));
        }
        // file length should be 10 after truncate
        Assert.assertEquals(newLength, fsView.getFileStatus(filePath).getLen());
    }
}

