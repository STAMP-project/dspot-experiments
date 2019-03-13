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
package org.jboss.as.test.integration.ejb.descriptor;


import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.common.Naming;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * [AS7-835] ejb-jar.xml doesn't need to define a session-type
 *
 * https://issues.jboss.org/browse/AS7-835
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
@RunWith(Arquillian.class)
public class AS835TestCase {
    /**
     * Make sure the ejb-jar.xml is actually processed.
     */
    @Test
    public void testEnvEntry() throws NamingException {
        final SimpleStatelessBean bean = Naming.lookup("java:global/as835/SimpleStatelessBean", SimpleStatelessBean.class);
        final String envValue = bean.getTest();
        // see ejb-jar.xml for the value
        Assert.assertEquals("*Hello world", envValue);
    }

    /**
     * Make sure the ejb-jar.xml is actually processed.
     */
    @Test
    public void testInterceptor() throws NamingException {
        final SimpleStatelessBean bean = Naming.lookup("java:global/as835/SimpleStatelessBean", SimpleStatelessBean.class);
        final String envValue = bean.getTest();
        // see SimpleStatelessBean.aroundInvoke for the value
        Assert.assertNotNull(envValue);
        Assert.assertTrue(envValue.startsWith("*"));
    }

    @Test
    public void testInvocation() throws NamingException {
        final SimpleStatelessBean bean = Naming.lookup("java:global/as835/SimpleStatelessBean", SimpleStatelessBean.class);
        bean.getTest();
        // if we can invoke the bean it must have been deployed properly
    }
}

