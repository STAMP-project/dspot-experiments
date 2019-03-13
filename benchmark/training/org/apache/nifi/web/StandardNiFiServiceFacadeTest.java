/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.web;


import org.apache.nifi.authorization.AccessDeniedException;
import org.apache.nifi.authorization.AuthorizationRequest;
import org.apache.nifi.authorization.Authorizer;
import org.apache.nifi.authorization.resource.ResourceFactory;
import org.apache.nifi.authorization.user.StandardNiFiUser.Builder;
import org.apache.nifi.web.api.dto.action.HistoryDTO;
import org.apache.nifi.web.api.dto.action.HistoryQueryDTO;
import org.apache.nifi.web.api.entity.ActionEntity;
import org.apache.nifi.web.security.token.NiFiAuthenticationToken;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class StandardNiFiServiceFacadeTest {
    private static final String USER_1 = "user-1";

    private static final String USER_2 = "user-2";

    private static final Integer UNKNOWN_ACTION_ID = 0;

    private static final Integer ACTION_ID_1 = 1;

    private static final String PROCESSOR_ID_1 = "processor-1";

    private static final Integer ACTION_ID_2 = 2;

    private static final String PROCESSOR_ID_2 = "processor-2";

    private StandardNiFiServiceFacade serviceFacade;

    private Authorizer authorizer;

    @Test(expected = ResourceNotFoundException.class)
    public void testGetUnknownAction() throws Exception {
        serviceFacade.getAction(StandardNiFiServiceFacadeTest.UNKNOWN_ACTION_ID);
    }

    @Test
    public void testGetActionApprovedThroughAction() throws Exception {
        // set the user
        final Authentication authentication = new NiFiAuthenticationToken(new org.apache.nifi.authorization.user.NiFiUserDetails(new Builder().identity(StandardNiFiServiceFacadeTest.USER_1).build()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // get the action
        final ActionEntity entity = serviceFacade.getAction(StandardNiFiServiceFacadeTest.ACTION_ID_1);
        // verify
        Assert.assertEquals(StandardNiFiServiceFacadeTest.ACTION_ID_1, entity.getId());
        Assert.assertTrue(entity.getCanRead());
        // resource exists and is approved, no need to check the controller
        Mockito.verify(authorizer, Mockito.times(1)).authorize(ArgumentMatchers.argThat(new ArgumentMatcher<AuthorizationRequest>() {
            @Override
            public boolean matches(Object o) {
                return getResource().getIdentifier().endsWith(StandardNiFiServiceFacadeTest.PROCESSOR_ID_1);
            }
        }));
        Mockito.verify(authorizer, Mockito.times(0)).authorize(ArgumentMatchers.argThat(new ArgumentMatcher<AuthorizationRequest>() {
            @Override
            public boolean matches(Object o) {
                return getResource().equals(ResourceFactory.getControllerResource());
            }
        }));
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetActionDeniedDespiteControllerAccess() throws Exception {
        // set the user
        final Authentication authentication = new NiFiAuthenticationToken(new org.apache.nifi.authorization.user.NiFiUserDetails(new Builder().identity(StandardNiFiServiceFacadeTest.USER_2).build()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            // get the action
            serviceFacade.getAction(StandardNiFiServiceFacadeTest.ACTION_ID_1);
            Assert.fail();
        } finally {
            // resource exists, but should trigger access denied and will not check the controller
            Mockito.verify(authorizer, Mockito.times(1)).authorize(ArgumentMatchers.argThat(new ArgumentMatcher<AuthorizationRequest>() {
                @Override
                public boolean matches(Object o) {
                    return getResource().getIdentifier().endsWith(StandardNiFiServiceFacadeTest.PROCESSOR_ID_1);
                }
            }));
            Mockito.verify(authorizer, Mockito.times(0)).authorize(ArgumentMatchers.argThat(new ArgumentMatcher<AuthorizationRequest>() {
                @Override
                public boolean matches(Object o) {
                    return getResource().equals(ResourceFactory.getControllerResource());
                }
            }));
        }
    }

    @Test
    public void testGetActionApprovedThroughController() throws Exception {
        // set the user
        final Authentication authentication = new NiFiAuthenticationToken(new org.apache.nifi.authorization.user.NiFiUserDetails(new Builder().identity(StandardNiFiServiceFacadeTest.USER_2).build()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // get the action
        final ActionEntity entity = serviceFacade.getAction(StandardNiFiServiceFacadeTest.ACTION_ID_2);
        // verify
        Assert.assertEquals(StandardNiFiServiceFacadeTest.ACTION_ID_2, entity.getId());
        Assert.assertTrue(entity.getCanRead());
        // component does not exists, so only checks against the controller
        Mockito.verify(authorizer, Mockito.times(0)).authorize(ArgumentMatchers.argThat(new ArgumentMatcher<AuthorizationRequest>() {
            @Override
            public boolean matches(Object o) {
                return getResource().getIdentifier().endsWith(StandardNiFiServiceFacadeTest.PROCESSOR_ID_2);
            }
        }));
        Mockito.verify(authorizer, Mockito.times(1)).authorize(ArgumentMatchers.argThat(new ArgumentMatcher<AuthorizationRequest>() {
            @Override
            public boolean matches(Object o) {
                return getResource().equals(ResourceFactory.getControllerResource());
            }
        }));
    }

    @Test
    public void testGetActionsForUser1() throws Exception {
        // set the user
        final Authentication authentication = new NiFiAuthenticationToken(new org.apache.nifi.authorization.user.NiFiUserDetails(new Builder().identity(StandardNiFiServiceFacadeTest.USER_1).build()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final HistoryDTO dto = serviceFacade.getActions(new HistoryQueryDTO());
        // verify user 1 only has access to actions for processor 1
        dto.getActions().forEach(( action) -> {
            if (PROCESSOR_ID_1.equals(action.getSourceId())) {
                assertTrue(action.getCanRead());
            } else
                if (PROCESSOR_ID_2.equals(action.getSourceId())) {
                    assertFalse(action.getCanRead());
                    assertNull(action.getAction());
                }

        });
    }

    @Test
    public void testGetActionsForUser2() throws Exception {
        // set the user
        final Authentication authentication = new NiFiAuthenticationToken(new org.apache.nifi.authorization.user.NiFiUserDetails(new Builder().identity(StandardNiFiServiceFacadeTest.USER_2).build()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final HistoryDTO dto = serviceFacade.getActions(new HistoryQueryDTO());
        // verify user 2 only has access to actions for processor 2
        dto.getActions().forEach(( action) -> {
            if (PROCESSOR_ID_1.equals(action.getSourceId())) {
                assertFalse(action.getCanRead());
                assertNull(action.getAction());
            } else
                if (PROCESSOR_ID_2.equals(action.getSourceId())) {
                    assertTrue(action.getCanRead());
                }

        });
    }
}

