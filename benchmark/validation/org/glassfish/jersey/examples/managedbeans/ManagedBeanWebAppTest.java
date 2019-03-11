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
package org.glassfish.jersey.examples.managedbeans;


import MediaTypes.WADL_TYPE;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Main test for the Managed Beans web application.
 * The application must be deployed and running on a standalone GlassFish container.
 * To run the tests then, you just launch the following command:
 * <pre>
 * mvn -DskipTests=false test</pre>
 *
 * @author Naresh Srinivas Bhimisetty
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public class ManagedBeanWebAppTest extends JerseyTest {
    /**
     * Test that provided query parameter makes it back.
     */
    @Test
    public void testPerRequestResource() {
        WebTarget perRequest = target().path("managedbean/per-request");
        String responseMsg = perRequest.queryParam("x", "X").request().get(String.class);
        MatcherAssert.assertThat(responseMsg, CoreMatchers.containsString("X"));
        MatcherAssert.assertThat(responseMsg, CoreMatchers.startsWith("INTERCEPTED"));
        responseMsg = perRequest.queryParam("x", "hi there").request().get(String.class);
        MatcherAssert.assertThat(responseMsg, CoreMatchers.containsString("hi there"));
        MatcherAssert.assertThat(responseMsg, CoreMatchers.startsWith("INTERCEPTED"));
    }

    /**
     * Test that singleton counter gets incremented with each call and can be reset.
     */
    @Test
    public void testSingletonResource() {
        WebTarget singleton = target().path("managedbean/singleton");
        String responseMsg = singleton.request().get(String.class);
        MatcherAssert.assertThat(responseMsg, CoreMatchers.containsString("3"));
        responseMsg = singleton.request().get(String.class);
        MatcherAssert.assertThat(responseMsg, CoreMatchers.containsString("4"));
        singleton.request().put(Entity.text("1"));
        responseMsg = singleton.request().get(String.class);
        MatcherAssert.assertThat(responseMsg, CoreMatchers.containsString("1"));
        responseMsg = singleton.request().get(String.class);
        MatcherAssert.assertThat(responseMsg, CoreMatchers.containsString("2"));
    }

    /**
     * Test the JPA backend.
     */
    @Test
    public void testWidget() {
        WebTarget target = target().path("managedbean/singleton/widget");
        final WebTarget widget = target.path("1");
        MatcherAssert.assertThat(widget.request().get().getStatus(), CoreMatchers.is(404));
        widget.request().put(Entity.text("One"));
        MatcherAssert.assertThat(widget.request().get(String.class), CoreMatchers.is("One"));
        widget.request().put(Entity.text("Two"));
        MatcherAssert.assertThat(widget.request().get(String.class), CoreMatchers.is("Two"));
        MatcherAssert.assertThat(widget.request().delete().getStatus(), CoreMatchers.is(204));
        MatcherAssert.assertThat(widget.request().get().getStatus(), CoreMatchers.is(404));
    }

    /**
     * Test exceptions are properly mapped.
     */
    @Test
    public void testExceptionMapper() {
        WebTarget singletonTarget = target().path("managedbean/singleton/exception");
        WebTarget perRequestTarget = target().path("managedbean/per-request/exception");
        _testExceptionOutput(singletonTarget, "singleton");
        _testExceptionOutput(perRequestTarget, "per-request");
    }

    /**
     * Test a non empty WADL is generated.
     */
    @Test
    public void testApplicationWadl() {
        WebTarget wadl = target().path("application.wadl");
        String wadlDoc = wadl.request(WADL_TYPE).get(String.class);
        MatcherAssert.assertThat(wadlDoc.length(), CoreMatchers.is(CoreMatchers.not(0)));
    }
}

