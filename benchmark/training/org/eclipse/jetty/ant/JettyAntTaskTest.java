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
package org.eclipse.jetty.ant;


import java.net.HttpURLConnection;
import java.net.URI;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class JettyAntTaskTest {
    @Test
    public void testConnectorTask() throws Exception {
        AntBuild build = new AntBuild(MavenTestingUtils.getTestResourceFile("connector-test.xml").getAbsolutePath());
        build.start();
        URI uri = new URI(((("http://" + (build.getJettyHost())) + ":") + (build.getJettyPort())));
        HttpURLConnection connection = ((HttpURLConnection) (uri.toURL().openConnection()));
        connection.connect();
        MatcherAssert.assertThat("response code is 404", connection.getResponseCode(), Matchers.is(404));
        build.stop();
    }

    @Test
    public void testWebApp() throws Exception {
        AntBuild build = new AntBuild(MavenTestingUtils.getTestResourceFile("webapp-test.xml").getAbsolutePath());
        build.start();
        URI uri = new URI((((("http://" + (build.getJettyHost())) + ":") + (build.getJettyPort())) + "/"));
        HttpURLConnection connection = ((HttpURLConnection) (uri.toURL().openConnection()));
        connection.connect();
        MatcherAssert.assertThat("response code is 200", connection.getResponseCode(), Matchers.is(200));
        System.err.println("Stop build!");
        build.stop();
    }
}

