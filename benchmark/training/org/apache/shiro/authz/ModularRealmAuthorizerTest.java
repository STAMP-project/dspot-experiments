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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.authz;


import java.util.ArrayList;
import java.util.Collection;
import junit.framework.Assert;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class ModularRealmAuthorizerTest {
    @Test
    public void testSettingOfRolePermissionResolver() {
        Collection<Realm> realms = new ArrayList<Realm>();
        realms.add(new ModularRealmAuthorizerTest.MockAuthorizingRealm());
        realms.add(new ModularRealmAuthorizerTest.MockAuthorizingRealm());
        // its null to start with
        for (Realm realm : realms) {
            Assert.assertNull(getRolePermissionResolver());
        }
        ModularRealmAuthorizer modRealmAuthz = new ModularRealmAuthorizer();
        modRealmAuthz.setRealms(realms);
        // make sure they are still null
        for (Realm realm : realms) {
            Assert.assertNull(getRolePermissionResolver());
        }
        // now set the RolePermissionResolver
        RolePermissionResolver rolePermissionResolver = new RolePermissionResolver() {
            public Collection<Permission> resolvePermissionsInRole(String roleString) {
                return null;
            }
        };
        modRealmAuthz.setRolePermissionResolver(rolePermissionResolver);
        // make sure they are set
        for (Realm realm : realms) {
            // check for same instance
            Assert.assertTrue(((getRolePermissionResolver()) == rolePermissionResolver));
        }
        // add a new realm and make sure the RolePermissionResolver is set
        ModularRealmAuthorizerTest.MockAuthorizingRealm mockRealm = new ModularRealmAuthorizerTest.MockAuthorizingRealm();
        realms.add(mockRealm);
        modRealmAuthz.setRealms(realms);
        assertTrue(((getRolePermissionResolver()) == rolePermissionResolver));
        // TODO: no way to unset them, not sure if that is a valid use case, but this is conistent with the PermissionResolver logic
        // // now just to be sure, unset them
        // modRealmAuthz.setRolePermissionResolver( null );
        // for ( Realm realm : realms )
        // {
        // Assert.assertNull( ((AuthorizingRealm)realm).getRolePermissionResolver() );
        // }
    }

    class MockAuthorizingRealm extends AuthorizingRealm {
        @Override
        protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
            return null;
        }

        @Override
        protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
            return null;
        }
    }
}

