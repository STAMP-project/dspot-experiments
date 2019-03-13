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
package org.jboss.as.test.integration.ejb.security;


import java.util.Map;
import java.util.concurrent.Callable;
import javax.ejb.EJB;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.categories.CommonCriteria;
import org.jboss.as.test.integration.ejb.security.lifecycle.BaseBean;
import org.jboss.as.test.integration.ejb.security.lifecycle.EntryBean;
import org.jboss.as.test.shared.integration.ejb.security.Util;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * EJB 3.1 Section 17.2.5 - This test case is to test the programmatic access to the callers's security context for the various
 * bean methods.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(Arquillian.class)
@ServerSetup({ EjbSecurityDomainSetup.class })
@Category(CommonCriteria.class)
public class LifecycleTestCase {
    private static final Logger log = Logger.getLogger(LifecycleTestCase.class.getName());

    private static final String USER1 = "user1";

    private static final String TRUE = "true";

    private static final String UNSUPPORTED_OPERATION = "UnsupportedOperationException";

    private static final String ILLEGAL_STATE = "IllegalStateException";

    @EJB(mappedName = "java:global/ejb3security/EntryBean")
    private EntryBean entryBean;

    @Test
    public void testStatefulBean() throws Exception {
        StringBuilder failureMessages = new StringBuilder();
        final Callable<Void> callable = () -> {
            Map<String, String> result = entryBean.testStatefulBean();
            verifyResult(result, BaseBean.LIFECYCLE_CALLBACK, LifecycleTestCase.USER1, LifecycleTestCase.UNSUPPORTED_OPERATION, LifecycleTestCase.TRUE, LifecycleTestCase.ILLEGAL_STATE, failureMessages);
            verifyResult(result, BaseBean.BUSINESS, LifecycleTestCase.USER1, LifecycleTestCase.UNSUPPORTED_OPERATION, LifecycleTestCase.TRUE, LifecycleTestCase.ILLEGAL_STATE, failureMessages);
            verifyResult(result, BaseBean.AFTER_BEGIN, LifecycleTestCase.USER1, LifecycleTestCase.UNSUPPORTED_OPERATION, LifecycleTestCase.TRUE, LifecycleTestCase.ILLEGAL_STATE, failureMessages);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
        if ((failureMessages.length()) > 0) {
            Assert.fail(failureMessages.toString());
        }
    }

    @Test
    public void testStatefulBeanDependencyInjection() throws Exception {
        StringBuilder failureMessages = new StringBuilder();
        final Callable<Void> callable = () -> {
            Map<String, String> result = entryBean.testStatefulBean();
            verifyResult(result, BaseBean.DEPENDENCY_INJECTION, LifecycleTestCase.ILLEGAL_STATE, LifecycleTestCase.UNSUPPORTED_OPERATION, LifecycleTestCase.ILLEGAL_STATE, LifecycleTestCase.ILLEGAL_STATE, failureMessages);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
        if ((failureMessages.length()) > 0) {
            Assert.fail(failureMessages.toString());
        }
    }

    @Test
    public void testStatelessBean() throws Exception {
        StringBuilder failureMessages = new StringBuilder();
        final Callable<Void> callable = () -> {
            Map<String, String> result = entryBean.testStatlessBean();
            for (String current : result.keySet()) {
                LifecycleTestCase.log.trace(((current + " = ") + (result.get(current))));
            }
            verifyResult(result, BaseBean.BUSINESS, LifecycleTestCase.USER1, LifecycleTestCase.UNSUPPORTED_OPERATION, LifecycleTestCase.TRUE, LifecycleTestCase.ILLEGAL_STATE, failureMessages);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
        if ((failureMessages.length()) > 0) {
            Assert.fail(failureMessages.toString());
        }
    }

    @Test
    public void testStatelessBeanDependencyInjection() throws Exception {
        StringBuilder failureMessages = new StringBuilder();
        final Callable<Void> callable = () -> {
            Map<String, String> result = entryBean.testStatlessBean();
            for (String current : result.keySet()) {
                LifecycleTestCase.log.trace(((current + " = ") + (result.get(current))));
            }
            verifyResult(result, BaseBean.DEPENDENCY_INJECTION, LifecycleTestCase.ILLEGAL_STATE, LifecycleTestCase.UNSUPPORTED_OPERATION, LifecycleTestCase.ILLEGAL_STATE, LifecycleTestCase.ILLEGAL_STATE, failureMessages);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
        if ((failureMessages.length()) > 0) {
            Assert.fail(failureMessages.toString());
        }
    }
}

