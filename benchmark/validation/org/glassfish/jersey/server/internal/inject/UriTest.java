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
package org.glassfish.jersey.server.internal.inject;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.server.ClientBinding;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.RequestContextBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.Uri;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class UriTest extends AbstractTest {
    @Path("test")
    public static class Resource1 {
        @Uri("http://oracle.com")
        WebTarget webTarget1;

        @GET
        @Path("1")
        public String doGet1() {
            return webTarget1.getUri().toString();
        }

        @GET
        @Path("2")
        public String doGet2(@Uri("http://oracle.com")
        WebTarget webTarget2) {
            return webTarget2.getUri().toString();
        }
    }

    @Path("test")
    public static class Resource2 {
        @Uri("http://oracle.com/{param}")
        WebTarget webTarget1;

        @GET
        @Path("1")
        public String doGet1() {
            return (webTarget1.getUri()) == null ? "null" : webTarget1.getUri().toString();
        }

        @GET
        @Path("{param}")
        public String doGet2(@Uri("http://oracle.com/{param}")
        WebTarget webTarget2) {
            return webTarget2.getUri().toString();
        }
    }

    @Path("test")
    public static class Resource3 {
        @Uri("{param}")
        WebTarget webTarget1;

        @GET
        @Path("1")
        public String doGet1() {
            return (webTarget1.getUri()) == null ? "null" : webTarget1.getUri().toString();
        }

        @GET
        @Path("{param}")
        public String doGet2(@Uri("{param}")
        WebTarget webTarget2) {
            return webTarget2.getUri().toString();
        }
    }

    @ClientBinding
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    public static @interface Managed {}

    @Path("test")
    public static class Resource4 {
        @Uri("http://oracle.com")
        @UriTest.Managed
        WebTarget webTarget1;

        @GET
        @Path("1")
        public String doGet1() {
            return ((String) (webTarget1.getConfiguration().getProperties().get("test-property")));
        }

        @GET
        @Path("2")
        public String doGet2(@Uri("http://oracle.com")
        @UriTest.Managed
        WebTarget webTarget2) {
            return ((String) (webTarget2.getConfiguration().getProperties().get("test-property")));
        }

        @GET
        @Path("3")
        public String doGet3(@Uri("relative")
        @UriTest.Managed
        WebTarget relativeTarget) {
            return relativeTarget.getUri().toString();
        }
    }

    @Test
    public void testGet1() throws InterruptedException, ExecutionException {
        initiateWebApplication(UriTest.Resource1.class);
        final ContainerResponse response = apply(RequestContextBuilder.from("/test/1", "GET").build());
        Assert.assertEquals("http://oracle.com", response.getEntity());
    }

    @Test
    public void testGet2() throws InterruptedException, ExecutionException {
        initiateWebApplication(UriTest.Resource1.class);
        final ContainerResponse response = apply(RequestContextBuilder.from("/test/2", "GET").build());
        Assert.assertEquals("http://oracle.com", response.getEntity());
    }

    @Test
    public void testGetParam1() throws InterruptedException, ExecutionException {
        initiateWebApplication(UriTest.Resource2.class);
        try {
            apply(RequestContextBuilder.from("/test/1", "GET").build());
        } catch (ExecutionException ex) {
            // ISE thrown from WebTarget
            Assert.assertThat(ex.getCause(), CoreMatchers.instanceOf(IllegalStateException.class));
            // IAE thrown from UriBuilder - unresolved template parameter value
            Assert.assertThat(ex.getCause().getCause(), CoreMatchers.instanceOf(IllegalArgumentException.class));
        }
    }

    @Test
    public void testGetParam2() throws InterruptedException, ExecutionException {
        initiateWebApplication(UriTest.Resource2.class);
        final ContainerResponse response = apply(RequestContextBuilder.from("/test/parameter", "GET").build());
        Assert.assertEquals("http://oracle.com/parameter", response.getEntity());
    }

    @Test
    public void testGetRelative1() throws InterruptedException, ExecutionException {
        initiateWebApplication(UriTest.Resource3.class);
        try {
            apply(RequestContextBuilder.from("/test/1", "GET").build());
        } catch (ExecutionException ex) {
            // ISE thrown from WebTarget
            Assert.assertThat(ex.getCause(), CoreMatchers.instanceOf(IllegalStateException.class));
            // IAE thrown from UriBuilder - unresolved template parameter value
            Assert.assertThat(ex.getCause().getCause(), CoreMatchers.instanceOf(IllegalArgumentException.class));
        }
    }

    @Test
    public void testGetRelative2() throws InterruptedException, ExecutionException {
        initiateWebApplication(UriTest.Resource3.class);
        final ContainerResponse response = apply(RequestContextBuilder.from("/test/parameter", "GET").build());
        Assert.assertEquals("/parameter", response.getEntity());
    }

    @Test
    public void testManagedClientInjection1() throws InterruptedException, ExecutionException {
        final ResourceConfig resourceConfig = new ResourceConfig(UriTest.Resource4.class);
        // TODO introduce new ResourceConfig.setClientProperty(Class<? extends Annotation>, String name, Object value) helper method
        resourceConfig.property(((UriTest.Managed.class.getName()) + ".property.test-property"), "test-value");
        initiateWebApplication(resourceConfig);
        final ContainerResponse response = apply(RequestContextBuilder.from("/test/1", "GET").build());
        Assert.assertEquals("test-value", response.getEntity());
    }

    @Test
    public void testManagedClientInjection2() throws InterruptedException, ExecutionException {
        final ResourceConfig resourceConfig = new ResourceConfig(UriTest.Resource4.class);
        resourceConfig.property(((UriTest.Managed.class.getName()) + ".property.test-property"), "test-value");
        initiateWebApplication(resourceConfig);
        final ContainerResponse response = apply(RequestContextBuilder.from("/test/2", "GET").build());
        Assert.assertEquals("test-value", response.getEntity());
    }

    @Test
    public void testManagedClientInjection3() throws InterruptedException, ExecutionException {
        final ResourceConfig resourceConfig = new ResourceConfig(UriTest.Resource4.class);
        resourceConfig.property(((UriTest.Managed.class.getName()) + ".property.test-property"), "test-value");
        resourceConfig.property(((UriTest.Managed.class.getName()) + ".baseUri"), "http://oracle.com");
        initiateWebApplication(resourceConfig);
        final ContainerResponse response = apply(RequestContextBuilder.from("/test/3", "GET").build());
        Assert.assertEquals("http://oracle.com/relative", response.getEntity());
    }

    @Path("test")
    public static class Resource5 {
        @Uri("http://oracle.com/{template}")
        WebTarget webTarget1;

        @GET
        @Path("1")
        public String doGet1() {
            return webTarget1.resolveTemplate("template", "foo").getUri().toString();
        }

        @GET
        @Path("2")
        public String doGet2(@Uri("http://oracle.com/{template}")
        WebTarget webTarget2) {
            return webTarget2.resolveTemplate("template", "bar").getUri().toString();
        }
    }

    @Test
    public void testResolveTemplateInFieldManagedClient() throws Exception {
        initiateWebApplication(UriTest.Resource5.class);
        final ContainerResponse response = apply(RequestContextBuilder.from("/test/1", "GET").build());
        Assert.assertThat(response.getEntity().toString(), CoreMatchers.equalTo("http://oracle.com/foo"));
    }

    @Test
    public void testResolveTemplateInParamManagedClient() throws Exception {
        initiateWebApplication(UriTest.Resource5.class);
        final ContainerResponse response = apply(RequestContextBuilder.from("/test/2", "GET").build());
        Assert.assertThat(response.getEntity().toString(), CoreMatchers.equalTo("http://oracle.com/bar"));
    }
}

