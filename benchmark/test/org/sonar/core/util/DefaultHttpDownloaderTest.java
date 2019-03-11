/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.util;


import CoreProperties.SERVER_ID;
import DefaultHttpDownloader.AuthenticatorFacade;
import DefaultHttpDownloader.BaseHttpDownloader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Properties;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.simpleframework.transport.connect.SocketConnection;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.platform.Server;
import org.sonar.api.utils.SonarException;

import static java.net.Proxy.Type.HTTP;
import static java.nio.charset.StandardCharsets.UTF_8;


public class DefaultHttpDownloaderTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TestRule safeguardTimeout = new DisableOnDebug(Timeout.seconds(60));

    private static SocketConnection socketConnection;

    private static String baseUrl;

    @Test(timeout = 10000)
    public void openStream_network_errors() throws IOException, URISyntaxException {
        // non routable address
        String url = "http://10.255.255.1";
        thrown.expect(SonarException.class);
        thrown.expect(ThrowableCauseMatcher.hasCause(new BaseMatcher<Exception>() {
            @Override
            public boolean matches(Object ex) {
                // Java 8
                return ((ex instanceof NoRouteToHostException) || (ex instanceof SocketException)) || // Java 7 or before
                (ex instanceof SocketTimeoutException);
            }

            @Override
            public void describeTo(Description arg0) {
            }
        }));
        DefaultHttpDownloader downloader = new DefaultHttpDownloader(new MapSettings().asConfig(), 10, 50000);
        downloader.openStream(new URI(url));
    }

    @Test
    public void downloadBytes() throws URISyntaxException {
        byte[] bytes = readBytes(new URI(DefaultHttpDownloaderTest.baseUrl));
        assertThat(bytes.length).isGreaterThan(10);
    }

    @Test
    public void readString() throws URISyntaxException {
        String text = new DefaultHttpDownloader(new MapSettings().asConfig()).readString(new URI(DefaultHttpDownloaderTest.baseUrl), UTF_8);
        assertThat(text.length()).isGreaterThan(10);
    }

    @Test
    public void readGzipString() throws URISyntaxException {
        String text = new DefaultHttpDownloader(new MapSettings().asConfig()).readString(new URI(((DefaultHttpDownloaderTest.baseUrl) + "/gzip/")), UTF_8);
        assertThat(text).isEqualTo("GZIP response");
    }

    @Test
    public void readStringWithDefaultTimeout() throws URISyntaxException {
        String text = new DefaultHttpDownloader(new MapSettings().asConfig()).readString(new URI(((DefaultHttpDownloaderTest.baseUrl) + "/timeout/")), UTF_8);
        assertThat(text.length()).isGreaterThan(10);
    }

    @Test
    public void readStringWithTimeout() throws URISyntaxException {
        thrown.expect(new BaseMatcher<Exception>() {
            @Override
            public boolean matches(Object ex) {
                return (ex instanceof SonarException) && ((getCause()) instanceof SocketTimeoutException);
            }

            @Override
            public void describeTo(Description arg0) {
            }
        });
        new DefaultHttpDownloader(new MapSettings().asConfig(), 50).readString(new URI(((DefaultHttpDownloaderTest.baseUrl) + "/timeout/")), UTF_8);
    }

    @Test
    public void downloadToFile() throws IOException, URISyntaxException {
        File toDir = temporaryFolder.newFolder();
        File toFile = new File(toDir, "downloadToFile.txt");
        download(new URI(DefaultHttpDownloaderTest.baseUrl), toFile);
        assertThat(toFile).exists();
        assertThat(toFile.length()).isGreaterThan(10L);
    }

    @Test
    public void shouldNotCreateFileIfFailToDownload() throws Exception {
        File toDir = temporaryFolder.newFolder();
        File toFile = new File(toDir, "downloadToFile.txt");
        try {
            int port = new InetSocketAddress("localhost", 0).getPort();
            download(new URI(("http://localhost:" + port)), toFile);
        } catch (SonarException e) {
            assertThat(toFile).doesNotExist();
        }
    }

    @Test
    public void userAgent_includes_version_and_SERVER_ID_when_server_is_provided() throws IOException, URISyntaxException {
        Server server = Mockito.mock(Server.class);
        Mockito.when(server.getVersion()).thenReturn("2.2");
        MapSettings settings = new MapSettings();
        settings.setProperty(SERVER_ID, "blablabla");
        InputStream stream = openStream(new URI(DefaultHttpDownloaderTest.baseUrl));
        Properties props = new Properties();
        props.load(stream);
        stream.close();
        assertThat(props.getProperty("agent")).isEqualTo("SonarQube 2.2 # blablabla");
    }

    @Test
    public void userAgent_includes_only_version_when_there_is_no_SERVER_ID_and_server_is_provided() throws IOException, URISyntaxException {
        Server server = Mockito.mock(Server.class);
        Mockito.when(server.getVersion()).thenReturn("2.2");
        InputStream stream = openStream(new URI(DefaultHttpDownloaderTest.baseUrl));
        Properties props = new Properties();
        props.load(stream);
        stream.close();
        assertThat(props.getProperty("agent")).isEqualTo("SonarQube 2.2 #");
    }

    @Test
    public void userAgent_is_static_value_when_server_is_not_provided() throws IOException, URISyntaxException {
        InputStream stream = openStream(new URI(DefaultHttpDownloaderTest.baseUrl));
        Properties props = new Properties();
        props.load(stream);
        stream.close();
        assertThat(props.getProperty("agent")).isEqualTo("SonarQube");
    }

    @Test
    public void followRedirect() throws URISyntaxException {
        String content = new DefaultHttpDownloader(new MapSettings().asConfig()).readString(new URI(((DefaultHttpDownloaderTest.baseUrl) + "/redirect/")), UTF_8);
        assertThat(content).contains("agent");
    }

    @Test
    public void shouldGetDirectProxySynthesis() throws URISyntaxException {
        ProxySelector proxySelector = Mockito.mock(ProxySelector.class);
        Mockito.when(proxySelector.select(ArgumentMatchers.any(URI.class))).thenReturn(Arrays.asList(Proxy.NO_PROXY));
        assertThat(BaseHttpDownloader.getProxySynthesis(new URI("http://an_url"), proxySelector)).isEqualTo("no proxy");
    }

    @Test
    public void shouldGetProxySynthesis() throws URISyntaxException {
        ProxySelector proxySelector = Mockito.mock(ProxySelector.class);
        Mockito.when(proxySelector.select(ArgumentMatchers.any(URI.class))).thenReturn(Arrays.<Proxy>asList(new DefaultHttpDownloaderTest.FakeProxy()));
        assertThat(BaseHttpDownloader.getProxySynthesis(new URI("http://an_url"), proxySelector)).isEqualTo("HTTP proxy: /123.45.67.89:4040");
    }

    @Test
    public void supported_schemes() {
        assertThat(getSupportedSchemes()).contains("http");
    }

    @Test
    public void uri_description() throws URISyntaxException {
        String description = description(new URI("http://sonarsource.org"));
        assertThat(description).matches("http://sonarsource.org \\(.*\\)");
    }

    @Test
    public void configure_http_proxy_credentials() {
        DefaultHttpDownloader.AuthenticatorFacade system = Mockito.mock(AuthenticatorFacade.class);
        MapSettings settings = new MapSettings();
        settings.setProperty("https.proxyHost", "1.2.3.4");
        settings.setProperty("http.proxyUser", "the_login");
        settings.setProperty("http.proxyPassword", "the_passwd");
        new DefaultHttpDownloader.BaseHttpDownloader(system, settings.asConfig(), null);
        Mockito.verify(system).setDefaultAuthenticator(ArgumentMatchers.argThat(( authenticator) -> {
            DefaultHttpDownloader.ProxyAuthenticator a = ((DefaultHttpDownloader.ProxyAuthenticator) (authenticator));
            PasswordAuthentication authentication = a.getPasswordAuthentication();
            return (authentication.getUserName().equals("the_login")) && (new String(authentication.getPassword()).equals("the_passwd"));
        }));
    }

    private static class FakeProxy extends Proxy {
        FakeProxy() {
            super(HTTP, new InetSocketAddress("123.45.67.89", 4040));
        }
    }
}

