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


import YarnConfiguration.RM_AM_EXPIRY_INTERVAL_MS;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.minikdc.KerberosSecurityTestcase;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class TestContainerManagerSecurity extends KerberosSecurityTestcase {
    static Logger LOG = LoggerFactory.getLogger(TestContainerManagerSecurity.class);

    static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    private static MiniYARNCluster yarnCluster;

    private static final File testRootDir = new File("target", ((TestContainerManagerSecurity.class.getName()) + "-root"));

    private static File httpSpnegoKeytabFile = new File(TestContainerManagerSecurity.testRootDir, "httpSpnegoKeytabFile.keytab");

    private static String httpSpnegoPrincipal = "HTTP/localhost@EXAMPLE.COM";

    private Configuration conf;

    public TestContainerManagerSecurity(String name, Configuration conf) {
        TestContainerManagerSecurity.LOG.info(("RUNNING TEST " + name));
        conf.setLong(RM_AM_EXPIRY_INTERVAL_MS, 100000L);
        this.conf = conf;
    }

    @Test
    public void testContainerManager() throws Exception {
        // TestNMTokens.
        testNMTokens(conf);
        // Testing for container token tampering
        testContainerToken(conf);
        // Testing for container token tampering with epoch
        testContainerTokenWithEpoch(conf);
    }
}

