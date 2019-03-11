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
package org.apache.geode.internal.process.lang;


import com.google.common.base.Stopwatch;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.geode.internal.process.ProcessUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AvailablePid}.
 */
public class AvailablePidTest {
    private AvailablePid availablePid;

    @Rule
    public Timeout timeout = Timeout.builder().withTimeout(20, TimeUnit.SECONDS).build();

    @Test
    public void lowerBoundShouldBeLegalPid() throws Exception {
        assertThat(ProcessUtils.isProcessAlive(AvailablePid.DEFAULT_LOWER_BOUND)).isIn(true, false);
    }

    @Test
    public void upperBoundShouldBeLegalPid() throws Exception {
        assertThat(ProcessUtils.isProcessAlive(AvailablePid.DEFAULT_UPPER_BOUND)).isIn(true, false);
    }

    @Test
    public void findAvailablePidShouldNotReturnLocalPid() throws Exception {
        int pid = availablePid.findAvailablePid();
        assertThat(pid).isNotEqualTo(ProcessUtils.identifyPid());
    }

    @Test
    public void findAvailablePidShouldNotReturnLivePid() throws Exception {
        int pid = availablePid.findAvailablePid();
        assertThat(ProcessUtils.isProcessAlive(pid)).isFalse();
    }

    @Test
    public void findAvailablePidShouldUseRandom() throws Exception {
        Random random = Mockito.spy(new Random());
        availablePid = new AvailablePid(random);
        availablePid.findAvailablePid();
        Mockito.verify(random, Mockito.atLeastOnce()).nextInt(ArgumentMatchers.anyInt());
    }

    @Test
    public void findAvailablePidsShouldReturnSpecifiedNumberOfPids() throws Exception {
        assertThat(availablePid.findAvailablePids(1)).hasSize(1);
        assertThat(availablePid.findAvailablePids(2)).hasSize(2);
        assertThat(availablePid.findAvailablePids(3)).hasSize(3);
        assertThat(availablePid.findAvailablePids(5)).hasSize(5);
    }

    @Test
    public void findAvailablePidsShouldReturnNoDuplicatedPids() throws Exception {
        assertThatNoPidIsDuplicated(availablePid.findAvailablePids(1));
        assertThatNoPidIsDuplicated(availablePid.findAvailablePids(2));
        assertThatNoPidIsDuplicated(availablePid.findAvailablePids(3));
        assertThatNoPidIsDuplicated(availablePid.findAvailablePids(5));
    }

    @Test
    public void findAvailablePidShouldReturnGreaterThanOrEqualToLowerBound() throws Exception {
        availablePid = new AvailablePid(new AvailablePid.Bounds(1, 10));
        Stopwatch stopwatch = Stopwatch.createStarted();
        do {
            assertThat(availablePid.findAvailablePid()).isGreaterThanOrEqualTo(1);
        } while ((stopwatch.elapsed(TimeUnit.SECONDS)) < 2 );
    }

    @Test
    public void findAvailablePidShouldReturnLessThanOrEqualToUpperBound() throws Exception {
        availablePid = new AvailablePid(new AvailablePid.Bounds(1, 10));
        Stopwatch stopwatch = Stopwatch.createStarted();
        do {
            assertThat(availablePid.findAvailablePid()).isLessThanOrEqualTo(10);
        } while ((stopwatch.elapsed(TimeUnit.SECONDS)) < 2 );
    }

    @Test
    public void randomLowerBoundIsInclusive() throws Exception {
        availablePid = new AvailablePid(new AvailablePid.Bounds(1, 3));
        await().untilAsserted(() -> assertThat(availablePid.random()).isEqualTo(1));
    }

    @Test
    public void randomUpperBoundIsInclusive() throws Exception {
        availablePid = new AvailablePid(new AvailablePid.Bounds(1, 3));
        await().untilAsserted(() -> assertThat(availablePid.random()).isEqualTo(3));
    }

    @Test
    public void lowerBoundMustBeGreaterThanZero() throws Exception {
        assertThatThrownBy(() -> new AvailablePid(new AvailablePid.Bounds(0, 1))).isInstanceOf(IllegalArgumentException.class).hasMessage("lowerBound must be greater than '0'");
    }

    @Test
    public void upperBoundMustBeGreaterThanLowerBound() throws Exception {
        assertThatThrownBy(() -> new AvailablePid(new AvailablePid.Bounds(1, 1))).isInstanceOf(IllegalArgumentException.class).hasMessage("upperBound must be greater than lowerBound '1'");
    }
}

