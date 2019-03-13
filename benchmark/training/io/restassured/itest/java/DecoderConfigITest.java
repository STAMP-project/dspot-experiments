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


import ContentType.JSON;
import ContentType.TEXT;
import DecoderConfig.ContentDecoder.GZIP;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseBuilder;
import io.restassured.config.DecoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.itest.java.support.WithJetty;
import java.nio.charset.StandardCharsets;
import org.junit.Test;


public class DecoderConfigITest extends WithJetty {
    @Test
    public void allows_specifying_content_decoders_statically() {
        // Given
        RestAssured.config = RestAssuredConfig.newConfig().decoderConfig(DecoderConfig.decoderConfig().contentDecoders(GZIP));
        try {
            expect().body("Accept-Encoding", hasItem("gzip")).body("Accept-Encoding", hasSize(1)).when().get("/headersWithValues");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void allows_specifying_content_decoders_per_request() {
        expect().body("Accept-Encoding", contains("gzip")).when().get("/headersWithValues");
    }

    @Test
    public void uses_gzip_and_deflate_content_decoders_by_default() {
        expect().body("Accept-Encoding", contains("gzip,deflate")).when().get("/headersWithValues");
    }

    @Test
    public void decoder_config_can_be_configured_to_use_no_decoders() {
        String hasAcceptEncodingParameter = "$.any { it.containsKey('Accept-Encoding') }";
        expect().body(hasAcceptEncodingParameter, is(false)).when().get("/headersWithValues");
    }

    @Test
    public void filters_can_change_content_decoders() {
        expect().body("Accept-Encoding", contains("deflate")).when().get("/headersWithValues");
    }

    @Test
    public void json_content_type_is_configured_to_use_utf_8_by_default() {
        given().config(RestAssuredConfig.newConfig().decoderConfig(DecoderConfig.decoderConfig().defaultContentCharset(StandardCharsets.UTF_16))).when().get("/lotto").then().header("Content-Type", equalTo(("application/json;charset=" + (DecoderConfig.decoderConfig().defaultCharsetForContentType(JSON).toLowerCase()))));
    }

    @Test
    public void can_specify_default_charset_for_a_specific_content_type() {
        given().config(RestAssuredConfig.newConfig().decoderConfig(DecoderConfig.decoderConfig().defaultCharsetForContentType(StandardCharsets.UTF_16, TEXT))).filter(( requestSpec, responseSpec, ctx) -> new ResponseBuilder().setStatusCode(200).setBody("something\ud824\udd22").setContentType("text/plain").build()).when().get("/non-existing").then().body(equalTo("something\ud824\udd22"));
    }
}

