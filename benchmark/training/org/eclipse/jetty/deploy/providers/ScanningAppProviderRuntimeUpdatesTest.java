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
package org.eclipse.jetty.deploy.providers;


import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.deploy.test.XmlConfiguredJetty;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;


/**
 * Similar in scope to {@link ScanningAppProviderStartupTest}, except is concerned with the modification of existing
 * deployed webapps due to incoming changes identified by the {@link ScanningAppProvider}.
 */
@ExtendWith(WorkDirExtension.class)
public class ScanningAppProviderRuntimeUpdatesTest {
    private static final Logger LOG = Log.getLogger(ScanningAppProviderRuntimeUpdatesTest.class);

    public WorkDir testdir;

    private static XmlConfiguredJetty jetty;

    private final AtomicInteger _scans = new AtomicInteger();

    private int _providers;

    /**
     * Simple webapp deployment after startup of server.
     *
     * @throws IOException
     * 		on test failure
     */
    @Test
    public void testAfterStartupContext() throws IOException {
        ScanningAppProviderRuntimeUpdatesTest.jetty.copyWebapp("foo-webapp-1.war", "foo.war");
        ScanningAppProviderRuntimeUpdatesTest.jetty.copyWebapp("foo.xml", "foo.xml");
        waitForDirectoryScan();
        waitForDirectoryScan();
        ScanningAppProviderRuntimeUpdatesTest.jetty.assertWebAppContextsExists("/foo");
    }

    /**
     * Simple webapp deployment after startup of server, and then removal of the webapp.
     *
     * @throws IOException
     * 		on test failure
     */
    @Test
    public void testAfterStartupThenRemoveContext() throws IOException {
        ScanningAppProviderRuntimeUpdatesTest.jetty.copyWebapp("foo-webapp-1.war", "foo.war");
        ScanningAppProviderRuntimeUpdatesTest.jetty.copyWebapp("foo.xml", "foo.xml");
        waitForDirectoryScan();
        waitForDirectoryScan();
        ScanningAppProviderRuntimeUpdatesTest.jetty.assertWebAppContextsExists("/foo");
        ScanningAppProviderRuntimeUpdatesTest.jetty.removeWebapp("foo.war");
        ScanningAppProviderRuntimeUpdatesTest.jetty.removeWebapp("foo.xml");
        waitForDirectoryScan();
        waitForDirectoryScan();
        ScanningAppProviderRuntimeUpdatesTest.jetty.assertNoWebAppContexts();
    }

    /**
     * Simple webapp deployment after startup of server, and then removal of the webapp.
     *
     * @throws Exception
     * 		on test failure
     */
    // This test will not work on Windows as second war file would, not be written over the first one because of a file lock
    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void testAfterStartupThenUpdateContext() throws Exception {
        ScanningAppProviderRuntimeUpdatesTest.jetty.copyWebapp("foo-webapp-1.war", "foo.war");
        ScanningAppProviderRuntimeUpdatesTest.jetty.copyWebapp("foo.xml", "foo.xml");
        waitForDirectoryScan();
        waitForDirectoryScan();
        ScanningAppProviderRuntimeUpdatesTest.jetty.assertWebAppContextsExists("/foo");
        // Test that webapp response contains "-1"
        ScanningAppProviderRuntimeUpdatesTest.jetty.assertResponseContains("/foo/info", "FooServlet-1");
        waitForDirectoryScan();
        // System.err.println("Updating war files");
        ScanningAppProviderRuntimeUpdatesTest.jetty.copyWebapp("foo.xml", "foo.xml");// essentially "touch" the context xml

        ScanningAppProviderRuntimeUpdatesTest.jetty.copyWebapp("foo-webapp-2.war", "foo.war");
        // This should result in the existing foo.war being replaced with the new foo.war
        waitForDirectoryScan();
        waitForDirectoryScan();
        ScanningAppProviderRuntimeUpdatesTest.jetty.assertWebAppContextsExists("/foo");
        // Test that webapp response contains "-2"
        ScanningAppProviderRuntimeUpdatesTest.jetty.assertResponseContains("/foo/info", "FooServlet-2");
    }
}

