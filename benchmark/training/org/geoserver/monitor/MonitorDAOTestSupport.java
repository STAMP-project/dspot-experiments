/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.monitor;


import Comparison.EQ;
import Comparison.IN;
import Comparison.LT;
import Comparison.NEQ;
import SortOrder.ASC;
import Status.RUNNING;
import Status.WAITING;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;


public abstract class MonitorDAOTestSupport {
    protected static MonitorTestData testData;

    protected static MonitorDAO dao;

    @Test
    public void testUpdate() throws Exception {
        RequestData data = MonitorDAOTestSupport.dao.getRequest(1);
        data.setPath("/one_updated");
        MonitorDAOTestSupport.dao.update(data);
        data = MonitorDAOTestSupport.dao.getRequest(1);
        Assert.assertEquals("/one_updated", data.getPath());
        data.getResources().add("one_layer");
        MonitorDAOTestSupport.dao.update(data);
        data = MonitorDAOTestSupport.dao.getRequest(1);
        Assert.assertEquals(1, data.getResources().size());
        Assert.assertEquals("one_layer", data.getResources().get(0));
    }

    @Test
    public void testGetRequests() throws Exception {
        List<RequestData> requests = MonitorDAOTestSupport.dao.getRequests();
        Assert.assertEquals(MonitorDAOTestSupport.testData.getData().size(), requests.size());
        // assertCovered(requests, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        MonitorTestData.assertCovered(requests, range(1, 20));
    }

    @Test
    public void testGetRequestsVisitor() throws Exception {
        final List<RequestData> datas = new ArrayList();
        MonitorDAOTestSupport.dao.getRequests(new Query().filter("path", "/seven", EQ), new RequestDataVisitor() {
            public void visit(RequestData data, Object... aggregates) {
                datas.add(data);
            }
        });
        MonitorTestData.assertCoveredInOrder(datas, 7);
    }

    @Test
    public void testGetRequestById() throws Exception {
        Assert.assertTrue(((MonitorDAOTestSupport.dao.getRequest(8)) != null));
        Assert.assertEquals("/eight", MonitorDAOTestSupport.dao.getRequest(8).getPath());
    }

    @Test
    public void testGetRequestsSorted() throws Exception {
        MonitorTestData.assertCoveredInOrder(MonitorDAOTestSupport.dao.getRequests(new Query().filter("id", 11L, LT).sort("path", ASC)), 8, 5, 4, 9, 1, 7, 6, 10, 3, 2);
    }

    @Test
    public void testGetRequestsBetween() throws Exception {
        List<RequestData> datas = MonitorDAOTestSupport.dao.getRequests(new Query().between(MonitorTestData.toDate("2010-07-23T15:55:00"), MonitorTestData.toDate("2010-07-23T16:17:00")));
        MonitorTestData.assertCoveredInOrder(datas, 6, 5, 4);
    }

    @Test
    public void testGetRequestsBetween2() throws Exception {
        // test that the query is inclusive, and test sorting
        List<RequestData> datas = MonitorDAOTestSupport.dao.getRequests(new Query().between(MonitorTestData.toDate("2010-07-23T15:56:44"), MonitorTestData.toDate("2010-07-23T16:16:44")).sort("startTime", ASC));
        MonitorTestData.assertCoveredInOrder(datas, 4, 5, 6);
    }

    @Test
    public void testGetRequestsPaged() throws Exception {
        List<RequestData> datas = MonitorDAOTestSupport.dao.getRequests(new Query().page(5L, 2L).sort("startTime", ASC));
        MonitorTestData.assertCoveredInOrder(datas, 6, 7);
    }

    @Test
    public void testGetRequestsFilter() throws Exception {
        MonitorTestData.assertCoveredInOrder(MonitorDAOTestSupport.dao.getRequests(new Query().filter("path", "/seven", EQ)), 7);
    }

    @Test
    public void testGetRequestsFilterNull() throws Exception {
        Assert.assertEquals(0, MonitorDAOTestSupport.dao.getRequests(new Query().filter("path", null, EQ)).size());
        Assert.assertEquals(MonitorDAOTestSupport.testData.getData().size(), MonitorDAOTestSupport.dao.getRequests(new Query().filter("path", null, NEQ)).size());
    }

    @Test
    public void testGetRequestsFilterIN() throws Exception {
        List<RequestData> datas = MonitorDAOTestSupport.dao.getRequests(new Query().filter("path", Arrays.asList("/two", "/seven"), IN));
        MonitorTestData.assertCovered(datas, 2, 7);
    }

    @Test
    public void testGetRequestsFilterIN2() throws Exception {
        List<RequestData> datas = MonitorDAOTestSupport.dao.getRequests(new Query().filter("status", Arrays.asList(RUNNING, WAITING), IN));
        MonitorTestData.assertCovered(datas, 1, 2, 5, 6, 10, 11, 12, 15, 16, 20);
    }

    @Test
    public void testGetCount() throws Exception {
        Assert.assertEquals(4, MonitorDAOTestSupport.dao.getCount(new Query().filter("path", "/foo", EQ)));
    }

    @Test
    public void testGetIterator() throws Exception {
        Iterator<RequestData> it = MonitorDAOTestSupport.dao.getIterator(new Query().filter("path", Arrays.asList("/two", "/seven"), IN));
        Assert.assertTrue(it.hasNext());
        RequestData data = it.next();
        Assert.assertEquals("/two", data.getPath());
        Assert.assertTrue(it.hasNext());
        data = it.next();
        Assert.assertEquals("/seven", data.getPath());
        assertFalse(it.hasNext());
    }
}

