/**
 * Copyright 2011-2016 the original author or authors.
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
package org.springframework.security.cas.web.authentication;


import java.util.regex.Pattern;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.util.UrlUtils;


/**
 *
 *
 * @author Rob Winch
 */
public class DefaultServiceAuthenticationDetailsTests {
    private DefaultServiceAuthenticationDetails details;

    private MockHttpServletRequest request;

    private Pattern artifactPattern;

    private String casServiceUrl;

    private ConfigurableApplicationContext context;

    @Test
    public void getServiceUrlNullQuery() throws Exception {
        this.details = new DefaultServiceAuthenticationDetails(this.casServiceUrl, this.request, this.artifactPattern);
        assertThat(this.details.getServiceUrl()).isEqualTo(UrlUtils.buildFullRequestUrl(this.request));
    }

    @Test
    public void getServiceUrlTicketOnlyParam() throws Exception {
        this.request.setQueryString("ticket=123");
        this.details = new DefaultServiceAuthenticationDetails(this.casServiceUrl, this.request, this.artifactPattern);
        String serviceUrl = this.details.getServiceUrl();
        this.request.setQueryString(null);
        assertThat(serviceUrl).isEqualTo(UrlUtils.buildFullRequestUrl(this.request));
    }

    @Test
    public void getServiceUrlTicketFirstMultiParam() throws Exception {
        this.request.setQueryString("ticket=123&other=value");
        this.details = new DefaultServiceAuthenticationDetails(this.casServiceUrl, this.request, this.artifactPattern);
        String serviceUrl = this.details.getServiceUrl();
        this.request.setQueryString("other=value");
        assertThat(serviceUrl).isEqualTo(UrlUtils.buildFullRequestUrl(this.request));
    }

    @Test
    public void getServiceUrlTicketLastMultiParam() throws Exception {
        this.request.setQueryString("other=value&ticket=123");
        this.details = new DefaultServiceAuthenticationDetails(this.casServiceUrl, this.request, this.artifactPattern);
        String serviceUrl = this.details.getServiceUrl();
        this.request.setQueryString("other=value");
        assertThat(serviceUrl).isEqualTo(UrlUtils.buildFullRequestUrl(this.request));
    }

    @Test
    public void getServiceUrlTicketMiddleMultiParam() throws Exception {
        this.request.setQueryString("other=value&ticket=123&last=this");
        this.details = new DefaultServiceAuthenticationDetails(this.casServiceUrl, this.request, this.artifactPattern);
        String serviceUrl = this.details.getServiceUrl();
        this.request.setQueryString("other=value&last=this");
        assertThat(serviceUrl).isEqualTo(UrlUtils.buildFullRequestUrl(this.request));
    }

    @Test
    public void getServiceUrlDoesNotUseHostHeader() throws Exception {
        this.casServiceUrl = "https://example.com/j_spring_security_cas";
        this.request.setServerName("evil.com");
        this.details = new DefaultServiceAuthenticationDetails(this.casServiceUrl, this.request, this.artifactPattern);
        assertThat(this.details.getServiceUrl()).isEqualTo("https://example.com/cas-sample/secure/");
    }

    @Test
    public void getServiceUrlDoesNotUseHostHeaderExplicit() {
        this.casServiceUrl = "https://example.com/j_spring_security_cas";
        this.request.setServerName("evil.com");
        ServiceAuthenticationDetails details = loadServiceAuthenticationDetails("defaultserviceauthenticationdetails-explicit.xml");
        assertThat(details.getServiceUrl()).isEqualTo("https://example.com/cas-sample/secure/");
    }
}

