/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.profiler;


import ProfilePhase.ANALYZE;
import ProfilePhase.FINISH;
import com.google.common.io.ByteStreams;
import com.google.devtools.build.lib.profiler.MemoryProfiler.MemoryProfileStableHeapParameters;
import com.google.devtools.build.lib.profiler.MemoryProfiler.Sleeper;
import java.lang.management.MemoryMXBean;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link MemoryProfiler}.
 */
@RunWith(JUnit4.class)
public class MemoryProfilerTest {
    @Test
    public void profilerDoesOneGcAndNoSleepNormally() throws Exception {
        MemoryProfiler profiler = MemoryProfiler.instance();
        profiler.setStableMemoryParameters(new MemoryProfileStableHeapParameters.Converter().convert("1,10"));
        profiler.start(ByteStreams.nullOutputStream());
        MemoryMXBean bean = Mockito.mock(MemoryMXBean.class);
        MemoryProfilerTest.RecordingSleeper sleeper = new MemoryProfilerTest.RecordingSleeper();
        profiler.prepareBean(ANALYZE, bean, sleeper);
        assertThat(sleeper.sleeps).isEmpty();
        Mockito.verify(bean, Mockito.times(1)).gc();
        profiler.prepareBean(FINISH, bean, sleeper);
        Mockito.verify(bean, Mockito.times(2)).gc();
        assertThat(sleeper.sleeps).isEmpty();
    }

    @Test
    public void profilerDoesOneGcAndNoSleepExceptInFinish() throws Exception {
        MemoryProfiler profiler = MemoryProfiler.instance();
        profiler.setStableMemoryParameters(new MemoryProfileStableHeapParameters.Converter().convert("3,10"));
        profiler.start(ByteStreams.nullOutputStream());
        MemoryMXBean bean = Mockito.mock(MemoryMXBean.class);
        MemoryProfilerTest.RecordingSleeper sleeper = new MemoryProfilerTest.RecordingSleeper();
        profiler.prepareBean(ANALYZE, bean, sleeper);
        assertThat(sleeper.sleeps).isEmpty();
        Mockito.verify(bean, Mockito.times(1)).gc();
        profiler.prepareBean(FINISH, bean, sleeper);
        assertThat(sleeper.sleeps).containsExactly(Duration.ofSeconds(10), Duration.ofSeconds(10)).inOrder();
        Mockito.verify(bean, Mockito.times(4)).gc();
    }

    private static class RecordingSleeper implements Sleeper {
        private final List<Duration> sleeps = new ArrayList<>();

        @Override
        public void sleep(Duration duration) {
            sleeps.add(duration);
        }
    }
}

