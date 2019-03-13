/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.manualmode.ws;


import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Some tests on changes to the model that are applied immediately to the runtime
 * when there's no WS deployment on the server.
 *
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class WSAttributesChangesTestCase {
    private static final String DEFAULT_JBOSSAS = "default-jbossas";

    private static final String DEP_1 = "jaxws-manual-pojo-1";

    private static final String DEP_2 = "jaxws-manual-pojo-2";

    @ArquillianResource
    ContainerController containerController;

    @ArquillianResource
    Deployer deployer;

    @Test
    public void testWsdlHostChanges() throws Exception {
        performWsdlHostAttributeTest(false);
        performWsdlHostAttributeTest(true);
    }

    @Test
    public void testWsdlPortChanges() throws Exception {
        performWsdlPortAttributeTest(false);
        performWsdlPortAttributeTest(true);
    }

    @Test
    public void testWsdlUriSchemeChanges() throws Exception {
        performWsdlUriSchemeAttributeTest(false);
        performWsdlUriSchemeAttributeTest(true);
    }

    @Test
    public void testWsdlPathRewriteRuleChanges() throws Exception {
        performWsdlPathRewriteRuleAttributeTest(false);
        performWsdlPathRewriteRuleAttributeTest(true);
    }
}

