/**
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors as indicated
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
package org.jboss.as.test.integration.ee.injection.resource.persistenceunitref;


import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class PersistenceUnitRefTestCase {
    private static final String ARCHIVE_NAME = "persistence-unit-ref";

    private static final String persistence_xml = ((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + (((((("<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\" version=\"1.0\">" + "  <persistence-unit name=\"mypc\">") + "    <description>Persistence Unit.") + "    </description>") + "  <jta-data-source>java:jboss/datasources/ExampleDS</jta-data-source>") + "  <exclude-unlisted-classes>true</exclude-unlisted-classes>") + "  <class>")) + (PuMyEntity.class.getName())) + "</class>") + "<properties> <property name=\"hibernate.hbm2ddl.auto\" value=\"create-drop\"/></properties>") + "  </persistence-unit>") + "  <persistence-unit name=\"otherpc\">") + "    <description>Persistence Unit.") + "    </description>") + "  <jta-data-source>java:jboss/datasources/ExampleDS</jta-data-source>") + "  <exclude-unlisted-classes>true</exclude-unlisted-classes>") + "  <class>") + (PuOtherEntity.class.getName())) + "</class>") + "<properties> <property name=\"hibernate.hbm2ddl.auto\" value=\"create-drop\"/></properties>") + "  </persistence-unit>") + "</persistence>";

    @Test
    public void testCorrectPersistenceUnitInjectedFromAnnotation() throws NamingException {
        PuManagedBean bean = getManagedBean();
        bean.getMypu().getMetamodel().entity(PuMyEntity.class);
    }

    @Test
    public void testCorrectPersistenceUnitInjectedFromAnnotation2() throws NamingException {
        try {
            PuManagedBean bean = getManagedBean();
            bean.getMypu().getMetamodel().entity(PuOtherEntity.class);
        } catch (IllegalArgumentException e) {
            // all is fine!
            return;
        }
        Assert.fail("IllegalArgumentException should occur but didn't!");
    }

    @Test
    public void testCorrectPersistenceUnitInjectedFromPersistenceUnitRef() throws NamingException {
        try {
            PuManagedBean bean = getManagedBean();
            bean.getOtherpc().getMetamodel().entity(PuMyEntity.class);
        } catch (IllegalArgumentException e) {
            // all is fine!
            return;
        }
        Assert.fail("IllegalArgumentException should occur but didn't!");
    }

    @Test
    public void testCorrectPersistenceUnitInjectedFromPersistenceUnitRef2() throws NamingException {
        PuManagedBean bean = getManagedBean();
        bean.getOtherpc().getMetamodel().entity(PuOtherEntity.class);
    }

    @Test
    public void testCorrectPersistenceUnitInjectedFromRefInjectionTarget() throws NamingException {
        PuManagedBean bean = getManagedBean();
        bean.getMypu2().getMetamodel().entity(PuMyEntity.class);
    }

    @Test
    public void testCorrectPersistenceUnitInjectedFromRefInjectionTarget2() throws NamingException {
        try {
            PuManagedBean bean = getManagedBean();
            bean.getMypu2().getMetamodel().entity(PuOtherEntity.class);
        } catch (IllegalArgumentException e) {
            // all is fine!
            return;
        }
        Assert.fail("IllegalArgumentException should occur but didn't!");
    }
}

