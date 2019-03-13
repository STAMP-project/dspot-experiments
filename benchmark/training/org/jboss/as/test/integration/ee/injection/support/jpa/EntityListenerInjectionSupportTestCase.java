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
package org.jboss.as.test.integration.ee.injection.support.jpa;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Matus Abaffy
 */
@RunWith(Arquillian.class)
public class EntityListenerInjectionSupportTestCase {
    private static final String ARCHIVE_NAME = "test";

    @ArquillianResource
    private static InitialContext iniCtx;

    @Test
    public void testCDIInjectionPerformed() throws Exception {
        MyBean cmt = lookup("MyBean", MyBean.class);
        cmt.createEmployee("Joe Black", "Brno 2", 0);
        Assert.assertTrue(MyListener.isIjectionPerformed());
    }

    @Test
    public void testInterceptorCalled() throws Exception {
        MyListener.setInvocationCount(0);
        int id = 1;
        MyBean bean = lookup("MyBean", MyBean.class);
        bean.createEmployee("Joe Black", "Brno 2", id);
        Assert.assertEquals("EntityListener was not called.", 1, MyListener.getInvocationCount());
        Assert.assertTrue("Interceptor was not called.", MyListenerInterceptor.wasCalled);
        // ID should be increased by 1 in MyListenerInterceptor
        // id++;
        Employee emp = bean.getEmployeeById(id);
        Assert.assertNotNull("Could not load added employee.", emp);
    }
}

