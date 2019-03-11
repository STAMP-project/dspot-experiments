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
package org.glassfish.jersey.tests.e2e.server;


import InternalProperties.JSON_FEATURE_CLIENT;
import java.io.IOException;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michal Gajdos
 */
public class RuntimeConfigTest extends JerseyTest {
    @Path("/")
    public static class Resource {
        @GET
        public String get() {
            return "get";
        }
    }

    public static class EmptyFeature implements Feature {
        @Override
        public boolean configure(final FeatureContext context) {
            return true;
        }
    }

    public static class ClientFeature implements Feature {
        @Override
        public boolean configure(final FeatureContext context) {
            context.register(RuntimeConfigTest.ClientReaderInterceptor.class);
            context.property("foo", "bar");
            return true;
        }
    }

    public static class ClientReaderInterceptor implements ReaderInterceptor {
        private final Configuration config;

        @Inject
        public ClientReaderInterceptor(final Configuration configuration) {
            this.config = configuration;
        }

        @Override
        public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
            Assert.assertTrue(config.isRegistered(RuntimeConfigTest.ClientFeature.class));
            Assert.assertTrue(config.isRegistered(RuntimeConfigTest.ClientReaderInterceptor.class));
            Assert.assertThat(config.getProperties().size(), CoreMatchers.is(2));
            Assert.assertThat(config.getProperty("foo").toString(), CoreMatchers.is("bar"));
            // JsonFeature
            Assert.assertThat(config.getProperty(JSON_FEATURE_CLIENT), CoreMatchers.notNullValue());
            // MetaInfAutoDiscoverable
            Assert.assertThat(config.getInstances().size(), CoreMatchers.is(1));
            Assert.assertTrue(config.isEnabled(RuntimeConfigTest.ClientFeature.class));
            context.getHeaders().add("CustomHeader", "ClientReaderInterceptor");
            return context.proceed();
        }
    }

    @Test
    public void testRuntimeClientConfig() throws Exception {
        final WebTarget target = target();
        target.register(RuntimeConfigTest.ClientFeature.class);
        final Response response = target.request(MediaType.WILDCARD_TYPE).get(Response.class);
        Assert.assertEquals(1, target.getConfiguration().getClasses().size());
        Assert.assertTrue(target.getConfiguration().isRegistered(RuntimeConfigTest.ClientFeature.class));
        Assert.assertTrue(target.getConfiguration().getInstances().isEmpty());
        Assert.assertTrue(target.getConfiguration().getProperties().isEmpty());
        Assert.assertFalse(target.getConfiguration().isEnabled(RuntimeConfigTest.ClientFeature.class));
        WebTarget t = target();
        Assert.assertEquals(0, t.getConfiguration().getClasses().size());
        Assert.assertFalse(t.getConfiguration().isRegistered(RuntimeConfigTest.ClientFeature.class));
        Assert.assertTrue(t.getConfiguration().getInstances().isEmpty());
        Assert.assertTrue(t.getConfiguration().getProperties().isEmpty());
        Assert.assertFalse(t.getConfiguration().isEnabled(RuntimeConfigTest.ClientFeature.class));
        Assert.assertEquals("get", response.readEntity(String.class));
        Assert.assertEquals("ClientReaderInterceptor", response.getHeaderString("CustomHeader"));
    }
}

