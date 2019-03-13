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
package org.apache.ambari.server.topology;


import Stack.ConfigProperty;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.controller.internal.Stack;
import org.apache.ambari.server.topology.validators.RequiredPasswordValidator;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Rule;
import org.junit.Test;


/**
 * Unit tests for RequiredPasswordValidator.
 */
public class RequiredPasswordValidatorTest extends EasyMockSupport {
    @Rule
    public EasyMockRule mocks = new EasyMockRule(this);

    @Mock
    private ClusterTopology topology;

    @Mock
    private Blueprint blueprint;

    @Mock
    private Stack stack;

    @Mock
    private HostGroup group1;

    @Mock
    private HostGroup group2;

    private static Configuration stackDefaults;

    private static Configuration bpClusterConfig;

    private static Configuration topoClusterConfig;

    private static Configuration bpGroup1Config;

    private static Configuration bpGroup2Config;

    private static Configuration topoGroup1Config;

    private static Configuration topoGroup2Config;

    private static final Map<String, HostGroup> hostGroups = new HashMap<>();

    private static final Map<String, HostGroupInfo> hostGroupInfo = new HashMap<>();

    private static final Collection<String> group1Components = new HashSet<>();

    private static final Collection<String> group2Components = new HashSet<>();

    private static final Collection<String> service1Components = new HashSet<>();

    private static final Collection<String> service2Components = new HashSet<>();

    private static final Collection<String> service3Components = new HashSet<>();

    private static final Collection<Stack.ConfigProperty> service1RequiredPwdConfigs = new HashSet<>();

    private static final Collection<Stack.ConfigProperty> service2RequiredPwdConfigs = new HashSet<>();

    private static final Collection<Stack.ConfigProperty> service3RequiredPwdConfigs = new HashSet<>();

    @TestSubject
    private RequiredPasswordValidator validator = new RequiredPasswordValidator();

    @Test
    public void testValidate_noRequiredProps__noDefaultPwd() throws Exception {
        // GIVEN
        // no required pwd properties so shouldn't throw an exception
        expect(topology.getDefaultPassword()).andReturn(null);
        replayAll();
        // WHEN
        validator.validate(topology);
    }

    @Test
    public void testValidate_noRequiredProps__defaultPwd() throws Exception {
        // GIVEN
        expect(topology.getDefaultPassword()).andReturn("pwd");
        replayAll();
        // WHEN
        validator.validate(topology);
    }

    @Test(expected = InvalidTopologyException.class)
    public void testValidate_missingPwd__NoDefaultPwd() throws Exception {
        expect(topology.getDefaultPassword()).andReturn(null);
        replayAll();
        Stack.ConfigProperty pwdProp = new Stack.ConfigProperty("test-type", "pwdProp", null);
        RequiredPasswordValidatorTest.service1RequiredPwdConfigs.add(pwdProp);
        validator.validate(topology);
    }

    @Test
    public void testValidate_missingPwd__defaultPwd() throws Exception {
        expect(topology.getDefaultPassword()).andReturn("default-pwd");
        replayAll();
        Stack.ConfigProperty pwdProp = new Stack.ConfigProperty("test-type", "pwdProp", null);
        RequiredPasswordValidatorTest.service1RequiredPwdConfigs.add(pwdProp);
        // default value should be set
        validator.validate(topology);
        Assert.assertEquals(1, RequiredPasswordValidatorTest.topoClusterConfig.getProperties().size());
        Assert.assertEquals("default-pwd", RequiredPasswordValidatorTest.topoClusterConfig.getProperties().get("test-type").get("pwdProp"));
    }

    @Test
    public void testValidate_pwdPropertyInTopoGroupConfig__NoDefaultPwd() throws Exception {
        expect(topology.getDefaultPassword()).andReturn(null);
        replayAll();
        Stack.ConfigProperty pwdProp = new Stack.ConfigProperty("test-type", "pwdProp", null);
        RequiredPasswordValidatorTest.service3RequiredPwdConfigs.add(pwdProp);
        // group2 has a component from service 3
        RequiredPasswordValidatorTest.topoGroup2Config.getProperties().put("test-type", Collections.singletonMap("pwdProp", "secret"));
        validator.validate(topology);
    }

    @Test
    public void testValidate_pwdPropertyInTopoClusterConfig__NoDefaultPwd() throws Exception {
        expect(topology.getDefaultPassword()).andReturn(null);
        replayAll();
        Stack.ConfigProperty pwdProp = new Stack.ConfigProperty("test-type", "pwdProp", null);
        RequiredPasswordValidatorTest.service3RequiredPwdConfigs.add(pwdProp);
        // group2 has a component from service 3
        RequiredPasswordValidatorTest.topoClusterConfig.getProperties().put("test-type", Collections.singletonMap("pwdProp", "secret"));
        validator.validate(topology);
    }

    @Test
    public void testValidate_pwdPropertyInBPGroupConfig__NoDefaultPwd() throws Exception {
        expect(topology.getDefaultPassword()).andReturn(null);
        replayAll();
        Stack.ConfigProperty pwdProp = new Stack.ConfigProperty("test-type", "pwdProp", null);
        RequiredPasswordValidatorTest.service3RequiredPwdConfigs.add(pwdProp);
        // group2 has a component from service 3
        RequiredPasswordValidatorTest.bpGroup2Config.getProperties().put("test-type", Collections.singletonMap("pwdProp", "secret"));
        validator.validate(topology);
    }

    @Test
    public void testValidate_pwdPropertyInBPClusterConfig__NoDefaultPwd() throws Exception {
        expect(topology.getDefaultPassword()).andReturn(null);
        replayAll();
        Stack.ConfigProperty pwdProp = new Stack.ConfigProperty("test-type", "pwdProp", null);
        RequiredPasswordValidatorTest.service3RequiredPwdConfigs.add(pwdProp);
        // group2 has a component from service 3
        RequiredPasswordValidatorTest.bpClusterConfig.getProperties().put("test-type", Collections.singletonMap("pwdProp", "secret"));
        validator.validate(topology);
    }

    @Test(expected = InvalidTopologyException.class)
    public void testValidate_pwdPropertyInStackConfig__NoDefaultPwd() throws Exception {
        expect(topology.getDefaultPassword()).andReturn(null);
        replayAll();
        Stack.ConfigProperty pwdProp = new Stack.ConfigProperty("test-type", "pwdProp", null);
        RequiredPasswordValidatorTest.service3RequiredPwdConfigs.add(pwdProp);
        // group2 has a component from service 3
        RequiredPasswordValidatorTest.stackDefaults.getProperties().put("test-type", Collections.singletonMap("pwdProp", "secret"));
        // because stack config is ignored for validation, an exception should be thrown
        validator.validate(topology);
    }

    @Test
    public void testValidate_twoRequiredPwdOneSpecified__defaultPwd() throws Exception {
        expect(topology.getDefaultPassword()).andReturn("default-pwd");
        replayAll();
        Stack.ConfigProperty pwdProp = new Stack.ConfigProperty("test-type", "pwdProp", null);
        Stack.ConfigProperty pwdProp2 = new Stack.ConfigProperty("test2-type", "pwdProp2", null);
        RequiredPasswordValidatorTest.service1RequiredPwdConfigs.add(pwdProp);
        RequiredPasswordValidatorTest.service3RequiredPwdConfigs.add(pwdProp2);
        RequiredPasswordValidatorTest.topoClusterConfig.getProperties().put("test2-type", Collections.singletonMap("pwdProp2", "secret"));
        // default value should be set
        validator.validate(topology);
        Assert.assertEquals(2, RequiredPasswordValidatorTest.topoClusterConfig.getProperties().size());
        Assert.assertEquals("default-pwd", RequiredPasswordValidatorTest.topoClusterConfig.getProperties().get("test-type").get("pwdProp"));
        Assert.assertEquals("secret", RequiredPasswordValidatorTest.topoClusterConfig.getProperties().get("test2-type").get("pwdProp2"));
    }

    @Test
    public void testValidate_twoRequiredPwdTwoSpecified__noDefaultPwd() throws Exception {
        expect(topology.getDefaultPassword()).andReturn("default-pwd");
        replayAll();
        Stack.ConfigProperty pwdProp = new Stack.ConfigProperty("test-type", "pwdProp", null);
        Stack.ConfigProperty pwdProp2 = new Stack.ConfigProperty("test2-type", "pwdProp2", null);
        RequiredPasswordValidatorTest.service1RequiredPwdConfigs.add(pwdProp);
        RequiredPasswordValidatorTest.service3RequiredPwdConfigs.add(pwdProp2);
        RequiredPasswordValidatorTest.topoClusterConfig.getProperties().put("test2-type", Collections.singletonMap("pwdProp2", "secret2"));
        RequiredPasswordValidatorTest.topoClusterConfig.getProperties().put("test-type", Collections.singletonMap("pwdProp", "secret1"));
        // default value should be set
        validator.validate(topology);
        Assert.assertEquals(2, RequiredPasswordValidatorTest.topoClusterConfig.getProperties().size());
        Assert.assertEquals("secret1", RequiredPasswordValidatorTest.topoClusterConfig.getProperties().get("test-type").get("pwdProp"));
        Assert.assertEquals("secret2", RequiredPasswordValidatorTest.topoClusterConfig.getProperties().get("test2-type").get("pwdProp2"));
    }

    @Test
    public void testValidate_multipleMissingPwd__defaultPwd() throws Exception {
        expect(topology.getDefaultPassword()).andReturn("default-pwd");
        replayAll();
        Stack.ConfigProperty pwdProp = new Stack.ConfigProperty("test-type", "pwdProp", null);
        Stack.ConfigProperty pwdProp2 = new Stack.ConfigProperty("test2-type", "pwdProp2", null);
        RequiredPasswordValidatorTest.service1RequiredPwdConfigs.add(pwdProp);
        RequiredPasswordValidatorTest.service3RequiredPwdConfigs.add(pwdProp2);
        // default value should be set
        validator.validate(topology);
        Assert.assertEquals(2, RequiredPasswordValidatorTest.topoClusterConfig.getProperties().size());
        Assert.assertEquals("default-pwd", RequiredPasswordValidatorTest.topoClusterConfig.getProperties().get("test-type").get("pwdProp"));
        Assert.assertEquals("default-pwd", RequiredPasswordValidatorTest.topoClusterConfig.getProperties().get("test2-type").get("pwdProp2"));
    }
}

