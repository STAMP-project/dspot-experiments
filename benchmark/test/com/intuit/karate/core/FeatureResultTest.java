/**
 * The MIT License
 *
 * Copyright 2017 Intuit Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.intuit.karate.core;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author vmchukky
 */
public class FeatureResultTest {
    private static final Logger logger = LoggerFactory.getLogger(FeatureResultTest.class);

    @Test
    public void testFailureMultiScenarioFeature() throws Exception {
        FeatureResult result = FeatureResultTest.result("failed.feature");
        Assert.assertEquals(2, result.getFailedCount());
        Assert.assertEquals(3, result.getScenarioCount());
        String contents = FeatureResultTest.xml(result);
        Assert.assertTrue(contents.contains("assert evaluated to false: a != 1"));
        Assert.assertTrue(contents.contains("assert evaluated to false: a == 3"));
        // failure1 should have first step as failure, and second step as skipped
        // TODO: generate the expected content string, below code puts a hard dependency
        // with KarateJunitFormatter$TestCase.addStepAndResultListing()
        Assert.assertTrue(contents.contains("Then assert a != 1 ........................................................ failed"));
        Assert.assertTrue(contents.contains("And assert a == 2 ......................................................... skipped"));
        // failure2 should have first step as passed, and second step as failed
        Assert.assertTrue(contents.contains("Then assert a != 2 ........................................................ passed"));
        Assert.assertTrue(contents.contains("And assert a == 3 ......................................................... failed"));
        // pass1 should have both steps as passed
        Assert.assertTrue(contents.contains("Then assert a != 4 ........................................................ passed"));
        Assert.assertTrue(contents.contains("And assert a != 5 ......................................................... passed"));
    }

    @Test
    public void testAbortMultiScenarioFeature() throws Exception {
        FeatureResult result = FeatureResultTest.result("aborted.feature");
        Assert.assertEquals(0, result.getFailedCount());
        Assert.assertEquals(3, result.getScenarioCount());
        String contents = FeatureResultTest.xml(result);
        // skip-pass and skip-fail both should have all steps as skipped
        // TODO: generate the expected content string, below code puts a hard dependency
        // with KarateJunitFormatter$TestCase.addStepAndResultListing()
        Assert.assertTrue(contents.contains("* eval karate.abort() ..................................................... skipped"));
        Assert.assertTrue(contents.contains("* assert a == 1 ........................................................... skipped"));
        Assert.assertTrue(contents.contains("* assert a == 2 ........................................................... skipped"));
        // noskip should have both steps as passed
        Assert.assertTrue(contents.contains("Then assert a != 3 ........................................................ passed"));
        Assert.assertTrue(contents.contains("And assert a != 4 ......................................................... passed"));
    }
}

