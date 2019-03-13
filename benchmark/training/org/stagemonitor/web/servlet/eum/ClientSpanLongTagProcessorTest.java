package org.stagemonitor.web.servlet.eum;


import SpanWrappingTracer.SpanWrappingSpanBuilder;
import Tags.SAMPLING_PRIORITY;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import java.util.HashMap;
import org.junit.Test;
import org.stagemonitor.tracing.wrapper.SpanWrapper;


public class ClientSpanLongTagProcessorTest {
    private static final String REQUEST_PARAMETER_NAME = "param";

    private static final String TAG = "tag";

    private MockTracer mockTracer;

    private SpanWrappingSpanBuilder spanBuilder;

    private HashMap<String, String[]> servletRequestParameters = new HashMap<>();

    @Test
    public void testProcessSpan_withValidLongValueReturnsThatValue() throws Exception {
        // Given
        addServletParameter("123");
        ClientSpanTagProcessor clientSpanLongTagProcessor = new ClientSpanLongTagProcessor(ClientSpanTagProcessor.TYPE_ALL, ClientSpanLongTagProcessorTest.TAG, ClientSpanLongTagProcessorTest.REQUEST_PARAMETER_NAME);
        // When
        SpanWrapper span = runProcessor(clientSpanLongTagProcessor);
        // Then
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = mockTracer.finishedSpans().get(0);
        assertThat(mockSpan.tags().get(ClientSpanLongTagProcessorTest.TAG)).isEqualTo(123L);
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isNotEqualTo(0);
    }

    @Test
    public void testProcessSpan_withInvalidValueReturnsNull() throws Exception {
        // Given
        addServletParameter("invalid");
        ClientSpanTagProcessor clientSpanLongTagProcessor = new ClientSpanLongTagProcessor(ClientSpanTagProcessor.TYPE_ALL, ClientSpanLongTagProcessorTest.TAG, ClientSpanLongTagProcessorTest.REQUEST_PARAMETER_NAME);
        // When
        SpanWrapper span = runProcessor(clientSpanLongTagProcessor);
        // Then
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = mockTracer.finishedSpans().get(0);
        assertThat(mockSpan.tags()).doesNotContainKey(ClientSpanLongTagProcessorTest.TAG);
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isNotEqualTo(0);
    }

    @Test
    public void testProcessSpan_withUpperIgnoresIfGreater() throws Exception {
        // Given
        addServletParameter("6");
        ClientSpanTagProcessor clientSpanLongTagProcessor = upperBound(5L);
        // When
        SpanWrapper span = runProcessor(clientSpanLongTagProcessor);
        // Then
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = mockTracer.finishedSpans().get(0);
        assertThat(mockSpan.tags()).doesNotContainKeys(ClientSpanLongTagProcessorTest.TAG);
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isNotEqualTo(0);
    }

    @Test
    public void testProcessSpan_withUpperBoundDiscardsIfGreater() throws Exception {
        // Given
        addServletParameter("6");
        ClientSpanTagProcessor clientSpanLongTagProcessor = new ClientSpanLongTagProcessor(ClientSpanTagProcessor.TYPE_ALL, ClientSpanLongTagProcessorTest.TAG, ClientSpanLongTagProcessorTest.REQUEST_PARAMETER_NAME).upperBound(5L).discardSpanOnBoundViolation(true);
        // When
        SpanWrapper span = runProcessor(clientSpanLongTagProcessor);
        // Then
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = mockTracer.finishedSpans().get(0);
        assertThat(mockSpan.tags()).doesNotContainKeys(ClientSpanLongTagProcessorTest.TAG);
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isEqualTo(0);
    }

    @Test
    public void testProcessSpan_withUpperBoundAllowingIfEquals() throws Exception {
        // Given
        addServletParameter("6");
        ClientSpanTagProcessor clientSpanLongTagProcessor = new ClientSpanLongTagProcessor(ClientSpanTagProcessor.TYPE_ALL, ClientSpanLongTagProcessorTest.TAG, ClientSpanLongTagProcessorTest.REQUEST_PARAMETER_NAME).upperBound(6L).discardSpanOnBoundViolation(true);
        // When
        SpanWrapper span = runProcessor(clientSpanLongTagProcessor);
        // Then
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = mockTracer.finishedSpans().get(0);
        assertThat(mockSpan.tags().get(ClientSpanLongTagProcessorTest.TAG)).isEqualTo(6L);
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isNotEqualTo(0);
    }

    @Test
    public void testProcessSpan_withLowerBoundIgnoresIfLower() throws Exception {
        // Given
        addServletParameter("4");
        ClientSpanTagProcessor clientSpanLongTagProcessor = lowerBound(5L);
        // When
        SpanWrapper span = runProcessor(clientSpanLongTagProcessor);
        // Then
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = mockTracer.finishedSpans().get(0);
        assertThat(mockSpan.tags()).doesNotContainKeys(ClientSpanLongTagProcessorTest.TAG);
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isNotEqualTo(0);
    }

    @Test
    public void testProcessSpan_withLowerBoundDiscardsIfLower() throws Exception {
        // Given
        addServletParameter("4");
        ClientSpanTagProcessor clientSpanLongTagProcessor = new ClientSpanLongTagProcessor(ClientSpanTagProcessor.TYPE_ALL, ClientSpanLongTagProcessorTest.TAG, ClientSpanLongTagProcessorTest.REQUEST_PARAMETER_NAME).lowerBound(5L).discardSpanOnBoundViolation(true);
        // When
        SpanWrapper span = runProcessor(clientSpanLongTagProcessor);
        // Then
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = mockTracer.finishedSpans().get(0);
        assertThat(mockSpan.tags()).doesNotContainKeys(ClientSpanLongTagProcessorTest.TAG);
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isEqualTo(0);
    }

    @Test
    public void testProcessSpan_withLowerBoundAllowingIfEquals() throws Exception {
        // Given
        addServletParameter("5");
        ClientSpanTagProcessor clientSpanLongTagProcessor = new ClientSpanLongTagProcessor(ClientSpanTagProcessor.TYPE_ALL, ClientSpanLongTagProcessorTest.TAG, ClientSpanLongTagProcessorTest.REQUEST_PARAMETER_NAME).lowerBound(5L).discardSpanOnBoundViolation(true);
        // When
        SpanWrapper span = runProcessor(clientSpanLongTagProcessor);
        // Then
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = mockTracer.finishedSpans().get(0);
        assertThat(mockSpan.tags().get(ClientSpanLongTagProcessorTest.TAG)).isEqualTo(5L);
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isNotEqualTo(0);
    }
}

