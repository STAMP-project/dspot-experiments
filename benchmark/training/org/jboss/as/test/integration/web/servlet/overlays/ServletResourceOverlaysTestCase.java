/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.web.servlet.overlays;


import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.dmr.ModelNode;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(ServletResourceOverlaysTestCase.ServletResourceOverlaysTestCaseServerSetup.class)
public class ServletResourceOverlaysTestCase {
    private static final String SUBSYSTEM_NAME = "undertow";

    @ArquillianResource
    private URL url;

    public static class ServletResourceOverlaysTestCaseServerSetup implements ServerSetupTask {
        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            ModelNode op = new ModelNode();
            op.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            op.get(OP_ADDR).add(SUBSYSTEM, ServletResourceOverlaysTestCase.SUBSYSTEM_NAME);
            op.get(OP_ADDR).add("server", "default-server");
            op.get(OP_ADDR).add("http-server", "default");
            op.get(NAME).set("allow-encoded-slash");
            op.get(VALUE).set(true);
            managementClient.getControllerClient().execute(op);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            ModelNode op = new ModelNode();
            op.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            op.get(OP_ADDR).add(SUBSYSTEM, ServletResourceOverlaysTestCase.SUBSYSTEM_NAME);
            op.get(OP_ADDR).add("server", "default-server");
            op.get(OP_ADDR).add("http-server", "default");
            op.get(NAME).set("allow-encoded-slash");
            op.get(VALUE).set(false);
            managementClient.getControllerClient().execute(op);
        }
    }

    @Test
    public void testLifeCycle() throws Exception {
        String result = performCall(url, "a.txt");
        Assert.assertEquals("a", result);
        result = performCall(url, "b.txt");
        Assert.assertEquals("b", result);
        result = performCall(url, "c.txt");
        Assert.assertEquals("c - overlay", result);
    }

    /**
     * Tests that a servlet (through the use of {@link javax.servlet.ServletContext#getResourceAsStream(String)} (or similar APIs)
     * cannot access paths outside of the deployment
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPathAccess() throws Exception {
        final String aTxtPath = "a.txt";
        final String aTxtAccess = performCall(url, "/check-path-access?path=a.txt&expected-accessible=true");
        Assert.assertEquals(("Unexpected result from call to " + aTxtPath), PathAccessCheckServlet.ACCESS_CHECKS_CORRECTLY_VALIDATED, aTxtAccess);
        // Deployment root virtual file is different on each Operating System, we have to find out how to navigate to the root folder from the deployed file
        VirtualFile deploymentRoot = VFS.getChild("content/single.war");
        final StringBuilder accessRootPath = new StringBuilder("");
        while (!(deploymentRoot.isRoot())) {
            accessRootPath.append("/..");
            deploymentRoot = deploymentRoot.getParent();
        } 
        final File fileUnderTest = Paths.get(System.getProperty("java.io.tmpdir"), "noaccess.txt").toFile();
        fileUnderTest.createNewFile();
        if (fileUnderTest.exists()) {
            String canonicalPath = fileUnderTest.getCanonicalPath();
            canonicalPath = canonicalPath.substring(fileUnderTest.toPath().getRoot().toString().length());
            if (File.separator.equals("\\")) {
                canonicalPath = canonicalPath.replace("\\", "%5c");
            }
            final String pathOutsideOfDeployment = ((accessRootPath.toString()) + "/../../../../../../../") + canonicalPath;
            final String outsidePathAccessCheck = performCall(url, (("/check-path-access?path=" + pathOutsideOfDeployment) + "&expected-accessible=false"));
            Assert.assertEquals(("Unexpected result from call to " + pathOutsideOfDeployment), PathAccessCheckServlet.ACCESS_CHECKS_CORRECTLY_VALIDATED, outsidePathAccessCheck);
            fileUnderTest.delete();
        } else {
            Assert.fail(("Cannot create the file under test: " + (fileUnderTest.getCanonicalPath())));
        }
    }
}

