/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.commonutil.elementvisibilityutil;


import java.util.regex.PatternSyntaxException;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.elementvisibilityutil.exception.VisibilityParseException;


/**
 * This test class is copied from org.apache.accumulo.core.security.VisibilityEvaluatorTest.
 */
public class VisibilityEvaluatorTest {
    VisibilityEvaluator ve = new VisibilityEvaluator(new Authorisations("one", "two", "three", "four"));

    @Test
    public void testVisibilityEvaluator() throws VisibilityParseException {
        // test for empty vis
        Assert.assertTrue(ve.evaluate(new ElementVisibility(new byte[0])));
        // test for and
        Assert.assertTrue("'and' test", ve.evaluate(new ElementVisibility("one&two")));
        // test for or
        Assert.assertTrue("'or' test", ve.evaluate(new ElementVisibility("foor|four")));
        // test for and and or
        Assert.assertTrue("'and' and 'or' test", ve.evaluate(new ElementVisibility("(one&two)|(foo&bar)")));
        // test for false negatives
        for (String marking : new String[]{ "one", "one|five", "five|one", "(one)", "(one&two)|(foo&bar)", "(one|foo)&three", "one|foo|bar", "(one|foo)|bar", "((one|foo)|bar)&two" }) {
            Assert.assertTrue(marking, ve.evaluate(new ElementVisibility(marking)));
        }
        // test for false positives
        for (String marking : new String[]{ "five", "one&five", "five&one", "((one|foo)|bar)&goober" }) {
            Assert.assertFalse(marking, ve.evaluate(new ElementVisibility(marking)));
        }
        // test missing separators; these should throw an exception
        for (String marking : new String[]{ "one(five)", "(five)one", "(one)(two)", "a|(b(c))" }) {
            try {
                ve.evaluate(new ElementVisibility(marking));
                Assert.fail((marking + " failed to throw"));
            } catch (PatternSyntaxException e) {
                // all is good
            }
        }
        // test unexpected separator
        for (String marking : new String[]{ "&(five)", "|(five)", "(five)&", "five|", "a|(b)&", "(&five)", "(five|)" }) {
            try {
                ve.evaluate(new ElementVisibility(marking));
                Assert.fail((marking + " failed to throw"));
            } catch (PatternSyntaxException e) {
                // all is good
            }
        }
        // test mismatched parentheses
        for (String marking : new String[]{ "(", ")", "(a&b", "b|a)" }) {
            try {
                ve.evaluate(new ElementVisibility(marking));
                Assert.fail((marking + " failed to throw"));
            } catch (PatternSyntaxException e) {
                // all is good
            }
        }
    }

    @Test
    public void testQuotedExpressions() throws VisibilityParseException {
        Authorisations auths = new Authorisations("A#C", "A\"C", "A\\C", "AC");
        VisibilityEvaluator ve = new VisibilityEvaluator(auths);
        Assert.assertTrue(ve.evaluate(new ElementVisibility((((ElementVisibility.quote("A#C")) + "|") + (ElementVisibility.quote("A?C"))))));
        Assert.assertFalse(ve.evaluate(new ElementVisibility(((ElementVisibility.quote("A#C")) + "&B"))));
        Assert.assertTrue(ve.evaluate(new ElementVisibility(ElementVisibility.quote("A#C"))));
        Assert.assertTrue(ve.evaluate(new ElementVisibility((("(" + (ElementVisibility.quote("A#C"))) + ")"))));
    }

    @Test
    public void testQuote() {
        Assert.assertEquals("\"A#C\"", ElementVisibility.quote("A#C"));
        Assert.assertEquals("\"A\\\"C\"", ElementVisibility.quote("A\"C"));
        Assert.assertEquals("\"A\\\"\\\\C\"", ElementVisibility.quote("A\"\\C"));
        Assert.assertEquals("ACS", ElementVisibility.quote("ACS"));
        Assert.assertEquals("\"\u4e5d\"", ElementVisibility.quote("?"));
        Assert.assertEquals("\"\u4e94\u5341\"", ElementVisibility.quote("??"));
    }

    @Test
    public void testNonAscii() throws VisibilityParseException {
        VisibilityEvaluator ve = new VisibilityEvaluator(new Authorisations("?", "?", "?", "?", "??"));
        Assert.assertTrue(ve.evaluate(new ElementVisibility((((ElementVisibility.quote("?")) + "|") + (ElementVisibility.quote("?"))))));
        Assert.assertFalse(ve.evaluate(new ElementVisibility((((ElementVisibility.quote("?")) + "&") + (ElementVisibility.quote("?"))))));
        Assert.assertTrue(ve.evaluate(new ElementVisibility(((((((ElementVisibility.quote("?")) + "&(") + (ElementVisibility.quote("?"))) + "|") + (ElementVisibility.quote("?"))) + ")"))));
        Assert.assertTrue(ve.evaluate(new ElementVisibility("\"\u4e94\"&(\"\u56db\"|\"\u4e94\u5341\")")));
        Assert.assertFalse(ve.evaluate(new ElementVisibility(((((((ElementVisibility.quote("?")) + "&(") + (ElementVisibility.quote("?"))) + "|") + (ElementVisibility.quote("?"))) + ")"))));
        Assert.assertFalse(ve.evaluate(new ElementVisibility("\"\u4e94\"&(\"\u56db\"|\"\u4e09\")")));
    }
}

