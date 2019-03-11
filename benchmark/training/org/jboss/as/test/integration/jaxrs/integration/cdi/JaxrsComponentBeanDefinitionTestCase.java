/**
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.as.test.integration.jaxrs.integration.cdi;


import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class JaxrsComponentBeanDefinitionTestCase {
    @Inject
    BeanManager beanManager;

    @Test
    public void testResource() throws Exception {
        // There's one bean of type CDIResource and it's scope is @RequestScoped (this is resteasy-cdi specific)
        Set<Bean<?>> beans = beanManager.getBeans(CDIResource.class);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals(RequestScoped.class, beans.iterator().next().getScope());
    }

    @Test
    public void testApplication() throws Exception {
        // There's one bean of type CDIApplication and it's scope is @ApplicationScoped
        Set<Bean<?>> beans = beanManager.getBeans(CDIApplication.class);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals(ApplicationScoped.class, beans.iterator().next().getScope());
    }

    @Test
    public void testProvider() throws Exception {
        // There's one bean of type CDIProvider and it's scope is @ApplicationScoped
        Set<Bean<?>> beans = beanManager.getBeans(CDIProvider.class);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals(ApplicationScoped.class, beans.iterator().next().getScope());
    }
}

