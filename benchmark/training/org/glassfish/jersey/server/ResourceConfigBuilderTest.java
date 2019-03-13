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
package org.glassfish.jersey.server;


import java.io.IOException;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class ResourceConfigBuilderTest {
    @Test
    public void testEmpty() {
        ResourceConfig resourceConfig = new ResourceConfig();
        Assert.assertTrue(((resourceConfig.getClasses()) != null));
        Assert.assertTrue(resourceConfig.getClasses().isEmpty());
        Assert.assertTrue(((resourceConfig.getSingletons()) != null));
        Assert.assertTrue(resourceConfig.getSingletons().isEmpty());
    }

    @Test
    public void testClasses() {
        ResourceConfig resourceConfig = new ResourceConfig(ResourceConfigBuilderTest.class);
        Assert.assertTrue(((resourceConfig.getClasses()) != null));
        Assert.assertTrue(((resourceConfig.getClasses().size()) == 1));
        Assert.assertTrue(resourceConfig.getClasses().contains(ResourceConfigBuilderTest.class));
        Assert.assertTrue(((resourceConfig.getSingletons()) != null));
        Assert.assertTrue(resourceConfig.getSingletons().isEmpty());
    }

    @Test
    public void testSingletons() {
        final ResourceConfigBuilderTest resourceConfigBuilderTest = new ResourceConfigBuilderTest();
        ResourceConfig resourceConfig = new ResourceConfig().registerInstances(resourceConfigBuilderTest);
        Assert.assertTrue(((resourceConfig.getClasses()) != null));
        Assert.assertTrue(resourceConfig.getClasses().isEmpty());
        Assert.assertTrue(((resourceConfig.getSingletons()) != null));
        Assert.assertTrue(((resourceConfig.getSingletons().size()) == 1));
        Assert.assertTrue(resourceConfig.getSingletons().contains(resourceConfigBuilderTest));
    }

    @Test
    public void testApplication() {
        final Application application = new Application() {
            @Override
            public Set<Class<?>> getClasses() {
                return super.getClasses();
            }

            @Override
            public Set<Object> getSingletons() {
                return super.getSingletons();
            }
        };
        ApplicationHandler ah = new ApplicationHandler(application);
        Assert.assertTrue(ah.getInjectionManager().getInstance(Application.class).equals(application));
    }

    /**
     * test that I can initialize resource config with application class instead of an application instance
     * and then read the app properties
     */
    @Test
    public void testApplicationClassProperties() {
        ResourceConfig resourceConfig = ResourceConfigBuilderTest.initApp(ResourceConfigBuilderTest.MyApplication.class);
        Assert.assertTrue(resourceConfig.getProperties().containsKey("myProperty"));
        Assert.assertTrue(resourceConfig.getProperties().get("myProperty").equals("myValue"));
    }

    /**
     * test that I can initialize resource config with application class instead of an application instance
     * and then read the app classes
     */
    @Test
    public void testApplicationClassClasses() {
        ResourceConfig resourceConfig = ResourceConfigBuilderTest.initApp(ResourceConfigBuilderTest.MyApplication2.class);
        Assert.assertTrue((!(resourceConfig.getClasses().isEmpty())));
    }

    private static class MyApplication extends ResourceConfig {
        public MyApplication() {
            property("myProperty", "myValue");
        }
    }

    public static class MyApplication2 extends ResourceConfig {
        public MyApplication2() {
            super(ResourceConfigBuilderTest.MyResource.class);
        }
    }

    @Path("resource")
    public static class MyResource {
        @GET
        public String getIt() {
            return "get it";
        }
    }

    public static class TestProvider implements ReaderInterceptor {
        @Override
        public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
            return context.proceed();
        }
    }

    // Reproducer JERSEY-1637
    @Test
    public void testRegisterNullOrEmptyContracts() {
        final ResourceConfigBuilderTest.TestProvider provider = new ResourceConfigBuilderTest.TestProvider();
        final ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(ResourceConfigBuilderTest.TestProvider.class, ((Class<?>[]) (null)));
        Assert.assertFalse(resourceConfig.getConfiguration().isRegistered(ResourceConfigBuilderTest.TestProvider.class));
        resourceConfig.register(provider, ((Class<?>[]) (null)));
        Assert.assertFalse(resourceConfig.getConfiguration().isRegistered(ResourceConfigBuilderTest.TestProvider.class));
        Assert.assertFalse(resourceConfig.getConfiguration().isRegistered(provider));
        resourceConfig.register(ResourceConfigBuilderTest.TestProvider.class, new Class[0]);
        Assert.assertFalse(resourceConfig.getConfiguration().isRegistered(ResourceConfigBuilderTest.TestProvider.class));
        resourceConfig.register(provider, new Class[0]);
        Assert.assertFalse(resourceConfig.getConfiguration().isRegistered(ResourceConfigBuilderTest.TestProvider.class));
        Assert.assertFalse(resourceConfig.getConfiguration().isRegistered(provider));
    }
}

