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
package org.flowable.ui.modeler.application;


import DefaultPrivileges.ACCESS_ADMIN;
import DefaultPrivileges.ACCESS_MODELER;
import DefaultPrivileges.ACCESS_REST_API;
import HttpHeaders.AUTHORIZATION;
import HttpHeaders.COOKIE;
import HttpHeaders.LOCATION;
import HttpMethod.GET;
import HttpStatus.FORBIDDEN;
import HttpStatus.FOUND;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import SpringBootTest.WebEnvironment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.flowable.ui.common.model.RemoteToken;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.service.idm.RemoteIdmService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
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
public class FlowableModelerApplicationSecurityTest {
    private static final Set<String> ACTUATOR_LINKS = new HashSet<>(Arrays.asList("self", "auditevents", "beans", "health", "conditions", "configprops", "env", "env-toMatch", "info", "loggers-name", "loggers", "heapdump", "threaddump", "metrics", "metrics-requiredMetricName", "scheduledtasks", "httptrace", "mappings", "caches", "caches-cache", "health-component", "health-component-instance"));

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RemoteIdmService remoteIdmService;

    private Map<String, RemoteToken> tokens = new HashMap<>();

    private Map<String, RemoteUser> users = new HashMap<>();

    @Test
    public void nonAuthenticatedUserShouldBeRedirectedToIdm() {
        String stencilsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/app/rest/stencil-sets/editor";
        ResponseEntity<Object> result = restTemplate.getForEntity(stencilsUrl, Object.class);
        assertThat(result.getStatusCode()).as("GET editor stencil-sets").isEqualTo(FOUND);
        assertThat(result.getHeaders().getFirst(LOCATION)).as("redirect location").isEqualTo(("http://localhost:8080/flowable-idm/#/login?redirectOnAuthSuccess=true&redirectUrl=" + stencilsUrl));
    }

    @Test
    public void nonAdminUserShouldBeRedirectedToIdm() {
        String stencilsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/app/rest/stencil-sets/editor";
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("user", "test-user-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(stencilsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET editor stencil-sets").isEqualTo(FOUND);
        assertThat(result.getHeaders().getFirst(LOCATION)).as("redirect location").isEqualTo(("http://localhost:8080/flowable-idm/#/login?redirectOnAuthSuccess=true&redirectUrl=" + stencilsUrl));
    }

    @Test
    public void adminUserShouldBeRedirectedToIdm() {
        String stencilsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/app/rest/stencil-sets/editor";
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("admin", "test-admin-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(stencilsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET editor stencil-sets").isEqualTo(FOUND);
        assertThat(result.getHeaders().getFirst(LOCATION)).as("redirect location").isEqualTo(("http://localhost:8080/flowable-idm/#/login?redirectOnAuthSuccess=true&redirectUrl=" + stencilsUrl));
    }

    @Test
    public void restUserShouldBeRedirectedToIdm() {
        String stencilsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/app/rest/stencil-sets/editor";
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("rest", "test-rest-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(stencilsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET editor stencil-sets").isEqualTo(FOUND);
        assertThat(result.getHeaders().getFirst(LOCATION)).as("redirect location").isEqualTo(("http://localhost:8080/flowable-idm/#/login?redirectOnAuthSuccess=true&redirectUrl=" + stencilsUrl));
    }

    @Test
    public void modelerUserShouldBeAbleToAccessStencilSets() {
        String stencilsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/app/rest/stencil-sets/editor";
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("modeler", "test-modeler-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(stencilsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET editor stencil-sets").isEqualTo(OK);
    }

    @Test
    public void nonAuthenticatedUserShouldNotBeAbleToAccessActuator() {
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/actuator";
        ResponseEntity<Object> entity = restTemplate.getForEntity(actuatorUrl, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(UNAUTHORIZED);
        Map<String, FlowableModelerApplicationSecurityTest.TestLink> links = getEndpointLinks();
        for (Map.Entry<String, FlowableModelerApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableModelerApplicationSecurityTest.TestLink link = entry.getValue();
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
        RemoteUser user = users.get("test-user");
        assertThat(user).as("test-user").isNotNull();
        assertThat(user.getPrivileges()).as("test-user privileges").doesNotContain(ACCESS_ADMIN);
        RemoteToken token = tokens.get("user");
        assertThat(token).as("test-user token").isNotNull();
        assertThat(token.getUserId()).as("test-user token user id").isEqualTo("test-user");
        assertThat(token.getId()).as("test-user token id").isEqualTo("user");
        assertThat(token.getValue()).as("test-user token value").isEqualTo("test-user-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableModelerApplicationSecurityTest.authorization("test-user", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(FORBIDDEN);
        Set<String> allowedEndpoints = new HashSet<>();
        allowedEndpoints.add("info");
        allowedEndpoints.add("health");
        Map<String, FlowableModelerApplicationSecurityTest.TestLink> links = getEndpointLinks();
        assertThat(links.keySet()).as("Endpoints").containsAll(allowedEndpoints);
        for (Map.Entry<String, FlowableModelerApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableModelerApplicationSecurityTest.TestLink link = entry.getValue();
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
        RemoteUser user = users.get("test-user");
        assertThat(user).as("test-user").isNotNull();
        assertThat(user.getPrivileges()).as("test-user privileges").doesNotContain(ACCESS_ADMIN);
        RemoteToken token = tokens.get("user");
        assertThat(token).as("test-user token").isNotNull();
        assertThat(token.getUserId()).as("test-user token user id").isEqualTo("test-user");
        assertThat(token.getId()).as("test-user token id").isEqualTo("user");
        assertThat(token.getValue()).as("test-user token value").isEqualTo("test-user-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("user", "test-user-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(UNAUTHORIZED);
        Map<String, FlowableModelerApplicationSecurityTest.TestLink> links = getEndpointLinks();
        for (Map.Entry<String, FlowableModelerApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableModelerApplicationSecurityTest.TestLink link = entry.getValue();
            if (link.isTemplated()) {
                // Templated links are ignored
                continue;
            }
            ResponseEntity<Object> endpointResponse = restTemplate.exchange(link.getHref(), GET, request, Object.class);
            assertThat(endpointResponse.getStatusCode()).as((("Endpoint '" + endpoint) + "' response status")).isEqualTo(UNAUTHORIZED);
        }
    }

    @Test
    public void modelerUserShouldNotBeAbleToAccessActuator() {
        RemoteUser user = users.get("test-modeler");
        assertThat(user).as("test-modeler").isNotNull();
        assertThat(user.getPrivileges()).as("test-modeler privileges").contains(ACCESS_MODELER).doesNotContain(ACCESS_ADMIN);
        RemoteToken token = tokens.get("modeler");
        assertThat(token).as("test-modeler token").isNotNull();
        assertThat(token.getUserId()).as("test-modeler token user id").isEqualTo("test-modeler");
        assertThat(token.getId()).as("test-modeler token id").isEqualTo("modeler");
        assertThat(token.getValue()).as("test-modeler token value").isEqualTo("test-modeler-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableModelerApplicationSecurityTest.authorization("test-modeler", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(FORBIDDEN);
        Set<String> allowedEndpoints = new HashSet<>();
        allowedEndpoints.add("info");
        allowedEndpoints.add("health");
        Map<String, FlowableModelerApplicationSecurityTest.TestLink> links = getEndpointLinks();
        assertThat(links.keySet()).as("Endpoints").containsAll(allowedEndpoints);
        for (Map.Entry<String, FlowableModelerApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableModelerApplicationSecurityTest.TestLink link = entry.getValue();
            if (link.isTemplated()) {
                // Templated links are ignored
                continue;
            }
            ResponseEntity<Object> endpointResponse = restTemplate.exchange(link.getHref(), GET, request, Object.class);
            assertThat(endpointResponse.getStatusCode()).as((("Endpoint '" + endpoint) + "' response status")).isEqualTo((allowedEndpoints.contains(endpoint) ? HttpStatus.OK : HttpStatus.FORBIDDEN));
        }
    }

    @Test
    public void modelerUserWithCookieShouldNotBeAbleToAccessActuator() {
        RemoteUser user = users.get("test-modeler");
        assertThat(user).as("test-modeler").isNotNull();
        assertThat(user.getPrivileges()).as("test-modeler privileges").contains(ACCESS_MODELER).doesNotContain(ACCESS_ADMIN);
        RemoteToken token = tokens.get("modeler");
        assertThat(token).as("test-modeler token").isNotNull();
        assertThat(token.getUserId()).as("test-modeler token user id").isEqualTo("test-modeler");
        assertThat(token.getId()).as("test-modeler token id").isEqualTo("modeler");
        assertThat(token.getValue()).as("test-modeler token value").isEqualTo("test-modeler-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("modeler", "test-modeler-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(UNAUTHORIZED);
        Map<String, FlowableModelerApplicationSecurityTest.TestLink> links = getEndpointLinks();
        for (Map.Entry<String, FlowableModelerApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableModelerApplicationSecurityTest.TestLink link = entry.getValue();
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
        RemoteUser user = users.get("test-rest");
        assertThat(user).as("test-rest").isNotNull();
        assertThat(user.getPrivileges()).as("test-rest privileges").contains(ACCESS_REST_API).doesNotContain(ACCESS_ADMIN, ACCESS_MODELER);
        RemoteToken token = tokens.get("rest");
        assertThat(token).as("test-rest token").isNotNull();
        assertThat(token.getUserId()).as("test-rest token user id").isEqualTo("test-rest");
        assertThat(token.getId()).as("test-rest token id").isEqualTo("rest");
        assertThat(token.getValue()).as("test-rest token value").isEqualTo("test-rest-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableModelerApplicationSecurityTest.authorization("test-rest", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(FORBIDDEN);
        Set<String> allowedEndpoints = new HashSet<>();
        allowedEndpoints.add("info");
        allowedEndpoints.add("health");
        Map<String, FlowableModelerApplicationSecurityTest.TestLink> links = getEndpointLinks();
        assertThat(links.keySet()).as("Endpoints").containsAll(allowedEndpoints);
        for (Map.Entry<String, FlowableModelerApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableModelerApplicationSecurityTest.TestLink link = entry.getValue();
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
        RemoteUser user = users.get("test-rest");
        assertThat(user).as("test-rest").isNotNull();
        assertThat(user.getPrivileges()).as("test-rest privileges").contains(ACCESS_REST_API).doesNotContain(ACCESS_ADMIN, ACCESS_MODELER);
        RemoteToken token = tokens.get("rest");
        assertThat(token).as("test-rest token").isNotNull();
        assertThat(token.getUserId()).as("test-rest token user id").isEqualTo("test-rest");
        assertThat(token.getId()).as("test-rest token id").isEqualTo("rest");
        assertThat(token.getValue()).as("test-rest token value").isEqualTo("test-rest-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("rest", "test-rest-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(UNAUTHORIZED);
        Map<String, FlowableModelerApplicationSecurityTest.TestLink> links = getEndpointLinks();
        for (Map.Entry<String, FlowableModelerApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableModelerApplicationSecurityTest.TestLink link = entry.getValue();
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
        RemoteUser user = users.get("test-admin");
        assertThat(user).as("test-admin").isNotNull();
        assertThat(user.getPrivileges()).as("test-admin privileges").contains(ACCESS_ADMIN);
        RemoteToken token = tokens.get("admin");
        assertThat(token).as("test-admin token").isNotNull();
        assertThat(token.getUserId()).as("test-admin token user id").isEqualTo("test-admin");
        assertThat(token.getId()).as("test-admin token id").isEqualTo("admin");
        assertThat(token.getValue()).as("test-admin token value").isEqualTo("test-admin-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableModelerApplicationSecurityTest.authorization("test-admin", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(OK);
        Map<String, FlowableModelerApplicationSecurityTest.TestLink> links = getEndpointLinks();
        Set<String> ignoredEndpoints = new HashSet<>();
        ignoredEndpoints.add("heapdump");
        ignoredEndpoints.add("threaddump");
        for (Map.Entry<String, FlowableModelerApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableModelerApplicationSecurityTest.TestLink link = entry.getValue();
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
        RemoteUser user = users.get("test-admin");
        assertThat(user).as("test-admin").isNotNull();
        assertThat(user.getPrivileges()).as("test-admin privileges").contains(ACCESS_ADMIN);
        RemoteToken token = tokens.get("admin");
        assertThat(token).as("test-admin token").isNotNull();
        assertThat(token.getUserId()).as("test-admin token user id").isEqualTo("test-admin");
        assertThat(token.getId()).as("test-admin token id").isEqualTo("admin");
        assertThat(token.getValue()).as("test-admin token value").isEqualTo("test-admin-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("admin", "test-admin-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String actuatorUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/actuator";
        ResponseEntity<Object> entity = restTemplate.exchange(actuatorUrl, GET, request, Object.class);
        assertThat(entity.getStatusCode()).as("GET Actuator response status").isEqualTo(UNAUTHORIZED);
        Map<String, FlowableModelerApplicationSecurityTest.TestLink> links = getEndpointLinks();
        Set<String> ignoredEndpoints = new HashSet<>();
        ignoredEndpoints.add("heapdump");
        ignoredEndpoints.add("threaddump");
        for (Map.Entry<String, FlowableModelerApplicationSecurityTest.TestLink> entry : links.entrySet()) {
            String endpoint = entry.getKey();
            FlowableModelerApplicationSecurityTest.TestLink link = entry.getValue();
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
        String apiModelsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/api/editor/models";
        ResponseEntity<Object> result = restTemplate.getForEntity(apiModelsUrl, Object.class);
        assertThat(result.getStatusCode()).as("GET API Editor models").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void nonAuthorizedShouldNotBeAbleToAccessApi() {
        RemoteUser user = users.get("test-user");
        assertThat(user).as("test-user").isNotNull();
        assertThat(user.getPrivileges()).as("test-user privileges").doesNotContain(ACCESS_ADMIN, ACCESS_REST_API);
        RemoteToken token = tokens.get("user");
        assertThat(token).as("test-user token").isNotNull();
        assertThat(token.getUserId()).as("test-user token user id").isEqualTo("test-user");
        assertThat(token.getId()).as("test-user token id").isEqualTo("user");
        assertThat(token.getValue()).as("test-user token value").isEqualTo("test-user-value");
        String apiModelsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/api/editor/models";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableModelerApplicationSecurityTest.authorization("test-user", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(apiModelsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET API Editor models").isEqualTo(FORBIDDEN);
    }

    @Test
    public void nonAuthorizedUserWithCookieShouldNotBeAbleToAccessApi() {
        RemoteUser user = users.get("test-user");
        assertThat(user).as("test-user").isNotNull();
        assertThat(user.getPrivileges()).as("test-user privileges").doesNotContain(ACCESS_ADMIN, ACCESS_REST_API);
        RemoteToken token = tokens.get("user");
        assertThat(token).as("test-user token").isNotNull();
        assertThat(token.getUserId()).as("test-user token user id").isEqualTo("test-user");
        assertThat(token.getId()).as("test-user token id").isEqualTo("user");
        assertThat(token.getValue()).as("test-user token value").isEqualTo("test-user-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("user", "test-user-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String apiModelsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/api/editor/models";
        ResponseEntity<Object> result = restTemplate.exchange(apiModelsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET API Editor models").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void modelerUserShouldNotBeAbleToAccessApi() {
        RemoteUser user = users.get("test-modeler");
        assertThat(user).as("test-modeler").isNotNull();
        assertThat(user.getPrivileges()).as("test-modeler privileges").contains(ACCESS_MODELER).doesNotContain(ACCESS_REST_API);
        RemoteToken token = tokens.get("modeler");
        assertThat(token).as("test-modeler token").isNotNull();
        assertThat(token.getUserId()).as("test-modeler token modeler id").isEqualTo("test-modeler");
        assertThat(token.getId()).as("test-modeler token id").isEqualTo("modeler");
        assertThat(token.getValue()).as("test-modeler token value").isEqualTo("test-modeler-value");
        String apiModelsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/api/editor/models";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableModelerApplicationSecurityTest.authorization("test-modeler", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(apiModelsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET API Editor models").isEqualTo(FORBIDDEN);
    }

    @Test
    public void modelerUserWithCookieShouldNotBeAbleToAccessApi() {
        RemoteUser user = users.get("test-modeler");
        assertThat(user).as("test-modeler").isNotNull();
        assertThat(user.getPrivileges()).as("test-modeler privileges").contains(ACCESS_MODELER).doesNotContain(ACCESS_REST_API);
        RemoteToken token = tokens.get("modeler");
        assertThat(token).as("test-modeler token").isNotNull();
        assertThat(token.getUserId()).as("test-modeler token modeler id").isEqualTo("test-modeler");
        assertThat(token.getId()).as("test-modeler token id").isEqualTo("modeler");
        assertThat(token.getValue()).as("test-modeler token value").isEqualTo("test-modeler-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("modeler", "test-modeler-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String apiModelsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/api/editor/models";
        ResponseEntity<Object> result = restTemplate.exchange(apiModelsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET API Editor models").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void adminUserShouldNotBeAbleToAccessApi() {
        RemoteUser user = users.get("test-admin");
        assertThat(user).as("test-admin").isNotNull();
        assertThat(user.getPrivileges()).as("test-admin privileges").contains(ACCESS_ADMIN).doesNotContain(ACCESS_REST_API);
        RemoteToken token = tokens.get("admin");
        assertThat(token).as("test-admin token").isNotNull();
        assertThat(token.getUserId()).as("test-admin token admin id").isEqualTo("test-admin");
        assertThat(token.getId()).as("test-admin token id").isEqualTo("admin");
        assertThat(token.getValue()).as("test-admin token value").isEqualTo("test-admin-value");
        String apiModelsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/api/editor/models";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableModelerApplicationSecurityTest.authorization("test-admin", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(apiModelsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET API Editor models").isEqualTo(FORBIDDEN);
    }

    @Test
    public void adminUserWithCookieShouldNotBeAbleToAccessApi() {
        RemoteUser user = users.get("test-admin");
        assertThat(user).as("test-admin").isNotNull();
        assertThat(user.getPrivileges()).as("test-admin privileges").contains(ACCESS_ADMIN).doesNotContain(ACCESS_REST_API);
        RemoteToken token = tokens.get("admin");
        assertThat(token).as("test-admin token").isNotNull();
        assertThat(token.getUserId()).as("test-admin token admin id").isEqualTo("test-admin");
        assertThat(token.getId()).as("test-admin token id").isEqualTo("admin");
        assertThat(token.getValue()).as("test-admin token value").isEqualTo("test-admin-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("admin", "test-admin-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String apiModelsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/api/editor/models";
        ResponseEntity<Object> result = restTemplate.exchange(apiModelsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET API Editor models").isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void restUserShouldNotBeAbleToAccessApi() {
        RemoteUser user = users.get("test-rest");
        assertThat(user).as("test-rest").isNotNull();
        assertThat(user.getPrivileges()).as("test-rest privileges").contains(ACCESS_REST_API);
        RemoteToken token = tokens.get("rest");
        assertThat(token).as("test-rest token").isNotNull();
        assertThat(token.getUserId()).as("test-rest token rest id").isEqualTo("test-rest");
        assertThat(token.getId()).as("test-rest token id").isEqualTo("rest");
        assertThat(token.getValue()).as("test-rest token value").isEqualTo("test-rest-value");
        String apiModelsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/api/editor/models";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, FlowableModelerApplicationSecurityTest.authorization("test-rest", "test"));
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<Object> result = restTemplate.exchange(apiModelsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET API Editor models").isEqualTo(OK);
    }

    @Test
    public void restUserWithCookieShouldNotBeAbleToAccessApi() {
        RemoteUser user = users.get("test-rest");
        assertThat(user).as("test-rest").isNotNull();
        assertThat(user.getPrivileges()).as("test-rest privileges").contains(ACCESS_REST_API);
        RemoteToken token = tokens.get("rest");
        assertThat(token).as("test-rest token").isNotNull();
        assertThat(token.getUserId()).as("test-rest token rest id").isEqualTo("test-rest");
        assertThat(token.getId()).as("test-rest token id").isEqualTo("rest");
        assertThat(token.getValue()).as("test-rest token value").isEqualTo("test-rest-value");
        HttpHeaders headers = new HttpHeaders();
        headers.set(COOKIE, FlowableModelerApplicationSecurityTest.rememberMeCookie("rest", "test-rest-value"));
        HttpEntity<?> request = new HttpEntity(headers);
        String apiModelsUrl = ("http://localhost:" + (serverPort)) + "/flowable-modeler/api/editor/models";
        ResponseEntity<Object> result = restTemplate.exchange(apiModelsUrl, GET, request, Object.class);
        assertThat(result.getStatusCode()).as("GET API Editor models").isEqualTo(UNAUTHORIZED);
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
}

