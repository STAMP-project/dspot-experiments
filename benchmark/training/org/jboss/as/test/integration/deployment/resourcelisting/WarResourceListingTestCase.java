/**
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.as.test.integration.deployment.resourcelisting;


import java.net.URL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.modules.ModuleClassLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class WarResourceListingTestCase {
    private static final Logger log = Logger.getLogger(WarResourceListingTestCase.class);

    private static final String jarLibName = "innerJarLibrary.jar";

    @Test
    public void testRecursiveResourceRetrieval() {
        WarResourceListingTestCase.log.trace("Test recursive listing of resources");
        doTestResourceRetrieval(true, "/");
    }

    @Test
    public void testNonRecursiveResourceRetrieval() {
        WarResourceListingTestCase.log.trace("Test nonrecursive listing of resources");
        doTestResourceRetrieval(false, "/");
    }

    @Test
    public void testRecursiveResourceRetrievalForSpecifiedRootDir() {
        WarResourceListingTestCase.log.trace("Test recursive listing of resources in specific directory");
        doTestResourceRetrieval(true, "/WEB-INF");
    }

    @Test
    public void testNonRecursiveResourceRetrievalForSpecifiedRootDir() {
        WarResourceListingTestCase.log.trace("Test recursive listing of resources in specific directory");
        doTestResourceRetrieval(false, "/WEB-INF");
    }

    @Test
    public void testDirectResourceRetrieval() {
        WarResourceListingTestCase.log.trace("Test accessing resources using getResource method");
        ModuleClassLoader classLoader = ((ModuleClassLoader) (getClass().getClassLoader()));
        // checking that resource under META-INF is accessible
        URL manifestResource = classLoader.getResource("META-INF/example.txt");
        Assert.assertNotNull("Resource in META-INF should be accessible", manifestResource);
        // checking that resource under META-INF is accessible
        URL nestedManifestResource = classLoader.getResource("META-INF/properties/nested.properties");
        Assert.assertNotNull("Nested resource should be also accessible", nestedManifestResource);
        // checking that resource which is not under META-INF is not accessible
        URL nonManifestResource = classLoader.getResource("example2.txt");
        Assert.assertNull("Resource in the root of WAR shouldn't be accessible", nonManifestResource);
    }
}

