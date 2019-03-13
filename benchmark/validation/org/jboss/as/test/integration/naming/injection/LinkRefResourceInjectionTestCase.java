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
package org.jboss.as.test.integration.naming.injection;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A test which enforces that
 *
 * @author Eduardo Martins
 */
@RunWith(Arquillian.class)
public class LinkRefResourceInjectionTestCase {
    private static final String BINDER_JAR_NAME = "binder";

    private static final String INJECTED_JAR_NAME = "injected";

    @ArquillianResource
    public Deployer deployer;

    @ArquillianResource
    private InitialContext initialContext;

    @Test
    public void test() throws NamingException {
        String bindValue = "az";
        // lookup binder ejb
        final Binder binder = ((Binder) (initialContext.lookup(((((("java:global/" + (LinkRefResourceInjectionTestCase.BINDER_JAR_NAME)) + "/") + (BinderBean.class.getSimpleName())) + "!") + (Binder.class.getName())))));
        // bind the value, which will be accessible at Binder.LINK_NAME
        binder.bindAndLink(bindValue);
        // deploy the injected bean, which has a field resource injection pointing to Binder.LINK_NAME
        deployer.deploy(LinkRefResourceInjectionTestCase.INJECTED_JAR_NAME);
        try {
            final Injected injected = ((Injected) (initialContext.lookup(((("java:global/" + (LinkRefResourceInjectionTestCase.INJECTED_JAR_NAME)) + "/InjectedBean!") + (Injected.class.getName())))));
            // this assertion implies that the link was followed corrected into the src binding
            Assert.assertEquals(injected.getInjectedResource(), bindValue);
        } finally {
            deployer.undeploy(LinkRefResourceInjectionTestCase.INJECTED_JAR_NAME);
        }
    }
}

