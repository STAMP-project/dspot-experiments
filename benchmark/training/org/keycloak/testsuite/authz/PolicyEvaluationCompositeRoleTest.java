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
package org.keycloak.testsuite.authz;


import DecisionEffect.PERMIT;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.PolicyEvaluationRequest;
import org.keycloak.representations.idm.authorization.PolicyEvaluationResponse;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PolicyEvaluationCompositeRoleTest extends AbstractAuthzTest {
    @Test
    public void testCreate() throws Exception {
        testingClient.server().run(PolicyEvaluationCompositeRoleTest::setup);
        RealmResource realm = adminClient.realm(TEST);
        String resourceServerId = realm.clients().findByClientId("myclient").get(0).getId();
        UserRepresentation user = realm.users().search("user").get(0);
        PolicyEvaluationRequest request = new PolicyEvaluationRequest();
        request.setUserId(user.getId());
        request.setClientId(resourceServerId);
        request.addResource("myresource", "myscope");
        PolicyEvaluationResponse result = realm.clients().get(resourceServerId).authorization().policies().evaluate(request);
        Assert.assertEquals(result.getStatus(), PERMIT);
    }
}

