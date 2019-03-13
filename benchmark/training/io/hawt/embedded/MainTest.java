package io.hawt.embedded;


import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MainTest {
    @Test
    public void run() throws Exception {
        System.setProperty("hawtio.authenticationEnabled", "false");
        int port = 12345;
        Main main = new Main();
        main.setWarLocation("../hawtio-default/target/");
        main.setPort(port);
        main.run();
        HttpResponse response = Request.Get(String.format("http://localhost:%s/hawtio/jolokia/version", port)).execute().returnResponse();
        Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.equalTo(200));
    }
}

