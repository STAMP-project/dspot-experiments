/**
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.tests.apps.bookstore.se;


import io.helidon.webserver.WebServer;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MainTest {
    private static WebServer webServer;

    @Test
    public void testHelloWorld() throws Exception {
        HttpURLConnection conn;
        String json = getBookAsJson();
        conn = getURLConnection("GET", "/books");
        Assertions.assertEquals(200, conn.getResponseCode(), "HTTP response1");
        conn = getURLConnection("POST", "/books");
        writeJsonContent(conn, json);
        Assertions.assertEquals(200, conn.getResponseCode(), "HTTP response POST");
        conn = getURLConnection("GET", "/books/123456");
        Assertions.assertEquals(200, conn.getResponseCode(), "HTTP response GET good ISBN");
        JsonReader jsonReader = Json.createReader(conn.getInputStream());
        JsonObject jsonObject = jsonReader.readObject();
        Assertions.assertEquals("123456", jsonObject.getString("isbn"), "Checking if correct ISBN");
        conn = getURLConnection("GET", "/books/0000");
        Assertions.assertEquals(404, conn.getResponseCode(), "HTTP response GET bad ISBN");
        conn = getURLConnection("GET", "/books");
        Assertions.assertEquals(200, conn.getResponseCode(), "HTTP response list books");
        conn = getURLConnection("DELETE", "/books/123456");
        Assertions.assertEquals(200, conn.getResponseCode(), "HTTP response delete book");
    }
}

