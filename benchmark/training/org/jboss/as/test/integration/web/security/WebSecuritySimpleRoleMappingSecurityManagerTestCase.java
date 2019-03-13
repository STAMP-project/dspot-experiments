/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.web.security;


import java.net.URL;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the situation when security manager is enabled and MappingResult is called in Servlet.
 *
 * RuntimePermission("getClassLoader") and RuntimePermission("createClassLoader") should NOT be necessary.
 * See: https://issues.jboss.org/browse/WFLY-8760
 *
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(WebSimpleRoleMappingSecurityDomainSetup.class)
public class WebSecuritySimpleRoleMappingSecurityManagerTestCase {
    private static final String JBOSS_WEB_CONTENT = ((("<?xml version=\"1.0\"?>\n" + ("<jboss-web>\n" + "    <security-domain>")) + (WebSimpleRoleMappingSecurityDomainSetup.WEB_SECURITY_DOMAIN)) + "</security-domain>\n") + "</jboss-web>";

    private static final String WEB_CONTENT = "<?xml version=\"1.0\"?>\n" + (((((((("<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "    xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\"\n") + "    version=\"3.0\">\n") + "    <login-config>\n") + "        <auth-method>BASIC</auth-method>\n") + "        <realm-name>WebSecurityBasic</realm-name>\n") + "    </login-config>\n") + "</web-app>");

    @ArquillianResource
    private URL url;

    /**
     * At this time peter can go through because he has role mapped in the map-module option.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPrincipalMappingOnRole() throws Exception {
        makeCall("peter", "peter", 200, "gooduser:superuser");
    }
}

