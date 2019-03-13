/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.kml;


import KMLMapOutputFormat.MIME_TYPE;
import MockData.AGGREGATEGEOFEATURE;
import WfsFactory.eINSTANCE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import net.opengis.wfs.FeatureCollectionType;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.ServiceException;
import org.geoserver.wfs.WFSTestSupport;
import org.geoserver.wfs.request.FeatureCollectionResponse;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.collection.DecoratingSimpleFeatureCollection;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;

import static KMLMapOutputFormat.MIME_TYPE;


public class KMLWFSTest extends WFSTestSupport {
    @Test
    public void testGetCapabilities() throws Exception {
        Document doc = getAsDOM("wfs?request=GetCapabilities&version=1.1.0");
        // print(doc);
        // check the new output format is part of the caps document
        XMLAssert.assertXpathEvaluatesTo("1", ((("count(//ows:Operation[@name='GetFeature']/" + "ows:Parameter[@name='outputFormat']/ows:Value[text() = '") + (MIME_TYPE)) + "'])"), doc);
    }

    @Test
    public void testGetFeature() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((("wfs?service=WFS&version=1.1.0&request=GetFeature&typeName=" + (getLayerId(AGGREGATEGEOFEATURE))) + "&outputFormat=") + (MIME_TYPE.replace("+", "%2B"))));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals((("inline; filename=" + (AGGREGATEGEOFEATURE.getLocalPart())) + ".kml"), response.getHeader("Content-Disposition"));
        Document doc = dom(new ByteArrayInputStream(response.getContentAsString().getBytes()));
        checkAggregateGeoFeatureKmlContents(doc);
    }

    @Test
    public void testGetFeatureKMLAlias() throws Exception {
        Document doc = getAsDOM((("wfs?service=WFS&version=1.1.0&request=GetFeature&typeName=" + (getLayerId(AGGREGATEGEOFEATURE))) + "&outputFormat=KML"));
        checkAggregateGeoFeatureKmlContents(doc);
    }

    @Test
    public void testForceWGS84() throws Exception {
        Document doc = getAsDOM((("wfs?service=WFS&version=1.1.0&request=GetFeature&typeName=" + (getLayerId(MockData.MPOINTS))) + "&outputFormat=KML"));
        // print(doc);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//kml:Folder)", doc);
        KMLTest.assertPointCoordinate(doc, "//kml:Placemark/kml:MultiGeometry/kml:Point[1]/kml:coordinates", (-92.99707024070754), 4.523788746085423);
        KMLTest.assertPointCoordinate(doc, "//kml:Placemark/kml:MultiGeometry/kml:Point[2]/kml:coordinates", (-92.99661950641159), 4.524241081543828);
    }

    @Test
    public void testCloseIterators() throws IOException, ServiceException {
        // build a wfs response with an iterator that will mark if close has been called, or not
        FeatureTypeInfo fti = getCatalog().getFeatureTypeByName(getLayerId(MockData.POLYGONS));
        FeatureSource fs = fti.getFeatureSource(null, null);
        SimpleFeatureCollection fc = ((SimpleFeatureCollection) (fs.getFeatures()));
        final AtomicInteger openIterators = new AtomicInteger(0);
        FeatureCollection decorated = new DecoratingSimpleFeatureCollection(fc) {
            @Override
            public SimpleFeatureIterator features() {
                openIterators.incrementAndGet();
                final SimpleFeature f = DataUtilities.first(delegate);
                return new SimpleFeatureIterator() {
                    @Override
                    public SimpleFeature next() throws NoSuchElementException {
                        return f;
                    }

                    @Override
                    public boolean hasNext() {
                        return true;
                    }

                    @Override
                    public void close() {
                        openIterators.decrementAndGet();
                    }
                };
            }
        };
        FeatureCollectionType response = eINSTANCE.createFeatureCollectionType();
        response.getFeature().add(decorated);
        FeatureCollectionResponse fcResponse = FeatureCollectionResponse.adapt(response);
        WFSKMLOutputFormat outputFormat = GeoServerExtensions.bean(WFSKMLOutputFormat.class);
        FilterOutputStream fos = new FilterOutputStream(new ByteArrayOutputStream()) {
            int count = 0;

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                for (int i = off; i < len; i++) {
                    write(b[i]);
                }
            }

            @Override
            public void write(int b) throws IOException {
                (count)++;
                if ((count) > 100) {
                    throw new IOException("Simularing client shutting down connection");
                }
                super.write(b);
            }
        };
        try {
            outputFormat.write(fcResponse, fos, null);
        } catch (Exception e) {
            // fine, it's expected
        }
        Assert.assertEquals(0, openIterators.get());
    }
}

