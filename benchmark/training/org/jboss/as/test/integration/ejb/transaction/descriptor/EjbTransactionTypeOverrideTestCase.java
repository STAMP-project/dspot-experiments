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
package org.jboss.as.test.integration.ejb.transaction.descriptor;


import Status.STATUS_ACTIVE;
import Status.STATUS_NO_TRANSACTION;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for override transaction type for method through ejb-jar.xml.
 * Bugzilla 1180556 https://bugzilla.redhat.com/show_bug.cgi?id=1180556
 *
 * @author Hynek Svabek
 */
@RunWith(Arquillian.class)
public class EjbTransactionTypeOverrideTestCase {
    @ArquillianResource
    private InitialContext initialContext;

    @Test
    public void testOverrideSupportToRequiredThroughtEjbJarXml() throws NamingException, NotSupportedException, SystemException {
        final TransactionLocal bean = ((TransactionLocal) (initialContext.lookup(((("java:module/" + (DescriptorBean.class.getSimpleName())) + "!") + (TransactionLocal.class.getName())))));
        Assert.assertEquals(STATUS_NO_TRANSACTION, bean.transactionStatus());
        Assert.assertEquals(STATUS_ACTIVE, bean.transactionStatus2());
    }
}

