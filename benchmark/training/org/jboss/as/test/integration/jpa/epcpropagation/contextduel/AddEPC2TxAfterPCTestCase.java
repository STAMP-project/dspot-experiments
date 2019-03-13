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
package org.jboss.as.test.integration.jpa.epcpropagation.contextduel;


import javax.ejb.EJBException;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test JPA 2.0 section 7.6.3.1
 * "
 * If the component is a stateful session bean to which an extended persistence context has been
 * bound and there is a different persistence context bound to the JTA transaction,
 * an EJBException is thrown by the container.
 * "
 *
 * @author Scott Marlow
 */
@RunWith(Arquillian.class)
public class AddEPC2TxAfterPCTestCase {
    private static final String ARCHIVE_NAME = "jpa_AddEPC2TxAfterPCTestCase";

    @ArquillianResource
    private InitialContext iniCtx;

    @Test
    public void testShouldGetEjbExceptionBecauseEPCIsAddedToTxAfterPc() throws Exception {
        BMTEPCStatefulBean stateful = lookup("BMTEPCStatefulBean", BMTEPCStatefulBean.class);
        try {
            stateful.shouldThrowError();
        } catch (EJBException expected) {
            // success
        }
    }

    @Test
    public void testShouldNotGetError() throws Exception {
        BMTEPCStatefulBean stateful = lookup("BMTEPCStatefulBean", BMTEPCStatefulBean.class);
        stateful.shouldNotThrowError();
    }
}

