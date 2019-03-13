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


import MediaType.APPLICATION_JSON_TYPE;
import MediaType.TEXT_HTML_TYPE;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import org.glassfish.jersey.server.mvc.Template;
import org.glassfish.jersey.server.mvc.Viewable;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that {@link Template} annotated methods are selected by the routing algorithms as if they
 * would actually return {@link Viewable} instead of the model.
 *
 * @author Miroslav Fuksa
 */
public class TemplateMethodSelectionTest extends JerseyTest {
    private static final Map<String, String> MODEL = new HashMap<String, String>() {
        {
            put("a", "hello");
            put("b", "world");
        }
    };

    @XmlRootElement
    public static class MyBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

    @Path("annotatedMethod")
    public static class TemplateAnnotatedResourceMethod {
        @GET
        @Produces(MediaType.TEXT_HTML)
        @Template
        public Map<String, String> getAsHTML() {
            return TemplateMethodSelectionTest.MODEL;
        }

        @GET
        @Produces("application/json")
        public TemplateMethodSelectionTest.MyBean getAsJSON() {
            return TemplateMethodSelectionTest.getMyBean();
        }
    }

    @Path("noTemplate")
    public static class NoTemplateResource {
        @GET
        @Produces(MediaType.TEXT_HTML)
        public Map<String, String> getAsHTML() {
            return TemplateMethodSelectionTest.MODEL;
        }

        @GET
        @Produces("application/json")
        public TemplateMethodSelectionTest.MyBean getAsJSON() {
            return TemplateMethodSelectionTest.getMyBean();
        }
    }

    @Path("annotatedClass")
    @Template
    @Produces(MediaType.TEXT_HTML)
    public static class TemplateAnnotatedResource {
        @GET
        @Produces("application/json")
        public TemplateMethodSelectionTest.MyBean getAsJSON() {
            return TemplateMethodSelectionTest.getMyBean();
        }

        @Override
        public String toString() {
            return "This toString() method will be used to get model.";
        }
    }

    @Path("basic")
    public static class BasicResource {
        @GET
        @Produces(MediaType.TEXT_HTML)
        public String getAsHTML() {
            return "Hello World";
        }

        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public TemplateMethodSelectionTest.MyBean getAsJSON() {
            return TemplateMethodSelectionTest.getMyBean();
        }
    }

    @Path("viewable")
    public static class AsViewableResource {
        @GET
        @Produces(MediaType.TEXT_HTML)
        public Viewable getAsHTML() {
            return new Viewable("index.testp", TemplateMethodSelectionTest.MODEL);
        }

        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public TemplateMethodSelectionTest.MyBean getAsJSON() {
            return TemplateMethodSelectionTest.getMyBean();
        }
    }

    /**
     * This test makes request for text/html which is preferred. The resource defines the method
     * {@link org.glassfish.jersey.tests.e2e.server.mvc.TemplateMethodSelectionTest.TemplateAnnotatedResourceMethod#getAsHTML()}
     * which returns {@link Map} for which there is not {@link javax.ws.rs.ext.MessageBodyWriter}. The absence of the
     * writer would cause that the method would not have been selected but as the {@link Template} annotation
     * is on the method, the {@link org.glassfish.jersey.server.internal.routing.MethodSelectingRouter} considers
     * it as if this would have been {@link Viewable} instead of the {@link Map}.
     */
    @Test
    public void testAnnotatedMethodByTemplateHtml() {
        final Response response = target().path("annotatedMethod").request("text/html;q=0.8", "application/json;q=0.7").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(TEXT_HTML_TYPE, response.getMediaType());
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.anyOf(CoreMatchers.containsString("{b=world, a=hello}"), CoreMatchers.containsString("{a=hello, b=world}")));
    }

    @Test
    public void testAnnotatedMethodByTemplateJson() {
        final Response response = target().path("annotatedMethod").request("text/html;q=0.6", "application/json;q=0.7").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
        Assert.assertEquals("hello", response.readEntity(TemplateMethodSelectionTest.MyBean.class).getName());
    }

    @Test
    public void testAnnotatedClassByTemplateHtml() {
        final Response response = target().path("annotatedClass").request("text/html;q=0.8", "application/json;q=0.7").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(TEXT_HTML_TYPE, response.getMediaType());
        Assert.assertTrue(response.readEntity(String.class).contains("model=This toString() method will be used to get model."));
    }

    @Test
    public void testAnnotatedClassByTemplateJson() {
        final Response response = target().path("annotatedClass").request("text/html;q=0.6", "application/json;q=0.7").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
        Assert.assertEquals("hello", response.readEntity(TemplateMethodSelectionTest.MyBean.class).getName());
    }

    @Test
    public void testBasicHtml() {
        final Response response = target().path("basic").request("text/html;q=0.8", "application/json;q=0.7").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(TEXT_HTML_TYPE, response.getMediaType());
        Assert.assertTrue(response.readEntity(String.class).contains("Hello World"));
    }

    @Test
    public void testBasicJson() {
        final Response response = target().path("basic").request("text/html;q=0.6", "application/json;q=0.7").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
        Assert.assertEquals("hello", response.readEntity(TemplateMethodSelectionTest.MyBean.class).getName());
    }

    @Test
    public void testAsViewableHtml() {
        final Response response = target().path("viewable").request("text/html;q=0.8", "application/json;q=0.7").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(TEXT_HTML_TYPE, response.getMediaType());
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.anyOf(CoreMatchers.containsString("{b=world, a=hello}"), CoreMatchers.containsString("{a=hello, b=world}")));
    }

    @Test
    public void testAsViewableJson() {
        final Response response = target().path("viewable").request("text/html;q=0.6", "application/json;q=0.7").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
        Assert.assertEquals("hello", response.readEntity(TemplateMethodSelectionTest.MyBean.class).getName());
    }

    /**
     * This test verifies that there is really no {@link javax.ws.rs.ext.MessageBodyWriter}
     * for {@code Map<String,String>}}. text/html is requested but application/json is chosen there is no
     * MBW for {@code Map}.
     */
    @Test
    public void testNoTemplateHtml() {
        final Response response = target().path("noTemplate").request("text/html;q=0.9", "application/json;q=0.7").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
        Assert.assertEquals("hello", response.readEntity(TemplateMethodSelectionTest.MyBean.class).getName());
    }

    @Test
    public void testNoTemplateJson() {
        final Response response = target().path("noTemplate").request("text/html;q=0.6", "application/json;q=0.7").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
        Assert.assertEquals("hello", response.readEntity(TemplateMethodSelectionTest.MyBean.class).getName());
    }
}

