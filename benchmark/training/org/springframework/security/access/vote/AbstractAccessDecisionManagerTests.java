/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
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
package org.springframework.security.access.vote;


import java.util.Collection;
import java.util.List;
import java.util.Vector;
import org.junit.Test;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;


/**
 * Tests {@link AbstractAccessDecisionManager}.
 *
 * @author Ben Alex
 */
@SuppressWarnings("unchecked")
public class AbstractAccessDecisionManagerTests {
    // ~ Methods
    // ========================================================================================================
    @Test
    public void testAllowIfAccessDecisionManagerDefaults() {
        List list = new Vector();
        DenyAgainVoter denyVoter = new DenyAgainVoter();
        list.add(denyVoter);
        AbstractAccessDecisionManagerTests.MockDecisionManagerImpl mock = new AbstractAccessDecisionManagerTests.MockDecisionManagerImpl(list);
        assertThat((!(isAllowIfAllAbstainDecisions()))).isTrue();// default

        setAllowIfAllAbstainDecisions(true);
        assertThat(isAllowIfAllAbstainDecisions()).isTrue();// changed

    }

    @Test
    public void testDelegatesSupportsClassRequests() throws Exception {
        List list = new Vector();
        list.add(new DenyVoter());
        list.add(new AbstractAccessDecisionManagerTests.MockStringOnlyVoter());
        AbstractAccessDecisionManagerTests.MockDecisionManagerImpl mock = new AbstractAccessDecisionManagerTests.MockDecisionManagerImpl(list);
        assertThat(supports(String.class)).isTrue();
        assertThat((!(supports(Integer.class)))).isTrue();
    }

    @Test
    public void testDelegatesSupportsRequests() throws Exception {
        List list = new Vector();
        DenyVoter voter = new DenyVoter();
        DenyAgainVoter denyVoter = new DenyAgainVoter();
        list.add(voter);
        list.add(denyVoter);
        AbstractAccessDecisionManagerTests.MockDecisionManagerImpl mock = new AbstractAccessDecisionManagerTests.MockDecisionManagerImpl(list);
        ConfigAttribute attr = new SecurityConfig("DENY_AGAIN_FOR_SURE");
        assertThat(mock.supports(attr)).isTrue();
        ConfigAttribute badAttr = new SecurityConfig("WE_DONT_SUPPORT_THIS");
        assertThat((!(mock.supports(badAttr)))).isTrue();
    }

    @Test
    public void testProperlyStoresListOfVoters() throws Exception {
        List list = new Vector();
        DenyVoter voter = new DenyVoter();
        DenyAgainVoter denyVoter = new DenyAgainVoter();
        list.add(voter);
        list.add(denyVoter);
        AbstractAccessDecisionManagerTests.MockDecisionManagerImpl mock = new AbstractAccessDecisionManagerTests.MockDecisionManagerImpl(list);
        assertThat(getDecisionVoters()).hasSize(list.size());
    }

    @Test
    public void testRejectsEmptyList() throws Exception {
        List list = new Vector();
        try {
            new AbstractAccessDecisionManagerTests.MockDecisionManagerImpl(list);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testRejectsNullVotersList() throws Exception {
        try {
            new AbstractAccessDecisionManagerTests.MockDecisionManagerImpl(null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testRoleVoterAlwaysReturnsTrueToSupports() {
        RoleVoter rv = new RoleVoter();
        assertThat(rv.supports(String.class)).isTrue();
    }

    @Test
    public void testWillNotStartIfDecisionVotersNotSet() throws Exception {
        try {
            new AbstractAccessDecisionManagerTests.MockDecisionManagerImpl(null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    // ~ Inner Classes
    // ==================================================================================================
    private class MockDecisionManagerImpl extends AbstractAccessDecisionManager {
        protected MockDecisionManagerImpl(List<AccessDecisionVoter<? extends Object>> decisionVoters) {
            super(decisionVoters);
        }

        public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) {
        }
    }

    private class MockStringOnlyVoter implements AccessDecisionVoter<Object> {
        public boolean supports(Class<?> clazz) {
            return String.class.isAssignableFrom(clazz);
        }

        public boolean supports(ConfigAttribute attribute) {
            throw new UnsupportedOperationException("mock method not implemented");
        }

        public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
            throw new UnsupportedOperationException("mock method not implemented");
        }
    }
}

