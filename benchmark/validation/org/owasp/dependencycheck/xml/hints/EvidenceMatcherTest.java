/**
 * This file is part of dependency-check-core.
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
 *
 * Copyright (c) 2017 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.xml.hints;


import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.dependency.Confidence;
import org.owasp.dependencycheck.dependency.Evidence;


/**
 * Unit tests for {@link EvidenceMatcher}.
 *
 * @author Hans Aikema
 */
public class EvidenceMatcherTest {
    private static final Evidence EVIDENCE_HIGHEST = new Evidence("source", "name", "value", Confidence.HIGHEST);

    private static final Evidence EVIDENCE_HIGH = new Evidence("source", "name", "value", Confidence.HIGH);

    private static final Evidence EVIDENCE_MEDIUM = new Evidence("source", "name", "value", Confidence.MEDIUM);

    private static final Evidence EVIDENCE_MEDIUM_SECOND_SOURCE = new Evidence("source 2", "name", "value", Confidence.MEDIUM);

    private static final Evidence EVIDENCE_LOW = new Evidence("source", "name", "value", Confidence.LOW);

    private static final Evidence REGEX_EVIDENCE_HIGHEST = new Evidence("source", "name", "value 1", Confidence.HIGHEST);

    private static final Evidence REGEX_EVIDENCE_HIGH = new Evidence("source", "name", "value 2", Confidence.HIGH);

    private static final Evidence REGEX_EVIDENCE_MEDIUM = new Evidence("source", "name", "Value will not match because of case", Confidence.MEDIUM);

    private static final Evidence REGEX_EVIDENCE_MEDIUM_SECOND_SOURCE = new Evidence("source 2", "name", "yet another value that will match", Confidence.MEDIUM);

    private static final Evidence REGEX_EVIDENCE_MEDIUM_THIRD_SOURCE = new Evidence("source 3", "name", "and even more values to match", Confidence.MEDIUM);

    private static final Evidence REGEX_EVIDENCE_LOW = new Evidence("source", "name", "val that should not match", Confidence.LOW);

    @Test
    public void testExactMatching() throws Exception {
        final EvidenceMatcher exactMatcherHighest = new EvidenceMatcher("source", "name", "value", false, Confidence.HIGHEST);
        Assert.assertTrue("exact matcher should match EVIDENCE_HIGHEST", exactMatcherHighest.matches(EvidenceMatcherTest.EVIDENCE_HIGHEST));
        Assert.assertFalse("exact matcher should not match EVIDENCE_HIGH", exactMatcherHighest.matches(EvidenceMatcherTest.EVIDENCE_HIGH));
        Assert.assertFalse("exact matcher should not match EVIDENCE_MEDIUM", exactMatcherHighest.matches(EvidenceMatcherTest.EVIDENCE_MEDIUM));
        Assert.assertFalse("exact matcher should not match EVIDENCE_MEDIUM_SECOND_SOURCE", exactMatcherHighest.matches(EvidenceMatcherTest.EVIDENCE_MEDIUM_SECOND_SOURCE));
        Assert.assertFalse("exact matcher should not match EVIDENCE_LOW", exactMatcherHighest.matches(EvidenceMatcherTest.EVIDENCE_LOW));
    }

    @Test
    public void testWildcardConfidenceMatching() throws Exception {
        final EvidenceMatcher wildcardCofidenceMatcher = new EvidenceMatcher("source", "name", "value", false, null);
        Assert.assertTrue("wildcard confidence matcher should match EVIDENCE_HIGHEST", wildcardCofidenceMatcher.matches(EvidenceMatcherTest.EVIDENCE_HIGHEST));
        Assert.assertTrue("wildcard confidence matcher should match EVIDENCE_HIGH", wildcardCofidenceMatcher.matches(EvidenceMatcherTest.EVIDENCE_HIGH));
        Assert.assertTrue("wildcard confidence matcher should match EVIDENCE_MEDIUM", wildcardCofidenceMatcher.matches(EvidenceMatcherTest.EVIDENCE_MEDIUM));
        Assert.assertFalse("wildcard confidence matcher should not match EVIDENCE_MEDIUM_SECOND_SOURCE", wildcardCofidenceMatcher.matches(EvidenceMatcherTest.EVIDENCE_MEDIUM_SECOND_SOURCE));
        Assert.assertTrue("wildcard confidence matcher should match EVIDENCE_LOW", wildcardCofidenceMatcher.matches(EvidenceMatcherTest.EVIDENCE_LOW));
    }

    @Test
    public void testWildcardSourceMatching() throws Exception {
        final EvidenceMatcher wildcardSourceMatcher = new EvidenceMatcher(null, "name", "value", false, Confidence.MEDIUM);
        Assert.assertFalse("wildcard source matcher should not match EVIDENCE_HIGHEST", wildcardSourceMatcher.matches(EvidenceMatcherTest.EVIDENCE_HIGHEST));
        Assert.assertFalse("wildcard source matcher should not match EVIDENCE_HIGH", wildcardSourceMatcher.matches(EvidenceMatcherTest.EVIDENCE_HIGH));
        Assert.assertTrue("wildcard source matcher should match EVIDENCE_MEDIUM", wildcardSourceMatcher.matches(EvidenceMatcherTest.EVIDENCE_MEDIUM));
        Assert.assertTrue("wildcard source matcher should match EVIDENCE_MEDIUM_SECOND_SOURCE", wildcardSourceMatcher.matches(EvidenceMatcherTest.EVIDENCE_MEDIUM_SECOND_SOURCE));
        Assert.assertFalse("wildcard source matcher should not match EVIDENCE_LOW", wildcardSourceMatcher.matches(EvidenceMatcherTest.EVIDENCE_LOW));
    }

    @Test
    public void testRegExMatching() throws Exception {
        final EvidenceMatcher regexMediumMatcher = new EvidenceMatcher("source 2", "name", ".*value.*", true, Confidence.MEDIUM);
        Assert.assertFalse("regex medium matcher should not match REGEX_EVIDENCE_HIGHEST", regexMediumMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_HIGHEST));
        Assert.assertFalse("regex medium matcher should not match REGEX_EVIDENCE_HIGH", regexMediumMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_HIGH));
        Assert.assertFalse("regex medium matcher should not match REGEX_EVIDENCE_MEDIUM", regexMediumMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM));
        Assert.assertTrue("regex medium matcher should match REGEX_EVIDENCE_MEDIUM_SECOND_SOURCE", regexMediumMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM_SECOND_SOURCE));
        Assert.assertFalse("regex medium matcher should not match REGEX_EVIDENCE_MEDIUM_THIRD_SOURCE", regexMediumMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM_THIRD_SOURCE));
        Assert.assertFalse("regex medium matcher should not match REGEX_EVIDENCE_LOW", regexMediumMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_LOW));
    }

    @Test
    public void testRegExWildcardSourceMatching() throws Exception {
        final EvidenceMatcher regexMediumWildcardSourceMatcher = new EvidenceMatcher(null, "name", "^.*v[al]{2,2}ue[a-z ]+$", true, Confidence.MEDIUM);
        Assert.assertFalse("regex medium wildcard source matcher should not match REGEX_EVIDENCE_HIGHEST", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_HIGHEST));
        Assert.assertFalse("regex medium wildcard source matcher should not match REGEX_EVIDENCE_HIGH", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_HIGH));
        Assert.assertFalse("regex medium wildcard source matcher should not match REGEX_EVIDENCE_MEDIUM", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM));
        Assert.assertTrue("regex medium wildcard source matcher should match REGEX_EVIDENCE_MEDIUM_SECOND_SOURCE", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM_SECOND_SOURCE));
        Assert.assertTrue("regex medium wildcard source matcher should match REGEX_EVIDENCE_MEDIUM_THIRD_SOURCE", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM_THIRD_SOURCE));
        Assert.assertFalse("regex medium wildcard source matcher should not match REGEX_EVIDENCE_LOW", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_LOW));
    }

    @Test
    public void testRegExWildcardSourceWildcardConfidenceMatching() throws Exception {
        final EvidenceMatcher regexMediumWildcardSourceMatcher = new EvidenceMatcher(null, "name", ".*value.*", true, null);
        Assert.assertTrue("regex wildcard source wildcard confidence matcher should match REGEX_EVIDENCE_HIGHEST", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_HIGHEST));
        Assert.assertTrue("regex wildcard source wildcard confidence matcher should match REGEX_EVIDENCE_HIGH", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_HIGH));
        Assert.assertFalse("regex wildcard source wildcard confidence matcher should not match REGEX_EVIDENCE_MEDIUM", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM));
        Assert.assertTrue("regex wildcard source wildcard confidence matcher should match REGEX_EVIDENCE_MEDIUM_SECOND_SOURCE", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM_SECOND_SOURCE));
        Assert.assertTrue("regex wildcard source wildcard confidence matcher should match REGEX_EVIDENCE_MEDIUM_THIRD_SOURCE", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM_THIRD_SOURCE));
        Assert.assertFalse("regex wildcard source wildcard confidence matcher should match REGEX_EVIDENCE_LOW", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_LOW));
    }

    @Test
    public void testRegExWildcardSourceWildcardConfidenceFourMatching() throws Exception {
        final EvidenceMatcher regexMediumWildcardSourceMatcher = new EvidenceMatcher(null, "name", "^.*[Vv][al]{2,2}[a-z ]+$", true, null);
        Assert.assertFalse("regex wildcard source wildcard confidence matcher should not match REGEX_EVIDENCE_HIGHEST", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_HIGHEST));
        Assert.assertFalse("regex wildcard source wildcard confidence matcher should not match REGEX_EVIDENCE_HIGH", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_HIGH));
        Assert.assertTrue("regex wildcard source wildcard confidence matcher should match REGEX_EVIDENCE_MEDIUM", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM));
        Assert.assertTrue("regex wildcard source wildcard confidence matcher should match REGEX_EVIDENCE_MEDIUM_SECOND_SOURCE", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM_SECOND_SOURCE));
        Assert.assertTrue("regex wildcard source wildcard confidence matcher should match REGEX_EVIDENCE_MEDIUM_THIRD_SOURCE", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_MEDIUM_THIRD_SOURCE));
        Assert.assertTrue("regex wildcard source wildcard confidence matcher should match REGEX_EVIDENCE_LOW", regexMediumWildcardSourceMatcher.matches(EvidenceMatcherTest.REGEX_EVIDENCE_LOW));
    }
}

