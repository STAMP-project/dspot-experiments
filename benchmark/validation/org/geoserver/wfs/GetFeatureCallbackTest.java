/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQL;
import org.junit.Test;
import org.w3c.dom.Document;


public class GetFeatureCallbackTest extends WFSTestSupport {
    private GetFeatureCallbackTester tester;

    @Test
    public void testNoOp() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&typename=cdf:Fifteen&version=1.0.0&service=wfs");
        print(doc);
        assertXpathEvaluatesTo("15", "count(//cdf:Fifteen)", doc);
    }

    @Test
    public void testAlterQuery() throws Exception {
        tester.contextConsumer = (GetFeatureContext ctx) -> {
            Query query = new Query(ctx.getQuery());
            try {
                query.setFilter(CQL.toFilter("NAME = 'Main Street'"));
                ctx.setQuery(query);
            } catch ( e) {
                throw new <e>RuntimeException();
            }
        };
        Document doc = getAsDOM((("wfs?request=GetFeature&typename=" + (getLayerId(ROAD_SEGMENTS))) + "&version=1.1.0&service=wfs"));
        print(doc);
        assertXpathEvaluatesTo("1", "count(//cite:RoadSegments)", doc);
        assertXpathEvaluatesTo("Main Street", "//cite:RoadSegments/cite:NAME", doc);
    }
}

