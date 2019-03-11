/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.core.authority.mapping;


import org.junit.Test;


/**
 *
 *
 * @author TSARDD
 * @since 18-okt-2007
 */
public class SimpleRoles2GrantedAuthoritiesMapperTests {
    @Test
    public final void testAfterPropertiesSetConvertToUpperAndLowerCase() {
        SimpleAttributes2GrantedAuthoritiesMapper mapper = new SimpleAttributes2GrantedAuthoritiesMapper();
        mapper.setConvertAttributeToLowerCase(true);
        mapper.setConvertAttributeToUpperCase(true);
        try {
            mapper.afterPropertiesSet();
            fail("Expected exception not thrown");
        } catch (IllegalArgumentException expected) {
        } catch (Exception unexpected) {
            fail(("Unexpected exception: " + unexpected));
        }
    }

    @Test
    public final void testAfterPropertiesSet() {
        SimpleAttributes2GrantedAuthoritiesMapper mapper = new SimpleAttributes2GrantedAuthoritiesMapper();
        try {
            mapper.afterPropertiesSet();
        } catch (Exception unexpected) {
            fail(("Unexpected exception: " + unexpected));
        }
    }

    @Test
    public final void testGetGrantedAuthoritiesNoConversion() {
        String[] roles = new String[]{ "Role1", "Role2" };
        String[] expectedGas = new String[]{ "Role1", "Role2" };
        SimpleAttributes2GrantedAuthoritiesMapper mapper = getDefaultMapper();
        testGetGrantedAuthorities(mapper, roles, expectedGas);
    }

    @Test
    public final void testGetGrantedAuthoritiesToUpperCase() {
        String[] roles = new String[]{ "Role1", "Role2" };
        String[] expectedGas = new String[]{ "ROLE1", "ROLE2" };
        SimpleAttributes2GrantedAuthoritiesMapper mapper = getDefaultMapper();
        mapper.setConvertAttributeToUpperCase(true);
        testGetGrantedAuthorities(mapper, roles, expectedGas);
    }

    @Test
    public final void testGetGrantedAuthoritiesToLowerCase() {
        String[] roles = new String[]{ "Role1", "Role2" };
        String[] expectedGas = new String[]{ "role1", "role2" };
        SimpleAttributes2GrantedAuthoritiesMapper mapper = getDefaultMapper();
        mapper.setConvertAttributeToLowerCase(true);
        testGetGrantedAuthorities(mapper, roles, expectedGas);
    }

    @Test
    public final void testGetGrantedAuthoritiesAddPrefixIfAlreadyExisting() {
        String[] roles = new String[]{ "Role1", "Role2", "ROLE_Role3" };
        String[] expectedGas = new String[]{ "ROLE_Role1", "ROLE_Role2", "ROLE_ROLE_Role3" };
        SimpleAttributes2GrantedAuthoritiesMapper mapper = getDefaultMapper();
        mapper.setAddPrefixIfAlreadyExisting(true);
        mapper.setAttributePrefix("ROLE_");
        testGetGrantedAuthorities(mapper, roles, expectedGas);
    }

    @Test
    public final void testGetGrantedAuthoritiesDontAddPrefixIfAlreadyExisting1() {
        String[] roles = new String[]{ "Role1", "Role2", "ROLE_Role3" };
        String[] expectedGas = new String[]{ "ROLE_Role1", "ROLE_Role2", "ROLE_Role3" };
        SimpleAttributes2GrantedAuthoritiesMapper mapper = getDefaultMapper();
        mapper.setAddPrefixIfAlreadyExisting(false);
        mapper.setAttributePrefix("ROLE_");
        testGetGrantedAuthorities(mapper, roles, expectedGas);
    }

    @Test
    public final void testGetGrantedAuthoritiesDontAddPrefixIfAlreadyExisting2() {
        String[] roles = new String[]{ "Role1", "Role2", "role_Role3" };
        String[] expectedGas = new String[]{ "ROLE_Role1", "ROLE_Role2", "ROLE_role_Role3" };
        SimpleAttributes2GrantedAuthoritiesMapper mapper = getDefaultMapper();
        mapper.setAddPrefixIfAlreadyExisting(false);
        mapper.setAttributePrefix("ROLE_");
        testGetGrantedAuthorities(mapper, roles, expectedGas);
    }

    @Test
    public final void testGetGrantedAuthoritiesCombination1() {
        String[] roles = new String[]{ "Role1", "Role2", "role_Role3" };
        String[] expectedGas = new String[]{ "ROLE_ROLE1", "ROLE_ROLE2", "ROLE_ROLE3" };
        SimpleAttributes2GrantedAuthoritiesMapper mapper = getDefaultMapper();
        mapper.setAddPrefixIfAlreadyExisting(false);
        mapper.setConvertAttributeToUpperCase(true);
        mapper.setAttributePrefix("ROLE_");
        testGetGrantedAuthorities(mapper, roles, expectedGas);
    }
}

