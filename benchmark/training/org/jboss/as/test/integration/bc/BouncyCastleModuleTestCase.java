/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.integration.bc;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Verify that BouncyCastle securtiy provider can be loaded and used through JCE api.
 * Basically, this can fail when security provider class isn't in properly signed jar with signature accepted by used JDK.
 * See https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/HowToImplAProvider.html#Step1a for details.
 */
@RunWith(Arquillian.class)
public class BouncyCastleModuleTestCase {
    private static final String BC_DEPLOYMENT = "bc-test";

    private static final Logger logger = Logger.getLogger(BouncyCastleModuleTestCase.class);

    @Test
    public void testBouncyCastleProviderIsUsableThroughJceApi() throws Exception {
        BouncyCastleProvider bcProvider = null;
        try {
            bcProvider = new BouncyCastleProvider();
            BouncyCastleModuleTestCase.useBouncyCastleProviderThroughJceApi(bcProvider);
        } catch (Exception e) {
            if ((e instanceof SecurityException) && (e.getMessage().contains("JCE cannot authenticate the provider"))) {
                String bcLocation = (bcProvider == null) ? "" : ("(" + (bcProvider.getClass().getResource("/"))) + ")";
                throw new Exception((("Packaging with BouncyCastleProvider" + bcLocation) + " is probably not properly signed for JCE usage, see server log for details."), e);
            } else {
                throw e;
            }
        }
    }
}

