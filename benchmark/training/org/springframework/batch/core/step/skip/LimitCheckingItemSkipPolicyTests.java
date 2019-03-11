/**
 * Copyright 2006-2008 the original author or authors.
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
package org.springframework.batch.core.step.skip;


import java.io.FileNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.WriteFailedException;
import org.springframework.batch.item.WriterNotOpenException;
import org.springframework.batch.item.file.FlatFileParseException;


/**
 *
 *
 * @author Lucas Ward
 * @author Dave Syer
 */
public class LimitCheckingItemSkipPolicyTests {
    private LimitCheckingItemSkipPolicy failurePolicy;

    @Test
    public void testLimitExceed() {
        try {
            failurePolicy.shouldSkip(new FlatFileParseException("", ""), 2);
            Assert.fail();
        } catch (SkipLimitExceededException ex) {
            // expected
        }
    }

    @Test
    public void testNonSkippableException() {
        Assert.assertFalse(failurePolicy.shouldSkip(new FileNotFoundException(), 2));
    }

    @Test
    public void testSkip() {
        Assert.assertTrue(failurePolicy.shouldSkip(new FlatFileParseException("", ""), 0));
    }

    /**
     * condition: skippable < fatal; exception is unclassified
     *
     * expected: false; default classification
     */
    @Test
    public void testSkippableSubset_unclassified() {
        Assert.assertFalse(getSkippableSubsetSkipPolicy().shouldSkip(new RuntimeException(), 0));
    }

    /**
     * condition: skippable < fatal; exception is skippable
     *
     * expected: true
     */
    @Test
    public void testSkippableSubset_skippable() {
        Assert.assertTrue(getSkippableSubsetSkipPolicy().shouldSkip(new WriteFailedException(""), 0));
    }

    /**
     * condition: skippable < fatal; exception is fatal
     *
     * expected: false
     */
    @Test
    public void testSkippableSubset_fatal() {
        Assert.assertFalse(getSkippableSubsetSkipPolicy().shouldSkip(new WriterNotOpenException(""), 0));
    }

    /**
     * condition: fatal < skippable; exception is unclassified
     *
     * expected: false; default classification
     */
    @Test
    public void testFatalSubset_unclassified() {
        Assert.assertFalse(getFatalSubsetSkipPolicy().shouldSkip(new RuntimeException(), 0));
    }

    /**
     * condition: fatal < skippable; exception is skippable
     *
     * expected: true
     */
    @Test
    public void testFatalSubset_skippable() {
        Assert.assertTrue(getFatalSubsetSkipPolicy().shouldSkip(new WriterNotOpenException(""), 0));
    }

    /**
     * condition: fatal < skippable; exception is fatal
     *
     * expected: false
     */
    @Test
    public void testFatalSubset_fatal() {
        Assert.assertFalse(getFatalSubsetSkipPolicy().shouldSkip(new WriteFailedException(""), 0));
    }
}

