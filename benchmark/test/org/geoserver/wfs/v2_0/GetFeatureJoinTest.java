/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v2_0;


import java.util.List;
import javax.xml.namespace.QName;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.data.test.SystemTestData;
import org.geotools.data.FeatureSource;
import org.geotools.filter.v2_0.FES;
import org.geotools.wfs.v2_0.WFS;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class GetFeatureJoinTest extends WFS20TestSupport {
    @Test
    public void testSpatialJoinPOST() throws Exception {
        String xml = ((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='a b'>") + "<fes:Filter> ") + "<fes:Intersects> ") + "<fes:ValueReference>a/the_geom</fes:ValueReference> ") + "<fes:ValueReference>b/the_geom</fes:ValueReference>") + "</fes:Intersects> ") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[position() = 1]/wfs:member/gs:Forests/gs:NAME[text() = 'Green Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[position() = 1]/wfs:member/gs:Lakes/gs:NAME[text() = 'Blue Lake']", dom);
        XMLAssert.assertXpathExists("wfs:FeatureCollection/wfs:member[position()=2]/wfs:Tuple//gs:Forests/gs:NAME[text() = 'Foo Forest']", dom);
        XMLAssert.assertXpathExists("wfs:FeatureCollection/wfs:member[position()=2]/wfs:Tuple//gs:Lakes/gs:NAME[text() = 'Green Lake']", dom);
    }

    @Test
    public void testSpatialJoinNoAliasesCustomPrefixes() throws Exception {
        String xml = (((((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "'") + " xmlns:ns123='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='ns123:Forests ns123:Lakes'>") + "<fes:Filter> ") + "<fes:Intersects> ") + "<fes:ValueReference>ns123:Forests/the_geom</fes:ValueReference> ") + "<fes:ValueReference>ns123:Lakes/the_geom</fes:ValueReference>") + "</fes:Intersects> ") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[position() = 1]/wfs:member/gs:Forests/gs:NAME[text() = 'Green Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[position() = 1]/wfs:member/gs:Lakes/gs:NAME[text() = 'Blue Lake']", dom);
        XMLAssert.assertXpathExists("wfs:FeatureCollection/wfs:member[position()=2]/wfs:Tuple//gs:Forests/gs:NAME[text() = 'Foo Forest']", dom);
        XMLAssert.assertXpathExists("wfs:FeatureCollection/wfs:member[position()=2]/wfs:Tuple//gs:Lakes/gs:NAME[text() = 'Green Lake']", dom);
    }

    @Test
    public void testSpatialJoinGET() throws Exception {
        Document dom = getAsDOM("wfs?service=WFS&version=2.0.0&request=getFeature&typenames=gs:Forests,gs:Lakes&aliases=a,b&filter=%3CFilter%3E%3CIntersects%3E%3CValueReference%3Ea%2Fthe_geom%3C%2FValueReference%3E%3CValueReference%3Eb%2Fthe_geom%3C%2FValueReference%3E%3C%2FIntersects%3E%3C%2FFilter%3E");
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[position() = 1]/wfs:member/gs:Forests/gs:NAME[text() = 'Green Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[position() = 1]/wfs:member/gs:Lakes/gs:NAME[text() = 'Blue Lake']", dom);
        XMLAssert.assertXpathExists("wfs:FeatureCollection/wfs:member[position()=2]/wfs:Tuple//gs:Forests/gs:NAME[text() = 'Foo Forest']", dom);
        XMLAssert.assertXpathExists("wfs:FeatureCollection/wfs:member[position()=2]/wfs:Tuple//gs:Lakes/gs:NAME[text() = 'Green Lake']", dom);
    }

    @Test
    public void testSpatialJoinPOSTWithPrimaryFilter() throws Exception {
        String xml = ((((((((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='a b'>") + "<fes:Filter> ") + "<fes:And>") + "<fes:Intersects> ") + "<fes:ValueReference>a/the_geom</fes:ValueReference> ") + "<fes:ValueReference>b/the_geom</fes:ValueReference>") + "</fes:Intersects> ") + "<PropertyIsEqualTo>") + "<ValueReference>a/FID</ValueReference>") + "<Literal>110</Literal>") + "</PropertyIsEqualTo>") + "</fes:And>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Forests/gs:NAME[text() = 'Foo Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Lakes/gs:NAME[text() = 'Green Lake']", dom);
    }

    @Test
    public void testSpatialJoinPOSTWithSecondaryFilter() throws Exception {
        String xml = ((((((((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='a b'>") + "<fes:Filter> ") + "<fes:And>") + "<fes:Intersects> ") + "<fes:ValueReference>a/the_geom</fes:ValueReference> ") + "<fes:ValueReference>b/the_geom</fes:ValueReference>") + "</fes:Intersects> ") + "<PropertyIsEqualTo>") + "<ValueReference>b/FID</ValueReference>") + "<Literal>101</Literal>") + "</PropertyIsEqualTo>") + "</fes:And>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Forests/gs:NAME[text() = 'Green Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Lakes/gs:NAME[text() = 'Blue Lake']", dom);
    }

    @Test
    public void testSpatialJoinWithBothFilters() throws Exception {
        String xml = ((((((((((((((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='a b'>") + "<fes:Filter> ") + "<fes:And>") + "<fes:Disjoint> ") + "<fes:ValueReference>a/the_geom</fes:ValueReference> ") + "<fes:ValueReference>b/the_geom</fes:ValueReference>") + "</fes:Disjoint> ") + "<fes:And>") + "<PropertyIsEqualTo>") + "<ValueReference>a/NAME</ValueReference>") + "<Literal>Bar Forest</Literal>") + "</PropertyIsEqualTo>") + "<PropertyIsGreaterThan>") + "<ValueReference>b/FID</ValueReference>") + "<Literal>102</Literal>") + "</PropertyIsGreaterThan>") + "</fes:And>") + "</fes:And>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:Tuple/wfs:member/gs:Forests/gs:NAME[text() = 'Bar Forest'])", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Lakes/gs:NAME[text() = 'Green Lake']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Lakes/gs:NAME[text() = 'Black Lake']", dom);
    }

    @Test
    public void testStandardJoin() throws Exception {
        String xml = ((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='a b'>") + "<fes:Filter> ") + "<PropertyIsEqualTo>") + "<ValueReference>a/FID</ValueReference>") + "<ValueReference>b/FID</ValueReference>") + "</PropertyIsEqualTo>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Forests/gs:NAME[text() = 'Foo Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Lakes/gs:NAME[text() = 'Black Lake']", dom);
    }

    @Test
    public void testJoinAliasConflictProperty() throws Exception {
        String xml = ((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='a NAME'>") + "<fes:Filter> ") + "<PropertyIsEqualTo>") + "<ValueReference>a/FID</ValueReference>") + "<ValueReference>NAME/FID</ValueReference>") + "</PropertyIsEqualTo>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Forests/gs:NAME[text() = 'Foo Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Lakes/gs:NAME[text() = 'Black Lake']", dom);
    }

    @Test
    public void testStandardJoinThreeWays() throws Exception {
        String xml = ((((((((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes gs:Lakes' aliases='a b c'>") + "<fes:Filter> ") + "<And>") + "<PropertyIsEqualTo>") + "<ValueReference>a/FID</ValueReference>") + "<ValueReference>b/FID</ValueReference>") + "</PropertyIsEqualTo>") + "<PropertyIsEqualTo>") + "<ValueReference>b/FID</ValueReference>") + "<ValueReference>c/FID</ValueReference>") + "</PropertyIsEqualTo>") + "</And>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member[1]/gs:Lakes/gs:NAME[text() = 'Black Lake']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member[2]/gs:Forests/gs:NAME[text() = 'Foo Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member[3]/gs:Lakes/gs:NAME[text() = 'Black Lake']", dom);
    }

    @Test
    public void testStandardJoinThreeWaysLocalFilters() throws Exception {
        String xml = ((((((((((((((((((((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:t1 gs:t2 gs:t3' aliases='a b c'>") + "<fes:Filter> ") + "<And>") + "<PropertyIsEqualTo>") + "<ValueReference>a/name1</ValueReference>") + "<Literal>First</Literal>") + "</PropertyIsEqualTo>") + "<PropertyIsEqualTo>") + "<ValueReference>a/code1</ValueReference>") + "<ValueReference>b/code2</ValueReference>") + "</PropertyIsEqualTo>") + "<PropertyIsEqualTo>") + "<ValueReference>b/name2</ValueReference>") + "<Literal>Second</Literal>") + "</PropertyIsEqualTo>") + "<PropertyIsEqualTo>") + "<ValueReference>c/name3</ValueReference>") + "<Literal>Third</Literal>") + "</PropertyIsEqualTo>") + "<PropertyIsEqualTo>") + "<ValueReference>b/code2</ValueReference>") + "<ValueReference>c/code3</ValueReference>") + "</PropertyIsEqualTo>") + "</And>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member[1]/gs:t2[gs:name2 = 'Second']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member[2]/gs:t1[gs:name1 = 'First']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member[3]/gs:t3[gs:name3 = 'Third']", dom);
    }

    @Test
    public void testStandardJoin2() throws Exception {
        String xml = ((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='c d'>") + "<fes:Filter> ") + "<PropertyIsEqualTo>") + "<ValueReference>c/FID</ValueReference>") + "<ValueReference>d/FID</ValueReference>") + "</PropertyIsEqualTo>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Forests/gs:NAME[text() = 'Foo Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Lakes/gs:NAME[text() = 'Black Lake']", dom);
    }

    @Test
    public void testStandardJoinMainTypeRenamed() throws Exception {
        Catalog catalog = getCatalog();
        FeatureTypeInfo forestsInfo = catalog.getFeatureTypeByName("gs:Forests");
        String oldName = forestsInfo.getName();
        forestsInfo.setName("ForestsRenamed");
        try {
            catalog.save(forestsInfo);
            String xml = ((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:ForestsRenamed gs:Lakes' aliases='c d'>") + "<fes:Filter> ") + "<PropertyIsEqualTo>") + "<ValueReference>c/FID</ValueReference>") + "<ValueReference>d/FID</ValueReference>") + "</PropertyIsEqualTo>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
            Document dom = postAsDOM("wfs", xml);
            // print(dom);
            XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:Tuple)", dom);
            XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:ForestsRenamed/gs:NAME[text() = 'Foo Forest']", dom);
            XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Lakes/gs:NAME[text() = 'Black Lake']", dom);
        } finally {
            forestsInfo.setName(oldName);
            catalog.save(forestsInfo);
        }
    }

    @Test
    public void testStandardJoinNoAliases() throws Exception {
        String xml = ((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes'>") + "<fes:Filter> ") + "<PropertyIsEqualTo>") + "<ValueReference>gs:Forests/FID</ValueReference>") + "<ValueReference>gs:Lakes/FID</ValueReference>") + "</PropertyIsEqualTo>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Forests/gs:NAME[text() = 'Foo Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Lakes/gs:NAME[text() = 'Black Lake']", dom);
    }

    @Test
    public void testSelfJoin() throws Exception {
        String xml = ((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Forests' aliases='a b'>") + "<fes:Filter> ") + "<Disjoint>") + "<ValueReference>a/the_geom</ValueReference>") + "<ValueReference>b/the_geom</ValueReference>") + "</Disjoint>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("6", "count(//wfs:Tuple)", dom);
    }

    @Test
    public void testSelfJoinLocalNamespaces() throws Exception {
        String xml = ((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:ns42='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='ns42:Forests ns42:Forests' aliases='a b'>") + "<fes:Filter> ") + "<Disjoint>") + "<ValueReference>a/ns42:the_geom</ValueReference>") + "<ValueReference>b/ns42:the_geom</ValueReference>") + "</Disjoint>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("6", "count(//wfs:Tuple)", dom);
    }

    @Test
    public void testTemporalJoin() throws Exception {
        String xml = ((((((((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:PrimitiveGeoFeature gs:TimeFeature' aliases='a b'>") + "<fes:Filter> ") + "<And>") + "<After>") + "<ValueReference>a/dateTimeProperty</ValueReference>") + "<ValueReference>b/dateTime</ValueReference>") + "</After>") + "<PropertyIsEqualTo>") + "<ValueReference>a/name</ValueReference>") + "<Literal>name-f008</Literal>") + "</PropertyIsEqualTo>") + "</And>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:Tuple)", dom);
    }

    @Test
    public void testStandardJoinLocalFilterNot() throws Exception {
        String xml = ((((((((((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='c d'>") + "<fes:Filter> ") + "<And>") + "<PropertyIsEqualTo>") + "<ValueReference>c/FID</ValueReference>") + "<ValueReference>d/FID</ValueReference>") + "</PropertyIsEqualTo>") + "<Not>") + "<PropertyIsEqualTo>") + "<ValueReference>d/NAME</ValueReference>") + "<Literal>foo</Literal>") + "</PropertyIsEqualTo>") + "</Not>") + "</And>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Forests/gs:NAME[text() = 'Foo Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Lakes/gs:NAME[text() = 'Black Lake']", dom);
    }

    @Test
    public void testStandardJoinLocalFilterOr() throws Exception {
        String xml = ((((((((((((((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='c d'>") + "<fes:Filter> ") + "<And>") + "<PropertyIsEqualTo>") + "<ValueReference>c/FID</ValueReference>") + "<ValueReference>d/FID</ValueReference>") + "</PropertyIsEqualTo>") + "<Or>") + "<PropertyIsEqualTo>") + "<ValueReference>d/NAME</ValueReference>") + "<Literal>foo</Literal>") + "</PropertyIsEqualTo>") + "<PropertyIsEqualTo>") + "<ValueReference>d/NAME</ValueReference>") + "<Literal>Black Lake</Literal>") + "</PropertyIsEqualTo>") + "</Or>") + "</And>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Forests/gs:NAME[text() = 'Foo Forest']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple/wfs:member/gs:Lakes/gs:NAME[text() = 'Black Lake']", dom);
    }

    @Test
    public void testOredJoinCondition() throws Exception {
        String xml = ((((((((((((((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='c d'>") + "<fes:Filter> ") + "<Or>") + "<PropertyIsEqualTo>") + "<ValueReference>c/FID</ValueReference>") + "<ValueReference>d/FID</ValueReference>") + "</PropertyIsEqualTo>") + "<And>") + "<PropertyIsEqualTo>") + "<ValueReference>c/NAME</ValueReference>") + "<Literal>Bar Forest</Literal>") + "</PropertyIsEqualTo>") + "<PropertyIsEqualTo>") + "<ValueReference>d/NAME</ValueReference>") + "<Literal>Green Lake</Literal>") + "</PropertyIsEqualTo>") + "</And>") + "</Or>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists(("//wfs:Tuple[wfs:member/gs:Forests/gs:NAME = 'Foo Forest' " + "and wfs:member/gs:Lakes/gs:NAME = 'Black Lake']"), dom);
        XMLAssert.assertXpathExists(("//wfs:Tuple[wfs:member/gs:Forests/gs:NAME = 'Bar Forest' " + "and wfs:member/gs:Lakes/gs:NAME = 'Green Lake']"), dom);
    }

    @Test
    public void testStandardJoinCSV() throws Exception {
        String xml = (((((((((((((((("<?xml version='1.0' encoding='UTF-8'?>" + "<wfs:GetFeature xmlns:wfs='") + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' outputFormat='csv' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='a b'>") + "<fes:Filter> ") + "<PropertyIsEqualTo>") + "<ValueReference>a/FID</ValueReference>") + "<ValueReference>b/FID</ValueReference>") + "</PropertyIsEqualTo>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/xml", "UTF-8");
        // check the mime type
        Assert.assertEquals("text/csv", resp.getContentType());
        // check the charset encoding
        Assert.assertEquals("UTF-8", resp.getCharacterEncoding());
        // check the content disposition
        Assert.assertEquals("attachment; filename=Forests.csv", resp.getHeader("Content-Disposition"));
        // read the response back with a parser that can handle escaping, newlines
        // and what not
        List<String[]> lines = readLines(resp.getContentAsString());
        FeatureSource fs1 = getFeatureSource(MockData.FORESTS);
        FeatureSource fs2 = getFeatureSource(MockData.LAKES);
        for (String[] line : lines) {
            // check each line has the expected number of elements (num of att1 +
            // num of att2+1 for the id)
            Assert.assertEquals((((fs1.getSchema().getDescriptors().size()) + (fs2.getSchema().getDescriptors().size())) + 1), line.length);
        }
    }

    @Test
    public void testJoinAliasConflictingPropertyCSV() throws Exception {
        String xml = (((((((((((((((("<?xml version='1.0' encoding='UTF-8'?>" + "<wfs:GetFeature xmlns:wfs='") + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' outputFormat='csv' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='a NAME'>") + "<fes:Filter> ") + "<PropertyIsEqualTo>") + "<ValueReference>a/FID</ValueReference>") + "<ValueReference>NAME/FID</ValueReference>") + "</PropertyIsEqualTo>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/xml", "UTF-8");
        // check the mime type
        Assert.assertEquals("text/csv", resp.getContentType());
        // check the charset encoding
        Assert.assertEquals("UTF-8", resp.getCharacterEncoding());
        // check the content disposition
        Assert.assertEquals("attachment; filename=Forests.csv", resp.getHeader("Content-Disposition"));
        // read the response back with a parser that can handle escaping, newlines
        // and what not
        List<String[]> lines = readLines(resp.getContentAsString());
        FeatureSource fs1 = getFeatureSource(MockData.FORESTS);
        FeatureSource fs2 = getFeatureSource(MockData.LAKES);
        for (String[] line : lines) {
            // check each line has the expected number of elements (num of att1 +
            // num of att2+1 for the id)
            Assert.assertEquals((((fs1.getSchema().getDescriptors().size()) + (fs2.getSchema().getDescriptors().size())) + 1), line.length);
        }
    }

    @Test
    public void testStandardJoinNoAliasesCSV() throws Exception {
        String xml = (((((((((((((((("<?xml version='1.0' encoding='UTF-8'?>" + "<wfs:GetFeature xmlns:wfs='") + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' outputFormat='csv' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes'>") + "<fes:Filter> ") + "<PropertyIsEqualTo>") + "<ValueReference>Forests/FID</ValueReference>") + "<ValueReference>Lakes/FID</ValueReference>") + "</PropertyIsEqualTo>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/xml", "UTF-8");
        // check the mime type
        Assert.assertEquals("text/csv", resp.getContentType());
        // check the charset encoding
        Assert.assertEquals("UTF-8", resp.getCharacterEncoding());
        // check the content disposition
        Assert.assertEquals("attachment; filename=Forests.csv", resp.getHeader("Content-Disposition"));
        // read the response back with a parser that can handle escaping, newlines
        // and what not
        List<String[]> lines = readLines(resp.getContentAsString());
        FeatureSource fs1 = getFeatureSource(MockData.FORESTS);
        FeatureSource fs2 = getFeatureSource(MockData.LAKES);
        for (String[] line : lines) {
            Assert.assertEquals((((fs1.getSchema().getDescriptors().size()) + (fs2.getSchema().getDescriptors().size())) + 1), line.length);
        }
    }

    @Test
    public void testSelfJoinCSV() throws Exception {
        String xml = (((((((((((((((("<?xml version='1.0' encoding='UTF-8'?>" + "<wfs:GetFeature xmlns:wfs='") + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' outputFormat='csv' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests Forests' aliases='a b'>") + "<fes:Filter> ") + "<Disjoint>") + "<ValueReference>a/the_geom</ValueReference>") + "<ValueReference>b/the_geom</ValueReference>") + "</Disjoint>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/xml", "UTF-8");
        // check the mime type
        Assert.assertEquals("text/csv", resp.getContentType());
        // check the charset encoding
        Assert.assertEquals("UTF-8", resp.getCharacterEncoding());
        // check the content disposition
        Assert.assertEquals("attachment; filename=Forests.csv", resp.getHeader("Content-Disposition"));
        // read the response back with a parser that can handle escaping, newlines
        // and what not
        List<String[]> lines = readLines(resp.getContentAsString());
        FeatureSource fs = getFeatureSource(MockData.FORESTS);
        for (String[] line : lines) {
            Assert.assertEquals(((2 * (fs.getSchema().getDescriptors().size())) + 1), line.length);
        }
    }

    @Test
    public void testSpatialJoinPOST_CSV() throws Exception {
        String xml = ((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' outputFormat='csv' version='2.0.0'>") + "<wfs:Query typeNames='gs:Forests gs:Lakes' aliases='a b'>") + "<fes:Filter> ") + "<fes:Intersects> ") + "<fes:ValueReference>a/the_geom</fes:ValueReference> ") + "<fes:ValueReference>b/the_geom</fes:ValueReference>") + "</fes:Intersects> ") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/xml", "UTF-8");
        // check the mime type
        Assert.assertEquals("text/csv", resp.getContentType());
        // check the charset encoding
        Assert.assertEquals("UTF-8", resp.getCharacterEncoding());
        // check the content disposition
        Assert.assertEquals("attachment; filename=Forests.csv", resp.getHeader("Content-Disposition"));
        // read the response back with a parser that can handle escaping, newlines
        // and what not
        List<String[]> lines = readLines(resp.getContentAsString());
        FeatureSource fs1 = getFeatureSource(MockData.FORESTS);
        FeatureSource fs2 = getFeatureSource(MockData.LAKES);
        for (String[] line : lines) {
            Assert.assertEquals((((fs1.getSchema().getDescriptors().size()) + (fs2.getSchema().getDescriptors().size())) + 1), line.length);
        }
    }

    @Test
    public void testStandardJoinThreeWaysLocalFiltersCSV() throws Exception {
        String xml = ((((((((((((((((((((((((((((((((("<wfs:GetFeature xmlns:wfs='" + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'") + " xmlns:gs='") + (SystemTestData.DEFAULT_URI)) + "' outputFormat='csv' version='2.0.0'>") + "<wfs:Query typeNames='gs:t1 gs:t2 gs:t3' aliases='a b c'>") + "<fes:Filter> ") + "<And>") + "<PropertyIsEqualTo>") + "<ValueReference>a/name1</ValueReference>") + "<Literal>First</Literal>") + "</PropertyIsEqualTo>") + "<PropertyIsEqualTo>") + "<ValueReference>a/code1</ValueReference>") + "<ValueReference>b/code2</ValueReference>") + "</PropertyIsEqualTo>") + "<PropertyIsEqualTo>") + "<ValueReference>b/name2</ValueReference>") + "<Literal>Second</Literal>") + "</PropertyIsEqualTo>") + "<PropertyIsEqualTo>") + "<ValueReference>c/name3</ValueReference>") + "<Literal>Third</Literal>") + "</PropertyIsEqualTo>") + "<PropertyIsEqualTo>") + "<ValueReference>b/code2</ValueReference>") + "<ValueReference>c/code3</ValueReference>") + "</PropertyIsEqualTo>") + "</And>") + "</fes:Filter> ") + "</wfs:Query>") + "</wfs:GetFeature>";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/xml", "UTF-8");
        // check the mime type
        Assert.assertEquals("text/csv", resp.getContentType());
        // check the charset encoding
        Assert.assertEquals("UTF-8", resp.getCharacterEncoding());
        // check the content disposition
        Assert.assertEquals("attachment; filename=t1.csv", resp.getHeader("Content-Disposition"));
        // read the response back with a parser that can handle escaping, newlines
        // and what not
        List<String[]> lines = readLines(resp.getContentAsString());
        FeatureSource fs1 = getFeatureSource(new QName("t1"));
        FeatureSource fs2 = getFeatureSource(new QName("t2"));
        FeatureSource fs3 = getFeatureSource(new QName("t3"));
        for (String[] line : lines) {
            Assert.assertEquals(((((fs1.getSchema().getDescriptors().size()) + (fs2.getSchema().getDescriptors().size())) + (fs3.getSchema().getDescriptors().size())) + 1), line.length);
        }
    }

    @Test
    public void testSelfJoinNoAliases() throws Exception {
        String xml = ((((((("<wfs:GetFeature xmlns:wfs=\"http://www.opengis.net/wfs/2.0\" count=\"10\" " + ("service=\"WFS\" startIndex=\"0\" version=\"2.0.0\"> <wfs:Query " + "xmlns:ns76=\"")) + (SystemTestData.DEFAULT_URI)) + "\" ") + "typeNames=\"ns76:PrimitiveGeoFeature ns76:PrimitiveGeoFeature\"> <Filter ") + "xmlns=\"http://www.opengis.net/fes/2.0\"> <PropertyIsEqualTo> ") + "<ValueReference>ns76:PrimitiveGeoFeature/ns76:booleanProperty</ValueReference> ") + "<ValueReference>ns76:PrimitiveGeoFeature/ns76:booleanProperty</ValueReference> ") + "</PropertyIsEqualTo> </Filter> </wfs:Query> </wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        // many tuples match:
        // f001 with itself, f003, f008
        // f003 with itself, f001, f008
        // f008 with itself, f001, f003
        // f002 with itself
        XMLAssert.assertXpathEvaluatesTo("10", "count(//wfs:Tuple)", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[wfs:member[1]//gml:description = 'description-f001' and wfs:member[2]//gml:description = 'description-f001']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[wfs:member[1]//gml:description = 'description-f001' and wfs:member[2]//gml:description = 'description-f003']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[wfs:member[1]//gml:description = 'description-f001' and wfs:member[2]//gml:description = 'description-f008']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[wfs:member[1]//gml:description = 'description-f003' and wfs:member[2]//gml:description = 'description-f001']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[wfs:member[1]//gml:description = 'description-f003' and wfs:member[2]//gml:description = 'description-f003']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[wfs:member[1]//gml:description = 'description-f003' and wfs:member[2]//gml:description = 'description-f008']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[wfs:member[1]//gml:description = 'description-f008' and wfs:member[2]//gml:description = 'description-f001']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[wfs:member[1]//gml:description = 'description-f008' and wfs:member[2]//gml:description = 'description-f003']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[wfs:member[1]//gml:description = 'description-f008' and wfs:member[2]//gml:description = 'description-f008']", dom);
        XMLAssert.assertXpathExists("//wfs:Tuple[wfs:member[1]//gml:description = 'description-f002' and wfs:member[2]//gml:description = 'description-f002']", dom);
    }
}

