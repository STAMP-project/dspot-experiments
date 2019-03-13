/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.remote.security;


import java.util.Properties;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.security.common.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A test case to test an unsecured EJB setting the username and password before the call reaches a secured EJB.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RemoteIdentityTestCase {
    @ArquillianResource
    private ManagementClient mgmtClient;

    @Test
    public void testDirect() throws Exception {
        final Properties ejbClientConfiguration = EJBUtil.createEjbClientConfiguration(Utils.getHost(mgmtClient));
        final SecurityInformation targetBean = EJBUtil.lookupEJB(SecuredBean.class, SecurityInformation.class, ejbClientConfiguration);
        Assert.assertEquals("guest", targetBean.getPrincipalName());
    }

    @Test
    public void testUnsecured() throws Exception {
        final Properties ejbClientConfiguration = EJBUtil.createEjbClientConfiguration(Utils.getHost(mgmtClient));
        final IntermediateAccess targetBean = EJBUtil.lookupEJB(EntryBean.class, IntermediateAccess.class, ejbClientConfiguration);
        Assert.assertEquals("anonymous", targetBean.getPrincipalName());
    }

    @Test
    public void testSwitched() throws Exception {
        final Properties ejbClientConfiguration = EJBUtil.createEjbClientConfiguration(Utils.getHost(mgmtClient));
        final IntermediateAccess targetBean = EJBUtil.lookupEJB(EntryBean.class, IntermediateAccess.class, ejbClientConfiguration);
        Assert.assertEquals("user1", targetBean.getPrincipalName("user1", "password1"));
    }

    @Test
    public void testNotSwitched() throws Exception {
        final Properties ejbClientConfiguration = EJBUtil.createEjbClientConfiguration(Utils.getHost(mgmtClient));
        final IntermediateAccess targetBean = EJBUtil.lookupEJB(EntryBean.class, IntermediateAccess.class, ejbClientConfiguration);
        Assert.assertEquals("guest", targetBean.getPrincipalName(null, null));
    }
}

