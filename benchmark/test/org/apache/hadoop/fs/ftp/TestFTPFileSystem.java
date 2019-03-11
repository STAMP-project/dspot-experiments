/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.ftp;


import FTP.BLOCK_TRANSFER_MODE;
import FTP.COMPRESSED_TRANSFER_MODE;
import FTP.DEFAULT_PORT;
import FTP.STREAM_TRANSFER_MODE;
import FTPClient.ACTIVE_LOCAL_DATA_CONNECTION_MODE;
import FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE;
import FTPFileSystem.DEFAULT_TIMEOUT;
import FTPFileSystem.FS_FTP_DATA_CONNECTION_MODE;
import FTPFileSystem.FS_FTP_TIMEOUT;
import FTPFileSystem.FS_FTP_TRANSFER_MODE;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsAction;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Test basic @{link FTPFileSystem} class methods. Contract tests are in
 * TestFTPContractXXXX.
 */
public class TestFTPFileSystem {
    @Rule
    public Timeout testTimeout = new Timeout(180000);

    @Test
    public void testFTPDefaultPort() throws Exception {
        FTPFileSystem ftp = new FTPFileSystem();
        Assert.assertEquals(DEFAULT_PORT, ftp.getDefaultPort());
    }

    @Test
    public void testFTPTransferMode() throws Exception {
        Configuration conf = new Configuration();
        FTPFileSystem ftp = new FTPFileSystem();
        Assert.assertEquals(BLOCK_TRANSFER_MODE, ftp.getTransferMode(conf));
        conf.set(FS_FTP_TRANSFER_MODE, "STREAM_TRANSFER_MODE");
        Assert.assertEquals(STREAM_TRANSFER_MODE, ftp.getTransferMode(conf));
        conf.set(FS_FTP_TRANSFER_MODE, "COMPRESSED_TRANSFER_MODE");
        Assert.assertEquals(COMPRESSED_TRANSFER_MODE, ftp.getTransferMode(conf));
        conf.set(FS_FTP_TRANSFER_MODE, "invalid");
        Assert.assertEquals(FTPClient.BLOCK_TRANSFER_MODE, ftp.getTransferMode(conf));
    }

    @Test
    public void testFTPDataConnectionMode() throws Exception {
        Configuration conf = new Configuration();
        FTPClient client = new FTPClient();
        FTPFileSystem ftp = new FTPFileSystem();
        Assert.assertEquals(ACTIVE_LOCAL_DATA_CONNECTION_MODE, client.getDataConnectionMode());
        ftp.setDataConnectionMode(client, conf);
        Assert.assertEquals(ACTIVE_LOCAL_DATA_CONNECTION_MODE, client.getDataConnectionMode());
        conf.set(FS_FTP_DATA_CONNECTION_MODE, "invalid");
        ftp.setDataConnectionMode(client, conf);
        Assert.assertEquals(ACTIVE_LOCAL_DATA_CONNECTION_MODE, client.getDataConnectionMode());
        conf.set(FS_FTP_DATA_CONNECTION_MODE, "PASSIVE_LOCAL_DATA_CONNECTION_MODE");
        ftp.setDataConnectionMode(client, conf);
        Assert.assertEquals(PASSIVE_LOCAL_DATA_CONNECTION_MODE, client.getDataConnectionMode());
    }

    @Test
    public void testGetFsAction() {
        FTPFileSystem ftp = new FTPFileSystem();
        int[] accesses = new int[]{ FTPFile.USER_ACCESS, FTPFile.GROUP_ACCESS, FTPFile.WORLD_ACCESS };
        FsAction[] actions = FsAction.values();
        for (int i = 0; i < (accesses.length); i++) {
            for (int j = 0; j < (actions.length); j++) {
                enhancedAssertEquals(actions[j], ftp.getFsAction(accesses[i], getFTPFileOf(accesses[i], actions[j])));
            }
        }
    }

    @Test
    public void testFTPSetTimeout() {
        Configuration conf = new Configuration();
        FTPClient client = new FTPClient();
        FTPFileSystem ftp = new FTPFileSystem();
        ftp.setTimeout(client, conf);
        Assert.assertEquals(client.getControlKeepAliveTimeout(), DEFAULT_TIMEOUT);
        long timeout = 600;
        conf.setLong(FS_FTP_TIMEOUT, timeout);
        ftp.setTimeout(client, conf);
        Assert.assertEquals(client.getControlKeepAliveTimeout(), timeout);
    }
}

