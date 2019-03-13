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
package org.apache.ambari.server.controller.internal;


import AlertTargetResourceProvider.ALERT_TARGET_ENABLED;
import AlertTargetResourceProvider.ALERT_TARGET_GLOBAL;
import AlertTargetResourceProvider.ALERT_TARGET_GROUPS;
import AlertTargetResourceProvider.ALERT_TARGET_ID;
import TargetType.EMAIL;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.metadata.ActionMetadata;
import org.apache.ambari.server.orm.dao.AlertDispatchDAO;
import org.apache.ambari.server.orm.entities.AlertGroupEntity;
import org.apache.ambari.server.orm.entities.AlertTargetEntity;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * {@link AlertTargetResourceProvider} tests.
 */
public class AlertTargetResourceProviderTest {
    private static final Long ALERT_TARGET_ID = Long.valueOf(28);

    private static final String ALERT_TARGET_NAME = "The Administrators";

    private static final String ALERT_TARGET_DESC = "Admins and Others";

    private static final String ALERT_TARGET_TYPE = EMAIL.name();

    private static final String ALERT_TARGET_PROPS = "{\"foo\":\"bar\",\"foobar\":\"baz\"}";

    private static final String ALERT_TARGET_PROPS2 = "{\"foobar\":\"baz\"}";

    private AlertDispatchDAO m_dao;

    private Injector m_injector;

    private AmbariManagementController m_amc;

    @Test
    public void testGetResourcesNoPredicateAsAdministrator() throws Exception {
        testGetResourcesNoPredicate(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetResourcesNoPredicateAsClusterAdministrator() throws Exception {
        testGetResourcesNoPredicate(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetResourcesNoPredicateAsServiceAdministrator() throws Exception {
        testGetResourcesNoPredicate(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesNoPredicateAsClusterUser() throws Exception {
        testGetResourcesNoPredicate(TestAuthenticationFactory.createClusterUser());
    }

    @Test
    public void testGetResourcesNoPredicateAsViewUser() throws Exception {
        testGetResourcesNoPredicate(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testGetSingleResourceAsAdministrator() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetSingleResourceAsClusterAdministrator() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetSingleResourceAsServiceAdministrator() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetSingleResourceAsClusterUser() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createClusterUser());
    }

    @Test
    public void testGetSingleResourceAsViewUser() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testCreateResourcesAsAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testCreateResourcesAsClusterAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsServiceAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsClusterUser() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsViewUser() throws Exception {
        testCreateResources(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testCreateResourcesWithGroupsAsAdministrator() throws Exception {
        testCreateResourcesWithGroups(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testCreateResourcesWithGroupsAsClusterAdministrator() throws Exception {
        testCreateResourcesWithGroups(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesWithGroupsAsServiceAdministrator() throws Exception {
        testCreateResourcesWithGroups(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesWithGroupsAsClusterUser() throws Exception {
        testCreateResourcesWithGroups(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesWithGroupsAsViewUser() throws Exception {
        testCreateResourcesWithGroups(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testCreateGlobalTargetAsAdministrator() throws Exception {
        testCreateGlobalTarget(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testCreateGlobalTargetAsClusterAdministrator() throws Exception {
        testCreateGlobalTarget(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateGlobalTargetAsServiceAdministrator() throws Exception {
        testCreateGlobalTarget(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateGlobalTargetAsClusterUser() throws Exception {
        testCreateGlobalTarget(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateGlobalTargetAsViewUser() throws Exception {
        testCreateGlobalTarget(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testCreateResourceWithRecipientArrayAsAdministrator() throws Exception {
        testCreateResourceWithRecipientArray(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testCreateResourceWithRecipientArrayAsClusterAdministrator() throws Exception {
        testCreateResourceWithRecipientArray(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourceWithRecipientArrayAsServiceAdministrator() throws Exception {
        testCreateResourceWithRecipientArray(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourceWithRecipientArrayAsClusterUser() throws Exception {
        testCreateResourceWithRecipientArray(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesWithRecipientArrayAsViewUser() throws Exception {
        testCreateResourceWithRecipientArray(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testCreateResourceWithAlertStatesAsAdministrator() throws Exception {
        testCreateResourceWithAlertStates(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testCreateResourceWithAlertStatesAsClusterAdministrator() throws Exception {
        testCreateResourceWithAlertStates(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourceWithAlertStatesAsServiceAdministrator() throws Exception {
        testCreateResourceWithAlertStates(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourceWithAlertStatesAsClusterUser() throws Exception {
        testCreateResourceWithAlertStates(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourceWithAlertStatesAsViewUser() throws Exception {
        testCreateResourceWithAlertStates(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testUpdateResourcesAsAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testUpdateResourcesAsClusterAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesAsServiceAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesAsClusterUser() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesAsViewUser() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testUpdateResourcesWithGroupsAsAdministrator() throws Exception {
        testUpdateResourcesWithGroups(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testUpdateResourcesWithGroupsAsClusterAdministrator() throws Exception {
        testUpdateResourcesWithGroups(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesWithGroupsAsServiceAdministrator() throws Exception {
        testUpdateResourcesWithGroups(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesWithGroupsAsClusterUser() throws Exception {
        testUpdateResourcesWithGroups(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesWithGroupsAsViewUser() throws Exception {
        testUpdateResourcesWithGroups(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testDeleteResourcesAsAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testDeleteResourcesAsClusterAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsServiceAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsClusterUser() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsViewUser() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testOverwriteDirectiveAsAdministrator() throws Exception {
        testOverwriteDirective(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testOverwriteDirectiveAsClusterAdministrator() throws Exception {
        testOverwriteDirective(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testOverwriteDirectiveAsServiceAdministrator() throws Exception {
        testOverwriteDirective(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testOverwriteDirectiveAsClusterUser() throws Exception {
        testOverwriteDirective(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testOverwriteDirectiveAsViewUser() throws Exception {
        testOverwriteDirective(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testUpdateAlertTargetsWithCustomGroups() throws Exception {
        Capture<AlertTargetEntity> entityCapture = EasyMock.newCapture();
        m_dao.create(capture(entityCapture));
        expectLastCall().times(1);
        AlertTargetEntity target = new AlertTargetEntity();
        expect(m_dao.findTargetById(AlertTargetResourceProviderTest.ALERT_TARGET_ID)).andReturn(target).once();
        Capture<AlertGroupEntity> groupEntityCapture = EasyMock.newCapture();
        // All Groups in the Database with CLuster ID = 1L
        List<AlertGroupEntity> groups = getMockGroupEntities();
        // List of group ids to be selected in Custom option
        List<Long> groupIds = Arrays.asList(1L, 2L, 3L);
        expect(m_dao.findGroupsById(EasyMock.eq(groupIds))).andReturn(groups).anyTimes();
        expect(m_dao.findAllGroups()).andReturn(groups).anyTimes();
        for (AlertGroupEntity group : groups) {
            expect(m_dao.merge(capture(groupEntityCapture))).andReturn(group).anyTimes();
        }
        expect(m_dao.merge(capture(entityCapture))).andReturn(target).anyTimes();
        // start execution with the above Expectation setters
        replay(m_amc, m_dao);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        AlertTargetResourceProvider provider = createProvider(m_amc);
        Map<String, Object> requestProps = getCreationProperties();
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        provider.createResources(request);
        requestProps = new HashMap<>();
        requestProps.put(AlertTargetResourceProvider.ALERT_TARGET_ID, AlertTargetResourceProviderTest.ALERT_TARGET_ID.toString());
        // selecting CUSTOM option for Groups, 2 group ids selected from the available options
        requestProps.put(ALERT_TARGET_GLOBAL, "false");
        requestProps.put(ALERT_TARGET_GROUPS, groupIds);
        Predicate predicate = new PredicateBuilder().property(AlertTargetResourceProvider.ALERT_TARGET_ID).equals(AlertTargetResourceProviderTest.ALERT_TARGET_ID.toString()).toPredicate();
        request = PropertyHelper.getUpdateRequest(requestProps, null);
        provider.updateResources(request, predicate);
        Assert.assertTrue(entityCapture.hasCaptured());
        AlertTargetEntity entity = entityCapture.getValue();
        // verify that only the selected 2 groups were mapped with the target
        Assert.assertEquals(3, entity.getAlertGroups().size());
        // target must not be global for CUSTOM option
        Assert.assertEquals(false, entity.isGlobal());
        verify(m_amc, m_dao);
    }

    @Test
    public void testUpdateAlertTargetsWithAllGroups() throws Exception {
        Capture<AlertTargetEntity> entityCapture = EasyMock.newCapture();
        m_dao.create(capture(entityCapture));
        expectLastCall().times(1);
        AlertTargetEntity target = new AlertTargetEntity();
        expect(m_dao.findTargetById(AlertTargetResourceProviderTest.ALERT_TARGET_ID)).andReturn(target).once();
        // All Groups in the Database with CLuster ID = 1L
        List<AlertGroupEntity> groups = getMockGroupEntities();
        // Groups to be selected for ALL option
        List<Long> groupIds = Arrays.asList(1L, 2L, 3L);
        expect(m_dao.findGroupsById(EasyMock.eq(groupIds))).andReturn(groups).anyTimes();
        expect(m_dao.findAllGroups()).andReturn(groups).once();
        expect(m_dao.merge(capture(entityCapture))).andReturn(target).anyTimes();
        // start execution with these Expectation setters
        replay(m_amc, m_dao);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        AlertTargetResourceProvider provider = createProvider(m_amc);
        Map<String, Object> requestProps = getCreationProperties();
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        provider.createResources(request);
        requestProps = new HashMap<>();
        requestProps.put(AlertTargetResourceProvider.ALERT_TARGET_ID, AlertTargetResourceProviderTest.ALERT_TARGET_ID.toString());
        // selecting ALL option for Groups
        requestProps.put(ALERT_TARGET_GLOBAL, "true");
        Predicate predicate = new PredicateBuilder().property(AlertTargetResourceProvider.ALERT_TARGET_ID).equals(AlertTargetResourceProviderTest.ALERT_TARGET_ID.toString()).toPredicate();
        request = PropertyHelper.getUpdateRequest(requestProps, null);
        provider.updateResources(request, predicate);
        Assert.assertTrue(entityCapture.hasCaptured());
        AlertTargetEntity entity = entityCapture.getValue();
        // verify that all the groups got mapped with target
        Assert.assertEquals(3, entity.getAlertGroups().size());
        // Target must be global for ALL option
        Assert.assertEquals(true, entity.isGlobal());
        verify(m_amc, m_dao);
    }

    @Test
    public void testEnable() throws Exception {
        Capture<AlertTargetEntity> entityCapture = EasyMock.newCapture();
        m_dao.create(capture(entityCapture));
        expectLastCall().times(1);
        AlertTargetEntity target = new AlertTargetEntity();
        target.setEnabled(false);
        target.setProperties("{prop1=val1}");
        expect(m_dao.findTargetById(AlertTargetResourceProviderTest.ALERT_TARGET_ID)).andReturn(target).times(1);
        expect(m_dao.merge(capture(entityCapture))).andReturn(target).once();
        replay(m_amc, m_dao);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        AlertTargetResourceProvider provider = createProvider(m_amc);
        Map<String, Object> requestProps = getCreationProperties();
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        provider.createResources(request);
        // create new properties, and include the ID since we're not going through
        // a service layer which would add it for us automatically
        requestProps = new HashMap<>();
        requestProps.put(AlertTargetResourceProvider.ALERT_TARGET_ID, AlertTargetResourceProviderTest.ALERT_TARGET_ID.toString());
        requestProps.put(ALERT_TARGET_ENABLED, "true");
        Predicate predicate = new PredicateBuilder().property(AlertTargetResourceProvider.ALERT_TARGET_ID).equals(AlertTargetResourceProviderTest.ALERT_TARGET_ID.toString()).toPredicate();
        request = PropertyHelper.getUpdateRequest(requestProps, null);
        provider.updateResources(request, predicate);
        Assert.assertTrue(entityCapture.hasCaptured());
        AlertTargetEntity entity = entityCapture.getValue();
        Assert.assertTrue("{prop1=val1}".equals(entity.getProperties()));
        Assert.assertTrue(entity.isEnabled());
        verify(m_amc, m_dao);
    }

    /**
     *
     */
    private class MockModule implements Module {
        /**
         *
         */
        @Override
        public void configure(Binder binder) {
            binder.bind(AlertDispatchDAO.class).toInstance(m_dao);
            binder.bind(Clusters.class).toInstance(EasyMock.createNiceMock(Clusters.class));
            binder.bind(Cluster.class).toInstance(EasyMock.createNiceMock(Cluster.class));
            binder.bind(ActionMetadata.class);
        }
    }
}

