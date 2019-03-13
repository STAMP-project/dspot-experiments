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
package org.apache.hadoop.hbase.io.asyncfs;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hbase.thirdparty.io.netty.channel.Channel;
import org.apache.hbase.thirdparty.io.netty.channel.EventLoopGroup;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ MiscTests.class, LargeTests.class })
public class TestSaslFanOutOneBlockAsyncDFSOutput {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSaslFanOutOneBlockAsyncDFSOutput.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static DistributedFileSystem FS;

    private static EventLoopGroup EVENT_LOOP_GROUP;

    private static Class<? extends Channel> CHANNEL_CLASS;

    private static int READ_TIMEOUT_MS = 200000;

    private static final File KEYTAB_FILE = new File(getDataTestDir("keytab").toUri().getPath());

    private static MiniKdc KDC;

    private static String HOST = "localhost";

    private static String USERNAME;

    private static String PRINCIPAL;

    private static String HTTP_PRINCIPAL;

    private static String TEST_KEY_NAME = "test_key";

    @Rule
    public TestName name = new TestName();

    @Parameterized.Parameter(0)
    public String protection;

    @Parameterized.Parameter(1)
    public String encryptionAlgorithm;

    @Parameterized.Parameter(2)
    public String cipherSuite;

    private Path testDirOnTestFs;

    private Path entryptionTestDirOnTestFs;

    @Test
    public void test() throws IOException, InterruptedException, ExecutionException {
        test(getTestFile());
        test(getEncryptionTestFile());
    }
}

