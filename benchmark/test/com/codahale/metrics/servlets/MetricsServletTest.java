package com.codahale.metrics.servlets;


import HttpHeader.CONTENT_TYPE;
import MetricsServlet.METRICS_REGISTRY;
import com.codahale.metrics.Clock;
import com.codahale.metrics.MetricRegistry;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.eclipse.jetty.servlet.ServletTester;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class MetricsServletTest extends AbstractServletTest {
    private final Clock clock = Mockito.mock(Clock.class);

    private final MetricRegistry registry = new MetricRegistry();

    private ServletTester tester;

    @Test
    public void returnsA200() throws Exception {
        processRequest();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.get("Access-Control-Allow-Origin")).isEqualTo("*");
        assertThat(response.getContent()).isEqualTo(("{" + ((((((((((((("\"version\":\"4.0.0\"," + "\"gauges\":{") + "\"g1\":{\"value\":100}") + "},") + "\"counters\":{") + "\"c\":{\"count\":1}") + "},") + "\"histograms\":{") + "\"h\":{\"count\":1,\"max\":1,\"mean\":1.0,\"min\":1,\"p50\":1.0,\"p75\":1.0,\"p95\":1.0,\"p98\":1.0,\"p99\":1.0,\"p999\":1.0,\"stddev\":0.0}") + "},") + "\"meters\":{") + "\"m\":{\"count\":1,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":3333333.3333333335,\"units\":\"events/second\"}},\"timers\":{\"t\":{\"count\":1,\"max\":1.0,\"mean\":1.0,\"min\":1.0,\"p50\":1.0,\"p75\":1.0,\"p95\":1.0,\"p98\":1.0,\"p99\":1.0,\"p999\":1.0,\"stddev\":0.0,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":1.0E7,\"duration_units\":\"seconds\",\"rate_units\":\"calls/second\"}") + "}") + "}")));
        assertThat(response.get(CONTENT_TYPE)).isEqualTo("application/json");
    }

    @Test
    public void returnsJsonWhenJsonpInitParamNotSet() throws Exception {
        String callbackParamName = "callbackParam";
        String callbackParamVal = "callbackParamVal";
        request.setURI(((("/metrics?" + callbackParamName) + "=") + callbackParamVal));
        processRequest();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.get("Access-Control-Allow-Origin")).isEqualTo("*");
        assertThat(response.getContent()).isEqualTo(("{" + ((((((((((((("\"version\":\"4.0.0\"," + "\"gauges\":{") + "\"g1\":{\"value\":100}") + "},") + "\"counters\":{") + "\"c\":{\"count\":1}") + "},") + "\"histograms\":{") + "\"h\":{\"count\":1,\"max\":1,\"mean\":1.0,\"min\":1,\"p50\":1.0,\"p75\":1.0,\"p95\":1.0,\"p98\":1.0,\"p99\":1.0,\"p999\":1.0,\"stddev\":0.0}") + "},") + "\"meters\":{") + "\"m\":{\"count\":1,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":3333333.3333333335,\"units\":\"events/second\"}},\"timers\":{\"t\":{\"count\":1,\"max\":1.0,\"mean\":1.0,\"min\":1.0,\"p50\":1.0,\"p75\":1.0,\"p95\":1.0,\"p98\":1.0,\"p99\":1.0,\"p999\":1.0,\"stddev\":0.0,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":1.0E7,\"duration_units\":\"seconds\",\"rate_units\":\"calls/second\"}") + "}") + "}")));
        assertThat(response.get(CONTENT_TYPE)).isEqualTo("application/json");
    }

    @Test
    public void returnsJsonpWhenInitParamSet() throws Exception {
        String callbackParamName = "callbackParam";
        String callbackParamVal = "callbackParamVal";
        request.setURI(((("/metrics?" + callbackParamName) + "=") + callbackParamVal));
        tester.getContext().setInitParameter("com.codahale.metrics.servlets.MetricsServlet.jsonpCallback", callbackParamName);
        processRequest();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.get("Access-Control-Allow-Origin")).isEqualTo("*");
        assertThat(response.getContent()).isEqualTo((((((((((((((((callbackParamVal + "({") + "\"version\":\"4.0.0\",") + "\"gauges\":{") + "\"g1\":{\"value\":100}") + "},") + "\"counters\":{") + "\"c\":{\"count\":1}") + "},") + "\"histograms\":{") + "\"h\":{\"count\":1,\"max\":1,\"mean\":1.0,\"min\":1,\"p50\":1.0,\"p75\":1.0,\"p95\":1.0,\"p98\":1.0,\"p99\":1.0,\"p999\":1.0,\"stddev\":0.0}") + "},") + "\"meters\":{") + "\"m\":{\"count\":1,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":3333333.3333333335,\"units\":\"events/second\"}},\"timers\":{\"t\":{\"count\":1,\"max\":1.0,\"mean\":1.0,\"min\":1.0,\"p50\":1.0,\"p75\":1.0,\"p95\":1.0,\"p98\":1.0,\"p99\":1.0,\"p999\":1.0,\"stddev\":0.0,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":1.0E7,\"duration_units\":\"seconds\",\"rate_units\":\"calls/second\"}") + "}") + "})"));
        assertThat(response.get(CONTENT_TYPE)).isEqualTo("application/json");
    }

    @Test
    public void optionallyPrettyPrintsTheJson() throws Exception {
        request.setURI("/metrics?pretty=true");
        processRequest();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.get("Access-Control-Allow-Origin")).isEqualTo("*");
        assertThat(response.getContent()).isEqualTo(String.format(("{%n" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((((("  \"version\" : \"4.0.0\",%n" + "  \"gauges\" : {%n") + "    \"g1\" : {%n") + "      \"value\" : 100%n") + "    }%n") + "  },%n") + "  \"counters\" : {%n") + "    \"c\" : {%n") + "      \"count\" : 1%n") + "    }%n") + "  },%n") + "  \"histograms\" : {%n") + "    \"h\" : {%n") + "      \"count\" : 1,%n") + "      \"max\" : 1,%n") + "      \"mean\" : 1.0,%n") + "      \"min\" : 1,%n") + "      \"p50\" : 1.0,%n") + "      \"p75\" : 1.0,%n") + "      \"p95\" : 1.0,%n") + "      \"p98\" : 1.0,%n") + "      \"p99\" : 1.0,%n") + "      \"p999\" : 1.0,%n") + "      \"stddev\" : 0.0%n") + "    }%n") + "  },%n") + "  \"meters\" : {%n") + "    \"m\" : {%n") + "      \"count\" : 1,%n") + "      \"m15_rate\" : 0.0,%n") + "      \"m1_rate\" : 0.0,%n") + "      \"m5_rate\" : 0.0,%n") + "      \"mean_rate\" : 3333333.3333333335,%n") + "      \"units\" : \"events/second\"%n") + "    }%n") + "  },%n") + "  \"timers\" : {%n") + "    \"t\" : {%n") + "      \"count\" : 1,%n") + "      \"max\" : 1.0,%n") + "      \"mean\" : 1.0,%n") + "      \"min\" : 1.0,%n") + "      \"p50\" : 1.0,%n") + "      \"p75\" : 1.0,%n") + "      \"p95\" : 1.0,%n") + "      \"p98\" : 1.0,%n") + "      \"p99\" : 1.0,%n") + "      \"p999\" : 1.0,%n") + "      \"stddev\" : 0.0,%n") + "      \"m15_rate\" : 0.0,%n") + "      \"m1_rate\" : 0.0,%n") + "      \"m5_rate\" : 0.0,%n") + "      \"mean_rate\" : 1.0E7,%n") + "      \"duration_units\" : \"seconds\",%n") + "      \"rate_units\" : \"calls/second\"%n") + "    }%n") + "  }%n") + "}"))));
        assertThat(response.get(CONTENT_TYPE)).isEqualTo("application/json");
    }

    @Test
    public void constructorWithRegistryAsArgumentIsUsedInPreferenceOverServletConfig() throws Exception {
        final MetricRegistry metricRegistry = Mockito.mock(MetricRegistry.class);
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        final ServletConfig servletConfig = Mockito.mock(ServletConfig.class);
        Mockito.when(servletConfig.getServletContext()).thenReturn(servletContext);
        final MetricsServlet metricsServlet = new MetricsServlet(metricRegistry);
        metricsServlet.init(servletConfig);
        Mockito.verify(servletConfig, Mockito.times(1)).getServletContext();
        Mockito.verify(servletContext, Mockito.never()).getAttribute(ArgumentMatchers.eq(METRICS_REGISTRY));
    }

    @Test
    public void constructorWithRegistryAsArgumentUsesServletConfigWhenNull() throws Exception {
        final MetricRegistry metricRegistry = Mockito.mock(MetricRegistry.class);
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        final ServletConfig servletConfig = Mockito.mock(ServletConfig.class);
        Mockito.when(servletConfig.getServletContext()).thenReturn(servletContext);
        Mockito.when(servletContext.getAttribute(ArgumentMatchers.eq(METRICS_REGISTRY))).thenReturn(metricRegistry);
        final MetricsServlet metricsServlet = new MetricsServlet(null);
        metricsServlet.init(servletConfig);
        Mockito.verify(servletConfig, Mockito.times(1)).getServletContext();
        Mockito.verify(servletContext, Mockito.times(1)).getAttribute(ArgumentMatchers.eq(METRICS_REGISTRY));
    }

    @Test(expected = ServletException.class)
    public void constructorWithRegistryAsArgumentUsesServletConfigWhenNullButWrongTypeInContext() throws Exception {
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        final ServletConfig servletConfig = Mockito.mock(ServletConfig.class);
        Mockito.when(servletConfig.getServletContext()).thenReturn(servletContext);
        Mockito.when(servletContext.getAttribute(ArgumentMatchers.eq(METRICS_REGISTRY))).thenReturn("IRELLEVANT_STRING");
        final MetricsServlet metricsServlet = new MetricsServlet(null);
        metricsServlet.init(servletConfig);
    }
}

