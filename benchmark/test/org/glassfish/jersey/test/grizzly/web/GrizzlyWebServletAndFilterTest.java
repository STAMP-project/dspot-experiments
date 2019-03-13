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
package org.glassfish.jersey.test.grizzly.web;


import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory} support for
 * servlet + filter deployment scenarios.
 *
 * @author pavel.bucek@oracle.com
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class GrizzlyWebServletAndFilterTest extends JerseyTest {
    public static class MyServlet extends ServletContainer {
        public static boolean visited = false;

        @Override
        public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            super.service(req, resp);
            GrizzlyWebServletAndFilterTest.MyServlet.visited = true;
        }
    }

    public static class MyFilter1 implements Filter {
        public static boolean visited = false;

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            GrizzlyWebServletAndFilterTest.MyFilter1.visited = true;
            filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {
        }
    }

    public static class MyFilter2 implements Filter {
        public static boolean visited = false;

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            GrizzlyWebServletAndFilterTest.MyFilter2.visited = true;
            filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {
        }
    }

    @Path("GrizzlyWebServletAndFilterTest")
    public static class TestResource {
        @GET
        public String get() {
            return "GET";
        }
    }

    @Test
    public void testGet() {
        WebTarget target = target("GrizzlyWebServletAndFilterTest");
        String s = target.request().get(String.class);
        Assert.assertEquals("GET", s);
        Assert.assertTrue(GrizzlyWebServletAndFilterTest.MyServlet.visited);
        Assert.assertTrue(GrizzlyWebServletAndFilterTest.MyFilter1.visited);
        Assert.assertTrue(GrizzlyWebServletAndFilterTest.MyFilter2.visited);
    }
}

