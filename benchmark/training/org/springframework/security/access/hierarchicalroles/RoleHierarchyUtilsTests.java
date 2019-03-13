/**
 * Copyright 2012-2016 the original author or authors.
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
package org.springframework.security.access.hierarchicalroles;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;


/**
 * Tests for {@link RoleHierarchyUtils}.
 *
 * @author Joe Grandja
 */
public class RoleHierarchyUtilsTests {
    private static final String EOL = System.lineSeparator();

    @Test
    public void roleHierarchyFromMapWhenMapValidThenConvertsCorrectly() throws Exception {
        // @formatter:off
        String expectedRoleHierarchy = (((((("ROLE_A > ROLE_B" + (RoleHierarchyUtilsTests.EOL)) + "ROLE_A > ROLE_C") + (RoleHierarchyUtilsTests.EOL)) + "ROLE_B > ROLE_D") + (RoleHierarchyUtilsTests.EOL)) + "ROLE_C > ROLE_D") + (RoleHierarchyUtilsTests.EOL);
        // @formatter:on
        Map<String, List<String>> roleHierarchyMap = new TreeMap<String, List<String>>();
        roleHierarchyMap.put("ROLE_A", Arrays.asList("ROLE_B", "ROLE_C"));
        roleHierarchyMap.put("ROLE_B", Arrays.asList("ROLE_D"));
        roleHierarchyMap.put("ROLE_C", Arrays.asList("ROLE_D"));
        String roleHierarchy = RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap);
        assertThat(roleHierarchy).isEqualTo(expectedRoleHierarchy);
    }

    @Test(expected = IllegalArgumentException.class)
    public void roleHierarchyFromMapWhenMapNullThenThrowsIllegalArgumentException() throws Exception {
        RoleHierarchyUtils.roleHierarchyFromMap(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void roleHierarchyFromMapWhenMapEmptyThenThrowsIllegalArgumentException() throws Exception {
        RoleHierarchyUtils.roleHierarchyFromMap(Collections.<String, List<String>>emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void roleHierarchyFromMapWhenRoleNullThenThrowsIllegalArgumentException() throws Exception {
        Map<String, List<String>> roleHierarchyMap = new HashMap<String, List<String>>();
        roleHierarchyMap.put(null, Arrays.asList("ROLE_B", "ROLE_C"));
        RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void roleHierarchyFromMapWhenRoleEmptyThenThrowsIllegalArgumentException() throws Exception {
        Map<String, List<String>> roleHierarchyMap = new HashMap<String, List<String>>();
        roleHierarchyMap.put("", Arrays.asList("ROLE_B", "ROLE_C"));
        RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void roleHierarchyFromMapWhenImpliedRolesNullThenThrowsIllegalArgumentException() throws Exception {
        Map<String, List<String>> roleHierarchyMap = new HashMap<String, List<String>>();
        roleHierarchyMap.put("ROLE_A", null);
        RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void roleHierarchyFromMapWhenImpliedRolesEmptyThenThrowsIllegalArgumentException() throws Exception {
        Map<String, List<String>> roleHierarchyMap = new HashMap<String, List<String>>();
        roleHierarchyMap.put("ROLE_A", Collections.<String>emptyList());
        RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap);
    }
}

