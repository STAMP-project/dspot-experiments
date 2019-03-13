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
package org.keycloak.testsuite.admin.group;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.UserRepresentation;


/**
 *
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class GroupMappersTest extends AbstractGroupTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testGroupMappers() throws Exception {
        RealmResource realm = adminClient.realms().realm("test");
        {
            UserRepresentation user = realm.users().search("topGroupUser", (-1), (-1)).get(0);
            AccessToken token = login(user.getUsername(), "test-app", "password", user.getId());
            Assert.assertTrue(token.getRealmAccess().getRoles().contains("user"));
            List<String> groups = ((List<String>) (token.getOtherClaims().get("groups")));
            Assert.assertNotNull(groups);
            Assert.assertTrue(((groups.size()) == 1));
            Assert.assertEquals("topGroup", groups.get(0));
            Assert.assertEquals("true", token.getOtherClaims().get("topAttribute"));
        }
        {
            UserRepresentation user = realm.users().search("level2GroupUser", (-1), (-1)).get(0);
            AccessToken token = login(user.getUsername(), "test-app", "password", user.getId());
            Assert.assertTrue(token.getRealmAccess().getRoles().contains("user"));
            Assert.assertTrue(token.getRealmAccess().getRoles().contains("admin"));
            Assert.assertTrue(token.getResourceAccess("test-app").getRoles().contains("customer-user"));
            List<String> groups = ((List<String>) (token.getOtherClaims().get("groups")));
            Assert.assertNotNull(groups);
            Assert.assertTrue(((groups.size()) == 1));
            Assert.assertEquals("level2group", groups.get(0));
            Assert.assertEquals("true", token.getOtherClaims().get("topAttribute"));
            Assert.assertEquals("true", token.getOtherClaims().get("level2Attribute"));
        }
    }
}

