/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
import java.util.Set;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.cli.CommandContext;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Alexey Loubyansky
 */
@RunWith(Arquillian.class)
@RunAsClient
public class UndeployWildcardTestCase {
    @ArquillianResource
    URL url;

    private static File[] appFiles;

    private CommandContext ctx;

    private Set<String> afterTestDeployments;

    @Test
    public void testUndeployAllWars() throws Exception {
        ctx.handle("undeploy *.war");
        afterTestDeployments.add(UndeployWildcardTestCase.appFiles[3].getName());
    }

    @Test
    public void testUndeployCliTestApps() throws Exception {
        ctx.handle("undeploy cli-test-app*");
        afterTestDeployments.add(UndeployWildcardTestCase.appFiles[2].getName());
    }

    @Test
    public void testUndeployTestAps() throws Exception {
        ctx.handle("undeploy *test-ap*");
        afterTestDeployments.add(UndeployWildcardTestCase.appFiles[2].getName());
    }

    @Test
    public void testUndeployTestAs() throws Exception {
        ctx.handle("undeploy *test-a*");
    }

    @Test
    public void testUndeployTestAWARs() throws Exception {
        ctx.handle("undeploy *test-a*.war");
        afterTestDeployments.add(UndeployWildcardTestCase.appFiles[3].getName());
    }
}

