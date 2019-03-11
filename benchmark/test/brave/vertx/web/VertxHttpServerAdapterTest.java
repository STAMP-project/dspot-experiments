package brave.vertx.web;


import io.vertx.core.http.HttpServerResponse;
import org.junit.Test;


public class VertxHttpServerAdapterTest {
    VertxHttpServerAdapter adapter = new VertxHttpServerAdapter();

    @Test
    public void methodFromResponse() {
        HttpServerResponse response = dummyResponse();
        VertxHttpServerAdapter.setCurrentMethodAndPath("GET", null);
        assertThat(adapter.methodFromResponse(response)).isEqualTo("GET");
    }

    @Test
    public void route_emptyByDefault() {
        HttpServerResponse response = dummyResponse();
        VertxHttpServerAdapter.setCurrentMethodAndPath("GET", null);
        assertThat(adapter.route(response)).isEmpty();
    }

    @Test
    public void route() {
        HttpServerResponse response = dummyResponse();
        VertxHttpServerAdapter.setCurrentMethodAndPath("GET", "/users/:userID");
        assertThat(adapter.route(response)).isEqualTo("/users/:userID");
    }

    @Test
    public void setCurrentMethodAndPath_doesntPreventClassUnloading() {
        assertRunIsUnloadable(VertxHttpServerAdapterTest.MethodFromResponse.class, getClass().getClassLoader());
    }

    static class MethodFromResponse implements Runnable {
        final VertxHttpServerAdapter adapter = new VertxHttpServerAdapter();

        @Override
        public void run() {
            VertxHttpServerAdapter.setCurrentMethodAndPath("GET", null);
            adapter.methodFromResponse(null);
        }
    }
}

