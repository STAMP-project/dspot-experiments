package com.intuit.karate.junit4.http;


import com.intuit.karate.Http;
import com.intuit.karate.netty.FeatureServer;
import org.junit.Test;


/**
 *
 *
 * @author pthomas3
 */
public class JavaHttpTest {
    private static FeatureServer server;

    @Test
    public void testHttp() {
        Http http = Http.forUrl(null, ("http://localhost:" + (JavaHttpTest.server.getPort())));
        http.path("echo").get().response().equals("{ uri: '/echo' }");
        String expected = "ws://127.0.0.1:9222/devtools/page/E54102F8004590484CC9FF85E2ECFCD0";
        http.path("chrome").get().response().equalsText("#[1]").jsonPath("get[0] $..webSocketDebuggerUrl").equalsText(expected);
    }
}

