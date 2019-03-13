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


import JarScanType.TLD;
import LifecycleState.DESTROYED;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Service;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.valves.RemoteIpValve;
import org.apache.tomcat.JarScanFilter;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactoryTests;


/**
 * Tests for {@link TomcatServletWebServerFactory}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class TomcatServletWebServerFactoryTests extends AbstractServletWebServerFactoryTests {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    // JMX MBean names clash if you get more than one Engine with the same name...
    @Test
    public void tomcatEngineNames() {
        TomcatServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer();
        factory.setPort(0);
        TomcatWebServer tomcatWebServer = ((TomcatWebServer) (factory.getWebServer()));
        // Make sure that the names are different
        String firstName = getTomcat().getEngine().getName();
        String secondName = tomcatWebServer.getTomcat().getEngine().getName();
        assertThat(firstName).as("Tomcat engines must have different names").isNotEqualTo(secondName);
        tomcatWebServer.stop();
    }

    @Test
    public void defaultTomcatListeners() {
        TomcatServletWebServerFactory factory = getFactory();
        assertThat(factory.getContextLifecycleListeners()).hasSize(1).first().isInstanceOf(AprLifecycleListener.class);
    }

    @Test
    public void tomcatListeners() {
        TomcatServletWebServerFactory factory = getFactory();
        LifecycleListener[] listeners = new LifecycleListener[4];
        Arrays.setAll(listeners, ( i) -> mock(.class));
        factory.setContextLifecycleListeners(Arrays.asList(listeners[0], listeners[1]));
        factory.addContextLifecycleListeners(listeners[2], listeners[3]);
        this.webServer = factory.getWebServer();
        InOrder ordered = Mockito.inOrder(((Object[]) (listeners)));
        for (LifecycleListener listener : listeners) {
            ordered.verify(listener).lifecycleEvent(ArgumentMatchers.any(LifecycleEvent.class));
        }
    }

    @Test
    public void tomcatCustomizers() {
        TomcatServletWebServerFactory factory = getFactory();
        TomcatContextCustomizer[] listeners = new TomcatContextCustomizer[4];
        Arrays.setAll(listeners, ( i) -> mock(.class));
        factory.setTomcatContextCustomizers(Arrays.asList(listeners[0], listeners[1]));
        factory.addContextCustomizers(listeners[2], listeners[3]);
        this.webServer = factory.getWebServer();
        InOrder ordered = Mockito.inOrder(((Object[]) (listeners)));
        for (TomcatContextCustomizer listener : listeners) {
            ordered.verify(listener).customize(ArgumentMatchers.any(Context.class));
        }
    }

    @Test
    public void contextIsAddedToHostBeforeCustomizersAreCalled() {
        TomcatServletWebServerFactory factory = getFactory();
        TomcatContextCustomizer customizer = Mockito.mock(TomcatContextCustomizer.class);
        factory.addContextCustomizers(customizer);
        this.webServer = factory.getWebServer();
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        Mockito.verify(customizer).customize(contextCaptor.capture());
        assertThat(contextCaptor.getValue().getParent()).isNotNull();
    }

    @Test
    public void tomcatConnectorCustomizers() {
        TomcatServletWebServerFactory factory = getFactory();
        TomcatConnectorCustomizer[] listeners = new TomcatConnectorCustomizer[4];
        Arrays.setAll(listeners, ( i) -> mock(.class));
        factory.setTomcatConnectorCustomizers(Arrays.asList(listeners[0], listeners[1]));
        factory.addConnectorCustomizers(listeners[2], listeners[3]);
        this.webServer = factory.getWebServer();
        InOrder ordered = Mockito.inOrder(((Object[]) (listeners)));
        for (TomcatConnectorCustomizer listener : listeners) {
            ordered.verify(listener).customize(ArgumentMatchers.any(Connector.class));
        }
    }

    @Test
    public void tomcatAdditionalConnectors() {
        TomcatServletWebServerFactory factory = getFactory();
        Connector[] listeners = new Connector[4];
        Arrays.setAll(listeners, ( i) -> new Connector());
        factory.addAdditionalTomcatConnectors(listeners);
        this.webServer = factory.getWebServer();
        Map<Service, Connector[]> connectors = getServiceConnectors();
        assertThat(connectors.values().iterator().next().length).isEqualTo(((listeners.length) + 1));
    }

    @Test
    public void addNullAdditionalConnectorThrows() {
        TomcatServletWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.addAdditionalTomcatConnectors(((Connector[]) (null)))).withMessageContaining("Connectors must not be null");
    }

    @Test
    public void sessionTimeout() {
        TomcatServletWebServerFactory factory = getFactory();
        factory.getSession().setTimeout(Duration.ofSeconds(10));
        assertTimeout(factory, 1);
    }

    @Test
    public void sessionTimeoutInMins() {
        TomcatServletWebServerFactory factory = getFactory();
        factory.getSession().setTimeout(Duration.ofMinutes(1));
        assertTimeout(factory, 1);
    }

    @Test
    public void noSessionTimeout() {
        TomcatServletWebServerFactory factory = getFactory();
        factory.getSession().setTimeout(null);
        assertTimeout(factory, (-1));
    }

    @Test
    public void valve() {
        TomcatServletWebServerFactory factory = getFactory();
        Valve valve = Mockito.mock(Valve.class);
        factory.addContextValves(valve);
        this.webServer = factory.getWebServer();
        Mockito.verify(valve).setNext(ArgumentMatchers.any(Valve.class));
    }

    @Test
    public void setNullTomcatContextCustomizersThrows() {
        TomcatServletWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.setTomcatContextCustomizers(null)).withMessageContaining("TomcatContextCustomizers must not be null");
    }

    @Test
    public void addNullContextCustomizersThrows() {
        TomcatServletWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.addContextCustomizers(((TomcatContextCustomizer[]) (null)))).withMessageContaining("TomcatContextCustomizers must not be null");
    }

    @Test
    public void setNullTomcatConnectorCustomizersThrows() {
        TomcatServletWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.setTomcatConnectorCustomizers(null)).withMessageContaining("TomcatConnectorCustomizers must not be null");
    }

    @Test
    public void addNullConnectorCustomizersThrows() {
        TomcatServletWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.addConnectorCustomizers(((TomcatConnectorCustomizer[]) (null)))).withMessageContaining("TomcatConnectorCustomizers must not be null");
    }

    @Test
    public void uriEncoding() {
        TomcatServletWebServerFactory factory = getFactory();
        factory.setUriEncoding(StandardCharsets.US_ASCII);
        Tomcat tomcat = getTomcat(factory);
        Connector connector = ((TomcatWebServer) (this.webServer)).getServiceConnectors().get(tomcat.getService())[0];
        assertThat(connector.getURIEncoding()).isEqualTo("US-ASCII");
    }

    @Test
    public void defaultUriEncoding() {
        TomcatServletWebServerFactory factory = getFactory();
        Tomcat tomcat = getTomcat(factory);
        Connector connector = ((TomcatWebServer) (this.webServer)).getServiceConnectors().get(tomcat.getService())[0];
        assertThat(connector.getURIEncoding()).isEqualTo("UTF-8");
    }

    @Test
    public void primaryConnectorPortClashThrowsWebServerException() throws IOException {
        doWithBlockedPort(( port) -> {
            TomcatServletWebServerFactory factory = getFactory();
            factory.setPort(port);
            assertThatExceptionOfType(WebServerException.class).isThrownBy(() -> {
                this.webServer = factory.getWebServer();
                this.webServer.start();
            });
        });
    }

    @Test
    public void startupFailureDoesNotResultInUnstoppedThreadsBeingReported() throws IOException {
        super.portClashOfPrimaryConnectorResultsInPortInUseException();
        String string = this.outputCapture.toString();
        assertThat(string).doesNotContain("appears to have started a thread named [main]");
    }

    @Test
    public void stopCalledWithoutStart() {
        TomcatServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.stop();
        Tomcat tomcat = ((TomcatWebServer) (this.webServer)).getTomcat();
        assertThat(tomcat.getServer().getState()).isSameAs(DESTROYED);
    }

    @Test
    public void useForwardHeaders() throws Exception {
        TomcatServletWebServerFactory factory = getFactory();
        factory.addContextValves(new RemoteIpValve());
        assertForwardHeaderIsUsed(factory);
    }

    @Test
    public void disableDoesNotSaveSessionFiles() throws Exception {
        File baseDir = this.temporaryFolder.newFolder();
        TomcatServletWebServerFactory factory = getFactory();
        // If baseDir is not set SESSIONS.ser is written to a different temp directory
        // each time. By setting it we can really ensure that data isn't saved
        factory.setBaseDirectory(baseDir);
        this.webServer = factory.getWebServer(sessionServletRegistration());
        this.webServer.start();
        String s1 = getResponse(getLocalUrl("/session"));
        String s2 = getResponse(getLocalUrl("/session"));
        this.webServer.stop();
        this.webServer = factory.getWebServer(sessionServletRegistration());
        this.webServer.start();
        String s3 = getResponse(getLocalUrl("/session"));
        String message = (((("Session error s1=" + s1) + " s2=") + s2) + " s3=") + s3;
        assertThat(s2.split(":")[0]).as(message).isEqualTo(s1.split(":")[1]);
        assertThat(s3.split(":")[0]).as(message).isNotEqualTo(s2.split(":")[1]);
    }

    @Test
    public void jndiLookupsCanBePerformedDuringApplicationContextRefresh() throws NamingException {
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory(0) {
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }
        };
        // Server is created in onRefresh
        this.webServer = factory.getWebServer();
        // Lookups should now be possible
        new InitialContext().lookup("java:comp/env");
        // Called in finishRefresh, giving us an opportunity to remove the context binding
        // and avoid a leak
        this.webServer.start();
        // Lookups should no longer be possible
        assertThatExceptionOfType(NamingException.class).isThrownBy(() -> new InitialContext().lookup("java:comp/env"));
    }

    @Test
    public void defaultLocaleCharsetMappingsAreOverridden() {
        TomcatServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer();
        // override defaults, see org.apache.catalina.util.CharsetMapperDefault.properties
        assertThat(getCharset(Locale.ENGLISH)).isEqualTo(StandardCharsets.UTF_8);
        assertThat(getCharset(Locale.FRENCH)).isEqualTo(StandardCharsets.UTF_8);
    }

    @Test
    public void sessionIdGeneratorIsConfiguredWithAttributesFromTheManager() {
        System.setProperty("jvmRoute", "test");
        try {
            TomcatServletWebServerFactory factory = getFactory();
            this.webServer = factory.getWebServer();
            this.webServer.start();
        } finally {
            System.clearProperty("jvmRoute");
        }
        Tomcat tomcat = ((TomcatWebServer) (this.webServer)).getTomcat();
        Context context = ((Context) (tomcat.getHost().findChildren()[0]));
        SessionIdGenerator sessionIdGenerator = context.getManager().getSessionIdGenerator();
        assertThat(sessionIdGenerator).isInstanceOf(LazySessionIdGenerator.class);
        assertThat(sessionIdGenerator.getJvmRoute()).isEqualTo("test");
    }

    @Test
    public void tldSkipPatternsShouldBeAppliedToContextJarScanner() {
        TomcatServletWebServerFactory factory = getFactory();
        factory.addTldSkipPatterns("foo.jar", "bar.jar");
        this.webServer = factory.getWebServer();
        this.webServer.start();
        Tomcat tomcat = ((TomcatWebServer) (this.webServer)).getTomcat();
        Context context = ((Context) (tomcat.getHost().findChildren()[0]));
        JarScanFilter jarScanFilter = context.getJarScanner().getJarScanFilter();
        assertThat(jarScanFilter.check(TLD, "foo.jar")).isFalse();
        assertThat(jarScanFilter.check(TLD, "bar.jar")).isFalse();
        assertThat(jarScanFilter.check(TLD, "test.jar")).isTrue();
    }

    @Test
    public void customTomcatHttpOnlyCookie() {
        TomcatServletWebServerFactory factory = getFactory();
        factory.getSession().getCookie().setHttpOnly(false);
        this.webServer = factory.getWebServer();
        this.webServer.start();
        Tomcat tomcat = ((TomcatWebServer) (this.webServer)).getTomcat();
        Context context = ((Context) (tomcat.getHost().findChildren()[0]));
        assertThat(context.getUseHttpOnly()).isFalse();
    }

    @Test
    public void exceptionThrownOnLoadFailureWhenFailCtxIfServletStartFailsIsTrue() {
        TomcatServletWebServerFactory factory = getFactory();
        factory.addContextCustomizers(( context) -> {
            if (context instanceof StandardContext) {
                ((StandardContext) (context)).setFailCtxIfServletStartFails(true);
            }
        });
        this.webServer = factory.getWebServer(( context) -> context.addServlet("failing", .class).setLoadOnStartup(0));
        assertThatExceptionOfType(WebServerException.class).isThrownBy(this.webServer::start);
    }

    @Test
    public void exceptionThrownOnLoadFailureWhenFailCtxIfServletStartFailsIsFalse() {
        TomcatServletWebServerFactory factory = getFactory();
        factory.addContextCustomizers(( context) -> {
            if (context instanceof StandardContext) {
                ((StandardContext) (context)).setFailCtxIfServletStartFails(false);
            }
        });
        this.webServer = factory.getWebServer(( context) -> context.addServlet("failing", .class).setLoadOnStartup(0));
        this.webServer.start();
    }

    @Test
    public void referenceClearingIsDisabled() {
        TomcatServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer();
        this.webServer.start();
        Tomcat tomcat = ((TomcatWebServer) (this.webServer)).getTomcat();
        StandardContext context = ((StandardContext) (tomcat.getHost().findChildren()[0]));
        assertThat(context.getClearReferencesObjectStreamClassCaches()).isFalse();
        assertThat(context.getClearReferencesRmiTargets()).isFalse();
        assertThat(context.getClearReferencesThreadLocals()).isFalse();
    }
}

