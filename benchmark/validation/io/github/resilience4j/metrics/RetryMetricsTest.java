package io.github.resilience4j.metrics;


import com.codahale.metrics.MetricRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.test.HelloWorldService;
import io.vavr.control.Try;
import javax.xml.ws.WebServiceException;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


public class RetryMetricsTest {
    private MetricRegistry metricRegistry;

    private HelloWorldService helloWorldService;

    @Test
    public void shouldRegisterMetricsWithoutRetry() throws Throwable {
        // Given
        RetryRegistry retryRegistry = RetryRegistry.ofDefaults();
        Retry retry = retryRegistry.retry("testName");
        metricRegistry.registerAll(RetryMetrics.ofRetryRegistry(retryRegistry));
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn("Hello world");
        // Setup circuitbreaker with retry
        String value = retry.executeSupplier(helloWorldService::returnHelloWorld);
        // Then
        assertThat(value).isEqualTo("Hello world");
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
        assertThat(metricRegistry.getMetrics()).hasSize(4);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (SUCCESSFUL_CALLS_WITH_RETRY))).getValue()).isEqualTo(0L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (SUCCESSFUL_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(1L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (FAILED_CALLS_WITH_RETRY))).getValue()).isEqualTo(0L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (FAILED_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(0L);
    }

    @Test
    public void shouldRegisterMetricsWithRetry() throws Throwable {
        // Given
        RetryRegistry retryRegistry = RetryRegistry.ofDefaults();
        Retry retry = retryRegistry.retry("testName");
        metricRegistry.registerAll(RetryMetrics.ofRetryRegistry(retryRegistry));
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willThrow(new WebServiceException("BAM!")).willReturn("Hello world").willThrow(new WebServiceException("BAM!")).willThrow(new WebServiceException("BAM!")).willThrow(new WebServiceException("BAM!"));
        // Setup circuitbreaker with retry
        String value1 = retry.executeSupplier(helloWorldService::returnHelloWorld);
        Try.ofSupplier(Retry.decorateSupplier(retry, helloWorldService::returnHelloWorld));
        // Then
        assertThat(value1).isEqualTo("Hello world");
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(5)).returnHelloWorld();
        assertThat(metricRegistry.getMetrics()).hasSize(4);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (SUCCESSFUL_CALLS_WITH_RETRY))).getValue()).isEqualTo(1L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (SUCCESSFUL_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(0L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (FAILED_CALLS_WITH_RETRY))).getValue()).isEqualTo(1L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (FAILED_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(0L);
    }

    @Test
    public void shouldUseCustomPrefix() throws Throwable {
        // Given
        RetryRegistry retryRegistry = RetryRegistry.ofDefaults();
        Retry retry = retryRegistry.retry("testName");
        metricRegistry.registerAll(RetryMetrics.ofRetryRegistry("testPrefix", retryRegistry));
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn("Hello world");
        String value = retry.executeSupplier(helloWorldService::returnHelloWorld);
        // Then
        assertThat(value).isEqualTo("Hello world");
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
        assertThat(metricRegistry.getMetrics()).hasSize(4);
        assertThat(metricRegistry.getGauges().get(("testPrefix.testName." + (SUCCESSFUL_CALLS_WITH_RETRY))).getValue()).isEqualTo(0L);
        assertThat(metricRegistry.getGauges().get(("testPrefix.testName." + (SUCCESSFUL_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(1L);
        assertThat(metricRegistry.getGauges().get(("testPrefix.testName." + (FAILED_CALLS_WITH_RETRY))).getValue()).isEqualTo(0L);
        assertThat(metricRegistry.getGauges().get(("testPrefix.testName." + (FAILED_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(0L);
    }
}

