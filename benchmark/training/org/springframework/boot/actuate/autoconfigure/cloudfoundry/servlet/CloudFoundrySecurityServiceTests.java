/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.autoconfigure.cloudfoundry.servlet;


import AccessLevel.FULL;
import AccessLevel.RESTRICTED;
import Reason.ACCESS_DENIED;
import Reason.INVALID_TOKEN;
import Reason.SERVICE_UNAVAILABLE;
import java.util.Map;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.cloudfoundry.AccessLevel;
import org.springframework.boot.actuate.autoconfigure.cloudfoundry.CloudFoundryAuthorizationException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;


/**
 * Tests for {@link CloudFoundrySecurityService}.
 *
 * @author Madhura Bhave
 */
public class CloudFoundrySecurityServiceTests {
    private static final String CLOUD_CONTROLLER = "http://my-cloud-controller.com";

    private static final String CLOUD_CONTROLLER_PERMISSIONS = (CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER) + "/v2/apps/my-app-id/permissions";

    private static final String UAA_URL = "http://my-uaa.com";

    private CloudFoundrySecurityService securityService;

    private MockRestServiceServer server;

    @Test
    public void skipSslValidationWhenTrue() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        this.securityService = new CloudFoundrySecurityService(builder, CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER, true);
        RestTemplate restTemplate = ((RestTemplate) (ReflectionTestUtils.getField(this.securityService, "restTemplate")));
        assertThat(restTemplate.getRequestFactory()).isInstanceOf(SkipSslVerificationHttpRequestFactory.class);
    }

    @Test
    public void doNotskipSslValidationWhenFalse() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        this.securityService = new CloudFoundrySecurityService(builder, CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER, false);
        RestTemplate restTemplate = ((RestTemplate) (ReflectionTestUtils.getField(this.securityService, "restTemplate")));
        assertThat(restTemplate.getRequestFactory()).isNotInstanceOf(SkipSslVerificationHttpRequestFactory.class);
    }

    @Test
    public void getAccessLevelWhenSpaceDeveloperShouldReturnFull() {
        String responseBody = "{\"read_sensitive_data\": true,\"read_basic_data\": true}";
        this.server.expect(requestTo(CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER_PERMISSIONS)).andExpect(header("Authorization", "bearer my-access-token")).andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
        AccessLevel accessLevel = this.securityService.getAccessLevel("my-access-token", "my-app-id");
        this.server.verify();
        assertThat(accessLevel).isEqualTo(FULL);
    }

    @Test
    public void getAccessLevelWhenNotSpaceDeveloperShouldReturnRestricted() {
        String responseBody = "{\"read_sensitive_data\": false,\"read_basic_data\": true}";
        this.server.expect(requestTo(CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER_PERMISSIONS)).andExpect(header("Authorization", "bearer my-access-token")).andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
        AccessLevel accessLevel = this.securityService.getAccessLevel("my-access-token", "my-app-id");
        this.server.verify();
        assertThat(accessLevel).isEqualTo(RESTRICTED);
    }

    @Test
    public void getAccessLevelWhenTokenIsNotValidShouldThrowException() {
        this.server.expect(requestTo(CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER_PERMISSIONS)).andExpect(header("Authorization", "bearer my-access-token")).andRespond(withUnauthorizedRequest());
        assertThatExceptionOfType(CloudFoundryAuthorizationException.class).isThrownBy(() -> this.securityService.getAccessLevel("my-access-token", "my-app-id")).satisfies(reasonRequirement(INVALID_TOKEN));
    }

    @Test
    public void getAccessLevelWhenForbiddenShouldThrowException() {
        this.server.expect(requestTo(CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER_PERMISSIONS)).andExpect(header("Authorization", "bearer my-access-token")).andRespond(withStatus(HttpStatus.FORBIDDEN));
        assertThatExceptionOfType(CloudFoundryAuthorizationException.class).isThrownBy(() -> this.securityService.getAccessLevel("my-access-token", "my-app-id")).satisfies(reasonRequirement(ACCESS_DENIED));
    }

    @Test
    public void getAccessLevelWhenCloudControllerIsNotReachableThrowsException() {
        this.server.expect(requestTo(CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER_PERMISSIONS)).andExpect(header("Authorization", "bearer my-access-token")).andRespond(withServerError());
        assertThatExceptionOfType(CloudFoundryAuthorizationException.class).isThrownBy(() -> this.securityService.getAccessLevel("my-access-token", "my-app-id")).satisfies(reasonRequirement(SERVICE_UNAVAILABLE));
    }

    @Test
    public void fetchTokenKeysWhenSuccessfulShouldReturnListOfKeysFromUAA() {
        this.server.expect(requestTo(((CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER) + "/info"))).andRespond(withSuccess("{\"token_endpoint\":\"http://my-uaa.com\"}", MediaType.APPLICATION_JSON));
        String tokenKeyValue = "-----BEGIN PUBLIC KEY-----\n" + (((((("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0m59l2u9iDnMbrXHfqkO\n" + "rn2dVQ3vfBJqcDuFUK03d+1PZGbVlNCqnkpIJ8syFppW8ljnWweP7+LiWpRoz0I7\n") + "fYb3d8TjhV86Y997Fl4DBrxgM6KTJOuE/uxnoDhZQ14LgOU2ckXjOzOdTsnGMKQB\n") + "LCl0vpcXBtFLMaSbpv1ozi8h7DJyVZ6EnFQZUWGdgTMhDrmqevfx95U/16c5WBDO\n") + "kqwIn7Glry9n9Suxygbf8g5AzpWcusZgDLIIZ7JTUldBb8qU2a0Dl4mvLZOn4wPo\n") + "jfj9Cw2QICsc5+Pwf21fP+hzf+1WSRHbnYv8uanRO0gZ8ekGaghM/2H6gqJbo2nI\n") + "JwIDAQAB\n-----END PUBLIC KEY-----");
        String responseBody = ("{\"keys\" : [ {\"kid\":\"test-key\",\"value\" : \"" + (tokenKeyValue.replace("\n", "\\n"))) + "\"} ]}";
        this.server.expect(requestTo(((CloudFoundrySecurityServiceTests.UAA_URL) + "/token_keys"))).andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
        Map<String, String> tokenKeys = this.securityService.fetchTokenKeys();
        this.server.verify();
        assertThat(tokenKeys.get("test-key")).isEqualTo(tokenKeyValue);
    }

    @Test
    public void fetchTokenKeysWhenNoKeysReturnedFromUAA() {
        this.server.expect(requestTo(((CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER) + "/info"))).andRespond(withSuccess((("{\"token_endpoint\":\"" + (CloudFoundrySecurityServiceTests.UAA_URL)) + "\"}"), MediaType.APPLICATION_JSON));
        String responseBody = "{\"keys\": []}";
        this.server.expect(requestTo(((CloudFoundrySecurityServiceTests.UAA_URL) + "/token_keys"))).andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
        Map<String, String> tokenKeys = this.securityService.fetchTokenKeys();
        this.server.verify();
        assertThat(tokenKeys).hasSize(0);
    }

    @Test
    public void fetchTokenKeysWhenUnsuccessfulShouldThrowException() {
        this.server.expect(requestTo(((CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER) + "/info"))).andRespond(withSuccess((("{\"token_endpoint\":\"" + (CloudFoundrySecurityServiceTests.UAA_URL)) + "\"}"), MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(((CloudFoundrySecurityServiceTests.UAA_URL) + "/token_keys"))).andRespond(withServerError());
        assertThatExceptionOfType(CloudFoundryAuthorizationException.class).isThrownBy(() -> this.securityService.fetchTokenKeys()).satisfies(reasonRequirement(SERVICE_UNAVAILABLE));
    }

    @Test
    public void getUaaUrlShouldCallCloudControllerInfoOnlyOnce() {
        this.server.expect(requestTo(((CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER) + "/info"))).andRespond(withSuccess((("{\"token_endpoint\":\"" + (CloudFoundrySecurityServiceTests.UAA_URL)) + "\"}"), MediaType.APPLICATION_JSON));
        String uaaUrl = this.securityService.getUaaUrl();
        this.server.verify();
        assertThat(uaaUrl).isEqualTo(CloudFoundrySecurityServiceTests.UAA_URL);
        // Second call should not need to hit server
        uaaUrl = this.securityService.getUaaUrl();
        assertThat(uaaUrl).isEqualTo(CloudFoundrySecurityServiceTests.UAA_URL);
    }

    @Test
    public void getUaaUrlWhenCloudControllerUrlIsNotReachableShouldThrowException() {
        this.server.expect(requestTo(((CloudFoundrySecurityServiceTests.CLOUD_CONTROLLER) + "/info"))).andRespond(withServerError());
        assertThatExceptionOfType(CloudFoundryAuthorizationException.class).isThrownBy(() -> this.securityService.getUaaUrl()).satisfies(reasonRequirement(SERVICE_UNAVAILABLE));
    }
}

