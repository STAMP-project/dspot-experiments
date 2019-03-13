/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.xml;


import MockData.FIFTEEN;
import MockData.POLYGONS;
import MockData.SEVEN;
import WfsFactory.eINSTANCE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.geoserver.wfs.WFSTestSupport;
import org.geoserver.wfs.request.FeatureCollectionResponse;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.w3c.dom.Document;


public class GML3FeatureProducerTest extends WFSTestSupport {
    @Test
    public void testSingle() throws Exception {
        FeatureSource<? extends FeatureType, ? extends Feature> source = getFeatureSource(SEVEN);
        FeatureCollection<? extends FeatureType, ? extends Feature> features = source.getFeatures();
        FeatureCollectionResponse fcType = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        fcType.getFeature().add(features);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        producer().write(fcType, output, request(SEVEN));
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(new ByteArrayInputStream(output.toByteArray()));
        Assert.assertEquals(7, document.getElementsByTagName("cdf:Seven").getLength());
    }

    @Test
    public void testMultipleSameNamespace() throws Exception {
        FeatureCollectionResponse fcType = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        fcType.getFeature().add(getFeatureSource(SEVEN).getFeatures());
        fcType.getFeature().add(getFeatureSource(FIFTEEN).getFeatures());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        producer().write(fcType, output, request(SEVEN, FIFTEEN));
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(new ByteArrayInputStream(output.toByteArray()));
        Assert.assertEquals((7 + 15), ((document.getElementsByTagName("cdf:Seven").getLength()) + (document.getElementsByTagName("cdf:Fifteen").getLength())));
    }

    @Test
    public void testMultipleDifferentNamespace() throws Exception {
        FeatureCollectionResponse fcType = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        fcType.getFeature().add(getFeatureSource(SEVEN).getFeatures());
        fcType.getFeature().add(getFeatureSource(POLYGONS).getFeatures());
        int npolys = getFeatureSource(POLYGONS).getFeatures().size();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        producer().write(fcType, output, request(SEVEN, POLYGONS));
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(new ByteArrayInputStream(output.toByteArray()));
        Assert.assertEquals((7 + npolys), ((document.getElementsByTagName("cdf:Seven").getLength()) + (document.getElementsByTagName("cgf:Polygons").getLength())));
    }
}

