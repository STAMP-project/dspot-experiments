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


import org.apache.geode.internal.cache.versions.ConcurrentCacheModificationException;
import org.apache.geode.internal.cache.versions.VersionStamp;
import org.apache.geode.internal.cache.versions.VersionTag;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PowerMockIgnore("*.UnitTest")
@PrepareForTest({ SearchLoadAndWriteProcessor.class })
public class DistributedRegionSearchLoadJUnitTest {
    @Test
    public void testClientEventIsUpdatedWithCurrentEntryVersionTagAfterLoad() {
        DistributedRegion region = prepare(true);
        EntryEventImpl event = createDummyEvent(region);
        region.basicInvalidate(event);
        createSearchLoad();
        KeyInfo ki = new KeyInfo(event.getKey(), null, null);
        region.findObjectInSystem(ki, false, null, false, null, false, false, null, event, false);
        Assert.assertNotNull("ClientEvent version tag is not set with region version tag.", event.getVersionTag());
    }

    @Test
    public void testClientEventIsUpdatedWithCurrentEntryVersionTagAfterSearchConcurrencyException() {
        DistributedRegion region = prepare(true);
        EntryEventImpl event = createDummyEvent(region);
        region.basicInvalidate(event);
        VersionTag tag = createVersionTag(true);
        RegionEntry re = Mockito.mock(RegionEntry.class);
        VersionStamp stamp = Mockito.mock(VersionStamp.class);
        Mockito.doReturn(re).when(region).getRegionEntry(ArgumentMatchers.any());
        Mockito.when(re.getVersionStamp()).thenReturn(stamp);
        Mockito.when(stamp.asVersionTag()).thenReturn(tag);
        createSearchLoad();
        Mockito.doThrow(new ConcurrentCacheModificationException()).when(region).basicPutEntry(ArgumentMatchers.any(EntryEventImpl.class), ArgumentMatchers.anyLong());
        KeyInfo ki = new KeyInfo(event.getKey(), null, null);
        region.findObjectInSystem(ki, false, null, false, null, false, false, null, event, false);
        Assert.assertNotNull("ClientEvent version tag is not set with region version tag.", event.getVersionTag());
    }
}

