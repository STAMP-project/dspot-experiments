/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.oauth2.client.web;


import OAuth2ParameterNames.STATE;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;


/**
 * Tests for {@link HttpSessionOAuth2AuthorizationRequestRepository}.
 *
 * @author Joe Grandja
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpSessionOAuth2AuthorizationRequestRepositoryTests {
    private HttpSessionOAuth2AuthorizationRequestRepository authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();

    @Test(expected = IllegalArgumentException.class)
    public void loadAuthorizationRequestWhenHttpServletRequestIsNullThenThrowIllegalArgumentException() {
        this.authorizationRequestRepository.loadAuthorizationRequest(null);
    }

    @Test
    public void loadAuthorizationRequestWhenNotSavedThenReturnNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(STATE, "state-1234");
        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository.loadAuthorizationRequest(request);
        assertThat(authorizationRequest).isNull();
    }

    @Test
    public void loadAuthorizationRequestWhenSavedThenReturnAuthorizationRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        request.addParameter(STATE, authorizationRequest.getState());
        OAuth2AuthorizationRequest loadedAuthorizationRequest = this.authorizationRequestRepository.loadAuthorizationRequest(request);
        assertThat(loadedAuthorizationRequest).isEqualTo(authorizationRequest);
    }

    // gh-5110
    @Test
    public void loadAuthorizationRequestWhenMultipleSavedThenReturnMatchingAuthorizationRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String state1 = "state-1122";
        OAuth2AuthorizationRequest authorizationRequest1 = createAuthorizationRequest().state(state1).build();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest1, request, response);
        String state2 = "state-3344";
        OAuth2AuthorizationRequest authorizationRequest2 = createAuthorizationRequest().state(state2).build();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest2, request, response);
        String state3 = "state-5566";
        OAuth2AuthorizationRequest authorizationRequest3 = createAuthorizationRequest().state(state3).build();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest3, request, response);
        request.addParameter(STATE, state1);
        OAuth2AuthorizationRequest loadedAuthorizationRequest1 = this.authorizationRequestRepository.loadAuthorizationRequest(request);
        assertThat(loadedAuthorizationRequest1).isEqualTo(authorizationRequest1);
        request.removeParameter(STATE);
        request.addParameter(STATE, state2);
        OAuth2AuthorizationRequest loadedAuthorizationRequest2 = this.authorizationRequestRepository.loadAuthorizationRequest(request);
        assertThat(loadedAuthorizationRequest2).isEqualTo(authorizationRequest2);
        request.removeParameter(STATE);
        request.addParameter(STATE, state3);
        OAuth2AuthorizationRequest loadedAuthorizationRequest3 = this.authorizationRequestRepository.loadAuthorizationRequest(request);
        assertThat(loadedAuthorizationRequest3).isEqualTo(authorizationRequest3);
    }

    @Test
    public void loadAuthorizationRequestWhenSavedAndStateParameterNullThenReturnNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, new MockHttpServletResponse());
        assertThat(this.authorizationRequestRepository.loadAuthorizationRequest(request)).isNull();
    }

    @Test
    public void saveAuthorizationRequestWhenHttpServletRequestIsNullThenThrowIllegalArgumentException() {
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        assertThatThrownBy(() -> this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, null, new MockHttpServletResponse())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void saveAuthorizationRequestWhenHttpServletResponseIsNullThenThrowIllegalArgumentException() {
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        assertThatThrownBy(() -> this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, new MockHttpServletRequest(), null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void saveAuthorizationRequestWhenStateNullThenThrowIllegalArgumentException() {
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().state(null).build();
        assertThatThrownBy(() -> this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, new MockHttpServletRequest(), new MockHttpServletResponse())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void saveAuthorizationRequestWhenNotNullThenSaved() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, new MockHttpServletResponse());
        request.addParameter(STATE, authorizationRequest.getState());
        OAuth2AuthorizationRequest loadedAuthorizationRequest = this.authorizationRequestRepository.loadAuthorizationRequest(request);
        assertThat(loadedAuthorizationRequest).isEqualTo(authorizationRequest);
    }

    @Test
    public void saveAuthorizationRequestWhenNoExistingSessionAndDistributedSessionThenSaved() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(new HttpSessionOAuth2AuthorizationRequestRepositoryTests.MockDistributedHttpSession());
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, new MockHttpServletResponse());
        request.addParameter(STATE, authorizationRequest.getState());
        OAuth2AuthorizationRequest loadedAuthorizationRequest = this.authorizationRequestRepository.loadAuthorizationRequest(request);
        assertThat(loadedAuthorizationRequest).isEqualTo(authorizationRequest);
    }

    @Test
    public void saveAuthorizationRequestWhenExistingSessionAndDistributedSessionThenSaved() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(new HttpSessionOAuth2AuthorizationRequestRepositoryTests.MockDistributedHttpSession());
        OAuth2AuthorizationRequest authorizationRequest1 = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest1, request, new MockHttpServletResponse());
        OAuth2AuthorizationRequest authorizationRequest2 = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest2, request, new MockHttpServletResponse());
        request.addParameter(STATE, authorizationRequest2.getState());
        OAuth2AuthorizationRequest loadedAuthorizationRequest = this.authorizationRequestRepository.loadAuthorizationRequest(request);
        assertThat(loadedAuthorizationRequest).isEqualTo(authorizationRequest2);
    }

    @Test
    public void saveAuthorizationRequestWhenNullThenRemoved() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        // Save
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        request.addParameter(STATE, authorizationRequest.getState());
        // Null value removes
        this.authorizationRequestRepository.saveAuthorizationRequest(null, request, response);
        OAuth2AuthorizationRequest loadedAuthorizationRequest = this.authorizationRequestRepository.loadAuthorizationRequest(request);
        assertThat(loadedAuthorizationRequest).isNull();
    }

    @Test
    public void removeAuthorizationRequestWhenHttpServletRequestIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.authorizationRequestRepository.removeAuthorizationRequest(null, new MockHttpServletResponse())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void removeAuthorizationRequestWhenHttpServletResponseIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.authorizationRequestRepository.removeAuthorizationRequest(new MockHttpServletRequest(), null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void removeAuthorizationRequestWhenSavedThenRemoved() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        request.addParameter(STATE, authorizationRequest.getState());
        OAuth2AuthorizationRequest removedAuthorizationRequest = this.authorizationRequestRepository.removeAuthorizationRequest(request, response);
        OAuth2AuthorizationRequest loadedAuthorizationRequest = this.authorizationRequestRepository.loadAuthorizationRequest(request);
        assertThat(removedAuthorizationRequest).isNotNull();
        assertThat(loadedAuthorizationRequest).isNull();
    }

    // gh-5263
    @Test
    public void removeAuthorizationRequestWhenSavedThenRemovedFromSession() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        request.addParameter(STATE, authorizationRequest.getState());
        OAuth2AuthorizationRequest removedAuthorizationRequest = this.authorizationRequestRepository.removeAuthorizationRequest(request, response);
        String sessionAttributeName = (HttpSessionOAuth2AuthorizationRequestRepository.class.getName()) + ".AUTHORIZATION_REQUEST";
        assertThat(removedAuthorizationRequest).isNotNull();
        assertThat(request.getSession().getAttribute(sessionAttributeName)).isNull();
    }

    @Test
    public void removeAuthorizationRequestWhenNotSavedThenNotRemoved() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(STATE, "state-1234");
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2AuthorizationRequest removedAuthorizationRequest = this.authorizationRequestRepository.removeAuthorizationRequest(request, response);
        assertThat(removedAuthorizationRequest).isNull();
    }

    static class MockDistributedHttpSession extends MockHttpSession {
        @Override
        public Object getAttribute(String name) {
            return wrap(super.getAttribute(name));
        }

        @Override
        public void setAttribute(String name, Object value) {
            super.setAttribute(name, wrap(value));
        }

        private Object wrap(Object object) {
            if (object instanceof Map) {
                object = new HashMap<>(((Map<Object, Object>) (object)));
            }
            return object;
        }
    }
}

