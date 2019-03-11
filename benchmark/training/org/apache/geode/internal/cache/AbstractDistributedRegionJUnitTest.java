/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import org.apache.geode.internal.cache.versions.VersionTag;
import org.apache.geode.internal.logging.LogService;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractDistributedRegionJUnitTest {
    protected static final Logger logger = LogService.getLogger();

    @Test
    public void testConcurrencyFalseTagNull() {
        // case 1: concurrencyCheckEanbled = false, version tag is null: distribute
        DistributedRegion region = prepare(false, false);
        EntryEventImpl event = createDummyEvent(region);
        Assert.assertNull(event.getVersionTag());
        doTest(region, event, 1);
    }

    @Test
    public void testConcurrencyTrueTagNull() {
        // case 2: concurrencyCheckEanbled = true, version tag is null: not to distribute
        DistributedRegion region = prepare(true, false);
        EntryEventImpl event = createDummyEvent(region);
        Assert.assertNull(event.getVersionTag());
        doTest(region, event, 0);
    }

    @Test
    public void testConcurrencyTrueTagInvalid() {
        // case 3: concurrencyCheckEanbled = true, version tag is invalid: not to distribute
        DistributedRegion region = prepare(true, false);
        EntryEventImpl event = createDummyEvent(region);
        VersionTag tag = createVersionTag(false);
        event.setVersionTag(tag);
        Assert.assertFalse(tag.hasValidVersion());
        doTest(region, event, 0);
    }

    @Test
    public void testConcurrencyTrueTagValid() {
        // case 4: concurrencyCheckEanbled = true, version tag is valid: distribute
        DistributedRegion region = prepare(true, false);
        EntryEventImpl event = createDummyEvent(region);
        VersionTag tag = createVersionTag(true);
        event.setVersionTag(tag);
        Assert.assertTrue(tag.hasValidVersion());
        doTest(region, event, 1);
    }
}

