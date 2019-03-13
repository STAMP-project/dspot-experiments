/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.response;


import javax.xml.namespace.QName;
import org.geoserver.data.test.MockData;
import org.geoserver.wfs.WFSTestSupport;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Contains tests related with GeoJSON output format when requested through WFS.
 */
public final class GeoJsonOutputFormatTest extends WFSTestSupport {
    private static final QName LINESTRING_ZM = new QName(MockData.DEFAULT_URI, "lineStringZm", MockData.DEFAULT_PREFIX);

    @Test
    public void testMeasuresEncoding() throws Exception {
        // execute the WFS request asking for a GeoJSON output
        MockHttpServletResponse response = getAsServletResponse(("wfs?request=GetFeature&typenames=gs:lineStringZm&version=2.0.0" + "&service=wfs&outputFormat=application/json"));
        // check that measures where not encoded
        Assert.assertThat(response.getContentAsString(), Matchers.notNullValue());
        Assert.assertThat(response.getContentAsString(), Matchers.containsString("[[120,50,20],[90,80,35]]"));
        // activate measures encoding
        WFSTestSupport.setMeasuresEncoding(getCatalog(), GeoJsonOutputFormatTest.LINESTRING_ZM.getLocalPart(), true);
        response = getAsServletResponse(("wfs?request=GetFeature&typenames=gs:lineStringZm&version=2.0.0" + "&service=wfs&outputFormat=application/json"));
        // check that measures where encoded
        Assert.assertThat(response.getContentAsString(), Matchers.notNullValue());
        Assert.assertThat(response.getContentAsString(), Matchers.containsString("[[120,50,20,15],[90,80,35,5]]"));
    }
}

