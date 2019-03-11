/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.topology;


import SecurityConfigurationFactory.KERBEROS_DESCRIPTOR_PROPERTY_ID;
import SecurityConfigurationFactory.KERBEROS_DESCRIPTOR_REFERENCE_PROPERTY_ID;
import SecurityConfigurationFactory.SECURITY_PROPERTY_ID;
import SecurityConfigurationFactory.TYPE_PROPERTY_ID;
import SecurityType.KERBEROS;
import SecurityType.NONE;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.orm.dao.KerberosDescriptorDAO;
import org.apache.ambari.server.orm.entities.KerberosDescriptorEntity;
import org.apache.ambari.server.state.SecurityType;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class SecurityConfigurationFactoryTest {
    private static final String TEST_KERBEROS_DESCRIPTOR_REFERENCE = "test-kd-reference";

    private static final String TEST_KERBEROS_DESCRIPTOR_JSON = "{\"test\":\"test json\"}";

    @Rule
    public EasyMockRule mocks = new EasyMockRule(this);

    @Mock(type = MockType.STRICT)
    private KerberosDescriptorDAO kerberosDescriptorDAO;

    private SecurityConfigurationFactory testSubject;

    @Test
    public void testShouldLoadKerberosDescriptorWhenKDReferenceFoundInRequest() throws Exception {
        EasyMock.expect(kerberosDescriptorDAO.findByName(SecurityConfigurationFactoryTest.TEST_KERBEROS_DESCRIPTOR_REFERENCE)).andReturn(testKDEntity());
        Map<String, Object> reuqestMap = new HashMap<>();
        Map<String, Object> security = new HashMap<>();
        security.put(TYPE_PROPERTY_ID, KERBEROS.toString());
        security.put(KERBEROS_DESCRIPTOR_REFERENCE_PROPERTY_ID, SecurityConfigurationFactoryTest.TEST_KERBEROS_DESCRIPTOR_REFERENCE);
        reuqestMap.put(SECURITY_PROPERTY_ID, security);
        EasyMock.replay(kerberosDescriptorDAO);
        SecurityConfiguration securityConfiguration = testSubject.createSecurityConfigurationFromRequest(reuqestMap, false);
        EasyMock.verify(kerberosDescriptorDAO);
        Assert.assertTrue(((securityConfiguration.getType()) == (SecurityType.KERBEROS)));
    }

    @Test
    public void testShouldPersistKDWhenKDFoundInRequest() throws Exception {
        // GIVEN
        Capture<KerberosDescriptorEntity> kdEntityCaptor = EasyMock.newCapture();
        kerberosDescriptorDAO.create(capture(kdEntityCaptor));
        EasyMock.replay(kerberosDescriptorDAO);
        Map<String, Object> reuqestMap = new HashMap<>();
        Map<String, Object> security = new HashMap<>();
        security.put(TYPE_PROPERTY_ID, KERBEROS.toString());
        security.put(KERBEROS_DESCRIPTOR_PROPERTY_ID, testKDReqPropertyMap());
        reuqestMap.put(SECURITY_PROPERTY_ID, security);
        // WHEN
        testSubject.createSecurityConfigurationFromRequest(reuqestMap, true);
        // THEN
        EasyMock.verify(kerberosDescriptorDAO);
        Assert.assertEquals("The persisted descriptortext is not as expected", "{\"test\":\"{\\\"test\\\":\\\"test json\\\"}\"}", kdEntityCaptor.getValue().getKerberosDescriptorText());
        Assert.assertNotNull("There is no generated kerberos descriptor reference in the persisting entity!", kdEntityCaptor.getValue().getName());
    }

    @Test
    public void testCreateKerberosSecurityWithoutDescriptor() throws Exception {
        Map<String, Object> reuqestMap = new HashMap<>();
        Map<String, Object> security = new HashMap<>();
        security.put(TYPE_PROPERTY_ID, KERBEROS.toString());
        reuqestMap.put(SECURITY_PROPERTY_ID, security);
        SecurityConfiguration securityConfiguration = testSubject.createSecurityConfigurationFromRequest(reuqestMap, false);
        Assert.assertTrue(((securityConfiguration.getType()) == (SecurityType.KERBEROS)));
    }

    @Test
    public void testCreateEmpty() throws Exception {
        Map<String, Object> reuqestMap = new HashMap<>();
        SecurityConfiguration securityConfiguration = testSubject.createSecurityConfigurationFromRequest(reuqestMap, false);
        Assert.assertTrue((securityConfiguration == null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidSecurityType() throws Exception {
        Map<String, Object> reuqestMap = new HashMap<>();
        Map<String, Object> security = new HashMap<>();
        security.put(TYPE_PROPERTY_ID, "INVALID_SECURITY_TYPE");
        reuqestMap.put(SECURITY_PROPERTY_ID, security);
        SecurityConfiguration securityConfiguration = testSubject.createSecurityConfigurationFromRequest(reuqestMap, false);
        Assert.assertTrue(((securityConfiguration.getType()) == (SecurityType.KERBEROS)));
    }

    @Test
    public void testCreateKerberosSecurityTypeNone() throws Exception {
        Map<String, Object> reuqestMap = new HashMap<>();
        Map<String, Object> security = new HashMap<>();
        security.put(TYPE_PROPERTY_ID, NONE.toString());
        reuqestMap.put(SECURITY_PROPERTY_ID, security);
        SecurityConfiguration securityConfiguration = testSubject.createSecurityConfigurationFromRequest(reuqestMap, false);
        Assert.assertTrue(((securityConfiguration.getType()) == (SecurityType.NONE)));
    }
}

