/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.multinode.transaction.async;


import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * <p>
 * Testing transaction propagation to a remote server when bean method is annotated
 * as asynchronous.
 * <p>
 * Specification says at such case that no transaction context is provided
 * <p>
 * EJB 3.2 4.5.3 Transactions<br>
 * The client?s transaction context does not propagate with an asynchronous method invocation. From the
 * Bean Provider?s point of view, there is never a transaction context flowing in from the client. This
 * means, for example, that the semantics of the REQUIRED transaction attribute on an asynchronous
 * method are exactly the same as REQUIRES_NEW.
 *
 * @author Ondrej Chaloupka
 */
@RunWith(Arquillian.class)
public class TransactionPropagationTestCase {
    public static final String CLIENT_DEPLOYMENT = "client-txt-propag-async";

    public static final String SERVER_DEPLOYMENT = "server-txt-propag-async";

    @Test
    @OperateOnDeployment(TransactionPropagationTestCase.CLIENT_DEPLOYMENT)
    public void testRemoteInvocation() throws Exception {
        final ClientBean ejb = getClient();
        ejb.callToMandatory();
    }

    @Test
    @OperateOnDeployment(TransactionPropagationTestCase.CLIENT_DEPLOYMENT)
    public void testRemoteWithStatusAtRegistry() throws Exception {
        final ClientBean ejb = getClient();
        ejb.callToStatusByRegistry();
    }

    @Test
    @OperateOnDeployment(TransactionPropagationTestCase.CLIENT_DEPLOYMENT)
    public void testRemoteWithStatusAtTransactionManager() throws Exception {
        final ClientBean ejb = getClient();
        ejb.callToStatusByTransactionmanager();
    }
}

