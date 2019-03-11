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
package org.jboss.as.test.integration.deployment.classloading.ear;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the ear-subdeployments-isolated option in jboss-deployment-structure.xml
 *
 * By default ejb-jar's should not be visible to each other, with this option enabled they are.
 */
@RunWith(Arquillian.class)
public class EarJbossStructureExtendedVisibilityTestCase {
    @Test(expected = ClassNotFoundException.class)
    public void testWarModuleStillNotAccessible() throws ClassNotFoundException {
        EarJbossStructureExtendedVisibilityTestCase.loadClass("org.jboss.as.test.integration.deployment.classloading.ear.TestAA", getClass().getClassLoader());
    }

    @Test
    public void testOtherEjbJarAcessible() throws ClassNotFoundException {
        EarJbossStructureExtendedVisibilityTestCase.loadClass("org.jboss.as.test.integration.deployment.classloading.ear.MyEjb2", getClass().getClassLoader());
    }
}

