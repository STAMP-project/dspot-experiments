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


import java.io.File;
import org.eclipse.jetty.deploy.test.XmlConfiguredJetty;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@Disabled("See issue #1200")
@ExtendWith(WorkDirExtension.class)
public class WebAppProviderTest {
    public WorkDir testdir;

    private static XmlConfiguredJetty jetty;

    private boolean symlinkSupported = false;

    @Test
    public void testStartupContext() {
        // Check Server for Handlers
        WebAppProviderTest.jetty.assertWebAppContextsExists("/bar", "/foo");
        File workDir = WebAppProviderTest.jetty.getJettyDir("workish");
        // Test for regressions
        WebAppProviderTest.assertDirNotExists("root of work directory", workDir, "webinf");
        WebAppProviderTest.assertDirNotExists("root of work directory", workDir, "jsp");
        // Test for correct behaviour
        Assertions.assertTrue(WebAppProviderTest.hasJettyGeneratedPath(workDir, "foo.war"), ("Should have generated directory in work directory: " + workDir));
    }

    @Test
    public void testStartupSymlinkContext() {
        Assumptions.assumeTrue(symlinkSupported);
        // Check for path
        File barLink = WebAppProviderTest.jetty.getJettyDir("webapps/bar.war");
        Assertions.assertTrue(barLink.exists(), ("bar.war link exists: " + (barLink.toString())));
        Assertions.assertTrue(barLink.isFile(), ("bar.war link isFile: " + (barLink.toString())));
        // Check Server for expected Handlers
        WebAppProviderTest.jetty.assertWebAppContextsExists("/bar", "/foo");
        // Test for expected work/temp directory behaviour
        File workDir = WebAppProviderTest.jetty.getJettyDir("workish");
        Assertions.assertTrue(WebAppProviderTest.hasJettyGeneratedPath(workDir, "bar.war"), ("Should have generated directory in work directory: " + workDir));
    }
}

