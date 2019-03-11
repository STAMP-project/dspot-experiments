/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.web.authentication.preauth.j2ee;


import org.junit.Test;


/**
 *
 *
 * @author TSARDD
 */
public class J2eeBasedPreAuthenticatedWebAuthenticationDetailsSourceTests {
    @Test
    public final void testAfterPropertiesSetException() {
        J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource t = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource();
        try {
            t.afterPropertiesSet();
            fail("AfterPropertiesSet didn't throw expected exception");
        } catch (IllegalArgumentException expected) {
        } catch (Exception unexpected) {
            fail("AfterPropertiesSet throws unexpected exception");
        }
    }

    @Test
    public final void testBuildDetailsHttpServletRequestNoMappedNoUserRoles() {
        String[] mappedRoles = new String[]{  };
        String[] roles = new String[]{  };
        String[] expectedRoles = new String[]{  };
        testDetails(mappedRoles, roles, expectedRoles);
    }

    @Test
    public final void testBuildDetailsHttpServletRequestNoMappedUnmappedUserRoles() {
        String[] mappedRoles = new String[]{  };
        String[] roles = new String[]{ "Role1", "Role2" };
        String[] expectedRoles = new String[]{  };
        testDetails(mappedRoles, roles, expectedRoles);
    }

    @Test
    public final void testBuildDetailsHttpServletRequestNoUserRoles() {
        String[] mappedRoles = new String[]{ "Role1", "Role2", "Role3", "Role4" };
        String[] roles = new String[]{  };
        String[] expectedRoles = new String[]{  };
        testDetails(mappedRoles, roles, expectedRoles);
    }

    @Test
    public final void testBuildDetailsHttpServletRequestAllUserRoles() {
        String[] mappedRoles = new String[]{ "Role1", "Role2", "Role3", "Role4" };
        String[] roles = new String[]{ "Role1", "Role2", "Role3", "Role4" };
        String[] expectedRoles = new String[]{ "Role1", "Role2", "Role3", "Role4" };
        testDetails(mappedRoles, roles, expectedRoles);
    }

    @Test
    public final void testBuildDetailsHttpServletRequestUnmappedUserRoles() {
        String[] mappedRoles = new String[]{ "Role1", "Role2", "Role3", "Role4" };
        String[] roles = new String[]{ "Role1", "Role2", "Role3", "Role4", "Role5" };
        String[] expectedRoles = new String[]{ "Role1", "Role2", "Role3", "Role4" };
        testDetails(mappedRoles, roles, expectedRoles);
    }

    @Test
    public final void testBuildDetailsHttpServletRequestPartialUserRoles() {
        String[] mappedRoles = new String[]{ "Role1", "Role2", "Role3", "Role4" };
        String[] roles = new String[]{ "Role2", "Role3" };
        String[] expectedRoles = new String[]{ "Role2", "Role3" };
        testDetails(mappedRoles, roles, expectedRoles);
    }

    @Test
    public final void testBuildDetailsHttpServletRequestPartialAndUnmappedUserRoles() {
        String[] mappedRoles = new String[]{ "Role1", "Role2", "Role3", "Role4" };
        String[] roles = new String[]{ "Role2", "Role3", "Role5" };
        String[] expectedRoles = new String[]{ "Role2", "Role3" };
        testDetails(mappedRoles, roles, expectedRoles);
    }
}

