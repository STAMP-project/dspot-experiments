/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
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


import com.cloudera.oryx.api.KeyMessage;
import com.cloudera.oryx.app.als.ALSUtilsTest;
import com.cloudera.oryx.app.pmml.AppPMMLUtils;
import com.cloudera.oryx.common.pmml.PMMLUtils;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.cloudera.oryx.common.text.TextUtils;
import com.cloudera.oryx.lambda.speed.AbstractSpeedIT;
import com.typesafe.config.Config;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class ALSSpeedIT extends AbstractSpeedIT {
    private static final Logger log = LoggerFactory.getLogger(ALSSpeedIT.class);

    @Test
    public void testALSSpeed() throws Exception {
        Map<String, Object> overlayConfig = new HashMap<>();
        overlayConfig.put("oryx.speed.model-manager-class", ALSSpeedModelManager.class.getName());
        overlayConfig.put("oryx.speed.streaming.generation-interval-sec", 5);
        overlayConfig.put("oryx.als.hyperparams.features", 2);
        Config config = ConfigUtils.overlayOn(overlayConfig, getConfig());
        startMessaging();
        List<KeyMessage<String, String>> updates = startServerProduceConsumeTopics(config, new MockALSInputGenerator(), new MockALSModelUpdateGenerator(), 10, 10);
        if (ALSSpeedIT.log.isDebugEnabled()) {
            updates.forEach(( update) -> log.debug("{}", update));
        }
        // 10 original updates. 9 generate just 1 update since user or item is new.
        assertEquals(19, updates.size());
        assertEquals("MODEL", updates.get(0).getKey());
        assertEquals(2, Integer.parseInt(AppPMMLUtils.getExtensionValue(PMMLUtils.fromString(updates.get(0).getMessage()), "features")));
        for (int i = 1; i <= 9; i++) {
            assertEquals("UP", updates.get(i).getKey());
            List<?> update = TextUtils.readJSON(updates.get(i).getMessage(), List.class);
            boolean isX = "X".equals(update.get(0).toString());
            String id = update.get(1).toString();
            float[] expected = (isX ? MockALSModelUpdateGenerator.X : MockALSModelUpdateGenerator.Y).get(id);
            assertArrayEquals(expected, TextUtils.convertViaJSON(update.get(2), float[].class));
            @SuppressWarnings("unchecked")
            Collection<String> knownUsersItems = ((Collection<String>) (update.get(3)));
            Collection<String> expectedKnownUsersItems = (isX ? MockALSModelUpdateGenerator.A : MockALSModelUpdateGenerator.At).get(id);
            assertContainsSame(knownUsersItems, expectedKnownUsersItems);
        }
        /* User 100 - 104 are solutions to eye(5)*Y*pinv(Y'*Y), but default scaling
        will produce values that are 3/4 of this since they are brand new.
        That is, it's really the solution to (0.75*eye(5))*Y*pinv(Y'*Y)
        Likewise 105 - 108 are (0.75*eye(4))*X*pinv(X'*X)
         */
        Map<String, float[]> X = MockALSModelUpdateGenerator.buildMatrix(100, new float[][]{ new float[]{ -0.20859924F, 0.25232133F }, new float[]{ -0.22472803F, -0.1929485F }, new float[]{ -0.15592135F, 0.3977631F }, new float[]{ -0.3006522F, -0.12239703F }, new float[]{ -0.09205295F, -0.37471837F } });
        Map<String, float[]> Y = MockALSModelUpdateGenerator.buildMatrix(105, new float[][]{ new float[]{ -0.19663288F, 0.09574106F }, new float[]{ -0.23840417F, -0.50850725F }, new float[]{ -0.34360975F, 0.2466687F }, new float[]{ -0.060204573F, 0.29311115F } });
        for (int i = 10; i <= 18; i++) {
            assertEquals("UP", updates.get(i).getKey());
            List<?> update = TextUtils.readJSON(updates.get(i).getMessage(), List.class);
            boolean isX = "X".equals(update.get(0).toString());
            String id = update.get(1).toString();
            float[] expected = (isX ? X : Y).get(id);
            assertArrayEquals(expected, TextUtils.convertViaJSON(update.get(2), float[].class), 1.0E-5F);
            String otherID = ALSUtilsTest.idToStringID(((ALSUtilsTest.stringIDtoID(id)) - 99));
            @SuppressWarnings("unchecked")
            Collection<String> knownUsersItems = ((Collection<String>) (update.get(3)));
            assertEquals(1, knownUsersItems.size());
            assertEquals(otherID, knownUsersItems.iterator().next());
        }
    }
}

