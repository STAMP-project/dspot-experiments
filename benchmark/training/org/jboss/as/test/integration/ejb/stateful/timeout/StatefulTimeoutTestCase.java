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
package org.jboss.as.test.integration.ejb.stateful.timeout;


import javax.ejb.NoSuchEJBException;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that the stateful timeout annotation works
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class StatefulTimeoutTestCase {
    private static final String ARCHIVE_NAME = "StatefulTimeoutTestCase";

    @ArquillianResource
    private InitialContext iniCtx;

    @Inject
    private UserTransaction userTransaction;

    @Test
    public void testStatefulTimeoutFromAnnotation() throws Exception {
        AnnotatedBean sfsb1 = lookup(AnnotatedBean.class);
        Assert.assertFalse(AnnotatedBean.preDestroy);
        sfsb1.doStuff();
        Thread.sleep(2000);
        try {
            sfsb1.doStuff();
            throw new RuntimeException("Expecting NoSuchEjbException");
        } catch (NoSuchEJBException expected) {
        }
        Assert.assertTrue(AnnotatedBean.preDestroy);
    }

    @Test
    public void testStatefulTimeoutWithPassivation() throws Exception {
        PassivatingBean sfsb1 = lookup(PassivatingBean.class);
        Assert.assertFalse(PassivatingBean.preDestroy);
        sfsb1.doStuff();
        Thread.sleep(2000);
        try {
            sfsb1.doStuff();
            throw new RuntimeException("Expecting NoSuchEjbException");
        } catch (NoSuchEJBException expected) {
        }
        Assert.assertTrue(PassivatingBean.preDestroy);
    }

    @Test
    public void testStatefulTimeoutFromDescriptor() throws Exception {
        DescriptorBean sfsb1 = lookup(DescriptorBean.class);
        Assert.assertFalse(DescriptorBean.preDestroy);
        sfsb1.doStuff();
        Thread.sleep(2000);
        try {
            sfsb1.doStuff();
            throw new RuntimeException("Expecting NoSuchEjbException");
        } catch (NoSuchEJBException expected) {
        }
        Assert.assertTrue(DescriptorBean.preDestroy);
    }

    @Test
    public void testStatefulBeanNotDiscardedWhileInTransaction() throws Exception {
        try {
            userTransaction.begin();
            AnnotatedBean2 sfsb1 = lookup(AnnotatedBean2.class);
            Assert.assertFalse(AnnotatedBean2.preDestroy);
            sfsb1.doStuff();
            Thread.sleep(2000);
            sfsb1.doStuff();
            Assert.assertFalse(AnnotatedBean2.preDestroy);
        } finally {
            userTransaction.commit();
        }
    }

    @Test
    public void testStatefulBeanWithPassivationNotDiscardedWhileInTransaction() throws Exception {
        try {
            userTransaction.begin();
            PassivatingBean2 sfsb1 = lookup(PassivatingBean2.class);
            Assert.assertFalse(PassivatingBean2.preDestroy);
            sfsb1.doStuff();
            Thread.sleep(2000);
            sfsb1.doStuff();
            Assert.assertFalse(PassivatingBean2.preDestroy);
        } finally {
            userTransaction.commit();
        }
    }
}

