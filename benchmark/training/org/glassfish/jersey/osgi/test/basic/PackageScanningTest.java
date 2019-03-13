/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.osgi.test.basic;


import ServerProperties.PROVIDER_PACKAGES;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.osgi.test.util.Helper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;


/**
 * NOTE: This test is excluded on JDK6 as it requires Servlet 3.1 API that is built against JDK 7.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 * @author Michal Gajdos
 */
@RunWith(PaxExam.class)
public class PackageScanningTest {
    private static final String CONTEXT = "/jersey";

    private static final URI baseUri = UriBuilder.fromUri("http://localhost").port(Helper.getPort()).path(PackageScanningTest.CONTEXT).build();

    @Test
    public void testSimpleResource() throws Exception {
        final ResourceConfig resourceConfig = new ResourceConfig().packages(SimpleResource.class.getPackage().getName());
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(PackageScanningTest.baseUri, resourceConfig);
        _testScannedResources(server);
    }

    @Test
    public void testSimpleResourceInitParameters() throws Exception {
        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put(PROVIDER_PACKAGES, SimpleResource.class.getPackage().getName());
        // TODO - temporary workaround
        // This is a workaround related to issue JERSEY-2093; grizzly (1.9.5) needs to have the correct context
        // classloader set
        ClassLoader myClassLoader = getClass().getClassLoader();
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        HttpServer server = null;
        try {
            Thread.currentThread().setContextClassLoader(myClassLoader);
            server = GrizzlyWebContainerFactory.create(PackageScanningTest.baseUri, ServletContainer.class, initParams);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }
        // END of workaround - when grizzly updated to more recent version, only the inner line of try clause should remain:
        _testScannedResources(server);
    }
}

