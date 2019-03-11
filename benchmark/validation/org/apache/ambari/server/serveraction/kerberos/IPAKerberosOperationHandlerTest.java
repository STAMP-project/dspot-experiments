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
package org.apache.ambari.server.serveraction.kerberos;


import IPAKerberosOperationHandler.KERBEROS_ENV_USER_PRINCIPAL_GROUP;
import KDCKerberosOperationHandler.InteractivePasswordHandler;
import ShellCommandUtil.Result;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.utils.ShellCommandUtil;
import org.easymock.Capture;
import org.junit.Test;


public class IPAKerberosOperationHandlerTest extends KDCKerberosOperationHandlerTest {
    private static Injector injector;

    private static final Map<String, String> KERBEROS_ENV_MAP;

    static {
        Map<String, String> map = new HashMap<>(KerberosOperationHandlerTest.DEFAULT_KERBEROS_ENV_MAP);
        map.put(KERBEROS_ENV_USER_PRINCIPAL_GROUP, "");
        KERBEROS_ENV_MAP = Collections.unmodifiableMap(map);
    }

    @Test
    public void testGetAdminServerHost() throws KerberosOperationException {
        ShellCommandUtil.Result kinitResult = createMock(Result.class);
        expect(kinitResult.isSuccessful()).andReturn(true).anyTimes();
        Capture<String[]> capturedKinitCommand = newCapture(CaptureType.ALL);
        IPAKerberosOperationHandler handler = createMockedHandler(KDCKerberosOperationHandlerTest.methodExecuteCommand);
        expect(handler.executeCommand(capture(capturedKinitCommand), anyObject(Map.class), anyObject(InteractivePasswordHandler.class))).andReturn(kinitResult).anyTimes();
        Map<String, String> config = new HashMap<>();
        config.put("encryption_types", "aes des3-cbc-sha1 rc4 des-cbc-md5");
        replayAll();
        config.put("admin_server_host", "kdc.example.com");
        handler.open(getAdminCredentials(), KerberosOperationHandlerTest.DEFAULT_REALM, config);
        Assert.assertEquals("kdc.example.com", handler.getAdminServerHost(false));
        Assert.assertEquals("kdc.example.com", handler.getAdminServerHost(true));
        handler.close();
        config.put("admin_server_host", "kdc.example.com:749");
        handler.open(getAdminCredentials(), KerberosOperationHandlerTest.DEFAULT_REALM, config);
        Assert.assertEquals("kdc.example.com", handler.getAdminServerHost(false));
        Assert.assertEquals("kdc.example.com:749", handler.getAdminServerHost(true));
        handler.close();
        verifyAll();
        Assert.assertTrue(capturedKinitCommand.hasCaptured());
        List<String[]> capturedValues = capturedKinitCommand.getValues();
        Assert.assertEquals(2, capturedValues.size());
    }
}

