/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.ejb2.reference.global;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Migration test from EJB Testsuite (reference21_30) to AS7 [JIRA JBQA-5483].
 * Test for EJB3.0/EJB2.1 references
 *
 * @author William DeCoste, Ondrej Chaloupka
 */
@RunWith(Arquillian.class)
public class GlobalReferenceTestCase {
    private static final String EJB2 = "global-reference-ejb2";

    private static final String EJB3 = "global-reference-ejb3";

    @Test
    public void testSession21() throws Exception {
        Session21Home home = this.getHome(Session21Home.class, "Session21");
        Session21 session = home.create();
        String access = session.access();
        Assert.assertEquals("Session21", access);
        access = session.globalAccess30();
        Assert.assertEquals("Session30", access);
    }

    @Test
    public void testSession30() throws Exception {
        Session30RemoteBusiness session = ((Session30RemoteBusiness) (getInitialContext().lookup(((("ejb:/" + (GlobalReferenceTestCase.EJB3)) + "/GlobalSession30!") + (Session30RemoteBusiness.class.getName())))));
        String access = session.access();
        Assert.assertEquals("Session30", access);
        access = session.globalAccess21();
        Assert.assertEquals("Session21", access);
    }
}

