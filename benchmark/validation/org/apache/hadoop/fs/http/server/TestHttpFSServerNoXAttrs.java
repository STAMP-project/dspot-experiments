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
package org.apache.hadoop.fs.http.server;


import java.io.OutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.test.HTestCase;
import org.apache.hadoop.test.TestDir;
import org.apache.hadoop.test.TestHdfs;
import org.apache.hadoop.test.TestJetty;
import org.junit.Test;


/**
 * This test class ensures that everything works as expected when XAttr
 * support is turned off HDFS.  This is the default configuration.  The other
 * tests operate with XAttr support turned on.
 */
public class TestHttpFSServerNoXAttrs extends HTestCase {
    private MiniDFSCluster miniDfs;

    private Configuration nnConf;

    /**
     * Ensure that GETXATTRS, SETXATTR, REMOVEXATTR fail.
     */
    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testWithXAttrs() throws Exception {
        final String name1 = "user.a1";
        final byte[] value1 = new byte[]{ 49, 50, 51 };
        final String dir = "/noXAttr";
        final String path = dir + "/file";
        startMiniDFS();
        createHttpFSServer();
        FileSystem fs = FileSystem.get(nnConf);
        fs.mkdirs(new Path(dir));
        OutputStream os = fs.create(new Path(path));
        os.write(1);
        os.close();
        /* GETXATTRS, SETXATTR, REMOVEXATTR fail */
        getStatus(path, "GETXATTRS");
        putCmd(path, "SETXATTR", TestHttpFSServer.setXAttrParam(name1, value1));
        putCmd(path, "REMOVEXATTR", ("xattr.name=" + name1));
    }
}

