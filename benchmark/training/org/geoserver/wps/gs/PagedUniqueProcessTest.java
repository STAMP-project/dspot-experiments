/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.gs;


import com.google.common.collect.Ordering;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.geoserver.wps.WPSTestSupport;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.visitor.UniqueVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.opengis.feature.simple.SimpleFeatureType;


public class PagedUniqueProcessTest extends WPSTestSupport {
    private static final String FIELD_NAME = "state_name";

    private static final int TOTAL_DISTINCT = 4;

    public PagedUniqueProcessTest() {
        super();
    }

    @Test
    public void testAll() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, null, null, null);
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals(size, values.size());
    }

    @Test
    public void testASCPagination1() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, 0, 1, "ASC");
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("Delaware", values.get(0));
    }

    @Test
    public void testASCPagination2() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, 1, 1, "ASC");
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals("District of Columbia", values.get(0));
    }

    @Test
    public void testASCPagination3() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, 2, 1, "ASC");
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals("Illinois", values.get(0));
    }

    @Test
    public void testDESCPagination1() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, 0, 1, "DESC");
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals("West Virginia", values.get(0));
    }

    @Test
    public void testDESCPagination2() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, 1, 1, "DESC");
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals("Illinois", values.get(0));
    }

    @Test
    public void testDESCPagination3() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, 2, 1, "DESC");
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals("District of Columbia", values.get(0));
    }

    @Test
    public void testLimits() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, 2, 2, null);
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals(2, values.size());
    }

    @Test
    public void testUniqueVisitorAlwaysDeclaresLimits() throws Exception {
        PagedUniqueProcess process = new PagedUniqueProcess();
        SimpleFeatureCollection features = Mockito.mock(SimpleFeatureCollection.class);
        SimpleFeatureType featureType = ((SimpleFeatureType) (WPSTestSupport.catalog.getFeatureTypeByName("states").getFeatureType()));
        Mockito.when(features.getSchema()).thenReturn(featureType);
        final AtomicInteger counter = new AtomicInteger();
        // mock optimized store behaviour to always
        // use hasLimits
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                UniqueVisitor visitor = ((UniqueVisitor) (invocation.getArguments()[0]));
                if (visitor.hasLimits()) {
                    counter.incrementAndGet();
                }
                visitor.setValue(Arrays.asList("a", "b", "c", "d"));
                return null;
            }
        }).when(features).accepts(Mockito.any(UniqueVisitor.class), Mockito.any());
        process.execute(features, PagedUniqueProcessTest.FIELD_NAME, 0, 2);
        // checks that hasLimits is always true
        // both for size calculation query and for page extraction query
        Assert.assertEquals(2, counter.intValue());
    }

    /* MaxFeature overflow is not an error: return all result from startIndex to end */
    @Test
    public void testMaxFeaturesOverflow() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, 2, 20, null);
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals(((PagedUniqueProcessTest.TOTAL_DISTINCT) - 2), values.size());
    }

    @Test
    public void testAllParameters() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, "*a*", 1, 2, "DESC");
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(3, size);
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(true, Ordering.natural().reverse().isOrdered(values));
        for (int count = 0; count < (values.size()); count++) {
            Assert.assertEquals(true, ((String) (values.get(count))).matches(".*(?i:a)?.*"));
        }
    }

    @Test
    public void testFilteredStarts() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, "d*", null, null, null);
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(size, values.size());
        for (int count = 0; count < (values.size()); count++) {
            Assert.assertEquals(true, ((String) (values.get(count))).matches("^(?i:d).*"));
        }
    }

    @Test
    public void testFilteredContains() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, "*A*", null, null, null);
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(size, values.size());
        for (int count = 0; count < (values.size()); count++) {
            Assert.assertEquals(true, ((String) (values.get(count))).matches(".*(?i:a)?.*"));
        }
    }

    @Test
    public void testFilteredEnds() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, "*A", null, null, null);
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(size, values.size());
        for (int count = 0; count < (values.size()); count++) {
            Assert.assertEquals(true, ((String) (values.get(count))).matches(".*(?i:a)$"));
        }
    }

    /* StartIndex overflow is an error: return no result */
    @Test
    public void testStartIndexOverflow() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, 6, 4, null);
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals(0, values.size());
    }

    @Test
    public void testAscOrder() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, null, null, "ASC");
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals(true, Ordering.natural().isOrdered(values));
    }

    @Test
    public void testDescOrder() throws Exception {
        String xml = buildInputXml(PagedUniqueProcessTest.FIELD_NAME, null, null, null, "DESC");
        String jsonString = string(post(root(), xml));
        JSONObject json = ((JSONObject) (JSONSerializer.toJSON(jsonString)));
        JSONArray values = json.getJSONArray("values");
        int size = json.getInt("size");
        Assert.assertEquals(PagedUniqueProcessTest.TOTAL_DISTINCT, size);
        Assert.assertEquals(true, Ordering.natural().reverse().isOrdered(values));
    }
}

