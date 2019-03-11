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
package org.glassfish.jersey.server;


import ResourceModel.Builder;
import java.util.concurrent.ExecutionException;
import javax.annotation.Priority;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceModel;
import org.junit.Test;


/**
 * Test scope of resources enhanced by model processors.
 *
 * @author Miroslav Fuksa
 */
public class ModelProcessorScopeTest {
    public static class ModelProcessorFeature implements Feature {
        @Override
        public boolean configure(FeatureContext context) {
            context.register(ModelProcessorScopeTest.ModelProcessorFeature.SimpleModelProcessor.class);
            return true;
        }

        @Priority(5000)
        private static class SimpleModelProcessor implements ModelProcessor {
            @Override
            public ResourceModel processResourceModel(ResourceModel resourceModel, Configuration configuration) {
                ResourceModel.Builder builder = new ResourceModel.Builder(resourceModel.getRootResources(), false);
                final Resource singletonResource = Resource.from(ModelProcessorScopeTest.ModelProcessorFeature.SingletonResource.class);
                builder.addResource(singletonResource);
                final Resource requestScopeResource = Resource.from(ModelProcessorScopeTest.ModelProcessorFeature.RequestScopeResource.class);
                builder.addResource(requestScopeResource);
                final Resource.Builder resourceBuilder = Resource.builder("instance");
                resourceBuilder.addMethod("GET").handledBy(new org.glassfish.jersey.process.Inflector<ContainerRequestContext, String>() {
                    private int counter = 0;

                    @Override
                    public String apply(ContainerRequestContext containerRequestContext) {
                        return String.valueOf(("Inflector:" + ((counter)++)));
                    }
                });
                final Resource instanceResource = resourceBuilder.build();
                builder.addResource(instanceResource);
                return builder.build();
            }

            @Override
            public ResourceModel processSubResource(ResourceModel subResource, Configuration configuration) {
                final Resource resource = Resource.builder().mergeWith(Resource.from(ModelProcessorScopeTest.EnhancedSubResourceSingleton.class)).mergeWith(Resource.from(ModelProcessorScopeTest.EnhancedSubResource.class)).mergeWith(subResource.getResources().get(0)).build();
                return new ResourceModel.Builder(true).addResource(resource).build();
            }
        }

        @Path("request-scope")
        public static class RequestScopeResource {
            private int counter = 0;

            @GET
            public String get() {
                return String.valueOf(("RequestScopeResource:" + ((counter)++)));
            }
        }

        @Path("singleton")
        @Singleton
        public static class SingletonResource {
            private int counter = 0;

            @GET
            public String get() {
                return String.valueOf(("SingletonResource:" + ((counter)++)));
            }
        }
    }

    @Path("root")
    public static class RootResource {
        @GET
        public String get() {
            return "root";
        }

        @Path("sub-resource-singleton")
        public Class<ModelProcessorScopeTest.SubResourceSingleton> getSubResourceSingleton() {
            return ModelProcessorScopeTest.SubResourceSingleton.class;
        }

        @Path("sub-resource-instance")
        public ModelProcessorScopeTest.SubResourceSingleton getSubResourceSingletonInstance() {
            return new ModelProcessorScopeTest.SubResourceSingleton();
        }
    }

    @Singleton
    public static class SubResourceSingleton {
        private int counter = 0;

        @GET
        public String get() {
            return String.valueOf(("SubResourceSingleton:" + ((counter)++)));
        }
    }

    public static class EnhancedSubResource {
        private int counter = 0;

        @GET
        @Path("enhanced")
        public String get() {
            return String.valueOf(("EnhancedSubResource:" + ((counter)++)));
        }
    }

    @Singleton
    public static class EnhancedSubResourceSingleton {
        private int counter = 0;

        @GET
        @Path("enhanced-singleton")
        public String get() {
            return "EnhancedSubResourceSingleton:" + (String.valueOf(((counter)++)));
        }
    }

    @Path("root-singleton")
    @Singleton
    public static class RootSingletonResource {
        private int counter = 0;

        @GET
        public String get() {
            return "RootSingletonResource:" + (String.valueOf(((counter)++)));
        }
    }

    @Test
    public void testSingleton() throws InterruptedException, ExecutionException {
        ApplicationHandler applicationHandler = new ApplicationHandler(new ResourceConfig(ModelProcessorScopeTest.ModelProcessorFeature.SingletonResource.class));
        final String requestUri = "/singleton";
        _testCounter(applicationHandler, requestUri, "SingletonResource", "1");
    }

    @Test
    public void testSingletonInModelProcessor() throws InterruptedException, ExecutionException {
        ApplicationHandler applicationHandler = new ApplicationHandler(new ResourceConfig(ModelProcessorScopeTest.RootResource.class, ModelProcessorScopeTest.ModelProcessorFeature.class));
        final String requestUri = "/singleton";
        _testCounter(applicationHandler, requestUri, "SingletonResource", "1");
    }

    @Test
    public void testSubResourceSingletonInOriginalModel() throws InterruptedException, ExecutionException {
        ApplicationHandler applicationHandler = new ApplicationHandler(new ResourceConfig(ModelProcessorScopeTest.RootResource.class, ModelProcessorScopeTest.ModelProcessorFeature.class));
        final String requestUri = "/root/sub-resource-singleton";
        _testCounter(applicationHandler, requestUri, "SubResourceSingleton", "1");
    }

    @Test
    public void testSubResourceEnhancedSingleton() throws InterruptedException, ExecutionException {
        ApplicationHandler applicationHandler = new ApplicationHandler(new ResourceConfig(ModelProcessorScopeTest.RootResource.class, ModelProcessorScopeTest.ModelProcessorFeature.class));
        final String requestUri = "/root/sub-resource-singleton/enhanced-singleton";
        _testCounter(applicationHandler, requestUri, "EnhancedSubResourceSingleton", "1");
    }

    @Test
    public void testSubResourceInstanceEnhancedSingleton() throws InterruptedException, ExecutionException {
        ApplicationHandler applicationHandler = new ApplicationHandler(new ResourceConfig(ModelProcessorScopeTest.RootResource.class, ModelProcessorScopeTest.ModelProcessorFeature.class));
        final String requestUri = "/root/sub-resource-instance/enhanced-singleton";
        _testCounter(applicationHandler, requestUri, "EnhancedSubResourceSingleton", "1");
    }

    @Test
    public void testSubResourceInstanceEnhancedSubResource() throws InterruptedException, ExecutionException {
        ApplicationHandler applicationHandler = new ApplicationHandler(new ResourceConfig(ModelProcessorScopeTest.RootResource.class, ModelProcessorScopeTest.ModelProcessorFeature.class));
        final String requestUri = "/root/sub-resource-instance/enhanced";
        _testCounter(applicationHandler, requestUri, "EnhancedSubResource", "0");
    }

    @Test
    public void testSubResourceEnhancedSubResource() throws InterruptedException, ExecutionException {
        ApplicationHandler applicationHandler = new ApplicationHandler(new ResourceConfig(ModelProcessorScopeTest.RootResource.class, ModelProcessorScopeTest.ModelProcessorFeature.class));
        final String requestUri = "/root/sub-resource-singleton/enhanced";
        _testCounter(applicationHandler, requestUri, "EnhancedSubResource", "0");
    }

    @Test
    public void testInstanceInModelProcessor() throws InterruptedException, ExecutionException {
        ApplicationHandler applicationHandler = new ApplicationHandler(new ResourceConfig(ModelProcessorScopeTest.RootResource.class, ModelProcessorScopeTest.ModelProcessorFeature.class));
        final String requestUri = "/instance";
        _testCounter(applicationHandler, requestUri, "Inflector", "1");
    }

    @Test
    public void testRootSingleton() throws InterruptedException, ExecutionException {
        ApplicationHandler applicationHandler = new ApplicationHandler(new ResourceConfig(ModelProcessorScopeTest.RootResource.class, ModelProcessorScopeTest.RootSingletonResource.class, ModelProcessorScopeTest.ModelProcessorFeature.class));
        final String requestUri = "/root-singleton";
        _testCounter(applicationHandler, requestUri, "RootSingletonResource", "1");
    }

    @Test
    public void testRequestScopeResource() throws InterruptedException, ExecutionException {
        ApplicationHandler applicationHandler = new ApplicationHandler(new ResourceConfig(ModelProcessorScopeTest.RootResource.class, ModelProcessorScopeTest.RootSingletonResource.class, ModelProcessorScopeTest.ModelProcessorFeature.class));
        final String requestUri = "/request-scope";
        _testCounter(applicationHandler, requestUri, "RequestScopeResource", "0");
    }
}

