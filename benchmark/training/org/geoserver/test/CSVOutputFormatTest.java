/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Test for SF0 CSV outputFormat in App-schema {@link BoreholeViewMockData}
 *
 * @author Rini Angreani (CSIRO Earth Science and Resource Engineering)
 */
public class CSVOutputFormatTest extends AbstractAppSchemaTestSupport {
    /**
     * Tests full request with CSV outputFormat.
     */
    @Test
    public void testFullRequest() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?service=WFS&version=1.1.0&request=GetFeature&typename=gsmlp:BoreholeView&outputFormat=csv");
        // check the mime type
        Assert.assertEquals("text/csv", resp.getContentType());
        // check the content disposition
        Assert.assertEquals("attachment; filename=BoreholeView.csv", resp.getHeader("Content-Disposition"));
        // read the response back with a parser that can handle escaping, newlines and what not
        List<String[]> lines = CSVOutputFormatTest.readLines(resp.getContentAsString());
        // we should have one header line and then all the features in that feature type
        Assert.assertEquals(3, lines.size());
        // check the header
        String[] header = new String[]{ "gml:id", "gsmlp:identifier", "gsmlp:name", "gsmlp:drillingMethod", "gsmlp:driller", "gsmlp:drillStartDate", "gsmlp:startPoint", "gsmlp:inclinationType", "gsmlp:boreholeMaterialCustodian", "gsmlp:boreholeLength_m", "gsmlp:elevation_m", "gsmlp:elevation_srs", "gsmlp:specification_uri", "gsmlp:metadata_uri", "gsmlp:shape" };
        Assert.assertTrue(Arrays.asList(lines.get(0)).containsAll(Arrays.asList(header)));
        // check each line has the expected number of elements (num of att + 1 for the id)
        int headerCount = lines.get(0).length;
        Assert.assertEquals(headerCount, lines.get(1).length);
        Assert.assertEquals(headerCount, lines.get(2).length);
    }

    /**
     * Tests CSV outputFormat with filters.
     */
    @Test
    public void testFilter() throws Exception {
        String IDENTIFIER = "borehole.GA.17338";
        String xml = ((((((("<wfs:GetFeature service=\"WFS\" "// 
         + ((((((("version=\"1.1.0\" "// 
         + "xmlns:ogc=\"http://www.opengis.net/ogc\" ")// 
         + "xmlns:wfs=\"http://www.opengis.net/wfs\" ")// 
         + "xmlns:gsmlp=\"http://xmlns.geosciml.org/geosciml-portrayal/2.0\" >")// 
         + "    <wfs:Query typeName=\"gsmlp:BoreholeView\" outputFormat=\"csv\">")// 
         + "        <ogc:Filter>")// 
         + "            <ogc:PropertyIsEqualTo>")// 
         + "                <ogc:Literal>")) + IDENTIFIER) + "</ogc:Literal>")// 
         + "                <ogc:PropertyName>gsmlp:identifier</ogc:PropertyName>")// 
         + "            </ogc:PropertyIsEqualTo>")// 
         + "        </ogc:Filter>")// 
         + "    </wfs:Query> ")// 
         + "</wfs:GetFeature>";
        MockHttpServletResponse resp = postAsServletResponse("wfs?service=WFS&request=GetFeature&version=1.1.0&typeName=gsmlp:BoreholeView&outputFormat=csv", xml, "text/csv");
        // check the mime type
        Assert.assertEquals("text/csv", resp.getContentType());
        // check the content disposition
        Assert.assertEquals("attachment; filename=BoreholeView.csv", resp.getHeader("Content-Disposition"));
        // read the response back with a parser that can handle escaping, newlines and what not
        List<String[]> lines = CSVOutputFormatTest.readLines(resp.getContentAsString());
        // we should have one header line and then all the features in that feature type
        Assert.assertEquals(2, lines.size());
        int identifierIndex = Arrays.asList(lines.get(0)).indexOf("gsmlp:identifier");
        Assert.assertEquals(IDENTIFIER, lines.get(1)[identifierIndex]);
        // check the header
        String[] header = new String[]{ "gml:id", "gsmlp:identifier", "gsmlp:name", "gsmlp:drillingMethod", "gsmlp:driller", "gsmlp:drillStartDate", "gsmlp:startPoint", "gsmlp:inclinationType", "gsmlp:boreholeMaterialCustodian", "gsmlp:boreholeLength_m", "gsmlp:elevation_m", "gsmlp:elevation_srs", "gsmlp:specification_uri", "gsmlp:metadata_uri", "gsmlp:shape" };
        Assert.assertTrue(Arrays.asList(lines.get(0)).containsAll(Arrays.asList(header)));
        // check each line has the expected number of elements (num of att + 1 for the id)
        int headerCount = lines.get(0).length;
        Assert.assertEquals(headerCount, lines.get(1).length);
    }
}

