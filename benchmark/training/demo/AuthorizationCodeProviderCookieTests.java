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
package demo;


import HttpStatus.CREATED;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import sparklr.common.AbstractEmptyAuthorizationCodeProviderTests;


/**
 *
 *
 * @author Dave Syer
 */
public class AuthorizationCodeProviderCookieTests extends AbstractEmptyAuthorizationCodeProviderTests {
    @Test
    @OAuth2ContextConfiguration(resource = MyClientWithRegisteredRedirect.class, initialize = false)
    public void testPostToProtectedResource() throws Exception {
        approveAccessTokenGrant("http://anywhere?key=value", true);
        Assert.assertNotNull(context.getAccessToken());
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap();
        form.set("foo", "bar");
        Assert.assertEquals(CREATED, http.postForStatus("/", getAuthenticatedHeaders(), form).getStatusCode());
    }
}

