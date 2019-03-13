/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.instance;


import GroupProperty.INIT_CLUSTER_VERSION;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.internal.cluster.ClusterVersionListener;
import com.hazelcast.internal.cluster.impl.JoinRequest;
import com.hazelcast.internal.cluster.impl.VersionMismatchException;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Packet;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


/**
 * Test DefaultNodeExtension behavior
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DefaultNodeExtensionTest extends HazelcastTestSupport {
    private int buildNumber;

    private HazelcastInstance hazelcastInstance;

    private Node node;

    private NodeExtension nodeExtension;

    private MemberVersion nodeVersion;

    private Address joinAddress;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void test_nodeVersionCompatibleWith_ownClusterVersion() {
        MemberVersion currentVersion = HazelcastTestSupport.getNode(hazelcastInstance).getVersion();
        Assert.assertTrue(nodeExtension.isNodeVersionCompatibleWith(currentVersion.asVersion()));
    }

    @Test
    public void test_nodeVersionNotCompatibleWith_otherMinorVersion() {
        MemberVersion currentVersion = HazelcastTestSupport.getNode(hazelcastInstance).getVersion();
        Version minorMinusOne = Version.of(currentVersion.getMajor(), ((currentVersion.getMinor()) - 1));
        Assert.assertFalse(nodeExtension.isNodeVersionCompatibleWith(minorMinusOne));
    }

    @Test
    public void test_nodeVersionNotCompatibleWith_otherMajorVersion() {
        MemberVersion currentVersion = HazelcastTestSupport.getNode(hazelcastInstance).getVersion();
        Version majorPlusOne = Version.of(((currentVersion.getMajor()) + 1), currentVersion.getMinor());
        Assert.assertFalse(nodeExtension.isNodeVersionCompatibleWith(majorPlusOne));
    }

    @Test
    public void test_joinRequestAllowed_whenSameVersion() {
        JoinRequest joinRequest = new JoinRequest(Packet.VERSION, buildNumber, nodeVersion, joinAddress, UuidUtil.newUnsecureUuidString(), false, null, null, null, null, null);
        nodeExtension.validateJoinRequest(joinRequest);
    }

    @Test
    public void test_joinRequestAllowed_whenNextPatchVersion() {
        MemberVersion nextPatchVersion = MemberVersion.of(nodeVersion.getMajor(), nodeVersion.getMinor(), ((nodeVersion.getPatch()) + 1));
        JoinRequest joinRequest = new JoinRequest(Packet.VERSION, buildNumber, nextPatchVersion, joinAddress, UuidUtil.newUnsecureUuidString(), false, null, null, null, null, null);
        nodeExtension.validateJoinRequest(joinRequest);
    }

    @Test
    public void test_joinRequestFails_whenNextMinorVersion() {
        MemberVersion nextMinorVersion = MemberVersion.of(nodeVersion.getMajor(), ((nodeVersion.getMinor()) + 1), nodeVersion.getPatch());
        JoinRequest joinRequest = new JoinRequest(Packet.VERSION, buildNumber, nextMinorVersion, joinAddress, UuidUtil.newUnsecureUuidString(), false, null, null, null, null, null);
        expected.expect(VersionMismatchException.class);
        expected.expectMessage(Matchers.containsString("Rolling Member Upgrades are only supported in Hazelcast Enterprise"));
        nodeExtension.validateJoinRequest(joinRequest);
    }

    @Test
    public void test_joinRequestFails_whenPreviousMinorVersion() {
        MemberVersion nextMinorVersion = MemberVersion.of(nodeVersion.getMajor(), ((nodeVersion.getMinor()) - 1), nodeVersion.getPatch());
        JoinRequest joinRequest = new JoinRequest(Packet.VERSION, buildNumber, nextMinorVersion, joinAddress, UuidUtil.newUnsecureUuidString(), false, null, null, null, null, null);
        expected.expect(VersionMismatchException.class);
        expected.expectMessage(Matchers.containsString("Rolling Member Upgrades are only supported for the next minor version"));
        expected.expectMessage(Matchers.containsString("Rolling Member Upgrades are only supported in Hazelcast Enterprise"));
        nodeExtension.validateJoinRequest(joinRequest);
    }

    @Test
    public void test_joinRequestFails_whenNextMajorVersion() {
        MemberVersion nextMajorVersion = MemberVersion.of(((nodeVersion.getMajor()) + 1), nodeVersion.getMinor(), nodeVersion.getPatch());
        JoinRequest joinRequest = new JoinRequest(Packet.VERSION, buildNumber, nextMajorVersion, joinAddress, UuidUtil.newUnsecureUuidString(), false, null, null, null, null, null);
        expected.expect(VersionMismatchException.class);
        expected.expectMessage(Matchers.containsString("Rolling Member Upgrades are only supported for the same major version"));
        expected.expectMessage(Matchers.containsString("Rolling Member Upgrades are only supported in Hazelcast Enterprise"));
        nodeExtension.validateJoinRequest(joinRequest);
    }

    @Test
    public void test_clusterVersionListener_invokedOnRegistration() {
        final CountDownLatch latch = new CountDownLatch(1);
        ClusterVersionListener listener = new ClusterVersionListener() {
            @Override
            public void onClusterVersionChange(Version newVersion) {
                latch.countDown();
            }
        };
        Assert.assertTrue(nodeExtension.registerListener(listener));
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test
    public void test_listenerNotRegistered_whenUnknownType() {
        Assert.assertFalse(nodeExtension.registerListener(new Object()));
    }

    @Test
    public void test_listenerHazelcastInstanceInjected_whenHazelcastInstanceAware() {
        DefaultNodeExtensionTest.HazelcastInstanceAwareVersionListener listener = new DefaultNodeExtensionTest.HazelcastInstanceAwareVersionListener();
        Assert.assertTrue(nodeExtension.registerListener(listener));
        Assert.assertEquals(hazelcastInstance, listener.getInstance());
    }

    @Test
    public void test_clusterVersionListener_invokedWithNodeCodebaseVersion_whenClusterVersionIsNull() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean failed = new AtomicBoolean(false);
        ClusterVersionListener listener = new ClusterVersionListener() {
            @Override
            public void onClusterVersionChange(Version newVersion) {
                if (!(newVersion.equals(nodeVersion.asVersion()))) {
                    failed.set(true);
                }
                latch.countDown();
            }
        };
        makeClusterVersionUnknownAndVerifyListener(latch, failed, listener);
    }

    @Test
    public void test_clusterVersionListener_invokedWithOverriddenPropertyValue_whenClusterVersionIsNull() throws Exception {
        // override initial cluster version
        System.setProperty(INIT_CLUSTER_VERSION.getName(), "2.1.7");
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean failed = new AtomicBoolean(false);
        ClusterVersionListener listener = new ClusterVersionListener() {
            @Override
            public void onClusterVersionChange(Version newVersion) {
                if (!(newVersion.equals(Version.of("2.1.7")))) {
                    failed.set(true);
                }
                latch.countDown();
            }
        };
        makeClusterVersionUnknownAndVerifyListener(latch, failed, listener);
        System.clearProperty(INIT_CLUSTER_VERSION.getName());
    }

    public static class HazelcastInstanceAwareVersionListener implements HazelcastInstanceAware , ClusterVersionListener {
        private HazelcastInstance instance;

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            instance = hazelcastInstance;
        }

        @Override
        public void onClusterVersionChange(Version newVersion) {
        }

        public HazelcastInstance getInstance() {
            return instance;
        }
    }
}

