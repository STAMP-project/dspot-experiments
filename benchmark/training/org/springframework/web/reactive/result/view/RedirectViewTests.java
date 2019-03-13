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
package org.springframework.web.reactive.result.view;


import HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
import HttpStatus.FOUND;
import HttpStatus.SEE_OTHER;
import MediaType.TEXT_HTML;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;


/**
 * Tests for redirect view, and query string construction.
 * Doesn't test URL encoding, although it does check that it's called.
 *
 * @author Sebastien Deleuze
 */
public class RedirectViewTests {
    private MockServerWebExchange exchange;

    @Test(expected = IllegalArgumentException.class)
    public void noUrlSet() throws Exception {
        RedirectView rv = new RedirectView(null);
        rv.afterPropertiesSet();
    }

    @Test
    public void defaultStatusCode() {
        String url = "http://url.somewhere.com";
        RedirectView view = new RedirectView(url);
        view.render(new HashMap(), TEXT_HTML, this.exchange).block();
        Assert.assertEquals(SEE_OTHER, this.exchange.getResponse().getStatusCode());
        Assert.assertEquals(URI.create(url), this.exchange.getResponse().getHeaders().getLocation());
    }

    @Test
    public void customStatusCode() {
        String url = "http://url.somewhere.com";
        RedirectView view = new RedirectView(url, HttpStatus.FOUND);
        view.render(new HashMap(), TEXT_HTML, this.exchange).block();
        Assert.assertEquals(FOUND, this.exchange.getResponse().getStatusCode());
        Assert.assertEquals(URI.create(url), this.exchange.getResponse().getHeaders().getLocation());
    }

    @Test
    public void contextRelative() {
        String url = "/test.html";
        RedirectView view = new RedirectView(url);
        view.render(new HashMap(), TEXT_HTML, this.exchange).block();
        Assert.assertEquals(URI.create("/context/test.html"), this.exchange.getResponse().getHeaders().getLocation());
    }

    @Test
    public void contextRelativeQueryParam() {
        String url = "/test.html?id=1";
        RedirectView view = new RedirectView(url);
        view.render(new HashMap(), TEXT_HTML, this.exchange).block();
        Assert.assertEquals(URI.create("/context/test.html?id=1"), this.exchange.getResponse().getHeaders().getLocation());
    }

    @Test
    public void remoteHost() {
        RedirectView view = new RedirectView("");
        Assert.assertFalse(view.isRemoteHost("http://url.somewhere.com"));
        Assert.assertFalse(view.isRemoteHost("/path"));
        Assert.assertFalse(view.isRemoteHost("http://url.somewhereelse.com"));
        view.setHosts("url.somewhere.com");
        Assert.assertFalse(view.isRemoteHost("http://url.somewhere.com"));
        Assert.assertFalse(view.isRemoteHost("/path"));
        Assert.assertTrue(view.isRemoteHost("http://url.somewhereelse.com"));
    }

    @Test
    public void expandUriTemplateVariablesFromModel() {
        String url = "http://url.somewhere.com?foo={foo}";
        Map<String, String> model = Collections.singletonMap("foo", "bar");
        RedirectView view = new RedirectView(url);
        view.render(model, TEXT_HTML, this.exchange).block();
        Assert.assertEquals(URI.create("http://url.somewhere.com?foo=bar"), this.exchange.getResponse().getHeaders().getLocation());
    }

    @Test
    public void expandUriTemplateVariablesFromExchangeAttribute() {
        String url = "http://url.somewhere.com?foo={foo}";
        Map<String, String> attributes = Collections.singletonMap("foo", "bar");
        this.exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, attributes);
        RedirectView view = new RedirectView(url);
        view.render(new HashMap(), TEXT_HTML, exchange).block();
        Assert.assertEquals(URI.create("http://url.somewhere.com?foo=bar"), this.exchange.getResponse().getHeaders().getLocation());
    }

    @Test
    public void propagateQueryParams() throws Exception {
        RedirectView view = new RedirectView("http://url.somewhere.com?foo=bar#bazz");
        view.setPropagateQuery(true);
        this.exchange = MockServerWebExchange.from(MockServerHttpRequest.get("http://url.somewhere.com?a=b&c=d"));
        view.render(new HashMap(), TEXT_HTML, this.exchange).block();
        Assert.assertEquals(SEE_OTHER, this.exchange.getResponse().getStatusCode());
        Assert.assertEquals(URI.create("http://url.somewhere.com?foo=bar&a=b&c=d#bazz"), this.exchange.getResponse().getHeaders().getLocation());
    }
}

