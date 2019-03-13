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
package org.springframework.http.server.reactive;


import java.net.URI;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rossen Stoyanchev
 */
@RunWith(Parameterized.class)
public class CookieIntegrationTests extends AbstractHttpHandlerIntegrationTests {
    private final CookieIntegrationTests.CookieHandler cookieHandler = new CookieIntegrationTests.CookieHandler();

    @SuppressWarnings("unchecked")
    @Test
    public void basicTest() throws Exception {
        URI url = new URI(("http://localhost:" + (port)));
        String header = "SID=31d4d96e407aad42; lang=en-US";
        ResponseEntity<Void> response = new RestTemplate().exchange(org.springframework.http.RequestEntity.get(url).header("Cookie", header).build(), Void.class);
        Map<String, List<HttpCookie>> requestCookies = this.cookieHandler.requestCookies;
        Assert.assertEquals(2, requestCookies.size());
        List<HttpCookie> list = requestCookies.get("SID");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("31d4d96e407aad42", list.iterator().next().getValue());
        list = requestCookies.get("lang");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("en-US", list.iterator().next().getValue());
        List<String> headerValues = response.getHeaders().get("Set-Cookie");
        Assert.assertEquals(2, headerValues.size());
        Assert.assertThat(splitCookie(headerValues.get(0)), containsInAnyOrder(CoreMatchers.equalTo("SID=31d4d96e407aad42"), equalToIgnoringCase("Path=/"), equalToIgnoringCase("Secure"), equalToIgnoringCase("HttpOnly")));
        Assert.assertThat(splitCookie(headerValues.get(1)), containsInAnyOrder(CoreMatchers.equalTo("lang=en-US"), equalToIgnoringCase("Path=/"), equalToIgnoringCase("Domain=example.com")));
    }

    private class CookieHandler implements HttpHandler {
        private Map<String, List<HttpCookie>> requestCookies;

        @Override
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            this.requestCookies = request.getCookies();
            this.requestCookies.size();// Cause lazy loading

            response.getCookies().add("SID", ResponseCookie.from("SID", "31d4d96e407aad42").path("/").secure(true).httpOnly(true).build());
            response.getCookies().add("lang", ResponseCookie.from("lang", "en-US").domain("example.com").path("/").build());
            return response.setComplete();
        }
    }
}

