/**
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
package org.flowable.ui.idm.application;


import DefaultPrivileges.ACCESS_ADMIN;
import DefaultPrivileges.ACCESS_IDM;
import DefaultPrivileges.ACCESS_REST_API;
import HttpHeaders.AUTHORIZATION;
import HttpHeaders.COOKIE;
import HttpMethod.GET;
import HttpStatus.FORBIDDEN;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import SpringBootTest.WebEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.Privilege;
import org.flowable.idm.api.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author Filip Hrisafov
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient(registerRestTemplate = true)
@Import(FlowableIdmApplicationSecurityTest.TestBootstrapConfiguration.class)
public class FlowableIdmApplicationSecurityTest {
    private static final Set<String> ACTUATOR_LINKS = new HashSet<>(Arrays.asList("self", "auditevents", "beans", "health", "conditions", "configprops", "env", "env-toMatch", "info", "loggers-name", "loggers", "heapdump", "threaddump", "metrics", "metrics-requiredMetricName", "scheduledtasks", "httptrace", "mappings", "caches", "caches-cache", "health-component", "health-component-instance"));

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IdmIdentityService idmIdentityService;

    @Test
    public void nonAuthenticatedUserShouldBeUnauthotized() {
        String authenticateUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/app/rest/admin/groups";
        ResponseEntity<Object> result = restTemplate.getForEntity(authenticateUrl, Object.class);
        assertThat(result.getStatusCode()).as("GET App Groups").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void nonIdmUserShouldBeUnauthotized() {
        String authenticateUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/app/rest/admin/groups";
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("user", "test-user-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(authenticateUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET App Groups").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void adminUserShouldBeUnauthorized() {
        String authenticateUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/app/rest/admin/groups";
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("admin", "test-admin-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(authenticateUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET App Groups").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void restUserShouldBeUnauthorized() {
        String authenticateUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/app/rest/admin/groups";
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("rest", "test-rest-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(authenticateUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET App Groups").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void idmUserShouldBeAbleToAccessInternalRestApp() {
        String authenticateUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/app/rest/admin/groups";
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("idm", "test-idm-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(authenticateUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET App Groups").isEqualTo(OK);
    }

    @Test
    public void nonAuthenticatedUserShouldNotBeAbleToAccessActuator() {
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/actuator";
        ResponseEntity<Object> entity = restTemplate.getForEntity(actuatorUrl, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(UNAUTHORIZED);
        Map<String, FlowableIdmApplicationSecurityTest.TestLink> links = getEndpointLinks();
        for (Map.Entry<String, FlowableIdmApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableIdmApplicationSecurityTest.TestLink link = entry.getValue();
            if (link.isTemplated()) {
                // Templated links are ignored
                continue;
            }
            ResponseEntity<Object> endpointResponse = restTemplate.getForEntity(link.getHref(), Object.class);
            assertThat(endpointResponse.getStatusCode()).as((("Endpoint '" + endpoint) + "' response status")).isEqualTo(UNAUTHORIZED);
        }
    }

    @Test
    public void nonAuthorizedUserShouldNotBeAbleToAccessActuator() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableIdmApplicationSecurityTest.authorization("test-user", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(FORBIDDEN);
        Set<String> allowedEndpoints = new HashSet<>();
        allowedEndpoints.add("info");
        allowedEndpoints.add("health");
        Map<String, FlowableIdmApplicationSecurityTest.TestLink> links = getEndpointLinks();
        assertThat(links.keySet()).as("Endpoints").containsAll(allowedEndpoints);
        for (Map.Entry<String, FlowableIdmApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableIdmApplicationSecurityTest.TestLink link = entry.getValue();
            if (link.isTemplated()) {
                // Templated links are ignored
                continue;
            }
            ResponseEntity<Object> endpointResponse = restTemplate.exchange(link.getHref(), GET, request, Object.class);
            assertThat(endpointResponse.getStatusCode()).as((("Endpoint '" + endpoint) + "' response status")).isEqualTo((allowedEndpoints.contains(endpoint) ? HttpStatus.OK : HttpStatus.FORBIDDEN));
        }
    }

    @Test
    public void nonAuthorizedUserWithCookieShouldNotBeAbleToAccessActuator() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("user", "test-user-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(UNAUTHORIZED);
        Map<String, FlowableIdmApplicationSecurityTest.TestLink> links = getEndpointLinks();
        for (Map.Entry<String, FlowableIdmApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableIdmApplicationSecurityTest.TestLink link = entry.getValue();
            if (link.isTemplated()) {
                // Templated links are ignored
                continue;
            }
            ResponseEntity<Object> endpointResponse = restTemplate.exchange(link.getHref(), GET, request, Object.class);
            assertThat(endpointResponse.getStatusCode()).as((("Endpoint '" + endpoint) + "' response status")).isEqualTo(UNAUTHORIZED);
        }
    }

    @Test
    public void idmUserShouldNotBeAbleToAccessActuator() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableIdmApplicationSecurityTest.authorization("test-idm", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(FORBIDDEN);
        Set<String> allowedEndpoints = new HashSet<>();
        allowedEndpoints.add("info");
        allowedEndpoints.add("health");
        Map<String, FlowableIdmApplicationSecurityTest.TestLink> links = getEndpointLinks();
        assertThat(links.keySet()).as("Endpoints").containsAll(allowedEndpoints);
        for (Map.Entry<String, FlowableIdmApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableIdmApplicationSecurityTest.TestLink link = entry.getValue();
            if (link.isTemplated()) {
                // Templated links are ignored
                continue;
            }
            ResponseEntity<Object> endpointResponse = restTemplate.exchange(link.getHref(), GET, request, Object.class);
            assertThat(endpointResponse.getStatusCode()).as((("Endpoint '" + endpoint) + "' response status")).isEqualTo((allowedEndpoints.contains(endpoint) ? HttpStatus.OK : HttpStatus.FORBIDDEN));
        }
    }

    @Test
    public void idmUserWithCookieShouldNotBeAbleToAccessActuator() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("idm", "test-idm-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(UNAUTHORIZED);
        Map<String, FlowableIdmApplicationSecurityTest.TestLink> links = getEndpointLinks();
        for (Map.Entry<String, FlowableIdmApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableIdmApplicationSecurityTest.TestLink link = entry.getValue();
            if (link.isTemplated()) {
                // Templated links are ignored
                continue;
            }
            ResponseEntity<Object> endpointResponse = restTemplate.exchange(link.getHref(), GET, request, Object.class);
            assertThat(endpointResponse.getStatusCode()).as((("Endpoint '" + endpoint) + "' response status")).isEqualTo(UNAUTHORIZED);
        }
    }

    @Test
    public void restUserShouldNotBeAbleToAccessActuator() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableIdmApplicationSecurityTest.authorization("test-rest", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(FORBIDDEN);
        Set<String> allowedEndpoints = new HashSet<>();
        allowedEndpoints.add("info");
        allowedEndpoints.add("health");
        Map<String, FlowableIdmApplicationSecurityTest.TestLink> links = getEndpointLinks();
        assertThat(links.keySet()).as("Endpoints").containsAll(allowedEndpoints);
        for (Map.Entry<String, FlowableIdmApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableIdmApplicationSecurityTest.TestLink link = entry.getValue();
            if (link.isTemplated()) {
                // Templated links are ignored
                continue;
            }
            ResponseEntity<Object> endpointResponse = restTemplate.exchange(link.getHref(), GET, request, Object.class);
            assertThat(endpointResponse.getStatusCode()).as((("Endpoint '" + endpoint) + "' response status")).isEqualTo((allowedEndpoints.contains(endpoint) ? HttpStatus.OK : HttpStatus.FORBIDDEN));
        }
    }

    @Test
    public void restUserWithCookieShouldNotBeAbleToAccessActuator() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("rest", "test-rest-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(UNAUTHORIZED);
        Map<String, FlowableIdmApplicationSecurityTest.TestLink> links = getEndpointLinks();
        for (Map.Entry<String, FlowableIdmApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableIdmApplicationSecurityTest.TestLink link = entry.getValue();
            if (link.isTemplated()) {
                // Templated links are ignored
                continue;
            }
            ResponseEntity<Object> endpointResponse = restTemplate.exchange(link.getHref(), GET, request, Object.class);
            assertThat(endpointResponse.getStatusCode()).as((("Endpoint '" + endpoint) + "' response status")).isEqualTo(UNAUTHORIZED);
        }
    }

    @Test
    public void authorizedUserShouldBeAbleToAccessActuator() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableIdmApplicationSecurityTest.authorization("test-admin", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(OK);
        Map<String, FlowableIdmApplicationSecurityTest.TestLink> links = getEndpointLinks();
        Set<String> ignoredEndpoints = new HashSet<>();
        ignoredEndpoints.add("heapdump");
        ignoredEndpoints.add("threaddump");
        for (Map.Entry<String, FlowableIdmApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableIdmApplicationSecurityTest.TestLink link = entry.getValue();
            if ((link.isTemplated()) || (ignoredEndpoints.contains(endpoint))) {
                // Templated links are ignored
                continue;
            }
            ResponseEntity<Object> endpointResponse = restTemplate.exchange(link.getHref(), GET, request, Object.class);
            assertThat(endpointResponse.getStatusCode()).as((("Endpoint '" + endpoint) + "' response status")).isEqualTo(OK);
        }
    }

    @Test
    public void authorizedUserWithCookieShouldNotBeAbleToAccessActuator() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("admin", "test-admin-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(UNAUTHORIZED);
        Map<String, FlowableIdmApplicationSecurityTest.TestLink> links = getEndpointLinks();
        Set<String> ignoredEndpoints = new HashSet<>();
        ignoredEndpoints.add("heapdump");
        ignoredEndpoints.add("threaddump");
        for (Map.Entry<String, FlowableIdmApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableIdmApplicationSecurityTest.TestLink link = entry.getValue();
            if ((link.isTemplated()) || (ignoredEndpoints.contains(endpoint))) {
                // Templated links are ignored
                continue;
            }
            ResponseEntity<Object> endpointResponse = restTemplate.exchange(link.getHref(), GET, request, Object.class);
            assertThat(endpointResponse.getStatusCode()).as((("Endpoint '" + endpoint) + "' response status")).isEqualTo(UNAUTHORIZED);
        }
    }

    @Test
    public void nonAuthenticatedShouldNotBeAbleToAccessApi() {
        String processDefinitionsUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/api/idm/users";
        ResponseEntity<Object> result = restTemplate.getForEntity(processDefinitionsUrl, Object.class);
        assertThat(result.getStatusCode()).as("GET IDM Api Users").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void nonAuthorizedShouldNotBeAbleToAccessApi() {
        String processDefinitionsUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/api/idm/users";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableIdmApplicationSecurityTest.authorization("test-user", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(processDefinitionsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET IDM Api Users").isEqualTo(FORBIDDEN);
    }

    @Test
    public void nonAuthorizedUserWithCookieShouldNotBeAbleToAccessApi() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("user", "test-user-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String processDefinitionsUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/api/idm/users";
        ResponseEntity<Object> result = restTemplate.exchange(processDefinitionsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET IDM Api Users").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void idmUserShouldNotBeAbleToAccessApi() {
        String processDefinitionsUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/api/idm/users";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableIdmApplicationSecurityTest.authorization("test-idm", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(processDefinitionsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET IDM Api Users").isEqualTo(FORBIDDEN);
    }

    @Test
    public void idmUserWithCookieShouldNotBeAbleToAccessApi() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("idm", "test-idm-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String processDefinitionsUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/api/idm/users";
        ResponseEntity<Object> result = restTemplate.exchange(processDefinitionsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET IDM Api Users").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void adminUserShouldNotBeAbleToAccessApi() {
        String processDefinitionsUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/api/idm/users";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableIdmApplicationSecurityTest.authorization("test-admin", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(processDefinitionsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET IDM Api Users").isEqualTo(FORBIDDEN);
    }

    @Test
    public void adminUserWithCookieShouldNotBeAbleToAccessApi() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("admin", "test-admin-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String processDefinitionsUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/api/idm/users";
        ResponseEntity<Object> result = restTemplate.exchange(processDefinitionsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET IDM Api Users").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void restUserShouldNotBeAbleToAccessApi() {
        String processDefinitionsUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/api/idm/users?filter={filter}";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableIdmApplicationSecurityTest.authorization("test-rest", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(processDefinitionsUrl, GET, request, Object.class, "test");
        assertThat(result.getStatusCode()).as("GET IDM Api Users").isEqualTo(OK);
    }

    @Test
    public void restUserWithCookieShouldNotBeAbleToAccessApi() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableIdmApplicationSecurityTest.rememberMeCookie("rest", "test-rest-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String processDefinitionsUrl = ("http://localhost:" + (serverPort)) + "/flowable-idm/api/idm/users";
        ResponseEntity<Object> result = restTemplate.exchange(processDefinitionsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET IDM Api Users").isEqualTo(UNAUTHORIZED);
    }

    static class TestLink {
        private String href;

        private boolean templated;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public boolean isTemplated() {
            return templated;
        }

        public void setTemplated(boolean templated) {
            this.templated = templated;
        }
    }

    @TestConfiguration
    static class TestBootstrapConfiguration {
        @Bean
        public CommandLineRunner initTestUsers(IdmIdentityService idmIdentityService) {
            return ( args) -> {
                User testUser = idmIdentityService.createUserQuery().userId("test-user").singleResult();
                if (testUser == null) {
                    createTestUser(idmIdentityService);
                }
            };
        }

        private void createTestUser(IdmIdentityService idmIdentityService) {
            User user = idmIdentityService.newUser("test-user");
            user.setPassword("test");
            idmIdentityService.saveUser(user);
            User idm = idmIdentityService.newUser("test-idm");
            idm.setPassword("test");
            idmIdentityService.saveUser(idm);
            User rest = idmIdentityService.newUser("test-rest");
            rest.setPassword("test");
            idmIdentityService.saveUser(rest);
            User admin = idmIdentityService.newUser("test-admin");
            admin.setPassword("test");
            idmIdentityService.saveUser(admin);
            Privilege adminAccess = idmIdentityService.createPrivilegeQuery().privilegeName(ACCESS_ADMIN).singleResult();
            if (adminAccess == null) {
                adminAccess = idmIdentityService.createPrivilege(ACCESS_ADMIN);
            }
            idmIdentityService.addUserPrivilegeMapping(adminAccess.getId(), "test-admin");
            Privilege idmAccess = idmIdentityService.createPrivilegeQuery().privilegeName(ACCESS_IDM).singleResult();
            if (idmAccess == null) {
                idmAccess = idmIdentityService.createPrivilege(ACCESS_IDM);
            }
            idmIdentityService.addUserPrivilegeMapping(idmAccess.getId(), "test-idm");
            Privilege restAccess = idmIdentityService.createPrivilegeQuery().privilegeName(ACCESS_REST_API).singleResult();
            if (restAccess == null) {
                restAccess = idmIdentityService.createPrivilege(ACCESS_REST_API);
            }
            idmIdentityService.addUserPrivilegeMapping(restAccess.getId(), "test-rest");
        }
    }
}

