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
package org.springframework.boot.web.embedded.undertow;


import Undertow.Builder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactoryTests;
import org.springframework.http.server.reactive.HttpHandler;


/**
 * Tests for {@link UndertowReactiveWebServerFactory} and {@link UndertowWebServer}.
 *
 * @author Brian Clozel
 * @author Madhura Bhave
 */
public class UndertowReactiveWebServerFactoryTests extends AbstractReactiveWebServerFactoryTests {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void setNullBuilderCustomizersShouldThrowException() {
        UndertowReactiveWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.setBuilderCustomizers(null)).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void addNullBuilderCustomizersShouldThrowException() {
        UndertowReactiveWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.addBuilderCustomizers(((UndertowBuilderCustomizer[]) (null)))).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void builderCustomizersShouldBeInvoked() {
        UndertowReactiveWebServerFactory factory = getFactory();
        HttpHandler handler = Mockito.mock(HttpHandler.class);
        UndertowBuilderCustomizer[] customizers = new UndertowBuilderCustomizer[4];
        Arrays.setAll(customizers, ( i) -> mock(.class));
        factory.setBuilderCustomizers(Arrays.asList(customizers[0], customizers[1]));
        factory.addBuilderCustomizers(customizers[2], customizers[3]);
        this.webServer = factory.getWebServer(handler);
        InOrder ordered = Mockito.inOrder(((Object[]) (customizers)));
        for (UndertowBuilderCustomizer customizer : customizers) {
            ordered.verify(customizer).customize(ArgumentMatchers.any(Builder.class));
        }
    }

    @Test
    public void useForwardedHeaders() {
        UndertowReactiveWebServerFactory factory = getFactory();
        factory.setUseForwardHeaders(true);
        assertForwardHeaderIsUsed(factory);
    }

    @Test
    public void accessLogCanBeEnabled() throws IOException, InterruptedException, URISyntaxException {
        testAccessLog(null, null, "access_log.log");
    }

    @Test
    public void accessLogCanBeCustomized() throws IOException, InterruptedException, URISyntaxException {
        testAccessLog("my_access.", "logz", "my_access.logz");
    }
}

