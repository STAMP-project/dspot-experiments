/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.zuul.filters.route.support;


import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.PATCH;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import MediaType.TEXT_HTML;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.cloud.netflix.zuul.RoutesEndpoint;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


/**
 *
 *
 * @author Spencer Gibb
 * @author Ryan Baxter
 */
public abstract class ZuulProxyTestBase {
    @Value("${local.server.port}")
    protected int port;

    @Autowired
    protected DiscoveryClientRouteLocator routes;

    @Autowired
    protected RoutesEndpoint endpoint;

    @Autowired
    protected RibbonCommandFactory<?> ribbonCommandFactory;

    @Autowired
    protected ZuulProxyTestBase.MyErrorController myErrorController;

    @Test
    public void bindRouteUsingPhysicalRoute() {
        assertThat(getRoute("/test/**")).isEqualTo("http://localhost:7777/local");
    }

    @Test
    public void bindRouteUsingOnlyPath() {
        assertThat(getRoute("/simple/**")).isEqualTo("simple");
    }

    @Test
    public void getOnSelfViaRibbonRoutingFilter() {
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/simple/local/1"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Gotten 1!");
    }

    @Test
    public void deleteOnSelfViaSimpleHostRoutingFilter() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/local"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/1"), DELETE, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Deleted 1!");
    }

    @Test
    public void stripPrefixFalseAppendsPath() {
        this.routes.addRoute(new ZuulProperties.ZuulRoute("strip", "/strip/**", "strip", (("http://localhost:" + (this.port)) + "/local"), false, false, null));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/strip"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        // Prefix not stripped to it goes to /local/strip
        assertThat(result.getBody()).isEqualTo("Gotten strip!");
    }

    @Test
    public void testNotFoundFromApp() {
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/simple/local/notfound"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void testNotFoundOnProxy() {
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/myinvalidpath"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void getSecondLevel() {
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/another/twolevel/local/1"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Gotten 1!");
    }

    @Test
    public void ribbonRouteWithSpace() {
        String uri = "/simple/spa ce";
        this.myErrorController.setUriToMatch(uri);
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + uri), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Hello space");
        assertThat(myErrorController.wasControllerUsed()).isFalse();
    }

    @Test
    public void ribbonDeleteWithBody() {
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/simple/deletewithbody"), DELETE, new org.springframework.http.HttpEntity("deleterequestbody"), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        if (supportsDeleteWithBody()) {
            assertThat(result.getBody()).isEqualTo("Deleted deleterequestbody");
        } else {
            assertThat(result.getBody()).isEqualTo("Deleted null");
        }
    }

    @Test
    public void ribbonRouteWithNonExistentUri() {
        String uri = "/simple/nonExistent";
        this.myErrorController.setUriToMatch(uri);
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + uri), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(myErrorController.wasControllerUsed()).isFalse();
    }

    @Test
    public void simpleHostRouteWithSpace() {
        this.routes.addRoute("/self/**", ("http://localhost:" + (this.port)));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/spa ce"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Hello space");
    }

    @Test
    public void simpleHostRouteWithOriginalQueryString() {
        this.routes.addRoute("/self/**", ("http://localhost:" + (this.port)));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/qstring?original=value1&original=value2"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Received {original=[value1, value2]}");
    }

    @Test
    public void simpleHostRouteWithOverriddenQString() {
        this.routes.addRoute("/self/**", ("http://localhost:" + (this.port)));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/qstring?override=true&different=key"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Received {key=[overridden]}");
    }

    @Test
    public void patchOnSelfViaSimpleHostRoutingFilter() {
        Assume.assumeThat(supportsPatch(), Matchers.is(true));
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/local"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/1"), PATCH, new org.springframework.http.HttpEntity("TestPatch"), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Patched 1!");
    }

    @SuppressWarnings("deprecation")
    @Test
    public void javascriptEncodedFormParams() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ArrayList<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.addAll(Arrays.asList(new StringHttpMessageConverter(), new NoEncodingFormHttpMessageConverter()));
        testRestTemplate.getRestTemplate().setMessageConverters(converters);
        MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap();
        map.add("foo", "(bar)");
        ResponseEntity<String> result = testRestTemplate.postForEntity((("http://localhost:" + (this.port)) + "/simple/local"), map, String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted [(bar)] and Content-Length was: 13!");
    }

    public abstract static class AbstractZuulProxyApplication extends DelegatingWebMvcConfiguration {
        private final Log LOG = LogFactory.getLog(RibbonRetryIntegrationTestBase.RetryableTestConfig.class);

        @RequestMapping(value = "/local/{id}", method = RequestMethod.PATCH)
        public String patch(@PathVariable
        final String id, @RequestBody
        final String body) {
            return ("Patched " + id) + "!";
        }

        @RequestMapping("/testing123")
        public String testing123() {
            throw new RuntimeException("myerror");
        }

        @RequestMapping("/local")
        public String local() {
            return "Hello local";
        }

        @RequestMapping(value = "/local", method = RequestMethod.POST)
        public String postWithFormParam(HttpServletRequest request, @RequestBody
        MultiValueMap<String, String> body) {
            return ((("Posted " + (body.get("foo"))) + " and Content-Length was: ") + (request.getContentLength())) + "!";
        }

        @RequestMapping(value = "/deletewithbody", method = RequestMethod.DELETE)
        public String deleteWithBody(@RequestBody(required = false)
        String body) {
            return "Deleted " + body;
        }

        @RequestMapping(value = "/local/{id}", method = RequestMethod.DELETE)
        public String delete(@PathVariable
        String id) {
            return ("Deleted " + id) + "!";
        }

        @RequestMapping(value = "/local/{id}", method = RequestMethod.GET)
        public ResponseEntity<?> get(@PathVariable
        String id) {
            if ("notfound".equalsIgnoreCase(id)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok((("Gotten " + id) + "!"));
        }

        @RequestMapping(value = "/local/{id}", method = RequestMethod.POST)
        public String post(@PathVariable
        String id, @RequestBody
        String body) {
            return ("Posted " + id) + "!";
        }

        @RequestMapping("/qstring")
        public String qstring(@RequestParam
        MultiValueMap<String, String> params) {
            return "Received " + (params.toString());
        }

        @RequestMapping("/")
        public String home() {
            return "Hello world";
        }

        @RequestMapping("/spa ce")
        public String space() {
            return "Hello space";
        }

        @RequestMapping("/slow")
        public String slow() {
            try {
                Thread.sleep(80000);
            } catch (InterruptedException e) {
                LOG.info(e);
                Thread.currentThread().interrupt();
            }
            return "slow";
        }

        @Bean
        public FallbackProvider fallbackProvider() {
            return new ZuulProxyTestBase.ZuulFallbackProvider();
        }

        @Bean
        public ZuulFilter sampleFilter() {
            return new ZuulFilter() {
                @Override
                public String filterType() {
                    return PRE_TYPE;
                }

                @Override
                public boolean shouldFilter() {
                    return true;
                }

                @Override
                public Object run() {
                    if (RequestContext.getCurrentContext().getRequest().getParameterMap().containsKey("override")) {
                        Map<String, List<String>> overridden = new HashMap<>();
                        overridden.put("key", Arrays.asList("overridden"));
                        RequestContext.getCurrentContext().setRequestQueryParams(overridden);
                    }
                    return null;
                }

                @Override
                public int filterOrder() {
                    return 0;
                }
            };
        }

        @Override
        public RequestMappingHandlerMapping requestMappingHandlerMapping() {
            RequestMappingHandlerMapping mapping = super.requestMappingHandlerMapping();
            mapping.setRemoveSemicolonContent(false);
            return mapping;
        }
    }

    public static class ZuulFallbackProvider implements FallbackProvider {
        @Override
        public String getRoute() {
            return "simple";
        }

        @Override
        public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
            return new ClientHttpResponse() {
                @Override
                public HttpStatus getStatusCode() throws IOException {
                    return HttpStatus.OK;
                }

                @Override
                public int getRawStatusCode() throws IOException {
                    return 200;
                }

                @Override
                public String getStatusText() throws IOException {
                    return null;
                }

                @Override
                public void close() {
                }

                @Override
                public InputStream getBody() throws IOException {
                    return new ByteArrayInputStream("fallback".getBytes());
                }

                @Override
                public HttpHeaders getHeaders() {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(TEXT_HTML);
                    return headers;
                }
            };
        }
    }

    @Configuration
    public class FormEncodedMessageConverterConfiguration extends WebMvcConfigurerAdapter {
        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            FormHttpMessageConverter converter = new FormHttpMessageConverter();
            MediaType mediaType = new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8"));
            converter.setSupportedMediaTypes(Arrays.asList(mediaType));
            converters.add(converter);
            super.configureMessageConverters(converters);
        }
    }

    // Load balancer with fixed server list for "simple" pointing to localhost
    @Configuration
    public static class SimpleRibbonClientConfiguration {
        @Value("${local.server.port}")
        private int port;

        @Bean
        public ServerList<Server> ribbonServerList() {
            return new org.springframework.cloud.netflix.ribbon.StaticServerList(new Server("localhost", this.port));
        }
    }

    @Configuration
    public static class AnotherRibbonClientConfiguration {
        @Value("${local.server.port}")
        private int port;

        @Bean
        public ServerList<Server> ribbonServerList() {
            return new org.springframework.cloud.netflix.ribbon.StaticServerList(new Server("localhost", this.port));
        }
    }

    public static class MyErrorController extends BasicErrorController {
        ThreadLocal<String> uriToMatch = new ThreadLocal<>();

        AtomicBoolean controllerUsed = new AtomicBoolean();

        public MyErrorController(ErrorAttributes errorAttributes) {
            super(errorAttributes, new ErrorProperties());
        }

        @Override
        public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
            String errorUri = ((String) (request.getAttribute("javax.servlet.error.request_uri")));
            if ((errorUri != null) && (errorUri.equals(this.uriToMatch.get()))) {
                controllerUsed.set(true);
            }
            this.uriToMatch.remove();
            return super.error(request);
        }

        public void setUriToMatch(String uri) {
            this.uriToMatch.set(uri);
        }

        public boolean wasControllerUsed() {
            return this.controllerUsed.get();
        }

        public void clear() {
            this.controllerUsed.set(false);
        }
    }
}

