package com.example;


import java.nio.charset.Charset;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.IntegrationTest;
import org.rapidoid.http.Self;
import org.rapidoid.test.RapidoidIntegrationTest;
import org.rapidoid.u.U;


/**
 * This test will execute the main class specified in the annotation.
 */
@IntegrationTest(main = DslJsonExample.class)
public class DslJsonTest extends RapidoidIntegrationTest {
    @Test
    public void testHelloWorld() {
        // connects to the local server that was started by the Main class (http://localhost:8080)
        // then sends HTTP request GET /hello and parses the JSON result as Map
        Map<String, Object> resp = Self.get("/hello").toMap();
        eq(resp, U.map("msg", "Hello, world!"));
    }

    @Test
    public void testPost() {
        Map<String, Object> resp = Self.post("/compiled").body("{\"x\":3,\"s\":\"a\"}".getBytes(Charset.forName("UTF-8"))).toMap();
        eq(resp, U.map("x", 6, "s", "a"));
    }
}

