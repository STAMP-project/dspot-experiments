/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw.store.internal;


import org.geotools.csw.CSWConfiguration;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 *
 *
 * @author Niels Charlier
 */
public class LayerGroupTest extends CSWInternalTestSupport {
    private static String NAME_FORESTSANDSTREAMS = "Forests and Streams";

    private static String NAME_BUILDINGSANDBRIDGES = "Buildings and Bridges";

    private String id_forestsandstreams;

    private String id_buildingsandbridges;

    @Test
    public void testRecords() throws Exception {
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=csw:Record" + "&resultType=results&elementSetName=full&maxRecords=100";
        Document d = getAsDOM(request);
        // print(d);
        checkValidationErrors(d, new CSWConfiguration());
        assertXpathExists((("//csw:Record[dc:title='" + (LayerGroupTest.NAME_BUILDINGSANDBRIDGES)) + "']"), d);
        assertXpathExists((("//csw:Record[dc:title='" + (LayerGroupTest.NAME_FORESTSANDSTREAMS)) + "']"), d);
        // check that layer groups keywords were encoded
        assertXpathExists("//csw:Record[dc:subject='keyword1']", d);
        assertXpathExists("//csw:Record[dc:subject='keyword2']", d);
    }

    @Test
    public void testRecordById() throws Exception {
        String request = "csw?service=CSW&version=2.0.2&request=GetRecordById&typeNames=csw:Record&id=" + (id_forestsandstreams);
        Document d = getAsDOM(request);
        // print(d);
        checkValidationErrors(d);
        assertXpathEvaluatesTo("1", "count(//csw:SummaryRecord)", d);
    }

    @Test
    public void testRecordSortedAndPaged() throws Exception {
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=csw:Record&resultType=results&SortBy=title&StartPosition=4&maxRecords=2";
        Document d = getAsDOM(request);
        print(d);
        checkValidationErrors(d);
        assertXpathEvaluatesTo("2", "count(//csw:SummaryRecord)", d);
        assertXpathExists((("//csw:SummaryRecord[dc:title='" + (LayerGroupTest.NAME_BUILDINGSANDBRIDGES)) + "']"), d);
        assertXpathExists("//csw:SummaryRecord[dc:title='Buildings']", d);
    }
}

