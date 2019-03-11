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
package org.apache.hadoop.hdfs.server.datanode.fsdataset;


import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestAvailableSpaceVolumeChoosingPolicy {
    private static final int RANDOMIZED_ITERATIONS = 10000;

    private static final float RANDOMIZED_ERROR_PERCENT = 0.05F;

    private static final long RANDOMIZED_ALLOWED_ERROR = ((long) ((TestAvailableSpaceVolumeChoosingPolicy.RANDOMIZED_ERROR_PERCENT) * (TestAvailableSpaceVolumeChoosingPolicy.RANDOMIZED_ITERATIONS)));

    // Test the Round-Robin block-volume fallback path when all volumes are within
    // the threshold.
    @Test(timeout = 60000)
    public void testRR() throws Exception {
        @SuppressWarnings("unchecked")
        final AvailableSpaceVolumeChoosingPolicy<FsVolumeSpi> policy = ReflectionUtils.newInstance(AvailableSpaceVolumeChoosingPolicy.class, null);
        TestAvailableSpaceVolumeChoosingPolicy.initPolicy(policy, 1.0F);
        TestRoundRobinVolumeChoosingPolicy.testRR(policy);
    }

    // ChooseVolume should throw DiskOutOfSpaceException
    // with volume and block sizes in exception message.
    @Test(timeout = 60000)
    public void testRRPolicyExceptionMessage() throws Exception {
        final AvailableSpaceVolumeChoosingPolicy<FsVolumeSpi> policy = new AvailableSpaceVolumeChoosingPolicy<FsVolumeSpi>();
        TestAvailableSpaceVolumeChoosingPolicy.initPolicy(policy, 1.0F);
        TestRoundRobinVolumeChoosingPolicy.testRRPolicyExceptionMessage(policy);
    }

    @Test(timeout = 60000)
    public void testTwoUnbalancedVolumes() throws Exception {
        @SuppressWarnings("unchecked")
        final AvailableSpaceVolumeChoosingPolicy<FsVolumeSpi> policy = ReflectionUtils.newInstance(AvailableSpaceVolumeChoosingPolicy.class, null);
        TestAvailableSpaceVolumeChoosingPolicy.initPolicy(policy, 1.0F);
        List<FsVolumeSpi> volumes = new ArrayList<FsVolumeSpi>();
        // First volume with 1MB free space
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(0).getAvailable()).thenReturn((1024L * 1024L));
        // Second volume with 3MB free space, which is a difference of 2MB, more
        // than the threshold of 1MB.
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(1).getAvailable()).thenReturn(((1024L * 1024L) * 3));
        Assert.assertEquals(volumes.get(1), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(1), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(1), policy.chooseVolume(volumes, 100, null));
    }

    @Test(timeout = 60000)
    public void testThreeUnbalancedVolumes() throws Exception {
        @SuppressWarnings("unchecked")
        final AvailableSpaceVolumeChoosingPolicy<FsVolumeSpi> policy = ReflectionUtils.newInstance(AvailableSpaceVolumeChoosingPolicy.class, null);
        List<FsVolumeSpi> volumes = new ArrayList<FsVolumeSpi>();
        // First volume with 1MB free space
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(0).getAvailable()).thenReturn((1024L * 1024L));
        // Second volume with 3MB free space, which is a difference of 2MB, more
        // than the threshold of 1MB.
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(1).getAvailable()).thenReturn(((1024L * 1024L) * 3));
        // Third volume, again with 3MB free space.
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(2).getAvailable()).thenReturn(((1024L * 1024L) * 3));
        // We should alternate assigning between the two volumes with a lot of free
        // space.
        TestAvailableSpaceVolumeChoosingPolicy.initPolicy(policy, 1.0F);
        Assert.assertEquals(volumes.get(1), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(2), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(1), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(2), policy.chooseVolume(volumes, 100, null));
        // All writes should be assigned to the volume with the least free space.
        TestAvailableSpaceVolumeChoosingPolicy.initPolicy(policy, 0.0F);
        Assert.assertEquals(volumes.get(0), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(0), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(0), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(0), policy.chooseVolume(volumes, 100, null));
    }

    @Test(timeout = 60000)
    public void testFourUnbalancedVolumes() throws Exception {
        @SuppressWarnings("unchecked")
        final AvailableSpaceVolumeChoosingPolicy<FsVolumeSpi> policy = ReflectionUtils.newInstance(AvailableSpaceVolumeChoosingPolicy.class, null);
        List<FsVolumeSpi> volumes = new ArrayList<FsVolumeSpi>();
        // First volume with 1MB free space
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(0).getAvailable()).thenReturn((1024L * 1024L));
        // Second volume with 1MB + 1 byte free space
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(1).getAvailable()).thenReturn(((1024L * 1024L) + 1));
        // Third volume with 3MB free space, which is a difference of 2MB, more
        // than the threshold of 1MB.
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(2).getAvailable()).thenReturn(((1024L * 1024L) * 3));
        // Fourth volume, again with 3MB free space.
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(3).getAvailable()).thenReturn(((1024L * 1024L) * 3));
        // We should alternate assigning between the two volumes with a lot of free
        // space.
        TestAvailableSpaceVolumeChoosingPolicy.initPolicy(policy, 1.0F);
        Assert.assertEquals(volumes.get(2), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(3), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(2), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(3), policy.chooseVolume(volumes, 100, null));
        // We should alternate assigning between the two volumes with less free
        // space.
        TestAvailableSpaceVolumeChoosingPolicy.initPolicy(policy, 0.0F);
        Assert.assertEquals(volumes.get(0), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(1), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(0), policy.chooseVolume(volumes, 100, null));
        Assert.assertEquals(volumes.get(1), policy.chooseVolume(volumes, 100, null));
    }

    @Test(timeout = 60000)
    public void testNotEnoughSpaceOnSelectedVolume() throws Exception {
        @SuppressWarnings("unchecked")
        final AvailableSpaceVolumeChoosingPolicy<FsVolumeSpi> policy = ReflectionUtils.newInstance(AvailableSpaceVolumeChoosingPolicy.class, null);
        List<FsVolumeSpi> volumes = new ArrayList<FsVolumeSpi>();
        // First volume with 1MB free space
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(0).getAvailable()).thenReturn((1024L * 1024L));
        // Second volume with 3MB free space, which is a difference of 2MB, more
        // than the threshold of 1MB.
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(1).getAvailable()).thenReturn(((1024L * 1024L) * 3));
        // All writes should be assigned to the volume with the least free space.
        // However, if the volume with the least free space doesn't have enough
        // space to accept the replica size, and another volume does have enough
        // free space, that should be chosen instead.
        TestAvailableSpaceVolumeChoosingPolicy.initPolicy(policy, 0.0F);
        Assert.assertEquals(volumes.get(1), policy.chooseVolume(volumes, ((1024L * 1024L) * 2), null));
    }

    @Test(timeout = 60000)
    public void testAvailableSpaceChanges() throws Exception {
        @SuppressWarnings("unchecked")
        final AvailableSpaceVolumeChoosingPolicy<FsVolumeSpi> policy = ReflectionUtils.newInstance(AvailableSpaceVolumeChoosingPolicy.class, null);
        TestAvailableSpaceVolumeChoosingPolicy.initPolicy(policy, 1.0F);
        List<FsVolumeSpi> volumes = new ArrayList<FsVolumeSpi>();
        // First volume with 1MB free space
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(0).getAvailable()).thenReturn((1024L * 1024L));
        // Second volume with 3MB free space, which is a difference of 2MB, more
        // than the threshold of 1MB.
        volumes.add(Mockito.mock(FsVolumeSpi.class));
        Mockito.when(volumes.get(1).getAvailable()).thenReturn(((1024L * 1024L) * 3)).thenReturn(((1024L * 1024L) * 3)).thenReturn(((1024L * 1024L) * 3)).thenReturn(((1024L * 1024L) * 1));// After the third check, return 1MB.

        // Should still be able to get a volume for the replica even though the
        // available space on the second volume changed.
        Assert.assertEquals(volumes.get(1), policy.chooseVolume(volumes, 100, null));
    }

    @Test(timeout = 60000)
    public void randomizedTest1() throws Exception {
        doRandomizedTest(0.75F, 1, 1);
    }

    @Test(timeout = 60000)
    public void randomizedTest2() throws Exception {
        doRandomizedTest(0.75F, 5, 1);
    }

    @Test(timeout = 60000)
    public void randomizedTest3() throws Exception {
        doRandomizedTest(0.75F, 1, 5);
    }

    @Test(timeout = 60000)
    public void randomizedTest4() throws Exception {
        doRandomizedTest(0.9F, 5, 1);
    }
}

