/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.rar;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.rar.ejb.NoOpEJB;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that a .rar deployed within a .ear doesn't run into deployment problems
 *
 * @author Jaikiran Pai
 * @see https://issues.jboss.org/browse/AS7-2111
 */
@RunWith(Arquillian.class)
public class RarDeploymentTestCase {
    /**
     * Test the deployment succeeded.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDeployment() throws Exception {
        final NoOpEJB noOpEJB = InitialContext.doLookup(("java:app/ejb/" + (NoOpEJB.class.getSimpleName())));
        noOpEJB.doNothing();
    }
}

