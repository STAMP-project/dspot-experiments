/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.websocket.jsr356.server;


import ServerEndpointConfig.Configurator;
import java.util.Iterator;
import java.util.ServiceLoader;
import javax.websocket.server.ServerEndpointConfig;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Test the JettyServerEndpointConfigurator impl.
 */
public class JettyServerEndpointConfiguratorTest {
    @Test
    public void testServiceLoader() {
        System.out.printf("Service Name: %s%n", Configurator.class.getName());
        ServiceLoader<ServerEndpointConfig.Configurator> loader = ServiceLoader.load(javax.websocket.server.ServerEndpointConfig.Configurator.class);
        MatcherAssert.assertThat("loader", loader, Matchers.notNullValue());
        Iterator<ServerEndpointConfig.Configurator> iter = loader.iterator();
        MatcherAssert.assertThat("loader.iterator", iter, Matchers.notNullValue());
        MatcherAssert.assertThat("loader.iterator.hasNext", iter.hasNext(), Matchers.is(true));
        ServerEndpointConfig.Configurator configr = iter.next();
        MatcherAssert.assertThat("Configurator", configr, Matchers.notNullValue());
        MatcherAssert.assertThat("Configurator type", configr, Matchers.instanceOf(ContainerDefaultConfigurator.class));
    }
}

