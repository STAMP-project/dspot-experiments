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
package org.springframework.security.access.expression.method;


import AccessDecisionVoter.ACCESS_DENIED;
import AccessDecisionVoter.ACCESS_GRANTED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.util.SimpleMethodInvocation;


@SuppressWarnings("unchecked")
public class MethodExpressionVoterTests {
    private TestingAuthenticationToken joe = new TestingAuthenticationToken("joe", "joespass", "ROLE_blah");

    private PreInvocationAuthorizationAdviceVoter am = new PreInvocationAuthorizationAdviceVoter(new ExpressionBasedPreInvocationAdvice());

    @Test
    public void hasRoleExpressionAllowsUserWithRole() throws Exception {
        MethodInvocation mi = new SimpleMethodInvocation(new MethodExpressionVoterTests.TargetImpl(), methodTakingAnArray());
        assertThat(am.vote(joe, mi, createAttributes(new PreInvocationExpressionAttribute(null, null, "hasRole('blah')")))).isEqualTo(ACCESS_GRANTED);
    }

    @Test
    public void hasRoleExpressionDeniesUserWithoutRole() throws Exception {
        List<ConfigAttribute> cad = new ArrayList<>(1);
        cad.add(new PreInvocationExpressionAttribute(null, null, "hasRole('joedoesnt')"));
        MethodInvocation mi = new SimpleMethodInvocation(new MethodExpressionVoterTests.TargetImpl(), methodTakingAnArray());
        assertThat(am.vote(joe, mi, cad)).isEqualTo(ACCESS_DENIED);
    }

    @Test
    public void matchingArgAgainstAuthenticationNameIsSuccessful() throws Exception {
        MethodInvocation mi = new SimpleMethodInvocation(new MethodExpressionVoterTests.TargetImpl(), methodTakingAString(), "joe");
        assertThat(am.vote(joe, mi, createAttributes(new PreInvocationExpressionAttribute(null, null, "(#argument == principal) and (principal == 'joe')")))).isEqualTo(ACCESS_GRANTED);
    }

    @Test
    public void accessIsGrantedIfNoPreAuthorizeAttributeIsUsed() throws Exception {
        Collection arg = createCollectionArg("joe", "bob", "sam");
        MethodInvocation mi = new SimpleMethodInvocation(new MethodExpressionVoterTests.TargetImpl(), methodTakingACollection(), arg);
        assertThat(am.vote(joe, mi, createAttributes(new PreInvocationExpressionAttribute("(filterObject == 'jim')", "collection", null)))).isEqualTo(ACCESS_GRANTED);
        // All objects should have been removed, because the expression is always false
        assertThat(arg).isEmpty();
    }

    @Test
    public void collectionPreFilteringIsSuccessful() throws Exception {
        List arg = createCollectionArg("joe", "bob", "sam");
        MethodInvocation mi = new SimpleMethodInvocation(new MethodExpressionVoterTests.TargetImpl(), methodTakingACollection(), arg);
        am.vote(joe, mi, createAttributes(new PreInvocationExpressionAttribute("(filterObject == 'joe' or filterObject == 'sam')", "collection", "permitAll")));
        assertThat(arg).containsExactly("joe", "sam");
    }

    @Test(expected = IllegalArgumentException.class)
    public void arraysCannotBePrefiltered() throws Exception {
        MethodInvocation mi = new SimpleMethodInvocation(new MethodExpressionVoterTests.TargetImpl(), methodTakingAnArray(), createArrayArg("sam", "joe"));
        am.vote(joe, mi, createAttributes(new PreInvocationExpressionAttribute("(filterObject == 'jim')", "someArray", null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void incorrectFilterTargetNameIsRejected() throws Exception {
        MethodInvocation mi = new SimpleMethodInvocation(new MethodExpressionVoterTests.TargetImpl(), methodTakingACollection(), createCollectionArg("joe", "bob"));
        am.vote(joe, mi, createAttributes(new PreInvocationExpressionAttribute("(filterObject == 'joe')", "collcetion", null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullNamedFilterTargetIsRejected() throws Exception {
        MethodInvocation mi = new SimpleMethodInvocation(new MethodExpressionVoterTests.TargetImpl(), methodTakingACollection(), new Object[]{ null });
        am.vote(joe, mi, createAttributes(new PreInvocationExpressionAttribute("(filterObject == 'joe')", "collection", null)));
    }

    @Test
    public void ruleDefinedInAClassMethodIsApplied() throws Exception {
        MethodInvocation mi = new SimpleMethodInvocation(new MethodExpressionVoterTests.TargetImpl(), methodTakingAString(), "joe");
        assertThat(am.vote(joe, mi, createAttributes(new PreInvocationExpressionAttribute(null, null, "T(org.springframework.security.access.expression.method.SecurityRules).isJoe(#argument)")))).isEqualTo(ACCESS_GRANTED);
    }

    // ~ Inner Classes
    // ==================================================================================================
    private interface Target {
        void methodTakingAnArray(Object[] args);

        void methodTakingAString(String argument);

        Collection methodTakingACollection(Collection collection);
    }

    private static class TargetImpl implements MethodExpressionVoterTests.Target {
        public void methodTakingAnArray(Object[] args) {
        }

        public void methodTakingAString(String argument) {
        }

        public Collection methodTakingACollection(Collection collection) {
            return collection;
        }
    }
}

