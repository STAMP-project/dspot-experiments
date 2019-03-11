/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.rest.catalog.CatalogRESTTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 * Test REST configuration of app-schema. Note that the mapping and properties file are still copied
 * locally.
 *
 * @author Ben Caradoc-Davies (CSIRO Earth Science and Resource Engineering)
 */
public class RestconfigWfsTest extends CatalogRESTTestSupport {
    private static final String WORKSPACE = "<workspace>"// 
     + ("<name>gsml</name>"// 
     + "</workspace>");

    private static final String NAMESPACE = "<namespace>"// 
     + ("<uri>urn:cgi:xmlns:CGI:GeoSciML:2.0</uri>"// 
     + "</namespace>");

    private static final String DATASTORE = "<dataStore>"// 
     + (((((((((("<name>MappedFeature</name>"// 
     + "<enabled>true</enabled>")// 
     + "<workspace>")// 
     + "<name>gsml</name>")// 
     + "</workspace>")// 
     + "<connectionParameters>")// 
     + "<entry key='dbtype'>app-schema</entry>")// 
     + "<entry key='url'>file:workspaces/gsml/MappedFeature/MappedFeature.xml</entry>")// 
     + "<entry key='namespace'>urn:cgi:xmlns:CGI:GeoSciML:2.0</entry>")// 
     + "</connectionParameters>")// 
     + "</dataStore>");

    private static final String FEATURETYPE = "<featureType>"// 
     + ((((((((((((((((((((((((((("<name>MappedFeature</name>"// 
     + "<nativeName>MappedFeature</nativeName>")// 
     + "<namespace>")// 
     + "<prefix>gsml</prefix>")// 
     + "</namespace>")// 
     + "<title>... TITLE ...</title>")// 
     + "<abstract>... ABSTRACT ...</abstract>")// 
     + "<srs>EPSG:4326</srs>")// 
     + "<latLonBoundingBox>")// 
     + "<minx>-180</minx>")// 
     + "<maxx>180</maxx>")// 
     + "<miny>-90</miny>")// 
     + "<maxy>90</maxy>")// 
     + "<crs>EPSG:4326</crs>")// 
     + "</latLonBoundingBox>")// 
     + "<projectionPolicy>REPROJECT_TO_DECLARED</projectionPolicy>")// 
     + "<enabled>true</enabled>")// 
     + "<metadata>")// 
     + "<entry key='kml.regionateFeatureLimit'>10</entry>")// 
     + "<entry key='indexingEnabled'>false</entry>")// 
     + "<entry key='cachingEnabled'>false</entry>")// 
     + "</metadata>")// 
     + "<store class='dataStore'>")// 
     + "<name>MappedFeature</name>")// 
     + "</store>")// 
     + "<maxFeatures>0</maxFeatures>")// 
     + "<numDecimals>0</numDecimals>")// 
     + "</featureType>");

    public static final String DS_PARAMETERS = "<parameters>"// 
     + (((("<Parameter>"// 
     + "<name>directory</name>")// 
     + "<value>file:./</value>")// 
     + "</Parameter>")// 
     + "</parameters>");// 


    public static final String MAPPING = (((((((((((((((((((((((("<as:AppSchemaDataAccess xmlns:as='http://www.geotools.org/app-schema'>"// 
     + (((((((("<namespaces>"// 
     + "<Namespace>")// 
     + "<prefix>gsml</prefix>")// 
     + "<uri>urn:cgi:xmlns:CGI:GeoSciML:2.0</uri>")// 
     + "</Namespace>")// 
     + "</namespaces>")// 
     + "<sourceDataStores>")// 
     + "<DataStore>")// 
     + "<id>datastore</id>"))// 
     + (RestconfigWfsTest.DS_PARAMETERS)) + "</DataStore>")// 
     + "</sourceDataStores>")// 
     + "<targetTypes>")// 
     + "<FeatureType>")// 
     + "<schemaUri>http://www.geosciml.org/geosciml/2.0/xsd/geosciml.xsd</schemaUri>")// 
     + "</FeatureType>")// 
     + "</targetTypes>")// 
     + "<typeMappings>")// 
     + "<FeatureTypeMapping>")// 
     + "<sourceDataStore>datastore</sourceDataStore>")// 
     + "<sourceType>MAPPEDFEATURE</sourceType>")// 
     + "<targetElement>gsml:MappedFeature</targetElement>")// 
     + "<attributeMappings>")// 
     + "<AttributeMapping>")// 
     + "<targetAttribute>gsml:shape</targetAttribute>")// 
     + "<sourceExpression>")// 
     + "<OCQL>SHAPE</OCQL>")// 
     + "</sourceExpression>")// 
     + "</AttributeMapping>")// 
     + "</attributeMappings>")// 
     + "</FeatureTypeMapping>")// 
     + "</typeMappings>")// 
     + "</as:AppSchemaDataAccess>";

    public static final String PROPERTIES = "_=SHAPE:Geometry:srid=4326\n"// 
     + ("mf.1=POINT(0 1)\n"// 
     + "mf.2=POINT(2 3)\n");

    /**
     * Test that REST can be used to configure an app-schema datastore and that this datastore can
     * be used to service a WFS request.
     */
    @Test
    public void testRestconfig() throws Exception {
        MockHttpServletResponse response;
        // create workspace
        response = postAsServletResponse("/rest/workspaces", RestconfigWfsTest.WORKSPACE, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        WorkspaceInfo ws = getCatalog().getWorkspaceByName("gsml");
        Assert.assertNotNull(ws);
        // set namespace uri (response is 200 as update not create)
        // (default http://gsml was created when workspace was created)
        response = putAsServletResponse("/rest/namespaces/gsml", RestconfigWfsTest.NAMESPACE, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        NamespaceInfo ns = getCatalog().getNamespaceByPrefix("gsml");
        Assert.assertNotNull(ns);
        Assert.assertEquals("urn:cgi:xmlns:CGI:GeoSciML:2.0", ns.getURI());
        // create datastore
        response = postAsServletResponse("/rest/workspaces/gsml/datastores", RestconfigWfsTest.DATASTORE, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        DataStoreInfo ds = getCatalog().getDataStoreByName("gsml", "MappedFeature");
        Assert.assertNotNull(ds);
        // copy the mapping and properties files
        copyFiles();
        // create featuretype
        response = postAsServletResponse("/rest/workspaces/gsml/datastores/MappedFeature/featuretypes", RestconfigWfsTest.FEATURETYPE, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        FeatureTypeInfo ft = getCatalog().getFeatureTypeByName("gsml", "MappedFeature");
        Assert.assertNotNull(ft);
        // test that features can be obtained via WFS
        Document doc = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=gsml:MappedFeature");
        LOGGER.info(("WFS GetFeature&typename=gsml:MappedFeature response:\n" + (prettyString(doc))));
        Assert.assertEquals("wfs:FeatureCollection", doc.getDocumentElement().getNodeName());
        assertXpathCount(2, "//gsml:MappedFeature", doc);
    }
}

