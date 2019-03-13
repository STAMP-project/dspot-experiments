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
package org.jboss.as.test.integration.domain.management.cli;


import DomainControllerLogger.ROOT_LOGGER;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.jboss.as.test.integration.management.base.AbstractCliTestBase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
public class DomainDeployWithRuntimeNameTestCase extends AbstractCliTestBase {
    private File warFile;

    private static String[] serverGroups;

    public static final String RUNTIME_NAME = "SimpleServlet.war";

    public static final String OTHER_RUNTIME_NAME = "OtherSimpleServlet.war";

    private static final String APP_NAME = "simple1";

    private static final String OTHER_APP_NAME = "simple2";

    @Test
    public void testDeployWithSameRuntimeNameOnSameServerGroup() throws Exception {
        // deploy to group servers
        warFile = createWarFile("Version1");
        cli.sendLine(buildDeployCommand(DomainDeployWithRuntimeNameTestCase.serverGroups[0], DomainDeployWithRuntimeNameTestCase.RUNTIME_NAME, DomainDeployWithRuntimeNameTestCase.APP_NAME));
        checkURL("/SimpleServlet/hello", "SimpleHelloWorldServlet", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
        checkURL("/SimpleServlet/page.html", "Version1", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
        checkURL("/SimpleServlet/hello", "SimpleHelloWorldServlet", DomainDeployWithRuntimeNameTestCase.serverGroups[1], true);
        warFile = createWarFile("Shouldn't be deployed, as runtime already exist");
        cli.sendLine(buildDeployCommand(DomainDeployWithRuntimeNameTestCase.serverGroups[0], DomainDeployWithRuntimeNameTestCase.RUNTIME_NAME, DomainDeployWithRuntimeNameTestCase.OTHER_APP_NAME), true);
        Assert.assertThat(cli.readOutput(), CoreMatchers.containsString(ROOT_LOGGER.runtimeNameMustBeUnique(DomainDeployWithRuntimeNameTestCase.APP_NAME, DomainDeployWithRuntimeNameTestCase.RUNTIME_NAME, DomainDeployWithRuntimeNameTestCase.serverGroups[0]).getMessage()));
        checkURL("/SimpleServlet/hello", "SimpleHelloWorldServlet", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
        checkURL("/SimpleServlet/page.html", "Version1", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
    }

    @Test
    public void testDeployWithSameRuntimeNameOnDifferentServerGroup() throws Exception {
        // deploy to group servers
        warFile = createWarFile("Version1");
        cli.sendLine(buildDeployCommand(DomainDeployWithRuntimeNameTestCase.serverGroups[0], DomainDeployWithRuntimeNameTestCase.RUNTIME_NAME, DomainDeployWithRuntimeNameTestCase.APP_NAME));
        checkURL("/SimpleServlet/hello", "SimpleHelloWorldServlet", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
        checkURL("/SimpleServlet/page.html", "Version1", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
        checkURL("/SimpleServlet/hello", "SimpleHelloWorldServlet", DomainDeployWithRuntimeNameTestCase.serverGroups[1], true);
        warFile = createWarFile("Version2");
        cli.sendLine(buildDeployCommand(DomainDeployWithRuntimeNameTestCase.serverGroups[1], DomainDeployWithRuntimeNameTestCase.RUNTIME_NAME, DomainDeployWithRuntimeNameTestCase.OTHER_APP_NAME));
        checkURL("/SimpleServlet/hello", "SimpleHelloWorldServlet", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
        checkURL("/SimpleServlet/page.html", "Version1", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
        checkURL("/SimpleServlet/hello", "SimpleHelloWorldServlet", DomainDeployWithRuntimeNameTestCase.serverGroups[1], false);
        checkURL("/SimpleServlet/page.html", "Version2", DomainDeployWithRuntimeNameTestCase.serverGroups[1], false);
    }

    @Test
    public void testDeployWithDifferentRuntimeNameOnDifferentServerGroup() throws Exception {
        // deploy to group servers
        warFile = createWarFile("Version1");
        cli.sendLine(buildDeployCommand(DomainDeployWithRuntimeNameTestCase.serverGroups[0], DomainDeployWithRuntimeNameTestCase.RUNTIME_NAME, DomainDeployWithRuntimeNameTestCase.APP_NAME));
        checkURL("/SimpleServlet/hello", "SimpleHelloWorldServlet", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
        checkURL("/SimpleServlet/page.html", "Version1", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
        checkURL("/SimpleServlet/hello", "SimpleHelloWorldServlet", DomainDeployWithRuntimeNameTestCase.serverGroups[1], true);
        warFile = createWarFile("Version3");
        cli.sendLine(buildDeployCommand(DomainDeployWithRuntimeNameTestCase.serverGroups[1], DomainDeployWithRuntimeNameTestCase.OTHER_RUNTIME_NAME, DomainDeployWithRuntimeNameTestCase.OTHER_APP_NAME));
        checkURL("/SimpleServlet/hello", "SimpleHelloWorldServlet", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
        checkURL("/SimpleServlet/page.html", "Version1", DomainDeployWithRuntimeNameTestCase.serverGroups[0], false);
        checkURL("/OtherSimpleServlet/hello", "SimpleHelloWorldServlet", DomainDeployWithRuntimeNameTestCase.serverGroups[1], false);
        checkURL("/OtherSimpleServlet/page.html", "Version3", DomainDeployWithRuntimeNameTestCase.serverGroups[1], false);
    }
}

