/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.wildfly.test.integration.elytron.ejb;


import java.io.File;
import java.security.Principal;
import java.util.concurrent.Callable;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.shared.integration.ejb.security.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.test.security.common.elytron.EjbElytronDomainSetup;
import org.wildfly.test.security.common.elytron.ElytronDomainSetup;


/**
 *
 *
 * @author <a href="mailto:szhantem@redhat.com">Sultan Zhantemirov</a> (c) 2018 Red Hat, Inc.
Test case on a deployment containing a secured EJB with non-default security domain and an unsecured one.
Test passes if deployment is successful and functional.
 */
@RunWith(Arquillian.class)
@ServerSetup({ DefaultElytronEjbSecurityDomainTestCase.ElytronDomainSetupTestCaseOverride.class, EjbElytronDomainSetup.class })
public class DefaultElytronEjbSecurityDomainTestCase {
    @ArquillianResource
    private Context ctx;

    @EJB(mappedName = "java:global/ejb3security/WhoAmIBean!org.wildfly.test.integration.elytron.ejb.WhoAmI")
    private WhoAmI securedBean;

    @Test
    public void testSecurityOnTwoBeansInAbsenceOfExplicitSecurityDomain() throws Exception {
        final EjbUnsecuredBean unsecuredBean = InitialContext.doLookup(("java:module/" + (EjbUnsecuredBean.class.getSimpleName())));
        final String echoResult = unsecuredBean.echo("unsecuredBeanEcho");
        Assert.assertEquals("unsecuredBeanEcho", echoResult);
        final Callable<Void> callable = () -> {
            try {
                final Principal principal = securedBean.getCallerPrincipal();
                Assert.assertNotNull("EJB must never return a null from the getCallerPrincipal method.", principal);
                Assert.assertEquals("user1", principal.getName());
            } catch (RuntimeException e) {
                Assert.fail((("EJB must provide the caller?s security context information during the execution of a business method (" + (e.getMessage())) + ")"));
            }
            return null;
        };
        Util.switchIdentitySCF("user1", "password1", callable);
    }

    public static class ElytronDomainSetupTestCaseOverride extends ElytronDomainSetup {
        public ElytronDomainSetupTestCaseOverride() {
            super(new File(AuthenticationTestCase.class.getResource("users.properties").getFile()).getAbsolutePath(), new File(AuthenticationTestCase.class.getResource("roles.properties").getFile()).getAbsolutePath());
        }
    }
}

