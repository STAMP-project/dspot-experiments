/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.management.cli;


import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.as.test.integration.management.util.CLITestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author btison
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ArchiveDefaultScriptNamesTestCase {
    private static File cliArchiveFile;

    @ArquillianResource
    URL url;

    @Test
    public void testDeployUndeployArchive() throws Exception {
        final CommandContext ctx = CLITestUtil.getCommandContext();
        try {
            ctx.connectController();
            ctx.handle(("deploy " + (ArchiveDefaultScriptNamesTestCase.cliArchiveFile.getAbsolutePath())));
            // check that now both wars are deployed
            String response = HttpRequest.get(((getBaseURL(url)) + "deployment0/SimpleServlet"), 10, TimeUnit.SECONDS);
            Assert.assertTrue(("Invalid response: " + response), ((response.indexOf("SimpleServlet")) >= 0));
            response = HttpRequest.get(((getBaseURL(url)) + "deployment1/SimpleServlet"), 10, TimeUnit.SECONDS);
            Assert.assertTrue(("Invalid response: " + response), ((response.indexOf("SimpleServlet")) >= 0));
            Assert.assertTrue(checkUndeployed(((getBaseURL(url)) + "deployment2/SimpleServlet")));
            ctx.handle((("undeploy " + "--path=") + (ArchiveDefaultScriptNamesTestCase.cliArchiveFile.getAbsolutePath())));
            // check that both wars are undeployed
            Assert.assertTrue(checkUndeployed(((getBaseURL(url)) + "deployment0/SimpleServlet")));
            Assert.assertTrue(checkUndeployed(((getBaseURL(url)) + "deployment1/SimpleServlet")));
        } finally {
            ctx.terminateSession();
        }
    }
}

