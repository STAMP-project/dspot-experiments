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
package org.keycloak.testsuite.admin.authentication;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;


/**
 *
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class InitialFlowsTest extends AbstractAuthenticationTest {
    private HashMap<String, AuthenticatorConfigRepresentation> configs = new HashMap<>();

    private HashMap<String, AuthenticatorConfigRepresentation> expectedConfigs = new HashMap<>();

    {
        expectedConfigs.put("idp-review-profile", newConfig("review profile config", new String[]{ "update.profile.on.first.login", "missing" }));
        expectedConfigs.put("idp-create-user-if-unique", newConfig("create unique user config", new String[]{ "require.password.update.after.registration", "false" }));
    }

    @Test
    public void testInitialFlows() {
        List<InitialFlowsTest.FlowExecutions> result = new LinkedList<>();
        // get all flows
        List<AuthenticationFlowRepresentation> flows = authMgmtResource.getFlows();
        for (AuthenticationFlowRepresentation flow : flows) {
            // get all executions for flow
            List<AuthenticationExecutionInfoRepresentation> executionReps = authMgmtResource.getExecutions(flow.getAlias());
            for (AuthenticationExecutionInfoRepresentation exec : executionReps) {
                // separately load referenced configurations
                String configId = exec.getAuthenticationConfig();
                if ((configId != null) && (!(configs.containsKey(configId)))) {
                    configs.put(configId, authMgmtResource.getAuthenticatorConfig(configId));
                }
            }
            result.add(new InitialFlowsTest.FlowExecutions(flow, executionReps));
        }
        // make sure received flows and their details are as expected
        compare(expectedFlows(), orderAlphabetically(result));
    }

    private static class FlowExecutions implements Comparable<InitialFlowsTest.FlowExecutions> {
        AuthenticationFlowRepresentation flow;

        List<AuthenticationExecutionInfoRepresentation> executions;

        FlowExecutions(AuthenticationFlowRepresentation flow, List<AuthenticationExecutionInfoRepresentation> executions) {
            this.flow = flow;
            this.executions = executions;
        }

        @Override
        public int compareTo(InitialFlowsTest.FlowExecutions o) {
            return flow.getAlias().compareTo(o.flow.getAlias());
        }
    }
}

