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
package org.keycloak.testsuite.admin;


import javax.ws.rs.core.Response;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.util.Time;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.UserBuilder;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class CrossRealmPermissionsTest extends AbstractKeycloakTest {
    private static final String REALM_NAME = "crossrealm-test";

    private static final String REALM2_NAME = "crossrealm2-test";

    private static Keycloak adminClient1;

    private static Keycloak adminClient2;

    private RealmResource realm1;

    private RealmResource realm2;

    @Test
    public void users() {
        UserRepresentation user = UserBuilder.create().username(("randomuser-" + (Time.currentTimeMillis()))).build();
        Response response = realm1.users().create(user);
        String userId = ApiUtil.getCreatedId(response);
        response.close();
        realm1.users().get(userId).toRepresentation();
        expectNotFound(new PermissionsTest.Invocation() {
            @Override
            public void invoke(RealmResource realm) {
                realm.users().get(userId).toRepresentation();
            }
        }, realm2);
        expectNotFound(new PermissionsTest.Invocation() {
            @Override
            public void invoke(RealmResource realm) {
                realm.users().get(userId).update(new UserRepresentation());
            }
        }, realm2);
        expectNotFound(new PermissionsTest.Invocation() {
            @Override
            public void invoke(RealmResource realm) {
                realm.users().get(userId).remove();
            }
        }, realm2);
        expectNotFound(new PermissionsTest.Invocation() {
            @Override
            public void invoke(RealmResource realm) {
                realm.users().get(userId).getUserSessions();
            }
        }, realm2);
    }
}

