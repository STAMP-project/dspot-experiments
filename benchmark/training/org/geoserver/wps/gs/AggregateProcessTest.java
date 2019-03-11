/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.gs;


import MockData.PRIMITIVEGEOFEATURE;
import javax.xml.namespace.QName;
import junit.framework.Assert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.wps.WPSTestSupport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.w3c.dom.Document;


public class AggregateProcessTest extends WPSTestSupport {
    @Test
    public void testSum() throws Exception {
        String xml = aggregateCall("Sum");
        Document dom = postAsDOM(root(), xml);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(/AggregationResults/*)", dom);
        assertXpathEvaluatesTo("-111.0", "/AggregationResults/Sum", dom);
    }

    @Test
    public void testMin() throws Exception {
        String xml = aggregateCall("Min");
        Document dom = postAsDOM(root(), xml);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(/AggregationResults/*)", dom);
        assertXpathEvaluatesTo("-900.0", "/AggregationResults/Min", dom);
    }

    @Test
    public void testMax() throws Exception {
        String xml = aggregateCall("Max");
        Document dom = postAsDOM(root(), xml);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(/AggregationResults/*)", dom);
        assertXpathEvaluatesTo("300.0", "/AggregationResults/Max", dom);
    }

    @Test
    public void testAverage() throws Exception {
        String xml = aggregateCall("Average");
        Document dom = postAsDOM(root(), xml);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(/AggregationResults/*)", dom);
        assertXpathEvaluatesTo("-22.2", "/AggregationResults/Average", dom);
    }

    @Test
    public void testStdDev() throws Exception {
        String xml = aggregateCall("StdDev");
        Document dom = postAsDOM(root(), xml);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(/AggregationResults/*)", dom);
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertTrue(xpath.evaluate("/AggregationResults/StandardDeviation", dom).matches("442\\.19380.*"));
    }

    @Test
    public void testNonRawOutput() throws Exception {
        String xml = aggregateCall("StdDev", false);
        Document dom = postAsDOM(root(), xml);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(//AggregationResults/*)", dom);
    }

    @Test
    public void testAllOneByOne() throws Exception {
        String xml = callAll(false);
        Document dom = postAsDOM(root(), xml);
        // print(dom);
        assertXpathEvaluatesTo("5", "count(/AggregationResults/*)", dom);
        assertXpathEvaluatesTo("-111.0", "/AggregationResults/Sum", dom);
        assertXpathEvaluatesTo("-900.0", "/AggregationResults/Min", dom);
        assertXpathEvaluatesTo("300.0", "/AggregationResults/Max", dom);
        assertXpathEvaluatesTo("-22.2", "/AggregationResults/Average", dom);
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertTrue(xpath.evaluate("/AggregationResults/StandardDeviation", dom).matches("442\\.19380.*"));
    }

    @Test
    public void testAllSinglePass() throws Exception {
        String xml = callAll(true);
        Document dom = postAsDOM(root(), xml);
        // print(dom);
        assertXpathEvaluatesTo("5", "count(/AggregationResults/*)", dom);
        assertXpathEvaluatesTo("-111.0", "/AggregationResults/Sum", dom);
        assertXpathEvaluatesTo("-900.0", "/AggregationResults/Min", dom);
        assertXpathEvaluatesTo("300.0", "/AggregationResults/Max", dom);
        assertXpathEvaluatesTo("-22.2", "/AggregationResults/Average", dom);
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertTrue(xpath.evaluate("/AggregationResults/StandardDeviation", dom).matches("442\\.19380.*"));
    }

    @Test
    public void testSumAsJson() throws Exception {
        String xml = aggregateCall("Sum", true, "application/json", false);
        JSONObject result = executeJsonRequest(xml);
        Assert.assertTrue(((result.size()) == 4));
        String aggregationAttribute = ((String) (result.get("AggregationAttribute")));
        JSONArray aggregationFunctions = ((JSONArray) (result.get("AggregationFunctions")));
        JSONArray groupByAttributes = ((JSONArray) (result.get("GroupByAttributes")));
        JSONArray aggregationResults = ((JSONArray) (result.get("AggregationResults")));
        Assert.assertTrue(aggregationAttribute.equals("intProperty"));
        Assert.assertTrue(((aggregationFunctions.size()) == 1));
        Assert.assertTrue(aggregationFunctions.get(0).equals("Sum"));
        Assert.assertTrue(((groupByAttributes.size()) == 0));
        Assert.assertTrue(((aggregationResults.size()) == 1));
        JSONArray sumResult = ((JSONArray) (aggregationResults.get(0)));
        Assert.assertTrue(((sumResult.size()) == 1));
        Assert.assertTrue(sumResult.get(0).equals((-111L)));
    }

    @Test
    public void testSumWithGroupBy() throws Exception {
        String xml = aggregateCall("Sum", true, "text/xml", true);
        Document dom = postAsDOM(root(), xml);
        assertXpathEvaluatesTo("1", "count(/AggregationResults/*)", dom);
        assertXpathEvaluatesTo("5", "count(/AggregationResults/GroupByResult/*)", dom);
    }

    @Test
    public void testSumAsJsonWithGroupBy() throws Exception {
        String xml = aggregateCall("Sum", true, "application/json", true);
        JSONObject result = executeJsonRequest(xml);
        Assert.assertTrue(((result.size()) == 4));
        String aggregationAttribute = ((String) (result.get("AggregationAttribute")));
        JSONArray aggregationFunctions = ((JSONArray) (result.get("AggregationFunctions")));
        JSONArray groupByAttributes = ((JSONArray) (result.get("GroupByAttributes")));
        JSONArray aggregationResults = ((JSONArray) (result.get("AggregationResults")));
        Assert.assertTrue(aggregationAttribute.equals("intProperty"));
        Assert.assertTrue(((aggregationFunctions.size()) == 1));
        Assert.assertTrue(aggregationFunctions.get(0).equals("Sum"));
        Assert.assertTrue(((groupByAttributes.size()) == 1));
        Assert.assertTrue(groupByAttributes.get(0).equals("name"));
        Assert.assertTrue(((aggregationResults.size()) == 5));
    }

    /**
     * Extends AggregateProcessTest to support local workspace resolution cases
     *
     * @author Geosolutions
     */
    public static class LocalWSTest extends AggregateProcessTest {
        @Override
        protected String getWorkspaceAndServicePath() {
            return (PRIMITIVEGEOFEATURE.getPrefix()) + "/wps?";
        }

        @Override
        protected String root() {
            return (PRIMITIVEGEOFEATURE.getPrefix()) + "/wps?";
        }

        @Override
        protected String getWFSQueryLayerId(QName layerName) {
            return layerName.getLocalPart();
        }
    }
}

