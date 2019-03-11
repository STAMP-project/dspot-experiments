/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.deployment.deploymentoverlay.jar;


import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.deployment.deploymentoverlay.AbstractOverlayTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author baranowb
 */
@RunWith(Arquillian.class)
@RunAsClient
public class OverlayExistingResourceTestCase extends JarOverlayTestBase {
    private static final String OVERLAY = "HAL9000";

    private static final String DEPLOYMENT_OVERLAYED = "overlayed";

    private static final String DEPLOYMENT_OVERLAYED_ARCHIVE = (OverlayExistingResourceTestCase.DEPLOYMENT_OVERLAYED) + ".jar";

    @Test
    public void testOverlay() throws Exception {
        final InitialContext ctx = JarOverlayTestBase.getInitialContext();
        try {
            OverlayableInterface iface = ((OverlayableInterface) (ctx.lookup(JarOverlayTestBase.getEjbBinding("", OverlayExistingResourceTestCase.DEPLOYMENT_OVERLAYED, "", OverlayEJB.class, OverlayableInterface.class))));
            Assert.assertEquals("Overlayed resource does not match pre-overlay expectations!", OverlayableInterface.ORIGINAL, iface.fetchResource());
            Assert.assertEquals("Static resource does not match pre-overlay expectations!", OverlayableInterface.STATIC, iface.fetchResourceStatic());
            setupOverlay(OverlayExistingResourceTestCase.DEPLOYMENT_OVERLAYED_ARCHIVE, OverlayExistingResourceTestCase.OVERLAY, OverlayableInterface.RESOURCE, OverlayableInterface.OVERLAYED);
            Assert.assertEquals("Overlayed resource does not match post-overlay expectations!", OverlayableInterface.OVERLAYED, iface.fetchResource());
            Assert.assertEquals("Static resource does not match post-overlay expectations!", OverlayableInterface.STATIC, iface.fetchResourceStatic());
        } finally {
            try {
                ctx.close();
            } catch (Exception e) {
                AbstractOverlayTestBase.LOGGER.error("Closing context failed", e);
            }
            try {
                removeOverlay(OverlayExistingResourceTestCase.DEPLOYMENT_OVERLAYED_ARCHIVE, OverlayExistingResourceTestCase.OVERLAY, OverlayableInterface.RESOURCE);
            } catch (Exception e) {
                AbstractOverlayTestBase.LOGGER.error("Removing overlay failed", e);
            }
        }
    }
}

