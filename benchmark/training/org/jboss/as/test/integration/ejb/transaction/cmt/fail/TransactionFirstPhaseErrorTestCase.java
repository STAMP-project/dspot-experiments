/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018 Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.transaction.cmt.fail;


import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.transaction.HeuristicMixedException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.transactions.TransactionCheckerSingleton;
import org.jboss.as.test.integration.transactions.TransactionTestLookupUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test of behavior when one phase commit is used.
 */
@RunWith(Arquillian.class)
public class TransactionFirstPhaseErrorTestCase {
    @ArquillianResource
    private InitialContext initCtx;

    @Inject
    private TransactionCheckerSingleton checker;

    /**
     * Using {@link XAResource} which fails with <code>XAResource.XAER_RMFAIL</code>
     * during commit.<br>
     * Expecting the error will be propagated to the caller.
     */
    @Test
    public void xaOnePhaseCommitFail() throws Exception {
        OuterBean bean = TransactionTestLookupUtil.lookupModule(initCtx, OuterBean.class);
        try {
            bean.outerMethodXA();
            Assert.fail("Expecting the one phase commit failed and exception was propagated to the caller.");
        } catch (EJBException expected) {
            Assert.assertTrue("Expecting on RMFAIL to get unknown state of the transaction outcome - ie. HeuristicMixedException", (((expected.getCause()) != null) && (expected.getCause().getClass().equals(HeuristicMixedException.class))));
        }
    }

    /**
     * Using two {@link XAResource}s where the first one fails with <code>XAResource.XAER_RMFAIL</code>
     * during commit.<br>
     * Expecting the no error will be thrown as 2PC prepare phase finished and rmfail says
     * that recovery manager should retry the commit later.
     */
    @Test
    public void xaTwoPhaseCommitFail() throws Exception {
        OuterBean bean = TransactionTestLookupUtil.lookupModule(initCtx, OuterBean.class);
        bean.outerMethod2pcXA();
    }

    /**
     * Using {@link XAResource} where optimization for {@link LastResource} is used.
     * The commit call fails with <code>XAResource.XAER_RMFAIL</code><br>
     * Expecting the error will be propagated to the caller.
     */
    @Test
    public void localOnePhaseCommitFail() throws Exception {
        OuterBean bean = TransactionTestLookupUtil.lookupModule(initCtx, OuterBean.class);
        try {
            bean.outerMethodLocal();
            Assert.fail("Expecting the one phase commit failed and exception was propagated to the caller.");
        } catch (EJBException expected) {
            Assert.assertTrue("Expecting on RMFAIL to get unknown state of the transaction outcome - ie. HeuristicMixedException", (((expected.getCause()) != null) && (expected.getCause().getClass().equals(HeuristicMixedException.class))));
        }
    }
}

