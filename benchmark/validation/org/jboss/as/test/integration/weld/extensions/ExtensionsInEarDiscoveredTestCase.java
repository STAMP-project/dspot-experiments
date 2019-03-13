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
package org.jboss.as.test.integration.weld.extensions;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * AS7-623
 *
 * Make sure extensions in WEB-INF/lib of a war in an ear are discovered.
 *
 * A jar with a portable extension that adds MyBean as a bean is added to a war that is deployed as an ear
 *
 * Normally MyBean would not be a bean as it is not in a jar with a beans.xml
 *
 * The test checks that it is possible to inject MyBean
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class ExtensionsInEarDiscoveredTestCase {
    @Test
    public void testExtensionIsLoaded() throws NamingException {
        SomeInterface bean = ((SomeInterface) (new InitialContext().lookup("java:global/testExtensions/testWar/WarSLSB")));
        bean.testInjectionWorked();
    }
}

