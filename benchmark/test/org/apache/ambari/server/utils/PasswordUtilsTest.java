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
package org.apache.ambari.server.utils;


import com.google.inject.Injector;
import java.io.File;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.security.encryption.CredentialProvider;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(PasswordUtils.class)
public class PasswordUtilsTest extends EasyMockSupport {
    private static final String CS_ALIAS = "${alias=testAlias}";

    private PasswordUtils passwordUtils;

    private Injector injector;

    private Configuration configuration;

    @Test
    public void shouldReadPasswordFromCredentialStoreOfAnAlias() throws Exception {
        final CredentialProvider credentialProvider = PowerMock.createNiceMock(CredentialProvider.class);
        setupBasicCredentialProviderExpectations(credentialProvider);
        credentialProvider.getPasswordForAlias(PasswordUtilsTest.CS_ALIAS);
        PowerMock.expectLastCall().andReturn("testPassword".toCharArray()).once();
        PowerMock.replay(credentialProvider, CredentialProvider.class);
        replayAll();
        Assert.assertEquals("testPassword", passwordUtils.readPassword(PasswordUtilsTest.CS_ALIAS, "testPassword"));
        verifyAll();
    }

    @Test
    public void shouldReadPasswordFromFileIfPasswordPropertyIsPasswordFilePath() throws Exception {
        final String testPassword = "ambariTest";
        final File passwordFile = writeTestPasswordFile(testPassword);
        Assert.assertEquals("ambariTest", passwordUtils.readPassword(passwordFile.getAbsolutePath(), "testPasswordDefault"));
    }

    @Test
    public void shouldReadDefaultPasswordIfPasswordPropertyIsPasswordFilePathButItDoesNotExists() throws Exception {
        final File passwordFile = new File("/my/test/password/file.dat");
        Assert.assertEquals("testPasswordDefault", passwordUtils.readPassword(passwordFile.getAbsolutePath(), "testPasswordDefault"));
    }
}

