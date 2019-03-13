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
package org.jboss.as.test.integration.jpa.entitylistener;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * EntityListeners tests
 *
 * @author Scott Marlow
 */
@RunWith(Arquillian.class)
public class EntityListenersTestCase {
    private static final String ARCHIVE_NAME = "jpa_EntityListeners";

    @ArquillianResource
    private static InitialContext iniCtx;

    /**
     * Tests that the entity listeners are correctly invoked and have access to the java:comp/EJBContext
     * when an entity is persisted via a stateful BMT bean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSFSBBMT() throws Exception {
        MyListener.setInvocationCount(0);
        SFSBBMT bmt = lookup("SFSBBMT", SFSBBMT.class);
        this.doBMTTest(bmt, 1);
    }

    /**
     * Tests that the entity listeners are correctly invoked and have access to the java:comp/EJBContext
     * when an entity is persisted via a stateful CMT bean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSFSBCMT() throws Exception {
        MyListener.setInvocationCount(0);
        SFSBCMT cmt = lookup("SFSBCMT", SFSBCMT.class);
        this.doCMTTest(cmt, 2);
    }

    /**
     * Test that @PostConstruct is invoked
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCDICallbackInvoked() throws Exception {
        SFSBCMT cmt = lookup("SFSBCMT", SFSBCMT.class);
        Assert.assertEquals(("MyListener should of been created by the BeanManager instance passed into the " + "persistence provider via property 'javax.persistence.bean.manager'. "), 1, MyListener.getPostCtorInvocationCount());
    }

    /**
     * Tests that the entity listeners are correctly invoked and have access to the java:comp/EJBContext
     * when an entity is persisted via a stateless CMT bean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSLSBCMT() throws Exception {
        MyListener.setInvocationCount(0);
        SLSBCMT cmt = lookup("SLSBCMT", SLSBCMT.class);
        this.doCMTTest(cmt, 3);
    }

    /**
     * Tests that the entity listeners are correctly invoked and have access to the java:comp/EJBContext
     * when an entity is persisted via a stateless BMT bean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSLSBBMT() throws Exception {
        MyListener.setInvocationCount(0);
        SLSBBMT bmt = lookup("SLSBBMT", SLSBBMT.class);
        this.doBMTTest(bmt, 4);
    }

    /**
     * Tests that the entity listeners are correctly invoked and have access to the java:comp/EJBContext
     * when an entity is persisted via a singleton CMT bean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSingletonCMT() throws Exception {
        MyListener.setInvocationCount(0);
        SingletonCMT cmt = lookup("SingletonCMT", SingletonCMT.class);
        this.doCMTTest(cmt, 5);
    }

    /**
     * Tests that the entity listeners are correctly invoked and have access to the java:comp/EJBContext
     * when an entity is persisted via a singleton BMT bean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSingletonBMT() throws Exception {
        MyListener.setInvocationCount(0);
        SingletonBMT bmt = lookup("SingletonBMT", SingletonBMT.class);
        this.doBMTTest(bmt, 6);
    }
}

