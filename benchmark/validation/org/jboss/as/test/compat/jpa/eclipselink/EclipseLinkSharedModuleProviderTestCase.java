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
package org.jboss.as.test.compat.jpa.eclipselink;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Scott Marlow
 */
@RunWith(Arquillian.class)
public class EclipseLinkSharedModuleProviderTestCase {
    private static final String ARCHIVE_NAME = "toplink_module_test";

    private static final String persistence_xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + ((((((((((("<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\" version=\"1.0\">" + "  <persistence-unit name=\"hibernate3_pc\">") + "<provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>") + "    <description>TopLink Persistence Unit.") + "    </description>") + "  <jta-data-source>java:jboss/datasources/ExampleDS</jta-data-source>") + "  <properties>") + "  <property name=\"jboss.as.jpa.providerModule\" value=\"org.eclipse.persistence:test\"/>") + "  <property name=\"eclipselink.ddl-generation\" value=\"drop-and-create-tables\"/>") + "  </properties>") + "  </persistence-unit>") + "</persistence>");

    @ArquillianResource
    private static InitialContext iniCtx;

    @Test
    public void testSimpleCreateAndLoadEntities() throws Exception {
        Assume.assumeTrue(((System.getSecurityManager()) == null));// ignore test if System.getSecurityManager() returns non-null

        SFSB1 sfsb1 = EclipseLinkSharedModuleProviderTestCase.lookup("SFSB1", SFSB1.class);
        sfsb1.createEmployee("Kelly Smith", "Watford, England", 10);
        sfsb1.createEmployee("Alex Scott", "London, England", 20);
        sfsb1.getEmployeeNoTX(10);
        sfsb1.getEmployeeNoTX(20);
    }
}

