/**
 * Copyright 2015-2016 the original author or authors.
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
package org.springframework.security.web.jackson2;


import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.authentication.WebAuthenticationDetails;


/**
 *
 *
 * @author Jitendra Singh
 * @since 4.2
 */
public class WebAuthenticationDetailsMixinTests extends AbstractMixinTests {
    // @formatter:off
    private static final String AUTHENTICATION_DETAILS_JSON = "{" + (((("\"@class\": \"org.springframework.security.web.authentication.WebAuthenticationDetails\"," + "\"sessionId\": \"1\", ") + "\"remoteAddress\": ") + "\"/localhost\"") + "}");

    // @formatter:on
    @Test
    public void buildWebAuthenticationDetailsUsingDifferentConstructors() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("localhost");
        request.setSession(new MockHttpSession(null, "1"));
        WebAuthenticationDetails details = new WebAuthenticationDetails(request);
        WebAuthenticationDetails authenticationDetails = mapper.readValue(WebAuthenticationDetailsMixinTests.AUTHENTICATION_DETAILS_JSON, WebAuthenticationDetails.class);
        assertThat(details.equals(authenticationDetails));
    }

    @Test
    public void webAuthenticationDetailsSerializeTest() throws JsonProcessingException, JSONException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("/localhost");
        request.setSession(new MockHttpSession(null, "1"));
        WebAuthenticationDetails details = new WebAuthenticationDetails(request);
        String actualJson = mapper.writeValueAsString(details);
        JSONAssert.assertEquals(WebAuthenticationDetailsMixinTests.AUTHENTICATION_DETAILS_JSON, actualJson, true);
    }

    @Test
    public void webAuthenticationDetailsDeserializeTest() throws IOException, JSONException {
        WebAuthenticationDetails details = mapper.readValue(WebAuthenticationDetailsMixinTests.AUTHENTICATION_DETAILS_JSON, WebAuthenticationDetails.class);
        assertThat(details).isNotNull();
        assertThat(details.getRemoteAddress()).isEqualTo("/localhost");
        assertThat(details.getSessionId()).isEqualTo("1");
    }
}

