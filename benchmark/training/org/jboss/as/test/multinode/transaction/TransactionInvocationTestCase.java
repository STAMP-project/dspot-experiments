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
package org.jboss.as.test.multinode.transaction;


import java.io.IOException;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A simple EJB Remoting transaction context propagation in JTS style from one AS7 server to another.
 *
 * @author Stuart Douglas
 * @author Ivo Studensky
 */
@RunWith(Arquillian.class)
public class TransactionInvocationTestCase {
    public static final String SERVER_DEPLOYMENT = "server";

    public static final String CLIENT_DEPLOYMENT = "client";

    @Test
    @OperateOnDeployment("client")
    public void testRemoteInvocation() throws IOException, NamingException, NotSupportedException, SystemException {
        final ClientEjb ejb = getClient();
        ejb.basicTransactionPropagationTest();
    }

    @Test
    @OperateOnDeployment("client")
    public void testRollbackOnly() throws IOException, NamingException, NotSupportedException, SystemException {
        final ClientEjb ejb = getClient();
        ejb.testRollbackOnly();
    }

    @Test
    @OperateOnDeployment("client")
    public void testRollbackOnlyBeforeCompletion() throws IOException, NamingException, HeuristicMixedException, HeuristicRollbackException, NotSupportedException, SystemException {
        final ClientEjb ejb = getClient();
        ejb.testRollbackOnlyBeforeCompletion();
    }

    @Test
    @OperateOnDeployment("client")
    public void testSameTransactionEachCall() throws IOException, NamingException, NotSupportedException, SystemException {
        final ClientEjb ejb = getClient();
        ejb.testSameTransactionEachCall();
    }

    @Test
    @OperateOnDeployment("client")
    public void testSynchronizationSucceeded() throws IOException, NamingException, HeuristicMixedException, HeuristicRollbackException, NotSupportedException, RollbackException, SystemException {
        final ClientEjb ejb = getClient();
        ejb.testSynchronization(true);
    }

    @Test
    @OperateOnDeployment("client")
    public void testSynchronizationFailed() throws IOException, NamingException, HeuristicMixedException, HeuristicRollbackException, NotSupportedException, RollbackException, SystemException {
        final ClientEjb ejb = getClient();
        ejb.testSynchronization(false);
    }
}

