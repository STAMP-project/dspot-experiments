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
package io.restassured.itest.java;


import io.restassured.RestAssured;
import io.restassured.itest.java.support.WithJetty;
import org.hamcrest.Matchers;
import org.junit.Test;


public class DefaultParserITest extends WithJetty {
    @Test
    public void usingStaticDefaultParserParsersAnyContentWhenResponseContentTypeIsDefined() throws Exception {
        RestAssured.defaultParser = JSON;
        try {
            RestAssured.expect().body("message", Matchers.equalTo("It works")).when().get("/customMimeTypeJsonCompatible");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void usingDefaultParserParsersAnyContentWhenResponseContentTypeIsDefined() throws Exception {
        RestAssured.expect().defaultParser(JSON).and().body("message", Matchers.equalTo("It works")).when().get("/customMimeTypeJsonCompatible");
    }

    @Test
    public void usingStaticDefaultParserWhenResponseContentTypeIsUnDefined() throws Exception {
        RestAssured.defaultParser = JSON;
        try {
            RestAssured.expect().body("message", Matchers.equalTo("It works")).when().get("/noContentTypeJsonCompatible");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void usingDefaultParserWhenResponseContentTypeIsUnDefined() throws Exception {
        RestAssured.expect().defaultParser(JSON).and().body("message", Matchers.equalTo("It works")).when().get("/noContentTypeJsonCompatible");
    }
}

