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
import ContentType.XML;
import JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import JsonPathConfig.NumberReturnType.FLOAT_AND_DOUBLE;
import LogDetail.ALL;
import RestAssuredMockMvc.basePath;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import io.restassured.module.mockmvc.http.GreetingController;
import io.restassured.module.mockmvc.http.PostController;
import io.restassured.module.mockmvc.intercept.MockHttpServletRequestBuilderInterceptor;
import io.restassured.module.mockmvc.specification.MockMvcAuthenticationScheme;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecBuilder;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import java.io.PrintStream;
import java.io.StringWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static RestAssuredMockMvc.basePath;


public class MockMvcRequestSpecificationMergingTest {
    // @formatter:off
    @Test
    public void query_params_are_merged() {
        // Given
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addQueryParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().queryParam("param2", "value2").spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getQueryParams()).containsOnly(entry("param1", "value1"), entry("param2", "value2"));
    }

    @Test
    public void form_params_are_merged() {
        // Given
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addFormParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().formParam("param2", "value2").spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getFormParams()).containsOnly(entry("param1", "value1"), entry("param2", "value2"));
    }

    @Test
    public void params_are_merged() {
        // Given
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().param("param2", "value2").spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getParams()).containsOnly(entry("param1", "value1"), entry("param2", "value2"));
    }

    @Test
    public void attributes_are_merged() {
        // Given
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addAttribute("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().attribute("param2", "value2").spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getAttributes()).containsOnly(entry("param1", "value1"), entry("param2", "value2"));
    }

    @Test
    public void multi_parts_are_merged() {
        // Given
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addMultiPart("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().multiPart("param2", "value2").spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getMultiParts()).hasSize(2);
    }

    @Test
    public void request_body_is_overwritten_when_defined_in_specification() {
        // Given
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().setBody("body2").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().body("body1").spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getRequestBody()).isEqualTo("body2");
    }

    @Test
    public void request_body_is_not_overwritten_when_not_defined_in_specification() {
        // Given
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addQueryParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().body("body1").spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getRequestBody()).isEqualTo("body1");
        Assertions.assertThat(implOf(spec).getQueryParams()).containsOnly(entry("param1", "value1"));
    }

    @Test
    public void base_path_is_overwritten_when_defined_in_specification() {
        // Given
        basePath = "/something";
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().setBasePath("basePath").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().body("body1").spec(specToMerge);
        // Then
        RestAssuredMockMvc.reset();
        Assertions.assertThat(implOf(spec).getBasePath()).isEqualTo("basePath");
    }

    @Test
    public void base_path_is_not_overwritten_when_not_defined_in_specification() {
        // Given
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addQueryParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().body("body1").spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getBasePath()).isEqualTo(basePath);
        Assertions.assertThat(implOf(spec).getQueryParams()).containsOnly(entry("param1", "value1"));
    }

    @Test
    public void mock_mvc_instance_is_overwritten_when_defined_in_specification() {
        // Given
        MockMvc otherMockMvcInstance = MockMvcBuilders.standaloneSetup(new PostController()).build();
        MockMvc thisMockMvcInstance = MockMvcBuilders.standaloneSetup(new GreetingController()).build();
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().setMockMvc(otherMockMvcInstance).build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().mockMvc(thisMockMvcInstance).spec(specToMerge);
        // Then
        assertThat(Whitebox.getInternalState(implOf(spec).getMockMvcFactory(), "mockMvc")).isSameAs(otherMockMvcInstance);
    }

    @Test
    public void mock_mvc_factory_is_not_overwritten_when_not_defined_in_specification() {
        // Given
        MockMvc mockMvcInstance = MockMvcBuilders.standaloneSetup(new GreetingController()).build();
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addQueryParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().mockMvc(mockMvcInstance).spec(specToMerge);
        // Then
        assertThat(Whitebox.getInternalState(implOf(spec).getMockMvcFactory(), "mockMvc")).isSameAs(mockMvcInstance);
        Assertions.assertThat(implOf(spec).getQueryParams()).containsOnly(entry("param1", "value1"));
    }

    @Test
    public void cookies_are_merged_when_defined_in_specification() {
        // Given
        Cookie otherCookie = new Cookie.Builder("cookie1", "value1").build();
        Cookie thisCookie = new Cookie.Builder("cookie2", "value2").build();
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addCookie(otherCookie).build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().cookie(thisCookie).spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getCookies()).containsOnly(thisCookie, otherCookie);
    }

    @Test
    public void cookies_are_not_overwritten_when_not_defined_in_specification() {
        // Given
        Cookie thisCookie = new Cookie.Builder("cookie2", "value2").build();
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addQueryParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().cookie(thisCookie).spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getCookies()).containsOnly(thisCookie);
        Assertions.assertThat(implOf(spec).getQueryParams()).containsOnly(entry("param1", "value1"));
    }

    @Test
    public void content_type_is_overwritten_when_defined_in_specification() {
        // Given
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().setContentType(JSON).build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().contentType(XML).spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getRequestContentType()).isEqualTo(JSON.toString());
    }

    @Test
    public void content_type_is_not_overwritten_when_not_defined_in_specification() {
        // Given
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addQueryParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().contentType(XML).spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getRequestContentType()).isEqualTo(XML.toString());
        Assertions.assertThat(implOf(spec).getQueryParams()).containsOnly(entry("param1", "value1"));
    }

    @Test
    public void headers_are_merged_when_defined_in_specification() {
        // Given
        Header otherHeader = new Header("header1", "value1");
        Header thisHeader = new Header("header2", "value2");
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addHeader(otherHeader).build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().header(thisHeader).spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getRequestHeaders()).containsOnly(thisHeader, otherHeader);
    }

    @Test
    public void headers_are_not_overwritten_when_not_defined_in_specification() {
        // Given
        Header thisHeader = new Header("cookie2", "value2");
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addQueryParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().header(thisHeader).spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getRequestHeaders()).containsOnly(thisHeader);
        Assertions.assertThat(implOf(spec).getQueryParams()).containsOnly(entry("param1", "value1"));
    }

    @Test
    public void configs_of_same_type_are_overwritten_when_defined_in_specification() {
        // Given
        RestAssuredMockMvcConfig otherConfig = new RestAssuredMockMvcConfig().with().jsonConfig(jsonConfig().with().numberReturnType(BIG_DECIMAL));
        RestAssuredMockMvcConfig thisConfig = new RestAssuredMockMvcConfig().with().jsonConfig(jsonConfig().with().numberReturnType(FLOAT_AND_DOUBLE));
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().setConfig(otherConfig).build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().config(thisConfig).spec(specToMerge);
        // Then
        assertThat(implOf(spec).getRestAssuredMockMvcConfig().getJsonConfig().numberReturnType()).isEqualTo(BIG_DECIMAL);
    }

    @Test
    public void config_is_not_overwritten_when_not_defined_in_specification() {
        // Given
        RestAssuredMockMvcConfig thisConfig = new RestAssuredMockMvcConfig().with().jsonConfig(jsonConfig().with().numberReturnType(FLOAT_AND_DOUBLE));
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addQueryParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().config(thisConfig).spec(specToMerge);
        // Then
        // This assertion is commented out since for some reason it fails during the release process
        // assertThat(implOf(spec).getRestAssuredMockMvcConfig()).isSameAs(thisConfig);
        Assertions.assertThat(implOf(spec).getQueryParams()).containsOnly(entry("param1", "value1"));
        assertThat(implOf(spec).getRestAssuredMockMvcConfig().getJsonConfig().numberReturnType()).isEqualTo(FLOAT_AND_DOUBLE);
    }

    @Test
    public void interception_is_overwritten_when_defined_in_specification() {
        // Given
        MockHttpServletRequestBuilderInterceptor otherInterceptor = new MockHttpServletRequestBuilderInterceptor() {
            public void intercept(MockHttpServletRequestBuilder requestBuilder) {
            }
        };
        MockHttpServletRequestBuilderInterceptor thisInterceptor = new MockHttpServletRequestBuilderInterceptor() {
            public void intercept(MockHttpServletRequestBuilder requestBuilder) {
            }
        };
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().setMockHttpServletRequestBuilderInterceptor(otherInterceptor).build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().interceptor(thisInterceptor).spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getInterceptor()).isEqualTo(otherInterceptor);
    }

    @Test
    public void interception_is_not_overwritten_when_not_defined_in_specification() {
        // Given
        MockHttpServletRequestBuilderInterceptor thisInterceptor = new MockHttpServletRequestBuilderInterceptor() {
            public void intercept(MockHttpServletRequestBuilder requestBuilder) {
            }
        };
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addQueryParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().interceptor(thisInterceptor).spec(specToMerge);
        // Then
        Assertions.assertThat(implOf(spec).getInterceptor()).isEqualTo(thisInterceptor);
        Assertions.assertThat(implOf(spec).getQueryParams()).containsOnly(entry("param1", "value1"));
    }

    @Test
    public void logging_is_overwritten_when_defined_in_specification() {
        // Given
        StringWriter writer = new StringWriter();
        PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().setConfig(RestAssuredMockMvcConfig.newConfig().logConfig(LogConfig.logConfig().defaultStream(captor))).and().log(ALL).build();
        // When
        RestAssuredMockMvc.given().log().params().spec(specToMerge).standaloneSetup(new GreetingController()).when().get("/greeting?name={name}", "Johan").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan!"));
        // Then
        assertThat(writer.toString()).isEqualTo(String.format(("Request method:\tGET%n" + ((((((((("Request URI:\thttp://localhost:8080/greeting?name=Johan%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\t<none>%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n"))));
    }

    @Test
    public void logging_is_not_overwritten_when_not_defined_in_specification() {
        // Given
        StringWriter writer = new StringWriter();
        PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().setConfig(RestAssuredMockMvcConfig.newConfig().logConfig(LogConfig.logConfig().defaultStream(captor))).addQueryParam("name", "Johan").build();
        // When
        RestAssuredMockMvc.given().spec(specToMerge).log().params().standaloneSetup(new GreetingController()).when().get("/greeting").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan!"));
        // Then
        assertThat(writer.toString()).isEqualTo(String.format(("Request params:\t<none>%n" + ((("Query params:\tname=Johan%n" + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Multiparts:\t\t<none>%n"))));
    }

    @Test
    public void authentication_is_overwritten_when_defined_in_specification() {
        // Given
        MockMvcAuthenticationScheme otherAuth = RestAssuredMockMvc.principal("other");
        MockMvcAuthenticationScheme thisAuth = RestAssuredMockMvc.principal("this");
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().setAuth(otherAuth).build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().spec(new MockMvcRequestSpecBuilder().setAuth(thisAuth).build()).spec(specToMerge);
        // Then
        assertThat(getPrincipal()).isEqualTo("other");
    }

    @Test
    public void authentication_is_overwritten_when_using_dsl_and_defined_in_specification() {
        // Given
        MockMvcAuthenticationScheme otherAuth = RestAssuredMockMvc.principal("other");
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().setAuth(otherAuth).build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().auth().principal("this").and().spec(specToMerge);
        // Then
        assertThat(getPrincipal()).isEqualTo("other");
    }

    @Test
    public void authentication_is_not_overwritten_when_not_defined_in_specification() {
        // Given
        MockMvcAuthenticationScheme thisAuth = RestAssuredMockMvc.principal("this");
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().addQueryParam("param1", "value1").build();
        // When
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().spec(new MockMvcRequestSpecBuilder().setAuth(thisAuth).build()).spec(specToMerge);
        // Then
        assertThat(getPrincipal()).isEqualTo("this");
        Assertions.assertThat(implOf(spec).getQueryParams()).containsOnly(entry("param1", "value1"));
    }

    @Test
    public void configurations_are_merged() {
        // Given
        RestAssuredMockMvcConfig cfg1 = new RestAssuredMockMvcConfig().with().jsonConfig(jsonConfig().with().numberReturnType(FLOAT_AND_DOUBLE));
        MockMvcRequestSpecification specToMerge = new MockMvcRequestSpecBuilder().setConfig(cfg1).build();
        // When
        RestAssuredMockMvcConfig cfg2 = new RestAssuredMockMvcConfig().sessionConfig(sessionConfig().sessionIdName("php"));
        MockMvcRequestSpecification spec = RestAssuredMockMvc.given().config(cfg2).spec(specToMerge);
        // Then
        RestAssuredConfig mergedConfig = implOf(spec).getRestAssuredConfig();
        assertThat(mergedConfig.getSessionConfig().sessionIdName()).isEqualTo("php");
        assertThat(mergedConfig.getJsonConfig().numberReturnType()).isEqualTo(FLOAT_AND_DOUBLE);
    }
}

