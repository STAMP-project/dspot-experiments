/**
 * Copyright (c) 2015, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.speed.als;


import com.cloudera.oryx.common.OryxTest;
import java.util.Collections;
import org.junit.Test;


public final class ALSSpeedModelTest extends OryxTest {
    @Test
    public void testUserItemVector() {
        ALSSpeedModel model = new ALSSpeedModel(2, true, false, Double.NaN);
        assertEquals(2, model.getFeatures());
        model.setUserVector("U1", new float[]{ 1.5F, -2.5F });
        assertArrayEquals(new float[]{ 1.5F, -2.5F }, model.getUserVector("U1"));
        model.setItemVector("I0", new float[]{ 0.5F, 0.0F });
        assertArrayEquals(new float[]{ 0.5F, 0.0F }, model.getItemVector("I0"));
    }

    @Test
    public void testToString() {
        String modelToString = new ALSSpeedModel(2, true, true, 0.01).toString();
        assertContains(modelToString, "ALSSpeedModel");
        assertContains(modelToString, "features:2");
        assertContains(modelToString, "implicit:true");
        assertContains(modelToString, "logStrength:true");
        assertContains(modelToString, "epsilon:0.01");
    }

    @Test
    public void testFractionLoaded() {
        ALSSpeedModel model = new ALSSpeedModel(2, true, false, Double.NaN);
        assertEquals(1.0F, model.getFractionLoaded());
        model.retainRecentAndUserIDs(Collections.singleton("U1"));
        model.retainRecentAndItemIDs(Collections.singleton("I0"));
        assertEquals(0.0F, model.getFractionLoaded());
        model.setUserVector("U1", new float[]{ 1.5F, -2.5F });
        assertEquals(0.5F, model.getFractionLoaded());
        model.setItemVector("I0", new float[]{ 0.5F, 0.0F });
        assertEquals(1.0F, model.getFractionLoaded());
    }
}

