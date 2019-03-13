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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;


/**
 * Tests {@link AffirmativeBased}.
 *
 * @author Ben Alex
 */
public class AffirmativeBasedTests {
    private final List<ConfigAttribute> attrs = new ArrayList<>();

    private final Authentication user = new TestingAuthenticationToken("somebody", "password", "ROLE_1", "ROLE_2");

    private AffirmativeBased mgr;

    private AccessDecisionVoter grant;

    private AccessDecisionVoter abstain;

    private AccessDecisionVoter deny;

    @Test
    public void oneAffirmativeVoteOneDenyVoteOneAbstainVoteGrantsAccess() throws Exception {
        mgr = new AffirmativeBased(Arrays.<AccessDecisionVoter<? extends Object>>asList(grant, deny, abstain));
        mgr.afterPropertiesSet();
        mgr.decide(user, new Object(), attrs);
    }

    @Test
    public void oneDenyVoteOneAbstainVoteOneAffirmativeVoteGrantsAccess() throws Exception {
        mgr = new AffirmativeBased(Arrays.<AccessDecisionVoter<? extends Object>>asList(deny, abstain, grant));
        mgr.decide(user, new Object(), attrs);
    }

    @Test
    public void oneAffirmativeVoteTwoAbstainVotesGrantsAccess() throws Exception {
        mgr = new AffirmativeBased(Arrays.<AccessDecisionVoter<? extends Object>>asList(grant, abstain, abstain));
        mgr.decide(user, new Object(), attrs);
    }

    @Test(expected = AccessDeniedException.class)
    public void oneDenyVoteTwoAbstainVotesDeniesAccess() throws Exception {
        mgr = new AffirmativeBased(Arrays.<AccessDecisionVoter<? extends Object>>asList(deny, abstain, abstain));
        mgr.decide(user, new Object(), attrs);
    }

    @Test(expected = AccessDeniedException.class)
    public void onlyAbstainVotesDeniesAccessWithDefault() throws Exception {
        mgr = new AffirmativeBased(Arrays.<AccessDecisionVoter<? extends Object>>asList(abstain, abstain, abstain));
        assertThat((!(mgr.isAllowIfAllAbstainDecisions()))).isTrue();// check default

        mgr.decide(user, new Object(), attrs);
    }

    @Test
    public void testThreeAbstainVotesGrantsAccessIfAllowIfAllAbstainDecisionsIsSet() throws Exception {
        mgr = new AffirmativeBased(Arrays.<AccessDecisionVoter<? extends Object>>asList(abstain, abstain, abstain));
        mgr.setAllowIfAllAbstainDecisions(true);
        assertThat(mgr.isAllowIfAllAbstainDecisions()).isTrue();// check changed

        mgr.decide(user, new Object(), attrs);
    }
}

