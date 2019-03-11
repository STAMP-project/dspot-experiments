/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest;


import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.geoserver.rest.catalog.CatalogRESTTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;

import static RestBaseController.ROOT_PATH;


public class SettingsControllerTest extends CatalogRESTTestSupport {
    protected GeoServer geoServer;

    @Test
    public void testGetContactAsJSON() throws Exception {
        initContact();
        JSON json = getAsJSON(((ROOT_PATH) + "/settings/contact.json"));
        JSONObject jsonObject = ((JSONObject) (json));
        Assert.assertNotNull(jsonObject);
        JSONObject contactInfo = jsonObject.getJSONObject("contact");
        Assert.assertNotNull(contactInfo);
        Assert.assertEquals("United States", contactInfo.get("addressCountry"));
        Assert.assertEquals("1600 Pennsylvania Avenue", contactInfo.get("address"));
        Assert.assertEquals("Washington", contactInfo.get("addressCity"));
        Assert.assertEquals("DC", contactInfo.get("addressState"));
        Assert.assertEquals("20001", contactInfo.get("addressPostalCode").toString());
        Assert.assertEquals("The White House", contactInfo.get("addressDeliveryPoint").toString());
        Assert.assertEquals("info@whitehouse.gov", contactInfo.get("addressElectronicMailAddress").toString());
    }

    @Test
    public void testGetContactAsXML() throws Exception {
        initContact();
        Document dom = getAsDOM(((ROOT_PATH) + "/settings/contact.xml"));
        Assert.assertEquals("contact", dom.getDocumentElement().getLocalName());
        assertXpathEvaluatesTo("United States", "/contact/addressCountry", dom);
        assertXpathEvaluatesTo("Washington", "/contact/addressCity", dom);
        assertXpathEvaluatesTo("1600 Pennsylvania Avenue", "/contact/address", dom);
        assertXpathEvaluatesTo("DC", "/contact/addressState", dom);
        assertXpathEvaluatesTo("20001", "/contact/addressPostalCode", dom);
        assertXpathEvaluatesTo("The White House", "/contact/addressDeliveryPoint", dom);
        assertXpathEvaluatesTo("info@whitehouse.gov", "/contact/addressElectronicMailAddress", dom);
    }

    @Test
    public void testGetContactAsHTML() throws Exception {
        Document dom = getAsDOM(((ROOT_PATH) + "/settings/contact.html"), 200);
    }

    @Test
    public void testPutContactAsJSON() throws Exception {
        initContact();
        String inputJson = "{'contact':{" + ((((((("    'id':'contact'," + "    'address':'500 Market Street',") + "    'addressCity':'Philadelphia',") + "    'addressCountry':'United States',") + "    'addressPostalCode':'19106',") + "    'addressState':'PA',") + "    'addressDeliveryPoint':'The White House',") + "    'addressElectronicMailAddress':'info@whitehouse.gov'}}");
        MockHttpServletResponse response = putAsServletResponse(((ROOT_PATH) + "/settings/contact"), inputJson, "text/json");
        Assert.assertEquals(200, response.getStatus());
        JSON jsonMod = getAsJSON(((ROOT_PATH) + "/settings/contact.json"));
        JSONObject jsonObject = ((JSONObject) (jsonMod));
        Assert.assertNotNull(jsonObject);
        JSONObject contactInfo = jsonObject.getJSONObject("contact");
        Assert.assertEquals("United States", contactInfo.get("addressCountry"));
        Assert.assertEquals("500 Market Street", contactInfo.get("address"));
        Assert.assertEquals("Philadelphia", contactInfo.get("addressCity"));
        Assert.assertEquals("PA", contactInfo.get("addressState"));
        Assert.assertEquals("19106", contactInfo.get("addressPostalCode").toString());
        Assert.assertEquals("The White House", contactInfo.get("addressDeliveryPoint").toString());
        Assert.assertEquals("info@whitehouse.gov", contactInfo.get("addressElectronicMailAddress").toString());
    }

    @Test
    public void testPutContactAsXML() throws Exception {
        initContact();
        String xml = "<contact> <address>1600 Pennsylvania Avenue</address>" + (((((((((("<addressCity>Washington</addressCity>" + "<addressCountry>United States</addressCountry>") + "<addressPostalCode>20001</addressPostalCode>") + "<addressDeliveryPoint>The White House</addressDeliveryPoint>") + "<addressElectronicMailAddress>info@whitehouse.gov</addressElectronicMailAddress>") + "<addressState>DC</addressState>") + "<addressType>Avenue</addressType>") + "<contactEmail>chief.geographer@mail.com</contactEmail>") + "<contactOrganization>GeoServer</contactOrganization>") + "<contactPerson>ContactPerson</contactPerson>") + "<contactPosition>Chief Geographer</contactPosition> </contact>");
        MockHttpServletResponse response = putAsServletResponse(((ROOT_PATH) + "/settings/contact"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((ROOT_PATH) + "/settings/contact.xml"));
        Assert.assertEquals("contact", dom.getDocumentElement().getLocalName());
        assertXpathEvaluatesTo("United States", "/contact/addressCountry", dom);
        assertXpathEvaluatesTo("1600 Pennsylvania Avenue", "/contact/address", dom);
        assertXpathEvaluatesTo("Washington", "/contact/addressCity", dom);
        assertXpathEvaluatesTo("DC", "/contact/addressState", dom);
        assertXpathEvaluatesTo("20001", "/contact/addressPostalCode", dom);
        assertXpathEvaluatesTo("Chief Geographer", "/contact/contactPosition", dom);
        assertXpathEvaluatesTo("ContactPerson", "/contact/contactPerson", dom);
        assertXpathEvaluatesTo("The White House", "/contact/addressDeliveryPoint", dom);
        assertXpathEvaluatesTo("chief.geographer@mail.com", "/contact/addressElectronicMailAddress", dom);
    }

    @Test
    public void testGetGlobalAsJSON() throws Exception {
        JSON json = getAsJSON(((ROOT_PATH) + "/settings.json"));
        print(json);
        JSONObject jsonObject = ((JSONObject) (json));
        Assert.assertNotNull(jsonObject);
        JSONObject global = jsonObject.getJSONObject("global");
        Assert.assertNotNull(global);
        JSONObject settings = global.getJSONObject("settings");
        JSONObject contact = settings.getJSONObject("contact");
        Assert.assertNotNull(contact);
        Assert.assertEquals("Andrea Aime", contact.get("contactPerson"));
        Assert.assertEquals("UTF-8", settings.get("charset"));
        Assert.assertEquals("8", settings.get("numDecimals").toString().trim());
        Assert.assertEquals("http://geoserver.org", settings.get("onlineResource"));
        JSONObject jaiInfo = global.getJSONObject("jai");
        Assert.assertNotNull(jaiInfo);
        Assert.assertEquals("false", jaiInfo.get("allowInterpolation").toString().trim());
        Assert.assertEquals("0.75", jaiInfo.get("memoryThreshold").toString().trim());
        Assert.assertEquals("5", jaiInfo.get("tilePriority").toString().trim());
        JSONObject covInfo = global.getJSONObject("coverageAccess");
        Assert.assertEquals("UNBOUNDED", covInfo.get("queueType"));
    }

    @Test
    public void testGetGlobalAsXML() throws Exception {
        Document dom = getAsDOM(((ROOT_PATH) + "/settings.xml"));
        Assert.assertEquals("global", dom.getDocumentElement().getLocalName());
        assertXpathEvaluatesTo("UTF-8", "/global/settings/charset", dom);
        assertXpathEvaluatesTo("8", "/global/settings/numDecimals", dom);
        assertXpathEvaluatesTo("http://geoserver.org", "/global/settings/onlineResource", dom);
        assertXpathEvaluatesTo("Andrea Aime", "/global/settings/contact/contactPerson", dom);
        assertXpathEvaluatesTo("false", "/global/jai/allowInterpolation", dom);
        assertXpathEvaluatesTo("0.75", "/global/jai/memoryThreshold", dom);
        assertXpathEvaluatesTo("UNBOUNDED", "/global/coverageAccess/queueType", dom);
    }

    @Test
    public void testPutGlobalAsJSON() throws Exception {
        String inputJson = "{'global': {" + ((((((((((((((((((((((((((((((((("'settings':   {" + "'contact':     {") + "'contactPerson': 'Claudius Ptolomaeus'") + "},") + "'charset': 'UTF-8',") + "'numDecimals': '10',") + "'onlineResource': 'http://geoserver2.org',") + "'verbose': 'false',") + "'verboseExceptions': 'false'") + "},") + "'jai':   {") + "'allowInterpolation': 'false',") + "'recycling': 'true',") + "'tilePriority': '5',") + "'tileThreads': '7',") + "'memoryCapacity': '0.5',") + "'memoryThreshold': '0.75',") + "'imageIOCache': 'false',") + "'pngAcceleration': 'true',") + "'jpegAcceleration': 'true',") + "'allowNativeMosaic': 'false'") + "},") + "'coverageAccess':   {") + "'maxPoolSize': '5',") + "'corePoolSize': '5',") + "'keepAliveTime': '30000',") + "'queueType': 'UNBOUNDED',") + "'imageIOCacheThreshold': '10240'") + "},") + "'updateSequence': '0',") + "'featureTypeCacheSize': '0',") + "'globalServices': 'true',") + "'xmlPostRequestLogBufferSize': '2048'") + "}}");
        MockHttpServletResponse response = putAsServletResponse(((ROOT_PATH) + "/settings/"), inputJson, "text/json");
        Assert.assertEquals(200, response.getStatus());
        JSON json = getAsJSON(((ROOT_PATH) + "/settings.json"));
        JSONObject jsonObject = ((JSONObject) (json));
        Assert.assertNotNull(jsonObject);
        JSONObject global = jsonObject.getJSONObject("global");
        Assert.assertNotNull(global);
        Assert.assertEquals("true", global.get("globalServices").toString().trim());
        Assert.assertEquals("2048", global.get("xmlPostRequestLogBufferSize").toString().trim());
        JSONObject settings = global.getJSONObject("settings");
        Assert.assertNotNull(settings);
        Assert.assertEquals("UTF-8", settings.get("charset"));
        Assert.assertEquals("10", settings.get("numDecimals").toString().trim());
        Assert.assertEquals("http://geoserver2.org", settings.get("onlineResource"));
        JSONObject contact = settings.getJSONObject("contact");
        Assert.assertNotNull(contact);
        Assert.assertEquals("Claudius Ptolomaeus", contact.get("contactPerson"));
        JSONObject jaiInfo = global.getJSONObject("jai");
        Assert.assertNotNull(jaiInfo);
        Assert.assertEquals("false", jaiInfo.get("allowInterpolation").toString().trim());
        Assert.assertEquals("0.75", jaiInfo.get("memoryThreshold").toString().trim());
        JSONObject covInfo = global.getJSONObject("coverageAccess");
        Assert.assertEquals("UNBOUNDED", covInfo.get("queueType"));
    }

    @Test
    public void testGetGlobalAsHTML() throws Exception {
        Document dom = getAsDOM(((ROOT_PATH) + "/settings.html"), 200);
    }

    @Test
    public void testPutGlobalAsXML() throws Exception {
        String xml = "<global><settings>" + ((((((((((((((((((((((((((((("<charset>UTF-8</charset>" + "<numDecimals>10</numDecimals>") + "<onlineResource>http://geoserver.org</onlineResource>") + "<verbose>false</verbose>") + "<verboseExceptions>false</verboseExceptions>") + "<contact><contactPerson>Justin Deoliveira</contactPerson></contact></settings>") + "<jai>") + "<allowInterpolation>true</allowInterpolation>") + "<recycling>false</recycling>") + "<tilePriority>5</tilePriority>") + "<tileThreads>7</tileThreads>") + "<memoryCapacity>0.5</memoryCapacity>") + "<memoryThreshold>0.85</memoryThreshold>") + "<imageIOCache>false</imageIOCache>") + "<pngAcceleration>true</pngAcceleration>") + "<jpegAcceleration>true</jpegAcceleration>") + "<allowNativeMosaic>false</allowNativeMosaic>") + "</jai>") + "<coverageAccess>") + "<maxPoolSize>10</maxPoolSize>") + "<corePoolSize>5</corePoolSize>") + "<keepAliveTime>30000</keepAliveTime>") + "<queueType>UNBOUNDED</queueType>") + "<imageIOCacheThreshold>10240</imageIOCacheThreshold>") + "</coverageAccess>") + "<updateSequence>97</updateSequence>") + "<featureTypeCacheSize>0</featureTypeCacheSize>") + "<globalServices>false</globalServices>") + "<xmlPostRequestLogBufferSize>2048</xmlPostRequestLogBufferSize>") + "</global>");
        MockHttpServletResponse response = putAsServletResponse(((ROOT_PATH) + "/settings/"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((ROOT_PATH) + "/settings.xml"));
        Assert.assertEquals("global", dom.getDocumentElement().getLocalName());
        assertXpathEvaluatesTo("false", "/global/globalServices", dom);
        assertXpathEvaluatesTo("2048", "/global/xmlPostRequestLogBufferSize", dom);
        assertXpathEvaluatesTo("UTF-8", "/global/settings/charset", dom);
        assertXpathEvaluatesTo("10", "/global/settings/numDecimals", dom);
        assertXpathEvaluatesTo("http://geoserver.org", "/global/settings/onlineResource", dom);
        assertXpathEvaluatesTo("Justin Deoliveira", "/global/settings/contact/contactPerson", dom);
        assertXpathEvaluatesTo("true", "/global/jai/allowInterpolation", dom);
        assertXpathEvaluatesTo("0.85", "/global/jai/memoryThreshold", dom);
        assertXpathEvaluatesTo("UNBOUNDED", "/global/coverageAccess/queueType", dom);
    }

    @Test
    public void testGetLocalAsJSON() throws Exception {
        JSON json = getAsJSON(((ROOT_PATH) + "/workspaces/sf/settings.json"));
        JSONObject jsonObject = ((JSONObject) (json));
        Assert.assertNotNull(jsonObject);
        JSONObject settings = jsonObject.getJSONObject("settings");
        Assert.assertNotNull(settings);
        JSONObject workspace = settings.getJSONObject("workspace");
        Assert.assertEquals("sf", workspace.get("name"));
        Assert.assertEquals("UTF-8", settings.get("charset"));
        Assert.assertEquals("8", settings.get("numDecimals").toString().trim());
        Assert.assertEquals("false", settings.get("verbose").toString().trim());
        Assert.assertEquals("false", settings.get("verboseExceptions").toString().trim());
        JSONObject contact = settings.getJSONObject("contact");
        Assert.assertNotNull(contact);
        Assert.assertEquals("Andrea Aime", contact.get("contactPerson"));
    }

    @Test
    public void testGetLocalAsXML() throws Exception {
        Document dom = getAsDOM(((ROOT_PATH) + "/workspaces/sf/settings.xml"));
        Assert.assertEquals("settings", dom.getDocumentElement().getLocalName());
        assertXpathEvaluatesTo("sf", "/settings/workspace/name", dom);
        assertXpathEvaluatesTo("UTF-8", "/settings/charset", dom);
        assertXpathEvaluatesTo("8", "/settings/numDecimals", dom);
        assertXpathEvaluatesTo("false", "/settings/verbose", dom);
        assertXpathEvaluatesTo("false", "/settings/verboseExceptions", dom);
        assertXpathEvaluatesTo("Andrea Aime", "/settings/contact/contactPerson", dom);
    }

    @Test
    public void testGetLocalAsHTML() throws Exception {
        Document dom = getAsDOM(((ROOT_PATH) + "/workspaces/sf/settings.html"), 200);
    }

    @Test
    public void testCreateLocalAsJSON() throws Exception {
        GeoServer geoServer = getGeoServer();
        geoServer.remove(geoServer.getSettings(geoServer.getCatalog().getWorkspaceByName("sf")));
        String json = "{'settings':{'workspace':{'name':'sf'}," + (((("'contact':{'addressCity':'Alexandria','addressCountry':'Egypt','addressType':'Work'," + "'contactEmail':'claudius.ptolomaeus@gmail.com','contactOrganization':'The ancient geographes INC',") + "'contactPerson':'Claudius Ptolomaeus','contactPosition':'Chief geographer'},") + "'charset':'UTF-8','numDecimals':10,'onlineResource':'http://geoserver.org',") + "'proxyBaseUrl':'http://proxy.url','verbose':false,'verboseExceptions':'true'}}");
        MockHttpServletResponse response = postAsServletResponse(((ROOT_PATH) + "/workspaces/sf/settings"), json, "text/json");
        Assert.assertEquals(201, response.getStatus());
        JSON jsonMod = getAsJSON(((ROOT_PATH) + "/workspaces/sf/settings.json"));
        JSONObject jsonObject = ((JSONObject) (jsonMod));
        Assert.assertNotNull(jsonObject);
        JSONObject settings = jsonObject.getJSONObject("settings");
        Assert.assertNotNull(settings);
        JSONObject workspace = settings.getJSONObject("workspace");
        Assert.assertNotNull(workspace);
        Assert.assertEquals("sf", workspace.get("name"));
        Assert.assertEquals("10", settings.get("numDecimals").toString().trim());
        Assert.assertEquals("http://geoserver.org", settings.get("onlineResource"));
        Assert.assertEquals("http://proxy.url", settings.get("proxyBaseUrl"));
        JSONObject contact = settings.getJSONObject("contact");
        Assert.assertEquals("Claudius Ptolomaeus", contact.get("contactPerson"));
        Assert.assertEquals("The ancient geographes INC", contact.get("contactOrganization"));
        Assert.assertEquals("Work", contact.get("addressType"));
        Assert.assertEquals("claudius.ptolomaeus@gmail.com", contact.get("contactEmail"));
    }

    @Test
    public void testCreateLocalAsXML() throws Exception {
        GeoServer geoServer = getGeoServer();
        geoServer.remove(geoServer.getSettings(geoServer.getCatalog().getWorkspaceByName("sf")));
        String xml = "<settings>" + (((((((((((((((("<workspace><name>sf</name></workspace>" + "<contact>") + "<addressCity>Alexandria</addressCity>") + "<addressCountry>Egypt</addressCountry>") + "<addressType>Work</addressType>") + "<contactEmail>claudius.ptolomaeus@gmail.com</contactEmail>") + "<contactOrganization>The ancient geographes INC</contactOrganization>") + "<contactPerson>Claudius Ptolomaeus</contactPerson>") + "<contactPosition>Chief geographer</contactPosition>") + "</contact>") + "<charset>UTF-8</charset>") + "<numDecimals>8</numDecimals>") + "<onlineResource>http://geoserver.org</onlineResource>") + "<proxyBaseUrl>http://proxy.url</proxyBaseUrl>") + "<verbose>false</verbose>") + "<verboseExceptions>false</verboseExceptions>") + "</settings>");
        MockHttpServletResponse response = postAsServletResponse(((ROOT_PATH) + "/workspaces/sf/settings"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        Document dom = getAsDOM(((ROOT_PATH) + "/workspaces/sf/settings.xml"));
        Assert.assertEquals("settings", dom.getDocumentElement().getLocalName());
        assertXpathEvaluatesTo("sf", "/settings/workspace/name", dom);
        assertXpathEvaluatesTo("false", "/settings/verbose", dom);
        assertXpathEvaluatesTo("false", "/settings/verboseExceptions", dom);
        assertXpathEvaluatesTo("http://geoserver.org", "/settings/onlineResource", dom);
        assertXpathEvaluatesTo("http://proxy.url", "/settings/proxyBaseUrl", dom);
        assertXpathEvaluatesTo("Claudius Ptolomaeus", "/settings/contact/contactPerson", dom);
        assertXpathEvaluatesTo("claudius.ptolomaeus@gmail.com", "/settings/contact/contactEmail", dom);
        assertXpathEvaluatesTo("Chief geographer", "/settings/contact/contactPosition", dom);
        assertXpathEvaluatesTo("The ancient geographes INC", "/settings/contact/contactOrganization", dom);
        assertXpathEvaluatesTo("Egypt", "/settings/contact/addressCountry", dom);
    }

    @Test
    public void testCreateLocalAlreadyExists() throws Exception {
        GeoServer geoServer = getGeoServer();
        geoServer.remove(geoServer.getSettings(geoServer.getCatalog().getWorkspaceByName("sf")));
        String xml = "<settings>" + (((((((((((((((("<workspace><name>sf</name></workspace>" + "<contact>") + "<addressCity>Alexandria</addressCity>") + "<addressCountry>Egypt</addressCountry>") + "<addressType>Work</addressType>") + "<contactEmail>claudius.ptolomaeus@gmail.com</contactEmail>") + "<contactOrganization>The ancient geographes INC</contactOrganization>") + "<contactPerson>Claudius Ptolomaeus</contactPerson>") + "<contactPosition>Chief geographer</contactPosition>") + "</contact>") + "<charset>UTF-8</charset>") + "<numDecimals>8</numDecimals>") + "<onlineResource>http://geoserver.org</onlineResource>") + "<proxyBaseUrl>http://proxy.url</proxyBaseUrl>") + "<verbose>false</verbose>") + "<verboseExceptions>false</verboseExceptions>") + "</settings>");
        MockHttpServletResponse response = postAsServletResponse(((ROOT_PATH) + "/workspaces/sf/settings"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        response = postAsServletResponse(((ROOT_PATH) + "/workspaces/sf/settings"), xml, "text/xml");
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testPutLocalAsJSON() throws Exception {
        String inputJson = "{'settings':{'workspace':{'name':'sf'}," + (((("'contact':{'addressCity':'Cairo','addressCountry':'Egypt','addressType':'Work'," + "'contactEmail':'claudius.ptolomaeus@gmail.com','contactOrganization':'The ancient geographes INC',") + "'contactPerson':'Claudius Ptolomaeus','contactPosition':'Chief geographer'},") + "'charset':'UTF-8','numDecimals':8,'onlineResource':'http://geoserver2.org',") + "'proxyBaseUrl':'http://proxy2.url','verbose':true,'verboseExceptions':'true'}}");
        MockHttpServletResponse response = putAsServletResponse(((ROOT_PATH) + "/workspaces/sf/settings"), inputJson, "text/json");
        Assert.assertEquals(200, response.getStatus());
        JSON jsonMod = getAsJSON(((ROOT_PATH) + "/workspaces/sf/settings.json"));
        JSONObject jsonObject = ((JSONObject) (jsonMod));
        Assert.assertNotNull(jsonObject);
        JSONObject settings = jsonObject.getJSONObject("settings");
        Assert.assertNotNull(settings);
        JSONObject workspace = settings.getJSONObject("workspace");
        Assert.assertNotNull(workspace);
        Assert.assertEquals("sf", workspace.get("name"));
        Assert.assertEquals("8", settings.get("numDecimals").toString().trim());
        Assert.assertEquals("http://geoserver2.org", settings.get("onlineResource"));
        Assert.assertEquals("http://proxy2.url", settings.get("proxyBaseUrl"));
        Assert.assertEquals("true", settings.get("verbose").toString().trim());
        Assert.assertEquals("true", settings.get("verboseExceptions").toString().trim());
        JSONObject contact = settings.getJSONObject("contact");
        Assert.assertNotNull(contact);
        Assert.assertEquals("Claudius Ptolomaeus", contact.get("contactPerson"));
        Assert.assertEquals("Cairo", contact.get("addressCity"));
    }

    @Test
    public void testPutLocalAsXML() throws Exception {
        String xml = "<settings>" + (((((((((((((((("<workspace><name>sf</name></workspace>" + "<contact>") + "<addressCity>Cairo</addressCity>") + "<addressCountry>Egypt</addressCountry>") + "<addressType>Work</addressType>") + "<contactEmail>claudius.ptolomaeus@gmail.com</contactEmail>") + "<contactOrganization>The ancient geographes INC</contactOrganization>") + "<contactPerson>Claudius Ptolomaeus</contactPerson>") + "<contactPosition>Chief geographer</contactPosition>") + "</contact>") + "<charset>UTF-8</charset>") + "<numDecimals>10</numDecimals>") + "<onlineResource>http://geoserver2.org</onlineResource>") + "<proxyBaseUrl>http://proxy2.url</proxyBaseUrl>") + "<verbose>true</verbose>") + "<verboseExceptions>true</verboseExceptions>") + "</settings>");
        MockHttpServletResponse response = putAsServletResponse(((ROOT_PATH) + "/workspaces/sf/settings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((ROOT_PATH) + "/workspaces/sf/settings.xml"));
        Assert.assertEquals("settings", dom.getDocumentElement().getLocalName());
        assertXpathEvaluatesTo("sf", "/settings/workspace/name", dom);
        assertXpathEvaluatesTo("true", "/settings/verbose", dom);
        assertXpathEvaluatesTo("true", "/settings/verboseExceptions", dom);
        assertXpathEvaluatesTo("http://geoserver2.org", "/settings/onlineResource", dom);
        assertXpathEvaluatesTo("http://proxy2.url", "/settings/proxyBaseUrl", dom);
        assertXpathEvaluatesTo("Claudius Ptolomaeus", "/settings/contact/contactPerson", dom);
        assertXpathEvaluatesTo("claudius.ptolomaeus@gmail.com", "/settings/contact/contactEmail", dom);
        assertXpathEvaluatesTo("Chief geographer", "/settings/contact/contactPosition", dom);
        assertXpathEvaluatesTo("The ancient geographes INC", "/settings/contact/contactOrganization", dom);
        assertXpathEvaluatesTo("Cairo", "/settings/contact/addressCity", dom);
    }

    @Test
    public void testDeleteLocal() throws Exception {
        JSON json = getAsJSON(((ROOT_PATH) + "/workspaces/sf/settings.json"));
        JSONObject jsonObject = ((JSONObject) (json));
        Assert.assertNotNull(jsonObject);
        Assert.assertEquals(200, deleteAsServletResponse(((ROOT_PATH) + "/workspaces/sf/settings")).getStatus());
        json = getAsJSON(((ROOT_PATH) + "/workspaces/sf/settings.json"));
        JSONObject deletedJson = ((JSONObject) (json));
        Assert.assertNull(deletedJson.get("workspace"));
    }
}

