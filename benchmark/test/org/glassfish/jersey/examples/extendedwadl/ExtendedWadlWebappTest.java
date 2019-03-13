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
import WadlUtils.DETAILED_WADL_QUERY_PARAM;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * WADL extension example tests.
 *
 * @author Naresh
 * @author Miroslav Fuksa
 */
public class ExtendedWadlWebappTest extends JerseyTest {
    private static final Logger LOGGER = Logger.getLogger(ExtendedWadlWebappTest.class.getName());

    /**
     * Test checks that the WADL generated using the WadlGenerator api doesn't
     * contain the expected text.
     *
     * @throws java.lang.Exception
     * 		in case of test error.
     */
    @Test
    public void testExtendedWadl() throws Exception {
        String wadl = target().path("application.wadl").queryParam(DETAILED_WADL_QUERY_PARAM, "true").request(WADL_TYPE).get(String.class);
        ExtendedWadlWebappTest.LOGGER.fine(wadl);
        Assert.assertTrue("Generated wadl is of null length", (!(wadl.isEmpty())));
        Assert.assertTrue("Generated wadl doesn't contain the expected text", wadl.contains("This is a paragraph"));
        Assert.assertFalse(wadl.contains("application.wadl/xsd0.xsd"));
    }

    @Test
    public void testWadlOptionsMethod() throws Exception {
        String wadl = target().path("items").queryParam(DETAILED_WADL_QUERY_PARAM, "true").request(WADL_TYPE).options(String.class);
        ExtendedWadlWebappTest.LOGGER.fine(wadl);
        Assert.assertTrue("Generated wadl is of null length", (!(wadl.isEmpty())));
        Assert.assertTrue("Generated wadl doesn't contain the expected text", wadl.contains("This is a paragraph"));
        checkWadl(wadl, getBaseUri());
    }

    /**
     * Programmatic resource class javadoc.
     */
    private static class ProgrammaticResource implements Inflector<ContainerRequestContext, Response> {
        @Override
        public Response apply(ContainerRequestContext data) {
            return Response.ok("programmatic").build();
        }
    }
}

