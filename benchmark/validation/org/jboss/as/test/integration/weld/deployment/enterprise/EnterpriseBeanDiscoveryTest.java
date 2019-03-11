/**
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.weld.deployment.enterprise;


import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class EnterpriseBeanDiscoveryTest {
    private static final String ALPHA_JAR = "alpha.jar";

    private static final String BRAVO_JAR = "bravo.jar";

    private static final String CHARLIE_JAR = "charlie.jar";

    private static final String DELTA_JAR = "delta.jar";

    private static final String ECHO_JAR = "echo.jar";

    private static final String FOXTROT_JAR = "foxtrot.jar";

    private static final String LEGACY_JAR = "legacy.jar";

    @Inject
    VerifyingExtension extension;

    @Inject
    private BeanManager manager;

    @Test
    public void testExplicitBeanArchiveModeAll() {
        assertDiscoveredAndAvailable(AlphaLocal.class, Alpha.class);
    }

    @Test
    public void testExplicitBeanArchiveEmptyDescriptor() {
        assertDiscoveredAndAvailable(BravoLocal.class, Bravo.class);
    }

    @Test
    public void testExplicitBeanArchiveLegacyDescriptor() {
        assertDiscoveredAndAvailable(CharlieLocal.class, Charlie.class);
    }

    @Test
    public void testImplicitBeanArchiveNoDescriptor() {
        assertDiscoveredAndAvailable(DeltaLocal.class, Delta.class);
    }

    @Test
    public void testImplicitBeanArchiveModeAnnotated() {
        assertDiscoveredAndAvailable(EchoLocal.class, Echo.class);
    }

    @Test
    public void testNoBeanArchiveModeNone() {
        assertNotDiscoveredAndNotAvailable(FoxtrotLocal.class, Foxtrot.class);
    }

    @Test
    public void testNotBeanArchiveExtension() {
        assertNotDiscoveredAndNotAvailable(LegacyBean.class, LegacyBean.class);
    }
}

