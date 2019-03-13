/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.module.mockmvc;


import ContentType.JSON;
import io.restassured.config.EncoderConfig;
import io.restassured.module.mockmvc.http.GreetingController;
import io.restassured.module.mockmvc.intercept.MockHttpServletRequestBuilderInterceptor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


public class ContentTypeTest {
    private static final String UTF_16 = "UTF-16";

    @Test
    public void adds_default_charset_to_content_type_by_default() {
        final AtomicReference<String> contentType = new AtomicReference<String>();
        RestAssuredMockMvc.given().standaloneSetup(new GreetingController()).contentType(JSON).interceptor(new MockHttpServletRequestBuilderInterceptor() {
            public void intercept(MockHttpServletRequestBuilder requestBuilder) {
                contentType.set(String.valueOf(Whitebox.getInternalState(requestBuilder, "contentType")));
            }
        }).when().get("/greeting?name={name}", "Johan").then().statusCode(200);
        assertThat(contentType.get()).isEqualTo(("application/json;charset=" + (RestAssuredMockMvc.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void adds_specific_charset_to_content_type_by_default() {
        final AtomicReference<String> contentType = new AtomicReference<String>();
        RestAssuredMockMvc.given().standaloneSetup(new GreetingController()).config(RestAssuredMockMvc.config().encoderConfig(EncoderConfig.encoderConfig().defaultCharsetForContentType(ContentTypeTest.UTF_16, JSON))).contentType(JSON).interceptor(new MockHttpServletRequestBuilderInterceptor() {
            public void intercept(MockHttpServletRequestBuilder requestBuilder) {
                contentType.set(String.valueOf(Whitebox.getInternalState(requestBuilder, "contentType")));
            }
        }).when().get("/greeting?name={name}", "Johan").then().statusCode(200);
        assertThat(contentType.get()).isEqualTo(("application/json;charset=" + (ContentTypeTest.UTF_16)));
        assertThat(contentType.get()).doesNotContain(RestAssuredMockMvc.config().getEncoderConfig().defaultContentCharset());
    }

    @Test
    public void doesnt_add_default_charset_to_content_type_if_charset_is_defined_explicitly() {
        final AtomicReference<String> contentType = new AtomicReference<String>();
        RestAssuredMockMvc.given().standaloneSetup(new GreetingController()).contentType(JSON.withCharset(ContentTypeTest.UTF_16)).interceptor(new MockHttpServletRequestBuilderInterceptor() {
            public void intercept(MockHttpServletRequestBuilder requestBuilder) {
                contentType.set(String.valueOf(Whitebox.getInternalState(requestBuilder, "contentType")));
            }
        }).when().get("/greeting?name={name}", "Johan").then().statusCode(200);
        assertThat(contentType.get()).isEqualTo("application/json;charset=UTF-16");
    }

    @Test
    public void doesnt_add_default_charset_to_content_type_if_configured_not_to_do_so() {
        final AtomicReference<String> contentType = new AtomicReference<String>();
        RestAssuredMockMvc.given().config(RestAssuredMockMvc.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).standaloneSetup(new GreetingController()).contentType(JSON).interceptor(new MockHttpServletRequestBuilderInterceptor() {
            public void intercept(MockHttpServletRequestBuilder requestBuilder) {
                contentType.set(String.valueOf(Whitebox.getInternalState(requestBuilder, "contentType")));
            }
        }).when().get("/greeting?name={name}", "Johan").then().statusCode(200);
        assertThat(contentType.get()).isEqualTo("application/json");
    }

    @Test
    public void doesnt_duplication_of_content_type_with_default_charset() {
        final List<String> contentTypes = new ArrayList<String>();
        RestAssuredMockMvc.given().standaloneSetup(new GreetingController()).contentType(JSON).interceptor(new MockHttpServletRequestBuilderInterceptor() {
            public void intercept(MockHttpServletRequestBuilder requestBuilder) {
                contentTypes.add(String.valueOf(Whitebox.getInternalState(requestBuilder, "contentType")));
            }
        }).when().get("/greeting?name={name}", "Johan").then().statusCode(200);
        assertThat(contentTypes.size()).isEqualTo(1);
        assertThat(contentTypes.get(0)).isEqualTo("application/json;charset=ISO-8859-1");
    }

    @Test
    public void doesnt_duplication_of_content_type() {
        final List<String> contentTypes = new ArrayList<String>();
        RestAssuredMockMvc.given().config(RestAssuredMockMvc.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).standaloneSetup(new GreetingController()).contentType(JSON).interceptor(new MockHttpServletRequestBuilderInterceptor() {
            public void intercept(MockHttpServletRequestBuilder requestBuilder) {
                contentTypes.add(String.valueOf(Whitebox.getInternalState(requestBuilder, "contentType")));
            }
        }).when().get("/greeting?name={name}", "Johan").then().statusCode(200);
        assertThat(contentTypes.size()).isEqualTo(1);
        assertThat(contentTypes.get(0)).isEqualTo("application/json");
    }
}

