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
package org.ehcache.config.units;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 *
 *
 * @author cdennis
 */
public class MemoryUnitTest {
    @Test
    public void testBasicPositiveConversions() {
        MatcherAssert.assertThat(MemoryUnit.B.toBytes(1L), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.KB.toBytes(1L), Matchers.is(1024L));
        MatcherAssert.assertThat(MemoryUnit.MB.toBytes(1L), Matchers.is((1024L * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.GB.toBytes(1L), Matchers.is(((1024L * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.TB.toBytes(1L), Matchers.is((((1024L * 1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.PB.toBytes(1L), Matchers.is(((((1024L * 1024L) * 1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.B.convert(1L, MemoryUnit.B), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.KB.convert(1L, MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(1L, MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(1L, MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(1L, MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(1L, MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.B.convert(1L, MemoryUnit.KB), Matchers.is(1024L));
        MatcherAssert.assertThat(MemoryUnit.KB.convert(1L, MemoryUnit.KB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(1L, MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(1L, MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(1L, MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(1L, MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.B.convert(1L, MemoryUnit.MB), Matchers.is((1024L * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.KB.convert(1L, MemoryUnit.MB), Matchers.is(1024L));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(1L, MemoryUnit.MB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(1L, MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(1L, MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(1L, MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.B.convert(1L, MemoryUnit.GB), Matchers.is(((1024L * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.KB.convert(1L, MemoryUnit.GB), Matchers.is((1024L * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(1L, MemoryUnit.GB), Matchers.is(1024L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(1L, MemoryUnit.GB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(1L, MemoryUnit.GB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(1L, MemoryUnit.GB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.B.convert(1L, MemoryUnit.TB), Matchers.is((((1024L * 1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.KB.convert(1L, MemoryUnit.TB), Matchers.is(((1024L * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(1L, MemoryUnit.TB), Matchers.is((1024L * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(1L, MemoryUnit.TB), Matchers.is(1024L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(1L, MemoryUnit.TB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(1L, MemoryUnit.TB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.B.convert(1L, MemoryUnit.PB), Matchers.is(((((1024L * 1024L) * 1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.KB.convert(1L, MemoryUnit.PB), Matchers.is((((1024L * 1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(1L, MemoryUnit.PB), Matchers.is(((1024L * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(1L, MemoryUnit.PB), Matchers.is((1024L * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(1L, MemoryUnit.PB), Matchers.is(1024L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(1L, MemoryUnit.PB), Matchers.is(1L));
    }

    @Test
    public void testBasicNegativeConversions() {
        MatcherAssert.assertThat(MemoryUnit.B.toBytes((-1L)), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.KB.toBytes((-1L)), Matchers.is((-1024L)));
        MatcherAssert.assertThat(MemoryUnit.MB.toBytes((-1L)), Matchers.is(((-1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.GB.toBytes((-1L)), Matchers.is((((-1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.TB.toBytes((-1L)), Matchers.is(((((-1024L) * 1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.PB.toBytes((-1L)), Matchers.is((((((-1024L) * 1024L) * 1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.B.convert((-1L), MemoryUnit.B), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.KB.convert((-1L), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.MB.convert((-1L), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert((-1L), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert((-1L), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((-1L), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.B.convert((-1L), MemoryUnit.KB), Matchers.is((-1024L)));
        MatcherAssert.assertThat(MemoryUnit.KB.convert((-1L), MemoryUnit.KB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.MB.convert((-1L), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert((-1L), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert((-1L), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((-1L), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.B.convert((-1L), MemoryUnit.MB), Matchers.is(((-1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.KB.convert((-1L), MemoryUnit.MB), Matchers.is((-1024L)));
        MatcherAssert.assertThat(MemoryUnit.MB.convert((-1L), MemoryUnit.MB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.GB.convert((-1L), MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert((-1L), MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((-1L), MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.B.convert((-1L), MemoryUnit.GB), Matchers.is((((-1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.KB.convert((-1L), MemoryUnit.GB), Matchers.is(((-1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.MB.convert((-1L), MemoryUnit.GB), Matchers.is((-1024L)));
        MatcherAssert.assertThat(MemoryUnit.GB.convert((-1L), MemoryUnit.GB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.TB.convert((-1L), MemoryUnit.GB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((-1L), MemoryUnit.GB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.B.convert((-1L), MemoryUnit.TB), Matchers.is(((((-1024L) * 1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.KB.convert((-1L), MemoryUnit.TB), Matchers.is((((-1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.MB.convert((-1L), MemoryUnit.TB), Matchers.is(((-1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.GB.convert((-1L), MemoryUnit.TB), Matchers.is((-1024L)));
        MatcherAssert.assertThat(MemoryUnit.TB.convert((-1L), MemoryUnit.TB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((-1L), MemoryUnit.TB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.B.convert((-1L), MemoryUnit.PB), Matchers.is((((((-1024L) * 1024L) * 1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.KB.convert((-1L), MemoryUnit.PB), Matchers.is(((((-1024L) * 1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.MB.convert((-1L), MemoryUnit.PB), Matchers.is((((-1024L) * 1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.GB.convert((-1L), MemoryUnit.PB), Matchers.is(((-1024L) * 1024L)));
        MatcherAssert.assertThat(MemoryUnit.TB.convert((-1L), MemoryUnit.PB), Matchers.is((-1024L)));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((-1L), MemoryUnit.PB), Matchers.is((-1L)));
    }

    @Test
    public void testPositiveThresholdConditions() {
        MatcherAssert.assertThat(MemoryUnit.KB.convert((1024L - 1), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.KB.convert((1024L + 1), MemoryUnit.B), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(((1024L * 1024L) - 1), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(((1024L * 1024L) + 1), MemoryUnit.B), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert((((1024L * 1024L) * 1024L) - 1), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert((((1024L * 1024L) * 1024L) + 1), MemoryUnit.B), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((((1024L * 1024L) * 1024L) * 1024L) - 1), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((((1024L * 1024L) * 1024L) * 1024L) + 1), MemoryUnit.B), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((((((1024L * 1024L) * 1024L) * 1024L) * 1024L) - 1), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((((((1024L * 1024L) * 1024L) * 1024L) * 1024L) + 1), MemoryUnit.B), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.MB.convert((1024L - 1), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.MB.convert((1024L + 1), MemoryUnit.KB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(((1024L * 1024L) - 1), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(((1024L * 1024L) + 1), MemoryUnit.KB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert((((1024L * 1024L) * 1024L) - 1), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert((((1024L * 1024L) * 1024L) + 1), MemoryUnit.KB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((((1024L * 1024L) * 1024L) * 1024L) - 1), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((((1024L * 1024L) * 1024L) * 1024L) + 1), MemoryUnit.KB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert((1024L - 1), MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert((1024L + 1), MemoryUnit.MB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((1024L * 1024L) - 1), MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((1024L * 1024L) + 1), MemoryUnit.MB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((((1024L * 1024L) * 1024L) - 1), MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((((1024L * 1024L) * 1024L) + 1), MemoryUnit.MB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert((1024L - 1), MemoryUnit.GB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert((1024L + 1), MemoryUnit.GB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((1024L * 1024L) - 1), MemoryUnit.GB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((1024L * 1024L) + 1), MemoryUnit.GB), Matchers.is(1L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((1024L - 1), MemoryUnit.TB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert((1024L + 1), MemoryUnit.TB), Matchers.is(1L));
    }

    @Test
    public void testNegativeThresholdConditions() {
        MatcherAssert.assertThat(MemoryUnit.KB.convert(((-1024L) + 1), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.KB.convert(((-1024L) - 1), MemoryUnit.B), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(((-(1024L * 1024L)) + 1), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(((-(1024L * 1024L)) - 1), MemoryUnit.B), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(((-((1024L * 1024L) * 1024L)) + 1), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(((-((1024L * 1024L) * 1024L)) - 1), MemoryUnit.B), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((-(((1024L * 1024L) * 1024L) * 1024L)) + 1), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((-(((1024L * 1024L) * 1024L) * 1024L)) - 1), MemoryUnit.B), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((-((((1024L * 1024L) * 1024L) * 1024L) * 1024L)) + 1), MemoryUnit.B), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((-((((1024L * 1024L) * 1024L) * 1024L) * 1024L)) - 1), MemoryUnit.B), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(((-1024L) + 1), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.MB.convert(((-1024L) - 1), MemoryUnit.KB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(((-(1024L * 1024L)) + 1), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(((-(1024L * 1024L)) - 1), MemoryUnit.KB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((-((1024L * 1024L) * 1024L)) + 1), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((-((1024L * 1024L) * 1024L)) - 1), MemoryUnit.KB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((-(((1024L * 1024L) * 1024L) * 1024L)) + 1), MemoryUnit.KB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((-(((1024L * 1024L) * 1024L) * 1024L)) - 1), MemoryUnit.KB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(((-1024L) + 1), MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.GB.convert(((-1024L) - 1), MemoryUnit.MB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((-(1024L * 1024L)) + 1), MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((-(1024L * 1024L)) - 1), MemoryUnit.MB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((-((1024L * 1024L) * 1024L)) + 1), MemoryUnit.MB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((-((1024L * 1024L) * 1024L)) - 1), MemoryUnit.MB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((-1024L) + 1), MemoryUnit.GB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.TB.convert(((-1024L) - 1), MemoryUnit.GB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((-(1024L * 1024L)) + 1), MemoryUnit.GB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((-(1024L * 1024L)) - 1), MemoryUnit.GB), Matchers.is((-1L)));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((-1024L) + 1), MemoryUnit.TB), Matchers.is(0L));
        MatcherAssert.assertThat(MemoryUnit.PB.convert(((-1024L) - 1), MemoryUnit.TB), Matchers.is((-1L)));
    }

    @Test
    public void testThresholdComparisons() {
        MatcherAssert.assertThat(MemoryUnit.KB.compareTo(1, (1024L - 1), MemoryUnit.B), Matchers.greaterThan(0));
        MatcherAssert.assertThat(MemoryUnit.KB.compareTo(1, 1024L, MemoryUnit.B), Matchers.is(0));
        MatcherAssert.assertThat(MemoryUnit.KB.compareTo(1, (1024L + 1), MemoryUnit.B), lessThan(0));
        MatcherAssert.assertThat(MemoryUnit.KB.compareTo(2, (2048L - 1), MemoryUnit.B), Matchers.greaterThan(0));
        MatcherAssert.assertThat(MemoryUnit.KB.compareTo(2, 2048L, MemoryUnit.B), Matchers.is(0));
        MatcherAssert.assertThat(MemoryUnit.KB.compareTo(2, (2048L + 1), MemoryUnit.B), lessThan(0));
    }
}

