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
package org.apache.geode.internal;


import Version.CURRENT_ORDINAL;
import Version.GEODE_1_1_0;
import Version.GEODE_1_1_1;
import Version.GEODE_1_2_0;
import Version.GEODE_1_3_0;
import Version.GEODE_1_4_0;
import Version.GEODE_1_5_0;
import Version.GFE_61;
import Version.GFE_65;
import Version.GFE_66;
import Version.GFE_662;
import Version.GFE_6622;
import Version.GFE_70;
import Version.GFE_71;
import Version.GFE_80;
import Version.GFE_81;
import Version.GFE_82;
import org.apache.geode.cache.UnsupportedVersionException;
import org.apache.geode.internal.cache.tier.sockets.CommandInitializer;
import org.junit.Assert;
import org.junit.Test;


public class VersionJUnitTest {
    @Test
    public void testVersionClass() throws Exception {
        compare(GFE_662, GFE_66);
        compare(GFE_6622, GFE_662);
        compare(GFE_71, GFE_70);
        compare(GFE_80, GFE_70);
        compare(GFE_80, GFE_71);
        compare(GFE_81, GFE_70);
        compare(GFE_81, GFE_71);
        compare(GFE_81, GFE_80);
        compare(GFE_82, GFE_81);
        compare(GEODE_1_1_0, GFE_82);
        compare(GEODE_1_2_0, GEODE_1_1_1);
        compare(GEODE_1_3_0, GEODE_1_2_0);
        compare(GEODE_1_4_0, GEODE_1_3_0);
        compare(GEODE_1_5_0, GEODE_1_4_0);
    }

    @Test
    public void testIsPre65() {
        Assert.assertTrue(GFE_61.isPre65());
        Assert.assertFalse(GFE_65.isPre65());
        Assert.assertFalse(GFE_70.isPre65());
    }

    @Test
    public void testCommandMapContainsAllVersions() {
        for (Version version : Version.getAllVersions()) {
            Assert.assertNotNull((("Please add a commnd set for " + version) + " of Geode to CommandInitializer"), CommandInitializer.getCommands(version));
        }
    }

    @Test
    public void testFromOrdinalForCurrentVersionSucceeds() throws UnsupportedVersionException {
        Version.fromOrdinal(CURRENT_ORDINAL, true);
    }
}

