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


import java.util.List;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.TestingAuthenticationToken;


/**
 * Tests {@link UnanimousBased}.
 *
 * @author Ben Alex
 */
public class UnanimousBasedTests {
    @Test
    public void testOneAffirmativeVoteOneDenyVoteOneAbstainVoteDeniesAccess() throws Exception {
        TestingAuthenticationToken auth = makeTestToken();
        UnanimousBased mgr = makeDecisionManager();
        List<ConfigAttribute> config = SecurityConfig.createList(new String[]{ "ROLE_1", "DENY_FOR_SURE" });
        try {
            mgr.decide(auth, new Object(), config);
            fail("Should have thrown AccessDeniedException");
        } catch (AccessDeniedException expected) {
        }
    }

    @Test
    public void testOneAffirmativeVoteTwoAbstainVotesGrantsAccess() throws Exception {
        TestingAuthenticationToken auth = makeTestToken();
        UnanimousBased mgr = makeDecisionManager();
        List<ConfigAttribute> config = SecurityConfig.createList("ROLE_2");
        mgr.decide(auth, new Object(), config);
    }

    @Test
    public void testOneDenyVoteTwoAbstainVotesDeniesAccess() throws Exception {
        TestingAuthenticationToken auth = makeTestToken();
        UnanimousBased mgr = makeDecisionManager();
        List<ConfigAttribute> config = SecurityConfig.createList("ROLE_WE_DO_NOT_HAVE");
        try {
            mgr.decide(auth, new Object(), config);
            fail("Should have thrown AccessDeniedException");
        } catch (AccessDeniedException expected) {
        }
    }

    @Test
    public void testRoleVoterPrefixObserved() throws Exception {
        TestingAuthenticationToken auth = makeTestTokenWithFooBarPrefix();
        UnanimousBased mgr = makeDecisionManagerWithFooBarPrefix();
        List<ConfigAttribute> config = SecurityConfig.createList(new String[]{ "FOOBAR_1", "FOOBAR_2" });
        mgr.decide(auth, new Object(), config);
    }

    @Test
    public void testThreeAbstainVotesDeniesAccessWithDefault() throws Exception {
        TestingAuthenticationToken auth = makeTestToken();
        UnanimousBased mgr = makeDecisionManager();
        assertThat((!(mgr.isAllowIfAllAbstainDecisions()))).isTrue();// check default

        List<ConfigAttribute> config = SecurityConfig.createList("IGNORED_BY_ALL");
        try {
            mgr.decide(auth, new Object(), config);
            fail("Should have thrown AccessDeniedException");
        } catch (AccessDeniedException expected) {
        }
    }

    @Test
    public void testThreeAbstainVotesGrantsAccessWithoutDefault() throws Exception {
        TestingAuthenticationToken auth = makeTestToken();
        UnanimousBased mgr = makeDecisionManager();
        mgr.setAllowIfAllAbstainDecisions(true);
        assertThat(mgr.isAllowIfAllAbstainDecisions()).isTrue();// check changed

        List<ConfigAttribute> config = SecurityConfig.createList("IGNORED_BY_ALL");
        mgr.decide(auth, new Object(), config);
    }

    @Test
    public void testTwoAffirmativeVotesTwoAbstainVotesGrantsAccess() throws Exception {
        TestingAuthenticationToken auth = makeTestToken();
        UnanimousBased mgr = makeDecisionManager();
        List<ConfigAttribute> config = SecurityConfig.createList(new String[]{ "ROLE_1", "ROLE_2" });
        mgr.decide(auth, new Object(), config);
    }
}

