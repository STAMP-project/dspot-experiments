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


import ContentType.BINARY;
import ContentType.JSON;
import ContentType.TEXT;
import ContentType.XML;
import io.restassured.RestAssured;
import io.restassured.itest.java.support.WithJetty;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileUploadingITest extends WithJetty {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void can_upload_json_from_file() throws IOException {
        // Given
        File file = folder.newFile("my.json");
        FileUtils.writeStringToFile(file, "{ \"message\" : \"hello world\"}");
        // When
        RestAssured.given().contentType(JSON).body(file).when().post("/jsonBody").then().body(Matchers.equalTo("hello world"));
    }

    @Test
    public void can_upload_xml_from_file() throws IOException {
        // Given
        File file = folder.newFile("my.xml");
        FileUtils.writeStringToFile(file, "<tag attr='value'>");
        // When
        RestAssured.given().contentType(XML).body(file).when().post("/validateContentTypeIsDefinedAndReturnBody").then().statusCode(200).contentType(XML).body(Matchers.equalTo("<tag attr='value'>"));
    }

    @Test
    public void can_upload_text_from_file() throws IOException {
        // Given
        File file = folder.newFile("my.txt");
        FileUtils.writeStringToFile(file, "Hello World");
        // When
        RestAssured.given().contentType(TEXT).body(file).when().post("/reflect").then().statusCode(200).body(Matchers.equalTo("Hello World"));
    }

    @Test
    public void can_upload_binary_from_file() throws IOException {
        // Given
        File file = folder.newFile("my.txt");
        FileUtils.writeStringToFile(file, "Hello World");
        // When
        RestAssured.given().contentType(BINARY).body(file).when().post("/reflect").then().statusCode(200).body(Matchers.equalTo("Hello World"));
    }

    @Test
    public void can_upload_file_with_custom_content_type() throws IOException {
        // Given
        File file = folder.newFile("my.txt");
        FileUtils.writeStringToFile(file, "Hello World");
        // When
        RestAssured.given().contentType("application/something").body(file).when().post("/reflect").then().statusCode(200).body(Matchers.equalTo("Hello World"));
    }
}

