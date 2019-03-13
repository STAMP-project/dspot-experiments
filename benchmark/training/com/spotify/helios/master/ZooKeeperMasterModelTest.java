/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.master;


import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.RolloutOptions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ZooKeeperMasterModelTest {
    @Test
    public void testRolloutOptionsWithFallback() throws Exception {
        final RolloutOptions options = RolloutOptions.newBuilder().setTimeout(123L).setParallelism(2).build();
        final Job job = Job.newBuilder().setName("foo").setRolloutOptions(RolloutOptions.newBuilder().setParallelism(2).setOverlap(true).setToken("bar").build()).build();
        final RolloutOptions rolloutOptions = ZooKeeperMasterModel.rolloutOptionsWithFallback(options, job);
        MatcherAssert.assertThat(rolloutOptions, Matchers.equalTo(RolloutOptions.newBuilder().setParallelism(2).setTimeout(123L).setOverlap(true).setToken("bar").setMigrate(false).setIgnoreFailures(false).build()));
    }
}

