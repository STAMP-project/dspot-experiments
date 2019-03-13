/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.extension.undertow;


import Constants.HOST;
import Constants.MOD_CLUSTER;
import Constants.REVERSE_PROXY;
import FailedOperationTransformationConfig.ChainedConfig;
import FailedOperationTransformationConfig.DISCARDED_RESOURCE;
import FailedOperationTransformationConfig.REJECTED_RESOURCE;
import ModClusterDefinition.FAILOVER_STRATEGY;
import ModClusterDefinition.MAX_RETRIES;
import ModelTestControllerVersion.EAP_7_0_0;
import ModelTestControllerVersion.EAP_7_1_0;
import UndertowExtension.AJP_LISTENER_PATH;
import UndertowExtension.BYTE_BUFFER_POOL_PATH;
import UndertowExtension.HOST_PATH;
import UndertowExtension.HTTPS_LISTENER_PATH;
import UndertowExtension.HTTP_LISTENER_PATH;
import UndertowExtension.PATH_APPLICATION_SECURITY_DOMAIN;
import UndertowExtension.PATH_FILTERS;
import UndertowExtension.PATH_HANDLERS;
import UndertowExtension.PATH_HTTP_INVOKER;
import UndertowExtension.PATH_SERVLET_CONTAINER;
import UndertowExtension.PATH_SSO;
import UndertowExtension.SERVER_PATH;
import UndertowExtension.SUBSYSTEM_NAME;
import UndertowExtension.SUBSYSTEM_PATH;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.model.test.FailedOperationTransformationConfig;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.junit.Test;
import org.wildfly.extension.undertow.filters.ModClusterDefinition;
import org.wildfly.extension.undertow.handlers.ReverseProxyHandler;

import static HostDefinition.QUEUE_REQUESTS_ON_START;
import static HttpListenerResourceDefinition.ALLOW_UNESCAPED_CHARACTERS_IN_URL;
import static HttpListenerResourceDefinition.CERTIFICATE_FORWARDING;
import static HttpListenerResourceDefinition.PROXY_ADDRESS_FORWARDING;
import static HttpListenerResourceDefinition.PROXY_PROTOCOL;
import static HttpListenerResourceDefinition.REQUIRE_HOST_HTTP11;
import static HttpListenerResourceDefinition.RFC6265_COOKIE_VALIDATION;
import static HttpsListenerResourceDefinition.SSL_CONTEXT;
import static ServletContainerDefinition.DEFAULT_COOKIE_VERSION;
import static ServletContainerDefinition.DISABLE_FILE_WATCH_SERVICE;
import static ServletContainerDefinition.DISABLE_SESSION_ID_REUSE;
import static ServletContainerDefinition.FILE_CACHE_MAX_FILE_SIZE;
import static ServletContainerDefinition.FILE_CACHE_METADATA_SIZE;
import static ServletContainerDefinition.FILE_CACHE_TIME_TO_LIVE;


/**
 * This is the barebone test example that tests subsystem
 *
 * @author <a href="mailto:tomaz.cerar@redhat.com">Tomaz Cerar</a>
 */
public class UndertowTransformersTestCase extends AbstractSubsystemTest {
    private static final ModelVersion EAP7_0_0 = ModelVersion.create(3, 1, 0);

    private static final ModelVersion EAP7_1_0 = ModelVersion.create(4, 0, 0);

    private static final ModelVersion EAP7_2_0 = ModelVersion.create(7, 0, 0);

    public UndertowTransformersTestCase() {
        super(SUBSYSTEM_NAME, new UndertowExtension());
    }

    @Test
    public void testTransformersEAP_7_0_0() throws Exception {
        testTransformers(EAP_7_0_0, UndertowTransformersTestCase.EAP7_0_0);
    }

    @Test
    public void testTransformersEAP_7_1_0() throws Exception {
        testTransformers(EAP_7_1_0, UndertowTransformersTestCase.EAP7_1_0);
    }

    @Test
    public void testTransformersEAP_7_2_0() throws Exception {
        // TODO Enable once we can transform to 7.2
        // testTransformers(ModelTestControllerVersion.EAP_7_1_0, "7.2", EAP7_2_0);
    }

    @Test
    public void testRejectTransformersEAP_7_0_0() throws Exception {
        PathAddress subsystemAddress = PathAddress.pathAddress(SUBSYSTEM_PATH);
        PathAddress serverAddress = subsystemAddress.append(SERVER_PATH);
        PathAddress hostAddress = serverAddress.append(HOST_PATH);
        PathAddress httpsAddress = serverAddress.append(HTTPS_LISTENER_PATH);
        PathAddress ajpAddress = serverAddress.append(AJP_LISTENER_PATH);
        PathAddress httpAddress = serverAddress.append(HTTP_LISTENER_PATH);
        PathAddress reverseProxy = subsystemAddress.append(PATH_HANDLERS).append(REVERSE_PROXY);
        PathAddress reverseProxyServerAddress = reverseProxy.append(HOST);
        PathAddress modClusterPath = subsystemAddress.append(PATH_FILTERS).append(MOD_CLUSTER);
        PathAddress servletContainer = subsystemAddress.append(PATH_SERVLET_CONTAINER);
        PathAddress byteBufferPath = subsystemAddress.append(BYTE_BUFFER_POOL_PATH);
        doRejectTest(EAP_7_0_0, UndertowTransformersTestCase.EAP7_0_0, new FailedOperationTransformationConfig().addFailedAttribute(byteBufferPath, REJECTED_RESOURCE).addFailedAttribute(hostAddress, new FailedOperationTransformationConfig.NewAttributesConfig(QUEUE_REQUESTS_ON_START)).addFailedAttribute(httpAddress, new FailedOperationTransformationConfig.NewAttributesConfig(REQUIRE_HOST_HTTP11, PROXY_PROTOCOL, ALLOW_UNESCAPED_CHARACTERS_IN_URL, RFC6265_COOKIE_VALIDATION)).addFailedAttribute(httpsAddress, new FailedOperationTransformationConfig.NewAttributesConfig(REQUIRE_HOST_HTTP11, PROXY_ADDRESS_FORWARDING, CERTIFICATE_FORWARDING, SSL_CONTEXT, HttpsListenerResourceDefinition.ALLOW_UNESCAPED_CHARACTERS_IN_URL, PROXY_PROTOCOL, RFC6265_COOKIE_VALIDATION)).addFailedAttribute(reverseProxy, new FailedOperationTransformationConfig.NewAttributesConfig(ReverseProxyHandler.MAX_RETRIES)).addFailedAttribute(reverseProxyServerAddress, new FailedOperationTransformationConfig.NewAttributesConfig(Constants.SSL_CONTEXT)).addFailedAttribute(hostAddress.append(PATH_HTTP_INVOKER), REJECTED_RESOURCE).addFailedAttribute(subsystemAddress.append(PATH_APPLICATION_SECURITY_DOMAIN), DISCARDED_RESOURCE).addFailedAttribute(modClusterPath, new FailedOperationTransformationConfig.RejectExpressionsConfig(MAX_AJP_PACKET_SIZE)).addFailedAttribute(modClusterPath, ChainedConfig.createBuilder(HttpsListenerResourceDefinition.SSL_CONTEXT, MAX_RETRIES, FAILOVER_STRATEGY, MAX_AJP_PACKET_SIZE).addConfig(new FailedOperationTransformationConfig.RejectExpressionsConfig(MAX_AJP_PACKET_SIZE)).addConfig(new FailedOperationTransformationConfig.NewAttributesConfig(HttpsListenerResourceDefinition.SSL_CONTEXT, ModClusterDefinition.MAX_RETRIES, ModClusterDefinition.FAILOVER_STRATEGY)).build()).addFailedAttribute(subsystemAddress.append(PATH_APPLICATION_SECURITY_DOMAIN).append(PATH_SSO), REJECTED_RESOURCE).addFailedAttribute(subsystemAddress.append(PATH_APPLICATION_SECURITY_DOMAIN), REJECTED_RESOURCE).addFailedAttribute(servletContainer, new FailedOperationTransformationConfig.NewAttributesConfig(DEFAULT_COOKIE_VERSION, FILE_CACHE_MAX_FILE_SIZE, FILE_CACHE_METADATA_SIZE, FILE_CACHE_TIME_TO_LIVE, DISABLE_FILE_WATCH_SERVICE, DISABLE_SESSION_ID_REUSE)).addFailedAttribute(ajpAddress, new FailedOperationTransformationConfig.NewAttributesConfig(ListenerResourceDefinition.ALLOW_UNESCAPED_CHARACTERS_IN_URL, ListenerResourceDefinition.RFC6265_COOKIE_VALIDATION)));
    }

    @Test
    public void testRejectTransformersEAP_7_1_0() throws Exception {
        PathAddress subsystemAddress = PathAddress.pathAddress(SUBSYSTEM_PATH);
        PathAddress serverAddress = subsystemAddress.append(SERVER_PATH);
        PathAddress hostAddress = serverAddress.append(HOST_PATH);
        PathAddress httpsAddress = serverAddress.append(HTTPS_LISTENER_PATH);
        PathAddress ajpAddress = serverAddress.append(AJP_LISTENER_PATH);
        PathAddress httpAddress = serverAddress.append(HTTP_LISTENER_PATH);
        PathAddress servletContainer = subsystemAddress.append(PATH_SERVLET_CONTAINER);
        PathAddress byteBufferPath = subsystemAddress.append(BYTE_BUFFER_POOL_PATH);
        doRejectTest(EAP_7_1_0, UndertowTransformersTestCase.EAP7_1_0, new FailedOperationTransformationConfig().addFailedAttribute(byteBufferPath, REJECTED_RESOURCE).addFailedAttribute(hostAddress, new FailedOperationTransformationConfig.NewAttributesConfig(QUEUE_REQUESTS_ON_START)).addFailedAttribute(httpAddress, new FailedOperationTransformationConfig.NewAttributesConfig(PROXY_PROTOCOL, ALLOW_UNESCAPED_CHARACTERS_IN_URL)).addFailedAttribute(httpsAddress, new FailedOperationTransformationConfig.NewAttributesConfig(PROXY_PROTOCOL, HttpsListenerResourceDefinition.ALLOW_UNESCAPED_CHARACTERS_IN_URL)).addFailedAttribute(servletContainer, new FailedOperationTransformationConfig.NewAttributesConfig(DEFAULT_COOKIE_VERSION, FILE_CACHE_MAX_FILE_SIZE, FILE_CACHE_METADATA_SIZE, FILE_CACHE_TIME_TO_LIVE)).addFailedAttribute(ajpAddress, new FailedOperationTransformationConfig.NewAttributesConfig(ListenerResourceDefinition.ALLOW_UNESCAPED_CHARACTERS_IN_URL)));
    }

    @Test
    public void testRejectTransformersEAP_7_2_0() throws Exception {
        // TODO Enable once we can transform to 7.2
        // doRejectTest(ModelTestControllerVersion.EAP_7_1_0, EAP7_2_0, new FailedOperationTransformationConfig()
        // );
    }

    @Test
    public void testConvertTransformersEAP_7_1_0() throws Exception {
        // https://issues.jboss.org/browse/WFLY-9675 Fix max-post-size LongRangeValidator min to 0.
        // Test Listener attribute max-post-size value 0 is converted to Long.MAX
        doConvertTest(EAP_7_1_0, UndertowTransformersTestCase.EAP7_1_0);
    }
}

