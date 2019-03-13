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
package com.cloudera.oryx.app.serving.als.model;


import MockALSModelUpdateGenerator.A;
import MockALSModelUpdateGenerator.X;
import MockALSModelUpdateGenerator.Y;
import OryxResource.MODEL_MANAGER_KEY;
import com.cloudera.oryx.app.speed.als.MockALSModelUpdateGenerator;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.cloudera.oryx.lambda.serving.AbstractServingIT;
import com.typesafe.config.Config;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class ALSServingModelManagerIT extends AbstractServingIT {
    private static final Logger log = LoggerFactory.getLogger(ALSServingModelManagerIT.class);

    @Test
    public void testALSServingModel() throws Exception {
        Map<String, Object> overlayConfig = new HashMap<>();
        overlayConfig.put("oryx.serving.application-resources", "\"com.cloudera.oryx.app.serving,com.cloudera.oryx.app.serving.als\"");
        overlayConfig.put("oryx.serving.model-manager-class", ALSServingModelManager.class.getName());
        Config config = ConfigUtils.overlayOn(overlayConfig, getConfig());
        startMessaging();
        startServer(config);
        startUpdateTopics(new MockALSModelUpdateGenerator(), 10);
        // Let updates finish
        sleepSeconds(5);
        ALSServingModelManager manager = ((ALSServingModelManager) (getServingLayer().getContext().getServletContext().getAttribute(MODEL_MANAGER_KEY)));
        assertNotNull("Manager must initialize in web context", manager);
        ALSServingModel model = manager.getModel();
        ALSServingModelManagerIT.log.debug("{}", model);
        assertNotNull(model);
        assertEquals(2, model.getFeatures());
        assertTrue(model.isImplicit());
        assertContainsSame(Y.keySet(), model.getAllItemIDs());
        assertNotNull(model.getYTYSolver());
        X.forEach(( id, vector) -> assertArrayEquals(vector, model.getUserVector(id)));
        Y.forEach(( id, vector) -> assertArrayEquals(vector, model.getItemVector(id)));
        A.forEach(( id, expected) -> assertContainsSame(expected, model.getKnownItems(id)));
    }
}

