/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtable.backend;


import GuidedDecisionTable52.HitPolicy.FIRST_HIT;
import GuidedDecisionTable52.HitPolicy.NONE;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.Assert;
import org.junit.Test;


public class HitPolicyPersistenceTest {
    @Test
    public void testDefault() throws Exception {
        Assert.assertEquals(NONE, new GuidedDecisionTable52().getHitPolicy());
    }

    @Test
    public void testRoundTrip() {
        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setHitPolicy(FIRST_HIT);
        final String xml = GuidedDTXMLPersistence.getInstance().marshal(dt);
        Assert.assertNotNull(xml);
        Assert.assertTrue(xml.contains("<hitPolicy>FIRST_HIT</hitPolicy>"));
        final GuidedDecisionTable52 dt_ = GuidedDTXMLPersistence.getInstance().unmarshal(xml);
        Assert.assertEquals(FIRST_HIT, dt_.getHitPolicy());
    }

    @Test
    public void testBackwardsCompatibility() throws Exception {
        final String xml = TestUtil.loadResource("ExistingDecisionTable.xml");
        final GuidedDecisionTable52 dt_ = GuidedDTXMLPersistence.getInstance().unmarshal(xml);
        Assert.assertNotNull(dt_);
        Assert.assertEquals(NONE, dt_.getHitPolicy());
    }
}

