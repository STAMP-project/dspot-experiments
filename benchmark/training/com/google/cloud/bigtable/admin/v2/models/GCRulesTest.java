/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.admin.v2.models;


import com.google.bigtable.admin.v2.GcRule;
import com.google.bigtable.admin.v2.GcRule.Intersection;
import com.google.bigtable.admin.v2.GcRule.Union;
import com.google.cloud.bigtable.admin.v2.models.GCRules.DurationRule;
import com.google.cloud.bigtable.admin.v2.models.GCRules.IntersectionRule;
import com.google.cloud.bigtable.admin.v2.models.GCRules.UnionRule;
import com.google.cloud.bigtable.admin.v2.models.GCRules.VersionRule;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.Duration;


@RunWith(JUnit4.class)
public class GCRulesTest {
    @Test
    public void duration() {
        DurationRule actual = GCRules.GCRULES.maxAge(Duration.ofSeconds(61, 9));
        GcRule expected = GCRulesTest.buildAgeRule(61, 9);
        Assert.assertNotNull(actual.getMaxAge());
        assertThat(actual.toProto()).isEqualTo(expected);
    }

    @Test
    public void durationSeconds() {
        GcRule actual = GCRules.GCRULES.maxAge(Duration.ofSeconds(1)).toProto();
        GcRule expected = GCRulesTest.buildAgeRule(1, 0);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void durationNanos() {
        GcRule actual = GCRules.GCRULES.maxAge(Duration.ofNanos(11)).toProto();
        GcRule expected = GCRulesTest.buildAgeRule(0, 11);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void durationTimeUnitSeconds() {
        GcRule actual = GCRules.GCRULES.maxAge(1, TimeUnit.DAYS).toProto();
        GcRule expected = GCRulesTest.buildAgeRule((3600 * 24), 0);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void durationTimeUnitMinutes() {
        GcRule actual = GCRules.GCRULES.maxAge(1, TimeUnit.MINUTES).toProto();
        GcRule expected = GCRulesTest.buildAgeRule(60, 0);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void durationTimeUnitNanos() {
        GcRule actual = GCRules.GCRULES.maxAge(1, TimeUnit.NANOSECONDS).toProto();
        GcRule expected = GCRulesTest.buildAgeRule(0, 1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void durationTimeUnitNegative() {
        GcRule actual = GCRules.GCRULES.maxAge((-1), TimeUnit.MINUTES).toProto();
        GcRule expected = GCRulesTest.buildAgeRule((-60), 0);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void versions() {
        VersionRule actual = GCRules.GCRULES.maxVersions(10);
        GcRule expected = GCRulesTest.buildVersionsRule(10);
        Assert.assertNotNull(actual.getMaxVersions());
        assertThat(actual.toProto()).isEqualTo(expected);
    }

    @Test
    public void unionEmpty() {
        GcRule actual = GCRules.GCRULES.union().toProto();
        GcRule expected = GcRule.newBuilder().build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void unionOne() {
        GcRule actual = GCRules.GCRULES.union().rule(GCRules.GCRULES.maxVersions(1)).toProto();
        GcRule expected = GCRulesTest.buildVersionsRule(1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void unionTwo() {
        GcRule actual = GCRules.GCRULES.union().rule(GCRules.GCRULES.maxVersions(1)).rule(GCRules.GCRULES.maxAge(Duration.ofSeconds(1))).toProto();
        GcRule expected = GcRule.newBuilder().setUnion(Union.newBuilder().addRules(GCRulesTest.buildVersionsRule(1)).addRules(GCRulesTest.buildAgeRule(1, 0))).build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void unionThree() {
        GcRule actual = GCRules.GCRULES.union().rule(GCRules.GCRULES.maxVersions(1)).rule(GCRules.GCRULES.maxAge(Duration.ofSeconds(1))).rule(GCRules.GCRULES.maxAge(Duration.ofNanos(1))).toProto();
        GcRule expected = GcRule.newBuilder().setUnion(Union.newBuilder().addRules(GCRulesTest.buildVersionsRule(1)).addRules(GCRulesTest.buildAgeRule(1, 0)).addRules(GCRulesTest.buildAgeRule(0, 1))).build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void intersectionEmpty() {
        GcRule actual = GCRules.GCRULES.intersection().toProto();
        GcRule expected = GcRule.newBuilder().build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void intersectionOne() {
        GcRule actual = GCRules.GCRULES.intersection().rule(GCRules.GCRULES.maxVersions(1)).toProto();
        GcRule expected = GCRulesTest.buildVersionsRule(1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void intersectionTwo() {
        GcRule actual = GCRules.GCRULES.intersection().rule(GCRules.GCRULES.maxVersions(1)).rule(GCRules.GCRULES.maxAge(Duration.ofSeconds(1))).toProto();
        GcRule expected = GcRule.newBuilder().setIntersection(Intersection.newBuilder().addRules(GCRulesTest.buildVersionsRule(1)).addRules(GCRulesTest.buildAgeRule(1, 0))).build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void intersectionThree() {
        GcRule actual = GCRules.GCRULES.intersection().rule(GCRules.GCRULES.maxVersions(1)).rule(GCRules.GCRULES.maxAge(Duration.ofSeconds(1))).rule(GCRules.GCRULES.maxAge(Duration.ofNanos(1))).toProto();
        GcRule expected = GcRule.newBuilder().setIntersection(Intersection.newBuilder().addRules(GCRulesTest.buildVersionsRule(1)).addRules(GCRulesTest.buildAgeRule(1, 0)).addRules(GCRulesTest.buildAgeRule(0, 1))).build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void unionOfIntersections() {
        UnionRule actual = GCRules.GCRULES.union().rule(GCRules.GCRULES.intersection().rule(GCRules.GCRULES.maxVersions(1)).rule(GCRules.GCRULES.maxAge(Duration.ofSeconds(1)))).rule(GCRules.GCRULES.intersection().rule(GCRules.GCRULES.maxVersions(1)).rule(GCRules.GCRULES.maxAge(Duration.ofSeconds(1))));
        GcRule expected = GcRule.newBuilder().setUnion(Union.newBuilder().addRules(GcRule.newBuilder().setIntersection(Intersection.newBuilder().addRules(GCRulesTest.buildVersionsRule(1)).addRules(GCRulesTest.buildAgeRule(1, 0)))).addRules(GcRule.newBuilder().setIntersection(Intersection.newBuilder().addRules(GCRulesTest.buildVersionsRule(1)).addRules(GCRulesTest.buildAgeRule(1, 0))))).build();
        Assert.assertEquals(2, actual.getRulesList().size());
        assertThat(actual.toProto()).isEqualTo(expected);
    }

    @Test
    public void intersectionOfUnions() {
        IntersectionRule actual = GCRules.GCRULES.intersection().rule(GCRules.GCRULES.union().rule(GCRules.GCRULES.maxVersions(1)).rule(GCRules.GCRULES.maxAge(Duration.ofSeconds(1)))).rule(GCRules.GCRULES.union().rule(GCRules.GCRULES.maxVersions(1)).rule(GCRules.GCRULES.maxAge(Duration.ofSeconds(1))));
        GcRule expected = GcRule.newBuilder().setIntersection(Intersection.newBuilder().addRules(GcRule.newBuilder().setUnion(Union.newBuilder().addRules(GCRulesTest.buildVersionsRule(1)).addRules(GCRulesTest.buildAgeRule(1, 0)))).addRules(GcRule.newBuilder().setUnion(Union.newBuilder().addRules(GCRulesTest.buildVersionsRule(1)).addRules(GCRulesTest.buildAgeRule(1, 0))))).build();
        Assert.assertEquals(2, actual.getRulesList().size());
        assertThat(actual.toProto()).isEqualTo(expected);
    }
}

