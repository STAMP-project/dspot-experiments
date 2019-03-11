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
package org.glassfish.jersey.server.model;


import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.junit.Assert;
import org.junit.Test;


/**
 * Taken from Jersey 1: jersey-tests: com.sun.jersey.impl.errors.PathAndResourceMethodErrorsTest
 *
 * @author Paul Sandoz
 */
public class PathAndResourceMethodErrorsTest {
    @Path("/{")
    public static class PathErrorsResource {
        @Path("/{")
        @GET
        public String get() {
            return null;
        }

        @Path("/{sub")
        public Object sub() {
            return null;
        }
    }

    // TODO: testing not yet available feature (registering explicit resources).
    @Path("/{one}")
    public static class PathErrorsOneResource {}

    @Path("/{two}")
    public static class PathErrorsTwoResource {}

    @Path("/{three}")
    public static class PathErrorsThreeResource {}

    @Path("/")
    public static class AmbiguousResourceMethodsGET {
        @GET
        public String get1() {
            return null;
        }

        @GET
        public String get2() {
            return null;
        }

        @GET
        public String get3() {
            return null;
        }
    }

    @Test
    public void testAmbiguousResourceMethodGET() {
        final List<ResourceModelIssue> issues = initiateWebApplication(PathAndResourceMethodErrorsTest.AmbiguousResourceMethodsGET.class);
        Assert.assertEquals(3, issues.size());
    }

    @Path("/")
    public static class AmbiguousResourceMethodsProducesGET {
        @GET
        @Produces("application/xml")
        public String getXml() {
            return null;
        }

        @GET
        @Produces("text/plain")
        public String getText1() {
            return null;
        }

        @GET
        @Produces("text/plain")
        public String getText2() {
            return null;
        }

        @GET
        @Produces({ "text/plain", "image/png" })
        public String getText3() {
            return null;
        }
    }

    @Test
    public void testAmbiguousResourceMethodsProducesGET() {
        final List<ResourceModelIssue> issues = initiateWebApplication(PathAndResourceMethodErrorsTest.AmbiguousResourceMethodsProducesGET.class);
        Assert.assertEquals(3, issues.size());
    }

    @Path("/")
    public static class AmbiguousResourceMethodsConsumesPUT {
        @PUT
        @Consumes("application/xml")
        public void put1(Object o) {
        }

        @PUT
        @Consumes({ "text/plain", "image/jpeg" })
        public void put2(Object o) {
        }

        @PUT
        @Consumes("text/plain")
        public void put3(Object o) {
        }
    }

    @Test
    public void testAmbiguousResourceMethodsConsumesPUT() {
        final List<ResourceModelIssue> issues = initiateWebApplication(PathAndResourceMethodErrorsTest.AmbiguousResourceMethodsConsumesPUT.class);
        Assert.assertEquals(1, issues.size());
    }

    @Path("/")
    public static class AmbiguousSubResourceMethodsGET {
        @Path("{one}")
        @GET
        public String get1() {
            return null;
        }

        @Path("{seven}")
        @GET
        public String get2() {
            return null;
        }

        @Path("{million}")
        @GET
        public String get3() {
            return null;
        }

        @Path("{million}/")
        @GET
        public String get4() {
            return null;
        }
    }

    @Test
    public void testAmbiguousSubResourceMethodsGET() {
        final List<ResourceModelIssue> issues = initiateWebApplication(PathAndResourceMethodErrorsTest.AmbiguousSubResourceMethodsGET.class);
        Assert.assertEquals(6, issues.size());
    }

    @Path("/")
    public static class AmbiguousSubResourceMethodsProducesGET {
        @Path("x")
        @GET
        @Produces("application/xml")
        public String getXml() {
            return null;
        }

        @Path("x")
        @GET
        @Produces("text/plain")
        public String getText1() {
            return null;
        }

        @Path("x")
        @GET
        @Produces("text/plain")
        public String getText2() {
            return null;
        }

        @Path("x")
        @GET
        @Produces({ "text/plain", "image/png" })
        public String getText3() {
            return null;
        }
    }

    @Test
    public void testAmbiguousClassProducingWarnings() {
        final List<ResourceModelIssue> issues = initiateWebApplication(PathAndResourceMethodErrorsTest.AmbiguousClassProducingWarnings.class);
        Assert.assertEquals(2, issues.size());
        assertNumberOfIssues(issues, 1, 1);
    }

    @Path("test")
    public static class AmbiguousClassProducingWarnings {
        // GET methods validation will produce a warning
        @GET
        @Produces("text/plain")
        public String getHtml() {
            return null;
        }

        @GET
        public String getAllPossible() {
            return null;
        }

        // POST methods validation will fail
        @POST
        @Consumes("text/plain")
        public String postA() {
            return null;
        }

        @POST
        @Consumes("text/plain")
        public String postB() {
            return null;
        }
    }

    @Test
    public void testAmbiguousPostClassProducingWarnings() {
        final List<ResourceModelIssue> issues = initiateWebApplication(PathAndResourceMethodErrorsTest.AmbiguousPostClassProducingWarnings.class);
        Assert.assertEquals(2, issues.size());
        assertNumberOfIssues(issues, 1, 1);
    }

    @Path("test2")
    public static class AmbiguousPostClassProducingWarnings {
        // GET methods validation will fail
        @GET
        @Produces("text/plain")
        public String getPlain() {
            return null;
        }

        @GET
        @Produces("text/plain")
        public String getPlain2() {
            return null;
        }

        // POST methods validation will produce a warning
        @POST
        @Consumes("text/plain")
        @Produces("text/plain")
        public String postA() {
            return null;
        }

        @POST
        @Consumes("text/plain")
        public String postB() {
            return null;
        }
    }

    @Test
    public void testAmbiguousSubResourceMethodsProducesGET() {
        final List<ResourceModelIssue> issues = initiateWebApplication(PathAndResourceMethodErrorsTest.AmbiguousSubResourceMethodsProducesGET.class);
        Assert.assertEquals(3, issues.size());
    }

    @Path("/")
    public static class AmbiguousSubResourceLocatorsResource {
        @Path("{one}")
        public Object l1() {
            return null;
        }

        @Path("{two}")
        public Object l2() {
            return null;
        }
    }

    @Test
    public void testAmbiguousSubResourceLocatorsResource() {
        final List<ResourceModelIssue> issues = initiateWebApplication(PathAndResourceMethodErrorsTest.AmbiguousSubResourceLocatorsResource.class);
        Assert.assertEquals(1, issues.size());
    }

    @Path("/")
    public static class AmbiguousSubResourceLocatorsWithSlashResource {
        @Path("{one}")
        public Object l1() {
            return null;
        }

        @Path("{two}/")
        public Object l2() {
            return null;
        }
    }
}

