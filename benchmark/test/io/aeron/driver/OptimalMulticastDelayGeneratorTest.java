/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package io.aeron.driver;


import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class OptimalMulticastDelayGeneratorTest {
    private static final long MAX_BACKOFF = TimeUnit.MILLISECONDS.toNanos(60);

    private static final long GROUP_SIZE = 10;

    private final OptimalMulticastDelayGenerator generator = new OptimalMulticastDelayGenerator(OptimalMulticastDelayGeneratorTest.MAX_BACKOFF, OptimalMulticastDelayGeneratorTest.GROUP_SIZE);

    @Test
    public void shouldNotExceedTmaxBackoff() {
        for (int i = 0; i < 100000; i++) {
            final double delay = generator.generateNewOptimalDelay();
            MatcherAssert.assertThat(delay, Matchers.lessThanOrEqualTo(((double) (OptimalMulticastDelayGeneratorTest.MAX_BACKOFF))));
        }
    }
}

