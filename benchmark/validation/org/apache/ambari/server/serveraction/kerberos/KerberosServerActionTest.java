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


import HostRoleStatus.COMPLETED;
import HostRoleStatus.FAILED;
import KDCType.MIT_KDC;
import KerberosServerAction.DEFAULT_REALM;
import com.google.inject.Injector;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.agent.CommandReport;
import org.apache.ambari.server.controller.KerberosHelper;
import org.apache.ambari.server.security.credential.PrincipalKeyCredential;
import org.apache.ambari.server.serveraction.kerberos.stageutils.KerberosKeytabController;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Config;
import org.easymock.EasyMockSupport;
import org.junit.Test;


public class KerberosServerActionTest extends EasyMockSupport {
    private static final Map<String, String> KERBEROS_ENV_PROPERTIES = Collections.singletonMap("admin_server_host", "kdc.example.com");

    Map<String, String> commandParams = new HashMap<>();

    File temporaryDirectory;

    private Injector injector;

    private KerberosServerAction action;

    private Cluster cluster;

    private KerberosKeytabController kerberosKeytabController;

    @Test
    public void testGetCommandParameterValueStatic() throws Exception {
        Assert.assertNull(KerberosServerAction.getCommandParameterValue(commandParams, "nonexistingvalue"));
        Assert.assertEquals("REALM.COM", KerberosServerAction.getCommandParameterValue(commandParams, DEFAULT_REALM));
    }

    @Test
    public void testGetDefaultRealmStatic() throws Exception {
        Assert.assertEquals("REALM.COM", KerberosServerAction.getDefaultRealm(commandParams));
    }

    @Test
    public void testGetKDCTypeStatic() throws Exception {
        Assert.assertEquals(MIT_KDC, KerberosServerAction.getKDCType(commandParams));
    }

    @Test
    public void testGetDataDirectoryPathStatic() throws Exception {
        Assert.assertEquals(temporaryDirectory.getAbsolutePath(), KerberosServerAction.getDataDirectoryPath(commandParams));
    }

    @Test
    public void testSetPrincipalPasswordMapStatic() throws Exception {
        ConcurrentMap<String, Object> sharedMap = new ConcurrentHashMap<>();
        Map<String, String> dataMap = new HashMap<>();
        KerberosServerAction.setPrincipalPasswordMap(sharedMap, dataMap);
        Assert.assertSame(dataMap, KerberosServerAction.getPrincipalPasswordMap(sharedMap));
    }

    @Test
    public void testGetPrincipalPasswordMapStatic() throws Exception {
        ConcurrentMap<String, Object> sharedMap = new ConcurrentHashMap<>();
        Assert.assertNotNull(KerberosServerAction.getPrincipalPasswordMap(sharedMap));
    }

    @Test
    public void testGetDataDirectoryPath() throws Exception {
        replayAll();
        Assert.assertEquals(temporaryDirectory.getAbsolutePath(), action.getDataDirectoryPath());
        verifyAll();
    }

    @Test
    public void testProcessIdentitiesSuccess() throws Exception {
        KerberosHelper kerberosHelper = injector.getInstance(KerberosHelper.class);
        expect(kerberosHelper.getKDCAdministratorCredentials(anyObject(String.class))).andReturn(new PrincipalKeyCredential("principal", "password")).anyTimes();
        KerberosOperationHandler kerberosOperationHandler = createMock(KerberosOperationHandler.class);
        kerberosOperationHandler.open(anyObject(PrincipalKeyCredential.class), anyString(), anyObject(Map.class));
        expectLastCall().atLeastOnce();
        kerberosOperationHandler.close();
        expectLastCall().atLeastOnce();
        KerberosOperationHandlerFactory factory = injector.getInstance(KerberosOperationHandlerFactory.class);
        expect(factory.getKerberosOperationHandler(MIT_KDC)).andReturn(kerberosOperationHandler).once();
        replayAll();
        ConcurrentMap<String, Object> sharedMap = new ConcurrentHashMap<>();
        CommandReport report = action.processIdentities(sharedMap);
        Assert.assertNotNull(report);
        Assert.assertEquals(COMPLETED.toString(), report.getStatus());
        for (Map.Entry<String, Object> entry : sharedMap.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey());
        }
        verifyAll();
    }

    @Test
    public void testProcessIdentitiesFail() throws Exception {
        KerberosHelper kerberosHelper = injector.getInstance(KerberosHelper.class);
        expect(kerberosHelper.getKDCAdministratorCredentials(anyObject(String.class))).andReturn(new PrincipalKeyCredential("principal", "password")).anyTimes();
        KerberosOperationHandler kerberosOperationHandler = createMock(KerberosOperationHandler.class);
        kerberosOperationHandler.open(anyObject(PrincipalKeyCredential.class), anyString(), anyObject(Map.class));
        expectLastCall().atLeastOnce();
        kerberosOperationHandler.close();
        expectLastCall().atLeastOnce();
        KerberosOperationHandlerFactory factory = injector.getInstance(KerberosOperationHandlerFactory.class);
        expect(factory.getKerberosOperationHandler(MIT_KDC)).andReturn(kerberosOperationHandler).once();
        replayAll();
        ConcurrentMap<String, Object> sharedMap = new ConcurrentHashMap<>();
        sharedMap.put("FAIL", "true");
        CommandReport report = action.processIdentities(sharedMap);
        Assert.assertNotNull(report);
        Assert.assertEquals(FAILED.toString(), report.getStatus());
        verifyAll();
    }

    @Test
    public void testGetConfigurationProperties() throws AmbariException {
        Config emptyConfig = createMock(Config.class);
        expect(emptyConfig.getProperties()).andReturn(Collections.emptyMap()).once();
        Config missingPropertiesConfig = createMock(Config.class);
        expect(missingPropertiesConfig.getProperties()).andReturn(null).once();
        expect(cluster.getDesiredConfigByType("invalid-type")).andReturn(null).once();
        expect(cluster.getDesiredConfigByType("missing-properties-type")).andReturn(missingPropertiesConfig).once();
        expect(cluster.getDesiredConfigByType("empty-type")).andReturn(emptyConfig).once();
        replayAll();
        Assert.assertNull(action.getConfigurationProperties(null));
        Assert.assertNull(action.getConfigurationProperties("invalid-type"));
        Assert.assertNull(action.getConfigurationProperties("missing-properties-type"));
        Assert.assertEquals(Collections.emptyMap(), action.getConfigurationProperties("empty-type"));
        Assert.assertEquals(KerberosServerActionTest.KERBEROS_ENV_PROPERTIES, action.getConfigurationProperties("kerberos-env"));
        verifyAll();
    }
}

