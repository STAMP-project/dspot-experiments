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
package org.apache.hadoop.fs.aliyun.oss;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.contract.AbstractFSContractTestBase;
import org.junit.Test;


/**
 * Tests use of temporary credentials (for example, Aliyun STS & Aliyun OSS).
 * This test extends a class that "does things to the root directory", and
 * should only be used against transient filesystems where you don't care about
 * the data.
 */
public class TestAliyunCredentials extends AbstractFSContractTestBase {
    @Test
    public void testCredentialMissingAccessKeyId() throws Throwable {
        Configuration conf = new Configuration();
        conf.set(Constants.ACCESS_KEY_ID, "");
        conf.set(Constants.ACCESS_KEY_SECRET, "accessKeySecret");
        conf.set(Constants.SECURITY_TOKEN, "token");
        validateCredential(conf);
    }

    @Test
    public void testCredentialMissingAccessKeySecret() throws Throwable {
        Configuration conf = new Configuration();
        conf.set(Constants.ACCESS_KEY_ID, "accessKeyId");
        conf.set(Constants.ACCESS_KEY_SECRET, "");
        conf.set(Constants.SECURITY_TOKEN, "token");
        validateCredential(conf);
    }
}

