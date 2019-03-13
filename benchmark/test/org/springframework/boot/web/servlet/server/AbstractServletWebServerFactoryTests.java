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
package org.springframework.boot.web.servlet.server;


import ClientAuth.NEED;
import ClientAuth.WANT;
import HttpMethod.GET;
import HttpMethod.POST;
import HttpMethod.PUT;
import HttpStatus.NOT_FOUND;
import MimeMappings.Mapping;
import SessionTrackingMode.COOKIE;
import SessionTrackingMode.SSL;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.HttpServlet;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.InputStreamFactory;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.jasper.EmbeddedServletOptions;
import org.apache.jasper.servlet.JspServlet;
import org.hamcrest.CoreMatchers;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.system.ApplicationTemp;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.boot.testsupport.web.servlet.ExampleFilter;
import org.springframework.boot.testsupport.web.servlet.ExampleServlet;
import org.springframework.boot.web.server.Compression;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.SocketUtils;


/**
 * Base for testing classes that extends {@link AbstractServletWebServerFactory}.
 *
 * @author Phillip Webb
 * @author Greg Turnquist
 * @author Andy Wilkinson
 * @author Raja Kolli
 */
public abstract class AbstractServletWebServerFactoryTests {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public OutputCapture output = new OutputCapture();

    protected WebServer webServer;

    private final HttpClientContext httpClientContext = HttpClientContext.create();

    @Test
    public void startServlet() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        assertThat(getResponse(getLocalUrl("/hello"))).isEqualTo("Hello World");
    }

    @Test
    public void startCalledTwice() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        int port = this.webServer.getPort();
        this.webServer.start();
        assertThat(this.webServer.getPort()).isEqualTo(port);
        assertThat(getResponse(getLocalUrl("/hello"))).isEqualTo("Hello World");
        assertThat(this.output.toString()).containsOnlyOnce("started on port");
    }

    @Test
    public void stopCalledTwice() {
        AbstractServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        this.webServer.stop();
        this.webServer.stop();
    }

    @Test
    public void emptyServerWhenPortIsMinusOne() {
        AbstractServletWebServerFactory factory = getFactory();
        factory.setPort((-1));
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        assertThat(this.webServer.getPort()).isLessThan(0);// Jetty is -2

    }

    @Test
    public void stopServlet() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        int port = this.webServer.getPort();
        this.webServer.stop();
        assertThatIOException().isThrownBy(() -> getResponse(getLocalUrl(port, "/hello")));
    }

    @Test
    public void startServletAndFilter() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer(exampleServletRegistration(), new org.springframework.boot.web.servlet.FilterRegistrationBean(new ExampleFilter()));
        this.webServer.start();
        assertThat(getResponse(getLocalUrl("/hello"))).isEqualTo("[Hello World]");
    }

    @Test
    public void startBlocksUntilReadyToServe() {
        AbstractServletWebServerFactory factory = getFactory();
        final Date[] date = new Date[1];
        this.webServer = factory.getWebServer(( servletContext) -> {
            try {
                Thread.sleep(500);
                date[0] = new Date();
            } catch ( ex) {
                throw new <ex>ServletException();
            }
        });
        this.webServer.start();
        assertThat(date[0]).isNotNull();
    }

    @Test
    public void loadOnStartAfterContextIsInitialized() {
        AbstractServletWebServerFactory factory = getFactory();
        final AbstractServletWebServerFactoryTests.InitCountingServlet servlet = new AbstractServletWebServerFactoryTests.InitCountingServlet();
        this.webServer = factory.getWebServer(( servletContext) -> servletContext.addServlet("test", servlet).setLoadOnStartup(1));
        assertThat(servlet.getInitCount()).isEqualTo(0);
        this.webServer.start();
        assertThat(servlet.getInitCount()).isEqualTo(1);
    }

    @Test
    public void specificPort() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        int specificPort = SocketUtils.findAvailableTcpPort(41000);
        factory.setPort(specificPort);
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        assertThat(getResponse((("http://localhost:" + specificPort) + "/hello"))).isEqualTo("Hello World");
        assertThat(this.webServer.getPort()).isEqualTo(specificPort);
    }

    @Test
    public void specificContextRoot() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        factory.setContextPath("/say");
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        assertThat(getResponse(getLocalUrl("/say/hello"))).isEqualTo("Hello World");
    }

    @Test
    public void contextPathIsLoggedOnStartup() {
        AbstractServletWebServerFactory factory = getFactory();
        factory.setContextPath("/custom");
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        assertThat(this.output.toString()).containsOnlyOnce("with context path '/custom'");
    }

    @Test
    public void contextPathMustStartWithSlash() {
        assertThatIllegalArgumentException().isThrownBy(() -> getFactory().setContextPath("missingslash")).withMessageContaining("ContextPath must start with '/' and not end with '/'");
    }

    @Test
    public void contextPathMustNotEndWithSlash() {
        assertThatIllegalArgumentException().isThrownBy(() -> getFactory().setContextPath("extraslash/")).withMessageContaining("ContextPath must start with '/' and not end with '/'");
    }

    @Test
    public void contextRootPathMustNotBeSlash() {
        assertThatIllegalArgumentException().isThrownBy(() -> getFactory().setContextPath("/")).withMessageContaining("Root ContextPath must be specified using an empty string");
    }

    @Test
    public void multipleConfigurations() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        ServletContextInitializer[] initializers = new ServletContextInitializer[6];
        Arrays.setAll(initializers, ( i) -> mock(.class));
        factory.setInitializers(Arrays.asList(initializers[2], initializers[3]));
        factory.addInitializers(initializers[4], initializers[5]);
        this.webServer = factory.getWebServer(initializers[0], initializers[1]);
        this.webServer.start();
        InOrder ordered = Mockito.inOrder(((Object[]) (initializers)));
        for (ServletContextInitializer initializer : initializers) {
            ordered.verify(initializer).onStartup(ArgumentMatchers.any(ServletContext.class));
        }
    }

    @Test
    public void documentRoot() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        addTestTxtFile(factory);
        this.webServer = factory.getWebServer();
        this.webServer.start();
        assertThat(getResponse(getLocalUrl("/test.txt"))).isEqualTo("test");
    }

    @Test
    public void mimeType() throws Exception {
        FileCopyUtils.copy("test", new FileWriter(this.temporaryFolder.newFile("test.xxcss")));
        AbstractServletWebServerFactory factory = getFactory();
        factory.setDocumentRoot(this.temporaryFolder.getRoot());
        MimeMappings mimeMappings = new MimeMappings();
        mimeMappings.add("xxcss", "text/css");
        factory.setMimeMappings(mimeMappings);
        this.webServer = factory.getWebServer();
        this.webServer.start();
        ClientHttpResponse response = getClientResponse(getLocalUrl("/test.xxcss"));
        assertThat(response.getHeaders().getContentType().toString()).isEqualTo("text/css");
        response.close();
    }

    @Test
    public void errorPage() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        factory.addErrorPages(new org.springframework.boot.web.server.ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/hello"));
        this.webServer = factory.getWebServer(exampleServletRegistration(), errorServletRegistration());
        this.webServer.start();
        assertThat(getResponse(getLocalUrl("/hello"))).isEqualTo("Hello World");
        assertThat(getResponse(getLocalUrl("/bang"))).isEqualTo("Hello World");
    }

    @Test
    public void errorPageFromPutRequest() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        factory.addErrorPages(new org.springframework.boot.web.server.ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/hello"));
        this.webServer = factory.getWebServer(exampleServletRegistration(), errorServletRegistration());
        this.webServer.start();
        assertThat(getResponse(getLocalUrl("/hello"), PUT)).isEqualTo("Hello World");
        assertThat(getResponse(getLocalUrl("/bang"), PUT)).isEqualTo("Hello World");
    }

    @Test
    public void basicSslFromClassPath() throws Exception {
        testBasicSslWithKeyStore("classpath:test.jks");
    }

    @Test
    public void basicSslFromFileSystem() throws Exception {
        testBasicSslWithKeyStore("src/test/resources/test.jks");
    }

    @Test
    public void sslDisabled() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        Ssl ssl = getSsl(null, "password", "classpath:test.jks");
        ssl.setEnabled(false);
        factory.setSsl(ssl);
        this.webServer = factory.getWebServer(new ServletRegistrationBean(new ExampleServlet(true, false), "/hello"));
        this.webServer.start();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        assertThatExceptionOfType(SSLException.class).isThrownBy(() -> getResponse(getLocalUrl("https", "/hello"), requestFactory));
    }

    @Test
    public void sslGetScheme() throws Exception {
        // gh-2232
        AbstractServletWebServerFactory factory = getFactory();
        factory.setSsl(getSsl(null, "password", "src/test/resources/test.jks"));
        this.webServer = factory.getWebServer(new ServletRegistrationBean(new ExampleServlet(true, false), "/hello"));
        this.webServer.start();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        assertThat(getResponse(getLocalUrl("https", "/hello"), requestFactory)).contains("scheme=https");
    }

    @Test
    public void sslKeyAlias() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        Ssl ssl = getSsl(null, "password", "test-alias", "src/test/resources/test.jks");
        factory.setSsl(ssl);
        ServletRegistrationBean<ExampleServlet> registration = new ServletRegistrationBean(new ExampleServlet(true, false), "/hello");
        this.webServer = factory.getWebServer(registration);
        this.webServer.start();
        TrustStrategy trustStrategy = new AbstractServletWebServerFactoryTests.SerialNumberValidatingTrustSelfSignedStrategy("3a3aaec8");
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext)).build();
        String response = getResponse(getLocalUrl("https", "/hello"), new HttpComponentsClientHttpRequestFactory(httpClient));
        assertThat(response).contains("scheme=https");
    }

    @Test
    public void serverHeaderIsDisabledByDefaultWhenUsingSsl() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        factory.setSsl(getSsl(null, "password", "src/test/resources/test.jks"));
        this.webServer = factory.getWebServer(new ServletRegistrationBean(new ExampleServlet(true, false), "/hello"));
        this.webServer.start();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        ClientHttpResponse response = getClientResponse(getLocalUrl("https", "/hello"), GET, new HttpComponentsClientHttpRequestFactory(httpClient));
        assertThat(response.getHeaders().get("Server")).isNullOrEmpty();
    }

    @Test
    public void serverHeaderCanBeCustomizedWhenUsingSsl() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        factory.setServerHeader("MyServer");
        factory.setSsl(getSsl(null, "password", "src/test/resources/test.jks"));
        this.webServer = factory.getWebServer(new ServletRegistrationBean(new ExampleServlet(true, false), "/hello"));
        this.webServer.start();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        ClientHttpResponse response = getClientResponse(getLocalUrl("https", "/hello"), GET, new HttpComponentsClientHttpRequestFactory(httpClient));
        assertThat(response.getHeaders().get("Server")).containsExactly("MyServer");
    }

    @Test
    public void pkcs12KeyStoreAndTrustStore() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        addTestTxtFile(factory);
        factory.setSsl(getSsl(NEED, null, "classpath:test.p12", "classpath:test.p12", null, null));
        this.webServer = factory.getWebServer();
        this.webServer.start();
        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        keyStore.load(new FileInputStream(new File("src/test/resources/test.p12")), "secret".toCharArray());
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).loadKeyMaterial(keyStore, "secret".toCharArray()).build());
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        assertThat(getResponse(getLocalUrl("https", "/test.txt"), requestFactory)).isEqualTo("test");
    }

    @Test
    public void sslNeedsClientAuthenticationSucceedsWithClientCertificate() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        addTestTxtFile(factory);
        factory.setSsl(getSsl(NEED, "password", "classpath:test.jks", "classpath:test.jks", null, null));
        this.webServer = factory.getWebServer();
        this.webServer.start();
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream(new File("src/test/resources/test.jks")), "secret".toCharArray());
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).loadKeyMaterial(keyStore, "password".toCharArray()).build());
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        assertThat(getResponse(getLocalUrl("https", "/test.txt"), requestFactory)).isEqualTo("test");
    }

    @Test
    public void sslNeedsClientAuthenticationFailsWithoutClientCertificate() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        addTestTxtFile(factory);
        factory.setSsl(getSsl(NEED, "password", "classpath:test.jks"));
        this.webServer = factory.getWebServer();
        this.webServer.start();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        String localUrl = getLocalUrl("https", "/test.txt");
        assertThatIOException().isThrownBy(() -> getResponse(localUrl, requestFactory));
    }

    @Test
    public void sslWantsClientAuthenticationSucceedsWithClientCertificate() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        addTestTxtFile(factory);
        factory.setSsl(getSsl(WANT, "password", "classpath:test.jks", null, new String[]{ "TLSv1.2" }, null));
        this.webServer = factory.getWebServer();
        this.webServer.start();
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream(new File("src/test/resources/test.jks")), "secret".toCharArray());
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).loadKeyMaterial(keyStore, "password".toCharArray()).build());
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        assertThat(getResponse(getLocalUrl("https", "/test.txt"), requestFactory)).isEqualTo("test");
    }

    @Test
    public void sslWantsClientAuthenticationSucceedsWithoutClientCertificate() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        addTestTxtFile(factory);
        factory.setSsl(getSsl(WANT, "password", "classpath:test.jks"));
        this.webServer = factory.getWebServer();
        this.webServer.start();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        assertThat(getResponse(getLocalUrl("https", "/test.txt"), requestFactory)).isEqualTo("test");
    }

    @Test
    public void sslWithCustomSslStoreProvider() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        addTestTxtFile(factory);
        Ssl ssl = new Ssl();
        ssl.setClientAuth(NEED);
        ssl.setKeyPassword("password");
        factory.setSsl(ssl);
        SslStoreProvider sslStoreProvider = Mockito.mock(SslStoreProvider.class);
        BDDMockito.given(sslStoreProvider.getKeyStore()).willReturn(loadStore());
        BDDMockito.given(sslStoreProvider.getTrustStore()).willReturn(loadStore());
        factory.setSslStoreProvider(sslStoreProvider);
        this.webServer = factory.getWebServer();
        this.webServer.start();
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream(new File("src/test/resources/test.jks")), "secret".toCharArray());
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).loadKeyMaterial(keyStore, "password".toCharArray()).build());
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        assertThat(getResponse(getLocalUrl("https", "/test.txt"), requestFactory)).isEqualTo("test");
        Mockito.verify(sslStoreProvider, Mockito.atLeastOnce()).getKeyStore();
        Mockito.verify(sslStoreProvider, Mockito.atLeastOnce()).getTrustStore();
    }

    @Test
    public void disableJspServletRegistration() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        factory.getJsp().setRegistered(false);
        this.webServer = factory.getWebServer();
        assertThat(getJspServlet()).isNull();
    }

    @Test
    public void cannotReadClassPathFiles() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        ClientHttpResponse response = getClientResponse(getLocalUrl("/org/springframework/boot/SpringApplication.class"));
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void defaultSessionTimeout() {
        assertThat(getFactory().getSession().getTimeout()).isEqualTo(Duration.ofMinutes(30));
    }

    @Test
    public void persistSession() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        factory.getSession().setPersistent(true);
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
        assertThat(s3.split(":")[0]).as(message).isEqualTo(s2.split(":")[1]);
    }

    @Test
    public void persistSessionInSpecificSessionStoreDir() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        File sessionStoreDir = this.temporaryFolder.newFolder();
        factory.getSession().setPersistent(true);
        factory.getSession().setStoreDir(sessionStoreDir);
        this.webServer = factory.getWebServer(sessionServletRegistration());
        this.webServer.start();
        getResponse(getLocalUrl("/session"));
        this.webServer.stop();
        File[] dirContents = sessionStoreDir.listFiles(( dir, name) -> !((".".equals(name)) || ("..".equals(name))));
        assertThat(dirContents.length).isGreaterThan(0);
    }

    @Test
    public void getValidSessionStoreWhenSessionStoreNotSet() {
        AbstractServletWebServerFactory factory = getFactory();
        File dir = factory.getValidSessionStoreDir(false);
        assertThat(dir.getName()).isEqualTo("servlet-sessions");
        assertThat(dir.getParentFile()).isEqualTo(new ApplicationTemp().getDir());
    }

    @Test
    public void getValidSessionStoreWhenSessionStoreIsRelative() {
        AbstractServletWebServerFactory factory = getFactory();
        factory.getSession().setStoreDir(new File("sessions"));
        File dir = factory.getValidSessionStoreDir(false);
        assertThat(dir.getName()).isEqualTo("sessions");
        assertThat(dir.getParentFile()).isEqualTo(new ApplicationHome().getDir());
    }

    @Test
    public void getValidSessionStoreWhenSessionStoreReferencesFile() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        factory.getSession().setStoreDir(this.temporaryFolder.newFile());
        assertThatIllegalStateException().isThrownBy(() -> factory.getValidSessionStoreDir(false)).withMessageContaining("points to a file");
    }

    @Test
    public void sessionCookieConfiguration() {
        AbstractServletWebServerFactory factory = getFactory();
        factory.getSession().getCookie().setName("testname");
        factory.getSession().getCookie().setDomain("testdomain");
        factory.getSession().getCookie().setPath("/testpath");
        factory.getSession().getCookie().setComment("testcomment");
        factory.getSession().getCookie().setHttpOnly(true);
        factory.getSession().getCookie().setSecure(true);
        factory.getSession().getCookie().setMaxAge(Duration.ofSeconds(60));
        final AtomicReference<SessionCookieConfig> configReference = new AtomicReference<>();
        this.webServer = factory.getWebServer(( context) -> configReference.set(context.getSessionCookieConfig()));
        SessionCookieConfig sessionCookieConfig = configReference.get();
        assertThat(sessionCookieConfig.getName()).isEqualTo("testname");
        assertThat(sessionCookieConfig.getDomain()).isEqualTo("testdomain");
        assertThat(sessionCookieConfig.getPath()).isEqualTo("/testpath");
        assertThat(sessionCookieConfig.getComment()).isEqualTo("testcomment");
        assertThat(sessionCookieConfig.isHttpOnly()).isTrue();
        assertThat(sessionCookieConfig.isSecure()).isTrue();
        assertThat(sessionCookieConfig.getMaxAge()).isEqualTo(60);
    }

    @Test
    public void sslSessionTracking() {
        AbstractServletWebServerFactory factory = getFactory();
        Ssl ssl = new Ssl();
        ssl.setEnabled(true);
        ssl.setKeyStore("src/test/resources/test.jks");
        ssl.setKeyPassword("password");
        factory.setSsl(ssl);
        factory.getSession().setTrackingModes(EnumSet.of(SSL));
        AtomicReference<ServletContext> contextReference = new AtomicReference<>();
        this.webServer = factory.getWebServer(contextReference::set);
        assertThat(contextReference.get().getEffectiveSessionTrackingModes()).isEqualTo(EnumSet.of(javax.servlet.SessionTrackingMode.SSL));
    }

    @Test
    public void compressionOfResponseToGetRequest() throws Exception {
        assertThat(doTestCompression(10000, null, null)).isTrue();
    }

    @Test
    public void compressionOfResponseToPostRequest() throws Exception {
        assertThat(doTestCompression(10000, null, null, POST)).isTrue();
    }

    @Test
    public void noCompressionForSmallResponse() throws Exception {
        assertThat(doTestCompression(100, null, null)).isFalse();
    }

    @Test
    public void noCompressionForMimeType() throws Exception {
        String[] mimeTypes = new String[]{ "text/html", "text/xml", "text/css" };
        assertThat(doTestCompression(10000, mimeTypes, null)).isFalse();
    }

    @Test
    public void noCompressionForUserAgent() throws Exception {
        assertThat(doTestCompression(10000, null, new String[]{ "testUserAgent" })).isFalse();
    }

    @Test
    public void compressionWithoutContentSizeHeader() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        Compression compression = new Compression();
        compression.setEnabled(true);
        factory.setCompression(compression);
        this.webServer = factory.getWebServer(new ServletRegistrationBean(new ExampleServlet(false, true), "/hello"));
        this.webServer.start();
        AbstractServletWebServerFactoryTests.TestGzipInputStreamFactory inputStreamFactory = new AbstractServletWebServerFactoryTests.TestGzipInputStreamFactory();
        Map<String, InputStreamFactory> contentDecoderMap = Collections.singletonMap("gzip", ((InputStreamFactory) (inputStreamFactory)));
        getResponse(getLocalUrl("/hello"), new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().setContentDecoderRegistry(contentDecoderMap).build()));
        assertThat(inputStreamFactory.wasCompressionUsed()).isTrue();
    }

    @Test
    public void mimeMappingsAreCorrectlyConfigured() {
        AbstractServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer();
        Map<String, String> configuredMimeMappings = getActualMimeMappings();
        Collection<MimeMappings.Mapping> expectedMimeMappings = getExpectedMimeMappings();
        configuredMimeMappings.forEach(( key, value) -> assertThat(expectedMimeMappings).contains(new MimeMappings.Mapping(key, value)));
        for (MimeMappings.Mapping mapping : expectedMimeMappings) {
            assertThat(configuredMimeMappings).containsEntry(mapping.getExtension(), mapping.getMimeType());
        }
        assertThat(configuredMimeMappings.size()).isEqualTo(expectedMimeMappings.size());
    }

    @Test
    public void rootServletContextResource() {
        AbstractServletWebServerFactory factory = getFactory();
        final AtomicReference<URL> rootResource = new AtomicReference<>();
        this.webServer = factory.getWebServer(( servletContext) -> {
            try {
                rootResource.set(servletContext.getResource("/"));
            } catch ( ex) {
                throw new <ex>ServletException();
            }
        });
        this.webServer.start();
        assertThat(rootResource.get()).isNotNull();
    }

    @Test
    public void customServerHeader() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        factory.setServerHeader("MyServer");
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        ClientHttpResponse response = getClientResponse(getLocalUrl("/hello"));
        assertThat(response.getHeaders().getFirst("server")).isEqualTo("MyServer");
    }

    @Test
    public void serverHeaderIsDisabledByDefault() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        ClientHttpResponse response = getClientResponse(getLocalUrl("/hello"));
        assertThat(response.getHeaders().getFirst("server")).isNull();
    }

    @Test
    public void portClashOfPrimaryConnectorResultsInPortInUseException() throws IOException {
        doWithBlockedPort(( port) -> {
            assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
                AbstractServletWebServerFactory factory = getFactory();
                factory.setPort(port);
                this.webServer = factory.getWebServer();
                this.webServer.start();
            }).satisfies(( ex) -> handleExceptionCausedByBlockedPort(ex, port));
        });
    }

    @Test
    public void portClashOfSecondaryConnectorResultsInPortInUseException() throws IOException {
        doWithBlockedPort(( port) -> {
            assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
                AbstractServletWebServerFactory factory = getFactory();
                addConnector(port, factory);
                this.webServer = factory.getWebServer();
                this.webServer.start();
            }).satisfies(( ex) -> handleExceptionCausedByBlockedPort(ex, port));
        });
    }

    @Test
    public void localeCharsetMappingsAreConfigured() {
        AbstractServletWebServerFactory factory = getFactory();
        Map<Locale, Charset> mappings = new HashMap<>();
        mappings.put(Locale.GERMAN, StandardCharsets.UTF_8);
        factory.setLocaleCharsetMappings(mappings);
        this.webServer = factory.getWebServer();
        assertThat(getCharset(Locale.GERMAN)).isEqualTo(StandardCharsets.UTF_8);
        assertThat(getCharset(Locale.ITALIAN)).isNull();
    }

    @Test
    public void jspServletInitParameters() throws Exception {
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("a", "alpha");
        AbstractServletWebServerFactory factory = getFactory();
        factory.getJsp().setInitParameters(initParameters);
        this.webServer = factory.getWebServer();
        Assume.assumeThat(getJspServlet(), CoreMatchers.notNullValue());
        JspServlet jspServlet = getJspServlet();
        assertThat(jspServlet.getInitParameter("a")).isEqualTo("alpha");
    }

    @Test
    public void jspServletIsNotInDevelopmentModeByDefault() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer();
        Assume.assumeThat(getJspServlet(), CoreMatchers.notNullValue());
        JspServlet jspServlet = getJspServlet();
        EmbeddedServletOptions options = ((EmbeddedServletOptions) (ReflectionTestUtils.getField(jspServlet, "options")));
        assertThat(options.getDevelopment()).isFalse();
    }

    @Test
    public void faultyFilterCausesStartFailure() {
        AbstractServletWebServerFactory factory = getFactory();
        factory.addInitializers(( servletContext) -> servletContext.addFilter("faulty", new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
                throw new ServletException("Faulty filter");
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                chain.doFilter(request, response);
            }

            @Override
            public void destroy() {
            }
        }));
        assertThatExceptionOfType(WebServerException.class).isThrownBy(() -> factory.getWebServer().start());
    }

    @Test
    public void sessionConfiguration() {
        AbstractServletWebServerFactory factory = getFactory();
        factory.getSession().setTimeout(Duration.ofSeconds(123));
        factory.getSession().setTrackingModes(EnumSet.of(COOKIE, SessionTrackingMode.URL));
        factory.getSession().getCookie().setName("testname");
        factory.getSession().getCookie().setDomain("testdomain");
        factory.getSession().getCookie().setPath("/testpath");
        factory.getSession().getCookie().setComment("testcomment");
        factory.getSession().getCookie().setHttpOnly(true);
        factory.getSession().getCookie().setSecure(true);
        factory.getSession().getCookie().setMaxAge(Duration.ofMinutes(1));
        AtomicReference<ServletContext> contextReference = new AtomicReference<>();
        factory.getWebServer(contextReference::set).start();
        ServletContext servletContext = contextReference.get();
        assertThat(servletContext.getEffectiveSessionTrackingModes()).isEqualTo(EnumSet.of(javax.servlet.SessionTrackingMode.COOKIE, javax.servlet.SessionTrackingMode.URL));
        assertThat(servletContext.getSessionCookieConfig().getName()).isEqualTo("testname");
        assertThat(servletContext.getSessionCookieConfig().getDomain()).isEqualTo("testdomain");
        assertThat(servletContext.getSessionCookieConfig().getPath()).isEqualTo("/testpath");
        assertThat(servletContext.getSessionCookieConfig().getComment()).isEqualTo("testcomment");
        assertThat(servletContext.getSessionCookieConfig().isHttpOnly()).isTrue();
        assertThat(servletContext.getSessionCookieConfig().isSecure()).isTrue();
        assertThat(servletContext.getSessionCookieConfig().getMaxAge()).isEqualTo(60);
    }

    @Test
    public void servletContextListenerContextDestroyedIsCalledWhenContainerIsStopped() throws Exception {
        ServletContextListener listener = Mockito.mock(ServletContextListener.class);
        this.webServer = getFactory().getWebServer(( servletContext) -> servletContext.addListener(listener));
        this.webServer.start();
        this.webServer.stop();
        Mockito.verify(listener).contextDestroyed(ArgumentMatchers.any(ServletContextEvent.class));
    }

    @Test
    public void exceptionThrownOnLoadFailureIsRethrown() {
        AbstractServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer(( context) -> context.addServlet("failing", .class).setLoadOnStartup(0));
        assertThatExceptionOfType(WebServerException.class).isThrownBy(this.webServer::start).satisfies(this::wrapsFailingServletException);
    }

    private class TestGzipInputStreamFactory implements InputStreamFactory {
        private final AtomicBoolean requested = new AtomicBoolean(false);

        @Override
        public InputStream create(InputStream in) throws IOException {
            if (this.requested.get()) {
                throw new IllegalStateException("On deflated InputStream already requested");
            }
            this.requested.set(true);
            return new GZIPInputStream(in);
        }

        public boolean wasCompressionUsed() {
            return this.requested.get();
        }
    }

    @SuppressWarnings("serial")
    private static class InitCountingServlet extends GenericServlet {
        private int initCount;

        @Override
        public void init() {
            (this.initCount)++;
        }

        @Override
        public void service(ServletRequest req, ServletResponse res) {
        }

        public int getInitCount() {
            return this.initCount;
        }
    }

    public interface BlockedPortAction {
        void run(int port);
    }

    /**
     * {@link TrustSelfSignedStrategy} that also validates certificate serial number.
     */
    private static final class SerialNumberValidatingTrustSelfSignedStrategy extends TrustSelfSignedStrategy {
        private final String serialNumber;

        private SerialNumberValidatingTrustSelfSignedStrategy(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        @Override
        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            String hexSerialNumber = chain[0].getSerialNumber().toString(16);
            boolean isMatch = hexSerialNumber.equals(this.serialNumber);
            return (super.isTrusted(chain, authType)) && isMatch;
        }
    }

    public static class FailingServlet extends HttpServlet {
        @Override
        public void init() throws ServletException {
            throw new AbstractServletWebServerFactoryTests.FailingServletException();
        }
    }

    private static class FailingServletException extends RuntimeException {
        FailingServletException() {
            super("Init Failure");
        }
    }
}

