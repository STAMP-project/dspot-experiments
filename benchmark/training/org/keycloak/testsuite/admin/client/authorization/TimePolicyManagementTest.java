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


import DecisionStrategy.AFFIRMATIVE;
import Logic.POSITIVE;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.TimePoliciesResource;
import org.keycloak.admin.client.resource.TimePolicyResource;
import org.keycloak.representations.idm.authorization.TimePolicyRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class TimePolicyManagementTest extends AbstractPolicyManagementTest {
    @Test
    public void testCreate() {
        AuthorizationResource authorization = getClient().authorization();
        assertCreated(authorization, createRepresentation("Time Policy"));
    }

    @Test
    public void testUpdate() {
        AuthorizationResource authorization = getClient().authorization();
        TimePolicyRepresentation representation = createRepresentation("Update Time Policy");
        assertCreated(authorization, representation);
        representation.setName("changed");
        representation.setDescription("changed");
        representation.setDecisionStrategy(AFFIRMATIVE);
        representation.setLogic(POSITIVE);
        representation.setDayMonth("11");
        representation.setDayMonthEnd("22");
        representation.setMonth("7");
        representation.setMonthEnd("9");
        representation.setYear("2019");
        representation.setYearEnd("2030");
        representation.setHour("15");
        representation.setHourEnd("23");
        representation.setMinute("55");
        representation.setMinuteEnd("58");
        representation.setNotBefore("2019-01-01 00:00:00");
        representation.setNotOnOrAfter("2019-02-03 00:00:00");
        TimePoliciesResource policies = authorization.policies().time();
        TimePolicyResource permission = policies.findById(representation.getId());
        permission.update(representation);
        assertRepresentation(representation, permission);
        representation.setDayMonth(null);
        representation.setDayMonthEnd(null);
        representation.setMonth(null);
        representation.setMonthEnd(null);
        representation.setYear(null);
        representation.setYearEnd(null);
        representation.setHour(null);
        representation.setHourEnd(null);
        representation.setMinute(null);
        representation.setMinuteEnd(null);
        representation.setNotBefore(null);
        representation.setNotOnOrAfter("2019-02-03 00:00:00");
        permission.update(representation);
        assertRepresentation(representation, permission);
        representation.setNotOnOrAfter(null);
        representation.setHour("2");
        permission.update(representation);
        assertRepresentation(representation, permission);
    }

    @Test
    public void testDelete() {
        AuthorizationResource authorization = getClient().authorization();
        TimePolicyRepresentation representation = createRepresentation("Test Delete Policy");
        TimePoliciesResource policies = authorization.policies().time();
        try (Response response = policies.create(representation)) {
            TimePolicyRepresentation created = response.readEntity(TimePolicyRepresentation.class);
            policies.findById(created.getId()).remove();
            TimePolicyResource removed = policies.findById(created.getId());
            try {
                removed.toRepresentation();
                Assert.fail("Permission not removed");
            } catch (NotFoundException ignore) {
            }
        }
    }
}

