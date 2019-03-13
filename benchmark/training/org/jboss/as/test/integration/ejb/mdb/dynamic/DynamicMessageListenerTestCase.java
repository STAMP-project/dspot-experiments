/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.mdb.dynamic;


import java.io.DataInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.ejb.mdb.dynamic.impl.TelnetInputStream;
import org.jboss.as.test.integration.ejb.mdb.dynamic.impl.TelnetPrintStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DynamicMessageListenerTestCase {
    @ContainerResource
    private ManagementClient managementClient;

    @Test
    public void test1() throws Exception {
        final Socket socket = new Socket(managementClient.getWebUri().getHost(), 2020);
        final OutputStream sockOut = socket.getOutputStream();
        final DataInputStream in = new DataInputStream(new TelnetInputStream(socket.getInputStream(), sockOut));
        final PrintStream out = new TelnetPrintStream(sockOut);
        DynamicMessageListenerTestCase.assertEmptyLine(in);
        Assert.assertEquals("type 'help' for a list of commands", in.readLine());
        out.println("set a b");
        out.flush();
        DynamicMessageListenerTestCase.assertReply("set a to b", in.readLine());
        DynamicMessageListenerTestCase.assertEmptyLine(in);
        out.println("get a");
        out.flush();
        DynamicMessageListenerTestCase.assertReply("b", in.readLine());
        DynamicMessageListenerTestCase.assertEmptyLine(in);
        out.println("list");
        out.flush();
        DynamicMessageListenerTestCase.assertReply("a = b", in.readLine());
        DynamicMessageListenerTestCase.assertEmptyLine(in);
    }
}

