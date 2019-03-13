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
package org.glassfish.jersey.tests.e2e.server.validation;


import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import org.glassfish.jersey.server.validation.ValidationConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michal Gajdos
 */
public class CustomConfigValidationTest extends JerseyTest {
    @javax.ws.rs.Path("customconfigvalidation/{path: .*}")
    public static class CustomConfigResource {
        @POST
        @Consumes("application/xml")
        @Produces("application/xml")
        @NotNull
        @Valid
        public CustomBean post(@PathParam("path")
        final String path, final CustomBean beanParameter, @Size(min = 5)
        @HeaderParam("myHeader")
        final String header) {
            if ("".equals(path)) {
                beanParameter.setPath(null);
                beanParameter.setValidate(false);
            } else {
                beanParameter.setPath(path);
                beanParameter.setValidate(true);
            }
            return beanParameter;
        }
    }

    @Test
    public void testPositive() throws Exception {
        final Response response = target("customconfigvalidation").path("ok").request().header("myHeader", "12345").post(javax.ws.rs.client.Entity.entity(new CustomBean(), MediaType.APPLICATION_XML_TYPE));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("ok", response.readEntity(CustomBean.class).getPath());
    }

    @Test
    public void testParameterNameWithInterpolator() throws Exception {
        final Response response = target("customconfigvalidation").path("ok").request().header("myHeader", "1234").post(javax.ws.rs.client.Entity.entity(new CustomBean(), MediaType.APPLICATION_XML_TYPE));
        Assert.assertEquals(400, response.getStatus());
        final String message = response.readEntity(String.class);
        Assert.assertFalse(message.contains("arg2"));
        Assert.assertTrue(message.contains("header"));
        Assert.assertFalse(message.contains("size must be between"));
        Assert.assertTrue(message.contains("message"));
    }

    @Test
    public void testTraversableResolver() throws Exception {
        final Response response = target("customconfigvalidation/").request().header("myHeader", "12345").post(javax.ws.rs.client.Entity.entity(new CustomBean(), MediaType.APPLICATION_XML_TYPE));
        Assert.assertEquals(200, response.getStatus());
        // return value passed validation because of "corrupted" traversableresolver
        Assert.assertEquals(null, response.readEntity(CustomBean.class).getPath());
    }

    public static class ValidationConfigurationContextResolver implements ContextResolver<ValidationConfig> {
        private final ValidationConfig config;

        public ValidationConfigurationContextResolver() {
            config = new ValidationConfig();
            // ConstraintValidatorFactory is set by default.
            config.messageInterpolator(new CustomConfigValidationTest.CustomMessageInterpolator());
            config.parameterNameProvider(new CustomConfigValidationTest.CustomParameterNameProvider());
            config.traversableResolver(new CustomConfigValidationTest.CustomTraversableResolver());
        }

        @Override
        public ValidationConfig getContext(final Class<?> type) {
            return ValidationConfig.class.isAssignableFrom(type) ? config : null;
        }
    }

    private static class CustomMessageInterpolator implements MessageInterpolator {
        @Override
        public String interpolate(final String messageTemplate, final Context context) {
            return "message";
        }

        @Override
        public String interpolate(final String messageTemplate, final Context context, final Locale locale) {
            return "localized message";
        }
    }

    private static class CustomParameterNameProvider implements ParameterNameProvider {
        private final ParameterNameProvider nameProvider;

        public CustomParameterNameProvider() {
            nameProvider = Validation.byDefaultProvider().configure().getDefaultParameterNameProvider();
        }

        @Override
        public List<String> getParameterNames(final Constructor<?> constructor) {
            return nameProvider.getParameterNames(constructor);
        }

        @Override
        public List<String> getParameterNames(final Method method) {
            try {
                final Method post = CustomConfigValidationTest.CustomConfigResource.class.getMethod("post", String.class, CustomBean.class, String.class);
                if (method.equals(post)) {
                    return Arrays.asList("path", "beanParameter", "header");
                }
            } catch (final NoSuchMethodException e) {
                // Do nothing.
            }
            return nameProvider.getParameterNames(method);
        }
    }

    private static class CustomTraversableResolver implements TraversableResolver {
        @Override
        public boolean isReachable(final Object traversableObject, final Path.Node traversableProperty, final Class<?> rootBeanType, final Path pathToTraversableObject, final ElementType elementType) {
            return false;
        }

        @Override
        public boolean isCascadable(final Object traversableObject, final Path.Node traversableProperty, final Class<?> rootBeanType, final Path pathToTraversableObject, final ElementType elementType) {
            return false;
        }
    }
}

