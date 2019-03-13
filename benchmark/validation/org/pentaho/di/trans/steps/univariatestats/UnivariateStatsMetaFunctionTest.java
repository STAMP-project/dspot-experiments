/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.univariatestats;


import java.io.IOException;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.steps.loadsave.MemoryRepository;
import org.pentaho.test.util.GetterSetterTester;
import org.pentaho.test.util.ObjectTester;
import org.pentaho.test.util.ObjectTesterBuilder;
import org.pentaho.test.util.ObjectValidator;


public class UnivariateStatsMetaFunctionTest {
    @Test
    public void testValuesConstructor() {
        UnivariateStatsMetaFunction function = new UnivariateStatsMetaFunction(null, false, false, false, false, false, false, 0, false);
        Assert.assertNull(function.getSourceFieldName());
        Assert.assertFalse(function.getCalcN());
        Assert.assertFalse(function.getCalcMean());
        Assert.assertFalse(function.getCalcStdDev());
        Assert.assertFalse(function.getCalcMin());
        Assert.assertFalse(function.getCalcMax());
        Assert.assertFalse(function.getCalcMedian());
        Assert.assertEquals(0, function.getCalcPercentile(), 0);
        Assert.assertFalse(function.getInterpolatePercentile());
        function = new UnivariateStatsMetaFunction("test", true, true, true, true, true, true, 0.5, true);
        Assert.assertEquals("test", function.getSourceFieldName());
        Assert.assertTrue(function.getCalcN());
        Assert.assertTrue(function.getCalcMean());
        Assert.assertTrue(function.getCalcStdDev());
        Assert.assertTrue(function.getCalcMin());
        Assert.assertTrue(function.getCalcMax());
        Assert.assertTrue(function.getCalcMedian());
        Assert.assertEquals(0.5, function.getCalcPercentile(), 0);
        Assert.assertTrue(function.getInterpolatePercentile());
    }

    @Test
    public void testNodeConstructor() throws IOException, KettleXMLException {
        String functionXml = IOUtils.toString(UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream("org/pentaho/di/trans/steps/univariatestats/trueValuesUnivariateStatsMetaFunctionNode.xml"));
        UnivariateStatsMetaFunction function = new UnivariateStatsMetaFunction(XMLHandler.loadXMLString(functionXml).getFirstChild());
        Assert.assertEquals("a", function.getSourceFieldName());
        Assert.assertTrue(function.getCalcN());
        Assert.assertTrue(function.getCalcMean());
        Assert.assertTrue(function.getCalcStdDev());
        Assert.assertTrue(function.getCalcMin());
        Assert.assertTrue(function.getCalcMax());
        Assert.assertTrue(function.getCalcMedian());
        Assert.assertEquals(0.5, function.getCalcPercentile(), 0);
        Assert.assertTrue(function.getInterpolatePercentile());
        functionXml = IOUtils.toString(UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream("org/pentaho/di/trans/steps/univariatestats/falseValuesUnivariateStatsMetaFunctionNode.xml"));
        function = new UnivariateStatsMetaFunction(XMLHandler.loadXMLString(functionXml).getFirstChild());
        Assert.assertTrue(Utils.isEmpty(function.getSourceFieldName()));
        Assert.assertFalse(function.getCalcN());
        Assert.assertFalse(function.getCalcMean());
        Assert.assertFalse(function.getCalcStdDev());
        Assert.assertFalse(function.getCalcMin());
        Assert.assertFalse(function.getCalcMax());
        Assert.assertFalse(function.getCalcMedian());
        Assert.assertEquals((-1.0), function.getCalcPercentile(), 0);
        Assert.assertFalse(function.getInterpolatePercentile());
    }

    @Test
    public void testRepoConstructor() throws IOException, ParseException, KettleException {
        String jsString = IOUtils.toString(UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream("org/pentaho/di/trans/steps/univariatestats/trueValuesUnivariateStatsMetaFunctionNode.json"));
        Repository repo = new MemoryRepository(jsString);
        UnivariateStatsMetaFunction function = new UnivariateStatsMetaFunction(repo, new StringObjectId("test"), 0);
        Assert.assertEquals("test", function.getSourceFieldName());
        Assert.assertTrue(function.getCalcN());
        Assert.assertTrue(function.getCalcMean());
        Assert.assertTrue(function.getCalcStdDev());
        Assert.assertTrue(function.getCalcMin());
        Assert.assertTrue(function.getCalcMax());
        Assert.assertTrue(function.getCalcMedian());
        Assert.assertEquals(0.5, function.getCalcPercentile(), 0);
        Assert.assertTrue(function.getInterpolatePercentile());
        jsString = IOUtils.toString(UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream("org/pentaho/di/trans/steps/univariatestats/falseValuesUnivariateStatsMetaFunctionNode.json"));
        repo = new MemoryRepository(jsString);
        function = new UnivariateStatsMetaFunction(repo, new StringObjectId("test"), 0);
        Assert.assertTrue(Utils.isEmpty(function.getSourceFieldName()));
        Assert.assertFalse(function.getCalcN());
        Assert.assertFalse(function.getCalcMean());
        Assert.assertFalse(function.getCalcStdDev());
        Assert.assertFalse(function.getCalcMin());
        Assert.assertFalse(function.getCalcMax());
        Assert.assertFalse(function.getCalcMedian());
        Assert.assertEquals((-1.0), function.getCalcPercentile(), 0);
        Assert.assertFalse(function.getInterpolatePercentile());
    }

    @Test
    public void testEquals() throws IOException, KettleXMLException {
        String functionXml = IOUtils.toString(UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream("org/pentaho/di/trans/steps/univariatestats/trueValuesUnivariateStatsMetaFunctionNode.xml"));
        UnivariateStatsMetaFunction function = new UnivariateStatsMetaFunction(XMLHandler.loadXMLString(functionXml).getFirstChild());
        UnivariateStatsMetaFunction function2 = new UnivariateStatsMetaFunction(XMLHandler.loadXMLString(functionXml).getFirstChild());
        Assert.assertEquals(function, function2);
        functionXml = IOUtils.toString(UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream("org/pentaho/di/trans/steps/univariatestats/falseValuesUnivariateStatsMetaFunctionNode.xml"));
        function = new UnivariateStatsMetaFunction(XMLHandler.loadXMLString(functionXml).getFirstChild());
        Assert.assertFalse(function.equals(function2));
        function2 = new UnivariateStatsMetaFunction(XMLHandler.loadXMLString(functionXml).getFirstChild());
        Assert.assertEquals(function, function2);
    }

    @Test
    public void testClone() {
        UnivariateStatsMetaFunction function = new UnivariateStatsMetaFunction(null, false, false, false, false, false, false, 0, false);
        Assert.assertEquals(UnivariateStatsMetaFunction.class, function.clone().getClass());
    }

    @Test
    public void testGettersAndSetters() {
        GetterSetterTester<UnivariateStatsMetaFunction> getterSetterTest = new GetterSetterTester<UnivariateStatsMetaFunction>(UnivariateStatsMetaFunction.class);
        ObjectTester<Boolean> primitiveBooleanTester = new ObjectTesterBuilder<Boolean>().addObject(true).addObject(false).build();
        getterSetterTest.addObjectTester("sourceFieldName", new ObjectTesterBuilder<String>().addObject(null).addObject(UUID.randomUUID().toString()).build());
        getterSetterTest.addObjectTester("calcN", primitiveBooleanTester);
        getterSetterTest.addObjectTester("calcMean", primitiveBooleanTester);
        getterSetterTest.addObjectTester("calcStdDev", primitiveBooleanTester);
        getterSetterTest.addObjectTester("calcMin", primitiveBooleanTester);
        getterSetterTest.addObjectTester("calcMax", primitiveBooleanTester);
        getterSetterTest.addObjectTester("calcMedian", primitiveBooleanTester);
        getterSetterTest.addObjectTester("interpolatePercentile", primitiveBooleanTester);
        getterSetterTest.addObjectTester("calcPercentile", new ObjectTesterBuilder<Double>().addObject((-100.0)).addObject(0.0).addObject(55.5).addObject(100.0).setValidator(new ObjectValidator<Double>() {
            @Override
            public void validate(Double expected, Object actual) {
                Assert.assertEquals(Double.class, actual.getClass());
                double actualValue = ((Double) (actual)).doubleValue();
                if ((0 <= expected) && (expected <= 100)) {
                    Assert.assertEquals((expected / 100.0), actualValue, 0);
                } else {
                    Assert.assertEquals((-1.0), actualValue, 0);
                }
            }
        }).build());
        getterSetterTest.test(new UnivariateStatsMetaFunction(null, false, false, false, false, false, false, 0, false));
    }
}

