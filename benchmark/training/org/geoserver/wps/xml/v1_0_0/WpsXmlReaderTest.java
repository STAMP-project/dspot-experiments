/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.xml.v1_0_0;


import java.io.StringReader;
import java.util.Map;
import net.opengis.wfs.GetFeatureType;
import net.opengis.wps10.ExecuteType;
import net.opengis.wps10.InputType;
import org.geoserver.wps.WPSTestSupport;
import org.geoserver.wps.xml.WPSConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class WpsXmlReaderTest extends WPSTestSupport {
    @Test
    public void testWpsExecuteWithGetFeatureAndViewParams() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((((((((((((((("<wps:Execute version=\"1.0.0\" service=\"WPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.opengis.net/wps/1.0.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:wcs=\"http://www.opengis.net/wcs/1.1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd\">\n" + "  <ows:Identifier>gs:Bounds</ows:Identifier>\n") + "  <wps:DataInputs>\n") + "    <wps:Input>\n") + "      <ows:Identifier>features</ows:Identifier>\n") + "      <wps:Reference mimeType=\"text/xml; subtype=wfs-collection/1.0\" ") + " xlink:href=\"http://geoserver/wfs\" method=\"POST\">\n") + "         <wps:Body>\n") + "           <wfs:GetFeature service=\"WFS\" version=\"1.0.0\" outputFormat=\"GML2\" xmlns:test=\"test\" viewParams=\"A:b;C:d\">\n") + "             <wfs:Query typeName=\"test:test\"/>\n") + "           </wfs:GetFeature>\n") + "         </wps:Body>\n") + "      </wps:Reference>\n") + "    </wps:Input>\n") + "  </wps:DataInputs>\n") + "  <wps:ResponseForm>\n") + "    <wps:RawDataOutput>\n") + "      <ows:Identifier>bounds</ows:Identifier>\n") + "    </wps:RawDataOutput>\n") + "  </wps:ResponseForm>\n") + "</wps:Execute>");
        WpsXmlReader reader = new WpsXmlReader("Execute", "1.0.0", new WPSConfiguration(), new org.geoserver.util.EntityResolverProvider(getGeoServer()));
        Object parsed = reader.read(null, new StringReader(xml), null);
        Assert.assertTrue((parsed instanceof ExecuteType));
        ExecuteType request = ((ExecuteType) (parsed));
        Assert.assertTrue(((request.getDataInputs().eContents().size()) > 0));
        InputType features = ((InputType) (request.getDataInputs().eContents().get(0)));
        Assert.assertNotNull(features.getReference());
        Assert.assertNotNull(features.getReference().getBody());
        Assert.assertTrue(((features.getReference().getBody()) instanceof GetFeatureType));
        GetFeatureType getFeature = ((GetFeatureType) (features.getReference().getBody()));
        Assert.assertTrue(((getFeature.getViewParams().size()) > 0));
        Map viewParams = ((Map) (getFeature.getViewParams().get(0)));
        Assert.assertEquals(2, viewParams.keySet().size());
        Assert.assertEquals("b", viewParams.get("A"));
        Assert.assertEquals("d", viewParams.get("C"));
    }
}

