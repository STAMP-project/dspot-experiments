/**
 * Copyright (C) 2011 The Guava Authors
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
package com.github.benmanes.caffeine.cache;


import Strength.SOFT;
import Strength.WEAK;
import com.google.common.testing.EqualsTester;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;


/**
 * A port of Guava's CacheBuilderSpecTest.
 * TODO(user): tests of a few invalid input conditions, boundary conditions.
 *
 * @author Adam Winer
 */
public class CaffeineSpecGuavaTest extends TestCase {
    public void testParse_empty() {
        CaffeineSpec spec = CaffeineSpec.parse("");
        TestCase.assertEquals(spec.initialCapacity, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumSize, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumWeight, Caffeine.UNSET_INT);
        TestCase.assertNull(spec.keyStrength);
        TestCase.assertNull(spec.valueStrength);
        TestCase.assertNull(spec.expireAfterAccessTimeUnit);
        TestCase.assertNull(spec.expireAfterWriteTimeUnit);
        TestCase.assertNull(spec.refreshAfterWriteTimeUnit);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder(), Caffeine.from(spec));
    }

    public void testParse_initialCapacity() {
        CaffeineSpec spec = CaffeineSpec.parse("initialCapacity=10");
        TestCase.assertEquals(10, spec.initialCapacity);
        TestCase.assertEquals(spec.maximumSize, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumWeight, Caffeine.UNSET_INT);
        TestCase.assertNull(spec.keyStrength);
        TestCase.assertNull(spec.valueStrength);
        TestCase.assertNull(spec.expireAfterWriteTimeUnit);
        TestCase.assertNull(spec.expireAfterAccessTimeUnit);
        TestCase.assertNull(spec.refreshAfterWriteTimeUnit);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().initialCapacity(10), Caffeine.from(spec));
    }

    public void testParse_initialCapacityRepeated() {
        try {
            CaffeineSpec.parse("initialCapacity=10, initialCapacity=20");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_maximumSize() {
        CaffeineSpec spec = CaffeineSpec.parse("maximumSize=9000");
        TestCase.assertEquals(spec.initialCapacity, Caffeine.UNSET_INT);
        TestCase.assertEquals(9000, spec.maximumSize);
        TestCase.assertNull(spec.keyStrength);
        TestCase.assertNull(spec.valueStrength);
        TestCase.assertNull(spec.expireAfterWriteTimeUnit);
        TestCase.assertNull(spec.expireAfterAccessTimeUnit);
        TestCase.assertNull(spec.refreshAfterWriteTimeUnit);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().maximumSize(9000), Caffeine.from(spec));
    }

    public void testParse_maximumSizeRepeated() {
        try {
            CaffeineSpec.parse("maximumSize=10, maximumSize=20");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_maximumWeight() {
        CaffeineSpec spec = CaffeineSpec.parse("maximumWeight=9000");
        TestCase.assertEquals(spec.initialCapacity, Caffeine.UNSET_INT);
        TestCase.assertEquals(9000, spec.maximumWeight);
        TestCase.assertNull(spec.keyStrength);
        TestCase.assertNull(spec.valueStrength);
        TestCase.assertNull(spec.expireAfterWriteTimeUnit);
        TestCase.assertNull(spec.expireAfterAccessTimeUnit);
        TestCase.assertNull(spec.refreshAfterWriteTimeUnit);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().maximumWeight(9000), Caffeine.from(spec));
    }

    public void testParse_maximumWeightRepeated() {
        try {
            CaffeineSpec.parse("maximumWeight=10, maximumWeight=20");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_maximumSizeAndMaximumWeight() {
        try {
            CaffeineSpec.parse("maximumSize=10, maximumWeight=20");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_weakKeys() {
        CaffeineSpec spec = CaffeineSpec.parse("weakKeys");
        TestCase.assertEquals(spec.initialCapacity, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumSize, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumWeight, Caffeine.UNSET_INT);
        TestCase.assertEquals(WEAK, spec.keyStrength);
        TestCase.assertNull(spec.valueStrength);
        TestCase.assertNull(spec.expireAfterWriteTimeUnit);
        TestCase.assertNull(spec.expireAfterAccessTimeUnit);
        TestCase.assertNull(spec.refreshAfterWriteTimeUnit);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().weakKeys(), Caffeine.from(spec));
    }

    public void testParse_weakKeysCannotHaveValue() {
        try {
            CaffeineSpec.parse("weakKeys=true");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_repeatedKeyStrength() {
        try {
            CaffeineSpec.parse("weakKeys, weakKeys");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_softValues() {
        CaffeineSpec spec = CaffeineSpec.parse("softValues");
        TestCase.assertEquals(spec.initialCapacity, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumSize, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumWeight, Caffeine.UNSET_INT);
        TestCase.assertNull(spec.keyStrength);
        TestCase.assertEquals(SOFT, spec.valueStrength);
        TestCase.assertNull(spec.expireAfterWriteTimeUnit);
        TestCase.assertNull(spec.expireAfterAccessTimeUnit);
        TestCase.assertNull(spec.refreshAfterWriteTimeUnit);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().softValues(), Caffeine.from(spec));
    }

    public void testParse_softValuesCannotHaveValue() {
        try {
            CaffeineSpec.parse("softValues=true");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_weakValues() {
        CaffeineSpec spec = CaffeineSpec.parse("weakValues");
        TestCase.assertEquals(spec.initialCapacity, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumSize, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumWeight, Caffeine.UNSET_INT);
        TestCase.assertNull(spec.keyStrength);
        TestCase.assertEquals(WEAK, spec.valueStrength);
        TestCase.assertNull(spec.expireAfterWriteTimeUnit);
        TestCase.assertNull(spec.expireAfterAccessTimeUnit);
        TestCase.assertNull(spec.refreshAfterWriteTimeUnit);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().weakValues(), Caffeine.from(spec));
    }

    public void testParse_weakValuesCannotHaveValue() {
        try {
            CaffeineSpec.parse("weakValues=true");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_repeatedValueStrength() {
        try {
            CaffeineSpec.parse("softValues, softValues");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            CaffeineSpec.parse("softValues, weakValues");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            CaffeineSpec.parse("weakValues, softValues");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            CaffeineSpec.parse("weakValues, weakValues");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_writeExpirationDays() {
        CaffeineSpec spec = CaffeineSpec.parse("expireAfterWrite=10d");
        TestCase.assertEquals(spec.initialCapacity, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumSize, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumWeight, Caffeine.UNSET_INT);
        TestCase.assertNull(spec.keyStrength);
        TestCase.assertNull(spec.valueStrength);
        TestCase.assertEquals(TimeUnit.DAYS, spec.expireAfterWriteTimeUnit);
        TestCase.assertEquals(10L, spec.expireAfterWriteDuration);
        TestCase.assertNull(spec.expireAfterAccessTimeUnit);
        TestCase.assertNull(spec.refreshAfterWriteTimeUnit);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().expireAfterWrite(10L, TimeUnit.DAYS), Caffeine.from(spec));
    }

    public void testParse_writeExpirationHours() {
        CaffeineSpec spec = CaffeineSpec.parse("expireAfterWrite=150h");
        TestCase.assertEquals(TimeUnit.HOURS, spec.expireAfterWriteTimeUnit);
        TestCase.assertEquals(150L, spec.expireAfterWriteDuration);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().expireAfterWrite(150L, TimeUnit.HOURS), Caffeine.from(spec));
    }

    public void testParse_writeExpirationMinutes() {
        CaffeineSpec spec = CaffeineSpec.parse("expireAfterWrite=10m");
        TestCase.assertEquals(TimeUnit.MINUTES, spec.expireAfterWriteTimeUnit);
        TestCase.assertEquals(10L, spec.expireAfterWriteDuration);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().expireAfterWrite(10L, TimeUnit.MINUTES), Caffeine.from(spec));
    }

    public void testParse_writeExpirationSeconds() {
        CaffeineSpec spec = CaffeineSpec.parse("expireAfterWrite=10s");
        TestCase.assertEquals(TimeUnit.SECONDS, spec.expireAfterWriteTimeUnit);
        TestCase.assertEquals(10L, spec.expireAfterWriteDuration);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().expireAfterWrite(10L, TimeUnit.SECONDS), Caffeine.from(spec));
    }

    public void testParse_writeExpirationRepeated() {
        try {
            CaffeineSpec.parse("expireAfterWrite=10s,expireAfterWrite=10m");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_accessExpirationDays() {
        CaffeineSpec spec = CaffeineSpec.parse("expireAfterAccess=10d");
        TestCase.assertEquals(spec.initialCapacity, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumSize, Caffeine.UNSET_INT);
        TestCase.assertEquals(spec.maximumWeight, Caffeine.UNSET_INT);
        TestCase.assertNull(spec.keyStrength);
        TestCase.assertNull(spec.valueStrength);
        TestCase.assertNull(spec.expireAfterWriteTimeUnit);
        TestCase.assertEquals(TimeUnit.DAYS, spec.expireAfterAccessTimeUnit);
        TestCase.assertEquals(10L, spec.expireAfterAccessDuration);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().expireAfterAccess(10L, TimeUnit.DAYS), Caffeine.from(spec));
    }

    public void testParse_accessExpirationHours() {
        CaffeineSpec spec = CaffeineSpec.parse("expireAfterAccess=150h");
        TestCase.assertEquals(TimeUnit.HOURS, spec.expireAfterAccessTimeUnit);
        TestCase.assertEquals(150L, spec.expireAfterAccessDuration);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().expireAfterAccess(150L, TimeUnit.HOURS), Caffeine.from(spec));
    }

    public void testParse_accessExpirationMinutes() {
        CaffeineSpec spec = CaffeineSpec.parse("expireAfterAccess=10m");
        TestCase.assertEquals(TimeUnit.MINUTES, spec.expireAfterAccessTimeUnit);
        TestCase.assertEquals(10L, spec.expireAfterAccessDuration);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().expireAfterAccess(10L, TimeUnit.MINUTES), Caffeine.from(spec));
    }

    public void testParse_accessExpirationSeconds() {
        CaffeineSpec spec = CaffeineSpec.parse("expireAfterAccess=10s");
        TestCase.assertEquals(TimeUnit.SECONDS, spec.expireAfterAccessTimeUnit);
        TestCase.assertEquals(10L, spec.expireAfterAccessDuration);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().expireAfterAccess(10L, TimeUnit.SECONDS), Caffeine.from(spec));
    }

    public void testParse_accessExpirationRepeated() {
        try {
            CaffeineSpec.parse("expireAfterAccess=10s,expireAfterAccess=10m");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_recordStats() {
        CaffeineSpec spec = CaffeineSpec.parse("recordStats");
        TestCase.assertTrue(spec.recordStats);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().recordStats(), Caffeine.from(spec));
    }

    public void testParse_recordStatsValueSpecified() {
        try {
            CaffeineSpec.parse("recordStats=True");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_recordStatsRepeated() {
        try {
            CaffeineSpec.parse("recordStats,recordStats");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testParse_accessExpirationAndWriteExpiration() {
        CaffeineSpec spec = CaffeineSpec.parse("expireAfterAccess=10s,expireAfterWrite=9m");
        TestCase.assertEquals(TimeUnit.MINUTES, spec.expireAfterWriteTimeUnit);
        TestCase.assertEquals(9L, spec.expireAfterWriteDuration);
        TestCase.assertEquals(TimeUnit.SECONDS, spec.expireAfterAccessTimeUnit);
        TestCase.assertEquals(10L, spec.expireAfterAccessDuration);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(Caffeine.newBuilder().expireAfterAccess(10L, TimeUnit.SECONDS).expireAfterWrite(9L, TimeUnit.MINUTES), Caffeine.from(spec));
    }

    public void testParse_multipleKeys() {
        CaffeineSpec spec = CaffeineSpec.parse(("initialCapacity=10,maximumSize=20," + "weakKeys,weakValues,expireAfterAccess=10m,expireAfterWrite=1h"));
        TestCase.assertEquals(10, spec.initialCapacity);
        TestCase.assertEquals(20, spec.maximumSize);
        TestCase.assertEquals(spec.maximumWeight, Caffeine.UNSET_INT);
        TestCase.assertEquals(WEAK, spec.keyStrength);
        TestCase.assertEquals(WEAK, spec.valueStrength);
        TestCase.assertEquals(TimeUnit.HOURS, spec.expireAfterWriteTimeUnit);
        TestCase.assertEquals(TimeUnit.MINUTES, spec.expireAfterAccessTimeUnit);
        TestCase.assertEquals(1L, spec.expireAfterWriteDuration);
        TestCase.assertEquals(10L, spec.expireAfterAccessDuration);
        Caffeine<?, ?> expected = Caffeine.newBuilder().initialCapacity(10).maximumSize(20).weakKeys().weakValues().expireAfterAccess(10L, TimeUnit.MINUTES).expireAfterWrite(1L, TimeUnit.HOURS);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(expected, Caffeine.from(spec));
    }

    public void testParse_whitespaceAllowed() {
        CaffeineSpec spec = CaffeineSpec.parse((" initialCapacity=10,\nmaximumSize=20,\t\r" + "weakKeys \t ,softValues \n , \r  expireAfterWrite \t =  15s\n\n"));
        TestCase.assertEquals(10, spec.initialCapacity);
        TestCase.assertEquals(20, spec.maximumSize);
        TestCase.assertEquals(spec.maximumWeight, Caffeine.UNSET_INT);
        TestCase.assertEquals(WEAK, spec.keyStrength);
        TestCase.assertEquals(SOFT, spec.valueStrength);
        TestCase.assertEquals(TimeUnit.SECONDS, spec.expireAfterWriteTimeUnit);
        TestCase.assertEquals(15L, spec.expireAfterWriteDuration);
        TestCase.assertNull(spec.expireAfterAccessTimeUnit);
        Caffeine<?, ?> expected = Caffeine.newBuilder().initialCapacity(10).maximumSize(20).weakKeys().softValues().expireAfterWrite(15L, TimeUnit.SECONDS);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(expected, Caffeine.from(spec));
    }

    public void testParse_unknownKey() {
        try {
            CaffeineSpec.parse("foo=17");
            TestCase.fail("Expected exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testEqualsAndHashCode() {
        new EqualsTester().addEqualityGroup(CaffeineSpec.parse(""), CaffeineSpec.parse("")).addEqualityGroup(CaffeineSpec.parse("initialCapacity=7"), CaffeineSpec.parse("initialCapacity=7")).addEqualityGroup(CaffeineSpec.parse("initialCapacity=15"), CaffeineSpec.parse("initialCapacity=15")).addEqualityGroup(CaffeineSpec.parse("maximumSize=7"), CaffeineSpec.parse("maximumSize=7")).addEqualityGroup(CaffeineSpec.parse("maximumSize=15"), CaffeineSpec.parse("maximumSize=15")).addEqualityGroup(CaffeineSpec.parse("maximumWeight=7"), CaffeineSpec.parse("maximumWeight=7")).addEqualityGroup(CaffeineSpec.parse("maximumWeight=15"), CaffeineSpec.parse("maximumWeight=15")).addEqualityGroup(CaffeineSpec.parse("expireAfterAccess=60s"), CaffeineSpec.parse("expireAfterAccess=1m")).addEqualityGroup(CaffeineSpec.parse("expireAfterAccess=60m"), CaffeineSpec.parse("expireAfterAccess=1h")).addEqualityGroup(CaffeineSpec.parse("expireAfterWrite=60s"), CaffeineSpec.parse("expireAfterWrite=1m")).addEqualityGroup(CaffeineSpec.parse("expireAfterWrite=60m"), CaffeineSpec.parse("expireAfterWrite=1h")).addEqualityGroup(CaffeineSpec.parse("weakKeys"), CaffeineSpec.parse("weakKeys")).addEqualityGroup(CaffeineSpec.parse("softValues"), CaffeineSpec.parse("softValues")).addEqualityGroup(CaffeineSpec.parse("weakValues"), CaffeineSpec.parse("weakValues")).addEqualityGroup(CaffeineSpec.parse("recordStats"), CaffeineSpec.parse("recordStats")).testEquals();
    }

    public void testMaximumWeight_withWeigher() {
        Caffeine<Object, Object> builder = Caffeine.from(CaffeineSpec.parse("maximumWeight=9000"));
        builder.weigher(( k, v) -> 42).build(( k) -> null);
    }

    public void testMaximumWeight_withoutWeigher() {
        Caffeine<Object, Object> builder = Caffeine.from(CaffeineSpec.parse("maximumWeight=9000"));
        try {
            builder.build(( k) -> null);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testMaximumSize_withWeigher() {
        Caffeine<Object, Object> builder = Caffeine.from(CaffeineSpec.parse("maximumSize=9000"));
        builder.weigher(( k, v) -> 42).build(( k) -> null);
    }

    public void testMaximumSize_withoutWeigher() {
        Caffeine<Object, Object> builder = Caffeine.from(CaffeineSpec.parse("maximumSize=9000"));
        builder.build(( k) -> null);
    }

    public void testCaffeineFrom_string() {
        Caffeine<?, ?> fromString = Caffeine.from("initialCapacity=10,maximumSize=20,weakKeys,weakValues,expireAfterAccess=10m");
        Caffeine<?, ?> expected = Caffeine.newBuilder().initialCapacity(10).maximumSize(20).weakKeys().weakValues().expireAfterAccess(10L, TimeUnit.MINUTES);
        CaffeineSpecGuavaTest.assertCaffeineEquivalence(expected, fromString);
    }
}

