/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.web.embedded.tomcat;


import java.util.Arrays;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.valves.RemoteIpValve;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactoryTests;
import org.springframework.http.server.reactive.HttpHandler;


/**
 * Tests for {@link TomcatReactiveWebServerFactory}.
 *
 * @author Brian Clozel
 * @author Madhura Bhave
 */
public class TomcatReactiveWebServerFactoryTests extends AbstractReactiveWebServerFactoryTests {
    @Test
    public void tomcatCustomizers() {
        TomcatReactiveWebServerFactory factory = getFactory();
        TomcatContextCustomizer[] listeners = new TomcatContextCustomizer[4];
        Arrays.setAll(listeners, ( i) -> mock(.class));
        factory.setTomcatContextCustomizers(Arrays.asList(listeners[0], listeners[1]));
        factory.addContextCustomizers(listeners[2], listeners[3]);
        this.webServer = factory.getWebServer(Mockito.mock(HttpHandler.class));
        InOrder ordered = Mockito.inOrder(((Object[]) (listeners)));
        for (TomcatContextCustomizer listener : listeners) {
            ordered.verify(listener).customize(ArgumentMatchers.any(Context.class));
        }
    }

    @Test
    public void contextIsAddedToHostBeforeCustomizersAreCalled() {
        TomcatReactiveWebServerFactory factory = getFactory();
        TomcatContextCustomizer customizer = Mockito.mock(TomcatContextCustomizer.class);
        factory.addContextCustomizers(customizer);
        this.webServer = factory.getWebServer(Mockito.mock(HttpHandler.class));
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        Mockito.verify(customizer).customize(contextCaptor.capture());
        assertThat(contextCaptor.getValue().getParent()).isNotNull();
    }

    @Test
    public void defaultTomcatListeners() {
        TomcatReactiveWebServerFactory factory = getFactory();
        assertThat(factory.getContextLifecycleListeners()).hasSize(1).first().isInstanceOf(AprLifecycleListener.class);
    }

    @Test
    public void tomcatListeners() {
        TomcatReactiveWebServerFactory factory = getFactory();
        LifecycleListener[] listeners = new LifecycleListener[4];
        Arrays.setAll(listeners, ( i) -> mock(.class));
        factory.setContextLifecycleListeners(Arrays.asList(listeners[0], listeners[1]));
        factory.addContextLifecycleListeners(listeners[2], listeners[3]);
        this.webServer = factory.getWebServer(Mockito.mock(HttpHandler.class));
        InOrder ordered = Mockito.inOrder(((Object[]) (listeners)));
        for (LifecycleListener listener : listeners) {
            ordered.verify(listener).lifecycleEvent(ArgumentMatchers.any(LifecycleEvent.class));
        }
    }

    @Test
    public void setNullConnectorCustomizersShouldThrowException() {
        TomcatReactiveWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.setTomcatConnectorCustomizers(null)).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void addNullAddConnectorCustomizersShouldThrowException() {
        TomcatReactiveWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.addConnectorCustomizers(((TomcatConnectorCustomizer[]) (null)))).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void tomcatConnectorCustomizersShouldBeInvoked() {
        TomcatReactiveWebServerFactory factory = getFactory();
        HttpHandler handler = Mockito.mock(HttpHandler.class);
        TomcatConnectorCustomizer[] listeners = new TomcatConnectorCustomizer[4];
        Arrays.setAll(listeners, ( i) -> mock(.class));
        factory.setTomcatConnectorCustomizers(Arrays.asList(listeners[0], listeners[1]));
        factory.addConnectorCustomizers(listeners[2], listeners[3]);
        this.webServer = factory.getWebServer(handler);
        InOrder ordered = Mockito.inOrder(((Object[]) (listeners)));
        for (TomcatConnectorCustomizer listener : listeners) {
            ordered.verify(listener).customize(ArgumentMatchers.any(Connector.class));
        }
    }

    @Test
    public void useForwardedHeaders() {
        TomcatReactiveWebServerFactory factory = getFactory();
        RemoteIpValve valve = new RemoteIpValve();
        valve.setProtocolHeader("X-Forwarded-Proto");
        factory.addEngineValves(valve);
        assertForwardHeaderIsUsed(factory);
    }

    @Test
    public void referenceClearingIsDisabled() {
        TomcatReactiveWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer(Mockito.mock(HttpHandler.class));
        this.webServer.start();
        Tomcat tomcat = getTomcat();
        StandardContext context = ((StandardContext) (tomcat.getHost().findChildren()[0]));
        assertThat(context.getClearReferencesObjectStreamClassCaches()).isFalse();
        assertThat(context.getClearReferencesRmiTargets()).isFalse();
        assertThat(context.getClearReferencesThreadLocals()).isFalse();
    }
}

