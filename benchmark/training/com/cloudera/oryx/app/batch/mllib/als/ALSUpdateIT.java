/**
 * Copyright (c) 2014, Cloudera and Intel, Inc. All Rights Reserved.
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
package com.cloudera.oryx.app.batch.mllib.als;


import MLUpdate.MODEL_FILE_NAME;
import com.cloudera.oryx.api.KeyMessage;
import com.cloudera.oryx.app.als.ALSUtilsTest;
import com.cloudera.oryx.app.batch.mllib.AbstractAppMLlibIT;
import com.cloudera.oryx.app.pmml.AppPMMLUtils;
import com.cloudera.oryx.common.io.IOUtils;
import com.cloudera.oryx.common.pmml.PMMLUtils;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.cloudera.oryx.common.text.TextUtils;
import com.typesafe.config.Config;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.dmg.pmml.PMML;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class ALSUpdateIT extends AbstractALSIT {
    private static final Logger log = LoggerFactory.getLogger(ALSUpdateIT.class);

    private static final int DATA_TO_WRITE = 2000;

    private static final int WRITE_INTERVAL_MSEC = 10;

    private static final int FEATURES = 4;

    private static final double LAMBDA = 0.001;

    private static final int NUM_USERS_ITEMS = 1000;

    @Test
    public void testALS() throws Exception {
        Path tempDir = getTempDir();
        Path dataDir = tempDir.resolve("data");
        Path modelDir = tempDir.resolve("model");
        Map<String, Object> overlayConfig = new HashMap<>();
        overlayConfig.put("oryx.batch.update-class", ALSUpdate.class.getName());
        ConfigUtils.set(overlayConfig, "oryx.batch.storage.data-dir", dataDir);
        ConfigUtils.set(overlayConfig, "oryx.batch.storage.model-dir", modelDir);
        overlayConfig.put("oryx.batch.streaming.generation-interval-sec", AbstractAppMLlibIT.GEN_INTERVAL_SEC);
        overlayConfig.put("oryx.als.implicit", false);
        overlayConfig.put("oryx.als.hyperparams.lambda", ALSUpdateIT.LAMBDA);
        overlayConfig.put("oryx.als.hyperparams.features", ALSUpdateIT.FEATURES);
        Config config = ConfigUtils.overlayOn(overlayConfig, getConfig());
        startMessaging();
        List<KeyMessage<String, String>> updates = startServerProduceConsumeTopics(config, new RandomALSDataGenerator(ALSUpdateIT.NUM_USERS_ITEMS, ALSUpdateIT.NUM_USERS_ITEMS, 1, 5), ALSUpdateIT.DATA_TO_WRITE, ALSUpdateIT.WRITE_INTERVAL_MSEC);
        List<Path> modelInstanceDirs = IOUtils.listFiles(modelDir, "*");
        int generations = modelInstanceDirs.size();
        checkIntervals(generations, ALSUpdateIT.DATA_TO_WRITE, ALSUpdateIT.WRITE_INTERVAL_MSEC, AbstractAppMLlibIT.GEN_INTERVAL_SEC);
        List<Collection<String>> userIDs = new ArrayList<>();
        userIDs.add(Collections.emptySet());// Add dummy empty set as prior value

        List<Collection<String>> productIDs = new ArrayList<>();
        productIDs.add(Collections.emptySet());// Add dummy empty set as prior value

        for (Path modelInstanceDir : modelInstanceDirs) {
            Path modelFile = modelInstanceDir.resolve(MODEL_FILE_NAME);
            assertNonEmpty(modelFile);
            PMMLUtils.read(modelFile);// Shouldn't throw exception

            Path xDir = modelInstanceDir.resolve("X");
            assertTrue(Files.exists(xDir));
            userIDs.add(ALSUpdateIT.checkFeatures(xDir, userIDs.get(((userIDs.size()) - 1))));
            Path yDir = modelInstanceDir.resolve("Y");
            assertTrue(Files.exists(yDir));
            productIDs.add(ALSUpdateIT.checkFeatures(yDir, productIDs.get(((productIDs.size()) - 1))));
        }
        // Remove dummy empty sets
        userIDs.remove(0);
        productIDs.remove(0);
        Collection<String> expectedUsers = null;
        Collection<String> expectedProducts = null;
        Collection<String> seenUsers = null;
        Collection<String> seenProducts = null;
        Collection<String> lastModelUsers = null;
        Collection<String> lastModelProducts = null;
        int whichGeneration = -1;
        for (KeyMessage<String, String> km : updates) {
            String type = km.getKey();
            String value = km.getMessage();
            ALSUpdateIT.log.debug("{} = {}", type, value);
            if ("UP".equals(type)) {
                assertNotNull(seenUsers);
                assertNotNull(seenProducts);
                List<?> update = TextUtils.readJSON(value, List.class);
                // First field is X or Y, depending on whether it's a user or item vector
                String whichMatrixField = update.get(0).toString();
                boolean isUser = "X".equals(whichMatrixField);
                boolean isProduct = "Y".equals(whichMatrixField);
                // Next is user/item ID
                String id = update.get(1).toString();
                assertTrue((isUser || isProduct));
                if (isUser) {
                    seenUsers.add(id);
                } else {
                    seenProducts.add(id);
                }
                // Verify that feature vector are valid floats
                for (float f : TextUtils.convertViaJSON(update.get(2), float[].class)) {
                    assertTrue(((!(Float.isNaN(f))) && (!(Float.isInfinite(f)))));
                }
                if (isUser) {
                    // Only known-items for users exist now, not known users for items
                    @SuppressWarnings("unchecked")
                    Collection<String> knownUsersItems = ((Collection<String>) (update.get(3)));
                    assertNotEquals(0, knownUsersItems.size());
                    for (String known : knownUsersItems) {
                        int i = ALSUtilsTest.stringIDtoID(known);
                        assertElementIndex(i, ALSUpdateIT.NUM_USERS_ITEMS);
                    }
                }
            } else {
                assertContains(Arrays.asList("MODEL", "MODEL-REF"), type);
                PMML pmml = AppPMMLUtils.readPMMLFromUpdateKeyMessage(type, value, null);
                assertNotNull(pmml);
                AbstractAppMLlibIT.checkHeader(pmml.getHeader());
                assertEquals(8, pmml.getExtensions().size());
                Map<String, Object> expected = new HashMap<>();
                expected.put("features", ALSUpdateIT.FEATURES);
                expected.put("lambda", ALSUpdateIT.LAMBDA);
                expected.put("implicit", false);
                expected.put("logStrength", false);
                AbstractAppMLlibIT.checkExtensions(pmml, expected);
                // See if users/item sets seen in updates match what was expected from output
                assertContainsSame(expectedUsers, seenUsers);
                assertContainsSame(expectedProducts, seenProducts);
                // Also check key sets reported in model
                assertContainsSame(expectedUsers, lastModelUsers);
                assertContainsSame(expectedProducts, lastModelProducts);
                // Update for next round
                whichGeneration++;
                expectedUsers = userIDs.get(whichGeneration);
                expectedProducts = productIDs.get(whichGeneration);
                seenUsers = new HashSet<>();
                seenProducts = new HashSet<>();
                lastModelUsers = AppPMMLUtils.getExtensionContent(pmml, "XIDs");
                lastModelProducts = AppPMMLUtils.getExtensionContent(pmml, "YIDs");
            }
        }
    }
}

