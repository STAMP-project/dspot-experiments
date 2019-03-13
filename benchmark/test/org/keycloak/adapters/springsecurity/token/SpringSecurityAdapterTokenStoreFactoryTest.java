/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.adapters.springsecurity.token;


import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.spi.AdapterSessionStore;
import org.mockito.Mock;


/**
 * Spring Security adapter token store factory tests.
 */
public class SpringSecurityAdapterTokenStoreFactoryTest {
    private AdapterTokenStoreFactory factory = new SpringSecurityAdapterTokenStoreFactory();

    @Mock
    private KeycloakDeployment deployment;

    @Mock
    private HttpServletRequest request;

    @Test
    public void testCreateAdapterTokenStore() throws Exception {
        AdapterSessionStore store = factory.createAdapterTokenStore(deployment, request);
        Assert.assertNotNull(store);
        Assert.assertTrue((store instanceof SpringSecurityTokenStore));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAdapterTokenStoreNullDeployment() throws Exception {
        factory.createAdapterTokenStore(null, request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAdapterTokenStoreNullRequest() throws Exception {
        factory.createAdapterTokenStore(deployment, null);
    }
}

