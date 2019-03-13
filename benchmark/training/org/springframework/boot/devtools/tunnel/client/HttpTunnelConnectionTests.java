/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.devtools.tunnel.client;


import HttpStatus.GONE;
import HttpStatus.NO_CONTENT;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.net.ConnectException;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Executor;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.devtools.test.MockClientHttpRequestFactory;
import org.springframework.boot.devtools.tunnel.client.HttpTunnelConnection.TunnelChannel;
import org.springframework.boot.test.rule.OutputCapture;


/**
 * Tests for {@link HttpTunnelConnection}.
 *
 * @author Phillip Webb
 * @author Rob Winch
 * @author Andy Wilkinson
 */
public class HttpTunnelConnectionTests {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    private String url;

    private ByteArrayOutputStream incomingData;

    private WritableByteChannel incomingChannel;

    @Mock
    private Closeable closeable;

    private MockClientHttpRequestFactory requestFactory = new MockClientHttpRequestFactory();

    @Test
    public void urlMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpTunnelConnection(null, this.requestFactory)).withMessageContaining("URL must not be empty");
    }

    @Test
    public void urlMustNotBeEmpty() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpTunnelConnection("", this.requestFactory)).withMessageContaining("URL must not be empty");
    }

    @Test
    public void urlMustNotBeMalformed() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpTunnelConnection("htttttp:///ttest", this.requestFactory)).withMessageContaining("Malformed URL 'htttttp:///ttest'");
    }

    @Test
    public void requestFactoryMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpTunnelConnection(this.url, null)).withMessageContaining("RequestFactory must not be null");
    }

    @Test
    public void closeTunnelChangesIsOpen() throws Exception {
        this.requestFactory.willRespondAfterDelay(1000, GONE);
        WritableByteChannel channel = openTunnel(false);
        assertThat(channel.isOpen()).isTrue();
        channel.close();
        assertThat(channel.isOpen()).isFalse();
    }

    @Test
    public void closeTunnelCallsCloseableOnce() throws Exception {
        this.requestFactory.willRespondAfterDelay(1000, GONE);
        WritableByteChannel channel = openTunnel(false);
        Mockito.verify(this.closeable, Mockito.never()).close();
        channel.close();
        channel.close();
        Mockito.verify(this.closeable, Mockito.times(1)).close();
    }

    @Test
    public void typicalTraffic() throws Exception {
        this.requestFactory.willRespond("hi", "=2", "=3");
        TunnelChannel channel = openTunnel(true);
        write(channel, "hello");
        write(channel, "1+1");
        write(channel, "1+2");
        assertThat(this.incomingData.toString()).isEqualTo("hi=2=3");
    }

    @Test
    public void trafficWithLongPollTimeouts() throws Exception {
        for (int i = 0; i < 10; i++) {
            this.requestFactory.willRespond(NO_CONTENT);
        }
        this.requestFactory.willRespond("hi");
        TunnelChannel channel = openTunnel(true);
        write(channel, "hello");
        assertThat(this.incomingData.toString()).isEqualTo("hi");
        assertThat(this.requestFactory.getExecutedRequests().size()).isGreaterThan(10);
    }

    @Test
    public void connectFailureLogsWarning() throws Exception {
        this.requestFactory.willRespond(new ConnectException());
        TunnelChannel tunnel = openTunnel(true);
        assertThat(tunnel.isOpen()).isFalse();
        this.outputCapture.expect(Matchers.containsString("Failed to connect to remote application at http://localhost:12345"));
    }

    private static class CurrentThreadExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}

