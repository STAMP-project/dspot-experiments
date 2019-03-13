/**
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.moquette.integration;


import BrokerConstants.AUTHENTICATOR_CLASS_NAME;
import BrokerConstants.AUTHORIZATOR_CLASS_NAME;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.security.IAuthenticator;
import io.moquette.broker.security.IAuthorizatorPolicy;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


public class ConfigurationClassLoaderTest implements IAuthenticator , IAuthorizatorPolicy {
    Server m_server;

    IConfig m_config;

    @Test
    public void loadAuthenticator() throws Exception {
        Properties props = new Properties(IntegrationUtils.prepareTestProperties());
        props.setProperty(AUTHENTICATOR_CLASS_NAME, getClass().getName());
        startServer(props);
        Assert.assertTrue(true);
    }

    @Test
    public void loadAuthorizator() throws Exception {
        Properties props = new Properties(IntegrationUtils.prepareTestProperties());
        props.setProperty(AUTHORIZATOR_CLASS_NAME, getClass().getName());
        startServer(props);
        Assert.assertTrue(true);
    }
}

