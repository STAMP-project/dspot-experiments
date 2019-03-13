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
package com.cloudera.oryx.lambda.serving;


import com.cloudera.oryx.common.OryxTest;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.junit.Test;


public final class ServingLayerTest extends OryxTest {
    @Test
    public void testServingLayer() throws Exception {
        Map<String, Object> overlay = ServingLayerTest.buildOverlay();
        Config config = ConfigUtils.overlayOn(overlay, ConfigUtils.getDefault());
        ServingLayerTest.doTestServingLayer(config);
    }

    @Test
    public void testServingLayerSecure() throws Exception {
        Path keystoreFile = SecureAPIConfigIT.buildKeystoreFile();
        Map<String, Object> overlay = ServingLayerTest.buildOverlay();
        overlay.put("oryx.serving.api.keystore-file", (("\"" + keystoreFile) + "\""));
        overlay.put("oryx.serving.api.keystore-password", "oryxpass");
        overlay.put("oryx.serving.api.key-alias", "oryxtest");
        Config config = ConfigUtils.overlayOn(overlay, ConfigUtils.getDefault());
        try {
            ServingLayerTest.doTestServingLayer(config);
        } finally {
            Files.delete(Paths.get(config.getString("oryx.serving.api.keystore-file")));
        }
    }
}

