/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.test.web.client;


import HttpMethod.GET;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import org.apache.http.client.config.RequestConfig;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.web.client.TestRestTemplate.CustomHttpComponentsClientHttpRequestFactory;
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;


/**
 * Tests for {@link TestRestTemplate}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @author Kristine Jetzke
 */
public class TestRestTemplateTests {
    @Test
    public void fromRestTemplateBuilder() {
        RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class);
        RestTemplate delegate = new RestTemplate();
        BDDMockito.given(builder.build()).willReturn(delegate);
        assertThat(getRestTemplate()).isEqualTo(delegate);
    }

    @Test
    public void simple() {
        // The Apache client is on the classpath so we get the fully-fledged factory
        assertThat(new TestRestTemplate().getRestTemplate().getRequestFactory()).isInstanceOf(HttpComponentsClientHttpRequestFactory.class);
    }

    @Test
    public void doNotReplaceCustomRequestFactory() {
        RestTemplateBuilder builder = new RestTemplateBuilder().requestFactory(OkHttp3ClientHttpRequestFactory.class);
        TestRestTemplate testRestTemplate = new TestRestTemplate(builder);
        assertThat(testRestTemplate.getRestTemplate().getRequestFactory()).isInstanceOf(OkHttp3ClientHttpRequestFactory.class);
    }

    @Test
    public void getRootUriRootUriSetViaRestTemplateBuilder() {
        String rootUri = "http://example.com";
        RestTemplateBuilder delegate = new RestTemplateBuilder().rootUri(rootUri);
        assertThat(getRootUri()).isEqualTo(rootUri);
    }

    @Test
    public void getRootUriRootUriSetViaLocalHostUriTemplateHandler() {
        String rootUri = "http://example.com";
        TestRestTemplate template = new TestRestTemplate();
        LocalHostUriTemplateHandler templateHandler = Mockito.mock(LocalHostUriTemplateHandler.class);
        BDDMockito.given(templateHandler.getRootUri()).willReturn(rootUri);
        template.setUriTemplateHandler(templateHandler);
        assertThat(template.getRootUri()).isEqualTo(rootUri);
    }

    @Test
    public void getRootUriRootUriNotSet() {
        assertThat(new TestRestTemplate().getRootUri()).isEqualTo("");
    }

    @Test
    public void authenticated() {
        assertThat(new TestRestTemplate("user", "password").getRestTemplate().getRequestFactory()).isInstanceOf(InterceptingClientHttpRequestFactory.class);
    }

    @Test
    public void options() {
        TestRestTemplate template = new TestRestTemplate(HttpClientOption.ENABLE_REDIRECTS);
        CustomHttpComponentsClientHttpRequestFactory factory = ((CustomHttpComponentsClientHttpRequestFactory) (template.getRestTemplate().getRequestFactory()));
        RequestConfig config = factory.getRequestConfig();
        assertThat(config.isRedirectsEnabled()).isTrue();
    }

    @Test
    public void restOperationsAreAvailable() {
        RestTemplate delegate = Mockito.mock(RestTemplate.class);
        BDDMockito.given(delegate.getRequestFactory()).willReturn(new SimpleClientHttpRequestFactory());
        BDDMockito.given(delegate.getUriTemplateHandler()).willReturn(new DefaultUriBuilderFactory());
        RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class);
        BDDMockito.given(builder.build()).willReturn(delegate);
        TestRestTemplate restTemplate = new TestRestTemplate(builder);
        ReflectionUtils.doWithMethods(RestOperations.class, new MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException {
                Method equivalent = ReflectionUtils.findMethod(TestRestTemplate.class, method.getName(), method.getParameterTypes());
                assertThat(equivalent).as("Method %s not found", method).isNotNull();
                assertThat(Modifier.isPublic(equivalent.getModifiers())).as("Method %s should have been public", equivalent).isTrue();
                try {
                    equivalent.invoke(restTemplate, mockArguments(method.getParameterTypes()));
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }

            private Object[] mockArguments(Class<?>[] parameterTypes) throws Exception {
                Object[] arguments = new Object[parameterTypes.length];
                for (int i = 0; i < (parameterTypes.length); i++) {
                    arguments[i] = mockArgument(parameterTypes[i]);
                }
                return arguments;
            }

            @SuppressWarnings("rawtypes")
            private Object mockArgument(Class<?> type) throws Exception {
                if (String.class.equals(type)) {
                    return "String";
                }
                if (Object[].class.equals(type)) {
                    return new Object[0];
                }
                if (URI.class.equals(type)) {
                    return new URI("http://localhost");
                }
                if (HttpMethod.class.equals(type)) {
                    return HttpMethod.GET;
                }
                if (Class.class.equals(type)) {
                    return Object.class;
                }
                if (RequestEntity.class.equals(type)) {
                    return new RequestEntity(HttpMethod.GET, new URI("http://localhost"));
                }
                return Mockito.mock(type);
            }
        }, ( method) -> Modifier.isPublic(method.getModifiers()));
    }

    @Test
    public void withBasicAuthAddsBasicAuthInterceptorWhenNotAlreadyPresent() {
        TestRestTemplate originalTemplate = new TestRestTemplate();
        TestRestTemplate basicAuthTemplate = originalTemplate.withBasicAuth("user", "password");
        assertThat(basicAuthTemplate.getRestTemplate().getMessageConverters()).containsExactlyElementsOf(originalTemplate.getRestTemplate().getMessageConverters());
        assertThat(basicAuthTemplate.getRestTemplate().getRequestFactory()).isInstanceOf(InterceptingClientHttpRequestFactory.class);
        assertThat(ReflectionTestUtils.getField(basicAuthTemplate.getRestTemplate().getRequestFactory(), "requestFactory")).isInstanceOf(CustomHttpComponentsClientHttpRequestFactory.class);
        assertThat(basicAuthTemplate.getRestTemplate().getUriTemplateHandler()).isSameAs(originalTemplate.getRestTemplate().getUriTemplateHandler());
        assertThat(basicAuthTemplate.getRestTemplate().getInterceptors()).hasSize(1);
        assertBasicAuthorizationInterceptorCredentials(basicAuthTemplate, "user", "password");
    }

    @Test
    public void withBasicAuthReplacesBasicAuthInterceptorWhenAlreadyPresent() {
        TestRestTemplate original = new TestRestTemplate("foo", "bar").withBasicAuth("replace", "replace");
        TestRestTemplate basicAuth = original.withBasicAuth("user", "password");
        assertThat(basicAuth.getRestTemplate().getMessageConverters()).containsExactlyElementsOf(original.getRestTemplate().getMessageConverters());
        assertThat(basicAuth.getRestTemplate().getRequestFactory()).isInstanceOf(InterceptingClientHttpRequestFactory.class);
        assertThat(ReflectionTestUtils.getField(basicAuth.getRestTemplate().getRequestFactory(), "requestFactory")).isInstanceOf(CustomHttpComponentsClientHttpRequestFactory.class);
        assertThat(basicAuth.getRestTemplate().getUriTemplateHandler()).isSameAs(original.getRestTemplate().getUriTemplateHandler());
        assertThat(basicAuth.getRestTemplate().getInterceptors()).hasSize(1);
        assertBasicAuthorizationInterceptorCredentials(basicAuth, "user", "password");
    }

    @Test
    public void withBasicAuthDoesNotResetErrorHandler() {
        TestRestTemplate originalTemplate = new TestRestTemplate("foo", "bar");
        ResponseErrorHandler errorHandler = Mockito.mock(ResponseErrorHandler.class);
        originalTemplate.getRestTemplate().setErrorHandler(errorHandler);
        TestRestTemplate basicAuthTemplate = originalTemplate.withBasicAuth("user", "password");
        assertThat(basicAuthTemplate.getRestTemplate().getErrorHandler()).isSameAs(errorHandler);
    }

    @Test
    public void deleteHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(TestRestTemplate::delete);
    }

    @Test
    public void exchangeWithRequestEntityAndClassHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.exchange(new RequestEntity<String>(HttpMethod.GET, relativeUri), String.class));
    }

    @Test
    public void exchangeWithRequestEntityAndParameterizedTypeReferenceHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.exchange(new RequestEntity<String>(HttpMethod.GET, relativeUri), new org.springframework.core.ParameterizedTypeReference<String>() {}));
    }

    @Test
    public void exchangeHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.exchange(relativeUri, GET, new org.springframework.http.HttpEntity(new byte[0]), String.class));
    }

    @Test
    public void exchangeWithParameterizedTypeReferenceHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.exchange(relativeUri, GET, new org.springframework.http.HttpEntity(new byte[0]), new org.springframework.core.ParameterizedTypeReference<String>() {}));
    }

    @Test
    public void executeHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.execute(relativeUri, GET, null, null));
    }

    @Test
    public void getForEntityHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.getForEntity(relativeUri, String.class));
    }

    @Test
    public void getForObjectHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.getForObject(relativeUri, String.class));
    }

    @Test
    public void headForHeadersHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(TestRestTemplate::headForHeaders);
    }

    @Test
    public void optionsForAllowHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(TestRestTemplate::optionsForAllow);
    }

    @Test
    public void patchForObjectHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.patchForObject(relativeUri, "hello", String.class));
    }

    @Test
    public void postForEntityHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.postForEntity(relativeUri, "hello", String.class));
    }

    @Test
    public void postForLocationHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.postForLocation(relativeUri, "hello"));
    }

    @Test
    public void postForObjectHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.postForObject(relativeUri, "hello", String.class));
    }

    @Test
    public void putHandlesRelativeUris() throws IOException {
        verifyRelativeUriHandling(( testRestTemplate, relativeUri) -> testRestTemplate.put(relativeUri, "hello"));
    }

    private interface TestRestTemplateCallback {
        void doWithTestRestTemplate(TestRestTemplate testRestTemplate, URI relativeUri);
    }
}

