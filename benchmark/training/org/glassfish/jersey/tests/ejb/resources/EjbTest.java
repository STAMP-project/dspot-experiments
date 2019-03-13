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
package org.glassfish.jersey.tests.ejb.resources;


import CounterFilter.RequestCountHEADER;
import EchoBean.PREFIX;
import EjbExceptionMapperOne.RESPONSE_BODY;
import ExceptionEjbResource.CheckedExceptionMESSAGE;
import ExceptionEjbResource.EjbExceptionMESSAGE;
import StandaloneServlet.ThrowCheckedExceptionACTION;
import StandaloneServlet.ThrowEjbExceptionACTION;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Test for EJB web application resources.
 * Run with:
 * <pre>
 * mvn clean package
 * $AS_HOME/bin/asadmin deploy target/ejb-test-webapp
 * mvn -DskipTests=false test</pre>
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public class EjbTest extends JerseyTest {
    @Test
    public void testEjbException() {
        final Response jerseyResponse = target().path("rest/exception/ejb").request().get();
        _check500Response(jerseyResponse, EjbExceptionMESSAGE);
        final Response servletResponse = target().path("servlet").queryParam("action", ThrowEjbExceptionACTION).request().get();
        _check500Response(servletResponse, EjbExceptionMESSAGE);
    }

    @Test
    public void testCheckedException() {
        final Response jerseyResponse = target().path("rest/exception/checked").request().get();
        _check500Response(jerseyResponse, CheckedExceptionMESSAGE);
        final Response servletResponse = target().path("servlet").queryParam("action", ThrowCheckedExceptionACTION).request().get();
        _check500Response(servletResponse, CheckedExceptionMESSAGE);
    }

    @Test
    public void testCustomException1() {
        Response jerseyResponse = target().path("rest/exception/custom1/big").request().get();
        MatcherAssert.assertThat(jerseyResponse.getStatus(), CoreMatchers.is(200));
        MatcherAssert.assertThat(jerseyResponse.readEntity(String.class), CoreMatchers.is(RESPONSE_BODY));
        MatcherAssert.assertThat(jerseyResponse.getHeaderString("My-Location"), CoreMatchers.is("exception/custom1/big"));
        MatcherAssert.assertThat(jerseyResponse.getHeaderString("My-Echo"), CoreMatchers.is("ECHOED: 1"));
        jerseyResponse = target().path("rest/exception/custom1/one").request().get();
        MatcherAssert.assertThat(jerseyResponse.getStatus(), CoreMatchers.is(200));
        MatcherAssert.assertThat(jerseyResponse.readEntity(String.class), CoreMatchers.is(RESPONSE_BODY));
        MatcherAssert.assertThat(jerseyResponse.getHeaderString("My-Location"), CoreMatchers.is("exception/custom1/one"));
        MatcherAssert.assertThat(jerseyResponse.getHeaderString("My-Echo"), CoreMatchers.is("ECHOED: 1"));
    }

    @Test
    public void testCustomException2() {
        Response jerseyResponse = target().path("rest/exception/custom2/small").request().get();
        MatcherAssert.assertThat(jerseyResponse.getStatus(), CoreMatchers.is(200));
        MatcherAssert.assertThat(jerseyResponse.readEntity(String.class), CoreMatchers.is(EjbExceptionMapperTwo.RESPONSE_BODY));
        MatcherAssert.assertThat(jerseyResponse.getHeaderString("My-Location"), CoreMatchers.is("exception/custom2/small"));
        MatcherAssert.assertThat(jerseyResponse.getHeaderString("My-Echo"), CoreMatchers.is("ECHOED: 2"));
        jerseyResponse = target().path("rest/exception/custom2/one").request().get();
        MatcherAssert.assertThat(jerseyResponse.getStatus(), CoreMatchers.is(200));
        MatcherAssert.assertThat(jerseyResponse.readEntity(String.class), CoreMatchers.is(EjbExceptionMapperTwo.RESPONSE_BODY));
        MatcherAssert.assertThat(jerseyResponse.getHeaderString("My-Location"), CoreMatchers.is("exception/custom2/one"));
        MatcherAssert.assertThat(jerseyResponse.getHeaderString("My-Echo"), CoreMatchers.is("ECHOED: 2"));
    }

    @Test
    public void testRemoteLocalEJBInterface() {
        final String message = "Hi there";
        final Response response = target().path("rest/echo").queryParam("message", message).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        final String responseMessage = response.readEntity(String.class);
        MatcherAssert.assertThat(responseMessage, CoreMatchers.startsWith(PREFIX));
        MatcherAssert.assertThat(responseMessage, CoreMatchers.endsWith(message));
    }

    @Test
    public void testRemoteAnnotationRegisteredEJBInterface() {
        final String message = "Hi there";
        final Response response = target().path("rest/raw-echo").queryParam("message", message).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        final String responseMessage = response.readEntity(String.class);
        MatcherAssert.assertThat(responseMessage, CoreMatchers.startsWith(PREFIX));
        MatcherAssert.assertThat(responseMessage, CoreMatchers.endsWith(message));
    }

    @Test
    public void testRequestCountGetsIncremented() {
        final Response response1 = target().path("rest/echo").queryParam("message", "whatever").request().get();
        MatcherAssert.assertThat(response1.getStatus(), CoreMatchers.is(200));
        final String counterHeader1 = response1.getHeaderString(RequestCountHEADER);
        final int requestCount1 = Integer.parseInt(counterHeader1);
        final Response response2 = target().path("rest/echo").queryParam("message", requestCount1).request().get();
        MatcherAssert.assertThat(response2.getStatus(), CoreMatchers.is(200));
        final int requestCount2 = Integer.parseInt(response2.getHeaderString(RequestCountHEADER));
        MatcherAssert.assertThat(requestCount2, CoreMatchers.is(Matchers.greaterThan(requestCount1)));
    }

    @Test
    public void testSync() {
        final Response response = target().path("rest/async-test/sync").request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        MatcherAssert.assertThat(response.readEntity(String.class), CoreMatchers.is("sync"));
    }

    @Test
    public void testAsync() {
        final Response response = target().path("rest/async-test/async").request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        MatcherAssert.assertThat(response.readEntity(String.class), CoreMatchers.is("async"));
    }

    @Test
    public void testAppIsEjbSingleton() {
        int c1 = target().path("rest/app/count").request().get(Integer.class);
        int c2 = target().path("rest/app/count").request().get(Integer.class);
        int c3 = target().path("rest/app/count").request().get(Integer.class);
        MatcherAssert.assertThat("the first count should be less than the second one", c1, CoreMatchers.is(Matchers.lessThan(c2)));
        MatcherAssert.assertThat("the second count should be less than the third one", c2, CoreMatchers.is(Matchers.lessThan(c3)));
    }
}

