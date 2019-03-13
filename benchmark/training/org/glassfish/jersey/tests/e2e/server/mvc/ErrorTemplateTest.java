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
package org.glassfish.jersey.tests.e2e.server.mvc;


import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import org.glassfish.jersey.server.mvc.ErrorTemplate;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


/**
 *
 *
 * @author Michal Gajdos
 */
public class ErrorTemplateTest extends JerseyTest {
    @Path("/")
    public static class ErrorTemplateResource {
        @GET
        @ErrorTemplate
        public String method() {
            throw new RuntimeException("ErrorTemplate");
        }

        @GET
        @Path("methodRelativePath")
        @ErrorTemplate(name = "relative")
        public String methodRelativePath() {
            throw new RuntimeException("ErrorTemplate");
        }

        @GET
        @Path("methodAbsolutePath")
        @ErrorTemplate(name = "/org/glassfish/jersey/tests/e2e/server/mvc/ErrorTemplateTest/ErrorTemplateResource/absolute")
        public String methodAbsolutePath() {
            throw new RuntimeException("ErrorTemplate");
        }

        @Path("subResource")
        public ErrorTemplateTest.ErrorTemplateResource subResource() {
            return new ErrorTemplateTest.ErrorTemplateResource();
        }

        @Path("subResourceTemplate")
        @ErrorTemplate
        public ErrorTemplateTest.ErrorTemplateSubResource subResourceTemplate() {
            return new ErrorTemplateTest.ErrorTemplateSubResource();
        }
    }

    public static class ErrorTemplateSubResource {
        @GET
        public String get() {
            throw new RuntimeException("ErrorTemplate");
        }
    }

    @Test(expected = InternalServerErrorException.class)
    public void testErrorMethodTemplateSubResource() throws Exception {
        target("subResourceTemplate").request().get(String.class);
    }

    @Test
    public void testErrorTemplate() throws Exception {
        testErrorTemplate(target());
    }

    @Test
    public void testErrorTemplateSubResource() throws Exception {
        testErrorTemplate(target("subResource"));
    }
}

