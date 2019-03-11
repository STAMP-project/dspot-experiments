package org.stagemonitor.web.servlet.widget;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.core.util.JsonUtils;


public class StagemonitorMetricsServletTest {
    private StagemonitorMetricsServlet servlet;

    private Metric2Registry registry;

    @Test
    public void getAllMetrics() throws Exception {
        registry.counter(name("foo").tag("bar", "baz").build()).inc();
        registry.counter(name("qux").tag("quux", "foo").build()).inc();
        final MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.doGet(new MockHttpServletRequest(), resp);
        Assert.assertEquals(("[" + (("{\"name\":\"foo\",\"tags\":{\"bar\":\"baz\"},\"values\":{\"count\":1}}," + "{\"name\":\"qux\",\"tags\":{\"quux\":\"foo\"},\"values\":{\"count\":1}}") + "]")), resp.getContentAsString());
    }

    @Test
    public void getFilteredMetrics() throws Exception {
        registry.counter(name("foo").tag("bar", "baz").build()).inc();
        registry.counter(name("qux").tag("quux", "foo").build()).inc();
        final MockHttpServletResponse resp = new MockHttpServletResponse();
        final MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter("metricNames[]", "foo");
        servlet.doGet(req, resp);
        Assert.assertEquals("[{\"name\":\"foo\",\"tags\":{\"bar\":\"baz\"},\"values\":{\"count\":1}}]", resp.getContentAsString());
    }

    @Test
    public void getMeter() throws Exception {
        registry.meter(name("foo").tag("bar", "baz").build()).mark();
        final MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.doGet(new MockHttpServletRequest(), resp);
        final double mean_rate = JsonUtils.getMapper().readTree(resp.getContentAsString()).get(0).get("values").get("mean_rate").doubleValue();
        Assert.assertTrue(("Expected m1 rate of > 0, but got " + mean_rate), (mean_rate > 0));
    }
}

