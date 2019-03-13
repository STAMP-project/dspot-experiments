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
package org.jboss.as.test.integration.ejb.localview;


import java.io.Serializable;
import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that local views of SLSF's are handled properly, as per EE 3.1 4.9.7
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class EjbLocalViewTestCase {
    @Test
    public void testImplicitNoInterface() throws NamingException {
        ensureExists(ImplicitNoInterfaceBean.class, ImplicitNoInterfaceBean.class, true);
        ensureDoesNotExist(ImplicitNoInterfaceBean.class, Serializable.class);
    }

    @Test
    public void testSingleImplicitInterface() throws NamingException {
        ensureExists(SimpleImplicitLocalInterfaceBean.class, ImplicitLocalInterface.class, true);
        ensureDoesNotExist(SimpleImplicitLocalInterfaceBean.class, Serializable.class);
    }

    @Test
    public void testSingleImplicitInterfaceWithSerializable() throws NamingException {
        ensureExists(ImplicitLocalInterfaceBean.class, ImplicitLocalInterface.class, true);
    }

    @Test
    public void testSingleLocalDeclaredOnBean() throws NamingException {
        ensureExists(SingleLocalDeclaredOnBean.class, OtherInterface.class, true);
        ensureDoesNotExist(SingleLocalDeclaredOnBean.class, LocalInterface.class);
    }

    @Test
    public void testSingleLocalDeclaredOnInterface() throws NamingException {
        ensureExists(SingleLocalDeclaredOnInterface.class, LocalInterface.class, true);
        ensureDoesNotExist(SingleLocalDeclaredOnInterface.class, NotViewInterface.class);
        ensureDoesNotExist(SingleLocalDeclaredOnInterface.class, Serializable.class);
    }

    @Test
    public void testTwoLocalsDeclaredOnBean() throws NamingException {
        ensureExists(TwoLocalsDeclaredOnBean.class, OtherInterface.class, false);
        ensureExists(TwoLocalsDeclaredOnBean.class, OtherInterface.class, false);
        ensureDoesNotExist(TwoLocalsDeclaredOnBean.class);
    }

    @Test
    public void testTwoLocalsDeclaredOnInterface() throws NamingException {
        ensureExists(TwoLocalsDeclaredOnInterface.class, LocalInterface.class, false);
        ensureExists(TwoLocalsDeclaredOnInterface.class, OtherLocalInterface.class, false);
        ensureDoesNotExist(TwoLocalsDeclaredOnInterface.class);
        ensureDoesNotExist(TwoLocalsDeclaredOnInterface.class, NotViewInterface.class);
        ensureDoesNotExist(TwoLocalsDeclaredOnInterface.class, Serializable.class);
    }
}

