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
package org.jboss.as.test.integration.ejb.mapbased;


import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for EJBCLIENT-34: properties-based JNDI InitialContext for EJB clients.
 *
 * @author Jan Martiska / jmartisk@redhat.com
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MapBasedInitialContextEjbClientTestCase {
    private static final String ARCHIVE_NAME = "map-based-client-1";

    /**
     * Tests that invocations on a scoped EJB client context use the correct receiver(s)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testScopedEJBClientContexts() throws Exception {
        InitialContext ctx = new InitialContext(getEjbClientProperties(System.getProperty("node0", "127.0.0.1"), 8080));
        try {
            String lookupName = (((("ejb:/" + (MapBasedInitialContextEjbClientTestCase.ARCHIVE_NAME)) + "/") + (StatelessBean.class.getSimpleName())) + "!") + (StatelessIface.class.getCanonicalName());
            StatelessIface beanStateless = ((StatelessIface) (ctx.lookup(lookupName)));
            Assert.assertEquals("Unexpected EJB client context used for invoking stateless bean", CustomCallbackHandler.USER_NAME, beanStateless.getCallerPrincipalName());
            lookupName = ((((("ejb:/" + (MapBasedInitialContextEjbClientTestCase.ARCHIVE_NAME)) + "/") + (StatefulBean.class.getSimpleName())) + "!") + (StatefulIface.class.getCanonicalName())) + "?stateful";
            StatefulIface beanStateful = ((StatefulIface) (ctx.lookup(lookupName)));
            Assert.assertEquals("Unexpected EJB client context used for invoking stateful bean", CustomCallbackHandler.USER_NAME, beanStateful.getCallerPrincipalName());
            ctx.close();
        } finally {
            ctx.close();
        }
    }
}

