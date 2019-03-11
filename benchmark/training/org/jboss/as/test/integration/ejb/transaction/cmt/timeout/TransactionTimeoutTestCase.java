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
package org.jboss.as.test.integration.ejb.transaction.cmt.timeout;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(Arquillian.class)
public class TransactionTimeoutTestCase {
    /**
     *
     */
    @Test
    public void testBeanTimeouts() throws Exception {
        TimeoutRemoteView remoteView = ((TimeoutRemoteView) (new InitialContext().lookup("java:module/BeanWithTimeoutValue!org.jboss.as.test.integration.ejb.transaction.cmt.timeout.TimeoutRemoteView")));
        TimeoutLocalView localView = ((TimeoutLocalView) (new InitialContext().lookup("java:module/BeanWithTimeoutValue!org.jboss.as.test.integration.ejb.transaction.cmt.timeout.TimeoutLocalView")));
        long timeoutValue = -1;
        timeoutValue = ((long) (remoteView.getBeanTimeout()));
        Assert.assertEquals("Bean-level timeout failed", 5L, timeoutValue);
        timeoutValue = ((long) (remoteView.getBeanMethodTimeout()));
        Assert.assertEquals("Bean-method timeout failed", 6L, timeoutValue);
        timeoutValue = ((long) (remoteView.getRemoteMethodTimeout()));
        Assert.assertEquals("Remote-method timeout failed", 7L, timeoutValue);
        timeoutValue = ((long) (localView.getLocalViewTimeout()));
        Assert.assertEquals("Local-view timeout failed", 5L, timeoutValue);
    }

    @Test
    public void testDescriptor() throws Exception {
        final TimeoutLocalView localView = ((TimeoutLocalView) (new InitialContext().lookup(("java:module/DDBeanWithTimeoutValue!" + (TimeoutLocalView.class.getName())))));
        Assert.assertEquals(10, localView.getLocalViewTimeout());
    }

    @Test
    public void testDescriptorWithNestedExpressions() throws Exception {
        final TimeoutLocalView localView = ((TimeoutLocalView) (new InitialContext().lookup(("java:module/DDBeanWithTimeoutValueUsingNestedExpression!" + (TimeoutLocalView.class.getName())))));
        Assert.assertEquals(90, localView.getLocalViewTimeout());
    }
}

