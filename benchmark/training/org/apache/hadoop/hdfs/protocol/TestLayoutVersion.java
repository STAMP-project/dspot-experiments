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
package org.apache.hadoop.hdfs.protocol;


import Feature.FSIMAGE_NAME_OPTIMIZATION;
import Feature.RESERVED_REL1_2_0;
import Feature.RESERVED_REL20_203;
import Feature.RESERVED_REL20_204;
import Feature.SNAPSHOT;
import LayoutVersion.Feature.CONCAT;
import LayoutVersion.Feature.DELEGATION_TOKEN;
import LayoutVersion.Feature.RESERVED_REL2_4_0;
import NameNodeLayoutVersion.Feature.APPEND_NEW_BLOCK;
import NameNodeLayoutVersion.Feature.ERASURE_CODING;
import NameNodeLayoutVersion.Feature.EXPANDED_STRING_TABLE;
import NameNodeLayoutVersion.Feature.QUOTA_BY_STORAGE_TYPE;
import NameNodeLayoutVersion.Feature.TRUNCATE;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import org.apache.hadoop.hdfs.protocol.LayoutVersion.Feature;
import org.apache.hadoop.hdfs.protocol.LayoutVersion.FeatureInfo;
import org.apache.hadoop.hdfs.protocol.LayoutVersion.LayoutFeature;
import org.apache.hadoop.hdfs.server.datanode.DataNodeLayoutVersion;
import org.apache.hadoop.hdfs.server.namenode.NameNodeLayoutVersion;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test for {@link LayoutVersion}
 */
public class TestLayoutVersion {
    public static final LayoutFeature LAST_NON_RESERVED_COMMON_FEATURE;

    public static final LayoutFeature LAST_COMMON_FEATURE;

    static {
        final Feature[] features = Feature.values();
        LAST_COMMON_FEATURE = features[((features.length) - 1)];
        LAST_NON_RESERVED_COMMON_FEATURE = LayoutVersion.getLastNonReservedFeature(features);
    }

    /**
     * Tests to make sure a given layout version supports all the
     * features from the ancestor
     */
    @Test
    public void testFeaturesFromAncestorSupported() {
        for (LayoutFeature f : Feature.values()) {
            validateFeatureList(f);
        }
    }

    /**
     * Test to make sure 0.20.203 supports delegation token
     */
    @Test
    public void testRelease203() {
        Assert.assertTrue(NameNodeLayoutVersion.supports(DELEGATION_TOKEN, RESERVED_REL20_203.getInfo().getLayoutVersion()));
    }

    /**
     * Test to make sure 0.20.204 supports delegation token
     */
    @Test
    public void testRelease204() {
        Assert.assertTrue(NameNodeLayoutVersion.supports(DELEGATION_TOKEN, RESERVED_REL20_204.getInfo().getLayoutVersion()));
    }

    /**
     * Test to make sure release 1.2.0 support CONCAT
     */
    @Test
    public void testRelease1_2_0() {
        Assert.assertTrue(NameNodeLayoutVersion.supports(CONCAT, RESERVED_REL1_2_0.getInfo().getLayoutVersion()));
    }

    /**
     * Test to make sure NameNode.Feature support previous features
     */
    @Test
    public void testNameNodeFeature() {
        final LayoutFeature first = Feature.ROLLING_UPGRADE;
        Assert.assertTrue(NameNodeLayoutVersion.supports(TestLayoutVersion.LAST_NON_RESERVED_COMMON_FEATURE, first.getInfo().getLayoutVersion()));
        Assert.assertEquals(((TestLayoutVersion.LAST_COMMON_FEATURE.getInfo().getLayoutVersion()) - 1), first.getInfo().getLayoutVersion());
    }

    /**
     * Test to make sure DataNode.Feature support previous features
     */
    @Test
    public void testDataNodeFeature() {
        final LayoutFeature first = Feature.FIRST_LAYOUT;
        Assert.assertTrue(DataNodeLayoutVersion.supports(TestLayoutVersion.LAST_NON_RESERVED_COMMON_FEATURE, first.getInfo().getLayoutVersion()));
        Assert.assertEquals(((TestLayoutVersion.LAST_COMMON_FEATURE.getInfo().getLayoutVersion()) - 1), first.getInfo().getLayoutVersion());
    }

    /**
     * Tests expected values for minimum compatible layout version in NameNode
     * features.  TRUNCATE, APPEND_NEW_BLOCK and QUOTA_BY_STORAGE_TYPE are all
     * features that launched in the same release.  TRUNCATE was added first, so
     * we expect all 3 features to have a minimum compatible layout version equal
     * to TRUNCATE's layout version.  All features older than that existed prior
     * to the concept of a minimum compatible layout version, so for each one, the
     * minimum compatible layout version must be equal to itself.
     */
    @Test
    public void testNameNodeFeatureMinimumCompatibleLayoutVersions() {
        int baseLV = TRUNCATE.getInfo().getLayoutVersion();
        EnumSet<NameNodeLayoutVersion.Feature> compatibleFeatures = EnumSet.of(TRUNCATE, APPEND_NEW_BLOCK, QUOTA_BY_STORAGE_TYPE, ERASURE_CODING, EXPANDED_STRING_TABLE);
        for (LayoutFeature f : compatibleFeatures) {
            Assert.assertEquals(String.format(("Expected minimum compatible layout version " + "%d for feature %s."), baseLV, f), baseLV, f.getInfo().getMinimumCompatibleLayoutVersion());
        }
        List<LayoutFeature> features = new ArrayList<>();
        features.addAll(EnumSet.allOf(Feature.class));
        features.addAll(EnumSet.allOf(Feature.class));
        for (LayoutFeature f : features) {
            if (!(compatibleFeatures.contains(f))) {
                Assert.assertEquals(String.format(("Expected feature %s to have minimum " + "compatible layout version set to itself."), f), f.getInfo().getLayoutVersion(), f.getInfo().getMinimumCompatibleLayoutVersion());
            }
        }
    }

    /**
     * Tests that NameNode features are listed in order of minimum compatible
     * layout version.  It would be inconsistent to have features listed out of
     * order with respect to minimum compatible layout version, because it would
     * imply going back in time to change compatibility logic in a software release
     * that had already shipped.
     */
    @Test
    public void testNameNodeFeatureMinimumCompatibleLayoutVersionAscending() {
        LayoutFeature prevF = null;
        for (LayoutFeature f : EnumSet.allOf(Feature.class)) {
            if (prevF != null) {
                Assert.assertTrue(String.format(("Features %s and %s not listed in order of " + "minimum compatible layout version."), prevF, f), ((f.getInfo().getMinimumCompatibleLayoutVersion()) <= (prevF.getInfo().getMinimumCompatibleLayoutVersion())));
            } else {
                prevF = f;
            }
        }
    }

    /**
     * Tests that attempting to add a new NameNode feature out of order with
     * respect to minimum compatible layout version will fail fast.
     */
    @Test(expected = AssertionError.class)
    public void testNameNodeFeatureMinimumCompatibleLayoutVersionOutOfOrder() {
        FeatureInfo ancestorF = RESERVED_REL2_4_0.getInfo();
        LayoutFeature f = Mockito.mock(LayoutFeature.class);
        Mockito.when(f.getInfo()).thenReturn(new FeatureInfo(((ancestorF.getLayoutVersion()) - 1), ancestorF.getLayoutVersion(), ((ancestorF.getMinimumCompatibleLayoutVersion()) + 1), "Invalid feature.", false));
        Map<Integer, SortedSet<LayoutFeature>> features = new HashMap<>();
        LayoutVersion.updateMap(features, LayoutVersion.Feature.values());
        LayoutVersion.updateMap(features, new LayoutFeature[]{ f });
    }

    /**
     * Asserts the current minimum compatible layout version of the software, if a
     * release were created from the codebase right now.  This test is meant to
     * make developers stop and reconsider if they introduce a change that requires
     * a new minimum compatible layout version.  This would make downgrade
     * impossible.
     */
    @Test
    public void testCurrentMinimumCompatibleLayoutVersion() {
        int expectedMinCompatLV = TRUNCATE.getInfo().getLayoutVersion();
        int actualMinCompatLV = LayoutVersion.getMinimumCompatibleLayoutVersion(NameNodeLayoutVersion.Feature.values());
        Assert.assertEquals(("The minimum compatible layout version has changed.  " + (("Downgrade to prior versions is no longer possible.  Please either " + "restore compatibility, or if the incompatibility is intentional, ") + "then update this assertion.")), expectedMinCompatLV, actualMinCompatLV);
    }

    /**
     * When a LayoutVersion support SNAPSHOT, it must support
     * FSIMAGE_NAME_OPTIMIZATION.
     */
    @Test
    public void testSNAPSHOT() {
        for (Feature f : Feature.values()) {
            final int version = f.getInfo().getLayoutVersion();
            if (NameNodeLayoutVersion.supports(SNAPSHOT, version)) {
                Assert.assertTrue(NameNodeLayoutVersion.supports(FSIMAGE_NAME_OPTIMIZATION, version));
            }
        }
    }
}

