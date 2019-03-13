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


import Configuration.SRVR_KEY_NAME;
import ShellCommandUtil.MASK_OWNER_ONLY_RW;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import java.io.File;
import java.util.Properties;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.state.stack.OsFamily;
import org.apache.ambari.server.utils.ShellCommandUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CertGenerationTest {
    private static final int PASS_FILE_NAME_LEN = 20;

    private static final float MAX_PASS_LEN = 100;

    private static final Logger LOG = LoggerFactory.getLogger(CertGenerationTest.class);

    public static TemporaryFolder temp = new TemporaryFolder();

    private static Injector injector;

    private static CertificateManager certMan;

    private static String passFileName;

    private static int passLen;

    private static class SecurityModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Properties.class).toInstance(CertGenerationTest.buildTestProperties());
            bind(Configuration.class).toConstructor(CertGenerationTest.getConfigurationConstructor());
            bind(OsFamily.class).toInstance(createNiceMock(OsFamily.class));
            requestStaticInjection(CertGenerationTest.class);
        }
    }

    @Test
    public void testServerKeyGen() throws Exception {
        File serverKey = new File((((CertGenerationTest.temp.getRoot().getAbsoluteFile()) + (File.separator)) + (SRVR_KEY_NAME.getDefaultValue())));
        Assert.assertTrue(serverKey.exists());
    }

    @Test
    public void testPassFileGen() throws Exception {
        File passFile = new File((((CertGenerationTest.temp.getRoot().getAbsolutePath()) + (File.separator)) + (CertGenerationTest.passFileName)));
        Assert.assertTrue(passFile.exists());
        String pass = FileUtils.readFileToString(passFile);
        Assert.assertEquals(pass.length(), CertGenerationTest.passLen);
        if (ShellCommandUtil.LINUX) {
            String permissions = ShellCommandUtil.getUnixFilePermissions(passFile.getAbsolutePath());
            Assert.assertEquals(MASK_OWNER_ONLY_RW, permissions);
        }
    }
}

