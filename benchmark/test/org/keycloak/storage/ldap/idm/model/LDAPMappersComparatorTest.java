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
package org.keycloak.storage.ldap.idm.model;


import LDAPConstants.CN;
import LDAPConstants.SAM_ACCOUNT_NAME;
import LDAPConstants.USERNAME_LDAP_ATTRIBUTE;
import java.util.List;
import org.junit.Test;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.storage.ldap.LDAPConfig;
import org.keycloak.storage.ldap.mappers.LDAPMappersComparator;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class LDAPMappersComparatorTest {
    @Test
    public void testCompareWithCNUsername() {
        MultivaluedHashMap<String, String> cfg = new MultivaluedHashMap();
        cfg.add(USERNAME_LDAP_ATTRIBUTE, CN);
        LDAPConfig config = new LDAPConfig(cfg);
        List<ComponentModel> sorted = LDAPMappersComparator.sortAsc(config, getMappers());
        assertOrder(sorted, "username-cn", "sAMAccountName", "first name", "full name");
        sorted = LDAPMappersComparator.sortDesc(config, getMappers());
        assertOrder(sorted, "full name", "first name", "sAMAccountName", "username-cn");
    }

    @Test
    public void testCompareWithSAMAccountNameUsername() {
        MultivaluedHashMap<String, String> cfg = new MultivaluedHashMap();
        cfg.add(USERNAME_LDAP_ATTRIBUTE, SAM_ACCOUNT_NAME);
        LDAPConfig config = new LDAPConfig(cfg);
        List<ComponentModel> sorted = LDAPMappersComparator.sortAsc(config, getMappers());
        assertOrder(sorted, "sAMAccountName", "username-cn", "first name", "full name");
        sorted = LDAPMappersComparator.sortDesc(config, getMappers());
        assertOrder(sorted, "full name", "first name", "username-cn", "sAMAccountName");
    }
}

