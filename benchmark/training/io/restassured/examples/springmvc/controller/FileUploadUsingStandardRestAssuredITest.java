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
package io.restassured.examples.springmvc.controller;


import io.restassured.RestAssured;
import io.restassured.examples.springmvc.support.WithJetty;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileUploadUsingStandardRestAssuredITest extends WithJetty {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void file_uploading_works_using_standard_rest_assured() throws IOException {
        File something = folder.newFile("something");
        IOUtils.write("Something21", new FileOutputStream(something));
        RestAssured.given().multiPart(something).when().post("/fileUpload").then().body("size", Matchers.greaterThan(10)).body("name", Matchers.equalTo("file"));
    }

    @Test
    public void file_uploading_works_using_standard_rest_assured2() {
        RestAssured.given().multiPart("controlName", "fileName", new ByteArrayInputStream("Something21".getBytes())).when().post("/fileUpload2").then().body("size", Matchers.greaterThan(10)).body("name", Matchers.equalTo("controlName")).body("originalName", Matchers.equalTo("fileName"));
    }
}

