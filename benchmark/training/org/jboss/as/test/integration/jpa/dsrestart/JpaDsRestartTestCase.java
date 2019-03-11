/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.integration.jpa.dsrestart;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.msc.service.ServiceContainer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
@RunWith(Arquillian.class)
public class JpaDsRestartTestCase {
    @ArquillianResource
    private InitialContext iniCtx;

    @ArquillianResource
    ServiceContainer serviceContainer;

    @Test
    public void testRestartDataSource() throws Exception {
        Assert.assertNotNull(serviceContainer);
        NonJpaSfsb nonJpaSfsb = ((NonJpaSfsb) (iniCtx.lookup(("java:module/" + (NonJpaSfsb.class.getSimpleName())))));
        Assert.assertEquals("test", nonJpaSfsb.echo("test"));
        JpaInjectedSfsb jpaInjected = ((JpaInjectedSfsb) (iniCtx.lookup(("java:module/" + (JpaInjectedSfsb.class.getSimpleName())))));
        Assert.assertNotNull(jpaInjected.getEntityManager());
        EjbInjectedSfsb ejbInjected = ((EjbInjectedSfsb) (iniCtx.lookup(("java:module/" + (EjbInjectedSfsb.class.getSimpleName())))));
        Assert.assertNotNull(ejbInjected.getJpaEjb());
        Assert.assertNotNull(ejbInjected.getJpaEjb().getEntityManager());
        // Should fail since app has dependencies on it
        toggleDataSource(false, true);
        // The persistence unit service should be restarted
        Assert.assertEquals("test", nonJpaSfsb.echo("test"));
        Assert.assertNotNull(jpaInjected.getEntityManager());
        Assert.assertNotNull(ejbInjected.getJpaEjb());
        Assert.assertNotNull(ejbInjected.getJpaEjb().getEntityManager());
    }
}

