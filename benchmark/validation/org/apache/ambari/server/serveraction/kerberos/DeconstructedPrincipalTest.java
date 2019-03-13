/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.serveraction.kerberos;


import org.junit.Assert;
import org.junit.Test;


public class DeconstructedPrincipalTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNullPrincipal() throws Exception {
        DeconstructedPrincipal.valueOf(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPrincipal() throws Exception {
        DeconstructedPrincipal.valueOf("", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPrincipal() throws Exception {
        DeconstructedPrincipal.valueOf("/invalid", null);
    }

    @Test
    public void testPrimary() throws Exception {
        DeconstructedPrincipal deconstructedPrincipal = DeconstructedPrincipal.valueOf("primary", "REALM");
        Assert.assertNotNull(deconstructedPrincipal);
        Assert.assertEquals("primary", deconstructedPrincipal.getPrimary());
        Assert.assertNull(deconstructedPrincipal.getInstance());
        Assert.assertEquals("REALM", deconstructedPrincipal.getRealm());
        Assert.assertEquals("primary", deconstructedPrincipal.getPrincipalName());
        Assert.assertEquals("primary@REALM", deconstructedPrincipal.getNormalizedPrincipal());
    }

    @Test
    public void testPrimaryRealm() throws Exception {
        DeconstructedPrincipal deconstructedPrincipal = DeconstructedPrincipal.valueOf("primary@MYREALM", "REALM");
        Assert.assertNotNull(deconstructedPrincipal);
        Assert.assertEquals("primary", deconstructedPrincipal.getPrimary());
        Assert.assertNull(deconstructedPrincipal.getInstance());
        Assert.assertEquals("MYREALM", deconstructedPrincipal.getRealm());
        Assert.assertEquals("primary", deconstructedPrincipal.getPrincipalName());
        Assert.assertEquals("primary@MYREALM", deconstructedPrincipal.getNormalizedPrincipal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstance() throws Exception {
        DeconstructedPrincipal.valueOf("/instance", "REALM");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstanceRealm() throws Exception {
        DeconstructedPrincipal.valueOf("/instance@MYREALM", "REALM");
    }

    @Test
    public void testPrimaryInstance() throws Exception {
        DeconstructedPrincipal deconstructedPrincipal = DeconstructedPrincipal.valueOf("primary/instance", "REALM");
        Assert.assertNotNull(deconstructedPrincipal);
        Assert.assertEquals("primary", deconstructedPrincipal.getPrimary());
        Assert.assertEquals("instance", deconstructedPrincipal.getInstance());
        Assert.assertEquals("instance", deconstructedPrincipal.getInstance());
        Assert.assertEquals("REALM", deconstructedPrincipal.getRealm());
        Assert.assertEquals("primary/instance", deconstructedPrincipal.getPrincipalName());
        Assert.assertEquals("primary/instance@REALM", deconstructedPrincipal.getNormalizedPrincipal());
    }

    @Test
    public void testPrimaryInstanceRealm() throws Exception {
        DeconstructedPrincipal deconstructedPrincipal = DeconstructedPrincipal.valueOf("primary/instance@MYREALM", "REALM");
        Assert.assertNotNull(deconstructedPrincipal);
        Assert.assertEquals("primary", deconstructedPrincipal.getPrimary());
        Assert.assertEquals("instance", deconstructedPrincipal.getInstance());
        Assert.assertEquals("MYREALM", deconstructedPrincipal.getRealm());
        Assert.assertEquals("primary/instance", deconstructedPrincipal.getPrincipalName());
        Assert.assertEquals("primary/instance@MYREALM", deconstructedPrincipal.getNormalizedPrincipal());
    }

    @Test
    public void testOddCharacters() throws Exception {
        DeconstructedPrincipal deconstructedPrincipal = DeconstructedPrincipal.valueOf("p_ri.ma-ry/i.n_s-tance@M_Y-REALM.COM", "REALM");
        Assert.assertNotNull(deconstructedPrincipal);
        Assert.assertEquals("p_ri.ma-ry", deconstructedPrincipal.getPrimary());
        Assert.assertEquals("i.n_s-tance", deconstructedPrincipal.getInstance());
        Assert.assertEquals("M_Y-REALM.COM", deconstructedPrincipal.getRealm());
        Assert.assertEquals("p_ri.ma-ry/i.n_s-tance", deconstructedPrincipal.getPrincipalName());
        Assert.assertEquals("p_ri.ma-ry/i.n_s-tance@M_Y-REALM.COM", deconstructedPrincipal.getNormalizedPrincipal());
    }
}

