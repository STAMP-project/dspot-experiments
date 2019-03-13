/**
 * Copyright 2006-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.security.oauth2.provider.error;


import HttpServletResponse.SC_FORBIDDEN;
import MediaType.APPLICATION_JSON_VALUE;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;


/**
 *
 *
 * @author Dave Syer
 */
public class OAuth2AccessDeniedHandlerTests {
    private OAuth2AccessDeniedHandler handler = new OAuth2AccessDeniedHandler();

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    public void testHandleWithJson() throws Exception {
        request.addHeader("Accept", APPLICATION_JSON_VALUE);
        handler.handle(request, response, new AccessDeniedException("Bad"));
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        Assert.assertTrue(response.getContentType().contains(APPLICATION_JSON_VALUE));
        Assert.assertEquals(null, response.getErrorMessage());
    }
}

