/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.config.http;


import AbstractRememberMeServices.DEFAULT_PARAMETER;
import OpenIDAuthenticationFilter.DEFAULT_CLAIMED_IDENTITY_FIELD;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openid4java.consumer.ConsumerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.openid.OpenID4JavaConsumer;
import org.springframework.security.openid.OpenIDAuthenticationFilter;
import org.springframework.security.openid.OpenIDConsumer;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Tests usage of the &lt;openid-login&gt; element
 *
 * @author Luke Taylor
 */
public class OpenIDConfigTests {
    private static final String CONFIG_LOCATION_PREFIX = "classpath:org/springframework/security/config/http/OpenIDConfigTests";

    @Autowired
    MockMvc mvc;

    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Test
    public void requestWhenOpenIDAndFormLoginBothConfiguredThenRedirectsToGeneratedLoginPage() throws Exception {
        this.spring.configLocations(this.xml("WithFormLogin")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isFound()).andExpect(redirectedUrl("http://localhost/login"));
        assertThat(getFilter(DefaultLoginPageGeneratingFilter.class)).isNotNull();
    }

    @Test
    public void requestWhenOpenIDAndFormLoginWithFormLoginPageConfiguredThenFormLoginPageWins() throws Exception {
        this.spring.configLocations(this.xml("WithFormLoginPage")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isFound()).andExpect(redirectedUrl("http://localhost/form-page"));
    }

    @Test
    public void requestWhenOpenIDAndFormLoginWithOpenIDLoginPageConfiguredThenOpenIDLoginPageWins() throws Exception {
        this.spring.configLocations(this.xml("WithOpenIDLoginPageAndFormLogin")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isFound()).andExpect(redirectedUrl("http://localhost/openid-page"));
    }

    @Test
    public void configureWhenOpenIDAndFormLoginBothConfigureLoginPagesThenWiringException() throws Exception {
        assertThatCode(() -> this.spring.configLocations(this.xml("WithFormLoginAndOpenIDLoginPages")).autowire()).isInstanceOf(BeanDefinitionParsingException.class);
    }

    @Test
    public void requestWhenOpenIDAndRememberMeConfiguredThenRememberMePassedToIdp() throws Exception {
        this.spring.configLocations(this.xml("WithRememberMe")).autowire();
        OpenIDAuthenticationFilter openIDFilter = getFilter(OpenIDAuthenticationFilter.class);
        String openIdEndpointUrl = "http://testopenid.com?openid.return_to=";
        Set<String> returnToUrlParameters = new HashSet<>();
        returnToUrlParameters.add(DEFAULT_PARAMETER);
        openIDFilter.setReturnToUrlParameters(returnToUrlParameters);
        OpenIDConsumer consumer = Mockito.mock(OpenIDConsumer.class);
        Mockito.when(consumer.beginConsumption(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).then(( invocation) -> openIdEndpointUrl + (invocation.getArgument(2)));
        openIDFilter.setConsumer(consumer);
        String expectedReturnTo = new StringBuilder("http://localhost/login/openid").append("?").append(DEFAULT_PARAMETER).append("=").append("on").toString();
        this.mvc.perform(get("/")).andExpect(status().isFound()).andExpect(redirectedUrl("http://localhost/login"));
        this.mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(content().string(CoreMatchers.containsString(DEFAULT_PARAMETER)));
        this.mvc.perform(get("/login/openid").param(DEFAULT_CLAIMED_IDENTITY_FIELD, "http://hey.openid.com/").param(DEFAULT_PARAMETER, "on")).andExpect(status().isFound()).andExpect(redirectedUrl((openIdEndpointUrl + expectedReturnTo)));
    }

    @Test
    public void requestWhenAttributeExchangeConfiguredThenFetchAttributesPassedToIdp() throws Exception {
        this.spring.configLocations(this.xml("WithOpenIDAttributes")).autowire();
        OpenIDAuthenticationFilter openIDFilter = getFilter(OpenIDAuthenticationFilter.class);
        OpenID4JavaConsumer consumer = OpenIDConfigTests.getFieldValue(openIDFilter, "consumer");
        ConsumerManager manager = OpenIDConfigTests.getFieldValue(consumer, "consumerManager");
        manager.setMaxAssocAttempts(0);
        try (MockWebServer server = new MockWebServer()) {
            String endpoint = server.url("/").toString();
            server.enqueue(new MockResponse().addHeader(YADIS_XRDS_LOCATION, endpoint));
            server.enqueue(new MockResponse().setBody(String.format("<XRDS><XRD><Service><URI>%s</URI></Service></XRD></XRDS>", endpoint)));
            this.mvc.perform(get("/login/openid").param(DEFAULT_CLAIMED_IDENTITY_FIELD, endpoint)).andExpect(status().isFound()).andExpect(( result) -> result.getResponse().getRedirectedUrl().endsWith(("openid.ext1.type.nickname=http%3A%2F%2Fschema.openid.net%2FnamePerson%2Ffriendly&" + ((("openid.ext1.if_available=nickname&" + "openid.ext1.type.email=http%3A%2F%2Fschema.openid.net%2Fcontact%2Femail&") + "openid.ext1.required=email&") + "openid.ext1.count.email=2"))));
        }
    }

    /**
     * SEC-2919
     */
    @Test
    public void requestWhenLoginPageConfiguredWithPhraseLoginThenRedirectsOnlyToUserGeneratedLoginPage() throws Exception {
        this.spring.configLocations(this.xml("Sec2919")).autowire();
        assertThat(getFilter(DefaultLoginPageGeneratingFilter.class)).isNull();
        this.mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(content().string("a custom login page"));
    }

    @RestController
    static class CustomLoginController {
        @GetMapping("/login")
        public String custom() {
            return "a custom login page";
        }
    }
}

