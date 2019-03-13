/**
 * The MIT License
 *
 * Copyright 2018 Intuit Inc.
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


import com.intuit.karate.Match;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author pthomas3
 */
public class FeatureParserTest {
    private static final Logger logger = LoggerFactory.getLogger(FeatureParserTest.class);

    @Test
    public void testEngineForSimpleFeature() {
        FeatureResult result = FeatureParserTest.execute("test-simple.feature");
        Map<String, Object> map = result.toMap();
        Match.equals(map.get("tags"), "[{ name: '@foo', line: 1 }]");
        ScenarioResult sr = ((ScenarioResult) (result.getScenarioResults().get(0)));
        map = sr.toMap();
        Match.equals(map.get("tags"), "[{ name: '@bar', line: 5 }]");
        Engine.saveResultJson("target", result, null);
        Engine.saveResultXml("target", result, null);
    }

    @Test
    public void testEngineForSimpleFeatureWithBackground() {
        FeatureResult result = FeatureParserTest.execute("test-simple-background.feature");
        Assert.assertEquals(1, result.getScenarioResults().size());
        Engine.saveResultJson("target", result, null);
        Engine.saveResultXml("target", result, null);
    }

    @Test
    public void testEngineForError() {
        FeatureResult result = FeatureParserTest.execute("test-error.feature");
        Engine.saveResultJson("target", result, null);
        Engine.saveResultXml("target", result, null);
    }

    @Test
    public void testParsingFeatureDescription() {
        Feature feature = FeatureParser.parse("classpath:com/intuit/karate/core/test-simple.feature");
        Assert.assertEquals("the first line", feature.getName());
        Assert.assertEquals("and the second", feature.getDescription());
    }

    @Test
    public void testFeatureWithIgnore() {
        FeatureResult result = FeatureParserTest.execute("test-ignore-feature.feature");
        Assert.assertEquals(0, result.getScenarioResults().size());
    }

    @Test
    public void testScenarioWithIgnore() {
        FeatureResult result = FeatureParserTest.execute("test-ignore-scenario.feature");
        Assert.assertEquals(1, result.getScenarioResults().size());
    }

    @Test
    public void testDefDocString() {
        FeatureResult result = FeatureParserTest.execute("test-def-docstring.feature");
        for (StepResult step : result.getScenarioResults().get(0).getStepResults()) {
            Assert.assertEquals("passed", step.getResult().getStatus());
        }
    }

    @Test
    public void testSetTable() {
        FeatureResult result = FeatureParserTest.execute("test-set-table.feature");
        Map<String, Object> map = result.getResultAsPrimitiveMap();
        Match.equals(map.get("output"), "{ name: 'Bob', age: 2 }");
    }

    @Test
    public void testEmptyFeature() {
        try {
            FeatureResult result = FeatureParserTest.execute("empty.feature.txt");
            Assert.fail("we expected parsing to fail");
        } catch (Exception e) {
            String message = e.getMessage();
            Assert.assertTrue(e.getMessage().contains("mismatched input '<EOF>'"));
        }
    }

    @Test
    public void testEmptyFirstLine() {
        FeatureResult result = FeatureParserTest.execute("test-empty-first-line1.feature");
        Map<String, Object> map = result.getResultAsPrimitiveMap();
        Match.equalsText(map.get("foo"), "bar");
        result = FeatureParserTest.execute("test-empty-first-line2.feature");
        map = result.getResultAsPrimitiveMap();
        Match.equalsText(map.get("foo"), "bar");
        result = FeatureParserTest.execute("test-empty-first-line3.feature");
        map = result.getResultAsPrimitiveMap();
        Match.equalsText(map.get("foo"), "bar");
    }

    @Test
    public void testFeatureHeaderOnly() {
        FeatureResult result = FeatureParserTest.execute("feature-header-only.feature");
    }

    @Test
    public void testTablePipe() {
        FeatureResult result = FeatureParserTest.execute("test-table-pipe.feature");
        Map<String, Object> map = result.getResultAsPrimitiveMap();
        Match.equalsText(map.get("value"), "pi|pe");
    }

    @Test
    public void testOutlineName() {
        FeatureResult result = FeatureParserTest.execute("test-outline-name.feature");
        Map<String, Object> map = result.getResultAsPrimitiveMap();
        Match.equalsText(map.get("name"), "Nyan");
        Match.equalsText(map.get("title"), "name is Nyan and age is 5");
    }

    @Test
    public void testTagsMultiline() {
        FeatureResult result = FeatureParserTest.execute("test-tags-multiline.feature");
        Map<String, Object> map = result.getResultAsPrimitiveMap();
        Match.contains(map.get("tags"), "[ 'tag1', 'tag2', 'tag3', 'tag4' ]");
    }

    @Test
    public void testEdgeCases() {
        FeatureResult result = FeatureParserTest.execute("test-edge-cases.feature");
    }

    @Test
    public void testOutlineDynamic() {
        FeatureResult result = FeatureParserTest.execute("test-outline-dynamic.feature");
        Assert.assertEquals(2, result.getScenarioResults().size());
        Map<String, Object> map = result.getResultAsPrimitiveMap();
        Match.equalsText(map.get("name"), "Nyan");
        Match.equalsText(map.get("title"), "name is Nyan and age is 7");
    }

    @Test
    public void testStepEditing() {
        Feature feature = FeatureParser.parse("classpath:com/intuit/karate/core/test-simple.feature");
        Step step = feature.getStep(0, (-1), 0);
        Assert.assertEquals("def a = 1", step.getText());
        FeatureParser.updateStepFromText(step, "* def a = 2 - 1");
        Assert.assertEquals("def a = 2 - 1", step.getText());
    }

    @Test
    public void testEmptyBackground() {
        FeatureResult result = FeatureParserTest.execute("test-empty-background.feature");
        Assert.assertFalse(result.isFailed());
        Map<String, Object> map = result.getResultAsPrimitiveMap();
        Match.equals(map.get("temp"), "['foo']");
    }

    @Test
    public void testHide() {
        Feature feature = FeatureParser.parse("classpath:com/intuit/karate/core/test-hide.feature");
        Step step = feature.getStep(0, (-1), 0);
        Assert.assertTrue(step.isPrefixStar());
        Assert.assertFalse(step.isPrint());
        Assert.assertEquals("def a = 1", step.getText());
        step = feature.getStep(0, (-1), 1);
        Assert.assertTrue(step.isPrefixStar());
        Assert.assertTrue(step.isPrint());
        Assert.assertEquals("print a", step.getText());
        step = feature.getStep(0, (-1), 2);
        Assert.assertFalse(step.isPrefixStar());
        Assert.assertTrue(step.isPrint());
        Assert.assertEquals("print a", step.getText());
    }
}

