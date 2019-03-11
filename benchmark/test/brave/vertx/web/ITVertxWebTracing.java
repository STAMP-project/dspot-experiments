package brave.vertx.web;


import brave.test.http.ITHttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import org.junit.Test;


public class ITVertxWebTracing extends ITHttpServer {
    Vertx vertx;

    HttpServer server;

    volatile int port;

    // makes sure we don't accidentally rewrite the incoming http path
    @Test
    public void handlesReroute() throws Exception {
        handlesReroute("/reroute");
    }

    @Test
    public void handlesRerouteAsync() throws Exception {
        handlesReroute("/rerouteAsync");
    }

    @Override
    @Test
    public void httpRoute_nested() throws Exception {
        // Can't currently fully resolve the route template of a sub-router
        // We get "/nested" not "/nested/items/:itemId
        // https://groups.google.com/forum/?fromgroups#!topic/vertx/FtF2yVr5ZF8
        try {
            super.httpRoute_nested();
            failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (AssertionError e) {
            assertThat(e.getMessage().contains("nested"));
        }
    }
}

