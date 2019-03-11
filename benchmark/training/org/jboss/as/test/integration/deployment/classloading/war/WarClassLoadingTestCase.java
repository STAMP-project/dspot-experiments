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
package org.jboss.as.test.integration.deployment.classloading.war;


import java.net.URL;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class WarClassLoadingTestCase {
    @Test
    public void testWebInfLibAccessible() throws ClassNotFoundException {
        WarClassLoadingTestCase.loadClass("org.jboss.as.test.integration.deployment.classloading.war.WebInfLibClass");
    }

    /* Test case for AS7-5172. Ensure META-INF/* is accessible */
    @Test
    public void testMetaInfAccessible() throws ClassNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL manifestResource = cl.getResource("META-INF/example.txt");
        Assert.assertNotNull(manifestResource);
    }

    /* Test case for AS7-5172. Ensure that non-META-INF/* is not accessible */
    @Test
    public void testNonMetaInfNotAccessible() throws ClassNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL nonManifestResource = cl.getResource("example2.txt");
        Assert.assertNull(nonManifestResource);
    }
}

