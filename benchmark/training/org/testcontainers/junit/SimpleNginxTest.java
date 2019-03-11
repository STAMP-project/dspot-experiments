package org.testcontainers.junit;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLConnection;
import lombok.Cleanup;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.NginxContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;


/**
 *
 *
 * @author richardnorth
 */
public class SimpleNginxTest {
    private static File contentFolder = new File(((System.getProperty("user.home")) + "/.tmp-test-container"));

    @Rule
    public NginxContainer nginx = new NginxContainer().withCustomContent(SimpleNginxTest.contentFolder.toString()).waitingFor(new HttpWaitStrategy());

    @Test
    public void testSimple() throws Exception {
        info(("Base URL is " + (nginx.getBaseUrl("http", 80))));
        URLConnection urlConnection = nginx.getBaseUrl("http", 80).openConnection();
        @Cleanup
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String line = reader.readLine();
        System.out.println(line);
        assertTrue("Using URLConnection, an HTTP GET from the nginx server returns the index.html from the custom content directory", line.contains("This worked"));
    }
}

