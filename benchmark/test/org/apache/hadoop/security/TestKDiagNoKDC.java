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
package org.apache.hadoop.security;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestKDiagNoKDC extends Assert {
    private static final Logger LOG = LoggerFactory.getLogger(TestKDiagNoKDC.class);

    public static final String KEYLEN = "128";

    @Rule
    public TestName methodName = new TestName();

    @Rule
    public Timeout testTimeout = new Timeout(30000);

    private static Configuration conf = new Configuration();

    /**
     * Test that the core kdiag command works when there's no KDC around.
     * This test produces different outcomes on hosts where there is a default
     * KDC -it needs to work on hosts without kerberos as well as those with it.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testKDiagStandalone() throws Throwable {
        kdiagFailure(KDiag.CAT_LOGIN, KDiag.ARG_KEYLEN, TestKDiagNoKDC.KEYLEN);
    }

    @Test
    public void testKDiagNoLogin() throws Throwable {
        kdiagFailure(KDiag.CAT_LOGIN, KDiag.ARG_KEYLEN, TestKDiagNoKDC.KEYLEN, KDiag.ARG_NOLOGIN);
    }

    @Test
    public void testKDiagStandaloneNofail() throws Throwable {
        kdiag(KDiag.ARG_KEYLEN, TestKDiagNoKDC.KEYLEN, KDiag.ARG_NOFAIL);
    }

    @Test
    public void testKDiagUsage() throws Throwable {
        Assert.assertEquals((-1), kdiag("usage"));
    }

    @Test
    public void testTokenFile() throws Throwable {
        TestKDiagNoKDC.conf.set(CommonConfigurationKeysPublic.HADOOP_TOKEN_FILES, "SomeNonExistentFile");
        kdiagFailure(KDiag.CAT_TOKEN, KDiag.ARG_KEYLEN, TestKDiagNoKDC.KEYLEN);
        TestKDiagNoKDC.conf.unset(CommonConfigurationKeysPublic.HADOOP_TOKEN_FILES);
    }
}

