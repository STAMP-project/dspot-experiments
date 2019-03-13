/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.integration.tracing;


import Invocation.Builder;
import Utils.APPLICATION_X_JERSEY_TEST;
import Utils.TestAction.MESSAGE_BODY_READER_THROW_ANY;
import Utils.TestAction.MESSAGE_BODY_READER_THROW_PROCESSING;
import Utils.TestAction.MESSAGE_BODY_READER_THROW_WEB_APPLICATION;
import Utils.TestAction.MESSAGE_BODY_WRITER_THROW_ANY;
import Utils.TestAction.MESSAGE_BODY_WRITER_THROW_PROCESSING;
import Utils.TestAction.MESSAGE_BODY_WRITER_THROW_WEB_APPLICATION;
import Utils.TestAction.PRE_MATCHING_REQUEST_FILTER_THROW_ANY;
import Utils.TestAction.PRE_MATCHING_REQUEST_FILTER_THROW_PROCESSING;
import Utils.TestAction.PRE_MATCHING_REQUEST_FILTER_THROW_WEB_APPLICATION;
import java.io.IOException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * 'ALL' tracing support test that is running in external Jetty container.
 *
 * @author Libor Kramolis (libor.kramolis at oracle.com)
 */
@RunWith(Parameterized.class)
public class AllTracingSupportITCase extends JerseyTest {
    private final String resourcePath;

    public AllTracingSupportITCase(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    // 
    // tests
    // 
    @Test
    public void testGet() {
        Invocation.Builder builder = resource(resourcePath).path("NAME").request();
        Response response = builder.get();
        assertXJerseyTrace(response, false);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testRuntimeException() {
        Invocation.Builder builder = resource(resourcePath).path("runtime-exception").request();
        Response response = builder.get();
        assertXJerseyTrace(response, true);
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testMappedException() throws IOException, InterruptedException {
        Invocation.Builder builder = resource(resourcePath).path("mapped-exception").request();
        Response response = builder.get();
        assertXJerseyTrace(response, true);
        Assert.assertEquals(501, response.getStatus());
    }

    @Test
    public void testGet405() {
        Invocation.Builder builder = resource(resourcePath).request();
        Response response = builder.get();
        assertXJerseyTrace(response, false);
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testPostSubResourceMethod() {
        Invocation.Builder builder = resource(resourcePath).path("sub-resource-method").request();
        Response response = builder.post(Entity.entity(new Message("POST"), APPLICATION_X_JERSEY_TEST));
        assertXJerseyTrace(response, false);
        Assert.assertEquals("TSOP", response.readEntity(Message.class).getText());
    }

    @Test
    public void testPostSubResourceLocator() {
        Invocation.Builder builder = resource(resourcePath).path("sub-resource-locator").request();
        Response response = builder.post(Entity.entity(new Message("POST"), APPLICATION_X_JERSEY_TEST));
        assertXJerseyTrace(response, false);
        Assert.assertEquals("TSOP", response.readEntity(Message.class).getText());
    }

    @Test
    public void testPostSubResourceLocatorNull() {
        Invocation.Builder builder = resource(resourcePath).path("sub-resource-locator-null").request();
        Response response = builder.post(Entity.entity(new Message("POST"), APPLICATION_X_JERSEY_TEST));
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testPostSubResourceLocatorSubResourceMethod() {
        Invocation.Builder builder = resource(resourcePath).path("sub-resource-locator").path("sub-resource-method").request();
        Response response = builder.post(Entity.entity(new Message("POST"), APPLICATION_X_JERSEY_TEST));
        assertXJerseyTrace(response, false);
        Assert.assertEquals("TSOP", response.readEntity(Message.class).getText());
    }

    @Test
    public void testTraceInnerException() {
        // PRE_MATCHING_REQUEST_FILTER
        testTraceInnerExceptionImpl(PRE_MATCHING_REQUEST_FILTER_THROW_WEB_APPLICATION, 500, false);
        testTraceInnerExceptionImpl(PRE_MATCHING_REQUEST_FILTER_THROW_PROCESSING, 500, true);
        testTraceInnerExceptionImpl(PRE_MATCHING_REQUEST_FILTER_THROW_ANY, 500, true);
        // MESSAGE_BODY_WRITER
        testTraceInnerExceptionImpl(MESSAGE_BODY_WRITER_THROW_WEB_APPLICATION, 500, false);
        testTraceInnerExceptionImpl(MESSAGE_BODY_WRITER_THROW_PROCESSING, 500, true);
        testTraceInnerExceptionImpl(MESSAGE_BODY_WRITER_THROW_ANY, 500, true);
        // MESSAGE_BODY_READER
        testTraceInnerExceptionImpl(MESSAGE_BODY_READER_THROW_WEB_APPLICATION, 500, false);
        testTraceInnerExceptionImpl(MESSAGE_BODY_READER_THROW_PROCESSING, 500, true);
        testTraceInnerExceptionImpl(MESSAGE_BODY_READER_THROW_ANY, 500, true);
    }
}

