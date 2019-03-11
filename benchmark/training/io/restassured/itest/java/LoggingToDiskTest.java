/**
 * Copyright 2018 the original author or authors.
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
import io.restassured.itest.java.support.WriteLogsToDisk;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

import static io.restassured.itest.java.support.WithJetty.JettyOption.DONT_RESET_REST_ASSURED_BEFORE_TEST;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoggingToDiskTest extends WithJetty {
    public LoggingToDiskTest() {
        super(DONT_RESET_REST_ASSURED_BEFORE_TEST);
    }

    private static File directory;

    static {
        try {
            LoggingToDiskTest.directory = Files.createTempDirectory("ra").toFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Rule
    public TestName testName = new TestName();

    @Rule
    public WriteLogsToDisk writeLogsToDisk = new WriteLogsToDisk(LoggingToDiskTest.directory);

    @Test
    public void example1() {
        RestAssured.given().queryParam("firstName", "John").queryParam("lastName", "Doe").when().get("/greet").then().log().all().body("greeting", equalTo("Greetings John Doe"));
    }

    @Test
    public void example2() {
        RestAssured.given().queryParam("firstName", "Jane").queryParam("lastName", "Doe").when().get("/greet").then().log().all().body("greeting", equalTo("Greetings Jane Doe"));
    }

    // This test needs to be executed last, thus @FixMethodOrder
    @Test
    public void make_sure_logging_to_disk_works() throws Exception {
        List<LoggingToDiskTest.FileAndContents> files = Files.list(LoggingToDiskTest.directory.toPath()).filter(( file) -> !(file.endsWith(((testName.getMethodName()) + ".log")))).map(( file) -> {
            try {
                return LoggingToDiskTest.FileAndContents.of(file.getFileName().toString(), Files.readAllLines(file));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).collect(Collectors.toList());
        MatcherAssert.assertThat(files.toString(), files, hasSize(2));
        MatcherAssert.assertThat(files.get(0).fileName, is("example1.log"));
        MatcherAssert.assertThat(files.get(0).content.getLast(), is("{\"greeting\":\"Greetings John Doe\"}"));
        MatcherAssert.assertThat(files.get(1).fileName, is("example2.log"));
        MatcherAssert.assertThat(files.get(1).content.getLast(), is("{\"greeting\":\"Greetings Jane Doe\"}"));
    }

    private static class FileAndContents {
        final String fileName;

        final LinkedList<String> content;

        FileAndContents(String fileName, List<String> content) {
            this.fileName = fileName;
            this.content = new LinkedList<>(content);
        }

        private static LoggingToDiskTest.FileAndContents of(String fileName, List<String> content) {
            return new LoggingToDiskTest.FileAndContents(fileName, content);
        }

        @Override
        public String toString() {
            return (((((("FileAndContents{" + "fileName='") + (fileName)) + '\'') + ", content='") + (content)) + '\'') + '}';
        }
    }
}

