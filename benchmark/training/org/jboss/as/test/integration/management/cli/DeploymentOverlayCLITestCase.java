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
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.test.integration.common.HttpRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Alexey Loubyansky
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DeploymentOverlayCLITestCase {
    private static File replacedLibrary;

    private static File addedLibrary;

    private static File war1;

    private static File war1_exploded;

    private static File war2;

    private static File war2_exploded;

    private static File war3;

    private static File ear1;

    private static File ear1_exploded;

    private static File ear2;

    private static File ear2_exploded;

    private static File webXml;

    private static File overrideXml;

    private static File replacedAjsp;

    @ArquillianResource
    URL url;

    private String baseUrl;

    private CommandContext ctx;

    @Test
    public void testSimpleOverride() throws Exception {
        simpleOverrideTest(false);
    }

    @Test
    public void testSimpleOverrideMultipleDeploymentOverlay() throws Exception {
        simpleOverrideTest(true);
    }

    @Test
    public void testSimpleOverrideExploded() throws Exception {
        simpleOverrideExplodedTest(false);
    }

    @Test
    public void testSimpleOverrideExplodedMultipleDeploymentOverlay() throws Exception {
        simpleOverrideExplodedTest(true);
    }

    @Test
    public void testSimpleOverrideInEarAtWarLevel() throws Exception {
        simpleOverrideInEarAtWarLevelTest(false);
    }

    @Test
    public void testSimpleOverrideInEarAtWarLevelMultipleDeploymentOverlay() throws Exception {
        simpleOverrideInEarAtWarLevelTest(true);
    }

    @Test
    public void testSimpleOverrideInEarAtWarLevelExploded() throws Exception {
        simpleOverrideInEarAtWarLevelExplodedTest(false);
    }

    @Test
    public void testSimpleOverrideInEarAtWarLevelExplodedMultipleDeploymentOverlay() throws Exception {
        simpleOverrideInEarAtWarLevelExplodedTest(true);
    }

    @Test
    public void testSimpleOverrideInEarAtEarLevel() throws Exception {
        ctx.handle(("deploy " + (DeploymentOverlayCLITestCase.ear2.getAbsolutePath())));
        ctx.handle(((("deployment-overlay add --name=overlay-test --content=lib/lib.jar=" + (DeploymentOverlayCLITestCase.replacedLibrary.getAbsolutePath())) + " --deployments=") + (DeploymentOverlayCLITestCase.ear2.getName())));
        // now test Libraries
        Assert.assertEquals("original library", HttpRequest.get(((baseUrl) + "deployment0/EarServlet"), 10, TimeUnit.SECONDS).trim());
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.ear2.getName())) + ":redeploy"));
        // now test Libraries
        Assert.assertEquals("replaced library", HttpRequest.get(((baseUrl) + "deployment0/EarServlet"), 10, TimeUnit.SECONDS).trim());
    }

    @Test
    public void testSimpleOverrideInEarAtEarLevelExploded() throws Exception {
        ctx.handle((((("/deployment=" + (DeploymentOverlayCLITestCase.ear2_exploded.getName())) + ":add(content=[{\"path\"=>\"") + (DeploymentOverlayCLITestCase.ear2_exploded.getAbsolutePath().replace("\\", "\\\\"))) + "\",\"archive\"=>false}], enabled=true)"));
        ctx.handle(((("deployment-overlay add --name=overlay-test --content=lib/lib.jar=" + (DeploymentOverlayCLITestCase.replacedLibrary.getAbsolutePath())) + " --deployments=") + (DeploymentOverlayCLITestCase.ear2_exploded.getName())));
        // now test Libraries
        Assert.assertEquals("original library", HttpRequest.get(((baseUrl) + "deployment0/EarServlet"), 10, TimeUnit.SECONDS).trim());
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.ear2_exploded.getName())) + ":redeploy"));
        // now test Libraries
        Assert.assertEquals("replaced library", HttpRequest.get(((baseUrl) + "deployment0/EarServlet"), 10, TimeUnit.SECONDS).trim());
    }

    @Test
    public void testSimpleOverrideWithRedeployAffected() throws Exception {
        simpleOverrideWithRedeployAffectedTest(false);
    }

    @Test
    public void testSimpleOverrideWithRedeployAffectedMultipleDeploymentOverlay() throws Exception {
        simpleOverrideWithRedeployAffectedTest(true);
    }

    @Test
    public void testWildcardOverride() throws Exception {
        wildcardOverrideTest(false);
    }

    @Test
    public void testWildcardOverrideMultipleDeploymentOverlay() throws Exception {
        wildcardOverrideTest(true);
    }

    @Test
    public void testWildcardOverrideWithRedeployAffected() throws Exception {
        wildcardOverrideWithRedeployAffectedTest(false);
    }

    @Test
    public void testWildcardOverrideWithRedeployAffectedMultipleDeploymentOverlay() throws Exception {
        wildcardOverrideWithRedeployAffectedTest(true);
    }

    @Test
    public void testMultipleLinks() throws Exception {
        ctx.handle(((("deployment-overlay add --name=overlay-test --content=WEB-INF/web.xml=" + (DeploymentOverlayCLITestCase.overrideXml.getAbsolutePath())) + " --deployments=") + (DeploymentOverlayCLITestCase.war1.getName())));
        ctx.handle(("deploy " + (DeploymentOverlayCLITestCase.war1.getAbsolutePath())));
        ctx.handle(("deploy " + (DeploymentOverlayCLITestCase.war2.getAbsolutePath())));
        ctx.handle(("deploy " + (DeploymentOverlayCLITestCase.war3.getAbsolutePath())));
        String response = readResponse("deployment0");
        Assert.assertEquals("OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("another");
        Assert.assertEquals("NON OVERRIDDEN", response);
        ctx.handle("deployment-overlay link --name=overlay-test --deployments=a*.war");
        response = readResponse("deployment0");
        Assert.assertEquals("OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("another");
        Assert.assertEquals("NON OVERRIDDEN", response);
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war1.getName())) + ":redeploy"));
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war2.getName())) + ":redeploy"));
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war3.getName())) + ":redeploy"));
        response = readResponse("deployment0");
        Assert.assertEquals("OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("another");
        Assert.assertEquals("OVERRIDDEN", response);
        ctx.handle((("deployment-overlay link --name=overlay-test --deployments=" + (DeploymentOverlayCLITestCase.war2.getName())) + " --redeploy-affected"));
        response = readResponse("deployment0");
        Assert.assertEquals("OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("OVERRIDDEN", response);
        response = readResponse("another");
        Assert.assertEquals("OVERRIDDEN", response);
        ctx.handle((("deployment-overlay remove --name=overlay-test --deployments=" + (DeploymentOverlayCLITestCase.war2.getName())) + " --redeploy-affected"));
        response = readResponse("deployment0");
        Assert.assertEquals("OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("another");
        Assert.assertEquals("OVERRIDDEN", response);
        ctx.handle("deployment-overlay remove --name=overlay-test --deployments=a*.war");
        response = readResponse("deployment0");
        Assert.assertEquals("OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("another");
        Assert.assertEquals("OVERRIDDEN", response);
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war1.getName())) + ":redeploy"));
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war2.getName())) + ":redeploy"));
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war3.getName())) + ":redeploy"));
        response = readResponse("deployment0");
        Assert.assertEquals("OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("another");
        Assert.assertEquals("NON OVERRIDDEN", response);
        ctx.handle("deployment-overlay remove --name=overlay-test --content=WEB-INF/web.xml --redeploy-affected");
        response = readResponse("deployment0");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("another");
        Assert.assertEquals("NON OVERRIDDEN", response);
        ctx.handle((("deployment-overlay upload --name=overlay-test --content=WEB-INF/web.xml=" + (DeploymentOverlayCLITestCase.overrideXml.getAbsolutePath())) + " --redeploy-affected"));
        response = readResponse("deployment0");
        Assert.assertEquals("OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("another");
        Assert.assertEquals("NON OVERRIDDEN", response);
    }

    @Test
    public void testRedeployAffected() throws Exception {
        ctx.handle(("deploy " + (DeploymentOverlayCLITestCase.war1.getAbsolutePath())));
        ctx.handle(("deploy " + (DeploymentOverlayCLITestCase.war2.getAbsolutePath())));
        ctx.handle(("deploy " + (DeploymentOverlayCLITestCase.war3.getAbsolutePath())));
        ctx.handle(("deployment-overlay add --name=overlay-test --content=WEB-INF/web.xml=" + (DeploymentOverlayCLITestCase.overrideXml.getAbsolutePath())));
        ctx.handle("deployment-overlay link --name=overlay-test --deployments=deployment0.war,a*.war");
        String response = readResponse("deployment0");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("another");
        Assert.assertEquals("NON OVERRIDDEN", response);
        ctx.handle("deployment-overlay redeploy-affected --name=overlay-test");
        response = readResponse("deployment0");
        Assert.assertEquals("OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("another");
        Assert.assertEquals("OVERRIDDEN", response);
    }

    @Test
    public void testSimpleOverrideRemoveOverlay() throws Exception {
        ctx.handle(("deploy " + (DeploymentOverlayCLITestCase.war1.getAbsolutePath())));
        ctx.handle(("deploy " + (DeploymentOverlayCLITestCase.war2.getAbsolutePath())));
        ctx.handle(((((((((((((("deployment-overlay add --name=" + ("overlay-test --content=" + "WEB-INF/web.xml=")) + (DeploymentOverlayCLITestCase.overrideXml.getAbsolutePath())) + ",") + "a.jsp=") + (DeploymentOverlayCLITestCase.replacedAjsp.getAbsolutePath())) + ",") + "WEB-INF/lib/lib.jar=") + (DeploymentOverlayCLITestCase.replacedLibrary.getAbsolutePath())) + ",") + "WEB-INF/lib/addedlib.jar=") + (DeploymentOverlayCLITestCase.addedLibrary.getAbsolutePath())) + " --deployments=") + (DeploymentOverlayCLITestCase.war1.getName())));
        String response = readResponse("deployment0");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war1.getName())) + ":redeploy"));
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war2.getName())) + ":redeploy"));
        response = readResponse("deployment0");
        Assert.assertEquals("OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        // now test JSP
        Assert.assertEquals("Replaced JSP File", HttpRequest.get(((baseUrl) + "deployment0/a.jsp"), 10, TimeUnit.SECONDS).trim());
        // now test Libraries
        Assert.assertEquals("Replaced Library Servlet", HttpRequest.get(((baseUrl) + "deployment0/LibraryServlet"), 10, TimeUnit.SECONDS).trim());
        Assert.assertEquals("Added Library Servlet", HttpRequest.get(((baseUrl) + "deployment0/AddedLibraryServlet"), 10, TimeUnit.SECONDS).trim());
        ctx.handleSafe("deployment-overlay remove --name=overlay-test");
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war1.getName())) + ":redeploy"));
        response = readResponse("deployment0");
        Assert.assertEquals("NON OVERRIDDEN", response);
        // now test Libraries
        Assert.assertEquals("Original Library Servlet", HttpRequest.get(((baseUrl) + "deployment0/LibraryServlet"), 10, TimeUnit.SECONDS).trim());
        try {
            // Assert.assertNotEquals("Added Library Servlet", HttpRequest.get(baseUrl + "deployment0/AddedLibraryServlet", 10, TimeUnit.SECONDS).trim());
            HttpRequest.get(((baseUrl) + "deployment0/AddedLibraryServlet"), 10, TimeUnit.SECONDS);
            Assert.fail();
        } catch (IOException e) {
            // ok
        }
        // now test JSP
        Assert.assertEquals("Original JSP File", HttpRequest.get(((baseUrl) + "deployment0/a.jsp"), 10, TimeUnit.SECONDS).trim());
    }

    @Test
    public void testSimpleOverrideRemoveOverlay2() throws Exception {
        ctx.handle(("deploy " + (DeploymentOverlayCLITestCase.war1.getAbsolutePath())));
        ctx.handle(("deploy " + (DeploymentOverlayCLITestCase.war2.getAbsolutePath())));
        ctx.handle(((((("deployment-overlay add --name=" + ("overlay-test --content=" + "a.jsp=")) + (DeploymentOverlayCLITestCase.replacedAjsp.getAbsolutePath())) + ",") + " --deployments=") + (DeploymentOverlayCLITestCase.war1.getName())));
        String response = readResponse("deployment0");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war1.getName())) + ":redeploy"));
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war2.getName())) + ":redeploy"));
        response = readResponse("deployment0");
        Assert.assertEquals("NON OVERRIDDEN", response);
        response = readResponse("deployment1");
        Assert.assertEquals("NON OVERRIDDEN", response);
        // now test JSP
        Assert.assertEquals("Replaced JSP File", HttpRequest.get(((baseUrl) + "deployment0/a.jsp"), 10, TimeUnit.SECONDS).trim());
        ctx.handleSafe("deployment-overlay remove --name=overlay-test");
        ctx.handle((("/deployment=" + (DeploymentOverlayCLITestCase.war1.getName())) + ":redeploy"));
        response = readResponse("deployment0");
        Assert.assertEquals("NON OVERRIDDEN", response);
        // now test JSP
        Assert.assertEquals("Original JSP File", HttpRequest.get(((baseUrl) + "deployment0/a.jsp"), 10, TimeUnit.SECONDS).trim());
    }
}

