/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.web.security.jaspi;


import java.net.URL;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.categories.CommonCriteria;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Tests that JASPI authentication reports error if the authentication process fails.
 * <p>
 * The AuthModule always throws Exception, but doesn't set the http error code.
 *
 * @author <a href="mailto:bspyrkos@redhat.com">Bartosz Spyrko-Smietanko</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(WebJaspiTestsSecurityDomainSetup.WithFailingAuthModule.class)
@Category(CommonCriteria.class)
public class WebSecurityJaspiWithFailingAuthModuleTestCase {
    private static final String JBOSS_WEB_CONTENT = ((("<?xml version=\"1.0\"?>\n" + ("<jboss-web>\n" + "    <security-domain>")) + (WebJaspiTestsSecurityDomainSetup.WEB_SECURITY_DOMAIN)) + "</security-domain>\n") + "</jboss-web>";

    @ArquillianResource
    private URL url;

    @Test
    public void testShouldReturnErrorCodeIfTheAuthModuleThrowsException() throws Exception {
        makeCall("anil", "anil", 401);
    }
}

