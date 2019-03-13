package com.codahale.metrics.servlets;


import HealthCheckServlet.HEALTH_CHECK_REGISTRY;
import HttpHeader.CONTENT_TYPE;
import com.codahale.metrics.Clock;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class HealthCheckServletTest extends AbstractServletTest {
    private static final ZonedDateTime FIXED_TIME = ZonedDateTime.now();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private static final String EXPECTED_TIMESTAMP = HealthCheckServletTest.DATE_TIME_FORMATTER.format(HealthCheckServletTest.FIXED_TIME);

    private static final Clock FIXED_CLOCK = new Clock() {
        @Override
        public long getTick() {
            return 0L;
        }

        @Override
        public long getTime() {
            return HealthCheckServletTest.FIXED_TIME.toInstant().toEpochMilli();
        }
    };

    private final HealthCheckRegistry registry = new HealthCheckRegistry();

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    @Test
    public void returns501IfNoHealthChecksAreRegistered() throws Exception {
        processRequest();
        assertThat(response.getStatus()).isEqualTo(501);
        assertThat(response.getContent()).isEqualTo("{}");
        assertThat(response.get(CONTENT_TYPE)).isEqualTo("application/json");
    }

    @Test
    public void returnsA200IfAllHealthChecksAreHealthy() throws Exception {
        registry.register("fun", new HealthCheck() {
            @Override
            protected Result check() {
                return HealthCheckServletTest.healthyResultUsingFixedClockWithMessage("whee");
            }

            @Override
            protected Clock clock() {
                return HealthCheckServletTest.FIXED_CLOCK;
            }
        });
        processRequest();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContent()).isEqualTo((("{\"fun\":{\"healthy\":true,\"message\":\"whee\",\"duration\":0,\"timestamp\":\"" + (HealthCheckServletTest.EXPECTED_TIMESTAMP)) + "\"}}"));
        assertThat(response.get(CONTENT_TYPE)).isEqualTo("application/json");
    }

    @Test
    public void returnsASubsetOfHealthChecksIfFiltered() throws Exception {
        registry.register("fun", new HealthCheck() {
            @Override
            protected Result check() {
                return HealthCheckServletTest.healthyResultUsingFixedClockWithMessage("whee");
            }

            @Override
            protected Clock clock() {
                return HealthCheckServletTest.FIXED_CLOCK;
            }
        });
        registry.register("filtered", new HealthCheck() {
            @Override
            protected Result check() {
                return Result.unhealthy("whee");
            }

            @Override
            protected Clock clock() {
                return HealthCheckServletTest.FIXED_CLOCK;
            }
        });
        processRequest();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContent()).isEqualTo((("{\"fun\":{\"healthy\":true,\"message\":\"whee\",\"duration\":0,\"timestamp\":\"" + (HealthCheckServletTest.EXPECTED_TIMESTAMP)) + "\"}}"));
        assertThat(response.get(CONTENT_TYPE)).isEqualTo("application/json");
    }

    @Test
    public void returnsA500IfAnyHealthChecksAreUnhealthy() throws Exception {
        registry.register("fun", new HealthCheck() {
            @Override
            protected Result check() {
                return HealthCheckServletTest.healthyResultUsingFixedClockWithMessage("whee");
            }

            @Override
            protected Clock clock() {
                return HealthCheckServletTest.FIXED_CLOCK;
            }
        });
        registry.register("notFun", new HealthCheck() {
            @Override
            protected Result check() {
                return Result.builder().usingClock(HealthCheckServletTest.FIXED_CLOCK).unhealthy().withMessage("whee").build();
            }

            @Override
            protected Clock clock() {
                return HealthCheckServletTest.FIXED_CLOCK;
            }
        });
        processRequest();
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getContent()).contains((("{\"fun\":{\"healthy\":true,\"message\":\"whee\",\"duration\":0,\"timestamp\":\"" + (HealthCheckServletTest.EXPECTED_TIMESTAMP)) + "\"}"), ((",\"notFun\":{\"healthy\":false,\"message\":\"whee\",\"duration\":0,\"timestamp\":\"" + (HealthCheckServletTest.EXPECTED_TIMESTAMP)) + "\"}}"));
        assertThat(response.get(CONTENT_TYPE)).isEqualTo("application/json");
    }

    @Test
    public void optionallyPrettyPrintsTheJson() throws Exception {
        registry.register("fun", new HealthCheck() {
            @Override
            protected Result check() {
                return HealthCheckServletTest.healthyResultUsingFixedClockWithMessage("foo bar 123");
            }

            @Override
            protected Clock clock() {
                return HealthCheckServletTest.FIXED_CLOCK;
            }
        });
        request.setURI("/healthchecks?pretty=true");
        processRequest();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContent()).isEqualTo(String.format((((("{%n" + (((("  \"fun\" : {%n" + "    \"healthy\" : true,%n") + "    \"message\" : \"foo bar 123\",%n") + "    \"duration\" : 0,%n") + "    \"timestamp\" : \"")) + (HealthCheckServletTest.EXPECTED_TIMESTAMP)) + "\"") + "%n  }%n}")));
        assertThat(response.get(CONTENT_TYPE)).isEqualTo("application/json");
    }

    @Test
    public void constructorWithRegistryAsArgumentIsUsedInPreferenceOverServletConfig() throws Exception {
        final HealthCheckRegistry healthCheckRegistry = Mockito.mock(HealthCheckRegistry.class);
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        final ServletConfig servletConfig = Mockito.mock(ServletConfig.class);
        Mockito.when(servletConfig.getServletContext()).thenReturn(servletContext);
        final HealthCheckServlet healthCheckServlet = new HealthCheckServlet(healthCheckRegistry);
        healthCheckServlet.init(servletConfig);
        Mockito.verify(servletConfig, Mockito.times(1)).getServletContext();
        Mockito.verify(servletContext, Mockito.never()).getAttribute(ArgumentMatchers.eq(HEALTH_CHECK_REGISTRY));
    }

    @Test
    public void constructorWithRegistryAsArgumentUsesServletConfigWhenNull() throws Exception {
        final HealthCheckRegistry healthCheckRegistry = Mockito.mock(HealthCheckRegistry.class);
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        final ServletConfig servletConfig = Mockito.mock(ServletConfig.class);
        Mockito.when(servletConfig.getServletContext()).thenReturn(servletContext);
        Mockito.when(servletContext.getAttribute(ArgumentMatchers.eq(HEALTH_CHECK_REGISTRY))).thenReturn(healthCheckRegistry);
        final HealthCheckServlet healthCheckServlet = new HealthCheckServlet(null);
        healthCheckServlet.init(servletConfig);
        Mockito.verify(servletConfig, Mockito.times(1)).getServletContext();
        Mockito.verify(servletContext, Mockito.times(1)).getAttribute(ArgumentMatchers.eq(HEALTH_CHECK_REGISTRY));
    }

    @Test(expected = ServletException.class)
    public void constructorWithRegistryAsArgumentUsesServletConfigWhenNullButWrongTypeInContext() throws Exception {
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        final ServletConfig servletConfig = Mockito.mock(ServletConfig.class);
        Mockito.when(servletConfig.getServletContext()).thenReturn(servletContext);
        Mockito.when(servletContext.getAttribute(ArgumentMatchers.eq(HEALTH_CHECK_REGISTRY))).thenReturn("IRELLEVANT_STRING");
        final HealthCheckServlet healthCheckServlet = new HealthCheckServlet(null);
        healthCheckServlet.init(servletConfig);
    }
}

