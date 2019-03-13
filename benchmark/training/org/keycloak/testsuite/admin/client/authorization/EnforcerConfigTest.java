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
package org.keycloak.testsuite.admin.client.authorization;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.authorization.PolicyEnforcer;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig.PathConfig;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class EnforcerConfigTest extends AbstractKeycloakTest {
    @Test
    public void testMultiplePathsWithSameName() {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getClass().getResourceAsStream("/authorization-test/enforcer-config-paths-same-name.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        Map<String, PolicyEnforcerConfig.PathConfig> paths = policyEnforcer.getPaths();
        Assert.assertEquals(1, paths.size());
        Assert.assertEquals(4, paths.values().iterator().next().getMethods().size());
    }

    @Test
    public void testPathConfigClaimInformationPoint() {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getClass().getResourceAsStream("/authorization-test/enforcer-config-path-cip.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        Map<String, PolicyEnforcerConfig.PathConfig> paths = policyEnforcer.getPaths();
        Assert.assertEquals(1, paths.size());
        PathConfig pathConfig = paths.values().iterator().next();
        Map<String, Map<String, Object>> cipConfig = pathConfig.getClaimInformationPointConfig();
        Assert.assertEquals(1, cipConfig.size());
        Map<String, Object> claims = cipConfig.get("claims");
        Assert.assertNotNull(claims);
        Assert.assertEquals(3, claims.size());
        Assert.assertEquals("{request.parameter['a']}", claims.get("claim-a"));
        Assert.assertEquals("{request.header['b']}", claims.get("claim-b"));
        Assert.assertEquals("{request.cookie['c']}", claims.get("claim-c"));
    }
}

