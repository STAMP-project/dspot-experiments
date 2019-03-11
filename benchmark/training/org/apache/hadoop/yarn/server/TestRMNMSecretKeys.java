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
package org.apache.hadoop.yarn.server;


import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import java.io.File;
import java.util.UUID;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Test;


public class TestRMNMSecretKeys {
    private static final String KRB5_CONF = "java.security.krb5.conf";

    private static final File KRB5_CONF_ROOT_DIR = new File(System.getProperty("test.build.dir", "target/test-dir"), UUID.randomUUID().toString());

    @Test(timeout = 1000000)
    public void testNMUpdation() throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        // validating RM NM keys for Unsecured environment
        validateRMNMKeyExchange(conf);
        // validating RM NM keys for secured environment
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        validateRMNMKeyExchange(conf);
    }
}

