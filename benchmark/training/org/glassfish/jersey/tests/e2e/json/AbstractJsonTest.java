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
package org.glassfish.jersey.tests.e2e.json;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.ws.rs.client.Entity;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.glassfish.jersey.jettison.JettisonConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Common functionality for JSON tests that are using multiple JSON providers (e.g. MOXy, Jackson, Jettison).
 *
 * @author Michal Gajdos
 */
public abstract class AbstractJsonTest extends JerseyTest {
    private static final String PKG_NAME = "org/glassfish/jersey/tests/e2e/json/entity/";

    private static final Logger LOGGER = Logger.getLogger(AbstractJsonTest.class.getName());

    /**
     * Helper class representing configuration for one test case.
     */
    protected static final class JsonTestSetup {
        private final JsonTestProvider jsonProvider;

        private final Class<?>[] testClasses;

        protected JsonTestSetup(final Class<?> testClass, final JsonTestProvider jsonProvider) {
            this(new Class<?>[]{ testClass }, jsonProvider);
        }

        protected JsonTestSetup(final Class<?>[] testClasses, final JsonTestProvider jsonProvider) {
            this.testClasses = testClasses;
            this.jsonProvider = jsonProvider;
        }

        public JsonTestProvider getJsonProvider() {
            return jsonProvider;
        }

        public Set<Object> getProviders() {
            return jsonProvider.getProviders();
        }

        public Class<?> getEntityClass() {
            return testClasses[0];
        }

        public Class<?>[] getTestClasses() {
            return testClasses;
        }

        public Object getTestEntity() throws Exception {
            return getEntityClass().getDeclaredMethod("createTestInstance").invoke(null);
        }
    }

    @Provider
    private static final class JAXBContextResolver implements ContextResolver<JAXBContext> {
        private final JAXBContext context;

        private final Set<Class<?>> types;

        public JAXBContextResolver(final JettisonConfig jsonConfiguration, final Class<?>[] classes, final boolean forMoxyProvider) throws Exception {
            this.types = new HashSet<>(Arrays.asList(classes));
            if (jsonConfiguration != null) {
                this.context = new org.glassfish.jersey.jettison.JettisonJaxbContext(jsonConfiguration, classes);
            } else {
                this.context = (forMoxyProvider) ? JAXBContextFactory.createContext(classes, new HashMap()) : JAXBContext.newInstance(classes);
            }
        }

        @Override
        public JAXBContext getContext(final Class<?> objectType) {
            return types.contains(objectType) ? context : null;
        }
    }

    private final AbstractJsonTest.JsonTestSetup jsonTestSetup;

    protected AbstractJsonTest(final AbstractJsonTest.JsonTestSetup jsonTestSetup) throws Exception {
        super(AbstractJsonTest.configureJaxrsApplication(jsonTestSetup));
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        this.jsonTestSetup = jsonTestSetup;
    }

    @Test
    public void test() throws Exception {
        final Object entity = getJsonTestSetup().getTestEntity();
        final Object receivedEntity = target().path(getProviderPathPart()).path(getEntityPathPart()).request("application/json; charset=UTF-8").post(Entity.entity(entity, "application/json; charset=UTF-8"), getJsonTestSetup().getEntityClass());
        // Print out configuration for this test case as there is no way to rename generated JUnit tests at the moment.
        // TODO remove once JUnit supports parameterized tests with custom names
        // TODO (see http://stackoverflow.com/questions/650894/change-test-name-of-parameterized-tests
        // TODO or https://github.com/KentBeck/junit/pull/393)
        Assert.assertEquals(String.format("%s - %s: Received JSON entity content does not match expected JSON entity content.", getJsonTestSetup().getJsonProvider().getClass().getSimpleName(), getJsonTestSetup().getEntityClass().getSimpleName()), entity, receivedEntity);
    }
}

