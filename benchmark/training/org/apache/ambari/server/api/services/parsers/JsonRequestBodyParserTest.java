/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.api.services.parsers;


import JsonRequestBodyParser.REQUEST_BLOB_TITLE;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.services.NamedPropertySet;
import org.apache.ambari.server.api.services.RequestBody;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;


/**
 * Unit tests for JsonPropertyParser.
 */
public class JsonRequestBodyParserTest {
    String serviceJson = "{\"Services\" : {" + ((((((((("    \"display_name\" : \"HDFS\"," + "    \"description\" : \"Apache Hadoop Distributed File System\",") + "    \"service_name\" : \"HDFS\"") + "  },") + "  \"ServiceInfo\" : {") + "    \"cluster_name\" : \"tbmetrictest\",") + "    \"state\" : \"STARTED\"") + "  },") + "\"OuterCategory\" : { \"propName\" : 100, \"nested1\" : { \"nested2\" : { \"innerPropName\" : \"innerPropValue\" } } },") + "\"topLevelProp\" : \"value\" }");

    String serviceJsonWithQuery = ("{ \"RequestInfo\" : { \"query\" : \"foo=bar\" }, \"Body\":" + (serviceJson)) + "}";

    String arrayJson = "[ {" + (((((((((((("\"Clusters\" : {\n" + "    \"cluster_name\" : \"unitTestCluster1\"") + "} },") + "{") + "\"Clusters\" : {\n") + "    \"cluster_name\" : \"unitTestCluster2\",") + "    \"property1\" : \"prop1Value\"") + "} },") + "{") + "\"Clusters\" : {\n") + "    \"cluster_name\" : \"unitTestCluster3\",") + "    \"Category\" : { \"property2\" : null}") + "} } ]");

    String arrayJson2 = "{" + (((((((((((("\"Clusters\" : {\n" + "    \"cluster_name\" : \"unitTestCluster1\"") + "} },") + "{") + "\"Clusters\" : {\n") + "    \"cluster_name\" : \"unitTestCluster2\",") + "    \"property1\" : \"prop1Value\"") + "} },") + "{") + "\"Clusters\" : {\n") + "    \"cluster_name\" : \"unitTestCluster3\",") + "    \"Category\" : { \"property2\" : \"prop2Value\"}") + "} }");

    String multiBody = "[\n" + (((((((((((((((((((((((((((((("  {\n" + "    \"RequestInfo\":{\n") + "      \"query\":\"Hosts/host_name=h1\"\n") + "    },\n") + "    \"Body\":\n") + "    {\n") + "      \"Hosts\": {\n") + "        \"desired_config\": {\n") + "          \"type\": \"global\",\n") + "          \"tag\": \"version20\",\n") + "          \"properties\": { \"a\": \"b\", \"x\": \"y\" }\n") + "        }\n") + "      }\n") + "    }\n") + "  },\n") + "  {\n") + "    \"RequestInfo\":{\n") + "      \"query\":\"Hosts/host_name=h2\"\n") + "    },\n") + "    \"Body\":\n") + "    {\n") + "      \"Hosts\": {\n") + "        \"desired_config\": {\n") + "          \"type\": \"global\",\n") + "          \"tag\": \"version21\",\n") + "          \"properties\": { \"a\": \"c\", \"x\": \"z\" }\n") + "        }\n") + "      }\n") + "    }\n") + "  }\n") + "]\n");

    String queryPostJson = "{ \"services\" : [ {" + (((((((((((("\"ServiceInfo\" : {\n" + "    \"service_name\" : \"unitTestService1\"") + "} },") + "{") + "\"ServiceInfo\" : {\n") + "    \"service_name\" : \"unitTestService2\",") + "    \"property1\" : \"prop1Value\"") + "} },") + "{") + "\"ServiceInfo\" : {\n") + "    \"service_name\" : \"unitTestService3\",") + "    \"Category\" : { \"property2\" : \"prop2Value\"}") + "} } ] }");

    String queryPostJsonWithQuery = ("{ \"RequestInfo\" : { \"query\" : \"foo=bar\" }, \"Body\":" + (queryPostJson)) + "}";

    String queryPostMultipleSubResourcesJson = "{ \"foo\" : [ {" + ((((((((("\"ServiceInfo\" : {\n" + "    \"service_name\" : \"unitTestService1\"") + "} }") + "],") + " \"bar\" : [") + "{") + "\"ServiceInfo\" : {\n") + "    \"service_name\" : \"unitTestService2\",") + "    \"Category\" : { \"property2\" : \"prop2Value\"}") + "} } ] }");

    String bodyQueryOnly = "{ \"RequestInfo\" : { \"query\" : \"foo=bar\" }}";

    String malformedJson = "{ \"Category\" : { \"foo\" : \"bar\"}";

    String bodyWithRequestInfoProperties = ("{ \"RequestInfo\" : { \"query\" : \"foo=bar\", \"prop1\" : \"val1\", \"prop2\" : \"val2\" }, \"Body\":" + (serviceJson)) + "}";

    String bodyWithRequestBlobProperties = (("{ \"RequestBodyInfo\" : { " + "\"RequestInfo\" : { \"query\" : \"foo=bar\", \"prop1\" : \"val1\", \"prop2\" : \"val2\" }, \"Body\":") + (serviceJson)) + "} }";

    @Test
    public void testParse() throws BodyParseException {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse(serviceJson).iterator().next();
        Set<NamedPropertySet> setProps = body.getNamedPropertySets();
        Assert.assertEquals(1, setProps.size());
        Map<String, Object> mapExpected = new HashMap<>();
        mapExpected.put(PropertyHelper.getPropertyId("Services", "service_name"), "HDFS");
        mapExpected.put(PropertyHelper.getPropertyId("Services", "display_name"), "HDFS");
        mapExpected.put(PropertyHelper.getPropertyId("ServiceInfo", "cluster_name"), "tbmetrictest");
        mapExpected.put(PropertyHelper.getPropertyId("Services", "description"), "Apache Hadoop Distributed File System");
        mapExpected.put(PropertyHelper.getPropertyId("ServiceInfo", "state"), "STARTED");
        mapExpected.put(PropertyHelper.getPropertyId("OuterCategory", "propName"), "100");
        mapExpected.put(PropertyHelper.getPropertyId("OuterCategory/nested1/nested2", "innerPropName"), "innerPropValue");
        mapExpected.put(PropertyHelper.getPropertyId(null, "topLevelProp"), "value");
        Assert.assertEquals(mapExpected, setProps.iterator().next().getProperties());
        // assert body is correct by checking that properties match
        String b = body.getBody();
        body = parser.parse(b).iterator().next();
        Set<NamedPropertySet> setProps2 = body.getNamedPropertySets();
        Assert.assertEquals(mapExpected, setProps2.iterator().next().getProperties());
    }

    @Test
    public void testParse_NullBody() throws BodyParseException {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse(null).iterator().next();
        Assert.assertNotNull(body.getNamedPropertySets());
        Assert.assertEquals(0, body.getNamedPropertySets().size());
        Assert.assertNull(body.getQueryString());
        Assert.assertNull(body.getPartialResponseFields());
        Assert.assertNull(body.getBody());
    }

    @Test
    public void testParse_EmptyBody() throws BodyParseException {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse("").iterator().next();
        Assert.assertNotNull(body.getNamedPropertySets());
        Assert.assertEquals(0, body.getNamedPropertySets().size());
        Assert.assertNull(body.getQueryString());
        Assert.assertNull(body.getPartialResponseFields());
        Assert.assertNull(body.getBody());
    }

    @Test
    public void testParse_MultiBody() throws BodyParseException {
        RequestBodyParser parser = new JsonRequestBodyParser();
        Set<RequestBody> bodySet = parser.parse(multiBody);
        Assert.assertEquals(2, bodySet.size());
        for (RequestBody body : bodySet) {
            Set<NamedPropertySet> setProps = body.getNamedPropertySets();
            Assert.assertEquals(1, setProps.size());
            Map<String, Object> mapProps = setProps.iterator().next().getProperties();
            Assert.assertEquals(4, mapProps.size());
            Assert.assertEquals("global", mapProps.get("Hosts/desired_config/type"));
        }
    }

    @Test
    public void testParse_Array() throws BodyParseException {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse(arrayJson).iterator().next();
        Set<NamedPropertySet> setProps = body.getNamedPropertySets();
        Assert.assertEquals(3, setProps.size());
        boolean cluster1Matches = false;
        boolean cluster2Matches = false;
        boolean cluster3Matches = false;
        Map<String, String> mapCluster1 = new HashMap<>();
        mapCluster1.put(PropertyHelper.getPropertyId("Clusters", "cluster_name"), "unitTestCluster1");
        Map<String, String> mapCluster2 = new HashMap<>();
        mapCluster2.put(PropertyHelper.getPropertyId("Clusters", "cluster_name"), "unitTestCluster2");
        mapCluster2.put(PropertyHelper.getPropertyId("Clusters", "property1"), "prop1Value");
        Map<String, String> mapCluster3 = new HashMap<>();
        mapCluster3.put(PropertyHelper.getPropertyId("Clusters", "cluster_name"), "unitTestCluster3");
        mapCluster3.put(PropertyHelper.getPropertyId("Clusters/Category", "property2"), null);
        for (NamedPropertySet propertySet : setProps) {
            Assert.assertEquals("", propertySet.getName());
            Map<String, Object> mapProps = propertySet.getProperties();
            if (mapProps.equals(mapCluster1)) {
                cluster1Matches = true;
            } else
                if (mapProps.equals(mapCluster2)) {
                    cluster2Matches = true;
                } else
                    if (mapProps.equals(mapCluster3)) {
                        cluster3Matches = true;
                    }


        }
        Assert.assertTrue(cluster1Matches);
        Assert.assertTrue(cluster2Matches);
        Assert.assertTrue(cluster3Matches);
        // assert body is correct by checking that properties match
        String b = body.getBody();
        body = parser.parse(b).iterator().next();
        Set<NamedPropertySet> setProps2 = body.getNamedPropertySets();
        Assert.assertEquals(3, setProps2.size());
        Assert.assertEquals(setProps, setProps2);
    }

    @Test
    public void testParse___Array_NoArrayBrackets() throws BodyParseException {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse(arrayJson2).iterator().next();
        Set<NamedPropertySet> setProps = body.getNamedPropertySets();
        Assert.assertEquals(3, setProps.size());
        boolean cluster1Matches = false;
        boolean cluster2Matches = false;
        boolean cluster3Matches = false;
        Map<String, String> mapCluster1 = new HashMap<>();
        mapCluster1.put(PropertyHelper.getPropertyId("Clusters", "cluster_name"), "unitTestCluster1");
        Map<String, String> mapCluster2 = new HashMap<>();
        mapCluster2.put(PropertyHelper.getPropertyId("Clusters", "cluster_name"), "unitTestCluster2");
        mapCluster2.put(PropertyHelper.getPropertyId("Clusters", "property1"), "prop1Value");
        Map<String, String> mapCluster3 = new HashMap<>();
        mapCluster3.put(PropertyHelper.getPropertyId("Clusters", "cluster_name"), "unitTestCluster3");
        mapCluster3.put(PropertyHelper.getPropertyId("Clusters/Category", "property2"), "prop2Value");
        for (NamedPropertySet propertySet : setProps) {
            Map<String, Object> mapProps = propertySet.getProperties();
            if (mapProps.equals(mapCluster1)) {
                cluster1Matches = true;
            } else
                if (mapProps.equals(mapCluster2)) {
                    cluster2Matches = true;
                } else
                    if (mapProps.equals(mapCluster3)) {
                        cluster3Matches = true;
                    }


        }
        Assert.assertTrue(cluster1Matches);
        Assert.assertTrue(cluster2Matches);
        Assert.assertTrue(cluster3Matches);
        // assert body is correct by checking that properties match
        String b = body.getBody();
        body = parser.parse(b).iterator().next();
        Set<NamedPropertySet> setProps2 = body.getNamedPropertySets();
        Assert.assertEquals(3, setProps2.size());
        Assert.assertEquals(setProps, setProps2);
    }

    @Test
    public void testParse_QueryInBody() throws BodyParseException {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse(serviceJsonWithQuery).iterator().next();
        Set<NamedPropertySet> setProps = body.getNamedPropertySets();
        Assert.assertEquals(1, setProps.size());
        Map<String, Object> mapExpected = new HashMap<>();
        mapExpected.put(PropertyHelper.getPropertyId("Services", "service_name"), "HDFS");
        mapExpected.put(PropertyHelper.getPropertyId("Services", "display_name"), "HDFS");
        mapExpected.put(PropertyHelper.getPropertyId("ServiceInfo", "cluster_name"), "tbmetrictest");
        mapExpected.put(PropertyHelper.getPropertyId("Services", "description"), "Apache Hadoop Distributed File System");
        mapExpected.put(PropertyHelper.getPropertyId("ServiceInfo", "state"), "STARTED");
        mapExpected.put(PropertyHelper.getPropertyId("OuterCategory", "propName"), "100");
        mapExpected.put(PropertyHelper.getPropertyId("OuterCategory/nested1/nested2", "innerPropName"), "innerPropValue");
        mapExpected.put(PropertyHelper.getPropertyId(null, "topLevelProp"), "value");
        Assert.assertEquals(mapExpected, setProps.iterator().next().getProperties());
        Assert.assertEquals("foo=bar", body.getQueryString());
        // assert body is correct by checking that properties match
        String b = body.getBody();
        body = parser.parse(b).iterator().next();
        Set<NamedPropertySet> setProps2 = body.getNamedPropertySets();
        Assert.assertEquals(mapExpected, setProps2.iterator().next().getProperties());
    }

    @Test
    public void testParse_QueryPost() throws BodyParseException {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse(queryPostJson).iterator().next();
        Set<NamedPropertySet> setProperties = body.getNamedPropertySets();
        Assert.assertEquals(1, setProperties.size());
        boolean contains1 = false;
        boolean contains2 = false;
        boolean contains3 = false;
        for (NamedPropertySet ps : setProperties) {
            Map<String, Object> mapProps = ps.getProperties();
            Assert.assertEquals(1, mapProps.size());
            Set<Map<String, Object>> set = ((Set<Map<String, Object>>) (mapProps.get("services")));
            for (Map<String, Object> map : set) {
                String serviceName = ((String) (map.get("ServiceInfo/service_name")));
                if (serviceName.equals("unitTestService1")) {
                    Assert.assertEquals(1, map.size());
                    contains1 = true;
                } else
                    if (serviceName.equals("unitTestService2")) {
                        Assert.assertEquals("prop1Value", map.get("ServiceInfo/property1"));
                        Assert.assertEquals(2, map.size());
                        contains2 = true;
                    } else
                        if (serviceName.equals("unitTestService3")) {
                            Assert.assertEquals("prop2Value", map.get("ServiceInfo/Category/property2"));
                            Assert.assertEquals(2, map.size());
                            contains3 = true;
                        } else {
                            Assert.fail("Unexpected service name");
                        }


            }
        }
        Assert.assertTrue(contains1);
        Assert.assertTrue(contains2);
        Assert.assertTrue(contains3);
        // assert body is correct by checking that properties match
        String b = body.getBody();
        body = parser.parse(b).iterator().next();
        Set<NamedPropertySet> setProps2 = body.getNamedPropertySets();
        Assert.assertEquals(1, setProps2.size());
        Assert.assertEquals(setProperties, setProps2);
        Assert.assertEquals("java.util.LinkedHashSet", body.getNamedPropertySets().iterator().next().getProperties().get("services").getClass().getName());
    }

    @Test
    public void testParse___QueryPost_multipleSubResTypes() throws BodyParseException {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse(queryPostMultipleSubResourcesJson).iterator().next();
        Set<NamedPropertySet> setProperties = body.getNamedPropertySets();
        Assert.assertEquals(1, setProperties.size());
        boolean contains1 = false;
        boolean contains2 = false;
        for (NamedPropertySet ps : setProperties) {
            Map<String, Object> mapProps = ps.getProperties();
            for (Map.Entry<String, Object> entry : mapProps.entrySet()) {
                Set<Map<String, Object>> set = ((Set<Map<String, Object>>) (entry.getValue()));
                for (Map<String, Object> map : set) {
                    String serviceName = ((String) (map.get("ServiceInfo/service_name")));
                    if (serviceName.equals("unitTestService1")) {
                        Assert.assertEquals("foo", entry.getKey());
                        Assert.assertEquals(1, map.size());
                        contains1 = true;
                    } else
                        if (serviceName.equals("unitTestService2")) {
                            Assert.assertEquals("bar", entry.getKey());
                            Assert.assertEquals("prop2Value", map.get("ServiceInfo/Category/property2"));
                            Assert.assertEquals(2, map.size());
                            contains2 = true;
                        } else {
                            Assert.fail("Unexpected service name");
                        }

                }
            }
        }
        Assert.assertTrue(contains1);
        Assert.assertTrue(contains2);
        // assert body is correct by checking that properties match
        String b = body.getBody();
        body = parser.parse(b).iterator().next();
        Set<NamedPropertySet> setProps2 = body.getNamedPropertySets();
        Assert.assertEquals(1, setProps2.size());
        Assert.assertEquals(setProperties, setProps2);
    }

    @Test
    public void testParse___QueryPost_QueryInBody() throws BodyParseException {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse(queryPostJsonWithQuery).iterator().next();
        Set<NamedPropertySet> setProperties = body.getNamedPropertySets();
        Assert.assertEquals("foo=bar", body.getQueryString());
        Assert.assertEquals(1, setProperties.size());
        boolean contains1 = false;
        boolean contains2 = false;
        boolean contains3 = false;
        for (NamedPropertySet ps : setProperties) {
            Assert.assertEquals("", ps.getName());
            Map<String, Object> mapProps = ps.getProperties();
            for (Map.Entry<String, Object> entry : mapProps.entrySet()) {
                Set<Map<String, Object>> set = ((Set<Map<String, Object>>) (entry.getValue()));
                for (Map<String, Object> map : set) {
                    String serviceName = ((String) (map.get("ServiceInfo/service_name")));
                    if (serviceName.equals("unitTestService1")) {
                        Assert.assertEquals(1, map.size());
                        contains1 = true;
                    } else
                        if (serviceName.equals("unitTestService2")) {
                            Assert.assertEquals("prop1Value", map.get("ServiceInfo/property1"));
                            Assert.assertEquals(2, map.size());
                            contains2 = true;
                        } else
                            if (serviceName.equals("unitTestService3")) {
                                Assert.assertEquals("prop2Value", map.get("ServiceInfo/Category/property2"));
                                Assert.assertEquals(2, map.size());
                                contains3 = true;
                            } else {
                                Assert.fail("Unexpected service name");
                            }


                }
            }
        }
        Assert.assertTrue(contains1);
        Assert.assertTrue(contains2);
        Assert.assertTrue(contains3);
        // assert body is correct by checking that properties match
        String b = body.getBody();
        Assert.assertEquals(queryPostJsonWithQuery, b);
        body = parser.parse(b).iterator().next();
        Set<NamedPropertySet> setProps2 = body.getNamedPropertySets();
        Assert.assertEquals(1, setProps2.size());
        Assert.assertEquals(setProperties, setProps2);
    }

    @Test
    public void testParse_QueryOnlyInBody() throws BodyParseException {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse(bodyQueryOnly).iterator().next();
        Assert.assertEquals("foo=bar", body.getQueryString());
        Assert.assertEquals(bodyQueryOnly, body.getBody());
    }

    @Test
    public void testParse_malformedBody() {
        RequestBodyParser parser = new JsonRequestBodyParser();
        try {
            parser.parse(malformedJson);
            Assert.fail("Expected exception due to malformed body");
        } catch (BodyParseException e) {
            // expected case
        }
    }

    @Test
    public void testRequestInfoProps() throws Exception {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse(bodyWithRequestInfoProperties).iterator().next();
        Set<NamedPropertySet> setProps = body.getNamedPropertySets();
        Assert.assertEquals(1, setProps.size());
        Map<String, Object> mapExpected = new HashMap<>();
        mapExpected.put(PropertyHelper.getPropertyId("Services", "service_name"), "HDFS");
        mapExpected.put(PropertyHelper.getPropertyId("Services", "display_name"), "HDFS");
        mapExpected.put(PropertyHelper.getPropertyId("ServiceInfo", "cluster_name"), "tbmetrictest");
        mapExpected.put(PropertyHelper.getPropertyId("Services", "description"), "Apache Hadoop Distributed File System");
        mapExpected.put(PropertyHelper.getPropertyId("ServiceInfo", "state"), "STARTED");
        mapExpected.put(PropertyHelper.getPropertyId("OuterCategory", "propName"), "100");
        mapExpected.put(PropertyHelper.getPropertyId("OuterCategory/nested1/nested2", "innerPropName"), "innerPropValue");
        mapExpected.put(PropertyHelper.getPropertyId(null, "topLevelProp"), "value");
        Assert.assertEquals(mapExpected, setProps.iterator().next().getProperties());
        Assert.assertEquals("foo=bar", body.getQueryString());
        Map<String, String> mapRequestInfoProps = body.getRequestInfoProperties();
        Assert.assertEquals(4, mapRequestInfoProps.size());
        Assert.assertEquals("val1", mapRequestInfoProps.get("prop1"));
        Assert.assertEquals("val2", mapRequestInfoProps.get("prop2"));
        Assert.assertEquals("foo=bar", mapRequestInfoProps.get("query"));
        Assert.assertEquals(bodyWithRequestInfoProperties, mapRequestInfoProps.get("RAW_REQUEST_BODY"));
        // assert body is correct by checking that properties match
        String b = body.getBody();
        body = parser.parse(b).iterator().next();
        Set<NamedPropertySet> setProps2 = body.getNamedPropertySets();
        Assert.assertEquals(mapExpected, setProps2.iterator().next().getProperties());
    }

    @Test
    public void testRequestBlobProperties() throws Exception {
        RequestBodyParser parser = new JsonRequestBodyParser();
        RequestBody body = parser.parse(bodyWithRequestBlobProperties).iterator().next();
        Set<NamedPropertySet> setProps = body.getNamedPropertySets();
        Assert.assertEquals(1, setProps.size());
        String requestBlob = null;
        for (NamedPropertySet ps : setProps) {
            Assert.assertEquals("", ps.getName());
            Map<String, Object> mapProps = ps.getProperties();
            for (Map.Entry<String, Object> entry : mapProps.entrySet()) {
                if (entry.getKey().equals(REQUEST_BLOB_TITLE)) {
                    requestBlob = ((String) (entry.getValue()));
                }
            }
        }
        assertNotNull(requestBlob);
        body = parser.parse(requestBlob).iterator().next();
        Map<String, Object> mapExpected = new HashMap<>();
        mapExpected.put(PropertyHelper.getPropertyId("Services", "service_name"), "HDFS");
        mapExpected.put(PropertyHelper.getPropertyId("Services", "display_name"), "HDFS");
        mapExpected.put(PropertyHelper.getPropertyId("ServiceInfo", "cluster_name"), "tbmetrictest");
        mapExpected.put(PropertyHelper.getPropertyId("Services", "description"), "Apache Hadoop Distributed File System");
        mapExpected.put(PropertyHelper.getPropertyId("ServiceInfo", "state"), "STARTED");
        mapExpected.put(PropertyHelper.getPropertyId("OuterCategory", "propName"), "100");
        mapExpected.put(PropertyHelper.getPropertyId("OuterCategory/nested1/nested2", "innerPropName"), "innerPropValue");
        mapExpected.put(PropertyHelper.getPropertyId(null, "topLevelProp"), "value");
        Set<NamedPropertySet> setProps2 = body.getNamedPropertySets();
        Assert.assertEquals(mapExpected, setProps2.iterator().next().getProperties());
    }
}

