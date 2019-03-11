/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.compat.jpa.hibernate;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Verify that application deployments can include their own copy of Hibernate jars
 */
@RunWith(Arquillian.class)
public class HibernateJarsInDeploymentTestCase {
    private static final String ARCHIVE_NAME = "HibernateJarsInDeploymentTestCase";

    @ArquillianResource
    private InitialContext iniCtx;

    @Test
    public void verifyPackagedHibernateJarsUsed() throws Exception {
        SFSB1 sfsb1 = lookup("SFSB1", SFSB1.class);
        Assert.assertNotNull(sfsb1.getEmImpl());
        // verify that the Hibernate ORM classloader == ear classloader
        // fail if the static Hibernate module classloader is used instead of Hibernate jars in application
        Assert.assertEquals(LibClass.class.getClassLoader(), sfsb1.getEmImpl().getClass().getClassLoader());
    }
}

