/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.transports.http.transport.netty;


import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.transports.http.transport.HttpTransport;
import org.kaaproject.kaa.server.transports.http.transport.commands.SyncCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * NettyHttpServerIT Class to test Netty HTTP Server, using serious of http
 * requests to check validity of transmission.
 *
 * @author Andrey Panasenko <apanasenko@cybervisiontech.com>
 */
public class NettyHttpServerIT {
    /**
     * Port which used to bind to for Netty HTTP
     */
    public static final String TEST_HOST = "localhost";

    /**
     * Port which used to bind to for Netty HTTP
     */
    public static final int TEST_PORT = 9193;

    /**
     * Max HTTP request size which used in Netty framework
     */
    public static final int MAX_HTTP_REQUEST_SIZE = 65536;

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NettyHttpServerIT.class);

    private static ExecutorService executor = null;

    private static HttpTransport netty;

    /**
     * Test on incorrect URL
     */
    @Test
    public void testIncorrectRequest() {
        NettyHttpServerIT.LOG.info("Test Incorrect request");
        PostParameters params = new PostParameters();
        // Incorrect command name
        String commandName = "test";
        try {
            final HttpTestClient client = new HttpTestClient(params, new HttpActivity() {
                @Override
                public void httpRequestComplete(IOException ioe, Map<String, List<String>> header, String body) {
                    Assert.assertNotNull(ioe);
                    NettyHttpServerIT.LOG.info("Test complete, Error 500 got.");
                }
            }, commandName);
            NettyHttpServerIT.executor.execute(client);
            Thread.sleep(100);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }

    /**
     * Test on incorrect HTTP method
     */
    @Test
    public void testIncorrectMethod() {
        NettyHttpServerIT.LOG.info("Test Incorrect Method request");
        try {
            URLConnection connection = new URL(((((("http://" + (NettyHttpServerIT.TEST_HOST)) + ":") + (NettyHttpServerIT.TEST_PORT)) + "/domain/") + (SyncCommand.getCommandName()))).openConnection();
            StringBuffer b = new StringBuffer();
            InputStreamReader r = new InputStreamReader(connection.getInputStream(), "UTF-8");
            int c;
            while ((c = r.read()) != (-1)) {
                b.append(((char) (c)));
            } 
            Assert.fail("Exception not cauth");
        } catch (IOException e) {
            Assert.assertNotNull(e);
            if (!(e.toString().contains("HTTP response code: 400 for URL"))) {
                Assert.fail(e.toString());
            } else {
                NettyHttpServerIT.LOG.info("Test for incorrect method pass");
            }
        }
    }
}

