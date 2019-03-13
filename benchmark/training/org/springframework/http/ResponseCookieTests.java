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
package org.springframework.http;


import java.time.Duration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link ResponseCookie}.
 *
 * @author Rossen Stoyanchev
 */
public class ResponseCookieTests {
    @Test
    public void defaultValues() {
        Assert.assertEquals("id=1fWa", ResponseCookie.from("id", "1fWa").build().toString());
    }

    @Test
    public void httpOnlyStrictSecureWithDomainAndPath() {
        Assert.assertEquals("id=1fWa; Path=/projects; Domain=spring.io; Secure; HttpOnly; SameSite=strict", ResponseCookie.from("id", "1fWa").domain("spring.io").path("/projects").httpOnly(true).secure(true).sameSite("strict").build().toString());
    }

    @Test
    public void maxAge() {
        Duration maxAge = Duration.ofDays(365);
        String expires = HttpHeaders.formatDate(((System.currentTimeMillis()) + (maxAge.toMillis())));
        expires = expires.substring(0, ((expires.indexOf(":")) + 1));
        Assert.assertThat(ResponseCookie.from("id", "1fWa").maxAge(maxAge).build().toString(), CoreMatchers.allOf(CoreMatchers.startsWith(("id=1fWa; Max-Age=31536000; Expires=" + expires)), CoreMatchers.endsWith(" GMT")));
        Assert.assertThat(ResponseCookie.from("id", "1fWa").maxAge(maxAge.getSeconds()).build().toString(), CoreMatchers.allOf(CoreMatchers.startsWith(("id=1fWa; Max-Age=31536000; Expires=" + expires)), CoreMatchers.endsWith(" GMT")));
    }

    @Test
    public void maxAge0() {
        Assert.assertEquals("id=1fWa; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT", ResponseCookie.from("id", "1fWa").maxAge(Duration.ofSeconds(0)).build().toString());
        Assert.assertEquals("id=1fWa; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT", ResponseCookie.from("id", "1fWa").maxAge(0).build().toString());
    }
}

