/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.opensearch.eo.kvp;


import Filter.INCLUDE;
import OSEOInfo.DEFAULT_RECORDS_PER_PAGE;
import OpenSearchAccess.EO_NAMESPACE;
import OpenSearchParameters.GEO_BOX;
import OpenSearchParameters.GEO_UID;
import OpenSearchParameters.SEARCH_TERMS;
import OpenSearchParameters.START_INDEX;
import ProductClass.GENERIC;
import ProductClass.OPTICAL;
import SEARCH_TERMS.key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.geoserver.opensearch.eo.JDBCOpenSearchAccessTest;
import org.geoserver.opensearch.eo.OSEOTestSupport;
import org.geoserver.opensearch.eo.SearchRequest;
import org.geoserver.platform.OWS20Exception;
import org.geotools.data.Parameter;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.IsEqualsToImpl;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.Converters;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Intersects;


public class SearchRequestKvpReaderTest extends OSEOTestSupport {
    private SearchRequestKvpReader reader;

    private static FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2();

    @Test
    public void testGetAll() throws Exception {
        SearchRequest request = parseSearchRequest(Collections.emptyMap());
        Assert.assertEquals(null, request.getParentId());
        final Query query = request.getQuery();
        Assert.assertNotNull(query);
        Assert.assertEquals(INCLUDE, query.getFilter());
        Assert.assertNull(query.getStartIndex());
        Assert.assertEquals(DEFAULT_RECORDS_PER_PAGE, query.getMaxFeatures());
    }

    @Test
    public void testParseSearchTerms() throws Exception {
        final String searchTermsValue = "a b \"c and d\"";
        Map<String, String> map = toMap(key, searchTermsValue);
        SearchRequest request = parseSearchRequest(map);
        Assert.assertEquals(null, request.getParentId());
        final Query query = request.getQuery();
        Assert.assertNotNull(query);
        final String expectedCql = "htmlDescription ILIKE '%a%' OR htmlDescription ILIKE '%b%' OR htmlDescription ILIKE '%c and d%'";
        Assert.assertEquals(expectedCql, ECQL.toCQL(query.getFilter()));
        Map<Parameter, String> searchParameters = request.getSearchParameters();
        Assert.assertEquals(1, searchParameters.size());
        Assert.assertThat(searchParameters, Matchers.hasEntry(SEARCH_TERMS, searchTermsValue));
    }

    /**
     * From spec the parameter keys are case sensitive, and unknown ones should be ignored
     */
    @Test
    public void testParseSearchTermsWrongCase() throws Exception {
        Map<String, String> map = toMap(key.toUpperCase(), "a b \"c and d\"");
        SearchRequest request = parseSearchRequest(map);
        Assert.assertEquals(null, request.getParentId());
        final Query query = request.getQuery();
        Assert.assertEquals(INCLUDE, query.getFilter());
    }

    @Test
    public void testParseGeoUid() throws Exception {
        Map<String, String> map = toMap(GEO_UID.key, "abcd");
        SearchRequest request = parseSearchRequest(map);
        Assert.assertEquals(null, request.getParentId());
        final Query query = request.getQuery();
        Assert.assertNotNull(query);
        final String expectedCql = "identifier = 'abcd'";
        Assert.assertEquals(expectedCql, ECQL.toCQL(query.getFilter()));
        Map<Parameter, String> searchParameters = request.getSearchParameters();
        Assert.assertEquals(1, searchParameters.size());
        Assert.assertThat(searchParameters, Matchers.hasEntry(GEO_UID, "abcd"));
    }

    @Test
    public void testParseTimeBox() throws Exception {
        Map<String, String> map = toMap(GEO_BOX.key, "10,20,30,40");
        SearchRequest request = parseSearchRequest(map);
        Assert.assertEquals(null, request.getParentId());
        final Query query = request.getQuery();
        Assert.assertNotNull(query);
        final String expectedCql = "BBOX(, 10.0,20.0,30.0,40.0)";
        Assert.assertEquals(expectedCql, ECQL.toCQL(query.getFilter()));
        Map<Parameter, String> searchParameters = request.getSearchParameters();
        Assert.assertEquals(1, searchParameters.size());
        Assert.assertThat(searchParameters, Matchers.hasEntry(GEO_BOX, "10,20,30,40"));
    }

    @Test
    public void testParseBBoxWholeWorld() throws Exception {
        Map<String, String> map = toMap(GEO_BOX.key, "-180,-90,180,90");
        SearchRequest request = parseSearchRequest(map);
        Assert.assertEquals(null, request.getParentId());
        final Query query = request.getQuery();
        Assert.assertNotNull(query);
        final String expectedCql = "BBOX(, -180.0,-90.0,180.0,90.0)";
        Assert.assertEquals(expectedCql, ECQL.toCQL(query.getFilter()));
    }

    @Test
    public void testParseBBoxDatelineCrossing() throws Exception {
        Map<String, String> map = toMap(GEO_BOX.key, "170,-90,-170,90");
        SearchRequest request = parseSearchRequest(map);
        Assert.assertEquals(null, request.getParentId());
        final Query query = request.getQuery();
        Assert.assertNotNull(query);
        final String expectedCql = "BBOX(, 170.0,-90.0,180.0,90.0) OR BBOX(, -180.0,-90.0,-170.0,90.0)";
        Assert.assertEquals(expectedCql, ECQL.toCQL(query.getFilter()));
    }

    @Test
    public void testPaging() throws Exception {
        Map<String, String> map = toMap(START_INDEX.key, "10", SearchRequestKvpReader.COUNT_KEY, "5");
        SearchRequest request = parseSearchRequest(map);
        Assert.assertEquals(null, request.getParentId());
        final Query query = request.getQuery();
        Assert.assertNotNull(query);
        Assert.assertEquals(INCLUDE, query.getFilter());
        Assert.assertEquals(9, ((int) (query.getStartIndex())));// from 1 based to 0 based

        Assert.assertEquals(5, query.getMaxFeatures());
        Map<Parameter, String> searchParameters = request.getSearchParameters();
        Assert.assertEquals(2, searchParameters.size());
        Assert.assertThat(searchParameters, Matchers.hasEntry(START_INDEX, "10"));
        // does not work but yet to figure out why
        // assertThat(searchParameters, hasEntry(hasProperty("name", equalTo("count")), "5"));
    }

    @Test
    public void testStartIndexNegative() throws Exception {
        try {
            parseSearchRequest(toMap(START_INDEX.key, "-10"));
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
        }
    }

    @Test
    public void testStartIndexNotNumber() throws Exception {
        try {
            parseSearchRequest(toMap(START_INDEX.key, "abc"));
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
        }
    }

    @Test
    public void testStartIndexFloat() throws Exception {
        try {
            parseSearchRequest(toMap(START_INDEX.key, "1.23"));
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
        }
    }

    @Test
    public void testCountIndexNegative() throws Exception {
        try {
            parseSearchRequest(toMap(SearchRequestKvpReader.COUNT_KEY, "-10"));
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
        }
    }

    @Test
    public void testCountIndexNotNumber() throws Exception {
        try {
            parseSearchRequest(toMap(SearchRequestKvpReader.COUNT_KEY, "abc"));
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
        }
    }

    @Test
    public void testCountTooBig() throws Exception {
        try {
            parseSearchRequest(toMap(SearchRequestKvpReader.COUNT_KEY, "1000"));
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
        }
    }

    @Test
    public void testCountIndexFloat() throws Exception {
        try {
            parseSearchRequest(toMap(SearchRequestKvpReader.COUNT_KEY, "1.23"));
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
        }
    }

    @Test
    public void testParentId() throws Exception {
        Map<String, String> map = toMap(SearchRequestKvpReader.PARENT_ID_KEY, "SENTINEL2");
        SearchRequest request = parseSearchRequest(map);
        Assert.assertEquals("SENTINEL2", request.getParentId());
        final Query query = request.getQuery();
        Assert.assertNotNull(query);
        // no filter here, the parent id needs to be translate to an internal identifier
        Assert.assertEquals(INCLUDE, query.getFilter());
    }

    @Test
    public void testDistanceFromPoint() throws Exception {
        Map<String, String> map = toMap(GEO_LON.key, "12", GEO_LAT.key, "45", GEO_RADIUS.key, "20000");
        SearchRequest request = parseSearchRequest(map);
        final Query query = request.getQuery();
        Assert.assertNotNull(query);
        final String expectedCql = "DWITHIN(\"\", POINT (12 45), 20000.0, m)";
        Assert.assertEquals(expectedCql, ECQL.toCQL(query.getFilter()));
    }

    @Test
    public void testNegativeDistanceFromPoint() throws Exception {
        try {
            Map<String, String> map = toMap(GEO_LON.key, "12", GEO_LAT.key, "45", GEO_RADIUS.key, "-10");
            parseSearchRequest(map);
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
        }
    }

    @Test
    public void testTimeRelationInvalid() throws Exception {
        try {
            Map<String, String> map = toMap(TIME_RELATION.key, "abcd");
            parseSearchRequest(map);
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
            Assert.assertEquals("timeRelation", e.getLocator());
        }
    }

    @Test
    public void testTimeRelationAlone() throws Exception {
        try {
            Map<String, String> map = toMap(TIME_RELATION.key, "intersects");
            parseSearchRequest(map);
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
            Assert.assertEquals("timeRelation", e.getLocator());
        }
    }

    @Test
    public void testTimeStartInvalid() throws Exception {
        try {
            Map<String, String> map = toMap(TIME_START.key, "abcd");
            parseSearchRequest(map);
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
            Assert.assertEquals(TIME_START.key, e.getLocator());
        }
    }

    @Test
    public void testTimeEndInvalid() throws Exception {
        try {
            Map<String, String> map = toMap(TIME_END.key, "abcd");
            parseSearchRequest(map);
            Assert.fail("Should have failed");
        } catch (OWS20Exception e) {
            Assert.assertEquals("InvalidParameterValue", e.getCode());
            Assert.assertEquals(TIME_END.key, e.getLocator());
        }
    }

    @Test
    public void testTimeFilterStartOnly() throws Exception {
        Map<String, String> map;
        // intersection behavior, the features must overlap the provided range
        map = toMap(TIME_START.key, "2010-09-01T00:00:00Z");
        Assert.assertEquals(ECQL.toFilter("timeEnd >= 2010-09-01T00:00:00Z OR timeEnd IS NULL"), parseAndGetFilter(map));
        // intersection behavior again, explicit
        map = toMap(TIME_START.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "intersects");
        Assert.assertEquals(ECQL.toFilter("timeEnd >= 2010-09-01T00:00:00Z OR timeEnd IS NULL"), parseAndGetFilter(map));
        // contains (the feature must contain the requested interval)
        map = toMap(TIME_START.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "contains");
        Assert.assertEquals(ECQL.toFilter("timeStart <= 2010-09-01T00:00:00Z and timeEnd IS NULL"), parseAndGetFilter(map));
        // during (the features are inside the requested interval)
        map = toMap(TIME_START.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "during");
        Assert.assertEquals(ECQL.toFilter("timeStart >= 2010-09-01T00:00:00Z"), parseAndGetFilter(map));
        // disjoint (the features are outside the requested interval)
        map = toMap(TIME_START.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "disjoint");
        Assert.assertEquals(ECQL.toFilter("timeEnd < 2010-09-01T00:00:00Z"), parseAndGetFilter(map));
        // equal (the features have the same interval of validity
        map = toMap(TIME_START.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "equals");
        Assert.assertEquals(ECQL.toFilter("timeStart = 2010-09-01T00:00:00Z and timeEnd IS NULL"), parseAndGetFilter(map));
    }

    @Test
    public void testTimeFilterEndOnly() throws Exception {
        Map<String, String> map;
        // intersection behavior, the features must overlap the provided range
        map = toMap(TIME_END.key, "2010-09-01T00:00:00Z");
        Assert.assertEquals(ECQL.toFilter("timeStart <= 2010-09-01T00:00:00Z OR timeStart IS NULL"), parseAndGetFilter(map));
        // intersection behavior again, explicit
        map = toMap(TIME_END.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "intersects");
        Assert.assertEquals(ECQL.toFilter("timeStart <= 2010-09-01T00:00:00Z OR timeStart IS NULL"), parseAndGetFilter(map));
        // contains (the feature must contain the requested interval)
        map = toMap(TIME_END.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "contains");
        Assert.assertEquals(ECQL.toFilter("timeEnd >= 2010-09-01T00:00:00Z and timeStart IS NULL"), parseAndGetFilter(map));
        // during (the features are inside the requested interval)
        map = toMap(TIME_END.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "during");
        Assert.assertEquals(ECQL.toFilter("timeEnd <= 2010-09-01T00:00:00Z"), parseAndGetFilter(map));
        // disjoint (the features are outside the requested interval)
        map = toMap(TIME_END.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "disjoint");
        Assert.assertEquals(ECQL.toFilter("timeStart > 2010-09-01T00:00:00Z"), parseAndGetFilter(map));
        // equal (the features have the same interval of validity
        map = toMap(TIME_END.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "equals");
        Assert.assertEquals(ECQL.toFilter("timeEnd = 2010-09-01T00:00:00Z and timeStart IS NULL"), parseAndGetFilter(map));
    }

    @Test
    public void testTimeFilterStartEndOnly() throws Exception {
        Map<String, String> map;
        // intersection behavior, the features must overlap the provided range
        map = toMap(TIME_START.key, "2010-08-01T00:00:00Z", TIME_END.key, "2010-09-01T00:00:00Z");
        Assert.assertEquals(ECQL.toFilter("(timeStart <= 2010-09-01T00:00:00Z or timeStart IS NULL) AND (timeEnd >= 2010-08-01T00:00:00Z or timeEnd IS NULL)"), parseAndGetFilter(map));
        // intersection behavior again, explicit
        map = toMap(TIME_START.key, "2010-08-01T00:00:00Z", TIME_END.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "intersects");
        Assert.assertEquals(ECQL.toFilter("(timeStart <= 2010-09-01T00:00:00Z or timeStart IS NULL) AND (timeEnd >= 2010-08-01T00:00:00Z or timeEnd IS NULL)"), parseAndGetFilter(map));
        // contains (the feature must contain the requested interval)
        map = toMap(TIME_START.key, "2010-08-01T00:00:00Z", TIME_END.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "contains");
        Assert.assertEquals(ECQL.toFilter("timeStart <= 2010-08-01T00:00:00Z and timeEnd >= 2010-09-01T00:00:00Z"), parseAndGetFilter(map));
        // during (the features are inside the requested interval)
        map = toMap(TIME_START.key, "2010-08-01T00:00:00Z", TIME_END.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "during");
        Assert.assertEquals(ECQL.toFilter("timeStart >= 2010-08-01T00:00:00Z and timeEnd <= 2010-09-01T00:00:00Z"), parseAndGetFilter(map));
        // disjoint (the features are outside the requested interval)
        map = toMap(TIME_START.key, "2010-08-01T00:00:00Z", TIME_END.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "disjoint");
        Assert.assertEquals(ECQL.toFilter("timeStart > 2010-09-01T00:00:00Z or timeEnd < 2010-08-01T00:00:00Z"), parseAndGetFilter(map));
        // equal (the features have the same interval of validity
        map = toMap(TIME_START.key, "2010-08-01T00:00:00Z", TIME_END.key, "2010-09-01T00:00:00Z", TIME_RELATION.key, "equals");
        Assert.assertEquals(ECQL.toFilter("timeStart = 2010-08-01T00:00:00Z and timeEnd = 2010-09-01T00:00:00Z"), parseAndGetFilter(map));
    }

    @Test
    public void testTimeStartOnlyDate() throws Exception {
        Map<String, String> map;
        // intersection behavior, the features must overlap the provided range
        map = toMap(TIME_START.key, "2010-09-01");
        Assert.assertEquals(ECQL.toFilter("timeEnd >= 2010-09-01T00:00:00Z OR timeEnd IS NULL"), parseAndGetFilter(map));
    }

    @Test
    public void testCollectionSensorTypeSingle() throws Exception {
        Map<String, String> map = toMap("sensorType", "OPTICAL");
        Filter filter = parseAndGetFilter(map);
        Assert.assertThat(filter, Matchers.instanceOf(PropertyIsEqualTo.class));
        assertBinaryFilter(filter, EO_NAMESPACE, "sensorType", "OPTICAL");
    }

    @Test
    public void testCollectionSensorTypeCustom() throws Exception {
        Map<String, String> map = toMap("sensorType", JDBCOpenSearchAccessTest.GS_PRODUCT.getName());
        Filter filter = parseAndGetFilter(map);
        Assert.assertThat(filter, Matchers.instanceOf(PropertyIsEqualTo.class));
        assertBinaryFilter(filter, EO_NAMESPACE, "sensorType", JDBCOpenSearchAccessTest.GS_PRODUCT.getName());
    }

    @Test
    public void testCollectionSensorTypeList() throws Exception {
        Map<String, String> map = toMap("sensorType", "OPTICAL,RADAR,ALTIMETRIC");
        Filter filter = parseAndGetFilter(map);
        Assert.assertThat(filter, Matchers.instanceOf(Or.class));
        Or or = ((Or) (filter));
        final List<Filter> children = or.getChildren();
        Assert.assertEquals(3, children.size());
        assertBinaryFilter(children.get(0), EO_NAMESPACE, "sensorType", "OPTICAL");
        assertBinaryFilter(children.get(1), EO_NAMESPACE, "sensorType", "RADAR");
        assertBinaryFilter(children.get(2), EO_NAMESPACE, "sensorType", "ALTIMETRIC");
    }

    @Test
    public void testCloudCoverEmpty() throws Exception {
        Map<String, String> map = toMap("parentId", "SENTINEL2", "cloudCover", "");
        Filter filter = parseAndGetFilter(map);
        Assert.assertThat(filter, CoreMatchers.equalTo(INCLUDE));
    }

    @Test
    public void testCloudCoverGreater() throws Exception {
        Map<String, String> map = toMap("parentId", "SENTINEL2", "cloudCover", "[30");
        Filter filter = parseAndGetFilter(map);
        Assert.assertThat(filter, Matchers.instanceOf(PropertyIsGreaterThanOrEqualTo.class));
        assertBinaryFilter(filter, OPTICAL.getNamespace(), "cloudCover", 30);
    }

    @Test
    public void testCloudCoverSmaller() throws Exception {
        Map<String, String> map = toMap("parentId", "SENTINEL2", "cloudCover", "20]");
        Filter filter = parseAndGetFilter(map);
        Assert.assertThat(filter, Matchers.instanceOf(PropertyIsLessThanOrEqualTo.class));
        assertBinaryFilter(filter, OPTICAL.getNamespace(), "cloudCover", 20);
    }

    @Test
    public void testCloudCoverClosedRange() throws Exception {
        Map<String, String> map = toMap("parentId", "SENTINEL2", "cloudCover", "[20,40]");
        Filter filter = parseAndGetFilter(map);
        Assert.assertThat(filter, Matchers.instanceOf(And.class));
        And and = ((And) (filter));
        final List<Filter> children = and.getChildren();
        Assert.assertEquals(2, children.size());
        BinaryComparisonOperator op1 = ((BinaryComparisonOperator) (children.get(0)));
        Assert.assertThat(op1, Matchers.instanceOf(PropertyIsGreaterThanOrEqualTo.class));
        assertBinaryFilter(op1, OPTICAL.getNamespace(), "cloudCover", 20);
        BinaryComparisonOperator op2 = ((BinaryComparisonOperator) (children.get(1)));
        Assert.assertThat(op2, Matchers.instanceOf(PropertyIsLessThanOrEqualTo.class));
        assertBinaryFilter(op2, OPTICAL.getNamespace(), "cloudCover", 40);
    }

    @Test
    public void testCloudCoverOpenRange() throws Exception {
        Map<String, String> map = toMap("parentId", "SENTINEL2", "cloudCover", "]20,40[");
        Filter filter = parseAndGetFilter(map);
        Assert.assertThat(filter, Matchers.instanceOf(And.class));
        And and = ((And) (filter));
        final List<Filter> children = and.getChildren();
        Assert.assertEquals(2, children.size());
        BinaryComparisonOperator op1 = ((BinaryComparisonOperator) (children.get(0)));
        Assert.assertThat(op1, Matchers.instanceOf(PropertyIsGreaterThan.class));
        assertBinaryFilter(op1, OPTICAL.getNamespace(), "cloudCover", 20);
        BinaryComparisonOperator op2 = ((BinaryComparisonOperator) (children.get(1)));
        Assert.assertThat(op2, Matchers.instanceOf(PropertyIsLessThan.class));
        assertBinaryFilter(op2, OPTICAL.getNamespace(), "cloudCover", 40);
    }

    @Test
    public void testGeometryFilter() throws Exception {
        String wkt = "POINT(0 0)";
        Geometry point = new WKTReader().read(wkt);
        // implicit relation
        Filter filter = parseAndGetFilter(toMap("geometry", wkt));
        Assert.assertThat(filter, Matchers.instanceOf(Intersects.class));
        assertBinarySpatialFilter(filter, "", point);
        // explicit intersects
        filter = parseAndGetFilter(toMap("geometry", wkt, "geoRelation", "intersects"));
        Assert.assertThat(filter, Matchers.instanceOf(Intersects.class));
        assertBinarySpatialFilter(filter, "", point);
        // explicit contains
        filter = parseAndGetFilter(toMap("geometry", wkt, "geoRelation", "contains"));
        Assert.assertThat(filter, Matchers.instanceOf(Contains.class));
        // ... expressions are inverted here, the attribute is contained in the search area
        Contains bso = ((Contains) (filter));
        Assert.assertThat(bso.getExpression2(), Matchers.instanceOf(PropertyName.class));
        PropertyName pn = ((PropertyName) (bso.getExpression2()));
        Assert.assertEquals("", pn.getPropertyName());
        Assert.assertThat(bso.getExpression1(), Matchers.instanceOf(Literal.class));
        Assert.assertEquals(point, bso.getExpression1().evaluate(null));
        // explict disjoint
        filter = parseAndGetFilter(toMap("geometry", wkt, "geoRelation", "disjoint"));
        Assert.assertThat(filter, Matchers.instanceOf(Disjoint.class));
        assertBinarySpatialFilter(filter, "", point);
    }

    @Test
    public void testEopCreationDate() throws Exception {
        Map<String, String> map = toMap("parentId", "SENTINEL2", "creationDate", "]2016-01-01");
        Filter filter = parseAndGetFilter(map);
        BinaryComparisonOperator op = ((BinaryComparisonOperator) (filter));
        Assert.assertThat(op, Matchers.instanceOf(PropertyIsGreaterThan.class));
        assertBinaryFilter(op, GENERIC.getNamespace(), "creationDate", Converters.convert("2016-01-01", Date.class));
    }

    @Test
    public void testCustomProperty() throws Exception {
        Map<String, String> map = toMap("parentId", "gsTestCollection", "test", "abcde");
        Filter filter = parseAndGetFilter(map);
        Assert.assertThat(filter, Matchers.instanceOf(IsEqualsToImpl.class));
        assertBinaryFilter(filter, JDBCOpenSearchAccessTest.GS_PRODUCT.getNamespace(), "test", "abcde");
    }
}

