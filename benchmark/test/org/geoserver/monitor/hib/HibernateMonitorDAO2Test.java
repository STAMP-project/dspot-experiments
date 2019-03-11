/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.monitor.hib;


import Comparison.EQ;
import Comparison.IN;
import Comparison.NEQ;
import SortOrder.ASC;
import SortOrder.DESC;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geoserver.monitor.Filter;
import org.geoserver.monitor.MonitorDAOTestSupport;
import org.geoserver.monitor.MonitorTestData;
import org.geoserver.monitor.Query;
import org.geoserver.monitor.Query.Comparison;
import org.geoserver.monitor.RequestData;
import org.geoserver.monitor.RequestDataVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.context.support.XmlWebApplicationContext;


// @Test
// public void testFoo() throws Exception {
// SessionFactory sessionFactory = ((HibernateMonitorDAO2)dao).getSessionFactory();
// Session session = sessionFactory.getCurrentSession();
// 
// /*Query q = session.createQuery("SELECT rd.path FROM RequestData rd, LayerData ld " +
// "WHERE ld in elements(rd.layers) " +
// "AND ld.name = 'things'");*/
// /*Query q = session.createQuery("SELECT rd.path FROM RequestData rd " +
// "INNER JOIN rd.layers as layer WITH layer.name = 'things'");*/
// Query q = session.createQuery(
// "SELECT r.path, layer FROM RequestData r LEFT JOIN r.layers as layer " +
// " WHERE r.path = '/foo' GROUP BY r.path, layer");
// 
// for (Object o : q.list()) {
// Object[] vals = (Object[]) o;
// System.out.println(String.format("%s, %s", vals[0].toString(),
// vals[1].toString()));
// }
// 
// }
public class HibernateMonitorDAO2Test extends MonitorDAOTestSupport {
    private static XmlWebApplicationContext ctx;

    @Test
    public void testGetRequestsFilterIN3() throws Exception {
        List<RequestData> datas = dao.getRequests(new Query().filter("widgets", "resources", IN));
        MonitorTestData.assertCovered(datas, 11, 14, 18);
    }

    @Test
    public void testGetRequestsAggregate() throws Exception {
        final List<RequestData> datas = new ArrayList();
        final List<Object> aggs = new ArrayList();
        RequestDataVisitor v = new RequestDataVisitor() {
            public void visit(RequestData data, Object... aggregates) {
                datas.add(data);
                aggs.addAll(Arrays.asList(aggregates));
            }
        };
        dao.getRequests(new Query().properties("path").filter("path", "/foo", EQ).aggregate("count()").group("path"), v);
        Assert.assertEquals(1, datas.size());
        Assert.assertEquals("/foo", datas.get(0).getPath());
        Assert.assertEquals(4, ((Number) (aggs.get(0))).intValue());
        datas.clear();
        aggs.clear();
        dao.getRequests(new Query().properties("service", "operation").filter("service", null, NEQ).aggregate("count()").group("service", "operation").sort("count()", DESC), v);
        RequestData r = datas.get(0);
        Assert.assertEquals("foo", r.getService());
        Assert.assertEquals("x", r.getOperation());
        Assert.assertEquals(4, ((Number) (aggs.get(0))).intValue());
        r = datas.get(1);
        Assert.assertEquals("bam", r.getService());
        Assert.assertEquals("y", r.getOperation());
        Assert.assertEquals(2, ((Number) (aggs.get(1))).intValue());
    }

    @Test
    public void testGetRequestsCount() throws Exception {
        final List<Object> aggs = new ArrayList();
        RequestDataVisitor v = new RequestDataVisitor() {
            public void visit(RequestData data, Object... aggregates) {
                aggs.addAll(Arrays.asList(aggregates));
            }
        };
        dao.getRequests(new Query().aggregate("count()").filter("path", "/foo", EQ), v);
        Assert.assertEquals(1, aggs.size());
        Assert.assertEquals(4, ((Number) (aggs.get(0))).intValue());
    }

    @Test
    public void testGetRequestsFilterAnd() throws Exception {
        Assert.assertEquals(1, dao.getRequests(new Query().filter("path", "/foo", EQ).filter("widgets", "resources", IN)).size());
    }

    @Test
    public void testGetRequestsFilterOr() throws Exception {
        Assert.assertEquals(4, dao.getRequests(new Query().filter("path", "/seven", EQ).or("widgets", "resources", IN)).size());
    }

    @Test
    public void testGetRequestsJoin() throws Exception {
        List<RequestData> datas = dao.getRequests(new Query().properties("path", "resource").filter("path", "/foo", EQ).group("path", "resource").sort("resource", ASC));
        Assert.assertEquals(3, datas.size());
        Assert.assertEquals("stuff", datas.get(0).getResources().get(0));
        Assert.assertEquals("things", datas.get(1).getResources().get(0));
        Assert.assertEquals("widgets", datas.get(2).getResources().get(0));
    }

    @Test
    public void testGetRequestsJoinVisitor() throws Exception {
        final List<RequestData> datas = new ArrayList();
        final List<Object> aggs = new ArrayList();
        RequestDataVisitor v = new RequestDataVisitor() {
            public void visit(RequestData data, Object... aggregates) {
                datas.add(data);
                // aggs.addAll(Arrays.asList(aggregates));
            }
        };
        dao.getRequests(new Query().properties("path", "resource").filter("path", "/foo", EQ).group("path", "resource").sort("resource", ASC), v);
        Assert.assertEquals(3, datas.size());
        Assert.assertEquals(1, datas.get(0).getResources().size());
        Assert.assertEquals("stuff", datas.get(0).getResources().get(0));
        Assert.assertEquals(1, datas.get(1).getResources().size());
        Assert.assertEquals("things", datas.get(1).getResources().get(0));
        Assert.assertEquals(1, datas.get(2).getResources().size());
        Assert.assertEquals("widgets", datas.get(2).getResources().get(0));
    }

    @Test
    public void testGetRequestsJoin2() throws Exception {
        final List<RequestData> datas = new ArrayList();
        final List<Object> aggs = new ArrayList();
        dao.getRequests(new Query().properties("resource").aggregate("count()").filter("resource", null, NEQ).group("resource"), new RequestDataVisitor() {
            public void visit(RequestData data, Object... aggregates) {
                datas.add(data);
                aggs.add(aggregates[0]);
            }
        });
        // assertEquals(3, datas.size());
        for (RequestData data : datas) {
            System.out.println(data.getResources());
        }
    }

    @Test
    public void testGetRequestsJoinIN() throws Exception {
        List<String> resources = Arrays.asList("widgets", "things");
        List<RequestData> datas = dao.getRequests(new Query().properties("resource").aggregate("count()").filter("resource", resources, IN).group("resource").sort("resource", ASC));
        Assert.assertEquals(2, datas.size());
        Assert.assertEquals("things", datas.get(0).getResources().get(0));
        Assert.assertEquals("widgets", datas.get(1).getResources().get(0));
    }

    @Test
    public void testGetRequestsAdvancedFilter() throws Exception {
        Filter filter = new Filter("path", "/four", Comparison.EQ).or(and(new Filter("resource", Arrays.asList("widgets"), Comparison.IN)));
        List<RequestData> datas = dao.getRequests(new Query().filter(filter));
        Assert.assertEquals(2, datas.size());
        MonitorTestData.assertCovered(datas, 4, 11);
    }
}

