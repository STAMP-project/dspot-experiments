/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.web.client;


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.RatisTestHelper;
import org.apache.hadoop.ozone.client.protocol.ClientProtocol;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * The same as {@link TestKeys} except that this test is Ratis enabled.
 */
public class TestKeysRatis {
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    private static RatisTestHelper.RatisTestSuite suite;

    private static MiniOzoneCluster ozoneCluster = null;

    private static String path;

    private static ClientProtocol client = null;

    @Test
    public void testPutKey() throws Exception {
        TestKeys.runTestPutKey(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path));
        String delimiter = RandomStringUtils.randomAlphanumeric(1);
        TestKeys.runTestPutKey(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path, TestKeys.getMultiPartKey(delimiter)));
    }

    @Test
    public void testPutAndGetKeyWithDnRestart() throws Exception {
        TestKeys.runTestPutAndGetKeyWithDnRestart(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path), TestKeysRatis.ozoneCluster);
        String delimiter = RandomStringUtils.randomAlphanumeric(1);
        TestKeys.runTestPutAndGetKeyWithDnRestart(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path, TestKeys.getMultiPartKey(delimiter)), TestKeysRatis.ozoneCluster);
    }

    @Test
    public void testPutAndGetKey() throws Exception {
        TestKeys.runTestPutAndGetKey(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path));
        String delimiter = RandomStringUtils.randomAlphanumeric(1);
        TestKeys.runTestPutAndGetKey(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path, TestKeys.getMultiPartKey(delimiter)));
    }

    @Test
    public void testPutAndDeleteKey() throws Exception {
        TestKeys.runTestPutAndDeleteKey(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path));
        String delimiter = RandomStringUtils.randomAlphanumeric(1);
        TestKeys.runTestPutAndDeleteKey(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path, TestKeys.getMultiPartKey(delimiter)));
    }

    @Test
    public void testPutAndListKey() throws Exception {
        TestKeys.runTestPutAndListKey(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path));
        String delimiter = RandomStringUtils.randomAlphanumeric(1);
        TestKeys.runTestPutAndListKey(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path, TestKeys.getMultiPartKey(delimiter)));
    }

    @Test
    public void testGetKeyInfo() throws Exception {
        TestKeys.runTestGetKeyInfo(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path));
        String delimiter = RandomStringUtils.randomAlphanumeric(1);
        TestKeys.runTestGetKeyInfo(new TestKeys.PutHelper(TestKeysRatis.client, TestKeysRatis.path, TestKeys.getMultiPartKey(delimiter)));
    }
}

