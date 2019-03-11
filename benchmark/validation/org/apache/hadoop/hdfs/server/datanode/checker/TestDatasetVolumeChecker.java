/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode.checker;


import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi.VolumeCheckContext;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.FakeTimer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for {@link DatasetVolumeChecker} when the {@link FsVolumeSpi#check}
 * method returns different values of {@link VolumeCheckResult}.
 */
@RunWith(Parameterized.class)
public class TestDatasetVolumeChecker {
    public static final Logger LOG = LoggerFactory.getLogger(TestDatasetVolumeChecker.class);

    @Rule
    public TestName testName = new TestName();

    /**
     * When null, the check call should throw an exception.
     */
    private final VolumeCheckResult.VolumeCheckResult expectedVolumeHealth;

    private static final int NUM_VOLUMES = 2;

    public TestDatasetVolumeChecker(VolumeCheckResult.VolumeCheckResult expectedVolumeHealth) {
        this.expectedVolumeHealth = expectedVolumeHealth;
    }

    /**
     * Test {@link DatasetVolumeChecker#checkVolume} propagates the
     * check to the delegate checker.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 10000)
    public void testCheckOneVolume() throws Exception {
        TestDatasetVolumeChecker.LOG.info("Executing {}", testName.getMethodName());
        final FsVolumeSpi volume = TestDatasetVolumeChecker.makeVolumes(1, expectedVolumeHealth).get(0);
        final DatasetVolumeChecker checker = new DatasetVolumeChecker(new HdfsConfiguration(), new FakeTimer());
        checker.setDelegateChecker(new TestDatasetVolumeChecker.DummyChecker());
        final AtomicLong numCallbackInvocations = new AtomicLong(0);
        /**
         * Request a check and ensure it triggered {@link FsVolumeSpi#check}.
         */
        boolean result = checker.checkVolume(volume, new DatasetVolumeChecker.Callback() {
            @Override
            public void call(Set<FsVolumeSpi> healthyVolumes, Set<FsVolumeSpi> failedVolumes) {
                numCallbackInvocations.incrementAndGet();
                if (((expectedVolumeHealth) != null) && ((expectedVolumeHealth) != (FAILED))) {
                    Assert.assertThat(healthyVolumes.size(), CoreMatchers.is(1));
                    Assert.assertThat(failedVolumes.size(), CoreMatchers.is(0));
                } else {
                    Assert.assertThat(healthyVolumes.size(), CoreMatchers.is(0));
                    Assert.assertThat(failedVolumes.size(), CoreMatchers.is(1));
                }
            }
        });
        GenericTestUtils.waitFor(() -> (numCallbackInvocations.get()) > 0, 5, 10000);
        // Ensure that the check was invoked at least once.
        Mockito.verify(volume, Mockito.times(1)).check(ArgumentMatchers.any());
        if (result) {
            Assert.assertThat(numCallbackInvocations.get(), CoreMatchers.is(1L));
        }
    }

    /**
     * Test {@link DatasetVolumeChecker#checkAllVolumes} propagates
     * checks for all volumes to the delegate checker.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 10000)
    public void testCheckAllVolumes() throws Exception {
        TestDatasetVolumeChecker.LOG.info("Executing {}", testName.getMethodName());
        final List<FsVolumeSpi> volumes = TestDatasetVolumeChecker.makeVolumes(TestDatasetVolumeChecker.NUM_VOLUMES, expectedVolumeHealth);
        final FsDatasetSpi<FsVolumeSpi> dataset = TestDatasetVolumeChecker.makeDataset(volumes);
        final DatasetVolumeChecker checker = new DatasetVolumeChecker(new HdfsConfiguration(), new FakeTimer());
        checker.setDelegateChecker(new TestDatasetVolumeChecker.DummyChecker());
        Set<FsVolumeSpi> failedVolumes = checker.checkAllVolumes(dataset);
        TestDatasetVolumeChecker.LOG.info("Got back {} failed volumes", failedVolumes.size());
        if (((expectedVolumeHealth) == null) || ((expectedVolumeHealth) == (FAILED))) {
            Assert.assertThat(failedVolumes.size(), CoreMatchers.is(TestDatasetVolumeChecker.NUM_VOLUMES));
        } else {
            Assert.assertTrue(failedVolumes.isEmpty());
        }
        // Ensure each volume's check() method was called exactly once.
        for (FsVolumeSpi volume : volumes) {
            Mockito.verify(volume, Mockito.times(1)).check(ArgumentMatchers.any());
        }
    }

    /**
     * A checker to wraps the result of {@link FsVolumeSpi#check} in
     * an ImmediateFuture.
     */
    static class DummyChecker implements AsyncChecker<VolumeCheckContext, VolumeCheckResult.VolumeCheckResult> {
        @Override
        public Optional<ListenableFuture<VolumeCheckResult.VolumeCheckResult>> schedule(Checkable<VolumeCheckContext, VolumeCheckResult.VolumeCheckResult> target, VolumeCheckContext context) {
            try {
                return Optional.of(Futures.immediateFuture(target.check(context)));
            } catch (Exception e) {
                TestDatasetVolumeChecker.LOG.info(("check routine threw exception " + e));
                return Optional.of(Futures.immediateFailedFuture(e));
            }
        }

        @Override
        public void shutdownAndWait(long timeout, TimeUnit timeUnit) throws InterruptedException {
            // Nothing to cancel.
        }
    }
}

