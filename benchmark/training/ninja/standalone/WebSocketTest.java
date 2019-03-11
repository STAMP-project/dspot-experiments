/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.standalone;


import WebSocketCloseCode.NORMAL;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class WebSocketTest {
    @Test
    public void websocketsEnabled() throws Exception {
        // this test is not really specific to jetty, but its easier to test here
        NinjaJetty standalone = new NinjaJetty().externalConfigurationPath("conf/jetty.com.session.conf").port(NinjaJettyTest.RANDOM_PORT);
        try {
            standalone.start();
            String url = ("ws://localhost:" + (NinjaJettyTest.RANDOM_PORT)) + "/example";
            WebSocket ws = new WebSocketFactory().createSocket(url);
            try {
                ws.connect();
                Assert.assertThat(ws.isOpen(), CoreMatchers.is(true));
            } finally {
                ws.disconnect(NORMAL);
            }
        } finally {
            standalone.shutdown();
        }
    }
}

