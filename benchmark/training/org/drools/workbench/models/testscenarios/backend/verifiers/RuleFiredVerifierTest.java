/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.testscenarios.backend.verifiers;


import java.util.HashMap;
import java.util.Map;
import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;
import org.junit.Assert;
import org.junit.Test;


public class RuleFiredVerifierTest {
    @Test
    public void testCountVerification() throws Exception {
        Map<String, Integer> firingCounts = new HashMap<String, Integer>();
        firingCounts.put("foo", 2);
        firingCounts.put("bar", 1);
        // and baz, we leave out
        RuleFiredVerifier ruleFiredVerifier = new RuleFiredVerifier();
        ruleFiredVerifier.setFireCounter(firingCounts);
        VerifyRuleFired v = new VerifyRuleFired();
        v.setRuleName("foo");
        v.setExpectedFire(true);
        ruleFiredVerifier.verifyFiringCounts(v);
        Assert.assertTrue(v.getSuccessResult());
        Assert.assertEquals(2, v.getActualResult().intValue());
        v = new VerifyRuleFired();
        v.setRuleName("foo");
        v.setExpectedFire(false);
        ruleFiredVerifier.verifyFiringCounts(v);
        Assert.assertFalse(v.getSuccessResult());
        Assert.assertEquals(2, v.getActualResult().intValue());
        Assert.assertNotNull(v.getExplanation());
        v = new VerifyRuleFired();
        v.setRuleName("foo");
        v.setExpectedCount(2);
        ruleFiredVerifier.verifyFiringCounts(v);
        Assert.assertTrue(v.getSuccessResult());
        Assert.assertEquals(2, v.getActualResult().intValue());
    }

    @Test
    public void testRuleFiredWithEnum() throws Exception {
        Map<String, Integer> firingCounts = new HashMap<String, Integer>();
        firingCounts.put("foo", 2);
        firingCounts.put("bar", 1);
        // and baz, we leave out
        RuleFiredVerifier ruleFiredVerifier = new RuleFiredVerifier();
        ruleFiredVerifier.setFireCounter(firingCounts);
        VerifyRuleFired v = new VerifyRuleFired();
        v.setRuleName("foo");
        v.setExpectedFire(true);
        ruleFiredVerifier.verifyFiringCounts(v);
        Assert.assertTrue(v.getSuccessResult());
        Assert.assertEquals(2, v.getActualResult().intValue());
    }

    @Test
    public void testVerifyRuleFired() throws Exception {
        RuleFiredVerifier ruleFiredVerifier = new RuleFiredVerifier();
        VerifyRuleFired vr = new VerifyRuleFired("qqq", 42, null);
        Map<String, Integer> f = new HashMap<String, Integer>();
        f.put("qqq", 42);
        f.put("qaz", 1);
        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        Assert.assertTrue(vr.wasSuccessful());
        Assert.assertEquals(42, vr.getActualResult().intValue());
        vr = new VerifyRuleFired("qqq", 41, null);
        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        Assert.assertFalse(vr.wasSuccessful());
        Assert.assertEquals(42, vr.getActualResult().intValue());
        vr = new VerifyRuleFired("qaz", 1, null);
        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        Assert.assertTrue(vr.wasSuccessful());
        Assert.assertEquals(1, vr.getActualResult().intValue());
        vr = new VerifyRuleFired("XXX", null, false);
        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        Assert.assertTrue(vr.wasSuccessful());
        Assert.assertEquals(0, vr.getActualResult().intValue());
        vr = new VerifyRuleFired("qqq", null, true);
        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        Assert.assertTrue(vr.wasSuccessful());
        Assert.assertEquals(42, vr.getActualResult().intValue());
        vr = new VerifyRuleFired("qqq", null, false);
        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        Assert.assertFalse(vr.wasSuccessful());
        Assert.assertEquals(42, vr.getActualResult().intValue());
    }
}

