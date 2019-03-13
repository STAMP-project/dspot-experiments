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
package org.glassfish.jersey.tests.e2e.client;


import MediaType.TEXT_PLAIN;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


/**
 * Test if JAX-RS injection points work in client side providers.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public class InjectedClientBodyWorker extends JerseyTest {
    // media types are used to determine what kind of injection should be tested
    static final String ProviderType = "test/providers";

    static final String ConfigurationTYPE = "test/configuration";

    public static class MyContext {}

    @Provider
    public static class MyContextResolver implements ContextResolver<InjectedClientBodyWorker.MyContext> {
        @Override
        public InjectedClientBodyWorker.MyContext getContext(Class<?> type) {
            return null;
        }
    }

    @Provider
    @Produces(InjectedClientBodyWorker.ProviderType)
    public static class ProvidersInjectedWriter implements MessageBodyWriter<String> {
        @Context
        Providers providers;

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (String.class);
        }

        @Override
        public long getSize(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            // make sure no exception occurs here
            providers.getExceptionMapper(Exception.class);
            final ContextResolver<InjectedClientBodyWorker.MyContext> contextResolver = providers.getContextResolver(InjectedClientBodyWorker.MyContext.class, MediaType.valueOf(InjectedClientBodyWorker.ProviderType));
            entityStream.write(String.format("%s", contextResolver).getBytes());
        }
    }

    @Provider
    @Consumes(InjectedClientBodyWorker.ProviderType)
    public static class ProvidersInjectedReader implements MessageBodyReader<String> {
        @Context
        Providers providers;

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (String.class);
        }

        @Override
        public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            // make sure no exception occurs here
            providers.getExceptionMapper(Exception.class);
            final ContextResolver<InjectedClientBodyWorker.MyContext> contextResolver = providers.getContextResolver(InjectedClientBodyWorker.MyContext.class, MediaType.valueOf(InjectedClientBodyWorker.ProviderType));
            return String.format("%s", contextResolver);
        }
    }

    @Provider
    @Produces(InjectedClientBodyWorker.ConfigurationTYPE)
    public static class ConfigurationInjectedWriter implements MessageBodyWriter<String> {
        @Context
        Configuration configuration;

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (String.class);
        }

        @Override
        public long getSize(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            final boolean ctxResolverRegistered = configuration.isRegistered(InjectedClientBodyWorker.MyContextResolver.class);
            entityStream.write(String.format("%b", ctxResolverRegistered).getBytes());
        }
    }

    @Provider
    @Consumes(InjectedClientBodyWorker.ConfigurationTYPE)
    public static class ConfigurationInjectedReader implements MessageBodyReader<String> {
        @Context
        Configuration configuration;

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (String.class);
        }

        @Override
        public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            final boolean ctxResolverRegistered = configuration.isRegistered(InjectedClientBodyWorker.MyContextResolver.class);
            return String.format("%b", ctxResolverRegistered);
        }
    }

    @Path("echo")
    public static class EchoResource {
        @POST
        @Consumes({ InjectedClientBodyWorker.ProviderType, InjectedClientBodyWorker.ConfigurationTYPE, MediaType.TEXT_PLAIN })
        @Produces({ InjectedClientBodyWorker.ProviderType, InjectedClientBodyWorker.ConfigurationTYPE, MediaType.TEXT_PLAIN })
        public String post(String p) {
            return p;
        }
    }

    @Test
    public void testProvidersInReader() throws Exception {
        _testProviders(InjectedClientBodyWorker.ProviderType, TEXT_PLAIN);
    }

    @Test
    public void testProvidersInWriter() throws Exception {
        _testProviders(TEXT_PLAIN, InjectedClientBodyWorker.ProviderType);
    }

    @Test
    public void testConfigurationInReader() throws Exception {
        testConfiguration(InjectedClientBodyWorker.ConfigurationTYPE, TEXT_PLAIN);
    }

    @Test
    public void testConfigurationInWriter() throws Exception {
        testConfiguration(TEXT_PLAIN, InjectedClientBodyWorker.ConfigurationTYPE);
    }
}

