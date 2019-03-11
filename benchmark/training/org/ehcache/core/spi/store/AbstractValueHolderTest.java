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
package org.ehcache.core.spi.store;


import org.ehcache.core.spi.time.TimeSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 *
 *
 * @author Ludovic Orban
 */
public class AbstractValueHolderTest {
    @Test
    public void testCreationTime() throws Exception {
        AbstractValueHolder<String> valueHolder = newAbstractValueHolder(1000L);
        MatcherAssert.assertThat(valueHolder.creationTime(), Is.is(1000L));
    }

    @Test
    public void testExpirationTime() throws Exception {
        AbstractValueHolder<String> valueHolder = newAbstractValueHolder(0L, 1000L);
        MatcherAssert.assertThat(valueHolder.expirationTime(), Is.is(1000L));
    }

    @Test
    public void testLastAccessTime() throws Exception {
        // last access time defaults to create time
        AbstractValueHolder<String> valueHolder = newAbstractValueHolder(1000L);
        MatcherAssert.assertThat(valueHolder.lastAccessTime(), Is.is(1000L));
        valueHolder = newAbstractValueHolder(1000L, 0L, 2000L);
        MatcherAssert.assertThat(valueHolder.lastAccessTime(), Is.is(2000L));
    }

    @Test
    public void testIsExpired() throws Exception {
        MatcherAssert.assertThat(newAbstractValueHolder(1000L).isExpired(1000L), Is.is(false));
        MatcherAssert.assertThat(newAbstractValueHolder(1000L, 1001L).isExpired(1000L), Is.is(false));
        MatcherAssert.assertThat(newAbstractValueHolder(1000L, 1000L).isExpired(1000L), Is.is(true));
    }

    @Test
    public void testEquals() throws Exception {
        MatcherAssert.assertThat(newAbstractValueHolder(0L).equals(newAbstractValueHolder(0L)), Is.is(true));
        MatcherAssert.assertThat(newAbstractValueHolder(1L).equals(newAbstractValueHolder(0L)), Is.is(false));
        MatcherAssert.assertThat(newAbstractValueHolder(2L, 0L).equals(newAbstractValueHolder(2L, 0L)), Is.is(true));
        MatcherAssert.assertThat(newAbstractValueHolder(2L, 0L).equals(newAbstractValueHolder(2L, 1L)), Is.is(false));
        MatcherAssert.assertThat(newAbstractValueHolder(2L, 0L).equals(newAbstractValueHolder(3L, 0L)), Is.is(false));
        MatcherAssert.assertThat(newAbstractValueHolder(0L, 2L, 1L).equals(newAbstractValueHolder(0L, 2L, 1L)), Is.is(true));
        MatcherAssert.assertThat(newAbstractValueHolder(1L, 2L, 1L).equals(newAbstractValueHolder(0L, 2L, 1L)), Is.is(false));
        MatcherAssert.assertThat(newAbstractValueHolder(0L, 3L, 1L).equals(newAbstractValueHolder(0L, 2L, 1L)), Is.is(false));
        MatcherAssert.assertThat(newAbstractValueHolder(0L, 2L, 3L).equals(newAbstractValueHolder(0L, 2L, 1L)), Is.is(false));
    }

    @Test
    public void testSubclassEquals() throws Exception {
        MatcherAssert.assertThat(new AbstractValueHolder<String>((-1), 1L) {
            @Override
            public String get() {
                return "aaa";
            }

            @Override
            public int hashCode() {
                return (super.hashCode()) + (get().hashCode());
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof AbstractValueHolder) {
                    AbstractValueHolder<?> other = ((AbstractValueHolder<?>) (obj));
                    return (super.equals(obj)) && (get().equals(other.get()));
                }
                return false;
            }
        }.equals(new AbstractValueHolder<String>((-1), 1L) {
            @Override
            public String get() {
                return "aaa";
            }

            @Override
            public int hashCode() {
                return (super.hashCode()) + (get().hashCode());
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof AbstractValueHolder) {
                    AbstractValueHolder<?> other = ((AbstractValueHolder<?>) (obj));
                    return (super.equals(obj)) && (get().equals(other.get()));
                }
                return false;
            }
        }), Is.is(true));
    }

    private static class TestTimeSource implements TimeSource {
        private long time = 0;

        @Override
        public long getTimeMillis() {
            return time;
        }

        public void advanceTime(long step) {
            time += step;
        }
    }
}

