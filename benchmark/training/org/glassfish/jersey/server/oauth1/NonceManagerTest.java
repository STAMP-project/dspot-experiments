/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.server.oauth1;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Thomas Meire
 * @author Miroslav Fuksa
 */
public class NonceManagerTest {
    @Test
    public void testExpiredNonce() {
        NonceManager nonces = new NonceManager(1000, 50, TimeUnit.SECONDS, 2000000);
        boolean accepted = nonces.verify("old-nonce-key", stamp(2000), "old-nonce");
        Assert.assertFalse(accepted);
        long size = nonces.checkAndGetSize();
        Assert.assertEquals(0, size);
    }

    @Test
    public void testValidNonce() {
        NonceManager nonces = new NonceManager(1000, 50, TimeUnit.SECONDS, 2000000);
        boolean accepted = nonces.verify("nonce-key", stamp(), "nonce");
        Assert.assertTrue(accepted);
        long size = nonces.checkAndGetSize();
        Assert.assertEquals(1, size);
    }

    @Test
    public void testDuplicateNonce() {
        NonceManager nonces = new NonceManager(1000, 50, TimeUnit.SECONDS, 2000000);
        String stamp = stamp();
        boolean accepted;
        accepted = nonces.verify("nonce-key", stamp, "nonce");
        Assert.assertTrue(accepted);
        accepted = nonces.verify("nonce-key", stamp, "nonce");
        Assert.assertFalse(accepted);
    }

    @Test
    public void testAutoGC() {
        NonceManager nonces = new NonceManager(1000, 10, TimeUnit.SECONDS, 2000000);
        // verify nine
        for (int i = 0; i < 9; i++) {
            Assert.assertTrue(nonces.verify(("testing-" + i), stamp(), Integer.toString(i)));
        }
        Assert.assertEquals(9, nonces.checkAndGetSize());
        // invalid nonces don't trigger gc's
        Assert.assertFalse(nonces.verify("testing-9", stamp(2000), "9"));
        Assert.assertEquals(9, nonces.checkAndGetSize());
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Assert.fail("Can't guarantee we slept long enough...");
        }
        // 10th valid nonce triggers a gc on old tokens
        Assert.assertTrue(nonces.verify("testing-10", stamp(), "10"));
        Assert.assertEquals(1, nonces.checkAndGetSize());
    }

    @Test
    public void testManualGC() {
        NonceManager nonces = new NonceManager(1000, 5000, TimeUnit.SECONDS, 2000000);
        // insert 100 valid nonces
        for (int i = 0; i < 100; i++) {
            nonces.verify(("testing-" + i), stamp(), Integer.toString(i));
        }
        Assert.assertEquals(100, nonces.checkAndGetSize());
        // make sure the gc doesn't clean valid nonces
        nonces.gc(System.currentTimeMillis());
        Assert.assertEquals(100, nonces.checkAndGetSize());
        // sleep a while to invalidate the nonces
        try {
            Thread.sleep(1100);
        } catch (Exception e) {
            Assert.fail("Can't guarantee we slept long enough...");
        }
        // gc should remove all the nonces
        nonces.gc(System.currentTimeMillis());
        Assert.assertEquals(0, nonces.checkAndGetSize());
    }

    @Test
    public void testFutureTimeStamps() {
        NonceManager nonces = new NonceManager(10000, 5000, TimeUnit.SECONDS, 2000000);
        Assert.assertFalse(nonces.verify("a", stamp((-20000)), "1"));
        Assert.assertEquals(0, nonces.checkAndGetSize());
        Assert.assertFalse(nonces.verify("a", stamp((-15000)), "1"));
        Assert.assertEquals(0, nonces.checkAndGetSize());
        Assert.assertFalse(nonces.verify("a", stamp(15000), "1"));
        Assert.assertEquals(0, nonces.checkAndGetSize());
        final String stamp = stamp((-1000));
        Assert.assertTrue(nonces.verify("a", stamp, "1"));
        Assert.assertEquals(1, nonces.checkAndGetSize());
        Assert.assertFalse(nonces.verify("a", stamp, "1"));
        Assert.assertEquals(1, nonces.checkAndGetSize());
        Assert.assertTrue(nonces.verify("a", stamp((-2001)), "1"));
        Assert.assertEquals(2, nonces.checkAndGetSize());
        Assert.assertTrue(nonces.verify("a", stamp((-3001)), "1"));
        Assert.assertEquals(3, nonces.checkAndGetSize());
    }

    @Test
    public void testMaxCacheSize() {
        // initializa max cache size to 3
        NonceManager nonces = new NonceManager(1000, 5000, TimeUnit.MILLISECONDS, 3);
        Assert.assertTrue(nonces.verify("a", "1000", "1", 1000));
        Assert.assertEquals(1, nonces.checkAndGetSize());
        Assert.assertTrue(nonces.verify("a", "1050", "1", 1000));
        Assert.assertEquals(2, nonces.checkAndGetSize());
        Assert.assertTrue(nonces.verify("a", "1100", "1", 1000));
        Assert.assertEquals(3, nonces.checkAndGetSize());
        // this will not fit to the cache (cache is already full)
        Assert.assertFalse(nonces.verify("a", "500", "1", 1000));
        Assert.assertEquals(3, nonces.checkAndGetSize());
        // now time is 2100, so we clear the cache values lower than 1060
        Assert.assertTrue(nonces.verify("a", "2040", "1", 2060));
        Assert.assertEquals(2, nonces.checkAndGetSize());
    }

    @Test
    public void testUnits() {
        // initialize max cache size to 3
        NonceManager nonces = new NonceManager(240000, 5000, TimeUnit.MINUTES, 30);
        Assert.assertTrue(nonces.verify("a", "1", "1", 60000));
        Assert.assertEquals(1, nonces.checkAndGetSize());
        Assert.assertFalse(nonces.verify("a", "1", "1", 60001));
        Assert.assertEquals(1, nonces.checkAndGetSize());
        Assert.assertTrue(nonces.verify("a", "2", "1", 120002));
        Assert.assertEquals(2, nonces.checkAndGetSize());
        Assert.assertTrue(nonces.verify("a", "3", "1", 180003));
        Assert.assertEquals(3, nonces.checkAndGetSize());
        Assert.assertFalse(nonces.verify("a", "1", "1", 300000));
        Assert.assertEquals(3, nonces.checkAndGetSize());
    }
}

