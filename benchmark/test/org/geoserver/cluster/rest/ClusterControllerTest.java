/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster.rest;


import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class ClusterControllerTest extends GeoServerSystemTestSupport {
    @Test
    public void testGetConfigurationXML() throws Exception {
        Document dom = getAsDOM("rest/cluster.xml");
        // print(dom);
        // checking a property that's unlikely to change
        assertXpathEvaluatesTo("VirtualTopic.geoserver", "/properties/property[@name='topicName']/@value", dom);
    }

    @Test
    public void testGetConfigurationHTML() throws Exception {
        Document dom = getAsDOM("rest/cluster.html");
        Assert.assertEquals("html", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testGetConfigurationJSON() throws Exception {
        // get JSON properties
        JSON json = getAsJSON("rest/cluster.json");
        Assert.assertThat(json, CoreMatchers.notNullValue());
        Assert.assertThat(json, CoreMatchers.instanceOf(JSONObject.class));
        JSONObject jsonObject = ((JSONObject) (json));
        Assert.assertThat(jsonObject.get("properties"), CoreMatchers.notNullValue());
        Assert.assertThat(jsonObject.get("properties"), CoreMatchers.instanceOf(JSONObject.class));
        Assert.assertThat(jsonObject.getJSONObject("properties").get("property"), CoreMatchers.notNullValue());
        Assert.assertThat(jsonObject.getJSONObject("properties").get("property"), CoreMatchers.instanceOf(JSONArray.class));
        JSONArray properties = jsonObject.getJSONObject("properties").getJSONArray("property");
        Assert.assertThat(properties.size(), CoreMatchers.is(15));
        // check properties exist
        checkPropertyExists(properties, "toggleSlave");
        checkPropertyExists(properties, "connection");
        checkPropertyExists(properties, "topicName");
        checkPropertyExists(properties, "brokerURL");
        checkPropertyExists(properties, "durable");
        checkPropertyExists(properties, "xbeanURL");
        checkPropertyExists(properties, "toggleMaster");
        checkPropertyExists(properties, "embeddedBroker");
        checkPropertyExists(properties, "CLUSTER_CONFIG_DIR");
        checkPropertyExists(properties, "embeddedBrokerProperties");
        checkPropertyExists(properties, "connection.retry");
        checkPropertyExists(properties, "readOnly");
        checkPropertyExists(properties, "instanceName");
        checkPropertyExists(properties, "group");
        checkPropertyExists(properties, "connection.maxwait");
    }

    @Test
    public void testUpdateConfiguration() throws Exception {
        String config = "<properties><property name=\"toggleSlave\" value=\"false\"/></properties>";
        MockHttpServletResponse response = postAsServletResponse("rest/cluster.xml", config);
        Assert.assertEquals(201, response.getStatus());
        Document dom = getAsDOM("rest/cluster.xml");
        // print(dom);
        // checking the property just modified
        assertXpathEvaluatesTo("false", "/properties/property[@name='toggleSlave']/@value", dom);
    }
}

