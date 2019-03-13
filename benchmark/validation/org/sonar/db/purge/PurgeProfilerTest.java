/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.db.purge;


import PurgeProfiler.Clock;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.log.Logger;


public class PurgeProfilerTest {
    private PurgeProfilerTest.MockedClock clock;

    private PurgeProfiler profiler;

    private Logger logger;

    @Test
    public void shouldProfilePurge() {
        profiler.start("foo");
        clock.sleep(10);
        profiler.stop();
        profiler.start("bar");
        clock.sleep(5);
        profiler.stop();
        profiler.start("foo");
        clock.sleep(8);
        profiler.stop();
        profiler.dump(50, logger);
        Mockito.verify(logger).info(ArgumentMatchers.contains("foo: 18ms"));
        Mockito.verify(logger).info(ArgumentMatchers.contains("bar: 5ms"));
    }

    @Test
    public void shouldResetPurgeProfiling() {
        profiler.start("foo");
        clock.sleep(10);
        profiler.stop();
        profiler.reset();
        profiler.start("bar");
        clock.sleep(5);
        profiler.stop();
        profiler.start("foo");
        clock.sleep(8);
        profiler.stop();
        profiler.dump(50, logger);
        Mockito.verify(logger).info(ArgumentMatchers.contains("foo: 8ms"));
        Mockito.verify(logger).info(ArgumentMatchers.contains("bar: 5ms"));
    }

    private class MockedClock extends PurgeProfiler.Clock {
        private long now = 0;

        @Override
        public long now() {
            return now;
        }

        public void sleep(long duration) {
            now += duration;
        }
    }
}

