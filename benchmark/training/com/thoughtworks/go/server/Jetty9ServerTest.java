/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.server;


import DispatcherType.REQUEST;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class Jetty9ServerTest {
    @Mock
    private Server server;

    @Mock
    private SystemEnvironment systemEnvironment;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private SSLSocketFactory sslSocketFactory;

    @Mock
    private DeploymentManager deploymentManager;

    private Jetty9Server jetty9Server;

    private Handler serverLevelHandler;

    private File configDir;

    private ArgumentCaptor<App> appCaptor;

    @Test
    public void shouldAddMBeanContainerAsEventListener() throws Exception {
        ArgumentCaptor<MBeanContainer> captor = ArgumentCaptor.forClass(MBeanContainer.class);
        jetty9Server.configure();
        Mockito.verify(server).addEventListener(captor.capture());
        MBeanContainer mBeanContainer = captor.getValue();
        Assert.assertThat(mBeanContainer.getMBeanServer(), is(not(nullValue())));
    }

    @Test
    public void shouldAddHttpSocketConnector() throws Exception {
        ArgumentCaptor<Connector> captor = ArgumentCaptor.forClass(Connector.class);
        jetty9Server.configure();
        Mockito.verify(server, Mockito.times(2)).addConnector(captor.capture());
        List<Connector> connectors = captor.getAllValues();
        Connector plainConnector = connectors.get(0);
        Assert.assertThat((plainConnector instanceof ServerConnector), is(true));
        ServerConnector connector = ((ServerConnector) (plainConnector));
        Assert.assertThat(connector.getServer(), is(server));
        Assert.assertThat(connector.getConnectionFactories().size(), is(1));
        ConnectionFactory connectionFactory = connector.getConnectionFactories().iterator().next();
        Assert.assertThat((connectionFactory instanceof HttpConnectionFactory), is(true));
    }

    @Test
    public void shouldAddSSLSocketConnector() throws Exception {
        ArgumentCaptor<Connector> captor = ArgumentCaptor.forClass(Connector.class);
        jetty9Server.configure();
        Mockito.verify(server, Mockito.times(2)).addConnector(captor.capture());
        List<Connector> connectors = captor.getAllValues();
        Connector sslConnector = connectors.get(1);
        Assert.assertThat((sslConnector instanceof ServerConnector), is(true));
        ServerConnector connector = ((ServerConnector) (sslConnector));
        Assert.assertThat(connector.getServer(), is(server));
        Assert.assertThat(connector.getConnectionFactories().size(), is(2));
        Iterator<ConnectionFactory> iterator = connector.getConnectionFactories().iterator();
        ConnectionFactory first = iterator.next();
        ConnectionFactory second = iterator.next();
        Assert.assertThat((first instanceof SslConnectionFactory), is(true));
        SslConnectionFactory sslConnectionFactory = ((SslConnectionFactory) (first));
        Assert.assertThat(sslConnectionFactory.getProtocol(), is("SSL"));
        Assert.assertThat(sslConnectionFactory.getNextProtocol(), is("HTTP/1.1"));
        Assert.assertThat((second instanceof HttpConnectionFactory), is(true));
    }

    @Test
    public void shouldAddRootRequestHandler() throws Exception {
        jetty9Server.configure();
        jetty9Server.startHandlers();
        ContextHandler rootRequestHandler = getLoadedHandlers().get(GoServerLoadingIndicationHandler.class);
        Assert.assertThat(rootRequestHandler.getContextPath(), is("/"));
    }

    @Test
    public void shouldAddDefaultHeadersForRootContext() throws Exception {
        jetty9Server.configure();
        jetty9Server.startHandlers();
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Request baseRequest = Mockito.mock(Request.class);
        Mockito.when(baseRequest.getDispatcherType()).thenReturn(REQUEST);
        Mockito.when(baseRequest.getHttpFields()).thenReturn(Mockito.mock(HttpFields.class));
        ContextHandler rootPathHandler = getLoadedHandlers().get(GoServerLoadingIndicationHandler.class);
        rootPathHandler.setServer(server);
        rootPathHandler.start();
        rootPathHandler.handle("/something", baseRequest, request, response);
        Mockito.verify(response).setHeader("X-XSS-Protection", "1; mode=block");
        Mockito.verify(response).setHeader("X-Content-Type-Options", "nosniff");
        Mockito.verify(response).setHeader("X-Frame-Options", "SAMEORIGIN");
        Mockito.verify(response).setHeader("X-UA-Compatible", "chrome=1");
    }

    @Test
    public void shouldAddResourceHandlerForAssets() throws Exception {
        jetty9Server.configure();
        jetty9Server.startHandlers();
        ContextHandler assetsContextHandler = getLoadedHandlers().get(AssetsContextHandler.class);
        Assert.assertThat(assetsContextHandler.getContextPath(), is("context/assets"));
    }

    @Test
    public void shouldAddWebAppContextHandler() throws Exception {
        jetty9Server.configure();
        jetty9Server.startHandlers();
        WebAppContext webAppContext = ((WebAppContext) (getLoadedHandlers().get(WebAppContext.class)));
        Assert.assertThat(webAppContext, instanceOf(WebAppContext.class));
        List<String> configClasses = new java.util.ArrayList(Arrays.asList(webAppContext.getConfigurationClasses()));
        Assert.assertThat(configClasses.contains(WebInfConfiguration.class.getCanonicalName()), is(true));
        Assert.assertThat(configClasses.contains(WebXmlConfiguration.class.getCanonicalName()), is(true));
        Assert.assertThat(configClasses.contains(JettyWebXmlConfiguration.class.getCanonicalName()), is(true));
        Assert.assertThat(webAppContext.getContextPath(), is("context"));
        Assert.assertThat(webAppContext.getWar(), is("cruise.war"));
        Assert.assertThat(webAppContext.isParentLoaderPriority(), is(true));
        Assert.assertThat(webAppContext.getDefaultsDescriptor(), is("jar:file:cruise.war!/WEB-INF/webdefault.xml"));
    }

    @Test
    public void shouldSetStopAtShutdown() throws Exception {
        jetty9Server.configure();
        Mockito.verify(server).setStopAtShutdown(true);
    }

    @Test
    public void shouldSetSessionMaxInactiveInterval() throws Exception {
        jetty9Server.configure();
        jetty9Server.setSessionConfig();
        jetty9Server.startHandlers();
        WebAppContext webAppContext = ((WebAppContext) (getLoadedHandlers().get(WebAppContext.class)));
        Assert.assertThat(webAppContext.getSessionHandler().getMaxInactiveInterval(), is(1234));
    }

    @Test
    public void shouldSetSessionCookieConfig() throws Exception {
        Mockito.when(systemEnvironment.isSessionCookieSecure()).thenReturn(true);
        jetty9Server.configure();
        jetty9Server.setSessionConfig();
        jetty9Server.startHandlers();
        WebAppContext webAppContext = ((WebAppContext) (getLoadedHandlers().get(WebAppContext.class)));
        SessionCookieConfig sessionCookieConfig = webAppContext.getSessionHandler().getSessionCookieConfig();
        Assert.assertThat(sessionCookieConfig.isHttpOnly(), is(true));
        Assert.assertThat(sessionCookieConfig.isSecure(), is(true));
        Assert.assertThat(sessionCookieConfig.getMaxAge(), is(5678));
        Mockito.when(systemEnvironment.isSessionCookieSecure()).thenReturn(false);
        jetty9Server.setSessionConfig();
        Assert.assertThat(sessionCookieConfig.isSecure(), is(false));
    }

    @Test
    public void shouldAddExtraJarsIntoClassPath() throws Exception {
        jetty9Server.configure();
        jetty9Server.addExtraJarsToClasspath("test-addons/some-addon-dir/addon-1.JAR,test-addons/some-addon-dir/addon-2.jar");
        jetty9Server.startHandlers();
        WebAppContext webAppContext = ((WebAppContext) (getLoadedHandlers().get(WebAppContext.class)));
        Assert.assertThat(webAppContext.getExtraClasspath(), is(("test-addons/some-addon-dir/addon-1.JAR,test-addons/some-addon-dir/addon-2.jar," + (configDir))));
    }

    @Test
    public void shouldSetInitParams() throws Exception {
        jetty9Server.configure();
        jetty9Server.setInitParameter("name", "value");
        jetty9Server.startHandlers();
        WebAppContext webAppContext = ((WebAppContext) (getLoadedHandlers().get(WebAppContext.class)));
        Assert.assertThat(webAppContext.getInitParameter("name"), is("value"));
    }

    @Test
    public void shouldReplaceJettyXmlIfItDoesNotContainCorrespondingJettyVersionNumber() throws IOException {
        File jettyXml = temporaryFolder.newFile("jetty.xml");
        Mockito.when(systemEnvironment.getJettyConfigFile()).thenReturn(jettyXml);
        String originalContent = "jetty-v6.2.3\nsome other local changes";
        FileUtils.writeStringToFile(jettyXml, originalContent, StandardCharsets.UTF_8);
        jetty9Server.replaceJettyXmlIfItBelongsToADifferentVersion(systemEnvironment.getJettyConfigFile());
        Assert.assertThat(FileUtils.readFileToString(systemEnvironment.getJettyConfigFile(), StandardCharsets.UTF_8), is(FileUtils.readFileToString(new File(getClass().getResource("config/jetty.xml").getPath()), StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldNotReplaceJettyXmlIfItAlreadyContainsCorrespondingVersionNumber() throws IOException {
        File jettyXml = temporaryFolder.newFile("jetty.xml");
        Mockito.when(systemEnvironment.getJettyConfigFile()).thenReturn(jettyXml);
        String originalContent = "jetty-v9.4.8.v20171121\nsome other local changes";
        FileUtils.writeStringToFile(jettyXml, originalContent, StandardCharsets.UTF_8);
        jetty9Server.replaceJettyXmlIfItBelongsToADifferentVersion(systemEnvironment.getJettyConfigFile());
        Assert.assertThat(FileUtils.readFileToString(systemEnvironment.getJettyConfigFile(), StandardCharsets.UTF_8), is(originalContent));
    }

    @Test
    public void shouldSetErrorHandlerForServer() throws Exception {
        jetty9Server.configure();
        Mockito.verify(server).addBean(ArgumentMatchers.any(JettyCustomErrorPageHandler.class));
    }

    @Test
    public void shouldSetErrorHandlerForWebAppContext() throws Exception {
        jetty9Server.configure();
        jetty9Server.startHandlers();
        WebAppContext webAppContext = ((WebAppContext) (getLoadedHandlers().get(WebAppContext.class)));
        Assert.assertThat(((webAppContext.getErrorHandler()) instanceof JettyCustomErrorPageHandler), is(true));
    }

    @Test
    public void shouldAddGzipHandlerAtWebAppContextLevel() throws Exception {
        jetty9Server.configure();
        jetty9Server.startHandlers();
        WebAppContext webAppContext = ((WebAppContext) (getLoadedHandlers().get(WebAppContext.class)));
        Assert.assertThat(webAppContext.getGzipHandler(), is(not(nullValue())));
    }

    @Test
    public void shouldHaveAHandlerCollectionAtServerLevel_ToAllowRequestLoggingHandlerToBeAdded() throws Exception {
        jetty9Server.configure();
        jetty9Server.startHandlers();
        Assert.assertThat(serverLevelHandler, instanceOf(HandlerCollection.class));
        Assert.assertThat(serverLevelHandler, not(instanceOf(ContextHandlerCollection.class)));
        Handler[] contentsOfServerLevelHandler = getHandlers();
        Assert.assertThat(contentsOfServerLevelHandler.length, is(1));
        Assert.assertThat(contentsOfServerLevelHandler[0], instanceOf(ContextHandlerCollection.class));
    }
}

