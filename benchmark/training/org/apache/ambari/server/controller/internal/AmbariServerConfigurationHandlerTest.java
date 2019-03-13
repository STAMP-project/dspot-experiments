/**
 * * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package org.apache.ambari.server.controller.internal;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.api.services.RootServiceComponentConfiguration;
import org.apache.ambari.server.configuration.AmbariServerConfigurationCategory;
import org.apache.ambari.server.configuration.AmbariServerConfigurationKey;
import org.apache.ambari.server.events.AmbariConfigurationChangedEvent;
import org.apache.ambari.server.events.publishers.AmbariEventPublisher;
import org.apache.ambari.server.orm.dao.AmbariConfigurationDAO;
import org.apache.ambari.server.orm.entities.AmbariConfigurationEntity;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class AmbariServerConfigurationHandlerTest extends EasyMockSupport {
    @Test
    public void getComponentConfigurations() {
        List<AmbariConfigurationEntity> ssoEntities = new ArrayList<>();
        ssoEntities.add(createEntity(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.SSO_MANAGE_SERVICES.key(), "true"));
        ssoEntities.add(createEntity(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.SSO_ENABLED_SERVICES.key(), "AMBARI,SERVICE1"));
        List<AmbariConfigurationEntity> ldapEntities = new ArrayList<>();
        ldapEntities.add(createEntity(AmbariServerConfigurationCategory.LDAP_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.LDAP_ENABLED.key(), "true"));
        ldapEntities.add(createEntity(AmbariServerConfigurationCategory.LDAP_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.SERVER_HOST.key(), "host1"));
        List<AmbariConfigurationEntity> tproxyEntities = new ArrayList<>();
        tproxyEntities.add(createEntity(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key(), "true"));
        tproxyEntities.add(createEntity(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName(), "ambari.tproxy.proxyuser.knox.hosts", "host1"));
        List<AmbariConfigurationEntity> allEntities = new ArrayList<>();
        allEntities.addAll(ssoEntities);
        allEntities.addAll(ldapEntities);
        allEntities.addAll(tproxyEntities);
        AmbariConfigurationDAO ambariConfigurationDAO = createMock(AmbariConfigurationDAO.class);
        expect(ambariConfigurationDAO.findAll()).andReturn(allEntities).once();
        expect(ambariConfigurationDAO.findByCategory(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName())).andReturn(ssoEntities).once();
        expect(ambariConfigurationDAO.findByCategory(AmbariServerConfigurationCategory.LDAP_CONFIGURATION.getCategoryName())).andReturn(ldapEntities).once();
        expect(ambariConfigurationDAO.findByCategory(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName())).andReturn(tproxyEntities).once();
        expect(ambariConfigurationDAO.findByCategory("invalid category")).andReturn(null).once();
        AmbariEventPublisher publisher = createMock(AmbariEventPublisher.class);
        AmbariServerConfigurationHandler handler = new AmbariServerConfigurationHandler(ambariConfigurationDAO, publisher);
        replayAll();
        Map<String, RootServiceComponentConfiguration> allConfigurations = handler.getComponentConfigurations(null);
        Assert.assertEquals(3, allConfigurations.size());
        Assert.assertTrue(allConfigurations.containsKey(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName()));
        Assert.assertTrue(allConfigurations.containsKey(AmbariServerConfigurationCategory.LDAP_CONFIGURATION.getCategoryName()));
        Assert.assertTrue(allConfigurations.containsKey(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName()));
        Map<String, RootServiceComponentConfiguration> ssoConfigurations = handler.getComponentConfigurations(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName());
        Assert.assertEquals(1, ssoConfigurations.size());
        Assert.assertTrue(ssoConfigurations.containsKey(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName()));
        Map<String, RootServiceComponentConfiguration> ldapConfigurations = handler.getComponentConfigurations(AmbariServerConfigurationCategory.LDAP_CONFIGURATION.getCategoryName());
        Assert.assertEquals(1, ldapConfigurations.size());
        Assert.assertTrue(ldapConfigurations.containsKey(AmbariServerConfigurationCategory.LDAP_CONFIGURATION.getCategoryName()));
        Map<String, RootServiceComponentConfiguration> tproxyConfigurations = handler.getComponentConfigurations(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName());
        Assert.assertEquals(1, tproxyConfigurations.size());
        Assert.assertTrue(tproxyConfigurations.containsKey(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName()));
        Map<String, RootServiceComponentConfiguration> invalidConfigurations = handler.getComponentConfigurations("invalid category");
        Assert.assertNull(invalidConfigurations);
        verifyAll();
    }

    @Test
    public void removeComponentConfiguration() {
        AmbariConfigurationDAO ambariConfigurationDAO = createMock(AmbariConfigurationDAO.class);
        expect(ambariConfigurationDAO.removeByCategory(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName())).andReturn(1).once();
        expect(ambariConfigurationDAO.removeByCategory("invalid category")).andReturn(0).once();
        AmbariEventPublisher publisher = createMock(AmbariEventPublisher.class);
        publisher.publish(anyObject(AmbariConfigurationChangedEvent.class));
        expectLastCall().once();
        AmbariServerConfigurationHandler handler = new AmbariServerConfigurationHandler(ambariConfigurationDAO, publisher);
        replayAll();
        handler.removeComponentConfiguration(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName());
        handler.removeComponentConfiguration("invalid category");
        verifyAll();
    }

    @Test
    public void updateComponentCategory() throws AmbariException {
        Map<String, String> properties = new HashMap<>();
        properties.put(AmbariServerConfigurationKey.SSO_ENABLED_SERVICES.key(), "SERVICE1");
        properties.put(AmbariServerConfigurationKey.SSO_MANAGE_SERVICES.key(), "true");
        AmbariConfigurationDAO ambariConfigurationDAO = createMock(AmbariConfigurationDAO.class);
        expect(ambariConfigurationDAO.reconcileCategory(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName(), properties, true)).andReturn(true).once();
        expect(ambariConfigurationDAO.reconcileCategory(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName(), properties, false)).andReturn(true).once();
        AmbariEventPublisher publisher = createMock(AmbariEventPublisher.class);
        publisher.publish(anyObject(AmbariConfigurationChangedEvent.class));
        expectLastCall().times(2);
        AmbariServerConfigurationHandler handler = new AmbariServerConfigurationHandler(ambariConfigurationDAO, publisher);
        replayAll();
        handler.updateComponentCategory(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName(), properties, false);
        handler.updateComponentCategory(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName(), properties, true);
        try {
            handler.updateComponentCategory("invalid category", properties, true);
            Assert.fail("Expecting IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            // This is expected
        }
        verifyAll();
    }

    @Test
    public void getConfigurations() {
        List<AmbariConfigurationEntity> ssoEntities = new ArrayList<>();
        ssoEntities.add(createEntity(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.SSO_MANAGE_SERVICES.key(), "true"));
        ssoEntities.add(createEntity(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.SSO_ENABLED_SERVICES.key(), "AMBARI,SERVICE1"));
        List<AmbariConfigurationEntity> allEntities = new ArrayList(ssoEntities);
        allEntities.add(createEntity(AmbariServerConfigurationCategory.LDAP_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.LDAP_ENABLED.key(), "true"));
        allEntities.add(createEntity(AmbariServerConfigurationCategory.LDAP_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.SERVER_HOST.key(), "host1"));
        AmbariConfigurationDAO ambariConfigurationDAO = createMock(AmbariConfigurationDAO.class);
        expect(ambariConfigurationDAO.findAll()).andReturn(allEntities).once();
        AmbariEventPublisher publisher = createMock(AmbariEventPublisher.class);
        AmbariServerConfigurationHandler handler = new AmbariServerConfigurationHandler(ambariConfigurationDAO, publisher);
        replayAll();
        Map<String, Map<String, String>> allConfigurations = handler.getConfigurations();
        Assert.assertEquals(2, allConfigurations.size());
        Assert.assertTrue(allConfigurations.containsKey(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName()));
        Assert.assertTrue(allConfigurations.containsKey(AmbariServerConfigurationCategory.LDAP_CONFIGURATION.getCategoryName()));
        verifyAll();
    }

    @Test
    public void getConfigurationProperties() {
        List<AmbariConfigurationEntity> ssoEntities = new ArrayList<>();
        ssoEntities.add(createEntity(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.SSO_MANAGE_SERVICES.key(), "true"));
        ssoEntities.add(createEntity(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.SSO_ENABLED_SERVICES.key(), "AMBARI,SERVICE1"));
        List<AmbariConfigurationEntity> allEntities = new ArrayList(ssoEntities);
        allEntities.add(createEntity(AmbariServerConfigurationCategory.LDAP_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.LDAP_ENABLED.key(), "true"));
        allEntities.add(createEntity(AmbariServerConfigurationCategory.LDAP_CONFIGURATION.getCategoryName(), AmbariServerConfigurationKey.SERVER_HOST.key(), "host1"));
        AmbariConfigurationDAO ambariConfigurationDAO = createMock(AmbariConfigurationDAO.class);
        expect(ambariConfigurationDAO.findByCategory(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName())).andReturn(ssoEntities).once();
        expect(ambariConfigurationDAO.findByCategory("invalid category")).andReturn(null).once();
        AmbariEventPublisher publisher = createMock(AmbariEventPublisher.class);
        AmbariServerConfigurationHandler handler = new AmbariServerConfigurationHandler(ambariConfigurationDAO, publisher);
        replayAll();
        Map<String, String> ssoConfigurations = handler.getConfigurationProperties(AmbariServerConfigurationCategory.SSO_CONFIGURATION.getCategoryName());
        Assert.assertEquals(2, ssoConfigurations.size());
        Assert.assertTrue(ssoConfigurations.containsKey(AmbariServerConfigurationKey.SSO_ENABLED_SERVICES.key()));
        Assert.assertTrue(ssoConfigurations.containsKey(AmbariServerConfigurationKey.SSO_MANAGE_SERVICES.key()));
        Map<String, String> invalidConfigurations = handler.getConfigurationProperties("invalid category");
        Assert.assertNull(invalidConfigurations);
        verifyAll();
    }
}

