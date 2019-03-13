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


import java.util.HashMap;
import org.junit.Test;


/**
 *
 *
 * @author Ruud Senden
 */
@SuppressWarnings("unchecked")
public class MapBasedAttributes2GrantedAuthoritiesMapperTests {
    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetNoMap() throws Exception {
        MapBasedAttributes2GrantedAuthoritiesMapper mapper = new MapBasedAttributes2GrantedAuthoritiesMapper();
        mapper.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetEmptyMap() throws Exception {
        MapBasedAttributes2GrantedAuthoritiesMapper mapper = new MapBasedAttributes2GrantedAuthoritiesMapper();
        mapper.setAttributes2grantedAuthoritiesMap(new HashMap());
        mapper.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetInvalidKeyTypeMap() throws Exception {
        MapBasedAttributes2GrantedAuthoritiesMapper mapper = new MapBasedAttributes2GrantedAuthoritiesMapper();
        HashMap m = new HashMap();
        m.put(new Object(), "ga1");
        mapper.setAttributes2grantedAuthoritiesMap(m);
        mapper.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetInvalidValueTypeMap1() throws Exception {
        MapBasedAttributes2GrantedAuthoritiesMapper mapper = new MapBasedAttributes2GrantedAuthoritiesMapper();
        HashMap m = new HashMap();
        m.put("role1", new Object());
        mapper.setAttributes2grantedAuthoritiesMap(m);
        mapper.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetInvalidValueTypeMap2() throws Exception {
        MapBasedAttributes2GrantedAuthoritiesMapper mapper = new MapBasedAttributes2GrantedAuthoritiesMapper();
        HashMap m = new HashMap();
        m.put("role1", new Object[]{ new String[]{ "ga1", "ga2" }, new Object() });
        mapper.setAttributes2grantedAuthoritiesMap(m);
        mapper.afterPropertiesSet();
    }

    @Test
    public void testAfterPropertiesSetValidMap() throws Exception {
        MapBasedAttributes2GrantedAuthoritiesMapper mapper = new MapBasedAttributes2GrantedAuthoritiesMapper();
        HashMap m = getValidAttributes2GrantedAuthoritiesMap();
        mapper.setAttributes2grantedAuthoritiesMap(m);
        mapper.afterPropertiesSet();
    }

    @Test
    public void testMapping1() throws Exception {
        String[] roles = new String[]{ "role1" };
        String[] expectedGas = new String[]{ "ga1" };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testMapping2() throws Exception {
        String[] roles = new String[]{ "role2" };
        String[] expectedGas = new String[]{ "ga2" };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testMapping3() throws Exception {
        String[] roles = new String[]{ "role3" };
        String[] expectedGas = new String[]{ "ga3", "ga4" };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testMapping4() throws Exception {
        String[] roles = new String[]{ "role4" };
        String[] expectedGas = new String[]{ "ga5", "ga6" };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testMapping5() throws Exception {
        String[] roles = new String[]{ "role5" };
        String[] expectedGas = new String[]{ "ga7", "ga8", "ga9" };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testMapping6() throws Exception {
        String[] roles = new String[]{ "role6" };
        String[] expectedGas = new String[]{ "ga10", "ga11", "ga12" };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testMapping7() throws Exception {
        String[] roles = new String[]{ "role7" };
        String[] expectedGas = new String[]{ "ga13", "ga14" };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testMapping8() throws Exception {
        String[] roles = new String[]{ "role8" };
        String[] expectedGas = new String[]{ "ga13", "ga14" };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testMapping9() throws Exception {
        String[] roles = new String[]{ "role9" };
        String[] expectedGas = new String[]{  };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testMapping10() throws Exception {
        String[] roles = new String[]{ "role10" };
        String[] expectedGas = new String[]{  };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testMapping11() throws Exception {
        String[] roles = new String[]{ "role11" };
        String[] expectedGas = new String[]{  };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testNonExistingMapping() throws Exception {
        String[] roles = new String[]{ "nonExisting" };
        String[] expectedGas = new String[]{  };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }

    @Test
    public void testMappingCombination() throws Exception {
        String[] roles = new String[]{ "role1", "role2", "role3", "role4", "role5", "role6", "role7", "role8", "role9", "role10", "role11" };
        String[] expectedGas = new String[]{ "ga1", "ga2", "ga3", "ga4", "ga5", "ga6", "ga7", "ga8", "ga9", "ga10", "ga11", "ga12", "ga13", "ga14" };
        testGetGrantedAuthorities(getDefaultMapper(), roles, expectedGas);
    }
}

