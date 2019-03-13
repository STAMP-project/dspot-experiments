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
package org.jboss.as.test.iiop.basic;


import java.io.IOException;
import javax.naming.NamingException;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A simple IIOP invocation for one AS7 server to another
 */
@RunWith(Arquillian.class)
public class BasicIIOPInvocationTestCase {
    @Test
    @OperateOnDeployment("client")
    public void testRemoteIIOPInvocation() throws IOException, NamingException {
        final ClientEjb ejb = client();
        Assert.assertEquals("hello", ejb.getRemoteMessage());
    }

    @Test
    @OperateOnDeployment("client")
    public void testHomeHandle() throws IOException, NamingException {
        final ClientEjb ejb = client();
        Assert.assertEquals("hello", ejb.getRemoteViaHomeHandleMessage());
    }

    @Test
    @OperateOnDeployment("client")
    public void testHandle() throws IOException, NamingException {
        final ClientEjb ejb = client();
        Assert.assertEquals("hello", ejb.getRemoteViaHandleMessage());
    }

    /**
     * Tests that even if a handle is returned embedded in another object the substitution service will
     * replace it with a correct IIOP version of a handle.
     */
    @Test
    @OperateOnDeployment("client")
    public void testWrappedHandle() throws IOException, NamingException {
        final ClientEjb ejb = client();
        Assert.assertEquals("hello", ejb.getRemoteViaWrappedHandle());
    }

    @Test
    @OperateOnDeployment("client")
    public void testEjbMetadata() throws IOException, NamingException {
        final ClientEjb ejb = client();
        Assert.assertEquals("hello", ejb.getRemoteMessageViaEjbMetadata());
    }

    @Test
    @OperateOnDeployment("client")
    public void testIsIdentical() throws IOException, NamingException {
        final ClientEjb ejb = client();
        ejb.testIsIdentical();
    }
}

