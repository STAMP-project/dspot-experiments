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
package org.springframework.web.servlet.mvc.method.annotation;


import javax.servlet.http.Cookie;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;


/**
 * Test fixture with {@link ServletCookieValueMethodArgumentResolver}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class ServletCookieValueMethodArgumentResolverTests {
    private ServletCookieValueMethodArgumentResolver resolver;

    private MockHttpServletRequest request;

    private ServletWebRequest webRequest;

    private MethodParameter cookieParameter;

    private MethodParameter cookieStringParameter;

    @Test
    public void resolveCookieArgument() throws Exception {
        Cookie expected = new Cookie("name", "foo");
        request.setCookies(expected);
        Cookie result = ((Cookie) (resolver.resolveArgument(cookieParameter, null, webRequest, null)));
        Assert.assertEquals("Invalid result", expected, result);
    }

    @Test
    public void resolveCookieStringArgument() throws Exception {
        Cookie cookie = new Cookie("name", "foo");
        request.setCookies(cookie);
        String result = ((String) (resolver.resolveArgument(cookieStringParameter, null, webRequest, null)));
        Assert.assertEquals("Invalid result", cookie.getValue(), result);
    }
}

