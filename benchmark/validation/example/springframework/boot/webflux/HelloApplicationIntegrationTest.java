package example.springframework.boot.webflux;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class HelloApplicationIntegrationTest {
    @LocalServerPort
    int port;

    private WebTestClient client;

    @Test
    public void helloWorld() {
        client.get().uri("/hello").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("Hello, World");
    }
}

