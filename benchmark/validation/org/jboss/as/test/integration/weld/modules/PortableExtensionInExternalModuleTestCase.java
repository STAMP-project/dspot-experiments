/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.weld.modules;


import javax.ejb.EJB;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.module.util.TestModule;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the deployment of simple enterprise application depending on some external WFLY module. The external WFLY module
 * defines beans and contains an extension (application scoped by default) which can be injected into
 * <ul>
 * <li>module itself</li>
 * <li>utility library in application</li>
 * <li>EJB sub-deployment in application</li>
 * <li>WAR sub-deployment in application</li>
 * </ul>
 *
 * @see WFLY-1746
 * @author Petr Andreev
 */
@RunWith(Arquillian.class)
public class PortableExtensionInExternalModuleTestCase {
    private static final Logger log = Logger.getLogger(PortableExtensionInExternalModuleTestCase.class.getName());

    private static final String MANIFEST = "MANIFEST.MF";

    private static final String MODULE_NAME = "portable-extension";

    private static TestModule testModule;

    @Inject
    private PortableExtension extension;

    /**
     * The CDI-style EJB injection into the the test-case does not work!
     */
    @EJB(mappedName = "java:global/test/ejb-subdeployment/PortableExtensionSubdeploymentLookup")
    private PortableExtensionLookup ejbInjectionTarget;

    @Test
    public void testInWarSubdeployment() {
        Assert.assertNotNull(extension);
        BeanManager beanManager = extension.getBeanManager();
        Assert.assertNotNull(beanManager);
    }
}

