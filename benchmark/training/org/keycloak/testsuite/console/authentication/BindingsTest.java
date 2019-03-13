/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.keycloak.testsuite.console.authentication;


import BindingsOption.REGISTRATION;
import BindingsOption.RESET_CREDENTIALS;
import BindingsSelect.BROWSER;
import BindingsSelect.DIRECT_GRANT;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.page.authentication.bindings.Bindings;


/**
 *
 *
 * @author <a href="mailto:vramik@redhat.com">Vlastislav Ramik</a>
 */
public class BindingsTest extends AbstractConsoleTest {
    @Page
    private Bindings bindingsPage;

    @Test
    public void bindingsTest() {
        bindingsPage.form().select(BROWSER, REGISTRATION);
        bindingsPage.form().select(BindingsSelect.REGISTRATION, RESET_CREDENTIALS);
        bindingsPage.form().select(DIRECT_GRANT, BindingsOption.BROWSER);
        bindingsPage.form().select(BindingsSelect.RESET_CREDENTIALS, BindingsOption.DIRECT_GRANT);
        bindingsPage.form().save();
        Assert.assertEquals("Success! Your changes have been saved to the realm.", bindingsPage.getSuccessMessage());
        RealmRepresentation realm = testRealmResource().toRepresentation();
        Assert.assertEquals("registration", realm.getBrowserFlow());
        Assert.assertEquals("reset credentials", realm.getRegistrationFlow());
        Assert.assertEquals("browser", realm.getDirectGrantFlow());
        Assert.assertEquals("direct grant", realm.getResetCredentialsFlow());
        Assert.assertEquals("clients", realm.getClientAuthenticationFlow());
    }
}

