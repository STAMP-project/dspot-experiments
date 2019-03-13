package example.armeria.server.files;


import HttpStatus.OK;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.server.Server;
import org.junit.Test;


public class MainTest {
    private static Server server;

    private static HttpClient client;

    @Test
    public void testFavicon() {
        // Download the favicon.
        final AggregatedHttpMessage res = MainTest.client.get("/favicon.ico").aggregate().join();
        assertThat(res.status()).isEqualTo(OK);
        assertThat(res.headers().contentType()).isEqualTo(MediaType.parse("image/x-icon"));
    }

    @Test
    public void testDirectoryListing() {
        // Download the directory listing.
        final AggregatedHttpMessage res = MainTest.client.get("/").aggregate().join();
        assertThat(res.status()).isEqualTo(OK);
        assertThat(res.content().toStringUtf8()).contains("Directory listing: /");
    }
}

