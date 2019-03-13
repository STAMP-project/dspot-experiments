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
package org.jboss.as.test.integration.ejb.packaging.war.namingcontext;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that EJB's packaged in a war use the correct naming context
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class WarEjbNamingContextTestCase {
    private static final String ARCHIVE_NAME = "WarEjbNamingContextTestCase";

    @Test
    public void testCorrectNamingContextUsedForEjbInWar() throws Exception {
        EjbInterface ejb = ((EjbInterface) (new InitialContext().lookup("java:app/war1/War1Ejb")));
        Assert.assertNotNull(ejb.lookupUserTransaction());
        try {
            ejb.lookupOtherUserTransaction();
            Assert.fail();
        } catch (NamingException expected) {
        }
        ejb = ((EjbInterface) (new InitialContext().lookup("java:app/war2/War2Ejb")));
        Assert.assertNotNull(ejb.lookupUserTransaction());
        try {
            ejb.lookupOtherUserTransaction();
            Assert.fail();
        } catch (NamingException expected) {
        }
    }
}

