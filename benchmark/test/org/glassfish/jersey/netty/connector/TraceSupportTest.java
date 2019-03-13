/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.netty.connector;


import Response.Status.OK;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.logging.Logger;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * This very basic resource showcases support of a HTTP TRACE method,
 * not directly supported by JAX-RS API.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class TraceSupportTest extends JerseyTest {
    private static final Logger LOGGER = Logger.getLogger(TraceSupportTest.class.getName());

    /**
     * Programmatic tracing root resource path.
     */
    public static final String ROOT_PATH_PROGRAMMATIC = "tracing/programmatic";

    /**
     * Annotated class-based tracing root resource path.
     */
    public static final String ROOT_PATH_ANNOTATED = "tracing/annotated";

    @HttpMethod(TraceSupportTest.TRACE.NAME)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TRACE {
        public static final String NAME = "TRACE";
    }

    @Path(TraceSupportTest.ROOT_PATH_ANNOTATED)
    public static class TracingResource {
        @TraceSupportTest.TRACE
        @Produces("text/plain")
        public String trace(Request request) {
            return TraceSupportTest.stringify(((ContainerRequest) (request)));
        }
    }

    private String[] expectedFragmentsProgrammatic = new String[]{ ("TRACE http://localhost:" + (getPort())) + "/tracing/programmatic" };

    private String[] expectedFragmentsAnnotated = new String[]{ ("TRACE http://localhost:" + (getPort())) + "/tracing/annotated" };

    @Test
    public void testProgrammaticApp() throws Exception {
        Response response = prepareTarget(TraceSupportTest.ROOT_PATH_PROGRAMMATIC).request("text/plain").method(TraceSupportTest.TRACE.NAME);
        Assert.assertEquals(OK.getStatusCode(), response.getStatusInfo().getStatusCode());
        String responseEntity = response.readEntity(String.class);
        for (String expectedFragment : expectedFragmentsProgrammatic) {
            // toLowerCase - http header field names are case insensitive
            Assert.assertTrue(((("Expected fragment '" + expectedFragment) + "\' not found in response:\n") + responseEntity), responseEntity.contains(expectedFragment));
        }
    }

    @Test
    public void testAnnotatedApp() throws Exception {
        Response response = prepareTarget(TraceSupportTest.ROOT_PATH_ANNOTATED).request("text/plain").method(TraceSupportTest.TRACE.NAME);
        Assert.assertEquals(OK.getStatusCode(), response.getStatusInfo().getStatusCode());
        String responseEntity = response.readEntity(String.class);
        for (String expectedFragment : expectedFragmentsAnnotated) {
            // toLowerCase - http header field names are case insensitive
            Assert.assertTrue(((("Expected fragment '" + expectedFragment) + "\' not found in response:\n") + responseEntity), responseEntity.contains(expectedFragment));
        }
    }

    @Test
    public void testTraceWithEntity() throws Exception {
        _testTraceWithEntity(false, false);
    }

    @Test
    public void testAsyncTraceWithEntity() throws Exception {
        _testTraceWithEntity(true, false);
    }

    @Test
    public void testTraceWithEntityApacheConnector() throws Exception {
        _testTraceWithEntity(false, true);
    }

    @Test
    public void testAsyncTraceWithEntityApacheConnector() throws Exception {
        _testTraceWithEntity(true, true);
    }
}

