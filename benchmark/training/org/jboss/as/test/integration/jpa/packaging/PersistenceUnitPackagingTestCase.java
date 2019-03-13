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
package org.jboss.as.test.integration.jpa.packaging;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that we are following the JPA 8.2.2 persistence unit scoping rules
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class PersistenceUnitPackagingTestCase {
    @ArquillianResource
    private static InitialContext iniCtx;

    /**
     * As override.jar has it's own PU with the same name as the ear level PU then the local PU should be used
     */
    @Test
    public void testLocalPuDefinitionOverridesEarLibPu() throws NamingException {
        OrganisationBean bean = ((OrganisationBean) (PersistenceUnitPackagingTestCase.iniCtx.lookup("java:app/override/OrganisationBean")));
        PersistenceUnitPackagingTestCase.validate(bean.getEntityManagerFactory(), Organisation.class, Employee.class);
        PersistenceUnitPackagingTestCase.validate(bean.getDefaultEntityManagerFactory(), Organisation.class, Employee.class);
    }

    /**
     * noOverride.jar should be able to resolve the ear level pu
     */
    @Test
    public void testUsingEarLibPuInSubdeployment() throws NamingException {
        EmployeeBean bean = ((EmployeeBean) (PersistenceUnitPackagingTestCase.iniCtx.lookup("java:app/noOverride/EmployeeBean")));
        PersistenceUnitPackagingTestCase.validate(bean.getEntityManagerFactory(), Employee.class, Organisation.class);
        PersistenceUnitPackagingTestCase.validate(bean.getDefaultEntityManagerFactory(), Employee.class, Organisation.class);
    }

    @Test
    public void testUserOfOveriddenSubDeploymentUsingExplicitPath() throws NamingException {
        LibPersistenceUnitBean bean = ((LibPersistenceUnitBean) (PersistenceUnitPackagingTestCase.iniCtx.lookup("java:app/override/LibPersistenceUnitBean")));
        PersistenceUnitPackagingTestCase.validate(bean.getEntityManagerFactory(), Employee.class, Organisation.class);
    }
}

