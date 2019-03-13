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
package org.springframework.boot.actuate.endpoint.web;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.web.annotation.ExposableControllerEndpoint;


/**
 * Tests for {@link EndpointLinksResolver}.
 *
 * @author Andy Wilkinson
 */
public class EndpointLinksResolverTests {
    @Test
    public void linkResolutionWithTrailingSlashStripsSlashOnSelfLink() {
        Map<String, Link> links = new EndpointLinksResolver(Collections.emptyList()).resolveLinks("https://api.example.com/actuator/");
        assertThat(links).hasSize(1);
        assertThat(links).hasEntrySatisfying("self", linkWithHref("https://api.example.com/actuator"));
    }

    @Test
    public void linkResolutionWithoutTrailingSlash() {
        Map<String, Link> links = new EndpointLinksResolver(Collections.emptyList()).resolveLinks("https://api.example.com/actuator");
        assertThat(links).hasSize(1);
        assertThat(links).hasEntrySatisfying("self", linkWithHref("https://api.example.com/actuator"));
    }

    @Test
    public void resolvedLinksContainsALinkForEachWebEndpointOperation() {
        List<WebOperation> operations = new ArrayList<>();
        operations.add(operationWithPath("/alpha", "alpha"));
        operations.add(operationWithPath("/alpha/{name}", "alpha-name"));
        ExposableWebEndpoint endpoint = Mockito.mock(ExposableWebEndpoint.class);
        BDDMockito.given(endpoint.getEndpointId()).willReturn(EndpointId.of("alpha"));
        BDDMockito.given(endpoint.isEnableByDefault()).willReturn(true);
        BDDMockito.given(endpoint.getOperations()).willReturn(operations);
        String requestUrl = "https://api.example.com/actuator";
        Map<String, Link> links = resolveLinks(requestUrl);
        assertThat(links).hasSize(3);
        assertThat(links).hasEntrySatisfying("self", linkWithHref("https://api.example.com/actuator"));
        assertThat(links).hasEntrySatisfying("alpha", linkWithHref("https://api.example.com/actuator/alpha"));
        assertThat(links).hasEntrySatisfying("alpha-name", linkWithHref("https://api.example.com/actuator/alpha/{name}"));
    }

    @Test
    public void resolvedLinksContainsALinkForServletEndpoint() {
        ExposableServletEndpoint servletEndpoint = Mockito.mock(ExposableServletEndpoint.class);
        BDDMockito.given(servletEndpoint.getEndpointId()).willReturn(EndpointId.of("alpha"));
        BDDMockito.given(servletEndpoint.isEnableByDefault()).willReturn(true);
        BDDMockito.given(servletEndpoint.getRootPath()).willReturn("alpha");
        String requestUrl = "https://api.example.com/actuator";
        Map<String, Link> links = resolveLinks(requestUrl);
        assertThat(links).hasSize(2);
        assertThat(links).hasEntrySatisfying("self", linkWithHref("https://api.example.com/actuator"));
        assertThat(links).hasEntrySatisfying("alpha", linkWithHref("https://api.example.com/actuator/alpha"));
    }

    @Test
    public void resolvedLinksContainsALinkForControllerEndpoint() {
        ExposableControllerEndpoint controllerEndpoint = Mockito.mock(ExposableControllerEndpoint.class);
        BDDMockito.given(controllerEndpoint.getEndpointId()).willReturn(EndpointId.of("alpha"));
        BDDMockito.given(controllerEndpoint.isEnableByDefault()).willReturn(true);
        BDDMockito.given(controllerEndpoint.getRootPath()).willReturn("alpha");
        String requestUrl = "https://api.example.com/actuator";
        Map<String, Link> links = resolveLinks(requestUrl);
        assertThat(links).hasSize(2);
        assertThat(links).hasEntrySatisfying("self", linkWithHref("https://api.example.com/actuator"));
        assertThat(links).hasEntrySatisfying("alpha", linkWithHref("https://api.example.com/actuator/alpha"));
    }
}

