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
package org.glassfish.jersey.server.internal.routing;


import MediaType.TEXT_PLAIN_TYPE;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.junit.Assert;
import org.junit.Test;

import static org.glassfish.jersey.server.RequestContextBuilder.from;


/**
 * {@link ExtendedUriInfo} unit tests - testing e.g. getting matched resources, mapped throwable, etc.
 *
 * @author Michal Gajdos
 * @author Miroslav Fuksa
 */
public class ExtendedUriInfoTest {
    @Path("root")
    public static class SimpleResource {
        @Inject
        private ExtendedUriInfo extendedUriInfo;

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String get() {
            Assert.assertEquals("root", extendedUriInfo.getMatchedModelResource().getPath());
            Assert.assertEquals(TEXT_PLAIN_TYPE, extendedUriInfo.getMatchedResourceMethod().getProducedTypes().get(0));
            Assert.assertEquals("/root", extendedUriInfo.getMatchedRuntimeResources().get(0).getRegex());
            Assert.assertEquals(1, extendedUriInfo.getMatchedRuntimeResources().size());
            return "root";
        }

        @Path("child")
        @GET
        public String child() {
            final Resource resource = extendedUriInfo.getMatchedModelResource();
            Assert.assertEquals("child", resource.getPath());
            final List<RuntimeResource> runtimeResources = extendedUriInfo.getMatchedRuntimeResources();
            Assert.assertEquals("root", resource.getParent().getPath());
            Assert.assertEquals(2, runtimeResources.size());
            Assert.assertEquals("/child;/root", ExtendedUriInfoTest.convertToString(runtimeResources));
            Assert.assertEquals("/child", runtimeResources.get(0).getRegex());
            Assert.assertEquals("/root", runtimeResources.get(1).getRegex());
            return "child";
        }
    }

    @Test
    public void testResourceMethod() throws InterruptedException, ExecutionException {
        final ApplicationHandler applicationHandler = getApplication();
        final ContainerResponse response = applicationHandler.apply(from("/root", "GET").build()).get();
        Assert.assertEquals("root", response.getEntity());
    }

    @Test
    public void testChildResourceMethod() throws InterruptedException, ExecutionException {
        final ApplicationHandler applicationHandler = getApplication();
        final ContainerResponse response = applicationHandler.apply(from("/root/child", "GET").build()).get();
        Assert.assertEquals("child", response.getEntity());
    }

    @Path("locator-test")
    public static class ResourceWithLocator {
        @Path("/")
        public Class<ExtendedUriInfoTest.SubResourceLocator> getResourceLocator() {
            return ExtendedUriInfoTest.SubResourceLocator.class;
        }

        @Path("sublocator")
        public Class<ExtendedUriInfoTest.SubResourceLocator> getSubResourceLocator() {
            return ExtendedUriInfoTest.SubResourceLocator.class;
        }
    }

    public static class SubResourceLocator {
        @Inject
        private ExtendedUriInfo extendedUriInfo;

        @GET
        public String get() {
            Assert.assertFalse(((extendedUriInfo.getMatchedModelResource().getPath()) != null));
            final List<RuntimeResource> matchedRuntimeResources = extendedUriInfo.getMatchedRuntimeResources();
            return ExtendedUriInfoTest.convertToString(matchedRuntimeResources);
        }

        @GET
        @Path("subget")
        public String subGet() {
            final List<RuntimeResource> matchedRuntimeResources = extendedUriInfo.getMatchedRuntimeResources();
            Assert.assertEquals("/subget", matchedRuntimeResources.get(0).getRegex());
            Assert.assertEquals("subget", extendedUriInfo.getMatchedModelResource().getPath());
            return ExtendedUriInfoTest.convertToString(matchedRuntimeResources);
        }

        @Path("sub")
        public Class<ExtendedUriInfoTest.SubResourceLocator> getRecursive() {
            final List<RuntimeResource> matchedRuntimeResources = extendedUriInfo.getMatchedRuntimeResources();
            Assert.assertEquals("/sub", matchedRuntimeResources.get(0).getRegex());
            Assert.assertNull(extendedUriInfo.getMatchedModelResource());
            Assert.assertNull(extendedUriInfo.getMatchedResourceMethod());
            return ExtendedUriInfoTest.SubResourceLocator.class;
        }
    }

    @Test
    public void testLocator() throws InterruptedException, ExecutionException {
        final String requestUri = "/locator-test";
        final String expectedResourceList = "<no-path>;/locator\\-test";
        _testResourceList(requestUri, expectedResourceList);
    }

    @Test
    public void testLocatorChild() throws InterruptedException, ExecutionException {
        _testResourceList("/locator-test/subget", "/subget;<no-path>;/locator\\-test");
    }

    @Test
    public void testSubResourceLocator() throws InterruptedException, ExecutionException {
        _testResourceList("/locator-test/sublocator", "<no-path>;/sublocator;/locator\\-test");
    }

    @Test
    public void testSubResourceLocatorSubGet() throws InterruptedException, ExecutionException {
        _testResourceList("/locator-test/sublocator/subget", "/subget;<no-path>;/sublocator;/locator\\-test");
    }

    @Test
    public void testSubResourceLocatorRecursive() throws InterruptedException, ExecutionException {
        _testResourceList("/locator-test/sublocator/sub", "<no-path>;/sub;<no-path>;/sublocator;/locator\\-test");
    }

    @Test
    public void testSubResourceLocatorRecursive2() throws InterruptedException, ExecutionException {
        _testResourceList("/locator-test/sublocator/sub/sub/subget", "/subget;<no-path>;/sub;<no-path>;/sub;<no-path>;/sublocator;/locator\\-test");
    }
}

