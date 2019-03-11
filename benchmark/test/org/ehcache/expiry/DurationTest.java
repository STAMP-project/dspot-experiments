/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.expiry;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class DurationTest {
    @Test
    public void testBasic() {
        Duration duration = new Duration(1, TimeUnit.SECONDS);
        MatcherAssert.assertThat(duration.getLength(), Matchers.equalTo(1L));
        MatcherAssert.assertThat(duration.getTimeUnit(), Matchers.equalTo(TimeUnit.SECONDS));
        MatcherAssert.assertThat(duration.isInfinite(), Matchers.equalTo(false));
    }

    @Test
    public void testExplicitZero() {
        Duration zero1 = new Duration(0, TimeUnit.SECONDS);
        Duration zero2 = new Duration(0, TimeUnit.MILLISECONDS);
        MatcherAssert.assertThat(zero1.equals(zero2), Matchers.equalTo(true));
        MatcherAssert.assertThat(zero2.equals(zero1), Matchers.equalTo(true));
        MatcherAssert.assertThat(((zero1.hashCode()) == (zero2.hashCode())), Matchers.equalTo(true));
    }

    @Test
    public void testEqualsHashcode() {
        Set<Duration> set = new HashSet<>();
        assertAdd(Duration.INFINITE, set);
        assertAdd(Duration.ZERO, set);
        MatcherAssert.assertThat(set.add(new Duration(0L, TimeUnit.SECONDS)), Matchers.equalTo(false));
        assertAdd(new Duration(1L, TimeUnit.SECONDS), set);
        assertAdd(new Duration(1L, TimeUnit.MILLISECONDS), set);
        assertAdd(new Duration(42L, TimeUnit.SECONDS), set);
        assertAdd(new Duration(43L, TimeUnit.SECONDS), set);
    }

    @Test
    public void testForever() {
        MatcherAssert.assertThat(Duration.INFINITE.isInfinite(), Matchers.equalTo(true));
        MatcherAssert.assertThat(Duration.INFINITE.equals(Duration.ZERO), Matchers.is(false));
        try {
            Duration.INFINITE.getLength();
            throw new AssertionError();
        } catch (IllegalStateException ise) {
            // expected
        }
        try {
            Duration.INFINITE.getTimeUnit();
            throw new AssertionError();
        } catch (IllegalStateException ise) {
            // expected
        }
    }

    @Test
    public void testZero() {
        MatcherAssert.assertThat(Duration.ZERO.isInfinite(), Matchers.equalTo(false));
        MatcherAssert.assertThat(Duration.ZERO.getLength(), Matchers.equalTo(0L));
        MatcherAssert.assertThat(Duration.ZERO.getTimeUnit(), Matchers.any(TimeUnit.class));
        MatcherAssert.assertThat(Duration.ZERO.equals(Duration.INFINITE), Matchers.is(false));
    }
}

