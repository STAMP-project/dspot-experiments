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
package org.jboss.as.test.integration.naming.local.simple;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author John Bailey
 */
@RunWith(Arquillian.class)
public class DeploymentWithBindTestCase {
    @ArquillianResource
    private InitialContext iniCtx;

    @ArquillianResource
    private ManagementClient managementClient;

    @Test
    @InSequence(1)
    public void testEjb() throws Exception {
        final BeanWithBind bean = lookup(BeanWithBind.class);
        bean.doBind();
        Assert.assertNotNull(iniCtx.lookup("java:jboss/test"));
        Assert.assertNotNull(iniCtx.lookup("java:/test"));
    }

    @Test
    @InSequence(2)
    public void testServlet() throws Exception {
        performCall("simple", "bind");
        Assert.assertNotNull(iniCtx.lookup("java:jboss/test"));
        Assert.assertNotNull(iniCtx.lookup("java:/test"));
    }

    @Test
    @InSequence(3)
    public void testBasicsNamespaces() throws Exception {
        iniCtx.lookup("java:global");
        iniCtx.listBindings("java:global");
        iniCtx.lookup("java:jboss");
        iniCtx.listBindings("java:jboss");
        iniCtx.lookup("java:jboss/exported");
        iniCtx.listBindings("java:jboss/exported");
    }
}

