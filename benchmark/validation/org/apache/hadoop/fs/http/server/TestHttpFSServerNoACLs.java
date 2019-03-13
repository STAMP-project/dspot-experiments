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
import org.apache.hadoop.test.TestJetty;
import org.junit.Test;


/**
 * This test class ensures that everything works as expected when ACL
 * support is turned off HDFS.  This is the default configuration.  The other
 * tests operate with ACL support turned on.
 */
public class TestHttpFSServerNoACLs extends HTestCase {
    private MiniDFSCluster miniDfs;

    private Configuration nnConf;

    /**
     * Test without ACLs.
     * Ensure that
     * <ol>
     *   <li>GETFILESTATUS and LISTSTATUS work happily</li>
     *   <li>ACLSTATUS throws an exception</li>
     *   <li>The ACL SET, REMOVE, etc calls all fail</li>
     * </ol>
     *
     * @throws Exception
     * 		
     */
    @Test
    @TestDir
    @TestJetty
    public void testWithNoAcls() throws Exception {
        final String aclUser1 = "user:foo:rw-";
        final String rmAclUser1 = "user:foo:";
        final String aclUser2 = "user:bar:r--";
        final String aclGroup1 = "group::r--";
        final String aclSpec = ((("aclspec=user::rwx," + aclUser1) + ",") + aclGroup1) + ",other::---";
        final String modAclSpec = "aclspec=" + aclUser2;
        final String remAclSpec = "aclspec=" + rmAclUser1;
        final String defUser1 = "default:user:glarch:r-x";
        final String defSpec1 = "aclspec=" + defUser1;
        final String dir = "/noACLs";
        final String path = dir + "/foo";
        startMiniDFS();
        createHttpFSServer();
        FileSystem fs = FileSystem.get(nnConf);
        fs.mkdirs(new Path(dir));
        OutputStream os = fs.create(new Path(path));
        os.write(1);
        os.close();
        /* The normal status calls work as expected; GETACLSTATUS fails */
        getStatus(path, "GETFILESTATUS", true);
        getStatus(dir, "LISTSTATUS", true);
        getStatus(path, "GETACLSTATUS", false);
        /* All the ACL-based PUT commands fail with ACL exceptions */
        putCmd(path, "SETACL", aclSpec, false);
        putCmd(path, "MODIFYACLENTRIES", modAclSpec, false);
        putCmd(path, "REMOVEACLENTRIES", remAclSpec, false);
        putCmd(path, "REMOVEACL", null, false);
        putCmd(dir, "SETACL", defSpec1, false);
        putCmd(dir, "REMOVEDEFAULTACL", null, false);
        miniDfs.shutdown();
    }
}

