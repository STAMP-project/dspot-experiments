/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.pmml_4_2.model.mining;


import SimpleSegmentPredicate.EQUAL;
import SimpleSegmentPredicate.GREATER;
import SimpleSegmentPredicate.GREATER_EQUAL;
import SimpleSegmentPredicate.LESSER;
import SimpleSegmentPredicate.LESSER_EQUAL;
import SimpleSegmentPredicate.MISSING;
import SimpleSegmentPredicate.NOT_EQUAL;
import SimpleSegmentPredicate.NOT_MISSING;
import org.dmg.pmml.pmml_4_2.descr.SimplePredicate;
import org.junit.Assert;
import org.junit.Test;


public class SimpleSegmentPredicateTest {
    private SimplePredicate predicate;

    private SimplePredicate stringPred;

    private static final String BAD_OP = "invalidOp";

    @Test
    public void testStringValue() {
        stringPred.setOperator(EQUAL);
        SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(stringPred);
        String text = ssp.getPredicateRule();
        Assert.assertNotNull(text);
        Assert.assertEquals("( mTF3 == false ) && ( vTF3 == \"optA\" )", text);
        predicate.setOperator(EQUAL);
        ssp = new SimpleSegmentPredicate(predicate);
        text = ssp.getPredicateRule();
        Assert.assertNotNull(text);
        Assert.assertEquals("( mTF2 == false ) && ( vTF2 == 123 )", text);
    }

    @Test
    public void testEquals() {
        predicate.setOperator(EQUAL);
        SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
        String text = ssp.getPredicateRule();
        Assert.assertNotNull(text);
        Assert.assertEquals("( mTF2 == false ) && ( vTF2 == 123 )", text);
    }

    @Test
    public void testNotEquals() {
        predicate.setOperator(NOT_EQUAL);
        SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
        String text = ssp.getPredicateRule();
        Assert.assertNotNull(text);
        Assert.assertEquals("( mTF2 == false ) && ( vTF2 != 123 )", text);
    }

    @Test
    public void testGreaterThan() {
        predicate.setOperator(GREATER);
        SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
        String text = ssp.getPredicateRule();
        Assert.assertNotNull(text);
        Assert.assertEquals("( mTF2 == false ) && ( vTF2 > 123 )", text);
    }

    @Test
    public void testLessThan() {
        predicate.setOperator(LESSER);
        SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
        String text = ssp.getPredicateRule();
        Assert.assertNotNull(text);
        Assert.assertEquals("( mTF2 == false ) && ( vTF2 < 123 )", text);
    }

    @Test
    public void testMissing() {
        predicate.setOperator(MISSING);
        SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
        String text = ssp.getPredicateRule();
        Assert.assertNotNull(text);
        Assert.assertEquals("mTF2 == true", text);
    }

    @Test
    public void testGreaterEqual() {
        predicate.setOperator(GREATER_EQUAL);
        SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
        String text = ssp.getPredicateRule();
        Assert.assertNotNull(text);
        Assert.assertEquals("( mTF2 == false ) && ( vTF2 >= 123 )", text);
    }

    @Test
    public void testLesserEqual() {
        predicate.setOperator(LESSER_EQUAL);
        SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
        String text = ssp.getPredicateRule();
        Assert.assertNotNull(text);
        Assert.assertEquals("( mTF2 == false ) && ( vTF2 <= 123 )", text);
    }

    @Test
    public void testNotMissing() {
        predicate.setOperator(NOT_MISSING);
        SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
        String text = ssp.getPredicateRule();
        Assert.assertNotNull(text);
        Assert.assertEquals("mTF2 == false", text);
    }

    @Test(expected = IllegalStateException.class)
    public void testBadOperator() {
        predicate.setOperator(SimpleSegmentPredicateTest.BAD_OP);
        SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
        String text = ssp.getPredicateRule();
        Assert.assertNull(text);
    }
}

