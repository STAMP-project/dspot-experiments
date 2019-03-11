package example.springframework.boot.tomcat;


import HttpStatus.OK;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.server.Server;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ActiveProfiles("testbed")
@SpringBootTest(classes = { HelloConfiguration.class, HelloController.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
@EnableAutoConfiguration
public class HelloIntegrationTest {
    @Inject
    private Server server;

    private HttpClient client;

    @Test
    public void index() {
        final AggregatedHttpMessage res = client.get("/").aggregate().join();
        assertThat(res.status()).isEqualTo(OK);
        assertThat(res.contentUtf8()).isEqualTo("index");
    }

    @Test
    public void hello() throws Exception {
        final AggregatedHttpMessage res = client.get("/hello").aggregate().join();
        assertThat(res.status()).isEqualTo(OK);
        assertThat(res.contentUtf8()).isEqualTo("Hello, World");
    }

    @Test
    public void healthCheck() throws Exception {
        final AggregatedHttpMessage res = client.get("/internal/healthcheck").aggregate().join();
        assertThat(res.status()).isEqualTo(OK);
        assertThat(res.contentUtf8()).isEqualTo("ok");
    }
}

