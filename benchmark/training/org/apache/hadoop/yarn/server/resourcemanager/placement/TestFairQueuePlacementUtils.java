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
package org.apache.hadoop.yarn.server.resourcemanager.placement;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests of the utility methods from {@link FairQueuePlacementUtils}.
 */
public class TestFairQueuePlacementUtils {
    /**
     * Test name trimming and dot replacement in names.
     */
    @Test
    public void testCleanName() {
        // permutations of dot placements
        final String clean = "clean";
        final String dotted = "not.clean";
        final String multiDot = "more.un.clean";
        final String seqDot = "not..clean";
        final String unTrimmed = " .invalid. ";// not really a valid queue

        String cleaned = FairQueuePlacementUtils.cleanName(clean);
        Assert.assertEquals("Name was changed and it should not", clean, cleaned);
        cleaned = FairQueuePlacementUtils.cleanName(dotted);
        Assert.assertFalse("Cleaned name contains dots and it should not", cleaned.contains(FairQueuePlacementUtils.DOT));
        cleaned = FairQueuePlacementUtils.cleanName(multiDot);
        Assert.assertFalse("Cleaned name contains dots and it should not", cleaned.contains(FairQueuePlacementUtils.DOT));
        Assert.assertNotEquals("Multi dot failed: wrong replacements found", cleaned.indexOf(FairQueuePlacementUtils.DOT_REPLACEMENT), cleaned.lastIndexOf(FairQueuePlacementUtils.DOT_REPLACEMENT));
        cleaned = FairQueuePlacementUtils.cleanName(seqDot);
        Assert.assertFalse("Cleaned name contains dots and it should not", cleaned.contains(FairQueuePlacementUtils.DOT));
        Assert.assertNotEquals("Sequential dot failed: wrong replacements found", cleaned.indexOf(FairQueuePlacementUtils.DOT_REPLACEMENT), cleaned.lastIndexOf(FairQueuePlacementUtils.DOT_REPLACEMENT));
        cleaned = FairQueuePlacementUtils.cleanName(unTrimmed);
        Assert.assertTrue("Trimming start failed: space not removed or dot not replaced", cleaned.startsWith(FairQueuePlacementUtils.DOT_REPLACEMENT));
        Assert.assertTrue("Trimming end failed: space not removed or dot not replaced", cleaned.endsWith(FairQueuePlacementUtils.DOT_REPLACEMENT));
    }

    @Test
    public void testAssureRoot() {
        // permutations of rooted queue names
        final String queueName = "base";
        final String rootOnly = "root";
        final String rootNoDot = "rootbase";
        final String alreadyRoot = "root.base";
        String rooted = FairQueuePlacementUtils.assureRoot(queueName);
        Assert.assertTrue("Queue should have root prefix (base)", rooted.startsWith(((FairQueuePlacementUtils.ROOT_QUEUE) + (FairQueuePlacementUtils.DOT))));
        rooted = FairQueuePlacementUtils.assureRoot(rootOnly);
        Assert.assertEquals("'root' queue should not have root prefix (root)", rootOnly, rooted);
        rooted = FairQueuePlacementUtils.assureRoot(rootNoDot);
        Assert.assertTrue("Queue should have root prefix (rootbase)", rooted.startsWith(((FairQueuePlacementUtils.ROOT_QUEUE) + (FairQueuePlacementUtils.DOT))));
        Assert.assertEquals("'root' queue base was replaced and not prefixed", 5, rooted.lastIndexOf(FairQueuePlacementUtils.ROOT_QUEUE));
        rooted = FairQueuePlacementUtils.assureRoot(alreadyRoot);
        Assert.assertEquals("Root prefixed queue changed and it should not (root.base)", rooted, alreadyRoot);
        Assert.assertNull("Null queue did not return null queue", FairQueuePlacementUtils.assureRoot(null));
        Assert.assertEquals("Empty queue did not return empty name", "", FairQueuePlacementUtils.assureRoot(""));
    }

    @Test
    public void testIsValidQueueName() {
        // permutations of valid/invalid names
        final String valid = "valid";
        final String validRooted = "root.valid";
        final String rootOnly = "root";
        final String startDot = ".invalid";
        final String endDot = "invalid.";
        final String startSpace = " invalid";
        final String endSpace = "invalid ";
        final String unicodeSpace = "\u00a0invalid";
        Assert.assertFalse("'null' queue was not marked as invalid", FairQueuePlacementUtils.isValidQueueName(null));
        Assert.assertTrue("empty queue was not tagged valid", FairQueuePlacementUtils.isValidQueueName(""));
        Assert.assertTrue("Simple queue name was not tagged valid (valid)", FairQueuePlacementUtils.isValidQueueName(valid));
        Assert.assertTrue("Root only queue was not tagged valid (root)", FairQueuePlacementUtils.isValidQueueName(rootOnly));
        Assert.assertTrue("Root prefixed queue was not tagged valid (root.valid)", FairQueuePlacementUtils.isValidQueueName(validRooted));
        Assert.assertFalse("Queue starting with dot was not tagged invalid (.invalid)", FairQueuePlacementUtils.isValidQueueName(startDot));
        Assert.assertFalse("Queue ending with dot was not tagged invalid (invalid.)", FairQueuePlacementUtils.isValidQueueName(endDot));
        Assert.assertFalse("Queue starting with space was not tagged invalid ( invalid)", FairQueuePlacementUtils.isValidQueueName(startSpace));
        Assert.assertFalse("Queue ending with space was not tagged invalid (invalid )", FairQueuePlacementUtils.isValidQueueName(endSpace));
        // just one for sanity check extensive tests are in the scheduler utils
        Assert.assertFalse("Queue with unicode space was not tagged as invalid (unicode)", FairQueuePlacementUtils.isValidQueueName(unicodeSpace));
    }
}

