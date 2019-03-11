package org.robolectric.shadows.httpclient;


import RuntimeEnvironment.application;
import android.net.http.AndroidHttpClient;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.TestRunnerWithManifest;


@RunWith(TestRunnerWithManifest.class)
public class AndroidHttpClientTest {
    @Test
    public void testNewInstance() throws Exception {
        AndroidHttpClient client = AndroidHttpClient.newInstance("foo");
        assertThat(client).isNotNull();
    }

    @Test
    public void testNewInstanceWithContext() throws Exception {
        AndroidHttpClient client = AndroidHttpClient.newInstance("foo", application);
        assertThat(client).isNotNull();
    }

    @Test
    public void testExecute() throws IOException {
        AndroidHttpClient client = AndroidHttpClient.newInstance("foo");
        FakeHttp.addPendingHttpResponse(200, "foo");
        HttpResponse resp = client.execute(new HttpGet("/foo"));
        assertThat(resp.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(CharStreams.toString(new InputStreamReader(resp.getEntity().getContent(), StandardCharsets.UTF_8))).isEqualTo("foo");
    }
}

