/**
 * Copyright (C) 2013 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file
 * in the distribution for a full listing of individual contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jboss.as.test.integration.management.cli;


import java.io.File;
import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.cli.CommandLineException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DeployWithRuntimeNameTestCase {
    @ArquillianResource
    URL url;

    private CommandContext ctx;

    private File warFile;

    private String baseUrl;

    public static final String RUNTIME_NAME = "SimpleServlet.war";

    public static final String OTHER_RUNTIME_NAME = "OtherSimpleServlet.war";

    private static final String APP_NAME = "simple1";

    private static final String OTHER_APP_NAME = "simple2";

    @Test
    public void testDeployWithSameRuntimeName() throws Exception {
        warFile = createWarFile("Version1");
        ctx.handle(buildDeployCommand(DeployWithRuntimeNameTestCase.RUNTIME_NAME, DeployWithRuntimeNameTestCase.APP_NAME));
        checkURL("SimpleServlet/page.html", "Version1", false);
        checkURL("SimpleServlet/hello", "SimpleHelloWorldServlet", false);
        warFile = createWarFile("Shouldn't be deployed, as runtime already exist");
        try {
            ctx.handle(buildDeployCommand(DeployWithRuntimeNameTestCase.RUNTIME_NAME, DeployWithRuntimeNameTestCase.OTHER_APP_NAME));
        } catch (CommandLineException ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("WFLYSRV0205"));
        }
        checkURL("SimpleServlet/hello", "SimpleHelloWorldServlet", false);
        checkURL("SimpleServlet/page.html", "Version1", false);
    }

    @Test
    public void testDeployWithDifferentRuntimeName() throws Exception {
        warFile = createWarFile("Version1");
        ctx.handle(buildDeployCommand(DeployWithRuntimeNameTestCase.RUNTIME_NAME, DeployWithRuntimeNameTestCase.APP_NAME));
        checkURL("SimpleServlet/hello", "SimpleHelloWorldServlet", false);
        checkURL("SimpleServlet/page.html", "Version1", false);
        warFile = createWarFile("Version2");
        ctx.handle(buildDeployCommand(DeployWithRuntimeNameTestCase.OTHER_RUNTIME_NAME, DeployWithRuntimeNameTestCase.OTHER_APP_NAME));
        checkURL("SimpleServlet/hello", "SimpleHelloWorldServlet", false);
        checkURL("SimpleServlet/page.html", "Version1", false);
        checkURL("OtherSimpleServlet/hello", "SimpleHelloWorldServlet", false);
        checkURL("OtherSimpleServlet/page.html", "Version2", false);
    }

    @Test
    public void testUndeployWithDisabledSameRuntimeName() throws Exception {
        warFile = createWarFile("Version1");
        ctx.handle(buildDeployCommand(DeployWithRuntimeNameTestCase.RUNTIME_NAME, DeployWithRuntimeNameTestCase.APP_NAME));
        checkURL("SimpleServlet/hello", "SimpleHelloWorldServlet", false);
        checkURL("SimpleServlet/page.html", "Version1", false);
        warFile = createWarFile("Version2");
        ctx.handle(buildDisabledDeployCommand(DeployWithRuntimeNameTestCase.RUNTIME_NAME, DeployWithRuntimeNameTestCase.OTHER_APP_NAME));
        checkURL("SimpleServlet/hello", "SimpleHelloWorldServlet", false);
        checkURL("SimpleServlet/page.html", "Version1", false);
        ctx.handle(buildUndeployCommand(DeployWithRuntimeNameTestCase.OTHER_APP_NAME));
        checkURL("SimpleServlet/hello", "SimpleHelloWorldServlet", false);
        checkURL("SimpleServlet/page.html", "Version1", false);
    }
}

