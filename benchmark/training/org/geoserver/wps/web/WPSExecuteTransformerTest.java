/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.web;


import ParameterType.SUBPROCESS;
import ParameterType.VECTOR_LAYER;
import java.util.Arrays;
import junit.framework.Assert;
import org.custommonkey.xmlunit.XMLUnit;
import org.easymock.classextension.EasyMock;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.wps.web.InputParameterValues.ParameterValue;
import org.geotools.feature.NameImpl;
import org.junit.Test;
import org.opengis.feature.type.Name;
import org.w3c.dom.Document;


public class WPSExecuteTransformerTest extends GeoServerWicketTestSupport {
    @Test
    public void testSingleProcess() throws Exception {
        ExecuteRequest executeBuffer = getExecuteBuffer();
        WPSExecuteTransformer tx = new WPSExecuteTransformer();
        tx.setIndentation(2);
        String xml = tx.transform(executeBuffer);
        System.out.println(xml);
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((((((((((((((((((((("<wps:Execute version=\"1.0.0\" service=\"WPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + "xmlns=\"http://www.opengis.net/wps/1.0.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" ") + "xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wcs=\"http://www.opengis.net/wcs/1.1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" ") + "xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd\">\n") + "  <ows:Identifier>JTS:buffer</ows:Identifier>\n") + "  <wps:DataInputs>\n") + "    <wps:Input>\n") + "      <ows:Identifier>geom</ows:Identifier>\n") + "      <wps:Data>\n") + "        <wps:ComplexData mimeType=\"application/wkt\"><![CDATA[POINT(0 0)]]></wps:ComplexData>\n") + "      </wps:Data>\n") + "    </wps:Input>\n") + "    <wps:Input>\n") + "      <ows:Identifier>distance</ows:Identifier>\n") + "      <wps:Data>\n") + "        <wps:LiteralData>10</wps:LiteralData>\n") + "      </wps:Data>\n") + "    </wps:Input>\n") + "  </wps:DataInputs>\n") + "  <wps:ResponseForm>\n") + "    <wps:RawDataOutput mimeType=\"text/xml; subtype=gml/3.1.1\">\n") + "      <ows:Identifier>result</ows:Identifier>\n") + "    </wps:RawDataOutput>\n") + "  </wps:ResponseForm>\n") + "</wps:Execute>");
        Document test = XMLUnit.buildTestDocument(xml);
        checkValidationErrors(test);
        Document control = XMLUnit.buildControlDocument(expected);
        assertXMLEqual(control, test);
    }

    @Test
    public void testSubprocess() throws Exception {
        Name areaName = new NameImpl("JTS", "area");
        InputParameterValues areaGeomValues = new InputParameterValues(areaName, "geom");
        ParameterValue geom = areaGeomValues.values.get(0);
        geom.setType(SUBPROCESS);
        geom.setValue(getExecuteBuffer());
        OutputParameter bufferOutput = new OutputParameter(areaName, "result");
        ExecuteRequest executeArea = new ExecuteRequest(areaName.getURI(), Arrays.asList(areaGeomValues), Arrays.asList(bufferOutput));
        WPSExecuteTransformer tx = new WPSExecuteTransformer();
        tx.setIndentation(2);
        String xml = tx.transform(executeArea);
        // System.out.println(xml);
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((((((((((((((((((((((((((((((((("<wps:Execute version=\"1.0.0\" service=\"WPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.opengis.net/wps/1.0.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:wcs=\"http://www.opengis.net/wcs/1.1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd\">\n" + "  <ows:Identifier>JTS:area</ows:Identifier>\n") + "  <wps:DataInputs>\n") + "    <wps:Input>\n") + "      <ows:Identifier>geom</ows:Identifier>\n") + "      <wps:Reference mimeType=\"text/xml; subtype=gml/3.1.1\" xlink:href=\"http://geoserver/wps\" method=\"POST\">\n") + "        <wps:Body>\n") + "          <wps:Execute version=\"1.0.0\" service=\"WPS\">\n") + "            <ows:Identifier>JTS:buffer</ows:Identifier>\n") + "            <wps:DataInputs>\n") + "              <wps:Input>\n") + "                <ows:Identifier>geom</ows:Identifier>\n") + "                <wps:Data>\n") + "                  <wps:ComplexData mimeType=\"application/wkt\"><![CDATA[POINT(0 0)]]></wps:ComplexData>\n") + "                </wps:Data>\n") + "              </wps:Input>\n") + "              <wps:Input>\n") + "                <ows:Identifier>distance</ows:Identifier>\n") + "                <wps:Data>\n") + "                  <wps:LiteralData>10</wps:LiteralData>\n") + "                </wps:Data>\n") + "              </wps:Input>\n") + "            </wps:DataInputs>\n") + "            <wps:ResponseForm>\n") + "              <wps:RawDataOutput mimeType=\"text/xml; subtype=gml/3.1.1\">\n") + "                <ows:Identifier>result</ows:Identifier>\n") + "              </wps:RawDataOutput>\n") + "            </wps:ResponseForm>\n") + "          </wps:Execute>\n") + "        </wps:Body>\n") + "      </wps:Reference>\n") + "    </wps:Input>\n") + "  </wps:DataInputs>\n") + "  <wps:ResponseForm>\n") + "    <wps:RawDataOutput>\n") + "      <ows:Identifier>result</ows:Identifier>\n") + "    </wps:RawDataOutput>\n") + "  </wps:ResponseForm>\n") + "</wps:Execute>");
        Document test = XMLUnit.buildTestDocument(xml);
        checkValidationErrors(test);
        Document control = XMLUnit.buildControlDocument(expected);
        assertXMLEqual(control, test);
    }

    @Test
    public void testBoundingBoxEncoding() throws Exception {
        ExecuteRequest executeClipAndShip = getExecuteClipAndShip();
        WPSExecuteTransformer tx = new WPSExecuteTransformer();
        tx.setIndentation(2);
        String xml = tx.transform(executeClipAndShip);
        // System.out.println(xml);
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><wps:Execute version=\"1.0.0\" service=\"WPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.opengis.net/wps/1.0.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:wcs=\"http://www.opengis.net/wcs/1.1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd\">\n" + (((((((((((((((((((((((((((((((((((((((((((((((((((("  <ows:Identifier>gs:CropCoverage</ows:Identifier>\n" + "  <wps:DataInputs>\n") + "    <wps:Input>\n") + "      <ows:Identifier>coverage</ows:Identifier>\n") + "      <wps:Reference mimeType=\"image/tiff\" xlink:href=\"http://geoserver/wcs\" method=\"POST\">\n") + "        <wps:Body>\n") + "          <wcs:GetCoverage service=\"WCS\" version=\"1.1.1\">\n") + "            <ows:Identifier>geosolutions:usa</ows:Identifier>\n") + "            <wcs:DomainSubset>\n") + "              <ows:BoundingBox crs=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">\n") + "                <ows:LowerCorner>-180.0 -90.000000000036</ows:LowerCorner>\n") + "                <ows:UpperCorner>180.0 90.0</ows:UpperCorner>\n") + "              </ows:BoundingBox>\n") + "            </wcs:DomainSubset>\n") + "            <wcs:Output format=\"image/tiff\"/>\n") + "          </wcs:GetCoverage>\n") + "        </wps:Body>\n") + "      </wps:Reference>\n") + "    </wps:Input>\n") + "    <wps:Input>\n") + "      <ows:Identifier>cropShape</ows:Identifier>\n") + "      <wps:Reference mimeType=\"text/xml; subtype=gml/3.1.1\" xlink:href=\"http://geoserver/wps\" method=\"POST\">\n") + "        <wps:Body>\n") + "          <wps:Execute version=\"1.0.0\" service=\"WPS\">\n") + "            <ows:Identifier>gs:CollectGeometries</ows:Identifier>\n") + "            <wps:DataInputs>\n") + "              <wps:Input>\n") + "                <ows:Identifier>features</ows:Identifier>\n") + "                <wps:Reference mimeType=\"text/xml; subtype=wfs-collection/1.0\" xlink:href=\"http://geoserver/wfs\" method=\"POST\">\n") + "                  <wps:Body>\n") + "                    <wfs:GetFeature service=\"WFS\" version=\"1.0.0\" outputFormat=\"GML2\">\n") + "                      <wfs:Query typeName=\"geosolutions:states\"/>\n") + "                    </wfs:GetFeature>\n") + "                  </wps:Body>\n") + "                </wps:Reference>\n") + "              </wps:Input>\n") + "            </wps:DataInputs>\n") + "            <wps:ResponseForm>\n") + "              <wps:RawDataOutput mimeType=\"text/xml; subtype=gml/3.1.1\">\n") + "                <ows:Identifier>result</ows:Identifier>\n") + "              </wps:RawDataOutput>\n") + "            </wps:ResponseForm>\n") + "          </wps:Execute>\n") + "        </wps:Body>\n") + "      </wps:Reference>\n") + "    </wps:Input>\n") + "  </wps:DataInputs>\n") + "  <wps:ResponseForm>\n") + "    <wps:RawDataOutput mimeType=\"image/tiff\">\n") + "      <ows:Identifier>result</ows:Identifier>\n") + "    </wps:RawDataOutput>\n") + "  </wps:ResponseForm>\n") + "</wps:Execute>");
        Document test = XMLUnit.buildTestDocument(xml);
        checkValidationErrors(test);
        Document control = XMLUnit.buildControlDocument(expected);
        assertXMLEqual(control, test);
    }

    @Test
    public void testIncludeNamespaceMapping() throws Exception {
        Name centroidName = new NameImpl("gs", "Centroid");
        InputParameterValues inputValues = new InputParameterValues(centroidName, "features");
        VectorLayerConfiguration layer = new VectorLayerConfiguration();
        layer.setLayerName("foo:myLayer");
        ParameterValue features = inputValues.values.get(0);
        features.setType(VECTOR_LAYER);
        features.setValue(layer);
        OutputParameter output = new OutputParameter(centroidName, "result");
        ExecuteRequest execute = new ExecuteRequest(centroidName.getURI(), Arrays.asList(inputValues), Arrays.asList(output));
        NamespaceInfo fooNs = EasyMock.createNiceMock(NamespaceInfo.class);
        expect(fooNs.getURI()).andReturn("http://foo.org");
        replay(fooNs);
        Catalog cat = createNiceMock(Catalog.class);
        expect(cat.getNamespaceByPrefix("foo")).andReturn(fooNs);
        replay(cat);
        WPSExecuteTransformer tx = new WPSExecuteTransformer(cat);
        tx.setIndentation(2);
        String xml = tx.transform(execute);
        Assert.assertTrue(xml.contains("xmlns:foo=\"http://foo.org\""));
    }
}

