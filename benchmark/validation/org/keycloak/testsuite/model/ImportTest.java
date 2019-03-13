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
package org.keycloak.testsuite.model;


import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.models.RealmModel;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ImportTest extends AbstractTestRealmKeycloakTest {
    @Test
    public void demoDelete() throws Exception {
        // was having trouble deleting this realm from admin console
        removeRealm("demo-delete");
    }

    @Test
    public void install2() throws Exception {
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("demo");
            Assert.assertEquals(600, realm.getAccessCodeLifespanUserAction());
            Assert.assertEquals(Constants.DEFAULT_ACCESS_TOKEN_LIFESPAN_FOR_IMPLICIT_FLOW_TIMEOUT, realm.getAccessTokenLifespanForImplicitFlow());
            Assert.assertEquals(Constants.DEFAULT_OFFLINE_SESSION_IDLE_TIMEOUT, realm.getOfflineSessionIdleTimeout());
            Assert.assertEquals(1, realm.getRequiredCredentials().size());
            Assert.assertEquals("password", realm.getRequiredCredentials().get(0).getType());
        });
    }
}

