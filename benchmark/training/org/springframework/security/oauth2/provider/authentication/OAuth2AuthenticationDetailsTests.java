/**
 * Copyright 2013-2014 the original author or authors.
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
package org.springframework.security.oauth2.provider.authentication;


import OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE;
import OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.SerializationUtils;


/**
 *
 *
 * @author Dave Syer
 */
public class OAuth2AuthenticationDetailsTests {
    @Test
    public void testSerializationWithDetails() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(ACCESS_TOKEN_VALUE, "FOO");
        request.setAttribute(ACCESS_TOKEN_TYPE, "bearer");
        OAuth2AuthenticationDetails holder = new OAuth2AuthenticationDetails(request);
        OAuth2AuthenticationDetails other = ((OAuth2AuthenticationDetails) (SerializationUtils.deserialize(SerializationUtils.serialize(holder))));
        Assert.assertEquals(holder, other);
    }
}

