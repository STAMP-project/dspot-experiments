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
package org.apache.ambari.server.security;


import Configuration.PASSPHRASE;
import Configuration.SRVR_KSTR_DIR;
import SignCertResponse.ERROR_STATUS;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import java.util.Properties;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.state.stack.OsFamily;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SslExecutionTest {
    private static final Logger LOG = LoggerFactory.getLogger(SslExecutionTest.class);

    public TemporaryFolder temp = new TemporaryFolder();

    Injector injector;

    private static CertificateManager certMan;

    private class SecurityModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Properties.class).toInstance(buildTestProperties());
            bind(Configuration.class).toConstructor(getConfigurationConstructor());
            bind(OsFamily.class).toInstance(createNiceMock(OsFamily.class));
            requestStaticInjection(SslExecutionTest.class);
        }
    }

    @Test
    public void testSslLogging() throws Exception {
        SslExecutionTest.LOG.info("Testing sign");
        SslExecutionTest.certMan.configs.getConfigsMap().put(PASSPHRASE.getKey(), "123123");
        SslExecutionTest.LOG.info(("key dir = " + (SslExecutionTest.certMan.configs.getConfigsMap().get(SRVR_KSTR_DIR.getKey()))));
        SignCertResponse signAgentCrt = SslExecutionTest.certMan.signAgentCrt("somehost", "gdfgdfg", "123123");
        SslExecutionTest.LOG.info("-------------RESPONCE-------------");
        SslExecutionTest.LOG.info("-------------MESSAGE--------------");
        SslExecutionTest.LOG.info(signAgentCrt.getMessage());
        SslExecutionTest.LOG.info("---------------------------------");
        SslExecutionTest.LOG.info("-------------RESULT--------------");
        SslExecutionTest.LOG.info(signAgentCrt.getResult());
        SslExecutionTest.LOG.info("---------------------------------");
        Assert.assertTrue(ERROR_STATUS.equals(signAgentCrt.getResult()));
    }
}

