/**
 * Copyright 2018-2019 the original author or authors.
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
package org.springframework.cloud.config.server.environment;


import HttpMethod.GET;
import HttpStatus.OK;
import VaultEnvironmentRepository.VAULT_NAMESPACE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.VaultKvAccessStrategy.VaultResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


/**
 *
 *
 * @author Spencer Gibb
 * @author Ryan Baxter
 * @author Haroun Pacquee
 * @author Mark Paluch
 */
public class VaultEnvironmentRepositoryTests {
    private ObjectMapper objectMapper;

    @Test
    @SuppressWarnings("unchecked")
    public void testFindOneNoDefaultKey() {
        MockHttpServletRequest configRequest = new MockHttpServletRequest();
        configRequest.addHeader("X-CONFIG-TOKEN", "mytoken");
        RestTemplate rest = Mockito.mock(RestTemplate.class);
        ResponseEntity<VaultResponse> myAppResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(myAppResp.getStatusCode()).thenReturn(OK);
        VaultResponse myAppVaultResp = Mockito.mock(VaultResponse.class);
        Mockito.when(myAppVaultResp.getData()).thenReturn("{\"foo\":\"bar\"}");
        Mockito.when(myAppResp.getBody()).thenReturn(myAppVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/secret/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("myapp"))).thenReturn(myAppResp);
        ResponseEntity<VaultResponse> appResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(appResp.getStatusCode()).thenReturn(OK);
        VaultResponse appVaultResp = Mockito.mock(VaultResponse.class);
        Mockito.when(appVaultResp.getData()).thenReturn("{\"def-foo\":\"def-bar\"}");
        Mockito.when(appResp.getBody()).thenReturn(appVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/secret/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("application"))).thenReturn(appResp);
        VaultEnvironmentRepository repo = new VaultEnvironmentRepository(mockProvide(configRequest), new EnvironmentWatch.Default(), rest, new VaultEnvironmentProperties());
        Environment e = repo.findOne("myapp", null, null);
        assertThat(e.getName()).as("Name should be the same as the application argument").isEqualTo("myapp");
        assertThat(e.getPropertySources().size()).as("Properties for specified application and default application with key 'application' should be returned").isEqualTo(2);
        Map<String, String> firstResult = new HashMap<String, String>();
        firstResult.put("foo", "bar");
        assertThat(e.getPropertySources().get(0).getSource()).as("Properties for specified application should be returned in priority position").isEqualTo(firstResult);
        Map<String, String> secondResult = new HashMap<String, String>();
        secondResult.put("def-foo", "def-bar");
        assertThat(e.getPropertySources().get(1).getSource()).as("Properties for default application with key 'application' should be returned in second position").isEqualTo(secondResult);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBackendWithSlashes() {
        MockHttpServletRequest configRequest = new MockHttpServletRequest();
        configRequest.addHeader("X-CONFIG-TOKEN", "mytoken");
        RestTemplate rest = Mockito.mock(RestTemplate.class);
        ResponseEntity<VaultResponse> myAppResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(myAppResp.getStatusCode()).thenReturn(OK);
        VaultResponse myAppVaultResp = Mockito.mock(VaultResponse.class);
        Mockito.when(myAppVaultResp.getData()).thenReturn("{\"foo\":\"bar\"}");
        Mockito.when(myAppResp.getBody()).thenReturn(myAppVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/foo/bar/secret/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("myapp"))).thenReturn(myAppResp);
        ResponseEntity<VaultResponse> appResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(appResp.getStatusCode()).thenReturn(OK);
        VaultResponse appVaultResp = Mockito.mock(VaultResponse.class);
        Mockito.when(appVaultResp.getData()).thenReturn("{\"def-foo\":\"def-bar\"}");
        Mockito.when(appResp.getBody()).thenReturn(appVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/foo/bar/secret/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("application"))).thenReturn(appResp);
        VaultEnvironmentProperties properties = new VaultEnvironmentProperties();
        properties.setBackend("foo/bar/secret");
        VaultEnvironmentRepository repo = new VaultEnvironmentRepository(mockProvide(configRequest), new EnvironmentWatch.Default(), rest, properties);
        Environment e = repo.findOne("myapp", null, null);
        assertThat(e.getName()).as("Name should be the same as the application argument").isEqualTo("myapp");
        assertThat(e.getPropertySources().size()).as("Properties for specified application and default application with key 'application' should be returned").isEqualTo(2);
        Map<String, String> firstResult = new HashMap<String, String>();
        firstResult.put("foo", "bar");
        assertThat(e.getPropertySources().get(0).getSource()).as("Properties for specified application should be returned in priority position").isEqualTo(firstResult);
        Map<String, String> secondResult = new HashMap<String, String>();
        secondResult.put("def-foo", "def-bar");
        assertThat(e.getPropertySources().get(1).getSource()).as("Properties for default application with key 'application' should be returned in second position").isEqualTo(secondResult);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindOneDefaultKeySetAndDifferentToApplication() {
        MockHttpServletRequest configRequest = new MockHttpServletRequest();
        configRequest.addHeader("X-CONFIG-TOKEN", "mytoken");
        RestTemplate rest = Mockito.mock(RestTemplate.class);
        ResponseEntity<VaultResponse> myAppResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(myAppResp.getStatusCode()).thenReturn(OK);
        VaultResponse myAppVaultResp = Mockito.mock(VaultResponse.class);
        Mockito.when(myAppVaultResp.getData()).thenReturn("{\"foo\":\"bar\"}");
        Mockito.when(myAppResp.getBody()).thenReturn(myAppVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/secret/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("myapp"))).thenReturn(myAppResp);
        ResponseEntity<VaultResponse> myDefaultKeyResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(myDefaultKeyResp.getStatusCode()).thenReturn(OK);
        VaultResponse myDefaultKeyVaultResp = Mockito.mock(VaultResponse.class);
        Mockito.when(myDefaultKeyVaultResp.getData()).thenReturn("{\"def-foo\":\"def-bar\"}");
        Mockito.when(myDefaultKeyResp.getBody()).thenReturn(myDefaultKeyVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/secret/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("mydefaultkey"))).thenReturn(myDefaultKeyResp);
        VaultEnvironmentRepository repo = new VaultEnvironmentRepository(mockProvide(configRequest), new EnvironmentWatch.Default(), rest, new VaultEnvironmentProperties());
        repo.setDefaultKey("mydefaultkey");
        Environment e = repo.findOne("myapp", null, null);
        assertThat(e.getName()).as("Name should be the same as the application argument").isEqualTo("myapp");
        assertThat(e.getPropertySources().size()).as("Properties for specified application and default application with key 'mydefaultkey' should be returned").isEqualTo(2);
        Map<String, String> firstResult = new HashMap<String, String>();
        firstResult.put("foo", "bar");
        assertThat(e.getPropertySources().get(0).getSource()).as("Properties for specified application should be returned in priority position").isEqualTo(firstResult);
        Map<String, String> secondResult = new HashMap<String, String>();
        secondResult.put("def-foo", "def-bar");
        assertThat(e.getPropertySources().get(1).getSource()).as("Properties for default application with key 'mydefaultkey' should be returned in second position").isEqualTo(secondResult);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindOneDefaultKeySetAndEqualToApplication() {
        MockHttpServletRequest configRequest = new MockHttpServletRequest();
        configRequest.addHeader("X-CONFIG-TOKEN", "mytoken");
        RestTemplate rest = Mockito.mock(RestTemplate.class);
        ResponseEntity<VaultResponse> myAppResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(myAppResp.getStatusCode()).thenReturn(OK);
        VaultResponse myAppVaultResp = Mockito.mock(VaultResponse.class);
        Mockito.when(myAppVaultResp.getData()).thenReturn("{\"foo\":\"bar\"}");
        Mockito.when(myAppResp.getBody()).thenReturn(myAppVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/secret/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("myapp"))).thenReturn(myAppResp);
        ResponseEntity<VaultResponse> appResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(appResp.getStatusCode()).thenReturn(OK);
        VaultResponse appVaultResp = Mockito.mock(VaultResponse.class);
        Mockito.when(appVaultResp.getData()).thenReturn("{\"def-foo\":\"def-bar\"}");
        Mockito.when(appResp.getBody()).thenReturn(appVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/secret/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("application"))).thenReturn(appResp);
        VaultEnvironmentRepository repo = new VaultEnvironmentRepository(mockProvide(configRequest), new EnvironmentWatch.Default(), rest, new VaultEnvironmentProperties());
        repo.setDefaultKey("myapp");
        Environment e = repo.findOne("myapp", null, null);
        assertThat(e.getName()).as("Name should be the same as the application argument").isEqualTo("myapp");
        assertThat(e.getPropertySources().size()).as("Only properties for specified application should be returned").isEqualTo(1);
        Map<String, String> result = new HashMap<String, String>();
        result.put("foo", "bar");
        assertThat(e.getPropertySources().get(0).getSource()).as("Properties should be returned for specified application").isEqualTo(result);
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void missingConfigToken() {
        MockHttpServletRequest configRequest = new MockHttpServletRequest();
        RestTemplate rest = Mockito.mock(RestTemplate.class);
        ResponseEntity<VaultResponse> myAppResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(myAppResp.getStatusCode()).thenReturn(OK);
        VaultResponse myAppVaultResp = Mockito.mock(VaultResponse.class);
        Mockito.when(myAppVaultResp.getData()).thenReturn("{\"foo\":\"bar\"}");
        Mockito.when(myAppResp.getBody()).thenReturn(myAppVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/secret/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("myapp"))).thenReturn(myAppResp);
        VaultEnvironmentRepository repo = new VaultEnvironmentRepository(mockProvide(configRequest), new EnvironmentWatch.Default(), rest, new VaultEnvironmentProperties());
        repo.findOne("myapp", null, null);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void missingHttpRequest() {
        ObjectProvider<HttpServletRequest> objectProvider = Mockito.mock(ObjectProvider.class);
        Mockito.when(objectProvider.getIfAvailable()).thenReturn(null);
        RestTemplate rest = Mockito.mock(RestTemplate.class);
        VaultEnvironmentRepository repo = new VaultEnvironmentRepository(objectProvider, new EnvironmentWatch.Default(), rest, new VaultEnvironmentProperties());
        repo.findOne("myapp", null, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testVaultVersioning() {
        MockHttpServletRequest configRequest = new MockHttpServletRequest();
        configRequest.addHeader("X-CONFIG-TOKEN", "mytoken");
        RestTemplate rest = Mockito.mock(RestTemplate.class);
        ResponseEntity<VaultResponse> myAppResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(myAppResp.getStatusCode()).thenReturn(OK);
        VaultResponse myAppVaultResp = getVaultResponse("{\"data\": {\"data\": {\"foo\": \"bar\"}}}");
        Mockito.when(myAppResp.getBody()).thenReturn(myAppVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/secret/data/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("myapp"))).thenReturn(myAppResp);
        ResponseEntity<VaultResponse> appResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(appResp.getStatusCode()).thenReturn(OK);
        VaultResponse appVaultResp = getVaultResponse("{\"data\": {\"data\": {\"def-foo\":\"def-bar\"}}}");
        Mockito.when(appResp.getBody()).thenReturn(appVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/secret/data/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("application"))).thenReturn(appResp);
        final VaultEnvironmentProperties vaultEnvironmentProperties = new VaultEnvironmentProperties();
        vaultEnvironmentProperties.setKvVersion(2);
        VaultEnvironmentRepository repo = new VaultEnvironmentRepository(mockProvide(configRequest), new EnvironmentWatch.Default(), rest, vaultEnvironmentProperties);
        Environment e = repo.findOne("myapp", null, null);
        assertThat(e.getName()).as("Name should be the same as the application argument").isEqualTo("myapp");
        assertThat(e.getPropertySources().size()).as("Properties for specified application and default application with key 'application' should be returned").isEqualTo(2);
        Map<String, String> firstResult = new HashMap<String, String>();
        firstResult.put("foo", "bar");
        assertThat(e.getPropertySources().get(0).getSource()).as("Properties for specified application should be returned in priority position").isEqualTo(firstResult);
    }

    @Test
    @SuppressWarnings({ "Duplicates", "unchecked" })
    public void testNamespaceHeaderSent() {
        MockHttpServletRequest configRequest = new MockHttpServletRequest();
        configRequest.addHeader("X-CONFIG-TOKEN", "mytoken");
        RestTemplate rest = Mockito.mock(RestTemplate.class);
        ResponseEntity<VaultResponse> myAppResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(myAppResp.getStatusCode()).thenReturn(OK);
        VaultResponse myAppVaultResp = Mockito.mock(VaultResponse.class);
        Mockito.when(myAppVaultResp.getData()).thenReturn("{\"foo\":\"bar\"}");
        Mockito.when(myAppResp.getBody()).thenReturn(myAppVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/secret/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("myapp"))).thenReturn(myAppResp);
        ResponseEntity<VaultResponse> appResp = Mockito.mock(ResponseEntity.class);
        Mockito.when(appResp.getStatusCode()).thenReturn(OK);
        VaultResponse appVaultResp = Mockito.mock(VaultResponse.class);
        Mockito.when(appVaultResp.getData()).thenReturn("{\"def-foo\":\"def-bar\"}");
        Mockito.when(appResp.getBody()).thenReturn(appVaultResp);
        Mockito.when(rest.exchange(ArgumentMatchers.eq("http://127.0.0.1:8200/v1/secret/{key}"), ArgumentMatchers.eq(GET), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(VaultResponse.class), ArgumentMatchers.eq("application"))).thenReturn(appResp);
        VaultEnvironmentProperties properties = new VaultEnvironmentProperties();
        properties.setNamespace("mynamespace");
        VaultEnvironmentRepository repo = new VaultEnvironmentRepository(mockProvide(configRequest), new EnvironmentWatch.Default(), rest, properties);
        VaultEnvironmentRepositoryTests.TestAccessStrategy accessStrategy = new VaultEnvironmentRepositoryTests.TestAccessStrategy(rest, properties);
        repo.setAccessStrategy(accessStrategy);
        repo.findOne("myapp", null, null);
        assertThat(accessStrategy.headers).containsEntry(VAULT_NAMESPACE, Collections.singletonList("mynamespace"));
    }

    private static class TestAccessStrategy implements VaultKvAccessStrategy {
        private final VaultKvAccessStrategy accessStrategy;

        private HttpHeaders headers;

        TestAccessStrategy(RestTemplate restTemplate, VaultEnvironmentProperties properties) {
            String baseUrl = String.format("%s://%s:%s", properties.getScheme(), properties.getHost(), properties.getPort());
            this.accessStrategy = VaultKvAccessStrategyFactory.forVersion(restTemplate, baseUrl, properties.getKvVersion());
        }

        @Override
        public String getData(HttpHeaders headers, String backend, String key) throws RestClientException {
            this.headers = headers;
            return this.accessStrategy.getData(headers, backend, key);
        }
    }
}

