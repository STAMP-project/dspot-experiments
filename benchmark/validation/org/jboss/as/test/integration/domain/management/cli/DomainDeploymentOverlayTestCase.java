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
package org.jboss.as.test.integration.domain.management.cli;


import java.io.File;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alexey Loubyansky
 */
public class DomainDeploymentOverlayTestCase {
    private static final String SOCKET_BINDING_GROUP_NAME = "standard-sockets";

    private static File war1;

    private static File war2;

    private static File war3;

    private static File webXml;

    private static File overrideXml;

    private static DomainTestSupport testSupport;

    private CommandContext ctx;

    private DomainClient client;

    @Test
    public void testSimpleOverride() throws Exception {
        ctx.handle(("deploy --server-groups=main-server-group,other-server-group " + (DomainDeploymentOverlayTestCase.war1.getAbsolutePath())));
        ctx.handle(("deploy --server-groups=main-server-group,other-server-group " + (DomainDeploymentOverlayTestCase.war2.getAbsolutePath())));
        ctx.handle((((("deployment-overlay add --name=overlay-test --content=WEB-INF/web.xml=" + (DomainDeploymentOverlayTestCase.overrideXml.getAbsolutePath())) + " --deployments=") + (DomainDeploymentOverlayTestCase.war1.getName())) + " --server-groups=main-server-group,other-server-group"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        // assertEquals("NON OVERRIDDEN", performHttpCall("master", "other-one", "deployment0"));
        // assertEquals("NON OVERRIDDEN", performHttpCall("master", "other-one", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        // assertEquals("NON OVERRIDDEN", performHttpCall("slave", "other-two", "deployment0"));
        // assertEquals("NON OVERRIDDEN", performHttpCall("slave", "other-two", "deployment1"));
        ctx.handle("deployment-overlay redeploy-affected --name=overlay-test");
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        // assertEquals("OVERRIDDEN", performHttpCall("master", "other-one", "deployment0"));
        // assertEquals("NON OVERRIDDEN", performHttpCall("master", "other-one", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        // assertEquals("OVERRIDDEN", performHttpCall("slave", "other-two", "deployment0"));
        // assertEquals("NON OVERRIDDEN", performHttpCall("slave", "other-two", "deployment1"));
    }

    @Test
    public void testSimpleOverrideWithRedeployAffected() throws Exception {
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war1.getAbsolutePath())));
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war2.getAbsolutePath())));
        ctx.handle((((("deployment-overlay add --name=overlay-test --content=WEB-INF/web.xml=" + (DomainDeploymentOverlayTestCase.overrideXml.getAbsolutePath())) + " --deployments=") + (DomainDeploymentOverlayTestCase.war1.getName())) + " --server-groups=main-server-group --redeploy-affected"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
    }

    @Test
    public void testWildcardOverride() throws Exception {
        ctx.handle((("deployment-overlay add --name=overlay-test --content=WEB-INF/web.xml=" + (DomainDeploymentOverlayTestCase.overrideXml.getAbsolutePath())) + " --deployments=deployment*.war --server-groups=main-server-group --redeploy-affected"));
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war1.getAbsolutePath())));
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war2.getAbsolutePath())));
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war3.getAbsolutePath())));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
    }

    @Test
    public void testWildcardOverrideWithRedeployAffected() throws Exception {
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war1.getAbsolutePath())));
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war2.getAbsolutePath())));
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war3.getAbsolutePath())));
        ctx.handle((("deployment-overlay add --name=overlay-test --content=WEB-INF/web.xml=" + (DomainDeploymentOverlayTestCase.overrideXml.getAbsolutePath())) + " --deployments=deployment*.war --server-groups=main-server-group --redeploy-affected"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
    }

    @Test
    public void testMultipleLinks() throws Exception {
        ctx.handle((((("deployment-overlay add --name=overlay-test --content=WEB-INF/web.xml=" + (DomainDeploymentOverlayTestCase.overrideXml.getAbsolutePath())) + " --deployments=") + (DomainDeploymentOverlayTestCase.war1.getName())) + " --server-groups=main-server-group"));
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war1.getAbsolutePath())));
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war2.getAbsolutePath())));
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war3.getAbsolutePath())));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
        ctx.handle("deployment-overlay link --name=overlay-test --deployments=a*.war --server-groups=main-server-group");
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
        ctx.handle((("/server-group=main-server-group/deployment=" + (DomainDeploymentOverlayTestCase.war1.getName())) + ":redeploy"));
        ctx.handle((("/server-group=main-server-group/deployment=" + (DomainDeploymentOverlayTestCase.war2.getName())) + ":redeploy"));
        ctx.handle((("/server-group=main-server-group/deployment=" + (DomainDeploymentOverlayTestCase.war3.getName())) + ":redeploy"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
        ctx.handle((("deployment-overlay link --name=overlay-test --deployments=" + (DomainDeploymentOverlayTestCase.war2.getName())) + " --redeploy-affected --server-groups=main-server-group"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
        ctx.handle((("deployment-overlay remove --name=overlay-test --deployments=" + (DomainDeploymentOverlayTestCase.war2.getName())) + " --redeploy-affected --server-groups=main-server-group"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
        ctx.handle("deployment-overlay remove --name=overlay-test --deployments=a*.war --server-groups=main-server-group");
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
        ctx.handle((("/server-group=main-server-group/deployment=" + (DomainDeploymentOverlayTestCase.war1.getName())) + ":redeploy"));
        ctx.handle((("/server-group=main-server-group/deployment=" + (DomainDeploymentOverlayTestCase.war2.getName())) + ":redeploy"));
        ctx.handle((("/server-group=main-server-group/deployment=" + (DomainDeploymentOverlayTestCase.war3.getName())) + ":redeploy"));
        ctx.handle("deployment-overlay remove --name=overlay-test --content=WEB-INF/web.xml --redeploy-affected");
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
        ctx.handle((("deployment-overlay upload --name=overlay-test --content=WEB-INF/web.xml=" + (DomainDeploymentOverlayTestCase.overrideXml.getAbsolutePath())) + " --redeploy-affected"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
    }

    @Test
    public void testRedeployAffected() throws Exception {
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war1.getAbsolutePath())));
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war2.getAbsolutePath())));
        ctx.handle(("deploy --server-groups=main-server-group " + (DomainDeploymentOverlayTestCase.war3.getAbsolutePath())));
        ctx.handle(("deployment-overlay add --name=overlay-test --content=WEB-INF/web.xml=" + (DomainDeploymentOverlayTestCase.overrideXml.getAbsolutePath())));
        ctx.handle("deployment-overlay link --name=overlay-test --deployments=deployment0.war,a*.war --server-groups=main-server-group");
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
        ctx.handle("deployment-overlay redeploy-affected --name=overlay-test");
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("master", "main-one", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("master", "main-one", "another"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "deployment0"));
        Assert.assertEquals("NON OVERRIDDEN", performHttpCall("slave", "main-three", "deployment1"));
        Assert.assertEquals("OVERRIDDEN", performHttpCall("slave", "main-three", "another"));
    }
}

