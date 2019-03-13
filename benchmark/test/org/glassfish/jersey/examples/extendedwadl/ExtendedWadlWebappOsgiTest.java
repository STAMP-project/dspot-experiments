/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.examples.extendedwadl;


import MediaTypes.WADL_TYPE;
import TestProperties.CONTAINER_PORT;
import WadlUtils.DETAILED_WADL_QUERY_PARAM;
import java.net.URI;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;


/**
 *
 *
 * @author Naresh
 * @author Miroslav Fuksa
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 * @author Adam Lindenthal (adam.lindenthal at oracle.com)
 */
@RunWith(PaxExam.class)
public class ExtendedWadlWebappOsgiTest {
    @Inject
    BundleContext bundleContext;

    private static final Logger LOGGER = Logger.getLogger(ExtendedWadlWebappOsgiTest.class.getName());

    // we want to re-use the port number as set for Jersey test container to avoid CT port number clashes
    private static final String testContainerPort = System.getProperty(CONTAINER_PORT);

    private static final int testPort = ((ExtendedWadlWebappOsgiTest.testContainerPort) == null) ? TestProperties.DEFAULT_CONTAINER_PORT : Integer.parseInt(ExtendedWadlWebappOsgiTest.testContainerPort);

    private static final URI baseUri = UriBuilder.fromUri("http://localhost").port(ExtendedWadlWebappOsgiTest.testPort).path("extended-wadl-webapp").build();

    /**
     * Test checks that the WADL generated using the WadlGenerator api doesn't
     * contain the expected text.
     *
     * @throws java.lang.Exception
     * 		in case of a test error.
     */
    @Test
    public void testExtendedWadl() throws Exception {
        // TODO - temporary workaround
        // This is a workaround related to issue JERSEY-2093; grizzly (1.9.5) needs to have the correct context
        // class loader set
        ClassLoader myClassLoader = this.getClass().getClassLoader();
        for (Bundle bundle : bundleContext.getBundles()) {
            if ("webapp".equals(bundle.getSymbolicName())) {
                myClassLoader = bundle.loadClass("org.glassfish.jersey.examples.extendedwadl.resources.MyApplication").getClassLoader();
                break;
            }
        }
        Thread.currentThread().setContextClassLoader(myClassLoader);
        // END of workaround - the entire block can be deleted after grizzly is updated to recent version
        // List all the OSGi bundles
        StringBuilder sb = new StringBuilder();
        sb.append("-- Bundle list -- \n");
        for (Bundle b : bundleContext.getBundles()) {
            sb.append(String.format("%1$5s", (("[" + (b.getBundleId())) + "]"))).append(" ").append(String.format("%1$-70s", b.getSymbolicName())).append(" | ").append(String.format("%1$-20s", b.getVersion())).append(" |");
            try {
                b.start();
                sb.append(" STARTED  | ");
            } catch (BundleException e) {
                sb.append(" *FAILED* | ").append(e.getMessage());
            }
            sb.append(b.getLocation()).append("\n");
        }
        sb.append("-- \n\n");
        ExtendedWadlWebappOsgiTest.LOGGER.fine(sb.toString());
        final ResourceConfig resourceConfig = createResourceConfig();
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(ExtendedWadlWebappOsgiTest.baseUri, resourceConfig);
        final Client client = ClientBuilder.newClient();
        final Response response = client.target(ExtendedWadlWebappOsgiTest.baseUri).path("application.wadl").request(WADL_TYPE).buildGet().invoke();
        String wadl = response.readEntity(String.class);
        ExtendedWadlWebappOsgiTest.LOGGER.info(("RESULT = " + wadl));
        Assert.assertTrue("Generated wadl is of null length", (!(wadl.isEmpty())));
        Assert.assertTrue("Generated wadl doesn't contain the expected text", wadl.contains("This is a paragraph"));
        Assert.assertFalse(wadl.contains("application.wadl/xsd0.xsd"));
        server.shutdownNow();
    }

    @Test
    public void testWadlOptionsMethod() throws Exception {
        // TODO - temporary workaround
        // This is a workaround related to issue JERSEY-2093; grizzly (1.9.5) needs to have the correct context
        // class loader set
        ClassLoader myClassLoader = this.getClass().getClassLoader();
        for (Bundle bundle : bundleContext.getBundles()) {
            if ("webapp".equals(bundle.getSymbolicName())) {
                myClassLoader = bundle.loadClass("org.glassfish.jersey.examples.extendedwadl.resources.MyApplication").getClassLoader();
                break;
            }
        }
        Thread.currentThread().setContextClassLoader(myClassLoader);
        // END of workaround; The entire block can be removed after grizzly is migrated to more recent version
        final ResourceConfig resourceConfig = createResourceConfig();
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(ExtendedWadlWebappOsgiTest.baseUri, resourceConfig);
        final Client client = ClientBuilder.newClient();
        String wadl = client.target(ExtendedWadlWebappOsgiTest.baseUri).path("items").queryParam(DETAILED_WADL_QUERY_PARAM, "true").request(WADL_TYPE).options(String.class);
        Assert.assertTrue("Generated wadl is of null length", (!(wadl.isEmpty())));
        Assert.assertTrue("Generated wadl doesn't contain the expected text", wadl.contains("This is a paragraph"));
        checkWadl(wadl, ExtendedWadlWebappOsgiTest.baseUri);
        server.shutdownNow();
    }
}

