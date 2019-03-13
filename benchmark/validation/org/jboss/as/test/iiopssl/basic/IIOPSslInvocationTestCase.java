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
package org.jboss.as.test.iiopssl.basic;


import java.io.IOException;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A simple IIOP over SSL invocation for one server to another
 */
@RunWith(Arquillian.class)
public class IIOPSslInvocationTestCase {
    @Test
    @OperateOnDeployment("client")
    public void testSuccessfulInvocation() throws IOException, NamingException, LoginException {
        final ClientEjb ejb = client();
        Assert.assertEquals("hello", ejb.getRemoteMessage());
    }

    @Test
    @OperateOnDeployment("client")
    public void testManualSslLookup() throws Exception {
        final ClientEjb ejb = client();
        Assert.assertEquals("hello", ejb.lookupSsl(3629));
    }

    @Test
    @OperateOnDeployment("client")
    public void testManualCleartextLookup() throws Exception {
        try {
            final ClientEjb ejb = client();
            Assert.assertEquals("hello", ejb.lookup(3628));
            Assert.fail("Connection on CLEAR-TEXT port should be refused");
        } catch (NamingException e) {
        }
    }
}

