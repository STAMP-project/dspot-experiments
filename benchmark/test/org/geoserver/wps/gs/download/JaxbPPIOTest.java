package org.geoserver.wps.gs.download;


import java.io.ByteArrayInputStream;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static AbstractParametricEntity.NAMESPACE;


public class JaxbPPIOTest {
    @Test
    public void testParseLayer() throws Exception {
        String xml = (((((("<Layer xmlns=\"" + (NAMESPACE)) + "\">\n") + "  <Capabilities>http://demo.geo-solutions.it/geoserver/ows?REQUEST=GetCapabilities&amp;SERVICE=WMS\n") + "  </Capabilities>\n") + "  <Name>tiger:giant_polygon</Name>\n") + "  <Parameter key=\"format\">image/png8</Parameter>\n") + "</Layer>\n";
        JaxbPPIO ppio = new JaxbPPIO(Layer.class, null);
        Layer layer = ((Layer) (ppio.decode(new ByteArrayInputStream(xml.getBytes()))));
        Assert.assertEquals("tiger:giant_polygon", layer.getName());
        Assert.assertEquals("http://demo.geo-solutions.it/geoserver/ows?REQUEST=GetCapabilities&SERVICE=WMS", layer.getCapabilities());
        Map<String, String> parameters = layer.getParametersMap();
        Assert.assertEquals(1, parameters.size());
        Assert.assertEquals("image/png8", parameters.get("format"));
    }

    @Test
    public void testParseFormat() throws Exception {
        String xml = (((("<Format xmlns=\"" + (NAMESPACE)) + "\">\n") + "  <Name>image/jpeg</Name>\n") + "  <Parameter key=\"image-quality\">80%</Parameter>\n") + "</Format>\n";
        JaxbPPIO ppio = new JaxbPPIO(Format.class, null);
        Format format = ((Format) (ppio.decode(new ByteArrayInputStream(xml.getBytes()))));
        Assert.assertEquals("image/jpeg", format.getName());
        Map<String, String> parameters = format.getParametersMap();
        Assert.assertEquals(1, parameters.size());
        Assert.assertEquals("80%", parameters.get("image-quality"));
    }
}

