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
package io.helidon.examples.quickstart.se;


import io.helidon.webserver.WebServer;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MainTest {
    private static WebServer webServer;

    private static final JsonReaderFactory JSON = Json.createReaderFactory(Collections.emptyMap());

    @Test
    public void testHelloWorld() throws Exception {
        HttpURLConnection conn;
        conn = getURLConnection("GET", "/greet");
        Assertions.assertEquals(200, conn.getResponseCode(), "HTTP response1");
        JsonReader jsonReader = MainTest.JSON.createReader(conn.getInputStream());
        JsonObject jsonObject = jsonReader.readObject();
        Assertions.assertEquals("Hello World!", jsonObject.getString("message"), "default message");
        conn = getURLConnection("GET", "/greet/Joe");
        Assertions.assertEquals(200, conn.getResponseCode(), "HTTP response2");
        jsonReader = MainTest.JSON.createReader(conn.getInputStream());
        jsonObject = jsonReader.readObject();
        Assertions.assertEquals("Hello Joe!", jsonObject.getString("message"), "hello Joe message");
        conn = getURLConnection("PUT", "/greet/greeting");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write("{\"greeting\" : \"Hola\"}".getBytes());
        os.close();
        Assertions.assertEquals(204, conn.getResponseCode(), "HTTP response3");
        conn = getURLConnection("GET", "/greet/Jose");
        Assertions.assertEquals(200, conn.getResponseCode(), "HTTP response4");
        jsonReader = MainTest.JSON.createReader(conn.getInputStream());
        jsonObject = jsonReader.readObject();
        Assertions.assertEquals("Hola Jose!", jsonObject.getString("message"), "hola Jose message");
        conn = getURLConnection("GET", "/health");
        Assertions.assertEquals(200, conn.getResponseCode(), "HTTP response2");
        conn = getURLConnection("GET", "/metrics");
        Assertions.assertEquals(200, conn.getResponseCode(), "HTTP response2");
    }
}

