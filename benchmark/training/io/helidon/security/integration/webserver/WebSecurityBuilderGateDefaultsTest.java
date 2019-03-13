/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.security.integration.webserver;


import AuditEvent.AuditSeverity.SUCCESS;
import Http.Header.WWW_AUTHENTICATE;
import io.helidon.common.CollectionsHelper;
import io.helidon.security.AuditEvent;
import io.helidon.webserver.WebServer;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link WebSecurity}.
 */
public class WebSecurityBuilderGateDefaultsTest {
    private static UnitTestAuditProvider myAuditProvider;

    private static WebServer server;

    private static Client authFeatureClient;

    private static Client client;

    private static String serverBaseUri;

    @Test
    public void basicTestJohn() {
        String username = "john";
        String password = "password";
        testForbidden(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/noRoles"), username, password);
        testForbidden(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/user"), username, password);
        testForbidden(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/admin"), username, password);
        testForbidden(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/deny"), username, password);
    }

    @Test
    public void basicTestJack() {
        String username = "jack";
        String password = "jackIsGreat";
        testProtected(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/noRoles"), username, password, CollectionsHelper.setOf("user", "admin"), CollectionsHelper.setOf());
        testProtected(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/user"), username, password, CollectionsHelper.setOf("user", "admin"), CollectionsHelper.setOf());
        testProtected(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/admin"), username, password, CollectionsHelper.setOf("user", "admin"), CollectionsHelper.setOf());
        testProtected(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/deny"), username, password, CollectionsHelper.setOf("user", "admin"), CollectionsHelper.setOf());
    }

    @Test
    public void basicTestJill() {
        String username = "jill";
        String password = "password";
        testForbidden(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/noRoles"), username, password);
        testProtected(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/user"), username, password, CollectionsHelper.setOf("user"), CollectionsHelper.setOf("admin"));
        testForbidden(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/admin"), username, password);
        testForbidden(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/deny"), username, password);
    }

    @Test
    public void basicTest401() {
        // here we call the endpoint
        Response response = WebSecurityBuilderGateDefaultsTest.client.target(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/noRoles")).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(401));
        String authHeader = response.getHeaderString(WWW_AUTHENTICATE);
        MatcherAssert.assertThat(authHeader, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.is("basic realm=\"mic\""));
        response = callProtected(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/noRoles"), "invalidUser", "invalidPassword");
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(401));
        authHeader = response.getHeaderString(WWW_AUTHENTICATE);
        MatcherAssert.assertThat(authHeader, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.is("basic realm=\"mic\""));
    }

    @Test
    public void testCustomizedAudit() throws InterruptedException {
        // even though I send username and password, this MUST NOT require authentication
        // as then audit is called twice - first time with 401 (challenge) and second time with 200 (correct request)
        // and that intermittently breaks this test
        Response response = WebSecurityBuilderGateDefaultsTest.client.target(((WebSecurityBuilderGateDefaultsTest.serverBaseUri) + "/auditOnly")).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        // audit
        AuditEvent auditEvent = WebSecurityBuilderGateDefaultsTest.myAuditProvider.getAuditEvent();
        MatcherAssert.assertThat(auditEvent, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(auditEvent.toString(), auditEvent.messageFormat(), CoreMatchers.is(WebSecurityTests.AUDIT_MESSAGE_FORMAT));
        MatcherAssert.assertThat(auditEvent.toString(), auditEvent.severity(), CoreMatchers.is(SUCCESS));
    }
}

