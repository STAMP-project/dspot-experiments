/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.protocol.datatransfer.sasl;


import BlackListBasedTrustedChannelResolver.DFS_DATATRANSFER_CLIENT_FIXED_BLACK_LIST_FILE;
import BlackListBasedTrustedChannelResolver.DFS_DATATRANSFER_SERVER_FIXED_BLACK_LIST_FILE;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.protocol.datatransfer.BlackListBasedTrustedChannelResolver;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for  {@link BlackListBasedTrustedChannelResolver}.
 */
public class TestBlackListBasedTrustedChannelResolver {
    private static final String FILE_NAME = "blacklistfile.txt";

    private File blacklistFile;

    private static final String BLACK_LISTED = "127.0.0.1\n216.58.216.174\n";

    private BlackListBasedTrustedChannelResolver resolver;

    @Test
    public void testBlackListIpClient() throws IOException {
        Configuration conf = new Configuration();
        FileUtils.write(blacklistFile, InetAddress.getLocalHost().getHostAddress(), true);
        conf.set(DFS_DATATRANSFER_CLIENT_FIXED_BLACK_LIST_FILE, blacklistFile.getAbsolutePath());
        resolver.setConf(conf);
        Assert.assertFalse(resolver.isTrusted());
    }

    @Test
    public void testBlackListIpServer() throws UnknownHostException {
        Configuration conf = new Configuration();
        conf.set(DFS_DATATRANSFER_SERVER_FIXED_BLACK_LIST_FILE, blacklistFile.getAbsolutePath());
        resolver.setConf(conf);
        Assert.assertTrue(resolver.isTrusted());
        Assert.assertFalse(resolver.isTrusted(InetAddress.getByName("216.58.216.174")));
    }
}

