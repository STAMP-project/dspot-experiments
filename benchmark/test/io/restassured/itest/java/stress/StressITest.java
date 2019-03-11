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
package io.restassured.itest.java.stress;


import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.Component;

import static io.restassured.RestAssured.given;


public class StressITest {
    Component component;

    static final int wait = 60 * 1000;

    int iterations = 30;

    String post = "TEST";

    String expect = "HANG-TEST";

    String url = null;

    @Test(timeout = StressITest.wait)
    public void stressWithRestAssuredGet() throws UnsupportedEncodingException {
        for (int i = 0, n = iterations; i < n; i++) {
            given().expect().body(Matchers.equalTo(expect)).when().get(url);
        }
    }

    @Test(timeout = StressITest.wait)
    public void stressWithRestAssuredPost() throws UnsupportedEncodingException {
        for (int i = 0, n = iterations; i < n; i++) {
            given().contentType("text/plain;charset=utf-8").body(post.getBytes("UTF-8")).expect().body(Matchers.equalTo(expect)).when().post(url);
        }
    }

    @Test(timeout = StressITest.wait)
    public void stressWithRestAssuredPostWhenSameHttpClientInstanceIsReused() throws UnsupportedEncodingException {
        RestAssured.config = RestAssuredConfig.newConfig().httpClient(HttpClientConfig.httpClientConfig().reuseHttpClientInstance());
        try {
            for (int i = 0, n = iterations; i < n; i++) {
                given().contentType("text/plain;charset=utf-8").body(post.getBytes("UTF-8")).expect().body(Matchers.equalTo(expect)).when().post(url);
            }
        } finally {
            RestAssured.reset();
        }
    }

    @Test(timeout = StressITest.wait)
    public void stressWithRestAssuredGetManualClose() throws IOException, InterruptedException {
        RestAssured.config = RestAssuredConfig.newConfig().httpClient(HttpClientConfig.httpClientConfig().reuseHttpClientInstance());
        try {
            for (int i = 0, n = iterations; i < n; i++) {
                String body = IOUtils.toString(RestAssured.get(url).andReturn().body().asInputStream());
                Assert.assertEquals(expect, body);
            }
        } finally {
            RestAssured.reset();
        }
    }
}

