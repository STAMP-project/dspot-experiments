package org.elasticsearch.gradle;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.elasticsearch.gradle.test.GradleUnitTestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/* Licensed to Elasticsearch under one or more contributor
license agreements. See the NOTICE file distributed with
this work for additional information regarding copyright
ownership. Elasticsearch licenses this file to you under
the Apache License, Version 2.0 (the "License"); you may
not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */
public class BwcVersionsTests extends GradleUnitTestCase {
    private static final Map<String, List<String>> sampleVersions = new HashMap<>();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    static {
        // unreleased major and two unreleased minors ( minor in feature freeze )
        BwcVersionsTests.sampleVersions.put("8.0.0", Arrays.asList("7_0_0", "7_0_1", "7_1_0", "7_1_1", "7_2_0", "7_3_0", "8.0.0"));
        BwcVersionsTests.sampleVersions.put("7.0.0-alpha1", Arrays.asList("6_0_0_alpha1", "6_0_0_alpha2", "6_0_0_beta1", "6_0_0_beta2", "6_0_0_rc1", "6_0_0_rc2", "6_0_0", "6_0_1", "6_1_0", "6_1_1", "6_1_2", "6_1_3", "6_1_4", "6_2_0", "6_2_1", "6_2_2", "6_2_3", "6_2_4", "6_3_0", "6_3_1", "6_3_2", "6_4_0", "6_4_1", "6_4_2", "6_5_0", "7_0_0_alpha1"));
        BwcVersionsTests.sampleVersions.put("6.5.0", Arrays.asList("5_0_0_alpha1", "5_0_0_alpha2", "5_0_0_alpha3", "5_0_0_alpha4", "5_0_0_alpha5", "5_0_0_beta1", "5_0_0_rc1", "5_0_0", "5_0_1", "5_0_2", "5_1_1", "5_1_2", "5_2_0", "5_2_1", "5_2_2", "5_3_0", "5_3_1", "5_3_2", "5_3_3", "5_4_0", "5_4_1", "5_4_2", "5_4_3", "5_5_0", "5_5_1", "5_5_2", "5_5_3", "5_6_0", "5_6_1", "5_6_2", "5_6_3", "5_6_4", "5_6_5", "5_6_6", "5_6_7", "5_6_8", "5_6_9", "5_6_10", "5_6_11", "5_6_12", "5_6_13", "6_0_0_alpha1", "6_0_0_alpha2", "6_0_0_beta1", "6_0_0_beta2", "6_0_0_rc1", "6_0_0_rc2", "6_0_0", "6_0_1", "6_1_0", "6_1_1", "6_1_2", "6_1_3", "6_1_4", "6_2_0", "6_2_1", "6_2_2", "6_2_3", "6_2_4", "6_3_0", "6_3_1", "6_3_2", "6_4_0", "6_4_1", "6_4_2", "6_5_0"));
        BwcVersionsTests.sampleVersions.put("6.6.0", Arrays.asList("5_0_0_alpha1", "5_0_0_alpha2", "5_0_0_alpha3", "5_0_0_alpha4", "5_0_0_alpha5", "5_0_0_beta1", "5_0_0_rc1", "5_0_0", "5_0_1", "5_0_2", "5_1_1", "5_1_2", "5_2_0", "5_2_1", "5_2_2", "5_3_0", "5_3_1", "5_3_2", "5_3_3", "5_4_0", "5_4_1", "5_4_2", "5_4_3", "5_5_0", "5_5_1", "5_5_2", "5_5_3", "5_6_0", "5_6_1", "5_6_2", "5_6_3", "5_6_4", "5_6_5", "5_6_6", "5_6_7", "5_6_8", "5_6_9", "5_6_10", "5_6_11", "5_6_12", "5_6_13", "6_0_0_alpha1", "6_0_0_alpha2", "6_0_0_beta1", "6_0_0_beta2", "6_0_0_rc1", "6_0_0_rc2", "6_0_0", "6_0_1", "6_1_0", "6_1_1", "6_1_2", "6_1_3", "6_1_4", "6_2_0", "6_2_1", "6_2_2", "6_2_3", "6_2_4", "6_3_0", "6_3_1", "6_3_2", "6_4_0", "6_4_1", "6_4_2", "6_5_0", "6_6_0"));
        BwcVersionsTests.sampleVersions.put("6.4.2", Arrays.asList("5_0_0_alpha1", "5_0_0_alpha2", "5_0_0_alpha3", "5_0_0_alpha4", "5_0_0_alpha5", "5_0_0_beta1", "5_0_0_rc1", "5_0_0", "5_0_1", "5_0_2", "5_1_1", "5_1_2", "5_2_0", "5_2_1", "5_2_2", "5_3_0", "5_3_1", "5_3_2", "5_3_3", "5_4_0", "5_4_1", "5_4_2", "5_4_3", "5_5_0", "5_5_1", "5_5_2", "5_5_3", "5_6_0", "5_6_1", "5_6_2", "5_6_3", "5_6_4", "5_6_5", "5_6_6", "5_6_7", "5_6_8", "5_6_9", "5_6_10", "5_6_11", "5_6_12", "5_6_13", "6_0_0_alpha1", "6_0_0_alpha2", "6_0_0_beta1", "6_0_0_beta2", "6_0_0_rc1", "6_0_0_rc2", "6_0_0", "6_0_1", "6_1_0", "6_1_1", "6_1_2", "6_1_3", "6_1_4", "6_2_0", "6_2_1", "6_2_2", "6_2_3", "6_2_4", "6_3_0", "6_3_1", "6_3_2", "6_4_0", "6_4_1", "6_4_2"));
        BwcVersionsTests.sampleVersions.put("7.1.0", Arrays.asList("7_1_0", "7_0_0", "6_7_0", "6_6_1", "6_6_0"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionOnEmpty() {
        new BwcVersions(Arrays.asList("foo", "bar"), Version.fromString("7.0.0"));
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionOnNonCurrent() {
        new BwcVersions(Collections.singletonList(formatVersionToLine("6.5.0")), Version.fromString("7.0.0"));
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionOnTooManyMajors() {
        new BwcVersions(Arrays.asList(formatVersionToLine("5.6.12"), formatVersionToLine("6.5.0"), formatVersionToLine("7.0.0")), Version.fromString("6.5.0"));
    }
}

