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


import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class EarResourceListingTestCase {
    private static final Logger log = Logger.getLogger(EarResourceListingTestCase.class);

    private static final String INNER_WAR_ARCHIVE_NAME = "innerWarDeployment.war";

    private static final String INNER_JAR_ARCHIVE_NAME = "innerJarLibrary.jar";

    @Test
    public void testRecursiveEARResourceRetrieval() {
        EarResourceListingTestCase.log.trace("Test non-recursive listing of resources in EAR deployment");
        doTestEARResourceRetrieval(true, "/");
    }

    @Test
    public void testNonRecursiveEARResourceRetrieval() {
        EarResourceListingTestCase.log.trace("Test recursive listing of resources in EAR deployment");
        doTestEARResourceRetrieval(false, "/");
    }

    @Test
    public void testRecursiveEARResourceRetrievalForSpecifiedRootDir() {
        EarResourceListingTestCase.log.trace("Test recursive listing of resources in EAR deployment for root dir /META-INF");
        doTestEARResourceRetrieval(true, "/META-INF");
    }

    @Test
    public void testNonRecursiveEARResourceRetrievalForSpecifiedRootDir() {
        EarResourceListingTestCase.log.trace("Test non-recursive listing of resources in EAR deployment for root dir /META-INF");
        doTestEARResourceRetrieval(false, "/META-INF");
    }
}

