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
 * 2110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ejb.remote.security;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.security.auth.client.AuthenticationContext;


/**
 * A test case to test an unsecured EJB setting the username and password before the call reaches a secured EJB.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HttpRemoteIdentityTestCase {
    @ContainerResource
    private ManagementClient managementClient;

    private static AuthenticationContext old;

    @Test
    public void testDirect() throws Exception {
        final SecurityInformation targetBean = lookupEJB(SecuredBean.class, SecurityInformation.class);
        Assert.assertEquals("user1", targetBean.getPrincipalName());
    }

    @Test
    public void testUnsecured() throws Exception {
        final IntermediateAccess targetBean = lookupEJB(EntryBean.class, IntermediateAccess.class);
        Assert.assertEquals("anonymous", targetBean.getPrincipalName());
    }
}

