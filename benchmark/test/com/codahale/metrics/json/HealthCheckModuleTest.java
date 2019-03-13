package com.codahale.metrics.json;


import HealthCheck.Result;
import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;


public class HealthCheckModuleTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new HealthCheckModule());

    @Test
    public void serializesAHealthyResult() throws Exception {
        HealthCheck.Result result = Result.healthy();
        assertThat(mapper.writeValueAsString(result)).isEqualTo((("{\"healthy\":true,\"duration\":0,\"timestamp\":\"" + (result.getTimestamp())) + "\"}"));
    }

    @Test
    public void serializesAHealthyResultWithAMessage() throws Exception {
        HealthCheck.Result result = Result.healthy("yay for %s", "me");
        assertThat(mapper.writeValueAsString(result)).isEqualTo((((("{" + ((("\"healthy\":true," + "\"message\":\"yay for me\",") + "\"duration\":0,") + "\"timestamp\":\"")) + (result.getTimestamp())) + "\"") + "}"));
    }

    @Test
    public void serializesAnUnhealthyResult() throws Exception {
        HealthCheck.Result result = Result.unhealthy("boo");
        assertThat(mapper.writeValueAsString(result)).isEqualTo((((("{" + ((("\"healthy\":false," + "\"message\":\"boo\",") + "\"duration\":0,") + "\"timestamp\":\"")) + (result.getTimestamp())) + "\"") + "}"));
    }

    @Test
    public void serializesAnUnhealthyResultWithAnException() throws Exception {
        final Throwable e = Mockito.mock(Throwable.class);
        Mockito.when(e.getMessage()).thenReturn("oh no");
        Mockito.when(e.getStackTrace()).thenReturn(new StackTraceElement[]{ new StackTraceElement("Blah", "bloo", "Blah.java", 100) });
        HealthCheck.Result result = Result.unhealthy(e);
        assertThat(mapper.writeValueAsString(result)).isEqualTo((((("{" + ((((((("\"healthy\":false," + "\"message\":\"oh no\",") + "\"error\":{") + "\"message\":\"oh no\",") + "\"stack\":[\"Blah.bloo(Blah.java:100)\"]") + "},") + "\"duration\":0,") + "\"timestamp\":\"")) + (result.getTimestamp())) + "\"") + "}"));
    }

    @Test
    public void serializesAnUnhealthyResultWithNestedExceptions() throws Exception {
        final Throwable a = Mockito.mock(Throwable.class);
        Mockito.when(a.getMessage()).thenReturn("oh no");
        Mockito.when(a.getStackTrace()).thenReturn(new StackTraceElement[]{ new StackTraceElement("Blah", "bloo", "Blah.java", 100) });
        final Throwable b = Mockito.mock(Throwable.class);
        Mockito.when(b.getMessage()).thenReturn("oh well");
        Mockito.when(b.getStackTrace()).thenReturn(new StackTraceElement[]{ new StackTraceElement("Blah", "blee", "Blah.java", 150) });
        Mockito.when(b.getCause()).thenReturn(a);
        HealthCheck.Result result = Result.unhealthy(b);
        assertThat(mapper.writeValueAsString(result)).isEqualTo((((("{" + ((((((((((("\"healthy\":false," + "\"message\":\"oh well\",") + "\"error\":{") + "\"message\":\"oh well\",") + "\"stack\":[\"Blah.blee(Blah.java:150)\"],") + "\"cause\":{") + "\"message\":\"oh no\",") + "\"stack\":[\"Blah.bloo(Blah.java:100)\"]") + "}") + "},") + "\"duration\":0,") + "\"timestamp\":\"")) + (result.getTimestamp())) + "\"") + "}"));
    }

    @Test
    public void serializeResultWithDetail() throws Exception {
        Map<String, Object> complex = new LinkedHashMap<>();
        complex.put("field", "value");
        HealthCheck.Result result = Result.builder().healthy().withDetail("boolean", true).withDetail("integer", 1).withDetail("long", 2L).withDetail("float", 3.546F).withDetail("double", 4.567).withDetail("BigInteger", new BigInteger("12345")).withDetail("BigDecimal", new BigDecimal("12345.56789")).withDetail("String", "string").withDetail("complex", complex).build();
        assertThat(mapper.writeValueAsString(result)).isEqualTo((((("{" + ((((((((((((("\"healthy\":true," + "\"duration\":0,") + "\"boolean\":true,") + "\"integer\":1,") + "\"long\":2,") + "\"float\":3.546,") + "\"double\":4.567,") + "\"BigInteger\":12345,") + "\"BigDecimal\":12345.56789,") + "\"String\":\"string\",") + "\"complex\":{") + "\"field\":\"value\"") + "},") + "\"timestamp\":\"")) + (result.getTimestamp())) + "\"") + "}"));
    }
}

