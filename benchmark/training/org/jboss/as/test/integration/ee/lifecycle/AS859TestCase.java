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
package org.jboss.as.test.integration.ee.lifecycle;


import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.common.Naming;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * [AS7-859] On same named private lifecycle callbacks the super class callback is not called
 *
 * https://issues.jboss.org/browse/AS7-859
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
@RunWith(Arquillian.class)
public class AS859TestCase {
    @Test
    public void testPostConstruct() throws NamingException {
        final Child bean = Naming.lookup("java:global/as859/Child", Child.class);
        Assert.assertNotNull(bean);
        Assert.assertTrue("Child @PostConstruct has not been called", Child.postConstructCalled);
        Assert.assertTrue("Parent @PostConstruct has not been called", Parent.postConstructCalled);
    }
}

