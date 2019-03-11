/**
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.file.separator;


import junit.framework.TestCase;

import static SuffixRecordSeparatorPolicy.DEFAULT_SUFFIX;


public class SuffixRecordSeparatorPolicyTests extends TestCase {
    private static final String LINE = "a string";

    SuffixRecordSeparatorPolicy policy = new SuffixRecordSeparatorPolicy();

    public void testNormalLine() throws Exception {
        TestCase.assertFalse(policy.isEndOfRecord(SuffixRecordSeparatorPolicyTests.LINE));
    }

    public void testNormalLineWithDefaultSuffix() throws Exception {
        TestCase.assertTrue(policy.isEndOfRecord(((SuffixRecordSeparatorPolicyTests.LINE) + (DEFAULT_SUFFIX))));
    }

    public void testNormalLineWithNonDefaultSuffix() throws Exception {
        policy.setSuffix(":foo");
        TestCase.assertTrue(policy.isEndOfRecord(((SuffixRecordSeparatorPolicyTests.LINE) + ":foo")));
    }

    public void testNormalLineWithDefaultSuffixAndWhitespace() throws Exception {
        TestCase.assertTrue(policy.isEndOfRecord((((SuffixRecordSeparatorPolicyTests.LINE) + (DEFAULT_SUFFIX)) + "  ")));
    }

    public void testNormalLineWithDefaultSuffixWithIgnoreWhitespace() throws Exception {
        policy.setIgnoreWhitespace(false);
        TestCase.assertFalse(policy.isEndOfRecord((((SuffixRecordSeparatorPolicyTests.LINE) + (DEFAULT_SUFFIX)) + "  ")));
    }

    public void testEmptyLine() throws Exception {
        TestCase.assertFalse(policy.isEndOfRecord(""));
    }

    public void testNullLineIsEndOfRecord() throws Exception {
        TestCase.assertTrue(policy.isEndOfRecord(null));
    }

    public void testPostProcessSunnyDay() throws Exception {
        String line = SuffixRecordSeparatorPolicyTests.LINE;
        String record = line + (DEFAULT_SUFFIX);
        TestCase.assertEquals(line, policy.postProcess(record));
    }

    public void testPostProcessNullLine() throws Exception {
        String line = null;
        TestCase.assertEquals(null, policy.postProcess(line));
    }
}

